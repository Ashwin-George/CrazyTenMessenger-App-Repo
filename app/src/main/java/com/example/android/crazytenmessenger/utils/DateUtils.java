package com.example.android.crazytenmessenger.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


    static public String getCurrentTimestamp(){
        String timeStamp=String.valueOf(System.currentTimeMillis());
        return timeStamp;
    }
    static public boolean checkToday(String timeStamp){
        String currentTimeStamp=getCurrentTimestamp();
        SimpleDateFormat testerDateFormat=new SimpleDateFormat("EEE, MMM d, ''yy");
        String dateGiven=testerDateFormat.format(new Date(Long.parseLong(timeStamp)));
        String dateCurrent=testerDateFormat.format(new Date(Long.parseLong(currentTimeStamp)));
        return dateCurrent.equals(dateGiven);
    }
    static public String getSampleFormattedDate(String timeStamp){
        SimpleDateFormat dateFormat;
        Long unixTimeStamp=Long.parseLong(timeStamp);
        if(!checkToday(timeStamp)){
            dateFormat=new SimpleDateFormat("h:mm a \tEEE, MMM d, ''yy");
        }else {
            dateFormat=new SimpleDateFormat("h:mm a");
        }
        String dateString=dateFormat.format(new Date(unixTimeStamp));
        return dateString;
    }
}
