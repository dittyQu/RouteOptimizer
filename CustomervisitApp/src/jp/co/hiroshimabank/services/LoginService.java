/**
 * ユーザがログオンする際のロジッククラスです。
 */
package jp.co.hiroshimabank.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jp.co.hiroshimabank.dao.UserAuthDAO;
import jp.co.hiroshimabank.db.DBAccessor;
import jp.co.hiroshimabank.utils.CharacterUtils;
import jp.co.hiroshimabank.utils.LogUtils;
import jp.co.hiroshimabank.utils.TokenNotGeneratedException;

/**
 * @author IBM梅沢
 *
 */
@Path("/")
public class LoginService {

	@Context
	UriInfo uriInfo;
	
	/**
	 * ログイン時に呼び出されるメソッドです。
	 * 
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormParam("user_id") int userId,
			@FormParam("user_pwd") String userPwd) {

		// Token情報を保有するmapです
		Map<String, String> token = new HashMap<String, String>();
		LogUtils.print("ログイン");
		LogUtils.print("UserId:" + userId);
		LogUtils.print("UserPassword:" + userPwd);

		// 入力チェック
		// ユーザID
		if ("".equals(userId)
				|| CharacterUtils.isSpecialCharacters(Integer.toString(userId))) {
			// JAX-RSが数値以外の入力があると自動で400を返しているがユーザIDが文字列に変更になる可能性もあるので記載しておく
			LogUtils.print("userId Bad Request");
			return Response.status(400).build();
		}
		// パスワード
		if (userPwd == null || "".equals(userPwd)
				|| CharacterUtils.isSpecialCharacters(userPwd)) {
			LogUtils.print("userId Bad Request");
			return Response.status(400).build();
		}

		//TODO 認証のロジックこれでよいか再確認 認証とトークン作成でconnを分けてよいか
		// 認証
		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		UserAuthDAO dao = new UserAuthDAO(conn);
		Boolean result = false;
		try {
			result = dao.IsUserExist(userId, userPwd);
			conn.commit();
		} catch (SQLException e1) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
			return Response.status(500).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
		}

		// 認証が成功ならばトークンを発行します。
		if (result) {
			try {
				token = GenericServiceUtils.addToken(userId);
				LogUtils.print("トークン発行 :" + token);
			} catch (TokenNotGeneratedException e) {
				return Response.status(500).build();
			}
			NewCookie cookie = new NewCookie("HBank Agent", token.get("token"),
					"/Customervist/", uriInfo.getBaseUri().getHost(), null, 30*60, false);

			return Response.ok()
					.header("Set-Cookie", cookie.toString() + ";HttpOnly")
					.build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * ログオフ時に呼び出されるメソッドです。
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@CookieParam("HBank Agent") String cookie) {

		LogUtils.print("ログオフ");
		System.out.println(cookie);
		// deleteに失敗してもログオフします。
		GenericServiceUtils.deleteStatus(cookie);

		// クッキーに入っている値を初期化します。
		NewCookie delcookie = new NewCookie("HBank Agent", cookie,
				"/Customervist/", uriInfo.getBaseUri().getHost(), null, 0, false);
		return Response.ok()
				.header("Set-Cookie", delcookie.toString() + ";HttpOnly")
				.build();
	}

	/**
	 * 画面遷移時に呼び出されるメソッドです。
	 * 
	 * @return
	 */
	// @Path("/go")
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response go(@CookieParam("JP Agent") String cookie) {
	//
	// // ユーザがログイン済みか、ログインしてから30分たっているか確認します。
	// try {
	// addToken(cookie);
	// return Response.ok().build();
	// } catch (TokenNotUpdatedException e) {
	// return Response.status(401).build();
	// } catch (UserNotLoginException e) {
	// return Response.status(401).build();
	// }
	// }
}
