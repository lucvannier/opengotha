
package info.vannier.gotha;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * ScoredPlayer represents a player and all useful scoring information (nbw, mms, ... dc, sdc)
 *
 * All datas except dc and sdc are updated by (and only by) fillBaseScoringInfo(), according to gps as defined in current tournament
 * dc and sdc are updated by (and only by) fillDirScoringInfo(), according to pps and round number  as defined in argument.
 * 
 * ScoredPlayer does not contain any information about pairing
 * @author Luc
 */
public class ScoredPlayer extends Player implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;

    public final static int UNKNOWN = 0;
    public final static int ABSENT = -3;
    public final static int NOT_ASSIGNED = -2;
    public final static int BYE = -1;
    public final static int PAIRED = 1;

    /** generalParameterSet is a part of ScoredPlayer because mms is dependent on McMahon bars and floors */
    private GeneralParameterSet generalParameterSet;

    /** for each round, participation[r] can be : ABSENT, NOT_ASSIGNED, BYE or PAIRED */
    private int[] participation;
    /** games played by this player */
    private Game[] gameArray;


    // First level scores
    private int[] nbwX2;       // number of wins * 2
    private int[] mmsX2;       // mcmahon score * 2
    private int[] stsX2;       // strasbourg score *2

    // Second level scores
    private int[] cuswX2;      // Sum of successive nbw2
    private int[] cusmX2;      // Sum of successive mms2
    private int[] soswX2;      // Sum of Opponents nbw2
    private int[] soswM1X2;    // Sum of (n-1) Opponents nbw2
    private int[] soswM2X2;    // Sum of (n-2) Opponents nbw2
    private int[] sdswX4;      // Sum of Defeated Opponents nbw2 X2
    private int[] sosmX2;      // Sum of Opponents mms2
    private int[] sosmM1X2;    // Sum of (n-1) Opponents mms2
    private int[] sosmM2X2;    // Sum of (n-2) Opponents mms2
    private int[] sdsmX4;      // Sum of Defeated Opponents mms2 X2
    private int[] sostsX2;     // Sum of Opponents sts2

    private int[] extX2;      // Exploits tentes (based on nbw2, with a weight factor)
    private int[] exrX2;      // Exploits reussis(based on nbw2, with a weight factor)
    // Third level scores
    private int[] ssswX2;      // Sum of opponents sosw2 * 2
    private int[] sssmX2;      // Sum of opponents sosm2 * 2
    // Special Scores

    private int dc;            // Direct Confrontation
    private int sdc;           // Simplified Direct Confrontation

    // Pairing informations. Unlike preceeding data, these informations are computed for one round only : the current one
    public int numberOfGroups;      // Very redundant
    public int groupNumber;         //
    public int groupSize;           // Redundant
    public int innerPlacement;      // placement in homogeneous group (category and mainScore) beteen 0 and size(group) - 1
    public int nbDU;                // Number of Draw-ups
    public int nbDD;                // Number of Draw-downs

    private boolean isValidRoundNumber(int rn){
        if (rn < 0 || rn > participation.length){
            return false;
        }
        else return true;
    }
    public int getParticipation(int rn){
        if (isValidRoundNumber(rn)) return participation[rn];
        else return 0;
    }
    public void setParticipation(int rn, int participation){
        if (isValidRoundNumber(rn)) this.participation[rn] = participation;
        else this.participation[rn] = ScoredPlayer.UNKNOWN;
    }
    public Game getGame(int rn){
        if (isValidRoundNumber(rn)) return gameArray[rn];
        else return null;
    }
    public void setGame(int rn, Game g){
        if (isValidRoundNumber(rn)) gameArray[rn] = g;
    }
    public int getNBWX2(int rn){
        if (isValidRoundNumber(rn)) return nbwX2[rn];
        else return 0;
    }
    public void setNBWX2(int rn, int value){
        if (isValidRoundNumber(rn)) nbwX2[rn] = value;
    }
    public int getMMSX2(int rn){
        if (isValidRoundNumber(rn)) return mmsX2[rn];
        else return 0;
    }
    public void setMMSX2(int rn, int value){
        if (isValidRoundNumber(rn)) mmsX2[rn] = value;
    }
    public int getSTSX2(int rn){
        if (isValidRoundNumber(rn)) return stsX2[rn];
        else return 0;
    }
    public void setSTSX2(int rn, int value){
        if (isValidRoundNumber(rn)) stsX2[rn] = value;
    }
    public int getCUSWX2(int rn){
        if (isValidRoundNumber(rn)) return cuswX2[rn];
        else return 0;
    }
    public void setCUSWX2(int rn, int value){
        if (isValidRoundNumber(rn)) cuswX2[rn] = value;
    }
    public int getCUSMX2(int rn){
        if (isValidRoundNumber(rn)) return cusmX2[rn];
        else return 0;
    }
    public void setCUSMX2(int rn, int value){
        if (isValidRoundNumber(rn)) cusmX2[rn] = value;
    }
    public int getSOSWX2(int rn){
        if (isValidRoundNumber(rn)) return soswX2[rn];
        else return 0;
    }
    public void setSOSWX2(int rn, int value){
        if (isValidRoundNumber(rn)) soswX2[rn] = value;
    }
    public int getSOSWM1X2(int rn){
        if (isValidRoundNumber(rn)) return soswM1X2[rn];
        else return 0;
    }
    public void setSOSWM1X2(int rn, int value){
        if (isValidRoundNumber(rn)) soswM1X2[rn] = value;
    }
    public int getSOSWM2X2(int rn){
        if (isValidRoundNumber(rn)) return soswM2X2[rn];
        else return 0;
    }
    public void setSOSWM2X2(int rn, int value){
        if (isValidRoundNumber(rn)) soswM2X2[rn] = value;
    }
    public int getSDSWX4(int rn){
        if (isValidRoundNumber(rn)) return sdswX4[rn];
        else return 0;
    }
    public void setSDSWX4(int rn, int value){
        if (isValidRoundNumber(rn)) sdswX4[rn] = value;
    }
    public int getSOSMX2(int rn){
        if (isValidRoundNumber(rn)) return sosmX2[rn];
        else return 0;
    }
    public void setSOSMX2(int rn, int value){
        if (isValidRoundNumber(rn)) sosmX2[rn] = value;
    }
    public int getSOSMM1X2(int rn){
        if (isValidRoundNumber(rn)) return sosmM1X2[rn];
        else return 0;
    }
    public void setSOSMM1X2(int rn, int value){
        if (isValidRoundNumber(rn)) sosmM1X2[rn] = value;
    }
    public int getSOSMM2X2(int rn){
        if (isValidRoundNumber(rn)) return sosmM2X2[rn];
        else return 0;
    }
    public void setSOSMM2X2(int rn, int value){
        if (isValidRoundNumber(rn)) sosmM2X2[rn] = value;
    }
    public int getSDSMX4(int rn){
        if (isValidRoundNumber(rn)) return sdsmX4[rn];
        else return 0;
    }
    public void setSDSMX4(int rn, int value){
        if (isValidRoundNumber(rn)) sdsmX4[rn] = value;
    }
    public int getSOSTSX2(int rn){
        if (isValidRoundNumber(rn)) return sostsX2[rn];
        else return 0;
    }
    public void setSOSTSX2(int rn, int value){
        if (isValidRoundNumber(rn)) sostsX2[rn] = value;
    }
    public int getEXTX2(int rn){
        if (isValidRoundNumber(rn)) return extX2[rn];
        else return 0;
    }
    public void setEXTX2(int rn, int value){
        if (isValidRoundNumber(rn)) extX2[rn] = value;
    }
    public int getEXRX2(int rn){
        if (isValidRoundNumber(rn)) return exrX2[rn];
        else return 0;
    }
    public void setEXRX2(int rn, int value){
        if (isValidRoundNumber(rn)) exrX2[rn] = value;
    }
    public int getSSSWX2(int rn){
        if (isValidRoundNumber(rn)) return ssswX2[rn];
        else return 0;
    }
    public void setSSSWX2(int rn, int value){
        if (isValidRoundNumber(rn)) ssswX2[rn] = value;
    }
    public int getSSSMX2(int rn){
        if (isValidRoundNumber(rn)) return sssmX2[rn];
        else return 0;
    }
    public void setSSSMX2(int rn, int value){
        if (isValidRoundNumber(rn)) sssmX2[rn] = value;
    }
    public int getDC(){
        return dc;
    }
    public void setDC(int value){
        dc = value;
    }
    public int getSDC(){
        return sdc;
    }
    public void setSDC(int value){
        sdc = value;
    }

    public ScoredPlayer(){
    }

    public ScoredPlayer(GeneralParameterSet gps, Player player) {
        super(player);
        this.generalParameterSet = gps;

        int numberOfRounds = generalParameterSet.getNumberOfRounds();
        participation = new int[numberOfRounds];
        gameArray = new Game[numberOfRounds];
        nbwX2  = new int[numberOfRounds];
        mmsX2  = new int[numberOfRounds];
        stsX2  = new int[numberOfRounds];
        
        cuswX2 = new int[numberOfRounds];
        cusmX2 = new int[numberOfRounds];

        soswX2 = new int[numberOfRounds];
        soswM1X2 = new int[numberOfRounds];
        soswM2X2 = new int[numberOfRounds];

        sdswX4 = new int[numberOfRounds];

        sosmX2 = new int[numberOfRounds];
        sosmM1X2 = new int[numberOfRounds];
        sosmM2X2 = new int[numberOfRounds];

        sdsmX4 = new int[numberOfRounds];
       
        sostsX2 = new int[numberOfRounds];

        extX2  = new int[numberOfRounds];
        exrX2  = new int[numberOfRounds];

        ssswX2 = new int[numberOfRounds];
        sssmX2 = new int[numberOfRounds];

        for (int r = 0; r < numberOfRounds; r++){
            participation[r] = 0;
            gameArray[r] = null;
            nbwX2[r] = 0;
            mmsX2[r] = 0;
            stsX2[r] = 0;
            
            cuswX2[r] = 0;
            cusmX2[r] = 0;

            soswX2[r] = 0;
            soswM1X2[r] = 0;
            soswM2X2[r] = 0;

            sosmX2[r] = 0;
            sosmM1X2[r] = 0;
            sosmM2X2[r] = 0;
            
            sostsX2[r] = 0;

            extX2[r] = 0;
            exrX2[r] = 0;

            ssswX2[r] = 0;
            sssmX2[r] = 0;
        }
        // dc and sdc are defined for the current round number
        dc = 0;
        sdc = 0;
    }

    public int getCritValue(int criterion, int rn){
        switch(criterion){
            case PlacementParameterSet.PLA_CRIT_NUL    : return 0;                      // Null criterion
            case PlacementParameterSet.PLA_CRIT_CAT    : return  - category(generalParameterSet);// Category
            case PlacementParameterSet.PLA_CRIT_RANK   : return  getRank();      // Rank
            case PlacementParameterSet.PLA_CRIT_RATING : return  getRating();    // Rating
            case PlacementParameterSet.PLA_CRIT_NBW    : return  (rn >= 0) ? nbwX2[rn] : 0;                     // Number of Wins
            case PlacementParameterSet.PLA_CRIT_MMS    : return  (rn >= 0) ? mmsX2[rn] : 2 * smms(generalParameterSet);  // McMahon score
            case PlacementParameterSet.PLA_CRIT_STS    : return  (rn >= 0) ? stsX2[rn] : 2 * smms(generalParameterSet);  // STS score 
                
            case PlacementParameterSet.PLA_CRIT_SOSW   : return  (rn >= 0) ? this.soswX2[rn] : 0;	// Sum of Opponents McMahon scores
            case PlacementParameterSet.PLA_CRIT_SOSWM1 : return  (rn >= 0) ? this.soswM1X2[rn] : 0;
            case PlacementParameterSet.PLA_CRIT_SOSWM2 : return  (rn >= 0) ? this.soswM2X2[rn] : 0;
            case PlacementParameterSet.PLA_CRIT_SODOSW : return  (rn >= 0) ? this.getSdswX4()[rn] : 0;	// Sum of Defeated Opponents Scores
            case PlacementParameterSet.PLA_CRIT_SOSOSW : return  (rn >= 0) ? this.ssswX2[rn] : 0;	// Sum of opponents SOS
            case PlacementParameterSet.PLA_CRIT_CUSSW  : return  (rn >= 0) ? this.cuswX2[rn] : 0;	// Cuss

            case PlacementParameterSet.PLA_CRIT_SOSM   : return  (rn >= 0) ? this.sosmX2[rn] : 0;	// Sum of Opponents McMahon scores
            case PlacementParameterSet.PLA_CRIT_SOSMM1 : return  (rn >= 0) ? this.sosmM1X2[rn] : 0;
            case PlacementParameterSet.PLA_CRIT_SOSMM2 : return  (rn >= 0) ? this.sosmM2X2[rn] : 0;
            case PlacementParameterSet.PLA_CRIT_SODOSM : return  (rn >= 0) ? this.getSdsmX4()[rn] : 0;	// Sum of Defeated Opponents Scores
            case PlacementParameterSet.PLA_CRIT_SOSOSM : return  (rn >= 0) ? this.sssmX2[rn] : 0;	// Sum of opponents SOS
            case PlacementParameterSet.PLA_CRIT_CUSSM  : return  (rn >= 0) ? this.cusmX2[rn] : 0;	// Cuss
           
            case PlacementParameterSet.PLA_CRIT_SOSTS   : return  (rn >= 0) ? this.sostsX2[rn] : 0;	// Sum of Opponents STS scores  

            case PlacementParameterSet.PLA_CRIT_EXT    : return  (rn >= 0) ? this.extX2[rn] : 0;       // Exploits tentes
            case PlacementParameterSet.PLA_CRIT_EXR    : return  (rn >= 0) ? this.exrX2[rn] : 0;       // Exploits reussis


            case PlacementParameterSet.PLA_CRIT_DC      : return dc;
            case PlacementParameterSet.PLA_CRIT_SDC     : return sdc;

            default :
            return 0;
        }
    }

     /**
     * converts a score value into a string
     * fractional part will be formatted as : ½ ¼ ¾
     */
    public String formatScore(int crit, int roundNumber){
        int value = this.getCritValue(crit, roundNumber);
        int coef = PlacementParameterSet.criterionCoef(crit);
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
     * Generate strings with a "oooortch" format
     * oooo being opponent number,
     * r being the result "+", "-", "=" or "?"
     * t (type) is either "/" for normal results or "!" for by default results
     * c being the colour, "w", "b" or "?"
     * h being handicap, "0" ... "9"
     * @param tps Tournament parameter set. useful for placement criteria and for absent and values scores
     */
public static String[][] halfGamesStrings(ArrayList<ScoredPlayer> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps, boolean bFull) {
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        // Prepare hmPos for fast retrieving
        HashMap<String, Integer> hmPos = new HashMap<String, Integer>();
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            hmPos.put(alOrderedScoredPlayers.get(i).getKeyString(), i);
        }
        String[][] hG = new String[roundNumber +1][alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            ScoredPlayer sp = alOrderedScoredPlayers.get(i);
            for(int r = 0; r <= roundNumber; r++){
                String strOpp = "   0";
                String strRes = " ";
                String strTyp = "/";
                String strCol = " ";
                String strHd  = "0";

                Game g = sp.gameArray[r];
                if (g == null){
                    strOpp = "   0";
                    strCol = " ";
                    strTyp = " ";
                    strHd  = " ";
                    if (sp.participation[r] == ScoredPlayer.NOT_ASSIGNED) strRes = "-";
                    else{
                        int res = 0;
                        if (sp.participation[r] == ScoredPlayer.ABSENT)
                            if (tps.tournamentType() == TournamentParameterSet.TYPE_MCMAHON) res = gps.getGenMMS2ValueAbsent();
                            else res = gps.getGenNBW2ValueAbsent();
                        else if (sp.participation[r] == ScoredPlayer.BYE)
                            if (tps.tournamentType() == TournamentParameterSet.TYPE_MCMAHON) res = gps.getGenMMS2ValueBye();
                            else res = gps.getGenNBW2ValueBye();
                        if (res == 2) strRes = "+";
                        else if (res == 1) strRes = "=";
                        else strRes = "-";
                    }
                }
                else{   //Real Game
                   Player opp = null;
                   int result = g.getResult();
                   if (result == Game.RESULT_UNKNOWN) strTyp = "/";
                   else strTyp = (result >= Game.RESULT_BYDEF) ? "!" : "/";
                   if (!bFull) strTyp ="";
                   int res = result;
                   if (result >= Game.RESULT_BYDEF) res = result - Game.RESULT_BYDEF;
                   if (g.getWhitePlayer().hasSameKeyString(sp)){
                       opp = g.getBlackPlayer();
                       strCol = "w";
                       if (res == Game.RESULT_WHITEWINS || res == Game.RESULT_BOTHWIN) strRes = "+";
                       else if (res == Game.RESULT_BLACKWINS || res == Game.RESULT_BOTHLOSE) strRes = "-";
                       else if (res == Game.RESULT_EQUAL) strRes = "=";
                       else strRes = "?";
                   }
                   else{
                       opp = g.getWhitePlayer();
                       strCol = "b";
                       if (res == Game.RESULT_BLACKWINS || res == Game.RESULT_BOTHWIN) strRes = "+";
                       else if (res == Game.RESULT_WHITEWINS || res == Game.RESULT_BOTHLOSE) strRes = "-";
                       else if (res == Game.RESULT_EQUAL) strRes = "=";
                       else strRes = "?" ;

                   }
                   if (!g.isKnownColor()) strCol = "?";
                   if (!bFull) strCol = "";
                   int oppNum = hmPos.get(opp.getKeyString());
                   strOpp = "    " + (oppNum +1);
                   strOpp = strOpp.substring(strOpp.length() - 4);  // To have 4 chars exactly
                   strHd = "" + g.getHandicap();
                   if (!bFull) strHd = "";
                }
                hG[r][i] = strOpp + strRes + strTyp + strCol + strHd;

            }
        }
        return hG;
    }

    /**
     * Generate an array of strings representing placement between 1 and number of players.
     * Basically placement is the position in alOrderedScoredPlayers + 1.
     * Except for ex-aequos
     */
    public static String[] positionStrings(ArrayList<ScoredPlayer> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps) {
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        ScoredPlayerComparator spc = new ScoredPlayerComparator(pps.getPlaCriteria(), roundNumber, true);
        int[] place = new int[alOrderedScoredPlayers.size()];
        if (place.length > 0) place[0] = 0;
        for (int i = 1; i < alOrderedScoredPlayers.size(); i++){
            if (spc.compare(alOrderedScoredPlayers.get(i), alOrderedScoredPlayers.get(i-1)) == 0) place[i] = place[i-1];
            else place[i] = i;
        }
        String[] strPlace = new String[alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            if ( i > 0 && place[i] == place[i-1] ) strPlace[i] = "    ";
            else strPlace[i] = "    " + (place[i] + 1);
            strPlace[i] = strPlace[i].substring(strPlace[i].length() - 4);
        }
        return strPlace;
    }

    /**
     * Generate a array of strings representing placement inside category between 1 and number of players.
     * Basically placement is the position in alOrderedScoredPlayers + 1.
     * Except for ex-aequos
     */
    public static String[] catPositionStrings(ArrayList<ScoredPlayer> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps) {
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        ScoredPlayerComparator spc = new ScoredPlayerComparator(pps.getPlaCriteria(), roundNumber, true);
        int[] place = new int[alOrderedScoredPlayers.size()];
        if (place.length > 0) place[0] = 0;
        int curCat = 0;
        int nbPlayersBeforeCurCat = 0;
        for (int i = 1; i < alOrderedScoredPlayers.size(); i++){
            int newCat = alOrderedScoredPlayers.get(i).category(gps);
            if (newCat != curCat){
                curCat = newCat;
                nbPlayersBeforeCurCat = i;
                place[i] = 0;
            }
            if (spc.compare(alOrderedScoredPlayers.get(i), alOrderedScoredPlayers.get(i-1)) == 0) place[i] = place[i-1];
            else place[i] = i - nbPlayersBeforeCurCat;
        }
        String[] strPlace = new String[alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            if (i > 0 && alOrderedScoredPlayers.get(i).category(gps) != alOrderedScoredPlayers.get(i-1).category(gps) )
                strPlace[i] = "    " + (place[i] + 1);
            else if (i > 0 && place[i] == place[i-1] ) strPlace[i] = "    ";
            else strPlace[i] = "    " + (place[i] + 1);
            strPlace[i] = strPlace[i].substring(strPlace[i].length() - 4);
        }
        return strPlace;
    }

    /**
     * @return the sdswX4
     */
    private int[] getSdswX4() {
        return sdswX4;
    }

    /**
     * @return the sdsmX4
     */
    private int[] getSdsmX4() {
        return sdsmX4;
    }
}
