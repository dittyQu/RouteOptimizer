/**
 * ユーザ認証情報を格納するためのクラスです。
 */
package jp.co.hiroshimabank.dto;

/**
 * @author IBM梅沢
 *
 */
public class UserAuthDTO {

	/** ユーザID */
	private int userId;
	/** ユーザパスワード */
	private String userpwd;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

}
