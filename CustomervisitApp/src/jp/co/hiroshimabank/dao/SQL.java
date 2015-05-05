/**
 * SQLを記述します。
 */
package jp.co.hiroshimabank.dao;

/**
 * @author 日本IBM 梅沢
 *
 */
public enum SQL {

	selectCustomerListSQL("SELECT * FROM CUSTOMER where USER_ID = ? and STATUS='0'"),
	selectCustomerAddressNameSQL("select ADDRESS,NAME from CUSTOMER where ID = ?"),
	insertEventSQL("insert into EVENT values(?,?,?,?,?,?)"),
	selectEventListSQL("select * from EVENT where USER_ID = ? and START < ? and END >= ?"),
	updateEventSQL("update EVENT set NAME = ? , START = ? , END = ? where ID = ? and USER_ID = ?"),
	selectAccountListSQL("select * from Account where CUSTOMER_ID = ?"),
	selectEventSQL("select * from EVENT where ID = ? and USER_ID = ?"),
	selectUSERSTATUSSQL("select Count(*) from USERSTATUS where USER_ID=?"),
	selectTIMEDUSERSTATUSSQL("select Count(*) from USERSTATUS where USER_ID=? and (? < LAST_ACCESS_TIME + 30 MINUTES) "),
	selectLOGINUSERIDSQL("select USER_ID from USERSTATUS where TOKEN_ID=? and (? < LAST_ACCESS_TIME + 30 MINUTES)"),
	insertUSERSTATUSSQL("insert into USERSTATUS values(?,?,?)"),
	updateUSERSTATUSSQL("update USERSTATUS set TOKEN_ID=?,LAST_ACCESS_TIME=? where USER_ID=?"),
	deleteUSERSTATUSSQL("delete from USERSTATUS where USER_ID=?"),
	deleteUSERSTATUSTOKENSQL("delete from USERSTATUS where TOKEN_ID=?"),
	selectUSERPASSWDSQL("select USER_PASSWD from USERAUTH where USER_ID = ?"),
	insertUSERAUTHSQL("insert into USERAUTHINFO values(?,?)");

	private final String sql;

	private SQL(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return this.sql;
	}

}
