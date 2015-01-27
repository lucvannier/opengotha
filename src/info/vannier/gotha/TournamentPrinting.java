/*
 * TournamentPrinting.java
 */
package info.vannier.gotha;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * TournamentPrinting manages printing jobs.
 *
 * @author LV
 */
public class TournamentPrinting implements Printable {
    // PL = Players List
    static final int PL_NUMBER_BEG = 0;
    static final int PL_NUMBER_LEN = 4;
    static final int PL_PINLIC_BEG = PL_NUMBER_BEG + PL_NUMBER_LEN + 1;
    static final int PL_PINLIC_LEN = 8;
    static final int PL_NF_BEG = PL_PINLIC_BEG + PL_PINLIC_LEN + 1;
    static final int PL_NF_LEN = 25;
    static final int PL_COUNTRY_BEG = PL_NF_BEG + PL_NF_LEN + 1;
    static final int PL_COUNTRY_LEN = 2;
    static final int PL_CLUB_BEG = PL_COUNTRY_BEG + PL_COUNTRY_LEN + 1;
    static final int PL_CLUB_LEN = 4;
    static final int PL_RANK_BEG = PL_CLUB_BEG + PL_CLUB_LEN + 1;
    static final int PL_RANK_LEN = 3;
    static final int PL_GRADE_BEG = PL_RANK_BEG;
    static final int PL_GRADE_LEN = PL_RANK_LEN;
    static final int PL_RT_BEG = PL_RANK_BEG + PL_RANK_LEN + 1;
    static final int PL_RT_LEN = 4;
    static final int PL_MM_BEG = PL_RT_BEG + PL_RT_LEN + 1;
    static final int PL_MM_LEN = 3;
    static final int PL_PART_BEG = PL_MM_BEG + PL_MM_LEN + 1;
    static final int PL_PART_LEN = Gotha.MAX_NUMBER_OF_ROUNDS;
    static final int PL_PADDING = 0;
    static final int PL_NBCAR = PL_PART_BEG + PL_PART_LEN + PL_PADDING;
    // TL = Teams List
    static final int TL_NUMBER_BEG = 0;
    static final int TL_NUMBER_LEN = 4;
    static final int TL_TEAMNAME_BEG = TL_NUMBER_BEG + TL_NUMBER_LEN + 1;
    static final int TL_TEAMNAME_LEN = 20;
    static final int TL_BOARD_BEG = TL_TEAMNAME_BEG + TL_TEAMNAME_LEN + 1;
    static final int TL_BOARD_LEN = 2;
    static final int TL_NF_BEG = TL_BOARD_BEG + TL_BOARD_LEN + 1;
    static final int TL_NF_LEN = 20;
    static final int TL_COUNTRY_BEG = TL_NF_BEG + TL_NF_LEN + 1;
    static final int TL_COUNTRY_LEN = 2;
    static final int TL_CLUB_BEG = TL_COUNTRY_BEG + TL_COUNTRY_LEN + 1;
    static final int TL_CLUB_LEN = 4;
    static final int TL_RATING_BEG = TL_CLUB_BEG + TL_CLUB_LEN + 1;
    static final int TL_RATING_LEN = 4;
    static final int TL_MEMBER_BEG = TL_RATING_BEG + TL_RATING_LEN + 1;
    static final int TL_MEMBER_LEN = Gotha.MAX_NUMBER_OF_ROUNDS;
    static final int TL_PADDING = 0;
    static final int TL_NBCAR = TL_MEMBER_BEG + TL_MEMBER_LEN + TL_PADDING; //  
    // GL = Games List
    static final int GL_TN_BEG = 0; // Table Number
    static final int GL_TN_LEN = 4;
    static final int GL_WNF_BEG = GL_TN_BEG + GL_TN_LEN + 1;
    static final int GL_WNF_LEN = 33;       // 22 + 1 + 2 + 3 + 1 + 4
    static final int GL_BNF_BEG = GL_WNF_BEG + GL_WNF_LEN + 1;
    static final int GL_BNF_LEN = 33;
    static final int GL_HD_BEG = GL_BNF_BEG + GL_BNF_LEN + 1;
    static final int GL_HD_LEN = 1;
    static final int GL_RES_BEG = GL_HD_BEG + GL_HD_LEN + 1;
    static final int GL_RES_LEN = 3;
    static final int GL_PADDING = 2;
    static final int GL_NBCAR = GL_RES_BEG + GL_RES_LEN + GL_PADDING;
    // RS = Result sheets
    static final int RS_RSBYPAGE = 2; // Actual number of result sheets by page   
//    static final int RS_RS_HEIGHT = 200; // Result sheet height. virtual units. Calibrated for a page of 800 virtual units
    static final int RS_LINE_HEIGHT = 20; // Line height    
    static final int RS_TITLE1 = 10;
    static final int RS_TITLE2 = RS_TITLE1 + RS_LINE_HEIGHT / 2;
    static final int RS_TABLE = RS_TITLE2 + RS_LINE_HEIGHT * 3 / 2;
    static final int RS_COLOR = RS_TABLE + RS_LINE_HEIGHT * 3 / 2;
    static final int RS_PLAYERNAME = RS_COLOR + RS_LINE_HEIGHT;
    static final int RS_ID = RS_PLAYERNAME + RS_LINE_HEIGHT;
    static final int RS_SIGN = RS_ID + RS_LINE_HEIGHT;
    static final int RS_RS_HEIGHT = RS_SIGN + RS_LINE_HEIGHT * 3;
    static final int RS_PAGE_VIRTUAL_HEIGHT = RS_RSBYPAGE * RS_RS_HEIGHT;
    
    static final int RS_PAGE_VIRTUAL_WIDTH = 600;
    static final int RS_COL1 = 20;
    static final int RS_COL2 = 265;
    static final int RS_COL3 = 335;
    static final int RS_COL4 = 580;
    
    static final int RS_LEFTMARGIN = 10;
    
    // Non-playing list
    static final int NPL_REASON_BEG = 0;
    static final int NPL_REASON_LEN = 20;
    static final int NPL_NF_BEG = NPL_REASON_BEG + NPL_REASON_LEN + 1;
    static final int NPL_NF_LEN = 25;
    static final int NPL_RANK_BEG = NPL_NF_BEG + NPL_NF_LEN + 1;
    static final int NPL_RANK_LEN = 3;
    static final int NPL_GRADE_BEG = NPL_RANK_BEG;
    static final int NPL_GRADE_LEN = PL_RANK_LEN;
    static final int NPL_PADDING = 30;
    static final int NPL_NBCAR = NPL_RANK_BEG + NPL_RANK_LEN + NPL_PADDING; // 87
    // ML = Matches List
    static final int ML_TN_BEG = 1; // Table Number
    static final int ML_TN_LEN = 7;
    static final int ML_WTN_BEG = ML_TN_BEG + ML_TN_LEN + 1;
    static final int ML_WTN_LEN = 25;
    static final int ML_BTN_BEG = ML_WTN_BEG + ML_WTN_LEN + 1;
    static final int ML_BTN_LEN = 25;
    static final int ML_HD_BEG = ML_BTN_BEG + ML_BTN_LEN + 1;
    static final int ML_HD_LEN = 1;
    static final int ML_RES_BEG = ML_HD_BEG + ML_HD_LEN + 1;
    static final int ML_RES_LEN = 3;
    static final int ML_PADDING = 2;
    static final int ML_NBCAR = ML_RES_BEG + ML_RES_LEN + ML_PADDING;
    // ST = Standings // Dynamic system (V3.29.03)
    private int stNumBeg = 0;
    static final int ST_NUM_LEN = 4;
    private int stPlBeg;
    static final int ST_PL_LEN = 4;
    private int stNFBeg;
    static final int ST_NF_LEN = 22;
    private int stRkBeg;
    static final int ST_RK_LEN = 3;
    private int stGrBeg;
    static final int ST_GR_LEN = ST_RK_LEN;
    private int stCoBeg;
    static final int ST_CO_LEN = 2;
    private int stClBeg;
    static final int ST_CL_LEN = 4;
    private int stNbWBeg;
    static final int ST_NBW_LEN = 3;
    private int stRound0Beg;
    static final int ST_ROUND_LEN_FULL_FORM = 8;
    static final int ST_ROUND_LEN_SHORT_FORM = 5;
    private int stRoundLen = ST_ROUND_LEN_FULL_FORM;
    private int stCrit0Beg;
    static final int ST_CRIT_LEN = 6;
    static final int ST_PADDING = 1;
    // TST = Team Standings
    static final int TST_NUM_BEG = 0;
    static final int TST_NUM_LEN = 4;
    static final int TST_PL_BEG = TST_NUM_BEG + TST_NUM_LEN + 1;
    static final int TST_PL_LEN = 4;
    static final int TST_TN_BEG = TST_PL_BEG + TST_PL_LEN + 1;
    static final int TST_TN_LEN = 22;
    static final int TST_ROUND0_BEG = TST_TN_BEG + TST_TN_LEN + 1;
    static final int TST_ROUND_LEN = 8;
    static final int TST_CRIT_LEN = 6;
    static final int TST_PADDING = 1;
    static final int TST_NBFXCAR = TST_ROUND0_BEG + ST_PADDING;  // at runtime, numberOfCharactersInALine will be computed by adding round and crit infos
    // TP = Tournament Parameters
    static final int TP_TAB1 = 6;
    static final int TP_TAB2 = 12;
    static final int TP_TAB3 = 18;
    static final int TP_TAB4 = 24;
    static final int TP_NBCAR = 80;
    
    static final int WH_RATIO = 50;          // Width/Height ratio (%)
    static final int LINEFILLING_RATIO = 90; // Line filling ratio (%)
    static final int LHFS_RATIO = 140;       // Line Height/Font Size ratio (%)
    TournamentInterface tournament;
    private TournamentParameterSet tps; // this tps may differ with the tps in tournament. 
                                        // The reason is that some placement Criteria may differ (print Standings with temporary Criteria
    private TeamTournamentParameterSet ttps; 
    
    private int printType;
    private int printSubType;
    

    /**
     * from 0 to ...
     */
    private int roundNumber = -1;
    // For PlayersList and NotPlayingList
    ArrayList<Player> alPlayersToPrint;
    // For TeamsList
    ArrayList<Team> alTeamsToPrint;
    TeamMemberStrings[] arTMS;
    // For Standings
    private ArrayList<ScoredPlayer> alOrderedScoredPlayers;
    private String[][] halfGamesStrings;
    private int[] criteria;
    private String[] strPlace;
    // For TeamsStandings
    private ScoredTeamsSet scoredTeamsSet;
    private ArrayList<ScoredTeam> alOrderedScoredTeams;
    
    PrinterJob printerJob;
    PageFormat pageFormat;
    // These variables are computed by print method at first call    
    // Upper_Left coordinates, width and height of the usable printing page area
    private int usableX = -1;
    private int usableY = -1;
    private int usableWidth = -1;
    private int usableHeight = -1;
    private int fontSize;
    private int lineHeight;
    private int numberOfBodyLinesInAPage;
    private int numberOfPages;
    private int numberOfCharactersInALine;
    
    // Matches List specificities
    private int matchesPerPage;

    private TournamentPrinting(TournamentInterface tournament) {
        this.tournament = tournament;
        try {
            this.tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        startPrinterJob();
    }
    private TournamentPrinting(TournamentInterface tournament, TournamentParameterSet tps) {
        this.tournament = tournament;
        this.tps = tps;
        startPrinterJob();
    }
    private TournamentPrinting(TournamentInterface tournament, TournamentParameterSet tps, TeamTournamentParameterSet ttps) {
        this.tournament = tournament;
        this.tps = tps;
        this.ttps = ttps;
        startPrinterJob();
    }
    private void startPrinterJob(){
        printerJob = PrinterJob.getPrinterJob();
        pageFormat = new PageFormat();

        Paper paper = new Paper();
        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());  // forces Imageable to maximum
        pageFormat.setPaper(paper);
        printerJob.setPrintable(this, pageFormat);
    }

    public static void printPlayersList(TournamentInterface tournament){
        int playersSortType = PlayerComparator.NAME_ORDER;
        try {
            playersSortType = tournament.getTournamentParameterSet().getDPParameterSet().getPlayerSortType();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        printPlayersList(tournament, playersSortType);
    }
    public static void printPlayersList(TournamentInterface tournament, int playersSortType){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(-1);
        tpr.makePrinting(TournamentPublishing.TYPE_PLAYERSLIST, playersSortType, true);        
    }
    public static void printTeamsList(TournamentInterface tournament){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(-1);
        tpr.makePrinting(TournamentPublishing.TYPE_TEAMSLIST, TournamentPublishing.SUBTYPE_DEFAULT, true);        
    }
    public static void printTournamentParameters(TournamentInterface tournament){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(-1);
        tpr.makePrinting(TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS, TournamentPublishing.SUBTYPE_DEFAULT, true);
    }
    public static void printGamesList(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_GAMESLIST, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    public static void printResultSheets(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_RESULTSHEETS, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    public static void printNotPlayingPlayersList(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_NOTPLAYINGLIST, PlayerComparator.NAME_ORDER, true);   
    }
    
    public static void printStandings(TournamentInterface tournament, int roundNumber){
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        printStandings(tournament, tps, roundNumber);
    }
    public static void printStandings(TournamentInterface tournament, TournamentParameterSet tps, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament, tps);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_STANDINGS, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    public static void printMatchesList(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_MATCHESLIST, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    public static void printTeamsStandings(TournamentInterface tournament, int roundNumber){
        TournamentParameterSet tps = null;
        TeamTournamentParameterSet ttps = null;
        try {
            tps = tournament.getTournamentParameterSet();
            ttps = tournament.getTeamTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        printTeamsStandings(tournament, tps, ttps, roundNumber); 
    }
    public static void printTeamsStandings(TournamentInterface tournament, TournamentParameterSet tps,
            TeamTournamentParameterSet ttps, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament, tps, ttps);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_TEAMSSTANDINGS, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    /**
     * makePrinting 
     * 1) prepares the objects to be printed (if objects have to be prepared, sorted for example)
     * 2) manages the print Dialog to choose the printer (if askForPrionter == true)
     * 3) calls  printerJob.print()
     * 
     * @param printType
     * @param printSubType
     * @param askForPrinter 
     */
    private void makePrinting(int printType, int printSubType, boolean askForPrinter) {
        this.printType = printType;
        this.printSubType = printSubType;
        switch (printType) {
            case TournamentPublishing.TYPE_PLAYERSLIST:
                preparePrintPlayersList();
                break;
            case TournamentPublishing.TYPE_TEAMSLIST:
                preparePrintTeamsList();
                break;
            case TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS:
                this.preparePrintTournamentParameters();
                break;
            case TournamentPublishing.TYPE_GAMESLIST:
                this.preparePrintGamesList();
                break;
            case TournamentPublishing.TYPE_RESULTSHEETS:
                this.preparePrintResultSheets();
                break;
            case TournamentPublishing.TYPE_NOTPLAYINGLIST:
                this.preparePrintNPPList();
                break;
            case TournamentPublishing.TYPE_STANDINGS:
                this.preparePrintStandings();
                break;
            case TournamentPublishing.TYPE_MATCHESLIST:
                this.preparePrintMatchesList();
                break;
            case TournamentPublishing.TYPE_TEAMSSTANDINGS:
                this.preparePrintTeamsStandings();
                break;
        }

        if (!askForPrinter || printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }
    
    private void preparePrintPlayersList(){
        int playersSortType = printSubType;
        try {
            alPlayersToPrint = new ArrayList<Player>(tournament.playersList());
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(alPlayersToPrint, playerComparator);
    }
    
    private void preparePrintTeamsList(){
        try {
            arTMS = TeamMemberStrings.buildTeamMemberStrings(tournament);
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void preparePrintTournamentParameters(){
    }
    
    private void preparePrintGamesList(){
    }
    
    private void preparePrintResultSheets(){
    }
    
    
    
    private void preparePrintNPPList(){
        DPParameterSet dpps = tps.getDPParameterSet();
        
        PlayerComparator playerComparator = new PlayerComparator(PlayerComparator.NAME_ORDER);
        alPlayersToPrint = new ArrayList<Player>();
        try {
            // Bye player
            Player bP = tournament.getByePlayer(roundNumber);
            if (dpps.isShowByePlayer() && bP != null) {
                alPlayersToPrint.add(bP);
            }
            // Not paired players
            ArrayList<Player> alNotPairedPlayers = tournament.alNotPairedPlayers(roundNumber);
            if (dpps.isShowNotPairedPlayers() && alNotPairedPlayers != null) {
                Collections.sort(alNotPairedPlayers, playerComparator);
                alPlayersToPrint.addAll(alNotPairedPlayers);
            }
            // Not participating players
            ArrayList<Player> alNotParticipatingPlayers = tournament.alNotParticipantPlayers(roundNumber);
            if (dpps.isShowNotParticipatingPlayers() && alNotParticipatingPlayers != null) {
                Collections.sort(alNotParticipatingPlayers, playerComparator);
                alPlayersToPrint.addAll(alNotParticipatingPlayers);
            }
            // Not FIN Reg
            ArrayList<Player> alNotFINRegisteredPlayers = tournament.alNotFINRegisteredPlayers();
            if (alNotFINRegisteredPlayers != null) {
                Collections.sort(alNotFINRegisteredPlayers, playerComparator);
                alPlayersToPrint.addAll(alNotFINRegisteredPlayers);
            }

        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void preparePrintStandings(){
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        int[] crit = this.tps.getPlacementParameterSet().getPlaCriteria();
        int nbCriteria = 0;
        for (int c = 0; c < crit.length; c++){
            if (crit[c] != PlacementParameterSet.PLA_CRIT_NUL) nbCriteria++;
        }
        this.criteria = PlacementParameterSet.purgeUselessCriteria(crit);
//        this.criteria = new int[nbCriteria];
//        int numCrit = 0;
//        for (int c = 0; c < crit.length; c++){
//            if (crit[c] != PlacementParameterSet.PLA_CRIT_NUL) criteria[numCrit++] = crit[c];
//        }
        
        // Do we print by category ?
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        if (pps.getPlaCriteria()[0] == PlacementParameterSet.PLA_CRIT_CAT && gps.getNumberOfCategories() > 1) {
            this.printSubType = TournamentPublishing.SUBTYPE_ST_CAT;
        } else {
             this.printSubType = TournamentPublishing.SUBTYPE_DEFAULT;
        }                                                 
 
        try {
            this.alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean bFull = true;
        if (dpps.getGameFormat() == DPParameterSet.DP_GAME_FORMAT_FULL) bFull = true;
        else bFull = false;
        
        this.halfGamesStrings = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, roundNumber, tps, bFull);
    }

    private void preparePrintMatchesList(){
    }
    
    private void preparePrintTeamsStandings(){
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        DPParameterSet dpps = this.tps.getDPParameterSet();
        int[] crit = this.ttps.getTeamPlacementParameterSet().getPlaCriteria();
        int nbCriteria = 0;
        for (int c = 0; c < crit.length; c++){
            if (crit[c] != TeamPlacementParameterSet.TPL_CRIT_NUL) nbCriteria++;
        }
        this.criteria = PlacementParameterSet.purgeUselessCriteria(crit);
//        this.criteria = new int[nbCriteria];
//        int numCrit = 0;
//        for (int c = 0; c < crit.length; c++){
//            if (crit[c] != PlacementParameterSet.PLA_CRIT_NUL) criteria[numCrit++] = crit[c];
//        }
//        
        scoredTeamsSet = null;
        try {
            scoredTeamsSet = tournament.getAnUpToDateScoredTeamsSet(tpps, roundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        alOrderedScoredTeams = scoredTeamsSet.getOrderedScoredTeamsList();
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pi) {
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();

        if (usableX < 0) {          // if it is the first call to print
            usableX = (int) pf.getImageableX() + 1;
            usableY = (int) pf.getImageableY() + 1;
            usableWidth = (int) pf.getImageableWidth() - 2;
            usableHeight = (int) pf.getImageableHeight() - 2;

            switch (printType) {
                case TournamentPublishing.TYPE_DEFAULT:
                    fontSize = 12;
                    lineHeight = fontSize * LHFS_RATIO;
                    break;
                case TournamentPublishing.TYPE_PLAYERSLIST:
                    int nbCarRef = PL_NBCAR;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100;
                    try {
                        int numberOfBodyLines = tournament.numberOfPlayers();
                        numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;
                        numberOfPages = (numberOfBodyLines + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                    } catch (RemoteException ex) {
                        Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case TournamentPublishing.TYPE_TEAMSLIST:
                    nbCarRef = TL_NBCAR;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100; {
                    int numberOfBodyLines = 0;
                    for (int i = 0; i < arTMS.length; i++) {
                        numberOfBodyLines = i + 1;
                        if (arTMS[i] == null) {
                            break;
                        }
                    }
                    numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;
                    numberOfPages = (numberOfBodyLines + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                    }
                    break;
                case TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS:
                    nbCarRef = TP_NBCAR;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100;
                    numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;
                    numberOfPages = 2;
                    break;
                case TournamentPublishing.TYPE_GAMESLIST:
                    nbCarRef = GL_NBCAR;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100;
                    try {
                        int numberOfBodyLines = tournament.gamesList(roundNumber).size();
                        if (tournament.getByePlayer(roundNumber) != null) numberOfBodyLines++;
                        numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;
                        numberOfPages = (numberOfBodyLines + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                    } catch (RemoteException ex) {
                        Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case TournamentPublishing.TYPE_RESULTSHEETS:
                    nbCarRef = 85;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;

                    usableX = (int) pf.getImageableX();
                    usableY = (int) pf.getImageableY();
                    usableWidth = (int) pf.getImageableWidth();
                    usableHeight = (int) pf.getImageableHeight();
//                    System.out.println("usableX = " + usableX);
//                    System.out.println("usableY = " + usableY);
//                    System.out.println("usableWidth = " + usableWidth);
//                    System.out.println("usableHeight = " + usableHeight);
                    break;
                case TournamentPublishing.TYPE_NOTPLAYINGLIST:
                    nbCarRef = NPL_NBCAR;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100; {
                    int numberOfBodyLines = this.alPlayersToPrint.size();
                    numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;
                    numberOfPages = (numberOfBodyLines + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                    }
                    break;
                case TournamentPublishing.TYPE_STANDINGS:
                    int numberOfRoundsPrinted = roundNumber + 1;
                    int numberOfCriteriaPrinted = criteria.length;
                    stNumBeg = 0;
                    if (dpps.isDisplayNumCol()) {
                        stPlBeg = stNumBeg + TournamentPrinting.ST_NUM_LEN + ST_PADDING;
                    } else {
                        stPlBeg = stNumBeg;
                    }
                    if (dpps.isDisplayPlCol()) {
                        stNFBeg = stPlBeg + TournamentPrinting.ST_PL_LEN + ST_PADDING;
                    } else {
                        stNFBeg = stPlBeg;
                    }
//                    stRkBeg = stNFBeg + TournamentPrinting.ST_NF_LEN + ST_PADDING;
                    stGrBeg = stNFBeg + TournamentPrinting.ST_NF_LEN + ST_PADDING;
//                    stCoBeg = stRkBeg + TournamentPrinting.ST_RK_LEN + ST_PADDING;
                    stCoBeg = stGrBeg + TournamentPrinting.ST_GR_LEN + ST_PADDING;
                    if (dpps.isDisplayCoCol()){
                        stClBeg = stCoBeg + TournamentPrinting.ST_CO_LEN + ST_PADDING;
                    }
                    else{
                        stClBeg = stCoBeg;
                    }
                    if (dpps.isDisplayClCol()){
                        stNbWBeg = stClBeg + TournamentPrinting.ST_CL_LEN + ST_PADDING;
                    }
                    else{
                        stNbWBeg = stClBeg;
                    }
                    stRound0Beg = stNbWBeg + TournamentPrinting.ST_NBW_LEN + ST_PADDING;
                    stCrit0Beg = stRound0Beg + numberOfRoundsPrinted * (stRoundLen + ST_PADDING);

                    numberOfCharactersInALine = stCrit0Beg + numberOfCriteriaPrinted * (TournamentPrinting.ST_CRIT_LEN + ST_PADDING);

                    strPlace = ScoredPlayer.catPositionStrings(alOrderedScoredPlayers, roundNumber, tps);
                                
                    fontSize = usableWidth / this.numberOfCharactersInALine * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100;
                    numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;

                    int numberOfBodyLines = this.alOrderedScoredPlayers.size();
                    numberOfPages = (numberOfBodyLines + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                    if (this.printSubType == TournamentPublishing.SUBTYPE_ST_CAT) {
                        numberOfPages = 0;
                        for (int numCat = 0; numCat < gps.getNumberOfCategories(); numCat++) {
                            int nbPl = 0;
                            try {
                                nbPl = tournament.numberOfPlayersInCategory(numCat, alOrderedScoredPlayers);
                            } catch (RemoteException ex) {
                                Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            numberOfPages += (nbPl + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                        }
                    }
                    break;

                case TournamentPublishing.TYPE_MATCHESLIST:
                    nbCarRef = ML_NBCAR;
                    fontSize = usableWidth / nbCarRef * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    lineHeight = fontSize * LHFS_RATIO / 100;
                    try {
                        int linesPerMatch = 1;
                        TeamTournamentParameterSet ttps = tournament.getTeamTournamentParameterSet();
                        if (dpps.isDisplayIndGamesInMatches()) linesPerMatch = 1 + ttps.getTeamGeneralParameterSet().getTeamSize();
                        else linesPerMatch = 1;
                        numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;
                        this.matchesPerPage = numberOfBodyLinesInAPage / linesPerMatch;
                        matchesPerPage = Math.max(1, matchesPerPage);
                        numberOfPages = (tournament.matchesList(roundNumber).size() + matchesPerPage - 1) / matchesPerPage;
                    } catch (RemoteException ex) {
                        Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case TournamentPublishing.TYPE_TEAMSSTANDINGS:
                    numberOfRoundsPrinted = roundNumber + 1;
                    numberOfCriteriaPrinted = criteria.length;
                    this.numberOfCharactersInALine = TST_NBFXCAR + numberOfRoundsPrinted * TST_ROUND_LEN +
                            numberOfCriteriaPrinted * TST_CRIT_LEN;
                    fontSize = usableWidth / this.numberOfCharactersInALine * 100 / WH_RATIO * LINEFILLING_RATIO / 100;
                    
                    lineHeight = fontSize * LHFS_RATIO / 100;
                    numberOfBodyLinesInAPage = (usableHeight / lineHeight) - 5;

                    numberOfBodyLines = alOrderedScoredTeams.size();
                    numberOfPages = (numberOfBodyLines + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
                    break;
            }

        }

        try {
            switch (printType) {
                case TournamentPublishing.TYPE_DEFAULT:
                    return printADefaultPage(g, pf, pi);
                case TournamentPublishing.TYPE_PLAYERSLIST:
                    return printAPageOfPlayersList(g, pf, pi);
                case TournamentPublishing.TYPE_TEAMSLIST:
                    return printAPageOfTeamsList(g, pf, pi);
                case TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS:
                    return printAPageOfTournamentParameters(g, pf, pi);
                case TournamentPublishing.TYPE_GAMESLIST:
                    return printAPageOfGamesList(g, pf, pi);
                case TournamentPublishing.TYPE_RESULTSHEETS:
                    return printAPageOfResultSheets(g, pf, pi);
                case TournamentPublishing.TYPE_NOTPLAYINGLIST:
                    return printAPageOfNotPlayingPlayersList(g, pf, pi);
                case TournamentPublishing.TYPE_STANDINGS:
                    return printAPageOfStandings(g, pf, pi);
                case TournamentPublishing.TYPE_MATCHESLIST:
                    return printAPageOfMatchesList(g, pf, pi);
                case TournamentPublishing.TYPE_TEAMSSTANDINGS:
                    return printAPageOfTeamsStandings(g, pf, pi);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        return PAGE_EXISTS;
    }

    private int printAPageOfPlayersList(Graphics g, PageFormat pf, int pi) throws RemoteException {
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Players List";
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);

        // Header Line
        printPlayersListHeaderLine(g, pf, pi);

        // Body lines
        int ln;
        for (ln = 0; ln < numberOfBodyLinesInAPage; ln++) {
            int abstractLineNumber = ln + pi * numberOfBodyLinesInAPage;
            int playerNumber = abstractLineNumber;
            if (playerNumber >= alPlayersToPrint.size()) {
                break;
            }
            Player player = alPlayersToPrint.get(playerNumber);
            y = usableY + (4 + ln) * lineHeight;
            if ((ln % 2) == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(usableX, y - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                g.setColor(Color.BLACK);
            }
            if (player.getRegisteringStatus().compareTo("FIN") == 0) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.RED);
            }
            String strNumber = "" + (playerNumber + 1);
            x = usableX + usableWidth * (PL_NUMBER_BEG + PL_NUMBER_LEN) / PL_NBCAR;
            drawRightAlignedString(g, strNumber, x, y);

            String strPinLic = player.getEgfPin();
            if (strPinLic.length() == 0) {
                strPinLic = player.getFfgLicence();
            }
            if (strPinLic.length() == 0) {
                strPinLic = player.getAgaId();
            }
            if (strPinLic.length() == 0) {
                strPinLic = "--------";
            }

            x = usableX + usableWidth * PL_PINLIC_BEG / PL_NBCAR;
            Font fontCourier = new Font("Courier New", Font.BOLD, fontSize);
            g.setFont(fontCourier);
            g.drawString(strPinLic, x, y);
            g.setFont(font);    // back to default

            String strName = player.getName();
            String strFirstName = player.getFirstName();
            if (strName.length() > 20) {
                strName = strName.substring(0, 20);
            }
            String strNF = strName + " " + strFirstName;
            if (strNF.length() > 25) {
                strNF = strNF.substring(0, 25);
            }
            if (player.getRegisteringStatus().compareTo("PRE") == 0) {
                strNF += "(P)";
            }
            x = usableX + usableWidth * PL_NF_BEG / PL_NBCAR;
            g.drawString(strNF, x, y);

//            String strRk = Player.convertIntToKD(player.getRank());
//            x = usableX + usableWidth * (PL_RANK_BEG + PL_RANK_LEN) / PL_NBCAR;
//            drawRightAlignedString(g, strRk, x, y);
//            String strRt = "" + player.getRating();
//            x = usableX + usableWidth * (PL_RT_BEG + PL_RT_LEN) / PL_NBCAR;
//            drawRightAlignedString(g, strRt, x, y);

            String strGr = player.getStrGrade();
            x = usableX + usableWidth * (PL_GRADE_BEG + PL_GRADE_LEN) / PL_NBCAR;
            drawRightAlignedString(g, strGr, x, y);
            
            String strRt = "" + player.getRating();
            x = usableX + usableWidth * (PL_RT_BEG + PL_RT_LEN) / PL_NBCAR;
            drawRightAlignedString(g, strRt, x, y);

            String strMM = "" + player.smms(tournament.getTournamentParameterSet().getGeneralParameterSet());
            x = usableX + usableWidth * (PL_MM_BEG + PL_MM_LEN) / PL_NBCAR;
            drawRightAlignedString(g, strMM, x, y);

            String strCountry = player.getCountry();
            strCountry = Gotha.leftString(strCountry, 2);
            x = usableX + usableWidth * PL_COUNTRY_BEG / PL_NBCAR;
            g.drawString(strCountry, x, y);

            String strClub = player.getClub();
            strClub = Gotha.leftString(strClub, 4);
            x = usableX + usableWidth * PL_CLUB_BEG / PL_NBCAR;
            g.drawString(strClub, x, y);

            String strPart = Player.convertParticipationToString(player, tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds());
            x = usableX + usableWidth * PL_PART_BEG / PL_NBCAR;
            fontCourier = new Font("Courier New", Font.BOLD, fontSize);
            g.setFont(fontCourier);
            g.drawString(strPart, x, y);
            // Come back to default Color
            g.setFont(font);    // back to default            

            g.setColor(Color.BLACK);
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

        if (ln == 0) {
            return NO_SUCH_PAGE;
        }
        return PAGE_EXISTS;
    }

    private int printAPageOfTeamsList(Graphics g, PageFormat pf, int pi) throws RemoteException {
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Teams List";
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);

        // Header Line
        printTeamsListHeaderLine(g, pf, pi);

        // Body lines
        int ln;
        for (ln = 0; ln < numberOfBodyLinesInAPage; ln++) {
            int abstractLineNumber = ln + pi * numberOfBodyLinesInAPage;
            y = usableY + (4 + ln) * lineHeight;
            if ((ln % 2) == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(usableX, y - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                g.setColor(Color.BLACK);
            }
            TeamMemberStrings tms = arTMS[abstractLineNumber];
            if (tms == null) {
                break;
            }

            x = usableX + usableWidth * (TL_NUMBER_BEG + TL_NUMBER_LEN) / TL_NBCAR;
            drawRightAlignedString(g, tms.strTeamNumber, x, y);

            String strTeamName = tms.strTeamName;
            if (strTeamName.length() > TL_TEAMNAME_LEN) {
                strTeamName = strTeamName.substring(0, TL_TEAMNAME_LEN);
            }
            x = usableX + usableWidth * TL_TEAMNAME_BEG / TL_NBCAR;
            g.drawString(strTeamName, x, y);

            String strBoardNumber = tms.strBoardNumber;
            x = usableX + usableWidth * (TL_BOARD_BEG + TL_BOARD_LEN) / TL_NBCAR;
            drawRightAlignedString(g, strBoardNumber, x, y);

            String strNF = tms.strPlayerName;
            if (strNF.length() > TL_NF_LEN) {
                strNF = strNF.substring(0, TL_NF_LEN);
            }
            x = usableX + usableWidth * TL_NF_BEG / TL_NBCAR;
            g.drawString(strNF, x, y);

            String strCountry = "" + tms.strCountry;
            x = usableX + usableWidth * TL_COUNTRY_BEG / TL_NBCAR;
            g.drawString(strCountry, x, y);

            String strClub = "" + tms.strClub;
            x = usableX + usableWidth * TL_CLUB_BEG / TL_NBCAR;
            g.drawString(strClub, x, y);

            String strRating = "" + tms.strRating;
            x = usableX + usableWidth * (TL_RATING_BEG + TL_RATING_LEN) / TL_NBCAR;
            drawRightAlignedString(g, strRating, x, y);

            String strMembership = tms.strMembership;
            x = usableX + usableWidth * TL_MEMBER_BEG / TL_NBCAR;
            Font fontCourier = new Font("Courier New", Font.BOLD, fontSize);
            g.setFont(fontCourier);
            g.drawString(strMembership, x, y);
            g.setFont(font);    // back to default            
        }
                // Print Page Footer
        printPageFooter(g, pf, pi);

        if (ln == 0) {
            return NO_SUCH_PAGE;
        }
        return PAGE_EXISTS;
    }

    private int printAPageOfGamesList(Graphics g, PageFormat pf, int pi) throws RemoteException {
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Games List";
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);

        String strRound = "Round";
        strRound += " " + (roundNumber + 1);
        char[] cRound = strRound.toCharArray();
        int roundWidth = fm.charsWidth(cRound, 0, cRound.length);
        x = (usableWidth - roundWidth) / 2;
        y += lineHeight;
        g.drawString(strRound, x, y);

        printGamesListHeaderLine(g, pf, pi);

        ArrayList<Game> alGamesToPrint = new ArrayList<Game>(tournament.gamesList(roundNumber));

        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alGamesToPrint, gameComparator);
        
        DPParameterSet dpps = tps.getDPParameterSet();

        int ln;
        // Body lines
        for (ln = 0; ln < numberOfBodyLinesInAPage; ln++) {
            int abstractLineNumber = ln + pi * numberOfBodyLinesInAPage;
            int gameNumber = abstractLineNumber;
            if (gameNumber >= alGamesToPrint.size()) {
                break;
            }
            Game game = alGamesToPrint.get(gameNumber);
            y = usableY + (4 + ln) * lineHeight;
            if ((ln % 2) == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(usableX, y - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                g.setColor(Color.BLACK);
            }

            String strTN = "" + (game.getTableNumber() + 1);
            x = usableX + usableWidth * (GL_TN_BEG + GL_TN_LEN) / GL_NBCAR;
            drawRightAlignedString(g, strTN, x, y);

            Player wP = game.getWhitePlayer();
//            String strWP = augnentedPlayerString (wP, dpps);
            String strWP = wP.augmentedPlayerName(dpps);
            x = usableX + usableWidth * GL_WNF_BEG / GL_NBCAR;
            int result = game.getResult();
            if (result >= Game.RESULT_BYDEF) {
                result -= Game.RESULT_BYDEF;
            }
            if (result == Game.RESULT_BOTHLOSE || result == Game.RESULT_EQUAL || result == Game.RESULT_BLACKWINS) {
                g.setFont(new Font("Default", Font.PLAIN, fontSize));
            }
            g.drawString(strWP, x, y);
            g.setFont(font);

            Player bP = game.getBlackPlayer();
//            String strBP = augnentedPlayerString (bP, dpps);
            String strBP = bP.augmentedPlayerName(dpps);
            x = usableX + usableWidth * GL_BNF_BEG / GL_NBCAR;
            if (result == Game.RESULT_BOTHLOSE || result == Game.RESULT_EQUAL || result == Game.RESULT_WHITEWINS) {
                g.setFont(new Font("Default", Font.PLAIN, fontSize));
            }
            g.drawString(strBP, x, y);
            g.setFont(font);

            String strHd = "" + game.getHandicap();
            x = usableX + usableWidth * (GL_HD_BEG + GL_HD_LEN) / GL_NBCAR;
            drawRightAlignedString(g, strHd, x, y);

            String strResult = game.resultAsString(true);

            x = usableX + usableWidth * (GL_RES_BEG + GL_RES_LEN) / GL_NBCAR;
            drawRightAlignedString(g, strResult, x, y);
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

        if (ln == 0) {
            return NO_SUCH_PAGE;
        }
        return PAGE_EXISTS;
    }
    
    private int printAPageOfResultSheets(Graphics g, PageFormat pf, int pi) throws RemoteException {
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        Font bigFont = new Font("Default", Font.BOLD, fontSize * 4 / 3);
       
        int actRatioX1000 = usableWidth * 1000 /TournamentPrinting.RS_PAGE_VIRTUAL_WIDTH ;
        int x1 = TournamentPrinting.RS_COL1 * actRatioX1000 /1000;
        int x2 = TournamentPrinting.RS_COL2 * actRatioX1000 /1000;
        int x3 = TournamentPrinting.RS_COL3 * actRatioX1000 /1000;
        int x4 = TournamentPrinting.RS_COL4 * actRatioX1000 /1000;
        int actRatioY1000 = usableHeight * 1000 /TournamentPrinting.RS_PAGE_VIRTUAL_HEIGHT ;
        
        ArrayList<Game> alGamesToPrint = new ArrayList<Game>(tournament.gamesList(roundNumber));

        int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alGamesToPrint, gameComparator);
        
        DPParameterSet dpps = tps.getDPParameterSet();

        int irs;
        for(irs = 0; irs < TournamentPrinting.RS_RSBYPAGE; irs++){ 
            int abstractRSNumber = irs + pi * TournamentPrinting.RS_RSBYPAGE;
            int gameNumber = abstractRSNumber;
            if (gameNumber >= alGamesToPrint.size()) {
                break;
            }
            Game game = alGamesToPrint.get(gameNumber);

            int yBase = irs * TournamentPrinting.RS_RS_HEIGHT * actRatioY1000 /1000;
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            
            
            // Cut up Line
            g.drawLine(0, yBase, usableWidth, yBase);
            
            g.setFont(bigFont);

            // Title
            String strTN = "";
            try {
                strTN = tournament.getTournamentParameterSet().getGeneralParameterSet().getName();
            } catch (RemoteException ex) {
                Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
            }
            int y = yBase + TournamentPrinting.RS_TITLE1 * actRatioY1000 / 1000;
            int yT = y + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3;
            TournamentPrinting.drawCenterAlignedString(g, strTN , x1, x4, yT);
            
            y = yBase + TournamentPrinting.RS_TITLE2 * actRatioY1000 / 1000;
            yT = y + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3;
            TournamentPrinting.drawCenterAlignedString(g, "Result sheet", x1, x4, yT);
            
            // Table
            int y1 = yBase + TournamentPrinting.RS_TABLE * actRatioY1000 / 1000;
            int y2 = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000;
            g.drawLine(x1, y1, x1, y2);
            g.drawLine(x2, y1, x2, y2);
            g.drawLine(x3, y1, x3, y2);
            g.drawLine(x4, y1, x4, y2);
            g.drawLine(x1, y1, x4, y1);          
            g.drawLine(x1, y2, x4, y2);   
            yT = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3;
            int xT = x1 + TournamentPrinting.RS_LEFTMARGIN * actRatioX1000 /1000;
            g.drawString("Table : " + (game.getTableNumber() +1), xT, yT);
            TournamentPrinting.drawCenterAlignedString(g, "Hd = " + game.getHandicap(), x2, x3, yT);
            xT = x3 + TournamentPrinting.RS_LEFTMARGIN * actRatioX1000 /1000;
            g.drawString("Round : " + (game.getRoundNumber() +1), xT, yT);
            
            // Body
            y1 = yBase + TournamentPrinting.RS_COLOR * actRatioY1000 / 1000;
            y2 = yBase + (TournamentPrinting.RS_SIGN + TournamentPrinting.RS_LINE_HEIGHT) * actRatioY1000 / 1000;
            g.drawLine(x1, y1, x1, y2); //
            g.drawLine(x2, y1, x2, y2);
            g.drawLine(x3, y1, x3, y2);
            g.drawLine(x4, y1, x4, y2);
            
            g.drawLine(x1, y1, x4, y1); 
            yT = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3;           
            TournamentPrinting.drawCenterAlignedString(g, "White", x1, x2, yT);
            TournamentPrinting.drawCenterAlignedString(g, "Black", x3, x4, yT);
            TournamentPrinting.drawCenterAlignedString(g, "Result", x2, x3, yT);
            
            g.setFont(font);

            y1 = yBase + TournamentPrinting.RS_PLAYERNAME * actRatioY1000 / 1000;
            g.drawLine(x1, y1, x4, y1);
            yT = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3; 
      
            Player wP = game.getWhitePlayer();;
            String strWP = wP.augmentedPlayerName(dpps);
            
            // Adjust font
            FontMetrics fm = g.getFontMetrics(font);
            int wdt = fm.stringWidth(strWP);
            if (wdt > x2 - x1) {
                String fontName = font.getName();
                int fontSize = font.getSize();
                fontSize = fontSize * (x2 - x1) / wdt;
                int fontStyle = font.getStyle();
                Font tempFont = new Font(fontName, fontStyle, fontSize);
                g.setFont(tempFont);
            }           
            TournamentPrinting.drawCenterAlignedString(g, strWP, x1, x2, yT);
            g.setFont(font);
            
            Player bP = game.getBlackPlayer();;
            String strBP = bP.augmentedPlayerName(dpps);
            // Adjust font
            wdt = fm.stringWidth(strBP);
            if (wdt > x4 - x3) {
                String fontName = font.getName();
                int fontSize = font.getSize();
                fontSize = fontSize * (x2 - x1) / wdt;
                int fontStyle = font.getStyle();
                Font tempFont = new Font(fontName, fontStyle, fontSize);
                g.setFont(tempFont);
            }           
            TournamentPrinting.drawCenterAlignedString(g, strBP, x3, x4, yT);
            g.setFont(font);

            g.setFont(bigFont);
            TournamentPrinting.drawCenterAlignedString(g, "O  1 - 0", x2, x3, yT);
            g.setFont(font);
            
            y1 = yBase + TournamentPrinting.RS_ID * actRatioY1000 / 1000;
            g.drawLine(x1, y1, x2, y1);
            g.drawLine(x3, y1, x4, y1);
            yT = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3; 
            String strId = wP.getAnIdString();
            TournamentPrinting.drawCenterAlignedString(g, strId, x1, x2, yT);
            strId = bP.getAnIdString();
            TournamentPrinting.drawCenterAlignedString(g, strId, x3, x4, yT);
            
            g.setFont(bigFont);
            TournamentPrinting.drawCenterAlignedString(g, "O  0 - 1", x2, x3, yT);
            g.setFont(font);
            
            y1 = yBase + TournamentPrinting.RS_SIGN * actRatioY1000 / 1000;
            g.drawLine(x1, y1, x2, y1);
            g.drawLine(x3, y1, x4, y1);
            yT = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 / 1000 * 2 / 3; 
            xT = x1 + TournamentPrinting.RS_LEFTMARGIN * actRatioY1000 / 1000;
            g.drawString("Signature :", xT, yT);
            xT = x3 + TournamentPrinting.RS_LEFTMARGIN * actRatioY1000 / 1000;
            g.drawString("Signature :", xT, yT);

            g.setFont(bigFont);
            TournamentPrinting.drawCenterAlignedString(g, "O ½ - ½", x2, x3, yT);
            g.setFont(font);
            
            y2 = y1 + TournamentPrinting.RS_LINE_HEIGHT * actRatioY1000 /1000; 
            g.drawLine(x1, y2, x4, y2); 
            
            // Cut up Line
            int yBottom = yBase + TournamentPrinting.RS_RS_HEIGHT * actRatioY1000 /1000;
            g.drawLine(0, yBottom , usableWidth, yBottom);

        }
                

        if (irs == 0) {
            return NO_SUCH_PAGE;
        }
 
        return PAGE_EXISTS;
    }
    
   
    private int printAPageOfNotPlayingPlayersList(Graphics g, PageFormat pf, int pi) throws RemoteException {
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Players not playing in round " + (roundNumber + 1);
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);

        // Header Line
        printNotPlayingPlayersListHeaderLine(g, pf, pi);

        // Body lines
        int ln;
        for (ln = 0; ln < numberOfBodyLinesInAPage; ln++) {
            int abstractLineNumber = ln + pi * numberOfBodyLinesInAPage;
            int playerNumber = abstractLineNumber;
            if (playerNumber >= alPlayersToPrint.size()) {
                break;
            }
            Player player = alPlayersToPrint.get(playerNumber);
            y = usableY + (4 + ln) * lineHeight;
            if ((ln % 2) == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(usableX, y - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                g.setColor(Color.BLACK);
            }

            String strReason;
            if (!player.getRegisteringStatus().equals("FIN")) {
                strReason = "No Final Registration";
            } else if (player.hasSameKeyString(tournament.getByePlayer(roundNumber))) {
                strReason = "Bye player";
            } else if (!player.getParticipating(roundNumber)) {
                strReason = "Not participating";
            } else {
                strReason = "Not paired";
            }
            x = usableX + usableWidth * NPL_REASON_BEG / NPL_NBCAR;
            g.drawString(strReason, x, y);

            String strName = player.getName();
            String strFirstName = player.getFirstName();
            if (strName.length() > 20) {
                strName = strName.substring(0, 20);
            }
            String strNF = strName + " " + strFirstName;
            if (strNF.length() > 25) {
                strNF = strNF.substring(0, 25);
            }
            if (player.getRegisteringStatus().compareTo("PRE") == 0) {
                strNF += "(P)";
            }
            x = usableX + usableWidth * NPL_NF_BEG / NPL_NBCAR;
            g.drawString(strNF, x, y);

//            String strRk = Player.convertIntToKD(player.getRank());
            String strGr = player.getStrGrade();
//            x = usableX + usableWidth * (NPL_RANK_BEG + NPL_RANK_LEN) / NPL_NBCAR;
              x = usableX + usableWidth * (NPL_GRADE_BEG + NPL_GRADE_LEN) / NPL_NBCAR;
            drawRightAlignedString(g, strGr, x, y);
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

        if (ln == 0) {
            return NO_SUCH_PAGE;
        }
        return PAGE_EXISTS;
    }
    /** Concatenates name and firtName
     * Shortens if necessary
     * @param p
     * @return 
     */
        
    private int printAPageOfStandings(Graphics g, PageFormat pf, int pi) throws RemoteException {
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();

        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Standings after round" + " " + (roundNumber + 1);
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);
        y += lineHeight;

        // Knowing pi, what category are we working with ?
        int curCat = 0;
        int nbPlayersBeforeCurCat = 0;
        int nbPagesBeforeCurCat = 0;
        int nbPlayersOfCurCat = 0;
        try {
            nbPlayersOfCurCat = tournament.numberOfPlayersInCategory(curCat, alOrderedScoredPlayers);
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        // If the first criterion is not CAT, then, do as if everybody was in category 1
        if (this.printSubType != TournamentPublishing.SUBTYPE_ST_CAT) {
            nbPlayersOfCurCat = tournament.numberOfPlayers();
        }

        int nbPagesForCurCat = (nbPlayersOfCurCat + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
        while (pi >= nbPagesBeforeCurCat + nbPagesForCurCat) {
            curCat++;
            if (curCat >= gps.getNumberOfCategories()) {
                return NO_SUCH_PAGE;
            }
            nbPlayersBeforeCurCat += nbPlayersOfCurCat;
            nbPagesBeforeCurCat += nbPagesForCurCat;
            try {
                nbPlayersOfCurCat = tournament.numberOfPlayersInCategory(curCat, alOrderedScoredPlayers);
            } catch (RemoteException ex) {
                Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
            }
            nbPagesForCurCat = (nbPlayersOfCurCat + numberOfBodyLinesInAPage - 1) / numberOfBodyLinesInAPage;
        }

        if (criteria[0] == PlacementParameterSet.PLA_CRIT_CAT) {
            String strCat = "Category" + " " + (curCat + 1);
            char[] cCat = strCat.toCharArray();
            int catWidth = fm.charsWidth(cCat, 0, cCat.length);
            x = (usableWidth - catWidth) / 2;
            g.drawString(strCat, x, y);
        }
        printStandingsHeaderLine(g, pf, pi);

        int ln;
        // Body lines
        for (ln = 0; ln < numberOfBodyLinesInAPage; ln++) {
            y = usableY + (4 + ln) * lineHeight;
            int playerNumber = nbPlayersBeforeCurCat + ln + (pi - nbPagesBeforeCurCat) * numberOfBodyLinesInAPage;
            if (playerNumber >= this.alOrderedScoredPlayers.size()) {
                break;
            }
            ScoredPlayer sp = alOrderedScoredPlayers.get(playerNumber);

            if (this.printSubType == TournamentPublishing.SUBTYPE_ST_CAT && sp.category(gps) != curCat) {
                break;
            }

            if ((ln % 2) == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(usableX, y - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                g.setColor(Color.BLACK);
            }

            String strNum = "" + (playerNumber + 1);
            if (dpps.isDisplayNumCol()) {
                x = usableX + usableWidth * (this.stNumBeg + TournamentPrinting.ST_NUM_LEN) / numberOfCharactersInALine;
                TournamentPrinting.drawRightAlignedString(g, strNum, x, y);
            }

            String strPl = strPlace[playerNumber];
            if (dpps.isDisplayPlCol()) {
                x = usableX + usableWidth * (this.stPlBeg + TournamentPrinting.ST_PL_LEN) / numberOfCharactersInALine;
                TournamentPrinting.drawRightAlignedString(g, strPl, x, y);
            }

//            String strNF = playerString(sp);
            String strNF = sp.shortenedFullName();
            x = usableX + usableWidth * this.stNFBeg / numberOfCharactersInALine;
            g.drawString(strNF, x, y);

//            String strRk = Player.convertIntToKD(sp.getRank());
//            x = usableX + usableWidth * (this.stRkBeg + ST_RK_LEN) / numberOfCharactersInALine;
//            drawRightAlignedString(g, strRk, x, y);
            String strGr = sp.getStrGrade();
            x = usableX + usableWidth * (this.stGrBeg + ST_GR_LEN) / numberOfCharactersInALine;
            drawRightAlignedString(g, strGr, x, y);

            String strCo = sp.getCountry();
            if (dpps.isDisplayCoCol()) {
                x = usableX + usableWidth * this.stCoBeg  / numberOfCharactersInALine;
                g.drawString(strCo, x, y);
            }
            
            String strCl = sp.getClub();
            if (dpps.isDisplayClCol()) {
                x = usableX + usableWidth * this.stClBeg / numberOfCharactersInALine;
                g.drawString(strCl, x, y);
            }
            
            String strNbW = sp.formatScore(PlacementParameterSet.PLA_CRIT_NBW, roundNumber);
            x = usableX + usableWidth * (this.stNbWBeg + ST_NBW_LEN) / numberOfCharactersInALine;
            drawRightAlignedString(g, strNbW, x, y);

            int numberOfRoundsPrinted = roundNumber + 1;
            for (int r = 0; r < numberOfRoundsPrinted; r++) {
                x = usableX + usableWidth * (this.stRound0Beg + (r + 1) * (this.stRoundLen + ST_PADDING)) / numberOfCharactersInALine;
                TournamentPrinting.drawRightAlignedString(g, this.halfGamesStrings[r][playerNumber], x, y);
            }

            int numberOfCriteriaPrinted = criteria.length;
            for (int iC = 0; iC < numberOfCriteriaPrinted; iC++) {
                String strCritValue = sp.formatScore(criteria[iC], roundNumber);
                x = usableX + usableWidth * (this.stCrit0Beg + (iC + 1) * (ST_CRIT_LEN + ST_PADDING)) / numberOfCharactersInALine;
                TournamentPrinting.drawRightAlignedString(g, strCritValue, x, y);
            }
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

        if (ln == 0) {
            return NO_SUCH_PAGE;
        }

        return PAGE_EXISTS;
    }
    
    private int printAPageOfMatchesList(Graphics g, PageFormat pf, int pi) throws RemoteException {      
        DPParameterSet dpps = tps.getDPParameterSet();
        TeamTournamentParameterSet ttps = null;
        try {
            ttps = tournament.getTeamTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        TeamGeneralParameterSet tgps = ttps.getTeamGeneralParameterSet();
        
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Matches List";
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);

        String strRound = "Round";
        strRound += " " + (roundNumber + 1);
        char[] cRound = strRound.toCharArray();
        int roundWidth = fm.charsWidth(cRound, 0, cRound.length);
        x = (usableWidth - roundWidth) / 2;
        y += lineHeight;
        g.drawString(strRound, x, y);

        printMatchesListHeaderLine(g, pf, pi);

        ArrayList<Match> alM = tournament.matchesList(roundNumber);
        ArrayList<ComparableMatch> alComparableMatchesToPrint = ComparableMatch.buildComparableMatchesArray(alM, tournament, roundNumber);

        int matchesSortType = MatchComparator.BOARD0_TABLE_NUMBER_ORDER;
        MatchComparator matchComparator = new MatchComparator(matchesSortType);
        Collections.sort(alComparableMatchesToPrint, matchComparator);

        int iMatch;
        // Body lines
        for (iMatch = 0; iMatch < this.matchesPerPage; iMatch++) {
            int matchNumber = iMatch + pi * matchesPerPage;
            if (matchNumber >= alComparableMatchesToPrint.size()) {
                break;
            }
            int linesPerMatch;
            if (dpps.isDisplayIndGamesInMatches()) linesPerMatch = 1 + ttps.getTeamGeneralParameterSet().getTeamSize();
            else linesPerMatch = 1;
            y = usableY + (4 + linesPerMatch * iMatch) * lineHeight;

            ComparableMatch cm = alComparableMatchesToPrint.get(matchNumber);
            String strTN = "" + (cm.board0TableNumber + 1) + "---";
            x = usableX + usableWidth * (ML_TN_BEG + ML_TN_LEN) / ML_NBCAR;
            drawRightAlignedString(g, strTN, x, y);

            Match match = tournament.getMatch(roundNumber, cm.board0TableNumber);
            int wResult = match.getTeamScore(match.getWhiteTeam());
            int bResult = match.getTeamScore(match.getBlackTeam());

            String strWTN = cm.wst.getTeamName();
            if (strWTN.length() > ML_WTN_LEN) {
                strWTN = strWTN.substring(0, ML_WTN_LEN);
            }
            x = usableX + usableWidth * ML_WTN_BEG / ML_NBCAR;
            if (wResult <= 1) {
                g.setFont(new Font("Default", Font.PLAIN, fontSize));
            }
            g.drawString(strWTN, x, y);
            g.setFont(font);

            String strBTN = cm.bst.getTeamName();
            if (strBTN.length() > ML_BTN_LEN) {
                strBTN = strBTN.substring(0, ML_BTN_LEN);
            }
            x = usableX + usableWidth * ML_BTN_BEG / ML_NBCAR;
            if (bResult <= 1) {
                g.setFont(new Font("Default", Font.PLAIN, fontSize));
            }
            g.drawString(strBTN, x, y);
            g.setFont(font);

            String strWTeamNbW = Gotha.formatFractNumber(match.getWX2(match.getWhiteTeam()), 2);
            String strBTeamNbW = Gotha.formatFractNumber(match.getWX2(match.getBlackTeam()), 2);
            String strTeamResult = strWTeamNbW + "-" + strBTeamNbW;
            x = usableX + usableWidth * (ML_RES_BEG + ML_RES_LEN) / ML_NBCAR;
            drawRightAlignedString(g, strTeamResult, x, y);

            if (dpps.isDisplayIndGamesInMatches()){
                // games list for this match
                int gameFontSize = fontSize * 80 / 100;
                Font gameFont = new Font("Default", Font.BOLD, gameFontSize);
                g.setFont(gameFont);
                
                Team wTeam = match.getWhiteTeam();
                Team bTeam = match.getBlackTeam();
                int nbBoards = tgps.getTeamSize();
                for (int ib = 0; ib < nbBoards; ib++){
                    int yG = y + lineHeight * (ib + 1);
                    Player p1 = wTeam.getTeamMember(roundNumber, ib);
                    Player p2 = bTeam.getTeamMember(roundNumber, ib);
                    if ((ib % 2) == 0) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(usableX, yG - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                        g.setColor(Color.BLACK);
                    }
                    Game game = tournament.getGame(roundNumber, p1);
                    strTN = "" + (game.getTableNumber() + 1);
//                    x = usableX + usableWidth * ML_WTN_BEG / ML_NBCAR;                    
                    x = usableX + usableWidth * (ML_TN_BEG + ML_TN_LEN) / ML_NBCAR;                    
                    drawRightAlignedString(g, strTN, x, yG);
                    
                    String strP1Color = "";
                    String strP2Color = "";
                    if (game.isKnownColor()){
                        if (game.getWhitePlayer().hasSameKeyString(p1)){
                            strP1Color = "(w)";
                            strP2Color = "(b)";
                        }
                        else{
                            strP2Color = "(w)";
                            strP1Color = "(b)";
                        }
                    }
                    
                    String strNF = p1.augmentedPlayerName(dpps);                                
                    if (!game.isWinner(p1)) g.setFont(new Font("Default", Font.PLAIN, gameFontSize));
                    x = usableX + usableWidth * TournamentPrinting.ML_WTN_BEG / TournamentPrinting.ML_NBCAR;
                    g.drawString(strNF + strP1Color, x, yG);
                    g.setFont(gameFont);

                    strNF = p2.augmentedPlayerName(dpps);                  
                    if (!game.isWinner(p2)) g.setFont(new Font("Default", Font.PLAIN, gameFontSize));
                    x = usableX + usableWidth * TournamentPrinting.ML_BTN_BEG / TournamentPrinting.ML_NBCAR;
                    g.drawString(strNF + strP2Color, x, yG);
                    g.setFont(gameFont);
            
                    String strHd = "" + game.getHandicap();
                    x = usableX + usableWidth * (ML_HD_BEG + ML_HD_LEN) / ML_NBCAR;
                    drawRightAlignedString(g, strHd, x, yG);
                    
                    // Result
                    String strResult = game.resultAsString(game.getWhitePlayer().hasSameKeyString(p1));
                    x = usableX + usableWidth * (ML_RES_BEG + ML_RES_LEN) / ML_NBCAR;
                    drawRightAlignedString(g, strResult, x, yG);

               }
               // Back to normal font
               g.setFont(font);
            }
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

        if (iMatch == 0) {
            return NO_SUCH_PAGE;
        }
        return PAGE_EXISTS;
    }


    private int printAPageOfTeamsStandings(Graphics g, PageFormat pf, int pi) throws RemoteException {
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Teams Standings after round" + " " + (roundNumber + 1);
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);
        y += lineHeight;

        printTeamsStandingsHeaderLine(g, pf, pi);

        int ln;
        // Body lines
        for (ln = 0; ln < numberOfBodyLinesInAPage; ln++) {
            y = usableY + (4 + ln) * lineHeight;
            int abstractLineNumber = ln + pi * numberOfBodyLinesInAPage;
            int teamNumber = abstractLineNumber;
            if (teamNumber >= alOrderedScoredTeams.size()) {
                break;
            }

            if ((ln % 2) == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(usableX, y - lineHeight + 4, usableWidth, lineHeight);  // + 4 to keep leading part unfilled
                g.setColor(Color.BLACK);
            }

            String strNum = "" + (teamNumber + 1);
            x = usableX + usableWidth * (TST_NUM_BEG + TST_NUM_LEN) / numberOfCharactersInALine;
            drawRightAlignedString(g, strNum, x, y);

            ScoredTeam st = alOrderedScoredTeams.get(teamNumber);

            String strPL = this.scoredTeamsSet.getTeamPositionString(st);
            x = usableX + usableWidth * (TST_PL_BEG + TST_PL_LEN) / numberOfCharactersInALine;
            drawRightAlignedString(g, strPL, x, y);

            String strTN = st.getTeamName();

            if (strTN.length() > TST_TN_LEN) {
                strTN = strTN.substring(0, TST_TN_LEN);
            }
            x = usableX + usableWidth * (TST_TN_BEG) / numberOfCharactersInALine;
            g.drawString(strTN, x, y);

            int numberOfRoundsPrinted = roundNumber + 1;
            int rBeg = TST_ROUND0_BEG;
            for (int r = 0; r < numberOfRoundsPrinted; r++) {
                String strMatch = scoredTeamsSet.getHalfMatchString(st, r);
                int xR = usableX + usableWidth * (rBeg + (r + 1) * TST_ROUND_LEN) / numberOfCharactersInALine;
                drawRightAlignedString(g, strMatch, xR, y);
            }

            int numberOfCriteriaPrinted = criteria.length;
            int cBeg = rBeg + numberOfRoundsPrinted * TST_ROUND_LEN;
            for (int ic = 0; ic < numberOfCriteriaPrinted; ic++) {
                int crit = criteria[ic];
                int coef = TeamPlacementParameterSet.criterionCoef(crit);
                String strCritValue = Gotha.formatFractNumber(st.getCritValue(ic), coef);
                int xC = usableX + usableWidth * (cBeg + (ic + 1) * TST_CRIT_LEN) / numberOfCharactersInALine;
                drawRightAlignedString(g, strCritValue, xC, y);
            }
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

        if (ln == 0) {
            return NO_SUCH_PAGE;
        }

        return PAGE_EXISTS;
    }
    private int printAPageOfTournamentParameters(Graphics g, PageFormat pf, int pi) throws RemoteException {
        // Page 1 (pi = 0) contains General, Handicap and Placement parameters
        // Page 2 (pi = 1) contains Pairing parameters
        if (pi > 1) {
            return NO_SUCH_PAGE;
        }
        Font font = new Font("Default", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        String strTitle = "Tournament Parameters";
        char[] cTitle = strTitle.toCharArray();
        int titleWidth = fm.charsWidth(cTitle, 0, cTitle.length);
        int x = (usableWidth - titleWidth) / 2;
        int y = (usableY + lineHeight);
        g.drawString(strTitle, x, y);

        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        PairingParameterSet paiPS = tps.getPairingParameterSet();

        if (pi == 0) {
            int ln;

            x = usableX;
            ln = 0;
            y = usableY + (4 + ln) * lineHeight;
            Font title2Font = new Font("Default", Font.BOLD, fontSize + 4);
            g.setFont(title2Font);
            g.drawString("General Parameters", x, y);
            ln++;
            g.setFont(font);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString(gps.getName(), x, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            int tournamentType = tournament.tournamentType();
            String strType = "Undefined system";
            if (tournamentType == TournamentParameterSet.TYPE_MCMAHON) {
                strType = "McMahon system";
            }
            if (tournamentType == TournamentParameterSet.TYPE_SWISS) {
                strType = "Swiss system";
            }
            if (tournamentType == TournamentParameterSet.TYPE_SWISSCAT) {
                strType = "SwissCat system";
            }
            g.drawString(strType, x, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString(gps.getNumberOfRounds() + " " + "rounds", x, y);
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString("Special Results", x, y);
            int xNBW = usableX + usableWidth * TP_TAB3 / TP_NBCAR;
            drawRightAlignedString(g, "NBW", xNBW, y);
            int xMMS = usableX + usableWidth * TP_TAB4 / TP_NBCAR;
            TournamentPrinting.drawRightAlignedString(g, "MMS", xMMS, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString("Absent", usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            String strNBW = "";
            switch (gps.getGenNBW2ValueAbsent()) {
                case 0:
                    strNBW = "0";
                    break;
                case 1:
                    strNBW = "½";
                    break;
                case 2:
                    strNBW = "1";
                    break;
            }
            String strMMS = "";
            switch (gps.getGenMMS2ValueAbsent()) {
                case 0:
                    strMMS = "0";
                    break;
                case 1:
                    strMMS = "½";
                    break;
                case 2:
                    strMMS = "1";
                    break;
            }
            drawRightAlignedString(g, strNBW, xNBW, y);
            drawRightAlignedString(g, strMMS, xMMS, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString("Bye", usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            strNBW = "";
            switch (gps.getGenNBW2ValueBye()) {
                case 0:
                    strNBW = "0";
                    break;
                case 1:
                    strNBW = "½";
                    break;
                case 2:
                    strNBW = "1";
                    break;
            }
            strMMS = "";
            switch (gps.getGenMMS2ValueBye()) {
                case 0:
                    strMMS = "0";
                    break;
                case 1:
                    strMMS = "½";
                    break;
                case 2:
                    strMMS = "1";
                    break;
            }
            drawRightAlignedString(g, strNBW, xNBW, y);
            drawRightAlignedString(g, strMMS, xMMS, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strRoundDown = "Round down NBW and MMS scores : " + gps.isGenRoundDownNBWMMS();
            g.drawString(strRoundDown, x, y);

            if (gps.getNumberOfCategories() > 1) {
                ln++;
                for (int c = 0; c < gps.getNumberOfCategories(); c++) {
                    ln++;
                    y = usableY + (4 + ln) * lineHeight;
                    int lowL = Gotha.MIN_RANK;
                    int highL = Gotha.MAX_RANK;
                    if (c <= gps.getNumberOfCategories() - 2) {
                        lowL = gps.getLowerCategoryLimits()[c];
                    }
                    if (c >= 1) {
                        highL = gps.getLowerCategoryLimits()[c - 1] - 1;
                    }
                    String strLow = Player.convertIntToKD(lowL);
                    String strHigh = Player.convertIntToKD(highL);
                    g.drawString("Category"
                            + (c + 1) + " : " + strHigh + " - " + strLow, x, y);
                }
            }
            ln++;

            x = usableX;
            ln++;
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            title2Font = new Font("Default", Font.BOLD, fontSize + 4);
            g.setFont(title2Font);
            g.drawString("Handicap Parameters", x, y);
            ln++;
            g.setFont(font);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strRankThreshold = Player.convertIntToKD(hps.getHdNoHdRankThreshold());
            g.drawString("No handicap for players above " + strRankThreshold, x, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strHdCorr = "";
            if (hps.getHdCorrection() == 0) {
                strHdCorr = "Handicap not decreased";
            } else if (hps.getHdCorrection() > 0) {
                strHdCorr = "Handicap decreased by" + " " + hps.getHdCorrection();
            } else if (hps.getHdCorrection() < 0) {
                strHdCorr = "Handicap increased by" + " " + Math.abs(hps.getHdCorrection());
            }
            g.drawString(strHdCorr, x, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strHdCeil = "Handicap ceiling" + " : " + hps.getHdCeiling();
            g.drawString(strHdCeil, x, y);

            x = usableX;
            ln++;
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            title2Font = new Font("Default", Font.BOLD, fontSize + 4);
            g.setFont(title2Font);
            g.drawString("Placement Parameters", x, y);
            ln++;
            g.setFont(font);

            int[] plaC = pps.getPlaCriteria();
            // Get rid of useless criteria
            int nbCrit = plaC.length;
            for (int c = nbCrit - 1; c > 0; c--) {
                if (plaC[c] == PlacementParameterSet.PLA_CRIT_NUL) {
                    nbCrit--;
                } else {
                    break;
                }
            }
            int[] plaCrit = new int[nbCrit];
            System.arraycopy(plaC, 0, plaCrit, 0, nbCrit);

            for (int crit = 0; crit < plaCrit.length; crit++) {
                ln++;
                y = usableY + (4 + ln) * lineHeight;
                String strCrit = PlacementParameterSet.criterionLongName(plaCrit[crit]);
                g.drawString("Criterion" + (crit + 1) + " : " + strCrit, x, y);
            }
            // Criteria Descriptions
            for (int crit = 0; crit < plaCrit.length; crit++) {
                ln++;
                y = usableY + (4 + ln) * lineHeight;
                String strCrit = PlacementParameterSet.criterionLongName(plaCrit[crit]);
                String strDescr = PlacementParameterSet.criterionDescription(plaCrit[crit]);
                Font italFont = new Font("Default", Font.ITALIC, fontSize);
                g.setFont(italFont);
                g.drawString(strCrit + " = " + strDescr, x, y);
                g.setFont(font);
            }

            if (tournament.teamsList().size() > 0) {
                x = usableX;
                ln++;
                ln++;
                y = usableY + (4 + ln) * lineHeight;
                title2Font = new Font("Default", Font.BOLD, fontSize + 4);
                g.setFont(title2Font);
                g.drawString("Team Placement Parameters", x, y);
                ln++;
                g.setFont(font);

                TeamPlacementParameterSet tpps = tournament.getTeamTournamentParameterSet().getTeamPlacementParameterSet();
                plaC = tpps.getPlaCriteria();
                // Get rid of useless criteria
                nbCrit = plaC.length;
                for (int c = nbCrit - 1; c > 0; c--) {
                    if (plaC[c] == TeamPlacementParameterSet.TPL_CRIT_NUL) {
                        nbCrit--;
                    } else {
                        break;
                    }
                }
                plaCrit = new int[nbCrit];
                System.arraycopy(plaC, 0, plaCrit, 0, nbCrit);

                for (int crit = 0; crit < plaCrit.length; crit++) {
                    ln++;
                    y = usableY + (4 + ln) * lineHeight;
                    String strCrit = TeamPlacementParameterSet.criterionLongName(plaCrit[crit]);
                    g.drawString("Criterion" + (crit + 1) + " : " + strCrit, x, y);
                }
                // Criteria Descriptions
                for (int crit = 0; crit < plaCrit.length; crit++) {
                    ln++;
                    y = usableY + (4 + ln) * lineHeight;
                    String strCrit = TeamPlacementParameterSet.criterionLongName(plaCrit[crit]);
                    String strDescr = TeamPlacementParameterSet.criterionDescription(plaCrit[crit]);
                    Font italFont = new Font("Default", Font.ITALIC, fontSize);
                    g.setFont(italFont);
                    g.drawString(strCrit + " = " + strDescr, x, y);
                    g.setFont(font);
                }
            }

        }
        int ln;
        if (pi == 1) {
            x = usableX;
            ln = 0;
            y = usableY + (4 + ln) * lineHeight;
            Font title2Font = new Font("Default", Font.BOLD, fontSize + 4);
            g.setFont(title2Font);
            g.drawString("Pairing Parameters", x, y);
            ln++;
            g.setFont(font);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strSS = "Seeding system";
            int lastRSS1 = paiPS.getPaiMaLastRoundForSeedSystem1();
            if (lastRSS1 < gps.getNumberOfRounds() - 1) {
                strSS += " " + "until Round " + (lastRSS1 + 1);
            }
            strSS += " : ";
            if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM) {
                strSS += "Split and Random";
            } else if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD) {
                strSS += "Split and Fold";
            } else if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP) {
                strSS += "Split and Slip";
            }
            if (paiPS.getPaiMaAdditionalPlacementCritSystem1() != PlacementParameterSet.PLA_CRIT_NUL) {
                strSS += " " + "with additional criterion on" + " "
                        + PlacementParameterSet.criterionLongName(paiPS.getPaiMaAdditionalPlacementCritSystem1());
            }
            g.drawString(strSS, x, y);

            if (lastRSS1 < gps.getNumberOfRounds() - 1) {
                ln++;
                y = usableY + (4 + ln) * lineHeight;
                String strSS2 = "Seeding system starting from Round " + (lastRSS1 + 2) + " : ";
                if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM) {
                    strSS2 += "Split and Random";
                } else if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD) {
                    strSS2 += "Split and Fold";
                } else if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP) {
                    strSS2 += "Split and Slip";
                }
                if (paiPS.getPaiMaAdditionalPlacementCritSystem2() != PlacementParameterSet.PLA_CRIT_NUL) {
                    strSS2 += "with_additional_criterion_on"
                            + PlacementParameterSet.criterionLongName(paiPS.getPaiMaAdditionalPlacementCritSystem2());
                }
                g.drawString(strSS2, x, y);
            }

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strDG = "When pairing players from different groups is necessary,";
            g.drawString(strDG, x, y);
            int x1 = usableX + usableWidth * TP_TAB1 / TP_NBCAR;
            
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strCompensate = "Previous Draw up/down are compensated by Draw down/up";
            if (!paiPS.isPaiMaCompensateDUDD()) strCompensate = "No Draw up/down compensation system is used";
            g.drawString(strCompensate, x1, y);
            
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strUG = "The player in the upper group is chosen";
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_TOP) {
                strUG += " " + "in the top of the group";
            }
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_MID) {
                strUG += " " + "in the middle of the group";
            }
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_BOT) {
                strUG += " " + "in the bottom of the group";
            }
            g.drawString(strUG, x1, y);
            
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strLG = "The player in the lower group is chosen";
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_TOP) {
                strLG += " " + "in the top of the group";
            }
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_MID) {
                strLG += " " + "in the middle of the group";
            }
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_BOT) {
                strLG += " " + "in the bottom of the group";
            }
            g.drawString(strLG, x1, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strRan;
            if (paiPS.getPaiBaRandom() == 0) {
                strRan = "No random";
            } else if (paiPS.isPaiBaDeterministic()) {
                strRan = "Some deterministic random";
            } else {
                strRan = "Some non-deterministic random";
            }
            g.drawString(strRan, x, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strBalWB = "";
            if (paiPS.getPaiBaBalanceWB() != 0) {
                strBalWB = "Balance White and Black";
            }
            g.drawString(strBalWB, x, y);

            ln++;
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString("Secondary criteria", x, y);

            ln++;
            y = usableY + (4 + ln) * lineHeight;
            String strRankThreshold = Player.convertIntToKD(paiPS.getPaiSeRankThreshold());
            g.drawString("Secondary criteria not applied for players with a rank equal or stronger than"
                    + strRankThreshold, usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            if (paiPS.isPaiSeNbWinsThresholdActive()) {
                ln++;
                y = usableY + (4 + ln) * lineHeight;
                g.drawString("Secondary criteria not applied for players with at least nbRounds/2 wins",
                        usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            }
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString("Intra-country pairing is avoided. A group gap of" + " " + paiPS.getPaiSePreferMMSDiffRatherThanSameCountry() + " "
                    + "is preferred", usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            ln++;
            y = usableY + (4 + ln) * lineHeight;
            g.drawString("Intra-club pairing is avoided. A group gap of" + " " + paiPS.getPaiSePreferMMSDiffRatherThanSameClub() + " "
                    + "is preferred", usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            if (paiPS.getPaiSeMinimizeHandicap() != 0) {
                ln++;
                y = usableY + (4 + ln) * lineHeight;
                g.drawString("Low handicap games are preferred", usableX + usableWidth * TP_TAB1 / TP_NBCAR, y);
            }
        }

        // Print Page Footer
        printPageFooter(g, pf, pi);

//        if (ln == 0) return NO_SUCH_PAGE;
        return PAGE_EXISTS;
    }

    private void printPlayersListHeaderLine(Graphics g, PageFormat pf, int pi) {
        int y = usableY + 3 * lineHeight;
        int x = usableX + usableWidth * PL_PINLIC_BEG / PL_NBCAR;

        g.drawString("Pin/Lic/Id", x, y);
        x = usableX + usableWidth * PL_NF_BEG / PL_NBCAR;
        g.drawString("Name", x, y);
        x = usableX + usableWidth * (PL_RANK_BEG + PL_RANK_LEN) / PL_NBCAR;
//        drawRightAlignedString(g, "Rk", x, y);
        drawRightAlignedString(g, "Gr", x, y);
        x = usableX + usableWidth * (TournamentPrinting.PL_RT_BEG + PL_RT_LEN) / PL_NBCAR;
        drawRightAlignedString(g, "Rt", x, y);
        x = usableX + usableWidth * (PL_MM_BEG + PL_MM_LEN) / PL_NBCAR;
        drawRightAlignedString(g, "MM", x, y);

        x = usableX + usableWidth * PL_COUNTRY_BEG / PL_NBCAR;
        g.drawString("Co", x, y);
        x = usableX + usableWidth * PL_CLUB_BEG / PL_NBCAR;
        g.drawString("Club", x, y);
        x = usableX + usableWidth * PL_PART_BEG / PL_NBCAR;
        g.drawString("Participation", x, y);
    }

    private void printNotPlayingPlayersListHeaderLine(Graphics g, PageFormat pf, int pi) {
        int y = usableY + 3 * lineHeight;
        int x = usableX + usableWidth * NPL_REASON_BEG / NPL_NBCAR;

        g.drawString("Reason", x, y);
        x = usableX + usableWidth * NPL_NF_BEG / NPL_NBCAR;
        g.drawString("Last name" + " " + "First name", x, y);
        x = usableX + usableWidth * (PL_RANK_BEG + PL_RANK_LEN) / PL_NBCAR;
//        drawRightAlignedString(g, "Rk", x, y);
        drawRightAlignedString(g, "Gr", x, y);
    }

    private void printTeamsListHeaderLine(Graphics g, PageFormat pf, int pi) {
        int y = usableY + 3 * lineHeight;
        int x = usableX + usableWidth * TL_TEAMNAME_BEG / TL_NBCAR;
        g.drawString("Team name", x, y);

        x = usableX + usableWidth * (TL_BOARD_BEG + TL_BOARD_LEN) / TL_NBCAR;
        drawRightAlignedString(g, "Bd", x, y);

        x = usableX + usableWidth * TL_NF_BEG / TL_NBCAR;
        g.drawString("Player name", x, y);

        x = usableX + usableWidth * (TL_RATING_BEG + TL_RATING_LEN) / TL_NBCAR;
        drawRightAlignedString(g, "Rating", x, y);

        x = usableX + usableWidth * TL_COUNTRY_BEG / TL_NBCAR;
        g.drawString("Co", x, y);

        x = usableX + usableWidth * TL_CLUB_BEG / TL_NBCAR;
        g.drawString("Club", x, y);

        x = usableX + usableWidth * TL_MEMBER_BEG / TL_NBCAR;
        g.drawString("Rounds", x, y);
    }

    private void printGamesListHeaderLine(Graphics g, PageFormat pf, int pi) {
        int y = usableY + 3 * lineHeight;
        int x = usableX + usableWidth * (GL_TN_BEG + GL_TN_LEN) / GL_NBCAR;
        drawRightAlignedString(g, "Tble", x, y);
        x = usableX + usableWidth * GL_WNF_BEG / GL_NBCAR;
        g.drawString("White", x, y);
        x = usableX + usableWidth * GL_BNF_BEG / GL_NBCAR;
        g.drawString("Black", x, y);

        x = usableX + usableWidth * (GL_HD_BEG + GL_HD_LEN) / GL_NBCAR;
        drawRightAlignedString(g, "Hd", x, y);
        x = usableX + usableWidth * (GL_RES_BEG + GL_RES_LEN) / GL_NBCAR;
        drawRightAlignedString(g, "Res", x, y);
    }

    private void printMatchesListHeaderLine(Graphics g, PageFormat pf, int pi) {
        DPParameterSet dpps = tps.getDPParameterSet();
        int y = usableY + 3 * lineHeight;
        int x = usableX + usableWidth * (ML_TN_BEG + ML_TN_LEN) / ML_NBCAR;
        drawRightAlignedString(g, "Tables", x, y);

        x = usableX + usableWidth * (ML_HD_BEG + ML_HD_LEN)/ ML_NBCAR;
        if (dpps.isDisplayIndGamesInMatches()) drawRightAlignedString(g, "Hd", x, y);
        x = usableX + usableWidth * (ML_RES_BEG + ML_RES_LEN) / ML_NBCAR;
        drawRightAlignedString(g, "Res", x, y);
    }

    private void printStandingsHeaderLine(Graphics g, PageFormat pf, int pi) {
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        DPParameterSet dpps = tps.getDPParameterSet();

        int y = usableY + 3 * lineHeight;
        int x = usableX;
        if (dpps.isDisplayNumCol()) {
            x = usableX + usableWidth * (this.stNumBeg + TournamentPrinting.ST_NUM_LEN) / numberOfCharactersInALine;
            TournamentPrinting.drawRightAlignedString(g, "Num", x, y);
        }
        if (dpps.isDisplayPlCol()) {
            x = usableX + usableWidth * (this.stPlBeg + TournamentPrinting.ST_PL_LEN) / numberOfCharactersInALine;
            TournamentPrinting.drawRightAlignedString(g, "Pl", x, y);
        }
        x = usableX + usableWidth * (this.stNFBeg) / numberOfCharactersInALine;
        g.drawString("Name", x, y);
//        x = usableX + usableWidth * (this.stRkBeg + TournamentPrinting.ST_RK_LEN) / numberOfCharactersInALine;
//        TournamentPrinting.drawRightAlignedString(g, "Rk", x, y);
        x = usableX + usableWidth * (this.stGrBeg + TournamentPrinting.ST_GR_LEN) / numberOfCharactersInALine;
        TournamentPrinting.drawRightAlignedString(g, "Gr", x, y);
        if (dpps.isDisplayCoCol()){
            x = usableX + usableWidth * (this.stCoBeg) / numberOfCharactersInALine;
            g.drawString("Co", x, y);
        }
        if (dpps.isDisplayClCol()){
            x = usableX + usableWidth * (this.stClBeg) / numberOfCharactersInALine;
            g.drawString("Cl", x, y);
        }
        x = usableX + usableWidth * (this.stNbWBeg + TournamentPrinting.ST_NBW_LEN) / this.numberOfCharactersInALine;
        TournamentPrinting.drawRightAlignedString(g, "NbW", x, y);

        int numberOfRoundsPrinted = roundNumber + 1;
        for (int r = 0; r < numberOfRoundsPrinted; r++) {
            x = usableX + usableWidth * (this.stRound0Beg + (r + 1) * (this.stRoundLen + ST_PADDING)) / numberOfCharactersInALine;
            String strRound = "R" + (r + 1);
            TournamentPrinting.drawRightAlignedString(g, strRound, x, y);
        }
        int numberOfCriteriaPrinted = criteria.length;
        for (int iC = 0; iC < numberOfCriteriaPrinted; iC++) {
            x = usableX + usableWidth * (this.stCrit0Beg + (iC + 1) * (ST_CRIT_LEN + ST_PADDING)) / numberOfCharactersInALine;
            String strCrit = PlacementParameterSet.criterionShortName(criteria[iC]);
            TournamentPrinting.drawRightAlignedString(g, strCrit, x, y);
        }
    }

    private void printTeamsStandingsHeaderLine(Graphics g, PageFormat pf, int pi) {
        int y = usableY + 3 * lineHeight;
        int x = usableX + usableWidth * (TST_NUM_BEG + TST_NUM_LEN) / numberOfCharactersInALine;
        TournamentPrinting.drawRightAlignedString(g, "Num", x, y);
        x = usableX + usableWidth * (TST_PL_BEG + TST_PL_LEN) / numberOfCharactersInALine;
        TournamentPrinting.drawRightAlignedString(g, "Pl", x, y);
        x = usableX + usableWidth * TST_TN_BEG / this.numberOfCharactersInALine;
        g.drawString("Team name", x, y);

        int numberOfRoundsPrinted = roundNumber + 1;
        int rBeg = TST_ROUND0_BEG;
        for (int r = 0; r < numberOfRoundsPrinted; r++) {
            int xR = usableX + usableWidth * (rBeg + (r + 1) * TST_ROUND_LEN) / numberOfCharactersInALine;
            String strRound = "R" + (r + 1);
            TournamentPrinting.drawRightAlignedString(g, strRound, xR, y);
        }
        int numberOfCriteriaPrinted = criteria.length;
        int cBeg = rBeg + numberOfRoundsPrinted * TST_ROUND_LEN;
        for (int iC = 0; iC < numberOfCriteriaPrinted; iC++) {
            int xC = usableX + usableWidth * (cBeg + (iC + 1) * TST_CRIT_LEN) / numberOfCharactersInALine;
            String strCrit = TeamPlacementParameterSet.criterionShortName(criteria[iC]);
            TournamentPrinting.drawRightAlignedString(g, strCrit, xC, y);
        }
    }

    private void printPageFooter(Graphics g, PageFormat pf, int pi) {
        int footerFontSize = fontSize;
        if (footerFontSize > 12) {
            footerFontSize = 12;
        }
        Font f = new Font("Default", Font.BOLD, footerFontSize);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        String strName = "";
        try {
            strName = tournament.getTournamentParameterSet().getGeneralParameterSet().getName();
        } catch (RemoteException ex) {
            Logger.getLogger(TournamentPrinting.class.getName()).log(Level.SEVERE, null, ex);
        }
        String strLeft = Gotha.getGothaVersionnedName() + " : " + strName + "    ";

        // Center part of footer
        String strCenter = "Page" + " " + (pi + 1) + "/" + numberOfPages;

        char[] tcLeft = strLeft.toCharArray();
        char[] tcCenter = strCenter.toCharArray();
        int wLeft = fm.charsWidth(tcLeft, 0, tcLeft.length);
        int wCenter = fm.charsWidth(tcCenter, 0, tcCenter.length);
        while (wLeft + wCenter / 2 > usableWidth / 2) {
            if (strLeft.length() <= 2) {
                break;
            }
            strLeft = strLeft.substring(0, strLeft.length() - 2);
            tcLeft = strLeft.toCharArray();
            wLeft = fm.charsWidth(tcLeft, 0, tcLeft.length);
        }
        strLeft = strLeft.substring(0, strLeft.length() - 2);
        g.drawString(strLeft, usableX, usableY + usableHeight - fm.getDescent());
        int x = usableX + (usableWidth - wCenter) / 2;
        g.drawString(strCenter, x, usableY + usableHeight - fm.getDescent());

        // Right part of footer
        java.util.Date dh = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm  ");
        String strDH = sdf.format(dh);
        String strRight = strDH;
        x = usableX + usableWidth;
        drawRightAlignedString(g, strRight, x, usableY + usableHeight - fm.getDescent());
    }

    private int printADefaultPage(Graphics g, PageFormat pf, int pi) {
        if (pi > 0) {
            return NO_SUCH_PAGE;
        }
        g.drawString("This page is printed for test only", usableX, usableY + 20);

        g.drawString("uX = " + usableX, 10, 100);
        g.drawString("uY = " + usableY, 10, 120);
        g.drawString("uW = " + usableWidth, 10, 140);
        g.drawString("uH = " + usableHeight, 10, 160);

        g.drawRect(usableX, usableY, usableWidth - 1, usableHeight - 1);

        g.setFont(new Font("Default", Font.PLAIN, 40));
        g.drawString("Font size = 40", usableX, usableY + 260);
        g.setFont(new Font("Default", Font.PLAIN, 18));
        g.drawString("Font size = 18", usableX, usableY + 360);
        g.setFont(new Font("Default", Font.PLAIN, 16));
        g.drawString("Font size = 12", usableX, usableY + 420);
        g.setFont(new Font("Default", Font.PLAIN, 7));
        g.drawString("Font size =  7", usableX, usableY + 500);
        g.setFont(new Font("Default", Font.PLAIN, 6));
        g.drawString("Font size =  5", usableX, usableY + 540);
        g.setFont(new Font("Default", Font.PLAIN, 4));
        g.drawString("Font size =  3", usableX, usableY + 580);
        g.setFont(new Font("Default", Font.PLAIN, 2));

        Font f = new Font("Default", Font.PLAIN, 100);
        FontMetrics fm = g.getFontMetrics(f);
        g.setFont(new Font("Default", Font.PLAIN, 12));
        g.drawString("Font size = 100", usableX + 120, usableY + 380);
        g.drawString("Leading = " + fm.getLeading(), usableX + 120, usableY + 400);
        g.drawString("Ascent = " + fm.getAscent(), usableX + 120, usableY + 420);
        g.drawString("MaxAscent = " + fm.getMaxAscent(), usableX + 120, usableY + 440);
        g.drawString("Descent = " + fm.getDescent(), usableX + 120, usableY + 460);
        g.drawString("MaxDescent = " + fm.getMaxDescent(), usableX + 120, usableY + 480);
        g.drawString("Height = " + fm.getHeight(), usableX + 120, usableY + 500);
        char[] tci = {'i', 'i', 'i', 'i', 'i', 'i', 'i', 'i', 'i', 'i'};
        g.drawString("charsWidth(\"i\") = " + fm.charsWidth(tci, 0, 1), usableX + 120, usableY + 520);
        g.drawString("charsWidth(\"iiiiiiiiii\") = " + fm.charsWidth(tci, 0, 10), usableX + 120, usableY + 540);
        char[] tcw = {'Æ'};
        g.drawString("charsWidth(\"Æ\") = " + fm.charsWidth(tcw, 0, 1), usableX + 120, usableY + 560);

        g.drawLine(100, 100, 500, 100);
        g.drawLine(100, 100 + fm.getLeading(), 500, 100 + fm.getLeading());
        g.drawLine(100, 100 + fm.getLeading() + fm.getAscent(), 500, 100 + fm.getLeading() + fm.getAscent());
        g.drawLine(100, 100 + fm.getLeading() + fm.getAscent() + fm.getDescent(), 500, 100 + fm.getLeading() + fm.getAscent() + fm.getDescent());
        g.setFont(f);
        g.drawString("aç_ÀÆÑÔ", 50, 100 + fm.getLeading() + fm.getAscent());

        // A typical 100 points font gives a total height of 127 points :
        // leading = 4
        // ascent = 101
        // descent = 22
        // width = 22 for "i", 95 for "W", 100 for "Æ"

        return PAGE_EXISTS;
    }

    private static void drawRightAlignedString(Graphics g, String str, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        char[] tc = str.toCharArray();
        int w = fm.charsWidth(tc, 0, str.length());
        int xLeft = x - w;
        g.drawString(str, xLeft, y);

    }
    
    private static void drawCenterAlignedString(Graphics g, String str, int xL, int xR, int y) {
        FontMetrics fm = g.getFontMetrics();
        char[] tc = str.toCharArray();
        int w = fm.charsWidth(tc, 0, str.length());
        int xLeft = xL + (xR - xL - w) / 2;
        g.drawString(str, xLeft, y);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
}