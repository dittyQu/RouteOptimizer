/**
 * ログを管理するためのクラスです。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM 梅沢
 *
 */
public class LogUtils {
	
	/**
	 * エラー時のスタックトレースを表示させます。
	 * @param e エラー
	 */
	public static void print(Exception e){
		e.printStackTrace();
	}
	
	/**
	 * オブジェクトを表示させます。
	 * @param obj オブジェクト
	 */
	public static void print(Object obj){
		System.out.println(obj.toString());
	}
}
