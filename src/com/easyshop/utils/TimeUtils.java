/*
 * Copyright (C), 2002-2016, 苏宁易购电子商务有限公司
 * FileName: RandomUtils
 * Author:   15040635 wzs
 * Date:     2016/1/6 15:50
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author> <time> <version> <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.easyshop.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class TimeUtils {

	public final static String FORMAT10 = "yyyy-MM-dd";
	public final static String FORMAT14 = "yyyy-MM-dd HH:mm:ss";

	public static Date timestamp2Date(Object timestamp) throws ParseException {
		if (timestamp == null) {
			return new Date();
		} else {
			return new java.util.Date((long) timestamp * 1000);
		}
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(strDate, pos);
	}

	/**
	 * 功能描述: 获取当前之前或之后日期<br>
	 * 
	 * @param delay
	 *            正数date时间后移，负数则前移
	 * @return
	 */
	public static Date getNextDay(Date day, int delay) {
		Calendar c = Calendar.getInstance();
		c.setTime(day == null ? new Date() : day);
		c.add(Calendar.DAY_OF_MONTH, delay);
		return c.getTime();
	}

	/**
	 * 将长时间格式时间转换为字符串
	 *
	 * @param dateDate
	 *
	 * @return
	 */
	public static String dateToStr(java.util.Date dateDate, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(dateDate);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(TimeUtils.timestamp2Date(1453102429L));
	}

}