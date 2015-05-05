/**
 * 暗号化失敗時のエラーです。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM　梅沢
 *
 */
public class EncryptionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7312150747967663011L;

	public EncryptionException() {
		super();
	}

	public EncryptionException(String message) {
		super(message);
	}

	public EncryptionException(Throwable cause) {
		super(cause);
	}

	public EncryptionException(String message, Throwable cause) {
		super(message, cause);
	}
}
