/**
 * スケジュールに関連するリクエストを受け付けるクラスです。
 */
package jp.co.hiroshimabank.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jp.co.hiroshimabank.bean.Constant;
import jp.co.hiroshimabank.bean.Event;
import jp.co.hiroshimabank.dao.CustomerDAO;
import jp.co.hiroshimabank.dao.EventDAO;
import jp.co.hiroshimabank.db.DBAccessor;
import jp.co.hiroshimabank.dto.EventDTO;
import jp.co.hiroshimabank.utils.DateUtils;
import jp.co.hiroshimabank.utils.LogUtils;
import jp.co.hiroshimabank.utils.TokenNotUpdatedException;
import jp.co.hiroshimabank.utils.UserNotLoginException;

/**
 * @author 日本IBM 梅沢
 *
 */
@Path("/schedule")
public class ScheduleServices {

	@Context
	UriInfo uriInfo;
	
	/**
	 * イベントを複数件移動時間も込みでスケジュールに登録します。
	 * 
	 * @param customerId
	 *            お客様ID
	 * @param time
	 *            お客様会議時間(単位は分)
	 * @param duration
	 *            移動時間(単位は秒)
	 * @return
	 */
	@Path("/initevents")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response initializeSchedule(@FormParam("id") int[] customerId,
			@FormParam("time") int[] time, @FormParam("duration") int[] duration,@CookieParam("HBank Agent") String cookie) {
		
//		public Response initializeSchedule(@FormParam("id") int[] customerId,
//				@FormParam("time") int[] time, @FormParam("duration") int[] duration) {


		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
		int userId = 0;
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

		int visitCount = customerId.length;

		List<EventDTO> listevents = new ArrayList<EventDTO>();

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		try {
			List<EventDTO> tmp = new ArrayList<EventDTO>();
			Calendar calendar = DateUtils.getCalender();
			for (int i = 0; i < visitCount; i++) {
				if (i == 0) {
					listevents = generateEventDTO(conn, userId, customerId[i],
							time[i], duration[i],
							DateUtils.getTimestamp(calendar), calendar);
				} else {
					calendar.add(Calendar.MINUTE, DateUtils.INTERVALEVENT);
					tmp = generateEventDTO(conn, userId, customerId[i],
							time[i], duration[i],
							DateUtils.getTimestamp(calendar), calendar);
					for (int j = 0; j < tmp.size(); j++) {
						listevents.add(tmp.get(j));
					}
				}
			}
			EventDAO eventDao = new EventDAO(conn);
			eventDao.addEvents(listevents);
			conn.commit();
		} catch (Exception e) {
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
		return Response.ok().header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
//		return Response.ok().build();
	}

	/**
	 * イベントを一件移動時間も込みでスケジュールに登録します。
	 * 
	 * @param customerId
	 *            お客様ID
	 * @param time
	 *            お客様会議時間(単位は分)
	 * @param duration
	 *            移動時間(単位は秒)
	 * @return
	 */
	@Path("/initevent")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response initializeSchedule(@FormParam("id") int customerId,
			@FormParam("time") int time, @FormParam("duration") int duration,@CookieParam("HBank Agent") String cookie) {
		
//		public Response initializeSchedule(@FormParam("id") int customerId,
//				@FormParam("time") int time, @FormParam("duration") int duration) {

		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
		int userId = 0;
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
		try {
			// イベント登録
			List<EventDTO> listevents = new ArrayList<EventDTO>();
			Calendar calendar = DateUtils.getCalender();
			listevents = generateEventDTO(conn, userId, customerId, time,
					duration, DateUtils.getTimestamp(calendar), calendar);

			EventDAO eventDao = new EventDAO(conn);
			eventDao.addEvents(listevents);

			conn.commit();
		} catch (Exception e) {
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
//		return Response.ok().build();
		return Response.ok().header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
	}

	/**
	 * 指定した期間のイベント情報をすべて取得します。
	 * 
	 * @param start
	 *            指定した期間開始時間(ミリ秒)
	 * @param end
	 *            指定した期間終了時間(ミリ秒)
	 * @return
	 */
	@Path("/event")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response initializeSchedule(@FormParam("start") String start,
			@FormParam("end") String end,@CookieParam("HBank Agent") String cookie) {
		
//		public Response initializeSchedule(@FormParam("start") String start,
//				@FormParam("end") String end) {

		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
		int userId = 0;
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
		
		
		List<EventDTO> events = new ArrayList<EventDTO>();

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		try {
			EventDAO eventDao = new EventDAO(conn);
			// イベント取得
			Calendar calendar = DateUtils.getCalender();
			calendar.setTimeInMillis(Long.parseLong(start));
			Timestamp startTime = DateUtils.getTimestamp(calendar);
			calendar.setTimeInMillis(Long.parseLong(end));
			Timestamp endTime = DateUtils.getTimestamp(calendar);
			events = eventDao.getEventLists(userId, startTime, endTime);
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
		// 画面Beanに変換
		List<Event> listEvent = convertEvetList(events);
//		return Response.ok(listEvent).build();
		return Response.ok(listEvent).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
	}

	/**
	 * イベント情報を更新します。
	 * 
	 * @param eventId
	 *            イベントID
	 * @param title
	 *            イベントタイトル
	 * @param start
	 *            イベント開始時間
	 * @param end
	 *            イベント終了時間
	 * @return
	 */
	@Path("/update")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateSchedule(@FormParam("id") String eventId,
			@FormParam("title") String title, @FormParam("start") String start,
			@FormParam("end") String end,@CookieParam("HBank Agent") String cookie) {
//		public Response updateSchedule(@FormParam("id") String eventId,
//				@FormParam("title") String title, @FormParam("start") String start,
//				@FormParam("end") String end) {

		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
		int userId = 0;
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
		
		// 入力チェック
		if (title == null || "".equals(title)) {
			// タイトルがnullもしくは空ならばエラー
//			return Response.status(400).build();
			return Response.status(400).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}
		if (DateUtils.toDateFromString(start, DateUtils.DATE_FORM_007) == null) {
			// フォーマットが指定以外ならばエラー
//			return Response.status(400).build();
			return Response.status(400).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}
		if (DateUtils.toDateFromString(end, DateUtils.DATE_FORM_007) == null) {
			// フォーマットが指定以外ならばエラー
//			return Response.status(400).build();
			return Response.status(400).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}
		if (DateUtils.toDateFromString(start, DateUtils.DATE_FORM_007)
				.getTime() > DateUtils.toDateFromString(end,
				DateUtils.DATE_FORM_007).getTime()) {
			// スタート時間がエンド時間よりも未来ならばエラー
//			return Response.status(400).build();
			return Response.status(400).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		try {
			EventDTO eventDto = new EventDTO();
			eventDto.setEventId(eventId);
			eventDto.setUserId(userId);
			eventDto.setEventName(title);

			Calendar calendar = DateUtils.getCalender();
			calendar.setTime(DateUtils.toDateFromString(start,
					DateUtils.DATE_FORM_007));
			eventDto.setEventStartTime(DateUtils.getTimestamp(calendar));
			calendar.setTime(DateUtils.toDateFromString(end,
					DateUtils.DATE_FORM_007));
			eventDto.setEventEndTime(DateUtils.getTimestamp(calendar));
			EventDAO eventDao = new EventDAO(conn);
			// イベント更新
			eventDao.updateEvent(eventDto);
			conn.commit();
		} catch (Exception e) {
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
//		return Response.ok().build();
		return Response.ok().header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
	}

	/**
	 * イベント詳細情報を取得します。
	 * 
	 * @param eventId
	 *            イベントID
	 * @return
	 */
	@Path("/detail")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEventDetail(@FormParam("eventId") String eventId,@CookieParam("HBank Agent") String cookie) {
//		public Response getEventDetail(@FormParam("eventId") String eventId) {
		// ユーザがログイン済みか、ログインしてから30分たっているか確認します。
		int userId = 0;
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

		// 入力チェック
		if (eventId == null || "".equals(eventId)) {
			// タイトルがnullもしくは空ならばエラー
//			return Response.status(400).build();
			return Response.status(400).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
		}

		DBAccessor accessor = new DBAccessor();
		Connection conn = accessor.getConnection();
		EventDTO eventDto = new EventDTO();
		try {
			EventDAO eventDao = new EventDAO(conn);
			// イベント更新
			eventDto = eventDao.getEventDetail(eventId, userId);
			conn.commit();
		} catch (Exception e) {
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
//		return Response.ok(eventDto).build();
		return Response.ok(eventDto).header("Set-Cookie", updatedcookie.toString() + ";HttpOnly").build();
	}

	/**
	 * DTOからBeanに変換します。
	 * 
	 * @param events
	 *            イベントリスト
	 * @return 表示するイベントリスト
	 */
	private List<Event> convertEvetList(List<EventDTO> events) {
		List<Event> listEvent = new ArrayList<Event>();
		for (EventDTO dto : events) {
			Event event = new Event();
			event.setId(dto.getEventId());
			event.setTitle(dto.getEventName());
			event.setStart(DateUtils.getDate2(dto.getEventStartTime()));
			event.setEnd(DateUtils.getDate2(dto.getEventEndTime()));
			event.setTextColor(Constant.TEXTCOLOR);
			event.setBackgroundColor(event.generateColor(event.getEnd(),
					event.getId()));
			event.setBorderColor(event.generateColor(event.getEnd(),
					event.getId()));
			event.setEditable(false);
			listEvent.add(event);
		}
		return listEvent;
	}

	/**
	 * 追加するイベントのリストを作成します。
	 * 
	 * @param conn
	 *            DBとのコネクション
	 * @param userId
	 *            ユーザID
	 * @param customerId
	 *            お客様Id
	 * @param time
	 *            お客様会議時間(単位は分)
	 * @param duration
	 *            移動時間(単位は秒)
	 * @param startTime
	 *            イベント(移動＋イベント)開始時間
	 * @return
	 */
	private List<EventDTO> generateEventDTO(Connection conn, int userId,
			int customerId, int time, int duration, Timestamp startTime,
			Calendar calendar) throws SQLException {
		List<EventDTO> listevents = new ArrayList<EventDTO>();

		// お客様住所名前取得
		CustomerDAO customerDao = new CustomerDAO(conn);
		String address = customerDao.getCustomerAddressName(customerId)
				.getCustomerAddress();
		String name = customerDao.getCustomerAddressName(customerId)
				.getCustomerName();
		// イベント登録
		// 移動イベント
		EventDTO moveEventDto = new EventDTO();
		moveEventDto.setEventId(moveEventDto.generateId("move", customerId));
		moveEventDto.setUserId(userId);
		moveEventDto.setEventStartTime(startTime);

		calendar.add(Calendar.SECOND, duration);
		moveEventDto.setEventEndTime(DateUtils.getTimestamp(calendar));
		moveEventDto.setEventLocation(address);
		moveEventDto.setEventName(setEventName("move", name));
		listevents.add(moveEventDto);

		// 会議イベント
		EventDTO meetingEvent = new EventDTO();
		meetingEvent.setEventId(meetingEvent.generateId("meeting", customerId));
		meetingEvent.setUserId(userId);
		calendar.add(Calendar.MINUTE, DateUtils.INTERVALMOVE);
		meetingEvent.setEventStartTime(DateUtils.getTimestamp(calendar));
		calendar.add(Calendar.MINUTE, time);
		meetingEvent.setEventEndTime(DateUtils.getTimestamp(calendar));
		meetingEvent.setEventLocation(address);
		meetingEvent.setEventName(setEventName("meeting", name));
		listevents.add(meetingEvent);

		return listevents;
	}

	/**
	 * イベントの名前を作成します。
	 * 
	 * @param eventType
	 *            イベントタイプ move or meeting
	 * @param customerName
	 *            お客様名
	 * @return
	 */
	private String setEventName(String eventType, String customerName) {
		String eventName = "";
		if ("move".equals(eventType)) {
			return eventName + customerName + "様先へ　" + Constant.MOVE;
		} else if ("meeting".equals(eventType)) {
			return eventName + customerName + "様先へ　" + Constant.MEETING;
		} else {
			return eventName;
		}

	}
}
