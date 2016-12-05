package com.bbxiaoqu.comm.tool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dzy on 2016/8/1.
 */
public class CommUtil {
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6367000; //approximate radius of earth in meters
        lat1 = (lat1 * Math.PI) / 180;
        lng1 = (lng1 * Math.PI) / 180;
        lat2 = (lat2 * Math.PI) / 180;
        lng2 = (lng2 * Math.PI) / 180;
        double calcLongitude = lng2 - lng1;
        double calcLatitude = lat2 - lat1;
        double stepOne = Math.pow(Math.sin(calcLatitude / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(calcLongitude / 2), 2);
        double stepTwo = 2 * Math.asin(Math.min(1, Math.sqrt(stepOne)));
        double calculatedDistance = earthRadius * stepTwo;
        return Math.round(calculatedDistance); //四舍五入
    }


    public static String gethour(String sendtime)
    {
        DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String result="";
        try
        {
            Date d1 = new Date ();
            Date d2 = df.parse(sendtime);

            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            //System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            if(days>0)
            {
                result= String.valueOf (days)+"天前";
            }else if(hours>0)
            {
                result= String.valueOf (hours)+"小时前";
            }else if(minutes>0)
            {
                result= String.valueOf (minutes)+"分钟前";
            }

        }
        catch (Exception e)
        {
        }
        return result;
    }
}
