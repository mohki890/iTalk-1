package com.example.gagan.italk.com.example.gagan.italk.SmallFunctions;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

/**
 * Created by gagan on 12/7/15.
 */
public class TimeConvertor {
    private static long ONLINE_DURATION=60;//sec
    private static String MONTHS[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Nov","Dec"};
    private static Timestamp todayTimeStamp;
    private TimeConvertor()
    {
    }

    public static void setTodayTimeStamp(String timeStamp)
    {
        todayTimeStamp =Timestamp.valueOf(timeStamp);
    }

    public static String getShortTime(String Stimestamp)
    {
        Timestamp timestamp =Timestamp.valueOf(Stimestamp);

        String Sdate=getJustShortTime(Stimestamp);
        if(todayTimeStamp==null) return Sdate;
        if( (todayTimeStamp.getTime()-timestamp.getTime())<=ONLINE_DURATION*1000 )
            Sdate="Now";

        return Sdate;

    }
    public static String getJustShortTime(String Stimestamp)
    {
        if(todayTimeStamp==null)
            setServerTimeFromMobile();
        Timestamp timestamp =Timestamp.valueOf(Stimestamp);
        String Sdate="";
        int date=timestamp.getDate(),mon=timestamp.getMonth(),year=timestamp.getYear();
        int Tdate=todayTimeStamp.getDate(),Tmon=todayTimeStamp.getMonth(),Tyear=todayTimeStamp.getYear();
        if(date==Tdate && mon==Tmon && year==Tyear)
        {
            int h=timestamp.getHours();
            Sdate=((h%12)==0?12:h%12)+":"+timestamp.getMinutes()+(h>=12?"PM":"AM");
        }
        else
        {
            Sdate=date+"-"+MONTHS[mon]+"-"+(year%100);


        }
        return Sdate;

    }
    public static void setServerTimeFromMobile()
    {
     todayTimeStamp=new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
    }
}
