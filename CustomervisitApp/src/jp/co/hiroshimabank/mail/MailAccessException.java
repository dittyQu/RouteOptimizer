/**
 * SendGridにアクセスする際におきるエラーです。
 */
package jp.co.hiroshimabank.mail;

/**
 * @author IBM梅沢
 *
 */
public class MailAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MailAccessException() {
		super();
	}

	public MailAccessException(String message) {
		super(message);
	}

	public MailAccessException(Throwable cause) {
		super(cause);
	}

	public MailAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
