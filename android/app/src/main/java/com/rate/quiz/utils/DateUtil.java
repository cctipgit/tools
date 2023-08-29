package com.rate.quiz.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rate.quiz.R;
import com.rate.quiz.entity.KLineEntityChild;
import com.rate.quiz.entity.KLineEntity;

import java.io.InputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class DateUtil {
    /**
     * 获取系统时间戳
     * @return
     */
    public long getCurTimeLong(){
        long time=System.currentTimeMillis();
        return time;
    }
    /**
     * 获取当前时间
     * @param pattern
     * @return
     */
    public static String getCurDate(String pattern){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new java.util.Date());
    }

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }
    private static ArrayList<KLineEntity> sortData(ArrayList<KLineEntity> mList) {
        Collections.sort(mList, new Comparator<KLineEntity>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *     equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(KLineEntity lhs, KLineEntity rhs) {
                Date date1 = stringToDate(lhs.getTime());
                Date date2 = stringToDate(rhs.getTime());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date2.before(date1)) {
                    return 1;
                }
                return -1;
            }
        });
        return mList;
    }
    //随机数获取
    public static int getRandom(int number){
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(number);
        return index;
    }

    public static List<KLineEntityChild> getALL(Context context) {
        List<KLineEntityChild> datas = null;
        if (datas == null) {
            String string = getStringFromAssert(context, "ibm.json");
            Log.i("string==", string);
            final List<KLineEntityChild> data = new Gson().fromJson(getStringFromAssert(context, "ibm.json"), new TypeToken<List<KLineEntityChild>>() {
            }.getType());

            datas = data;
        }
        return datas;
    }
    public static String getStringFromAssert(Context context, String fileName) {
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            return new String(buffer, 0, buffer.length, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("exception==",e.getMessage());
        }
        return "";
    }
    public static String timetodate(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(time));
        SimpleDateFormat sf = new SimpleDateFormat("dd HH:mm");//这里的格式可换"yyyy年-MM月dd日-HH时mm分ss秒"等等格式

        String date = sf.format(calendar.getTime());
        return date;

    }

    public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //转化天
    public static String timeStamp2Date(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }
    public static String timezoneToTime(String timezone) {
        //服务器时区格式
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //需要展示时间格式
        SimpleDateFormat toSimple = new SimpleDateFormat("dd");
        // 本地时区
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        // 设定SDF的时区为本地
        toSimple.setTimeZone(localZone);

        // 设置时间区域为GMT
        simple.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            //解析GMT时间
            Date fromDate = simple.parse(timezone);
            // GMT转当前时区时间
            String toTime = toSimple.format(fromDate);
            return toTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "dd";
    }

    public static String getTimeFormat(Context context,String timeStr){
        String month = "";
        String strTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = sdf.parse(timeStr);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd hh:mm");
             strTime = sdf1.format(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
            String strTime2 = sdf2.format(date);
            month = strTime2;
            if (month.equals("01")){
                month = context.getResources().getString(R.string.january);
            }else if (month.equals("02")){
                month = context.getResources().getString(R.string.february);

            }else if (month.equals("03")){
                month = context.getResources().getString(R.string.march);

            }else if (month.equals("04")){
                month =context.getResources().getString(R.string.april);

            }else if (month.equals("05")){
                month = context.getResources().getString(R.string.may);

            }else if (month.equals("06")){
                month = context.getResources().getString(R.string.june);

            }else if (month.equals("07")){
                month = context.getResources().getString(R.string.july);

            }else if (month.equals("08")){
                month = context.getResources().getString(R.string.august);

            }else if (month.equals("09")){
                month = context.getResources().getString(R.string.september);

            }else if (month.equals("10")){
                month = context.getResources().getString(R.string.october);

            }else if (month.equals("11")){
                month = context.getResources().getString(R.string.november);

            }else if (month.equals("12")){
                month = context.getResources().getString(R.string.december);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return month +" "+ strTime;
    }

//    public static String GTMToLocal(String GTMDate) {
//
//
//        SimpleDateFormat d_f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//        d_f.setTimeZone(TimeZone.getTimeZone("GMT+08"));  //设置时区，+08是北京时间
//        String date = d_f.format(new Date());
//
//        System.out.println("获取的时间为："  +date);
//
//
//        return GTMDate;
//
//
//    }

    public static String stampToDate(String s){
        String date_Date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设定时间格式
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));  //设置时区，+08是北京时间
        long lt = new Long(s);
        Date date = new Date(lt);
        date_Date = simpleDateFormat.format(date);
        System.out.println("获取的时间为："  +date_Date);
        return date_Date;
    }


}
