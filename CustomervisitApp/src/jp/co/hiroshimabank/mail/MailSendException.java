/**
 * メール送信に失敗した場合の例外です。
 */
package jp.co.hiroshimabank.mail;

/**
 * @author 日本IBM　梅沢
 *
 */
public class MailSendException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5240501560359678333L;

	public MailSendException() {
		super();
	}

	public MailSendException(String message) {
		super(message);
	}

	public MailSendException(Throwable cause) {
		super(cause);
	}

	public MailSendException(String message, Throwable cause) {
		super(message, cause);
	}
}
