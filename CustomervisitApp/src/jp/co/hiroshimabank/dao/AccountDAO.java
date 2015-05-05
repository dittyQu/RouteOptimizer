/**
 * Accountテーブルにアクセスするためのクラスです。
 */
package jp.co.hiroshimabank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.hiroshimabank.dto.AccountDTO;

/**
 * @author 日本IBM 梅沢
 *
 */
public class AccountDAO {

	private Connection conn;

	public AccountDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * お客様の口座を取得します。
	 * 
	 * @param customerId
	 *            お客様ID
	 * @return お客様が保有する口座を返します。
	 * @throws SQLException
	 */
	public List<AccountDTO> getAccount(int customerId) throws SQLException {
		List<AccountDTO> listDtos = new ArrayList<AccountDTO>();
		PreparedStatement stmt = null;
		String sql = SQL.selectAccountListSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, customerId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			AccountDTO accountDto = new AccountDTO();
			accountDto.setAccountId(rs.getString("ID"));
			accountDto.setCustomerId(rs.getInt("CUSTOMER_ID"));
			accountDto.setDeposit(rs.getString("DEPOSIT"));
			accountDto.setBranch(rs.getString("BRANCH"));
			accountDto.setBalance(rs.getString("BALANCE").toString());
			listDtos.add(accountDto);
		}
		return listDtos;
	}
}
