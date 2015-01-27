package info.vannier.gotha;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Luc Vannier
 */
public class ScoredTeamComparator implements Comparator<ScoredTeam>, Serializable{
    private boolean bKeepExAequo = false;
    public ScoredTeamComparator(boolean bKeepExAequo){
        this.bKeepExAequo = bKeepExAequo;
    }

    @Override
    public int compare(ScoredTeam st1, ScoredTeam st2){
        int result = betterByScore(st1, st2);
        if (result != 0) return result;

        if (bKeepExAequo) return 0;

        if (st1.getTeamNumber() > st2.getTeamNumber()) result = 1;
        else if (st1.getTeamNumber() < st2.getTeamNumber()) result = -1;

        return result;
    }

    /**
     *
     * @param st1
     * @param st2
     * @return -1 if st1 is better by score than st2
     * <br> 1 if st2 is better by score than st1
     * <br> 0 if same scores
     */
    private int betterByScore(ScoredTeam st1, ScoredTeam st2){
        for (int ic = 0; ic < TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA; ic++){
            if (st1.getCritValue(ic) < st2.getCritValue(ic)) return 1;
            else if(st1.getCritValue(ic) > st2.getCritValue(ic)) return -1;
        }
        return 0;
    }
}
