/*
 * Player.java
 */
package info.vannier.gotha;

public class Player implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private String name;
    private String firstName;
    /** keyString is computed at creation/modification time */
    private transient String keyString = null;
    private String country;
    private String club;
    private String egfPin;
    private String ffgLicence;
    private String ffgLicenceStatus;
    private String agaId;
    private String agaExpirationDate;
    /**
     * Rank between -30 (30K) and +8 (9D)
     */
    private int rank = -20;
    
    /**
     * Rating.
     * from -900 to 2949
     * If the rating comes from FFG, rating = FFGrating + 2050.
     */
    static final int MIN_RATING = -900;
    static final int MAX_RATING = 2949;

    private int rating = MIN_RATING;
    
    /**
     * Either "EGF" or "FFG" or "MAN" or "INI"
     * "EGF" for rating coming from European Go DataBase
     * "FFG" for rating coming from FFG Rating list
     * "MAN" for rating specified by the organiser or imported from vBar-separated file
     * "INI" for rating computed from rank
     */
    private String ratingOrigin = "";
        
    /**
     * strGrade is relevant when player is registered from EGF rating list
     * "" when not relevant
     */
    private String strGrade = "";
    
    /**
     * When computing smms, rank is taken as a basis, then framed by McMahon floor and McMahon bar.
     * Then smms is added smsCorrection, which may be 0, 1 or 2, 1 0 for Bar Group, 1 for Super Group and 2 for Supersuper Group
     * smmsCorrection may also be negative;
     */
    private int smmsCorrection = 0;
       
    private boolean[] participating = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
    
    /**
     * "PRE" for Preregistering
     * "FIN" for Final Registering
     */
    private String registeringStatus;
        
    /** Creates a new instance of Player */
    public Player() {
    }
    public Player(Player p) {      
        deepCopy(p);
    }
     
    public Player(String name, String firstName, String country, String club, String egfPin, String ffgLicence, String ffgLicenceStatus,
            String agaId, String agaExpirationDate,
            int rank,  int rating, String ratingOrigin, String strGrade, int smmsCorrection, 
            String registeringStatus) throws PlayerException{
        if (name.length() < 1) throw new PlayerException("Player's name should have at least 1 character");
        this.name = name;
        if (firstName.length() < 1) throw new PlayerException("Player's first name should have at least 1 character");
        this.firstName = firstName;
        this.computeKeyString();
        if (country.length() == 1 || country.length() > 2) throw new PlayerException("Country name should either have 2 characters\n"
                + "or be absent");
        this.country = country;
        if (club.length() > 4) throw new PlayerException("Club name should have at most 4 character");
        this.club = club;
        this.egfPin = egfPin;
        this.ffgLicence = ffgLicence;  
        this.ffgLicenceStatus = ffgLicenceStatus;  
        this.agaId = agaId;
        this.agaExpirationDate = agaExpirationDate;
        // If rank is out of limits, set it according to strGrade, if exists
        if(rank < Gotha.MIN_RANK || rank > Gotha.MAX_RANK) rank = Player.convertKDPToInt(strGrade);
        this.rank = rank;
 
        this.rating = rating;
        this.ratingOrigin = ratingOrigin;
        
        if (strGrade.equals("")) strGrade = Player.convertIntToKD(rank);
        strGrade = strGrade.toUpperCase();
        this.strGrade = strGrade;        

        this.smmsCorrection = smmsCorrection;
        this.registeringStatus = registeringStatus;
        

      
        for(int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
            participating[i] = true;
        }
    }
        
    /** 
     * Copies p into this
     **/
    public void deepCopy(Player p){
        this.name = p.getName();
        this.firstName = p.getFirstName();
        this.keyString = p.getKeyString();
        this.country = p.getCountry();
        this.club = p.getClub();
        this.egfPin = p.getEgfPin();
        this.ffgLicence = p.getFfgLicence();
        this.ffgLicenceStatus = p.getFfgLicenceStatus();
        this.agaId = p.getAgaId();
        this.agaExpirationDate = p.getAgaExpirationDate();
        this.rank = p.getRank();
        this.rating = p.getRating();
        this.ratingOrigin = p.getRatingOrigin();
        this.strGrade = p.getStrGrade();
        this.smmsCorrection = p.getSmmsCorrection();
        boolean[] bPart = new boolean[p.getParticipating().length];
        System.arraycopy(p.getParticipating(), 0, bPart, 0, p.getParticipating().length);
        this.participating = bPart;
        this.registeringStatus = p.getRegisteringStatus();
    }

    public String getName() {
        return name;
    }
        
    public String getFirstName()  {
        return firstName;
    }

    public String fullName(){
        return name + " " + firstName;
    }

    public String fullUnblankedName(){
        return name.replaceAll(" ", "_") + " " + firstName.replaceAll(" ", "_");
    }
/** Concatenates name and firtName
     * Shortens if necessary
     * @param p
     * @return 
     */
    public String shortenedFullName(){
        String strName = getName();
        String strFirstName = getFirstName();
        if (strName.length() > 18) strName = strName.substring(0, 18);

        String strNF = strName + " " + strFirstName;
        if (strNF.length() > 22)  strNF = strNF.substring(0, 22);
        return strNF;
    }
    
    public String augmentedPlayerName(DPParameterSet dpps){
        String strNF = shortenedFullName();
            
//        String strRk = Player.convertIntToKD(this.getRank());
        String strGr = this.getStrGrade();
        String strCo = Gotha.leftString(this.getCountry(), 2);
        String strCl = Gotha.leftString(this.getClub(), 4);

//        boolean bRk = dpps.isShowPlayerRank();
        boolean bGr = dpps.isShowPlayerGrade();
        boolean bCo = dpps.isShowPlayerCountry();
        boolean bCl = dpps.isShowPlayerClub();
        
        if (!bGr && !bCo && !bCl) return strNF;
        String strPl = strNF + "(";
        boolean bFirst = true;
        if (bGr){
            strPl += strGr;
            bFirst = false;
        }
        if (bCo){
            if (!bFirst) strPl += ",";
            strPl += strCo;
            bFirst = false;
        }
        if (bCl){
            if (!bFirst) strPl += ",";
            strPl += strCl;
        }
        strPl += ")";
        
        return strPl;
    }
    
    public int getRank() {
        return rank;
    }
        
    public void setRank(int rank) {
        if (rank > Gotha.MAX_RANK) return;
        if (rank < Gotha.MIN_RANK) return;
        this.rank = rank;
    }
    
    private void computeKeyString(){
        this.keyString = (name + firstName).replaceAll(" ", "").toUpperCase();
    }
    
    public static String computeKeyString(String strNaFi){
        return strNaFi.replaceAll(" ", "").toUpperCase();
    }

    /**
     * Returns a key String for the player
     * fast and convenient for hash tables
     */
    public String getKeyString(){
        if (this.keyString == null) computeKeyString();
        return this.keyString;
   }
 
    /**
     * 2 players never have the same key string. 
     * hasSameKeyString is, thus a way to test if 2 references refer to the same player
     **/
    public boolean hasSameKeyString(Player p){
        if (p == null) return false;
        if (getKeyString().compareTo(p.getKeyString()) == 0) return true;
        else return false;
    }
    
    public int getRating() {
        return rating;
    }
        
    public void setRating(int val) {
        if (val > Player.MAX_RATING) val = MAX_RATING;
        if (val < Player.MIN_RATING) val = MIN_RATING;
        this.rating = val;
    }
    
    public String getRatingOrigin() {
        return ratingOrigin;
    }
    
    public void setRatingOrigin(String val) {
        this.ratingOrigin = val;
    }
    public String getStrRawRating() {
        int r = getRating();
        String strRR = "" + r;
        if (getRatingOrigin().equals("FFG")){
            strRR = "" + (r - 2050);
        }
        if (getRatingOrigin().equals("AGA")){
            r = r -2050;
            if (r >= 0) r = r + 100;
            if (r < 0) r = r - 100;
            
            // Generate a eeee.ff string
            int e = r /100;
            int f = Math.abs(r %100);
            String strF = ".00";
            if (f > 9) strF = "." + f;
            else strF = ".0" + f; 
            
            strRR = "" + e + strF;
        }
        return "" + strRR;
    }
    
    public String getClub()  {
        return club;
    }
    
    public void setClub(String val)  {
        this.club = val;
    }
       
    public boolean[] getParticipating() {
        return participating.clone();
    }

    public void setParticipating(boolean[] val) {
        this.participating = val.clone();
    }

    public boolean getParticipating(int roundNumber) {
        return participating[roundNumber];
    }

    public void setParticipating(int roundNumber, boolean val) {
        this.participating[roundNumber] = val;
    }
       
    public String getRegisteringStatus() {
        return registeringStatus;
    }
    
    public void setRegisteringStatus(String val) {
        this.registeringStatus = val;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String val) {
        this.country = val;
    }

   public int category(GeneralParameterSet gps){
       if (gps.getNumberOfCategories() <= 1) return 0;
       int[] cat = gps.getLowerCategoryLimits();
        for (int c = 0; c < cat.length; c++){
            if (rank >= cat[c]) return c;
        }
        return cat.length;
    }
    
    public int smms(GeneralParameterSet gps){
//        int smms = getRank() + 30;
//        int floor = gps.getGenMMFloor();
//        int bar = gps.getGenMMBar();
//        
//        if (smms < floor + 30) smms = floor + 30;
//        if (smms > bar + 30) smms = bar + 30;
        
        int zero = gps.getGenMMZero();
        int smms = getRank() - zero;
        int floor = gps.getGenMMFloor();
        int bar = gps.getGenMMBar();
        
        if (smms < floor - zero) smms = floor - zero;
        if (smms > bar - zero) smms = bar - zero;
        
        smms += smmsCorrection;
        
        return smms;
    }

    /**
     * Converts a String rank into an int rank 
     */
    public static int convertKDPToInt(String strKDP) {
        String strDraft = strKDP.trim();
        if (strDraft.length() < 1) strDraft = " ";
        char lastChar = strDraft.charAt(strDraft.length() - 1);
        int rank = -99;
        try{
            int numPart = new Integer(strDraft.substring(0, strDraft.length() -1)).intValue();
            if (lastChar == 'k' || lastChar == 'K') rank = - numPart;
            else if(lastChar == 'd' || lastChar == 'D') rank = numPart - 1;
            else if(lastChar == 'p' || lastChar == 'P'){
                if (numPart <= 3) rank = 6;
                else if (numPart <= 6) rank = 7;
                else rank = 8;
            }
            else rank = - numPart;
            if (rank < -30) rank = -30;
            if (rank > 8) rank = 8;
        }
        catch(NumberFormatException ex){
//            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            rank = -99;
        }
        return rank;
        
    }
    
    /**
     * Converts a IntScore to a String representing score /ratio
     */
    public static String convertIntScoreToString(int score, int ratio){
        int fract = score % ratio;
        int ent = score /ratio;
        String strFract = "";
        if (fract == 1 && ratio == 2) strFract = "½";
        if (fract == 1 && ratio == 4) strFract = "¼";
        if (fract == 3 && ratio == 4) strFract = "¾";
        String strEnt = "" + ent;
        if (score > 0 && score < ratio) strEnt = "";
        return strEnt + strFract;
    }
    
    /**
     * Converts an int rank into a String rank 
     */
    public static String convertIntToKD(int rank) {
        String strRank = "";
        
        if (rank >=0) strRank  = "" + (rank +1) + "D";
        if (rank < 0) strRank  = "" + (-rank)   + "K";
        return strRank;
    }
    
    /**
     * Converts rating to rank
     * rank = (rating + 1000)/100 - 30; 
     */
    public static int rankFromRating(int rating) {
        int rk = (rating + 950)/100 - 30;
        if (rk > 8 ) rk = 8;
        if (rk < -30) rk = -30;
        return rk;
    }
    
    /**
     * Converts rank to rating
     * rating = (rank + 30) *100 - 1000; 
     */
    public static int ratingFromRank(int rank) {
        return (rank + 30) * 100 - 900;     
    }

    /**
     * Generates a numberOfRounds characters String with '+' for participating 
     * and '-' for not participating
     */
    public static String convertParticipationToString(Player p, int numberOfRounds){
        boolean[] part = p.getParticipating();
        StringBuilder buf = new StringBuilder();
        for (int r = 0; r < numberOfRounds; r++){
            if (part[r]) buf.append("+");
            else buf.append("-");
        }
        return buf.toString();
    }
    
    /**
     * builds an Id String : EGF if exists, else FFG if exists, else AGA if exists, else ""
     * @return 
     */
    public String getAnIdString(){
        String egfP = this.getEgfPin();
        String ffgL = this.getFfgLicence();
        String agaI  = this.getAgaId();
        if (egfP != null && egfP.length() > 0) return "EGF Pin : " + egfP;
        else if (ffgL != null && ffgL.length() > 0) return "FFG Licence : " + ffgL;
        else if (agaI != null && agaI.length() > 0) return "AGA Id : " + agaI;
        return "";
    }
    
    public String getFfgLicence() {
        return ffgLicence;
    }

    public String getFfgLicenceStatus() {
        return ffgLicenceStatus;
    }

    public String getEgfPin() {
        return egfPin;
    }

    public void setEgfPin(String egfPin) {
        this.egfPin = egfPin;
    }
    
    public String getAgaId() {
        return agaId;
    }

    public String getAgaExpirationDate() {
        return agaExpirationDate;
    }

    public void setAgaId(String agaId) {
        this.agaId = agaId;
    }

    public int getSmmsCorrection() {
        return smmsCorrection;
    }

    public void setSmmsCorrection(int smmsCorrection) {
        this.smmsCorrection = smmsCorrection;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
        this.keyString = null;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.keyString = null;

    }

    /**
     * @return the strGrade
     */
    public String getStrGrade() {
        return strGrade;
    }

    /**
     * @param strGrade the strGrade to set
     */
    public void setStrGrade(String strGrade) {
        this.strGrade = strGrade;
    }
}

