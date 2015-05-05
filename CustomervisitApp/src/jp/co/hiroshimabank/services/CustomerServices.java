/**
 * 顧客情報を扱うクラスです。
 */
package jp.co.hiroshimabank.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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

import jp.co.hiroshimabank.dao.AccountDAO;
import jp.co.hiroshimabank.dao.CustomerDAO;
import jp.co.hiroshimabank.db.DBAccessor;
import jp.co.hiroshimabank.dto.AccountDTO;
import jp.co.hiroshimabank.dto.CustomerDTO;
import jp.co.hiroshimabank.utils.LogUtils;
import jp.co.hiroshimabank.utils.TokenNotUpdatedException;
import jp.co.hiroshimabank.utils.UserNotLoginException;


/**
 * @author 日本IBM 梅沢
 *
 */
@Path("/customer")
public class CustomerServices {
	
	@Context
	UriInfo uriInfo;

	/**
	 * ログインユーザが担当する顧客のリストを取得します。
	 * 
	 * @return 顧客リスト
	 */
	@Path("/list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerList(@CookieParam("HBank Agent") String cookie) {
//		public Response getCustomerList() {
		
		System.out.println(uriInfo.getBaseUri().getHost());
		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
		int userId = 0;
//		int userId = 10000000;
		NewCookie updatedcookie=null;
		try {
			Map<String, String> token = GenericServiceUtils.addToken(cookie);
			// クッキーに入っている値を初期化します。
			updatedcookie = new NewCookie("HBank Agent", token.get("token"),
					"/Customervist/", uriInfo.getBaseUri().getHost(), null, 30*60, false);
			userId = Integer.parseInt(token.get("userId"));
			LogUtils.print("トークン発行 :" + updatedcookie);
		} catch (TokenNotUpdatedException e) {
			//return Response.status(401).build();
			return Response.status(401).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		} catch (UserNotLoginException e) {
			//return Response.status(401).build();
			return Response.status(401).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();

		List<CustomerDTO> cutomerDto = null;

		try {
			CustomerDAO cutomerDao = new CustomerDAO(conn);
			cutomerDto = cutomerDao.getCustomerList(userId);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
			return Response.status(500).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
//			return Response.status(500).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
		}
		return Response.ok(cutomerDto).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
//		return Response.ok(cutomerDto).build();
	}

	/**
	 * お客様の口座情報を取得します。
	 * 
	 * @param customerId
	 * @return
	 */
	@Path("/accounts")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccountList(@FormParam("eventId") String eventId,@CookieParam("HBank Agent") String cookie) {
//		public Response getAccountList(@FormParam("eventId") String eventId) {
		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
//		int userId = 0;
		NewCookie updatedcookie=null;
		try {
			Map<String, String> token = GenericServiceUtils.addToken(cookie);
			// クッキーに入っている値を初期化します。
			updatedcookie = new NewCookie("HBank Agent", token.get("token"),
					"/Customervist/", uriInfo.getBaseUri().getHost(), null, 30*60, false);
//			userId = Integer.parseInt(token.get("userId"));
			LogUtils.print("トークン発行 :" + updatedcookie);
		} catch (TokenNotUpdatedException e) {
			//return Response.status(401).build();
			return Response.status(401).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		} catch (UserNotLoginException e) {
			//return Response.status(401).build();
			return Response.status(401).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();

		List<AccountDTO> accountDto = null;
		// イベントIDからCustomerIdを取得する。
		int customerId = Integer.parseInt(eventId.substring(7, 15));

		try {
			AccountDAO accountDao = new AccountDAO(conn);
			accountDto = accountDao.getAccount(customerId);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
//			return Response.status(500).build();
			return Response.status(500).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				LogUtils.print(e2);
			}
		}
//		return Response.ok(accountDto).build();
		return Response.ok(accountDto).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
	}
}
