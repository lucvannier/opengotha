package info.vannier.gotha;

import java.io.Serializable;
import java.util.*;

public class GameComparator implements Comparator<Game>, Serializable{
    public final static int NO_ORDER = 0;
//    public final static int GAME_NUMBER_ORDER = 1;
    public final static int TABLE_NUMBER_ORDER = 2;
    public final static int BEST_SCO_ORDER = 11; // Best Score
    public final static int BEST_SCR_ORDER = 12; // Best Score then Rating

    int gameOrderType = GameComparator.NO_ORDER;
    HashMap<String, ScoredPlayer> hmScoredPlayers;
    PlacementParameterSet pps;

    public GameComparator(int gameOrderType){
        this.gameOrderType = gameOrderType;
    }

    public GameComparator(int gameOrderType, HashMap<String, ScoredPlayer> hmScoredPlayers){
        this.gameOrderType = gameOrderType;
        this.hmScoredPlayers = new HashMap<>(hmScoredPlayers);
    }
    
    public GameComparator(int gameOrderType, HashMap<String, ScoredPlayer> hmScoredPlayers, PlacementParameterSet pps){
        this.gameOrderType = gameOrderType;
        this.hmScoredPlayers = new HashMap<>(hmScoredPlayers);
        this.pps = pps;
    }

    @Override
    public int compare(Game g1, Game g2){
        int roundNumber = g1.getRoundNumber();
        Player wP1 = g1.getWhitePlayer();
        Player bP1 = g1.getBlackPlayer();
        Player wP2 = g2.getWhitePlayer();
        Player bP2 = g2.getBlackPlayer();
        switch (gameOrderType){
            case TABLE_NUMBER_ORDER :
                if (g1.getTableNumber() < g2.getTableNumber()) return -1;
                else if (g1.getTableNumber() > g2.getTableNumber()) return 1;
                else return 0;
            case BEST_SCO_ORDER :   // Order according to Score
            case BEST_SCR_ORDER :   // Order according to Score and rating
                ScoredPlayer wSP1 = hmScoredPlayers.get(wP1.getKeyString());
                ScoredPlayer bSP1 = hmScoredPlayers.get(bP1.getKeyString());
                ScoredPlayer wSP2 = hmScoredPlayers.get(wP2.getKeyString());
                ScoredPlayer bSP2 = hmScoredPlayers.get(bP2.getKeyString());
                ScoredPlayerComparator spc = new ScoredPlayerComparator(pps, roundNumber, true);
                ScoredPlayer bestSP1 = wSP1;
                if (gameOrderType == BEST_SCR_ORDER && spc.compare(wSP1, bSP1)== 0 && wSP1.getRating() < bSP1.getRating()) bestSP1 = bSP1;
                if (spc.compare(wSP1, bSP1)== -1) bestSP1 = bSP1;
                ScoredPlayer bestSP2 = wSP2;
                if (gameOrderType == BEST_SCR_ORDER && spc.compare(wSP2, bSP2)== 0 && wSP2.getRating() < bSP2.getRating()) bestSP2 = bSP2;
                if (spc.compare(wSP2, bSP2)== -1) bestSP2 = bSP2;
                
                int compareScore = spc.compare(bestSP1, bestSP2);
                int compareTotal = compareScore;
                if (gameOrderType == BEST_SCR_ORDER && compareScore == 0){
                    if (bestSP1.getRating() > bestSP2.getRating()) compareTotal = -1;
                    else compareTotal = 1;
                }
                return compareTotal;
            default :
                return 0;

        }
    }
}
