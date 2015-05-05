/**
 * ユーザステータスを格納するためのクラスです。
 */
package jp.co.hiroshimabank.dto;

import java.sql.Timestamp;

/**
 * @author 日本IBM　梅沢
 *
 */
public class UserStatusDTO {

	/** ユーザID */
	private int userId;
	/** トークンID */
	private String token;
	/** 最終アクセス日時 */
	private Timestamp accessTime;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Timestamp accessTime) {
		this.accessTime = accessTime;
	}

}
