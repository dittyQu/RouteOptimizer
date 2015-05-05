/**
 * イベントクラスで使用するユーティリティです。
 */
package jp.co.hiroshimabank.utils;

/**
 * @author 日本IBM 梅沢
 *
 */
public class EventUtils {

	/**
	 * イベントIDを生成します。
	 * 
	 * @param schedule
	 *            スケジュール
	 * @param customerId
	 *            お客様ID
	 * @return ID スケジュール＋お客様ID＋yyyyMMdd
	 */
	public String generateId(String schedule, int customerId) {
		return schedule + customerId
				+ DateUtils.toStringFromDate(DateUtils.DATE_FORM_006);
	}

	/**
	 * 色を返します
	 * 
	 * @param end
	 *            イベント終了時間
	 * @param id
	 *            イベントID
	 * @return 
	 *         イベント終了時間が現在よりも過去ならば色を#c0c0c0、イベント終了時間が現在よりも未来でIDがmeetingで始まるならば#ff80ff
	 *         、イベント終了時間が現在よりも未来でIDがmeetingで始まるならば#F0FFFF
	 */
	public String generateColor(String end, String id) {
		if (DateUtils.compareDate(end)) {
			// 未来
			return makeColor(id);
		} else {
			// 過去
			return "#c0c0c0";
		}
	}

	/**
	 * 編集可能かどうか判断します。
	 * 
	 * @param end
	 *            イベント終了時間(yyyy-MM-dd HH:mm:ss)
	 * @return　編集可能ならばtrue、編集不可能ならばfalseを返します
	 */
//	public boolean isEditable(String end) {
//		if (DateUtils.compareDate(end)) {
//			// 未来
//			return true;
//		} else {
//			// 過去
//			return false;
//		}
//	}

	/**
	 * イベントIDからイベントの色を決めます。
	 * 
	 * @param id
	 *            イベントID
	 * @return id=moveならば#F0FFFF、id=meetingならば#ff80ff、それ以外ならば#00ff40を返します。
	 */
	private String makeColor(String id) {
		if (id.substring(0, 4).equals("move")) {
			return "#F0FFFF";
		} else if (id.substring(0, 7).equals("meeting")) {
			return "#ff80ff";
		} else {
			return "#00ff40";
		}
	}
}
