/**
 * トークンを更新できないときに発生します。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM　梅沢
 *
 */
public class TokenNotUpdatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2112075884501271822L;

	public TokenNotUpdatedException() {
		super();
	}

	public TokenNotUpdatedException(String message) {
		super(message);
	}

	public TokenNotUpdatedException(Throwable cause) {
		super(cause);
	}

	public TokenNotUpdatedException(String message, Throwable cause) {
		super(message, cause);
	}

}
