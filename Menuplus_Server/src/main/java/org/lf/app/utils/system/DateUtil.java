package org.lf.app.utils.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date util
 * @author LF
 * 
 */
public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdftm = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat sdfn = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdfdtm = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	
	
	public static String dateToStr(Date date) {
		return sdf.format(date);
	}
	
	public static Date strToDate(String date) throws ParseException {
		return sdf.parse(date);
	}
	
	
	public static String dateToStrn(Date date) {
		return sdfn.format(date);
	}
	
	public static Date strnToDate(String date) throws ParseException {
		return sdfn.parse(date);
	}
	
	
	public static String dateTimeToStr(Date date) {
		return sdft.format(date);
	}
	
	public static Date strToDateTime(String date) throws ParseException {
		return sdft.parse(date);
	}
	
	
	public static String tmToStr(Date date) {
		return sdftm.format(date);
	}
	
	public static Date strToTm(String date) throws ParseException {
		return sdftm.parse(date);
	}
	
	
	public static String dtmToStr(Date date) {
		return sdfdtm.format(date);
	}
	
	public static Date strToDtm(String date) throws ParseException {
		return sdfdtm.parse(date);
	}
	
	
	
	public static String dateToStr(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	
}