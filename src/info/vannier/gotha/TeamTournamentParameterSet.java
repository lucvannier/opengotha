/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.vannier.gotha;

/**
 *
 * @author Luc Vannier
 */
public class TeamTournamentParameterSet implements java.io.Serializable{
//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;

    private TeamGeneralParameterSet teamGeneralParameterSet = new TeamGeneralParameterSet();
    private TeamPlacementParameterSet teamPlacementParameterSet = new TeamPlacementParameterSet();

    /** Creates a new instance of TournamentParameterSet */
    public TeamTournamentParameterSet() {
    }

    /** Creates a new instance of TournamentParameterSet, tps' clone */
    public TeamTournamentParameterSet(TeamTournamentParameterSet ttps) {
        teamGeneralParameterSet   = new TeamGeneralParameterSet(ttps.getTeamGeneralParameterSet());
        teamPlacementParameterSet = new TeamPlacementParameterSet(ttps.getTeamPlacementParameterSet());
    }

    public void init(){
        teamGeneralParameterSet.init();
        teamPlacementParameterSet.init();
    }
    
    /**
     * @return the teamGeneralParameterSet
     */
    public TeamGeneralParameterSet getTeamGeneralParameterSet() {
        return teamGeneralParameterSet;
    }

    /**
     * @param teamGeneralParameterSet the teamGeneralParameterSet to set
     */
    public void setTeamGeneralParameterSet(TeamGeneralParameterSet teamGeneralParameterSet) {
        this.teamGeneralParameterSet = teamGeneralParameterSet;
    }

    /**
     * @return the teamPlacementParameterSet
     */
    public TeamPlacementParameterSet getTeamPlacementParameterSet() {
        return teamPlacementParameterSet;
    }

    /**
     * @param teamPlacementParameterSet the teamPlacementParameterSet to set
     */
    public void setTeamPlacementParameterSet(TeamPlacementParameterSet teamPlacementParameterSet) {
        this.teamPlacementParameterSet = teamPlacementParameterSet;
    }

}
