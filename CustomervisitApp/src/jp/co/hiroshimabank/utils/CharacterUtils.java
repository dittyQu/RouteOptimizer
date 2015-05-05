package jp.co.hiroshimabank.utils;


public class CharacterUtils {

	private static final String EMPTY_SRING = "";

	private static final char[] SPECIAL_CHARACTERS = { '<', '>', '\\', '&',
			'\'', '%', '(', ')', '+', ':', ';', '`' };

	/**
	 * 引数の値にXSS脆弱性、SQL Injectionを起こす可能性のある特殊文字が入っている場合はtrueを返します。
	 */
	public static final boolean isSpecialCharacters(String str) {
		if (str == null || EMPTY_SRING.equals(str)) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			for (int j = 0; j < SPECIAL_CHARACTERS.length; j++) {
				if (c == SPECIAL_CHARACTERS[j]) {
					return true;
				}
			}
		}
		return false;

	}
}
