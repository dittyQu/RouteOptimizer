/**
 * ユーザがアプリケーションにログインしていない場合に発生します。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM　梅沢
 *
 */
public class UserNotLoginException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6959345858160130841L;

	public UserNotLoginException() {
		super();
	}

	public UserNotLoginException(String message) {
		super(message);
	}

	public UserNotLoginException(Throwable cause) {
		super(cause);
	}

	public UserNotLoginException(String message, Throwable cause) {
		super(message, cause);
	}
}
