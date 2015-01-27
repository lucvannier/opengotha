/*
 * JFrGamesPair.java
 */
package info.vannier.gotha;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author  Luc Vannier
 */
public class JFrGamesPair extends javax.swing.JFrame {

    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    private static final int NAME_COL = 0;
    private static final int RANK_COL = 1;
    private static final int SCORE_COL = 2;
    private static final int COUNTRY_COL = 3;
    private static final int CLUB_COL = 4;
    private static final int TABLE_NUMBER_COL = 0;
    private static final int WHITE_PLAYER_COL = 1;
    private static final int BLACK_PLAYER_COL = 2;
    private static final int HANDICAP_COL = 3;
    
    private int playersSortType = PlayerComparator.SCORE_ORDER;
    private int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
    /**  current Tournament */
    private TournamentInterface tournament = null;
    /** current Round */
    private int processedRoundNumber = 0;

    /**
     * Creates new form JFrGamesPair
     */
    public JFrGamesPair(TournamentInterface tournament) throws RemoteException {
//        LogElements.incrementElement("games.pair", "");
        this.tournament = tournament;
        processedRoundNumber = tournament.presumablyCurrentRoundNumber();
        initComponents();
        customInitComponents();
        setupRefreshTimer();
    }

    private void setupRefreshTimer() {
        ActionListener taskPerformer = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (tournament.getLastTournamentModificationTime() > lastComponentsUpdateTime) {
                        updateAllViews();
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new javax.swing.Timer((int) REFRESH_DELAY, taskPerformer).start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * Unlike initComponents, customInitComponents is editable
     */
    private void customInitComponents() throws RemoteException {
        int w = JFrGotha.MEDIUM_FRAME_WIDTH;
        int h = JFrGotha.MEDIUM_FRAME_HEIGHT;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - w) / 2, (dim.height - h) / 2, w, h);

        setIconImage(Gotha.getIconImage());
        
        getRootPane().setDefaultButton(btnSearch);
        initPlayersComponents();
        initPreviousGamesComponents();
        initGamesComponents();

        updateAllViews();
    }

    private void initPlayersComponents() {
        initPlayersTable(tblPairablePlayers);
        initPlayersTable(tblNotPairablePlayers);
    }

    private void initPreviousGamesComponents() {

        this.pnlPreviousGames.setVisible(false);
        initPreviousGamesTable();
    }

    private void initGamesComponents() {
        initGamesTable(tblGames);
    }

    private void initPlayersTable(JTable tbl) {
        final int NAME_WIDTH = 125;
        final int RANK_WIDTH = 30;
        final int SCORE_WIDTH = 40;
        final int COUNTRY_WIDTH = 25;
        final int CLUB_WIDTH = 40;
        
        JFrGotha.formatColumn(tbl, NAME_COL, "First name", NAME_WIDTH, JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tbl, RANK_COL, "Rk", RANK_WIDTH, JLabel.RIGHT, JLabel.RIGHT);
        JFrGotha.formatColumn(tbl, SCORE_COL, "Sco", SCORE_WIDTH, JLabel.RIGHT, JLabel.RIGHT);
        JFrGotha.formatColumn(tbl, COUNTRY_COL, "Co", COUNTRY_WIDTH, JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tbl, CLUB_COL, "Club", CLUB_WIDTH, JLabel.LEFT, JLabel.LEFT);
    }

    private void initPreviousGamesTable() {
        final int ROUND_NUMBER_WIDTH = 25;
        final int OPPONENT_WIDTH = 160;
        final int COLOR_WIDTH = 25;
        final int HANDICAP_WIDTH = 25;
        final int RESULT_WIDTH = 25;
        
        JFrGotha.formatColumn(tblPreviousGames, 0, "R", ROUND_NUMBER_WIDTH, JLabel.RIGHT, JLabel.RIGHT);
        JFrGotha.formatColumn(tblPreviousGames, 1, "Opponent", OPPONENT_WIDTH, JLabel.LEFT, JLabel.CENTER);
        JFrGotha.formatColumn(tblPreviousGames, 2, "C", COLOR_WIDTH, JLabel.CENTER, JLabel.CENTER);
        JFrGotha.formatColumn(tblPreviousGames, 3, "H", HANDICAP_WIDTH, JLabel.CENTER, JLabel.CENTER);
        JFrGotha.formatColumn(tblPreviousGames, 4, "R", RESULT_WIDTH, JLabel.CENTER, JLabel.CENTER);

    }

    private void initGamesTable(JTable tbl) {
        final int TABLE_NUMBER_WIDTH = 40;
        final int PLAYER_WIDTH = 150;
        final int HANDICAP_WIDTH = 20;
        
        JFrGotha.formatColumn(tbl, TABLE_NUMBER_COL, "Table", TABLE_NUMBER_WIDTH, JLabel.RIGHT, JLabel.RIGHT);
        JFrGotha.formatColumn(tbl, WHITE_PLAYER_COL, "White", PLAYER_WIDTH, JLabel.LEFT, JLabel.CENTER);
        JFrGotha.formatColumn(tbl, BLACK_PLAYER_COL, "Black", PLAYER_WIDTH, JLabel.LEFT, JLabel.CENTER);
        JFrGotha.formatColumn(tbl, HANDICAP_COL, "Hd", HANDICAP_WIDTH, JLabel.CENTER, JLabel.CENTER);

    }

    private void updateComponents() {
        DefaultTableModel pairablePlayersModel = (DefaultTableModel) tblPairablePlayers.getModel();
        DefaultTableModel notPairablePlayersModel = (DefaultTableModel) tblNotPairablePlayers.getModel();
        DefaultTableModel gamesModel = (DefaultTableModel) tblGames.getModel();

        while (pairablePlayersModel.getRowCount() > 0) {
            pairablePlayersModel.removeRow(0);
        }
        while (notPairablePlayersModel.getRowCount() > 0) {
            notPairablePlayersModel.removeRow(0);
        }
        while (gamesModel.getRowCount() > 0) {
            gamesModel.removeRow(0);
        }

        this.spnRoundNumber.setValue(this.processedRoundNumber + 1);

        HashMap<String, Player> hmPlayers = null;
        ArrayList<Game> alActualGames = null;
        Player byePlayer = null;
        try {
            hmPlayers = tournament.playersHashMap();
            alActualGames = tournament.gamesList(processedRoundNumber);
            byePlayer = tournament.getByePlayer(processedRoundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Unassigned players
        // hmPairablePlayers will be set by substraction
        // alNotPairablePlayers will be set by addition
        HashMap<String, Player> hmPairablePlayers = new HashMap<String, Player>(hmPlayers);
        ArrayList<Player> alNotPairablePlayers = new ArrayList<Player>();

        for (Player p : hmPlayers.values()) {
            if (p.getRegisteringStatus().compareTo("FIN") != 0
                    || !p.getParticipating()[processedRoundNumber]) {
                alNotPairablePlayers.add(p);
                hmPairablePlayers.remove(p.getKeyString());
            }
        }

        for (Game g : alActualGames) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            if (wP != null) hmPairablePlayers.remove(wP.getKeyString());
            if (bP != null) hmPairablePlayers.remove(bP.getKeyString());
        }

        Player byeP = null;
        try {
            byeP = tournament.getByePlayer(processedRoundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (byeP != null) {
            hmPairablePlayers.remove(byeP.getKeyString());
        }
        ArrayList<Player> alPairablePlayers = new ArrayList<Player>(hmPairablePlayers.values());

        if (playersSortType == PlayerComparator.SCORE_ORDER){
            alPairablePlayers = this.scoreSortedPlayers(alPairablePlayers);
        }
        else{
            PlayerComparator playerComparator = new PlayerComparator(playersSortType);
            Collections.sort(alPairablePlayers, playerComparator);
        }
        this.txfNbPairablePlayers.setText("" + alPairablePlayers.size());

        this.txfNbUnPairablePlayers.setText("" + alNotPairablePlayers.size());

        this.txfNbGames.setText("" + alActualGames.size());

        fillPlayersTable(alPairablePlayers, tblPairablePlayers);
        fillPlayersTable(alNotPairablePlayers, tblNotPairablePlayers);
        fillGamesTable(alActualGames, tblGames);

        // Bye player issues
        if (byePlayer == null) {
            txfByePlayer.setText("No bye player");
            btnByePlayer.setText(">>>");

        } else {
            String strRk = Player.convertIntToKD(byePlayer.getRank());
            txfByePlayer.setText(byePlayer.fullName()
                    + " " + strRk + " " + byePlayer.getCountry() + " " + byePlayer.getClub());

            btnByePlayer.setText("<<<");

        }
        if (byePlayer == null && alPairablePlayers.size() % 2 == 0) {
            btnByePlayer.setVisible(false);
            lblByePlayer.setVisible(false);
        } else {
            btnByePlayer.setVisible(true);
            lblByePlayer.setVisible(true);
        }

        // update previous games and visibility issues
        setVisibilityOfPairablePlayersAndPreviousGamesPanels();
    }
    /**
     * From a non sorted alP array, returns a score sorted array
     * Sorting is made according to tournament ppas and processedRoundNumber
     * @param alP
     * @return 
     */
    private ArrayList<Player> scoreSortedPlayers(ArrayList<Player> alP){
        ArrayList<Player> alSSP = new ArrayList<Player>();
        PlacementParameterSet pps;
        ArrayList<ScoredPlayer> alSP = null;
        try {
            pps = tournament.getTournamentParameterSet().getPlacementParameterSet();
            alSP = tournament.orderedScoredPlayersList(processedRoundNumber - 1, pps);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(ScoredPlayer sp : alSP){
            for(Player p : alP){
                if (p.hasSameKeyString(sp)){
                    alSSP.add(p);
                }
            }
        }
        
        return alSSP;
    }

    private void updatePnlPreviousGames() {
        DefaultTableModel model = (DefaultTableModel) tblPreviousGames.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        ArrayList<Player> alPlayers = selectedPlayersList();
        if (alPlayers.size() != 1) {
            System.out.println("Internal Error. At this point, exactly one player should be selected");
            return;
        }

        Player p = alPlayers.get(0);
        this.lblPreviousGames.setText("Previous games of " + p.getName() + "" + p.getFirstName());


        ArrayList<Game> alG = null;
        try {
            alG = tournament.gamesPlayedBy(p);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int r = 0; r < this.processedRoundNumber; r++) {
            String strRound = "" + (r + 1);
            String strOpponent = "Not assigned";
            String strColor = "";
            String strHd = "";
            String strRes = "";
            boolean playerFound = false;
            if (!p.getParticipating()[r]) {
                strOpponent = "not participating";
                playerFound = true;
            }
            if (!playerFound) {
                Player byeP = null;
                try {
                    byeP = tournament.getByePlayer(r);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (p.hasSameKeyString(byeP)) {
                    strOpponent = "Bye player";
                    playerFound = true;
                }
            }
            if (!playerFound) {
                for (Game g : alG) {
                    if (g.getRoundNumber() != r) {
                        continue;
                    }
                    if (g.getWhitePlayer().hasSameKeyString(p)) {
                        Player op = g.getBlackPlayer();
                        strOpponent = op.fullName();
                        strColor = "W";
                        strHd = "" + g.getHandicap();
                        int result = g.getResult();
                        if (result >= Game.RESULT_BYDEF) {
                            result -= Game.RESULT_BYDEF;
                        }
                        switch (result) {
                            case Game.RESULT_WHITEWINS:
                            case Game.RESULT_BOTHWIN:
                                strRes = "+";
                                break;
                            case Game.RESULT_BLACKWINS:
                            case Game.RESULT_BOTHLOSE:
                                strRes = "-";
                                break;
                            case Game.RESULT_EQUAL:
                                strRes = "=";
                                break;
                            default:
                                strRes = "?";
                        }
                        break;
                    } else if (g.getBlackPlayer().hasSameKeyString(p)) {
                        Player op = g.getWhitePlayer();
                        strOpponent = op.fullName();
                        strColor = "B";
                        strHd = "" + g.getHandicap();
                        switch (g.getResult()) {
                            case Game.RESULT_BLACKWINS:
                            case Game.RESULT_BOTHWIN:
                                strRes = "+";
                                break;
                            case Game.RESULT_WHITEWINS:
                            case Game.RESULT_BOTHLOSE:
                                strRes = "-";
                                break;
                            case Game.RESULT_EQUAL:
                                strRes = "=";
                                break;
                            default:
                                strRes = "?";
                        }
                        break;
                    }
                }
            }
            Vector<String> row = new Vector<String>();
            row.add(strRound);
            row.add(strOpponent);
            row.add(strColor);
            row.add(strHd);
            row.add(strRes);

            model.addRow(row);
        }


    }

    private void fillGamesTable(ArrayList<Game> alG, JTable tblG) {
        DefaultTableModel model = (DefaultTableModel) tblG.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
            // sort
        }
        ArrayList<Game> alDisplayedGames = new ArrayList<Game>(alG);

        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alDisplayedGames, gameComparator);

        for (Game g : alDisplayedGames) {
            Vector<String> row = new Vector<String>();
            row.add("" + (g.getTableNumber() + 1));

            Player wP = g.getWhitePlayer();
            if(wP == null) continue;
            row.add(wP.fullName());

            Player bP = g.getBlackPlayer();
            if(bP == null) continue;
            row.add(bP.fullName());

            row.add("" + g.getHandicap());

            model.addRow(row);
        }
    }

    private void fillPlayersTable(ArrayList<Player> alP, JTable tblP) {
        ArrayList<Player> alDisplayedPlayers = new ArrayList<Player>(alP);
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = null;
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        try {
            alOrderedScoredPlayers = tournament.orderedScoredPlayersList(processedRoundNumber - 1, tps.getPlacementParameterSet());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Prepare hmScoredPlayers
        HashMap<String, ScoredPlayer> hmScoredPlayers = new HashMap<String, ScoredPlayer>();
        if (alOrderedScoredPlayers != null) {
            for (ScoredPlayer sp : alOrderedScoredPlayers) {
                hmScoredPlayers.put(sp.getKeyString(), sp);
            }
        }

        int mainCrit = pps.mainCriterion();
        for (Player p : alDisplayedPlayers) {
            ScoredPlayer sp = hmScoredPlayers.get(p.getKeyString());
            Vector<String> row = new Vector<String>();
            row.add(p.fullName());

            row.add(Player.convertIntToKD(p.getRank()));

            int mainScore2 = sp.getCritValue(mainCrit, processedRoundNumber - 1);

            row.add(Player.convertIntScoreToString(mainScore2, 2));
            row.add(p.getCountry());
            row.add(p.getClub());
            DefaultTableModel model = (DefaultTableModel) tblP.getModel();
            model.addRow(row);
        }
    }

    /**
     * Produces a list of selected players in tblPairablePlayers
     * If no player is selected, returns the full list  
     */
    private ArrayList<Player> selectedPlayersList() {
        ArrayList<Player> alSelectedPlayers = new ArrayList<Player>();

        boolean bNoPlayerSelected = false;
        if (tblPairablePlayers.getSelectedRowCount() == 0) {
            bNoPlayerSelected = true;
            // gather selected players into alPlayersToPair
        }
        for (int iRow = 0; iRow < tblPairablePlayers.getModel().getRowCount(); iRow++) {
            if (tblPairablePlayers.isRowSelected(iRow) || bNoPlayerSelected) {
                String name = (String) tblPairablePlayers.getModel().getValueAt(iRow, NAME_COL);
                Player p = null;
                try {
                    p = tournament.getPlayerByKeyString(name);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
                }
                alSelectedPlayers.add(p);
            }
        }
        return alSelectedPlayers;
    }

    /**
     * Produces a list of selected games in tblGames
     * If no game is selected, returns all the games  
     */
    private ArrayList<Game> selectedGamesList() {
        ArrayList<Game> alSelectedGames = new ArrayList<Game>();
        
        boolean bNoGameSelected = false;
        if (this.tblGames.getSelectedRowCount() == 0) {
            bNoGameSelected = true;
            // gather selected players into alPlayersToPair
        }
        for (int iRow = 0; iRow < tblGames.getModel().getRowCount(); iRow++) {
            if (tblGames.isRowSelected(iRow)|| bNoGameSelected ) {
                String s = (String) tblGames.getModel().getValueAt(iRow, TABLE_NUMBER_COL);
                int tableNumber = Integer.parseInt(s) - 1;
                Game g = null;
                try {
                    g = tournament.getGame(processedRoundNumber, tableNumber);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
                }
                alSelectedGames.add(g);
            }
        }
        return alSelectedGames;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pupGames = new javax.swing.JPopupMenu();
        mniRenumberTables = new javax.swing.JMenuItem();
        mniChangeTableNumber = new javax.swing.JMenuItem();
        mniShiftTables = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mniExchangeColors = new javax.swing.JMenuItem();
        mniModifyHandicap = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mniCancel = new javax.swing.JMenuItem();
        dlgPairingReport = new javax.swing.JDialog();
        ckbNotShownUp = new javax.swing.JCheckBox();
        ckbMMSGreaterThan = new javax.swing.JCheckBox();
        txfMMSDiffThreshold = new javax.swing.JTextField();
        ckbHandicapGreaterThan = new javax.swing.JCheckBox();
        txfHandicapThreshold = new javax.swing.JTextField();
        ckbIntraCountry = new javax.swing.JCheckBox();
        ckbIntraClub = new javax.swing.JCheckBox();
        btnGenerateReport = new javax.swing.JButton();
        btnDlgPairingReportClose = new javax.swing.JButton();
        ckbUnbalancedMMSDUDDPlayers = new javax.swing.JCheckBox();
        scpReport = new javax.swing.JScrollPane();
        txaReport = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        ckbUnbalancedWB = new javax.swing.JCheckBox();
        txfUnbalancedWB = new javax.swing.JTextField();
        pupPairablePlayers = new javax.swing.JPopupMenu();
        mniSortByName = new javax.swing.JMenuItem();
        mniSortByRank = new javax.swing.JMenuItem();
        mniSortByScore = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniCancel1 = new javax.swing.JMenuItem();
        pnlInternal = new javax.swing.JPanel();
        btnPair = new javax.swing.JButton();
        btnUnpair = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        pnlPlayers = new javax.swing.JPanel();
        txfNbPairablePlayers = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        scpPairablePlayers = new javax.swing.JScrollPane();
        tblPairablePlayers = new javax.swing.JTable();
        pnlPreviousGames = new javax.swing.JPanel();
        scpPreviousGames = new javax.swing.JScrollPane();
        tblPreviousGames = new javax.swing.JTable();
        lblPreviousGames = new javax.swing.JLabel();
        pnlUnPairablePlayers = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txfNbUnPairablePlayers = new javax.swing.JTextField();
        scpNotPairablePlayers = new javax.swing.JScrollPane();
        tblNotPairablePlayers = new javax.swing.JTable();
        pnlGames = new javax.swing.JPanel();
        scpGames = new javax.swing.JScrollPane();
        tblGames = new javax.swing.JTable();
        txfNbGames = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnPrint = new javax.swing.JButton();
        txfByePlayer = new javax.swing.JTextField();
        btnReport = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnByePlayer = new javax.swing.JButton();
        lblByePlayer = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        spnRoundNumber = new javax.swing.JSpinner();
        btnHelp = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txfSearchPlayer = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();

        mniRenumberTables.setText("Renumber all tables by MMS");
        mniRenumberTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRenumberTablesActionPerformed(evt);
            }
        });
        pupGames.add(mniRenumberTables);

        mniChangeTableNumber.setText("Change table number");
        mniChangeTableNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniChangeTableNumberActionPerformed(evt);
            }
        });
        pupGames.add(mniChangeTableNumber);

        mniShiftTables.setText("Shift tables");
        mniShiftTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniShiftTablesActionPerformed(evt);
            }
        });
        pupGames.add(mniShiftTables);
        pupGames.add(jSeparator2);

        mniExchangeColors.setText("Exchange colours");
        mniExchangeColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExchangeColorsActionPerformed(evt);
            }
        });
        pupGames.add(mniExchangeColors);

        mniModifyHandicap.setText("Modify handicap");
        mniModifyHandicap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniModifyHandicapActionPerformed(evt);
            }
        });
        pupGames.add(mniModifyHandicap);
        pupGames.add(jSeparator5);

        mniCancel.setText("Cancel");
        mniCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCancelActionPerformed(evt);
            }
        });
        pupGames.add(mniCancel);

        dlgPairingReport.getContentPane().setLayout(null);

        ckbNotShownUp.setSelected(true);
        ckbNotShownUp.setText(" Paired players who had not shown up");
        dlgPairingReport.getContentPane().add(ckbNotShownUp);
        ckbNotShownUp.setBounds(10, 20, 300, 23);

        ckbMMSGreaterThan.setSelected(true);
        ckbMMSGreaterThan.setText("Pairs with a MMS difference greater than ");
        dlgPairingReport.getContentPane().add(ckbMMSGreaterThan);
        ckbMMSGreaterThan.setBounds(10, 70, 260, 23);

        txfMMSDiffThreshold.setText("0");
        dlgPairingReport.getContentPane().add(txfMMSDiffThreshold);
        txfMMSDiffThreshold.setBounds(282, 70, 20, 20);

        ckbHandicapGreaterThan.setSelected(true);
        ckbHandicapGreaterThan.setText("Pairs with a handicap greater than ");
        dlgPairingReport.getContentPane().add(ckbHandicapGreaterThan);
        ckbHandicapGreaterThan.setBounds(10, 100, 260, 23);

        txfHandicapThreshold.setText("1");
        dlgPairingReport.getContentPane().add(txfHandicapThreshold);
        txfHandicapThreshold.setBounds(282, 100, 20, 20);

        ckbIntraCountry.setText("Intra-country pairs");
        dlgPairingReport.getContentPane().add(ckbIntraCountry);
        ckbIntraCountry.setBounds(10, 160, 260, 23);

        ckbIntraClub.setSelected(true);
        ckbIntraClub.setText("Intra-club pairs");
        dlgPairingReport.getContentPane().add(ckbIntraClub);
        ckbIntraClub.setBounds(10, 130, 260, 23);

        btnGenerateReport.setText("Generate report");
        btnGenerateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateReportActionPerformed(evt);
            }
        });
        dlgPairingReport.getContentPane().add(btnGenerateReport);
        btnGenerateReport.setBounds(10, 290, 300, 23);

        btnDlgPairingReportClose.setText("Close");
        btnDlgPairingReportClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDlgPairingReportCloseActionPerformed(evt);
            }
        });
        dlgPairingReport.getContentPane().add(btnDlgPairingReportClose);
        btnDlgPairingReportClose.setBounds(10, 480, 770, 23);

        ckbUnbalancedMMSDUDDPlayers.setSelected(true);
        ckbUnbalancedMMSDUDDPlayers.setText("Unbalanced MMS draw up/down players  ");
        dlgPairingReport.getContentPane().add(ckbUnbalancedMMSDUDDPlayers);
        ckbUnbalancedMMSDUDDPlayers.setBounds(10, 210, 260, 23);

        txaReport.setColumns(20);
        txaReport.setLineWrap(true);
        txaReport.setRows(5);
        scpReport.setViewportView(txaReport);

        dlgPairingReport.getContentPane().add(scpReport);
        scpReport.setBounds(340, 20, 440, 450);

        jLabel4.setText("in previous round");
        dlgPairingReport.getContentPane().add(jLabel4);
        jLabel4.setBounds(50, 40, 260, 20);

        ckbUnbalancedWB.setSelected(true);
        ckbUnbalancedWB.setText("White/Black unbalance greater than");
        ckbUnbalancedWB.setToolTipText("in no-handicap games only");
        dlgPairingReport.getContentPane().add(ckbUnbalancedWB);
        ckbUnbalancedWB.setBounds(10, 240, 260, 23);

        txfUnbalancedWB.setText("1");
        txfUnbalancedWB.setEnabled(false);
        dlgPairingReport.getContentPane().add(txfUnbalancedWB);
        txfUnbalancedWB.setBounds(282, 240, 20, 20);

        pupPairablePlayers.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniSortByName.setText("Sort by name");
        mniSortByName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByNameActionPerformed(evt);
            }
        });
        pupPairablePlayers.add(mniSortByName);

        mniSortByRank.setText("Sort by rank");
        mniSortByRank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByRankActionPerformed(evt);
            }
        });
        pupPairablePlayers.add(mniSortByRank);

        mniSortByScore.setText("Sort by score");
        mniSortByScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByScoreActionPerformed(evt);
            }
        });
        pupPairablePlayers.add(mniSortByScore);
        pupPairablePlayers.add(jSeparator1);

        mniCancel1.setText("Cancel");
        mniCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCancel1ActionPerformed(evt);
            }
        });
        pupPairablePlayers.add(mniCancel1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Games .. Pair");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(null);

        pnlInternal.setLayout(null);

        btnPair.setText(">>>");
        btnPair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPairActionPerformed(evt);
            }
        });
        pnlInternal.add(btnPair);
        btnPair.setBounds(280, 100, 120, 30);

        btnUnpair.setText("<<<");
        btnUnpair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnpairActionPerformed(evt);
            }
        });
        pnlInternal.add(btnUnpair);
        btnUnpair.setBounds(280, 160, 120, 30);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Pair");
        pnlInternal.add(jLabel6);
        jLabel6.setBounds(280, 80, 110, 14);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Unpair");
        pnlInternal.add(jLabel7);
        jLabel7.setBounds(280, 140, 110, 14);

        jLabel9.setText("Round");
        pnlInternal.add(jLabel9);
        jLabel9.setBounds(10, 20, 50, 14);

        pnlPlayers.setBorder(javax.swing.BorderFactory.createTitledBorder("Players"));
        pnlPlayers.setLayout(null);

        txfNbPairablePlayers.setEditable(false);
        txfNbPairablePlayers.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txfNbPairablePlayers.setText("1999");
        pnlPlayers.add(txfNbPairablePlayers);
        txfNbPairablePlayers.setBounds(10, 20, 30, 20);

        jLabel1.setText("pairable players");
        pnlPlayers.add(jLabel1);
        jLabel1.setBounds(50, 20, 200, 14);

        tblPairablePlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Rk", "Sco", "Co", "Club"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPairablePlayers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPairablePlayersMouseClicked(evt);
            }
        });
        scpPairablePlayers.setViewportView(tblPairablePlayers);

        pnlPlayers.add(scpPairablePlayers);
        scpPairablePlayers.setBounds(10, 40, 260, 280);

        pnlPreviousGames.setLayout(null);

        tblPreviousGames.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "R", "Opponent", "Color", "Hd", "Res"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scpPreviousGames.setViewportView(tblPreviousGames);

        pnlPreviousGames.add(scpPreviousGames);
        scpPreviousGames.setBounds(10, 20, 260, 120);

        lblPreviousGames.setText("Previous games of xxx");
        pnlPreviousGames.add(lblPreviousGames);
        lblPreviousGames.setBounds(10, 0, 260, 20);

        pnlPlayers.add(pnlPreviousGames);
        pnlPreviousGames.setBounds(0, 330, 280, 140);

        pnlUnPairablePlayers.setLayout(null);

        jLabel2.setText("unpairable players");
        pnlUnPairablePlayers.add(jLabel2);
        jLabel2.setBounds(50, 0, 200, 14);

        txfNbUnPairablePlayers.setEditable(false);
        txfNbUnPairablePlayers.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txfNbUnPairablePlayers.setText("1999");
        pnlUnPairablePlayers.add(txfNbUnPairablePlayers);
        txfNbUnPairablePlayers.setBounds(10, 0, 30, 20);

        tblNotPairablePlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Rk", "Sco", "Co", "Club"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scpNotPairablePlayers.setViewportView(tblNotPairablePlayers);

        pnlUnPairablePlayers.add(scpNotPairablePlayers);
        scpNotPairablePlayers.setBounds(10, 20, 260, 120);

        pnlPlayers.add(pnlUnPairablePlayers);
        pnlUnPairablePlayers.setBounds(0, 330, 280, 140);

        pnlInternal.add(pnlPlayers);
        pnlPlayers.setBounds(0, 40, 280, 480);

        pnlGames.setBorder(javax.swing.BorderFactory.createTitledBorder("Games"));
        pnlGames.setLayout(null);

        tblGames.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Table", "White", "Black", "Hd"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblGames.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGamesMouseClicked(evt);
            }
        });
        tblGames.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblGamesFocusLost(evt);
            }
        });
        scpGames.setViewportView(tblGames);

        pnlGames.add(scpGames);
        scpGames.setBounds(10, 40, 360, 250);

        txfNbGames.setEditable(false);
        txfNbGames.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txfNbGames.setText("1000");
        pnlGames.add(txfNbGames);
        txfNbGames.setBounds(10, 20, 40, 20);

        jLabel3.setText("tables");
        pnlGames.add(jLabel3);
        jLabel3.setBounds(60, 20, 200, 14);

        jLabel8.setText("Bye player");
        pnlGames.add(jLabel8);
        jLabel8.setBounds(10, 300, 80, 14);

        btnPrint.setText("Print...");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnlGames.add(btnPrint);
        btnPrint.setBounds(10, 360, 360, 25);

        txfByePlayer.setEditable(false);
        pnlGames.add(txfByePlayer);
        txfByePlayer.setBounds(120, 300, 250, 20);

        btnReport.setText("Pairing report ...");
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        pnlGames.add(btnReport);
        btnReport.setBounds(10, 330, 360, 20);

        jButton1.setText("Print Result sheets ...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        pnlGames.add(jButton1);
        jButton1.setBounds(10, 390, 360, 20);

        pnlInternal.add(pnlGames);
        pnlGames.setBounds(400, 40, 380, 430);

        btnByePlayer.setText(">>>");
        btnByePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnByePlayerActionPerformed(evt);
            }
        });
        pnlInternal.add(btnByePlayer);
        btnByePlayer.setBounds(280, 330, 120, 30);

        lblByePlayer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblByePlayer.setText("Bye player");
        pnlInternal.add(lblByePlayer);
        lblByePlayer.setBounds(280, 310, 110, 14);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        pnlInternal.add(btnClose);
        btnClose.setBounds(410, 480, 360, 30);

        spnRoundNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRoundNumberStateChanged(evt);
            }
        });
        pnlInternal.add(spnRoundNumber);
        spnRoundNumber.setBounds(60, 10, 40, 30);

        btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/gothalogo16.jpg"))); // NOI18N
        btnHelp.setText("help");
        btnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpActionPerformed(evt);
            }
        });
        pnlInternal.add(btnHelp);
        btnHelp.setBounds(280, 480, 120, 30);

        jLabel5.setText("Search for a player");
        pnlInternal.add(jLabel5);
        jLabel5.setBounds(290, 220, 110, 14);
        pnlInternal.add(txfSearchPlayer);
        txfSearchPlayer.setBounds(290, 240, 110, 20);

        btnSearch.setText("Search/Next");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnlInternal.add(btnSearch);
        btnSearch.setBounds(290, 260, 110, 23);

        getContentPane().add(pnlInternal);
        pnlInternal.setBounds(0, 0, 780, 520);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblPairablePlayersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPairablePlayersMouseClicked
        setVisibilityOfPairablePlayersAndPreviousGamesPanels();
        // Right click
        if (evt.getModifiers() != InputEvent.BUTTON3_MASK) return;
        Point p = evt.getLocationOnScreen();
        this.pupPairablePlayers.setLocation(p);
        pupPairablePlayers.setVisible(true); 
    }//GEN-LAST:event_tblPairablePlayersMouseClicked

    private void setVisibilityOfPairablePlayersAndPreviousGamesPanels() {
        if (tblPairablePlayers.getSelectedRowCount() == 1) {
            this.pnlPreviousGames.setVisible(true);
            this.pnlUnPairablePlayers.setVisible(false);
            updatePnlPreviousGames();
        } else {
            this.pnlPreviousGames.setVisible(false);
            this.pnlUnPairablePlayers.setVisible(true);
        }
    }
    
    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        TournamentPrinting.printGamesList(tournament, processedRoundNumber);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void mniModifyHandicapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniModifyHandicapActionPerformed
        pupGames.setVisible(false);
        ArrayList<Game> alSelectedGames = selectedGamesList();
        if (alSelectedGames.isEmpty() || alSelectedGames.size() >= 2) {
            JOptionPane.showMessageDialog(this, "Please, select one game");
            return;
        }

        Game g = alSelectedGames.get(0);
        String strOldHd = "" + g.getHandicap();
        String strResponse = JOptionPane.showInputDialog("Enter new handicap", strOldHd);
        if (strResponse == null) {
            return;
        }
        int hd = Integer.parseInt(strResponse);

        try {
            tournament.setGameHandicap(g, hd);
            this.tournamentChanged();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniModifyHandicapActionPerformed

    private void btnByePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnByePlayerActionPerformed
        try {
            // If  no bye player exists, it is a request for choosing one 
            if (tournament.getByePlayer(processedRoundNumber) == null) {
                ArrayList<Player> alP = selectedPlayersList();
                tournament.chooseAByePlayer(alP, processedRoundNumber);
            } // If a bye player exists, it is a request for removing it
            else {
                tournament.unassignByePlayer(processedRoundNumber);
            }
            this.tournamentChanged();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnByePlayerActionPerformed

    /**
     * For Debug
     */
    private void mniRenumberTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRenumberTablesActionPerformed
        pupGames.setVisible(false);
        try {
            tournament.renumberTablesByBestMMS(processedRoundNumber, this.selectedGamesList());
//            tournament.renumberTables(processedRoundNumber);
            this.tournamentChanged();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniRenumberTablesActionPerformed

    private void mniExchangeColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExchangeColorsActionPerformed
        pupGames.setVisible(false);
        ArrayList<Game> alSelectedGames = selectedGamesList();
        if (alSelectedGames.isEmpty() || alSelectedGames.size() >= 2) {
            JOptionPane.showMessageDialog(this, "Please, select one game");
            return;
        }

        Game g = alSelectedGames.get(0);
        try {
            tournament.exchangeGameColors(g);
            this.tournamentChanged();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniExchangeColorsActionPerformed

    private void tblGamesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGamesMouseClicked
        this.pupGames.setVisible(false);
        // Right click
        if (evt.getModifiers() != InputEvent.BUTTON3_MASK) return;
        
        ArrayList<Game> alGames = this.selectedGamesList();
        Point pInScreen = evt.getLocationOnScreen();
        Point pInSourceComponent = evt.getPoint();
        if (alGames.isEmpty()) {
            int row = this.tblGames.rowAtPoint(pInSourceComponent);
            this.tblGames.setRowSelectionInterval(row, row);
            alGames = this.selectedGamesList();
        }

        if (alGames.isEmpty()) {
            // This should not happen
            System.out.println("tblGamesMouseClicked" + " alGames empty!!!");
            return;
        } 
        
        Game game = alGames.get(0);
        String strW = game.getWhitePlayer().fullName();
        if (strW.length() > 20) {
            strW = strW.substring(0, 20);
        }
        String strB = game.getBlackPlayer().fullName();
        if (strB.length() > 20) {
            strB = strB.substring(0, 20);
        }
        this.mniChangeTableNumber.setText("Change table number of "
                + strW + "-" + strB);
        this.mniExchangeColors.setText("Exchange colours of "
                + strW + "-" + strB);
        this.mniModifyHandicap.setText("Modify handicap of "
                + strW + "-" + strB);

        pupGames.setLocation(pInScreen);
        pupGames.setVisible(true);
        
    }//GEN-LAST:event_tblGamesMouseClicked

    private void btnUnpairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnpairActionPerformed
//        boolean bRemoveAllGames = false;
        ArrayList<Game> alGamesToRemove = selectedGamesList();

        int nbGamesToRemove = alGamesToRemove.size();
        if (nbGamesToRemove > 1) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Gotha will unpair " + nbGamesToRemove + " games"
                    + "\nUnpair ?",
                    "Message",
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION);
            if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }

        }
        try {
            // And now, remove games from tournament
            for (Game g : alGamesToRemove) {
                tournament.removeGame(g);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TournamentException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        // If all games removed, also remove bye player
        try {
            if ((tournament.getByePlayer(processedRoundNumber) != null) && (tournament.gamesList(processedRoundNumber).isEmpty())) {
                tournament.unassignByePlayer(processedRoundNumber);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.tournamentChanged();

    }//GEN-LAST:event_btnUnpairActionPerformed

    private void btnPairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPairActionPerformed
        ArrayList<Player> alPlayersToPair = selectedPlayersList();

        // Issue an error message if a player is in "PRE" status
        for (Player p : alPlayersToPair) {
            if (p.getRegisteringStatus().compareTo("FIN") != 0) {
                JOptionPane.showMessageDialog(this, "At least one player is not in a Final registering status"
                        + "\n" + "You should update registering status (Players .. Players Quick Check)",
                        "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if (alPlayersToPair.size() % 2 != 0) {
            // if no possibility to choose a bye player, Error message
            Player bP = null;
            try {
                bP = tournament.getByePlayer(processedRoundNumber);
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!btnByePlayer.isVisible() || bP != null) {
                JOptionPane.showMessageDialog(this, "Please, select an even number of players",
                        "Message", JOptionPane.ERROR_MESSAGE);
                return;
            } else { // Gotha may choose a bye player
                int response = JOptionPane.showConfirmDialog(this, "Odd number of players"
                        + "\nGotha will choose a bye player. OK ?",
                        "Message", JOptionPane.WARNING_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return;
                } else {
                    try {
                        tournament.chooseAByePlayer(alPlayersToPair, processedRoundNumber);
                        // remove bye player from alPlayersToPair
                        Player byeP = tournament.getByePlayer(processedRoundNumber);
                        Player pToRemove = null;
                        for (Player p : alPlayersToPair) {
                            if (p.hasSameKeyString(byeP)) {
                                pToRemove = p;
                            }
                        }
                        alPlayersToPair.remove(pToRemove);
                    } catch (RemoteException ex) {
                        Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        ArrayList<Game> alNewGames = null;
        try {
            alNewGames = tournament.makeAutomaticPairing(alPlayersToPair, processedRoundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Check if there is a previously paired couple of players
        ArrayList<Game> alOldGames = null;
        try {
            alOldGames = tournament.gamesListBefore(processedRoundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        Game questionableGame = null;
        for (Game newG : alNewGames) {
            Player newWP = newG.getWhitePlayer();
            Player newBP = newG.getBlackPlayer();
            for (Game oldG : alOldGames) {
                Player oldWP = oldG.getWhitePlayer();
                Player oldBP = oldG.getBlackPlayer();
                if (oldWP.hasSameKeyString(newWP) && oldBP.hasSameKeyString(newBP)) {
                    questionableGame = oldG;
                    break;
                }
                if (oldWP.hasSameKeyString(newBP) && oldBP.hasSameKeyString(newWP)) {
                    questionableGame = oldG;
                    break;
                }
            }
        }
        if (questionableGame != null) {
            Player wP = questionableGame.getWhitePlayer();
            Player bP = questionableGame.getBlackPlayer();
            int r = questionableGame.getRoundNumber();

            int bAnswer = JOptionPane.showConfirmDialog(this, wP.fullName() + " " + "and"
                    + " " + bP.fullName()
                    + " " + "have been already paired in round " + (r + 1)
                    + "\n" + "Do you want to keep this pairing nevertheless ?",
                    "Message", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (bAnswer == JOptionPane.NO_OPTION) {
                return;
            }
        }

        // Give a table number
        int tN = 0;
        for (Game newG : alNewGames) {
            boolean bTNOK;  // Table number OK

            do {
                bTNOK = true;
                try {
                    for (Game oldG : tournament.gamesList(processedRoundNumber)) {
                        if (oldG.getRoundNumber() != processedRoundNumber) {
                            continue;
                        }
                        if (oldG.getTableNumber() == tN) {
                            tN++;
                            bTNOK = false;
                        }
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (!bTNOK);
            newG.setTableNumber(tN++);
        }

        // Renumber tables inside alNewGames
        try {
            tournament.renumberTablesByBestMMS(processedRoundNumber, alNewGames);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }


        for (Game g : alNewGames) {
            try {
                tournament.addGame(g);
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TournamentException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.tournamentChanged();
    }//GEN-LAST:event_btnPairActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void spnRoundNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRoundNumberStateChanged
        int demandedRN = (Integer) (spnRoundNumber.getValue()) - 1;
        this.demandedDisplayedRoundNumberHasChanged(demandedRN);
}//GEN-LAST:event_spnRoundNumberStateChanged

    private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
        Gotha.displayGothaHelp("Games Pair frame");
}//GEN-LAST:event_btnHelpActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
//        LogElements.incrementElement("games.pairingreport", "");
        int w = JFrGotha.MEDIUM_FRAME_WIDTH;
        int h = JFrGotha.MEDIUM_FRAME_HEIGHT;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dlgPairingReport.setBounds((dim.width - w) / 2, (dim.height - h) / 2, w, h);
        dlgPairingReport.setTitle("Pairing report");
        dlgPairingReport.setIconImage(Gotha.getIconImage());
        dlgPairingReport.setVisible(true);
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnGenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateReportActionPerformed
        String strReport = "";
        strReport += "Round " + (processedRoundNumber + 1) + "\n\n";

        if (this.ckbNotShownUp.isSelected()) {
            strReport += Pairing.notShownUpInPreviousRoundReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (this.ckbMMSGreaterThan.isSelected()) {
            int mmsDiffThreshold = Integer.parseInt(this.txfMMSDiffThreshold.getText());
            strReport += Pairing.mmsDiffGreaterThanReport(tournament, processedRoundNumber, mmsDiffThreshold) + "\n\n";
        }
        if (this.ckbHandicapGreaterThan.isSelected()) {
            int handicapThreshold = Integer.parseInt(this.txfHandicapThreshold.getText());
            strReport += Pairing.handicapGreaterThanReport(tournament, processedRoundNumber, handicapThreshold) + "\n\n";
        }
        if (this.ckbIntraClub.isSelected()) {
            strReport += Pairing.intraClubPairingReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (this.ckbIntraCountry.isSelected()) {
            strReport += Pairing.intraCountryPairingReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (this.ckbUnbalancedMMSDUDDPlayers.isSelected()) {
            strReport += Pairing.unbalancedMMSDUDDPlayersReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (this.ckbUnbalancedWB.isSelected()) {
            int unbalancedWBThreshold = Integer.parseInt(this.txfUnbalancedWB.getText());
            strReport += Pairing.unbalancedWBPlayersReport(tournament, processedRoundNumber, unbalancedWBThreshold) + "\n\n";
        }

        txaReport.setText(strReport);
    }//GEN-LAST:event_btnGenerateReportActionPerformed

    private void btnDlgPairingReportCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDlgPairingReportCloseActionPerformed
        this.dlgPairingReport.dispose();
    }//GEN-LAST:event_btnDlgPairingReportCloseActionPerformed

    private void mniCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCancelActionPerformed
        this.pupGames.setVisible(false);
        int index2 = tblGames.getRowCount() - 1;
        if (index2 >= 0) this.tblGames.removeRowSelectionInterval(0, tblGames.getRowCount() - 1);
}//GEN-LAST:event_mniCancelActionPerformed

    private void mniShiftTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniShiftTablesActionPerformed
        pupGames.setVisible(false);
        ArrayList<Game> alSelectedGames = selectedGamesList();
        if (alSelectedGames.size() != 1) {
            JOptionPane.showMessageDialog(this, "Please, select the starting table to shift");
            return;
        }

        Game g1 = alSelectedGames.get(0);
        // Ask for a new number
        int oldBegTN = g1.getTableNumber();
        String strOldBegTN = "" + (g1.getTableNumber() + 1);
        String strResponse = JOptionPane.showInputDialog("Shift tables starting from " + strOldBegTN + " to tables starting from : ", strOldBegTN);
        int newBegTN = - 1;
        try{
            newBegTN = Integer.parseInt(strResponse) - 1;
        }
        catch (NumberFormatException exc) {
            JOptionPane.showMessageDialog(this, " Table number should be a number",
                    "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newBegTN <= oldBegTN) {
            JOptionPane.showMessageDialog(this, " You can shift table numbers to higher numbers only",
                    "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Game> alGamesToConsider = null;
        try {
            alGamesToConsider = tournament.gamesList(processedRoundNumber);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<Game> alGamesToRemove = new ArrayList<Game>();
        ArrayList<Game> alGamesToAdd = new ArrayList<Game>();

        for (Game g : alGamesToConsider) {
            int oldCurrentTN = g.getTableNumber();
            if (oldCurrentTN < oldBegTN) {
                continue;
            }
            int newCurrentTN = oldCurrentTN + newBegTN - oldBegTN;
            Game newG = new Game(g.getRoundNumber(), newCurrentTN, g.getWhitePlayer(), g.getBlackPlayer(), g.isKnownColor(), g.getHandicap(), g.getResult());

            if (newCurrentTN >= Gotha.MAX_NUMBER_OF_TABLES) {
                JOptionPane.showMessageDialog(this, " The table shift you ask for would lead to table numbers greater than "
                        + Gotha.MAX_NUMBER_OF_TABLES
                        + "\nThis is not possible",
                        "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            alGamesToRemove.add(g);
            alGamesToAdd.add(newG);
        }

        for (Game g : alGamesToRemove) {
            try {
                tournament.removeGame(g);
            } catch (TournamentException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (Game g : alGamesToAdd) {
            try {
                tournament.addGame(g);
            } catch (TournamentException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.tournamentChanged();
}//GEN-LAST:event_mniShiftTablesActionPerformed

    private void mniChangeTableNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniChangeTableNumberActionPerformed
        pupGames.setVisible(false);
        ArrayList<Game> alSelectedGames = selectedGamesList();
        if (alSelectedGames.isEmpty() || alSelectedGames.size() >= 2) {
            JOptionPane.showMessageDialog(this, "Please, select one game");
            return;
        }

        Game g1 = alSelectedGames.get(0);
        // Ask for a new number
        int oldTN = g1.getTableNumber();
        String strOldTN = "" + (oldTN + 1);
        String strResponse = JOptionPane.showInputDialog("Enter a new table number", strOldTN);
        int newTN = -1;
        try{
            newTN = Integer.parseInt(strResponse) - 1;
        }
        catch(NumberFormatException exc){
        }
        if (newTN < 0 || newTN >= Gotha.MAX_NUMBER_OF_TABLES) {
            JOptionPane.showMessageDialog(this, " Table number should be a number between 1 and " + Gotha.MAX_NUMBER_OF_TABLES,
                    "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newTN == oldTN) {
            return;
        }

        // If there already a game with tn as table number ?
        Game g2 = null;
        try {
            ArrayList<Game> alGames = tournament.gamesList(this.processedRoundNumber);
            for (Game g : alGames) {
                if (g.getTableNumber() == newTN) {
                    g2 = g;
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            tournament.removeGame(g1);
            g1.setTableNumber(newTN);
            if (g2 != null) {
                tournament.removeGame(g2);
                g2.setTableNumber(oldTN);
                tournament.addGame(g2);
            }
            tournament.addGame(g1);

        } catch (TournamentException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.tournamentChanged();
    }//GEN-LAST:event_mniChangeTableNumberActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.pupGames.setVisible(false);
    }//GEN-LAST:event_formWindowClosed

    private void tblGamesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblGamesFocusLost
        this.pupGames.setVisible(false);
    }//GEN-LAST:event_tblGamesFocusLost

    private void mniSortByRankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByRankActionPerformed
        playersSortType = PlayerComparator.RANK_ORDER;
        pupPairablePlayers.setVisible(false);   
        this.updateComponents();

    }//GEN-LAST:event_mniSortByRankActionPerformed

    private void mniCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCancel1ActionPerformed
        this.pupPairablePlayers.setVisible(false);
        this.tblPairablePlayers.removeRowSelectionInterval(0, tblPairablePlayers.getRowCount() - 1);
    }//GEN-LAST:event_mniCancel1ActionPerformed

    private void mniSortByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByNameActionPerformed
        playersSortType = PlayerComparator.NAME_ORDER;
        pupPairablePlayers.setVisible(false);
        this.updateComponents();
    }//GEN-LAST:event_mniSortByNameActionPerformed

    private void mniSortByScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByScoreActionPerformed
        playersSortType = PlayerComparator.SCORE_ORDER;
        pupPairablePlayers.setVisible(false);
        this.updateComponents();
    }//GEN-LAST:event_mniSortByScoreActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String strSearchPlayer = this.txfSearchPlayer.getText().toLowerCase();
        if (strSearchPlayer.length() == 0) {
            tblGames.clearSelection();
            return;
        }
        TableModel model = tblGames.getModel();
        int rowNumber = -1;
        int startRow = tblGames.getSelectedRow() + 1;
        int nbRows = model.getRowCount();
        for (int iR = 0; iR < nbRows; iR++) {
            int row = (startRow + iR) % nbRows;
            String str = (String) model.getValueAt(row, WHITE_PLAYER_COL);
            str = str.toLowerCase();
            if (str.indexOf(strSearchPlayer) >= 0) {
                rowNumber = row;
                break;
            }
            str = (String) model.getValueAt(row, BLACK_PLAYER_COL);
            str = str.toLowerCase();
            if (str.indexOf(strSearchPlayer) >= 0) {
                rowNumber = row;
                break;
            }
        }

        tblGames.clearSelection();
        if (rowNumber == -1) {
            JOptionPane.showMessageDialog(this,
                "No player with the specified name is paired in round " + (this.processedRoundNumber + 1),
                "Message", JOptionPane.ERROR_MESSAGE);
        } else {
            tblGames.setRowSelectionAllowed(true);
            tblGames.clearSelection();
            tblGames.addRowSelectionInterval(rowNumber, rowNumber);

            Rectangle rect = tblGames.getCellRect(rowNumber, 0, true);
            tblGames.scrollRectToVisible(rect);
        }

        tblGames.repaint();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        TournamentPrinting.printResultSheets(tournament, processedRoundNumber);
    }//GEN-LAST:event_jButton1ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnByePlayer;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDlgPairingReportClose;
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JButton btnHelp;
    private javax.swing.JButton btnPair;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUnpair;
    private javax.swing.JCheckBox ckbHandicapGreaterThan;
    private javax.swing.JCheckBox ckbIntraClub;
    private javax.swing.JCheckBox ckbIntraCountry;
    private javax.swing.JCheckBox ckbMMSGreaterThan;
    private javax.swing.JCheckBox ckbNotShownUp;
    private javax.swing.JCheckBox ckbUnbalancedMMSDUDDPlayers;
    private javax.swing.JCheckBox ckbUnbalancedWB;
    private javax.swing.JDialog dlgPairingReport;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JLabel lblByePlayer;
    private javax.swing.JLabel lblPreviousGames;
    private javax.swing.JMenuItem mniCancel;
    private javax.swing.JMenuItem mniCancel1;
    private javax.swing.JMenuItem mniChangeTableNumber;
    private javax.swing.JMenuItem mniExchangeColors;
    private javax.swing.JMenuItem mniModifyHandicap;
    private javax.swing.JMenuItem mniRenumberTables;
    private javax.swing.JMenuItem mniShiftTables;
    private javax.swing.JMenuItem mniSortByName;
    private javax.swing.JMenuItem mniSortByRank;
    private javax.swing.JMenuItem mniSortByScore;
    private javax.swing.JPanel pnlGames;
    private javax.swing.JPanel pnlInternal;
    private javax.swing.JPanel pnlPlayers;
    private javax.swing.JPanel pnlPreviousGames;
    private javax.swing.JPanel pnlUnPairablePlayers;
    private javax.swing.JPopupMenu pupGames;
    private javax.swing.JPopupMenu pupPairablePlayers;
    private javax.swing.JScrollPane scpGames;
    private javax.swing.JScrollPane scpNotPairablePlayers;
    private javax.swing.JScrollPane scpPairablePlayers;
    private javax.swing.JScrollPane scpPreviousGames;
    private javax.swing.JScrollPane scpReport;
    private javax.swing.JSpinner spnRoundNumber;
    private javax.swing.JTable tblGames;
    private javax.swing.JTable tblNotPairablePlayers;
    private javax.swing.JTable tblPairablePlayers;
    private javax.swing.JTable tblPreviousGames;
    private javax.swing.JTextArea txaReport;
    private javax.swing.JTextField txfByePlayer;
    private javax.swing.JTextField txfHandicapThreshold;
    private javax.swing.JTextField txfMMSDiffThreshold;
    private javax.swing.JTextField txfNbGames;
    private javax.swing.JTextField txfNbPairablePlayers;
    private javax.swing.JTextField txfNbUnPairablePlayers;
    private javax.swing.JTextField txfSearchPlayer;
    private javax.swing.JTextField txfUnbalancedWB;
    // End of variables declaration//GEN-END:variables

    private void tournamentChanged() {
        try {
            tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        updateAllViews();
    }

    private void updateAllViews() {
        try {
            if (!tournament.isOpen()) dispose();
            this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();
            setTitle("Games .. Pair. " + tournament.getFullName());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }

        int nbRounds = Gotha.MAX_NUMBER_OF_ROUNDS;
        try {
            nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.processedRoundNumber >= nbRounds) {
            JOptionPane.showMessageDialog(this, "The number of rounds has been modified."
                    + "\n" + "Current round will be consequently changed",
                    "Games Pair Message", JOptionPane.WARNING_MESSAGE);
            this.processedRoundNumber = nbRounds - 1;
        }

        this.pnlInternal.setVisible(true);
        updateComponents();

    }

    private void demandedDisplayedRoundNumberHasChanged(int demandedRN) {
        int numberOfRounds = 0;
        try {
            numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (demandedRN < 0 || demandedRN >= numberOfRounds) {
            spnRoundNumber.setValue(processedRoundNumber + 1);
            return;
        }
        if (demandedRN == processedRoundNumber) {
            return;
        }

        processedRoundNumber = demandedRN;
        updateAllViews();
    }
}
