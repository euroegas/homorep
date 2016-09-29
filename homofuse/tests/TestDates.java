package tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TestDates {

	public static void main(String[] args) {
		String date2Str = "20160510";
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.getTimeInMillis());
		int day = (int) (calendar.getTimeInMillis()/1000/24/3600);
		System.out.println(day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date2Date = null;
		try {
			date2Date = sdf.parse(date2Str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int day2 = (int) ((date2Date.getTime()/1000/3600+TimeZone.LONG)/24);
		System.out.println(day2);

	}

}
