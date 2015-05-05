/**
 * ハッシュ化に失敗した場合に発生します
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM 梅沢
 *
 */
public class HashNotGeneratedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1481004586217191747L;

	public HashNotGeneratedException() {
		super();
	}

	public HashNotGeneratedException(String message) {
		super(message);
	}

	public HashNotGeneratedException(Throwable cause) {
		super(cause);
	}

	public HashNotGeneratedException(String message, Throwable cause) {
		super(message, cause);
	}

}
