/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 *
 * @author markwartman
 */
public class TimeZone_utility {
    
    public static String tzAdjust(String date, String time, String isZone){
        
        LocalDateTime ldt = LocalDateTime.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)), Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(3, 5)), 00);
        ZonedDateTime zdt;
        
        if(isZone.contains("toUTC")){
            
            // ADJUST TIMEZONE TO UNIVERSAL TIME
            zdt   = ZonedDateTime.of(ldt, ZoneId.systemDefault());
            ZonedDateTime zdt_utc   = zdt.withZoneSameInstant(ZoneId.of("UTC"));
            //System.out.println("UTC.zone:  " + zdt_utc);
            //System.out.println("ISO_DATE_TIME: " + zdt_utc.format(DateTimeFormatter.ISO_INSTANT).substring(0, 19) );
            return zdt_utc.format(DateTimeFormatter.ISO_INSTANT).substring(0, 19);
        }
        else if(isZone.contains("toLocal")){
            
            // ADJUST TIMEZONE TO LOCAL INSTANCE
            zdt   = ZonedDateTime.of(ldt, ZoneId.of("UTC"));
            zdt   = zdt.withZoneSameInstant(ZoneId.systemDefault());
            //System.out.println("this.zone: " + zdt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return zdt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            
            
        }
        
        return null;
    }
     
}
