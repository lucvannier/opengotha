package info.vannier.gotha;

import java.io.File;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Luc Vannier
 */
public class TournamentPublishing {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_PLAYERSLIST = 1;
    public static final int TYPE_TEAMSLIST = 2;
    public static final int TYPE_GAMESLIST = 11;
    public static final int TYPE_RESULTSHEETS = 12;
    public static final int TYPE_NOTPLAYINGLIST = 13;
    public static final int TYPE_MATCHESLIST = 14;
    public static final int TYPE_STANDINGS = 21;
    public static final int TYPE_TEAMSSTANDINGS = 22;
    public static final int TYPE_TOURNAMENT_PARAMETERS = 101;
    
    public static final int SUBTYPE_DEFAULT = 0;
    public static final int SUBTYPE_ST_CAT = 1; // Standings by cat

    public static void publish(TournamentInterface tournament, int roundNumber, int type, int subtype){
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        publish(tournament, tps, roundNumber, type, subtype);
    }
    
    /**
     * 
     * @param tournament
     * @param tps may differ of tournament.getTournamentParameterSet() (in Standings, when use temporary parameter set)
     * @param roundNumber
     * @param type
     * @param subtype 
     */
    public static void publish(TournamentInterface tournament, TournamentParameterSet tps, int roundNumber, int type, int subtype){
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        if (pubPS.isPrint()) print(tournament, tps, roundNumber, type, subtype);
        
        File f;
        f = exportToLocalFile(tournament, tps, roundNumber, type, subtype);
                
    }
    
    private static void print(TournamentInterface tournament, TournamentParameterSet tps, int roundNumber, int type, int subtype){
        switch(type){
            case TournamentPublishing.TYPE_PLAYERSLIST:
                TournamentPrinting.printPlayersList(tournament);
                break;
            case TournamentPublishing.TYPE_TEAMSLIST:
                TournamentPrinting.printTeamsList(tournament);
                break;
            case TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS:
                TournamentPrinting.printTournamentParameters(tournament);
                break;
            case TournamentPublishing.TYPE_GAMESLIST:
                TournamentPrinting.printGamesList(tournament, roundNumber);
                break;
            case TournamentPublishing.TYPE_RESULTSHEETS:
                TournamentPrinting.printResultSheets(tournament, roundNumber);
                break;
            case TournamentPublishing.TYPE_NOTPLAYINGLIST:
                TournamentPrinting.printNotPlayingPlayersList(tournament, roundNumber);
                break;
            case TournamentPublishing.TYPE_STANDINGS:
                TournamentPrinting.printStandings(tournament, tps, roundNumber);
                break;
            case TournamentPublishing.TYPE_MATCHESLIST:
                TournamentPrinting.printMatchesList(tournament, roundNumber);
                break;
            case TournamentPublishing.TYPE_TEAMSSTANDINGS:
                TournamentPrinting.printTeamsStandings(tournament, roundNumber);
                break;
        }
    }

    public static File exportToLocalFile(TournamentInterface tournament, TournamentParameterSet tps, int roundNumber, int type, int subtype){
        File f = null;
        switch(type){
            case TournamentPublishing.TYPE_PLAYERSLIST:
                f = ExternalDocument.generatePlayersListHTMLFile(tournament);
                break;
            case TournamentPublishing.TYPE_TEAMSLIST:
                f = ExternalDocument.generateTeamsListHTMLFile(tournament);
                break;
            case TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS:
                // 
                break;
            case TournamentPublishing.TYPE_GAMESLIST:
                f = ExternalDocument.generateGamesListHTMLFile(tournament, roundNumber);
                break;
             case TournamentPublishing.TYPE_RESULTSHEETS:
                //
                break;
            case TournamentPublishing.TYPE_NOTPLAYINGLIST:
                // 
                break;
            case TournamentPublishing.TYPE_STANDINGS:
                f = ExternalDocument.generateStandingsHTMLFile(tournament, roundNumber);
                break;
            case TournamentPublishing.TYPE_MATCHESLIST:
                f = ExternalDocument.generateMatchesListHTMLFile(tournament, roundNumber);
                break;
            case TournamentPublishing.TYPE_TEAMSSTANDINGS:
                f = ExternalDocument.generateTeamsStandingsHTMLFile(tournament, roundNumber);
                break;
        }
        if (f != null){
            String strMessage = f + " has been generated";
            JOptionPane.showMessageDialog(null, strMessage, "Message", JOptionPane.INFORMATION_MESSAGE);
        }        
        return f;
    }
    


}
