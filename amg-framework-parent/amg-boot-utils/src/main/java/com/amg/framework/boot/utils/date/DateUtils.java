package com.amg.framework.boot.utils.date;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期常用格式
 *
 */
public class DateUtils {

	/** yyyyMMddHHmmss **/
	public static final String FORMAT_1 = "yyyyMMddHHmmss";

	/** yyyy-MM-dd HH:mm:ss **/
	public static final String FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

	/** yyyy-MM-dd **/
	public static final String FORMAT_3 = "yyyy-MM-dd";

	/** HH:mm:ss **/
	public static final String FORMAT_4 = "HH:mm:ss";

	public static final String FORMAT_5 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String FORMAT_6 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static final String FORMAT_7 = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final SimpleDateFormat SDF = new SimpleDateFormat(DateUtils.FORMAT_3);

	public static String dateToString(Date date, String format) {
		return DateFormatUtils.format(date, format);
	}

	public static String calendarToString(Calendar calendar, String format) {
		return DateFormatUtils.format(calendar, format);
	}

	public static String longToString(Long millis, String format) {
		return DateFormatUtils.format(millis, format);
	}

	public static Date strToDate(String strDate, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 获取前几天
	 * 
	 * @param date 当前时间
	 * @param day  天数
	 * @return
	 */
	public static Date getBeforeDateByDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - day);
		return calendar.getTime();
	}


	/**
	 * 获取前几分钟
	 *
	 * @param date 当前时间
	 * @param min  天数
	 * @return
	 */
	public static Date getBeforeDateByMinit(Date date, int min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - min * 60);
		return calendar.getTime();
	}

	public static Date getAfterDateByMinit(Date date, int min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + min * 60);
		return calendar.getTime();
	}

	/**
	 * 获取后几天
	 * 
	 * @param date 当前时间
	 * @param day  天数
	 * @return
	 */
	public static Date getAfterDateByDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return calendar.getTime();
	}

	/**
	 * 判断时间是否超出对应天数
	 * 
	 * @param date 时间
	 * @param day  天数
	 * @return true没有超出 false超出
	 */
	public static boolean checkDay(Date date, int day) {
		return LocalDateTime.now().isBefore(getAfterDateByDay(date, day).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	/**
	 * 判断时间是否超出对应天数
	 * 
	 * @param date 时间
	 * @param day  天数
	 * @return true没有超出 false超出
	 */
	public static boolean checkDay(String date, int day) {
		return LocalDateTime.now().isBefore(getAfterDateByDay(strToDate(date, FORMAT_2), day).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	/**
	 * 添加 时分秒
	 * 
	 * @param time 日期
	 * @param type [start、end]=[00:00:00、23:59:59]
	 */
	public static String time(String time, String type) {
		if (StringUtils.isBlank(time))
			return time;
		if ("start".equalsIgnoreCase(type)) {
			return SDF.format(SDF.parse(time, new ParsePosition(0))) + " 00:00:00";
		} else {
			return SDF.format(SDF.parse(time, new ParsePosition(0))) + " 23:59:59";
		}
	}

	/**
	 * 开始日期添加时分秒
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public static void formatTime(String startTime, String endTime) {
		if (StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			startTime = SDF.format(SDF.parse(endTime, new ParsePosition(0))) + " 00:00:00";
			endTime = SDF.format(SDF.parse(endTime, new ParsePosition(0))) + " 23:59:59";
		} else if (StringUtils.isBlank(endTime) && StringUtils.isNotBlank(startTime)) {
			startTime = SDF.format(SDF.parse(startTime, new ParsePosition(0))) + " 00:00:00";
			endTime = SDF.format(SDF.parse(startTime, new ParsePosition(0))) + " 23:59:59";
		} else {
			startTime = StringUtils.isBlank(startTime) ? startTime : SDF.format(SDF.parse(startTime, new ParsePosition(0))) + " 00:00:00";
			endTime = StringUtils.isBlank(endTime) ? endTime : SDF.format(SDF.parse(endTime, new ParsePosition(0))) + " 23:59:59";
		}
	}

	/**
	 * 字符串转date
	 * @param source  时间
	 * @param format  时间格式
	 * @return
	 */
	public static Date parseDate(String source,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * date转字符串
	 * @param source  时间
	 * @param format  时间格式
	 * @return
	 */
	public static String formatDate(Date source,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		try{
			return sdf.format(source);
		}catch (Exception e){
			return null;
		}
	}
	
	
	/** 
     * Date 转 LocalDateTime 
     * 
     * @param date 
     * @return LocalDateTime
     */  
    public static LocalDateTime dateToLocalDateTime(Date date) { 
    	if (date == null) {
    		return null;  
		}
    	long nanoOfSecond = (date.getTime() % 1000) * 1000000;  
    	return LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
    }  
	
	/** 
     * Date 转 LocalDate 
     * 
     * @param date
     * @return LocalDate 
     */  
    public static LocalDate dateToLocalDate(Date date) {  
    	if (date == null) {
			return null;
		}
    	return dateToLocalDateTime(date).toLocalDate();  
    } 
	
	/** 
               *  计算两个日期相差的天数,相同日期为0天
     * 
     * @param before 前一日期
     * @param after  后一日期
     * @return int 天数
     */  
    public static Integer getDiffDay(Date before, Date after) { 
    	if (before == null || after == null) {
			return null;
		}
    	Long days = dateToLocalDate(before).until(dateToLocalDate(after), ChronoUnit.DAYS);
        return days.intValue();  
    }
    
    public static void main(String[] args) {
		System.out.println(getDiffDay(strToDate("2019-06-01",FORMAT_3),strToDate("2019-06-01",FORMAT_3)));
	}


	/**
	 * 将世界标准时间设置成为 北京时间
	 * @param UTCDate 传入的世界标准时间字符串 格式为：  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"  或者 "yyyy-MM-dd'T'HH:mm:ss'Z'" 其他格式返回原样字符串
	 * @return 返回北京时间字符串 （过个在传入数据格式正确的情况下 返回 "yyyy-MM-dd HH:mm:ss" 格式)
	 * @throws ParseException
	 */
	public static String UTCToCST(String UTCDate) throws ParseException {
		String format;
		if(StringUtils.isBlank(UTCDate)){
			return null;
		}else if(UTCDate.contains("T") && UTCDate.contains("Z") && UTCDate.contains(".")){
			format = FORMAT_5;
		}else if(UTCDate.contains("T") && UTCDate.contains("Z")){
			format = FORMAT_6;
		}else {
			return UTCDate;
		}
		SimpleDateFormat format1 = new SimpleDateFormat(format);
		Date date = format1.parse(UTCDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
		Date time = calendar.getTime();
		SimpleDateFormat format2 = new SimpleDateFormat(FORMAT_2);
		return format2.format(time);
	}











}
