/**
 * 復号化失敗時のエラーです。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM　梅沢
 *
 */
public class DecryptionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8821828153270029774L;

	public DecryptionException() {
		super();
	}

	public DecryptionException(String message) {
		super(message);
	}

	public DecryptionException(Throwable cause) {
		super(cause);
	}

	public DecryptionException(String message, Throwable cause) {
		super(message, cause);
	}
}
