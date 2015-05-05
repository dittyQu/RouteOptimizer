/**
 * Accountテーブル情報を格納するためのクラスです。
 */
package jp.co.hiroshimabank.dto;

/**
 * @author 日本IBM 梅沢
 *
 */
public class AccountDTO {

	/**口座番号*/
	private String accountId;
	/**お客様ID*/
	private int customerId;
	/**預金形態*/
	private String deposit;
	/**支店*/
	private String branch;
	/**残高*/
	private String balance;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
	
}
