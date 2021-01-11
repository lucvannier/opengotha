package info.vannier.gotha;

/**
 *
 * @author Luc Vannier
 */
public class TeamPlacementParameterSet implements java.io.Serializable{
//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;

    final static int TPL_MAX_NUMBER_OF_CRITERIA = 6;

    final static int TPL_CRIT_NUL    = 0;       // Null criterion
    final static int TPL_CRIT_TEAMPOINTS = 1;
    final static int TPL_CRIT_SOST = 11;
    final static int TPL_CRIT_BOARDWINS = 12;
    final static int TPL_CRIT_BOARDWINS_9UB = 109;  // 9 Upper Boards
    final static int TPL_CRIT_BOARDWINS_8UB = 108;
    final static int TPL_CRIT_BOARDWINS_7UB = 107;  
    final static int TPL_CRIT_BOARDWINS_6UB = 106;  
    final static int TPL_CRIT_BOARDWINS_5UB = 105;  
    final static int TPL_CRIT_BOARDWINS_4UB = 104;  
    final static int TPL_CRIT_BOARDWINS_3UB = 103;  
    final static int TPL_CRIT_BOARDWINS_2UB = 102;  
    final static int TPL_CRIT_BOARDWINS_1UB = 101;  

    final static int TPL_CRIT_MEAN_RATING = 201;

    final static PlacementCriterion[] allPlacementCriteria = {
        new PlacementCriterion(TPL_CRIT_NUL, "NULL", "NULL", "No tie break", 1),
        new PlacementCriterion(TPL_CRIT_TEAMPOINTS, "TP", "TEAMP", "Team points", 1),
        new PlacementCriterion(TPL_CRIT_SOST, "SOST", "SOST", "Sum of Opponents Scores (Team points)", 1),
        new PlacementCriterion(TPL_CRIT_BOARDWINS, "BDW", "BDW", "Board Wins", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_9UB, "B9U", "BDW9U", "Board Wins. 9 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_8UB, "B8U", "BDW8U", "Board Wins. 8 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_7UB, "B7U", "BDW7U", "Board Wins. 7 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_6UB, "B6U", "BDW6U", "Board Wins. 6 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_5UB, "B5U", "BDW5U", "Board Wins. 5 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_4UB, "B4U", "BDW4U", "Board Wins. 4 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_3UB, "B3U", "BDW3U", "Board Wins. 3 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_2UB, "B2U", "BDW2U", "Board Wins. 2 Upper boards", 2),
        new PlacementCriterion(TPL_CRIT_BOARDWINS_1UB, "B1U", "BDW1U", "Board Wins. 1 Upper board", 2),
        new PlacementCriterion(TPL_CRIT_MEAN_RATING, "MNR", "MNR", "Mean rating at first round", 1),
    };

            private int[] plaCriteria;

    public TeamPlacementParameterSet() {
        plaCriteria = new int[TPL_MAX_NUMBER_OF_CRITERIA];
        for (int i = 0; i < plaCriteria.length; i++) plaCriteria[i] = TPL_CRIT_NUL;
    }

    public TeamPlacementParameterSet(TeamPlacementParameterSet tpps) {
        int [] plaCritModel = tpps.getPlaCriteria();
        int[] plaCrit = new int[TPL_MAX_NUMBER_OF_CRITERIA];
        System.arraycopy(plaCritModel, 0, plaCrit, 0, TPL_MAX_NUMBER_OF_CRITERIA);
        this.setPlaCriteria(plaCrit);
    }

    public TeamPlacementParameterSet deepCopy(){
        return new TeamPlacementParameterSet(this);
    }

    public boolean equals(TeamPlacementParameterSet tpps){
        for (int ic = 0; ic < TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA; ic++){
            if (this.getPlaCriterion(ic) != tpps.getPlaCriterion(ic)) return false;
        }
        return true;
    }

    public void init(){
        plaCriteria = new int[TPL_MAX_NUMBER_OF_CRITERIA];
        plaCriteria[0] = TPL_CRIT_TEAMPOINTS;
        plaCriteria[1] = TPL_CRIT_BOARDWINS;
        plaCriteria[2] = TPL_CRIT_BOARDWINS_3UB;
        plaCriteria[3] = TPL_CRIT_BOARDWINS_2UB;
        plaCriteria[4] = TPL_CRIT_BOARDWINS_1UB;
        plaCriteria[5] = TPL_CRIT_MEAN_RATING;
    }

    public static String criterionShortName(int crit){
        for (PlacementCriterion pc : allPlacementCriteria){
            if (pc.uid == crit) return pc.shortName;
        }
        return "";
    }

    public static String criterionLongName(int crit){
        for (PlacementCriterion pc : allPlacementCriteria){
            if (pc.uid == crit) return pc.longName;
        }
        return "";
    }

    public static String criterionDescription(int crit){
        for (PlacementCriterion pc : allPlacementCriteria){
            if (pc.uid == crit) return pc.description;
        }
        return "";
    }

    public static String[] criteriaLongNames(){
        String[] critLN = new String[allPlacementCriteria.length];
        for (int i = 0; i < allPlacementCriteria.length; i++)
            critLN[i] = allPlacementCriteria[i].longName;
        return critLN;
    }

    public static int criterionCoef(int crit){
        for (PlacementCriterion pc : allPlacementCriteria){
            if (pc.uid == crit) return pc.coef;
        }
        return 1;

    }
    public static int criterionUID(String longName){
        for (PlacementCriterion pc : allPlacementCriteria){
            if (pc.longName.compareTo(longName) == 0) return pc.uid;
        }
        return PlacementParameterSet.PLA_CRIT_NUL;
    }

    /**
     * @return the plaCriteria
     */
    public int[] getPlaCriteria() {
        int[] plaC= new int[plaCriteria.length];
        System.arraycopy(plaCriteria, 0, plaC, 0, plaCriteria.length);
        return plaC;
    }

    /**
     * @return the plaCriterion for crit number
     */
    public int getPlaCriterion(int iCrit) {
        return plaCriteria[iCrit];
    }

    /**
     * @param plaCriteria the plaCriteria to set
     */
    public final void setPlaCriteria(int[] plaCriteria) {
        this.plaCriteria = new int[plaCriteria.length];
        System.arraycopy(plaCriteria, 0, this.plaCriteria, 0, plaCriteria.length);
    }
}
