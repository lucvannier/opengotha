/**
 * ExternalDocument.java 
 */
package info.vannier.gotha;

import java.io.*;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * ExternalTournamentDocument enables importing Players and Games and tournament parameters from a file
 * 
 * @author Luc Vannier
 */
public class ExternalDocument {

    public final static String DEFAULT_CHARSET = "UTF-8";
    public final static int DT_UNDEFINED = 0;
    public final static int DT_H9 = 1;
    public final static int DT_TOU = 2;
    public final static int DT_XML = 3;

    public static void importPlayersAndGamesFromPlainFile(File f, String importType, ArrayList<Player> alPlayers, ArrayList<Game> alGames) {
        if (importType.equals("h9")) {
//            LogElements.incrementElement("tournament.import.h9", "");
        }
        if (importType.equals("tou")) {
//            LogElements.incrementElement("tournament.import.tou", "");
        }
        if (importType.equals("wallist")) {
//            LogElements.incrementElement("tournament.import.wallist", "");
        }

        // Def values are ok for for FFG99 aka TOU files
        int posNaFi = 4;    // name, firstname
        int nbcNaFi = 23;

        ArrayList<String> alLines = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader d = new BufferedReader(new InputStreamReader(fis, java.nio.charset.Charset.forName("ISO-8859-15")));

            String s;
            do {
                s = d.readLine();
                if (s != null) {
                    alLines.add(s);
                }
            } while (s != null);
            d.close();
        } catch (Exception ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Parse player lines
        ArrayList<PotentialHalfGame> alPotentialHalfGames = new ArrayList<PotentialHalfGame>();
        for (String strLine : alLines) {
            if (strLine.length() < 10) {
                continue;
            }
            if (strLine.charAt(0) == ';') {
                continue;
            }

            String strNaFi;
            String strRemaining;
            if (importType.equals("wallist")) {
                String[] tabStrSplit = strLine.split("\t", 3);
                strNaFi = tabStrSplit[1];
                strRemaining = tabStrSplit[2];
            } else {
                strNaFi = Gotha.sousChaine(strLine, posNaFi, posNaFi + nbcNaFi);
                strRemaining = strLine.substring(posNaFi + nbcNaFi);
            }
            // Find out strNa and strFi from strNaFi
            strNaFi = strNaFi.trim();
            String strNa;
            String strFi;
            String[] strSplit = strNaFi.split(" ");
            int nbFields = strSplit.length;
            if (nbFields == 1) {
                strNa = strSplit[0];
                strFi = "x";    // Just because an empty first name is not allowed
            } else {
                strFi = strSplit[nbFields - 1];
                strNa = strNaFi.substring(0, strNaFi.length() - strFi.length() - 1);
            }

            String strRank = "";
            if (importType.equals("wallist")) {
                String[] tabStrSplit = strRemaining.split("\t", 2);
                strRank = tabStrSplit[0];
                strRemaining = tabStrSplit[1];
            } else {
                while (!Character.isDigit(strRemaining.charAt(0))) {
                    strRemaining = strRemaining.substring(1);
                }
                strRank = Gotha.sousChaine(strRemaining, 0, 3);
                strRemaining = strRemaining.substring(3);
            }

            String strCountry = "";
            if (importType.equals("wallist")) {
                String[] tabStrSplit = strRemaining.split("\t", 2);
                strCountry = tabStrSplit[0];
                strRemaining = tabStrSplit[1];
            } else if (importType.equals("h9")) {
                strRemaining = strRemaining.trim();
                strCountry = strRemaining.substring(0, 3);
                strRemaining = strRemaining.substring(3);
            } else if (importType.equals("tou")) {
                strRemaining = strRemaining.substring(Math.min(strRemaining.length(), 8));
            }
            strCountry = strCountry.trim();
            if (strCountry.length() > 2) {
                strCountry = strCountry.substring(0, 2);
            }

            String strClub;
            if (importType.equals("wallist")) {
                String[] tabStrSplit = strRemaining.split("\t", 2);
                strClub = tabStrSplit[0];
                strRemaining = tabStrSplit[1];
            } else {
                strRemaining = strRemaining.trim();
                strClub = Gotha.sousChaine(strRemaining, 0, 4);
                strRemaining = strRemaining.substring(strClub.length());
            }

            strRemaining = strRemaining.replace("\t", " ");

            String strGames;
            int pos = 0;
            for (int i = 0; i < strRemaining.length(); i++) {
                char c = strRemaining.charAt(i);
                if (c == '+' || c == '-' || c == '='
                        || c == '/' || c == '?' || c == '!') {
                    pos = i;
                    break;
                }
            }
            while (pos > 0 && strRemaining.charAt(pos) != ' ') {
                pos = pos - 1;
            }

            strGames = strRemaining.substring(pos).trim();

            Player p;
            try {
                p = new Player(
                        strNa,
                        strFi,
                        strCountry,
                        strClub,
                        "", // EGF Pin
                        "", // FFG Licence
                        "", // FFG Licence Status
                        "", // AGA Id
                        "", // AGA Expiration Date
                        Player.convertKDPToInt(strRank),
                        Player.convertKDPToInt(strRank) * 100,
                        "INI",
                        "",
                        0,
                        "FIN");
                boolean[] bPart = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
                for (int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
                    bPart[i] = true;
                }
                p.setParticipating(bPart);
            } catch (PlayerException pe) {
                JOptionPane.showMessageDialog(null, pe.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            alPlayers.add(p);
            int currentPlayerNumber = alPlayers.size() - 1;
            parseResultLine(currentPlayerNumber, strGames, alPotentialHalfGames);
        }
        // Process pseudo half games
        // ie set the player absent for current player and round
        for (int i = alPotentialHalfGames.size() - 1; i >= 0; i--) {
            PotentialHalfGame phg = alPotentialHalfGames.get(i);

            int opponentNumber = phg.opponentNumber;
            if (opponentNumber < 0) {
                int playerNumber = phg.playerNumber;
                int roundNumber = phg.roundNumber;
                Player p = alPlayers.get(playerNumber);
                p.setParticipating(roundNumber, false);
                alPotentialHalfGames.remove(i);
            }
        }

        // Build alGames
        buildALGames(alPotentialHalfGames, alPlayers, alGames);
    }

    public static void importPlayersFromVBSFile(File f, ArrayList<Player> alPlayers) {
//        LogElements.incrementElement("tournament.import.vbs", "");

        ArrayList<String> alLines = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader d = new BufferedReader(new InputStreamReader(fis, java.nio.charset.Charset.forName("ISO-8859-15")));

            String s;
            do {
                s = d.readLine();
                if (s != null) {
                    alLines.add(s);
                }
            } while (s != null);
            d.close();
        } catch (Exception ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Parse player lines
        for (String strLine : alLines) {
            int pos = strLine.indexOf(";");
            if (pos >= 0) {
                strLine = strLine.substring(0, pos - 1);
            }
            if (strLine.length() < 8) {
                continue;
            }
            String[] tabStr = strLine.split("\\|");

            String strNa = tabStr[0].trim();
            String strFi = tabStr[1].trim();
            String strRk = tabStr[2].trim();
            String strCl = tabStr[3].trim();
            String strCo = tabStr[4].trim();
            String strRt = tabStr[5].trim();
            String strRg = tabStr[6].trim();

            if (strCl.length() > 4) {
                strCl = strCl.substring(0, 4);
            }
            if (strCo.length() < 2) {
                strCo = "";
            }

            int rk = Player.convertKDPToInt(strRk);

            int rt = 0;
            String strRatingOrigin = "MAN";
            try {
                rt = Integer.parseInt(strRt);
            } catch (Exception e) {
                rt = rk * 100;
                strRatingOrigin = "INI";
            }

            strRg = strRg.toLowerCase();
            if (strRg.charAt(0) == 'p') {
                strRg = "PRE";
            } else {
                strRg = "FIN";
            }

            Player p = null;
            try {
                p = new Player(
                        strNa,
                        strFi,
                        strCo,
                        strCl,
                        "", // EGF Pin
                        "", // FFG Licence
                        "", // FFG Licence Status
                        "", // AGA Id
                        "", // AGA Expiration Date
                        rk,
                        rt,
                        strRatingOrigin,
                        "",
                        0,
                        strRg);
                boolean[] bPart = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
                for (int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
                    bPart[i] = true;
                }
                p.setParticipating(bPart);
            } catch (PlayerException pe) {
                JOptionPane.showMessageDialog(null, pe.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            alPlayers.add(p);
        }
    }

    public static Document getDocumentFromXMLFile(File sourceFile) {
        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("Wrong parser configuration: " + e.getMessage());
            return null;
        }
        try {
            doc = docBuilder.parse(sourceFile);
        } catch (SAXException e) {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Could not read source file: " + e.getMessage());
        }
        return doc;
    }

    public static String importTournamentFromXMLFile(File sourceFile, TournamentInterface tournament, 
            boolean bPlayers, boolean bGames, boolean bTPS, boolean bTeams, boolean bClubsGroups) {
        // What dataVersion ?
        long dataVersion = ExternalDocument.importDataVersionFromXMLFile(sourceFile);

        int nbImportedPlayers = 0;
        int nbNotImportedPlayers = 0;
        if (bPlayers) {
            ArrayList<Player> alPlayers = ExternalDocument.importPlayersFromXMLFile(sourceFile);
            if (alPlayers == null || alPlayers.isEmpty()) {
                System.out.println("No player has been imported");
            }
            if (alPlayers != null) {
                for (Player p : alPlayers) {
                    try {
                        tournament.addPlayer(p);
                        nbImportedPlayers++;
                    } catch (TournamentException ex) {
                        Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RemoteException ex) {
                        Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                nbNotImportedPlayers = alPlayers.size() - nbImportedPlayers;
            }
        }

        int nbImportedGames = 0;
        int nbNotImportedGames = 0;
        int nbReplacedGames = 0;
        int nbImportedByePlayers = 0;
        int nbReplacedByePlayers = 0;

        if (bGames) {
            // import games
            int nbGamesBeforeImport = 0;
            try {
                nbGamesBeforeImport = tournament.gamesList().size();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            ArrayList<Game> alGames = ExternalDocument.importGamesFromXMLFile(sourceFile, tournament);
            if (alGames == null) {
                System.out.println("No game could be imported");
            }

            if (alGames != null) {
                for (Game g : alGames) {
                    try {
                        tournament.addGame(g);
                        nbImportedGames++;
                    } catch (TournamentException ex) {
                        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RemoteException ex) {
                        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                nbNotImportedGames = alGames.size() - nbImportedGames;
            }

            int nbGamesAfterImport = 0;
            try {
                nbGamesAfterImport = tournament.gamesList().size();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            nbReplacedGames = nbImportedGames - (nbGamesAfterImport - nbGamesBeforeImport);

            // import bye players
            Player[] importedByePlayers = ExternalDocument.importByePlayersFromXMLFile(sourceFile, tournament);
            Player[] byePlayers = null;
            try {
                byePlayers = tournament.getByePlayers();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int r = 0; r < byePlayers.length; r++) {
                if (importedByePlayers[r] == null) {
                    continue;
                }
                nbImportedByePlayers++;
                if (byePlayers[r] != null) {
                    nbReplacedByePlayers++;
                }
                try {
                    tournament.assignByePlayer(importedByePlayers[r], r);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        if (bTPS) {
            // import tournament parameters
            TournamentParameterSet tps = ExternalDocument.importTournamentParameterSetFromXMLFile(sourceFile);
            if (tps == null) {
                System.out.println("No parameter could be imported");
            }
            if (tps != null) {
                try {
                    tournament.setTournamentParameterSet(tps);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        int nbImportedTeams = 0;
        int nbNotImportedTeams = 0;
        int nbReplacedTeams = 0;

        if (bTeams) {
            // import teams

            int nbTeamsBeforeImport = 0;
            try {
                nbTeamsBeforeImport = tournament.teamsList().size();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            ArrayList<Team> alTeams = ExternalDocument.importTeamsFromXMLFile(sourceFile, tournament);
            if (alTeams != null) {
                for (Team t : alTeams) {
                    try {
                        tournament.addTeam(t);
                        nbImportedTeams++;
                    } catch (RemoteException ex) {
                        Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                nbNotImportedTeams = alTeams.size() - nbImportedTeams;
            }

            int nbTeamsAfterImport = 0;
            try {
                nbTeamsAfterImport = tournament.teamsList().size();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            nbReplacedTeams = nbImportedTeams - (nbTeamsAfterImport - nbTeamsBeforeImport);

            // import team tournament parameters
            TeamTournamentParameterSet ttps = ExternalDocument.importTeamTournamentParameterSetFromXMLFile(sourceFile);
            if (ttps == null) {
                System.out.println("No team parameter could be imported");
            }
            if (ttps != null) {
                try {
                    tournament.setTeamTournamentParameterSet(ttps);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        // Import Clubs groups
        int nbImportedClubsGroups = 0;
        int nbNotImportedClubsGroups = 0;
        int nbReplacedClubsGroups = 0;
        if (bClubsGroups){
            int nbClubsGroupsBeforeImport = 0;
            try {
                nbClubsGroupsBeforeImport = tournament.clubsGroupsList().size();
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
            ArrayList<ClubsGroup> alClubsGroups = ExternalDocument.importClubsGroupsFromXMLFile(sourceFile);
            if (alClubsGroups != null) {              
                for(ClubsGroup cg : alClubsGroups){
                    try {
                        if (tournament.addClubsGroup(cg)) nbImportedClubsGroups++;
                    } catch (RemoteException ex) {
                        Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    nbNotImportedClubsGroups = alClubsGroups.size() - nbImportedClubsGroups;
                }
            }
            int nbClubsGroupsAfterImport = 0;
            try {
                nbClubsGroupsAfterImport = tournament.clubsGroupsList().size();
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }

            nbReplacedClubsGroups = nbClubsGroupsBeforeImport + nbImportedClubsGroups - nbClubsGroupsAfterImport;
        }
      
        

        try {
            tournament.updateNumberOfRoundsIfNecesary();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Report about imported objects
        String strReport = "" + nbImportedPlayers + " players have been imported";
        if (nbNotImportedPlayers > 0) {
            strReport += "\n" + nbNotImportedPlayers + " players could not be imported.";
        }

        strReport += "\n\n" + nbImportedGames + " games have been imported.";
        if (nbNotImportedGames > 0) {
            strReport = "\n" + nbNotImportedGames + " games could not be imported.";
        }
        if (nbReplacedGames > 0) {
            strReport += "\n" + nbReplacedGames + " games have been replaced.";
        }

        strReport += "\n\n" + nbImportedByePlayers + " bye players have been imported.";
        if (nbReplacedByePlayers > 0) {
            strReport += "\n" + nbReplacedByePlayers + " bye players have been replaced.";
        }

        strReport += "\n\n" + nbImportedTeams + " Teams have been imported.";
        if (nbNotImportedTeams > 0) {
            strReport = "\n" + nbNotImportedTeams + " Teams could not be imported.";
        }
        if (nbReplacedTeams > 0) {
            strReport += "\n" + nbReplacedTeams + " Teams have been replaced.";
        }

        strReport += "\n\n" + nbImportedClubsGroups + " Clubs Groups have been imported.";
        if (nbNotImportedClubsGroups > 0) {
            strReport += "\n" + nbNotImportedClubsGroups + " Clubs Groups could not be imported.";
        }
        if (nbReplacedClubsGroups > 0) {
            strReport += "\n" + nbReplacedClubsGroups + " Clubs Groups have been replaced.";
        }

        return strReport;
    }

    private static long importDataVersionFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        long dataVersion;
        if (doc == null) {
            return 0L;
        }
        NodeList nl = doc.getElementsByTagName("Tournament");
        Node n = nl.item(0);
        NamedNodeMap nnm = n.getAttributes();
        String strDataVersion = extractNodeValue(nnm, "dataVersion", "200");
        dataVersion = Long.parseLong(strDataVersion);

        return dataVersion;

    }

    private static ArrayList<Player> importPlayersFromXMLFile(File sourceFile) {
        long currentDataVersion = Gotha.GOTHA_DATA_VERSION;
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);

        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }
        ArrayList<Player> alPlayers = new ArrayList<Player>();

        NodeList nl = doc.getElementsByTagName("Player");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();

            String name = extractNodeValue(nnm, "name", "");
            String firstName = extractNodeValue(nnm, "firstName", "");
            String country = extractNodeValue(nnm, "country", "");
            String club = extractNodeValue(nnm, "club", "");
            String egfPin = extractNodeValue(nnm, "egfPin", "");
            String ffgLicence = extractNodeValue(nnm, "ffgLicence", "");
            String ffgLicenceStatus = extractNodeValue(nnm, "ffgLicenceStatus", "");          
            String agaId = extractNodeValue(nnm, "agaId", "");
            String agaExpirationDate = extractNodeValue(nnm, "agaExpirationDate", "");
            String strRank = extractNodeValue(nnm, "rank", "30K");
            int rank = Player.convertKDPToInt(strRank);
            String strRating = extractNodeValue(nnm, "rating", "-900");
            int rating = new Integer(strRating).intValue();
            if (importedDataVersion < 201L) {
                rating += 2050;
            }
            if (rating > Player.MAX_RATING) {
                rating = Player.MAX_RATING;
            }
            if (rating < Player.MIN_RATING) {
                rating = Player.MIN_RATING;
            }

            String ratingOrigin = extractNodeValue(nnm, "ratingOrigin", "");
            String strGrade = extractNodeValue(nnm, "grade", "");
            String strSmmsCorrection = extractNodeValue(nnm, "smmsCorrection", "0");
            int smmsCorrection = new Integer(strSmmsCorrection).intValue();
            String strDefaultParticipating = "";
            for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                strDefaultParticipating += "1";
            }
            String strParticipating = extractNodeValue(nnm, "participating", strDefaultParticipating);
            boolean[] participating = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
            for (int r = 0; r < participating.length; r++) {
                try {
                    char cPart = strParticipating.charAt(r);
                    if (cPart == '0') {
                        participating[r] = false;
                    } else {
                        participating[r] = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    participating[r] = true;
                }
            }
            String registeringStatus = extractNodeValue(nnm, "registeringStatus", "FIN");
            Player p = null;
            try {
                p = new Player(name, firstName, country, club, egfPin, ffgLicence, ffgLicenceStatus,
                        agaId, agaExpirationDate, rank, rating, ratingOrigin, strGrade, smmsCorrection, registeringStatus);
            } catch (PlayerException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
            p.setParticipating(participating);

            alPlayers.add(p);
        }
        return alPlayers;
    }

    private static TournamentParameterSet importTournamentParameterSetFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        // Is there a TournamentParameterSet node in file ?
        NodeList nlTPS = doc.getElementsByTagName("TournamentParameterSet");
        if (nlTPS == null || nlTPS.getLength() == 0) {
            return null;
        }

        TournamentParameterSet tps = new TournamentParameterSet();

        //GPS
        GeneralParameterSet gps = new GeneralParameterSet();
        NodeList nlGPS = doc.getElementsByTagName("GeneralParameterSet");
        Node nGPS = nlGPS.item(0);
        NamedNodeMap nnmGPS = nGPS.getAttributes();

        String shortName = extractNodeValue(nnmGPS, "shortName", "defaultshortname");
        gps.setShortName(shortName);
        String name = extractNodeValue(nnmGPS, "name", "default Name");
        gps.setName(name);
        String location = extractNodeValue(nnmGPS, "location", "Paris");
        gps.setLocation(location);
        String director = extractNodeValue(nnmGPS, "director", "");
        gps.setDirector(director);
        String strBeginDate = extractNodeValue(nnmGPS, "beginDate", "2000-01-01");
        try {
            gps.setBeginDate(new SimpleDateFormat("yyyy-MM-dd").parse(strBeginDate));
        } catch (ParseException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        String strEndDate = extractNodeValue(nnmGPS, "endDate", "2000-01-01");
        try {
            gps.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(strEndDate));
        } catch (ParseException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        int time = extractNodeIntValue(nnmGPS, "time", GeneralParameterSet.GEN_GP_BASICTIME_DEF);    // For old dataVersion
        gps.setBasicTime(extractNodeIntValue(nnmGPS, "basicTime", time));

        // Complementary time for old dataVersion
        String strByoYomi = extractNodeValue(nnmGPS, "byoYomi", "true");
        boolean bByoYomi = Boolean.valueOf(strByoYomi).booleanValue();
        if (bByoYomi) {
            gps.setComplementaryTimeSystem(GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI);
            gps.setNbMovesCanTime(GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_DEF);
            gps.setCanByoYomiTime(GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_DEF);
        } else {
            gps.setComplementaryTimeSystem(GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH);
        }

        String strCTS = extractNodeValue(nnmGPS, "complementaryTimeSystem", "CANBYOYOMI");
        int cts = GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI;
        if (strCTS.equals("SUDDENDEATH")) {
            cts = GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH;
        }
        if (strCTS.equals("STDBYOYOMI")) {
            cts = GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI;
        }
        if (strCTS.equals("CANBYOYOMI")) {
            cts = GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI;
        }
        if (strCTS.equals("FISCHER")) {
            cts = GeneralParameterSet.GEN_GP_CTS_FISCHER;
        }
        gps.setComplementaryTimeSystem(cts);

        gps.setStdByoYomiTime(extractNodeIntValue(nnmGPS, "stdByoYomiTime", GeneralParameterSet.GEN_GP_CTS_STDBYOYOMITIME_DEF));
        gps.setNbMovesCanTime(extractNodeIntValue(nnmGPS, "nbMovesCanTime", GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_DEF));
        gps.setCanByoYomiTime(extractNodeIntValue(nnmGPS, "canByoYomiTime", GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_DEF));
        gps.setFischerTime(extractNodeIntValue(nnmGPS, "fischerTime", GeneralParameterSet.GEN_GP_CTS_FISCHERTIME_DEF));

        String strSize = extractNodeValue(nnmGPS, "size", "19");
        gps.setStrSize(strSize);
        String strKomi = extractNodeValue(nnmGPS, "komi", "7.5");
        gps.setStrKomi(strKomi);
        String strNumberOfRounds = extractNodeValue(nnmGPS, "numberOfRounds", "5");
        gps.setNumberOfRounds(new Integer(strNumberOfRounds).intValue());
        String strNumberOfCategories = extractNodeValue(nnmGPS, "numberOfCategories", "1");
        int nbCategories = new Integer(strNumberOfCategories).intValue();
        gps.setNumberOfCategories(nbCategories);

        NodeList nl = doc.getElementsByTagName("Category");
        int[] lowerLimits = new int[nbCategories - 1];
        for (int c = 0; c < nl.getLength(); c++) {
            Node n = nl.item(c);
            NamedNodeMap nnm = n.getAttributes();
            String strNumber = extractNodeValue(nnm, "number", "1");
            String strLowerLimit = extractNodeValue(nnm, "lowerLimit", "30K");
            int numCat = new Integer(strNumber).intValue() - 1;
            lowerLimits[numCat] = Player.convertKDPToInt(strLowerLimit);
        }
        gps.setLowerCategoryLimits(lowerLimits);

        String strGenMMFloor = extractNodeValue(nnmGPS, "genMMFloor", "20K");
        gps.setGenMMFloor(Player.convertKDPToInt(strGenMMFloor));
        String strGenMMBar = extractNodeValue(nnmGPS, "genMMBar", "4D");
        gps.setGenMMBar(Player.convertKDPToInt(strGenMMBar));
        String strGenMMZero = extractNodeValue(nnmGPS, "genMMZero", "30K");
        gps.setGenMMZero(Player.convertKDPToInt(strGenMMZero));

        String strGenNBW2ValueAbsent = extractNodeValue(nnmGPS, "genNBW2ValueAbsent", "0");
        gps.setGenNBW2ValueAbsent(new Integer(strGenNBW2ValueAbsent).intValue());

        String strGenNBW2ValueBye = extractNodeValue(nnmGPS, "genNBW2ValueBye", "0");
        gps.setGenNBW2ValueBye(new Integer(strGenNBW2ValueBye).intValue());

        String strGenMMS2ValueAbsent = extractNodeValue(nnmGPS, "genMMS2ValueAbsent", "0");
        gps.setGenMMS2ValueAbsent(new Integer(strGenMMS2ValueAbsent).intValue());

        String strGenMMS2ValueBye = extractNodeValue(nnmGPS, "genMMS2ValueBye", "0");
        gps.setGenMMS2ValueBye(new Integer(strGenMMS2ValueBye).intValue());
        
        String strGenRoundDownNBWMMS = extractNodeValue(nnmGPS, "genRoundDownNBWMMS", "true");
        gps.setGenRoundDownNBWMMS(Boolean.valueOf(strGenRoundDownNBWMMS).booleanValue());

        tps.setGeneralParameterSet(gps);

        // HPS
        HandicapParameterSet hps = new HandicapParameterSet();
        NodeList nlHPS = doc.getElementsByTagName("HandicapParameterSet");
        Node nHPS = nlHPS.item(0);
        NamedNodeMap nnmHPS = nHPS.getAttributes();

        String strHdBasedOnMMS = extractNodeValue(nnmHPS, "hdBasedOnMMS", "true");
        hps.setHdBasedOnMMS(Boolean.valueOf(strHdBasedOnMMS).booleanValue());
        String strHdNoHdRankThreshold = extractNodeValue(nnmHPS, "hdNoHdRankThreshold", "1D");
        hps.setHdNoHdRankThreshold(Player.convertKDPToInt(strHdNoHdRankThreshold));
        String strHdCorrection = extractNodeValue(nnmHPS, "hdCorrection", "1");
        hps.setHdCorrection(new Integer(strHdCorrection).intValue());
        String strHdCeiling = extractNodeValue(nnmHPS, "hdCeiling", "9");
        hps.setHdCeiling(new Integer(strHdCeiling).intValue());
        tps.setHandicapParameterSet(hps);

        // PPS
        PlacementParameterSet pps = new PlacementParameterSet();
        NodeList nlPPS = doc.getElementsByTagName("PlacementParameterSet");
        ArrayList<Node> alCritNodes = extractNodes(nlPPS.item(0), "PlacementCriterion");

        int[] plaC = new int[TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];
        for (int nC = 0; nC < plaC.length; nC++) {
            plaC[nC] = PlacementParameterSet.PLA_CRIT_NUL;
        }
        for (Node n : alCritNodes) {
            NamedNodeMap nnm = n.getAttributes();
            String strNumber = extractNodeValue(nnm, "number", "1");
            int number = new Integer(strNumber).intValue();
            String strName = extractNodeValue(nnm, "name", "NULL");
            for (int nPC = 0; nPC < PlacementParameterSet.allPlacementCriteria.length; nPC++) {
                PlacementCriterion pC = PlacementParameterSet.allPlacementCriteria[nPC];
                if (strName.equals(pC.longName)) {
                    plaC[number - 1] = pC.uid;
                    break;
                }
            }

        }

        pps.setPlaCriteria(plaC);

        tps.setPlacementParameterSet(pps);

        //paiPS
        PairingParameterSet paiPS = new PairingParameterSet();
        NodeList nlPaiPS = doc.getElementsByTagName("PairingParameterSet");
        Node nPaiPS = nlPaiPS.item(0);
        NamedNodeMap nnmPaiPS = nPaiPS.getAttributes();

        paiPS.setPaiStandardNX1Factor(new Double(extractNodeValue(nnmPaiPS, "paiStandardNX1Factor", "0.5")).doubleValue());
        paiPS.setPaiBaAvoidDuplGame(new Long(extractNodeValue(nnmPaiPS, "paiBaAvoidDuplGame", "500000000000000")).longValue());
        paiPS.setPaiBaRandom(new Long(extractNodeValue(nnmPaiPS, "paiBaRandom", "0")).longValue());
        paiPS.setPaiBaDeterministic(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiBaDeterministic", "true")).booleanValue());
        paiPS.setPaiBaBalanceWB(new Long(extractNodeValue(nnmPaiPS, "paiBaBalanceWB", "1000")).longValue());
        paiPS.setPaiMaAvoidMixingCategories(new Long(extractNodeValue(nnmPaiPS, "paiMaAvoidMixingCategories", "20000000000000")).longValue());
        paiPS.setPaiMaMinimizeScoreDifference(new Long(extractNodeValue(nnmPaiPS, "paiMaMinimizeScoreDifference", "100000000000")).longValue());
        paiPS.setPaiMaDUDDWeight(new Long(extractNodeValue(nnmPaiPS, "paiMaDUDDWeight", "100000000")).longValue());
        paiPS.setPaiMaCompensateDUDD(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiMaCompensateDUDD", "true")).booleanValue());
        
        String strDUDDU = extractNodeValue(nnmPaiPS, "paiMaDUDDUpperMode", "MID");
        int duddu = PairingParameterSet.PAIMA_DUDD_MID;
        if (strDUDDU.equals("TOP")) {
            duddu = PairingParameterSet.PAIMA_DUDD_TOP;
        }
        if (strDUDDU.equals("MID")) {
            duddu = PairingParameterSet.PAIMA_DUDD_MID;
        }
        if (strDUDDU.equals("BOT")) {
            duddu = PairingParameterSet.PAIMA_DUDD_BOT;
        }
        paiPS.setPaiMaDUDDUpperMode(duddu);

        String strDUDDL = extractNodeValue(nnmPaiPS, "paiMaDUDDLowerMode", "MID");
        int duddl = PairingParameterSet.PAIMA_DUDD_MID;
        if (strDUDDL.equals("TOP")) {
            duddl = PairingParameterSet.PAIMA_DUDD_TOP;
        }
        if (strDUDDL.equals("MID")) {
            duddl = PairingParameterSet.PAIMA_DUDD_MID;
        }
        if (strDUDDL.equals("BOT")) {
            duddl = PairingParameterSet.PAIMA_DUDD_BOT;
        }
        paiPS.setPaiMaDUDDLowerMode(duddl);
        paiPS.setPaiMaMaximizeSeeding(new Long(extractNodeValue(nnmPaiPS, "paiMaMaximizeSeeding", "5000000")).longValue());
        paiPS.setPaiMaLastRoundForSeedSystem1(new Integer(extractNodeValue(nnmPaiPS, "paiMaLastRoundForSeedSystem1", "2")).intValue() - 1);

        String strS1 = extractNodeValue(nnmPaiPS, "paiMaSeedSystem1", "SPLITANDRANDOM");
        int s1 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        if (strS1.equals("SPLITANDRANDOM")) {
            s1 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        }
        if (strS1.equals("SPLITANDFOLD")) {
            s1 = PairingParameterSet.PAIMA_SEED_SPLITANDFOLD;
        }
        if (strS1.equals("SPLITANDSLIP")) {
            s1 = PairingParameterSet.PAIMA_SEED_SPLITANDSLIP;
        }
        paiPS.setPaiMaSeedSystem1(s1);

        String strS2 = extractNodeValue(nnmPaiPS, "paiMaSeedSystem2", "SPLITANDFOLD");
        int s2 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        if (strS2.equals("SPLITANDRANDOM")) {
            s2 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        }
        if (strS2.equals("SPLITANDFOLD")) {
            s2 = PairingParameterSet.PAIMA_SEED_SPLITANDFOLD;
        }
        if (strS2.equals("SPLITANDSLIP")) {
            s2 = PairingParameterSet.PAIMA_SEED_SPLITANDSLIP;
        }
        paiPS.setPaiMaSeedSystem2(s2);

        String strAddCrit1 = extractNodeValue(nnmPaiPS, "paiMaAdditionalPlacementCritSystem1", "RATING");
        int aCrit1 = PlacementParameterSet.PLA_CRIT_RATING;
        for (int nPC = 0; nPC < PlacementParameterSet.allPlacementCriteria.length; nPC++) {
            PlacementCriterion pC = PlacementParameterSet.allPlacementCriteria[nPC];
            if (strAddCrit1.equals(pC.longName)) {
                aCrit1 = pC.uid;
                break;
            }
        }
        paiPS.setPaiMaAdditionalPlacementCritSystem1(aCrit1);

        String strAddCrit2 = extractNodeValue(nnmPaiPS, "paiMaAdditionalPlacementCritSystem2", "NULL");
        int aCrit2 = PlacementParameterSet.PLA_CRIT_NUL;
        for (int nPC = 0; nPC < PlacementParameterSet.allPlacementCriteria.length; nPC++) {
            PlacementCriterion pC = PlacementParameterSet.allPlacementCriteria[nPC];
            if (strAddCrit2.equals(pC.longName)) {
                aCrit2 = pC.uid;
                break;
            }
        }
        paiPS.setPaiMaAdditionalPlacementCritSystem2(aCrit2);

        paiPS.setPaiSeRankThreshold(Player.convertKDPToInt(extractNodeValue(nnmPaiPS, "paiSeRankThreshold", "4D")));
        paiPS.setPaiSeNbWinsThresholdActive(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiSeNbWinsThresholdActive", "true")).booleanValue());
        paiPS.setPaiSeBarThresholdActive(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiSeBarThresholdActive", "true")).booleanValue());
        paiPS.setPaiSeDefSecCrit(new Long(extractNodeValue(nnmPaiPS, "paiSeDefSecCrit", "100000000000")).longValue());
        paiPS.setPaiSeMinimizeHandicap(new Long(extractNodeValue(nnmPaiPS, "paiSeMinimizeHandicap", "0")).longValue());
        paiPS.setPaiSeAvoidSameGeo(new Long(extractNodeValue(nnmPaiPS, "paiSeAvoidSameGeo", "100000000000")).longValue());
        paiPS.setPaiSePreferMMSDiffRatherThanSameCountry(new Integer(extractNodeValue(nnmPaiPS, "paiSePreferMMSDiffRatherThanSameCountry", "1")).intValue());
        paiPS.setPaiSePreferMMSDiffRatherThanSameClubsGroup(new Integer(extractNodeValue(nnmPaiPS, "paiSePreferMMSDiffRatherThanSameClubsGroup", "2")).intValue());
        paiPS.setPaiSePreferMMSDiffRatherThanSameClub(new Integer(extractNodeValue(nnmPaiPS, "paiSePreferMMSDiffRatherThanSameClub", "3")).intValue());

        tps.setPairingParameterSet(paiPS);

        // DPPS
        DPParameterSet dpps = new DPParameterSet();
        NodeList nlDPPS = doc.getElementsByTagName("DPParameterSet");
        Node nDPPS = nlDPPS.item(0);
        if (nDPPS != null) {
            NamedNodeMap nnmDPPS = nDPPS.getAttributes();
            
            String strPlayerSortType = extractNodeValue(nnmDPPS, "playerSortType", "name");
            int playerSortType = PlayerComparator.NAME_ORDER;
            if (strPlayerSortType.equals("rank")) playerSortType = PlayerComparator.RANK_ORDER;
            if (strPlayerSortType.equals("grade")) playerSortType = PlayerComparator.GRADE_ORDER;
            dpps.setPlayerSortType(playerSortType);
            
            String strGameFormat = extractNodeValue(nnmDPPS, "gameFormat", "full");
            int gameFormat = DPParameterSet.DP_GAME_FORMAT_FULL;
            if (strGameFormat.equals("short")) gameFormat = DPParameterSet.DP_GAME_FORMAT_SHORT;
            dpps.setGameFormat(gameFormat);

            String strShowPlayerGrade = extractNodeValue(nnmDPPS, "showPlayerGrade", "true");
            dpps.setShowPlayerGrade(Boolean.valueOf(strShowPlayerGrade).booleanValue());
            String strShowPlayerCountry = extractNodeValue(nnmDPPS, "showPlayerCountry", "false");
            dpps.setShowPlayerCountry(Boolean.valueOf(strShowPlayerCountry).booleanValue());
            String strShowPlayerClub = extractNodeValue(nnmDPPS, "showPlayerClub", "true");
            dpps.setShowPlayerClub(Boolean.valueOf(strShowPlayerClub).booleanValue());

            String strShowByePlayer = extractNodeValue(nnmDPPS, "showByePlayer", "true");
            dpps.setShowByePlayer(Boolean.valueOf(strShowByePlayer).booleanValue());
            String strShowNotPairedPlayers = extractNodeValue(nnmDPPS, "showNotPairedPlayers", "true");
            dpps.setShowNotPairedPlayers(Boolean.valueOf(strShowNotPairedPlayers).booleanValue());
            String strShowNotParticipatingPlayers = extractNodeValue(nnmDPPS, "showNotParticipatingPlayers", "true");
            dpps.setShowNotParticipatingPlayers(Boolean.valueOf(strShowNotParticipatingPlayers).booleanValue());
            String strShowNotFinallyRegisteredPlayers = extractNodeValue(nnmDPPS, "showNotFinallyRegisteredPlayers", "true");
            dpps.setShowNotFinallyRegisteredPlayers(Boolean.valueOf(strShowNotFinallyRegisteredPlayers).booleanValue());

            String strDisplayNumCol = extractNodeValue(nnmDPPS, "displayNumCol", "true");
            dpps.setDisplayNumCol(Boolean.valueOf(strDisplayNumCol).booleanValue());
            String strDisplayPlCol = extractNodeValue(nnmDPPS, "displayPlCol", "true");
            dpps.setDisplayPlCol(Boolean.valueOf(strDisplayPlCol).booleanValue());
            String strDisplayCoCol = extractNodeValue(nnmDPPS, "displayCoCol", "true");
            dpps.setDisplayCoCol(Boolean.valueOf(strDisplayCoCol).booleanValue());
            String strDisplayClCol = extractNodeValue(nnmDPPS, "displayClCol", "false");
            dpps.setDisplayClCol(Boolean.valueOf(strDisplayClCol).booleanValue());
            
            String strDisplayIndGamesInMatches = extractNodeValue(nnmDPPS, "displayIndGamesInMatches", "true");
            dpps.setDisplayIndGamesInMatches(Boolean.valueOf(strDisplayIndGamesInMatches).booleanValue());          
        }
        tps.setDPParameterSet(dpps);

        // PubPS
        PublishParameterSet pubPS = new PublishParameterSet();
        NodeList nlPubPS = doc.getElementsByTagName("PublishParameterSet");
        Node nPubPS = nlPubPS.item(0);
        if (nPubPS != null) {
            NamedNodeMap nnmPubPS = nPubPS.getAttributes();
            
            String strPrint = extractNodeValue(nnmPubPS, "print", "true");
            pubPS.setPrint(Boolean.valueOf(strPrint).booleanValue());
            String strExportToLocalFile = extractNodeValue(nnmPubPS, "exportToLocalFile", "true");
            pubPS.setExportToLocalFile(Boolean.valueOf(strExportToLocalFile).booleanValue());
            String strExportHFToOGSite = extractNodeValue(nnmPubPS, "exportHFToOGSite", "false");
            pubPS.setExportHFToOGSite(Boolean.valueOf(strExportHFToOGSite).booleanValue());
            String strExportTFToOGSite = extractNodeValue(nnmPubPS, "exportTFToOGSite", "true");
            pubPS.setExportTFToOGSite(Boolean.valueOf(strExportTFToOGSite).booleanValue());
            String strExportToUDSite = extractNodeValue(nnmPubPS, "exportToUDSite", "false");
            pubPS.setExportToUDSite(Boolean.valueOf(strExportToUDSite).booleanValue());  
            String strHtmlAutoScroll = extractNodeValue(nnmPubPS, "htmlAutoScroll", "false");
            pubPS.setHtmlAutoScroll(Boolean.valueOf(strHtmlAutoScroll).booleanValue());  
        }
        tps.setPublishParameterSet(pubPS);

        return tps;
    }

    private static ArrayList<Team> importTeamsFromXMLFile(File sourceFile, TournamentInterface tournament) {
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        NodeList nlTeamList = doc.getElementsByTagName("Team");
        
        if (nlTeamList == null || nlTeamList.getLength() == 0) {
            return null;
        }

        ArrayList<Team> alTeams = new ArrayList<Team>();
        for (int i = 0; i < nlTeamList.getLength(); i++) {
            Node nTeam = nlTeamList.item(i);
            NamedNodeMap nnmTeam = nTeam.getAttributes();

            String strTeamNumber = extractNodeValue(nnmTeam, "teamNumber", "1");
            String strTeamName = extractNodeValue(nnmTeam, "teamName", "Unnamed team");
            int teamNumber = new Integer(strTeamNumber).intValue() - 1;
            String teamName = strTeamName;
            Team t = new Team(teamNumber, teamName);

            NodeList nlElements = nTeam.getChildNodes();
            for (int iel = 0; iel < nlElements.getLength(); iel++) {
                Node nBoard = nlElements.item(iel);
                if (nBoard.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if (!nBoard.getNodeName().equals("Board")) {
                    continue;
                }
                NamedNodeMap nnmBoard = nBoard.getAttributes();
                String strRoundNumber = extractNodeValue(nnmBoard, "roundNumber", "0");
                int roundNumber = new Integer(strRoundNumber).intValue() - 1;
                String strBoardNumber = extractNodeValue(nnmBoard, "boardNumber", "1");
                int boardNumber = new Integer(strBoardNumber).intValue() - 1;
                String strPlayer = extractNodeValue(nnmBoard, "player", "unnamed player");
                Player p;
                try {
                    if (importedDataVersion <= 200) p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
                    else p = tournament.getPlayerByKeyString(strPlayer);
                    if (p == null) {
                        strPlayer = Gotha.forceToASCII(strPlayer).toUpperCase();
                        p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
                    }
                    if (p == null) continue;
                } catch (RemoteException ex) {
                    Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                if (roundNumber < 0){ // Before V3.28.04, roundNumber was not documented. So, set TeamMember for all rounds
                    for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++){
                        t.setTeamMember(p, r, boardNumber);
                    }
                    
                }
                else{
                    t.setTeamMember(p, roundNumber, boardNumber);
                }
            }
            alTeams.add(t);
        }
        return alTeams;
    }

    private static TeamTournamentParameterSet importTeamTournamentParameterSetFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        // Is there a TeamTournamentParameterSet node in file ?
        NodeList nlTTPS = doc.getElementsByTagName("TeamTournamentParameterSet");
        if (nlTTPS == null || nlTTPS.getLength() == 0) {
            return null;
        }

        TeamTournamentParameterSet ttps = new TeamTournamentParameterSet();

        // TGPS
        TeamGeneralParameterSet tgps = new TeamGeneralParameterSet();
        NodeList nlTGPS = doc.getElementsByTagName("TeamGeneralParameterSet");
        Node nTGPS = nlTGPS.item(0);
        NamedNodeMap nnmTGPS = nTGPS.getAttributes();

        String strTeamSize = extractNodeValue(nnmTGPS, "teamSize", "4");
        int teamSize = Integer.parseInt(strTeamSize);
        tgps.setTeamSize(teamSize);

        ttps.setTeamGeneralParameterSet(tgps);

        // TPPS
        TeamPlacementParameterSet tpps = new TeamPlacementParameterSet();
        NodeList nlTPPS = doc.getElementsByTagName("TeamPlacementParameterSet");
        ArrayList<Node> alCritNodes = extractNodes(nlTPPS.item(0), "PlacementCriterion");

        int[] plaC = new int[TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];
        for (int nC = 0; nC < plaC.length; nC++) {
            plaC[nC] = PlacementParameterSet.PLA_CRIT_NUL;
        }
        for (Node n : alCritNodes) {
            NamedNodeMap nnm = n.getAttributes();
            String strNumber = extractNodeValue(nnm, "number", "1");
            int number = new Integer(strNumber).intValue();
            String strName = extractNodeValue(nnm, "name", "NULL");
            for (int nPC = 0; nPC < TeamPlacementParameterSet.allPlacementCriteria.length; nPC++) {
                PlacementCriterion pC = TeamPlacementParameterSet.allPlacementCriteria[nPC];
                if (strName.equals(pC.longName)) {
                    plaC[number - 1] = pC.uid;
                    break;
                }
            }
        }
        tpps.setPlaCriteria(plaC);
        ttps.setTeamPlacementParameterSet(tpps);

        return ttps;
    }

    private static ArrayList<ClubsGroup> importClubsGroupsFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }
         
        NodeList nlClubsGroupList = doc.getElementsByTagName("ClubsGroup"); 
        // Is there a ClubsGroups node in file ?        
        if (nlClubsGroupList == null || nlClubsGroupList.getLength() == 0) {
            return null;
        }
        
        ArrayList<ClubsGroup> alClubsGroups = new ArrayList<ClubsGroup>();
        for(int i = 0; i < nlClubsGroupList.getLength(); i++){
            Node nClubsGroup = nlClubsGroupList.item(i);
            NamedNodeMap nnmClubsGroup = nClubsGroup.getAttributes();
            String strCGName = extractNodeValue(nnmClubsGroup, "name", "Unnamed Clubs Group");
            ClubsGroup cg = new ClubsGroup(strCGName);
            
            NodeList nlElements = nClubsGroup.getChildNodes();
            for(int iel = 0; iel < nlElements.getLength(); iel++){
                Node nClub = nlElements.item(iel);
                if (nClub.getNodeType() != Node.ELEMENT_NODE) continue;
                if (!nClub.getNodeName().equals("Club")) continue;
                NamedNodeMap nnmClub = nClub.getAttributes();
                String strClubName = extractNodeValue(nnmClub, "name", "Unnamed club");
                Club club = new Club(strClubName);
                cg.put(club);
            }
            alClubsGroups.add(cg);
        }
        return alClubsGroups;
    }

    public static String extractNodeValue(NamedNodeMap nnm, String attributeName, String defaultValue) {
        String value = defaultValue;
        Node node = nnm.getNamedItem(attributeName);
        if (node != null) {
            value = node.getNodeValue();
        }
        return value;
    }

    public static int extractNodeIntValue(NamedNodeMap nnm, String attributeName, int defaultValue) {
        String strValue = extractNodeValue(nnm, attributeName, "");
        int value = defaultValue;
        try {
            value = Integer.parseInt(strValue);
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * recursively searches a Node tree for nodes with a given name
     * @param nodeBase
     * @param nodeName
     * @return an ArrayList of all found Nodes
     */
    public static ArrayList<Node> extractNodes(Node nodeBase, String nodeName) {
        ArrayList<Node> alNodes = new ArrayList<Node>();
        NodeList nlElements = nodeBase.getChildNodes();
        for (int iel = 0; iel < nlElements.getLength(); iel++) {
            Node n = nlElements.item(iel);
            if (n.getNodeName().equals(nodeName)) {
                alNodes.add(n);
            }
            ArrayList<Node> alN = extractNodes(n, nodeName);
            alNodes.addAll(alN);
        }
        return alNodes;
    }

    private static ArrayList<Game> importGamesFromXMLFile(File sourceFile, TournamentInterface tournament) {
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        ArrayList<Game> alGames = new ArrayList<Game>();
        NodeList nl = doc.getElementsByTagName("Game");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();

            String strRoundNumber = extractNodeValue(nnm, "roundNumber", "1");
            int roundNumber = new Integer(strRoundNumber).intValue() - 1;
            String strTableNumber = extractNodeValue(nnm, "tableNumber", "1");
            int tableNumber = new Integer(strTableNumber).intValue() - 1;
            String strWhitePlayer = extractNodeValue(nnm, "whitePlayer", "");
            String strBlackPlayer = extractNodeValue(nnm, "blackPlayer", "");
            Player wP;
            Player bP;
            try {
                if (importedDataVersion <= 200) wP = tournament.getPlayerByObsoleteCanonicalName(strWhitePlayer);
                else wP = tournament.getPlayerByKeyString(strWhitePlayer);
                if (wP == null) {
                    strWhitePlayer = Gotha.forceToASCII(strWhitePlayer).toUpperCase();
                    wP = tournament.getPlayerByObsoleteCanonicalName(strWhitePlayer);
                }
                if (wP == null) continue;
                
                if (importedDataVersion <= 200) bP = tournament.getPlayerByObsoleteCanonicalName(strBlackPlayer);
                else bP = tournament.getPlayerByKeyString(strBlackPlayer);
                if (bP == null) {
                    strBlackPlayer = Gotha.forceToASCII(strBlackPlayer).toUpperCase();
                    bP = tournament.getPlayerByObsoleteCanonicalName(strBlackPlayer);
                }
                if (bP == null) continue;

            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            String strKnownColor = extractNodeValue(nnm, "strKnownColor", "true");
            boolean knownColor = true;
            if (strKnownColor.equals("false")) {
                knownColor = false;
            }
            String strHandicap = extractNodeValue(nnm, "handicap", "0");
            int handicap = new Integer(strHandicap).intValue();
            String strResult = extractNodeValue(nnm, "result", "RESULT_UNKNOWN");
            int result = Game.RESULT_UNKNOWN;
            if (strResult.equals("RESULT_WHITEWINS")) {
                result = Game.RESULT_WHITEWINS;
            }
            if (strResult.equals("RESULT_BLACKWINS")) {
                result = Game.RESULT_BLACKWINS;
            }
            if (strResult.equals("RESULT_EQUAL")) {
                result = Game.RESULT_EQUAL;
            }
            if (strResult.equals("RESULT_BOTHLOSE")) {
                result = Game.RESULT_BOTHLOSE;
            }
            if (strResult.equals("RESULT_BOTHWIN")) {
                result = Game.RESULT_BOTHWIN;
            }
            if (strResult.equals("RESULT_WHITEWINS_BYDEF")) {
                result = Game.RESULT_WHITEWINS_BYDEF;
            }
            if (strResult.equals("RESULT_BLACKWINS_BYDEF")) {
                result = Game.RESULT_BLACKWINS_BYDEF;
            }
            if (strResult.equals("RESULT_EQUAL_BYDEF")) {
                result = Game.RESULT_EQUAL_BYDEF;
            }
            if (strResult.equals("RESULT_BOTHLOSE_BYDEF")) {
                result = Game.RESULT_BOTHLOSE_BYDEF;
            }
            if (strResult.equals("RESULT_BOTHWIN_BYDEF")) {
                result = Game.RESULT_BOTHWIN_BYDEF;
            }

            Game g = new Game(roundNumber, tableNumber, wP, bP, true, handicap, result);
            g.setKnownColor(knownColor);
            alGames.add(g);
        }
        return alGames;
    }

    public static Player[] importByePlayersFromXMLFile(File sourceFile, TournamentInterface tournament) {
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);
        Document doc = getDocumentFromXMLFile(sourceFile);

        Player[] byePlayers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS];
        for (int r = 0; r < byePlayers.length; r++) {
            byePlayers[r] = null;
        }

        NodeList nl = doc.getElementsByTagName("ByePlayer");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();
            String strRoundNumber = extractNodeValue(nnm, "roundNumber", "1");
            int roundNumber = new Integer(strRoundNumber).intValue() - 1;
            String strPlayer = extractNodeValue(nnm, "player", "");
            Player p;
            try {
                if (importedDataVersion <= 200) p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
                else p = tournament.getPlayerByKeyString(strPlayer);
                if (p == null) {
                    strPlayer = Gotha.forceToASCII(strPlayer).toUpperCase();
                    p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
                }
                if (p == null) continue;
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            byePlayers[roundNumber] = p;
        }

        return byePlayers;
    }

    /**
     * Gathers potential half-games to build games into an ArrayList<Game>.
     * If necessary, reaffects round numbers so that a given game has been played in one only round, 
     * and so that a given player may not have played 2 games in the same round. 
     **/
    private static void buildALGames(ArrayList<PotentialHalfGame> alPotentialHalfGames, ArrayList<Player> alPlayers, ArrayList<Game> alGames) {
        // Initialize tabGames
        Game[][] tabGames = new Game[Gotha.MAX_NUMBER_OF_ROUNDS][alPotentialHalfGames.size()]; // Of course, it is over-dimensionned
        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            for (int ng = 0; ng < alPotentialHalfGames.size(); ng++) {
                tabGames[r][ng] = new Game();
            }
        }

        // vBProcessedHalfGames is mapped like alPotentialHalfGames. 
        // An element of vBProcessedPHG is set to true when processed
        ArrayList<Boolean> alBProcessedPHG = new ArrayList<Boolean>();
        for (PotentialHalfGame phg : alPotentialHalfGames) {
            alBProcessedPHG.add(false);
        }

        // Process every PotentialHalfGame
        for (int numPHG = 0; numPHG < alPotentialHalfGames.size(); numPHG++) {
            PotentialHalfGame phg = alPotentialHalfGames.get(numPHG);
            if (phg.opponentNumber == -1) {
                continue;
            }
            if (alBProcessedPHG.get(numPHG)) {
                continue;
            }
            int roundNumber = phg.roundNumber;
            int chosenRoundNumber = 0;
            Player p1 = alPlayers.get(phg.playerNumber);
            Player p2 = null;
            p2 = alPlayers.get(phg.opponentNumber);
            if (isASuitableRound(roundNumber, p1, p2, tabGames)) {
                chosenRoundNumber = roundNumber;
            } else {
                for (roundNumber = 0; roundNumber < Gotha.MAX_NUMBER_OF_ROUNDS; roundNumber++) {
                    if (isASuitableRound(roundNumber, p1, p2, tabGames)) {
                        chosenRoundNumber = roundNumber;
                        break;
                    }
                }
            }

            int chosenGameNumber = -1;
            for (int numG = 0; numG < tabGames[chosenRoundNumber].length; numG++) {
                Game game = tabGames[chosenRoundNumber][numG];
                if (game.getWhitePlayer() == null && game.getBlackPlayer() == null) {
                    chosenGameNumber = numG;
                    break;
                }
            }

            // Store this PotentialHalfGame into a Game
            Game g = tabGames[chosenRoundNumber][chosenGameNumber];
            Player player = alPlayers.get(phg.playerNumber);
            Player opponent = alPlayers.get(phg.opponentNumber);

            int res = Game.RESULT_UNKNOWN;
            if (phg.color == 'w') {
                g.setWhitePlayer(player);
                g.setBlackPlayer(opponent);
                if (phg.result == 1) {
                    res = Game.RESULT_WHITEWINS;
                } else if (phg.result == -1) {
                    res = Game.RESULT_BLACKWINS;
                }
                g.setKnownColor(true);
            } else if (phg.color == 'b') {
                g.setWhitePlayer(opponent);
                g.setBlackPlayer(player);
                if (phg.result == 1) {
                    res = Game.RESULT_BLACKWINS;
                } else if (phg.result == -1) {
                    res = Game.RESULT_WHITEWINS;
                }
                g.setKnownColor(true);
            } else {
                g.setWhitePlayer(player);
                g.setBlackPlayer(opponent);
                if (phg.result == 1) {
                    res = Game.RESULT_WHITEWINS;
                } else if (phg.result == -1) {
                    res = Game.RESULT_BLACKWINS;
                }
                g.setKnownColor(false);
            }
            if (phg.bydef) {
                res += Game.RESULT_BYDEF;
            }
            g.setResult(res);

            g.setHandicap(phg.handicap);
            g.setRoundNumber(chosenRoundNumber);

            // Choose a table number
            int tN = 0;
            for (int gN = 0; gN < tabGames[g.getRoundNumber()].length; gN++) {
                Game game = tabGames[g.getRoundNumber()][gN];
                if (tN <= game.getTableNumber()) {
                    tN = game.getTableNumber() + 1;
                }
            }
            g.setTableNumber(tN);

            // Freeze the potential halfGame or both potential halfGames just processed
            alBProcessedPHG.set(numPHG, true);
            for (int nPHG_work = 0; nPHG_work < alPotentialHalfGames.size(); nPHG_work++) {
                PotentialHalfGame phg_work = alPotentialHalfGames.get(nPHG_work);
                if (alBProcessedPHG.get(nPHG_work)) {
                    continue;
                }
                if (phg_work.playerNumber == phg.opponentNumber && phg_work.opponentNumber == phg.playerNumber) {
                    alBProcessedPHG.set(nPHG_work, true);
                    break;
                }
            }
        }

        // Store the games into alGames
        for (int numR = 0; numR < Gotha.MAX_NUMBER_OF_ROUNDS; numR++) {
            for (int numG = 0; numG < alPotentialHalfGames.size(); numG++) {
                Game g = tabGames[numR][numG];
                if (g.getWhitePlayer() != null && g.getBlackPlayer() != null) {
                    alGames.add(g);
                }
            }
        }
    }

    /**
     * isASuitableRound tests whether the referenced game by game can be inserted
     * in round number roundNumber. id est :
     * <br>If a game has already been played by either player of the pair in candidateRoundNumber, returns false.
     * <br>If not, returns true;
     */
    private static boolean isASuitableRound(int candidateRoundNumber, Player p1, Player p2, Game[][] tabGames) {
        for (int ng = 0; ng < tabGames[candidateRoundNumber].length; ng++) {
            Game g = tabGames[candidateRoundNumber][ng];
            if (p1.hasSameKeyString(g.getWhitePlayer())) {
                return false;
            }
            if (p1.hasSameKeyString(g.getBlackPlayer())) {
                return false;
            }
            if (p2.hasSameKeyString(g.getWhitePlayer())) {
                return false;
            }
            if (p2.hasSameKeyString(g.getBlackPlayer())) {
                return false;
            }
        }
        return true;
    }

    public static void generateTouFile(TournamentInterface tournament, File f) {
//        LogElements.incrementElement("export.ffg", "");
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();

        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "ISO-8859-15"));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Headers       
        try {
            String shortName = gps.getShortName();

            output.write(";name=" + shortName);
            output.write("\n;date=" + new SimpleDateFormat("dd/MM/yyyy").format(gps.getBeginDate()));
            output.write("\n;vill=" + gps.getLocation());
            output.write("\n;comm=" + gps.getName());
            output.write("\n;prog=" + Gotha.getGothaVersionnedName());
            output.write("\n;size=" + gps.getStrSize());
            String strByoYomi = "";
            switch (gps.getComplementaryTimeSystem()) {
                case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                    if (gps.getStdByoYomiTime() != 0) {
                        strByoYomi = "+b";
                    }
                    break;
                case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                    if (gps.getCanByoYomiTime() != 0) {
                        strByoYomi = "+b";
                    }
                    break;
                case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                    if (gps.getFischerTime() != 0) {
                        strByoYomi = "+b";
                    }
                    break;
            }
            String strT = "" + gps.getBasicTime() + strByoYomi;
            output.write("\n;time=" + strT);

            output.write("\n;komi=" + gps.getStrKomi());
            output.write("\n; Generated by " + Gotha.getGothaVersionnedName());
            output.write("\n;Num Nom Prenom               Niv Licence Club");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Contents
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = null;
        int roundNumber = gps.getNumberOfRounds() - 1;
        try {
            alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
            // Eliminate non-players
            for (Iterator<ScoredPlayer> it = alOrderedScoredPlayers.iterator(); it.hasNext();) {
                ScoredPlayer sP = it.next();
                if (!tournament.isPlayerImplied(sP)) {
                    it.remove();
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[][] hG = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, roundNumber, tps, true);
        String[] strPlace = ScoredPlayer.positionStrings(alOrderedScoredPlayers, roundNumber, tps);
        for (int iSP = 0; iSP < alOrderedScoredPlayers.size(); iSP++) {
            ScoredPlayer sP = alOrderedScoredPlayers.get(iSP);

            String strLine = "";

            String strPl = "    " + strPlace[iSP];
            strPl = strPl.substring(strPl.length() - 4);
            strLine += strPl;

            String strNF = sP.fullUnblankedName() + "                         ";
            strNF = strNF.substring(0, 25);
            strLine += " " + strNF;

            String strRank = "   " + Player.convertIntToKD(sP.getRank());
            strRank = strRank.substring(strRank.length() - 3);
            strLine += strRank;

            String strLic = "       " + sP.getFfgLicence();
            strLic = strLic.substring(strLic.length() - 7);
            strLine += " " + strLic;

            String strClub = "    " + sP.getClub();
            strClub = strClub.substring(strClub.length() - 4);
            strLine += " " + strClub;
            for (int r = 0; r <= roundNumber; r++) {
                String strHG = "        " + hG[r][iSP];
                strHG = strHG.replace("/", "");
                strHG = strHG.replace("!", "");

                strHG = strHG.substring(strHG.length() - 8);
                strLine += strHG;
            }

            try {
                output.write("\n" + strLine);
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            output.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generateH9File(TournamentInterface tournament, File f, boolean bKeepByDefResults) {
//        LogElements.incrementElement("export.egf", "");
        TournamentParameterSet tps;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();

        // Prepare tabCrit from pps
        int[] tC = pps.getPlaCriteria();
        int[] tabCrit = PlacementParameterSet.purgeUselessCriteria(tC);

        Writer output = null;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "ISO-8859-15"));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Headers       
        try {
            output.write("; CL[" + tournament.egfClass() + "]");
            output.write("\n; EV[" + gps.getName() + "]");
            output.write("\n; PC[," + gps.getLocation() + "]");
            output.write("\n; DT[" + new SimpleDateFormat("yyyy-MM-dd").format(gps.getBeginDate())
                    + "," + new SimpleDateFormat("yyyy-MM-dd").format(gps.getEndDate()) + "]");
            // HA
            String strHA = "";
            int hc = hps.getHdCorrection();
            if (hc > 0) strHA = "\n; HA[h" + hc + "]";
            output.write(strHA);
            //
            output.write("\n; KM[" + gps.getStrKomi() + "]");
            output.write("\n; TM[" + (tournament.egfAdjustedTime() / 60) + "]");
            String strCM = "";
            switch (gps.getComplementaryTimeSystem()) {
                case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                    strCM = "Sudden death";
                    break;
                case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                    strCM = "Standard byo-yomi";
                    break;
                case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                    strCM = "Canadian byo-yomi";
                    break;
                case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                    strCM = "Fischer";
                    break;
            }
            output.write("\n; CM[" + strCM + "]");
            output.write("\n; Generated by " + Gotha.getGothaVersionnedName());
            output.write("\n;");
            output.write("\n; Pl Name                            Rk Co Club");
            for (int c = 0; c < tabCrit.length; c++) {
                String strCritName = PlacementParameterSet.criterionShortName(tabCrit[c]);
                // Make strings with exactly 4 characters
                strCritName = strCritName.trim();
                if (strCritName.length() > 5) {
                    strCritName = strCritName.substring(0, 5);
                }
                while (strCritName.length() < 5) {
                    strCritName = " " + strCritName;
                }

                output.write(strCritName);
            }

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Contents
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = null;
        int roundNumber = gps.getNumberOfRounds() - 1;
        try {
            alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
            // Eliminate non-players
            for (Iterator<ScoredPlayer> it = alOrderedScoredPlayers.iterator(); it.hasNext();) {
                ScoredPlayer sP = it.next();
                if (!tournament.isPlayerImplied(sP)) {
                    it.remove();
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean bFull = true; 
        int gameFormat = tps.getDPParameterSet().getGameFormat();
        if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) bFull = false;
        String[][] hG = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, roundNumber, tps, bFull);
        String[] strPlace = ScoredPlayer.positionStrings(alOrderedScoredPlayers, roundNumber, tps);
        for (int iSP = 0; iSP < alOrderedScoredPlayers.size(); iSP++) {
            ScoredPlayer sP = alOrderedScoredPlayers.get(iSP);

            String strLine = "";

            String strPl = "    " + strPlace[iSP];
            strPl = strPl.substring(strPl.length() - 4);
            strLine += strPl;

            String strNF = sP.fullUnblankedName() + "                             ";
            strNF = strNF.substring(0, 30) + " ";
            strLine += " " + strNF;

            String strRank = "   " + Player.convertIntToKD(sP.getRank());
            strRank = strRank.substring(strRank.length() - 3);
            strLine += strRank;


            String strCountry = sP.getCountry().trim();
            if (strCountry.length() < 2) {
                strCountry = "XX";
            }
            strCountry = "  " + strCountry;
            strCountry = strCountry.substring(strCountry.length() - 2);
            strLine += " " + strCountry;

            String strClub = sP.getClub().trim();
            if (strClub.length() < 1) {
                strClub = "xxxx";
            }
            strClub = "    " + strClub;
            strClub = strClub.substring(strClub.length() - 4);
            strLine += " " + strClub;

            for (int c = 0; c < tabCrit.length; c++) {
                String strCritValue = sP.formatScore(tabCrit[c], roundNumber);
                // Make strings with exactly 4 characters
                strCritValue = strCritValue.trim();
                if (strCritValue.length() > 4) {
                    strCritValue = strCritValue.substring(0, 4);
                }
                while (strCritValue.length() < 4) {
                    strCritValue = " " + strCritValue;
                }

                strLine += " " + strCritValue;
            }
            // If tabCrit.length < 4, fill with dummy values
            for (int c = tabCrit.length; c < 4; c++) {
                strLine += " 0";
            }

            for (int r = 0; r <= roundNumber; r++) {
                String strHG = hG[r][iSP];
                strHG = "        " + strHG;
                strHG = strHG.substring(strHG.length() - 8);
                // Drop the game if by def and !bKeepByDefResults
                if (strHG.indexOf("!") >= 0 && !bKeepByDefResults) {
                    strHG = "      0=";
                }
                strLine += " " + strHG;

            }

            try {
                output.write("\n" + strLine);
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            output.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // This method added by Bart and adapted by Luc (Jan 2012)
    public static void generateAGAResultsFile(TournamentInterface tournament, File f) {
//        LogElements.incrementElement("export.aga", "");
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();

        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "ISO-8859-15"));

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Write  General Tournament Info
        try {
            output.write("Tourney\t" + gps.getName() + ", " + gps.getLocation() + "\n");
            output.write("\tstart=" + new SimpleDateFormat("yyyy-MM-dd").format(gps.getBeginDate()) + "\n");
            output.write("\tfinish=" + new SimpleDateFormat("yyyy-MM-dd").format(gps.getEndDate()) + "\n");
            output.write("PLAYERS\n");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<ScoredPlayer> alOrderedScoredPlayers = null;
        int roundNumber = gps.getNumberOfRounds() - 1;
        try {
            // Build alOrderedScoredPlayers which will be a work copy
            alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Sort on primary criteria
        PlayerComparator pc = new PlayerComparator(PlayerComparator.AGAID_ORDER);
        Collections.sort(alOrderedScoredPlayers, pc);

        // Assign a dummy AGA Id to people without Aga Id
        // For each player without an agaId, find an available Id, starting from 99999 and decreasing
        // To find a not assigned one, search alOrderedScoredPlayers starting from upper index and decreasing  
        int iMax = alOrderedScoredPlayers.size() - 1;
        int newId = 99999;

        boolean somethingHasChanged = false;
        for (Iterator<ScoredPlayer> it = alOrderedScoredPlayers.iterator(); it.hasNext();) {
            ScoredPlayer sP = it.next();
            if (getIntAgaId(sP) > 0) {
                continue;
            }
            // newId should stay above the id assigned to iMax
            while (newId == getIntAgaId(alOrderedScoredPlayers.get(iMax))) {
                newId--;
                iMax--;
            }
            Player p = null;
            try {
                p = tournament.getPlayerByKeyString(sP.getKeyString());
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
            p.setAgaId("" + newId);
            somethingHasChanged = true;
            newId--;
        }

        if (somethingHasChanged){
            try {
                tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Make a new version of alOrderedScoredPlayers, with dummy aga Ids
            try {
                // Build alOrderedScoredPlayers which will be a work copy
                alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Sort on primary criteria
        pc = new PlayerComparator(PlayerComparator.AGAID_ORDER);
        Collections.sort(alOrderedScoredPlayers, pc);
        
        for (Iterator<ScoredPlayer> it = alOrderedScoredPlayers.iterator(); it.hasNext();) {
            ScoredPlayer sP = it.next();
            try {
                output.write(sP.getAgaId() + "\t" + sP.getName() + ", " + sP.getFirstName() + "\t" + Player.convertIntToKD(sP.getRank()) + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (!tournament.isPlayerImplied(sP)) {
                    it.remove();
                }
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Write game results by round
        ArrayList<Game> roundGames = null;
        for (int r = 0; r < gps.getNumberOfRounds(); r++) {
            try {
                output.write("\nGAMES " + (r + 1) + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                roundGames = tournament.gamesList(r);
            } catch (RemoteException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (Iterator<Game> it = roundGames.iterator(); it.hasNext();) {
                try {
                    Game g = it.next();
                    String result = "*";
                    if (g.getResult() == Game.RESULT_WHITEWINS) {
                        result = "W";
                    } else if (g.getResult() == Game.RESULT_BLACKWINS) {
                        result = "B";
                    } else {
                        result = "?";
                    }
                    int hc = g.getHandicap();
                    int komi = 7;
                    if (hc == 1) {
                        hc = 0;
                        komi = 0;
                    }
                    if (hc >= 2) {
                        komi = 0;
                    }

                    output.write(g.getWhitePlayer().getAgaId() + "\t" + g.getBlackPlayer().getAgaId() + "\t" + result + "\t" + hc + "\t" + komi + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


        try {
            output.write("\n\nEND\n");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<Player> playerList = null;
        try {
            playerList = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Iterator<Player> it = playerList.iterator(); it.hasNext();) {
            Player p = it.next();
        }

        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int getIntAgaId(ScoredPlayer sP) {
        int id;
        try {
            id = Integer.parseInt(sP.getAgaId());
        } catch (Exception e) {
            id = 0;
        }
        return id;
    }

    public static void generatePlayersCSVFile(TournamentInterface tournament, File f) {
//        LogElements.incrementElement("export.csv", "");
        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        ArrayList<Player> alPlayers = null;
        try {
            alPlayers = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        PlayerComparator playerComparator = new PlayerComparator(PlayerComparator.RANK_ORDER);
        Collections.sort(alPlayers, playerComparator);
        // Column names

        String strLine = "";
        strLine += "Name" + ";";
        strLine += "FirstName" + ";";
        strLine += "Rank" + ";";
        strLine += "Country" + ";";
        strLine += "Club" + ";";

        try {
            output.write("\n" + strLine);
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Player p : alPlayers) {
            strLine = "";
            strLine += p.getName() + ";";
            strLine += p.getFirstName() + ";";
            strLine += Player.convertIntToKD(p.getRank()) + ";";
            strLine += p.getCountry() + ";";
            strLine += p.getClub() + ";";

            try {
                output.write("\n" + strLine);
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            output.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static File generatePlayersListHTMLFile(TournamentInterface tournament){
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        String defaultFileName = shortName + "_PlayersList";

        File f = new File(Gotha.exportHTMLDirectory, defaultFileName + ".html");
        // Manage css       
        createCSSFile(f);
        
        generatePlayersListHTMLFileContents(tournament, f);
        return f;
    }    
            
    public static File generateTeamsListHTMLFile(TournamentInterface tournament){
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        String defaultFileName = shortName + "_TeamsList";
        File f = new File(Gotha.exportHTMLDirectory, defaultFileName + ".html");
        
        // Manage css       
        Gotha.exportHTMLDirectory = f.getParentFile();
        createCSSFile(f);
        
        generateTeamsListHTMLFileContents(tournament, f);
        return f;
    } 
    
    public static File generateGamesListHTMLFile(TournamentInterface tournament, int round){
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        String defaultFileName = shortName + "_GamesListR" + (round + 1);

        File f = new File(Gotha.exportHTMLDirectory, defaultFileName + ".html");
        
        // Manage css       
        createCSSFile(f);
        
        generateGamesListHTMLFileContents(tournament, round, f);
        return f;
    }    

    
        public static File generateStandingsHTMLFile(TournamentInterface tournament, int round){
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        String defaultFileName = shortName + "_StandingsR" + (round + 1);
        File f = new File(Gotha.exportHTMLDirectory, defaultFileName + ".html");
         
        // Manage css       
        Gotha.exportHTMLDirectory = f.getParentFile();
        createCSSFile(f);
        
        generateStandingsHTMLFileContents(tournament, round, f);
        return f;
    }        
    
    public static File generateMatchesListHTMLFile(TournamentInterface tournament, int round){
        // Choose a File
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        String defaultFileName = shortName + "_MatchesListR" + (round + 1);
        File f = new File(Gotha.exportHTMLDirectory, defaultFileName + ".html");
        
        // Manage css       
        Gotha.exportHTMLDirectory = f.getParentFile();
        createCSSFile(f);
        
        generateMatchesListHTMLFileContents(tournament, round, f);
        return f;
    }
        
    public static File generateTeamsStandingsHTMLFile(TournamentInterface tournament, int round){
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        String defaultFileName = shortName + "_TeamsStandingsR" + (round + 1);
        File f = new File(Gotha.exportHTMLDirectory, defaultFileName + ".html");
        
        // Manage css       
        Gotha.exportHTMLDirectory = f.getParentFile();
        createCSSFile(f);
        
        generateTeamsStandingsHTMLFileContents(tournament, round, f);
        
        return f;
    }
    
    private static void createCSSFile(File f){
               // If current.css does not exist, create one from default.css
        File currentCSSFile = new File(f.getParentFile(), "current.css");
        if (!currentCSSFile.exists()) {
            try {
                FileChannel srcChannel = new FileInputStream(new File(Gotha.runningDirectory, "exportfiles/html/default.css")).getChannel();
                FileChannel dstChannel = new FileOutputStream(new File(f.getParentFile(), "current.css")).getChannel();

                dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
                srcChannel.close();
                dstChannel.close();
            } catch (IOException e) {
                System.out.println("Exception in css file copying");
            }
        }
    }
    
    public static void generatePlayersListHTMLFileContents(TournamentInterface tournament, File f){      
        TournamentParameterSet tps;
        try{
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        boolean bScroll = pubPS.isHtmlAutoScroll();
        String strMarqueeTagBeg = "\n<marquee direction =\"up\" height=\"550\" behavior=\"alternate\" loop=\"100\" SCROLLDELAY=\"1\" SCROLLAMOUNT=\"2\">";
        String strMarqueeTagEnd = "\n</marquee>";
        
        
        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Headers
        try {
            output.write("<html>");
            output.write("<head>");
            output.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_CHARSET + "\">");
            output.write("<title>" + gps.getName() + "</title>");
            output.write("<link href=\"current.css\" rel=\"stylesheet\" type=\"text/css\">");
            output.write("</head>");

            output.write("<body>");
            output.write("<h1 align=\"center\">" + gps.getName() + "</h1>");
            output.write("<h1 align=\"center\">" + "Players list" + "</h1>");
            if(bScroll) output.write(strMarqueeTagBeg);
            
            output.write("<table align=\"center\" class=\"simple\">");
            output.write("\n<th class=\"right\"> </th>");
            output.write("\n<th class=\"left\">Pin/Lic/Id</th>");
            output.write("\n<th class=\"left\">Name</th>");
            output.write("\n<th class=\"left\">Co</th>");
            output.write("\n<th class=\"left\">Club</th>");
//            output.write("\n<th class=\"right\">Rk</th>");
            output.write("\n<th class=\"right\">Gr</th>");
            output.write("\n<th class=\"right\">Rt</th>");
            output.write("\n<th class=\"right\">MM</th>");
            output.write("\n<th class=\"middle\">Participation</th>");

            output.write("\n</tr>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Contents
        ArrayList<Player> alP = null;
        try {
            alP = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        PlayerComparator pc = new PlayerComparator(dpps.getPlayerSortType());
        Collections.sort(alP, pc);

        try {
            for (int iP = 0; iP < alP.size(); iP++) {
                Player p = alP.get(iP);
                output.write("\n<tr>");
                String strPar = "pair";
                if (iP % 2 == 0) {
                    strPar = "impair";
                }
                output.write("<td class=" + strPar + " align=\"right\">" + (iP + 1) + "&nbsp;</td>");
                String strPinLic = p.getEgfPin();
                if (strPinLic.length() == 0) {
                    strPinLic = p.getFfgLicence();
                }
                if (strPinLic.length() == 0) {
                    strPinLic = p.getAgaId();
                }
                if (strPinLic.length() == 0) {
                    strPinLic = "--------";
                }                
                output.write("<td class=" + strPar + " align=\"left\">" + strPinLic + "</td>");
                String strNF = p.fullName();
                output.write("<td class=" + strPar + " align=\"left\">" + strNF + "</td>");
                output.write("<td class=" + strPar + " align=\"center\">" + p.getCountry() + "</td>");
                output.write("<td class=" + strPar + " align=\"center\">" + p.getClub() + "</td>");
                //String strRk = Player.convertIntToKD(p.getRank());
                String strGr = p.getStrGrade();
//                output.write("<td class=" + strPar + " align=\"center\">" + strRk + "</td>");
                output.write("<td class=" + strPar + " align=\"center\">" + strGr + "</td>");
                String strMM = "" + p.smms(tournament.getTournamentParameterSet().getGeneralParameterSet());
                output.write("<td class=" + strPar + " align=\"center\">" + strMM + "</td>");
                String strRt = "" + p.getRating();
                output.write("<td class=" + strPar + " align=\"center\">" + strRt + "</td>");
                String strPart = Player.convertParticipationToString(p, tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds());
                output.write("<td class=\"" + strPar + " participation\"" + "align=\"center\">" + strPart + "&nbsp;</td>");
                output.write("</tr>");
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</table>");
            if(bScroll) output.write(strMarqueeTagEnd);
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            output.write("<h4 align=center>" + Gotha.getGothaVersionnedName() + "<br>" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "</h4>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</body></html>");
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public static void generateTeamsListHTMLFileContents(TournamentInterface tournament, File f){  
        TeamTournamentParameterSet ttps;
        TournamentParameterSet tps;
        try{
            ttps = tournament.getTeamTournamentParameterSet();
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        boolean bScroll = pubPS.isHtmlAutoScroll();
        String strMarqueeTagBeg = "\n<marquee direction =\"up\" height=\"550\" behavior=\"alternate\" loop=\"100\" SCROLLDELAY=\"1\" SCROLLAMOUNT=\"2\">";
        String strMarqueeTagEnd = "\n</marquee>";

        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Headers
        try {
            output.write("<html>");
            output.write("<head>");
            output.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_CHARSET + "\">");
            output.write("<title>" + gps.getName() + "</title>");
            output.write("<link href=\"current.css\" rel=\"stylesheet\" type=\"text/css\">");
            output.write("</head>");

            output.write("<body>");
            output.write("<h1 align=\"center\">" + gps.getName() + "</h1>");
            output.write("<h1 align=\"center\">" + "Teams list" + "</h1>");
            if(bScroll) output.write(strMarqueeTagBeg);
            
            output.write("<table align=\"center\" class=\"simple\">");
            output.write("\n<th align=\"right\">Nr&nbsp;</th>");
            output.write("\n<th align=\"left\">&nbsp;Team name&nbsp;</th>");
            output.write("\n<th align=\"right\">&nbsp;Bd&nbsp;</th>");
            output.write("\n<th align=\"left\">&nbsp;Player&nbsp;</th>");
            output.write("\n<th align=\"middle\">&nbsp;Co&nbsp;</th>");
            output.write("\n<th align=\"middle\">&nbsp;Club&nbsp;</th>");
            output.write("\n<th align=\"right\">&nbsp;Rating&nbsp;</th>");
            output.write("\n<th align=\"middle\">&nbsp;Rounds&nbsp;</th>");

            output.write("\n</tr>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Contents
        TeamMemberStrings[] arTMS = null;
        try {
            arTMS = TeamMemberStrings.buildTeamMemberStrings(tournament);
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            for (int iTMS = 0; iTMS < arTMS.length; iTMS++) {
                TeamMemberStrings tMS = arTMS[iTMS];
                if (tMS == null) break;
                output.write("\n<tr>");
                String strPar = "pair";
                if (iTMS % 2 == 0) {
                    strPar = "impair";
                }
                output.write("<td class=" + strPar + " align=\"right\">" + tMS.strTeamNumber + "&nbsp;</td>");
                output.write("<td class=" + strPar + " align=\"left\">" + tMS.strTeamName + "</td>");
                output.write("<td class=" + strPar + " align=\"right\">" + tMS.strBoardNumber + "&nbsp;</td>");
                output.write("<td class=" + strPar + " align=\"left\">" + tMS.strPlayerName + "</td>");
                output.write("<td class=" + strPar + " align=\"center\">" + tMS.strCountry + "</td>");
                output.write("<td class=" + strPar + " align=\"center\">" + tMS.strClub + "</td>");
                output.write("<td class=" + strPar + " align=\"right\">" + tMS.strRating + "&nbsp;</td>");
//                output.write("<td class=" + strPar + " align=\"center\">" + tMS.strMembership + "</td>");
                output.write("<td class=\"" + strPar + " participation\"" +  " align=\"center\">" + tMS.strMembership + "</td>");
                output.write("</tr>");
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</table>");
            if(bScroll) output.write(strMarqueeTagEnd);
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            output.write("<h4 align=center>" + Gotha.getGothaVersionnedName() + "<br>" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "</h4>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</body></html>");
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
           

    }
    
    public static void generateGamesListHTMLFileContents(TournamentInterface tournament, int roundNumber, File f) {
       
        TournamentParameterSet tps;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        boolean bScroll = pubPS.isHtmlAutoScroll();
        String strMarqueeTagBeg = "\n<marquee direction =\"up\" height=\"550\" behavior=\"alternate\" loop=\"100\" SCROLLDELAY=\"1\" SCROLLAMOUNT=\"2\">";
        String strMarqueeTagEnd = "\n</marquee>";
        
        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        try {
            // Headers       
           
        output.write("<html>");
            output.write("<head>");
            output.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_CHARSET + "\">");
            output.write("<title>" + gps.getName() + "</title>");
            output.write("<link href=\"current.css\" rel=\"stylesheet\" type=\"text/css\">");
            output.write("</head>");
            
            output.write("<body>");
            output.write("<h1 align=\"center\">" + gps.getName() + "</h1>");
            output.write("<h1 align=\"center\">" + "Games list. Round " + (roundNumber + 1) + "</h1>");  
            if(bScroll) output.write(strMarqueeTagBeg);
            
            output.write("\n<table align=\"center\" class=\"simple\">");
            output.write("\n<th class=\"right\">&nbsp;Tble&nbsp;</th>" + 
                    "<th class=\"left\">&nbsp;White&nbsp;</th>" + 
                    "<th class=\"left\">&nbsp;Black&nbsp; </th>" + 
                    "<th class=\"center\">&nbsp;Hd&nbsp;</th>" +
                    "<th class=\"center\">&nbsp;Res&nbsp;</th>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Contents
         ArrayList<Game> alG = null;
        try {
            alG = new ArrayList<Game>(tournament.gamesList(roundNumber));
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alG, gameComparator);
        
        for (int iG = 0; iG < alG.size(); iG++){
            try {
                output.write("\n<tr>");
                String strPar = "pair";
                if (iG % 2 == 0) {
                    strPar = "impair";
                }
                Game g = alG.get(iG);

                String strTN = "" + (g.getTableNumber() + 1);
                output.write("<td class=" + strPar + ">" + strTN + "</td>");
                Player wP = g.getWhitePlayer();
                String strWP = wP.augmentedPlayerName(dpps);
                output.write("<td class=" + strPar + " align=\"left\">" + strWP + "&nbsp;</td>");
                Player bP = g.getBlackPlayer();
                String strBP = bP.augmentedPlayerName(dpps);
                output.write("<td class=" + strPar + " align=\"left\">" + strBP + "&nbsp;</td>");
                String strHd = "" + g.getHandicap();
                output.write("<td class=" + strPar + " align=\"center\">" + strHd + "&nbsp;</td>");
                String strRes = g.resultAsString(true);
                output.write("<td class=" + strPar + " align=\"center\">" + strRes + "&nbsp;</td>");

                output.write("</tr>");
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
  
        }
        try {
            output.write("\n</table>");
            if(bScroll) output.write(strMarqueeTagEnd);
            
            output.write("\n<h4 align=center>" + Gotha.getGothaVersionnedName() + "<br>" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "</h4>");
            output.write("\n</body></html>");
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
     public static void generateStandingsHTMLFileContents(TournamentInterface tournament, int roundNumber, File f) {
        TournamentParameterSet tps;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        boolean bScroll = pubPS.isHtmlAutoScroll();
        String strMarqueeTagBeg = "\n<marquee direction =\"up\" height=\"550\" behavior=\"alternate\" loop=\"100\" SCROLLDELAY=\"1\" SCROLLAMOUNT=\"2\">";
        String strMarqueeTagEnd = "\n</marquee>";

        // Prepare tabCrit from pps
        int[] tC = pps.getPlaCriteria();
        int[] tabCrit = PlacementParameterSet.purgeUselessCriteria(tC);

        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Headers       
        try {
            output.write("<html>");
            output.write("<head>");
            output.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_CHARSET + "\">");
            output.write("<title>" + gps.getName() + "</title>");
            output.write("<link href=\"current.css\" rel=\"stylesheet\" type=\"text/css\">");
            output.write("</head>");

            output.write("<body>");

            output.write("<h1 align=\"center\">" + gps.getName() + "</h1>");
            if(bScroll) output.write(strMarqueeTagBeg);
            
            output.write("\n<table align=\"center\" class=\"simple\">");
            if (tps.getDPParameterSet().isDisplayNumCol())
                output.write("\n<th class=\"left\">&nbsp;Num&nbsp;</th>");
            if (tps.getDPParameterSet().isDisplayPlCol())
                output.write("\n<th class=\"left\">&nbsp;Pl&nbsp;</th>");
            output.write("\n<th class=\"left\">&nbsp;Name&nbsp;</th>");
//            output.write("<th class=\"middle\">&nbsp;Rank&nbsp;</th>");
            output.write("<th class=\"middle\">&nbsp;Grade&nbsp;</th>");
            if (tps.getDPParameterSet().isDisplayCoCol())
                output.write("<th class=\"middle\">&nbsp;Co&nbsp;</th>");
            if (tps.getDPParameterSet().isDisplayClCol())
                output.write("<th class=\"middle\">&nbsp;Club&nbsp;</th>"); 
            output.write("<th class=\"middle\">&nbsp;NbW&nbsp;</th>");

            for (int r = 0; r < roundNumber + 1; r++) {
                output.write("<th class=\"middle\">R&nbsp;" + (r + 1) + "&nbsp;</th>");
            }
            for (int c = 0; c < tabCrit.length - 1; c++) {
                output.write("<th class=\"middle\">" + PlacementParameterSet.criterionShortName(tabCrit[c]) + "</th>");
            }
            output.write("<th class=\"right\">" + PlacementParameterSet.criterionShortName(tabCrit[tabCrit.length - 1]) + "</th>");

            output.write("\n</tr>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Contents
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = null;
//        int roundNumber = gps.getNumberOfRounds() - 1;
        try {
            alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
            // Eliminate non-players
            for (Iterator<ScoredPlayer> it = alOrderedScoredPlayers.iterator(); it.hasNext();) {
                ScoredPlayer sP = it.next();
                if (!tournament.isPlayerImplied(sP)) {
                    it.remove();
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean bFull = true; 
        int gameFormat = tps.getDPParameterSet().getGameFormat();
        if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) bFull = false;

        String[][] hG = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, roundNumber, tps, bFull);
        String[] strPlace = ScoredPlayer.positionStrings(alOrderedScoredPlayers, roundNumber, tps);
        try {
            for (int iSP = 0; iSP < alOrderedScoredPlayers.size(); iSP++) {
                output.write("\n<tr>");
                String strPar = "pair";
                ScoredPlayer sP = alOrderedScoredPlayers.get(iSP);
                if (iSP % 2 == 0) {
                    strPar = "impair";
                }
                String strAlCenter = " align=\"center\"";

                if (tps.getDPParameterSet().isDisplayNumCol())
                    output.write("<td class=" + strPar + " align=\"right\">" + (iSP + 1) + "&nbsp;</td>");
                if (tps.getDPParameterSet().isDisplayPlCol())
                    output.write("<td class=" + strPar + " align=\"right\">" + strPlace[iSP] + "&nbsp;</td>");
                String strNF = sP.fullName();
                output.write("<td class=" + strPar + ">" + strNF + "</td>");
//                String strRank = Player.convertIntToKD(sP.getRank());
                String strGrade = sP.getStrGrade();
                output.write("<td class=" + strPar + strAlCenter + ">" + strGrade + "</td>");
                
                if (tps.getDPParameterSet().isDisplayCoCol()){
                    String strCountry = sP.getCountry();
                    output.write("<td class=" + strPar + strAlCenter + ">" + strCountry + "</td>");
                }
                
                if (tps.getDPParameterSet().isDisplayClCol()){
                    String strClub = sP.getClub();
                    output.write("<td class=" + strPar + strAlCenter + ">" + strClub + "</td>");
                }
                
                output.write("<td class=" + strPar + strAlCenter + ">" + sP.formatScore(PlacementParameterSet.PLA_CRIT_NBW, roundNumber) + "</td>");

                for (int r = 0; r <= roundNumber; r++) {
                    String strHG = hG[r][iSP];
                    if (strHG.indexOf('+') > 0) {
                        strHG = "<b>" + strHG + "</b>";
                    }
                    output.write("<td class=" + strPar + strAlCenter + ">" + strHG + "</td>");
                }
                for (int c = 0; c < tabCrit.length; c++) {
                    String strCritValue = sP.formatScore(tabCrit[c], roundNumber);
                    output.write("<td class=" + strPar + strAlCenter + ">" + strCritValue + "</td>");

                }

                output.write("</tr>");
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</table>");
            if(bScroll) output.write(strMarqueeTagEnd);

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            output.write("<h4 align=center>" + Gotha.getGothaVersionnedName() + "<br>" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "</h4>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</body></html>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
     public static void generateMatchesListHTMLFileContents(TournamentInterface tournament, int roundNumber, File f) {
        TournamentParameterSet tps;
        TeamTournamentParameterSet ttps;
        try {
            tps = tournament.getTournamentParameterSet();
            ttps = tournament.getTeamTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        TeamGeneralParameterSet tgps = ttps.getTeamGeneralParameterSet();
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        boolean bScroll = pubPS.isHtmlAutoScroll();
        String strMarqueeTagBeg = "\n<marquee direction =\"up\" height=\"550\" behavior=\"alternate\" loop=\"100\" SCROLLDELAY=\"1\" SCROLLAMOUNT=\"2\">";
        String strMarqueeTagEnd = "\n</marquee>";

        
        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        try {
            // Headers       
           
        output.write("<html>");
            output.write("<head>");
            output.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_CHARSET + "\">");
            output.write("<title>" + gps.getName() + "</title>");
            output.write("<link href=\"current.css\" rel=\"stylesheet\" type=\"text/css\">");
            output.write("</head>");
            
            output.write("<body>");
            output.write("<h1 align=\"center\">" + gps.getName() + "</h1>");
            output.write("<h1 align=\"center\">" + "Matches list. Round " + (roundNumber + 1) + "</h1>");            
            if(bScroll) output.write(strMarqueeTagBeg);
            
            output.write("<table align=\"center\" class=\"simple\">");
            output.write("\n<th class=\"right\">&nbsp;Tble&nbsp;</th>" + 
                    "<th class=\"left\">&nbsp;&nbsp;</th>" + 
                    "<th class=\"left\">&nbsp;&nbsp; </th>" + 
                    "<th class=\"center\">&nbsp;Hd&nbsp;</th>" +
                    "<th class=\"center\">&nbsp;Res&nbsp;</th>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        // Contents
        ArrayList<Match> alM = null;
        try {
            alM = new ArrayList<Match>(tournament.matchesList(roundNumber));
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<ComparableMatch> alCM = ComparableMatch.buildComparableMatchesArray(alM, tournament, roundNumber);
        int matchesSortType = MatchComparator.BOARD0_TABLE_NUMBER_ORDER;
        MatchComparator matchComparator = new MatchComparator(matchesSortType);
        Collections.sort(alCM, matchComparator);
        boolean bPair = false;
        for (int iCM = 0; iCM < alCM.size(); iCM++){
            try {
                bPair = !bPair;
                output.write("\n<tr>");
                String strPar = "pair";
                if (!bPair) strPar = "impair";
                
                ComparableMatch cm = alCM.get(iCM);
                Match m = tournament.getMatch(roundNumber, cm.board0TableNumber);
                
                String strTN = "" + (cm.board0TableNumber + 1) + "---";
                output.write("<td class=" + strPar + ">" + strTN + "</td>");
                String strWTN = cm.wst.getTeamName();
                output.write("<td class=" + strPar + " align=\"left\">" + strWTN + "&nbsp;</td>");
                String strBTN = cm.bst.getTeamName();
                output.write("<td class=" + strPar + " align=\"left\">" + strBTN + "&nbsp;</td>");
                output.write("<td class=" + strPar + " align=\"center\">" + "" + "&nbsp;</td>");
                
                String strWTeamNbW = Gotha.formatFractNumber(m.getWX2(m.getWhiteTeam()), 2);
                String strBTeamNbW = Gotha.formatFractNumber(m.getWX2(m.getBlackTeam()), 2);
                String strTeamResult = strWTeamNbW + "-" + strBTeamNbW;
                output.write("<td class=" + strPar + " align=\"center\">" + strTeamResult + "&nbsp;</td>");
                output.write("</tr>");
                if (dpps.isDisplayIndGamesInMatches()){               
                    Team wTeam = m.getWhiteTeam();
                    Team bTeam = m.getBlackTeam();
                    int nbBoards = tgps.getTeamSize();
                    for (int ib = 0; ib < nbBoards; ib++){
                        bPair = !bPair;
                        strPar = "pair";
                        if (!bPair) strPar = "impair";
                        Player p1 = wTeam.getTeamMember(roundNumber, ib);
                        Player p2 = bTeam.getTeamMember(roundNumber, ib);
                        Game game = tournament.getGame(roundNumber, p1);
                        strTN = "" + (game.getTableNumber() + 1);
                        output.write("<td class=" + strPar + " align=\"right\">" + strTN + "&nbsp;</td>");
                        String strP1Color = "";
                        String strP2Color = "";
                        if (game.isKnownColor()){
                            if (game.getWhitePlayer().hasSameKeyString(p1)){
//                                strP1Color = " (w)";
//                                strP2Color = " (b)";
                                strP1Color = "<img alt=\"(w)\" src=\"whitestone.png\" />";
                                strP2Color = "<img alt=\"(b)\" src=\"blackstone.png\" />";
                            }
                            else{
//                                strP2Color = " (w)";
//                                strP1Color = " (b)";
                                strP2Color = "<img alt=\"(w)\" src=\"whitestone.png\" />";
                                strP1Color = "<img alt=\"(b)\" src=\"blackstone.png\" />";

                            }
                        }

                        String strNF = p1.augmentedPlayerName(dpps); 
                        output.write("<td class=" + strPar + " align=\"left\">" + strP1Color + " " + strNF + "&nbsp;</td>");
                        strNF = p2.augmentedPlayerName(dpps); 
                        output.write("<td class=" + strPar + " align=\"left\">" + strP2Color + " " + strNF + "&nbsp;</td>");

                        String strHd = "" + game.getHandicap();
                        output.write("<td class=" + strPar + " align=\"center\">" + strHd + "&nbsp;</td>");

                        // Result
                        String strResult = game.resultAsString(game.getWhitePlayer().hasSameKeyString(p1));
                        output.write("<td class=" + strPar + " align=\"center\">" + strResult + "&nbsp;</td>");

                        output.write("</tr>");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            output.write("\n</table>");
            if(bScroll) output.write(strMarqueeTagEnd);
            output.write("<h4 align=center>" + Gotha.getGothaVersionnedName() + "<br>" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "</h4>");
            output.write("\n</body></html>");
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public static void generateTeamsStandingsHTMLFileContents(TournamentInterface tournament, int round, File f) {
        TeamTournamentParameterSet ttps;
        TournamentParameterSet tps;
        try {
            ttps = tournament.getTeamTournamentParameterSet();
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        
        boolean bScroll = pubPS.isHtmlAutoScroll();
        String strMarqueeTagBeg = "\n<marquee direction =\"up\" height=\"550\" behavior=\"alternate\" loop=\"100\" SCROLLDELAY=\"1\" SCROLLAMOUNT=\"2\">";
        String strMarqueeTagEnd = "\n</marquee>";

        // Prepare tabCrit from pps
        int[] tC = tpps.getPlaCriteria();
        int[] tabCrit = PlacementParameterSet.purgeUselessCriteria(tC);
        
        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), DEFAULT_CHARSET));

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Headers
        try {
            output.write("<html>");
            output.write("<head>");
            output.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_CHARSET + "\">");
            output.write("<title>" + gps.getName() + "</title>");
            output.write("<link href=\"current.css\" rel=\"stylesheet\" type=\"text/css\">");
            output.write("</head>");

            output.write("<body>");
            output.write("<h1 align=\"center\">" + gps.getName() + "</h1>");
            output.write("<h1 align=\"center\">" + "Teams standings" + "</h1>");
            if(bScroll) output.write(strMarqueeTagBeg);
            
            output.write("<table align=\"center\" class=\"simple\">");
            output.write("\n<th class=\"left\">&nbsp;Pl&nbsp;</th>"
                    + "<th class=\"middle\">&nbsp;Team name&nbsp;</th>");
            for (int r = 0; r <= round; r++) {
                output.write("<th class=\"middle\">R&nbsp;" + (r + 1) + "&nbsp;</th>");
            }
            for (int c = 0; c < tabCrit.length - 1; c++) {
                output.write("<th class=\"middle\">" + TeamPlacementParameterSet.criterionShortName(tabCrit[c]) + "</th>");
            }
            output.write("<th class=\"right\">" + PlacementParameterSet.criterionShortName(tabCrit[tabCrit.length - 1]) + "</th>");

            output.write("\n</tr>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Contents

        ScoredTeamsSet sts = null;
        try {
            sts = tournament.getAnUpToDateScoredTeamsSet(tpps, round);
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<ScoredTeam> alOrderedScoredTeams = sts.getOrderedScoredTeamsList();

        try {
            for (int iST = 0; iST < alOrderedScoredTeams.size(); iST++) {
                ScoredTeam st = alOrderedScoredTeams.get(iST);
                output.write("\n<tr>");
                String strPar = "pair";
                if (iST % 2 == 0) {
                    strPar = "impair";
                }
                String strAlCenter = " align=\"center\"";
                String strPL = sts.getTeamPositionString(st);
                output.write("<td class=" + strPar + " align=\"right\">" + strPL + "&nbsp;</td>");
                String strTN = st.getTeamName();
                output.write("<td class=" + strPar + ">" + strTN + "</td>");
                for (int r = 0; r <= round; r++) {
                    String strHM = sts.getHalfMatchString(st, r);
                    output.write("<td class=" + strPar + strAlCenter + ">" + strHM + "</td>");
                }
                for (int ic = 0; ic < tabCrit.length; ic++) {
                    int crit = tabCrit[ic];
                    int coef = TeamPlacementParameterSet.criterionCoef(crit);
                    String strCritValue = Gotha.formatFractNumber(st.getCritValue(ic), coef);
                    output.write("<td class=" + strPar + strAlCenter + ">" + strCritValue + "</td>");
                }


                output.write("</tr>");
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</table>");
            if(bScroll) output.write(strMarqueeTagEnd);

        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            output.write("<h4 align=center>" + Gotha.getGothaVersionnedName() + "<br>" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "</h4>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            output.write("\n</body></html>");
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void generateXMLFile(TournamentInterface tournament, File xmlFile) {
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement("Tournament");
        rootElement.setAttribute("dataVersion", "" + Gotha.GOTHA_DATA_VERSION);
        document.appendChild(rootElement);

        // Include players
        ArrayList<Player> alPlayers = null;
        try {
            alPlayers = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element emPlayers = generateXMLPlayersElement(document, alPlayers);
        rootElement.appendChild(emPlayers);

        // Include games
        ArrayList<Game> alGames = null;
        try {
            alGames = tournament.gamesList();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element emGames = generateXMLGamesElement(document, alGames);
        rootElement.appendChild(emGames);

        // Include bye players if any
        Player[] byePlayers = null;
        try {
            byePlayers = tournament.getByePlayers();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element emByePlayers = generateXMLByePlayersElement(document, byePlayers);
        if (emByePlayers != null) {
            rootElement.appendChild(emByePlayers);
        }

        // Include teams if any
        ArrayList<Team> alTeams = null;
        try {
            alTeams = tournament.teamsList();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!alTeams.isEmpty()) {
            Element emTeams = generateXMLTeamsElement(document, alTeams);
            rootElement.appendChild(emTeams);
        }

        // Include tournament parameters
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element emTournamentParameterSet = generateXMLTournamentParameterSetElement(document, tps);
        rootElement.appendChild(emTournamentParameterSet);

        // Include team tournament parameters
        TeamTournamentParameterSet ttps = null;
        try {
            ttps = tournament.getTeamTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element emTeamTournamentParameterSet = generateXMLTeamTournamentParameterSetElement(document, ttps);
        rootElement.appendChild(emTeamTournamentParameterSet);

        
        // Include Clubs groups
        ArrayList<ClubsGroup> alClubsGroup = new ArrayList<ClubsGroup>();
        try {
            alClubsGroup = tournament.clubsGroupsList();
        } catch (RemoteException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!alClubsGroup.isEmpty()) {
            Element emClubsGroups = generateXMLClubsGroupsElement(document, alClubsGroup);
            rootElement.appendChild(emClubsGroups);
        }
       
        // Transform document into a DOM source
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, ExternalDocument.DEFAULT_CHARSET);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        DOMSource source = new DOMSource(document);
       
        // generate file
        Writer output = null;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), DEFAULT_CHARSET));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            String strMessage = "Unreachable file";
            JOptionPane.showMessageDialog(null, strMessage, "Message", JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        StreamResult result = new StreamResult(output);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }

    /**
     * Generates an xml players Liste Element and includes all players from alPlayers
     * returns the Element
     */
    private static Element generateXMLPlayersElement(Document document, ArrayList<Player> alPlayers) {
        Element emPlayers = document.createElement("Players");
        for (Player p : alPlayers) {
            String strName = p.getName();
            String strFirstName = p.getFirstName();
            String strCountry = p.getCountry();
            String strClub = p.getClub();
            String strEgfPin = p.getEgfPin();
            String strFfgLicence = p.getFfgLicence();
            String strFfgLicenceStatus = p.getFfgLicenceStatus();
            String strAgaId = p.getAgaId();
            String strAgaExpirationDate = p.getAgaExpirationDate();
            String strRank = Player.convertIntToKD(p.getRank());
            String strRating = Integer.valueOf(p.getRating()).toString();
            String strRatingOrigin = p.getRatingOrigin();
            String strGrade = p.getStrGrade();
            String strSMMSCorrection = Integer.valueOf(p.getSmmsCorrection()).toString();
            boolean[] part = p.getParticipating();
            String strParticipating = "";
            for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                if (part[r]) {
                    strParticipating += "1";
                } else {
                    strParticipating += "0";
                }
            }
            String strRegisteringStatus = p.getRegisteringStatus();

            Element emPlayer = document.createElement("Player");
            emPlayer.setAttribute("name", strName);
            emPlayer.setAttribute("firstName", strFirstName);
            emPlayer.setAttribute("country", strCountry);
            emPlayer.setAttribute("club", strClub);
            emPlayer.setAttribute("egfPin", strEgfPin);
            emPlayer.setAttribute("ffgLicence", strFfgLicence);
            emPlayer.setAttribute("ffgLicenceStatus", strFfgLicenceStatus);
            emPlayer.setAttribute("agaId", strAgaId);
            emPlayer.setAttribute("agaExpirationDate", strAgaExpirationDate);
            emPlayer.setAttribute("rank", strRank);
            emPlayer.setAttribute("rating", strRating);
            emPlayer.setAttribute("ratingOrigin", strRatingOrigin);
            emPlayer.setAttribute("grade", strGrade);
            emPlayer.setAttribute("smmsCorrection", strSMMSCorrection);
            emPlayer.setAttribute("participating", strParticipating);
            emPlayer.setAttribute("registeringStatus", strRegisteringStatus);

            emPlayers.appendChild(emPlayer);
        }

        return emPlayers;
    }

    /**
     * Generates an xml games list Element and includes all games from alGames()
     * returns the Element
     */
    private static Element generateXMLGamesElement(Document document, ArrayList<Game> alGames) {
        Element emGames = document.createElement("Games");
        for (Game g : alGames) {
            String strRoundNumber = Integer.valueOf(g.getRoundNumber() + 1).toString();
            String strTableNumber = Integer.valueOf(g.getTableNumber() + 1).toString();
            String strWhitePlayer = g.getWhitePlayer().getKeyString();
            String strBlackPlayer = g.getBlackPlayer().getKeyString();
            String strKnownColor = g.isKnownColor() ? "true" : "false";
            String strHandicap = Integer.valueOf(g.getHandicap()).toString();
            String strResult;
            switch (g.getResult()) {
                case Game.RESULT_WHITEWINS:
                    strResult = "RESULT_WHITEWINS";
                    break;
                case Game.RESULT_BLACKWINS:
                    strResult = "RESULT_BLACKWINS";
                    break;
                case Game.RESULT_EQUAL:
                    strResult = "RESULT_EQUAL";
                    break;
                case Game.RESULT_BOTHWIN:
                    strResult = "RESULT_BOTHWIN";
                    break;
                case Game.RESULT_BOTHLOSE:
                    strResult = "RESULT_BOTHLOSE";
                    break;
                case Game.RESULT_WHITEWINS_BYDEF:
                    strResult = "RESULT_WHITEWINS_BYDEF";
                    break;
                case Game.RESULT_BLACKWINS_BYDEF:
                    strResult = "RESULT_BLACKWINS_BYDEF";
                    break;
                case Game.RESULT_EQUAL_BYDEF:
                    strResult = "RESULT_EQUAL_BYDEF";
                    break;
                case Game.RESULT_BOTHWIN_BYDEF:
                    strResult = "RESULT_BOTHWIN_BYDEF";
                    break;
                case Game.RESULT_BOTHLOSE_BYDEF:
                    strResult = "RESULT_BOTHLOSE_BYDEF";
                    break;
                default:
                    strResult = "RESULT_UNKNOWN";
            }

            Element emGame = document.createElement("Game");
            emGame.setAttribute("roundNumber", strRoundNumber);
            emGame.setAttribute("tableNumber", strTableNumber);
            emGame.setAttribute("whitePlayer", strWhitePlayer);
            emGame.setAttribute("blackPlayer", strBlackPlayer);
            emGame.setAttribute("knownColor", strKnownColor);
            emGame.setAttribute("handicap", strHandicap);
            emGame.setAttribute("result", strResult);

            emGames.appendChild(emGame);
        }
        return emGames;
    }

    /**
     * Generates an xml teams list Element and includes all teams from alTeams()
     * returns the Element
     */
    private static Element generateXMLTeamsElement(Document document, ArrayList<Team> alTeams) {
        Element emTeams = document.createElement("Teams");
        for (Team t : alTeams) {
            String strTeamNumber = Integer.valueOf(t.getTeamNumber() + 1).toString();
            String strTeamName = t.getTeamName();
            Element emTeam = document.createElement("Team");
            emTeam.setAttribute("teamNumber", strTeamNumber);
            emTeam.setAttribute("teamName", strTeamName);
            for ( int ir = 0; ir < Gotha.MAX_NUMBER_OF_ROUNDS; ir++){
                String strRoundNumber = Integer.valueOf(ir + 1).toString();
                for (int ibn = 0; ibn < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ibn++) {
                    Player p = t.getTeamMember(ir, ibn);
                    if (p == null) {
                        continue;
                    }
                    String strBoardNumber = Integer.valueOf(ibn + 1).toString();
                    String strPlayer = p.getKeyString();
                    Element emBoard = document.createElement("Board");
                    emBoard.setAttribute("roundNumber", strRoundNumber);
                    emBoard.setAttribute("boardNumber", strBoardNumber);
                    emBoard.setAttribute("player", strPlayer);
                    emTeam.appendChild(emBoard);
                }
            }
            emTeams.appendChild(emTeam);
        }
        if (emTeams.hasChildNodes()) {
            return emTeams;
        } else {
            return null;
        }
    }

    /**
     * Generates an xml clubsgroups Element 
     * returns the Element
     */
    private static Element generateXMLClubsGroupsElement(Document document, ArrayList<ClubsGroup> alClubsGroups) {
        Element emClubsGroups = document.createElement("ClubsGroups");      
        for (ClubsGroup cg : alClubsGroups) {
            String strCGName = cg.getName();
            Element emClubsGroup = document.createElement("ClubsGroup");
            emClubsGroup.setAttribute("name", strCGName);
            for (Club club : cg.getHmClubs().values()){
                String strClub = club.getName();
                Element emClub = document.createElement("Club");
                emClub.setAttribute("name", strClub);
                emClubsGroup.appendChild(emClub);
            }
            emClubsGroups.appendChild(emClubsGroup);
        }
                    
        if(emClubsGroups.hasChildNodes()){
            return emClubsGroups;
        }
        else {
            return null;
        }

    }
    
    
    
    /**
     * Generates an xml ByePlayers Element and includes all  bye players
     * returns the Element or null if no bye players
     */
    private static Element generateXMLByePlayersElement(Document document, Player[] byePlayers) {
        Element emByePlayers = document.createElement("ByePlayers");
        for (int r = 0; r < byePlayers.length; r++) {
            String strRoundNumber = Integer.valueOf(r + 1).toString();
            Player p = byePlayers[r];
            if (byePlayers[r] == null) {
                continue;
            }
            String strPlayer = byePlayers[r].getKeyString();

            Element emByePlayer = document.createElement("ByePlayer");
            emByePlayer.setAttribute("roundNumber", strRoundNumber);
            emByePlayer.setAttribute("player", strPlayer);

            emByePlayers.appendChild(emByePlayer);
        }

        if (emByePlayers.hasChildNodes()) {
            return emByePlayers;
        } else {
            return null;
        }
    }

    /**
     * Generates an xml players Liste Element and includes all players from alPlayers
     * returns the Element
     */
    private static Element generateXMLTournamentParameterSetElement(Document document, TournamentParameterSet tps) {
        Element emTournamentParameterSet = document.createElement("TournamentParameterSet");

        GeneralParameterSet gps = tps.getGeneralParameterSet();
        Element emGeneralParameterSet = document.createElement("GeneralParameterSet");
        emGeneralParameterSet.setAttribute("shortName", gps.getShortName());
        emGeneralParameterSet.setAttribute("name", gps.getName());
        emGeneralParameterSet.setAttribute("location", gps.getLocation());
        emGeneralParameterSet.setAttribute("director", gps.getDirector());
        emGeneralParameterSet.setAttribute("beginDate", new SimpleDateFormat("yyyy-MM-dd").format(gps.getBeginDate()));
        emGeneralParameterSet.setAttribute("endDate", new SimpleDateFormat("yyyy-MM-dd").format(gps.getEndDate()));
        emGeneralParameterSet.setAttribute("size", gps.getStrSize());
        emGeneralParameterSet.setAttribute("komi", gps.getStrKomi());
        emGeneralParameterSet.setAttribute("basicTime", "" + gps.getBasicTime());
        String strComplementaryTimeSystem;
        switch (gps.getComplementaryTimeSystem()) {
            case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                strComplementaryTimeSystem = "SUDDENDEATH";
                break;
            case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                strComplementaryTimeSystem = "STDBYOYOMI";
                break;
            case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                strComplementaryTimeSystem = "CANBYOYOMI";
                break;
            case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                strComplementaryTimeSystem = "FISCHER";
                break;
            default:
                strComplementaryTimeSystem = "SUDDENDEATH";
        }
        emGeneralParameterSet.setAttribute("complementaryTimeSystem", strComplementaryTimeSystem);

        emGeneralParameterSet.setAttribute("stdByoYomiTime", "" + gps.getStdByoYomiTime());
        emGeneralParameterSet.setAttribute("nbMovesCanTime", "" + gps.getNbMovesCanTime());
        emGeneralParameterSet.setAttribute("canByoYomiTime", "" + gps.getCanByoYomiTime());
        emGeneralParameterSet.setAttribute("fischerTime", "" + gps.getFischerTime());

        emGeneralParameterSet.setAttribute("numberOfRounds", "" + gps.getNumberOfRounds());
        emGeneralParameterSet.setAttribute("numberOfCategories", "" + gps.getNumberOfCategories());
        emGeneralParameterSet.setAttribute("genMMFloor", Player.convertIntToKD(gps.getGenMMFloor()));
        emGeneralParameterSet.setAttribute("genMMBar", Player.convertIntToKD(gps.getGenMMBar()));
        emGeneralParameterSet.setAttribute("genMMZero", Player.convertIntToKD(gps.getGenMMZero()));
        
        emGeneralParameterSet.setAttribute("genNBW2ValueAbsent", "" + gps.getGenNBW2ValueAbsent());
        emGeneralParameterSet.setAttribute("genNBW2ValueBye", "" + gps.getGenNBW2ValueBye());
        emGeneralParameterSet.setAttribute("genMMS2ValueAbsent", "" + gps.getGenMMS2ValueAbsent());
        emGeneralParameterSet.setAttribute("genMMS2ValueBye", "" + gps.getGenMMS2ValueBye());
        emGeneralParameterSet.setAttribute("genRoundDownNBWMMS", "" + gps.isGenRoundDownNBWMMS());
        

        if (gps.getNumberOfCategories() > 1) {
            Element emCategories = document.createElement("Categories");
            int[] lCL = gps.getLowerCategoryLimits();
            for (int c = 0; c < lCL.length; c++) {
                Element emCategory = document.createElement("Category");
                emCategory.setAttribute("number", "" + (c + 1));
                emCategory.setAttribute("lowerLimit", Player.convertIntToKD(lCL[c]));
                emCategories.appendChild(emCategory);
            }
            if (emCategories.hasChildNodes()) {
                emGeneralParameterSet.appendChild(emCategories);
            }
        }

        emTournamentParameterSet.appendChild(emGeneralParameterSet);

        // HandicapParameterSet
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        Element emHandicapParameterSet = document.createElement("HandicapParameterSet");
        emHandicapParameterSet.setAttribute("hdBasedOnMMS", Boolean.valueOf(hps.isHdBasedOnMMS()).toString());
        emHandicapParameterSet.setAttribute("hdNoHdRankThreshold", Player.convertIntToKD(hps.getHdNoHdRankThreshold()));
        emHandicapParameterSet.setAttribute("hdCorrection", "" + hps.getHdCorrection());
        emHandicapParameterSet.setAttribute("hdCeiling", "" + hps.getHdCeiling());

        emTournamentParameterSet.appendChild(emHandicapParameterSet);

        // PlacementParameterSet
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        Element emPlacementParameterSet = document.createElement("PlacementParameterSet");

        Element emPlacementCriteria = document.createElement("PlacementCriteria");
        int[] plaC = pps.getPlaCriteria();
        for (int c = 0; c < plaC.length; c++) {
            Element emPlacementCriterion = document.createElement("PlacementCriterion");
            emPlacementCriterion.setAttribute("number", "" + (c + 1));
            emPlacementCriterion.setAttribute("name", PlacementParameterSet.criterionLongName(plaC[c]));
            emPlacementCriteria.appendChild(emPlacementCriterion);
        }
        emPlacementParameterSet.appendChild(emPlacementCriteria);

        emTournamentParameterSet.appendChild(emPlacementParameterSet);

        // PairingParameterSet
        PairingParameterSet paiPS = tps.getPairingParameterSet();
        Element emPairingParameterSet = document.createElement("PairingParameterSet");
        emPairingParameterSet.setAttribute("paiStandardNX1Factor", "" + paiPS.getPaiStandardNX1Factor());
        emPairingParameterSet.setAttribute("paiBaAvoidDuplGame", "" + paiPS.getPaiBaAvoidDuplGame());
        emPairingParameterSet.setAttribute("paiBaRandom", "" + paiPS.getPaiBaRandom());
        emPairingParameterSet.setAttribute("paiBaDeterministic", "" + paiPS.isPaiBaDeterministic());
        emPairingParameterSet.setAttribute("paiBaBalanceWB", "" + paiPS.getPaiBaBalanceWB());

        emPairingParameterSet.setAttribute("paiMaAvoidMixingCategories", "" + paiPS.getPaiMaAvoidMixingCategories());
        emPairingParameterSet.setAttribute("paiMaMinimizeScoreDifference", "" + paiPS.getPaiMaMinimizeScoreDifference());
        emPairingParameterSet.setAttribute("paiMaDUDDWeight", "" + paiPS.getPaiMaDUDDWeight());
        emPairingParameterSet.setAttribute("paiMaCompensateDUDD", "" + paiPS.isPaiMaCompensateDUDD());
        String strPaiMaDUDDUpperMode;
        switch (paiPS.getPaiMaDUDDUpperMode()) {
            case PairingParameterSet.PAIMA_DUDD_TOP:
                strPaiMaDUDDUpperMode = "TOP";
                break;
            case PairingParameterSet.PAIMA_DUDD_MID:
                strPaiMaDUDDUpperMode = "MID";
                break;
            case PairingParameterSet.PAIMA_DUDD_BOT:
                strPaiMaDUDDUpperMode = "BOT";
                break;
            default:
                strPaiMaDUDDUpperMode = "MID";
        }
        emPairingParameterSet.setAttribute("paiMaDUDDUpperMode", strPaiMaDUDDUpperMode);
        String strPaiMaDUDDLowerMode;
        switch (paiPS.getPaiMaDUDDLowerMode()) {
            case PairingParameterSet.PAIMA_DUDD_TOP:
                strPaiMaDUDDLowerMode = "TOP";
                break;
            case PairingParameterSet.PAIMA_DUDD_MID:
                strPaiMaDUDDLowerMode = "MID";
                break;
            case PairingParameterSet.PAIMA_DUDD_BOT:
                strPaiMaDUDDLowerMode = "BOT";
                break;
            default:
                strPaiMaDUDDLowerMode = "MID";
        }
        emPairingParameterSet.setAttribute("paiMaDUDDLowerMode", strPaiMaDUDDLowerMode);
        emPairingParameterSet.setAttribute("paiMaMaximizeSeeding", "" + paiPS.getPaiMaMaximizeSeeding());
        emPairingParameterSet.setAttribute("paiMaLastRoundForSeedSystem1", "" + (paiPS.getPaiMaLastRoundForSeedSystem1() + 1));
        String strPaiMaSeedSystem1;
        switch (paiPS.getPaiMaSeedSystem1()) {
            case PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM:
                strPaiMaSeedSystem1 = "SPLITANDRANDOM";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDFOLD:
                strPaiMaSeedSystem1 = "SPLITANDFOLD";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDSLIP:
                strPaiMaSeedSystem1 = "SPLITANDSLIP";
                break;
            default:
                strPaiMaSeedSystem1 = "SPLITANDFOLD";
        }
        emPairingParameterSet.setAttribute("paiMaSeedSystem1", strPaiMaSeedSystem1);
        String strPaiMaSeedSystem2;
        switch (paiPS.getPaiMaSeedSystem1()) {
            case PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM:
                strPaiMaSeedSystem2 = "SPLITANDRANDOM";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDFOLD:
                strPaiMaSeedSystem2 = "SPLITANDFOLD";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDSLIP:
                strPaiMaSeedSystem2 = "SPLITANDSLIP";
                break;
            default:
                strPaiMaSeedSystem2 = "SPLITANDFOLD";
        }
        emPairingParameterSet.setAttribute("paiMaSeedSystem1", strPaiMaSeedSystem2);
        emPairingParameterSet.setAttribute("paiMaAdditionalPlacementCritSystem1",
                PlacementParameterSet.criterionLongName(paiPS.getPaiMaAdditionalPlacementCritSystem1()));
        emPairingParameterSet.setAttribute("paiMaAdditionalPlacementCritSystem2",
                PlacementParameterSet.criterionLongName(paiPS.getPaiMaAdditionalPlacementCritSystem2()));

        emPairingParameterSet.setAttribute("paiSeRankThreshold", "" + Player.convertIntToKD(paiPS.getPaiSeRankThreshold()));
        emPairingParameterSet.setAttribute("paiSeNbWinsThresholdActive", "" + paiPS.isPaiSeNbWinsThresholdActive());
        emPairingParameterSet.setAttribute("paiSeBarThresholdActive", "" + paiPS.isPaiSeBarThresholdActive());
        emPairingParameterSet.setAttribute("paiSeDefSecCrit", "" + paiPS.getPaiSeDefSecCrit());
        emPairingParameterSet.setAttribute("paiSeMinimizeHandicap", "" + paiPS.getPaiSeMinimizeHandicap());
        emPairingParameterSet.setAttribute("paiSeAvoidSameGeo", "" + paiPS.getPaiSeAvoidSameGeo());
        emPairingParameterSet.setAttribute("paiSePreferMMSDiffRatherThanSameCountry", "" + paiPS.getPaiSePreferMMSDiffRatherThanSameCountry());
        emPairingParameterSet.setAttribute("paiSePreferMMSDiffRatherThanSameClubsGroup", "" + paiPS.getPaiSePreferMMSDiffRatherThanSameClubsGroup());
        emPairingParameterSet.setAttribute("paiSePreferMMSDiffRatherThanSameClub", "" + paiPS.getPaiSePreferMMSDiffRatherThanSameClub());

        emTournamentParameterSet.appendChild(emPairingParameterSet);

        // DPParameterSet
        DPParameterSet dpps = tps.getDPParameterSet();
        Element emDPParameterSet = document.createElement("DPParameterSet");
        String strPlayerSortType;
        switch (dpps.getPlayerSortType()) {
            case PlayerComparator.NAME_ORDER:
                strPlayerSortType = "name";
                break;
            case PlayerComparator.RANK_ORDER:
                strPlayerSortType = "rank";
                break;
            case PlayerComparator.GRADE_ORDER:
                strPlayerSortType = "grade";
                break;
            default:
                strPlayerSortType = "name";
        }
        emDPParameterSet.setAttribute("playerSortType", strPlayerSortType);
        
        String strGameFormat;
        switch (dpps.getGameFormat()) {
            case DPParameterSet.DP_GAME_FORMAT_FULL:
                strGameFormat = "full";
                break;
            case DPParameterSet.DP_GAME_FORMAT_SHORT:
                strGameFormat = "short";
                break;
            default:
                strGameFormat = "full";
        }
        emDPParameterSet.setAttribute("gameFormat", strGameFormat);
        
        emDPParameterSet.setAttribute("showPlayerGrade", Boolean.valueOf(dpps.isShowPlayerGrade()).toString());
        emDPParameterSet.setAttribute("showPlayerCountry", Boolean.valueOf(dpps.isShowPlayerCountry()).toString());
        emDPParameterSet.setAttribute("showPlayerClub", Boolean.valueOf(dpps.isShowPlayerClub()).toString());
        
        emDPParameterSet.setAttribute("showByePlayer", Boolean.valueOf(dpps.isShowByePlayer()).toString());
        emDPParameterSet.setAttribute("showNotPairedPlayers", Boolean.valueOf(dpps.isShowNotPairedPlayers()).toString());
        emDPParameterSet.setAttribute("showNotParticipatingPlayers", Boolean.valueOf(dpps.isShowNotParticipatingPlayers()).toString());
        emDPParameterSet.setAttribute("showNotFinallyRegisteredPlayers", Boolean.valueOf(dpps.isShowNotFinallyRegisteredPlayers()).toString());
        
        emDPParameterSet.setAttribute("displayNumCol", Boolean.valueOf(dpps.isDisplayNumCol()).toString());
        emDPParameterSet.setAttribute("displayPlCol", Boolean.valueOf(dpps.isDisplayPlCol()).toString());
        emDPParameterSet.setAttribute("displayCoCol", Boolean.valueOf(dpps.isDisplayCoCol()).toString());
        emDPParameterSet.setAttribute("displayClCol", Boolean.valueOf(dpps.isDisplayClCol()).toString());
        emDPParameterSet.setAttribute("displayIndGamesInMatches", Boolean.valueOf(dpps.isDisplayIndGamesInMatches()).toString());
        
        emTournamentParameterSet.appendChild(emDPParameterSet);

        // PublishParameterSet
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        Element emPublishParameterSet = document.createElement("PublishParameterSet");
        
        emPublishParameterSet.setAttribute("print", Boolean.valueOf(pubPS.isPrint()).toString());
        emPublishParameterSet.setAttribute("exportToLocalFile", Boolean.valueOf(pubPS.isExportToLocalFile()).toString());
        emPublishParameterSet.setAttribute("exportHFToOGSite", Boolean.valueOf(pubPS.isExportHFToOGSite()).toString());
        emPublishParameterSet.setAttribute("exportTFToOGSite", Boolean.valueOf(pubPS.isExportTFToOGSite()).toString());
        emPublishParameterSet.setAttribute("exportToUDSite", Boolean.valueOf(pubPS.isExportToUDSite()).toString());
        emPublishParameterSet.setAttribute("htmlAutoScroll", Boolean.valueOf(pubPS.isHtmlAutoScroll()).toString());
        
        emTournamentParameterSet.appendChild(emPublishParameterSet);
       
        return emTournamentParameterSet;
    }

    private static Element generateXMLTeamTournamentParameterSetElement(Document document, TeamTournamentParameterSet ttps) {
        Element emTeamTournamentParameterSet = document.createElement("TeamTournamentParameterSet");

        TeamGeneralParameterSet tgps = ttps.getTeamGeneralParameterSet();
        Element emTeamGeneralParameterSet = document.createElement("TeamGeneralParameterSet");
        emTeamGeneralParameterSet.setAttribute("teamSize", "" + tgps.getTeamSize());

        emTeamTournamentParameterSet.appendChild(emTeamGeneralParameterSet);

        // TeamPlacementParameterSet
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        Element emTeamPlacementParameterSet = document.createElement("TeamPlacementParameterSet");

        Element emPlacementCriteria = document.createElement("PlacementCriteria");
        int[] plaC = tpps.getPlaCriteria();
        for (int c = 0; c < plaC.length; c++) {
            Element emPlacementCriterion = document.createElement("PlacementCriterion");
            emPlacementCriterion.setAttribute("number", "" + (c + 1));
            emPlacementCriterion.setAttribute("name", TeamPlacementParameterSet.criterionLongName(plaC[c]));
            emPlacementCriteria.appendChild(emPlacementCriterion);
        }
        emTeamPlacementParameterSet.appendChild(emPlacementCriteria);

        emTeamTournamentParameterSet.appendChild(emTeamPlacementParameterSet);

        return emTeamTournamentParameterSet;
    }

        public static File chooseAFileForExport(TournamentInterface tournament, File path, String extension) {
        JFileChooser fileChoice = new JFileChooser(path);

        fileChoice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChoice.setDialogType(JFileChooser.SAVE_DIALOG);
        String shortName = "TournamentShortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        fileChoice.setSelectedFile(new File(path, shortName + "." + extension));

        MyFileFilter mff = new MyFileFilter(new String[]{extension}, "*." + extension);
        fileChoice.addChoosableFileFilter(mff);
        int result = fileChoice.showSaveDialog(null);
        if (result == JFileChooser.CANCEL_OPTION) {
            return null;
        } else {
            return fileChoice.getSelectedFile();
        }
    }

    /**
     * Parses a Result Line and inserts all half games found into alHalfGames
     * @param strResultLine
     * @param alHalfGames
     */
    private static void parseResultLine(int playerNumber, String strResultLine, ArrayList<PotentialHalfGame> alPotentialHalfGames) {
        String strDraft = strResultLine.trim();
        int roundNumber = 0;
        while (strDraft.length() > 0) {
            String strPHG;
            int endIndex = strDraft.indexOf(' ');
            if (endIndex > 0) {
                strPHG = strDraft.substring(0, endIndex);
            } else {
                strPHG = strDraft;
            }
            PotentialHalfGame phg = parsePotentialHalfGame(strPHG);
            phg.roundNumber = roundNumber++;
            phg.playerNumber = playerNumber;

            alPotentialHalfGames.add(phg);

            strDraft = strDraft.substring(strPHG.length());
            strDraft = strDraft.trim();
        }
    }

    /**
     * Parses a Half Game String into a HalfGame   
     * @param strHalfGame
     * @return a HalfGame Object
     */
    private static PotentialHalfGame parsePotentialHalfGame(String strHalfGame) {
        int posResult = -1;
        int indResult;
        boolean bByDef = false;
        if (strHalfGame.indexOf("!") >= 0) {
            bByDef = true;
        }
        strHalfGame = strHalfGame.replaceAll("!", "");
        strHalfGame = strHalfGame.replaceAll("/", "");

        indResult = strHalfGame.indexOf("+");
        if (indResult >= 0) {
            posResult = indResult;
        }
        indResult = strHalfGame.indexOf("-");
        if (indResult >= 0) {
            posResult = indResult;
        }
        indResult = strHalfGame.indexOf("=");
        if (indResult >= 0) {
            posResult = indResult;
        }
        indResult = strHalfGame.indexOf("?");
        if (indResult >= 0) {
            posResult = indResult;
        }
        String strOpponentNumber;
        String strResult;
        if (posResult <= 0) {
            strOpponentNumber = "";
        } else {
            strOpponentNumber = strHalfGame.substring(0, posResult);
        }
        if (posResult < 0) {
            strResult = "";
        } else {
            strResult = strHalfGame.substring(posResult, posResult + 1);
        }

        String strCH = strHalfGame.substring(posResult + 1, strHalfGame.length());
        String strColor;
        String strHandicap;
        if (strCH.length() <= 0) {
            strColor = "?";
            strHandicap = "0";
        } else {
            strColor = strCH.substring(0, 1).toLowerCase();
            if (strCH.length() <= 1) {
                strHandicap = "0";
            } else {
                strHandicap = strCH.substring(1);
            }
        }
        if (strColor.equals("w") || strColor.equals("b") || strColor.equals("?")) {
            if (strCH.length() <= 1) {
                strHandicap = "0";
            } else {
                strHandicap = strCH.substring(1);
            }
        }

        // Now we've got  strOpponentNumber, strResult, strColor and strHandicap;
        // Let's build phg               
        PotentialHalfGame phg = new PotentialHalfGame();
        try {
            phg.opponentNumber = (new Integer(strOpponentNumber).intValue() - 1);
        } catch (NumberFormatException ex) {
            phg.opponentNumber = -1;
            // Logger.getLogger(ExternalTournamentDocument.class.getName()).log(Level.SEVERE, null, ex)
        }
        if (strResult.equals("+")) {
            phg.result = 1;
        } else if (strResult.equals("-")) {
            phg.result = -1;
        } else {
            phg.result = 0;
        }
        // Consider by def particularity
        phg.bydef = bByDef;

        phg.color = strColor.charAt(0);
        phg.handicap = new Integer(strHandicap).intValue();
        return phg;
    }
}

/**
 * Represents the result of a player for one game.
 * It is named PotentialHalfGame because 2 instances of PotentialHalfGame (1 for each player) may 
 * make a game if coherence is found
 */
class PotentialHalfGame {

    /** from 0 to 31 */
    public int roundNumber;
    /** from 0 to ... */
    public int playerNumber;
    /** from 0 to ... , -1 if no opponent*/
    public int opponentNumber;
    /** 1, -1 or 0 */
    public int result;
    /** true or false */
    public boolean bydef;
    /** 'b', 'w' or '?' */
    public char color;
    /** from 0 to 9 */
    public int handicap;
}
