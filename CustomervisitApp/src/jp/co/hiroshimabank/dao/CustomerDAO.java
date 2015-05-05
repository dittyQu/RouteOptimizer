/**
 * 顧客テーブルにアクセスするためのクラスです。
 */
package jp.co.hiroshimabank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.hiroshimabank.dto.CustomerDTO;

/**
 * @author 日本IBM 梅沢
 *
 */
public class CustomerDAO {

	private Connection conn;

	public CustomerDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 顧客リストを取得するためのクラスです。
	 * @param userId ログインユーザ
	 * @return 顧客リスト
	 * @throws SQLException
	 */
	public List<CustomerDTO> getCustomerList(int userId) throws SQLException {
		List<CustomerDTO> listDtos = new ArrayList<CustomerDTO>();
		PreparedStatement stmt = null;
		String sql = SQL.selectCustomerListSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, userId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			CustomerDTO customerDto = new CustomerDTO();
			customerDto.setCustomerId(rs.getInt("ID"));
			customerDto.setCustomerName(rs.getString("NAME"));
			customerDto.setCustomerAddress(rs.getString("ADDRESS"));
			customerDto.setUserId(rs.getInt("USER_ID"));
			customerDto.setEvent(rs.getString("EVENT"));
			listDtos.add(customerDto);
		}
		return listDtos;
	}
	/**
	 * お客様住所、名前を取得します。
	 * @param customerId お客様ID
	 * @return お客様住所、名前が入ったDTO
	 * @throws SQLException
	 */
	public CustomerDTO getCustomerAddressName(int customerId) throws SQLException {
		PreparedStatement stmt = null;
		CustomerDTO customerDto = new CustomerDTO();
		String sql = SQL.selectCustomerAddressNameSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, customerId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			customerDto.setCustomerAddress(rs.getString("ADDRESS"));
			customerDto.setCustomerName(rs.getString("NAME"));
		}
		return customerDto;
	}
}
