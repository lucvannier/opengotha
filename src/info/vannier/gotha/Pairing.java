package info.vannier.gotha;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Contains a set of static methods related to pairing and pairing reports
 */
public class Pairing {
    public static final int PAIRING_GROUP_MIN_SIZE = 100;
    public static final int PAIRING_GROUP_MAX_SIZE = 3 * PAIRING_GROUP_MIN_SIZE; // must be >= 2 * PAIRING_GROUP_MIN_SIZE

    /**
     * returns the number of games played by sP as White
     * - the number of games played by sP as Black
     *   from round 0 to rn included
     */
    public static int wbBalance(ScoredPlayer sP, int rn) {
        if (rn < 0) {
            return 0;
        }
        int balance = 0;
        for (int r = 0; r <= rn; r++) {
            Game g = sP.getGame(r);
            if (g == null) {
                continue;
            }
            if (g.getHandicap() != 0) {
                continue;
            }
            if (sP.hasSameKeyString(g.getWhitePlayer())) {
                balance++;
            } else {
                balance--;
            }
        }
        return balance;
    }

    public static long detRandom(long max, Player p1, Player p2) {
        long nR = 0;
        String s = p1.getName() + p1.getFirstName() + p2.getName() + p2.getFirstName();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            nR += c * (i + 1);
        }
        nR = (nR * 1234567) % (max + 1);
        return nR;
    }

    public static long nonDetRandom(long max) {
        if (max == 0) {
            return 0;
        }
        double r = Math.random() * (max + 1);
        return (long) r;
    }

    public static String notShownUpInPreviousRoundReport(TournamentInterface tournament, int roundNumber) {
        ArrayList<Game> alG = null;
        ArrayList<Player> alP = null;
        try {
            alG = tournament.gamesList(roundNumber);
            alP = tournament.getPlayersWhoDidNotShowUp(roundNumber - 1);
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }

        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alG, gameComparator);


        String strReport = "";
        int nbNotShownUp = 0;
        for (Player p : alP) {
            for (Game g : alG) {
                if (p.hasSameKeyString(g.getWhitePlayer()) ||
                        p.hasSameKeyString(g.getBlackPlayer())) {
                    nbNotShownUp++;
                    strReport += "\n" + p.fullName();
                }

            }
        }


        strReport = "Number of paired players who had not shown up in previous round : " + nbNotShownUp + strReport;

        return strReport;
    }

    public static String mmsDiffGreaterThanReport(TournamentInterface tournament, int roundNumber, int mmsDiffThreshold) {
        ArrayList<Game> alG = null;
        try {
            alG = tournament.gamesList(roundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alG, gameComparator);

        int nbGamesMMSDiffGreaterThan = 0;
        String strReport = "";
        for (Game g : alG) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            int mms2W = 0;
            int mms2B = 0;

            try {
                mms2W = tournament.mms2(wP, roundNumber);
                mms2B = tournament.mms2(bP, roundNumber);
            } catch (RemoteException ex) {
                Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
            }
            int diffMMS2 = Math.abs(mms2W - mms2B);
            if (diffMMS2 / 2 > mmsDiffThreshold) {
                nbGamesMMSDiffGreaterThan++;
                String strDiffMMS = "" + (diffMMS2 /2) + ((diffMMS2%2 == 1)? ".5" : "");
                strReport += "\nTable " + (g.getTableNumber() + 1) + " : MMSdiff=" + strDiffMMS + " " +
                        wP.fullName() + "(" + mms2W/2 + ") - " + bP.fullName() + "(" + mms2B/2 + ")";
            }
        }
        strReport = "Number of pairs with a MMS difference greater than " + mmsDiffThreshold + " : " + nbGamesMMSDiffGreaterThan +
                strReport;
        return strReport;
    }

    public static String handicapGreaterThanReport(TournamentInterface tournament, int roundNumber, int handicapThreshold) {
        ArrayList<Game> alG = null;
        try {
            alG = tournament.gamesList(roundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alG, gameComparator);

        int nbGamesHandicapGreaterThan = 0;
        String strReport = "";
        for (Game g : alG) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            int hd = g.getHandicap();
            if (hd > handicapThreshold) {
                nbGamesHandicapGreaterThan++;
                strReport += "\nTable " + (g.getTableNumber() + 1) + " : handicap = " + hd + " " + wP.fullName() + " - " + bP.fullName();
            }
        }
        strReport = "Number of pairs with a handicap greater than " + handicapThreshold + " : " + nbGamesHandicapGreaterThan +
                strReport;
        return strReport;
    }

    public static String unbalancedMMSDUDDPlayersReport(TournamentInterface tournament, int roundNumber) {
        ArrayList<Player> alP = null;
        try {
            alP = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int playersSortType = PlayerComparator.RANK_ORDER;
        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(alP, playerComparator);

        ArrayList<Game> alG = null;
        try {
            alG = tournament.gamesListBefore(roundNumber + 1);
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }

        int nbPlayers = alP.size();
        int[] nbDU = new int[nbPlayers];
        int[] nbDD = new int[nbPlayers];
        for (int i = 0; i < nbPlayers; i++){
            nbDU[i] = nbDD[i] = 0;
        }

        for (Game g: alG){
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            int mmsW = 0;
            int mmsB = 0;
            try {
                mmsW = tournament.mms2(wP, g.getRoundNumber()) / 2;
                mmsB = tournament.mms2(bP, g.getRoundNumber()) / 2;
            } catch (RemoteException ex) {
                Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (mmsW == mmsB) continue;
            for (int i = 0; i < nbPlayers; i++){
                Player p = alP.get(i);
                if (wP.hasSameKeyString(p)){
                    if (mmsW > mmsB) nbDD[i]++;
                    if (mmsW < mmsB) nbDU[i]++;
                }
                if (bP.hasSameKeyString(p)){
                    if (mmsB > mmsW) nbDD[i]++;
                    if (mmsB < mmsW) nbDU[i]++;
                }
            }
        }

        int nbUnbalancedDUDDPlayers = 0;
        String strReport = "";
        for (int i = 0; i < nbPlayers; i++){
            Player p = alP.get(i);
            int bal = nbDU[i] -nbDD[i];
            if (bal != 0){
                nbUnbalancedDUDDPlayers++;
                String strBal = "" + bal;
                if (bal > 0) strBal = "+" + bal;
                strReport +="\n" + p.fullName() + " " + Player.convertIntToKD(p.getRank()) + " balance = " + strBal +
                        " " + nbDU[i] + "DU " + nbDD[i] + "DD";

            }
        }

        strReport = "Number of players with an unbalanced MMS draw up/down : " + nbUnbalancedDUDDPlayers +
                strReport;

        return strReport;
    }

    public static String unbalancedWBPlayersReport(TournamentInterface tournament, int roundNumber, int unbalancedWBThreshold){
        ArrayList<Player> alP = null;
        try {
            alP = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int playersSortType = PlayerComparator.RANK_ORDER;
        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(alP, playerComparator);

        String strReport = "";
        int nbUnbalancedWBPlayers = 0;
        for (Player p : alP){
            ArrayList<Game> alG = null;
            try {
                alG = (ArrayList<Game>) tournament.gamesPlayedBy(p);
            } catch (RemoteException ex) {
                Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
            }
            int nbW = 0;
            int nbB = 0;
            for (Game g : alG){
                if (g.getRoundNumber() > roundNumber) continue;
                if (g.getHandicap() > 0) continue;
                if (!g.isKnownColor()) continue;
                if (g.getWhitePlayer().hasSameKeyString(p)) nbW++;
                if (g.getBlackPlayer().hasSameKeyString(p)) nbB++;
            }
            if (Math.abs(nbW - nbB) > unbalancedWBThreshold){
                nbUnbalancedWBPlayers++;
                strReport +="\n" + p.fullName() + " " + Player.convertIntToKD(p.getRank()) +
                        " " + nbW + "W " + nbB + "B";

            }

        }

         strReport = "Number of players with White/Black unbalance greater than " +
                unbalancedWBThreshold + " : " + nbUnbalancedWBPlayers +
                strReport;

        return strReport;
    }

    public static String intraClubPairingReport(TournamentInterface tournament, int roundNumber) {
        ArrayList<Game> alG = null;
        try {
            alG = tournament.gamesList(roundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alG, gameComparator);

        String strReport = "";
        int nbIntraClubPairs = 0;
        for (Game g : alG) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            String clubW = wP.getClub();
            String clubB = bP.getClub();

            if (clubW.equals(clubB)) {
                nbIntraClubPairs++;
                strReport += "\nTable " + (g.getTableNumber() + 1) + " : club = " + clubW + " " + wP.fullName() + " - " + bP.fullName();
            }
        }
        strReport = "Number of intra-club pairs : " + nbIntraClubPairs + strReport;

        return strReport;
    }

    public static String intraCountryPairingReport(TournamentInterface tournament, int roundNumber) {
        ArrayList<Game> alG = null;
        try {
            alG = tournament.gamesList(roundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alG, gameComparator);

        String strReport = "";
        int nbIntraCountryPairs = 0;
        for (Game g : alG) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            String countryW = wP.getCountry();
            String countryB = bP.getCountry();

            if (countryW.equals(countryB)) {
                nbIntraCountryPairs++;
                strReport += "\nTable " + (g.getTableNumber() + 1) + " : country = " + countryW + " " + wP.fullName() + " - " + bP.fullName();
            }
        }
        strReport = "Number of intra-country pairs : " + nbIntraCountryPairs + strReport;

        return strReport;
    }

}

