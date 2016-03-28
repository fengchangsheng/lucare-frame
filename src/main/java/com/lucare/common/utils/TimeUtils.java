package com.lucare.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class TimeUtils {

    public static final long UNIXTIME_STD_DAY;

    public TimeUtils() {
    }

    public static String yyyyMMddHHmmssNow() {
        return yyyyMMddHHmmss(new Date());
    }

    public static String yyyyMMddHHmmss() {
        return yyyyMMddHHmmss(new Date());
    }

    public static String yyyyMMddHHmmss(long timestamp) {
        return yyyyMMddHHmmss(new Date(timestamp));
    }

    public static String yyyyMMddHHmmss(Date date) {
        return dateFormat4yyyyMMddHHmmss().format(date);
    }

    public static Date yyyyMMddHHmmss(String formatdate) {
        try {
            return dateFormat4yyyyMMddHHmmss().parse(formatdate);
        } catch (Exception var2) {
            throw new IllegalStateException("parse[" + formatdate + "] with ex.", var2);
        }
    }

    public static Date yyyyMMdd(String formatdate) {
        try {
            return dateFormat4yyyyMMdd().parse(formatdate);
        } catch (Exception var2) {
            throw new IllegalStateException("parse[" + formatdate + "] with ex.", var2);
        }
    }

    public static long getMilliseconds(String unixtime) {
        return Long.valueOf(unixtime).longValue() * 1000L;
    }

    public static Date setUnixTime(Date date, String unixtime) {
        if(date == null) {
            date = new Date();
        }

        date.setTime(getMilliseconds(unixtime));
        return date;
    }

    public static String format(DateFormat format, Date date) {
        try {
            return format.format(date);
        } catch (Exception var3) {
            throw new IllegalArgumentException("format[" + date + "] with ex.", var3);
        }
    }

    public static SimpleDateFormat dateFormatyyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static SimpleDateFormat dateFormat4yyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static SimpleDateFormat dateFormat2yyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
    }

    public static SimpleDateFormat dateFormat4yyyyMMdd() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static SimpleDateFormat yyyyMMddFormatter() {
        return new SimpleDateFormat("yyyyMMdd");
    }

    public static DateFormat yyyyMMddChianiseFormatter() {
        return new SimpleDateFormat("yyyy年MM月dd日");
    }

    public static String yyyyMMdd(Date date) {
        return dateFormat4yyyyMMdd().format(date);
    }

    public static String yyyyMMddHHMMSS(Date date) {
        return dateFormat2yyyyMMddHHmmss().format(date);
    }

    public static Date newDateByUnixTimestamp(long unixTimestamp) {
        return unixTimestamp == 0L?null:new Date(unixTimestamp * 1000L);
    }

    public static long getUnixTimestamp(Date date) {
        return date == null?0L:date.getTime() / 1000L;
    }

    public static long unixtimestampOfNow() {
        return System.currentTimeMillis() / 1000L;
    }

    public static String stringOfUnixtimestampNow() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }

    public static String stringOfUnixtimestamp(Date date) {
        return String.valueOf(date.getTime() / 1000L);
    }

    public static String yyyyMMddChianise(Date date) {
        return yyyyMMddChianiseFormatter().format(date);
    }

    public static void main(String[] args) {
        Date now = newDateByUnixTimestamp(1385027819L);
        System.out.println(yyyyMMdd(now));
    }

    public static Date newDateByUnixTimestamp(String timestamp) {
        timestamp = CommonUtils.emptyIfNull(timestamp);
        return timestamp.isEmpty()?null:newDateByUnixTimestamp(Long.valueOf(timestamp).longValue());
    }

    public static Date getDateZero(Date date) {
        Calendar c = Calendar.getInstance();
        if(date != null) {
            c.setTime(date);
        }

        setCalendarToZero(c);
        return c.getTime();
    }

    public static Date getTodayLimit() {
        return getDateEndTime(new Date());
    }

    public static Date getDateStartTime(Date date) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setCalendarToZero(c);
            return c.getTime();
        }
    }

    public static void setCalendarToZero(Calendar c) {
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
    }

    public static Date getDateEndTime(Date date) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(5, 1);
            setCalendarToZero(c);
            return c.getTime();
        }
    }

    public static Date getTodayZero() {
        return getDateZero((Date)null);
    }

    public static Date parse(Timestamp timestamp) {
        return timestamp == null?null:new Date(timestamp.getTime() * 1000L);
    }

    public static int getAge(Date birthday) {
        long day = TimeUnit.DAYS.convert(System.currentTimeMillis() - birthday.getTime(), TimeUnit.MILLISECONDS);
        return (int)(day / 365L);
    }

    public static Date getMonthStartTime(Date date) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setCalendarToZero(c);
            c.set(5, 1);
            return c.getTime();
        }
    }

    public static Date getMonthEndTime(Date date) {
        if(date == null) {
            return null;
        } else {
            Date newDate = getMonthStartTime(date);
            Calendar c = Calendar.getInstance();
            c.setTime(newDate);
            setCalendarToZero(c);
            c.set(2, c.get(2) + 1);
            return c.getTime();
        }
    }

    public static Date getWeekStartTime(Date date) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setCalendarToZero(c);
            c.set(7, 1);
            return c.getTime();
        }
    }

    public static Date getYesterdayStartTime(Date date) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setCalendarToZero(c);
            c.add(5, -1);
            return c.getTime();
        }
    }

    public static Date getDayOffStartTime(Date date, int value) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setCalendarToZero(c);
            c.add(5, value);
            return c.getTime();
        }
    }

    public static Date getDayOffTime(Date date, int value) {
        if(date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(5, value);
            setCalendarToZero(c);
            return c.getTime();
        }
    }

    public static Date getYearStartTime() {
        Calendar c = Calendar.getInstance();
        setCalendarToZero(c);
        c.set(6, 1);
        return c.getTime();
    }

    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, 1);
        return calendar.getTime();
    }

    public static Date getBeforeDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, -1);
        return calendar.getTime();
    }

    public static int getDiffSecond(int value) {
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(10, value);
        date = c.getTime();
        int second = (int)(getUnixTimestamp(date) - getUnixTimestamp(new Date()));
        return second;
    }

    public static String getTimeDiff(int second) {
        int h = 0;
        int d = 0;
        boolean s = false;
        int temp = second % 3600;
        int s1;
        if(second > 3600) {
            h = second / 3600;
            if(temp != 0) {
                if(temp > 60) {
                    d = temp / 60;
                    if(temp % 60 != 0) {
                        s1 = temp % 60;
                    }
                }
            }
        } else {
            d = second / 60;
            if(second % 60 != 0) {
                s1 = second % 60;
            }
        }

        return h + "小时" + d + "分钟";
    }

    public static String getUnixtimestampOfDateStartTime(String Date) {
        if(!CommonUtils.isEmpty(Date)) {
            Date date1 = yyyyMMdd(Date);
            return stringOfUnixtimestamp(getDateStartTime(date1));
        } else {
            return "";
        }
    }

    public static String getUnixtimestampOfDateEndTime(String Date) {
        if(!CommonUtils.isEmpty(Date)) {
            Date date2 = yyyyMMdd(Date);
            return stringOfUnixtimestamp(getDateEndTime(date2));
        } else {
            return "";
        }
    }

    static {
        UNIXTIME_STD_DAY = TimeUnit.SECONDS.convert(1L, TimeUnit.DAYS);
    }
}
