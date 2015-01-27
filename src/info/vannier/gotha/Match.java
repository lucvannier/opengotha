package info.vannier.gotha;

import java.rmi.RemoteException;

/**
 *
 * Match between two teams
 */
public class Match implements java.io.Serializable{
    private int teamSize;
    /**
     * round number .
     * For matches being part of a round, between 0 and Total number of rounds -1.
     */
    private int roundNumber;

    private Team whiteTeam;
    private Team blackTeam;
    private int[] wResults;
    private int[] bResults;


    private Match(){
        wResults = new int[Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM];
        bResults = new int[Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM];
        for (int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++){
            wResults[ib] = 0;
            bResults[ib] = 0;
        }
    }

    public static Match buildMatch(int roundNumber, Team team1, Team team2, TournamentInterface tournament)throws RemoteException{
        if (team1 == null) return null;
        if (team2 == null) return null;
        
        Match match = new Match();
        match.teamSize = tournament.getTeamSize();
        match.roundNumber = roundNumber;
        Player player0 = team1.getTeamMember(roundNumber, 0);
        if (tournament.getGame(roundNumber, player0).getWhitePlayer().hasSameKeyString(player0)){
            match.whiteTeam = team1;
            match.blackTeam = team2;
        }
        else{
            match.whiteTeam = team2;
            match.blackTeam = team1;
        }
        for(int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++){
            Player wtp = match.whiteTeam.getTeamMember(roundNumber, ib);
            Player btp = match.blackTeam.getTeamMember(roundNumber, ib);
            if(wtp == null) continue;
            if(btp == null) continue;
            Game g = tournament.getGame(roundNumber, wtp);
            if (g == null) continue;

            match.wResults[ib] = tournament.getWX2(g, wtp);
            match.bResults[ib] = tournament.getWX2(g, btp);
        }
        return match;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Team getWhiteTeam(){
        return whiteTeam;
    }

    public Team getBlackTeam(){
        return blackTeam;
    }

    public Team getOpponentTeam(Team team){
        if (team.getTeamName().equals(whiteTeam.getTeamName())) return blackTeam;
        else if (team.getTeamName().equals(blackTeam.getTeamName()))return whiteTeam;
        else return null;
    }

    public int getWBoardResult(int boardNumber){
        return wResults[boardNumber];
    }
    public int getBBoardResult(int boardNumber){
        return bResults[boardNumber];
    }

    public int getWX2(Team team){
        int wx2 = 0;
        if (team.getTeamName().equals(whiteTeam.getTeamName())){
            for(int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++){
                wx2 += wResults[ib];
            }
        }
        if (team.getTeamName().equals(blackTeam.getTeamName())){
            for(int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++){
                wx2 += bResults[ib];
            }
        }
        return wx2;
    }
    public int getTeamScore(Team team){
        int threshold = 0;
        Team oppTeam = null;
        if (team.getTeamName().equals(whiteTeam.getTeamName())) oppTeam = blackTeam;
        else if (team.getTeamName().equals(blackTeam.getTeamName()))oppTeam = whiteTeam;
        else return 0;
        threshold = Math.max(teamSize, getWX2(oppTeam));
        int wx2 = getWX2(team);
        if (wx2 >  threshold) return 2;
        if (wx2 == threshold) return 1;
        else return 0;
    }
}
