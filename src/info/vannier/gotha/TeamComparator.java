/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.vannier.gotha;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Luc Vannier
 */
public class TeamComparator implements Comparator<Team>, Serializable{
    public final static int NO_ORDER = 0;
    public final static int TEAM_NUMBER_ORDER = 1;
    public final static int TOTAL_RATING_ORDER = 11;

    int teamOrderType = TeamComparator.NO_ORDER;
    int nbMembers = Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM;
    
    public TeamComparator(int teamOrderType, int nbMembers){
        this.teamOrderType = teamOrderType;
        this.nbMembers = nbMembers;
    }

    @Override
    public int compare(Team t1, Team t2){
        switch (teamOrderType){
            case TEAM_NUMBER_ORDER :
                if (t1.getTeamNumber() < t2.getTeamNumber()) return -1;
                else if (t1.getTeamNumber() > t2.getTeamNumber()) return 1;
                else return 0;  // Should not happen
            case TOTAL_RATING_ORDER :
                int m1 = t1.meanRating(nbMembers);
                int m2 = t2.meanRating(nbMembers);
                if (m1 < m2) return 1;
                else if (m1 > m2) return -1;
                else if (t1.getTeamNumber() < t2.getTeamNumber()) return -1;
                else return 0;  
            default :
                return 0;
        }
    }

}
