/**
 * 顧客データを格納したクラスです。
 */
package jp.co.hiroshimabank.dto;

/**
 * @author 日本IBM 梅沢
 *
 */
public class CustomerDTO {

	/**顧客ID*/
	private int customerId;
	/**顧客住所*/
	private String customerAddress;
	/**顧客名*/
	private String customerName;
	/**担当者ID*/
	private int userId;
	/**訪問理由*/
	private String event;
	/**訪問ステータス*/
	private String status;
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
