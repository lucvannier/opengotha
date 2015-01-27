package info.vannier.gotha;

public class PlacementParameterSet implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    
    final static int PLA_MAX_NUMBER_OF_CRITERIA = 6; 
    
    final static int PLA_CRIT_NUL    = 0;       // Null criterion
    final static int PLA_CRIT_CAT    = 1;       // Category
    
    final static int PLA_CRIT_RANK   = 11;      // Rank
    final static int PLA_CRIT_RATING = 12;      // Rating

    final static int PLA_CRIT_NBW    = 100;	// Number of Wins
    final static int PLA_CRIT_MMS    = 200;	// McMahon score
    final static int PLA_CRIT_STS    = 300;     // Strasbourg score
    
    final static int PLA_CRIT_SOSW   = 110;	// Sum of Opponents NbW
    final static int PLA_CRIT_SOSWM1 = 111;	// Sum of (n-1)Opponents NbW
    final static int PLA_CRIT_SOSWM2 = 112;	// Sum of (n-2)Opponents NbW
    final static int PLA_CRIT_SODOSW = 120;	// Sum of Defeated opponents NbW scores
    final static int PLA_CRIT_SOSOSW = 130;	// Sum of opponents SOS 
    final static int PLA_CRIT_CUSSW  = 150;	// Cumulative Sum of Scores (Number of Wins) 

    final static int PLA_CRIT_SOSM   = 210;	// Sum of Opponents McMahon scores
    final static int PLA_CRIT_SOSMM1 = 211;	// Sum of (n-1)Opponents MMS
    final static int PLA_CRIT_SOSMM2 = 212;	// Sum of (n-2)Opponents MMS
    final static int PLA_CRIT_SODOSM = 220;	// Sum of Defeated opponents McMahon scores
    final static int PLA_CRIT_SOSOSM = 230;	// Sum of opponents SOS 
    final static int PLA_CRIT_CUSSM  = 250;	// Cumulative Sum of Scores (Number of Wins) 

    final static int PLA_CRIT_SOSTS  = 310;     // Sum of Opponents Strasbourg scores

    final static int PLA_CRIT_EXT    = 401;     // Exploits Reussis
    final static int PLA_CRIT_EXR    = 402;     // Exploits Tentes
    
    final static int PLA_CRIT_SDC    = 501;     // Simplified Direct Confrontation 
    final static int PLA_CRIT_DC     = 502;     // Direct Confrontation 

    final static PlacementCriterion[] allPlacementCriteria = {
        new PlacementCriterion(PLA_CRIT_NUL, "NULL", "NULL", "No tie break", 1),
        new PlacementCriterion(PLA_CRIT_CAT, "CAT", "CAT", "Category", -1),
        new PlacementCriterion(PLA_CRIT_NBW, "NBW", "NBW", "Number of Wins", 2),
        new PlacementCriterion(PLA_CRIT_MMS, "MMS", "MMS", "McMahon Score", 2),
        new PlacementCriterion(PLA_CRIT_STS, "STS", "STS", "Strasbourg Score", 2),
        new PlacementCriterion(PLA_CRIT_RANK, "Rank", "Rank", "Rank from 30K to 9D", 1),
        new PlacementCriterion(PLA_CRIT_RATING, "Rating", "Rating", "Rating from -900 to +2949", 1),
        
        new PlacementCriterion(PLA_CRIT_CUSSW, "CUSS", "CUSSW", "Cumulative Sum of Scores (NBW)", 2),
        new PlacementCriterion(PLA_CRIT_CUSSM, "CUSS", "CUSSM", "Cumulative Sum of Scores (MMS)", 2),

        new PlacementCriterion(PLA_CRIT_SOSW, "SOS", "SOSW", "Sum of Opponents Scores (NBW)", 2),
        new PlacementCriterion(PLA_CRIT_SOSWM1, "SOS-1", "SOSW-1", "Sum of (n-1) Best Opponents Scores (NBW)", 2),
        new PlacementCriterion(PLA_CRIT_SOSWM2, "SOS-2", "SOSW-2", "Sum of (n-2) Best Opponents Scores (NBW)", 2),
        new PlacementCriterion(PLA_CRIT_SODOSW, "SODOS", "SODOSW", "Sum of Defeated Opponents Scores (NBW)", 4),

        new PlacementCriterion(PLA_CRIT_SOSM, "SOS", "SOSM", "Sum of Opponents Scores (MMS)", 2),
        new PlacementCriterion(PLA_CRIT_SOSMM1, "SOS-1", "SOSM-1", "Sum of (n-1) Best Opponents Scores (MMS)", 2),
        new PlacementCriterion(PLA_CRIT_SOSMM2, "SOS-2", "SOSM-2", "Sum of (n-2) Best Opponents Scores (MMS)", 2),
        new PlacementCriterion(PLA_CRIT_SODOSM, "SODOS", "SODOSM", "Sum of Defeated Opponents Scores (MMS)", 4),

        new PlacementCriterion(PLA_CRIT_SOSTS, "SOSTS", "SOSTS", "Sum of Opponents Scores (STS)", 2),
        
        new PlacementCriterion(PLA_CRIT_SOSOSW, "SOSOS", "SOSOSW", "Sum of Opponents SOSW", 2),
        new PlacementCriterion(PLA_CRIT_SOSOSM, "SOSOS", "SOSOSM", "Sum of Opponents SOSM", 2),
        new PlacementCriterion(PLA_CRIT_EXT, "EXT", "EXT", "Exploits Tentes", 2),
        new PlacementCriterion(PLA_CRIT_EXR, "EXR", "EXR", "Exploits Reussis", 2),

        new PlacementCriterion(PLA_CRIT_DC, "DC", "DC", "Direct Confrontation", 1),
        new PlacementCriterion(PLA_CRIT_SDC, "SDC", "SDC", "Simplified Direct Confrontation", 1),

    };
    
    private int[] plaCriteria;
    
    final static int PLA_SMMS_CORR_MAX    =  2;
    final static int PLA_SMMS_CORR_MIN    = -1;
    
    public PlacementParameterSet() {             
        plaCriteria = new int[PLA_MAX_NUMBER_OF_CRITERIA];
        for (int i = 0; i < plaCriteria.length; i++) plaCriteria[i] = PLA_CRIT_NUL;
    }
     
    public PlacementParameterSet(PlacementParameterSet pps) {             
        int [] plaCritModel = pps.getPlaCriteria();
        int[] plaCrit = new int[PLA_MAX_NUMBER_OF_CRITERIA];
        System.arraycopy(plaCritModel, 0, plaCrit, 0, PLA_MAX_NUMBER_OF_CRITERIA);
        this.plaCriteria = plaCrit;
    }
    
    public void initForMM(){
        plaCriteria = new int[PLA_MAX_NUMBER_OF_CRITERIA];
        plaCriteria[0] = PLA_CRIT_MMS;
        plaCriteria[1] = PLA_CRIT_SOSM;
        plaCriteria[2] = PLA_CRIT_SOSOSM;
        plaCriteria[3] = PLA_CRIT_NUL;
        plaCriteria[4] = PLA_CRIT_NUL;
        plaCriteria[5] = PLA_CRIT_NUL;
    }
    
    public void initForSwiss(){
        plaCriteria = new int[PLA_MAX_NUMBER_OF_CRITERIA];
        plaCriteria[0] = PLA_CRIT_NBW;
        plaCriteria[1] = PLA_CRIT_SOSW;
        plaCriteria[2] = PLA_CRIT_SOSOSW;
        plaCriteria[3] = PLA_CRIT_NUL;
        plaCriteria[4] = PLA_CRIT_NUL;
        plaCriteria[5] = PLA_CRIT_NUL;
    }
    
    public void initForSwissCat(){
        plaCriteria = new int[PLA_MAX_NUMBER_OF_CRITERIA];
        plaCriteria[0] = PLA_CRIT_CAT;
        plaCriteria[1] = PLA_CRIT_NBW;
        plaCriteria[2] = PLA_CRIT_EXT;
        plaCriteria[3] = PLA_CRIT_EXR;
        plaCriteria[4] = PLA_CRIT_NUL;
        plaCriteria[5] = PLA_CRIT_NUL;
    }
    
    public String checkCriteriaCoherence(javax.swing.JFrame jfr){
        // DIR Coherence
        boolean bOK = true;
        String strMes = "Warning(s) :";
        int[] crit = this.getPlaCriteria();
        
        // 1st coherence test : DC or SDC should not appear twice
        int nbDirCrit = 0;
        for (int i = 0; i < crit.length; i++){
            if (crit[i] == PlacementParameterSet.PLA_CRIT_DC) nbDirCrit ++;
            if (crit[i] == PlacementParameterSet.PLA_CRIT_SDC) nbDirCrit ++;
        }
        if (nbDirCrit > 1){
            strMes += "\nOnly one Direct Confrontation criteria (DC or SDC) should appear";
            bOK = false;
        }
        // 2nd coherence test : Criteria should not mix elements from McMahon group with elements from Swiss group
        int nbSWCriteria = 0;
        int nbMMCriteria = 0;
        for (int i = 0; i < crit.length; i++){
            switch(crit[i]){
                case PlacementParameterSet.PLA_CRIT_CAT:
                case PlacementParameterSet.PLA_CRIT_NBW:
                case PlacementParameterSet.PLA_CRIT_SOSW:
                case PlacementParameterSet.PLA_CRIT_SOSWM1:
                case PlacementParameterSet.PLA_CRIT_SOSWM2:
                case PlacementParameterSet.PLA_CRIT_SODOSW:
                case PlacementParameterSet.PLA_CRIT_SOSOSW:
                case PlacementParameterSet.PLA_CRIT_CUSSW:
                case PlacementParameterSet.PLA_CRIT_EXR:
                case PlacementParameterSet.PLA_CRIT_EXT:
                    nbSWCriteria++;
                    break;
                case PlacementParameterSet.PLA_CRIT_MMS:
                case PlacementParameterSet.PLA_CRIT_SOSM:   
                case PlacementParameterSet.PLA_CRIT_SOSMM1:
                case PlacementParameterSet.PLA_CRIT_SOSMM2:
                case PlacementParameterSet.PLA_CRIT_SODOSM:
                case PlacementParameterSet.PLA_CRIT_SOSOSM:
                case PlacementParameterSet.PLA_CRIT_CUSSM:
                case PlacementParameterSet.PLA_CRIT_STS:
                case PlacementParameterSet.PLA_CRIT_SOSTS:
                    nbMMCriteria++;
                    break;
            } 
        }
        if (nbSWCriteria > 0 && nbMMCriteria > 0){
            strMes += "\nMcMahon and Swiss Criteria mixed";
            bOK = false;
        }

        // 3rd test : SODOSM is taboo
        boolean bSODOSM = false;
        for (int i = 0; i < crit.length; i++){
            if (crit[i] == PlacementParameterSet.PLA_CRIT_SODOSM) bSODOSM= true;
        }
        if (bSODOSM){
            strMes += "\nSODOSM is not recommended";
            bOK = false;
        }
        
        // 4rd test : STS warning
        boolean bSTS = false;
        for (int i = 0; i < crit.length; i++){
            if (crit[i] == PlacementParameterSet.PLA_CRIT_STS) bSTS= true;
            if (crit[i] == PlacementParameterSet.PLA_CRIT_SOSTS) bSTS= true;
        }
        if (bSTS){
            strMes += "\nSTS and SOSTS scores only make sense in a McMahon tournament"
                    + " with a single elimination bracket for players of the top group (see Help).";
            bOK = false;
        }


        if (bOK) return "";
        else return strMes;
        
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

    public static int[] purgeUselessCriteria(int[] tC){
        int nbC = 0;
        for (int c = 0; c < tC.length; c++) {
            if (tC[c] != PlacementParameterSet.PLA_CRIT_NUL) {
                nbC++;
            }
        }
        int[] tabCrit;
        if (nbC == 0) {
            tabCrit = new int[1];
            tabCrit[0] = PlacementParameterSet.PLA_CRIT_NUL;
        } else {
            tabCrit = new int[nbC];
            tabCrit[0] = PlacementParameterSet.PLA_CRIT_NUL;
            int crit = 0;
            for (int c = 0; c < tC.length; c++) {
                if (tC[c] != PlacementParameterSet.PLA_CRIT_NUL) {
                    tabCrit[crit++] = tC[c];
                }
            }
        }
        return tabCrit;
    }
    
    public int[] getPlaCriteria() {
        int[] plaC= new int[plaCriteria.length];
        System.arraycopy(plaCriteria, 0, plaC, 0, plaCriteria.length);
        return plaC;
    }

    public void setPlaCriteria(int[] plaCriteria) {
        this.plaCriteria = new int[plaCriteria.length];
        System.arraycopy(plaCriteria, 0, this.plaCriteria, 0, plaCriteria.length);
    }
    
    public int mainCriterion(){
        int mainCrit = PlacementParameterSet.PLA_CRIT_NBW;
        int[] crit = getPlaCriteria();
        for (int iC = 0; iC < crit.length; iC++){
            if (crit[iC] == PlacementParameterSet.PLA_CRIT_NBW){
                return PlacementParameterSet.PLA_CRIT_NBW;
            }
            if (crit[iC] == PlacementParameterSet.PLA_CRIT_MMS){
                return PlacementParameterSet.PLA_CRIT_MMS;
            }
        }  
        return mainCrit;
    }
}
    
class PlacementCriterion implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    
    public int uid;
    public String shortName;
    public String longName;
    public String description;
    public int coef;        // coef used for internal computations. Usually -1, 1, 2 or 4
                            // used at display time for division before displaying
    
    public PlacementCriterion(int uid, String shortName, String longName, String description, int coef){
        this.uid = uid;
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.coef = coef;
    }

}
