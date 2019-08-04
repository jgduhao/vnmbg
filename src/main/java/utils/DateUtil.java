package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private final static String dateTimeFormat = "yyyyMMddHHmmss";
    private final static String dateTimeSSSFormat = "yyyyMMddHHmmssSSS";

    public static String getDateTime(){
        return new SimpleDateFormat(dateTimeFormat).format(new Date());
    }

    public static String getDateTimeSSS(){
        return new SimpleDateFormat(dateTimeSSSFormat).format(new Date());
    }

    public static String getDateAfterMonths(Date date,int months){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,months);
        return new SimpleDateFormat(dateTimeFormat).format(calendar.getTime());
    }

    public static void main(String[] args) {
        System.out.println(getDateTimeSSS());
    }

}
