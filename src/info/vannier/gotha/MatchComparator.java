package info.vannier.gotha;

import java.io.Serializable;
import java.util.Comparator;


public class MatchComparator implements Comparator<ComparableMatch>, Serializable{
    public final static int NO_ORDER = 0;
    public final static int BOARD0_TABLE_NUMBER_ORDER = 2;
    public final static int BEST_TEAM_TS = 4;

    int matchOrderType = NO_ORDER;

    public MatchComparator(int matchOrderType){
        this.matchOrderType = matchOrderType;
    }

    @Override
    public int compare(ComparableMatch cm1, ComparableMatch cm2) {
        ScoredTeam wst1 = cm1.wst;
        ScoredTeam bst1 = cm1.bst;
        ScoredTeam wst2 = cm2.wst;
        ScoredTeam bst2 = cm2.bst;
        switch (matchOrderType){
            case BOARD0_TABLE_NUMBER_ORDER :
                if (cm1.board0TableNumber < cm2.board0TableNumber) return -1;
                else if (cm1.board0TableNumber > cm2.board0TableNumber) return 1;
                else return 0;
            case BEST_TEAM_TS:
                ScoredTeam st1 = wst1;
                ScoredTeamComparator scoredTeamComparator = new ScoredTeamComparator(false);
                if (scoredTeamComparator.compare(wst1, bst1) > 0) st1 = bst1;
                ScoredTeam st2 = wst2;
                if (scoredTeamComparator.compare(wst2, bst2) > 0) st2 = bst2;

                if (scoredTeamComparator.compare(st1, st2) < 0) return -1;
                else return 1;
                
            default :
                return 0;
        }


    }


}
