/**
 * トークンを削除できなかったときに発生します。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM 梅沢
 *
 */
public class TokeNotDeletedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7756129904608023982L;

	public TokeNotDeletedException() {
		super();
	}

	public TokeNotDeletedException(String message) {
		super(message);
	}

	public TokeNotDeletedException(Throwable cause) {
		super(cause);
	}

	public TokeNotDeletedException(String message, Throwable cause) {
		super(message, cause);
	}

}
