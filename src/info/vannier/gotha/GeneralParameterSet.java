/*
 * GeneralParameterSet.java
 *
 */

package info.vannier.gotha;

import java.util.Date;

/**
 *
 * @author Luc Vannier
 */
public class GeneralParameterSet implements java.io.Serializable{
//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
   
    final static int GEN_MM_FLOOR_MIN   = -30;  // 30K
    final static int GEN_MM_FLOOR_MAX   =  0;   // 1D
    final static int GEN_MM_BAR_MIN     = -10;  // 10K
    final static int GEN_MM_BAR_MAX     =  8;   // 9D
    final static int GEN_MM_ZERO_MIN     = -30;  // 30K
    final static int GEN_MM_ZERO_MAX     =  0;   // 1D
  
    /**  For instance : paris2009 */
    private String shortName = "Undefined";
    /**  For instance : Toyota Tour Paris Tournament 2009 */
    private String fullName = "Undefined";
    private String location = "";
    private String director = "";
    private java.util.Date beginDate = new java.util.Date ();
    private java.util.Date endDate  = new java.util.Date ();
    
    // Games parameters
   
    private String strSize = "19";
    private String strKomi = "7.5";

    // Basic time
    static final int GEN_GP_BASICTIME_MIN = 5;
    static final int GEN_GP_BASICTIME_MAX = 180;
    static final int GEN_GP_BASICTIME_DEF = 60;
    private int basicTime = 60;
    
    // Internet
    private boolean bInternet = false;
    
    // Complementary Time system
    static final int  GEN_GP_CTS_SUDDENDEATH   = 1;
    static final int  GEN_GP_CTS_STDBYOYOMI    = 2;
    static final int  GEN_GP_CTS_CANBYOYOMI    = 3;
    static final int  GEN_GP_CTS_FISCHER       = 4;
    private int complementaryTimeSystem = GEN_GP_CTS_CANBYOYOMI;
    
    static final int GEN_GP_CTS_STDBYOYOMITIME_MIN = 5;
    static final int GEN_GP_CTS_STDBYOYOMITIME_MAX = 120;
    static final int GEN_GP_CTS_STDBYOYOMITIME_DEF = 30;
    
    static final int GEN_GP_CTS_NBMOVESCANTIME_MIN = 5;
    static final int GEN_GP_CTS_NBMOVESCANTIME_MAX = 25;
    static final int GEN_GP_CTS_NBMOVESCANTIME_DEF = 15;
    
    static final int GEN_GP_CTS_CANBYOYOMITIME_MIN = 300;
    static final int GEN_GP_CTS_CANBYOYOMITIME_MAX = 900;
    static final int GEN_GP_CTS_CANBYOYOMITIME_DEF = 300;
    
    static final int GEN_GP_CTS_FISCHERTIME_MIN = 1;
    static final int GEN_GP_CTS_FISCHERTIME_MAX = 60;
    static final int GEN_GP_CTS_FISCHERTIME_DEF = 10;
    
    private int stdByoYomiTime = GEN_GP_CTS_STDBYOYOMITIME_DEF;
    private int nbMovesCanTime = GEN_GP_CTS_NBMOVESCANTIME_DEF;
    private int canByoYomiTime = GEN_GP_CTS_CANBYOYOMITIME_DEF;
    private int fischerTime = GEN_GP_CTS_FISCHERTIME_DEF;
    
    private int numberOfRounds = 5;
    private int numberOfCategories = 1;
    private int[] lowerCategoryLimits; // limits are defined in rank units (between +8 and -30)
    private int genMMFloor;
    private int genMMBar;
    private int genMMZero;  // Defines the rank as zero in MMS computations

    private int genNBW2ValueAbsent    = 0;  // 2 * Number of NBW points for a player absent of a round
    private int genNBW2ValueBye = 2;        // 2 * Number of NBW points for a player not paired in a round (uneven)

    private int genMMS2ValueAbsent = 1;     // 2 * Number of MMS points for a player absent of a round
    private int genMMS2ValueBye = 2;        // 2 * Number of MMS points for a player not paired in a round   
    
    private boolean genRoundDownNBWMMS = true;
    private boolean genCountNotPlayedGamesAsHalfPoint = false;
    
    private int genCPS2ValueAbsent    = 0;  // 2 * Number of NBW points for a player absent of a round
    private int genCPS2ValueBye = 0;        // 2 * Number of NBW points for a player not paired in a round (uneven)

    public GeneralParameterSet() {
    }
    public GeneralParameterSet(GeneralParameterSet gps) {
        this.shortName = gps.getShortName();
        this.fullName = gps.getName();
        this.location = gps.getLocation();
        this.beginDate = gps.getBeginDate();
        this.endDate = gps.getEndDate();

        this.basicTime = gps.getBasicTime();

        this.complementaryTimeSystem = gps.getComplementaryTimeSystem();
        this.stdByoYomiTime = gps.getStdByoYomiTime();
        this.nbMovesCanTime = gps.getNbMovesCanTime();
        this.canByoYomiTime = gps.getCanByoYomiTime();
        this.fischerTime = gps.getFischerTime();
        
        this.strSize = gps.getStrSize();
        this.strKomi = gps.getStrKomi();
        
        this.numberOfRounds = gps.getNumberOfRounds();
        this.numberOfCategories = gps.getNumberOfCategories();
        int[] llc = gps.getLowerCategoryLimits();
        int[] llc2 = null;
        if (llc != null){
            llc2 = new int[llc.length];
            System.arraycopy(llc, 0, llc2, 0, llc.length);
            this.lowerCategoryLimits = llc2;
        }
        this.genMMFloor = gps.getGenMMFloor();
        this.genMMBar = gps.getGenMMBar();
//        this.genMMZero = gps.getGenMMZero();
        this.genMMZero = -30;
        this.genNBW2ValueAbsent = gps.getGenNBW2ValueAbsent();
        this.genNBW2ValueBye = gps.getGenNBW2ValueBye();
        this.genMMS2ValueAbsent = gps.getGenMMS2ValueAbsent();
        this.genMMS2ValueBye = gps.getGenMMS2ValueBye();
        this.genRoundDownNBWMMS = gps.isGenRoundDownNBWMMS();
        this.genCountNotPlayedGamesAsHalfPoint = gps.isGenCountNotPlayedGamesAsHalfPoint();
    }

    public void initBase(String shortName, String name, String location, String director,
            java.util.Date beginDate, java.util.Date endDate, int numberOfRounds,  int numberOfCategories) {
        this.shortName = shortName;
        this.fullName = name;
        this.location = location;
        this.director = director;
        this.beginDate = (Date)beginDate.clone();
        this.endDate = (Date)endDate.clone();
        this.numberOfRounds = numberOfRounds;
        this.numberOfCategories = numberOfCategories;
        
        this.basicTime = GeneralParameterSet.GEN_GP_BASICTIME_DEF;
        this.complementaryTimeSystem = GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI;
        this.stdByoYomiTime = GeneralParameterSet.GEN_GP_CTS_STDBYOYOMITIME_DEF;
        this.nbMovesCanTime = GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_DEF;
        this.canByoYomiTime = GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_DEF;
        this.fischerTime = GeneralParameterSet.GEN_GP_CTS_FISCHERTIME_DEF;
        
    }

    public void initForMM(){
        setNumberOfCategories(1);
        genMMFloor              = -20;      // 20K
        genMMBar                = 3;        // 4D    
        genMMZero               = -30;      //30K
        
        genNBW2ValueAbsent      = 0;        
        genNBW2ValueBye         = 2;       

        genMMS2ValueAbsent      = 1;       
        genMMS2ValueBye         = 2; 

        genCPS2ValueAbsent      = 0;    // Not relevant
        genCPS2ValueBye         = 0;    // Not relevant
        
        this.genRoundDownNBWMMS = true;
        this.genCountNotPlayedGamesAsHalfPoint = false;
    }
    
    public void initForSwiss(){
        setNumberOfCategories(1);
        genMMFloor              = -30;    // Not relevant
        genMMBar                = 8;      // Not relevant
        genMMZero               = -30;    // Not relevant

        genNBW2ValueAbsent      = 0;    // 2 * Number of NBW points for a player absent of a round
        genNBW2ValueBye         = 2;    // 2 * Number of NBW points for a player not paired in a round (uneven)

        genMMS2ValueAbsent      = 1;    // Not relevant
        genMMS2ValueBye         = 2;    // Not relevant     
        
        genCPS2ValueAbsent      = 0;    // Not relevant
        genCPS2ValueBye         = 0;    // Not relevant

        this.genRoundDownNBWMMS = true;
        this.genCountNotPlayedGamesAsHalfPoint = false;
    }
    
    public void initForSwissCat(){
        setNumberOfCategories(3);
        lowerCategoryLimits[0]  = 0;
        lowerCategoryLimits[1]  = -5;
        genMMFloor              = -30;    // Relevant only for pseudo MMS in sec criteria pairing
        genMMBar                = 8;    // Relevant only for pseudo MMS in sec criteria pairing
        genMMZero               = -30;  // Not relevant
        
        genNBW2ValueAbsent      = 0;    // 2 * Number of NBW points for a player absent of a round
        genNBW2ValueBye         = 2;    // 2 * Number of NBW points for a player not paired in a round (uneven)

        genMMS2ValueAbsent      = 1;    // Not relevant
        genMMS2ValueBye         = 2;    // Not relevant  
        
        genCPS2ValueAbsent      = 0;    // Not relevant
        genCPS2ValueBye         = 0;    // Not relevant

        this.genRoundDownNBWMMS = true;
        this.genCountNotPlayedGamesAsHalfPoint = false;
    }
    
        public void initForCup(){
        setNumberOfCategories(1);
        genMMFloor              = -30;    // Not relevant
        genMMBar                = 8;      // Not relevant
        genMMZero               = -30;    // Not relevant

        genNBW2ValueAbsent      = 0;    // Not relevant
        genNBW2ValueBye         = 0;    // Not relevant

        genMMS2ValueAbsent      = 0;    // Not relevant
        genMMS2ValueBye         = 0;    // Not relevant  
        
        genCPS2ValueAbsent      = 0;    
        genCPS2ValueBye         = 0;    

        this.genRoundDownNBWMMS = true;
        this.genCountNotPlayedGamesAsHalfPoint = false;
    }

        
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        // Eliminate all characters after "."
//        int pos = shortName.indexOf(".");
//        if (pos >= 0) shortName = shortName.substring(0, pos);
//        if(shortName.length() < 1) shortName = "TournamentShortName";
        this.shortName = shortName;
    }

    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public java.util.Date getBeginDate() {
        return (Date) beginDate.clone();
    }

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = (Date)beginDate.clone();
    }

    public java.util.Date getEndDate() {
        return (Date)endDate.clone();
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = (Date)endDate.clone();
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public int getNumberOfCategories() {
        return numberOfCategories;
    }

    public void setNumberOfCategories(int numberOfCategories) {
        this.numberOfCategories = numberOfCategories;

        if (numberOfCategories <= 0){
            numberOfCategories = 1;
        }

        // Ensure coherence between numberOfCategories and lowerCategoryLimits
        if (numberOfCategories == 1){
            lowerCategoryLimits = null;
            return;
        }

        if (this.lowerCategoryLimits == null) lowerCategoryLimits = new int[numberOfCategories - 1];

        if ( (numberOfCategories - 1) < lowerCategoryLimits.length){
            int[] lcl = new int[numberOfCategories - 1];
            System.arraycopy(lowerCategoryLimits, 0, lcl, 0, numberOfCategories - 1);
            setLowerCategoryLimits(lcl);
            return;
        }
        if ( (numberOfCategories - 1) > lowerCategoryLimits.length){
            int[] lcl = new int[numberOfCategories - 1];
            System.arraycopy(lowerCategoryLimits, 0, lcl, 0, lowerCategoryLimits.length);
            for (int c = lowerCategoryLimits.length; c < numberOfCategories - 1; c++){
                lcl[c] = Gotha.MIN_RANK;
            }
            setLowerCategoryLimits(lcl);
        }
    }

    public int[] getLowerCategoryLimits() {
        if (lowerCategoryLimits == null) return null;
        int[] lcl = new int[lowerCategoryLimits.length];
        System.arraycopy(lowerCategoryLimits, 0, lcl, 0, lowerCategoryLimits.length);
        return lcl;
    }

    public final void setLowerCategoryLimits(int[] lowerCategoryLimits) {
        if (lowerCategoryLimits == null){
            this.lowerCategoryLimits = null;
            return;
        }
        this.lowerCategoryLimits = new int[lowerCategoryLimits.length];
        System.arraycopy(lowerCategoryLimits, 0, this.lowerCategoryLimits, 0, lowerCategoryLimits.length);
    }

    public int getGenMMBar() {
        return genMMBar;
    }

    public void setGenMMBar(int genMMBar) {
        this.genMMBar = genMMBar;
    }

    public int getGenMMFloor() {
        return genMMFloor;
    }

    public void setGenMMFloor(int genMMFloor) {
        this.genMMFloor = genMMFloor;
    }
    
    public int getGenMMZero() {
        return genMMZero;
    }

    public void setGenMMZero(int genMMZero) {
//        this.genMMZero = genMMZero;
        this.genMMZero = -30;
    }

    public int getGenNBW2ValueAbsent() {
        return genNBW2ValueAbsent;
    }

    public void setGenNBW2ValueAbsent(int genNBW2ValueAbsent) {
        this.genNBW2ValueAbsent = genNBW2ValueAbsent;
    }

    public int getGenNBW2ValueBye() {
        return genNBW2ValueBye;
    }

    public void setGenNBW2ValueBye(int genNBW2ValueBye) {
        this.genNBW2ValueBye = genNBW2ValueBye;
    }

    public int getGenMMS2ValueAbsent() {
        return genMMS2ValueAbsent;
    }

    public void setGenMMS2ValueAbsent(int genMMS2ValueAbsent) {
        this.genMMS2ValueAbsent = genMMS2ValueAbsent;
    }

    public int getGenMMS2ValueBye() {
        return genMMS2ValueBye;
    }

    public void setGenMMS2ValueBye(int genMMS2ValueBye) {
        this.genMMS2ValueBye = genMMS2ValueBye;
    }

    public String getStrSize() {
        return strSize;
    }

    public void setStrSize(String strSize) {
        this.strSize = strSize;
    }

    public String getStrKomi() {
        return strKomi;
    }

    public void setStrKomi(String strKomi) {
        this.strKomi = strKomi;
    }

    /**
     * @return the complementaryTimeSystem
     */
    public int getComplementaryTimeSystem() {
        return complementaryTimeSystem;
    }

    /**
     * @param complementaryTimeSystem the complementaryTimeSystem to set
     */
    public void setComplementaryTimeSystem(int complementaryTimeSystem) {
        this.complementaryTimeSystem = complementaryTimeSystem;
    }

    /**
     * @return the stdByoYomiTime
     */
    public int getStdByoYomiTime() {
        return stdByoYomiTime;
    }

    /**
     * @param val the stdByoYomiTime to set
     */
    public void setStdByoYomiTime(int val) {
        val = Math.max(val, GeneralParameterSet.GEN_GP_CTS_STDBYOYOMITIME_MIN);
        val = Math.min(val, GeneralParameterSet.GEN_GP_CTS_STDBYOYOMITIME_MAX);
        this.stdByoYomiTime = val;
    }

    /**
     * @return the nbMovesCanTime
     */
    public int getNbMovesCanTime() {
        return nbMovesCanTime;
    }

    /**
     * @param val the nbMovesCanTime to set
     */
    public void setNbMovesCanTime(int val) {
        val = Math.max(val, GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_MIN);
        val = Math.min(val, GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_MAX);
        this.nbMovesCanTime = val;
    }

    /**
     * @return the canByoYomiTime
     */
    public int getCanByoYomiTime() {
        return canByoYomiTime;
    }

    /**
     * @param val the canByoYomiTime to set
     */
    public void setCanByoYomiTime(int val) {
        val = Math.max(val, GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_MIN);
        val = Math.min(val, GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_MAX);
        this.canByoYomiTime = val;
    }

    /**
     * @return the bonusTime
     */
    public int getFischerTime() {
        return fischerTime;
    }

    /**
     * @param val the fischerTime to set
     */
    public void setFischerTime(int val) {
        val = Math.max(val, GeneralParameterSet.GEN_GP_CTS_FISCHERTIME_MIN);
        val = Math.min(val, GeneralParameterSet.GEN_GP_CTS_FISCHERTIME_MAX);
        this.fischerTime = val;
    }

    /**
     * @return the basicTime
     */
    public int getBasicTime() {
        return basicTime;
    }

    /**
     * @param val the basicTime to set
     */
    public void setBasicTime(int val) {
        val = Math.max(val, GeneralParameterSet.GEN_GP_BASICTIME_MIN);
        val = Math.min(val, GeneralParameterSet.GEN_GP_BASICTIME_MAX);
        this.basicTime = val;
    }

    /**
     * @return the bInternet
     */
    public boolean isBInternet() {
        return bInternet;
    }

    /**
     * @param val the bInternet to set
     */
    public void setBInternet(boolean val) {
        this.bInternet = val;
    }

    
    /**
     * @return the roundDownNBWMMS
     */
    public boolean isGenRoundDownNBWMMS() {
        return genRoundDownNBWMMS;
    }

    /**
     * @param roundDownNBWMMS the roundDownNBWMMS to set
     */
    public void setGenRoundDownNBWMMS(boolean roundDownNBWMMS) {
        this.genRoundDownNBWMMS = roundDownNBWMMS;
    }

    /**
     * @return the countNotPlayedGamesAsHalfPoint
     */
    public boolean isGenCountNotPlayedGamesAsHalfPoint() {
        return genCountNotPlayedGamesAsHalfPoint;
    }

    /**
     * @param countNotPlayedGamesAsHalfPoint 
     */
    public void setGenCountNotPlayedGamesAsHalfPoint(boolean countNotPlayedGamesAsHalfPoint) {
        this.genCountNotPlayedGamesAsHalfPoint = countNotPlayedGamesAsHalfPoint;
    }
}
