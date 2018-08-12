package com.happyshopping.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {

	public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static Date strToDate(String dateTimeStr, String format){
		if (StringUtils.isBlank(dateTimeStr)){
			return null;
		}
		DateTimeFormatter dtf = DateTimeFormat.forPattern(format);
		DateTime dateTime = dtf.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}
	
	public static String dateToStr(Date date, String format){
		if (date == null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(format);
	}
	
	public static Date strToDate(String dateTimeStr){
		if (StringUtils.isBlank(dateTimeStr)){
			return null;
		}
		DateTimeFormatter dtf = DateTimeFormat.forPattern(STANDARD_FORMAT);
		DateTime dateTime = dtf.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}
	
	public static String dateToStr(Date date){
		if (date == null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STANDARD_FORMAT);
	}
	
	
	/*public static void main(String[] args) {
		System.out.println("".equals(StringUtils.EMPTY));
		System.out.println(DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd"));
		System.out.println(DateTimeUtil.strToDate("2018-08-10 13:03:55"));
	}*/
	
}
