/**
 * 時間に関する機能をまとめています。
 */
package jp.co.hiroshimabank.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author 日本IBM梅沢
 *
 */
public abstract class DateUtils {

	/**
	 * 日付の形式はyyyy-MM-dd HH:mm:ssです。
	 */
	public static final String DATE_FORM_001 = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 日付の形式はyyyyMMdd HHmmです。
	 */
	public static final String DATE_FORM_002 = "yyyyMMdd HHmm";
	/**
	 * 日付の形式はyyyyMMddです。
	 */
	public static final String DATE_FORM_003 = "yyyyMMdd";
	/**
	 * 日付の形式はHHmmssです。
	 */
	public static final String DATE_FORM_004 = "HHmmss";
	/**
	 * 日付の形式はyyyy-MM-ddです。
	 */
	public static final String DATE_FORM_005 = "yyyy-MM-dd";
	/**
	 * 日付の形式はyyyyMMddHHmmです。
	 */
	public static final String DATE_FORM_006 = "yyyyMMddHHmm";
	
	/**
	 * 日付の型はEEE, d MMM yyyy HH:mm:ss Zです。
	 */
	public static final String DATE_FORM_007 = "yyyy/MM/dd HH:mm";

	public static final int INTERVALMOVE = 10;
	
	public static final int INTERVALEVENT = 20;

	private static Map<String, SimpleDateFormat> dateFormatCache = new HashMap<String, SimpleDateFormat>();
	
	/**
	 * 現在時刻のカレンダーを返します。
	 * @return
	 */
	public static Calendar getCalender(){
		Locale JaLocale = new Locale("ja","JP");
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("JST"),JaLocale);
		return calendar;
	}

	/**
	 * 開始時間にかかる時間を足します。開始時間はDATE_FORM_001でなければいけません。 変換に失敗するとnullを返します。
	 * 
	 * @param startTime
	 *            開始時間　型はDATE_FORM_001
	 * @param time
	 *            かかる時間　単位は秒
	 * @return 足した時間を返します。ただし型はDATE_FORM_001です。
	 */
	@Deprecated
	public static String calculateTime(String startTime, int time) {
		SimpleDateFormat df = getDateFormat(DATE_FORM_001);
		Date date = null;
		long second = 0;
		synchronized (df) {
			df.setTimeZone(TimeZone.getTimeZone("JST"));
			try {
				second = df.parse(startTime).getTime();
				System.out.println(second);
				second = second + time * 1000;
				System.out.println(second);
				date = new Date(second);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return toStringFromDate(date, DATE_FORM_001);
	}

	/**
	 * 開始時間にかかる時間を足します
	 * @param ts Timeｓtamp JST
	 * @param second　かかる時間　単位は秒
	 * @return 足した時間を返します
	 */
	@Deprecated
	public static Timestamp calculateTime(Timestamp ts, int second) {
		long miliSecond = ts.getTime() ;
		miliSecond = miliSecond + second * 1000;//JST
		return Timestamp.valueOf(toStringFromDate(new Date(miliSecond), DateUtils.DATE_FORM_001));
	}
	
	/**
	 * 文字列からDate型を作成します。
	 * 
	 * @param time
	 *            Date型に変換したい文字列です。
	 * @param format
	 *            文字列の型です。
	 * @return Date型が返ります。失敗するとnullが返ります。
	 */
	public static Date toDateFromString(String time, String format) {
		SimpleDateFormat df = getDateFormat(format);
		synchronized (df) {
			df.setTimeZone(TimeZone.getTimeZone("JST"));
			try {
				return df.parse(time);
			} catch (ParseException e) {
				return null;
			}
		}
	}

	/**
	 * 日付を文字列に変換します。
	 * 
	 * @param date
	 * @param form
	 * @return
	 */
	public static String toStringFromDate(Date date, String form) {
		if (date == null) {
			return "";
		}
		if (form == null) {
			throw new IllegalArgumentException("form is null");
		}
		SimpleDateFormat df = getDateFormat(form);
		synchronized (df) {
			df.setTimeZone(TimeZone.getTimeZone("JST"));
			return df.format(date);
		}
	}

	/**
	 * 現在時刻を文字列に変換します。
	 * 
	 * @param form
	 *            文字列の型を指定します。
	 * @return 日付文字列
	 */
	public static String toStringFromDate(String form) {
		Date date = new Date();
		if (form == null) {
			throw new IllegalArgumentException("form is null");
		}
		SimpleDateFormat df = getDateFormat(form);
		synchronized (df) {
			df.setTimeZone(TimeZone.getTimeZone("JST"));
			return df.format(date);
		}
	}

	/**
	 * 現在時刻と比較します。
	 * 
	 * @param eventEnd
	 *            イベント終了時間 型はyyyy-MM-dd HH:mm:ssです。
	 * @return 過去ならばfalse、未来ならばtrueを返します。
	 */
	public static Boolean compareDate(String eventEnd) {
		Date now = new Date();
		Date end = DateUtils.toDateFromString(eventEnd, DATE_FORM_001);

		if (end == null) {
			return false;
		}
		if (now.getTime() > end.getTime()) {
			// 過去
			return false;
		} else {
			// 未来
			return true;
		}
	}

	/**
	 * 現在時刻をTimeStampで返します
	 * 
	 * @return 現在時刻
	 */
	@Deprecated
	public static Timestamp getTimestamp() {		
		return new Timestamp(getCalender().getTimeInMillis());
	}
		
	/**
	 * 指定した時刻のTimestampを返します
	 * 
	 * @param calendar
	 *            指定したカレンダー
	 * @return Timestamp
	 */
	public static Timestamp getTimestamp(Calendar calendar) {
		//暫定対応 ローカルとサーバーの時間のずれを解消する
		Timestamp ts = new Timestamp(calendar.getTimeInMillis());
		String strTimestamp = getDate(ts);
		return Timestamp.valueOf(strTimestamp);
//		return new Timestamp(calendar.getTimeInMillis());
	}

	/**
	 * TimestampからStringの型yyyy-MM-dd HH:mm:ssに変換します。
	 * 
	 * @param ts
	 *            Timestamp
	 * @return Stringの型yyyy-MM-dd HH:mm:ss
	 */
	public static String getDate(Timestamp ts) {
		// JSTに変換します
		SimpleDateFormat sdf = getDateFormat(DATE_FORM_001);
		synchronized (sdf) {
			sdf.setTimeZone(TimeZone.getTimeZone("JST"));
//			Calendar cal = Calendar.getInstance();
//			Calendar calendar = sdf.getCalendar();
//			calendar.setTimeInMillis(ts.getTime());
//			Date date = calendar.getTime();
//			if(cal.getTimeZone().equals("JST")){
//				//JST
//			} else if(cal.getTimeZone().equals("UTC") || cal.getTimeZone().equals("GMT")) {
//				miliSecond = miliSecond - TimeZone.getTimeZone("Asia/Tokyo").getOffset(miliSecond);
//			}

//			miliSecond = miliSecond - TimeZone.getTimeZone("Asia/Tokyo").getOffset(miliSecond);
//			return sdf.format(new Date(miliSecond));
			return sdf.format(ts);
		}
	}
	
	public static String getDate2(Timestamp ts) {
		// JSTに変換します
		SimpleDateFormat sdf = getDateFormat(DATE_FORM_001);
		synchronized (sdf) {
			sdf.setTimeZone(TimeZone.getDefault());
//			Calendar cal = Calendar.getInstance();
//			Calendar calendar = sdf.getCalendar();
//			calendar.setTimeInMillis(ts.getTime());
//			Date date = calendar.getTime();
//			if(cal.getTimeZone().equals("JST")){
//				//JST
//			} else if(cal.getTimeZone().equals("UTC") || cal.getTimeZone().equals("GMT")) {
//				miliSecond = miliSecond - TimeZone.getTimeZone("Asia/Tokyo").getOffset(miliSecond);
//			}

//			miliSecond = miliSecond - TimeZone.getTimeZone("Asia/Tokyo").getOffset(miliSecond);
//			return sdf.format(new Date(miliSecond));
			return sdf.format(ts);
		}
	}

	/**
	 * プールされている型を取得します。
	 * 
	 * @param form
	 * @return
	 */
	private static SimpleDateFormat getDateFormat(String form) {
		synchronized (dateFormatCache) {
			SimpleDateFormat dateFormat = dateFormatCache.get(form);
			if (dateFormat == null) {
				dateFormat = new SimpleDateFormat(form);
				dateFormatCache.put(form, dateFormat);
			}
			return dateFormat;
		}
	}
}
