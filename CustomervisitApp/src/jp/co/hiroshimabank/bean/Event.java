/**
 * ひとつのスケジュールを格納したクラスです。
 */
package jp.co.hiroshimabank.bean;

import jp.co.hiroshimabank.utils.EventUtils;

/**
 * @author 日本IBM 梅沢
 *
 */
public class Event extends EventUtils {

	/** このイベントのID */
	private String id;
	/** このイベントのタイトル */
	private String title;
	/** このイベントの開始時間 */
	private String start;
	/** このイベントの終了時間 */
	private String end;
	/** イベントのバックグランドカラー */
	private String backgroundColor;
	/** イベントの枠カラー */
	private String borderColor;
	/** テキストカラー */
	private String textColor;
	/**編集可能か*/
	private boolean editable;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
