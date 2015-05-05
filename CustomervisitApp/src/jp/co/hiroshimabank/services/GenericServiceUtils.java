/**
 * サービスで利用するメソッドを集めたクラスです
 */
package jp.co.hiroshimabank.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.co.hiroshimabank.dao.UserStatusDAO;
import jp.co.hiroshimabank.db.DBAccessor;
import jp.co.hiroshimabank.dto.UserStatusDTO;
import jp.co.hiroshimabank.utils.Csrf;
import jp.co.hiroshimabank.utils.DateUtils;
import jp.co.hiroshimabank.utils.LogUtils;
import jp.co.hiroshimabank.utils.TokeNotDeletedException;
import jp.co.hiroshimabank.utils.TokenNotGeneratedException;
import jp.co.hiroshimabank.utils.TokenNotUpdatedException;
import jp.co.hiroshimabank.utils.UserNotLoginException;

/**
 * @author 日本IBM　梅沢
 *
 */
public class GenericServiceUtils {

	/**
	 * ログイン時に利用します。 ユーザステータステーブルにトークン、ユーザIDとアクセス時間を格納します。
	 * 
	 * @param userId
	 *            　ユーザID
	 * @return MapにユーザIDとトークンを発行して返します。
	 * @throws TokenNotGeneratedException
	 */
	public static Map<String, String> addToken(int userId)
			throws TokenNotGeneratedException {
		// ユーザIDとトークンを格納するためのMapです。
		Map<String, String> map = new HashMap<String, String>();

		LogUtils.print("トークン生成開始");
		map.put("userId", Integer.toString(userId));
		// トークンを発行させます
		String token = Csrf.getCsrfToken();
		map.put("token", token);

		UserStatusDTO status = new UserStatusDTO();
		status.setUserId(userId);
		status.setToken(token);
		Calendar calendar = DateUtils.getCalender();
		status.setAccessTime(DateUtils.getTimestamp(calendar));

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		int result = 0;
		try {

			UserStatusDAO dao = new UserStatusDAO(conn);
			// ユーザIDがユーザステータステーブルに格納されているか確認します。
			// ユーザIDがない場合ログインさせます。
			if (!dao.IsUserLogin(userId)) {
				// ユーザステータステーブルに登録します。
				result += dao.addUserStatus(status);
			} else {
				// ユーザIDがある場合
				// 30分以内ならばupdate 30分以上ならばdelete・insert
				if (dao.IsUserLoginInLimitedTime(status)) {
					// 30分以内の場合
					result += dao.updateUserStatus(status);
				} else {
					// 30分以上
					result += dao.deleteUserStatus(userId);
					result += dao.addUserStatus(status);
				}
			}
			if (result > 0) {
				conn.rollback();
				throw new TokenNotGeneratedException();
			} else {
				LogUtils.print("トークン生成終了");
				conn.commit();
			}

		} catch (SQLException e) {
			LogUtils.print("トークン生成失敗");
			LogUtils.print(e);
			try {
				conn.rollback();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
			throw new TokenNotGeneratedException();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
		}
		return map;
	}

	/**
	 * ログイン時以外でクライアントからリクエストがあったときにこのメソッドをかませます。 CookieのHBank
	 * Agentの値を取得して、それがテーブルにあれば （30分以内のもの）ユーザIDを取得します。トークンは発生させテーブルを更新します。(ToDo)
	 * ない場合30分たったとみなしてログイン画面に飛ばします。（エラーTokenNotUpdatedExceptionをなげます。）
	 * そもそもクッキーがなければ（nullで渡される。）エラーを投げます（エラーUserNotLoginException）
	 * 
	 * @param token
	 *            クライアントから送られてきたクッキー
	 * @return　MapにユーザIDとトークンを発行して返します。
	 */
	public static Map<String, String> addToken(String token) {

		if (token == null) {
			LogUtils.print("トークンが指定されていません");
			throw new UserNotLoginException();
		}
		// ユーザIDとトークンを格納するためのMapです。
		Map<String, String> map = new HashMap<String, String>();
		// テーブルにアクセスします。

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		int userId = 0;
		try {
			UserStatusDAO dao = new UserStatusDAO(conn);
			Calendar calendar = DateUtils.getCalender();
			userId = dao.IsUserLoginInLimitedTime(token,
					DateUtils.getTimestamp(calendar));
			if (userId == 0) {
				// 30分以内にユーザはログインしていなかった場合
				LogUtils.print("30分以内にログインしていません");
				conn.rollback();
				throw new TokenNotUpdatedException();
			} else {
				LogUtils.print("30分以内にログインしています");
				UserStatusDTO userStatusDto = new UserStatusDTO();
				userStatusDto.setUserId(userId);
				userStatusDto.setToken(Csrf.getCsrfToken());
				userStatusDto.setAccessTime(DateUtils.getTimestamp(calendar));
				int result = dao.updateUserStatus(userStatusDto);
				if(result == 0){
					conn.commit();
				} else{
					try {
						conn.rollback();
					} catch (Exception e2) {
						LogUtils.print(e2);
					}
					throw new TokenNotUpdatedException();
				}
				map.put("userId", Integer.toString(userId));
				map.put("token", userStatusDto.getToken());
				return map;
			}
		} catch (SQLException e) {
			LogUtils.print("トークン生成に失敗しました。");
			LogUtils.print(e);
			try {
				conn.rollback();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
			throw new TokenNotUpdatedException();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
		}
	}

	/**
	 * ログオフ時に利用します。テーブルからデータを削除します。
	 * 
	 * @param token
	 *            トークン
	 * @return 成功すれば0　失敗すれば1を返します。
	 * @throws TokeNotDeletedException
	 */
	public static int deleteStatus(String token) throws TokeNotDeletedException {

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		int result = 0;
		try {
			UserStatusDAO dao = new UserStatusDAO(conn);
			// ユーザステータスを削除します。
			result += dao.deleteUserStatus(token);
			if (result == 0) {
				// 成功
				conn.commit();
				LogUtils.print("ログオフ成功");
				return result;
			} else {
				// 失敗
				conn.rollback();
				LogUtils.print("ログオフ失敗");
				return result;
			}
		} catch (SQLException e) {
			LogUtils.print(e);
			try {
				conn.rollback();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
			throw new TokeNotDeletedException();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
		}

	}

	/**
	 * jsに渡すメッセージを作成します
	 * 
	 * @param boo
	 *            処理が成功したかどうか 成功：true 失敗:false
	 * @param map
	 *            jsに渡すデータ
	 * @return クライアントに渡すレスポンス　ステータスコード200
	 */
	// protected Response reply(Boolean boo, Map<String, Object> map) {
	// if (boo) {
	// // 成功
	// map.put("result", Boolean.TRUE);
	// map.put("success", Constants.SUCCESS);
	// return Response.ok(map).build();
	// } else {
	// // 失敗
	// map.put("result", Boolean.FALSE);
	// map.put("failure", Constants.FAILURE);
	// return Response.ok(map).build();
	// }
	// }

	/**
	 * jsに渡すメッセージを作成します
	 * 
	 * @param boo
	 *            検索結果が あり：true なし:false
	 * @param map
	 *            jsに渡すデータ
	 * @return クライアントに渡すレスポンス　ステータスコード200
	 */
	// protected Response replySearchResult(Boolean boo, Map<String, Object>
	// map,
	// List<Incident> incidents) {
	// map.put("incident", incidents);
	// if (boo) {
	// // 成功
	// map.put("result", Boolean.TRUE);
	// map.put("success", Constants.SUCCESS);
	// return Response.ok(map).build();
	// } else {
	// // 失敗
	// map.put("result", Boolean.FALSE);
	// map.put("failure", Constants.ZERO);
	// return Response.ok(map).build();
	// }
	// }
}
