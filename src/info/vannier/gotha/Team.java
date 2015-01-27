package info.vannier.gotha;

/**
 *
 * @author Luc
 */
public class Team implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private int teamNumber;
    private String teamName;
    private Player[][] teamMembers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS][Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM];;

    public Team(int teamNumber, String name){
        this.teamNumber = teamNumber;
        this.teamName = name;
    }

    public Team(String name){
        teamNumber = -1;    // Because it is not possible, here to give a team
        this.teamName = name;
    }
    public Team(Team team){
        deepCopy(team);
    }
    
    private void deepCopy(Team t){
        this.teamNumber = t.getTeamNumber();
        this.teamName = t.getTeamName();
        for (int ir = 0; ir < Gotha.MAX_NUMBER_OF_ROUNDS; ir++){
            for (int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++){
                Player p = t.getTeamMember(ir, ib);
                this.setTeamMember(p, ir, ib);
            }
        }
    }

    /**
     * @return the name
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * @param name the name to set
     */
    public void setTeamName(String name) {
        this.teamName = name;
    }

    public boolean setTeamMember(Player p, int roundNumber, int boardNumber){
        if (roundNumber >= teamMembers.length || roundNumber < 0) return false;
        if (boardNumber >= teamMembers[0].length || boardNumber < 0) return false;
        else{
            teamMembers[roundNumber][boardNumber] = p;
            return true;
        }
    }

    public Player getTeamMember(int roundNumber, int boardNumber){
        if (roundNumber >= teamMembers.length || roundNumber < 0) return null;
        if (boardNumber >= teamMembers[0].length || boardNumber < 0) return null;
        if (boardNumber >= teamMembers.length || boardNumber < 0) return null;
        else{
            return teamMembers[roundNumber][boardNumber];
        }
    }
    
    public int boardNumber(int roundNumber, Player player){
        int bn = -1;
        for (int ib = 0; ib <Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++ ){
            Player p = this.getTeamMember(roundNumber, ib);
            if (p == null) continue;
            if (p.hasSameKeyString(player)){
                bn = ib;
                break;
            }
        }
        return bn;
    }

    /**
     * @return the teamNumber
     */
    public int getTeamNumber() {
        return teamNumber;
    }

    /**
     * @param teamNumber the teamNumber to set
     */
    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }
    
    /**
     * Starting from V3.28.04 where team members may be variable, meanRating means mean rating at first round
     * @return 
     */
    public int meanRating(){
        int sum = 0;
        for (int ip = 0; ip < this.teamMembers[0].length; ip++){
            Player p = teamMembers[0][ip];
            if (p != null) sum += p.getRating();
        }
        int mean = sum/teamMembers.length;
        return mean;
    }
    /**
     * Starting from V3.28.04 where team members may be variable, meanRating means mean rating at first round
     * @return 
     */
    public int meanRating(int nbMembers){
        int sum = 0;
        for (int ip = 0; ip < this.teamMembers[0].length; ip++){
            Player p = teamMembers[0][ip];
            if (p != null) sum += p.getRating();
        }
        int mean = sum/nbMembers;
        return mean;
    }
}