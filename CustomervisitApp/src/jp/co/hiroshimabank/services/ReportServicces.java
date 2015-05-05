/**
 * レポートを送信するためのクラスです。
 */
package jp.co.hiroshimabank.services;

import java.util.Map;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jp.co.hiroshimabank.mail.MailAccessor;
import jp.co.hiroshimabank.mail.SendGrid;
import jp.co.hiroshimabank.mail.SendGridException;
import jp.co.hiroshimabank.utils.LogUtils;
import jp.co.hiroshimabank.utils.TokenNotUpdatedException;
import jp.co.hiroshimabank.utils.UserNotLoginException;


/**
 * @author 日本IBM 梅沢
 *
 */
@Path("/report")
public class ReportServicces {

	@Context
	UriInfo uriInfo;
	
	private String fromMailAddress = "e35372@jp.ibm.com";
	
	@GET
	public Response sendReport(@CookieParam("HBank Agent") String cookie){
//		public Response sendReport(){
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

		
		MailAccessor access = new MailAccessor();
		SendGrid sendgrid = new SendGrid(access.getUsername(),
				access.getPassword());
		SendGrid.Email email = new SendGrid.Email();
		email.addTo("e35372@jp.ibm.com");
		email.setFrom(fromMailAddress);
		email.setSubject("訪問結果報告");
		email.setText("完了致しました。");

		try {
			SendGrid.Response response = sendgrid.send(email);
		} catch (SendGridException e) {
//			return Response.status(500).build();
			return Response.status(500).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}
		return Response.ok().header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
//		return Response.ok().build();
	}
}
