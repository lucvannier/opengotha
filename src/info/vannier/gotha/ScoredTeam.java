package info.vannier.gotha;

//import java.util.ArrayList;
//import java.util.HashMap;

/**
 *
 * @author Luc Vannier
 */
public class ScoredTeam extends Team implements java.io.Serializable{
//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private int[] critValue = new int[TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];
    public ScoredTeam(Team team, int[] valCrit){
        super(team);
        //teamPoints =
        System.arraycopy(valCrit, 0, this.critValue, 0, valCrit.length);
        for(int ic = valCrit.length; ic < this.critValue.length; ic++){
            this.critValue[ic] = 0;
        }
    }

    public int getCritValue(int numCrit){
        return critValue[numCrit];
    }

    public void setCritValue(int numCrit, int valCrit){
        this.critValue[numCrit] = valCrit;
    }

}
