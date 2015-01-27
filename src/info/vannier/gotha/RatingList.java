/*
 * RatingList.java
 *
 * Represents a rating list.
 * A rating list may come from EGF or from FFG
 */

package info.vannier.gotha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RatingList {
    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_EGF = 1;
    public static final int TYPE_FFG = 257;
    public static final int TYPE_AGA = 513;
    
    private int ratingListType = TYPE_UNDEFINED;
    private String strPublicationDate = "";
    private ArrayList<RatedPlayer> alRatedPlayers = new ArrayList<RatedPlayer>();
    private HashMap<String, RatedPlayer> hmPinRatedPlayers; 
    private HashMap<String, RatedPlayer> hmNaFiRatedPlayers; 

    /** Creates a new instance of RatingList */
    public RatingList() {
        ratingListType = TYPE_UNDEFINED;
    }
    
    /** Creates a new instance of RatingList */
    public RatingList(int ratingListType, File f) {
        this.ratingListType = ratingListType;
        parseFile(f);
        
        // Build HashMap based on egfPin
        hmPinRatedPlayers = new HashMap<String, RatedPlayer>();
        for (RatedPlayer rp : alRatedPlayers){
            hmPinRatedPlayers.put(rp.getEgfPin(), rp);
        }

        // Build HashMap based on Name and firstName
        hmNaFiRatedPlayers = new HashMap<String, RatedPlayer>();
        for (RatedPlayer rp : alRatedPlayers){
            String strNaFi = (rp.getName() + rp.getFirstName()).replaceAll(" ", "").toUpperCase();
            hmNaFiRatedPlayers.put(strNaFi, rp);
        }
    }

    private void parseFile(File f){
        // Transfer file content to a ArrayList<String>
        ArrayList<String> vLines = new ArrayList<String>();
        try{
            FileInputStream fis = new FileInputStream(f);
            BufferedReader d = new BufferedReader(new InputStreamReader(fis, java.nio.charset.Charset.forName("ISO-8859-15")));

            String s;
            do{
                 s = d.readLine();
                if (s != null){
                    vLines.add(s);
                }
            } while (s !=null);
            d.close();
            fis.close();
        } catch (Exception ex){
            Logger.getLogger(RatingList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Parse rating list
        for (String strLine : vLines){
            if (strLine.length() == 0) continue;            
            int pos;
            
            if (ratingListType == TYPE_EGF){
                pos = strLine.indexOf("(");                
                if (pos >= 0) {
                    String str = strLine.substring(pos);
                    String[] dateElements = str.split(" ");
                    if ((dateElements.length >= 3)
                            && (dateElements[2].length() >= 2)
                            && (dateElements[2].substring(0,2).compareTo("20") == 0) ) // Just take care of the "2100 year bug" :)
                        strPublicationDate = str.substring(1, 12);
                }
                if (strLine.length() < 10) continue;
                String strPin = strLine.substring(1, 9);
                if (strPin.matches("[0-9]*")){
                    String strNF = strLine.substring(11, 48);
                    String strName = strNF;
                    String strFirstName = "x";
                    
                    pos = strNF.indexOf(" ");
                    
                    if (pos > 0) strName = strNF.substring(0, pos).trim();
                    if ((pos + 1) < strNF.length()) strFirstName = strNF.substring(pos + 1, strNF.length()).trim();
                    
                    String strCountry = strLine.substring(49, 52).trim();
                    String strClub = strLine.substring(53, 57).trim();
                    int rating = new Integer(strLine.substring(71, 75).trim()).intValue();
                    String strGrade = strLine.substring(60,63);
                    RatedPlayer rP = new RatedPlayer(
                            strPin, "", "", "", "", strName, strFirstName, strCountry, strClub, rating, strGrade, "EGF");
                    alRatedPlayers.add(rP);
                }
            }

            if (ratingListType == TYPE_FFG){
                pos = strLine.indexOf("Echelle au ");
                if (pos >= 0) {
                    strPublicationDate = strLine.substring(pos + 11, strLine.length());
                }
                if (strLine.length() < 60) continue;
                
                String strNF = strLine.substring(0, 38);
                if (strNF.matches("[a-zA-Z].*")){
                    pos = strNF.indexOf(" ");
                    String strName = strNF.substring(0, pos).trim();
                    String strFirstName = strNF.substring(pos + 1, strNF.length()).trim();
                    int rating = new Integer(strLine.substring(38, 43).trim()).intValue();
                    String strFfgLicenceStatus = strLine.substring(44, 45); 
                    String strFfgLicence = strLine.substring(46, 53);
//                    String strCC = strLine.substring(54, 58).trim();
                    String strClub = strLine.substring(54, 58).trim();
                    String strCountry = strLine.substring(59, 61).trim();
                    
//                    if (strCC.length() <= 2){
//                        strCountry = strCC;
//                        strClub = "";
//                    }
//                    else{
//                        strCountry = "";
//                        strClub = strCC;
//                    }

                     RatedPlayer rP = new RatedPlayer(
                            "", strFfgLicence, strFfgLicenceStatus, "", "", strName, strFirstName, strCountry, strClub, rating, "", "FFG");
                    alRatedPlayers.add(rP);
                }
            }
 
            if (ratingListType == TYPE_AGA){
                if (strLine.length() < 10) continue;
                int AGA_NAFI = 0;
                int AGA_ID = 1;
                int AGA_MTYPE = 2;
                int AGA_RATING = 3;
                int AGA_EXPIRATION = 4;
                int AGA_CLUB = 5;
                String[] myStrArr = strLine.split("\t");
                
                String name = "XXX";
                String firstName = "xxx";
                String agaID = "";
                String agaExpirationDate = "";
                String club = "";
                String country = "US";
                int rawRating = -2850; 
                
                if(myStrArr.length > AGA_NAFI){
                    String strNaFi = myStrArr[AGA_NAFI].trim();
                    if (strNaFi.length() == 0) continue;
                    String[] nameStrArr = strNaFi.split(",");
                    if (nameStrArr.length < 2) nameStrArr = strNaFi.split(" ");
                    name = nameStrArr[0].trim();
                    if(nameStrArr.length > 1) firstName = nameStrArr[1].trim();
                }
                
                if(myStrArr.length > AGA_ID){
                    agaID = myStrArr[AGA_ID];
                }

                if(myStrArr.length > AGA_EXPIRATION){
                    agaExpirationDate = myStrArr[AGA_EXPIRATION];
                }

                if(myStrArr.length > AGA_MTYPE){
                    String agaMType = myStrArr[AGA_MTYPE];
                    if (agaMType.equals("Forgn")) country = "";
                    else country = "US";
                }
                if(myStrArr.length > AGA_RATING){
                    String strAgaRating = myStrArr[AGA_RATING];
                    try{
                        Float d = (Float.parseFloat(strAgaRating));
                        rawRating = (int) Math.round(d.floatValue() * 100.0);
                    }catch(Exception e){} 
                 }
                if(myStrArr.length > AGA_CLUB){
                    club = myStrArr[AGA_CLUB];
                }
                
                RatedPlayer rP = new RatedPlayer(
                        "", "", "", agaID, agaExpirationDate, name, firstName, country, club, rawRating, "", "AGA");
                alRatedPlayers.add(rP);
            }
        } 
    }

    public int indexOf(RatedPlayer rp){
        return alRatedPlayers.indexOf(rp);
    }
    
    public RatedPlayer getRatedPlayer(String egfPin){       
        return hmPinRatedPlayers.get(egfPin);
    }

    public RatedPlayer getRatedPlayer(String name, String firstName){
        String strNaFi = (name + firstName).replaceAll(" ", "").toUpperCase();
        return hmNaFiRatedPlayers.get(strNaFi);
         
    }

    public RatedPlayer getRatedPlayer(Player p){
        String egfPin = p.getEgfPin();
        
        if (!egfPin.equals("")){
            RatedPlayer rp = hmPinRatedPlayers.get(egfPin);
            if (rp != null) return rp;
        }
        
        return getRatedPlayer(p.getName(), p.getFirstName());
    }

        
    // 
    public String getRatedPlayerString(Player p){
        RatedPlayer rp = getRatedPlayer(p);
        return getRatedPlayerString(rp);
    }
 
    public String getRatedPlayerString(RatedPlayer rp){
        String strPlayerString = "";
        if (rp !=null){
            String strAGAID = "";
            if (rp.getRatingOrigin().equals("AGA")) strAGAID = ":" + rp.getAgaId();
            
            strPlayerString = rp.getName() + " " + rp.getFirstName() + strAGAID + " " +
                    rp.getCountry() + " " + rp.getClub() + " " + rp.getStrRawRating();
        }
        return strPlayerString;
    }
    
    public ArrayList<RatedPlayer> getALRatedPlayers() {
        return alRatedPlayers;
    }

    public RatedPlayer getRatedPlayer(int line){
        RatedPlayer rp = null;
        try{
            rp = alRatedPlayers.get(line);
        }catch(Exception e){
            rp = null;
        }
        return rp;
        
    }
  
    /**
     * Search a RatedPlayer by AGAId
     * @param strId
     * @return 
     */
    
    public int getRatedPlayerByAGAID(String strId){
        int nbRP = alRatedPlayers.size();
        for (int i = 0; i < nbRP; i++){
            RatedPlayer rp = alRatedPlayers.get(i);
            if (rp.getAgaId().contentEquals(strId)) return i;
        }
        return -1;     
    }

//    public RatedPlayer getRatedPlayerByAGAID(String strId){
//        for (RatedPlayer rp : this.alRatedPlayers){
//            if (rp.getAgaId().contentEquals(strId)) return rp;
//        }
//        return null;     
//    }
    
    public String getStrPublicationDate() {
        return strPublicationDate;
    }

    public int getRatingListType() {
        return ratingListType;
    }

    public void setRatingListType(int ratingListType) {
        this.ratingListType = ratingListType;
    }
}


