/**
 * ひとつのスケジュールを格納したクラスです。
 */
package jp.co.hiroshimabank.dto;

import java.sql.Timestamp;

import jp.co.hiroshimabank.utils.EventUtils;


/**
 * @author 日本IBM 梅沢
 *
 */
public class EventDTO extends EventUtils{
	
	/**イベントID*/
	private String eventId;
	/**ユーザID*/
	private int userId;
	/**イベント開始時間*/
	private Timestamp eventStartTime;
	/**イベント終了時間*/
	private Timestamp eventEndTime;
	/**イベント開催地*/
	private String eventLocation;
	/**イベント名*/
	private String eventName;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Timestamp getEventStartTime() {
		return eventStartTime;
	}
	public void setEventStartTime(Timestamp eventStartTime) {
		this.eventStartTime = eventStartTime;
	}
	public Timestamp getEventEndTime() {
		return eventEndTime;
	}
	public void setEventEndTime(Timestamp eventEndTime) {
		this.eventEndTime = eventEndTime;
	}
	public String getEventLocation() {
		return eventLocation;
	}
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
}
