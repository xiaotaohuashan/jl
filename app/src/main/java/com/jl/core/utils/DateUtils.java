package com.jl.core.utils;

import android.content.Context;

import com.jl.myapplication.R;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.jiguang.api.JCoreInterface;

/**
 *
 * 描述：日期/时间相关的工具类
 */

public class DateUtils {

    //默认的patten
    public static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    //时间选择器的patten
    private static final String DATE_PICKER_PATTEN = "yyyy-MM-dd";

    //时间选择器的patten
    private static final String DATE_PICKER_PATTEN_TWO = "MM-dd";
    //一年的毫秒数
    private static final long YEAR_IN_MILLS = 31536000000L;

    private long mTimeStamp;
    private Context mContext;

    public DateUtils(Context context, long timeStamp) {
        this.mContext = context;
        this.mTimeStamp = timeStamp;//最后一条消息创建的时间
    }

    /**
     * 时间戳转字符串
     * @param timeStamp
     * @return
     */
    public static String timeStampToString(long timeStamp){
        return timeStampToString(DEFAULT_PATTERN, timeStamp);
    }

    /**
     * 时间戳转字符串
     * @param timeStamp
     * @return
     */
    public static String timeStampToJAVAString(long timeStamp){
        return timeStampToString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timeStamp);
    }


    /**
     * 时间戳转字符串
     * @param timeStamp
     * @return
     */
    public static String timeStampToDay(long timeStamp){
        return timeStampToString(DATE_PICKER_PATTEN, timeStamp);
    }

    /**
     * 时间戳转字符串
     * @param timeStamp
     * @return
     */
    public static String timeStampToDayTwo(long timeStamp){
        return timeStampToString(DATE_PICKER_PATTEN_TWO, timeStamp);
    }

    /**
     * 得到聊天的时间
     * @param timeStamp
     * @return
     */
    public static String getIMtime(long timeStamp){
        return timeStampToString(DATE_PICKER_PATTEN, timeStamp);
    }

    public static String timeStampToString(String pattern, long timeStamp){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(timeStamp));
    }


    /**
     * 字符串转时间戳
     * @param timeString
     * @return
     */
    public static long stringToTimeStamp(String timeString){
        return stringToTimeStamp(DEFAULT_PATTERN, timeString);
    }

    /**
     * 字符串转时间戳
     * @param timeString
     * @return
     */
    public static long stringToTimeStampTwo(String timeString){
        return stringToTimeStamp(DATE_PICKER_PATTEN, timeString);
    }


    /**
     * 字符串转时间戳
     * @param pattern 模板
     * @param timeString 时间
     * @return 对应的字符串 如果转换失败, 返回-1
     */
    public static long stringToTimeStamp(String pattern, String timeString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(timeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     *
     * @return 时间选择器的开始时间
     */
    public static int[] getStartTime(){
        return parseDate(System.currentTimeMillis());
    }

    /**
     *
     * @return 时间选择器的结束时间
     */
    public static int[] getEndTime(){
        long timeStamp = System.currentTimeMillis() + 2 * YEAR_IN_MILLS;
        return parseDate(timeStamp);
    }

    public static int[] getCurrentTime(){
        return parseDate(System.currentTimeMillis());
    }

    private static int[] parseDate(long timeStamp){
        String timeString = timeStampToString(DATE_PICKER_PATTEN, timeStamp);
        String[] split = timeString.split(",");
        int[] array = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Integer.parseInt(split[i]);
        }
        return array;
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static String getNowDay() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }
    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, String delay) {
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        }catch(Exception e){
            return "";
        }
    }


    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String delay) {
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = getNowDate();
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        }catch(Exception e){
            return "";
        }
    }

    /**

     * 得到几天前的时自间百

     * @度param d

     * @param day

     * @return

     */

    public static Date getDateBefore(int day){
        Calendar cal=Calendar.getInstance();
//System.out.println(Calendar.DATE);//5
        cal.add(Calendar.DATE,-day);
        Date time=cal.getTime();
        return time;
    }

    public static String getStringBefore(int day){
        Calendar cal=Calendar.getInstance();
//System.out.println(Calendar.DATE);//5
        cal.add(Calendar.DATE,-day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        c1.setTime(cal.getTime());
        return format.format(c1.getTime());
    }

    /**

     * 得到几天后的时间

     * @param d

     * @param day

     * @return

     */

    public static Date getDateAfter(Date d,int day){

        Calendar now =Calendar.getInstance();

        now.setTime(d);

        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);

        return now.getTime();

    }

    public static long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }

    public static int getCurrentTimeMillisToDay(long millis){
        long p = millis / 1000 / 60 / 60 /24;
        return (int) p;
    }

    /**
     *
     * @doc 日期转换星期几
     * @param datetime
     *            日期 例:2017-10-17
     * @return String 例:星期二
     * @author lzy
     * @history 2017年10月17日 上午9:55:30 Create by 【lzy】
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 将时间戳转换成当天零点的时间戳
     *
     * @param milliseconds
     * @return
     */
    private static Calendar zeroFromHour(long milliseconds) {
        Calendar calendar = Calendar.getInstance(); // 获得一个日历

        calendar.setTimeInMillis(milliseconds);
        zeroFromHour(calendar);
        return calendar;
    }

    /**
     * 将时，分，秒，以及毫秒值设置为0
     *
     * @param calendar
     */
    private static void zeroFromHour(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static String getWhatDay (long timeStamp) {
        Calendar cal = zeroFromHour(timeStamp);
        String whatDay="";
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
            whatDay="每周六";
        }
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            whatDay="每周日";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            whatDay = "每周一";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            whatDay = "每周二";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            whatDay = "每周三";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            whatDay = "每周四";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            whatDay = "每周五";
        }
        return whatDay;
    }

    /**
     * 获取年
     * @return
     */
    public static int getYear(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.YEAR);
    }
    /**
     * 获取月
     * @return
     */
    public static int getMonth(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.MONTH)+1;
    }
    /**
     * 获取日
     * @return
     */
    public static int getDay(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.DATE);
    }
    /**
     * 获取时
     * @return
     */
    public static int getHour(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.HOUR);
    }
    /**
     * 获取分
     * @return
     */
    public static int getMinute() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MINUTE);
    }

    //获取今天的年月日  yyyy-mm-dd
    public static String getCurentDatas() {
        int currentMonth = getMonth();
        String month = currentMonth > 9 ? "" + currentMonth : "0" + currentMonth;
        int day = getCurentDay();
        String days = day > 9 ? "" + day : "0" + day;
        return getCurentYear() + "-" + month + "-" + days;
    }

    //获取当前的年
    public static int getCurentYear() {
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.YEAR);
    }

    //获取今天的日期
    public static int getCurentDay(){
        // 使用calendar.get(Calendar.DAY_OF_MONTH) 第二次点进页面会有bug,日期显示为一个月的最后一天
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    //获取根据年，月所在的天数
    public static int getDayByMonth(int year, int month) {
        Calendar cd = Calendar.getInstance();
        cd.set(Calendar.YEAR, year);
        cd.set(Calendar.MONTH, month - 1);
        cd.set(Calendar.DATE, 1);
        cd.roll(Calendar.DATE, -1);
        int maxDate = cd.get(Calendar.DATE);
        return maxDate;
    }

    //第一个参数为格式如yy-MM-dd HH:mm:ss得到 '2002-1-1 17:55:00'
    public static String getFormatedDateTime(String pattern, long dateTime) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date(dateTime + 0));
    }

    /**
     * 根据提供的年月日获取该月份的第一天
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:26:57
     * @param year
     * @param monthOfYear
     * @return
     */
    public static String getSupportBeginDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(cal.getTime());
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:29:38
     * @param year
     * @param monthOfYear
     * @return
     */
    public static String getSupportEndDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(cal.getTime());
    }

    /**
     * 会话内时间显示规则：
     * 当天消息只显示具体时间, 举例子：18:09
     * 昨天和前天，举例: 昨天 18:09
     * 近7天（排除今天，昨天，前天）举例：周日 18:09
     * 今年其他时间，举例：4-22 18:09
     * 今年之前的时间，举例：2015-4-22 18:09
     * 时间显示的间隔：当两次发送或收取消息间隔大于5分钟，则显示新的时间
     */

    //用于显示消息具体时间
    public String getDetailTime() {
        //最后一条消息的 年 月 日 时 分
        //yyyy-MM-dd HH:mm:ss
        java.sql.Date date = new java.sql.Date(mTimeStamp);
        String dateStr = format(date, "yyyy-MM-dd HH:mm:ss ");
        String oldYear = dateStr.substring(0, 4);
        int oldMonth = Integer.parseInt(dateStr.substring(5, 7));
        int oldDay = Integer.parseInt(dateStr.substring(8, 10));
        String oldHour = dateStr.substring(11, 13);
        String oldMinute = dateStr.substring(14, 16);

        //当前时间
        long today = JCoreInterface.getReportTime();//当前时间
        java.sql.Date now = new java.sql.Date(today * 1000);//当前时间
        String nowStr = format(now, "yyyy-MM-dd HH:mm:ss ");

        String newYear = nowStr.substring(0, 4);
        int newMonth = Integer.parseInt(nowStr.substring(5, 7));
        int newDay = Integer.parseInt(nowStr.substring(8, 10));//当前 日
        String newHour = nowStr.substring(11, 13);
        String newMinute = nowStr.substring(14, 16);
        String result = "";
        long l = today * 1000 - mTimeStamp;
        long days = l / (24 * 60 * 60 * 1000);
        long hours = (l / (60 * 60 * 1000) - days * 24);
        long min = ((l / (60 * 1000)) - days * 24 * 60 - hours * 60);
        long s = (l / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - min * 60);

        if (!oldYear.equals(newYear)) {
            //往年
            result = oldYear + "-" + oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
        } else {
            //今年
            //同月
            if (oldMonth == newMonth) {
                //同天
                if (oldDay == newDay) {
                    result = oldHour + ":" + oldMinute;
                } else {
                    //不同天
                    int day = newDay - oldDay;
                    if (day == 1) {
                        result = "昨天 " + oldHour + ":" + oldMinute;
                    } else if (day == 2) {
                        result = "前天 " + oldHour + ":" + oldMinute;
                    } else if (day > 2 && day < 8) {
                        int week = date.getDay();
                        if (week == 1) {
                            result = "周一" + " " + oldHour + ":" + oldMinute;
                        } else if (week == 2) {
                            result = "周二" + " " + oldHour + ":" + oldMinute;
                        } else if (week == 3) {
                            result = "周三" + " " + oldHour + ":" + oldMinute;
                        } else if (week == 4) {
                            result = "周四" + " " + oldHour + ":" + oldMinute;
                        } else if (week == 5) {
                            result = "周五" + " " + oldHour + ":" + oldMinute;
                        } else if (week == 6) {
                            result = "周六" + " " + oldHour + ":" + oldMinute;
                        } else {
                            result = "周日" + " " + oldHour + ":" + oldMinute;
                        }
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                }
            } else {
                if (oldMonth == 1 || oldMonth == 3 || oldMonth == 5 || oldMonth == 7 || oldMonth == 8 || oldMonth == 10 || oldMonth == 12) {
                    if (newDay == 1 && oldDay == 30) {
                        result = "前天 " + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 31) {
                        result = "昨天 " + oldHour + ":" + oldMinute;
                    } else if (newDay == 2 && oldDay == 31) {
                        result = "前天 " + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                } else if (oldMonth == 2) {
                    if (newDay == 1 && oldDay == 27 || newDay == 2 && oldDay == 28) {
                        result = "前天 " + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 28) {
                        result = "昨天 " + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                } else if (oldMonth == 4 || oldMonth == 6 || oldMonth == 9 || oldMonth == 11) {
                    if (newDay == 1 && oldDay == 29) {
                        result = "前天 " + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 30) {
                        result = "昨天 " + oldHour + ":" + oldMinute;
                    } else if (newDay == 2 && oldDay == 30) {
                        result = "前天 " + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                }
            }
        }
        return result;
    }

    public static String format(java.sql.Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
