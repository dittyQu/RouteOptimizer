/**
 * 復号化を行うクラスです
 */
package jp.co.hiroshimabank.utils;

import java.io.IOException;

import javax.crypto.Cipher;

/**
 * @author 日本IBM　梅沢
 *
 */
public class DecryptionUtils extends EnDecryptionUtils {

	/**
	 * AESにより与えた暗号を複合化します
	 * @param encryptedString 復号化したい暗号
	 * @return 復号化文字列
	 */
	public static String decryptString(String encryptedString) {

		String decryptedString = "";

		//入力チェック
		if (encryptedString == null) {
			throw new DecryptionException();
		}
		try {
			decryptedString = new String(endecrypt(decode(encryptedString), Cipher.DECRYPT_MODE), STRING_ENCODING);
		} catch (IOException | RuntimeException e) {
			throw new DecryptionException();
		}
		return decryptedString;
	}

}
