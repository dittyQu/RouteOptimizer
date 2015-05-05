/**
 * DBにアクセスする際におきるエラーです。
 */
package jp.co.hiroshimabank.db;

/**
 * @author IBM梅沢
 *
 */
public class DbAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DbAccessException() {
		super();
	}

	public DbAccessException(String message) {
		super(message);
	}

	public DbAccessException(Throwable cause) {
		super(cause);
	}

	public DbAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
