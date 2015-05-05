/**
 * 32桁のトークンを発生させます
 */
package jp.co.hiroshimabank.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author 日本IBM　梅沢
 *
 */
public class Csrf {

	/** トークンの長さ */
	private static int TOKEN_LENGTH = 64;// 16*2=32バイト

	/**
	 * 32バイトのCSRFトークンを作成
	 * 
	 * @return
	 * @throws TokenNotGeneratedException
	 */
	public static String getCsrfToken() throws TokenNotGeneratedException {
		byte token[] = new byte[TOKEN_LENGTH];
		StringBuffer buf = new StringBuffer();
		SecureRandom random = null;

		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(token);

			for (int i = 0; i < token.length; i++) {
				buf.append(String.format("%02x", token[i]));
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new TokenNotGeneratedException();
		}

		return buf.toString();
	}
}
