/*
 * Gotha.java
 * 
 */
package info.vannier.gotha;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.help.BadIDException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * Contains general purpose constants
 * and simple static general purpose methods
 * @author Luc Vannier
 */
public class Gotha {
    static final int GOTHA_BASE = 0;
    static final int GOTHA_PREMIUM = 1;   
    static int gothaVariant = GOTHA_PREMIUM;
    
    static Locale locale = Locale.getDefault();
    static final long GOTHA_VERSION = 351L;
    static final long GOTHA_MINOR_VERSION = 2L;
    static final java.util.Date GOTHA_RELEASE_DATE = (new GregorianCalendar(2021, Calendar.JANUARY, 11)).getTime();
    // Data version for serialization. Since 3.23 version, not used because xml and compatibility is always granted in both senses
    //    static final long GOTHA_DATA_VERSION = 201L;
    
    // Should definitely stay below or equal to 32, due to internal limits in costValue() method
    static final int MAX_NUMBER_OF_ROUNDS = 20;
    // Should definitely stay below 16000, due to internal limits in PairingParameterSet parameter values
    // Should definitely stay below 9999, due to printing issues
    static final int MAX_NUMBER_OF_PLAYERS = 2000;
    // Should definitely stay below 10, due to complexity issues in costValue function    
    static final int MAX_NUMBER_OF_CATEGORIES = 9;
    static final int MAX_RANK = 8;      // =  9D
    static final int MIN_RANK = -30;    // = 30K
    static final int MAX_NUMBER_OF_TABLES = MAX_NUMBER_OF_PLAYERS / 2;
    static final int MIN_NUMBER_OF_MEMBERS_BY_TEAM = 2;
    static final int MAX_NUMBER_OF_MEMBERS_BY_TEAM = 10;
    static final int MAX_NUMBER_OF_TEAMS = MAX_NUMBER_OF_PLAYERS / MAX_NUMBER_OF_MEMBERS_BY_TEAM;
    static final int RUNNING_MODE_UNDEFINED = 0;    // Undefined
    static final int RUNNING_MODE_SAL = 1;          // Stand alone
    static final int RUNNING_MODE_SRV = 2;          // Server
    static final int RUNNING_MODE_CLI = 3;          // Client
    static int runningMode = RUNNING_MODE_UNDEFINED;
    static String serverName = "";  // relevant only when in Client mode
    static String clientName = "";  // relevant in Client mode
    static String strPreferences = "info/vannier/opengotha";
    static File runningDirectory;

    static File tournamentDirectory;
    static File exportDirectory;
    static File exportHTMLDirectory;
    
    static final int TU_NONE = 0;
    static final int TU_EVERYSAVE = 1;
    static final int TU_EVERYCHANGE = 2;

    public static String getGothaVersionNumber() {
        int mainVersion = (int) (GOTHA_VERSION / 100L);
        int auxVersion = (int) (GOTHA_VERSION % 100L);
        String strMainVersion = "" + mainVersion;
        String strAuxVersion = "" + auxVersion;
        if (auxVersion <= 9) {
            strAuxVersion = "0" + auxVersion;
        }
        return strMainVersion + "." + strAuxVersion;
    }

    /**
    * @return 
    * Returns X.yy if minor version = 0
    * Returns X.yy.zz if minor version != 0
     */
    public static String getGothaFullVersionNumber() {
        int minorVersion = (int) GOTHA_MINOR_VERSION;
        String strMinorVersion = "";
        if (minorVersion != 0) {
            strMinorVersion += minorVersion;
            if (minorVersion <= 9) {
                strMinorVersion = "0" + minorVersion;
            }
            strMinorVersion = "." + strMinorVersion;
        }
        return getGothaVersionNumber() + strMinorVersion;
    }

    public static String getGothaReleaseMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM yyyy", Gotha.locale);
        return sdf.format(GOTHA_RELEASE_DATE);
    }

    private static String getGothaName() {
        return "OpenGotha";
    }

    public static String getGothaVersionnedName() {
        return getGothaName() + " " + getGothaFullVersionNumber();
    }

    public static String getCopyLeftText() {
        String str = Gotha.getGothaVersionnedName();
        str += "    " + Gotha.getGothaReleaseMonthYear();
        str += "\n\n" + "OpenGotha is a free software";
        str += "\n" + "You may copy and deal it as far as you respect the terms of";
        str += "\n" + "GPL licence (GNU Public Licence)";
        str += "\n" + "as published by the Free Software Foundation";
        str += "\n" + "Full text of licence can be found in gpl.txt";

        return str;
    }

    public static String getThanksToText() {
        String str = "";
        str += "\n\nOpenGotha  has  been designed and written by Luc Vannier,";
        str += "\nwith precious help from many people.";
        str += "\nMaximum matching algorithm is a development by UCSB JICOS project";
        str += "\nof an O(n^3) implementation of Edmonds' algorithm, as presented";
        str += "\nby Harold N. Gabow. Jean-François Bocquet adapted the algorithm";
        str += "\nto 64 bits";
        str += "\nDirect Confrontation algorithm has been designed and written by";
        str += "\nMatthieu Walraet";
        str += "\nOther contributors are Alan Abramson, Paul Baratou, Jonathan M Bresler, Claude Brisson,";
        str += "\nClaude Burvenich, Barkın Çelebican, Laurent Coquelet, Loïc Cuvillon, Ian Davis,";
        str += "\nTilo Dickopp, Olivier Dulac, André Engels, Krzysztof Grabowski, Bart Jacob, Marc Krauth,";
        str += "\nRoland Lezuo, Guillaume Largounez, Loïc Lefebvre, Fabien Lips, Richard Mullens, François Mizessyn,";
        str += "\nKonstantin Pelepelin, Sylvain Ravera, Wandrille Sacquépée, Grzegorz Sobański,";
        str += "\nTuomo Salo, Rémi Vannier, Rory Wales and many others.";
        str += "\n\nThanks to all of them !";

        return str;
    }
    
    public static String getExternalIPAddress(){        
        URL whatismyip = null;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Gotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader in;
        String ip = null;
        try {
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = in.readLine(); //you get the IP as a String
        } catch (IOException ex) {
//            Logger.getLogger(Gotha.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Internet not available");
        }        
        return ip;
    }
    
    /**
    * Eliminates forbidden characters : \ / : * ? " < > |
    * These 9 characters are forbidden in Windows file names
     * @param str
     * @return 
    */
    public static String eliminateForbiddenCharacters(String str){
        String newStr = str;
        newStr = newStr.replace("\\", "");
        newStr = newStr.replace("/", "");
        newStr = newStr.replace(":", "");
        newStr = newStr.replace("*", "");
        newStr = newStr.replace("?", "");
        newStr = newStr.replace("\"", "");
        newStr = newStr.replace("<", "");
        newStr = newStr.replace(">", "");
        newStr = newStr.replace("|", "");

        return newStr;
    }

     /**
     * converts a fractional Number defined by (value, coef) into a string representing value/coef
     * fractional part will be formatted as : ½ ¼ ¾
     */
    public static String formatFractNumber(int value, int coef){
         if (coef == -1)   // only Cat
            return "" + (- value + 1);

        int i1 = value / coef;
        int f1 = value % coef;
        String strI1 = "" + i1;
        String strF1 = "";

        if (coef == 2){
            if (f1 == 1) strF1 = "½";
        }
        if (coef == 4){
            if (f1 == 1) strF1 = "¼";
            else if (f1 == 2) strF1 = "½";
            else if (f1 == 3) strF1 = "¾";
        }
        return strI1 + strF1;
    }
    /**
     * converts à long number into a String representation with 3 digits slices
     * @param nb
     * @return 
     */
    public static String formatLongNumberBy3digits(long nb){
        String s = "";
        long nbCurrent = nb;
        while (nbCurrent > 0){
            long quot = nbCurrent / 1000;
            long rem = nbCurrent - quot * 1000;
            String sRem = "" + rem;
            if(rem < 100) sRem = "0" + sRem;
            if(rem < 10 ) sRem = "0" + sRem;
            s = " " + sRem + s;
            nbCurrent = quot;
        } 
        return s;
    }
    
    public static String forceToASCII(String s){
        // Latin 9 upper
        s = s.replaceAll("[ÀÁÂÃÄÅ]", "A");  // 192 - 197
        s = s.replaceAll("Ç", "C");         // 199
        s = s.replaceAll("[ÈÉÊË]", "E");    // 200 - 203
        s = s.replaceAll("[ÌÍÎÏ]", "I");    // 204 - 207
        s = s.replaceAll("Ñ", "N");         // 209
        s = s.replaceAll("[ÒÓÔÕÖØ]", "O");  // 210-214 and 216
        s = s.replaceAll("[ÙÚÛÜ]", "U");    // 217 - 220
        s = s.replaceAll("Ý", "Y");         // 221
        
        // Latin 9 lower
        s = s.replaceAll("[àáâãäå]", "a");  // 224 - 229
        s = s.replaceAll("ç", "c");         // 231
        s = s.replaceAll("[èéêë]", "e");    // 232 - 235
        s = s.replaceAll("[ìíîï]", "i");    // 204 - 207
        s = s.replaceAll("ñ", "n");         // 241
        s = s.replaceAll("[òóôõöø]", "o");  // 242-246 and 248
        s = s.replaceAll("[ùúûü]", "u");    // 249 - 252
        s = s.replaceAll("ý", "y");         // 221

        // Turkish letters
        s = s.replace("ı", "i");
        s = s.replace("ğ", "g");
        s = s.replace("ü", "u");
        s = s.replace("ş", "s");
        s = s.replace("i", "i");
        s = s.replace("ö", "o");
        s = s.replace("ç", "c");
        
        s = s.replace("I", "I");
        s = s.replace("Ğ", "G");
        s = s.replace("Ü", "U");
        s = s.replace("Ş", "S");
        s = s.replace("İ", "I");
        s = s.replace("Ö", "O");
        s = s.replace("Ç", "C");


//        s = s.replaceAll("[[^a-z]&&[^A-Z]&&[^ ]]", "");
        for (int iC = 0; iC < s.length(); iC++){
            char c = s.charAt(iC);
           if (c > 127) 
               s = s.replace(c, '?');
        }        
        return s;
    }

    /**
     * Returns a String with numberOfCharacters characters, whatever the length of str is.
     */
    public static String leftString(String str, int numberOfCharacters) {
        String resStr;
        if (str == null) {
            resStr = "";
        } else {
            resStr = str;
        }
        while (resStr.length() < numberOfCharacters) {
            resStr = resStr + " ";
        }
        resStr = resStr.substring(0, numberOfCharacters);
        return resStr;
    }

    /**
     * Returns a new string that is a substring of this string. 
     * The substring begins at the specified beginIndex and extends to the character at index endIndex - 1. 
     * Thus the length of the substring is endIndex-beginIndex.
     * <br>The difference with the substring method of String class is that sousChaine always returns a String and does not throw any exception.
     * <br>If beginIndex >=  str's length, an empty String is returned
     * <br>If endIndex <= beginIndex  an empty String is returned
     * <br> if endIndex > str's length, the returned string is shortened to str's length - beginIndex
     */
    public static String sousChaine(String str, int beginIndex, int endIndex){
        if (str == null) return "";       
        int lgth = str.length();
        if (beginIndex >= lgth) return "";
        if (endIndex <= beginIndex) return "";
        if (endIndex > lgth) endIndex = lgth;
        
        return str.substring(beginIndex, endIndex);
    }
    
    public static Image getIconImage() {
        URL iconURL = null;
        try {
            iconURL = new URL("file:///" + Gotha.runningDirectory + "/resources/gothalogo64.jpg");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Gotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        ImageIcon ico = new ImageIcon();
        if (iconURL != null) {
            ico = new ImageIcon(iconURL);
        }

        return ico.getImage();
    }

    /**
     * for debug purpose
     * @param str
     */
    public static void printTopChrono(String str) {
        long topC = System.nanoTime();
        long nSEC = (topC / 1000000000) % 1000;
        long nMS = (topC % 1000000000) / 1000000;
        long nMicroS = (topC / 1000) % 1000;
        String strSEC = "000" + nSEC;
        strSEC = strSEC.substring(strSEC.length() - 3);
        String strMS = "000" + nMS;
        strMS = strMS.substring(strMS.length() - 3);
        String strMicroS = "000" + nMicroS;
        strMicroS = strMicroS.substring(strMicroS.length() - 3);
        
        System.out.println("topCn = " + strSEC + "." + strMS + " " + strMicroS + " " + str);
    }

    public static boolean isDateExpired(String strDate){
        boolean bExpired = false;
        Calendar currentCal = Calendar.getInstance();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy");
        try {
            Date d = sdf.parse(strDate);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DAY_OF_MONTH, 1);
        
        if (c.before(currentCal)) bExpired = true;

        } catch (ParseException ex) {
//            Logger.getLogger(Gotha.class.getName()).log(Level.SEVERE, null, ex);
            bExpired = false;
        }
        
        return bExpired;
    }
    
    public static TournamentInterface getTournamentFromFile(File f) throws IOException, ClassNotFoundException {
        TournamentInterface t = new Tournament();

        String strReport = ExternalDocument.importTournamentFromXMLFile(f, t, true, true, true, true, true); 
        try {
            t.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return t;
    }

    public static void download(JProgressBar pgb, String strURL, File fDestination) throws MalformedURLException, IOException {
        BufferedInputStream bis;
        FileOutputStream fos;
        
        URL url = new URL(strURL);
        URLConnection urlc = url.openConnection();

        bis = new BufferedInputStream(urlc.getInputStream());
        fos = new FileOutputStream(fDestination);

        int i;
        int contentLength = urlc.getContentLength();
        int nbChars = 0;

        if (pgb != null) {
            pgb.setValue(0);
            pgb.setVisible(true);
        }

        while ((i = bis.read()) != -1) {
            fos.write(i);
            nbChars++;
            if (nbChars % 2000 == 0) {
                int percent = 100 * nbChars / contentLength;
                if (contentLength <= 0){
                    // Rough estimation of percent
                    int estimatedLength = 100000;
                    percent = 100 * nbChars / (nbChars + estimatedLength);
                }
                if (pgb != null) {
                    pgb.setValue(percent);
                    pgb.paintImmediately(0, 0, pgb.getWidth(), pgb.getHeight());
                }
            }
        }

        if (pgb != null) {
            pgb.setVisible(false);
        }
        fos.close();
        bis.close();
    }

    public static String getHostName() {
        String hostName = "";
        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (java.net.UnknownHostException e) {
        }
        return hostName;
    }

    /**
     * gets all available InetAddresses from all available NetworkInterfaces
     *
     * @return
     */
    public static ArrayList<InetAddress> getAvailableIPAddresses() {
        ArrayList<InetAddress> al = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    InetAddress adIP = enumIpAddr.nextElement();
                    al.add(adIP);
                }
            }

        } catch (SocketException e) {
            System.out.println(" (error retrieving network interface list)");
        }
        return al;
    }

    /**
     * chooses the best IP Address
     * eliminates :
     * <br> non IP32 formed addresses
     * <br> loopback addresses
     * <br> stops elimination process when only one address remains.
     * <br> if several addresses remains, returns the first one
     * @return
     */
    public static InetAddress getBestIPAd() {
        ArrayList<InetAddress> alIPAd = Gotha.getAvailableIPAddresses();

        if (alIPAd.isEmpty()) {
            return null;
        }
        if (alIPAd.size() == 1) {
            return alIPAd.get(0);
        }
        // Eliminate non IPv4 addresses
        for (int i = alIPAd.size() - 1; i >= 0; i--) {
            if (alIPAd.size() == 1) {
                return alIPAd.get(0);
            }

            InetAddress ipAd = alIPAd.get(i);
            byte[] b = ipAd.getAddress();

            if (b.length != 4) {
                alIPAd.remove(i);
            }
        }

        // Eliminate loopback addresses
        for (int i = alIPAd.size() - 1; i >= 0; i--) {
            if (alIPAd.size() == 1) {
                return alIPAd.get(0);
            }

            InetAddress ipAd = alIPAd.get(i);
            byte[] b = ipAd.getAddress();
            if (b[0] == 127) {
                alIPAd.remove(i);
            }
        }

        return alIPAd.get(0);
    }

    public static void displayGothaHelp(String topic) {
        HelpSet hs;
        // What is the help language ?
        
        String strHelpDirectory = "english";
        File f = new File(Gotha.runningDirectory, "gothahelp/" + strHelpDirectory + "/helpset.hs");
        try {
            URI uri = f.toURI();
            URL url = uri.toURL();
            hs = new HelpSet(null, url);
            HelpBroker hb = hs.createHelpBroker();
            hb.setCurrentID(topic);
            hb.setDisplayed(true);

        } catch (MalformedURLException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HelpSetException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadIDException ex) {
            System.out.println("Non existing topic");
        }
    }

    public static boolean isRatingListsDownloadEnabled(){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);

        String strK = "ratingListsDownload";
        String strRLD = gothaPrefs.get(strK, "true");
        return strRLD.equals("true");
    }

    public static boolean isPhotosDownloadEnabled(){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);

        String strK = "photosDownload";
        String strPD = gothaPrefs.get(strK, "true");
        return strPD.equals("true");
    }
    
    
    public static int getTournamentUploadStatus(){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);

        String strK = "tournamentUploadStatus";
        String strPD = gothaPrefs.get(strK, "" + Gotha.TU_EVERYSAVE);
        
        return Integer.parseInt(strPD);
    }
    
    public static void setRatingListsDownloadEnabled(boolean enabled){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        String strK = "ratingListsDownload";
        gothaPrefs.put(strK, "" + enabled);
    }

    public static void setPhotosDownloadEnabled(boolean enabled){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        String strK = "photosDownload";
        gothaPrefs.put(strK, "" + enabled);
    }
    
    public static void setTournamentUploadStatus(int tus){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        String strK = "tournamentUploadStatus";
        gothaPrefs.put(strK, "" + tus);
    }
    
//    public static void setJournalingReportEnabled(boolean enabled){
//        Preferences prefsRoot = Preferences.userRoot();
//        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
//        String strK = "journalingReport";
//        gothaPrefs.put(strK, "" + enabled);
//    }
    
    public static String getPreference(String strK){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        String strPref = gothaPrefs.get(strK, "");
        return strPref;
    }
    public static void setPreference(String strK, String strValue){
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        gothaPrefs.put(strK, strValue);
    }
}


class GothaImageLoader extends Thread{
    String strURL;
    JLabel lbl;
    public GothaImageLoader(String strURL, JLabel lbl){
        this.strURL = strURL;
        this.lbl = lbl;
    }
    @Override
    public void run(){
        URL url = null;
        try {              
            url = new URL(strURL);
        } catch (MalformedURLException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ImageIcon myIcon = new ImageIcon(url);
        lbl.setIcon(myIcon);
    }
    
    public static void loadImage(String strURL, JLabel lbl){
        (new GothaImageLoader(strURL, lbl)).start();
    }
}
