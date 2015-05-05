package jp.co.hiroshimabank.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Date date = new Date();
		System.out.println(TimeZone.getTimeZone("Asia/Tokyo").getOffset(date.getTime())/1000/60/60);
		
		long l = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo")).getTimeInMillis();
		
		System.out.println(new Timestamp(l));
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORM_001);
		
		dateFormat.setTimeZone(TimeZone.getTimeZone("JST"));
		
		Timestamp ts = Timestamp.valueOf(dateFormat.format(date));
		
		System.out.println(ts.getTime());
		
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Timestamp ts1 = Timestamp.valueOf(dateFormat.format(date));
		
		System.out.println(ts1.getTime());
		
		System.out.println((ts.getTime() - ts1.getTime())/1000/60/60);
		
		
		System.out.println("-------------------------------------------");
		Date date1 = new Date();	//現在日時
		TimeZone defaultZone = TimeZone.getDefault();
		System.out.println(defaultZone.getID());
		System.out.println("デフォルト：" + date1 +" : "+ date1.getTime());

		TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
		System.out.println("アメリカ　：" + date1 +" : "+ date1.getTime());

		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		System.out.println("GMT 　　　：" + date1 +" : "+ date1.getTime());

		TimeZone.setDefault(defaultZone);
		System.out.println("元に戻した：" + date1 +" : "+ date1.getTime());
		
		
		System.out.println("-------------------------------------------");
		
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	    // GMT(世界標準時刻)をセットする。
	    TimeZone gmt = TimeZone.getTimeZone("GMT");
	    cal.setTimeZone(gmt);
	    
	    cal.setTimeInMillis(new Date().getTime());
	    System.out.println("\n"+ gmt.getDisplayName() );
	    System.out.println(cal.get(cal.YEAR) + "年"
	                        + (cal.get(cal.MONTH)+1) + "月"
	                        + cal.get(cal.DAY_OF_MONTH) + "日 "
	                        + cal.get(cal.HOUR_OF_DAY) + "時"
	                        + cal.get(cal.MINUTE) + "分"
	                        + cal.get(cal.SECOND) + "秒");
	 
	    // JST(日本標準時刻)をセットする。
	    TimeZone jst = TimeZone.getTimeZone("JST");
	    cal.setTimeZone(jst);
	    cal.setTimeInMillis(new Date().getTime());
	    System.out.println("\n"+  jst.getDisplayName() );
	    System.out.println(cal.get(cal.YEAR) + "年"
	            + (cal.get(cal.MONTH)+1) + "月"
	            + cal.get(cal.DAY_OF_MONTH) + "日 "
	            + cal.get(cal.HOUR_OF_DAY) + "時"
	            + cal.get(cal.MINUTE) + "分"
	            + cal.get(cal.SECOND) + "秒");
	 
	    // CTT(中国標準時刻)をセットする。
	    TimeZone ctt = TimeZone.getTimeZone("CTT");
	    cal.setTimeZone(ctt);
	    cal.setTimeInMillis(new Date().getTime());
	    System.out.println("\n"+  ctt.getDisplayName() );
	    System.out.println(cal.get(cal.YEAR) + "年"
	            + (cal.get(cal.MONTH)+1) + "月"
	            + cal.get(cal.DAY_OF_MONTH) + "日 "
	            + cal.get(cal.HOUR_OF_DAY) + "時"
	            + cal.get(cal.MINUTE) + "分"
	            + cal.get(cal.SECOND) + "秒");
	    
	    
	    System.out.println(DateUtils.getTimestamp());
	    
	    
	    System.out.println("----------------------------実験---------------------------");
	    Calendar calendar = DateUtils.getCalender();
	    for (int i = 0 ;i<10 ;i++){
	    	calucurate(calendar);
	    }
	    System.out.println("----------------------------実験---------------------------");

	    
	    System.out.println(DateUtils.toStringFromDate(new Date(), DateUtils.DATE_FORM_007));
	    
	    System.out.println("meeting10000003201502092113".subSequence(7, 15));
	    
	    
	    System.out.println("----------------暗号化実験-------------");
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000000").length());
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000001"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000002"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000003"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000004"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000005"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000006"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000007"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000008"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "10000009"));
	    System.out.println(EncryptionUtils.getStretchedPassword("zaq12wsx", "20000000"));

	}

	public static void calucurate(Calendar calendar) {
		calendar.add(Calendar.HOUR, 1);
		System.out.println(new Date(calendar.getTimeInMillis()));
	}
}
