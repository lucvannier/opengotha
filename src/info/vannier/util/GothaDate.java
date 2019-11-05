/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.vannier.util;

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Luc
 * if unparsable, returns current date
 */
public class GothaDate {
    public static Date parse(String strDate, String pattern){
        Date date = new Date(0, 0, 1);
        SimpleDateFormat sdfDate = new SimpleDateFormat(pattern);
        boolean valid = true;
        try {
            date = sdfDate.parse(strDate);
        } catch (ParseException ex) {
            valid = false;
            System.out.println("GothaDate.unparsable String : " + strDate);
        }
        if (date.getYear() <= 0) valid = false;
        
        // check errors like yyyy-13-dd or yyy-MM-32
        String strNewDate = new SimpleDateFormat(pattern).format(date);
        if(!strNewDate.equals(strDate)) valid= false;
        
        if (!valid){
            date = new Date(0, 0, 1);
            System.out.println("GothaDate.unvalid String : " + strDate);
        }
        return date;
    }
}
