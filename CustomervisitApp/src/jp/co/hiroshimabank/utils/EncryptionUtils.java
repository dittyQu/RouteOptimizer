/**
 * 暗号化を行うクラスです。
 */
package jp.co.hiroshimabank.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

/**
 * @author 日本IBM　梅沢
 *
 */
public class EncryptionUtils extends EnDecryptionUtils {

	private static final String FIXEDSALT = "zplw93k8jf7jv3n18udta6730kd30apow2918kld";
	private static int STRETCH_COUNT = 1000;

	/**
	 * AESにより与えた文字列を暗号化します。
	 * @param targetString 暗号化したい文字列
	 * @return 暗号化文字列 引数がnullもしくは暗号化に失敗した場合EncryptionExceptionが発生します。
	 */
	public static String encryptString(String targetString) {

		String encryptedValue = "";

		//入力チェック
		if (targetString == null) {
			throw new EncryptionException();
		}
		try {
			byte[] targetStringBytes = targetString.getBytes(STRING_ENCODING);
			encryptedValue = encode(endecrypt(targetStringBytes, Cipher.ENCRYPT_MODE));
		} catch (UnsupportedEncodingException | RuntimeException e) {
			throw new EncryptionException();
		}
		return encryptedValue;
	}

	/**
	 * 渡された文字列をMD5によりハッシュ値へと変換します。
	 *
	 * @param targetString
	 *            処理対象の文字列です。
	 * @return MD5によるハッシュ値です。
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static String getMD5(String targetString) {
		//		String md5Pwd = "";
		StringBuffer buf = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(targetString.getBytes());
			byte[] md5Byte = md.digest();

			//TODO
			//バイト配列をどう扱うか
			//			BASE64Encoder base64Encoder = new BASE64Encoder();
			//			md5Pwd = base64Encoder.encode(md5Byte);
			//			md5Pwd = md5Byte.toString();
			for (int i = 0; i < md5Byte.length; i++) {
				buf.append(String.format("%02x", md5Byte[i]));
			}

		} catch (NoSuchAlgorithmException e) {
			throw new HashNotGeneratedException(e);
		}
		return buf.toString();
	}

	/**
	 * 渡された文字列をSHA-256によりハッシュ値へと変換します。
	 * @param targetString 処理対象の文字列です
	 * @return SHA-256によるハッシュ値です。
	 */
	private static String getSha256(String targetString) {
		MessageDigest md = null;
		StringBuffer buf = new StringBuffer();
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(targetString.getBytes());
			byte[] digest = md.digest();

			for (int i = 0; i < digest.length; i++) {
				buf.append(String.format("%02x", digest[i]));
			}
		} catch (NoSuchAlgorithmException e) {
			throw new HashNotGeneratedException(e);
		}
		return buf.toString();
	}

	/**
	 * saltを利用してパスワードをハッシュ化します。
	 * @param password ユーザパスワード
	 * @param userId ユーザID
	 * @return ハッシュ化された値
	 */
	@Deprecated
	public static String getSaltedPassword(String password, String userId) {
		String salt = getSha256(userId);
		return getSha256(salt + FIXEDSALT + password);
	}

	/**
	 * saltとストレッチングを利用してパスワードをハッシュ化します。
	 * ストレッチングを利用しているのでレスポンスが遅くなる危険性あり。その場合は修正をお願いします。//このコメントは必要なくなりましたら削除お願いします。
	 * @param password ユーザパスワード
	 * @param userId ユーザID
	 * @return ハッシュ化された値
	 */
	public static String getStretchedPassword(String password, String userId) {
		String salt = getSha256(userId);
		String hash = "";
		for (int i = 0; i < STRETCH_COUNT; i++) {
			hash = getSha256(hash + salt + FIXEDSALT + password);
		}
		return hash;
	}

	/**
	 *ユーザ認証の際に利用します。ハッシュ化された値同士同じ値かチェックします。
	 * @param password ユーザパスワード
	 * @param userId ユーザID
	 * @param hashData DB等に格納されているハッシュ化されたハッシュ値
	 * @return ユーザ認証成功すればtrue、認証失敗すればfalseを返します
	 */
	public static Boolean compareString(String password, int userId, String hashData) {
		//入力チェック
		if (password == null | hashData == null) {
			return false;
		}
		String strUserId = Integer.toString(userId);
		if (hashData.equals(getStretchedPassword(password, strUserId))) {
			return true;
		} else {
			return false;
		}
	}
}
