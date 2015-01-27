package info.vannier.gotha;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface TournamentInterface extends Remote{
    
    public TournamentParameterSet getTournamentParameterSet()  throws RemoteException;
    public void setTournamentParameterSet(TournamentParameterSet tournamentParameterSet) throws RemoteException;
    public TeamTournamentParameterSet getTeamTournamentParameterSet()  throws RemoteException;
    public void setTeamTournamentParameterSet(TeamTournamentParameterSet teamTournamentParameterSet) throws RemoteException;

    public Player[] getByePlayers() throws RemoteException;

    public Player getByePlayer(int roundNumber) throws RemoteException;
    public ArrayList<Player> alNotFINRegisteredPlayers() throws RemoteException;
    public ArrayList<Player> alNotPairedPlayers(int roundNumber) throws RemoteException;
    public ArrayList<Player> alNotParticipantPlayers(int roundNumber) throws RemoteException;

    public ArrayList<Player> getPlayersWhoDidNotShowUp(int roundNumber) throws RemoteException;

    public String getFullName() throws RemoteException;
    public String getShortName() throws RemoteException;
    public void setShortName(String shortName) throws RemoteException;

    public int tournamentType() throws RemoteException;
    public boolean isOpen() throws RemoteException;
    public void close() throws RemoteException;
    public boolean isHasBeenSavedOnce() throws RemoteException;
    public void setHasBeenSavedOnce(boolean hasBeenSavedOnce) throws RemoteException;

    public void adjustCategoryLimits() throws RemoteException;
    
    public boolean addPlayer(Player p)  throws TournamentException, RemoteException;
    public boolean fastAddPlayer(Player p)  throws TournamentException, RemoteException;
    public boolean isPlayerImplied(Player p) throws RemoteException;
    public boolean isPlayerImpliedInRound(Player p, int r) throws RemoteException;
    public boolean removePlayer(Player p)  throws TournamentException, RemoteException;
    public void removeAllPlayers()  throws RemoteException;
    public void modifyPlayer(Player p, Player modifiedPlayer )  throws TournamentException, RemoteException;
    public Player getPlayerByKeyString(String keyString) throws RemoteException;
    public Player getPlayerByObsoleteCanonicalName(String canonicalName) throws RemoteException;
    public Player homonymPlayerOf(Player p)  throws RemoteException;
    
    public int numberOfPlayers() throws RemoteException;
    public int numberOfPlayersStrongerOrEqualTo(int rank) throws RemoteException;
    public int numberOfPlayersInCategory(int numCat, ArrayList<ScoredPlayer> alSP) throws RemoteException;
    public ArrayList<Player> playersList() throws RemoteException;
    public HashMap<String, Player> playersHashMap() throws RemoteException;

    public int mms2(Player p, int roundNumber) throws RemoteException;

    public boolean addGame(Game g)  throws TournamentException, RemoteException;
    public boolean removeGame(Game g)  throws TournamentException, RemoteException;
    public void removeAllGames()  throws RemoteException;
    public void exchangeGameColors(Game g) throws RemoteException;
    public boolean setGameHandicap(Game g, int handicap) throws RemoteException;
    public Game getGame(int roundNumber, int tableNumber)  throws RemoteException;
    public Game getGame(int roundNumber, Player player)  throws RemoteException;
    
    public Player opponent(Game g, Player p) throws RemoteException;
    public int getWX2(Game g, Player p) throws RemoteException;

    public ArrayList<Game> gamesList() throws RemoteException;
    public ArrayList<Game> gamesList(int roundNumber) throws RemoteException;
    public ArrayList<Game> gamesListBefore(int roundNumber) throws RemoteException;
    public ArrayList<Game> gamesPlayedBy(Player p) throws RemoteException;
    public ArrayList<Game> gamesPlayedBy(Player p1, Player p2) throws RemoteException;
    public ArrayList<Game> duplicateGames() throws RemoteException;

    public void updateNumberOfRoundsIfNecesary()throws RemoteException;

    public ArrayList<Game> makeAutomaticPairing(ArrayList<Player> alPlayersToPair, int roundNumber)throws RemoteException;

    public void setByePlayer(Player p, int roundNumber) throws RemoteException;
    public void chooseAByePlayer(ArrayList<Player> alPlayers, int roundNumber) throws RemoteException;
    public void assignByePlayer(Player p, int roundNumber) throws RemoteException;
    public void unassignByePlayer(int roundNumber) throws RemoteException;
    
    public void renumberTablesByBestMMS(int roundNumber, ArrayList<Game> alGamesToRenumber) throws RemoteException;
    public void setResult(Game g, int result)throws RemoteException;
    public void setRoundNumber(Game g, int rn) throws RemoteException;
    
    public int presumablyCurrentRoundNumber() throws RemoteException;

    public int getTeamSize() throws RemoteException;
    public void setTeamSize(int teamSize) throws RemoteException;
    public boolean addTeam(Team t) throws RemoteException;
    public boolean removeTeam(Team t)  throws RemoteException;
    public void removeAllTeams()  throws RemoteException;
    public Team getTeamByName(String name) throws RemoteException;
    public Team getTeamOfPlayer(Player player, int roundNumber) throws RemoteException;
    public void setTeamMember(Team team, int roundNumber, int boardMember, Player player) throws RemoteException;
    public void modifyTeamName(Team team, String newName) throws RemoteException;
    public void unteamTeamMember(Team team, int roundNumber, int boardNumber) throws RemoteException;
    public void unteamTeamMembers(Team team, int roundNumber) throws RemoteException;
    public void unteamAllTeams(int roundNumber) throws RemoteException;
    public void cleanTeams() throws RemoteException;
    public void reorderTeamMembersByRating(Team team, int roundNumber) throws RemoteException;
    public void reorderTeamMembersByRating(int roundNumber) throws RemoteException;
    public void renumberTeamsByTotalRating() throws RemoteException;
    public boolean isTeamComplete(Team team, int roundNumber) throws RemoteException;
    public ArrayList<Game> incoherentTeamGames() throws RemoteException;
    public Team opponentTeam(Team team, int roundNumber) throws RemoteException;
    public int nbWX2Team(Team team, Team opponentTeam, int roundNumber) throws RemoteException;

    public ArrayList<Player> playersList(Team team, int boardNumber) throws RemoteException;
    public boolean[] membership(Player p, Team t, int boardNumber) throws RemoteException;
    
    public int numberOfTeams() throws RemoteException;
    public ArrayList<Team> teamsList() throws RemoteException;
    public HashMap<String, Player> teamablePlayersHashMap(int roundNumber) throws RemoteException;

    public ArrayList<Match> matchesList(int roundNumber) throws RemoteException;
    public ArrayList<Match> matchesListUpTo(int roundNumber) throws RemoteException;
    public Match getMatch(int roundNumber, int tableNumber) throws RemoteException;

    public void pairTeams(Team team0, Team team1, int roundNumber) throws RemoteException;
//    public void renumberMatchTablesByBestScore(int roundNumber, ArrayList<Match> alMatchesToRenumber)  throws RemoteException;

    public int findFirstAvailableTableNumber(int roundNumber) throws RemoteException;

    /** returns an ordered scored players list after roundNumber round, ordered according to tps */
    public ArrayList<ScoredPlayer> orderedScoredPlayersList(
            int roundNumber, PlacementParameterSet pps) throws RemoteException;
    /** Fills  alSPlayers with pairing information ; group infos and DU DD infos */
    public void fillPairingInfo(int roundNumber) throws RemoteException;

    public ScoredTeamsSet getAnUpToDateScoredTeamsSet(TeamPlacementParameterSet tpps, int roundNumber) throws RemoteException;

    public long getLastTournamentModificationTime()throws RemoteException;
    public void setLastTournamentModificationTime(long lastTournamentModificationTime)throws RemoteException;
    public long getCurrentTournamentTime()throws RemoteException;
    
    public boolean isChangeSinceLastSave() throws RemoteException;
    public void setChangeSinceLastSaveAsFalse() throws RemoteException;

    public String addGothaRMIClient(String strClient) throws RemoteException;
    public boolean clockIn(String strClient) throws RemoteException;
    
    public String egfClass() throws RemoteException;
    public int egfAdjustedTime() throws RemoteException;
    
    public boolean addClubsGroup(ClubsGroup cg) throws RemoteException;
    public void removeClubsGroup(ClubsGroup cg) throws RemoteException;
    public ClubsGroup getClubsGroupByName(String name) throws RemoteException;
    public ArrayList<ClubsGroup> clubsGroupsList() throws RemoteException;
    public void addClubToClubsGroup(String groupName, String clubName) throws RemoteException;
    public void removeClubFromClubsGroup(String groupName, String clubName) throws RemoteException;
    public boolean playersAreInCommonGroup(Player p1, Player p2)throws RemoteException;
}