/**
 * データが既に登録されている場合におきるExceptionです
 */
package jp.co.hiroshimabank.db;

/**
 * @author 日本IBM　梅沢
 *
 */
public class RecordAlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -427429176778136223L;

	public RecordAlreadyExistException() {
		super();
	}

	public RecordAlreadyExistException(String message) {
		super(message);
	}

	public RecordAlreadyExistException(Throwable cause) {
		super(cause);
	}

	public RecordAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
