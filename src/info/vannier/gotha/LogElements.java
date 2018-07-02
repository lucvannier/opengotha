package info.vannier.gotha;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * LogElements contains a set of Log elements
 * Useful for OpenGotha's author, LogElements are sent when OpenGotha is exited
 * The informations are stored into a database by the php script log.php
 * @author Luc Vannier
 * 
 */
public class LogElements {
        
    public static void incrementElement(String name, String value){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences logPrefs = prefsRoot.node(Gotha.strPreferences + "/log");
        
        String strK = name + ":" + value;
        String strNbOcc = logPrefs.get(strK, "0");
        int nbOcc = Integer.parseInt(strNbOcc);
        nbOcc++;
        logPrefs.put(strK, "" + nbOcc);
    }
    
    public static void removeElement(String name, String value){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences logPrefs = prefsRoot.node(Gotha.strPreferences + "/log");
        
        String strK = name + ":" + value;
        logPrefs.remove(strK);
    }
    
    public static void sendLogElements(){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences logPrefs = prefsRoot.node(Gotha.strPreferences + "/log");
        String[] keys = null;
        try {
            keys = logPrefs.keys();
        } catch (BackingStoreException ex) {
            Logger.getLogger(LogElements.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String key : keys){
            int pos = key.indexOf(":");
            String name = key.substring(0, pos);
            String value = key.substring (pos + 1, key.length());
            String strNbOcc = logPrefs.get(key, "0");
            int nbOcc = Integer.parseInt(strNbOcc);

            String strLog = "name=" + name + "&value= " + value + "&nbocc=" + nbOcc;
            strLog = strLog.replaceAll(" ", "%20");
                     
            URI uri = null;
            try {
                uri = new URI("http://vannier.info/gotha/log.php?" + strLog);
            } catch (URISyntaxException ex) {
                Logger.getLogger(LogElements.class.getName()).log(Level.SEVERE, null, ex);
            }
            URL url = null;
            try {
                url = uri.toURL();
            } catch (MalformedURLException ex) {
                Logger.getLogger(LogElements.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedInputStream bis = null;
           
//            String strReceived = "";
            URLConnection urlc;
            try {
                urlc = url.openConnection();
                urlc.getInputStream();
                bis = new BufferedInputStream(urlc.getInputStream());
                int i;
                while((i = bis.read()) != -1){
                    char c = (char) i;
//                    strReceived += c;
                }
                bis.close();
                LogElements.removeElement(name, value);
            } catch (IOException ex) {
                System.out.println("sendLogElements : IOException");
                return;
            }
        }

    }
    
}
