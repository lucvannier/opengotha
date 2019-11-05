/*
 * IPLoc contains a set of static methods to get geo information on an IP address
 * Information is read from the URL specified by requestURL
 *  is supposed to  "http://ip-api.com/xml/" + externalIPAddress;
 */
package info.vannier.gotha;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author Luc
 */
public class IPLoc {
    
    public static String readStringFromURL(String externalIPAddress) throws IOException{
        String requestURL = "http://ip-api.com/xml/" + externalIPAddress;
        String str = "";
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            str = scanner.hasNext() ? scanner.next() : "";
            str = str.replace("<![CDATA[", "");
            str = str.replace("]]>", "");
        }
        return str;
    }
    
    public static String getCityFromDoc(String strDoc){
        int pos = strDoc.indexOf("<city>");
        if (pos <=0) return "???";
        String strCity = strDoc;
        strCity = strCity.substring(pos+ "<city>".length());
        pos = strCity.indexOf("</city>");
        strCity = strCity.substring(0, pos );
        return strCity;
    }
  
    public static String getCountryFromDoc(String strDoc){
        int pos = strDoc.indexOf("<country>");
        if (pos <=0) return "???";
        String strCountry = strDoc;
        strCountry = strCountry.substring(pos+ "<country>".length());
        pos = strCountry.indexOf("</country>");
        strCountry = strCountry.substring(0, pos );
        return strCountry;
    }
}
