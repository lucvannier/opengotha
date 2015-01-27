package info.vannier.gotha;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * TeamMemberStrings describes a member of a team.
 * It contains a set of methods dedicated to print or display a players list
 * @author Luc
 */
public class TeamMemberStrings {
    public String strTeamNumber;
    public String strTeamName;
    public String strBoardNumber;
    public String strPlayerName;
    public String strCountry;
    public String strClub;
    public String strRating;
    public String strMembership;
    
    public static TeamMemberStrings[] buildTeamMemberStrings(TournamentInterface tournament) throws RemoteException{
        TeamMemberStrings[] arTMS = new TeamMemberStrings
                [Gotha.MAX_NUMBER_OF_TEAMS * (Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM * Gotha.MAX_NUMBER_OF_ROUNDS +1)];
        
        ArrayList<Team> alTeams = tournament.teamsList();
        int teamSize = tournament.getTeamSize();
        int numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();

        TeamComparator teamComparator = new TeamComparator(TeamComparator.TEAM_NUMBER_ORDER, teamSize);
        Collections.sort(alTeams, teamComparator);
        
        int numTS = 0; 
        for (Team team : alTeams) {
            TeamMemberStrings tMS = new TeamMemberStrings();
            tMS.strTeamNumber  = "" + (team.getTeamNumber() + 1);
            tMS.strTeamName    = "" + team.getTeamName();
            tMS.strBoardNumber = "";
            tMS.strPlayerName  = "";
            tMS.strCountry     = "";
            tMS.strClub        = "";
            tMS.strRating      = "";
            tMS.strMembership  = "";
            
            arTMS[numTS++] = tMS;
            
            for (int ib = 0; ib < teamSize; ib++){
                ArrayList<Player> alP = tournament.playersList(team, ib);
                if (alP.isEmpty()) alP.add(null);
                for (Player p : alP){
                    tMS = new TeamMemberStrings();
                    tMS.strTeamNumber  = "";
                    tMS.strTeamName    = "";
                    tMS.strBoardNumber = "" + (ib + 1);
                    if (p == null){
                        tMS.strPlayerName  = "";
                        tMS.strCountry     = "";
                        tMS.strClub        = "";
                        tMS.strRating      = "";
                        tMS.strMembership  = "";
                    }
                    else{
                        tMS.strPlayerName  = p.getName() + " " + p.getFirstName();
                        tMS.strCountry     = p.getCountry();
                        tMS.strClub        = p.getClub();
                        tMS.strRating      = "" + p.getRating();
                        
                        boolean[] bM = tournament.membership(p, team, ib);
                        tMS.strMembership  = "";
                        for (int r = 0; r < numberOfRounds; r++){
                            tMS.strMembership += bM[r] ? "+" : "-"; 
                        }                       
                    }
                    arTMS[numTS++] = tMS;
                }
            }
        }
        return arTMS;
    }
    
}
