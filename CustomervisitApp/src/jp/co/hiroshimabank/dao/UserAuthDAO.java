/**
 * ユーザ認証テーブルにアクセスするためのクラスです。
 */
package jp.co.hiroshimabank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jp.co.hiroshimabank.db.RecordAlreadyExistException;
import jp.co.hiroshimabank.dto.UserAuthDTO;
import jp.co.hiroshimabank.utils.EncryptionUtils;
import jp.co.hiroshimabank.utils.LogUtils;

/**
 * @author IBM梅沢
 *
 */
public class UserAuthDAO {

	private Connection conn;

	public UserAuthDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 認証ロジックです。
	 * 
	 * @param userId
	 *            　ユーザID
	 * @param userPwd
	 *            　パスワード
	 * @return　認証成功ならtrueを認証失敗ならfalseを返します。
	 * @throws SQLException
	 */
	public Boolean IsUserExist(int userId, String userPwd) throws SQLException {
		PreparedStatement stmt = null;
		String pwd = null;

		String sql = SQL.selectUSERPASSWDSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, userId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			pwd = rs.getString("USER_PASSWD");
		}
		// パスワード比較
		if (EncryptionUtils.compareString(userPwd, userId, pwd)) {
			LogUtils.print("認証成功");
			return true;
		} else {
			LogUtils.print("認証失敗");
			return false;
		}
	}

	/**
	 * ユーザ認証テーブルにデータを登録します。
	 * 
	 * @param userauth
	 *            ユーザ認証情報
	 * @return　成功すれば0、失敗すれば1を返します
	 * @throws RecordAlreadyExistException
	 */
	public int addUserAuth(UserAuthDTO userauth)
			throws RecordAlreadyExistException {
		PreparedStatement stmt = null;
		int result = 0;
		try {
			String sql = SQL.insertUSERAUTHSQL.getSql();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, userauth.getUserId());
			stmt.setString(2, EncryptionUtils.getStretchedPassword(
					userauth.getUserpwd(),
					Integer.toString(userauth.getUserId())));
			result = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RecordAlreadyExistException();
		}
		if (result == 1) {
			return 0;
		} else {
			return 1;
		}
	}
}
