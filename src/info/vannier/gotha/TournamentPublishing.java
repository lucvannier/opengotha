package info.vannier.gotha;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luc
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
        
        if (pubPS.isExportHFToOGSite()) sendByFTPToOGSite(tournament, f);
        
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
                
        return f;
    }
    
    public static FTPClient connectToFTPOGSite() throws Exception{ 
        String strHost = "s206369267.onlinehome.fr";
        String strLogin = "u45348341-ogt";
        String strPassword = "hmeannnk";
        
        FTPClient client = new FTPClient();
        client.connect(strHost);
        client.login(strLogin, strPassword);
        
        return client;
    }


     public static String sendByFTPToOGSite(TournamentInterface tournament, File f) {
        GeneralParameterSet gps = null;
        String shortName = "defaultTournament";
        try {
            gps = tournament.getTournamentParameterSet().getGeneralParameterSet();
            shortName = tournament.getTournamentParameterSet().getGeneralParameterSet().getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPublish.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        FTPClient client = null;
        try {
            client = connectToFTPOGSite();
        } catch (Exception ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
             return "Error - FTP connection has failed";
        }
        
        String dirName = new SimpleDateFormat("yyyyMMdd").format(gps.getBeginDate()) + shortName;
        try {
            client.createDirectory(dirName);
        } catch (Exception ex) {
            // System.out.println("Création de répertoire a échoué");
        }
        try {
            client.changeDirectory(dirName);
            client.upload(f);
        } catch (IllegalStateException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPDataTransferException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPAbortedException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            File cssFile = new java.io.File(f.getParent(), "current.css");
            client.upload(cssFile);
//            File idxFile = new java.io.File(f.getParent(), "index.php");
//            client.upload(idxFile);
            client.upload(new java.io.File(f.getParent(), "whitestone.png"));
            client.upload(new java.io.File(f.getParent(), "blackstone.png"));
        } catch (Exception ex) {
            //System.out.println("Exception" + ex.toString());
        }
        try {
            client.disconnect(true);
        } catch (Exception ex) {
            Logger.getLogger(JFrPublish.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strURL = "" + f.getName() + " has been successfully uploaded to opengotha.info/tournaments/" + dirName + "/" + f.getName();
        return strURL;
    }

    public static String deleteOGHTMLFiles(TournamentInterface tournament) {
        GeneralParameterSet gps = null;
        String shortName = "defaultTournament";
        try {
            gps = tournament.getTournamentParameterSet().getGeneralParameterSet();
            shortName = tournament.getTournamentParameterSet().getGeneralParameterSet().getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPublish.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        FTPClient client = null;
        try {
            client = connectToFTPOGSite();
            
        } catch (Exception ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
             return "Error - FTP connection has failed";
        }

        String dirName = new SimpleDateFormat("yyyyMMdd").format(gps.getBeginDate()) + shortName;

        String[] files = null;
        int nbDeletedHTMLFiles = 0;
        int nbDeletedFiles = 0;
        int nbDeletedDir = 0;
        try {
            client.changeDirectory(dirName);
            files = client.listNames();
            for (int i = 0; i < files.length; i++){
                String fn = files[i];
                if (fn.endsWith(".html")){
                    client.deleteFile(fn);
                    nbDeletedFiles++;
                    nbDeletedHTMLFiles++;
                }
                if (fn.endsWith(".css") || fn.endsWith(".png")){
                    client.deleteFile(fn);
                    nbDeletedFiles++;
                }
            }
            
        } catch (IllegalStateException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPDataTransferException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPAbortedException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPListParseException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        try {
            client.changeDirectoryUp();
            client.deleteDirectory(dirName);
            nbDeletedDir++;
        } catch (IllegalStateException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(TournamentPublishing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            client.disconnect(true);
        } catch (Exception ex) {
            Logger.getLogger(JFrPublish.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return "" + nbDeletedFiles + " files have been deleted, including " + nbDeletedHTMLFiles + " html files." +
                "\n" + nbDeletedDir + " directory has  been deleted.";
    }
}
