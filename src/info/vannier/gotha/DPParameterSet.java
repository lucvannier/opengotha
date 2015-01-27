package info.vannier.gotha;

/**
 * Display and Print ParameterSet
 * @author Luc
 */
public class DPParameterSet implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    
    private int playerSortType = PlayerComparator.NAME_ORDER;
    
    public final static int DP_GAME_FORMAT_SHORT = 0;
    public final static int DP_GAME_FORMAT_FULL = 2;
    private int gameFormat = DP_GAME_FORMAT_FULL;
        
    private boolean showPlayerGrade               = true;
//    private boolean showPlayerRank               = true;
    private boolean showPlayerCountry            = false;
    private boolean showPlayerClub               = true;

    private boolean showByePlayer                   = true;
    private boolean showNotPairedPlayers            = true;
    private boolean showNotParticipatingPlayers     = false;
    private boolean showNotFinallyRegisteredPlayers = true;
    

    private boolean displayNumCol = true;
    private boolean displayPlCol  = true;
    private boolean displayCoCol  = true;
    private boolean displayClCol  = true;

    private boolean displayIndGamesInMatches  = true;   // Display Individual Games (and not only Team matches)
    
    public DPParameterSet() {             
    }
     
    public DPParameterSet(DPParameterSet dpps) { 
        this.playerSortType = dpps.playerSortType;
        
        this.gameFormat = dpps.gameFormat;
        
//        this.showPlayerRank = dpps.showPlayerRank;
        this.showPlayerCountry = dpps.showPlayerCountry;
        this.showPlayerClub = dpps.showPlayerClub;
        
        this.showByePlayer = dpps.showByePlayer;
        this.showNotPairedPlayers = dpps.showNotPairedPlayers;
        this.showNotParticipatingPlayers = dpps.showNotParticipatingPlayers;
        this.showNotFinallyRegisteredPlayers = dpps.showNotFinallyRegisteredPlayers;
                
        this.displayNumCol = dpps.displayNumCol;
        this.displayPlCol = dpps.displayPlCol;
        this.displayCoCol = dpps.displayCoCol;
        this.displayClCol = dpps.displayClCol;
        
        this.displayIndGamesInMatches = dpps.displayIndGamesInMatches;
    }
    
    public void initForMM(){
        this.gameFormat = DPParameterSet.DP_GAME_FORMAT_FULL;
        commonInit();
    }
    
    public void initForSwiss(){
        this.gameFormat = DPParameterSet.DP_GAME_FORMAT_SHORT;
        commonInit();
    }
    
    public void initForSwissCat(){
        this.gameFormat = DPParameterSet.DP_GAME_FORMAT_FULL;
        commonInit();
    }
    
    public void commonInit(){
        this.displayNumCol = true;
        this.displayPlCol = true;
        this.displayIndGamesInMatches = true;
    }

    /**
     * @return the gameFormat
     */
    public int getGameFormat() {
        return gameFormat;
    }

    /**
     * @param gameFormat the gameFormat to set
     */
    public void setGameFormat(int gameFormat) {
        this.gameFormat = gameFormat;
    }

    /**
     * @return the displayNumCol
     */
    public boolean isDisplayNumCol() {
        return displayNumCol;
    }

    /**
     * @param displayTeamNumCol the displayTeamNumCol to set
     */
    public void setDisplayNumCol(boolean displayTeamNumCol) {
        this.displayNumCol = displayTeamNumCol;
    }

    /**
     * @return the displayTeamPlCol
     */
    public boolean isDisplayPlCol() {
        return displayPlCol;
    }

    /**
     * @param displayTeamPlCol the displayTeamPlCol to set
     */
    public void setDisplayPlCol(boolean displayTeamPlCol) {
        this.displayPlCol = displayTeamPlCol;
    }

    /**
     * @return the displayIndGamesInMatches
     */
    public boolean isDisplayIndGamesInMatches() {
        return displayIndGamesInMatches;
    }

    /**
     * @param displayIndGamesInMatches the displayIndGamesInMatches to set
     */
    public void setDisplayIndGamesInMatches(boolean displayIndGamesInMatches) {
        this.displayIndGamesInMatches = displayIndGamesInMatches;
    }

    /**
     * @return the displayCoCol
     */
    public boolean isDisplayCoCol() {
        return displayCoCol;
    }

    /**
     * @param displayCoCol the displayCoCol to set
     */
    public void setDisplayCoCol(boolean displayCoCol) {
        this.displayCoCol = displayCoCol;
    }

    /**
     * @return the displayClCol
     */
    public boolean isDisplayClCol() {
        return displayClCol;
    }

    /**
     * @param displayClCol the displayClCol to set
     */
    public void setDisplayClCol(boolean displayClCol) {
        this.displayClCol = displayClCol;
    }

    /**
     * @return the showByePlayer
     */
    public boolean isShowByePlayer() {
        return showByePlayer;
    }

    /**
     * @param showByePlayer the showByePlayer to set
     */
    public void setShowByePlayer(boolean showByePlayer) {
        this.showByePlayer = showByePlayer;
    }

    /**
     * @return the showNotPairedPlayers
     */
    public boolean isShowNotPairedPlayers() {
        return showNotPairedPlayers;
    }

    /**
     * @param showNotPairedPlayers the showNotPairedPlayers to set
     */
    public void setShowNotPairedPlayers(boolean showNotPairedPlayers) {
        this.showNotPairedPlayers = showNotPairedPlayers;
    }

    /**
     * @return the showNotParticipatingPlayers
     */
    public boolean isShowNotParticipatingPlayers() {
        return showNotParticipatingPlayers;
    }

    /**
     * @param showNotParticipatingPlayers the showNotParticipatingPlayers to set
     */
    public void setShowNotParticipatingPlayers(boolean showNotParticipatingPlayers) {
        this.showNotParticipatingPlayers = showNotParticipatingPlayers;
    }

    /**
     * @return the showPlayerGrade
     */
    public boolean isShowPlayerGrade() {
        return showPlayerGrade;
    }

    /**
     * @param showPlayerGrade the showPlayerGrade to set
     */
    public void setShowPlayerGrade(boolean showPlayerGrade) {
        this.showPlayerGrade = showPlayerGrade;
    }

    /**
     * @return the showPlayerCountry
     */
    public boolean isShowPlayerCountry() {
        return showPlayerCountry;
    }

    /**
     * @param showPlayerCountry the showPlayerCountry to set
     */
    public void setShowPlayerCountry(boolean showPlayerCountry) {
        this.showPlayerCountry = showPlayerCountry;
    }

    /**
     * @return the showPlayerClub
     */
    public boolean isShowPlayerClub() {
        return showPlayerClub;
    }

    /**
     * @param showPlayerClub the showPlayerClub to set
     */
    public void setShowPlayerClub(boolean showPlayerClub) {
        this.showPlayerClub = showPlayerClub;
    }

    /**
     * @return the playerSortType
     */
    public int getPlayerSortType() {
        return playerSortType;
    }

    /**
     * @param playerSortType the playerSortType to set
     */
    public void setPlayerSortType(int playerSortType) {
        this.playerSortType = playerSortType;
    }

    /**
     * @return the showNotFinallyRegisteredPlayers
     */
    public boolean isShowNotFinallyRegisteredPlayers() {
        return showNotFinallyRegisteredPlayers;
    }

    /**
     * @param showNotFinallyRegisteredPlayers the showNotFinallyRegisteredPlayers to set
     */
    public void setShowNotFinallyRegisteredPlayers(boolean showNotFinallyRegisteredPlayers) {
        this.showNotFinallyRegisteredPlayers = showNotFinallyRegisteredPlayers;
    }

}
