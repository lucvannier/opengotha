/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.vannier.gotha;

/**
 *
 * @author Luc Vannier
 */
public class TeamGeneralParameterSet implements java.io.Serializable{
//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private int teamSize = 4; 

    /** Creates a new instance of GeneralParameterSet */
    public TeamGeneralParameterSet(int teamSize) {
        init(teamSize);
    }

    public TeamGeneralParameterSet() {
        init(4);
    }

    public TeamGeneralParameterSet(TeamGeneralParameterSet tgps) {
        this.teamSize = tgps.getTeamSize();
    }

    public final void init() {
        init(4);
    }

    public final void init(int teamSize) {
        this.teamSize = teamSize;
    }

    /**
     * @return the teamSize
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * @param teamSize the teamSize to set
     */
    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

}
