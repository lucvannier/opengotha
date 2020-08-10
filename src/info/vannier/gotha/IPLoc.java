/*
 * IPLoc contains a set of static methods to get geo information on an IP address
 * Information is read from the URL specified by requestURL
 *  is supposed to  "http://ip-api.com/xml/" + externalIPAddress;
 */
package info.vannier.gotha;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Luc
 */
public class IPLoc {
    private static HashMap<String, String> hmIPLocs = new HashMap<String, String>();
    private static int requestNumber = 0;
    public static String readStringFromURL(String externalIPAddress) throws IOException{
        String strLoc = hmIPLocs.get(externalIPAddress);
        if (strLoc == null){
            String requestURL = "http://ip-api.com/xml/" + externalIPAddress;
            try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                    StandardCharsets.UTF_8.toString()))
            {
                scanner.useDelimiter("\\A");
                strLoc = scanner.hasNext() ? scanner.next() : "";
                strLoc = strLoc.replace("<![CDATA[", "");
                strLoc = strLoc.replace("]]>", "");
                
            }
            catch(Exception e){
                System.out.println("Exception ip-api.com");
            }

            hmIPLocs.put(externalIPAddress, strLoc);
        }
                
        return strLoc;
    }
    
    public static String getCityFromLoc(String strLoc){
        int pos = strLoc.indexOf("<city>");
        if (pos <=0) return "???";
        String strCity = strLoc;
        strCity = strCity.substring(pos+ "<city>".length());
        pos = strCity.indexOf("</city>");
        strCity = strCity.substring(0, pos );
        return strCity;
    }
  
    public static String getCountryFromLoc(String strLoc){
        int pos = strLoc.indexOf("<country>");
        if (pos <=0) return "???";
        String strCountry = strLoc;
        strCountry = strCountry.substring(pos+ "<country>".length());
        pos = strCountry.indexOf("</country>");
        strCountry = strCountry.substring(0, pos );
        return strCountry;
    }
}
