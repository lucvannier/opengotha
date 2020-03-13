/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.vannier.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Luc
 * if unparsable, returns 1900-01-01
 */
public class GothaDate {
    public static Date parse(String strDate, String pattern){
        Date date;
        GregorianCalendar cal = new GregorianCalendar();            
        cal.set(1900, 0, 1);
        date =  cal.getTime();

        SimpleDateFormat sdfDate = new SimpleDateFormat(pattern);
        boolean valid = true;
        try {
            date = sdfDate.parse(strDate);
        } catch (ParseException ex) {
            valid = false;
            System.out.println("GothaDate.unparsable String : " + strDate);
        }

        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);     
        if (year <= 1900) valid = false;
        
        // check errors like yyyy-13-dd or yyy-MM-32
        String strNewDate = new SimpleDateFormat(pattern).format(date);
        if(!strNewDate.equals(strDate)) valid= false;
        
        if (!valid){
            System.out.println("GothaDate. Invalid date");
            cal.set(1900, 0, 1);
            date =  cal.getTime();
        }
        return date;
    }
    
    public static int getYear(Date d){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);     
    }

}
