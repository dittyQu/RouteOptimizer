/**
 * トークンを発生させたときに起こる例外です。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM　梅沢
 *
 */
public class TokenNotGeneratedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4866391429115249904L;

	public TokenNotGeneratedException() {
		super();
	}

	public TokenNotGeneratedException(String message) {
		super(message);
	}

	public TokenNotGeneratedException(Throwable cause) {
		super(cause);
	}

	public TokenNotGeneratedException(String message, Throwable cause) {
		super(message, cause);
	}

}
