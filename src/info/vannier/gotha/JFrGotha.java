/*
 * jFrGotha.java
 *
 */
package info.vannier.gotha;

import com.google.zxing.WriterException;
import info.vannier.qr.QR;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.*;

/**
 *
 * @author Luc Vannier
 */
public class JFrGotha extends javax.swing.JFrame {

    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    public static final int BIG_FRAME_WIDTH = 1000;
    public static final int BIG_FRAME_HEIGHT = 600;
    public static final int MEDIUM_FRAME_WIDTH = 796;
    public static final int MEDIUM_FRAME_HEIGHT = 553;
    public static final int SMALL_FRAME_WIDTH = 540;
    public static final int SMALL_FRAME_HEIGHT = 350;
    private static final int NUM_COL = 0;
    private static final int PL_COL = 1;
    private static final int NAME_COL = 2;
//    private static final int RANK_COL = 3;
    private static final int GRADE_COL = 3;
//    private static final int COUNTRY_COL = RANK_COL + 1;
    private static final int COUNTRY_COL = GRADE_COL + 1;
    private static final int CLUB_COL = COUNTRY_COL + 1;
    private static final int NBW_COL = CLUB_COL + 1;
    private static final int ROUND0_RESULT_COL = NBW_COL + 1;
    private static final int CRIT0_COL = ROUND0_RESULT_COL + Gotha.MAX_NUMBER_OF_ROUNDS;
    
    private static final int TEAM_PL_COL = 0;
    private static final int TEAM_NAME_COL = 1;
    private static final int TEAM_ROUND0_RESULT_COL = 2;
    private static final int TEAM_CRIT0_COL = TEAM_ROUND0_RESULT_COL + Gotha.MAX_NUMBER_OF_ROUNDS;
    // Teams Panel constants
    protected static final int TM_NUMBER_OF_COLS = 8;
    protected static final int TM_TEAM_NUMBER_COL = 0;
    protected static final int TM_TEAM_NAME_COL = 1;
    protected static final int TM_BOARD_NUMBER_COL = 2;
    protected static final int TM_PL_NAME_COL = 3;
    protected static final int TM_PL_COUNTRY_COL = 4;
    protected static final int TM_PL_CLUB_COL = 5;
    protected static final int TM_PL_RATING_COL = 6;
    protected static final int TM_PL_ROUNDS_COL = 7;
    /**
     * should stay between 0 and 9
     */
    private static final int MAX_NUMBER_OF_RECENT_TOURNAMENTS = 6;
    private int displayedRoundNumber = 0;
    private boolean bDisplayTemporaryParameterSet = false;
    private int[] displayedCriteria = new int[PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA];
    private int displayedTeamRoundNumber = 0;
    private boolean bDisplayTemporaryTeamParameterSet = false;
    private int[] displayedTeamCriteria = new int[PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA];
    /**
     * current Tournament
     */
    private TournamentInterface tournament = null;
    private long lastDisplayedStandingsUpdateTime = 0;
    private long lastDisplayedTeamsStandingsUpdateTime = 0;
    private ControlPanelTableCellRenderer cpTableCellRenderer = new ControlPanelTableCellRenderer();
    private TeamsPanelTableCellRenderer tpTableCellRenderer = new TeamsPanelTableCellRenderer();

    /**
     * Creates new form jFrGotha
     * @param tournament
     * @throws java.rmi.RemoteException
     */
    public JFrGotha(TournamentInterface tournament) throws RemoteException {
        this.tournament = tournament;

        initComponents();

        if (Gotha.runningMode == Gotha.RUNNING_MODE_SAL || Gotha.runningMode == Gotha.RUNNING_MODE_SRV) {
            ArrayList<String> alRT = getRecentTournamentsList();
            if (alRT.size() >= 1) {
                File f = new File(alRT.get(0));
                if (f.canRead()) {
                    try {
                        openTournament(f);
                    } catch (Exception ex) {
                        System.out.println("Problem opening file : " + f.getName());
                    }
                } else {
                    System.out.println("" + f.getName() + " cannot be read");
                }
            }
        }
        customInitComponents();
        setVisible(true);

        setupRefreshTimer();
    }

    private void setupRefreshTimer() {
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (tournament == null) {
                    return;
                }
                try {
                    boolean b = tournament.clockIn(Gotha.clientName);
                    if (!b && Gotha.runningMode == Gotha.RUNNING_MODE_CLI) {
                        JOptionPane.showMessageDialog(null, "Connection to Server has been reset for current tournament\nOpenGotha will stop",
                                "Message", JOptionPane.ERROR_MESSAGE);
                        exitOpenGotha();

                    }
                    if (tournament.getLastTournamentModificationTime() > lastComponentsUpdateTime) {
                        updateAllViews();
                    }
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Connection to Server has been reset\nOpenGotha will stop",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    exitOpenGotha();
                }
            }
        };
        new javax.swing.Timer((int) REFRESH_DELAY, taskPerformer).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpSystem = new javax.swing.ButtonGroup();
        dlgNew = new javax.swing.JDialog();
        pnlSystem = new javax.swing.JPanel();
        rdbMcMahon = new javax.swing.JRadioButton();
        rdbSwiss = new javax.swing.JRadioButton();
        rdbSwissCat = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        txfNumberOfRounds = new javax.swing.JTextField();
        lblRecommended = new javax.swing.JLabel();
        pnlTournamentDetails = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txfShortName = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txfName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txfLocation = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txfBeginDate = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txfEndDate = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txfDirector = new javax.swing.JTextField();
        btnDlgNewOK = new javax.swing.JButton();
        btnDlgNewCancel = new javax.swing.JButton();
        btnHelp = new javax.swing.JButton();
        grpPS = new javax.swing.ButtonGroup();
        dlgImportXML = new javax.swing.JDialog();
        btnDlgImportXMLOK = new javax.swing.JButton();
        btnDlgImportXMLCancel = new javax.swing.JButton();
        pnlObjectsToImport = new javax.swing.JPanel();
        chkPlayers = new javax.swing.JCheckBox();
        chkGames = new javax.swing.JCheckBox();
        chkTournamentParameters = new javax.swing.JCheckBox();
        chkTeams = new javax.swing.JCheckBox();
        chkClubsGroups = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        grpTeamPS = new javax.swing.ButtonGroup();
        tpnGotha = new javax.swing.JTabbedPane();
        pnlWelcome = new javax.swing.JPanel();
        lblTournamentPicture = new javax.swing.JLabel();
        lblFlowChart = new javax.swing.JLabel();
        pnlQRW = new javax.swing.JPanel();
        pnlControlPanel = new javax.swing.JPanel();
        pnlIntControlPanel = new javax.swing.JPanel();
        scpControlPanel = new javax.swing.JScrollPane();
        tblControlPanel = new javax.swing.JTable();
        lblWarningPRE = new javax.swing.JLabel();
        pnlQRCP = new javax.swing.JPanel();
        lblOGCP = new javax.swing.JLabel();
        pnlStandings = new javax.swing.JPanel();
        pnlIntStandings = new javax.swing.JPanel();
        lblStandingsAfter = new javax.swing.JLabel();
        pnlPS = new javax.swing.JPanel();
        rdbCurrentPS = new javax.swing.JRadioButton();
        rdbTemporaryPS = new javax.swing.JRadioButton();
        cbxCrit1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbxCrit2 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cbxCrit3 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cbxCrit4 = new javax.swing.JComboBox();
        scpStandings = new javax.swing.JScrollPane();
        tblStandings = new javax.swing.JTable();
        btnPrintStandings = new javax.swing.JButton();
        lblUpdateTime = new javax.swing.JLabel();
        spnRoundNumber = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        txfSearchPlayer = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlTeamsPanel = new javax.swing.JPanel();
        pnlIntTeamsPanel = new javax.swing.JPanel();
        scpTeamsPanel = new javax.swing.JScrollPane();
        tblTeamsPanel = new javax.swing.JTable();
        pnlTeamsStandings = new javax.swing.JPanel();
        pnlIntTeamsStandings = new javax.swing.JPanel();
        lblTeamsStandingsAfter = new javax.swing.JLabel();
        pnlTeamPS = new javax.swing.JPanel();
        rdbCurrentTeamPS = new javax.swing.JRadioButton();
        rdbTemporaryTeamPS = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        cbxTeamCrit1 = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        cbxTeamCrit2 = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        cbxTeamCrit3 = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cbxTeamCrit4 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        cbxTeamCrit5 = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        cbxTeamCrit6 = new javax.swing.JComboBox();
        scpTeamsStandings = new javax.swing.JScrollPane();
        tblTeamsStandings = new javax.swing.JTable();
        lblTeamUpdateTime = new javax.swing.JLabel();
        spnTeamRoundNumber = new javax.swing.JSpinner();
        btnPrintTeamsStandings = new javax.swing.JButton();
        mnuMain = new javax.swing.JMenuBar();
        mnuTournament = new javax.swing.JMenu();
        mniNew = new javax.swing.JMenuItem();
        mniOpen = new javax.swing.JMenuItem();
        mnuOpenRecent = new javax.swing.JMenu();
        mniSaveAs = new javax.swing.JMenuItem();
        mniSaveACopy = new javax.swing.JMenuItem();
        mniClose = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuImport = new javax.swing.JMenu();
        mniImportH9 = new javax.swing.JMenuItem();
        mniImportTou = new javax.swing.JMenuItem();
        mniImportWallist = new javax.swing.JMenuItem();
        mniImportVBS = new javax.swing.JMenuItem();
        mniImportXML = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mniExit = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        mniBuildTestTournament = new javax.swing.JMenuItem();
        mnuPlayers = new javax.swing.JMenu();
        mniPlayersManager = new javax.swing.JMenuItem();
        mniPlayersQuickCheck = new javax.swing.JMenuItem();
        mniUpdateRatings = new javax.swing.JMenuItem();
        mniMMGroups = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mniTeamsManager = new javax.swing.JMenuItem();
        mnuGames = new javax.swing.JMenu();
        mniPair = new javax.swing.JMenuItem();
        mniResults = new javax.swing.JMenuItem();
        mniRR = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mniTeamsPairing = new javax.swing.JMenuItem();
        mnuPublish = new javax.swing.JMenu();
        mniPublish = new javax.swing.JMenuItem();
        mnuOptions = new javax.swing.JMenu();
        mniTournamentOptions = new javax.swing.JMenuItem();
        mniGamesOptions = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mniPreferences = new javax.swing.JMenuItem();
        mnuTools = new javax.swing.JMenu();
        mniDiscardRounds = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniRMI = new javax.swing.JMenuItem();
        mniMemory = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mniExperimentalTools = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mniOpenGothaHelp = new javax.swing.JMenuItem();
        mniHelpAbout = new javax.swing.JMenuItem();

        dlgNew.getContentPane().setLayout(null);

        pnlSystem.setBorder(javax.swing.BorderFactory.createTitledBorder("System"));
        pnlSystem.setLayout(null);

        grpPS.add(rdbMcMahon);
        rdbMcMahon.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbMcMahon.setText("McMahon");
        rdbMcMahon.setToolTipText("Players will be paired according to their rank! The winner will be the strongest");
        rdbMcMahon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbMcMahon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSystem.add(rdbMcMahon);
        rdbMcMahon.setBounds(50, 30, 170, 13);

        grpPS.add(rdbSwiss);
        rdbSwiss.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbSwiss.setText("Swiss");
        rdbSwiss.setToolTipText("Good system for championships");
        rdbSwiss.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbSwiss.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSystem.add(rdbSwiss);
        rdbSwiss.setBounds(50, 60, 170, 13);

        grpPS.add(rdbSwissCat);
        rdbSwissCat.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbSwissCat.setText("Swiss with categories");
        rdbSwissCat.setToolTipText("Because of possible games with a big rank difference, this system is not usually recommended");
        rdbSwissCat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbSwissCat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSystem.add(rdbSwissCat);
        rdbSwissCat.setBounds(50, 90, 170, 13);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel13.setText("Number of rounds");
        pnlSystem.add(jLabel13);
        jLabel13.setBounds(50, 140, 120, 13);

        txfNumberOfRounds.setText("0");
        pnlSystem.add(txfNumberOfRounds);
        txfNumberOfRounds.setBounds(190, 140, 30, 20);

        lblRecommended.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblRecommended.setText("(recommended for ancilliary standings only)");
        pnlSystem.add(lblRecommended);
        lblRecommended.setBounds(60, 100, 240, 13);

        dlgNew.getContentPane().add(pnlSystem);
        pnlSystem.setBounds(410, 10, 300, 220);

        pnlTournamentDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Tournament details"));
        pnlTournamentDetails.setLayout(null);

        jLabel8.setText("Short name");
        pnlTournamentDetails.add(jLabel8);
        jLabel8.setBounds(10, 60, 80, 14);

        txfShortName.setText("tournamentshortname");
        txfShortName.setToolTipText("default file mame and RMI name.");
        pnlTournamentDetails.add(txfShortName);
        txfShortName.setBounds(100, 60, 180, 20);

        jLabel10.setText("Name");
        pnlTournamentDetails.add(jLabel10);
        jLabel10.setBounds(10, 30, 80, 14);

        txfName.setText("Tournament name");
        txfName.setToolTipText("tournament name as shown in headers and titles");
        pnlTournamentDetails.add(txfName);
        txfName.setBounds(100, 30, 180, 20);

        jLabel11.setText("Location");
        pnlTournamentDetails.add(jLabel11);
        jLabel11.setBounds(10, 90, 80, 14);

        txfLocation.setText("Location name");
        pnlTournamentDetails.add(txfLocation);
        txfLocation.setBounds(100, 90, 180, 20);

        jLabel12.setText("Begin date");
        pnlTournamentDetails.add(jLabel12);
        jLabel12.setBounds(10, 160, 80, 14);

        txfBeginDate.setText("yyyy-mm-dd");
        pnlTournamentDetails.add(txfBeginDate);
        txfBeginDate.setBounds(100, 160, 110, 20);

        jLabel19.setText("End date");
        pnlTournamentDetails.add(jLabel19);
        jLabel19.setBounds(10, 180, 80, 14);

        txfEndDate.setText("yyyy-mm-dd");
        pnlTournamentDetails.add(txfEndDate);
        txfEndDate.setBounds(100, 180, 110, 20);

        jLabel20.setText("Director");
        pnlTournamentDetails.add(jLabel20);
        jLabel20.setBounds(10, 120, 80, 14);

        txfDirector.setText("Director name");
        pnlTournamentDetails.add(txfDirector);
        txfDirector.setBounds(100, 120, 180, 20);

        dlgNew.getContentPane().add(pnlTournamentDetails);
        pnlTournamentDetails.setBounds(100, 10, 300, 220);

        btnDlgNewOK.setText("OK");
        btnDlgNewOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDlgNewOKActionPerformed(evt);
            }
        });
        dlgNew.getContentPane().add(btnDlgNewOK);
        btnDlgNewOK.setBounds(250, 260, 290, 30);

        btnDlgNewCancel.setText("Cancel");
        btnDlgNewCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDlgNewCancelActionPerformed(evt);
            }
        });
        dlgNew.getContentPane().add(btnDlgNewCancel);
        btnDlgNewCancel.setBounds(560, 260, 130, 30);

        btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/gothalogo16.jpg"))); // NOI18N
        btnHelp.setText("help");
        btnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpActionPerformed(evt);
            }
        });
        dlgNew.getContentPane().add(btnHelp);
        btnHelp.setBounds(100, 260, 130, 30);

        dlgImportXML.getContentPane().setLayout(null);

        btnDlgImportXMLOK.setText("OK");
        btnDlgImportXMLOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDlgImportXMLOKActionPerformed(evt);
            }
        });
        dlgImportXML.getContentPane().add(btnDlgImportXMLOK);
        btnDlgImportXMLOK.setBounds(130, 280, 120, 23);

        btnDlgImportXMLCancel.setText("Cancel");
        btnDlgImportXMLCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDlgImportXMLCancelActionPerformed(evt);
            }
        });
        dlgImportXML.getContentPane().add(btnDlgImportXMLCancel);
        btnDlgImportXMLCancel.setBounds(290, 280, 120, 23);

        pnlObjectsToImport.setBorder(javax.swing.BorderFactory.createTitledBorder("Objects to Import"));
        pnlObjectsToImport.setLayout(null);

        chkPlayers.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        chkPlayers.setSelected(true);
        chkPlayers.setText("Players");
        pnlObjectsToImport.add(chkPlayers);
        chkPlayers.setBounds(20, 20, 190, 21);

        chkGames.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        chkGames.setSelected(true);
        chkGames.setText("Games");
        pnlObjectsToImport.add(chkGames);
        chkGames.setBounds(20, 50, 190, 21);

        chkTournamentParameters.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        chkTournamentParameters.setText("Tournament Parameters");
        pnlObjectsToImport.add(chkTournamentParameters);
        chkTournamentParameters.setBounds(20, 80, 190, 21);

        chkTeams.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        chkTeams.setText("Teams and team parameters");
        pnlObjectsToImport.add(chkTeams);
        chkTeams.setBounds(20, 110, 190, 23);

        chkClubsGroups.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        chkClubsGroups.setSelected(true);
        chkClubsGroups.setText("Clubs Groups");
        chkClubsGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClubsGroupsActionPerformed(evt);
            }
        });
        pnlObjectsToImport.add(chkClubsGroups);
        chkClubsGroups.setBounds(20, 140, 190, 23);

        dlgImportXML.getContentPane().add(pnlObjectsToImport);
        pnlObjectsToImport.setBounds(140, 40, 260, 180);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Importation will merge information from xml file with information in current tournament.");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dlgImportXML.getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 230, 520, 14);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Before proceeding, make sure to have a good backup of your current tournament.");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dlgImportXML.getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 250, 520, 14);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gotha");
        setIconImage(getIconImage());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        tpnGotha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpnGothaStateChanged(evt);
            }
        });

        pnlWelcome.setLayout(null);

        lblTournamentPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTournamentPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/smartphone565X312.png"))); // NOI18N
        pnlWelcome.add(lblTournamentPicture);
        lblTournamentPicture.setBounds(77, 5, 615, 312);

        lblFlowChart.setBackground(new java.awt.Color(255, 255, 255));
        lblFlowChart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFlowChart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/flowchart.jpg"))); // NOI18N
        pnlWelcome.add(lblFlowChart);
        lblFlowChart.setBounds(20, 320, 760, 190);

        pnlQRW.setPreferredSize(new java.awt.Dimension(90, 90));
        pnlQRW.setLayout(null);
        pnlWelcome.add(pnlQRW);
        pnlQRW.setBounds(680, 210, 100, 100);

        tpnGotha.addTab("Welcome", pnlWelcome);

        pnlControlPanel.setLayout(null);

        pnlIntControlPanel.setLayout(null);

        scpControlPanel.setBorder(null);

        tblControlPanel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Round", "Participants", "Assigned players", "Entered results"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblControlPanel.setEnabled(false);
        tblControlPanel.setRowSelectionAllowed(false);
        scpControlPanel.setViewportView(tblControlPanel);

        pnlIntControlPanel.add(scpControlPanel);
        scpControlPanel.setBounds(180, 50, 370, 180);

        lblWarningPRE.setForeground(new java.awt.Color(255, 0, 0));
        pnlIntControlPanel.add(lblWarningPRE);
        lblWarningPRE.setBounds(10, 250, 510, 20);

        pnlQRCP.setLayout(null);
        pnlIntControlPanel.add(pnlQRCP);
        pnlQRCP.setBounds(650, 150, 100, 100);

        lblOGCP.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblOGCP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOGCP.setText("http://opengotha.info/tournaments/...");
        lblOGCP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlIntControlPanel.add(lblOGCP);
        lblOGCP.setBounds(190, 400, 360, 20);

        pnlControlPanel.add(pnlIntControlPanel);
        pnlIntControlPanel.setBounds(0, 0, 790, 470);

        tpnGotha.addTab("Control Panel", pnlControlPanel);

        pnlStandings.setLayout(null);

        pnlIntStandings.setLayout(null);

        lblStandingsAfter.setText("Standings after round");
        pnlIntStandings.add(lblStandingsAfter);
        lblStandingsAfter.setBounds(10, 40, 140, 14);

        pnlPS.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Placement parameter set"));
        pnlPS.setLayout(null);

        grpPS.add(rdbCurrentPS);
        rdbCurrentPS.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbCurrentPS.setText("use current set");
        rdbCurrentPS.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbCurrentPS.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbCurrentPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCurrentPSActionPerformed(evt);
            }
        });
        pnlPS.add(rdbCurrentPS);
        rdbCurrentPS.setBounds(10, 20, 170, 13);

        grpPS.add(rdbTemporaryPS);
        rdbTemporaryPS.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbTemporaryPS.setText("use temporary set");
        rdbTemporaryPS.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbTemporaryPS.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbTemporaryPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbTemporaryPSActionPerformed(evt);
            }
        });
        pnlPS.add(rdbTemporaryPS);
        rdbTemporaryPS.setBounds(10, 40, 170, 13);

        cbxCrit1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit1.setEnabled(false);
        cbxCrit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCritActionPerformed(evt);
            }
        });
        pnlPS.add(cbxCrit1);
        cbxCrit1.setBounds(60, 70, 120, 20);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel3.setText("Crit 1");
        pnlPS.add(jLabel3);
        jLabel3.setBounds(10, 70, 34, 13);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel4.setText("Crit 2");
        pnlPS.add(jLabel4);
        jLabel4.setBounds(10, 100, 34, 13);

        cbxCrit2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit2.setEnabled(false);
        cbxCrit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCritActionPerformed(evt);
            }
        });
        pnlPS.add(cbxCrit2);
        cbxCrit2.setBounds(60, 100, 120, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel5.setText("Crit 3");
        pnlPS.add(jLabel5);
        jLabel5.setBounds(10, 130, 34, 13);

        cbxCrit3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit3.setEnabled(false);
        cbxCrit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCritActionPerformed(evt);
            }
        });
        pnlPS.add(cbxCrit3);
        cbxCrit3.setBounds(60, 130, 120, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel6.setText("Crit 4");
        pnlPS.add(jLabel6);
        jLabel6.setBounds(10, 160, 34, 13);

        cbxCrit4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit4.setEnabled(false);
        cbxCrit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCritActionPerformed(evt);
            }
        });
        pnlPS.add(cbxCrit4);
        cbxCrit4.setBounds(60, 160, 120, 20);

        pnlIntStandings.add(pnlPS);
        pnlPS.setBounds(0, 70, 190, 210);

        tblStandings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Num", "Pl", "Name", "Gr", "Co", "Cl", "NBW", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12", "R13", "R14", "R15", "R16", "R17", "R18", "R19", "R20", "crit1", "crit2", "crit3", "crit4", "crit5", "crit6"
            }
        ));
        tblStandings.setEnabled(false);
        tblStandings.setRowSelectionAllowed(false);
        scpStandings.setViewportView(tblStandings);

        pnlIntStandings.add(scpStandings);
        scpStandings.setBounds(190, 10, 600, 500);

        btnPrintStandings.setText("Print...");
        btnPrintStandings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintStandingsActionPerformed(evt);
            }
        });
        pnlIntStandings.add(btnPrintStandings);
        btnPrintStandings.setBounds(0, 470, 190, 30);

        lblUpdateTime.setText("updated at : ");
        pnlIntStandings.add(lblUpdateTime);
        lblUpdateTime.setBounds(10, 300, 170, 14);

        spnRoundNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRoundNumberStateChanged(evt);
            }
        });
        pnlIntStandings.add(spnRoundNumber);
        spnRoundNumber.setBounds(150, 30, 40, 30);

        jLabel7.setText("Search for a player");
        pnlIntStandings.add(jLabel7);
        jLabel7.setBounds(10, 360, 150, 14);
        pnlIntStandings.add(txfSearchPlayer);
        txfSearchPlayer.setBounds(10, 380, 150, 20);

        btnSearch.setText("Search/Next");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnlIntStandings.add(btnSearch);
        btnSearch.setBounds(10, 410, 150, 23);

        pnlStandings.add(pnlIntStandings);
        pnlIntStandings.setBounds(0, 0, 790, 520);

        tpnGotha.addTab("Standings", pnlStandings);

        pnlTeamsPanel.setLayout(null);

        pnlIntTeamsPanel.setLayout(null);

        scpTeamsPanel.setBorder(null);

        tblTeamsPanel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Nr", "Team name", "Board", "Player name", "Co", "Club", "Rating", "Rounds"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTeamsPanel.setEnabled(false);
        tblTeamsPanel.setRowSelectionAllowed(false);
        scpTeamsPanel.setViewportView(tblTeamsPanel);

        pnlIntTeamsPanel.add(scpTeamsPanel);
        scpTeamsPanel.setBounds(180, 0, 480, 500);

        pnlTeamsPanel.add(pnlIntTeamsPanel);
        pnlIntTeamsPanel.setBounds(0, 0, 0, 0);

        tpnGotha.addTab("Teams Panel", pnlTeamsPanel);

        pnlTeamsStandings.setLayout(null);

        pnlIntTeamsStandings.setLayout(null);

        lblTeamsStandingsAfter.setText("Standings after round");
        pnlIntTeamsStandings.add(lblTeamsStandingsAfter);
        lblTeamsStandingsAfter.setBounds(10, 40, 140, 14);

        pnlTeamPS.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Team placement parameter set"));
        pnlTeamPS.setLayout(null);

        grpTeamPS.add(rdbCurrentTeamPS);
        rdbCurrentTeamPS.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbCurrentTeamPS.setText("use current set");
        rdbCurrentTeamPS.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbCurrentTeamPS.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbCurrentTeamPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCurrentTeamPSActionPerformed(evt);
            }
        });
        pnlTeamPS.add(rdbCurrentTeamPS);
        rdbCurrentTeamPS.setBounds(10, 20, 170, 13);

        grpTeamPS.add(rdbTemporaryTeamPS);
        rdbTemporaryTeamPS.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbTemporaryTeamPS.setText("use temporary set");
        rdbTemporaryTeamPS.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbTemporaryTeamPS.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbTemporaryTeamPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbTemporaryTeamPSActionPerformed(evt);
            }
        });
        pnlTeamPS.add(rdbTemporaryTeamPS);
        rdbTemporaryTeamPS.setBounds(10, 40, 170, 13);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel9.setText("Crit 1");
        pnlTeamPS.add(jLabel9);
        jLabel9.setBounds(10, 70, 34, 13);

        cbxTeamCrit1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit1.setEnabled(false);
        cbxTeamCrit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTeamCritActionPerformed(evt);
            }
        });
        pnlTeamPS.add(cbxTeamCrit1);
        cbxTeamCrit1.setBounds(60, 70, 120, 20);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel14.setText("Crit 2");
        pnlTeamPS.add(jLabel14);
        jLabel14.setBounds(10, 100, 34, 13);

        cbxTeamCrit2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit2.setEnabled(false);
        cbxTeamCrit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTeamCritActionPerformed(evt);
            }
        });
        pnlTeamPS.add(cbxTeamCrit2);
        cbxTeamCrit2.setBounds(60, 100, 120, 20);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel15.setText("Crit 3");
        pnlTeamPS.add(jLabel15);
        jLabel15.setBounds(10, 130, 34, 13);

        cbxTeamCrit3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit3.setEnabled(false);
        cbxTeamCrit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTeamCritActionPerformed(evt);
            }
        });
        pnlTeamPS.add(cbxTeamCrit3);
        cbxTeamCrit3.setBounds(60, 130, 120, 20);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel16.setText("Crit 4");
        pnlTeamPS.add(jLabel16);
        jLabel16.setBounds(10, 160, 34, 13);

        cbxTeamCrit4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit4.setEnabled(false);
        cbxTeamCrit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTeamCritActionPerformed(evt);
            }
        });
        pnlTeamPS.add(cbxTeamCrit4);
        cbxTeamCrit4.setBounds(60, 160, 120, 20);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel17.setText("Crit 5");
        pnlTeamPS.add(jLabel17);
        jLabel17.setBounds(10, 190, 34, 13);

        cbxTeamCrit5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit5.setEnabled(false);
        cbxTeamCrit5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTeamCritActionPerformed(evt);
            }
        });
        pnlTeamPS.add(cbxTeamCrit5);
        cbxTeamCrit5.setBounds(60, 190, 120, 20);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel18.setText("Crit 6");
        pnlTeamPS.add(jLabel18);
        jLabel18.setBounds(10, 220, 34, 13);

        cbxTeamCrit6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit6.setEnabled(false);
        cbxTeamCrit6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTeamCritActionPerformed(evt);
            }
        });
        pnlTeamPS.add(cbxTeamCrit6);
        cbxTeamCrit6.setBounds(60, 220, 120, 20);

        pnlIntTeamsStandings.add(pnlTeamPS);
        pnlTeamPS.setBounds(0, 70, 190, 260);

        tblTeamsStandings.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        tblTeamsStandings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Pl", "Name", "Rank", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12", "R13", "R14", "R15", "R16", "R17", "R18", "R19", "R20", "crit1", "crit2", "crit3", "crit4", "crit5", "crit6"
            }
        ));
        tblTeamsStandings.setEnabled(false);
        tblTeamsStandings.setRowSelectionAllowed(false);
        scpTeamsStandings.setViewportView(tblTeamsStandings);

        pnlIntTeamsStandings.add(scpTeamsStandings);
        scpTeamsStandings.setBounds(190, 10, 600, 500);

        lblTeamUpdateTime.setText("updated at : ");
        pnlIntTeamsStandings.add(lblTeamUpdateTime);
        lblTeamUpdateTime.setBounds(10, 360, 170, 14);

        spnTeamRoundNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnTeamRoundNumberStateChanged(evt);
            }
        });
        pnlIntTeamsStandings.add(spnTeamRoundNumber);
        spnTeamRoundNumber.setBounds(150, 30, 40, 30);

        btnPrintTeamsStandings.setText("Print...");
        btnPrintTeamsStandings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintTeamsStandingsActionPerformed(evt);
            }
        });
        pnlIntTeamsStandings.add(btnPrintTeamsStandings);
        btnPrintTeamsStandings.setBounds(0, 470, 190, 30);

        pnlTeamsStandings.add(pnlIntTeamsStandings);
        pnlIntTeamsStandings.setBounds(0, 0, 790, 570);

        tpnGotha.addTab("Teams Standings", pnlTeamsStandings);

        getContentPane().add(tpnGotha);
        tpnGotha.setBounds(10, 10, 970, 550);

        mnuMain.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mnuTournament.setText("Tournament");
        mnuTournament.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mniNew.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniNew.setText("New...");
        mniNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniNewActionPerformed(evt);
            }
        });
        mnuTournament.add(mniNew);

        mniOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mniOpen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniOpen.setText("Open...");
        mniOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOpenActionPerformed(evt);
            }
        });
        mnuTournament.add(mniOpen);

        mnuOpenRecent.setText("Open Recent ... ");
        mnuOpenRecent.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mnuTournament.add(mnuOpenRecent);

        mniSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mniSaveAs.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniSaveAs.setText("Save as ...");
        mniSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveAsActionPerformed(evt);
            }
        });
        mnuTournament.add(mniSaveAs);

        mniSaveACopy.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniSaveACopy.setText("Save a copy ...");
        mniSaveACopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveACopyActionPerformed(evt);
            }
        });
        mnuTournament.add(mniSaveACopy);

        mniClose.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniClose.setText("Close");
        mniClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCloseActionPerformed(evt);
            }
        });
        mnuTournament.add(mniClose);
        mnuTournament.add(jSeparator1);

        mnuImport.setText("Import ...");
        mnuImport.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniImportH9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniImportH9.setText("Import Players and Games from h9 file");
        mniImportH9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniImportH9ActionPerformed(evt);
            }
        });
        mnuImport.add(mniImportH9);

        mniImportTou.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniImportTou.setText("Import Players and Games from Tou file");
        mniImportTou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniImportTouActionPerformed(evt);
            }
        });
        mnuImport.add(mniImportTou);

        mniImportWallist.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniImportWallist.setText("Import Players and Games from Wallist file");
        mniImportWallist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniImportWallistActionPerformed(evt);
            }
        });
        mnuImport.add(mniImportWallist);

        mniImportVBS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniImportVBS.setText("Import Players From vBar-separated File");
        mniImportVBS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniImportVBSActionPerformed(evt);
            }
        });
        mnuImport.add(mniImportVBS);

        mniImportXML.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniImportXML.setText("Import Tournament from XML File");
        mniImportXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniImportXMLActionPerformed(evt);
            }
        });
        mnuImport.add(mniImportXML);

        mnuTournament.add(mnuImport);
        mnuTournament.add(jSeparator2);

        mniExit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniExit.setText("Exit");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuTournament.add(mniExit);
        mnuTournament.add(jSeparator4);

        mniBuildTestTournament.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniBuildTestTournament.setText("Build test tournament");
        mniBuildTestTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniBuildTestTournamentActionPerformed(evt);
            }
        });
        mnuTournament.add(mniBuildTestTournament);

        mnuMain.add(mnuTournament);

        mnuPlayers.setText("Players");
        mnuPlayers.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniPlayersManager.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mniPlayersManager.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniPlayersManager.setText("Players Manager");
        mniPlayersManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPlayersManagerActionPerformed(evt);
            }
        });
        mnuPlayers.add(mniPlayersManager);

        mniPlayersQuickCheck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        mniPlayersQuickCheck.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniPlayersQuickCheck.setText("Players Quick check");
        mniPlayersQuickCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPlayersQuickCheckActionPerformed(evt);
            }
        });
        mnuPlayers.add(mniPlayersQuickCheck);

        mniUpdateRatings.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniUpdateRatings.setText("Update ratings");
        mniUpdateRatings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniUpdateRatingsActionPerformed(evt);
            }
        });
        mnuPlayers.add(mniUpdateRatings);

        mniMMGroups.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniMMGroups.setText("McMahon groups");
        mniMMGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniMMGroupsActionPerformed(evt);
            }
        });
        mnuPlayers.add(mniMMGroups);
        mnuPlayers.add(jSeparator5);

        mniTeamsManager.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniTeamsManager.setText("Teams Manager");
        mniTeamsManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniTeamsManagerActionPerformed(evt);
            }
        });
        mnuPlayers.add(mniTeamsManager);

        mnuMain.add(mnuPlayers);

        mnuGames.setText("Games");
        mnuGames.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniPair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mniPair.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniPair.setText("Pair");
        mniPair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPairActionPerformed(evt);
            }
        });
        mnuGames.add(mniPair);

        mniResults.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mniResults.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniResults.setText("Results");
        mniResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniResultsActionPerformed(evt);
            }
        });
        mnuGames.add(mniResults);

        mniRR.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniRR.setText("Round-robin");
        mniRR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRRActionPerformed(evt);
            }
        });
        mnuGames.add(mniRR);
        mnuGames.add(jSeparator6);

        mniTeamsPairing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniTeamsPairing.setText("Teams Pairing");
        mniTeamsPairing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniTeamsPairingActionPerformed(evt);
            }
        });
        mnuGames.add(mniTeamsPairing);

        mnuMain.add(mnuGames);

        mnuPublish.setText("Publish");
        mnuPublish.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniPublish.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniPublish.setText("Publish ...");
        mniPublish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPublishActionPerformed(evt);
            }
        });
        mnuPublish.add(mniPublish);

        mnuMain.add(mnuPublish);

        mnuOptions.setText("Options");
        mnuOptions.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniTournamentOptions.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniTournamentOptions.setText("Tournament Options");
        mniTournamentOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniTournamentOptionsActionPerformed(evt);
            }
        });
        mnuOptions.add(mniTournamentOptions);

        mniGamesOptions.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniGamesOptions.setText("Games Options");
        mniGamesOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniGamesOptionsActionPerformed(evt);
            }
        });
        mnuOptions.add(mniGamesOptions);
        mnuOptions.add(jSeparator7);

        mniPreferences.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniPreferences.setText("Preferences");
        mniPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPreferencesActionPerformed(evt);
            }
        });
        mnuOptions.add(mniPreferences);

        mnuMain.add(mnuOptions);

        mnuTools.setText("Tools");
        mnuTools.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniDiscardRounds.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniDiscardRounds.setText("Discard rounds");
        mniDiscardRounds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDiscardRoundsActionPerformed(evt);
            }
        });
        mnuTools.add(mniDiscardRounds);
        mnuTools.add(jSeparator3);

        mniRMI.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniRMI.setText("RMI Manager");
        mniRMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRMIActionPerformed(evt);
            }
        });
        mnuTools.add(mniRMI);

        mniMemory.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniMemory.setText("Memory Manager");
        mniMemory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniMemoryActionPerformed(evt);
            }
        });
        mnuTools.add(mniMemory);
        mnuTools.add(jSeparator8);

        mniExperimentalTools.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniExperimentalTools.setText("Experimental tools");
        mniExperimentalTools.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExperimentalToolsActionPerformed(evt);
            }
        });
        mnuTools.add(mniExperimentalTools);

        mnuMain.add(mnuTools);

        mnuHelp.setText("Help");
        mnuHelp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniOpenGothaHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        mniOpenGothaHelp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniOpenGothaHelp.setText("OpenGotha help");
        mniOpenGothaHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOpenGothaHelpActionPerformed(evt);
            }
        });
        mnuHelp.add(mniOpenGothaHelp);

        mniHelpAbout.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        mniHelpAbout.setText("About OpenGotha");
        mniHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniHelpAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mniHelpAbout);

        mnuMain.add(mnuHelp);

        setJMenuBar(mnuMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mniGamesOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniGamesOptionsActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JFrame jfr = new JFrGamesOptions(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniGamesOptionsActionPerformed

    private void btnPrintStandingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintStandingsActionPerformed
        TournamentParameterSet tps = null;
        try {
            tps = tournament.getTournamentParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        TournamentParameterSet printTPS = new TournamentParameterSet(tps);
        PlacementParameterSet printPPS = printTPS.getPlacementParameterSet();
        printPPS.setPlaCriteria(displayedCriteria);
        TournamentPrinting.printStandings(tournament, printTPS, this.displayedRoundNumber);
        }//GEN-LAST:event_btnPrintStandingsActionPerformed

    private void mniMMGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniMMGroupsActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (tournament.tournamentType() != TournamentParameterSet.TYPE_MCMAHON) {
                JOptionPane.showMessageDialog(this, "McMahon Groups are relevant only in McMahon tournaments", "Message", JOptionPane.ERROR_MESSAGE);
                return;

            }
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            JFrame jfr = new JFrPlayersMMG(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniMMGroupsActionPerformed

    private void mniBuildTestTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniBuildTestTournamentActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrBuildTestTournament(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniBuildTestTournamentActionPerformed

    private void mniCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCloseActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Before carrying on, is there something to save ?
        if (!saveCurrentTournamentIfNecessary()) {
            return;
        }
        closeTournament();
    }//GEN-LAST:event_mniCloseActionPerformed

    private void mniImportTouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniImportTouActionPerformed
        this.importPlainFile("tou");
    }//GEN-LAST:event_mniImportTouActionPerformed

    private void btnDlgNewOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDlgNewOKActionPerformed
        int system = TournamentParameterSet.TYPE_MCMAHON;
        if (this.rdbMcMahon.isSelected()) {
            system = TournamentParameterSet.TYPE_MCMAHON;
        }
        if (this.rdbSwiss.isSelected()) {
            system = TournamentParameterSet.TYPE_SWISS;
        }
        if (this.rdbSwissCat.isSelected()) {
            system = TournamentParameterSet.TYPE_SWISSCAT;
        }

        TournamentParameterSet tps = new TournamentParameterSet();

        Date beginDate = new Date();
        try {
            beginDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.txfBeginDate.getText());
        } catch (ParseException ex) {
            beginDate = new java.util.Date();
        }
        Date endDate = new Date();
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.txfEndDate.getText());
        } catch (ParseException ex) {
            endDate = new java.util.Date();
        }

        int nbRounds = 5;
        try {
            nbRounds = Integer.parseInt(this.txfNumberOfRounds.getText());
        } catch (NumberFormatException ex) {
            nbRounds = 5;
        }
        if (nbRounds < 0) {
            nbRounds = 1;
        }
        if (nbRounds > Gotha.MAX_NUMBER_OF_ROUNDS) {
            nbRounds = Gotha.MAX_NUMBER_OF_ROUNDS;
        }

        tps.initBase(this.txfShortName.getText(), this.txfName.getText(),
                this.txfLocation.getText(), this.txfDirector.getText(),
                beginDate, endDate,
                nbRounds, 1); // numberOfCategories will be set by initForXX

        switch (system) {
            case TournamentParameterSet.TYPE_MCMAHON:
                tps.initForMM();
                break;
            case TournamentParameterSet.TYPE_SWISS:
                tps.initForSwiss();
                break;
            case TournamentParameterSet.TYPE_SWISSCAT:
                tps.initForSwissCat();
                break;
            default:
                tps.initForMM();

        }

        TeamTournamentParameterSet ttps = new TeamTournamentParameterSet();
        ttps.init();

        // close previous Tournament if necessary
        closeTournament();

        try {
            tournament = new Tournament();
            tournament.setTournamentParameterSet(tps);
            tournament.setTeamTournamentParameterSet(ttps);
            this.lastDisplayedStandingsUpdateTime = 0;
            this.lastDisplayedTeamsStandingsUpdateTime = 0;
            this.tournamentChanged();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        // If we are in Server mode, then rebind tournament
        if (Gotha.runningMode == Gotha.RUNNING_MODE_SRV) {
            GothaRMIServer.addTournament(tournament);
        }

        dlgNew.dispose();
    }//GEN-LAST:event_btnDlgNewOKActionPerformed

    private void btnDlgNewCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDlgNewCancelActionPerformed
        dlgNew.dispose();
    }//GEN-LAST:event_btnDlgNewCancelActionPerformed

    private void mniTournamentOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniTournamentOptionsActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrTournamentOptions(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniTournamentOptionsActionPerformed

    private void mniNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniNewActionPerformed
        // Before carrying on, is there something to save ?
        if (!saveCurrentTournamentIfNecessary()) {
            return;
        }
        int w = JFrGotha.MEDIUM_FRAME_WIDTH;
        int h = JFrGotha.SMALL_FRAME_HEIGHT;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dlgNew.setBounds((dim.width - w) / 2, (dim.height - h) / 2, w, h);
        dlgNew.setTitle("Create a new tournament");
        dlgNew.setIconImage(Gotha.getIconImage());


        this.rdbMcMahon.setSelected(true);
        this.txfNumberOfRounds.setText("5");
        this.txfBeginDate.setText(new java.util.Date().toString());
        this.txfBeginDate.setText((new SimpleDateFormat("yyyy-MM-dd")).format(new java.util.Date()));
        this.txfEndDate.setText(new java.util.Date().toString());
        this.txfEndDate.setText((new SimpleDateFormat("yyyy-MM-dd")).format(new java.util.Date()));

        this.txfName.selectAll();
        this.txfShortName.selectAll();
        this.txfLocation.selectAll();
        this.txfBeginDate.selectAll();
        this.txfNumberOfRounds.selectAll();

        dlgNew.setVisible(true);

    }//GEN-LAST:event_mniNewActionPerformed

    private void tpnGothaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpnGothaStateChanged
        updateAllViews();
    }//GEN-LAST:event_tpnGothaStateChanged

    private void cbxCritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxCritActionPerformed
        // In order to avoid useless updates, ...
        if (!this.cbxCrit1.isEnabled()) {
            return;
        }

        try {
            updateDisplayCriteria();
            updateStandingsComponents();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cbxCritActionPerformed

    private void rdbTemporaryPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbTemporaryPSActionPerformed
        updateAllViews();

    }//GEN-LAST:event_rdbTemporaryPSActionPerformed

    private void rdbCurrentPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCurrentPSActionPerformed
        updateAllViews();

    }//GEN-LAST:event_rdbCurrentPSActionPerformed

    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed
        exitOpenGotha();
    }//GEN-LAST:event_mniExitActionPerformed

    private void mniResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniResultsActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrGamesResults(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_mniResultsActionPerformed

    private void mniPairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPairActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrGamesPair(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_mniPairActionPerformed

    private void mniPlayersQuickCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPlayersQuickCheckActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrPlayersQuickCheck(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniPlayersQuickCheckActionPerformed

    private void customInitComponents() {
        int w = JFrGotha.BIG_FRAME_WIDTH;
        int h = JFrGotha.BIG_FRAME_HEIGHT;
        int y = 100;

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - w) / 2, y, w, h);
        setIconImage(Gotha.getIconImage());

        tpnGotha.setBounds(0, 0, w - 6, h - 54);

        int wTP = lblTournamentPicture.getWidth();
        lblTournamentPicture.setLocation((w - wTP) / 2, 5);
        int wFC = lblFlowChart.getWidth();
        int yFlowCart = lblTournamentPicture.getY() + lblTournamentPicture.getHeight() + 10;
        lblFlowChart.setLocation((w - wFC) / 2, yFlowCart);
        
        int wPNLQRW = this.pnlQRW.getWidth();
        int hPNLQRW = this.pnlQRW.getHeight();
        int xPNLQRW = lblFlowChart.getX() + lblFlowChart.getWidth() - wPNLQRW;
        int yPNLQRW = lblFlowChart.getY() - hPNLQRW;

        this.pnlQRW.setLocation(xPNLQRW, yPNLQRW);
        
        this.pnlIntControlPanel.setBounds(0, 0, w - 10, h - 30);
        int wCP = scpControlPanel.getWidth();
        this.scpControlPanel.setLocation((w - wCP) / 2, 100);

        int wQR = this.pnlQRCP.getWidth();
        this.pnlQRCP.setLocation((w - wQR) / 2, 300);
        int wOG = this.lblOGCP.getWidth();
        this.lblOGCP.setLocation((w - wOG) / 2, 395);
        
        this.pnlIntTeamsPanel.setBounds(0, 0, w - 10, h - 30);
        int wTeamsP = scpTeamsPanel.getWidth();
        this.scpTeamsPanel.setLocation((w - wTeamsP) / 2, 10);

        this.pnlIntStandings.setBounds(0, 0, w - 10, h - 30);
        this.scpStandings.setBounds(190, 10, w - 200, h - 100);
        this.pnlIntTeamsStandings.setBounds(0, 0, w - 10, h - 30);
        this.scpTeamsStandings.setBounds(190, 10, w - 200, h - 100);

        updateTitle();

        switch (Gotha.runningMode) {
            case Gotha.RUNNING_MODE_SAL:
                updateOpenRecentMenu();
                break;
            case Gotha.RUNNING_MODE_SRV:
                updateOpenRecentMenu();
                break;
            case Gotha.RUNNING_MODE_CLI:
                this.mniSaveAs.setVisible(false);
                this.mniSaveAs.setVisible(false);
                this.mniNew.setVisible(false);
                this.mniOpen.setVisible(false);
                this.mnuOpenRecent.setVisible(false);
                this.mniClose.setVisible(false);
                this.mnuImport.setVisible(false);
                this.mniBuildTestTournament.setVisible(false);
                this.mnuTools.setVisible(false);
                this.mniRMI.setVisible(false);
                break;
        }

        try {
            initCriteriaAndStandingsComponents();
            initControlPanelComponents();
            initTeamsPanelComponents();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        getRootPane().setDefaultButton(btnSearch);
        
        if (tournament == null){
            this.tpnGotha.setSelectedComponent(this.pnlWelcome);
        }
        else{
            this.tpnGotha.setSelectedComponent(this.pnlControlPanel);
        }
    }

    /**
     * Get recent tournaments list from Preferences. Recent tournaments names are
     * supposed to look like "recentTournamentx" where x = 0 to 9
     */
    private ArrayList<String> getRecentTournamentsList() {
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);

        ArrayList<String> alS = new ArrayList<String>();
        for (int numRT = 0; numRT < MAX_NUMBER_OF_RECENT_TOURNAMENTS; numRT++) {
            String strK = "recentTournament" + numRT;
            String strRT = gothaPrefs.get(strK, "");
            if (strRT.compareTo("") != 0) {
                alS.add(strRT);
            }
        }
        return alS;
    }

    private void removeAllRecentTournament() {
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        int nbRT = MAX_NUMBER_OF_RECENT_TOURNAMENTS;
        for (int numRT = 0; numRT < nbRT; numRT++) {
            String strK = "recentTournament" + numRT;
            gothaPrefs.remove(strK);
        }
        this.updateOpenRecentMenu();
    }

    /**
     * Insert file name into Preferences
     */
    private void addRecentTournament(String strRecentTournamentFileName) {
        ArrayList<String> alS = getRecentTournamentsList();
        // avoid double
        alS.remove(strRecentTournamentFileName);
        alS.add(0, strRecentTournamentFileName);
        // avoid null
        alS.remove("null");

        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        int nbRT = Math.min(alS.size(), MAX_NUMBER_OF_RECENT_TOURNAMENTS);
        removeAllRecentTournament();
        for (int numRT = 0; numRT < nbRT; numRT++) {
            String strK = "recentTournament" + numRT;
            String strRT = alS.get(numRT);
            gothaPrefs.put(strK, strRT);
        }

        this.updateOpenRecentMenu();
    }

    private void updateOpenRecentMenu() {
        ArrayList<String> alRT = this.getRecentTournamentsList();
        this.mnuOpenRecent.removeAll();
        mnuOpenRecent.setEnabled(true);
        if (alRT.isEmpty()) {
            mnuOpenRecent.setEnabled(false);
        }
        for (int numRT = 0; numRT < alRT.size(); numRT++) {
            final String strFile = alRT.get(numRT);
            JMenuItem mni = new JMenuItem(strFile);
            mni.setFont(new java.awt.Font("Arial", 0, 11));
            mni.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    File f = null;
                    try {
                        f = new File(strFile);
                        openTournament(f);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, " File not found", "Message", JOptionPane.ERROR_MESSAGE);
                        // Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        String strMessage = "Some problem occured with file : " + f.getName();
                        strMessage += "\nThe content of this file does not comply with OpenGotha Data version " + Gotha.GOTHA_DATA_VERSION;
                        strMessage += "\nHint : Read the Compatibility issues in the OpenGotha help";
                        strMessage += "\n\nThe tournament has not been opened";
                        JOptionPane.showMessageDialog(JFrGotha.this, strMessage, "Message", JOptionPane.ERROR_MESSAGE);
                        JFrGotha.this.removeAllRecentTournament();
                    }

                }
            });
            this.mnuOpenRecent.add(mni);
        }
    }

    private void initCriteriaAndStandingsComponents() throws RemoteException {
        cbxCrit1.setModel(new DefaultComboBoxModel(PlacementParameterSet.criteriaLongNames()));
        cbxCrit2.setModel(new DefaultComboBoxModel(PlacementParameterSet.criteriaLongNames()));
        cbxCrit3.setModel(new DefaultComboBoxModel(PlacementParameterSet.criteriaLongNames()));
        cbxCrit4.setModel(new DefaultComboBoxModel(PlacementParameterSet.criteriaLongNames()));

        cbxTeamCrit1.setModel(new DefaultComboBoxModel(TeamPlacementParameterSet.criteriaLongNames()));
        cbxTeamCrit2.setModel(new DefaultComboBoxModel(TeamPlacementParameterSet.criteriaLongNames()));
        cbxTeamCrit3.setModel(new DefaultComboBoxModel(TeamPlacementParameterSet.criteriaLongNames()));
        cbxTeamCrit4.setModel(new DefaultComboBoxModel(TeamPlacementParameterSet.criteriaLongNames()));
        cbxTeamCrit5.setModel(new DefaultComboBoxModel(TeamPlacementParameterSet.criteriaLongNames()));
        cbxTeamCrit6.setModel(new DefaultComboBoxModel(TeamPlacementParameterSet.criteriaLongNames()));

        if (tournament == null) {
            return;
        }
        try {
            displayedRoundNumber = tournament.presumablyCurrentRoundNumber();
            displayedTeamRoundNumber = tournament.presumablyCurrentRoundNumber();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        updateDisplayCriteria();
        updateDisplayTeamCriteria();
        DefaultTableModel model = (DefaultTableModel) tblStandings.getModel();
        model.setColumnCount(ROUND0_RESULT_COL + Gotha.MAX_NUMBER_OF_ROUNDS + PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA);
        model = (DefaultTableModel) tblTeamsStandings.getModel();
        model.setColumnCount(TEAM_ROUND0_RESULT_COL + Gotha.MAX_NUMBER_OF_ROUNDS + TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA);

        // Set the renderer for tblStandings
        tblStandings.setDefaultRenderer(Object.class, new StandingsTableCellRenderer());
        updateStandingsComponents();
        // Set the renderer for tblTeamsStandings
        tblTeamsStandings.setDefaultRenderer(Object.class, new StandingsTableCellRenderer());
        updateTeamsStandingsComponents();

    }

    private void updateTitle() {
        String strTitle = Gotha.getGothaVersionnedName() + " ";
        switch (Gotha.runningMode) {
            case Gotha.RUNNING_MODE_SRV:
                strTitle += "Server. " + " ";
                break;
            case Gotha.RUNNING_MODE_CLI:
                strTitle += "Client. " + " ";
        }

        if (tournament != null) {
            try {
                strTitle += tournament.getTournamentParameterSet().getGeneralParameterSet().getName();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setTitle(strTitle);
    }

    private void initControlPanelComponents() throws RemoteException {
        // Widths
        TableColumnModel tcm = this.tblControlPanel.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(30);
        tcm.getColumn(1).setPreferredWidth(80);
        tcm.getColumn(2).setPreferredWidth(80);
        tcm.getColumn(3).setPreferredWidth(100);

        // Headers
        JFrGotha.formatHeader(this.tblControlPanel, 0, "Round", JLabel.RIGHT);
        JFrGotha.formatHeader(this.tblControlPanel, 1, "Participants", JLabel.CENTER);
        JFrGotha.formatHeader(this.tblControlPanel, 2, "Assigned players", JLabel.CENTER);
        JFrGotha.formatHeader(this.tblControlPanel, 3, "Entered results", JLabel.CENTER);

        // Set the renderer for tblControlPanel
        tblControlPanel.setDefaultRenderer(Object.class, this.cpTableCellRenderer);

        updateControlPanel();
    }

    private void initTeamsPanelComponents() throws RemoteException {
        // Widths
        TableColumnModel tcm = this.tblTeamsPanel.getColumnModel();
        tcm.getColumn(TM_TEAM_NUMBER_COL).setPreferredWidth(20);
        tcm.getColumn(TM_TEAM_NAME_COL).setPreferredWidth(100);
        tcm.getColumn(TM_BOARD_NUMBER_COL).setPreferredWidth(20);
        tcm.getColumn(TM_PL_NAME_COL).setPreferredWidth(120);
        tcm.getColumn(TM_PL_COUNTRY_COL).setPreferredWidth(30);
        tcm.getColumn(TM_PL_CLUB_COL).setPreferredWidth(30);
        tcm.getColumn(TM_PL_RATING_COL).setPreferredWidth(40);
        tcm.getColumn(TM_PL_ROUNDS_COL).setPreferredWidth(120);

        // Headers
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_TEAM_NUMBER_COL, "Nr", JLabel.RIGHT);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_TEAM_NAME_COL, "Team name", JLabel.LEFT);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_BOARD_NUMBER_COL, "Board", JLabel.RIGHT);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_PL_NAME_COL, "Player name", JLabel.LEFT);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_PL_COUNTRY_COL, "Co", JLabel.CENTER);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_PL_CLUB_COL, "Club", JLabel.CENTER);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_PL_RATING_COL, "Rating", JLabel.CENTER);
        JFrGotha.formatHeader(this.tblTeamsPanel, TM_PL_ROUNDS_COL, "Rounds", JLabel.LEFT);

        // Set the renderer for tblControlPanel
        tblTeamsPanel.setDefaultRenderer(Object.class, this.tpTableCellRenderer);

        updateTeamsPanel();
    }

    private void updateDisplayCriteria() throws RemoteException {
        // update bDisplayTemporaryParameterSet
        bDisplayTemporaryParameterSet = (this.grpPS.getSelection() == this.rdbTemporaryPS.getModel());

        if (bDisplayTemporaryParameterSet) {
            displayedCriteria[0] = PlacementParameterSet.criterionUID((String) cbxCrit1.getModel().getSelectedItem());
            displayedCriteria[1] = PlacementParameterSet.criterionUID((String) cbxCrit2.getModel().getSelectedItem());
            displayedCriteria[2] = PlacementParameterSet.criterionUID((String) cbxCrit3.getModel().getSelectedItem());
            displayedCriteria[3] = PlacementParameterSet.criterionUID((String) cbxCrit4.getModel().getSelectedItem());
        } else {
            PlacementParameterSet displayedPPS = null;
            if (tournament != null) {
                displayedPPS = tournament.getTournamentParameterSet().getPlacementParameterSet();
                displayedCriteria[0] = displayedPPS.getPlaCriteria()[0];
                displayedCriteria[1] = displayedPPS.getPlaCriteria()[1];
                displayedCriteria[2] = displayedPPS.getPlaCriteria()[2];
                displayedCriteria[3] = displayedPPS.getPlaCriteria()[3];
            }
        }
    }

    private void updateDisplayTeamCriteria() throws RemoteException {
        // update bDisplayTemporaryTeamParameterSet
        bDisplayTemporaryTeamParameterSet = (this.grpTeamPS.getSelection() == this.rdbTemporaryTeamPS.getModel());

        if (bDisplayTemporaryTeamParameterSet) {
            displayedTeamCriteria[0] = TeamPlacementParameterSet.criterionUID((String) cbxTeamCrit1.getModel().getSelectedItem());
            displayedTeamCriteria[1] = TeamPlacementParameterSet.criterionUID((String) cbxTeamCrit2.getModel().getSelectedItem());
            displayedTeamCriteria[2] = TeamPlacementParameterSet.criterionUID((String) cbxTeamCrit3.getModel().getSelectedItem());
            displayedTeamCriteria[3] = TeamPlacementParameterSet.criterionUID((String) cbxTeamCrit4.getModel().getSelectedItem());
            displayedTeamCriteria[4] = TeamPlacementParameterSet.criterionUID((String) cbxTeamCrit5.getModel().getSelectedItem());
            displayedTeamCriteria[5] = TeamPlacementParameterSet.criterionUID((String) cbxTeamCrit6.getModel().getSelectedItem());
        } else {
            TeamPlacementParameterSet displayedTeamPPS = null;
            if (tournament != null) {
                displayedTeamPPS = tournament.getTeamTournamentParameterSet().getTeamPlacementParameterSet();
                displayedTeamCriteria[0] = displayedTeamPPS.getPlaCriteria()[0];
                displayedTeamCriteria[1] = displayedTeamPPS.getPlaCriteria()[1];
                displayedTeamCriteria[2] = displayedTeamPPS.getPlaCriteria()[2];
                displayedTeamCriteria[3] = displayedTeamPPS.getPlaCriteria()[3];
                displayedTeamCriteria[4] = displayedTeamPPS.getPlaCriteria()[4];
                displayedTeamCriteria[5] = displayedTeamPPS.getPlaCriteria()[5];
            }
        }
    }

    private void updateStandingsComponents() throws RemoteException {
        if (tournament == null) {
            this.pnlIntStandings.setVisible(false);
            return;
        }

        if (this.tpnGotha.getSelectedComponent() != pnlStandings) {
            return;
        }
        this.pnlIntStandings.setVisible(true);
        int nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (displayedRoundNumber > nbRounds - 1) {
            displayedRoundNumber = nbRounds - 1;
        }
        this.spnRoundNumber.setValue(displayedRoundNumber + 1);

        this.rdbCurrentPS.getModel().setSelected(!bDisplayTemporaryParameterSet);
        this.cbxCrit1.setEnabled(bDisplayTemporaryParameterSet);
        this.cbxCrit2.setEnabled(bDisplayTemporaryParameterSet);
        this.cbxCrit3.setEnabled(bDisplayTemporaryParameterSet);
        this.cbxCrit4.setEnabled(bDisplayTemporaryParameterSet);

        this.cbxCrit1.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[0]));
        this.cbxCrit2.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[1]));
        this.cbxCrit3.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[2]));
        this.cbxCrit4.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[3]));

        // Define displayedTPS
        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        TournamentParameterSet displayedTPS = new TournamentParameterSet(tps);
        PlacementParameterSet displayedPPS = displayedTPS.getPlacementParameterSet();
        displayedPPS.setPlaCriteria(displayedCriteria);

        int gameFormat = tps.getDPParameterSet().getGameFormat();
        int numberOfDisplayedRounds = 8;
        if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) {
            numberOfDisplayedRounds = 10;
        }

        lastDisplayedStandingsUpdateTime = tournament.getCurrentTournamentTime();
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = new ArrayList<ScoredPlayer>();
        try {
            alOrderedScoredPlayers = tournament.orderedScoredPlayersList(displayedRoundNumber, displayedTPS.getPlacementParameterSet());
            // Eliminate non-players
            alOrderedScoredPlayers = eliminateNonImpliedPlayers(alOrderedScoredPlayers);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean bFull = true;
        if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) {
            bFull = false;
        }
        String[][] hG = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, displayedRoundNumber, displayedTPS, bFull);

        tblStandings.clearSelection();
        tblStandings.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableColumnModel columnModel = (DefaultTableColumnModel) tblStandings.getColumnModel();

        String strNumHeader = "Num";
        if (!tps.getDPParameterSet().isDisplayNumCol()) {
            strNumHeader = "";
        }
        columnModel.getColumn(NUM_COL).setHeaderValue(strNumHeader);

        String strPlHeader = "Pl";
        if (!tps.getDPParameterSet().isDisplayPlCol()) {
            strPlHeader = "";
        }
        columnModel.getColumn(PL_COL).setHeaderValue(strPlHeader);
        
        columnModel.getColumn(NAME_COL).setHeaderValue("Name");
//        columnModel.getColumn(RANK_COL).setHeaderValue("Rk");
       columnModel.getColumn(GRADE_COL).setHeaderValue("Gr");
        String strCoHeader = "Co";
        if (!tps.getDPParameterSet().isDisplayCoCol()) {
            strCoHeader = "";
        }
        columnModel.getColumn(COUNTRY_COL).setHeaderValue(strCoHeader);

        String strClHeader = "Cl";
        if (!tps.getDPParameterSet().isDisplayClCol()) {
            strClHeader = "";
        }
        columnModel.getColumn(CLUB_COL).setHeaderValue(strClHeader);
        
        columnModel.getColumn(NBW_COL).setHeaderValue("NBW");

        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            columnModel.getColumn(ROUND0_RESULT_COL + r).setHeaderValue("R" + (r + 1));
        }
        for (int c = 0; c < PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA; c++) {
            columnModel.getColumn(CRIT0_COL + c).setHeaderValue(PlacementParameterSet.criterionShortName(displayedCriteria[c]));
        }
        int numWidth = 30;
        if (!tps.getDPParameterSet().isDisplayNumCol()) {
            numWidth = 0;
        }
        columnModel.getColumn(NUM_COL).setPreferredWidth(numWidth);
        int plWidth = 30;
        if (!tps.getDPParameterSet().isDisplayPlCol()) {
            plWidth = 0;
        }
        columnModel.getColumn(PL_COL).setPreferredWidth(plWidth);
        int coWidth = 20;
        if (!tps.getDPParameterSet().isDisplayCoCol()) {
            coWidth = 0;
        }
        columnModel.getColumn(COUNTRY_COL).setPreferredWidth(coWidth);
        int clWidth = 30;
        if (!tps.getDPParameterSet().isDisplayClCol()) {
            clWidth = 0;
        }
        columnModel.getColumn(CLUB_COL).setPreferredWidth(clWidth);

        columnModel.getColumn(NAME_COL).setPreferredWidth(110);
//        columnModel.getColumn(RANK_COL).setPreferredWidth(30);
          columnModel.getColumn(GRADE_COL).setPreferredWidth(30);
        columnModel.getColumn(NBW_COL).setPreferredWidth(20);
        for (int r = 0; r <= displayedRoundNumber - numberOfDisplayedRounds; r++) {
            columnModel.getColumn(ROUND0_RESULT_COL + r).setMinWidth(2);
            columnModel.getColumn(ROUND0_RESULT_COL + r).setPreferredWidth(2);
        }
        for (int r = Math.max(displayedRoundNumber - numberOfDisplayedRounds + 1, 0); r <= displayedRoundNumber; r++) {
            int roundColWidth = 55;
            if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) {
                roundColWidth = 35;
            }
            columnModel.getColumn(ROUND0_RESULT_COL + r).setPreferredWidth(roundColWidth);
        }
        for (int r = displayedRoundNumber + 1; r <= Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            columnModel.getColumn(ROUND0_RESULT_COL + r).setMinWidth(0);
            columnModel.getColumn(ROUND0_RESULT_COL + r).setPreferredWidth(0);
        }

        for (int c = 0; c < PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA; c++) {
            if (displayedPPS.getPlaCriteria()[c] == PlacementParameterSet.PLA_CRIT_NUL) {
                columnModel.getColumn(CRIT0_COL + c).setMinWidth(0);
                columnModel.getColumn(CRIT0_COL + c).setPreferredWidth(0);
            } else {
                columnModel.getColumn(CRIT0_COL + c).setPreferredWidth(40);
            }
        }

        DefaultTableModel model = (DefaultTableModel) tblStandings.getModel();
        model.setRowCount(alOrderedScoredPlayers.size());
        String[] strPlace = ScoredPlayer.catPositionStrings(alOrderedScoredPlayers, displayedRoundNumber, displayedTPS);
        for (int iSP = 0; iSP < alOrderedScoredPlayers.size(); iSP++) {
            int iCol = 0;
            ScoredPlayer sp = alOrderedScoredPlayers.get(iSP);
            String strNum = "" + (iSP + 1);
            if (!tps.getDPParameterSet().isDisplayNumCol()) {
                strNum = "";
            }
            model.setValueAt(strNum, iSP, iCol++);
            
            String strPl = "" + strPlace[iSP];
            if (!tps.getDPParameterSet().isDisplayPlCol()) {
                strPl = "";
            }
            model.setValueAt("" + strPl, iSP, iCol++);
  
            model.setValueAt(sp.fullName(), iSP, iCol++);
                        
//            model.setValueAt(Player.convertIntToKD(sp.getRank()), iSP, iCol++);
            model.setValueAt(sp.getStrGrade(), iSP, iCol++);

            String strCo = sp.getCountry();
            if (!tps.getDPParameterSet().isDisplayCoCol()) {
                strCo = "";
            }
            model.setValueAt(strCo, iSP, iCol++);
            
            String strCl = sp.getClub();
            if (!tps.getDPParameterSet().isDisplayClCol()) {
                strCl = "";
            }
            model.setValueAt(strCl, iSP, iCol++);
           
            
            model.setValueAt(sp.formatScore(PlacementParameterSet.PLA_CRIT_NBW, this.displayedRoundNumber), iSP, iCol++);
            for (int r = 0; r <= displayedRoundNumber; r++) {
                model.setValueAt((hG[r][iSP]), iSP, iCol++);
            }
            for (int r = displayedRoundNumber + 1; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                model.setValueAt("", iSP, iCol++);
            }
            for (int c = 0; c < displayedCriteria.length; c++) {
                model.setValueAt(sp.formatScore(displayedCriteria[c], this.displayedRoundNumber), iSP, iCol++);
            }
        }

        java.util.Date dh = new java.util.Date(lastDisplayedStandingsUpdateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        String strTime = sdf.format(dh);
        lblUpdateTime.setText("updated at : " + strTime);
    }

    private void updateTeamsStandingsComponents() throws RemoteException {
        int numberOfDisplayedRounds = 9;
        if (tournament == null) {
            this.pnlIntTeamsStandings.setVisible(false);
            return;
        }
        if (tournament.teamsList().isEmpty()) {
            this.pnlIntTeamsStandings.setVisible(false);
            return;
        }

        if (this.tpnGotha.getSelectedComponent() != pnlTeamsStandings) {
            return;
        }
        this.pnlIntTeamsStandings.setVisible(true);
        int nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (this.displayedTeamRoundNumber > nbRounds - 1) {
            displayedTeamRoundNumber = nbRounds - 1;
        }
        this.spnTeamRoundNumber.setValue(displayedTeamRoundNumber + 1);

        this.rdbCurrentTeamPS.getModel().setSelected(!bDisplayTemporaryTeamParameterSet);
        this.cbxTeamCrit1.setEnabled(bDisplayTemporaryTeamParameterSet);
        this.cbxTeamCrit2.setEnabled(bDisplayTemporaryTeamParameterSet);
        this.cbxTeamCrit3.setEnabled(bDisplayTemporaryTeamParameterSet);
        this.cbxTeamCrit4.setEnabled(bDisplayTemporaryTeamParameterSet);
        this.cbxTeamCrit5.setEnabled(bDisplayTemporaryTeamParameterSet);
        this.cbxTeamCrit6.setEnabled(bDisplayTemporaryTeamParameterSet);

        this.cbxTeamCrit1.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedTeamCriteria[0]));
        this.cbxTeamCrit2.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedTeamCriteria[1]));
        this.cbxTeamCrit3.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedTeamCriteria[2]));
        this.cbxTeamCrit4.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedTeamCriteria[3]));
        this.cbxTeamCrit5.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedTeamCriteria[4]));
        this.cbxTeamCrit6.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedTeamCriteria[5]));

        // Define displayedTeamTPS
        TeamTournamentParameterSet ttps = tournament.getTeamTournamentParameterSet();
        TeamTournamentParameterSet displayedTeamTPS = new TeamTournamentParameterSet(ttps);
        TeamPlacementParameterSet displayedTeamPPS = displayedTeamTPS.getTeamPlacementParameterSet();
        displayedTeamPPS.setPlaCriteria(displayedTeamCriteria);


        lastDisplayedTeamsStandingsUpdateTime = tournament.getCurrentTournamentTime();
        ScoredTeamsSet sts = tournament.getAnUpToDateScoredTeamsSet(displayedTeamPPS, displayedTeamRoundNumber);
        ArrayList<ScoredTeam> alOrderedScoredTeams = sts.getOrderedScoredTeamsList();

        this.tblTeamsStandings.clearSelection();
        tblTeamsStandings.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableColumnModel columnModel = (DefaultTableColumnModel) tblTeamsStandings.getColumnModel();

        ((DefaultTableCellRenderer) tblTeamsStandings.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        columnModel.getColumn(TEAM_PL_COL).setHeaderValue("PL.");
        columnModel.getColumn(TEAM_NAME_COL).setHeaderValue("Name");

        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            columnModel.getColumn(TEAM_ROUND0_RESULT_COL + r).setHeaderValue("R" + (r + 1));
        }
        for (int c = 0; c < TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA; c++) {
            String strCrit = TeamPlacementParameterSet.criterionShortName(displayedTeamCriteria[c]);
            if (displayedTeamCriteria[c] == TeamPlacementParameterSet.TPL_CRIT_NUL) {
                strCrit = "";
            }
            TableColumn tc = columnModel.getColumn(TEAM_CRIT0_COL + c);
            tc.setHeaderValue(strCrit);
        }
        columnModel.getColumn(TEAM_PL_COL).setPreferredWidth(30);
        columnModel.getColumn(TEAM_NAME_COL).setPreferredWidth(110);

        for (int r = 0; r <= this.displayedTeamRoundNumber - numberOfDisplayedRounds; r++) {
            columnModel.getColumn(TEAM_ROUND0_RESULT_COL + r).setMinWidth(2);
            columnModel.getColumn(TEAM_ROUND0_RESULT_COL + r).setPreferredWidth(2);
        }
        for (int r = Math.max(displayedTeamRoundNumber - numberOfDisplayedRounds + 1, 0); r <= displayedTeamRoundNumber; r++) {
            columnModel.getColumn(TEAM_ROUND0_RESULT_COL + r).setPreferredWidth(40);
        }
        for (int r = displayedTeamRoundNumber + 1; r <= Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            columnModel.getColumn(TEAM_ROUND0_RESULT_COL + r).setMinWidth(0);
            columnModel.getColumn(TEAM_ROUND0_RESULT_COL + r).setPreferredWidth(0);
        }

        for (int c = 0; c < TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA; c++) {
            if (displayedTeamPPS.getPlaCriteria()[c] == TeamPlacementParameterSet.TPL_CRIT_NUL) {
                columnModel.getColumn(TEAM_CRIT0_COL + c).setMinWidth(0);
                columnModel.getColumn(TEAM_CRIT0_COL + c).setPreferredWidth(0);
            } else {
                columnModel.getColumn(TEAM_CRIT0_COL + c).setPreferredWidth(40);
            }
        }

        DefaultTableModel model = (DefaultTableModel) tblTeamsStandings.getModel();
        int nbTeams = alOrderedScoredTeams.size();
        model.setRowCount(nbTeams);
        for (int ist = 0; ist < nbTeams; ist++) {
            int iCol = 0;
            ScoredTeam st = alOrderedScoredTeams.get(ist);
            model.setValueAt("" + (ist + 1), ist, iCol++);
            model.setValueAt(st.getTeamName(), ist, iCol++);
            for (int r = 0; r <= displayedTeamRoundNumber; r++) {
                model.setValueAt(sts.getHalfMatchString(st, r), ist, iCol++);
            }
            for (int r = displayedTeamRoundNumber + 1; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                model.setValueAt("", ist, iCol++);
            }
            for (int ic = 0; ic < this.displayedTeamCriteria.length; ic++) {
                int crit = this.displayedTeamCriteria[ic];
                int coef = TeamPlacementParameterSet.criterionCoef(crit);
                String strCritValue = Gotha.formatFractNumber(st.getCritValue(ic), coef);
                model.setValueAt(strCritValue, ist, TEAM_CRIT0_COL + ic);
            }
        }

        java.util.Date dh = new java.util.Date(lastDisplayedTeamsStandingsUpdateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        String strTime = sdf.format(dh);
        lblTeamUpdateTime.setText("updated at : " + strTime);
    }

    private ArrayList<ScoredPlayer> eliminateNonImpliedPlayers(ArrayList<ScoredPlayer> alSP) {
        HashMap<Player, Boolean> hmPlayersImplied = new HashMap<Player, Boolean>();
        try {
            ArrayList<Game> alG = tournament.gamesList();
            for (Game g : alG) {
                Player wP = g.getWhitePlayer();
                hmPlayersImplied.put(wP, true);
                Player bP = g.getBlackPlayer();
                hmPlayersImplied.put(bP, true);
            }
            for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                Player byeP = tournament.getByePlayer(r);
                if (byeP != null) {
                    hmPlayersImplied.put(byeP, true);
                }
            }
            for (Iterator<ScoredPlayer> it = alSP.iterator(); it.hasNext();) {
                ScoredPlayer sP = it.next();
                Boolean b = hmPlayersImplied.get(sP);
                if (b == null) {
                    continue;
                }
                if (!b) {
                    it.remove();
                }
            }

        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alSP;
    }
    
    private void updateWelcomePanel() throws RemoteException {        
        try {
            QR.createQRJButton("http://opengotha.info/", this.pnlQRW);          
        } catch (WriterException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    private void updateControlPanel() throws RemoteException {
        if (tournament == null) {
            this.pnlIntControlPanel.setVisible(false);
            return;
        }

        this.pnlIntControlPanel.setVisible(true);

        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        DefaultTableModel model = (DefaultTableModel) tblControlPanel.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        ArrayList<Player> alPlayers = tournament.playersList();

        for (int r = 0; r < tps.getGeneralParameterSet().getNumberOfRounds(); r++) {
            // Number of participants
            int nbParticipants = 0;
            for (Player p : alPlayers) {
                if (p.getParticipating()[r]) {
                    nbParticipants++;
                    // Assigned players, games, etc.
                }
            }
            ArrayList<Game> alGames = tournament.gamesList(r);
            int nbGames = alGames.size();
            int nbAssignedPlayers = 2 * nbGames;
            if (tournament.getByePlayer(r) != null) {
                nbAssignedPlayers++;
            }
            int nbEntResults = 0;
            for (Game g : alGames) {
                int result = g.getResult();
                if (result != Game.RESULT_UNKNOWN) {
                    nbEntResults++;
                }
            }

            Vector<String> row = new Vector<String>();
            row.add("" + (r + 1));

            row.add("" + nbParticipants);

            row.add("" + nbAssignedPlayers);
            if (nbAssignedPlayers != nbParticipants) {
                this.cpTableCellRenderer.cpWarning[r][2] = true;
            } else {
                this.cpTableCellRenderer.cpWarning[r][2] = false;
            }

            row.add("" + nbEntResults + "/" + nbGames);
            if (nbEntResults != nbGames) {
                this.cpTableCellRenderer.cpWarning[r][3] = true;
            } else {
                this.cpTableCellRenderer.cpWarning[r][3] = false;
            }

            model.addRow(row);
        }
        tblControlPanel.clearSelection();
        tblControlPanel.changeSelection(1, 2, true, false);
        tblControlPanel.changeSelection(5, 1, true, false);

        this.lblWarningPRE.setText("");

        int nbPreliminary = 0;
        int nbFinal = 0;
        for (Player p : alPlayers) {
            if (p.getRegisteringStatus().compareTo("PRE") == 0) {
                nbPreliminary++;

            }
            if (p.getRegisteringStatus().compareTo("FIN") == 0) {
                nbFinal++;

            }
        }
        if (nbPreliminary == 1) {
            lblWarningPRE.setText("Warning!" + nbPreliminary
                    + "player has a Preliminary registering status");
        }
        if (nbPreliminary > 1) {
            lblWarningPRE.setText("Warning!" + nbPreliminary
                    + "players have a Preliminary registering status");
        }
        
        // Aceess to opengotha.info
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        boolean bExportHFOG = pubPS.isExportHFToOGSite();
        if (bExportHFOG) {
            String dirName = new SimpleDateFormat("yyyyMMdd").format(gps.getBeginDate()) + tournament.getShortName() + "/";
            String strURL = "http://opengotha.info/tournaments/" + dirName;
            try {
                QR.createQRJButton(strURL, this.pnlQRCP);
            } catch (WriterException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.pnlQRCP.setVisible(true);
            
            this.lblOGCP.setText(strURL);
            this.lblOGCP.setVisible(true);
            
        }
        else{
            this.pnlQRCP.setVisible(false);
            this.lblOGCP.setVisible(false);
        }
    }

    // TODO : UpdateTeamsPanel should use TeamMemberStrings (See TournamentPrinting or ExternalDocument.generateTeamsListHTMLFile 
    private void updateTeamsPanel() throws RemoteException {
        DefaultTableModel model = (DefaultTableModel) this.tblTeamsPanel.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        if (tournament == null) {
            return;
        }

        ArrayList<Team> alDisplayedTeams = tournament.teamsList();

        int teamSize = 0;
        try {
            teamSize = tournament.getTeamSize();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrTeamsManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        TeamComparator teamComparator = new TeamComparator(TeamComparator.TEAM_NUMBER_ORDER, teamSize);
        Collections.sort(alDisplayedTeams, teamComparator);


        for (Team t : alDisplayedTeams) {
            Object[] row = new Object[TM_NUMBER_OF_COLS];
            row[TM_TEAM_NUMBER_COL] = "" + (t.getTeamNumber() + 1);
            row[TM_TEAM_NAME_COL] = t.getTeamName();
            row[TM_BOARD_NUMBER_COL] = "";
            row[TM_PL_NAME_COL] = "";
            row[TM_PL_COUNTRY_COL] = "";
            row[TM_PL_CLUB_COL] = "";
            row[TM_PL_RATING_COL] = "";
            row[TM_PL_ROUNDS_COL] = "";

            model = (DefaultTableModel) this.tblTeamsPanel.getModel();
            model.addRow(row);

            for (int ib = 0; ib < teamSize; ib++) {
                ArrayList<Player> alP = tournament.playersList(t, ib);
                if (alP.isEmpty()) {
                    alP.add(null);
                }
                for (Player p : alP) {
                    row = new Object[TM_NUMBER_OF_COLS];
                    row[TM_TEAM_NUMBER_COL] = "";
                    row[TM_TEAM_NAME_COL] = "";
                    row[TM_BOARD_NUMBER_COL] = "" + (ib + 1);
                    if (p == null) {
                        row[TM_PL_NAME_COL] = "";
                        row[TM_PL_COUNTRY_COL] = "";
                        row[TM_PL_CLUB_COL] = "";
                        row[TM_PL_RATING_COL] = "";
                    } else {
                        row[TM_PL_NAME_COL] = p.getName() + " " + p.getFirstName();
                        row[TM_PL_COUNTRY_COL] = p.getCountry();
                        row[TM_PL_CLUB_COL] = p.getClub();
                        row[TM_PL_RATING_COL] = p.getRating();
                    }

                    if (p == null) {
                        row[TM_PL_ROUNDS_COL] = "";
                    } else {
                        int numberOfRounds = 0;
                        try {
                            numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
                        } catch (RemoteException ex) {
                            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean[] bM = tournament.membership(p, t, ib);
                        String str = "";
                        for (int r = 0; r < numberOfRounds; r++) {
                            str += bM[r] ? "+" : "-";
                        }
                        row[TM_PL_ROUNDS_COL] = str;
                    }

                    model = (DefaultTableModel) this.tblTeamsPanel.getModel();
                    model.addRow(row);
                }
            }

        }
    }

    /**
     * if necessary, saves the current tournament
     *
     * @return false if operation has been cancelled
     */
    private boolean saveCurrentTournamentIfNecessary() {
        try {
            if (Gotha.runningMode == Gotha.RUNNING_MODE_CLI) {
                return true;
            }
            if (tournament == null) {
                return true;
            }

            
                if (!tournament.isChangeSinceLastSave()) {
                    return true;
                }

                int response = JOptionPane.showConfirmDialog(this, "Do you want to save current tournament ?",
                        "Message", JOptionPane.YES_NO_CANCEL_OPTION);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
                if (response == JOptionPane.YES_OPTION) {
                    File f =  this.chooseASaveFile(this.getDefaultSaveAsFileName());
                    updateShortNameFromFile(f);
                    this.saveTournament(f);
                    
                    tournament.setHasBeenSavedOnce(true);
                    this.addRecentTournament("" + f);
                    this.tournamentChanged();
                    
                    // Eventually send the file to opengotha.info
                    try {
                        if(tournament.getTournamentParameterSet().getPublishParameterSet().isExportTFToOGSite()){    
                            TournamentPublishing.sendByFTPToOGSite(tournament, f);
                        }
                    } catch (RemoteException ex) {
                        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                    }
   
                    return true;
                }
               return true;
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private File saveTournament(File f) {
        TournamentInterface t = tournament;
        return saveTournament(t, f);
    }

    private File saveTournament(TournamentInterface t, File f) {
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        // if current extension is not .xml, add .xml
        String suffix = null;
        String s = f.getName();
        System.out.println("Tournament.saveTournament. " + "s = " + s);
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            suffix = s.substring(i + 1).toLowerCase();
        }
        try {
            if (suffix == null || !suffix.equals("xml")) {
                f = new File(f.getCanonicalPath() + ".xml");
            }
        } catch (IOException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        ExternalDocument.generateXMLFile(t, f);
        try {
            t.setChangeSinceLastSaveAsFalse();
            t.setHasBeenSavedOnce(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.tournamentChanged();
        return f;
        
    }

    private void mniHelpAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniHelpAboutActionPerformed
//        LogElements.incrementElement("help.about", "");
        javax.swing.JTextArea txa = new javax.swing.JTextArea(Gotha.getCopyLeftText() + Gotha.getThanksToText());
        txa.setFont(new Font("Tahoma", Font.PLAIN, 11));
        JOptionPane.showMessageDialog(this, txa, "OpenGotha",
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Gotha.getIconImage()));
    }//GEN-LAST:event_mniHelpAboutActionPerformed

    private void mniPlayersManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPlayersManagerActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrPlayersManager(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniPlayersManagerActionPerformed

    private void mniSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveAsActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File f =  this.chooseASaveFile(this.getDefaultSaveAsFileName());
        
        updateShortNameFromFile(f);
        
        // Make actual save
        this.saveTournament(f);
        
        // Eventually send the file to opengotha.info
        try {
            if(tournament.getTournamentParameterSet().getPublishParameterSet().isExportTFToOGSite()){    
                TournamentPublishing.sendByFTPToOGSite(tournament, f);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
        try {
            tournament.setHasBeenSavedOnce(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.addRecentTournament("" + f);
        
        this.tournamentChanged();
    }//GEN-LAST:event_mniSaveAsActionPerformed
    
    // Manages the JFileChooser Dialog and makes actual save
    File chooseASaveFile(String fileName){
        File defFile = new File(fileName);
        
        File dir = defFile.getParentFile();
        String fn = defFile.getName();
        
        JFileChooser fileChoice = new JFileChooser(dir);
        fileChoice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChoice.setDialogType(JFileChooser.SAVE_DIALOG);
      
        fileChoice.setSelectedFile(new File(fn));

        MyFileFilter mff = new MyFileFilter(new String[]{"xml"},
                "Gotha Files(*.xml)");
        fileChoice.addChoosableFileFilter(mff);
        int result = fileChoice.showSaveDialog(this);
        File f = null;
        if (result == JFileChooser.CANCEL_OPTION) {
            f = null;
        } else {
            f = fileChoice.getSelectedFile();
        }
        if (f == null) {
            return null;
        }
        return f;
    }
     
    void updateShortNameFromFile(File f){
        String fileName = "" + f;
        int indLastSep = 0;
        // drop path and extension 
        for (int i = 0; i < fileName.length(); i++){
            if (fileName.charAt(i) == '/') indLastSep = i;
            if (fileName.charAt(i) == '\\') indLastSep = i;
        }
        String snExt = fileName.substring(indLastSep + 1);
        
        int indLastPoint = snExt.length();
        for (int i = 0; i < snExt.length(); i++){
            if (snExt.charAt(i) == '.') indLastPoint = i;
        }
        String sn = snExt.substring(0, indLastPoint);
        try {
            tournament.setShortName(sn);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        
    /** 
     * Used to know what is the default Tournament File Name for saving
     * if hasBeenSavedOnce = false, the default is based on runningDirectory + "/tournamentfile/" sand shortName
     * else default is the °th recent tournament file
     * if no recent tournament file, default is based on runningDirectory and shortName
     * @return 
     */
    String getDefaultSaveAsFileName(){
        boolean bHBSO = false;
        try {
            bHBSO = tournament.isHasBeenSavedOnce();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String shortName = "shortName";
        try {
            shortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        File snFile = new File(Gotha.runningDirectory + "/tournamentfiles", shortName + ".xml"); 
        String snFN;
        snFN = "" + snFile;

        String rtFN = "";
        ArrayList<String> alRT = this.getRecentTournamentsList();
        if (alRT != null && alRT.size() > 0) rtFN = alRT.get(0);

        if (!bHBSO) return snFN;
        if (rtFN.length() < 1) return snFN;
        return rtFN;
    }
 
private void mniRRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRRActionPerformed
    if (tournament == null) {
        JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
        return;
    }
    try {
        JFrame jfr = new JFrGamesRR(tournament);
        jfr.setVisible(true);
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }

}//GEN-LAST:event_mniRRActionPerformed

private void mniRMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRMIActionPerformed
    JFrame jfr = new JFrToolsRMI();
    jfr.setVisible(true);
}//GEN-LAST:event_mniRMIActionPerformed

private void mniOpenGothaHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOpenGothaHelpActionPerformed
//    LogElements.incrementElement("help.og", "");

    Gotha.displayGothaHelp("Starting OpenGotha");
}//GEN-LAST:event_mniOpenGothaHelpActionPerformed

private void mniImportXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniImportXMLActionPerformed
    if (tournament == null) {
        JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int w = JFrGotha.SMALL_FRAME_WIDTH;
    int h = JFrGotha.SMALL_FRAME_HEIGHT;
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    dlgImportXML.setBounds((dim.width - w) / 2, (dim.height - h) / 2, w, h);
    dlgImportXML.setTitle("Import from XML File");
    dlgImportXML.setIconImage(Gotha.getIconImage());

    dlgImportXML.setVisible(true);


}//GEN-LAST:event_mniImportXMLActionPerformed

private void btnDlgImportXMLOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDlgImportXMLOKActionPerformed
    dlgImportXML.dispose();

    // From what file shall we import ?
    File f = chooseAFile(Gotha.runningDirectory, "xml");
    if (f == null) {
        return;
    }

    String strReport = ExternalDocument.importTournamentFromXMLFile(f, tournament,
            this.chkPlayers.isSelected(), this.chkGames.isSelected(), this.chkTournamentParameters.isSelected(), this.chkTeams.isSelected(), this.chkClubsGroups.isSelected());

    this.tournamentChanged();
    JOptionPane.showMessageDialog(this, strReport, "Message", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_btnDlgImportXMLOKActionPerformed

private void btnDlgImportXMLCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDlgImportXMLCancelActionPerformed
    this.dlgImportXML.dispose();
}//GEN-LAST:event_btnDlgImportXMLCancelActionPerformed

private void spnRoundNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRoundNumberStateChanged
    int demandedRN = (Integer) (spnRoundNumber.getValue()) - 1;
    this.demandedDisplayedRoundNumberHasChanged(demandedRN);
}//GEN-LAST:event_spnRoundNumberStateChanged

private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
    Gotha.displayGothaHelp("Create a new tournament");
}//GEN-LAST:event_btnHelpActionPerformed

private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
    String strSearchPlayer = this.txfSearchPlayer.getText().toLowerCase();
    if (strSearchPlayer.length() == 0) {
        tblStandings.clearSelection();
        return;
    }
    TableModel model = tblStandings.getModel();

    int rowNumber = -1;
    int startRow = tblStandings.getSelectedRow() + 1;
    int nbRows = model.getRowCount();
    for (int iR = 0; iR < nbRows; iR++) {
        int row = (startRow + iR) % nbRows;
        String str = (String) model.getValueAt(row, NAME_COL);
        str = str.toLowerCase();
        if (str.indexOf(strSearchPlayer) < 0) {
            continue;
        }
        // OK! Found
        rowNumber = row;
        break;
    }

    tblStandings.clearSelection();
    if (rowNumber == -1) {
        JOptionPane.showMessageDialog(this,
                "No player with the specified name was found in the Standings table ",
                "Message", JOptionPane.ERROR_MESSAGE);
    } else {
        tblStandings.setRowSelectionAllowed(true);
        tblStandings.clearSelection();
        tblStandings.addRowSelectionInterval(rowNumber, rowNumber);

        Rectangle rect = tblStandings.getCellRect(rowNumber, 0, true);
        tblStandings.scrollRectToVisible(rect);
    }

    tblStandings.repaint();
}//GEN-LAST:event_btnSearchActionPerformed

private void mniImportH9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniImportH9ActionPerformed
    this.importPlainFile("h9");
}//GEN-LAST:event_mniImportH9ActionPerformed

    /**
     * imports players and games from a plain file
     *
     * @param importType either "h9", "tou", or "wallist"
     */
    private void importPlainFile(String importType) {
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String strExt = "txt";
        if (importType.equals("tou")) {
            strExt = "tou";
        } else if (importType.equals("h9")) {
            strExt = "h9";
        }
        File f = chooseAFile(Gotha.runningDirectory, strExt);
        if (f == null) {
            return;
        }
        ArrayList<Player> alPlayers = new ArrayList<Player>();
        ArrayList<Game> alGames = new ArrayList<Game>();
        try {
            ExternalDocument.importPlayersAndGamesFromPlainFile(f, importType, alPlayers, alGames);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errors occured in reading " + f.getName()
                    + "\nImport process has been aborted", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int nbErrors = 0;
        for (Player p : alPlayers) {
            try {
                tournament.addPlayer(p);
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TournamentException te) {
                nbErrors++;
                if (nbErrors <= 3) {
                    JOptionPane.showMessageDialog(this, te.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
                }
                if (nbErrors == 4) {
                    JOptionPane.showMessageDialog(this, "More than 3 errors have been detected", "Message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (nbErrors > 0) {
            JOptionPane.showMessageDialog(this, "Due to errors on players, games have not been imported", "Message", JOptionPane.ERROR_MESSAGE);
        } else {
            for (Game g : alGames) {
                try {
                    tournament.addGame(g);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TournamentException ex) {
                    Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        try {
            tournament.updateNumberOfRoundsIfNecesary();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.tournamentChanged();
    }

    /**
     * imports players from a vBar separated file
     */
    private void importVBSFile() {
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String strExt = "txt";
        File f = chooseAFile(Gotha.runningDirectory, strExt);
        if (f == null) {
            return;
        }
        ArrayList<Player> alPlayers = new ArrayList<Player>();
        ArrayList<Game> alGames = new ArrayList<Game>();
        try {
            ExternalDocument.importPlayersFromVBSFile(f, alPlayers);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errors occured in reading " + f.getName()
                    + "\nImport process has been aborted", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int nbErrors = 0;
        for (Player p : alPlayers) {
            try {
                tournament.addPlayer(p);
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TournamentException te) {
                nbErrors++;
                if (nbErrors <= 3) {
                    JOptionPane.showMessageDialog(this, te.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
                }
                if (nbErrors == 4) {
                    JOptionPane.showMessageDialog(this, "More than 3 errors have been detected", "Message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (nbErrors > 0) {
            JOptionPane.showMessageDialog(this, "Due to errors on players, games have not been imported", "Message", JOptionPane.ERROR_MESSAGE);
        }

        this.tournamentChanged();
    }

private void mniPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPreferencesActionPerformed
    JFrame jfr = new JFrPreferencesOptions();
    jfr.setVisible(true);

}//GEN-LAST:event_mniPreferencesActionPerformed

private void mniImportWallistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniImportWallistActionPerformed
    this.importPlainFile("wallist");
}//GEN-LAST:event_mniImportWallistActionPerformed

private void mniTeamsManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniTeamsManagerActionPerformed
    if (tournament == null) {
        JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
        return;
    }
    try {
        JFrame jfr = new JFrTeamsManager(tournament);
        jfr.setVisible(true);
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_mniTeamsManagerActionPerformed

private void rdbCurrentTeamPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCurrentTeamPSActionPerformed
    updateAllViews();
}//GEN-LAST:event_rdbCurrentTeamPSActionPerformed

private void rdbTemporaryTeamPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbTemporaryTeamPSActionPerformed
    updateAllViews();
}//GEN-LAST:event_rdbTemporaryTeamPSActionPerformed

private void spnTeamRoundNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnTeamRoundNumberStateChanged
    int demandedTeamRN = (Integer) (spnTeamRoundNumber.getValue()) - 1;
    this.demandedDisplayedTeamRoundNumberHasChanged(demandedTeamRN);

}//GEN-LAST:event_spnTeamRoundNumberStateChanged

private void cbxTeamCritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxTeamCritActionPerformed
    // In order to avoid useless updates, ...
    if (!this.cbxTeamCrit1.isEnabled()) {
        return;
    }

    try {
        updateDisplayTeamCriteria();
        updateTeamsStandingsComponents();
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_cbxTeamCritActionPerformed

private void mniTeamsPairingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniTeamsPairingActionPerformed
    if (tournament == null) {
        JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
        return;
    }
    try {
        JFrame jfr = new JFrTeamsPairing(tournament);
        jfr.setVisible(true);
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }

}//GEN-LAST:event_mniTeamsPairingActionPerformed

private void btnPrintTeamsStandingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintTeamsStandingsActionPerformed
    try {
        TournamentParameterSet printedTPS = tournament.getTournamentParameterSet();
        TeamTournamentParameterSet ttps = tournament.getTeamTournamentParameterSet();
        TeamTournamentParameterSet printedTeamTPS = new TeamTournamentParameterSet(ttps);
        TeamPlacementParameterSet printedTeamPPS = printedTeamTPS.getTeamPlacementParameterSet();
        printedTeamPPS.setPlaCriteria(displayedTeamCriteria);
//        sts = tournament.getAnUpToDateScoredTeamsSet(printedTeamPPS, displayedTeamRoundNumber);
        TournamentPrinting.printTeamsStandings(tournament, printedTPS, printedTeamTPS, this.displayedTeamRoundNumber);
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_btnPrintTeamsStandingsActionPerformed

private void mniOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOpenActionPerformed
//    File f = chooseAFile(Gotha.tournamentDirectory, "xml");
    File f = chooseAFile(new File(Gotha.runningDirectory, "tournamentfiles"), "xml");
    if (f == null) {
        return;
    }
    try {
        openTournament(f);
        // update Preferences
        this.addRecentTournament(f.getAbsolutePath());
    } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(this, " File not found", "Message", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        String strMessage = "Some problem occured with file : " + f.getName();
        strMessage += "\nThe content of this file does not comply with OpenGotha Data version " + Gotha.GOTHA_DATA_VERSION;
        strMessage += "\nHint : Read the Compatibility issues in the OpenGotha help";
        strMessage += "\n\nThe tournament has not been opened";
        JOptionPane.showMessageDialog(JFrGotha.this, strMessage, "Message", JOptionPane.ERROR_MESSAGE);
        JFrGotha.this.removeAllRecentTournament();

    }

}//GEN-LAST:event_mniOpenActionPerformed

private void mniImportVBSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniImportVBSActionPerformed
    this.importVBSFile();
}//GEN-LAST:event_mniImportVBSActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    exitOpenGotha();
}//GEN-LAST:event_formWindowClosing

private void mniUpdateRatingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniUpdateRatingsActionPerformed
    if (tournament == null) {
        JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        JFrame jfr = new JFrUpdateRatings(tournament);
        jfr.setVisible(true);
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_mniUpdateRatingsActionPerformed

private void mniExperimentalToolsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExperimentalToolsActionPerformed
    JFrame jfr = null;
    try {
        jfr = new JFrExperimentalTools(tournament);
    } catch (RemoteException ex) {
        Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
    }
    jfr.setVisible(true);
}//GEN-LAST:event_mniExperimentalToolsActionPerformed

private void mniMemoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniMemoryActionPerformed
    JFrame jfr = new JFrToolsMemory();
    jfr.setVisible(true);
}//GEN-LAST:event_mniMemoryActionPerformed

    private void mniDiscardRoundsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDiscardRoundsActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrDiscardRounds(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_mniDiscardRoundsActionPerformed

    private void mniSaveACopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveACopyActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        String strTC = gothaPrefs.get("tournamentCopy", "");
        if (strTC.length() < 1){
            String shortName = "shortName";
            try {
                shortName = tournament.getShortName();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            File tcFile = new File(Gotha.runningDirectory + "/tournamentfiles/copies", shortName + "_Copy.xml");
            strTC = "" + tcFile;
        }
            
//        File f = saveAs(strTC);
        File f = this.chooseASaveFile(strTC);
        this.updateShortNameFromFile(f);
        saveTournament(f);
        gothaPrefs.put("tournamentCopy", "" + f);
     
    }//GEN-LAST:event_mniSaveACopyActionPerformed

    private void mniPublishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPublishActionPerformed
        if (tournament == null) {
            JOptionPane.showMessageDialog(this, "No currently open tournament", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JFrame jfr = new JFrPublish(tournament);
            jfr.setVisible(true);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniPublishActionPerformed

    private void chkClubsGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClubsGroupsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkClubsGroupsActionPerformed


    private File chooseAFile(File path, String extension) {
        JFileChooser fileChoice = new JFileChooser(path);
        fileChoice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        MyFileFilter mff = new MyFileFilter(new String[]{extension}, "*." + extension);
        fileChoice.addChoosableFileFilter(mff);
        int result = fileChoice.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return null;
        } else {
            return fileChoice.getSelectedFile();
        }
    }

    private void openTournament(File f) throws Exception {
//        LogElements.incrementElement("tournament.open", f.getName());
        if (!saveCurrentTournamentIfNecessary()) {
            return;
        }

        TournamentInterface t = Gotha.getTournamentFromFile(f);
        if (t == null) {
            String strMessage = "Some problem occured with file : " + f.getName();
            strMessage += "\nThe corresponding tournament has not been opened";
            System.out.println(strMessage);
            return;
        }
        // Check if a tournament with same name exists (Server mode only)
        if (Gotha.runningMode == Gotha.RUNNING_MODE_SRV) {
            String tKN = null;
            try {
                tKN = t.getShortName();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            TournamentInterface oldT = GothaRMIServer.getTournament(tKN);
            if (oldT != null) {
                String strMessage = tKN + " is already opened on this server";
                strMessage += "\nIt will not be opened again";
                JOptionPane.showMessageDialog(this, strMessage, "Message", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (tournament != null) {
            closeTournament();
        }
        tournament = t;
        tournament.setChangeSinceLastSaveAsFalse();
        tournament.setHasBeenSavedOnce(true);

        // If we are in Server mode, then worry about adding it to registry
        if (Gotha.runningMode == Gotha.RUNNING_MODE_SRV) {
            GothaRMIServer.addTournament(tournament);
        }

        try {
            this.displayedRoundNumber = tournament.presumablyCurrentRoundNumber();
            this.tournamentChanged();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void closeTournament() {
        if (tournament == null) {
            return;
        }
        try {
            tournament.close();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.tournamentChanged();
        tournament = null;
        this.tournamentChanged();
        
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        gothaPrefs.put("tournamentCopy", "");
    }

    private void tournamentChanged() {
        updateTitle();
        if (tournament == null) {
            try {
                this.updateDisplayCriteria();
                this.updateStandingsComponents();
                this.updateControlPanel();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        try {
            tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        updateAllViews();
    }

    private void updateAllViews() {
        if (tournament != null) {
            try {
                if (Gotha.runningMode == Gotha.RUNNING_MODE_CLI && !tournament.isOpen()) {
                    dispose();
                }

                this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();
                if (tournament.tournamentType() == TournamentParameterSet.TYPE_MCMAHON) {
                    this.mniMMGroups.setEnabled(true);
                } else {
                    this.mniMMGroups.setEnabled(false);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        updateTitle();

        int idxTP = tpnGotha.indexOfTab("Teams Panel");
        int idxTS = tpnGotha.indexOfTab("Teams Standings");
        if (idxTP > 0) {
            this.tpnGotha.setEnabledAt(idxTP, false);
        }
        if (idxTS > 0) {
            this.tpnGotha.setEnabledAt(idxTS, false);
        }

        try {
            if (tournament != null && !tournament.teamsList().isEmpty()) {
                if (idxTP > 0) {
                    this.tpnGotha.setEnabledAt(idxTP, true);
                }
                if (idxTS > 0) {
                    this.tpnGotha.setEnabledAt(idxTS, true);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.tpnGotha.getSelectedComponent() == pnlWelcome) {
            try {
                this.updateWelcomePanel();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (this.tpnGotha.getSelectedComponent() == pnlStandings) {
            try {
                this.updateDisplayCriteria();
                this.updateStandingsComponents();

            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.tpnGotha.getSelectedComponent() == pnlControlPanel) {
            try {
                this.updateControlPanel();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.tpnGotha.getSelectedComponent() == pnlTeamsPanel) {
            try {
                this.updateTeamsPanel();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.tpnGotha.getSelectedComponent() == this.pnlTeamsStandings) {
            try {
                this.updateDisplayTeamCriteria();
                this.updateTeamsStandingsComponents();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    private void demandedDisplayedRoundNumberHasChanged(int demandedRN) {
        int numberOfRounds = 0;
        try {
            numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (demandedRN < 0 || demandedRN >= numberOfRounds) {
            spnRoundNumber.setValue(displayedRoundNumber + 1);
            return;
        }
        if (demandedRN == displayedRoundNumber) {
            return;
        }

        displayedRoundNumber = demandedRN;
        try {
            updateStandingsComponents();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void demandedDisplayedTeamRoundNumberHasChanged(int demandedRN) {
        int numberOfRounds = 0;
        try {
            numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (demandedRN < 0 || demandedRN >= numberOfRounds) {
            spnTeamRoundNumber.setValue(displayedTeamRoundNumber + 1);
            return;
        }
        if (demandedRN == displayedTeamRoundNumber) {
            return;
        }

        displayedTeamRoundNumber = demandedRN;
        try {
            updateTeamsStandingsComponents();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void exitOpenGotha() {
        if (!saveCurrentTournamentIfNecessary()) {
            return;
        }
        if (Gotha.isJournalingReportEnabled()) {
            LogElements.sendLogElements();
        }
        System.exit(0);
    }

    /**
     * formats column and cell of a given JTable. If a specific TableRenderer
     * class is used for this table, the behaviour of formatColumn will be
     * overridden by the specific method This method
     *
     * @param tbl Jtable
     * @param col column number
     * @param str header String
     * @param width comumn width
     * @param bodyAlign horizontal alignment for column cells
     * @param headerAlign horizontal alignment for haeder
     */
    public static void formatColumn(JTable tbl, int col, String str, int width, int bodyAlign, int headerAlign) {
        formatHeader(tbl, col, str, headerAlign);
        formatColumnBody(tbl, col, width, bodyAlign);
    }

    public static void formatHeader(JTable tbl, int col, String str, int align) {
        JTableHeader th = tbl.getTableHeader();
        th.getColumnModel().getColumn(col).setHeaderValue(str);
        th.repaint();
        TableColumn tc = tbl.getColumnModel().getColumn(col);
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setBackground(Color.LIGHT_GRAY);
        dtcr.setHorizontalAlignment(align);
        tc.setHeaderRenderer(dtcr);
    }

    private static void formatColumnBody(JTable tbl, int col, int width, int align) {
        TableColumnModel tcm = tbl.getColumnModel();

        tcm.getColumn(col).setPreferredWidth(width);

        // Alignment
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(align);
        tcm.getColumn(col).setCellRenderer(dtcr);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDlgImportXMLCancel;
    private javax.swing.JButton btnDlgImportXMLOK;
    private javax.swing.JButton btnDlgNewCancel;
    private javax.swing.JButton btnDlgNewOK;
    private javax.swing.JButton btnHelp;
    private javax.swing.JButton btnPrintStandings;
    private javax.swing.JButton btnPrintTeamsStandings;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cbxCrit1;
    private javax.swing.JComboBox cbxCrit2;
    private javax.swing.JComboBox cbxCrit3;
    private javax.swing.JComboBox cbxCrit4;
    private javax.swing.JComboBox cbxTeamCrit1;
    private javax.swing.JComboBox cbxTeamCrit2;
    private javax.swing.JComboBox cbxTeamCrit3;
    private javax.swing.JComboBox cbxTeamCrit4;
    private javax.swing.JComboBox cbxTeamCrit5;
    private javax.swing.JComboBox cbxTeamCrit6;
    private javax.swing.JCheckBox chkClubsGroups;
    private javax.swing.JCheckBox chkGames;
    private javax.swing.JCheckBox chkPlayers;
    private javax.swing.JCheckBox chkTeams;
    private javax.swing.JCheckBox chkTournamentParameters;
    private javax.swing.JDialog dlgImportXML;
    private javax.swing.JDialog dlgNew;
    private javax.swing.ButtonGroup grpPS;
    private javax.swing.ButtonGroup grpSystem;
    private javax.swing.ButtonGroup grpTeamPS;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JLabel lblFlowChart;
    private javax.swing.JLabel lblOGCP;
    private javax.swing.JLabel lblRecommended;
    private javax.swing.JLabel lblStandingsAfter;
    private javax.swing.JLabel lblTeamUpdateTime;
    private javax.swing.JLabel lblTeamsStandingsAfter;
    private javax.swing.JLabel lblTournamentPicture;
    private javax.swing.JLabel lblUpdateTime;
    private javax.swing.JLabel lblWarningPRE;
    private javax.swing.JMenuItem mniBuildTestTournament;
    private javax.swing.JMenuItem mniClose;
    private javax.swing.JMenuItem mniDiscardRounds;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniExperimentalTools;
    private javax.swing.JMenuItem mniGamesOptions;
    private javax.swing.JMenuItem mniHelpAbout;
    private javax.swing.JMenuItem mniImportH9;
    private javax.swing.JMenuItem mniImportTou;
    private javax.swing.JMenuItem mniImportVBS;
    private javax.swing.JMenuItem mniImportWallist;
    private javax.swing.JMenuItem mniImportXML;
    private javax.swing.JMenuItem mniMMGroups;
    private javax.swing.JMenuItem mniMemory;
    private javax.swing.JMenuItem mniNew;
    private javax.swing.JMenuItem mniOpen;
    private javax.swing.JMenuItem mniOpenGothaHelp;
    private javax.swing.JMenuItem mniPair;
    private javax.swing.JMenuItem mniPlayersManager;
    private javax.swing.JMenuItem mniPlayersQuickCheck;
    private javax.swing.JMenuItem mniPreferences;
    private javax.swing.JMenuItem mniPublish;
    private javax.swing.JMenuItem mniRMI;
    private javax.swing.JMenuItem mniRR;
    private javax.swing.JMenuItem mniResults;
    private javax.swing.JMenuItem mniSaveACopy;
    private javax.swing.JMenuItem mniSaveAs;
    private javax.swing.JMenuItem mniTeamsManager;
    private javax.swing.JMenuItem mniTeamsPairing;
    private javax.swing.JMenuItem mniTournamentOptions;
    private javax.swing.JMenuItem mniUpdateRatings;
    private javax.swing.JMenu mnuGames;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuImport;
    private javax.swing.JMenuBar mnuMain;
    private javax.swing.JMenu mnuOpenRecent;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JMenu mnuPlayers;
    private javax.swing.JMenu mnuPublish;
    private javax.swing.JMenu mnuTools;
    private javax.swing.JMenu mnuTournament;
    private javax.swing.JPanel pnlControlPanel;
    private javax.swing.JPanel pnlIntControlPanel;
    private javax.swing.JPanel pnlIntStandings;
    private javax.swing.JPanel pnlIntTeamsPanel;
    private javax.swing.JPanel pnlIntTeamsStandings;
    private javax.swing.JPanel pnlObjectsToImport;
    private javax.swing.JPanel pnlPS;
    private javax.swing.JPanel pnlQRCP;
    private javax.swing.JPanel pnlQRW;
    private javax.swing.JPanel pnlStandings;
    private javax.swing.JPanel pnlSystem;
    private javax.swing.JPanel pnlTeamPS;
    private javax.swing.JPanel pnlTeamsPanel;
    private javax.swing.JPanel pnlTeamsStandings;
    private javax.swing.JPanel pnlTournamentDetails;
    private javax.swing.JPanel pnlWelcome;
    private javax.swing.JRadioButton rdbCurrentPS;
    private javax.swing.JRadioButton rdbCurrentTeamPS;
    private javax.swing.JRadioButton rdbMcMahon;
    private javax.swing.JRadioButton rdbSwiss;
    private javax.swing.JRadioButton rdbSwissCat;
    private javax.swing.JRadioButton rdbTemporaryPS;
    private javax.swing.JRadioButton rdbTemporaryTeamPS;
    private javax.swing.JScrollPane scpControlPanel;
    private javax.swing.JScrollPane scpStandings;
    private javax.swing.JScrollPane scpTeamsPanel;
    private javax.swing.JScrollPane scpTeamsStandings;
    private javax.swing.JSpinner spnRoundNumber;
    private javax.swing.JSpinner spnTeamRoundNumber;
    private javax.swing.JTable tblControlPanel;
    private javax.swing.JTable tblStandings;
    private javax.swing.JTable tblTeamsPanel;
    private javax.swing.JTable tblTeamsStandings;
    private javax.swing.JTabbedPane tpnGotha;
    private javax.swing.JTextField txfBeginDate;
    private javax.swing.JTextField txfDirector;
    private javax.swing.JTextField txfEndDate;
    private javax.swing.JTextField txfLocation;
    private javax.swing.JTextField txfName;
    private javax.swing.JTextField txfNumberOfRounds;
    private javax.swing.JTextField txfSearchPlayer;
    private javax.swing.JTextField txfShortName;
    // End of variables declaration//GEN-END:variables
}

class MyFileFilter extends FileFilter {

    String[] suffixes;
    String description;

    public MyFileFilter(String[] suffixes, String description) {
        for (int i = 0; i < suffixes.length; i++) {
            this.suffixes = suffixes;
            this.description = description;
        }
    }

    boolean belongs(String suffix) {
        for (int i = 0; i < suffixes.length; i++) {
            if (suffix.equals(suffixes[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String suffix = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            suffix = s.substring(i + 1).toLowerCase();
        }
        return suffix != null && belongs(suffix);
    }

    @Override
    public String getDescription() {
        return description;
    }
}

class StandingsTableCellRenderer extends JLabel implements TableCellRenderer {
    // This method is called each time a cell in a column
    // using this renderer needs to be rendered.

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
        TableModel model = table.getModel();
        setText("" + model.getValueAt(rowIndex, colIndex));
        if (isSelected) {
            setFont(this.getFont().deriveFont(Font.BOLD));
        } else {
            setFont(this.getFont().deriveFont(Font.PLAIN));
        }
        return this;
    }
}

class ControlPanelTableCellRenderer extends JLabel implements TableCellRenderer {
    // Assigned players will be in column 2; Entered results will be in column 3;

    protected boolean[][] cpWarning = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS][4];

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
        TableModel model = table.getModel();
        setText("" + model.getValueAt(rowIndex, colIndex));
        if (cpWarning[rowIndex][colIndex]) {
            this.setForeground(Color.red);
        } else {
            setForeground(Color.black);
        }
        this.setHorizontalAlignment(JLabel.CENTER);
        if (colIndex == 0) { //
            this.setHorizontalAlignment(JLabel.RIGHT);
        }
        return this;
    }
}

class TeamsPanelTableCellRenderer extends JLabel implements TableCellRenderer {
    // Assigned players will be in column 2; Entered results will be in column 3;

    private Font defaultFont = this.getFont();
    protected boolean[][] cpWarning = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS][4];

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
        TableModel model = table.getModel();
        setText("" + model.getValueAt(rowIndex, colIndex));
        this.setHorizontalAlignment(JLabel.CENTER);
        if (colIndex == JFrGotha.TM_TEAM_NUMBER_COL
                || colIndex == JFrGotha.TM_BOARD_NUMBER_COL) { //
            this.setHorizontalAlignment(JLabel.RIGHT);
        }
        if (colIndex == JFrGotha.TM_TEAM_NAME_COL
                || colIndex == JFrGotha.TM_PL_NAME_COL) { //
            this.setHorizontalAlignment(JLabel.LEFT);
        }
        if (colIndex == JFrGotha.TM_PL_ROUNDS_COL) { //
            Font f = new Font("Courier New", Font.BOLD, 16);
            setFont(f);
        } else {
//          setFont(this.getFont().deriveFont(12.0F));
            setFont(defaultFont);

        }

        return this;
    }
}
