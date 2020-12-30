package com.chelsea.covid.crawler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author shevchenko
 *
 */
public class DateUtil {

    private DateUtil() {}

    private static final String TIME = " 00:00:00";
    /** 格式：yyyy-MM-dd HH:mm:ss */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** 格式：yyyyMMddHHmmss */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    /** 格式：yyyyMMdd */
    public static final DateTimeFormatter DATE_FORMAT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    /** 格式：yyyy-MM-dd */
    public static final DateTimeFormatter DATE_FORMAT2_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** 格式：yyMMddHHmmss */
    public static final DateTimeFormatter SEQ_FORMAT_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    /** 格式：yyMMddHHmm */
    public static final DateTimeFormatter SEQ_FORMAT2_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmm");
    /** 格式：yyyyMMddHHmmssSSS */
    public static final DateTimeFormatter DATE_MS_FORMAT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    /** 格式：yyyy-MM-dd HH:mm:ss.SSS */
    public static final DateTimeFormatter DATE_MS_FORMATTER_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    /** 格式：yyyy年MM月dd日 */
    public static final DateTimeFormatter CHINA_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    public static String nowFormatter(DateTimeFormatter formatter) {
        return LocalDateTime.now().format(formatter);
    }

    public static String dateFormatter(DateTimeFormatter formatter, Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(formatter);
    }

    /**
     * 序列化日期格式
     * 
     * @param formatter 日期格式，确认是带时分秒的时间格式,不带时分秒的会有问题
     * @param str 日期字符串
     * @return 日期
     */
    public static Date stringFormatter(DateTimeFormatter formatter, String str) {
        LocalDateTime localDateTime = LocalDateTime.parse(str, formatter);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前时间字符串，格式 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @return 日期字符串
     */
    public static String getDateMsFormatter() {
        return LocalDateTime.now().format(DATE_MS_FORMATTER_FORMATTER);
    }

    /**
     * 获取当前时间字符串，格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 日期字符串
     */
    public static String getTimeString() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * 获取当前时间字符串，格式 yyyyMMddHHmmss
     *
     * @return 日期字符串
     */
    public static String getDateTimeString() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * SEQ使用的时间格式yyMMddHHmmss
     *
     * @return 当前日期格式
     */
    public static String getSeqDateTimeString() {
        return LocalDateTime.now().format(SEQ_FORMAT_FORMATTER);
    }

    /**
     * SEQ使用的时间格式yyMMddHHmm
     *
     * @return 当前日期格式
     */
    public static String getSeqDateTimeString2() {
        return LocalDateTime.now().format(SEQ_FORMAT2_FORMATTER);
    }

    /**
     * 获取当前时间字符串，格式 yyyyMMdd
     *
     * @return 日期字符串
     */
    public static String getDateDateString() {
        return LocalDateTime.now().format(DATE_FORMAT_FORMATTER);
    }

    /**
     * 获取当前时间字符串，格式 yyyy-MM-dd
     *
     * @return 日期字符串
     */
    public static String getDateDateString2() {
        return LocalDateTime.now().format(DATE_FORMAT2_FORMATTER);
    }

    /**
     * 将日期类型转成指定格式字符串yyyy-MM-dd HH:mm:ss
     *
     * @param date java.util.Date
     * @return String
     */
    public static String getFormatDateToString(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(TIME_FORMATTER);
    }

    /**
     * 将日期类型转成指定格式字符串yyyy-MM-dd
     *
     * @param date java.util.Date
     * @return String
     */
    public static String getFormatDate2ToString(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(DATE_FORMAT2_FORMATTER);
    }

    /**
     * 将日期类型转成指定格式字符串yyyyMMddHHmmss
     *
     * @param date java.util.Date
     * @return String
     */
    public static String getDateToString(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 将日期类型转成指定格式字符串yyyyMMdd
     *
     * @param date java.util.Date
     * @return String
     */
    public static String getDateDateToString(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(DATE_FORMAT_FORMATTER);
    }

    /**
     * 将时间字符串类型转为Date
     */
    public static Date converStrTimeToDate(String dateString) {
        if (dateString.length() == 10) {
            dateString = dateString + TIME;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, TIME_FORMATTER);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 单次采集时间转换成入库格式
     */
    public static String timeFormatToDateTime(String loadTime) {
        LocalDateTime time = LocalDateTime.parse(loadTime, TIME_FORMATTER);
        return DATETIME_FORMATTER.format(time);
    }

    /**
     * 入库格式转换成页面需要格式
     */
    public static String dateTimeFormatToTime(String loadTime) {
        LocalDateTime time = LocalDateTime.parse(loadTime, DATETIME_FORMATTER);
        return TIME_FORMATTER.format(time);
    }

    /**
     * 将时间字符串类型转为Date
     */
    public static Date converStringTimeToDate(String dateString) {
        return converStrTimeToDate(dateString);
    }

    /**
     * 将时间字符串类型转为Date
     *
     * @param dateString 建议生效时间 yyyyMMddHHmmss
     * @return Date
     */
    public static Date converSuggestTimeToDate(String dateString) {
        if (dateString.length() == 8) {
            dateString = dateString + "000000";
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DATETIME_FORMATTER);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将时间字符串类型转为Date
     *
     * @param dateString 建议生效时间 yyyyMMdd
     * @return Date
     */
    public static Date converStrWithoutLineToDate(String dateString) {
        LocalDate localDateTime = LocalDate.parse(dateString, DATE_FORMAT_FORMATTER);
        return Date.from(localDateTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将时间字符串类型转为Date
     *
     * @param dateString 建议生效时间 yyyy-MM-dd
     * @return Date
     */
    public static Date converStrWithLineToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过日历类的Calendar.add方法第二个参数-1达到前一天日期的效果
     */
    public static Date getYesterdayByCalendar(Date date, int week) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).minusDays(week)
                .withHour(0).withMinute(0).withSecond(0);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 校验日期是否大于当前时间
     */
    public static boolean compareToCurrentTime(Date date) {
        return date.after(new Date());
    }

    /**
     * 比较两个日期大小
     */
    public static boolean compareToTime(Date date, Date compareDate) {
        // 对比大小
        return date.after(compareDate);
    }

    private static final int DATE_LENGTH = 8;

    /**
     * 格式化时间
     *
     * @param dateStr 时间字符串
     * @return 格式化后的时间(yyyy - MM - dd)
     */
    public static String formatDate(String dateStr) {
        dateStr = dateStr.trim().replace('/', '-').replace('.', '-');
        if (DATE_LENGTH == dateStr.length()) {
            String year = dateStr.substring(0, 4);
            String month = dateStr.substring(4, 6);
            String day = dateStr.substring(6);
            dateStr = year + month + day;
        }
        return dateStr;
    }

    /**
     * 获取当天最大时间
     */
    public static Date getCurrentMaxTime() {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 时间戳字符串转换为日期格式字符串
     */
    public static String getDateStrByTimeStamp(String timeStamp) {
        long time = Long.parseLong(timeStamp);
        Instant timestamp = Instant.ofEpochMilli(time);
        ZonedDateTime losAngelesTime = timestamp.atZone(ZoneId.of("Asia/Shanghai"));
        return losAngelesTime.format(DATETIME_FORMATTER);
    }

    /**
     * 将yyyy年MM月dd日，转换为yyyyMMdd
     * 
     * @return
     */
    public static String convertChinaDataToIntStr(String dataStr) {
        LocalDate localDateTime = LocalDate.parse(dataStr, CHINA_DATE_FORMATTER);
        return DATE_FORMAT_FORMATTER.format(localDateTime);
    }

}
