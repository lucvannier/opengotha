package info.vannier.gotha;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tournament extends UnicastRemoteObject implements TournamentInterface, java.io.Serializable {

//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private Date saveDT;
    private String externalIPAddress;
    private String remoteRunningMode = "---";
    private String remoteFullVersionNumber = "x.yy";
    
    @Override
    public String getRemoteRunningMode(){
        return remoteRunningMode;
    }
    
    @Override
    public void setRemoteRunningMode(String remoteRunningMode){
        this.remoteRunningMode = remoteRunningMode;
    }
    
    @Override
    public String getRemoteFullVersionNumber(){
        return remoteFullVersionNumber;
    }

    @Override
    public void setRemoteFullVersionNumber(String remoteFullVersionNumber){
        this.remoteFullVersionNumber = remoteFullVersionNumber;
    }
    
    @Override
    public Date getSaveDT()throws RemoteException{
        return saveDT;
    }
    
    @Override
    public void setSaveDT(Date saveDT) throws RemoteException{
        this.saveDT = saveDT;
    }
    
    @Override
    public String getExternalIPAddress()throws RemoteException{
        return externalIPAddress;
    }
    
    @Override
    public void setExternalIPAddress(String externalIPAddress) throws RemoteException{
        this.externalIPAddress = externalIPAddress;
    }

    /**
     * Tournament parameter set
     */
    private TournamentParameterSet tournamentParameterSet;
    /**
     * Team parameter set
     */
    private TeamTournamentParameterSet teamTournamentParameterSet;
    /**
     * HashMap of Players The key is the getKeyString
     */
    private HashMap<String, Player> hmPlayers;
    /**
     * HashMap of Games The key is (roundNumber * Gotha.MAX_NUMBER_OF_TABLES +
     * tableNumber)
     */
    private HashMap<Integer, Game> hmGames;
    /**
     * byePlayer For each round, there is 1 bye player or 0 bye player
     * (byePlayer=null)
     */
    Player[] byePlayers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS];
    /**
     * HashMap of Teams The key is the getKeyString
     */
    private HashMap<String, Team> hmTeams;
    /**
     * HashMap of clubs groups
     */
    private HashMap<String, ClubsGroup> hmClubsGroups;
    /**
     * set to false when the tournament is closed. It is consulted by JFrXXX
     * classes to decide of their suicide
     */
    private transient boolean bOpen = true;

     /**
     * set to true when the tournament has already been saved at least once
     */
    private transient boolean hasBeenSavedOnce = false;

    /**
     * defines whether modifications have been made since last save
     */
    private transient boolean changeSinceLastSave = false;
    /**
     *      */
    private transient HashMap<String, ScoredPlayer> hmScoredPlayers = new HashMap<String, ScoredPlayer>();
    /**
     * time of last Refresh of base scoring info
     */
    private transient long lastBaseScoringInfoRefreshTime = 0;
    /**
     * time of last Alter of Tournament modification
     */
    private transient long lastTournamentModificationTime = System.currentTimeMillis();
    public transient ScoredTeamsSet scoredTeamsSet;
    


    public Tournament() throws RemoteException {
        tournamentParameterSet = new TournamentParameterSet();
        teamTournamentParameterSet = new TeamTournamentParameterSet();

        hmPlayers = new HashMap<>();
        hmGames = new HashMap<>();
        byePlayers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS];

        hmTeams = new HashMap<>();
        hmClubsGroups = new HashMap<>();
    }

    @Override
    public TournamentParameterSet getTournamentParameterSet() throws RemoteException {
        return tournamentParameterSet;
    }

    @Override
    public void setTournamentParameterSet(TournamentParameterSet tournamentParameterSet) throws RemoteException {
        this.tournamentParameterSet = tournamentParameterSet;
        this.setChangeSinceLastSave(true);
    }

    @Override
    public TeamTournamentParameterSet getTeamTournamentParameterSet() throws RemoteException {
        if (teamTournamentParameterSet == null) {
            teamTournamentParameterSet = new TeamTournamentParameterSet();
        }
        return teamTournamentParameterSet;
    }

    @Override
    public void setTeamTournamentParameterSet(TeamTournamentParameterSet teamTournamentParameterSet) throws RemoteException {
        this.teamTournamentParameterSet = teamTournamentParameterSet;
        this.setChangeSinceLastSave(true);
    }

    /**
     *
     * @param roundNumber
     * @return an ArrayList of all games of the specified round number
     */
    private ArrayList<Game> getAlGames(int roundNumber) {
        ArrayList<Game> alG = new ArrayList<Game>();
        for (Game g : hmGames.values()) {
            if (g.getRoundNumber() == roundNumber) {
                alG.add(g);
            }
        }
        return alG;
    }

    @Override
    public Player[] getByePlayers() throws RemoteException {
        if (byePlayers == null) {
            byePlayers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS];
        }
        Player[] bp = new Player[byePlayers.length];
        for (int ip = 0; ip < byePlayers.length; ip++) {
            Player p = null;
            if (byePlayers[ip] != null) {
                p = new Player(byePlayers[ip]);
            }
            bp[ip] = p;
        }
        return bp;
    }

    @Override
    public Player getByePlayer(int roundNumber) throws RemoteException {
        if (byePlayers == null) {
            byePlayers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS];
        }
        return byePlayers[roundNumber];
    }

    @Override
    /**
     * returns players without Final Registration
     */
    public ArrayList<Player> alNotFINRegisteredPlayers() throws RemoteException {
        ArrayList<Player> alNotFRP = new ArrayList<Player>();
        ArrayList<Player> alP = playersList();
        for (Player p : alP) {
            if (!p.getRegisteringStatus().equals("FIN")) {
                alNotFRP.add(p);
            }
        }
        return alNotFRP;
    }

    @Override
    /**
     * Among FIN-Registered players, returns not participating in round
     * roundNumber
     */
    public ArrayList<Player> alNotParticipantPlayers(int roundNumber) throws RemoteException {
        ArrayList<Player> alNotPP = new ArrayList<Player>();
        ArrayList<Player> alP = playersList();
        for (Player p : alP) {
            if (!p.getRegisteringStatus().equals("FIN")) {
                continue;
            }

            if (!p.getParticipating(roundNumber)) {
                alNotPP.add(p);
            }
        }
        return alNotPP;
    }

    @Override
    /**
     * Among FIN-Registered and participating players returns players being
     * neither paired nor bye
     */
    public ArrayList<Player> alNotPairedPlayers(int roundNumber) throws RemoteException {
        ArrayList<Player> alNotPP = new ArrayList<Player>();
        ArrayList<Player> alP = playersList();
        for (Player p : alP) {
            if (!p.getRegisteringStatus().equals("FIN")) {
                continue;
            }
            if (!p.getParticipating(roundNumber)) {
                continue;
            }

            if (!this.isPlayerImpliedInRound(p, roundNumber)) {
                alNotPP.add(p);
            }
        }
        return alNotPP;
    }

    @Override
    public ArrayList<Player> getPlayersWhoDidNotShowUp(int roundNumber) throws RemoteException {
        ArrayList<Player> alP = new ArrayList<Player>();
        ArrayList<Game> alG = this.getAlGames(roundNumber);
        for (Game g : alG) {
            int res = g.getResult();
            if (res == Game.RESULT_BLACKWINS_BYDEF
                    || res == Game.RESULT_BOTHLOSE_BYDEF) {
                Player p = g.getWhitePlayer();
                Player copyP = new Player();
                copyP.deepCopy(p);
                alP.add(copyP);
            }
            if (res == Game.RESULT_WHITEWINS_BYDEF
                    || res == Game.RESULT_BOTHLOSE_BYDEF) {
                Player p = g.getBlackPlayer();
                Player copyP = new Player();
                copyP.deepCopy(p);
                alP.add(copyP);
            }

        }
        return alP;

    }

    /**
     * @throws java.rmi.RemoteException
     */
    @Override
    public String getFullName() throws RemoteException {
        return this.getTournamentParameterSet().getGeneralParameterSet().getName();
    }
    
    /**
     * The short name for a tournament is an identifier This identifier is used -
     * - As the default name for save function 
     * - As the name in the Registry for RMI access 
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public String getShortName() throws RemoteException {
        String sn = this.getTournamentParameterSet().getGeneralParameterSet().getShortName();
        return sn;
    }
    @Override
    public void setShortName(String shortName) throws RemoteException {
        this.getTournamentParameterSet().getGeneralParameterSet().setShortName(shortName);
    }

    @Override
    public int tournamentType() throws RemoteException {
        TournamentParameterSet tps = this.getTournamentParameterSet();
        if (tps == null) {
            return TournamentParameterSet.TYPE_UNDEFINED;
        } else {
            return tps.tournamentType();
        }
    }

    @Override
    public boolean isOpen() throws RemoteException {
        return bOpen;
    }

    @Override
    public void close() throws RemoteException {
        if (Gotha.runningMode == Gotha.RUNNING_MODE_SRV) GothaRMIServer.removeTournament(this.getShortName());
        bOpen = false;
    }
    
    @Override
    public boolean isHasBeenSavedOnce() throws RemoteException{
        return hasBeenSavedOnce;
    }

    @Override
    public void setHasBeenSavedOnce(boolean hasBeenSavedOnce) throws RemoteException {
        this.hasBeenSavedOnce = hasBeenSavedOnce;
    }


    @Override
    public void adjustCategoryLimits() throws RemoteException {
        GeneralParameterSet gps = this.tournamentParameterSet.getGeneralParameterSet();
        int nbCat = gps.getNumberOfCategories();
        if (nbCat == 1) {
            return;
        }
        int[] tabLCL = new int[nbCat - 1];
        int nbPlayers = hmPlayers.size();
        int nbRemainingPlayers = nbPlayers;
        int lowLimit = Gotha.MAX_RANK;

        for (int c = 0; c < nbCat - 1; c++) {
            int nbPlayersOfCurCat = 0;
            while (nbRemainingPlayers > 0) {
                int nbPotPlayersOfCurCat = numberOfPlayersStrongerOrEqualTo(lowLimit);
                if (c > 0) {
                    nbPotPlayersOfCurCat -= numberOfPlayersStrongerOrEqualTo(tabLCL[c - 1]);
                }
                if (nbPotPlayersOfCurCat > nbRemainingPlayers / (nbCat - c)) {
                    tabLCL[c] = lowLimit + 1;
                    nbRemainingPlayers -= nbPlayersOfCurCat;
                    break;
                } else {
                    nbPlayersOfCurCat = nbPotPlayersOfCurCat;
                    lowLimit--;
                }
            }
            gps.setLowerCategoryLimits(tabLCL);
        }
        this.setChangeSinceLastSave(true);
    }

    /**
     * Adds a player to the players list of the tournament
     *
     * @return true if addition succeeds. false if addition fails
     */
    @Override
    public boolean addPlayer(Player p) throws TournamentException, RemoteException {
        if (hmPlayers.size() >= Gotha.MAX_NUMBER_OF_PLAYERS) {
            throw new TournamentException("Player" + " " + p.fullName() + " " + "could not be inserted"
                    + "\n" + "Maximum number of players exceeded");
        }
        Player homonymPlayer = homonymPlayerOf(p);
        if (homonymPlayer == null) {
            hmPlayers.put(p.getKeyString(), p);
            this.setChangeSinceLastSave(true);
            return true;
        } else {
            throw new TournamentException("Player" + " " + p.fullName() + " " + "could not be inserted" + "\n" + "A player named" + " " + homonymPlayer.fullName() + " " + "already exists in the tournament");
        }
    }

    /**
     * Adds a player to the players list of the tournament without checking
     * homonymy This method is dedicated to debug and should not be used in
     * normal run
     *
     * @return true if addition succeeds. false if addition fails
     *
     */
    @Override
    public boolean fastAddPlayer(Player p) throws TournamentException, RemoteException {
        if (hmPlayers.size() >= Gotha.MAX_NUMBER_OF_PLAYERS) {
            throw new TournamentException("Player" + " " + p.fullName() + " " + "could not be inserted" + "\n" + "Maximum number of players exceeded");
        }
        hmPlayers.put(p.getKeyString(), p);
        this.setChangeSinceLastSave(true);
        return true;
    }

    /**
     * @return true if p is implied in a game or as a bye player. <br>false if p
     * is implied nowhere.
     */
    @Override
    public boolean isPlayerImplied(Player p) throws RemoteException {
        for (Game g : hmGames.values()) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();

            if (wP.hasSameKeyString(p) || bP.hasSameKeyString(p)) {
                return true;
            }
        }
        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            if (byePlayers[r] == null) {
                continue;
            }
            if (byePlayers[r].hasSameKeyString(p)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if p is implied in a game or as a bye player in round r.
     * <br>false if p is not implied in round r.
     */
    @Override
    public boolean isPlayerImpliedInRound(Player p, int r) throws RemoteException {
        for (Game g : hmGames.values()) {
            if (g.getRoundNumber() != r) {
                continue;
            }
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();

            if (wP.hasSameKeyString(p) || bP.hasSameKeyString(p)) {
                return true;
            }
        }
        if (byePlayers[r] == null) {
            return false;
        }
        if (byePlayers[r].hasSameKeyString(p)) {
            return true;
        }

        return false;
    }

    /**
     * Removes a player from the players list of the tournament
     *
     * @return true if removal succeeds. <br>false if the player does not exist
     * in vPlayers.
     */
    @Override
    public boolean removePlayer(Player player) throws TournamentException, RemoteException {
        if (this.isPlayerImplied(player)) {
            throw new TournamentException("" + player.fullName()
                    + " " + "could not be removed"
                    + "\n" + "This player is involved in a game or as a bye player");
        }

        if (hmPlayers.remove(player.getKeyString()) == null) {
            System.out.println("removePlayer. Possible Error. player is not known in hmPlayers");
            return false;
        }

        // Clean out teams
        for (Team team : teamsList()) {
            for (int ir = 0; ir < Gotha.MAX_NUMBER_OF_ROUNDS; ir++){
                for (int ip = 0; ip < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ip++) {
                    Player p = team.getTeamMember(ir, ip);
                    if (player.hasSameKeyString(p)) {
                        this.setTeamMember(team, ir, ip, null);
                    }
                }
            }
        }

        this.setChangeSinceLastSave(true);
        return true;

    }

    /**
     * Removes all players
     */
    @Override
    public void removeAllPlayers() throws RemoteException {
        if (hmPlayers != null) {
            hmPlayers.clear();
            this.setChangeSinceLastSave(true);
        }
    }

    @Override
    public void modifyPlayer(Player p, Player modifiedPlayer) throws TournamentException, RemoteException {
        // p is the key Player
        // modifiedPlayer defines the new contents to give to the actual Player
        // playerToModify is the actual Player to modify
        // homonymPlayer will be the homonym, if exists, of modifiedPlayer

        Player playerToModify = hmPlayers.get(p.getKeyString());

        Player homonymPlayer = homonymPlayerOf(modifiedPlayer);
        // Detect if a differentPlayer with same key string already exists
        if (homonymPlayer == playerToModify || homonymPlayer == null) {
            hmPlayers.remove(playerToModify.getKeyString());
            playerToModify.deepCopy(modifiedPlayer);
            hmPlayers.put(playerToModify.getKeyString(), playerToModify);
            this.setChangeSinceLastSave(true);
        } else {
            throw new TournamentException("Player " + p.fullName() + " " + "could not be modified" + "\n" + "A player named" + " " + homonymPlayer.fullName()
                    + " " + "already exists in the tournament");
        }
    }

    /**
     * @param strNaFi Name + FirstName
     * @return a reference to the player if found in hmPlayers <br> null if not
     * found
     * @throws java.rmi.RemoteException
     */
    @Override
    public Player getPlayerByKeyString(String strNaFi) throws RemoteException {
        return hmPlayers.get(Player.computeKeyString(strNaFi));
    }

    // designed to import games and teams from old xml files
    @Override
    public Player getPlayerByObsoleteCanonicalName(String canonicalName) throws RemoteException {
        Player player = null;
        for (Player p : hmPlayers.values()) {
            String s = p.getName().toUpperCase() + p.getFirstName().toUpperCase();
            s = s.replaceAll(" ", "");
            s = s.replaceAll("[ÀÁÂÃÄÅ]", "A");  // 192 - 197
            s = s.replaceAll("Ç", "C");         // 199
            s = s.replaceAll("[ÈÉÊË]", "E");    // 200 - 203
            s = s.replaceAll("[ÌÍÎÏ]", "I");    // 204 - 207
            s = s.replaceAll("Ñ", "N");         // 209
            s = s.replaceAll("[ÒÓÔÕÖØ]", "O");  // 210-214 and 216
            s = s.replaceAll("[ÙÚÛÜ]", "U");    // 217 - 220
            s = s.replaceAll("Ý", "Y");         // 221

            s = s.replaceAll("[^A-Z]", "");

            if (s.equals(canonicalName)) {
                player = p;
                break;
            }
        }
        return player;
    }

    /**
     * Finds the homonym, if exists, of player p
     */
    @Override
    public Player homonymPlayerOf(Player p) throws RemoteException {
        if (p == null) {
            return null;
        } else {
            return getPlayerByKeyString(p.getKeyString());
        }
    }

    /**
     * Returns the total number of players
     */
    @Override
    public int numberOfPlayers() throws RemoteException {
        if (hmPlayers == null) {
            return 0;
        } else {
            return hmPlayers.size();
        }
    }

    @Override
    public int numberOfPlayersStrongerOrEqualTo(int rank) throws RemoteException {
        int nb = 0;
        for (Player p : hmPlayers.values()) {
            if (p.getRank() >= rank) {
                nb++;
            }
        }
        return nb;
    }

    @Override
    public int numberOfPlayersInCategory(int numCat, ArrayList<ScoredPlayer> alSP) throws RemoteException {
        GeneralParameterSet gps = tournamentParameterSet.getGeneralParameterSet();
        int nb = 0;
        for (ScoredPlayer sp : alSP) {
            if (sp.category(gps) == numCat) {
                nb++;
            }
        }
        return nb;
    }

    @Override
    public ArrayList<Player> playersList() throws RemoteException {
        return new ArrayList<Player>(hmPlayers.values());
    }

    @Override
    public HashMap<String, Player> playersHashMap() throws RemoteException {
        return new HashMap<String, Player>(hmPlayers);
    }

    /**
     * returns 2 * MMS of the player before roundNumber. For example, if
     * roundNumber = 3, MMS takes in account results of round 0, 1, 2.
     */
    @Override
    public int mms2(Player p, int roundNumber) throws RemoteException {
        // This routine is slow :O(nbPlayers^2).
        // Use it carefully, only when necessary

        if (roundNumber == 0) {
            return 2 * p.smms(this.tournamentParameterSet.getGeneralParameterSet());
        }

        fillBaseScoringInfoIfNecessary();  // If necessary ...

        ScoredPlayer sp = hmScoredPlayers.get(p.getKeyString());
        return sp.getMMSX2(roundNumber - 1);
    }

    @Override
    public boolean addGame(Game g) throws TournamentException, RemoteException {
        // If one player of this game is already implied in a game of the same round or is bye player,
        // throw exception
        // ....
        //    throw new TournamentException("Game could not be inserted"
        //            + "\n One player is already assigned");

        if (g == null) {
            return false;
        }
        Player wp = g.getWhitePlayer();
        Player bp = g.getBlackPlayer();
        if (wp == null) {
            return false;
        }
        if (bp == null) {
            return false;
        }

        int r = g.getRoundNumber();
        int t = g.getTableNumber();
        Integer key = r * Gotha.MAX_NUMBER_OF_TABLES + t;
        hmGames.put(key, g);
        this.setChangeSinceLastSave(true);
        return true;
    }

    @Override
    public boolean removeGame(Game g) throws TournamentException, RemoteException {
        if (g == null) return false;
        int r = g.getRoundNumber();
        int t = g.getTableNumber();
        Integer key = r * Gotha.MAX_NUMBER_OF_TABLES + t;
        hmGames.remove(key);
        this.setChangeSinceLastSave(true);
        return true;
    }

    /**
     * Removes all games
     */
    @Override
    public void removeAllGames() throws RemoteException {
        if (hmGames != null) {
            hmGames.clear();
            this.setChangeSinceLastSave(true);
        }

    }

    /**
     * exchange whitePlayer and blackPlayer
     */
    @Override
    public void exchangeGameColors(Game g) throws RemoteException {
        int r = g.getRoundNumber();
        int t = g.getTableNumber();
        Integer key = r * Gotha.MAX_NUMBER_OF_TABLES + t;
        Game gameToModify = hmGames.get(key);
        Player wP = gameToModify.getWhitePlayer();
        Player bP = gameToModify.getBlackPlayer();
        gameToModify.setWhitePlayer(bP);
        gameToModify.setBlackPlayer(wP);
        if (gameToModify.getResult() == Game.RESULT_WHITEWINS) {
            gameToModify.setResult(Game.RESULT_BLACKWINS);
        } else if (gameToModify.getResult() == Game.RESULT_BLACKWINS) {
            gameToModify.setResult(Game.RESULT_WHITEWINS);
        } else if (gameToModify.getResult() == Game.RESULT_WHITEWINS_BYDEF) {
            gameToModify.setResult(Game.RESULT_BLACKWINS_BYDEF);
        } else if (gameToModify.getResult() == Game.RESULT_BLACKWINS_BYDEF) {
            gameToModify.setResult(Game.RESULT_WHITEWINS_BYDEF);
        }
        this.setChangeSinceLastSave(true);
    }

    @Override
    public boolean setGameHandicap(Game g, int handicap) throws RemoteException {
        int r = g.getRoundNumber();
        int t = g.getTableNumber();
        Integer key = r * Gotha.MAX_NUMBER_OF_TABLES + t;
        Game gameToModify = hmGames.get(key);
        gameToModify.setHandicap(handicap);
        return true;
    }

    @Override
    public Game getGame(int roundNumber, int tableNumber) throws RemoteException {
        return hmGames.get(roundNumber * Gotha.MAX_NUMBER_OF_TABLES + tableNumber);
    }

    @Override
    public Game getGame(int roundNumber, Player player) throws RemoteException {
        ArrayList<Game> alG = this.gamesPlayedBy(player);
        for (Game g : alG) {
            if (g.getRoundNumber() == roundNumber) {
                return g;
            }
        }
        return null;
    }

    @Override
    public Player opponent(Game g, Player p) throws RemoteException {
        if (g == null) {
            return null;
        }
        Player wP = g.getWhitePlayer();
        Player bP = g.getBlackPlayer();
        if (wP.hasSameKeyString(p)) {
            return bP;
        } else if (bP.hasSameKeyString(p)) {
            return wP;
        } else {
            return null;
        }
    }

    @Override
    public int getWX2(Game g, Player p) throws RemoteException {
        int wX2 = 0;
        Player wP = g.getWhitePlayer();
        Player bP = g.getBlackPlayer();
        boolean pIsWhite = true;
        if (wP.hasSameKeyString(p)) {
            pIsWhite = true;
        } else if (bP.hasSameKeyString(p)) {
            pIsWhite = false;
        } else {
            return 0;
        }
        switch (g.getResult()) {
            case Game.RESULT_BOTHLOSE:
            case Game.RESULT_BOTHLOSE_BYDEF:
            case Game.RESULT_UNKNOWN:
                wX2 = 0;
                break;
            case Game.RESULT_WHITEWINS:
            case Game.RESULT_WHITEWINS_BYDEF:
                if (pIsWhite) {
                    wX2 = 2;
                }
                break;
            case Game.RESULT_BLACKWINS:
            case Game.RESULT_BLACKWINS_BYDEF:
                if (!pIsWhite) {
                    wX2 = 2;
                }
                break;
            case Game.RESULT_EQUAL:
            case Game.RESULT_EQUAL_BYDEF:
                wX2 = 1;
                break;
            case Game.RESULT_BOTHWIN:
            case Game.RESULT_BOTHWIN_BYDEF:
                wX2 = 2;
                break;
        }
        return wX2;
    }

    @Override
    public ArrayList<Game> gamesList() throws RemoteException {
        return new ArrayList<Game>(hmGames.values());
    }

    @Override
    public ArrayList<Game> gamesList(int roundNumber) throws RemoteException {
        ArrayList<Game> gL = new ArrayList<Game>();
        for (Game g : hmGames.values()) {
            if (g.getRoundNumber() == roundNumber) {
                gL.add(g);
            }
        }
        return gL;
    }

    @Override
    public ArrayList<Game> gamesListBefore(int roundNumber) throws RemoteException {
        ArrayList<Game> gL = new ArrayList<Game>();
        for (Game g : hmGames.values()) {
            if (g.getRoundNumber() < roundNumber) {
                gL.add(g);
            }
        }
        return gL;
    }

    @Override
    public ArrayList<Game> gamesPlayedBy(Player p) throws RemoteException {
        Player player = null;
        try {
            player = this.homonymPlayerOf(p);
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Game> gL = new ArrayList<Game>();
        for (Game g : hmGames.values()) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            if (wP == null) {
                continue;
            }
            if (bP == null) {
                continue;
            }
            if (wP.hasSameKeyString(player) || bP.hasSameKeyString(player)) {
                gL.add(g);
            }
        }
        return gL;
    }

    @Override
    public ArrayList<Game> gamesPlayedBy(Player p1, Player p2) throws RemoteException {
        Player player1 = null;
        Player player2 = null;
        try {
            player1 = this.homonymPlayerOf(p1);
            player2 = this.homonymPlayerOf(p2);
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Game> gL = new ArrayList<Game>();
        for (Game g : hmGames.values()) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            if (wP.hasSameKeyString(player1) && bP.hasSameKeyString(player2)) {
                gL.add(g);
            }
            if (wP.hasSameKeyString(player2) && bP.hasSameKeyString(player1)) {
                gL.add(g);
            }
        }
        return gL;
    }

    /**
     * Checks all the games and detects multiple pairing of a couple of players
     *
     * @return the duplicate games
     * @throws java.rmi.RemoteException
     */
    @Override
    public ArrayList<Game> duplicateGames() throws RemoteException {
        ArrayList<Game> alG = this.gamesList();
        ArrayList<Game> alDupG = new ArrayList<Game>();

        ArrayList<Player> alP = playersList();
        for (Player p1 : alP) {
            for (Player p2 : alP) {
                ArrayList<Game> alGP1P2 = new ArrayList<Game>();
                for (Game g : alG) {
                    Player wP = g.getWhitePlayer();
                    Player bP = g.getBlackPlayer();
                    if ((p1.hasSameKeyString(wP) && p2.hasSameKeyString(bP)) || (p2.hasSameKeyString(wP) && p1.hasSameKeyString(bP))) {
                        alGP1P2.add(g);
                    }
                }
                if (alGP1P2.size() > 1) {
                    alDupG.addAll(alGP1P2);
                }
            }
        }
        return alDupG;
    }

    /**
     * After imports or during Round-robin management, it maytbe necessary to
     * update number Of Rounds. updateNumberOfRoundsIfNecesary checks the round
     * number of all games and updates numberOfRounds if necesary
     */
    @Override
    public void updateNumberOfRoundsIfNecesary() throws RemoteException {
        TournamentParameterSet tps = getTournamentParameterSet();
        int numberOfRounds = tps.getGeneralParameterSet().getNumberOfRounds();
        for (Game g : gamesList()) {
            int r = g.getRoundNumber();
            if (r >= numberOfRounds) {
                numberOfRounds = r + 1;
            }
        }
        for (int r = numberOfRounds; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            if (byePlayers[r] != null) {
                numberOfRounds = r + 1;
            }
        }

        tps.getGeneralParameterSet().setNumberOfRounds(numberOfRounds);
        setTournamentParameterSet(tps);
    }

    @Override
    public ArrayList<Game> makeAutomaticPairing(ArrayList<Player> alPlayersToPair, int roundNumber) throws RemoteException {
        if (alPlayersToPair.size() % 2 != 0) {
            return null;
        }

        GeneralParameterSet gps = tournamentParameterSet.getGeneralParameterSet();
        PlacementParameterSet pps = tournamentParameterSet.getPlacementParameterSet();

        // Prepare scoring info
        this.fillBaseScoringInfoIfNecessary();
        // Get alPreviousGames
        ArrayList<Game> alPreviousGames = gamesListBefore(roundNumber);

        // Fill pairing info
        fillPairingInfo(roundNumber);

        // What is the main score in this tps ?
        int mainCrit = pps.mainCriterion();

        // And what is mainScoreMin and mainScoreMax ?
        int mainScoreMin = 0;              // Default value
        int mainScoreMax = roundNumber;// Default value
        if (mainCrit == PlacementParameterSet.PLA_CRIT_MMS) {
            mainScoreMin = gps.getGenMMFloor() + PlacementParameterSet.PLA_SMMS_CORR_MIN - Gotha.MIN_RANK;
            mainScoreMax = gps.getGenMMBar() + PlacementParameterSet.PLA_SMMS_CORR_MAX + roundNumber - Gotha.MIN_RANK;
        }

        ArrayList<Game> alGames = new ArrayList<Game>();

        // If necessary, split alPlayersToPair into smaller players groups
        ArrayList<Player> alRemainingPlayers = new ArrayList<Player>(alPlayersToPair);

        while (alRemainingPlayers.size() > Pairing.PAIRING_GROUP_MAX_SIZE) {
            boolean bGroupReady = false;
            ArrayList<Player> alGroupedPlayers = new ArrayList<Player>();
            for (int cat = 0; cat < gps.getNumberOfCategories(); cat++) {
                for (int mainScore = mainScoreMax; mainScore >= mainScoreMin; mainScore--) {
                    for (Iterator<Player> it = alRemainingPlayers.iterator(); it.hasNext();) {
                        Player p = it.next();
                        if (p.category(gps) > cat) {
                            continue;
                        }
                        ScoredPlayer sp = hmScoredPlayers.get(p.getKeyString());
                        if (sp.getCritValue(mainCrit, roundNumber - 1) / 2 < mainScore) {
                            continue;
                        }
                        alGroupedPlayers.add(p);
                        it.remove();
                        // 2 Emergency breaks
                        if (alGroupedPlayers.size() >= Pairing.PAIRING_GROUP_MAX_SIZE) {
                            bGroupReady = true;
                            break;
                        }
                        if (alRemainingPlayers.size() <= Pairing.PAIRING_GROUP_MIN_SIZE) {
                            bGroupReady = true;
                            break;
                        }
                    }
                    // Is the group ready for pairing ?
                    if (alGroupedPlayers.size() >= Pairing.PAIRING_GROUP_MIN_SIZE && alGroupedPlayers.size() % 2 == 0) {
                        bGroupReady = true;
                    }
                    if (bGroupReady) {
                        break;
                    }
                }
                if (bGroupReady) {
                    break;
                }
            }

            ArrayList<Game> alG = pairAGroup(alGroupedPlayers, roundNumber, hmScoredPlayers, alPreviousGames);
            alGames.addAll(alG);
        }

        fillPairingInfo(roundNumber);

        ArrayList<Game> alG = pairAGroup(alRemainingPlayers, roundNumber, hmScoredPlayers, alPreviousGames);
        alGames.addAll(alG);

        return alGames;

    }

    private ArrayList<Game> pairAGroup(ArrayList<Player> alGroupedPlayers, int roundNumber,
            HashMap<String, ScoredPlayer> hmScoredPlayers,
            ArrayList<Game> alPreviousGames) {

        int numberOfPlayersInGroup = alGroupedPlayers.size();

//      Prepare infos about Score groups : sgSize, sgNumber and innerPosition
//      And DUDD information

        long[][] costs = new long[numberOfPlayersInGroup][numberOfPlayersInGroup];
        for (int i = 0; i < numberOfPlayersInGroup; i++) {
            costs[i][i] = 0;
            for (int j = i + 1; j < numberOfPlayersInGroup; j++) {
                Player p1 = alGroupedPlayers.get(i);
                Player p2 = alGroupedPlayers.get(j);
                ScoredPlayer sP1 = hmScoredPlayers.get(p1.getKeyString());
                ScoredPlayer sP2 = hmScoredPlayers.get(p2.getKeyString());
                costs[i][j] = costs[j][i] = costValue(sP1, sP2, roundNumber, alPreviousGames);
//                String sCost = Gotha.formatLongNumberBy3digits(costs[i][j]);
//                System.out.println("i = " + i + " j = " + j + " sCost = " +  sCost + " p1 = " + p1.getName() + " p2 = " + p2.getName());
            }
        }

        // match
        int[] mate = WeightedMatchLong.weightedMatchLong(costs, WeightedMatchLong.MAXIMIZE);

        ArrayList<Game> alG = new ArrayList<Game>();
        // define the games
        for (int i = 1; i <= costs.length; i++) {
            if (i < mate[i]) {
                Player p1 = alGroupedPlayers.get(i - 1);
                Player p2 = alGroupedPlayers.get(mate[i] - 1);
                ScoredPlayer sP1 = hmScoredPlayers.get(p1.getKeyString());
                ScoredPlayer sP2 = hmScoredPlayers.get(p2.getKeyString());

                Game g = gameBetween(sP1, sP2, roundNumber, alPreviousGames);
                alG.add(g);
            }
        }
        return alG;
    }

    private long costValue(ScoredPlayer sP1, ScoredPlayer sP2, int roundNumber, ArrayList<Game> alPreviousGames) {
        GeneralParameterSet gps = tournamentParameterSet.getGeneralParameterSet();
        PairingParameterSet paiPS = tournamentParameterSet.getPairingParameterSet();

        long cost = 1L;   // 1 is minimum value because 0 means "no matching allowed"

        // Base Criterion 1 : Avoid Duplicating Game
        // Did p1 and p2 already play ?
        //
        int numberOfPreviousGamesP1P2 = 0;

        for (int r = 0; r < roundNumber; r++) {
            Game g1 = sP1.getGame(r);
            if (g1 == null) {
                continue;
            }
            if (sP1.hasSameKeyString(g1.getWhitePlayer()) && sP2.hasSameKeyString(g1.getBlackPlayer())) {
                numberOfPreviousGamesP1P2++;
            }
            if (sP1.hasSameKeyString(g1.getBlackPlayer()) && sP2.hasSameKeyString(g1.getWhitePlayer())) {
                numberOfPreviousGamesP1P2++;
            }
        }
        if (numberOfPreviousGamesP1P2 == 0) {
            cost += paiPS.getPaiBaAvoidDuplGame();
        }

        // Base Criterion 2 : Random
        long nR;
        if (paiPS.isPaiBaDeterministic()) {
            nR = Pairing.detRandom(paiPS.getPaiBaRandom(), sP1, sP2);
        } else {
            nR = Pairing.nonDetRandom(paiPS.getPaiBaRandom());
        }
        cost += nR;

        // Base Criterion 3 : Balance W and B
        // This cost is never applied if potential Handicap != 0
        // It is fully applied if wbBalance(sP1) and wbBalance(sP2) are strictly of different signs
        // It is half applied if one of wbBalance is 0 and the other is >=2

        long bwBalanceCost = 0;
        Game g = gameBetween(sP1, sP2, roundNumber, alPreviousGames);
        int potHd = g.getHandicap();
        if (potHd == 0) {
            int wb1 = Pairing.wbBalance(sP1, roundNumber - 1);
            int wb2 = Pairing.wbBalance(sP2, roundNumber - 1);
            if (wb1 * wb2 < 0) {
                bwBalanceCost = paiPS.getPaiBaBalanceWB();
            } else if (wb1 == 0 && Math.abs(wb2) >= 2) {
                bwBalanceCost = paiPS.getPaiBaBalanceWB() / 2;
            } else if (wb2 == 0 && Math.abs(wb1) >= 2) {
                bwBalanceCost = paiPS.getPaiBaBalanceWB() / 2;
            }
        }
        cost += bwBalanceCost;


        // Main Criterion 1 : Avoid mixing categories
        long catCost = 0;
        int numberOfCategories = gps.getNumberOfCategories();
        if (numberOfCategories > 1) {
            // cost is f(x) = (1-x) * (1 + kx) where  0<=x<=1 and k is the NX1 factor 0<=k<=1
            double x = (double) Math.abs(sP1.category(gps) - sP2.category(gps)) / (double) numberOfCategories;
            double k = paiPS.getPaiStandardNX1Factor();
            catCost = (long) (paiPS.getPaiMaAvoidMixingCategories() * (1.0 - x) * (1.0 + k * x));
            // But if both players have lost 1 or more games, that is less important (added in 3.11)
        }

        cost += catCost;

        // Main Criterion 2 : Minimize score difference
        long scoCost = 0;
        int scoRange = sP1.numberOfGroups;
        if (sP1.category(gps) == sP2.category(gps)) {
            double x = (double) Math.abs(sP1.groupNumber - sP2.groupNumber) / (double) scoRange;
            double k = paiPS.getPaiStandardNX1Factor();
            scoCost = (long) (paiPS.getPaiMaMinimizeScoreDifference() * (1.0 - x) * (1.0 + k * x));
        }
        cost += scoCost;

        // Main Criterion 3 : If different groups, make a directed Draw-up/Draw-down
        // Modifs V3.44.05 (ideas from Tuomo Salo)
        long duddCost = 0;
        if (Math.abs(sP1.groupNumber - sP2.groupNumber) < 4
                && sP1.groupNumber != sP2.groupNumber) {
            // 5 scenarii
            // scenario = 0 : Both players have already been drawn in the same sense
            // scenario = 1 : One of the players has already been drawn in the same sense           
            // scenario = 2 : Normal conditions (does not correct anything and no previous drawn in the same sense)
            //                This case also occurs if one DU/DD is increased, while one is compensated
            // scenario = 3 : It corrects a previous DU/DD            //        
            // scenario = 4 : it corrects a previous DU/DD for both
            int scenario = 2;
            if (sP1.nbDU > 0 && sP1.groupNumber > sP2.groupNumber) {
                scenario--;
            }
            if (sP1.nbDD > 0 && sP1.groupNumber < sP2.groupNumber) {
                scenario--;
            }
            if (sP2.nbDU > 0 && sP2.groupNumber > sP1.groupNumber) {
                scenario--;
            }
            if (sP2.nbDD > 0 && sP2.groupNumber < sP1.groupNumber) {
                scenario--;
            }

            if (scenario != 0 && sP1.nbDU > 0 && sP1.nbDD < sP1.nbDU && sP1.groupNumber < sP2.groupNumber) {
                scenario++;
            }
            if (scenario != 0 && sP1.nbDD > 0 && sP1.nbDU < sP1.nbDD && sP1.groupNumber > sP2.groupNumber) {
                scenario++;
            }
            if (scenario != 0 && sP2.nbDU > 0 && sP2.nbDD < sP2.nbDU && sP2.groupNumber < sP1.groupNumber) {
                scenario++;
            }
            if (scenario != 0 && sP2.nbDD > 0 && sP2.nbDU < sP2.nbDD && sP2.groupNumber > sP1.groupNumber) {
                scenario++;
            }

            
            long duddWeight = paiPS.getPaiMaDUDDWeight() / 5;

            ScoredPlayer upperSP = (sP1.groupNumber < sP2.groupNumber) ? sP1 : sP2;
            ScoredPlayer lowerSP = (sP1.groupNumber < sP2.groupNumber) ? sP2 : sP1;
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_TOP) {
                duddCost += duddWeight / 2 * (upperSP.groupSize - 1 - upperSP.innerPlacement) / upperSP.groupSize;
            } else if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_MID) {
                duddCost += duddWeight / 2 * (upperSP.groupSize - 1 - Math.abs(2 * upperSP.innerPlacement - upperSP.groupSize + 1)) / upperSP.groupSize;
            } else if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_BOT) {
                duddCost += duddWeight / 2 * (upperSP.innerPlacement) / upperSP.groupSize;
            }
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_TOP) {
                duddCost += duddWeight / 2 * (lowerSP.groupSize - 1 - lowerSP.innerPlacement) / lowerSP.groupSize;
            } else if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_MID) {
                duddCost += duddWeight / 2 * (lowerSP.groupSize - 1 - Math.abs(2 * lowerSP.innerPlacement - lowerSP.groupSize + 1)) / lowerSP.groupSize;
            } else if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_BOT) {
                duddCost += duddWeight / 2 * (lowerSP.innerPlacement) / lowerSP.groupSize;
            }

            if (scenario == 0) {            
                // Do nothing
            }
            else if (scenario == 1 ){
                duddCost += 1 * duddWeight;
            }
            else if (scenario == 2 || (scenario > 2 && !paiPS.isPaiMaCompensateDUDD())) {
                duddCost += 2 * duddWeight;
            }
            else if (scenario == 3) {
                duddCost += 3 * duddWeight;
            }
            else if (scenario == 4) {
                duddCost += 4 * duddWeight;
            }
            
        }
        // But, if players come from different categories, decrease duddCost(added in 3.11)
        int catGap = Math.abs(sP1.category(gps) - sP2.category(gps));
        duddCost = duddCost / (catGap + 1) / (catGap + 1) / (catGap + 1) / (catGap + 1);

        cost += duddCost;

        // Main Criterion 4 : Seeding
        long seedCost = 0;
        if (sP1.groupNumber == sP2.groupNumber) {
            int groupSize = sP1.groupSize;
            int cla1 = sP1.innerPlacement;
            int cla2 = sP2.innerPlacement;
            long maxSeedingWeight = paiPS.getPaiMaMaximizeSeeding();
            int currentSeedSystem = (roundNumber <= paiPS.getPaiMaLastRoundForSeedSystem1()) ? paiPS.getPaiMaSeedSystem1() : paiPS.getPaiMaSeedSystem2();
            if (currentSeedSystem == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM) {
                if ((2 * cla1 < groupSize && 2 * cla2 >= groupSize) || (2 * cla1 >= groupSize && 2 * cla2 < groupSize)) {
                    long randRange = (long) (paiPS.getPaiMaMaximizeSeeding() * 0.2);
                    long rand = Pairing.detRandom(randRange, sP1, sP2);
                    seedCost = maxSeedingWeight - rand;
                }
            } else if (currentSeedSystem == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD) {
                // The best is to get cla1 + cla2 - (groupSize - 1) close to 0
                int x = cla1 + cla2 - (groupSize - 1);
                seedCost = maxSeedingWeight - (maxSeedingWeight * x / (groupSize - 1) * x / (groupSize - 1));
            } else if (currentSeedSystem == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP) {
                // The best is to get 2 * |Cla1 - Cla2| - groupSize    close to 0
                int x = 2 * Math.abs(cla1 - cla2) - groupSize;
                seedCost = maxSeedingWeight - (maxSeedingWeight * x / groupSize * x / groupSize);
            } else {
                System.out.println("Internal Error on seed system");
            }
        }
        cost += seedCost;

        // Secondary Criteria
        // Do we apply ?
        // secCase = 0 : No player is above thresholds
        // secCase = 1 : One player is above thresholds
        // secCase = 2 : Both players are above thresholds
        // pseudoMMS is MMS adjusted according to applying thresholds

        int secCase = 0;
        int nbw2Threshold;
        if (paiPS.isPaiSeNbWinsThresholdActive()) {
            nbw2Threshold = gps.getNumberOfRounds();
        } else {
            nbw2Threshold = 2 * gps.getNumberOfRounds();
        }
        
        int mmBar = gps.getGenMMBar() - Gotha.MIN_RANK;

        int pseudoMMSSP1 = sP1.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, roundNumber - 1) / 2;
        int pseudoMMSSP2 = sP2.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, roundNumber - 1) / 2;
        int maxMMS = gps.getGenMMBar() + PlacementParameterSet.PLA_SMMS_CORR_MAX - Gotha.MIN_RANK + roundNumber;

        int nbwSP1X2 = sP1.getCritValue(PlacementParameterSet.PLA_CRIT_NBW, roundNumber - 1);
        int nbwSP2X2 = sP2.getCritValue(PlacementParameterSet.PLA_CRIT_NBW, roundNumber - 1);

        boolean bStrongMMS = (2 * sP1.getRank() + sP1.getCritValue(PlacementParameterSet.PLA_CRIT_NBW, roundNumber - 1) >= 2 * paiPS.getPaiSeRankThreshold());
        boolean bManyWins = nbwSP1X2 >= nbw2Threshold;
        boolean bAboveMMBar = (sP1.smms(gps) >= mmBar && paiPS.isPaiSeBarThresholdActive());
        if (bManyWins
                || bStrongMMS 
                || bAboveMMBar) {
            secCase++;
            pseudoMMSSP1 = maxMMS;
        }
        
        bStrongMMS = (2 * sP2.getRank() + sP2.getCritValue(PlacementParameterSet.PLA_CRIT_NBW, roundNumber - 1) >= 2 * paiPS.getPaiSeRankThreshold());
        bManyWins = nbwSP2X2 >= nbw2Threshold;
        bAboveMMBar = (sP2.smms(gps) >= mmBar && paiPS.isPaiSeBarThresholdActive());
        if (bManyWins
                || bStrongMMS 
                || bAboveMMBar) {
            secCase++;
            pseudoMMSSP2 = maxMMS;
        }
        
        // Secondary Criterion 1 : Minimize handicap
        long hdCost = 0;
        int secRange;   // secRange is the maximum score difference between 2 players for this tournament
        int tType = TournamentParameterSet.TYPE_UNDEFINED;
        try {
            tType = tournamentType();
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tType == TournamentParameterSet.TYPE_MCMAHON) {
            secRange = scoRange;
        } else {
            secRange = (gps.getGenMMBar() - gps.getGenMMFloor() + PlacementParameterSet.PLA_SMMS_CORR_MAX - PlacementParameterSet.PLA_SMMS_CORR_MIN) + roundNumber;
        }

        double x = (double) Math.abs(pseudoMMSSP1 - pseudoMMSSP2) / (double) secRange;
        double k = paiPS.getPaiStandardNX1Factor();
        hdCost = (long) (paiPS.getPaiSeMinimizeHandicap() * (1.0 - x) * (1.0 + k * x));

        cost += hdCost;

        // Secondary criteria 2,3 and 4 : Geographical Criteria
        long geoMaxCost = paiPS.getPaiSeAvoidSameGeo();

        int countryFactor = paiPS.getPaiSePreferMMSDiffRatherThanSameCountry();
        int clubFactor = paiPS.getPaiSePreferMMSDiffRatherThanSameClub();
//        int groupFactor = paiPS.getPaiSePreferMMSDiffRatherThanSameClubsGroup();
        
        double countryRatio = 0.0;
        
        boolean bCommonCountry = false;
        try {
            bCommonCountry = this.playersAreInCommonCountry(sP1, sP2);
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!bCommonCountry){
            if (countryFactor == 0) countryRatio = 0.0;
            else{
                countryRatio = ((double) countryFactor) / (double) scoRange;
                if (countryRatio > 1.0) countryRatio = 1.0;
            }
        }                      

        double clubRatio = 0.0;

        boolean bCommonGroup = false;
        boolean bCommonClub = false;
        try {
            bCommonGroup = playersAreInCommonGroup(sP1, sP2);
            bCommonClub = playersAreInCommonClub(sP1, sP2);
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(bCommonGroup && !bCommonClub){
            if (clubFactor == 0) clubRatio = 0.0;
            else{
                clubRatio = (double) clubFactor /2.0 / (double) scoRange;    
            }  
        }
        if(!bCommonGroup && !bCommonClub){
            if (clubFactor == 0) clubRatio = 0.0;
            else{   
              clubRatio = (double) clubFactor * 1.2 / (double) scoRange;
            }
        }
        if ( clubRatio > 1.0) clubRatio = 1.0;
        
        // compute geoRatio
         double mainPart = Math.max(countryRatio, clubRatio);
         double secPart = Math.min(countryRatio, clubRatio);
         double geoRatio = mainPart + secPart / 2.0; 
                 if (geoRatio > 0.0) geoRatio += 0.5 / (double) scoRange;
         
         // The concavity function is applied to geoRatio to get geoCost
         double dbGeoCost =  (double) geoMaxCost * (1.0 - geoRatio) * (1.0 + k * geoRatio);
         long geoCost = paiPS.getPaiMaMinimizeScoreDifference() - (long) dbGeoCost;
         if (geoCost > geoMaxCost) geoCost = geoMaxCost;

        cost += geoCost;

        return cost;
    }

    /**
     * builds and return a new Game with everything defined except tableNumber
     */
    private Game gameBetween(ScoredPlayer sP1, ScoredPlayer sP2, int roundNumber, ArrayList<Game> alPreviousGames) {

        HandicapParameterSet hdPS = tournamentParameterSet.getHandicapParameterSet();

        Game g = new Game();

        // handicap
        int hd = 0;
        int pseudoRank1 = sP1.getRank();
        int pseudoRank2 = sP2.getRank();
        if (hdPS.isHdBasedOnMMS()) {
            pseudoRank1 = sP1.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, roundNumber - 1) / 2 + Gotha.MIN_RANK;
            pseudoRank2 = sP2.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, roundNumber - 1) / 2 + Gotha.MIN_RANK;
        }
        pseudoRank1 = Math.min(pseudoRank1, hdPS.getHdNoHdRankThreshold());
        pseudoRank2 = Math.min(pseudoRank2, hdPS.getHdNoHdRankThreshold());
        hd = pseudoRank1 - pseudoRank2;

        if (hd > 0) {
            hd = hd - hdPS.getHdCorrection();
            if (hd < 0) {
                hd = 0;
            }
        }
        if (hd < 0) {
            hd = hd + hdPS.getHdCorrection();
            if (hd > 0) {
                hd = 0;
            }
        }
        if (hd > hdPS.getHdCeiling()) {
            hd = hdPS.getHdCeiling();
        }
        if (hd < -hdPS.getHdCeiling()) {
            hd = -hdPS.getHdCeiling();
        }
        Player p1 = null;
        Player p2 = null;
        try {
            p1 = getPlayerByKeyString(sP1.getKeyString());
            p2 = getPlayerByKeyString(sP2.getKeyString());
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (hd > 0) {
            g.setWhitePlayer(p1);
            g.setBlackPlayer(p2);
            g.setHandicap(hd);
        } else if (hd < 0) {
            g.setWhitePlayer(p2);
            g.setBlackPlayer(p1);
            g.setHandicap(-hd);
        } else { // hd == 0
            g.setHandicap(0);
            if (Pairing.wbBalance(sP1, roundNumber - 1) > Pairing.wbBalance(sP2, roundNumber - 1)) {
                g.setWhitePlayer(p2);
                g.setBlackPlayer(p1);
            } else if (Pairing.wbBalance(sP1, roundNumber - 1) < Pairing.wbBalance(sP2, roundNumber - 1)) {
                g.setWhitePlayer(p1);
                g.setBlackPlayer(p2);
            } else { // choose color from a det random
                if (Pairing.detRandom(1L, sP1, sP2) == 0) {
                    g.setWhitePlayer(p1);
                    g.setBlackPlayer(p2);
                } else {
                    g.setWhitePlayer(p2);
                    g.setBlackPlayer(p1);
                }
            }
        }
        g.setKnownColor(true);
        g.setResult(Game.RESULT_UNKNOWN);
        g.setRoundNumber(roundNumber);

        return g;
    }

    @Override
    public void setByePlayer(Player p, int roundNumber) throws RemoteException {
        Player player = this.homonymPlayerOf(p);
        byePlayers[roundNumber] = player;
    }
    
    @Override
    public void chooseAByePlayer(ArrayList<Player> alPlayers, int roundNumber) throws RemoteException {

        // The weight allocated to each player is 1000 * number of previous byes + rank
        // The chosen player will be the player with the minimum weight
        Player bestPlayerForBye = null;
        int minWeight = 1000 * (Gotha.MAX_NUMBER_OF_ROUNDS - 1) + 38 + 1; // Nobody can have such a weight neither more

        for (Player p : alPlayers) {
            int weightForBye = p.getRank() + this.mms2(p, roundNumber);
            for (int r = 0; r < roundNumber; r++) {
                if (byePlayers[r] == null) {
                    continue;
                }
                if (p.hasSameKeyString(byePlayers[r])) {
                    weightForBye += 1000;
                }
            }
            if (weightForBye <= minWeight) {
                minWeight = weightForBye;
                bestPlayerForBye = p;
            }
        }
        byePlayers[roundNumber] = bestPlayerForBye;
    }

    @Override
    public void assignByePlayer(Player p, int roundNumber) throws RemoteException {
        byePlayers[roundNumber] = p;
        this.setChangeSinceLastSave(true);
    }

    @Override
    public void unassignByePlayer(int roundNumber) throws RemoteException {
        byePlayers[roundNumber] = null;
        this.setChangeSinceLastSave(true);
    }
    
    @Override
    public void renumberTablesByBestScoreThenRating(int roundNumber, ArrayList<Game> alGamesToRenumber) throws RemoteException{
        fillBaseScoringInfoIfNecessary();
        PlacementParameterSet pps = this.getTournamentParameterSet().getPlacementParameterSet();
        Collections.sort(alGamesToRenumber, new GameComparator(GameComparator.BEST_SCR_ORDER, hmScoredPlayers, pps));

        // Remove games from hmGames
        for (Game g : alGamesToRenumber) {
            try {
                removeGame(g);
            } catch (RemoteException | TournamentException ex) {
                Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ArrayList<Game> alExistingGames = this.gamesList(roundNumber);
        int tN = -1;
        for (Game g : alGamesToRenumber) {
            boolean bTableFound = false;
            while (!bTableFound) {
                tN++;
                boolean bTNOK = true;
                for (Game oldG : alExistingGames) {
                    if (oldG.getTableNumber() == tN) {
                        bTNOK = false;
                    }
                }
                if (bTNOK) {
                    g.setTableNumber(tN);
                    bTableFound = true;
                    try {
                        this.addGame(g);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (TournamentException ex) {
                        Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        }
        this.setChangeSinceLastSave(true);        
    }

    @Override
    public void setResult(Game g, int result) throws RemoteException {
        Game game = this.getGame(g.getRoundNumber(), g.getTableNumber());
        game.setResult(result);
        this.setChangeSinceLastSave(true);
    }
    
    @Override
    public void setRoundNumber(Game g, int rn) throws RemoteException {
        Game game = this.getGame(g.getRoundNumber(), g.getTableNumber());
        game.setRoundNumber(rn);
        this.setChangeSinceLastSave(true);
    }

    /**
     * The first round with at least one result unknown is presumed to be the
     * current round. But, whatever happens the returned value is >=0 and <=
     * roundNumber -1
     */
    @Override
    public int presumablyCurrentRoundNumber() throws RemoteException {
        int totalNumberOfPlayers = this.numberOfPlayers();
        if (totalNumberOfPlayers == 0) {
            return 0;
        }
        int presCurrentRoundNumber = -1;
        int numberOfRounds = getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        for (int r = 0; r < numberOfRounds; r++) {
            int numberOfNotParticipatingPlayers = 0;
            for (Player p : hmPlayers.values()) {
                if (!p.getParticipating()[r]) {
                    numberOfNotParticipatingPlayers++;
                }
            }
            int numberOfPlayersImpliedInAGame = 2 * this.gamesList(r).size();
            int numberOfByePlayers = 0;
            if (this.getByePlayer(r) != null) {
                numberOfByePlayers = 1;
            }
            if (numberOfNotParticipatingPlayers + numberOfPlayersImpliedInAGame + numberOfByePlayers < totalNumberOfPlayers) {
                presCurrentRoundNumber = r;
                break;
            }

            for (Game g : this.gamesList(r)) {
                if (g.getResult() == Game.RESULT_UNKNOWN) {
                    presCurrentRoundNumber = r;
                    break;
                }
            }
            if (presCurrentRoundNumber != -1) {
                break;
            }
        }
        if (presCurrentRoundNumber < 0) {
            presCurrentRoundNumber = numberOfRounds - 1;
        }
        if (presCurrentRoundNumber > numberOfRounds - 1) {
            presCurrentRoundNumber = numberOfRounds - 1;
        }

        return presCurrentRoundNumber;
    }

    @Override
    public int getTeamSize() throws RemoteException {
        return getTeamTournamentParameterSet().getTeamGeneralParameterSet().getTeamSize();
    }
    
    @Override
    public void setTeamSize(int teamSize) throws RemoteException {
        int oldTS = this.getTeamSize();
        if (teamSize == oldTS) {
            return;
        }
        if (teamSize > Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM) {
            return;
        }
        if (teamSize < Gotha.MIN_NUMBER_OF_MEMBERS_BY_TEAM) {
            return;
        }
        this.getTeamTournamentParameterSet().getTeamGeneralParameterSet().setTeamSize(teamSize);
        this.setChangeSinceLastSave(true);
    }

    @Override
    public boolean addTeam(Team t) throws RemoteException {
        // Is the asked name available ?
        String str = t.getTeamName();
        if (hmTeams.get(str) != null) {
            str = findAnAvailableTeamName(str);
        }
        t.setTeamName(str);

        int tn = t.getTeamNumber();
        if ((tn <= 0) || getTeam(tn) != null) {
            tn = findAnAvailableTeamNumber();
        }
        t.setTeamNumber(tn);

        String strKey = t.getTeamName();
        if (hmTeams == null) {
            hmTeams = new HashMap<String, Team>();
        }
        hmTeams.put(strKey, t);
        this.setChangeSinceLastSave(true);
        return true;
    }

    private int findAnAvailableTeamNumber() {
        int tN = 0;
        while (getTeam(tN) != null) {
            tN++;
        }
        return tN;
    }

    private String findAnAvailableTeamName(String strInit) {
        String str = strInit;
        if (hmTeams.get(str) == null) {
            return str;
        }
        for (int i = 0; i <= Gotha.MAX_NUMBER_OF_TEAMS; i++) {
            str = strInit + "_" + (i + 2);
            if (hmTeams.get(str) == null) {
                return str;
            }
        }
        return ""; // This should never happen;
    }

    private Team getTeam(int tN) {
        if (hmTeams == null) {
            hmTeams = new HashMap<String, Team>();
        }
        for (Team t : hmTeams.values()) {
            if (t.getTeamNumber() == tN) {
                return t;
            }
        }
        return null;
    }

    @Override
    public boolean removeTeam(Team t) throws RemoteException {
        if (hmTeams == null) {
            return false;
        }
        if (hmTeams.remove(t.getTeamName()) == null) {
            return false;
        }
        this.setChangeSinceLastSave(true);
        return true;
    }

    @Override
    public void removeAllTeams() throws RemoteException {
        if (hmTeams != null) {
            hmTeams.clear();
            this.setChangeSinceLastSave(true);
        }
    }

    @Override
    public Team getTeamByName(String name) throws RemoteException {
        return hmTeams.get(name);
    }

    @Override
    public Team getTeamOfPlayer(Player player, int roundNumber) throws RemoteException {
        for (Team team : this.teamsList()) {
            for (int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++) {
                Player p = team.getTeamMember(roundNumber, ib);
                if (p == null) {
                    continue;
                }
                if (p.hasSameKeyString(player)) {
                    return team;
                }
            }
        }
        return null;

    }

    @Override
    public void setTeamMember(Team team, int roundNumber, int boardMember, Player player) throws RemoteException {
        Team teamToModify = hmTeams.get(team.getTeamName());

//        teamToModify.setTeamMember(player, roundNumber, boardMember);
        Player p = this.homonymPlayerOf(player);
        teamToModify.setTeamMember(p, roundNumber, boardMember);
        this.setChangeSinceLastSave(true);
    }

    @Override
    public void modifyTeamName(Team team, String newName) throws RemoteException {
        Team teamToModify = hmTeams.get(team.getTeamName());
        removeTeam(teamToModify);
        if (hmTeams.get(newName) != null) {
            newName = findAnAvailableTeamName(newName);
        }
        teamToModify.setTeamName(newName);
        addTeam(team);
        this.setChangeSinceLastSave(true);
    }

    @Override
    public void unteamTeamMember(Team team, int roundNumber, int boardNumber) throws RemoteException {
        Team teamToModify = hmTeams.get(team.getTeamName());
        teamToModify.setTeamMember(null, roundNumber, boardNumber);
        this.setChangeSinceLastSave(true);
    }

    @Override
    public void unteamTeamMembers(Team team, int roundNumber) throws RemoteException {
        int teamSize = this.getTeamSize();
        for (int bn = 0; bn < teamSize; bn++) {
            this.unteamTeamMember(team, roundNumber, bn);
        }
    }

    @Override
    public void unteamAllTeams(int roundNumber) throws RemoteException {
        for (Team team : hmTeams.values()) {
            unteamTeamMembers(team, roundNumber);
        }
    }

    @Override
    public void cleanTeams() throws RemoteException{
        int teamSize = this.getTeamTournamentParameterSet().getTeamGeneralParameterSet().getTeamSize();
            for (Team team : hmTeams.values()) {
                for (int bn = teamSize; bn < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; bn++){
                    for (int ir = 0; ir < Gotha.MAX_NUMBER_OF_ROUNDS; ir++){
                        this.unteamTeamMember(team, ir, bn);
                    }                   
                }
        }

    }

    @Override
    public void reorderTeamMembersByRating(Team team, int roundNumber) throws RemoteException {
        ArrayList<Player> alPlayers = new ArrayList<Player>();
        int teamSize = this.getTeamSize();
        for (int iTM = 0; iTM < teamSize; iTM++) {
            Player p = team.getTeamMember(roundNumber, iTM);
            if (p != null) {
                alPlayers.add(p);
            }
        }
        PlayerComparator playerComparator = new PlayerComparator(PlayerComparator.RATING_ORDER);
        Collections.sort(alPlayers, playerComparator);
        for (int bn = 0; bn < alPlayers.size(); bn++) {
            this.setTeamMember(team, roundNumber, bn, alPlayers.get(bn));
        }
        for (int bn = alPlayers.size(); bn < teamSize; bn++) {
            this.setTeamMember(team, roundNumber, bn, null);
        }
        this.setChangeSinceLastSave(true);
    }

    @Override
    public void reorderTeamMembersByRating(int roundNumber) throws RemoteException {
        for (Team team : hmTeams.values()) {
            this.reorderTeamMembersByRating(team, roundNumber);
        }
        this.setChangeSinceLastSave(true);
    }

    @Override
    public void renumberTeamsByTotalRating() throws RemoteException {
        ArrayList<Team> alTeams = teamsList();
        int teamSize = getTeamSize();
        TeamComparator teamComparator = new TeamComparator(TeamComparator.TOTAL_RATING_ORDER, teamSize);
        Collections.sort(alTeams, teamComparator);
        for (int tn = 0; tn < alTeams.size(); tn++) {
            Team t = alTeams.get(tn);
            t.setTeamNumber(tn);
        }
        this.setChangeSinceLastSave(true);
    }

    @Override
    public boolean isTeamComplete(Team team, int roundNumber) throws RemoteException {
        Team t = hmTeams.get(team.getTeamName());
        int teamSize = this.getTeamSize();
        for (int ib = 0; ib < teamSize; ib++) {
            // New in V3.29.03 : each player must also be "Final" and participant
            Player p = t.getTeamMember(roundNumber, ib);
            if (p == null) {
                return false;
            }
            if (!p.getRegisteringStatus().equals("FIN")){
                return false;
            }
            if (!p.getParticipating(roundNumber)){
                return false;
            }
        }
        return true;

    }

    @Override
    public ArrayList<Game> incoherentTeamGames() throws RemoteException {
        ArrayList<Game> alIncohGames = new ArrayList<Game>();
        
        return alIncohGames;
    }

    @Override
    public Team opponentTeam(Team team, int roundNumber) throws RemoteException {
        Player player0 = team.getTeamMember(roundNumber, 0);
        Player opp0 = null;
        // Find opp0;
        ArrayList<Game> alG = this.gamesPlayedBy(player0);
        for (Game g : alG) {
            if (g.getRoundNumber() == roundNumber) {
                opp0 = this.opponent(g, player0);
                break;
            }
        }
        // What team for opp0 ?
        for (Team tOpp : hmTeams.values()) {
            if (tOpp.getTeamMember(roundNumber, 0).hasSameKeyString(opp0)) {
                return tOpp;
            }
        }
        return null;
    }

    @Override
    public int nbWX2Team(Team team, Team opponentTeam, int roundNumber) throws RemoteException {
        int nbWX2 = 0;
        for (int ib = 0; ib < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ib++) {
            Player p1 = team.getTeamMember(roundNumber, ib);
            if (p1 == null) {
                break;
            }
            Player p2 = opponentTeam.getTeamMember(roundNumber, ib);
            if (p2 == null) {
                break;
            }
            ArrayList<Game> alG = this.gamesPlayedBy(p1, p2);
            for (Game g : alG) {
                if (g.getRoundNumber() == roundNumber) {
                    nbWX2 += this.getWX2(g, p1);
                    break;
                }
            }
        }
        return nbWX2;

    }

    /**
     * returns players implied in a team at boardNumber board
     * 
     */
    @Override
    public ArrayList<Player> playersList(Team team, int boardNumber) throws RemoteException {
        ArrayList<Player> alP = new ArrayList<Player>();
        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++){
            Player p = team.getTeamMember(r, boardNumber);
            if (p == null) continue;
            if (!alP.contains(p)) alP.add(p);
        }
        return alP;
    }
    
    @Override
    public boolean[] membership(Player p, Team t, int boardNumber) throws RemoteException {
        boolean[] bM = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++){
            bM[r] = false;
            if (p == null) continue;
            if (t == null) continue;
            if (p.hasSameKeyString(t.getTeamMember(r, boardNumber))) bM[r] = true;
        }
        return bM;
    }

    /**
     * Returns the total number of players
     */
    @Override
    public int numberOfTeams() throws RemoteException {
        if (hmTeams == null) {
            return 0;
        } else {
            return hmTeams.size();
        }
    }

    @Override
    public ArrayList<Team> teamsList() throws RemoteException {
        if (hmTeams == null) {
            hmTeams = new HashMap<String, Team>();
        }
        return new ArrayList<Team>(hmTeams.values());
    }

    @Override
    public HashMap<String, Player> teamablePlayersHashMap(int roundNumber) throws RemoteException {
        HashMap<String, Player> hmTeamablePlayers = new HashMap<String, Player>(hmPlayers);
        int teamSize = getTeamSize();
        for (Team t : teamsList()) {
            for (int iTM = 0; iTM < teamSize; iTM++) {
                Player p = t.getTeamMember(roundNumber, iTM);
                if (p != null) {
                    hmTeamablePlayers.remove(p.getKeyString());
                }
            }

        }
        return hmTeamablePlayers;
    }

    @Override
    public ArrayList<Match> matchesList(int roundNumber) throws RemoteException {
        ArrayList<Match> alMatches = new ArrayList<Match>();

        ArrayList<Team> alTeams = teamsList();
        for (Team team : alTeams) {
            Player player0 = team.getTeamMember(roundNumber, 0);
            ArrayList<Game> alG = gamesPlayedBy(player0);
            for (Game game : alG) {
                if (game.getRoundNumber() != roundNumber) {
                    continue;
                }
                Player opponent0 = opponent(game, player0);
                Team oppTeam = this.getTeamOfPlayer(opponent0, roundNumber);
                if (oppTeam == null) {
                    continue;
                }
                if (team.getTeamName().compareTo(oppTeam.getTeamName()) < 0) { // Avoid double creation

                    Match match = Match.buildMatch(game.getRoundNumber(), team, oppTeam, this);
                    if (match != null) {
                        alMatches.add(match);
                    }
                }
            }
        }
        return alMatches;
    }

    @Override
    public ArrayList<Match> matchesListUpTo(int roundNumber) throws RemoteException {
        ArrayList<Match> alMatches = new ArrayList<Match>();

        ArrayList<Team> alTeams = teamsList();
        for (Team team : alTeams) {
            // V3.28.04
            ArrayList<Game> alG = new ArrayList<Game>();
            for (int r = 0; r <= roundNumber; r++){
                Player player0 = team.getTeamMember(r, 0);
                Game game = this.getGame(r, player0);
                Player opponent0 = opponent(game, player0);
                Team oppTeam = this.getTeamOfPlayer(opponent0, r);
                if (oppTeam == null) {
                    continue;
                }
                if (team.getTeamName().compareTo(oppTeam.getTeamName()) < 0) { // Avoid double creation
                    Match match = Match.buildMatch(game.getRoundNumber(), team, oppTeam, this);
                    if (match != null) {
                        alMatches.add(match);
                    }
                }
            }
        }
        
        return alMatches;
    }

    /**
     * Detects what team is concerned by (round number and table number) for
     * board 0 and builds a Match object.
     *
     * @param roundNumber
     * @param tableNumber table Number of first board (board 0);
     * @return the Match object or null if no Match object could be built
     * @throws RemoteException
     */
    @Override
    public Match getMatch(int roundNumber, int tableNumber) throws RemoteException {
        Match match = null;
        Game g0 = this.getGame(roundNumber, tableNumber);
        Player wp0 = g0.getWhitePlayer();
        Player bp0 = g0.getBlackPlayer();
        Team wt = this.getTeamOfPlayer(wp0, roundNumber);
        Team bt = this.getTeamOfPlayer(bp0, roundNumber);
        match = Match.buildMatch(roundNumber, wt, bt, this);

        return match;
    }

    @Override
    public void pairTeams(Team team0, Team team1, int roundNumber) throws RemoteException {
        int teamSize = getTeamSize();
        // First, remove games of concerned players for roundNumber round if any
        for (int ib = 1; ib < teamSize; ib++) {
            Player p0 = team0.getTeamMember(roundNumber, ib);
            Player p1 = team1.getTeamMember(roundNumber, ib);
            Game g0 = this.getGame(roundNumber, p0);
            Game g1 = this.getGame(roundNumber, p1);
            try {
                this.removeGame(g0);
                this.removeGame(g1);
            } catch (TournamentException ex) {
                Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

            // Color ?
        Player pt0 = team0.getTeamMember(roundNumber, 0);
        Player pt1 = team1.getTeamMember(roundNumber, 0);
        int wbBalance0 = 0;
        int wbBalance1 = 0;

        for (int r = 0; r <= roundNumber - 1; r++) {
            Game g0 = this.getGame(r, pt0);
            if (g0 != null && g0.getHandicap() == 0 && g0.isKnownColor()) {
                if (pt0.hasSameKeyString(g0.getWhitePlayer())) {
                    wbBalance0++;
                } else {
                    wbBalance0--;
                }
            }
            Game g1 = this.getGame(r, pt1);
            if (g1 != null && g1.getHandicap() == 0 && g1.isKnownColor()) {
                if (pt1.hasSameKeyString(g1.getWhitePlayer())) {
                    wbBalance1++;
                } else {
                    wbBalance1--;
                }
            }
        }
        boolean pt0IsWhite;
        if (wbBalance0 > wbBalance1) {
            pt0IsWhite = false;
        } else if (wbBalance0 < wbBalance1) {
            pt0IsWhite = true;
        } else { // choose color from a det random
            if (Pairing.detRandom(1L, pt0, pt1) == 0) {
                pt0IsWhite = true;
            } else {
                pt0IsWhite = false;
            }
        }

//        Game game = new Game(roundNumber, -1, null, null, true, 0, Game.RESULT_UNKNOWN);
        // Modif 3.31.01 for hd team tournaments
        ArrayList<Player> alP = new ArrayList<Player>();
        alP.add(pt0);
        alP.add(pt1);
        ArrayList<Game> alG = this.makeAutomaticPairing(alP, roundNumber);
        if (alG.size() != 1){
            System.out.println("Internal issue in teamsPair()");
            return;
        }
        Game game = alG.get(0);
        
//        if (game.getHandicap() != 0){
        if (game.getHandicap() == 0){
            if (pt0IsWhite) {
                game.setWhitePlayer(pt0);
                game.setBlackPlayer(pt1);
            } else {
                game.setWhitePlayer(pt1);
                game.setBlackPlayer(pt0);
            }
        }
        Game[] tabGames = new Game[teamSize];
        tabGames[0] = game;

        // The other boards;
        for (int ib = 1; ib < teamSize; ib++) {
            Player p0 = team0.getTeamMember(roundNumber, ib);
            Player p1 = team1.getTeamMember(roundNumber, ib);
//            Game g = new Game(roundNumber, -1, null, null, true, 0, Game.RESULT_UNKNOWN);
        // Modif 3.31.01 for hd team tournaments
        alP = new ArrayList<Player>();
        alP.add(p0);
        alP.add(p1);
        alG = this.makeAutomaticPairing(alP, roundNumber);
        if (alG.size() != 1){
            System.out.println("Internal issue in teamsPair()");
            return;
        }
        Game g = alG.get(0);
        if(g.getHandicap() == 0){
            if (pt0IsWhite == (ib % 2 == 0)) {
                g.setWhitePlayer(p0);
                g.setBlackPlayer(p1);
            } else {
                g.setWhitePlayer(p1);
                g.setBlackPlayer(p0);
            }
        }
            tabGames[ib] = g;
        }

        // Give table numbers and add games to the tournament
        for (int ib = 0; ib < teamSize; ib++) {
            int tn = findFirstAvailableTableNumber(roundNumber);
            if (tabGames[ib] == null) {
                continue;
            }
            tabGames[ib].setTableNumber(tn);
            try {
                this.addGame(tabGames[ib]);
            } catch (TournamentException ex) {
                Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public int findFirstAvailableTableNumber(int roundNumber) throws RemoteException {
        int tn = 0;
        boolean bTNOK;  // Table number OK
        do {
            bTNOK = true;
            for (Game g : this.gamesList(roundNumber)) {
                if (g.getTableNumber() == tn) {
                    tn++;
                    bTNOK = false;
                }
            }
        } while (!bTNOK);

        return tn;
    }

    @Override
    public ArrayList<ScoredPlayer> orderedScoredPlayersList(
            int roundNumber, PlacementParameterSet pps) throws RemoteException {
        fillBaseScoringInfoIfNecessary();

        // Order hmScoredPlayers into alOrderedScoredPlayers according to Main criteria (before DC and SDC)
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = new ArrayList<ScoredPlayer>(hmScoredPlayers.values());

        int[] crit = pps.getPlaCriteria();
        int[] primaryCrit = new int[crit.length];
        int iCritDir = crit.length; // set to first CD or SDC criterion if found
        for (int iC = 0; iC < crit.length; iC++) {
            if (crit[iC] == PlacementParameterSet.PLA_CRIT_DC || crit[iC] == PlacementParameterSet.PLA_CRIT_SDC) {
                iCritDir = iC;
                break;
            } else {
                primaryCrit[iC] = crit[iC];
            }
        }
        for (int iC = iCritDir; iC < crit.length; iC++) {
            primaryCrit[iC] = PlacementParameterSet.PLA_CRIT_NUL;
        }

        // Sort on primary criteria
        ScoredPlayerComparator spc = new ScoredPlayerComparator(primaryCrit, roundNumber, false);
        Collections.sort(alOrderedScoredPlayers, spc);
        // Compute (Simplified) Direct Confrontation criteria
        this.fillDirScoringInfo(alOrderedScoredPlayers, roundNumber, pps);

        // And now, complete sort
        spc = new ScoredPlayerComparator(crit, roundNumber, false);
        Collections.sort(alOrderedScoredPlayers, spc);

        return alOrderedScoredPlayers;
    }

    /**
     * Dedicated to be used by orderedScoredPlayersList If necessary, rebuilds
     * this.htScoredPlayersList and fills base info, ie basic criteria, but
     * neither DC, SDC nor ordering nor group information
     */
    private void fillBaseScoringInfoIfNecessary() throws RemoteException {
        // If info is already up to date, just return
        if (this.lastTournamentModificationTime < this.lastBaseScoringInfoRefreshTime) {
            return;
        }

        lastBaseScoringInfoRefreshTime = System.currentTimeMillis();

        // 0) Preparation
        // **************
        GeneralParameterSet gps = tournamentParameterSet.getGeneralParameterSet();
        hmScoredPlayers = new HashMap<String, ScoredPlayer>();
        for (Player p : this.hmPlayers.values()) {
            ScoredPlayer sp = new ScoredPlayer(this.tournamentParameterSet.getGeneralParameterSet(), p);
            hmScoredPlayers.put(p.getKeyString(), sp);
        }
        int numberOfRoundsToCompute = gps.getNumberOfRounds();

        // 1) participation
        // ****************
        for (ScoredPlayer sp : this.hmScoredPlayers.values()) {
            for (int r = 0; r < numberOfRoundsToCompute; r++) {
                if (!sp.getParticipating()[r]) {
                    sp.setParticipation(r, ScoredPlayer.ABSENT);
                } else {
                    sp.setParticipation(r, ScoredPlayer.NOT_ASSIGNED);    // As an initial status
                }
            }
        }
        for (Game g : this.hmGames.values()) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            if (wP == null) {
                continue;
            }
            if (bP == null) {
                continue;
            }
            int r = g.getRoundNumber();
            ScoredPlayer wSP = hmScoredPlayers.get(wP.getKeyString());
            ScoredPlayer bSP = hmScoredPlayers.get(bP.getKeyString());
            wSP.setParticipation(r, ScoredPlayer.PAIRED);
            bSP.setParticipation(r, ScoredPlayer.PAIRED);
        }
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            Player p = this.byePlayers[r];
            if (p != null) {
                ScoredPlayer sp = hmScoredPlayers.get(p.getKeyString());
                sp.setParticipation(r, ScoredPlayer.BYE);
            }
        }
        for (Game g : this.hmGames.values()) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            if (wP == null) {
                continue;
            }
            if (bP == null) {
                continue;
            }
            int r = g.getRoundNumber();
            ScoredPlayer wSP = hmScoredPlayers.get(wP.getKeyString());
            ScoredPlayer bSP = hmScoredPlayers.get(bP.getKeyString());
            wSP.setGame(r, g);
            bSP.setGame(r, g);
        }

        // 2) nbwX2,cpsX2 and mmsX2
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            // Initialize
            for (ScoredPlayer sp : hmScoredPlayers.values()) {
                if (r == 0) {
                    sp.setNBWX2(r, 0);
                    sp.setCPSX2(r, 0);
                    sp.setMMSX2(r, 2 * sp.smms(gps));
                } else {
                    sp.setNBWX2(r, sp.getNBWX2(r - 1));
                    sp.setCPSX2(r, 2 * sp.getCPSX2(r - 1));                  
                    sp.setMMSX2(r, sp.getMMSX2(r - 1));
                }
            }

            // Points from games
            for (Game g : hmGames.values()) {
                if (g.getRoundNumber() != r) {
                    continue;
                }
                Player wP = g.getWhitePlayer();
                Player bP = g.getBlackPlayer();
                if (wP == null) {
                    continue;
                }
                if (bP == null) {
                    continue;
                }
                ScoredPlayer wSP = hmScoredPlayers.get(wP.getKeyString());
                ScoredPlayer bSP = hmScoredPlayers.get(bP.getKeyString());
                switch (g.getResult()) {
                    case Game.RESULT_BOTHLOSE:
                    case Game.RESULT_BOTHLOSE_BYDEF:
                    case Game.RESULT_UNKNOWN:
                        break;
                    case Game.RESULT_WHITEWINS:
                    case Game.RESULT_WHITEWINS_BYDEF:
                        wSP.setNBWX2(r, wSP.getNBWX2(r) + 2);
                        wSP.setCPSX2(r, wSP.getCPSX2(r) + 2);
                        wSP.setMMSX2(r, wSP.getMMSX2(r) + 2);
                        break;
                    case Game.RESULT_BLACKWINS:
                    case Game.RESULT_BLACKWINS_BYDEF:
                        bSP.setNBWX2(r, bSP.getNBWX2(r) + 2);
                        bSP.setCPSX2(r, bSP.getCPSX2(r) + 2);
                        bSP.setMMSX2(r, bSP.getMMSX2(r) + 2);
                        break;
                    case Game.RESULT_EQUAL:
                    case Game.RESULT_EQUAL_BYDEF:
                        wSP.setNBWX2(r, wSP.getNBWX2(r) + 1);
                        wSP.setCPSX2(r, wSP.getCPSX2(r) + 1);
                        wSP.setMMSX2(r, wSP.getMMSX2(r) + 1);
                        bSP.setNBWX2(r, bSP.getNBWX2(r) + 1);
                        bSP.setCPSX2(r, bSP.getCPSX2(r) + 1);
                        bSP.setMMSX2(r, bSP.getMMSX2(r) + 1);
                        break;
                    case Game.RESULT_BOTHWIN:
                    case Game.RESULT_BOTHWIN_BYDEF:
                        wSP.setNBWX2(r, wSP.getNBWX2(r) + 2);
                        wSP.setCPSX2(r, wSP.getCPSX2(r) + 2);
                        wSP.setMMSX2(r, wSP.getMMSX2(r) + 2);
                        bSP.setNBWX2(r, bSP.getNBWX2(r) + 2);
                        bSP.setCPSX2(r, bSP.getCPSX2(r) + 2);
                        bSP.setMMSX2(r, bSP.getMMSX2(r) + 2);
                        break;
                }
            }
        }
        for (ScoredPlayer sp : hmScoredPlayers.values()) {
            int nbPtsNBW2AbsentOrBye = 0;
            int nbPtsMMS2AbsentOrBye = 0;
            for (int r = 0; r < numberOfRoundsToCompute; r++) {
                if (sp.getParticipation(r) == ScoredPlayer.ABSENT) {
                    nbPtsNBW2AbsentOrBye += gps.getGenNBW2ValueAbsent();
                    nbPtsMMS2AbsentOrBye += gps.getGenMMS2ValueAbsent();
                }
                if (sp.getParticipation(r) == ScoredPlayer.BYE) {
                    nbPtsNBW2AbsentOrBye += gps.getGenNBW2ValueBye();
                    nbPtsMMS2AbsentOrBye += gps.getGenMMS2ValueBye();
                }
                int nbPNBW2AB = nbPtsNBW2AbsentOrBye;
                int nbPMMS2AB = nbPtsMMS2AbsentOrBye;
                if (gps.isGenRoundDownNBWMMS()) {
                    nbPNBW2AB = (nbPtsNBW2AbsentOrBye / 2) * 2;
                    nbPMMS2AB = (nbPtsMMS2AbsentOrBye / 2) * 2;
                }
                sp.setNBWX2(r, sp.getNBWX2(r) + nbPNBW2AB);
                sp.setCPSX2(r, sp.getCPSX2(r) + nbPNBW2AB);
                sp.setMMSX2(r, sp.getMMSX2(r) + nbPMMS2AB);
            }
        }
        // 2b) STS
        for (ScoredPlayer sp : hmScoredPlayers.values()) {
            // First, initialize STS with MMS
            for (int r = 0; r < numberOfRoundsToCompute; r++) {
                sp.setSTSX2(r, sp.getMMSX2(r));
            }

            int nbRounds = gps.getNumberOfRounds();
            // Then, if sp is in topgroup and always winner up to quarterfinal, increase by 2 * 2
            if (sp.getMMSX2(nbRounds - 3) == 2 * (30 + gps.getGenMMBar() + nbRounds - 2)) {
                sp.setSTSX2(nbRounds - 3, sp.getSTSX2(nbRounds - 3) + 4);
                sp.setSTSX2(nbRounds - 2, sp.getSTSX2(nbRounds - 2) + 4);
                sp.setSTSX2(nbRounds - 1, sp.getSTSX2(nbRounds - 1) + 4);
            }
            // Then, if sp is in topgroup and always winner up to semifinal,    increase by 2 * 2
            if (sp.getMMSX2(nbRounds - 2) == 2 * (30 + gps.getGenMMBar() + nbRounds - 1)) {
                sp.setSTSX2(nbRounds - 2, sp.getSTSX2(nbRounds - 2) + 4);
                sp.setSTSX2(nbRounds - 1, sp.getSTSX2(nbRounds - 1) + 4);
            }
        }
        
        // 2bis) nbwVirtualX2 and mmsVirtualX2
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            // Initialize
            for (ScoredPlayer sp : hmScoredPlayers.values()) {
                if (r == 0) {
                    sp.setNBWVirtualX2(r, 0);
                    sp.setCPSVirtualX2(r, 0);
                    sp.setMMSVirtualX2(r, 2 * sp.smms(gps));
                } else {
                    sp.setNBWVirtualX2(r, sp.getNBWVirtualX2(r - 1));
                    sp.setCPSVirtualX2(r, 2 * sp.getNBWVirtualX2(r - 1));
                    sp.setMMSVirtualX2(r, sp.getMMSVirtualX2(r - 1));
                }
            }

            // Points from games
            for (Game g : hmGames.values()) {
                if (g.getRoundNumber() != r) {
                    continue;
                }
                Player wP = g.getWhitePlayer();
                Player bP = g.getBlackPlayer();
                if (wP == null) {
                    continue;
                }
                if (bP == null) {
                    continue;
                }
                ScoredPlayer wSP = hmScoredPlayers.get(wP.getKeyString());
                ScoredPlayer bSP = hmScoredPlayers.get(bP.getKeyString());
                switch (g.getResult()) {
                    case Game.RESULT_BOTHLOSE:
                    case Game.RESULT_BOTHLOSE_BYDEF: // All "BYDEF" results are separately processed
                    case Game.RESULT_WHITEWINS_BYDEF:
                    case Game.RESULT_BLACKWINS_BYDEF:
                    case Game.RESULT_EQUAL_BYDEF:
                    case Game.RESULT_BOTHWIN_BYDEF:
                    case Game.RESULT_UNKNOWN:
                        break;
                    case Game.RESULT_WHITEWINS:
                        wSP.setNBWVirtualX2(r, wSP.getNBWVirtualX2(r) + 2);
                        wSP.setCPSVirtualX2(r, wSP.getCPSVirtualX2(r) + 2);
                        wSP.setMMSVirtualX2(r, wSP.getMMSVirtualX2(r) + 2);
                        break;
                    case Game.RESULT_BLACKWINS:
                        bSP.setNBWVirtualX2(r, bSP.getNBWVirtualX2(r) + 2);
                        bSP.setCPSVirtualX2(r, bSP.getCPSVirtualX2(r) + 2);
                        bSP.setMMSVirtualX2(r, bSP.getMMSVirtualX2(r) + 2);
                        break;
                    case Game.RESULT_EQUAL:
                        wSP.setNBWVirtualX2(r, wSP.getNBWVirtualX2(r) + 1);
                        wSP.setCPSVirtualX2(r, wSP.getCPSVirtualX2(r) + 1);
                        wSP.setMMSVirtualX2(r, wSP.getMMSVirtualX2(r) + 1);
                        bSP.setNBWVirtualX2(r, bSP.getNBWVirtualX2(r) + 1);
                        bSP.setCPSVirtualX2(r, bSP.getCPSVirtualX2(r) + 1);
                        bSP.setMMSVirtualX2(r, bSP.getMMSVirtualX2(r) + 1);
                        break;
                    case Game.RESULT_BOTHWIN:
                        wSP.setNBWVirtualX2(r, wSP.getNBWVirtualX2(r) + 2);
                        wSP.setCPSVirtualX2(r, wSP.getCPSVirtualX2(r) + 2);
                        wSP.setMMSVirtualX2(r, wSP.getMMSVirtualX2(r) + 2);
                        bSP.setNBWVirtualX2(r, bSP.getNBWVirtualX2(r) + 2);
                        bSP.setCPSVirtualX2(r, bSP.getCPSVirtualX2(r) + 2);
                        bSP.setMMSVirtualX2(r, bSP.getMMSVirtualX2(r) + 2);
                        break;
                }
            }

        }
        for (ScoredPlayer sp : hmScoredPlayers.values()) {
            int nbVPX2 = 0;
            for (int r = 0; r < numberOfRoundsToCompute; r++) {
                if (!sp.gameWasPlayed(r)) nbVPX2++;
                sp.setNBWVirtualX2(r, sp.getNBWVirtualX2(r) + nbVPX2);
                sp.setCPSVirtualX2(r, sp.getCPSVirtualX2(r) + nbVPX2);
                sp.setMMSVirtualX2(r, sp.getMMSVirtualX2(r) + nbVPX2);  
            } 
        }

        // 3) CUSSW and CUSSM
        for (ScoredPlayer sp : hmScoredPlayers.values()) {
            sp.setCUSWX2(0, sp.getNBWX2(0));
            sp.setCUSMX2(0, sp.getMMSX2(0));
            for (int r = 1; r < numberOfRoundsToCompute; r++) {
                sp.setCUSWX2(r, sp.getCUSWX2(r - 1) + sp.getNBWX2(r));
                sp.setCUSMX2(r, sp.getCUSMX2(r - 1) + sp.getMMSX2(r));
            }
        }

        // 4.1) SOSW, SOSWM1, SOSWM2,SODOSW
        boolean bVirtual = this.getTournamentParameterSet().getGeneralParameterSet().isGenCountNotPlayedGamesAsHalfPoint();
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            for (ScoredPlayer sp : hmScoredPlayers.values()) {
                int[] oswX2 = new int[numberOfRoundsToCompute];
                int[] doswX4 = new int[numberOfRoundsToCompute];    // Defeated opponents score
//                int[] osmX2  = new int[numberOfRoundsToCompute];
//                int[] dosmX4 = new int[numberOfRoundsToCompute];    // Defeated opponents score
//                int[] ostsX2 = new int[numberOfRoundsToCompute];

                for (int rr = 0; rr <= r; rr++) {
                    if (sp.getParticipation(rr) != ScoredPlayer.PAIRED) {
                        oswX2[rr] = 0;
                        doswX4[rr] = 0;
//                        osmX2[rr] = 2 * sp.smms(gps);
//                        ostsX2[rr] = 2 * sp.smms(gps);
                    } else {
                        Game g = sp.getGame(rr);
                        Player opp = this.opponent(g, sp);
                        int result = getWX2(g, sp);

                        ScoredPlayer sOpp = hmScoredPlayers.get(opp.getKeyString());
                        if (bVirtual){
                            oswX2[rr] = sOpp.getNBWVirtualX2(r);
                        }
                        else{
                            oswX2[rr] = sOpp.getNBWX2(r);
                        }                       
                        doswX4[rr] = oswX2[rr] * result;                        
                    }
                }
                int sosX2 = 0;
                int sdsX4 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    sosX2 += oswX2[rr];
                    sdsX4 += doswX4[rr];
                }
                sp.setSOSWX2(r, sosX2);
                sp.setSDSWX4(r, sdsX4);

                // soswM1X2, soswM2X2
                int sosM1X2 = 0;
                int sosM2X2 = 0;
                if (r == 0) {
                    sosM1X2 = 0;
                    sosM2X2 = 0;
                } else if (r == 1) {
                    sosM1X2 = Math.max(oswX2[0], oswX2[1]);
                    sosM2X2 = 0;
                } else {
                    int rMin = 0;
                    for (int rr = 1; rr <= r; rr++) {
                        if (oswX2[rr] < oswX2[rMin]) {
                            rMin = rr;
                        }
                    }
                    int rMin2 = 0;
                    if (rMin == 0) {
                        rMin2 = 1;
                    }
                    for (int rr = 0; rr <= r; rr++) {
                        if (rr == rMin) {
                            continue;
                        }
                        if (oswX2[rr] < oswX2[rMin2]) {
                            rMin2 = rr;
                        }
                    }
                    sosM1X2 = sp.getSOSWX2(r) - oswX2[rMin];
                    sosM2X2 = sosM1X2 - oswX2[rMin2];
                }
                sp.setSOSWM1X2(r, sosM1X2);
                sp.setSOSWM2X2(r, sosM2X2);
            }
        }

        // 4.2) SOSM, SOSMM1, SOSMM2, SODOSM, SOSTS
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            for (ScoredPlayer sp : hmScoredPlayers.values()) {
                int[] osmX2  = new int[numberOfRoundsToCompute];
                int[] dosmX4 = new int[numberOfRoundsToCompute];    // Defeated opponents score
                int[] ostsX2 = new int[numberOfRoundsToCompute];
                for (int rr = 0; rr <= r; rr++) {
                    if (sp.getParticipation(rr) != ScoredPlayer.PAIRED) {
                        osmX2[rr] = 2 * sp.smms(gps);
                        ostsX2[rr] = 2 * sp.smms(gps);
                    } else {
                        Game g = sp.getGame(rr);
                        Player opp = opponent(g, sp);
                        int result = getWX2(g, sp);                       
                        ScoredPlayer sOpp = hmScoredPlayers.get(opp.getKeyString());
                        
                        if (bVirtual){
                            osmX2[rr] = sOpp.getMMSVirtualX2(r);
                            ostsX2[rr] = sOpp.getSTSVirtualX2(r);
                        }
                        else{
                            osmX2[rr] = sOpp.getMMSX2(r);
                            ostsX2[rr] = sOpp.getSTSX2(r);
                        }                       
                         
                        // osmX2[rr] = sOpp.getMMSX2(r);
                        // ostsX2[rr] = sOpp.getSTSX2(r);
                        
                        if (g.getWhitePlayer().hasSameKeyString(sp)) {
                            osmX2[rr] += 2 * g.getHandicap();
                            ostsX2[rr] += 2 * g.getHandicap();
                        } else {
                            osmX2[rr] -= 2 * g.getHandicap();
                            ostsX2[rr] -= 2 * g.getHandicap();
                        }
                        dosmX4[rr] = osmX2[rr] * getWX2(g, sp);

                    }
                }
                int sosX2 = 0;
                int sdsX4 = 0;
                int sostsX2 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    sosX2 += osmX2[rr];
                    sdsX4 += dosmX4[rr];
                    sostsX2 += ostsX2[rr];
                }
                sp.setSOSMX2(r, sosX2);
                sp.setSDSMX4(r, sdsX4);
                sp.setSOSTSX2(r, sostsX2);

                // sosmM1X2, sosmM2X2
                int sosM1X2 = 0;
                int sosM2X2 = 0;
                if (r == 0) {
                    sosM1X2 = 0;
                    sosM2X2 = 0;
                } else if (r == 1) {
                    sosM1X2 = Math.max(osmX2[0], osmX2[1]);
                    sosM2X2 = 0;
                } else {
                    int rMin = 0;
                    for (int rr = 1; rr <= r; rr++) {
                        if (osmX2[rr] < osmX2[rMin]) {
                            rMin = rr;
                        }
                    }
                    int rMin2 = 0;
                    if (rMin == 0) {
                        rMin2 = 1;
                    }
                    for (int rr = 0; rr <= r; rr++) {
                        if (rr == rMin) {
                            continue;
                        }
                        if (osmX2[rr] < osmX2[rMin2]) {
                            rMin2 = rr;
                        }
                    }
                    sosM1X2 = sp.getSOSMX2(r) - osmX2[rMin];
                    sosM2X2 = sosM1X2 - osmX2[rMin2];
                    sp.setSOSMM1X2(r, sosM1X2);
                    sp.setSOSMM2X2(r, sosM2X2);
                }
            }
        }
        
                
        
        // 5) SOSOSW and SOSOSM
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            for (ScoredPlayer sp : hmScoredPlayers.values()) {
                int sososwX2 = 0;
                int sososmX2 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    if (sp.getParticipation(rr) != ScoredPlayer.PAIRED) {
                        sososwX2 += 0;
                        sososmX2 += 2 * sp.smms(gps) * (r + 1);
                    } else {
                        Game g = sp.getGame(rr);
                        Player opp;
                        if (g.getWhitePlayer().hasSameKeyString(sp)) {
                            opp = g.getBlackPlayer();
                        } else {
                            opp = g.getWhitePlayer();
                        }
                        ScoredPlayer sOpp = hmScoredPlayers.get(opp.getKeyString());
                        sososwX2 += sOpp.getSOSWX2(r);
                        sososmX2 += sOpp.getSOSMX2(r);
                    }
                }
                sp.setSSSWX2(r, sososwX2);
                sp.setSSSMX2(r, sososmX2);
            }
        }


        // 6)  EXT EXR
        for (int r = 0; r < numberOfRoundsToCompute; r++) {
            for (ScoredPlayer sp : hmScoredPlayers.values()) {
                int extX2 = 0;
                int exrX2 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    if (sp.getParticipation(rr) != ScoredPlayer.PAIRED) {
                        continue;
                    }
                    Game g = sp.getGame(rr);
                    Player opp;
                    boolean spWasWhite;
                    if (g.getWhitePlayer().hasSameKeyString(sp)) {
                        opp = g.getBlackPlayer();
                        spWasWhite = true;
                    } else {
                        opp = g.getWhitePlayer();
                        spWasWhite = false;
                    }
                    ScoredPlayer sOpp = hmScoredPlayers.get(opp.getKeyString());

                    int realHd = g.getHandicap();
                    if (!spWasWhite) {
                        realHd = -realHd;
                    }
                    int naturalHd = sp.getRank() - sOpp.getRank();
                    int coef = 0;
                    if (realHd - naturalHd <= 0) {
                        coef = 0;
                    }
                    if (realHd - naturalHd == 0) {
                        coef = 1;
                    }
                    if (realHd - naturalHd == 1) {
                        coef = 2;
                    }
                    if (realHd - naturalHd >= 2) {
                        coef = 3;
                    }
                    extX2 += sOpp.getNBWX2(r) * coef;
                    boolean bWin = false;
                    if (spWasWhite
                            && (g.getResult() == Game.RESULT_WHITEWINS
                            || g.getResult() == Game.RESULT_WHITEWINS_BYDEF
                            || g.getResult() == Game.RESULT_BOTHWIN
                            || g.getResult() == Game.RESULT_BOTHWIN_BYDEF)) {
                        bWin = true;
                    }
                    if (!spWasWhite
                            && (g.getResult() == Game.RESULT_BLACKWINS
                            || g.getResult() == Game.RESULT_BLACKWINS_BYDEF
                            || g.getResult() == Game.RESULT_BOTHWIN
                            || g.getResult() == Game.RESULT_BOTHWIN_BYDEF)) {
                        bWin = true;
                    }
                    if (bWin) {
                        exrX2 += sOpp.getNBWX2(r) * coef;
                    }
                }
                sp.setEXTX2(r, extX2);
                sp.setEXRX2(r, exrX2);
            }
        }
    }

    /**
     * Defines dir attributes (dc and sdc) of all players in alSP if this
     * criterion exists in plaCriteria Dir value is a tie break (also said Dir
     * Confrontation) fillDirScoringInfo makes preparation work and subcontracts
     * actual computation to defineDirForExAequoGroup
     *
     * @param alSP ArrayList of ScoredPlayers for which Dir Criterion should be
     * made
     */
    private boolean fillDirScoringInfo(ArrayList<ScoredPlayer> alSP, int roundNumber, PlacementParameterSet pps) {
        // By default dir is 0
        for (ScoredPlayer sP : alSP) {
            sP.setSDC(0);
            sP.setDC(0);
        }

        int[] crit = pps.getPlaCriteria();
        int nbCritBeforeDir = -1;
        for (int cr = 0; cr < crit.length; cr++) {
            if (crit[cr] == PlacementParameterSet.PLA_CRIT_DC || crit[cr] == PlacementParameterSet.PLA_CRIT_SDC) {
                nbCritBeforeDir = cr;
                break;
            }
        }
        if (nbCritBeforeDir < 0) {
            return false;
        }

        int numPlayer = 0;

        while (numPlayer < alSP.size()) {
            ArrayList<ScoredPlayer> alExAequoBeforeDirScoredPlayers = new ArrayList<ScoredPlayer>();
            alExAequoBeforeDirScoredPlayers.add(alSP.get(numPlayer));
            int[] critValue = new int[nbCritBeforeDir];
            for (int cr = 0; cr < nbCritBeforeDir; cr++) {
                critValue[cr] = alSP.get(numPlayer).getCritValue(crit[cr], roundNumber);
            }

            numPlayer++;
            while (numPlayer < alSP.size()) {
                ScoredPlayer sP = alSP.get(numPlayer);
                boolean bCandidateOK = true;
                for (int cr = 0; cr < nbCritBeforeDir; cr++) {
                    if (sP.getCritValue(crit[cr], roundNumber) != critValue[cr]) {
                        bCandidateOK = false;
                    }
                }
                if (!bCandidateOK) {
                    break;
                } else {
                    alExAequoBeforeDirScoredPlayers.add(sP);
                    numPlayer++;
                }
            }
            defineDirForExAequoGroup(alExAequoBeforeDirScoredPlayers, roundNumber, pps);
        }
        return true;
    }

    /**
     * Fills pairing info into alSPlayers : groupNumber, groupSize,
     * innerPlacement numberOfGroups, DU and DD
     */
    @Override
    public void fillPairingInfo(int roundNumber) throws RemoteException {
        GeneralParameterSet gps = tournamentParameterSet.getGeneralParameterSet();
        PlacementParameterSet pps = tournamentParameterSet.getPlacementParameterSet();
        PairingParameterSet paiPS = tournamentParameterSet.getPairingParameterSet();

        // What is the main score in this tps ?
        int mainCrit = pps.mainCriterion();
        // And what is mainScoreMin and mainScoreMax ?
        int mainScoreMin = 0;              // Default value
        int mainScoreMax = roundNumber;     // Default value
        if (mainCrit == PlacementParameterSet.PLA_CRIT_MMS) {
            mainScoreMin = gps.getGenMMFloor() + PlacementParameterSet.PLA_SMMS_CORR_MIN - Gotha.MIN_RANK;
            mainScoreMax = gps.getGenMMBar() + PlacementParameterSet.PLA_SMMS_CORR_MAX + roundNumber - Gotha.MIN_RANK;
        }
        
        if (mainCrit == PlacementParameterSet.PLA_CRIT_CPS) {
            mainScoreMin = 0;
            mainScoreMax = 1;
            int wRound = 0;
            while (wRound++ < roundNumber) mainScoreMax *= 2;
        }

        int groupNumber = 0;
        for (int cat = 0; cat < gps.getNumberOfCategories(); cat++) {
            for (int mainScore = mainScoreMax; mainScore >= mainScoreMin; mainScore--) {
                ArrayList<ScoredPlayer> alSPGroup = new ArrayList<ScoredPlayer>();
                for (ScoredPlayer sp : this.hmScoredPlayers.values()) {
                    if (sp.category(gps) != cat) {
                        continue;
                    }
                    if (sp.getCritValue(mainCrit, roundNumber - 1) / 2 != mainScore) {
                        continue;
                    }
                    alSPGroup.add(sp);
                }
                if (alSPGroup.isEmpty()) {
                    groupNumber++; // Added in V3.49.01
                    continue;
                }

                // Sort alSPGroup to give each SPlayer an innerPlacement
                // sort is made according to pps criteria + specified additional criteria
                int[] crit = pps.getPlaCriteria();
                int additionalCrit = paiPS.getPaiMaAdditionalPlacementCritSystem1();
                if (roundNumber > paiPS.getPaiMaLastRoundForSeedSystem1()) {
                    additionalCrit = paiPS.getPaiMaAdditionalPlacementCritSystem2();
                }
                int[] paiCrit = new int[crit.length + 1];
                System.arraycopy(crit, 0, paiCrit, 0, crit.length);
                paiCrit[paiCrit.length - 1] = additionalCrit;
                ScoredPlayerComparator spc = new ScoredPlayerComparator(paiCrit, roundNumber - 1, false);
                Collections.sort(alSPGroup, spc);
                // Now, we can store group infos into sp s
                for (ScoredPlayer sp : alSPGroup) {
                    sp.groupNumber = groupNumber;
                    sp.groupSize = alSPGroup.size();
                    sp.innerPlacement = alSPGroup.indexOf(sp);
                }
                groupNumber++;
            }
        }
        int numberOfGroups = groupNumber;
        for (ScoredPlayer sp : this.hmScoredPlayers.values()) {
            sp.numberOfGroups = numberOfGroups;
        }

        // Compute number of DU (Draw-ups) and DD (Draw-downs)
        for (ScoredPlayer sp : this.hmScoredPlayers.values()) {
            sp.nbDU = sp.nbDD = 0;
        }
        if (roundNumber >= 1) {
            // prepare an Array of scores before round roundNumber
            ArrayList<ScoredPlayer> alTempScoredPlayers = new ArrayList<ScoredPlayer>(hmScoredPlayers.values());
            int nbP = alTempScoredPlayers.size();
            int[][] scoreBefore = new int[roundNumber][nbP];
            for (int r = 0; r < roundNumber; r++) {
                for (int iSP = 0; iSP < nbP; iSP++) {
                    ScoredPlayer sp = alTempScoredPlayers.get(iSP);
                    scoreBefore[r][iSP] = sp.getCritValue(mainCrit, r - 1) / 2;
                }
            }

            for (int r = 0; r < roundNumber; r++) {
                for (int iSP = 0; iSP < nbP; iSP++) {
                    ScoredPlayer sp = alTempScoredPlayers.get(iSP);
                    Game g = sp.getGame(r);
                    if (g == null) {
                        continue;
                    }
                    Player wP = g.getWhitePlayer();
                    Player bP = g.getBlackPlayer();
                    Player opp = null;
                    if (sp.hasSameKeyString(wP)) {
                        opp = bP;
                    } else {
                        opp = wP;
                    }
                    ScoredPlayer sOpp = hmScoredPlayers.get(opp.getKeyString());
                    int iSOpp = alTempScoredPlayers.indexOf(sOpp);
                    if (scoreBefore[r][iSP] < scoreBefore[r][iSOpp]) {
                        sp.nbDU++;
                    }
                    if (scoreBefore[r][iSP] > scoreBefore[r][iSOpp]) {
                        sp.nbDD++;
                    }
                }
            }
        }
    }

    @Override
    public ScoredTeamsSet getAnUpToDateScoredTeamsSet(TeamPlacementParameterSet tpps, int roundNumber) throws RemoteException {
        boolean bUpdateIsNecessary = false;
        if (scoredTeamsSet == null) {
            scoredTeamsSet = new ScoredTeamsSet(this);
            bUpdateIsNecessary = true;
        }
        if (!scoredTeamsSet.isOKWith(tpps, roundNumber)) {
            bUpdateIsNecessary = true;
        }
        if (!scoredTeamsSet.isUpToDate(this.lastTournamentModificationTime)) {
            bUpdateIsNecessary = true;
        }

        if (bUpdateIsNecessary) {
            scoredTeamsSet.update(tpps, roundNumber);
        }

        return scoredTeamsSet;
    }

    /**
     * Checks all games played between players references by alSP, and, for each
     * pair of players (i, j) stores the algebric sum of results between them.
     * For instance if player 2 won against player 3, the returned array will
     * contain +1 in [2][3] and -1 in [3][2]. This method is dedicated to be
     * used by defineDirForExAequoGroup
     *
     * @param alSP list of ScoredPlayers to be checked
     * @param roundNumber last round number to be taken in account
     * @return the
     */
    private int[][] defineAPairMatrix(ArrayList<ScoredPlayer> alSP, int roundNumber) {
        int nbP = alSP.size();
        int[][] pair = new int[nbP][nbP];

        for (int i = 0; i < nbP; i++) {
            for (int j = 0; j < nbP; j++) {
                pair[i][j] = 0;
            }
        }
        for (Game g : this.hmGames.values()) {
            if (g.getRoundNumber() > roundNumber) {
                continue;
            }
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            int numWP = -1;
            int numBP = -1;
            for (int i = 0; i < nbP; i++) {
                Player p = alSP.get(i);

                if (p.hasSameKeyString(wP)) {
                    numWP = i;
                }
                if (p.hasSameKeyString(bP)) {
                    numBP = i;
                }
            }
            if (numWP < 0) {
                continue;
            }
            if (numBP < 0) {
                continue;
            }
            int res = g.getResult();
            if (res == Game.RESULT_WHITEWINS) {
                pair[numWP][numBP]++;
                pair[numBP][numWP]--;
            }
            if (res == Game.RESULT_BLACKWINS) {
                pair[numWP][numBP]--;
                pair[numBP][numWP]++;
            }
        }
        return pair;
    }

    /**
     * computes DC or SDC for players of alExAequoBeforeDirScoredPlayers, taking
     * in account games in rounds 0 to to roundnumber included
     */
    private void defineDirForExAequoGroup(ArrayList<ScoredPlayer> alExAequoBeforeDirScoredPlayers, int roundNumber, PlacementParameterSet pps) {
        int nbP = alExAequoBeforeDirScoredPlayers.size();
        if (nbP <= 1) {
            return;
        }

        int[][] pair = this.defineAPairMatrix(alExAequoBeforeDirScoredPlayers, roundNumber);

        // limit to [-1 .. +1]
        for (int i = 0; i < nbP; i++) {
            for (int j = 0; j < nbP; j++) {
                if (i == j) {
                    pair[i][j] = 0;
                }
                if (pair[i][j] >= 1) {
                    pair[i][j] = 1;
                }
                if (pair[i][j] <= -1) {
                    pair[i][j] = -1;
                }
            }
        }

        // SDC Algorithm
        // Relevant only if every possible pair of players have played at least once
        // and, if more than one game has been played between both, their algebric sum  is != 0
        boolean bRelevant = true;
        for (int i = 0; i < nbP; i++) {
            for (int j = 0; j < nbP; j++) {
                if (i != j && pair[i][j] == 0) {
                    bRelevant = false;
                }
            }
        }
        if (bRelevant) {
            for (int i = 0; i < nbP; i++) {
                ScoredPlayer sP = alExAequoBeforeDirScoredPlayers.get(i);
                for (int j = 0; j < nbP; j++) {
                    if (pair[i][j] == 1) {
                        sP.setSDC(sP.getSDC() + 1);
                    }
                }
            }
        }

        // DC Algorithm
        // Order alExAequoBeforeDirScoredPlayers according to secondary criteria
        int[] crit = pps.getPlaCriteria();
        int[] secCrit = new int[crit.length];
        System.arraycopy(crit, 0, secCrit, 0, secCrit.length);
        for (int iC = 0; iC < secCrit.length; iC++) {
            if (crit[iC] != PlacementParameterSet.PLA_CRIT_DC) {
                secCrit[iC] = PlacementParameterSet.PLA_CRIT_NUL;
            } else {
                secCrit[iC] = PlacementParameterSet.PLA_CRIT_NUL;
                break;
            }
        }
        ScoredPlayerComparator spc = new ScoredPlayerComparator(secCrit, roundNumber, true);
        Collections.sort(alExAequoBeforeDirScoredPlayers, spc);
        int[] place = new int[alExAequoBeforeDirScoredPlayers.size()];
        place[0] = 0;
        // Give the same place to players with same values as secondary criteria
        for (int i = 1; i < alExAequoBeforeDirScoredPlayers.size(); i++) {
            if (spc.compare(alExAequoBeforeDirScoredPlayers.get(i), alExAequoBeforeDirScoredPlayers.get(i - 1)) == 0) {
                place[i] = place[i - 1];
            } else {
                place[i] = i;
            }
        }
        // Prepare sc for Matthieu
        int[] sc = new int[alExAequoBeforeDirScoredPlayers.size()];
        for (int i = 0; i < nbP; i++) {
            sc[i] = nbP - place[i];
        }
        // and make a pair[][] up to date after sorting
        pair = this.defineAPairMatrix(alExAequoBeforeDirScoredPlayers, roundNumber);

        // It's up to you, Matthieu!
        mw.go.confrontation.Confrontation conf = new mw.go.confrontation.Confrontation();
        for (int i = 0; i < nbP; i++) {
            conf.newPlayer(i, sc[i]);
        }
        for (int i = 0; i < nbP; i++) {
            for (int j = 0; j < nbP; j++) {
                if (pair[i][j] == 1) {
                    conf.newGame(i, j);
                }
            }
        }

        java.util.List<Integer> result = conf.topologicalSort(true, true);

        int max = 0;
        for (int i = 0; i < nbP; i++) {
            ScoredPlayer sp = alExAequoBeforeDirScoredPlayers.get(i);
            max = Math.max(max, conf.getPlayer(i).rank);
        }
        // Reverse order and store
        for (int i = 0; i < nbP; i++) {
            ScoredPlayer sp = alExAequoBeforeDirScoredPlayers.get(i);
            int dc = max - conf.getPlayer(i).rank;
            sp.setDC(dc);
        }
    }

    @Override
    public long getLastTournamentModificationTime() throws RemoteException {
        return lastTournamentModificationTime;
    }

    @Override
    public void setLastTournamentModificationTime(long lastTournamentModificationTime) throws RemoteException {
        this.lastTournamentModificationTime = lastTournamentModificationTime;
    }

    @Override
    public long getCurrentTournamentTime() throws RemoteException {
        return System.currentTimeMillis();
    }

    @Override
    public boolean isChangeSinceLastSave() throws RemoteException {
        return changeSinceLastSave;
    }

    private void setChangeSinceLastSave(boolean changeSinceLastSave) throws RemoteException {
        this.changeSinceLastSave = changeSinceLastSave;
    }

    @Override
    public void setChangeSinceLastSaveAsFalse() throws RemoteException {
        setChangeSinceLastSave(false);
    }

    @Override
    public String addGothaRMIClient(String strClient) throws RemoteException {
        return GothaRMIServer.addClient(strClient, this.getShortName());
    }

    @Override
    public boolean clockIn(String strClient) throws RemoteException {        
        if (GothaRMIServer.getTournament(this.getShortName()) == null) {
            return false;
        }
        GothaRMIServer.clockIn(strClient);
        return true;
    }

    @Override
    public String egfClass() throws RemoteException {
        GeneralParameterSet gps = this.tournamentParameterSet.getGeneralParameterSet();

        int bt = gps.getBasicTime() * 60;
        int at = egfAdjustedTime();

        String strClass = "X";
        int complementaryTimeSystem = gps.getComplementaryTimeSystem();
        boolean bInternet = gps.isBInternet();
        if(bInternet){
            switch (complementaryTimeSystem) {
                case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                    if (bt >= 2400 && at >= 3000) {
                        strClass = "D";
                    }
                    break;
                case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                    if (bt >= 1800 && at >= 3000) {
                        strClass = "D";
                    }
                    break;
            }
        }
        else{
            switch (complementaryTimeSystem) {
                case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                    if (bt >= 1500 && at >= 1800) {
                        strClass = "C";
                    }
                    if (bt >= 2400 && at >= 3000) {
                        strClass = "B";
                    }
                    if (bt >= 3600 && at >= 4500) {
                        strClass = "A";
                    }
                    break;
                case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                    if (bt >= 1200 && at >= 1800) {
                        strClass = "C";
                    }
                    if (bt >= 1800 && at >= 3000) {
                        strClass = "B";
                    }
                    if (bt >= 2700 && at >= 4500) {
                        strClass = "A";
                    }
                    break;
            }
        }

        return strClass;
    }

    @Override
    public int egfAdjustedTime() throws RemoteException {
        GeneralParameterSet gps = this.tournamentParameterSet.getGeneralParameterSet();

        int bt = gps.getBasicTime() * 60;
        int at = bt;

        int complementaryTimeSystem = gps.getComplementaryTimeSystem();
        switch (complementaryTimeSystem) {
            case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                at = bt;
                break;
            case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                at = bt + 45 * gps.getStdByoYomiTime();
//                at = bt + 120 * gps.getStdByoYomiTime();
                break;
            case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                at = bt + (60 * gps.getCanByoYomiTime()) / gps.getNbMovesCanTime();
//                at = bt + (120 * gps.getCanByoYomiTime()) / gps.getNbMovesCanTime();
                break;
            case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                at = bt + 120 * gps.getFischerTime();
                break;
        }

        return at;
    }

    @Override
    public boolean addClubsGroup(ClubsGroup cg) throws RemoteException {
        // Is the asked name available ?
        String str = cg.getName();
        if (hmClubsGroups.get(str) != null) return false;
       
        if (hmClubsGroups == null) {
            hmClubsGroups = new HashMap<String, ClubsGroup>();
        }
        hmClubsGroups.put(str, cg);
        this.setChangeSinceLastSave(true);
        return true;
    }
    
    @Override
    public void removeClubsGroup(ClubsGroup cg) throws RemoteException{
        hmClubsGroups.remove(cg.getName());
    }
    
    @Override
    public ClubsGroup getClubsGroupByName(String name) throws RemoteException{
        return this.hmClubsGroups.get(name);
    }
    @Override
    public ArrayList<ClubsGroup> clubsGroupsList() throws RemoteException{
        if (hmClubsGroups == null) {
            hmClubsGroups = new HashMap<String, ClubsGroup>();
        }
        return new ArrayList<ClubsGroup>(hmClubsGroups.values());
    }
    @Override
    public void addClubToClubsGroup(String groupName, String clubName) throws RemoteException{
        ClubsGroup cg = getClubsGroupByName(groupName);
        cg.put(new Club(clubName)); 
    }
    @Override
    public void removeClubFromClubsGroup(String groupName, String clubName) throws RemoteException{
        ClubsGroup cg = getClubsGroupByName(groupName);
        cg.remove(clubName); 
    }
    
     @Override
     public boolean playersAreInCommonGroup(Player p1, Player p2)throws RemoteException{
        String strClub1 = p1.getClub();
        String strClub2 = p2.getClub();
        strClub1 = strClub1.toLowerCase();
        strClub2 = strClub2.toLowerCase();
        for (ClubsGroup cg : clubsGroupsList()){
            boolean bP1 = false;
            boolean bP2 = false;
            for(Club club : cg.getHmClubs().values()){
                String strClub = club.getName();
                strClub = strClub.toLowerCase();
                if (strClub1.equals(strClub)) bP1 = true;
                if (strClub2.equals(strClub)) bP2 = true;
            }
            if (bP1 && bP2) return true;            
        }
        return false;    
    }
          
     @Override
     public boolean playersAreInCommonClub(Player p1, Player p2)throws RemoteException{
        String strClub1 = p1.getClub();
        String strClub2 = p2.getClub();
        strClub1 = strClub1.toLowerCase();
        strClub2 = strClub2.toLowerCase();
        if(strClub1.equals(strClub2)) return true;
        else return false;
    }

     @Override
     public boolean playersAreInCommonCountry(Player p1, Player p2)throws RemoteException{
        String strCountry1 = p1.getCountry();
        String strCountry2 = p2.getCountry();
        strCountry1 = strCountry1.toLowerCase();
        strCountry2 = strCountry2.toLowerCase();
        if(strCountry1.equals(strCountry2)) return true;
        else return false;
    }
}
