package info.vannier.gotha;

/**
 * Game between two players.
 */
 public class Game implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;

    /**
     * round number .
     * For games being part of a round, between 0 and Total number of rounds -1.
     */
    private int roundNumber;
     /**
     * table number.
     * Unique for a given roundnumber
     * From 0 to Gotha.MAX_NUMBER_OF_TABLES - 1
     */   
    private int tableNumber = -1;
    
    private Player whitePlayer;
    private Player blackPlayer;

    /**
     * true if colors are known.
     * Note that, in some no-handicap tournaments, color is not published.
     * When it happens, players are randomly said to be white or black.
     * <code>knownColor</code> remembers whether color was actually known
     */
    private boolean knownColor = false;

    private int handicap = 0;

    private int result = Game.RESULT_UNKNOWN;

    /**
     * Result : Unknown (usually : not yet input)
     */
    public static final int RESULT_UNKNOWN = 0;

    /**
     * results by default
     */
    public static final int RESULT_BYDEF = 256;

    /**
     * Result : White wins
     */
    public static final int RESULT_WHITEWINS = 17;
    public static final int RESULT_WHITEWINS_BYDEF = RESULT_WHITEWINS + RESULT_BYDEF;
    
    /**
     * Result : Black wins
     */
    public static final int RESULT_BLACKWINS = 18;
    public static final int RESULT_BLACKWINS_BYDEF = RESULT_BLACKWINS + RESULT_BYDEF;

    /**
     * Result : Equal (Jigo or any other reason for a draw). Means 1/2 - 1/2
     */
    public static final int RESULT_EQUAL = 19;
    public static final int RESULT_EQUAL_BYDEF = RESULT_EQUAL + RESULT_BYDEF;
   
    /**
     * Result : Both lose. Means 0 - 0
     */
    public static final int RESULT_BOTHLOSE = 32;
    public static final int RESULT_BOTHLOSE_BYDEF = RESULT_BOTHLOSE + RESULT_BYDEF;

    /**
     * Result : Both win. Means 1 - 1
     */
    public static final int RESULT_BOTHWIN = 35;
    public static final int RESULT_BOTHWIN_BYDEF = RESULT_BOTHWIN + RESULT_BYDEF;

    
    public Game() {
    }
    
    public Game(int roundNumber, int tableNumber, Player whitePlayer, Player blackPlayer, boolean knownColor, int handicap, int result) {
        this.roundNumber = roundNumber;
        this.tableNumber = tableNumber;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.knownColor  = knownColor;
        this.handicap    = handicap;
        this.result      = result;
    }

    public boolean isWinner(Player p){
        boolean bWinner = false;
        if (p.hasSameKeyString(whitePlayer)){
            switch(this.result){
                case Game.RESULT_BOTHWIN:
                case Game.RESULT_BOTHWIN_BYDEF:
                case Game.RESULT_WHITEWINS:
                case Game.RESULT_WHITEWINS_BYDEF:
                    bWinner = true; 
            }
        }
        else if (p.hasSameKeyString(blackPlayer)){
            switch(this.result){
                case Game.RESULT_BOTHWIN:
                case Game.RESULT_BOTHWIN_BYDEF:
                case Game.RESULT_BLACKWINS:
                case Game.RESULT_BLACKWINS_BYDEF:
                    bWinner = true; 
            }
        }
        return bWinner;
    }
    
    public Player getBlackPlayer()   {
        return blackPlayer;
    }

    public void setBlackPlayer(Player val) {
        this.blackPlayer = val;
    }

    public Player getWhitePlayer()   {
        return whitePlayer;
    }

    public void setWhitePlayer(Player val) {
        this.whitePlayer = val;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int val) {
        this.tableNumber = val;
    }

    public int getHandicap() {
        return handicap;
    }

    public void setHandicap(int val) {
        handicap = val;
        if (handicap < 0) handicap = 0;
        if (handicap > 9) handicap = 9;  
    }

    public int getResult()   {
        return result;
    }
    /**
     * 
     * @param wb : true the string should put White first
     * @return 
     */
    public String resultAsString(boolean wb){
        String strResult = " - ";
        switch(getResult()){
                case Game.RESULT_UNKNOWN        : strResult = " - "; break;
                case Game.RESULT_WHITEWINS      : strResult = "1-0"; break;
                case Game.RESULT_BLACKWINS      : strResult = "0-1"; break;
                case Game.RESULT_EQUAL          : strResult = "½-½"; break;
                case Game.RESULT_BOTHLOSE       : strResult = "0-0"; break;
                case Game.RESULT_BOTHWIN        : strResult = "1-1"; break;
                case Game.RESULT_WHITEWINS_BYDEF: strResult = "1-0!"; break;
                case Game.RESULT_BLACKWINS_BYDEF: strResult = "0-1!"; break;
                case Game.RESULT_EQUAL_BYDEF    : strResult = "½-½!"; break;
                case Game.RESULT_BOTHLOSE_BYDEF : strResult = "0-0!"; break;
                case Game.RESULT_BOTHWIN_BYDEF  : strResult = "1-1!"; break;
                default                         : strResult = "?-?";
            }
        if (!wb){
        switch(getResult()){
                case Game.RESULT_UNKNOWN        : strResult = " - "; break;
                case Game.RESULT_WHITEWINS      : strResult = "0-1"; break;
                case Game.RESULT_BLACKWINS      : strResult = "1-0"; break;
                case Game.RESULT_EQUAL          : strResult = "½-½"; break;
                case Game.RESULT_BOTHLOSE       : strResult = "0-0"; break;
                case Game.RESULT_BOTHWIN        : strResult = "1-1"; break;
                case Game.RESULT_WHITEWINS_BYDEF: strResult = "0-1!"; break;
                case Game.RESULT_BLACKWINS_BYDEF: strResult = "1-0!"; break;
                case Game.RESULT_EQUAL_BYDEF    : strResult = "½-½!"; break;
                case Game.RESULT_BOTHLOSE_BYDEF : strResult = "0-0!"; break;
                case Game.RESULT_BOTHWIN_BYDEF  : strResult = "1-1!"; break;
                default                         : strResult = "?-?";
            }            
        }
        return strResult;

    }

    public void setResult(int val)   {
        this.result = val;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int round) {
        this.roundNumber = round;
    }
     
     public boolean isKnownColor() {
         return knownColor;
     }

     public void setKnownColor(boolean knownColor) {
         this.knownColor = knownColor;
     }        
}
 

