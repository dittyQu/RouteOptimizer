/**
 * ユーザステータスデータに登録するためのクラスです
 */
package jp.co.hiroshimabank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import jp.co.hiroshimabank.db.RecordNotFoundException;
import jp.co.hiroshimabank.dto.UserStatusDTO;

/**
 * @author 日本IBM　梅沢
 *
 */
public class UserStatusDAO {

	private Connection conn;

	public UserStatusDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * ユーザステータステーブルにユーザIDが存在するか確認します。
	 * 
	 * @param userId
	 *            ユーザID
	 * @return テーブルに存在するならばTrue　存在しないならばfalseを返します
	 * @throws SQLException
	 */
	public Boolean IsUserLogin(int userId) throws SQLException {
		PreparedStatement stmt = null;
		int count = 0;

		String sql = SQL.selectUSERSTATUSSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, userId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			count = rs.getInt(1);
		}

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 30分以内にアクセスされているかどうかを確認します。
	 * 
	 * @param userId
	 *            　ユーザID
	 * @return テーブルに存在するならばTrue　存在しないならばfalseを返します
	 * @throws SQLException
	 */
	public Boolean IsUserLoginInLimitedTime(UserStatusDTO status)
			throws SQLException {
		PreparedStatement stmt = null;
		int count = 0;
		String sql = SQL.selectTIMEDUSERSTATUSSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, status.getUserId());
		stmt.setTimestamp(2, status.getAccessTime());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			count = rs.getInt(1);
		}

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 30分以内にユーザがログインしていたか確認します。
	 * 
	 * @param token
	 *            ユーザから送られてきたトークン
	 * @param accessTime
	 *            メソッドが呼び出されたときの時間
	 * @return ユーザが存在すればユーザIDを返します。　存在しなければ0を返します。
	 * @throws SQLException
	 */
	public int IsUserLoginInLimitedTime(String token, Timestamp accessTime)
			throws SQLException {
		PreparedStatement stmt = null;
		int userId = 0;

		String sql = SQL.selectLOGINUSERIDSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, token);
		stmt.setTimestamp(2, accessTime);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			userId = rs.getInt("USER_ID");
		}
		return userId;
	}

	/**
	 * テーブルにステータスを登録します。
	 * 
	 * @param status
	 *            ステータス情報
	 * @return　登録成功すれば0、登録失敗すれば1を返します
	 * @throws SQLException
	 */
	public int addUserStatus(UserStatusDTO status) throws SQLException {
		PreparedStatement stmt = null;
		int result = 0;

		String sql = SQL.insertUSERSTATUSSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, status.getUserId());
		stmt.setString(2, status.getToken());
		stmt.setTimestamp(3, status.getAccessTime());
		result = stmt.executeUpdate();

		if (result == 1) {
			// 成功
			return 0;
		} else {
			// 失敗
			return 1;
		}
	}

	/**
	 * テーブルのステータス情報を更新します。
	 * 
	 * @param status
	 *            ステータス情報
	 * @return 登録成功すれば0、登録失敗すれば1を返します
	 * @throws SQLException
	 */
	public int updateUserStatus(UserStatusDTO status) throws SQLException {
		PreparedStatement stmt = null;
		int result = 0;
		String sql = SQL.updateUSERSTATUSSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, status.getToken());
		stmt.setTimestamp(2, status.getAccessTime());
		stmt.setInt(3, status.getUserId());
		result = stmt.executeUpdate();
		if (result == 1) {
			// 成功
			return 0;
		} else {
			// 失敗
			return 1;
		}
	}

	/**
	 * ステータス情報を削除します。
	 * 
	 * @param userId
	 *            ユーザID
	 * @return 登録成功すれば0、登録失敗すれば1を返します
	 * @throws SQLException
	 * @throws RecordNotFoundException
	 */
	public int deleteUserStatus(int userId) throws SQLException {
		PreparedStatement stmt = null;
		int result = 0;

		String sql = SQL.deleteUSERSTATUSSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, userId);
		result = stmt.executeUpdate();

		if (result == 1) {
			// 成功
			return 0;
		} else {
			// 失敗
			return 1;
		}
	}

	/**
	 * ステータス情報を削除します。
	 * 
	 * @param token
	 *            トークン
	 * @return 登録成功すれば0、登録失敗すれば1を返します
	 * @throws RecordNotFoundException
	 * @throws SQLException
	 */
	public int deleteUserStatus(String token) throws SQLException {
		PreparedStatement stmt = null;
		int result = 0;

		String sql = SQL.deleteUSERSTATUSTOKENSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, token);
		result = stmt.executeUpdate();
		if (result == 1) {
			// 成功
			return 0;
		} else {
			// 失敗
			return 1;
		}
	}
}
