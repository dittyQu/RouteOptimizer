/**
 * イベントテーブルにアクセスするためのクラスです
 */
package jp.co.hiroshimabank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.co.hiroshimabank.db.RecordAlreadyExistException;
import jp.co.hiroshimabank.db.RecordNotFoundException;
import jp.co.hiroshimabank.dto.EventDTO;

/**
 * @author 日本IBM 梅沢
 *
 */
public class EventDAO {

	private Connection conn;

	public EventDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * イベントを一件追加します。
	 * 
	 * @param event
	 *            登録したいイベント
	 * @return 成功したらtrue 失敗したらRecordAlreadyExistExceptionをなげます
	 * @throws SQLException
	 */
	public boolean addEvent(EventDTO event) throws SQLException {
		PreparedStatement stmt = null;
		int result = 0;
		String sql = SQL.insertEventSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, event.getEventId());
		stmt.setInt(2, event.getUserId());
		stmt.setTimestamp(3, event.getEventStartTime());
		stmt.setTimestamp(4, event.getEventEndTime());
		stmt.setString(5, event.getEventLocation());
		stmt.setString(6, event.getEventName());

		result = stmt.executeUpdate();

		if (result == 1) {
			return true;
		} else {
			throw new RecordAlreadyExistException();
		}
	}

	/**
	 * イベントを複数件追加します。
	 * 
	 * @param events
	 *            登録したいイベント
	 * @throws SQLException
	 */
	public void addEvents(List<EventDTO> events) throws SQLException {
		for (EventDTO event : events) {
			if (addEvent(event)) {
				continue;
			}
		}
	}

	/**
	 * 指定した期間のイベントを全て取得します。
	 * 
	 * @param userId
	 *            ユーザID
	 * @return 当日のイベント全て
	 * @throws SQLException
	 */
	public List<EventDTO> getEventLists(int userId, Timestamp start,
			Timestamp end) throws SQLException {
		PreparedStatement stmt = null;
		List<EventDTO> events = new ArrayList<EventDTO>();
		String sql = SQL.selectEventListSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, userId);
		stmt.setTimestamp(2, end);
		stmt.setTimestamp(3, start);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			EventDTO event = new EventDTO();
			event.setEventId(rs.getString("ID"));
			event.setUserId(rs.getInt("USER_ID"));
			event.setEventStartTime(rs.getTimestamp("START"));
			event.setEventEndTime(rs.getTimestamp("END"));
			event.setEventLocation(rs.getString("ADDRESS"));
			event.setEventName(rs.getString("NAME"));
			events.add(event);
		}
		return events;
	}

	/**
	 * イベントを一件更新します。
	 * 
	 * @param event
	 *            イベント
	 * @return 更新に成功するとtrue失敗するとRecordNotFoundExceptionを返します。
	 * @throws SQLException
	 */
	public boolean updateEvent(EventDTO event) throws SQLException {
		int result = 0;
		PreparedStatement stmt = null;
		String sql = SQL.updateEventSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, event.getEventName());
		stmt.setTimestamp(2, event.getEventStartTime());
		stmt.setTimestamp(3, event.getEventEndTime());
		stmt.setString(4, event.getEventId());
		stmt.setInt(5, event.getUserId());

		result = stmt.executeUpdate();

		if (result == 1) {
			return true;
		} else {
			throw new RecordNotFoundException();
		}
	}

	/**
	 * イベント情報を取得します。
	 * 
	 * @param eventId
	 *            イベントID
	 * @param userId
	 *            ユーザID
	 * @return イベント情報を格納したDTO
	 * @throws SQLException
	 */
	public EventDTO getEventDetail(String eventId, int userId)
			throws SQLException {
		PreparedStatement stmt = null;
		EventDTO eventDto = new EventDTO();
		String sql = SQL.selectEventSQL.getSql();
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, eventId);
		stmt.setInt(2, userId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			eventDto.setEventId(rs.getString("ID"));
			eventDto.setUserId(rs.getInt("USER_ID"));
			eventDto.setEventStartTime(rs.getTimestamp("START"));
			eventDto.setEventEndTime(rs.getTimestamp("END"));
			eventDto.setEventLocation(rs.getString("ADDRESS"));
			eventDto.setEventName(rs.getString("NAME"));
		}
		return eventDto;
	}
}
