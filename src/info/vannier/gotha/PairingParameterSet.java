package info.vannier.gotha;

public class PairingParameterSet implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    // Standard NX1 factor ( = Rather N X 1 than 1 X N)
    private double paiStandardNX1Factor = 0.5;
    /**
     * Max value for paiBaAvoidDuplGame.
     * <br> In order to be compatible with max value of long (8 * 10^18),
     * with max number of games (8000),
     * with relative weight of this parameter (1/2)
     * PAIBA_MAX_AVOIDDUPLGAME should be strictly limited to 5 * 10^14
     **/ 
    static final long PAIBA_MAX_AVOIDDUPLGAME = 500000000000000L;   // 5 * 10^14
    /**
     * Max value for paiBaRandom.
     * <br> Due to internal coding,
     * PAIBA_MAX_RANDOM should be strictly limited to 2 * 10^9
     **/
    static final long PAIBA_MAX_RANDOM        =       1000000000L;   // 10^9
    static final long PAIBA_MAX_BALANCEWB     =          1000000L;   // 10^6

    
    private long paiBaAvoidDuplGame = PAIBA_MAX_AVOIDDUPLGAME;    
    private long paiBaRandom = 0;
    private boolean paiBaDeterministic = true;
    private long paiBaBalanceWB = PAIBA_MAX_BALANCEWB;
    
    static final long PAIMA_MAX_AVOID_MIXING_CATEGORIES           =   20000000000000L;  // 2. 10^13
    // Ratio between PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE and PAIMA_MAX_AVOID_MIXING_CATEGORIES should stay below 1/ nbcat^2
    static final long PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE         =     100000000000L;  // 10^11
    static final long PAIMA_MAX_DUDD_WEIGHT                       =     PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE / 1000;  // Draw-ups Draw-downs
    static final int  PAIMA_DUDD_TOP              = 1;
    static final int  PAIMA_DUDD_MID              = 2;
    static final int  PAIMA_DUDD_BOT              = 3;
    
    static final long PAIMA_MAX_MAXIMIZE_SEEDING                  =         PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE / 20000;
    static final int  PAIMA_SEED_SPLITANDRANDOM   = 1;
    static final int  PAIMA_SEED_SPLITANDFOLD     = 2;
    static final int  PAIMA_SEED_SPLITANDSLIP     = 3;

    private long paiMaAvoidMixingCategories             = PAIMA_MAX_AVOID_MIXING_CATEGORIES;

    private long paiMaMinimizeScoreDifference           = PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE;

    private long paiMaDUDDWeight                        = PAIMA_MAX_DUDD_WEIGHT;
    private boolean paiMaCompensateDUDD                 = true;
    private int paiMaDUDDUpperMode                      = PAIMA_DUDD_MID;
    private int paiMaDUDDLowerMode                      = PAIMA_DUDD_MID;
    
    private long paiMaMaximizeSeeding                   =         PAIMA_MAX_MAXIMIZE_SEEDING;      // 5 *10^6
    private int paiMaLastRoundForSeedSystem1            = 1;
    private int paiMaSeedSystem1                        = PAIMA_SEED_SPLITANDRANDOM;                              
    private int paiMaSeedSystem2                        = PAIMA_SEED_SPLITANDFOLD; 
    private int paiMaAdditionalPlacementCritSystem1     = PlacementParameterSet.PLA_CRIT_RATING;
    private int paiMaAdditionalPlacementCritSystem2     = PlacementParameterSet.PLA_CRIT_NUL;
         
    private boolean paiSeBarThresholdActive                  = true;        // Do not apply secondary criteria for players above bar
    private int paiSeRankThreshold                           = 0;           // Do not apply secondary criteria above 1D rank
    private boolean paiSeNbWinsThresholdActive               = true;        // Do not apply secondary criteria when nbWins >= nbRounds / 2
    private long paiSeDefSecCrit;                            // Should be PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE for MM, PAIMA_MAX_AVOID_MIXING_CATEGORIES for others
    private long paiSeMinimizeHandicap;                      // Should be paiSeDefSecCrit for SwCat, 0 for others
    private long paiSeAvoidSameGeo;                          // Should be paiSeDefSecCrit for SwCat and MM, 0 for Swiss
    private int  paiSePreferMMSDiffRatherThanSameCountry;    // Typically = 1
    private int  paiSePreferMMSDiffRatherThanSameClubsGroup; // Typically = 2
    private int  paiSePreferMMSDiffRatherThanSameClub;       // Typically = 3
     
    public PairingParameterSet() {   
    }
    
    public PairingParameterSet(PairingParameterSet paiPS) {  
        paiBaAvoidDuplGame = paiPS.getPaiBaAvoidDuplGame();
        paiBaRandom = paiPS.getPaiBaRandom();
        paiBaDeterministic = paiPS.isPaiBaDeterministic();
        paiBaBalanceWB = paiPS.getPaiBaBalanceWB();
        paiMaMinimizeScoreDifference = paiPS.getPaiMaMinimizeScoreDifference();
        paiMaMaximizeSeeding = paiPS.getPaiMaMaximizeSeeding();
        paiMaLastRoundForSeedSystem1 = paiPS.getPaiMaLastRoundForSeedSystem1();
        paiMaSeedSystem1 = paiPS.getPaiMaSeedSystem1();
        paiMaSeedSystem2 = paiPS.getPaiMaSeedSystem2();
        paiMaAdditionalPlacementCritSystem1 = paiPS.getPaiMaAdditionalPlacementCritSystem1();
        paiMaAdditionalPlacementCritSystem2 = paiPS.getPaiMaAdditionalPlacementCritSystem2();
    }
    
    public long getPaiBaAvoidDuplGame() {
        return paiBaAvoidDuplGame;
    }

    public void setPaiBaAvoidDuplGame(long paiBaAvoidDuplGame) {
        this.paiBaAvoidDuplGame = paiBaAvoidDuplGame;
    }

    public long getPaiBaRandom() {
        return paiBaRandom;
    }

    public void setPaiBaRandom(long paiBaRandom) {
        this.paiBaRandom = paiBaRandom;
    }

    public boolean isPaiBaDeterministic() {
        return paiBaDeterministic;
    }

    public void setPaiBaDeterministic(boolean paiBaDeterministic) {
        this.paiBaDeterministic = paiBaDeterministic;
    }

    public long getPaiBaBalanceWB() {
        return paiBaBalanceWB;
    }

    public void setPaiBaBalanceWB(long paiBaBalanceWB) {
        this.paiBaBalanceWB = paiBaBalanceWB;
    }

    public long getPaiMaMinimizeScoreDifference() {
        return paiMaMinimizeScoreDifference;
    }

    public void setPaiMaMinimizeScoreDifference(long paiMaMinimizeScoreDifference) {
        this.paiMaMinimizeScoreDifference = paiMaMinimizeScoreDifference;
    }

    public long getPaiMaMaximizeSeeding() {
        return paiMaMaximizeSeeding;
    }

    public void setPaiMaMaximizeSeeding(long paiMaMaximizeSeeding) {
        this.paiMaMaximizeSeeding = paiMaMaximizeSeeding;
    }

    public int getPaiMaLastRoundForSeedSystem1() {
        return paiMaLastRoundForSeedSystem1;
    }

    public void setPaiMaLastRoundForSeedSystem1(int paiMaLastRoundForSeedSystem1) {
        this.paiMaLastRoundForSeedSystem1 = paiMaLastRoundForSeedSystem1;
    }

    public int getPaiMaSeedSystem1() {
        return paiMaSeedSystem1;
    }

    public void setPaiMaSeedSystem1(int paiMaSeedSystem1) {
        this.paiMaSeedSystem1 = paiMaSeedSystem1;
    }

    public int getPaiMaSeedSystem2() {
        return paiMaSeedSystem2;
    }

    public void setPaiMaSeedSystem2(int paiMaSeedSystem2) {
        this.paiMaSeedSystem2 = paiMaSeedSystem2;
    }

    public int getPaiMaAdditionalPlacementCritSystem1() {
        return paiMaAdditionalPlacementCritSystem1;
    }

    public void setPaiMaAdditionalPlacementCritSystem1(int paiMaAdditionalPlacementCritSystem1) {
        this.paiMaAdditionalPlacementCritSystem1 = paiMaAdditionalPlacementCritSystem1;
    }

    public int getPaiMaAdditionalPlacementCritSystem2() {
        return paiMaAdditionalPlacementCritSystem2;
    }

    public void setPaiMaAdditionalPlacementCritSystem2(int paiMaAdditionalPlacementCritSystem2) {
        this.paiMaAdditionalPlacementCritSystem2 = paiMaAdditionalPlacementCritSystem2;
    }

    public long getPaiMaAvoidMixingCategories() {
        return paiMaAvoidMixingCategories;
    }

    public void setPaiMaAvoidMixingCategories(long paiMaAvoidMixingCategories) {
        this.paiMaAvoidMixingCategories = paiMaAvoidMixingCategories;
    }

    public long getPaiMaDUDDWeight() {
        return paiMaDUDDWeight;
    }

    public void setPaiMaDUDDWeight(long paiMaDUDDWeight) {
        this.paiMaDUDDWeight = paiMaDUDDWeight;
    }

    public int getPaiMaDUDDUpperMode() {
        return paiMaDUDDUpperMode;
    }

    public void setPaiMaDUDDUpperMode(int paiMaDUDDUpperMode) {
        this.paiMaDUDDUpperMode = paiMaDUDDUpperMode;
    }

    public int getPaiMaDUDDLowerMode() {
        return paiMaDUDDLowerMode;
    }

    public void setPaiMaDUDDLowerMode(int paiMaDUDDLowerMode) {
        this.paiMaDUDDLowerMode = paiMaDUDDLowerMode;
    }

    public int getPaiSeRankThreshold() {
        return paiSeRankThreshold;
    }

    public void setPaiSeRankThreshold(int paiSeRankThreshold) {
        this.paiSeRankThreshold = paiSeRankThreshold;
    }

    public boolean isPaiSeNbWinsThresholdActive() {
        return paiSeNbWinsThresholdActive;
    }

    public void setPaiSeNbWinsThresholdActive(boolean paiSeNbWinsThresholdActive) {
        this.paiSeNbWinsThresholdActive = paiSeNbWinsThresholdActive;
    }

    public long getPaiSeMinimizeHandicap() {
        return paiSeMinimizeHandicap;
    }

    public void setPaiSeMinimizeHandicap(long paiSeMinimizeHandicap) {
        this.paiSeMinimizeHandicap = paiSeMinimizeHandicap;
    }

    public int getPaiSePreferMMSDiffRatherThanSameCountry() {
        return paiSePreferMMSDiffRatherThanSameCountry;
    }

    public void setPaiSePreferMMSDiffRatherThanSameCountry(int paiSePreferMMSDiffRatherThanSameCountry) {
        this.paiSePreferMMSDiffRatherThanSameCountry = paiSePreferMMSDiffRatherThanSameCountry;
    }

    public int getPaiSePreferMMSDiffRatherThanSameClubsGroup() {
        return paiSePreferMMSDiffRatherThanSameClubsGroup;
    }

    public void setPaiSePreferMMSDiffRatherThanSameClubsGroup(int paiSePreferMMSDiffRatherThanSameClubsGroup) {
        this.paiSePreferMMSDiffRatherThanSameClubsGroup = paiSePreferMMSDiffRatherThanSameClubsGroup;
    }
    
    public int getPaiSePreferMMSDiffRatherThanSameClub() {
        return paiSePreferMMSDiffRatherThanSameClub;
    }

    public void setPaiSePreferMMSDiffRatherThanSameClub(int paiSePreferMMSDiffRatherThanSameClub) {
        this.paiSePreferMMSDiffRatherThanSameClub = paiSePreferMMSDiffRatherThanSameClub;
    }

    public double getPaiStandardNX1Factor() {
        return paiStandardNX1Factor;
    }

    public void setPaiStandardNX1Factor(double paiStandardNX1Factor) {
        this.paiStandardNX1Factor = paiStandardNX1Factor;
    }

    public long getPaiSeAvoidSameGeo() {
        return paiSeAvoidSameGeo;
    }

    public void setPaiSeAvoidSameGeo(long paiSeAvoidSameGeo) {
        this.paiSeAvoidSameGeo = paiSeAvoidSameGeo;
    }

    public void initForMM() {
        paiBaAvoidDuplGame                      =   PAIBA_MAX_AVOIDDUPLGAME;    
        paiBaRandom                             =   0;
        paiBaDeterministic                      =   true;
        paiBaBalanceWB                          =   PAIBA_MAX_BALANCEWB;
    
        paiMaAvoidMixingCategories              =   0;         // Not relevant in McMahon

        paiMaMinimizeScoreDifference            =   PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE;

        paiMaDUDDWeight                         =   PAIMA_MAX_DUDD_WEIGHT;
            paiMaCompensateDUDD                 =   true;    
            paiMaDUDDUpperMode                  =   PAIMA_DUDD_MID;
            paiMaDUDDLowerMode                  =   PAIMA_DUDD_MID;
    
        paiMaMaximizeSeeding                    =   PAIMA_MAX_MAXIMIZE_SEEDING;      // 10^5
            paiMaLastRoundForSeedSystem1        =   1;
            paiMaSeedSystem1                    =   PAIMA_SEED_SPLITANDRANDOM;                              
            paiMaSeedSystem2                    =   PAIMA_SEED_SPLITANDFOLD; 
            paiMaAdditionalPlacementCritSystem1 =   PlacementParameterSet.PLA_CRIT_RATING;
            paiMaAdditionalPlacementCritSystem2 =   PlacementParameterSet.PLA_CRIT_NUL;
        
        paiSeBarThresholdActive                 =   true;         // Do not apply secondary criteria above bar
        paiSeRankThreshold                      =   0;           // Do not apply secondary criteria above 1D rank
        setPaiSeNbWinsThresholdActive(false);       // paiSeNbWinsThresholdActive not relevant in MM
        paiSeDefSecCrit                         =   PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE;
        paiSeMinimizeHandicap                   =   0;           // Not relevant in McMahon
        paiSeAvoidSameGeo                       =   paiMaMinimizeScoreDifference;
        paiSePreferMMSDiffRatherThanSameCountry =   1;
        paiSePreferMMSDiffRatherThanSameClubsGroup =2;
        paiSePreferMMSDiffRatherThanSameClub    =   3;
    }
    public void initForSwiss(){
        paiBaAvoidDuplGame                      =   PAIBA_MAX_AVOIDDUPLGAME;    
        paiBaRandom                             =   0;
        paiBaDeterministic                      =   true;
        paiBaBalanceWB                          =   PAIBA_MAX_BALANCEWB;
    
        paiMaAvoidMixingCategories              =   0;          // Not relevant         

        paiMaMinimizeScoreDifference            =   PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE;

        paiMaDUDDWeight                         =   PAIMA_MAX_DUDD_WEIGHT;
            paiMaCompensateDUDD                 =   true;
            paiMaDUDDUpperMode                  =   PAIMA_DUDD_MID;
            paiMaDUDDLowerMode                  =   PAIMA_DUDD_MID;
    
        paiMaMaximizeSeeding                    =   PAIMA_MAX_MAXIMIZE_SEEDING;      // 10^5
            paiMaLastRoundForSeedSystem1        =   1;
            paiMaSeedSystem1                    =   PAIMA_SEED_SPLITANDSLIP;                              
            paiMaSeedSystem2                    =   PAIMA_SEED_SPLITANDSLIP; 
            paiMaAdditionalPlacementCritSystem1 =   PlacementParameterSet.PLA_CRIT_RATING;
            paiMaAdditionalPlacementCritSystem2 =   PlacementParameterSet.PLA_CRIT_RATING;
        
        paiSeBarThresholdActive                 =   true;           // Not relevant
        paiSeRankThreshold                      =   -30;            // Do not apply secondary criteria above rank 
        setPaiSeNbWinsThresholdActive(true);           // Not Relevant
        paiSeDefSecCrit                         =   PAIMA_MAX_AVOID_MIXING_CATEGORIES;
        paiSeMinimizeHandicap                   =   0;     
        paiSeAvoidSameGeo                       =   0;
        paiSePreferMMSDiffRatherThanSameCountry =   0;              // Not Relevant
        paiSePreferMMSDiffRatherThanSameClubsGroup =0;              // Not Relevant
        paiSePreferMMSDiffRatherThanSameClub    =   0;              // Not Relevant
    }
    public void initForSwissCat() {
        paiBaAvoidDuplGame                      =   PAIBA_MAX_AVOIDDUPLGAME;    
        paiBaRandom                             =   0;
        paiBaDeterministic                      =   true;
        paiBaBalanceWB                          =   PAIBA_MAX_BALANCEWB  ;
    
        paiMaAvoidMixingCategories              =   PAIMA_MAX_AVOID_MIXING_CATEGORIES;         

        paiMaMinimizeScoreDifference            =   PAIMA_MAX_MINIMIZE_SCORE_DIFFERENCE;

        paiMaDUDDWeight                         =   PAIMA_MAX_DUDD_WEIGHT;
            paiMaCompensateDUDD                 =   true;    
            paiMaDUDDUpperMode                  =   PAIMA_DUDD_MID;
            paiMaDUDDLowerMode                  =   PAIMA_DUDD_MID;
            
        paiMaMaximizeSeeding                    =   PAIMA_MAX_MAXIMIZE_SEEDING;      // 10^5
            paiMaLastRoundForSeedSystem1        =   1;
            paiMaSeedSystem1                    =   PAIMA_SEED_SPLITANDRANDOM;                              
            paiMaSeedSystem2                    =   PAIMA_SEED_SPLITANDFOLD; 
            paiMaAdditionalPlacementCritSystem1 =   PlacementParameterSet.PLA_CRIT_RATING;
            paiMaAdditionalPlacementCritSystem2 =   PlacementParameterSet.PLA_CRIT_NUL;
        
        paiSeBarThresholdActive                 =   true;           // Not relevant
        paiSeRankThreshold                      =   0;           // Do not apply secondary criteria above 1D rank
        setPaiSeNbWinsThresholdActive(true);        // Do not apply secondary criteria when nbWins >= nbRounds / 2
        paiSeDefSecCrit                         =   PAIMA_MAX_AVOID_MIXING_CATEGORIES;
        paiSeMinimizeHandicap                   =   paiSeDefSecCrit;     
        paiSeAvoidSameGeo                       =   paiSeDefSecCrit;
        paiSePreferMMSDiffRatherThanSameCountry =   1;
        paiSePreferMMSDiffRatherThanSameClubsGroup =2;
        paiSePreferMMSDiffRatherThanSameClub    =   3;
    }    

    public long getPaiSeDefSecCrit() {
        return paiSeDefSecCrit;
    }

    public void setPaiSeDefSecCrit(long paiSeDefSecCrit) {
        this.paiSeDefSecCrit = paiSeDefSecCrit;
    }

    /**
     * @return the paiSeBarThresholdActive
     */
    public boolean isPaiSeBarThresholdActive() {
        return paiSeBarThresholdActive;
    }

    /**
     * @param paiSeBarThresholdActive the paiSeBarThresholdActive to set
     */
    public void setPaiSeBarThresholdActive(boolean paiSeBarThresholdActive) {
        this.paiSeBarThresholdActive = paiSeBarThresholdActive;
    }

    /**
     * @return the paiMaCompensateDUDD
     */
    public boolean isPaiMaCompensateDUDD() {
        return paiMaCompensateDUDD;
    }

    /**
     * @param paiMaCompensateDUDD the paiMaCompensateDUDD to set
     */
    public void setPaiMaCompensateDUDD(boolean paiMaCompensateDUDD) {
        this.paiMaCompensateDUDD = paiMaCompensateDUDD;
    }
}
