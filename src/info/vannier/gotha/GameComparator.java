package info.vannier.gotha;

import java.io.Serializable;
import java.util.*;

public class GameComparator implements Comparator<Game>, Serializable{
    public final static int NO_ORDER = 0;
//    public final static int GAME_NUMBER_ORDER = 1;
    public final static int TABLE_NUMBER_ORDER = 2;
//    public final static int BEST_RATING_ORDER = 3;
    public final static int BEST_MMS_ORDER = 4;

    int gameOrderType = GameComparator.NO_ORDER;
    HashMap<String, ScoredPlayer> hmScoredPlayers;

    public GameComparator(int gameOrderType){
        this.gameOrderType = gameOrderType;
    }

    public GameComparator(int gameOrderType, HashMap<String, ScoredPlayer> hmScoredPlayers){
        this.gameOrderType = gameOrderType;
        this.hmScoredPlayers = new HashMap<String, ScoredPlayer>(hmScoredPlayers);
    }

    @Override
    public int compare(Game g1, Game g2){
        Player wP1 = g1.getWhitePlayer();
        Player bP1 = g1.getBlackPlayer();
        Player wP2 = g2.getWhitePlayer();
        Player bP2 = g2.getBlackPlayer();
        int best1, best2;
        switch (gameOrderType){
            case TABLE_NUMBER_ORDER :
                if (g1.getTableNumber() < g2.getTableNumber()) return -1;
                else if (g1.getTableNumber() > g2.getTableNumber()) return 1;
                else return 0;
//            case BEST_RATING_ORDER :
//                best1 = wP1.getRating();
//                if (bP1.getRating() > best1) best1 = bP1.getRating();
//                best2 = wP2.getRating();
//                if (bP2.getRating() > best1) best2 = bP2.getRating();
//                if (best1 < best2) return 1;
//                else return -1;
            case BEST_MMS_ORDER :
                ScoredPlayer wSP1 = hmScoredPlayers.get(wP1.getKeyString());
                ScoredPlayer bSP1 = hmScoredPlayers.get(bP1.getKeyString());
                int wMMS1 = wSP1.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, g1.getRoundNumber() -1);
                int bMMS1 = bSP1.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, g1.getRoundNumber() -1);
                int mms1 = Math.max(wMMS1, bMMS1);
                ScoredPlayer wSP2 = hmScoredPlayers.get(wP2.getKeyString());
                ScoredPlayer bSP2 = hmScoredPlayers.get(bP2.getKeyString());
                int wMMS2 = wSP2.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, g2.getRoundNumber() -1);
                int bMMS2 = bSP2.getCritValue(PlacementParameterSet.PLA_CRIT_MMS, g2.getRoundNumber() -1);
                int mms2 = Math.max(wMMS2, bMMS2);
                if (mms1 < mms2) return 1;
                if (mms1 > mms2) return -1;
                // If mms1 = mms2, compare RATING
                best1 = wP1.getRating();
                if (bP1.getRating() > best1) best1 = bP1.getRating();
                best2 = wP2.getRating();
                if (bP2.getRating() > best1) best2 = bP2.getRating();
                if (best1 < best2) return 1;
                if (best1 > best2) return -1;
                // last artificial criterion (to have a deterministic order
                String str1 = wP1.getKeyString();
                String str2 = wP2.getKeyString();
                if (str1.compareTo(str2) >= 0) return 1;
                else return -1;
            default :
                    return 0;

        }
    }
}
