/**
 * 暗号化復号化の共通処理です。
 */
package jp.co.hiroshimabank.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author 日本IBM　梅沢
 *
 */
public class EnDecryptionUtils {

	private static final String ALGORITHM = "AES";
	private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
	protected static final String STRING_ENCODING = "UTF-8";
	private static final String ENCRYPT_KEY = "1234567890123456";
	private static final String ENCRYPT_IV = "abcdefghijklmnop";
//	private static byte[] keyBytes;

	/**
	 * 暗号化及び復号化する際の共通メソッドです。
	 * @param targetStringBytes 暗号及び複合したい対象のバイト配列です。
	 * @param mode 暗号化か復号化を判断します。
	 * @return 暗号化及び復号化した際のバイト配列を返します
	 */
	protected static byte[] endecrypt(byte[] targetStringBytes, int mode) {

		//暗号化及び復号化した後に格納
		byte[] byteValue;
		try {
			byte[] keyBytes = ENCRYPT_KEY.getBytes(STRING_ENCODING);
			//キーを乱数生成する場合
//			if (mode == Cipher.ENCRYPT_MODE) {
//				keyBytes = genKey().getEncoded();
//			}
			byte[] ivBytes = ENCRYPT_IV.getBytes(STRING_ENCODING);

			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(ivBytes);

			//Cipherオブジェクトの生成
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			//Cipherオブジェクトの初期化
			cipher.init(mode, keySpec, iv);
			byteValue = cipher.doFinal(targetStringBytes);
			System.out.println(byteValue.length);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return byteValue;
	}

	/**
	 * エンコードします。
	 * @param targetStringBytes エンコードしたい対象のバイト配列
	 * @return エンコード後の文字列
	 */
	protected static String encode(byte[] targetStringBytes) {
		String encryptedValue = null;
		BASE64Encoder base64Encoder = new BASE64Encoder();
		encryptedValue = base64Encoder.encode(targetStringBytes);
		return encryptedValue;
	}

	/**
	 * デコードします。
	 * @param encryptedString エンコードされた対象のバイト配列
	 * @return デコード後のバイト配列
	 * @throws IOException デコードに失敗すると発生します。
	 */
	protected static byte[] decode(String encryptedString) throws IOException {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] encryptedBytes = base64Decoder.decodeBuffer(encryptedString);
		return encryptedBytes;
	}

	/**
	 * キーをランダム生成します。
	 * @return　キー
	 * @throws NoSuchAlgorithmException
	 */
	private static Key genKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		generator.init(128, random);
		Key key = generator.generateKey();
		return key;
	}

}
