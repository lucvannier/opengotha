/*
 * JFrPlayersManager.java
 */
package info.vannier.gotha;

import java.awt.PageAttributes.OriginType;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  Luc Vannier
 */
public class JFrPlayersManager extends javax.swing.JFrame {
    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    private int playersSortType = PlayerComparator.NAME_ORDER;
    private final static int PLAYER_MODE_NEW = 1;
    private final static int PLAYER_MODE_MODIF = 2;
    private int playerMode = PLAYER_MODE_NEW;
    private Player playerInModification = null;
    private static final int REG_COL = 0;
    private static final int NAME_COL = 1;
    private static final int FIRSTNAME_COL = 2;
    private static final int COUNTRY_COL = 3;
    private static final int CLUB_COL = 4;
    private static final int RANK_COL = 5;
    private static final int RATING_COL = 6;
    private static final int GRADE_COL = 7;
    /**  current Tournament */
    private TournamentInterface tournament;
    /** Rating List */
    private RatingList ratingList = new RatingList();
    
    /**
     * Creates new form JFrPlayersManager
     */
    public JFrPlayersManager(TournamentInterface tournament) throws RemoteException {
//        LogElements.incrementElement("players.manager", "");
        this.tournament = tournament;

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
                    Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
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
        int w = JFrGotha.BIG_FRAME_WIDTH;
        int h = JFrGotha.BIG_FRAME_HEIGHT;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - w) / 2, (dim.height - h) / 2, w, h);

        setIconImage(Gotha.getIconImage());

        AutoCompletion.enable(cbxRatingList);

        this.pgbRatingList.setVisible(false);
        this.scpWelcomeSheet.setVisible(false);

        tabCkbParticipation = new JCheckBox[Gotha.MAX_NUMBER_OF_ROUNDS];
        for (int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
            tabCkbParticipation[i] = new JCheckBox();
            tabCkbParticipation[i].setText("" + (i + 1));
            tabCkbParticipation[i].setFont(new Font("Default", Font.PLAIN, 9));
            pnlParticipation.add(tabCkbParticipation[i]);
            tabCkbParticipation[i].setBounds((i % 5) * 42 + 4, (i / 5) * 20 + 20, 40, 15);
        }
               
        getRootPane().setDefaultButton(btnRegister);

        initCountriesList();
        initRatingListControls();
        resetPlayerControls();
        initPnlRegisteredPlayers();
    }

    private void initCountriesList(){
        File f = new File(Gotha.runningDirectory, "documents/iso_3166-1_list_en.xml");
        if (f == null) {
            System.out.println("Country list file not found");
            return;
        }
        ArrayList<Country> alCountries = CountriesList.importCountriesFromXMLFile(f);
        this.cbxCountry.removeAllItems();
        this.cbxCountry.addItem("  ");

        if (alCountries == null) return;

        for(Country c : alCountries){
            cbxCountry.addItem(c.getAlpha2Code());
        }
    }
    
    private void initRatingListControls(){
        // Use the preferred rating list as in Preferences
        Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
        String defRL = prefs.get("defaultratinglist", "" );
        int rlType = RatingList.TYPE_UNDEFINED;
        try{
            rlType = Integer.parseInt(defRL);
        }catch(Exception e){
            rlType = RatingList.TYPE_UNDEFINED;
        }
        this.ckbRatingList.setSelected(true);
        this.btnSearchId.setVisible(false);
        this.txfSearchId.setVisible(false);
        switch(rlType){
            case RatingList.TYPE_UNDEFINED :
                this.ckbRatingList.setSelected(false); break;
            case RatingList.TYPE_EGF :
                this.rdbEGF.setSelected(true); break;
            case RatingList.TYPE_FFG :
                this.rdbFFG.setSelected(true); break;
            case RatingList.TYPE_AGA :
                this.rdbAGA.setSelected(true); 
                this.btnSearchId.setVisible(true);
                this.txfSearchId.setVisible(true); break;
        }     

        this.resetRatingListControls();
    }
    

    private void initPnlRegisteredPlayers() throws RemoteException {
        JFrGotha.formatColumn(tblRegisteredPlayers, REG_COL, "R", 10, JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tblRegisteredPlayers, NAME_COL, "Last name", 110, JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tblRegisteredPlayers, FIRSTNAME_COL, "First name", 80, JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tblRegisteredPlayers, COUNTRY_COL, "Co",30,  JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tblRegisteredPlayers, CLUB_COL, "Club", 40, JLabel.LEFT, JLabel.LEFT);
        JFrGotha.formatColumn(tblRegisteredPlayers, RANK_COL, "Rk", 30, JLabel.RIGHT, JLabel.RIGHT);
        JFrGotha.formatColumn(tblRegisteredPlayers, RATING_COL, "Rating",  40, JLabel.RIGHT, JLabel.RIGHT);
        JFrGotha.formatColumn(tblRegisteredPlayers, GRADE_COL, "Grade",  25, JLabel.RIGHT, JLabel.RIGHT);
                
        // Single selection
        tblRegisteredPlayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updatePnlRegisteredPlayers(tournament.playersList());
    }

    private void updatePnlRegisteredPlayers(ArrayList<Player> playersList) {
        int nbPreliminary = 0;
        int nbFinal = 0;
        for (Player p : playersList) {
            if (p.getRegisteringStatus().compareTo("PRE") == 0) {
                nbPreliminary++;
            }
            if (p.getRegisteringStatus().compareTo("FIN") == 0) {
                nbFinal++;
            }
        }
        txfNbPlPre.setText("" + nbPreliminary);
        txfNbPlFin.setText("" + nbFinal);
        DefaultTableModel model = (DefaultTableModel) tblRegisteredPlayers.getModel();
        // sort
        ArrayList<Player> displayedPlayersList = new ArrayList<Player>(playersList);

        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(displayedPlayersList, playerComparator);

        model.setRowCount(displayedPlayersList.size());
        for (Player p : displayedPlayersList) {
            int line = displayedPlayersList.indexOf(p);
            model.setValueAt((p.getRegisteringStatus().compareTo("PRE") == 0) ? "P" : "F", line, JFrPlayersManager.REG_COL);
            model.setValueAt(p.getName(), line, JFrPlayersManager.NAME_COL);
            model.setValueAt(p.getFirstName(), line, JFrPlayersManager.FIRSTNAME_COL);
            model.setValueAt(Player.convertIntToKD(p.getRank()), line, JFrPlayersManager.RANK_COL);
            model.setValueAt(p.getCountry(), line, JFrPlayersManager.COUNTRY_COL);
            model.setValueAt(p.getClub(), line, JFrPlayersManager.CLUB_COL);
            model.setValueAt(p.getRating(), line, JFrPlayersManager.RATING_COL);
//            model.setValueAt(Player.convertIntToKD(p.getGrade()), line, JFrPlayersManager.GRADE_COL);
            model.setValueAt(p.getStrGrade(), line, JFrPlayersManager.GRADE_COL);
        }
    }

    private void resetRatingListControls() {                
        boolean bRL = this.ckbRatingList.isSelected();
               
        this.rdbEGF.setVisible(bRL);
        this.rdbFFG.setVisible(bRL);
        this.rdbAGA.setVisible(bRL);
        this.btnUpdateRatingList.setVisible(bRL);
        this.rdbFirstCharacters.setVisible(bRL);
        this.rdbLevenshtein.setVisible(bRL);
        this.btnSearchId.setVisible(false);
        this.txfSearchId.setVisible(false);
                
        int rlType = RatingList.TYPE_UNDEFINED;
        if (bRL){
            if (this.rdbEGF.isSelected()) rlType = RatingList.TYPE_EGF;
            if (this.rdbFFG.isSelected()) rlType = RatingList.TYPE_FFG;
            if (this.rdbAGA.isSelected()) rlType = RatingList.TYPE_AGA;
        }
        // Save current rating list into Preferences
        Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
        prefs.put("defaultratinglist", "" + rlType);

        this.useRatingList(rlType);
                
        switch(rlType){
            case RatingList.TYPE_EGF :
                this.btnUpdateRatingList.setText("Update EGF rating list from ...");
                break;
            case RatingList.TYPE_FFG :
                this.btnUpdateRatingList.setText("Update FFG rating list from ...");
                break;
            case RatingList.TYPE_AGA :
                this.btnUpdateRatingList.setText("Update AGA rating list from ...");
                this.btnSearchId.setVisible(true);
                this.txfSearchId.setVisible(true);

                break;
            default :
                this.btnUpdateRatingList.setText("Update rating list");
        }        
        
        this.rdbRankFromGoR.setVisible(false);
        this.rdbRankFromGrade.setVisible(false);
        
        if (ratingList.getRatingListType() == RatingList.TYPE_EGF) {
            this.rdbRankFromGoR.setVisible(true);
            this.rdbRankFromGrade.setVisible(true);
        }
        if (ratingList.getRatingListType() == RatingList.TYPE_FFG) {
//            this.rdbRankFromGoR.setEnabled(true);
            this.rdbRankFromGoR.setSelected(true);
        }
        if (ratingList.getRatingListType() == RatingList.TYPE_AGA) {
//            this.rdbRankFromGoR.setEnabled(true);
            this.rdbRankFromGoR.setSelected(true);
        }
        
        if (ratingList.getRatingListType() == RatingList.TYPE_UNDEFINED) {           
            cbxRatingList.setEnabled(false);
            cbxRatingList.setVisible(true);
            txfPlayerNameChoice.setEnabled(false);
            txfPlayerNameChoice.setVisible(false);
            scpPlayerNameChoice.setEnabled(false);
            scpPlayerNameChoice.setVisible(false);
            lstPlayerNameChoice.setEnabled(false);
            lstPlayerNameChoice.setVisible(false);
            
            txfName.requestFocusInWindow();
        } else {
            if (rdbFirstCharacters.isSelected()) {
                resetControlsForFirstCharactersSearching();
            } else {
                resetControlsForLevenshteinSearching();
            }
            
        }
    }

    // Reset player related controls
    private void resetPlayerControls(){
        this.playerMode = JFrPlayersManager.PLAYER_MODE_NEW;
        txfName.setText("");
        txfFirstName.setText("");
//        txfRank.setText("30K");
        txfRank.setText("");
        txfSMMSCorrection.setText("0");
        txfRatingOrigin.setText("");
        txfRating.setText("");
        this.txfGrade.setText("");
        cbxCountry.setSelectedItem("  ");
        txfClub.setText("");
        txfFfgLicence.setText("");
        txfFfgLicenceStatus.setText("");
        lblFfgLicenceStatus.setText("");
        txfEgfPin.setText("");
        lblPhoto.setIcon(null);
        txfAgaId.setText("");
        lblAgaExpirationDate.setText("");
        lblAgaExpirationDate.setForeground(Color.BLACK);
        for (int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
            tabCkbParticipation[i].setSelected(true);
        }
        for (int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
            tabCkbParticipation[i].setEnabled(true);
        }
        Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
        String defRS = prefs.get("defaultregistration", "FIN" );
        if (defRS.equals("PRE")) this.rdbPreliminary.setSelected(true);
        else this.rdbFinal.setSelected(true);
        this.rdbPreliminary.setEnabled(true);
        this.rdbFinal.setEnabled(true);
        this.btnRegister.setText(("Register"));

        setPnlParticipationVisibility();
    }

    private void setPnlParticipationVisibility(){
        //  set pnlPartipation height to what is good for actual number of rounds
        GeneralParameterSet gps = null;
        try {
            gps = tournament.getTournamentParameterSet().getGeneralParameterSet();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        pnlParticipation.setSize(new Dimension(220, 30 + (gps.getNumberOfRounds() + 4) / 5 * 20));

        for (int i = 0; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
            if (i < gps.getNumberOfRounds()) {
                tabCkbParticipation[i].setVisible(true);
            } else {
                tabCkbParticipation[i].setVisible(false);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpRatingList = new javax.swing.ButtonGroup();
        grpAlgo = new javax.swing.ButtonGroup();
        grpSetRank = new javax.swing.ButtonGroup();
        grpRegistration = new javax.swing.ButtonGroup();
        pupRegisteredPlayers = new javax.swing.JPopupMenu();
        mniSortByName = new javax.swing.JMenuItem();
        mniSortByGrade = new javax.swing.JMenuItem();
        mniSortByRank = new javax.swing.JMenuItem();
        mniSortByRating = new javax.swing.JMenuItem();
        mniRemovePlayer = new javax.swing.JMenuItem();
        mniModifyPlayer = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mniCancel = new javax.swing.JMenuItem();
        pnlPlayer = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txfName = new javax.swing.JTextField();
        txfFirstName = new javax.swing.JTextField();
        txfRank = new javax.swing.JTextField();
        txfClub = new javax.swing.JTextField();
        txfFfgLicence = new javax.swing.JTextField();
        txfEgfPin = new javax.swing.JTextField();
        txfRatingOrigin = new javax.swing.JTextField();
        txfRating = new javax.swing.JTextField();
        txfFfgLicenceStatus = new javax.swing.JTextField();
        pnlParticipation = new javax.swing.JPanel();
        btnReset = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();
        pnlRegistration = new javax.swing.JPanel();
        rdbPreliminary = new javax.swing.JRadioButton();
        rdbFinal = new javax.swing.JRadioButton();
        lblRatingList = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblFfgLicenceStatus = new javax.swing.JLabel();
        rdbFirstCharacters = new javax.swing.JRadioButton();
        rdbLevenshtein = new javax.swing.JRadioButton();
        cbxRatingList = new javax.swing.JComboBox();
        txfPlayerNameChoice = new java.awt.TextField();
        scpPlayerNameChoice = new javax.swing.JScrollPane();
        lstPlayerNameChoice = new javax.swing.JList();
        txfSMMSCorrection = new javax.swing.JTextField();
        ckbWelcomeSheet = new javax.swing.JCheckBox();
        scpWelcomeSheet = new javax.swing.JScrollPane();
        txpWelcomeSheet = new javax.swing.JTextPane();
        cbxCountry = new javax.swing.JComboBox();
        pgbRatingList = new javax.swing.JProgressBar();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnChangeRating = new javax.swing.JButton();
        rdbRankFromGoR = new javax.swing.JRadioButton();
        rdbRankFromGrade = new javax.swing.JRadioButton();
        ckbRatingList = new javax.swing.JCheckBox();
        rdbFFG = new javax.swing.JRadioButton();
        rdbAGA = new javax.swing.JRadioButton();
        rdbEGF = new javax.swing.JRadioButton();
        btnUpdateRatingList = new javax.swing.JButton();
        btnSearchId = new javax.swing.JButton();
        txfSearchId = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txfAgaId = new javax.swing.JTextField();
        lblPhoto = new javax.swing.JLabel();
        lblAgaExpirationDate = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txfGrade = new javax.swing.JTextField();
        pnlPlayersList = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txfNbPlFin = new javax.swing.JTextField();
        txfNbPlPre = new javax.swing.JTextField();
        scpRegisteredPlayers = new javax.swing.JScrollPane();
        tblRegisteredPlayers = new javax.swing.JTable();
        btnPrint = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnHelp = new javax.swing.JButton();

        pupRegisteredPlayers.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        mniSortByName.setText("Sort by name");
        mniSortByName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByNameActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniSortByName);

        mniSortByGrade.setText("Sort by grade");
        mniSortByGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByGradeActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniSortByGrade);

        mniSortByRank.setText("Sort by rank");
        mniSortByRank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByRankActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniSortByRank);

        mniSortByRating.setText("Sort by rating");
        mniSortByRating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSortByRatingActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniSortByRating);

        mniRemovePlayer.setText("Remove player");
        mniRemovePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRemovePlayerActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniRemovePlayer);

        mniModifyPlayer.setText("Modify player");
        mniModifyPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniModifyPlayerActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniModifyPlayer);
        pupRegisteredPlayers.add(jSeparator5);

        mniCancel.setText("Cancel");
        mniCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCancelActionPerformed(evt);
            }
        });
        pupRegisteredPlayers.add(mniCancel);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Players Manager");
        setIconImage(getIconImage());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(null);

        pnlPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder("Player"));
        pnlPlayer.setLayout(null);

        jLabel2.setText("First name");
        pnlPlayer.add(jLabel2);
        jLabel2.setBounds(10, 280, 60, 14);

        jLabel3.setText("Origin");
        jLabel3.setToolTipText("from 30K to 9D");
        pnlPlayer.add(jLabel3);
        jLabel3.setBounds(120, 370, 40, 14);

        jLabel4.setText("Country");
        jLabel4.setToolTipText("Country where the player lives (2 letters)");
        pnlPlayer.add(jLabel4);
        jLabel4.setBounds(10, 310, 60, 14);

        jLabel5.setText("Club");
        jLabel5.setToolTipText("");
        pnlPlayer.add(jLabel5);
        jLabel5.setBounds(10, 330, 60, 14);

        jLabel6.setText("FFG Lic");
        pnlPlayer.add(jLabel6);
        jLabel6.setBounds(170, 435, 60, 15);

        jLabel7.setText("EGF PIN");
        pnlPlayer.add(jLabel7);
        jLabel7.setBounds(10, 435, 60, 14);

        txfName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfNameFocusLost(evt);
            }
        });
        pnlPlayer.add(txfName);
        txfName.setBounds(70, 250, 100, 20);

        txfFirstName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfFirstNameFocusLost(evt);
            }
        });
        pnlPlayer.add(txfFirstName);
        txfFirstName.setBounds(70, 280, 100, 20);

        txfRank.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txfRank.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfRankFocusLost(evt);
            }
        });
        pnlPlayer.add(txfRank);
        txfRank.setBounds(170, 405, 40, 20);
        pnlPlayer.add(txfClub);
        txfClub.setBounds(70, 330, 50, 20);
        pnlPlayer.add(txfFfgLicence);
        txfFfgLicence.setBounds(230, 435, 55, 20);
        pnlPlayer.add(txfEgfPin);
        txfEgfPin.setBounds(70, 435, 90, 20);

        txfRatingOrigin.setEditable(false);
        txfRatingOrigin.setFocusable(false);
        pnlPlayer.add(txfRatingOrigin);
        txfRatingOrigin.setBounds(170, 370, 70, 20);

        txfRating.setEditable(false);
        txfRating.setFocusable(false);
        pnlPlayer.add(txfRating);
        txfRating.setBounds(70, 370, 40, 20);

        txfFfgLicenceStatus.setEditable(false);
        txfFfgLicenceStatus.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txfFfgLicenceStatus.setFocusable(false);
        pnlPlayer.add(txfFfgLicenceStatus);
        txfFfgLicenceStatus.setBounds(290, 435, 15, 20);

        pnlParticipation.setBorder(javax.swing.BorderFactory.createTitledBorder("Participation"));
        pnlParticipation.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        pnlParticipation.setLayout(null);
        pnlPlayer.add(pnlParticipation);
        pnlParticipation.setBounds(260, 240, 200, 120);

        btnReset.setText("Reset");
        btnReset.setToolTipText("Reset form");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        pnlPlayer.add(btnReset);
        btnReset.setBounds(260, 470, 220, 30);

        btnRegister.setText("Register");
        btnRegister.setToolTipText("Register player into tournament");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });
        pnlPlayer.add(btnRegister);
        btnRegister.setBounds(10, 510, 474, 30);

        pnlRegistration.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Registration"));
        pnlRegistration.setLayout(null);

        grpRegistration.add(rdbPreliminary);
        rdbPreliminary.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbPreliminary.setText("Preliminary");
        pnlRegistration.add(rdbPreliminary);
        rdbPreliminary.setBounds(10, 14, 90, 21);

        grpRegistration.add(rdbFinal);
        rdbFinal.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        rdbFinal.setSelected(true);
        rdbFinal.setText("Final");
        pnlRegistration.add(rdbFinal);
        rdbFinal.setBounds(110, 14, 90, 21);

        pnlPlayer.add(pnlRegistration);
        pnlRegistration.setBounds(10, 465, 240, 40);

        lblRatingList.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblRatingList.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRatingList.setText("No rating list has been loaded yet");
        pnlPlayer.add(lblRatingList);
        lblRatingList.setBounds(260, 10, 220, 14);

        jLabel1.setText("Last name");
        pnlPlayer.add(jLabel1);
        jLabel1.setBounds(10, 250, 60, 14);

        lblFfgLicenceStatus.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblFfgLicenceStatus.setForeground(new java.awt.Color(255, 0, 102));
        lblFfgLicenceStatus.setText("statut licence");
        pnlPlayer.add(lblFfgLicenceStatus);
        lblFfgLicenceStatus.setBounds(230, 455, 90, 12);

        grpAlgo.add(rdbFirstCharacters);
        rdbFirstCharacters.setSelected(true);
        rdbFirstCharacters.setText("Compare first characters");
        rdbFirstCharacters.setEnabled(false);
        rdbFirstCharacters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbFirstCharactersActionPerformed(evt);
            }
        });
        pnlPlayer.add(rdbFirstCharacters);
        rdbFirstCharacters.setBounds(20, 140, 220, 20);

        grpAlgo.add(rdbLevenshtein);
        rdbLevenshtein.setText("Use Levenshtein algorithm");
        rdbLevenshtein.setEnabled(false);
        rdbLevenshtein.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLevenshteinActionPerformed(evt);
            }
        });
        pnlPlayer.add(rdbLevenshtein);
        rdbLevenshtein.setBounds(20, 160, 220, 20);

        cbxRatingList.setMaximumRowCount(9);
        cbxRatingList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "" }));
        cbxRatingList.setToolTipText("");
        cbxRatingList.setEnabled(false);
        cbxRatingList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxRatingListItemStateChanged(evt);
            }
        });
        pnlPlayer.add(cbxRatingList);
        cbxRatingList.setBounds(260, 30, 220, 20);

        txfPlayerNameChoice.setText("Enter approximate name and firstname");
        txfPlayerNameChoice.addTextListener(new java.awt.event.TextListener() {
            public void textValueChanged(java.awt.event.TextEvent evt) {
                txfPlayerNameChoiceTextValueChanged(evt);
            }
        });
        txfPlayerNameChoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txfPlayerNameChoiceKeyPressed(evt);
            }
        });
        pnlPlayer.add(txfPlayerNameChoice);
        txfPlayerNameChoice.setBounds(260, 30, 220, 30);

        lstPlayerNameChoice.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstPlayerNameChoiceValueChanged(evt);
            }
        });
        scpPlayerNameChoice.setViewportView(lstPlayerNameChoice);

        pnlPlayer.add(scpPlayerNameChoice);
        scpPlayerNameChoice.setBounds(260, 60, 220, 160);

        txfSMMSCorrection.setEditable(false);
        txfSMMSCorrection.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txfSMMSCorrection.setToolTipText("smms correction (relevant for McMahon super-groups)");
        txfSMMSCorrection.setFocusable(false);
        pnlPlayer.add(txfSMMSCorrection);
        txfSMMSCorrection.setBounds(220, 405, 20, 21);

        ckbWelcomeSheet.setText("Print Welcome sheet");
        ckbWelcomeSheet.setToolTipText("Welcome sheet can be edited in welcomesheet/welcomesheet.html");
        pnlPlayer.add(ckbWelcomeSheet);
        ckbWelcomeSheet.setBounds(10, 540, 220, 23);

        scpWelcomeSheet.setViewportView(txpWelcomeSheet);

        pnlPlayer.add(scpWelcomeSheet);
        scpWelcomeSheet.setBounds(0, 700, 840, 1188);

        cbxCountry.setEditable(true);
        cbxCountry.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxCountry.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        pnlPlayer.add(cbxCountry);
        cbxCountry.setBounds(70, 310, 50, 21);

        pgbRatingList.setStringPainted(true);
        pnlPlayer.add(pgbRatingList);
        pgbRatingList.setBounds(260, 30, 220, 17);

        jLabel10.setText("Rank");
        jLabel10.setToolTipText("from 30K to 9D");
        pnlPlayer.add(jLabel10);
        jLabel10.setBounds(120, 410, 50, 14);

        jLabel11.setText("Rating");
        jLabel11.setToolTipText("from 30K to 9D");
        pnlPlayer.add(jLabel11);
        jLabel11.setBounds(10, 370, 60, 14);

        btnChangeRating.setText("Change rating");
        btnChangeRating.setFocusable(false);
        btnChangeRating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeRatingActionPerformed(evt);
            }
        });
        pnlPlayer.add(btnChangeRating);
        btnChangeRating.setBounds(260, 370, 170, 23);

        grpSetRank.add(rdbRankFromGoR);
        rdbRankFromGoR.setSelected(true);
        rdbRankFromGoR.setText("set Rank from rating (GoR)");
        rdbRankFromGoR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbRankFromGoRActionPerformed(evt);
            }
        });
        pnlPlayer.add(rdbRankFromGoR);
        rdbRankFromGoR.setBounds(20, 185, 220, 20);

        grpSetRank.add(rdbRankFromGrade);
        rdbRankFromGrade.setText("set Rank from Grade");
        rdbRankFromGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbRankFromGradeActionPerformed(evt);
            }
        });
        pnlPlayer.add(rdbRankFromGrade);
        rdbRankFromGrade.setBounds(20, 205, 220, 20);

        ckbRatingList.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        ckbRatingList.setText("Use a rating list");
        ckbRatingList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processRatingListChangeEvent(evt);
            }
        });
        pnlPlayer.add(ckbRatingList);
        ckbRatingList.setBounds(20, 20, 220, 23);

        grpRatingList.add(rdbFFG);
        rdbFFG.setText("FFG");
        rdbFFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processRatingListChangeEvent(evt);
            }
        });
        pnlPlayer.add(rdbFFG);
        rdbFFG.setBounds(70, 60, 130, 23);

        grpRatingList.add(rdbAGA);
        rdbAGA.setText("AGA");
        rdbAGA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processRatingListChangeEvent(evt);
            }
        });
        pnlPlayer.add(rdbAGA);
        rdbAGA.setBounds(70, 80, 130, 23);

        grpRatingList.add(rdbEGF);
        rdbEGF.setSelected(true);
        rdbEGF.setText("EGF");
        rdbEGF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processRatingListChangeEvent(evt);
            }
        });
        pnlPlayer.add(rdbEGF);
        rdbEGF.setBounds(70, 40, 130, 23);

        btnUpdateRatingList.setText("Update rating list");
        btnUpdateRatingList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateRatingListActionPerformed(evt);
            }
        });
        pnlPlayer.add(btnUpdateRatingList);
        btnUpdateRatingList.setBounds(20, 110, 220, 23);

        btnSearchId.setText("Search by Id");
        btnSearchId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchIdActionPerformed(evt);
            }
        });
        pnlPlayer.add(btnSearchId);
        btnSearchId.setBounds(260, 200, 120, 20);
        pnlPlayer.add(txfSearchId);
        txfSearchId.setBounds(390, 200, 90, 20);

        jLabel12.setText("AGA ID");
        pnlPlayer.add(jLabel12);
        jLabel12.setBounds(330, 435, 60, 14);
        pnlPlayer.add(txfAgaId);
        txfAgaId.setBounds(390, 435, 90, 20);
        pnlPlayer.add(lblPhoto);
        lblPhoto.setBounds(170, 250, 80, 115);

        lblAgaExpirationDate.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblAgaExpirationDate.setForeground(new java.awt.Color(255, 0, 102));
        lblAgaExpirationDate.setText("expiration date");
        pnlPlayer.add(lblAgaExpirationDate);
        lblAgaExpirationDate.setBounds(390, 455, 90, 12);

        jLabel13.setText("Grade");
        pnlPlayer.add(jLabel13);
        jLabel13.setBounds(10, 405, 60, 14);

        txfGrade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfGradeFocusLost(evt);
            }
        });
        pnlPlayer.add(txfGrade);
        txfGrade.setBounds(70, 405, 40, 20);

        getContentPane().add(pnlPlayer);
        pnlPlayer.setBounds(10, 0, 494, 560);

        pnlPlayersList.setBorder(javax.swing.BorderFactory.createTitledBorder("Players"));
        pnlPlayersList.setLayout(null);

        jLabel8.setText("Registered players. Final (F)");
        pnlPlayersList.add(jLabel8);
        jLabel8.setBounds(60, 50, 250, 20);

        jLabel9.setText("Registered players. Preliminary (P)");
        pnlPlayersList.add(jLabel9);
        jLabel9.setBounds(60, 30, 250, 20);

        txfNbPlFin.setEditable(false);
        txfNbPlFin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        pnlPlayersList.add(txfNbPlFin);
        txfNbPlFin.setBounds(10, 50, 40, 20);

        txfNbPlPre.setEditable(false);
        txfNbPlPre.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        pnlPlayersList.add(txfNbPlPre);
        txfNbPlPre.setBounds(10, 30, 40, 20);

        scpRegisteredPlayers.setToolTipText("");

        tblRegisteredPlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "R", "Last name", "First name", "Co", "Club", "Rk", "Rating", "EGF Grade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRegisteredPlayers.setToolTipText("To modify, right click !");
        tblRegisteredPlayers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRegisteredPlayersMouseClicked(evt);
            }
        });
        tblRegisteredPlayers.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblRegisteredPlayersKeyPressed(evt);
            }
        });
        scpRegisteredPlayers.setViewportView(tblRegisteredPlayers);

        pnlPlayersList.add(scpRegisteredPlayers);
        scpRegisteredPlayers.setBounds(10, 80, 450, 390);

        btnPrint.setText("Print ...");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnlPlayersList.add(btnPrint);
        btnPrint.setBounds(10, 480, 450, 30);

        getContentPane().add(pnlPlayersList);
        pnlPlayersList.setBounds(510, 0, 470, 520);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose);
        btnClose.setBounds(640, 530, 330, 30);

        btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/gothalogo16.jpg"))); // NOI18N
        btnHelp.setText("help");
        btnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpActionPerformed(evt);
            }
        });
        getContentPane().add(btnHelp);
        btnHelp.setBounds(520, 530, 110, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txfFirstNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfFirstNameFocusLost
        txfFirstName.setText(normalizeCase(txfFirstName.getText()));
    }//GEN-LAST:event_txfFirstNameFocusLost

    private void txfNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfNameFocusLost
        txfName.setText(normalizeCase(txfName.getText()));
    }//GEN-LAST:event_txfNameFocusLost

    private String normalizeCase(String name) {
        StringBuilder sb = new StringBuilder();
        Pattern namePattern = Pattern.compile(
                "(?:(da|de|degli|del|der|di|el|la|le|ter|und|van|vom|von|zu|zum)" +
                "|(.+?))(?:\\b|(?=_))([- _]?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = namePattern.matcher(name.trim().toLowerCase());
        while (matcher.find()) {
            String noblePart = matcher.group(1);
            String namePart = matcher.group(2);
            String wordBreak = matcher.group(3);
            if (noblePart != null) {
                sb.append(noblePart);
            } else {
                sb.append(Character.toUpperCase(namePart.charAt(0)));
                sb.append(namePart.substring(1)); // always returns at least ""
            }
            if (wordBreak != null) {
                sb.append(wordBreak);
            }
        }
        return sb.toString();
    }

    private void tblRegisteredPlayersKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRegisteredPlayersKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedPlayer();
        }
    }//GEN-LAST:event_tblRegisteredPlayersKeyPressed

    private void mniModifyPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniModifyPlayerActionPerformed
        pupRegisteredPlayers.setVisible(false);
        modifySelectedPlayer();
    }//GEN-LAST:event_mniModifyPlayerActionPerformed

    private void modifySelectedPlayer() {
        resetRatingListControls();
        resetPlayerControls();
        this.playerMode = JFrPlayersManager.PLAYER_MODE_MODIF;

        // What player ?
        int row = tblRegisteredPlayers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please, select a player !");
            return;
        }
        String name = (String) this.tblRegisteredPlayers.getModel().getValueAt(row, JFrPlayersManager.NAME_COL);
        String firstName = (String) this.tblRegisteredPlayers.getModel().getValueAt(row, JFrPlayersManager.FIRSTNAME_COL);
        try {
            playerInModification = tournament.getPlayerByKeyString(name + firstName);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        updatePlayerControlsFromPlayerInModification();
        this.btnRegister.setText("Save modification");
    }

    private void mniRemovePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRemovePlayerActionPerformed
        pupRegisteredPlayers.setVisible(false);
        removeSelectedPlayer();
    }//GEN-LAST:event_mniRemovePlayerActionPerformed

    private void removeSelectedPlayer() {
        // What player ?
        int row = tblRegisteredPlayers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, ("Please, select a player !"));
            return;
        }
        String name = (String) this.tblRegisteredPlayers.getModel().getValueAt(row, JFrPlayersManager.NAME_COL);
        String firstName = (String) this.tblRegisteredPlayers.getModel().getValueAt(row, JFrPlayersManager.FIRSTNAME_COL);
        try {
            Player playerToRemove = tournament.getPlayerByKeyString(name + firstName);
            // You sure ?
            String strMessage = "Remove " + playerToRemove.fullName() + " ?";
            int rep = JOptionPane.showConfirmDialog(this, strMessage, "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                boolean b = tournament.removePlayer(playerToRemove);
                if (b) {
                    resetRatingListControls();
                    resetPlayerControls();
                    this.tournamentChanged();
                } else {
                    strMessage = "" + name + " " + firstName + "could not be removed";
                    JOptionPane.showMessageDialog(this, strMessage, "Message", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (TournamentException te) {
            JOptionPane.showMessageDialog(this, te.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mniSortByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByNameActionPerformed
        playersSortType = PlayerComparator.NAME_ORDER;
        pupRegisteredPlayers.setVisible(false);
        try {
            updatePnlRegisteredPlayers(tournament.playersList());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniSortByNameActionPerformed

    private void mniSortByRankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByRankActionPerformed
        playersSortType = PlayerComparator.RANK_ORDER;
        pupRegisteredPlayers.setVisible(false);
        try {
            updatePnlRegisteredPlayers(tournament.playersList());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mniSortByRankActionPerformed

    private void tblRegisteredPlayersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRegisteredPlayersMouseClicked
        // Double or multiple click
        if (evt.getClickCount() >= 2) {
            modifySelectedPlayer();
        }
        // Right click
        if (evt.getModifiers() != InputEvent.BUTTON3_MASK) return;
        Point p = evt.getLocationOnScreen();
        pupRegisteredPlayers.setLocation(p);
        pupRegisteredPlayers.setVisible(true);        
    }//GEN-LAST:event_tblRegisteredPlayersMouseClicked

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        TournamentPrinting.printPlayersList(tournament, playersSortType);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void txfPlayerNameChoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfPlayerNameChoiceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            lstPlayerNameChoice.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            lstPlayerNameChoice.requestFocusInWindow();
        }
    }//GEN-LAST:event_txfPlayerNameChoiceKeyPressed

    private void lstPlayerNameChoiceValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstPlayerNameChoiceValueChanged
        String strItem = (String) lstPlayerNameChoice.getSelectedValue();
        if (strItem == null) {
            resetPlayerControls();
            return;
        }
        String strNumber = strItem.substring(3, 8).trim();
        int number = new Integer(strNumber).intValue();
        this.updatePlayerControlsFromRatingList(number);
    }//GEN-LAST:event_lstPlayerNameChoiceValueChanged

    private void txfPlayerNameChoiceTextValueChanged(java.awt.event.TextEvent evt) {//GEN-FIRST:event_txfPlayerNameChoiceTextValueChanged
        String str = txfPlayerNameChoice.getText().toLowerCase();
        if (str.length() == 0) {
            resetPlayerControls();
            return;
        }
        int pos = str.indexOf(" ");
        String str1;
        String str2;
        if (pos < 0) {
            str1 = str;
            str2 = "";
        } else {
            str1 = str.substring(0, pos);
            if (str.length() <= pos + 1) {
                str2 = "";
            } else {
                str2 = str.substring(pos + 1, str.length());
            }
        }

        Vector<String> vS = new Vector<String>();

        for (int iRP = 0; iRP < ratingList.getALRatedPlayers().size(); iRP++) {
            RatedPlayer rP = ratingList.getALRatedPlayers().get(iRP);
            String strName = rP.getName().toLowerCase();
            String strFirstName = rP.getFirstName().toLowerCase();
            int dn1 = RatedPlayer.distance_Levenshtein(str1, strName);
            int df1 = RatedPlayer.distance_Levenshtein(str2, strFirstName);
            int dn2 = RatedPlayer.distance_Levenshtein(str2, strName);
            int df2 = RatedPlayer.distance_Levenshtein(str1, strFirstName);
            int d = Math.min(dn1 + df1, dn2 + df2);
            int threshold = 9;
            if (d <= threshold) {
                String strNumber = "" + iRP;
                while (strNumber.length() < 5) {
                    strNumber = " " + strNumber;
                }
                vS.addElement("(" + d + ")" + strNumber + " " + rP.getName() + " " + rP.getFirstName() + " " +
                        rP.getCountry() + " " + rP.getClub() + " " + rP.getStrRawRating());
            }
        }
        if (vS.isEmpty()) {
            resetPlayerControls();
        } else {
            Collections.sort(vS);
            lstPlayerNameChoice.setListData(vS);
            lstPlayerNameChoice.setVisible(true);
            scpPlayerNameChoice.setVisible(true);
            lstPlayerNameChoice.setSelectedIndex(0);
        }
    }//GEN-LAST:event_txfPlayerNameChoiceTextValueChanged

    private void rdbLevenshteinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLevenshteinActionPerformed
//        LogElements.incrementElement("players.manager.levenshtein", "");
        this.resetControlsForLevenshteinSearching();

    }//GEN-LAST:event_rdbLevenshteinActionPerformed

    private void rdbFirstCharactersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbFirstCharactersActionPerformed
        this.resetControlsForFirstCharactersSearching();
    }//GEN-LAST:event_rdbFirstCharactersActionPerformed

    private void cbxRatingListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxRatingListItemStateChanged
        int index = cbxRatingList.getSelectedIndex();
        if (index <= 0) {
            resetPlayerControls();
        } else {
            updatePlayerControlsFromRatingList(index - 1);
        }
    }//GEN-LAST:event_cbxRatingListItemStateChanged

    private void useRatingList(int typeRatingList) {
        switch (typeRatingList) {
            case RatingList.TYPE_EGF:
                lblRatingList.setText("Searching for EGF rating list");
                ratingList = new RatingList(RatingList.TYPE_EGF, new File(Gotha.runningDirectory, "ratinglists/egf_db.txt"));
                break;
            case RatingList.TYPE_FFG:
                lblRatingList.setText("Searching for FFG rating list");
//                ratingList = new RatingList(RatingList.TYPE_FFG, new File(Gotha.runningDirectory, "ratinglists/ech_ffg_new.txt"));
                ratingList = new RatingList(RatingList.TYPE_FFG, new File(Gotha.runningDirectory, "ratinglists/ech_ffg_V3.txt"));
                break;
            case RatingList.TYPE_AGA:
                lblRatingList.setText("Searching for AGA rating list");
                ratingList = new RatingList(RatingList.TYPE_AGA, new File(Gotha.runningDirectory, "ratinglists/tdlista.txt"));
                break;
            default:
                ratingList = new RatingList();
        }
        int nbPlayersInRL = ratingList.getALRatedPlayers().size();
        cbxRatingList.removeAllItems();
        cbxRatingList.addItem("");
        for (RatedPlayer rP : ratingList.getALRatedPlayers()) {
            cbxRatingList.addItem(this.ratingList.getRatedPlayerString(rP));        
            
        }
        if (nbPlayersInRL == 0) {
            ratingList.setRatingListType(RatingList.TYPE_UNDEFINED);
            lblRatingList.setText("No rating list has been loaded yet");
        } else {
            String strType = "";
            this.rdbFirstCharacters.setEnabled(true);
            this.rdbLevenshtein.setEnabled(true);

            switch (ratingList.getRatingListType()) {
                case RatingList.TYPE_EGF:
                    strType = "EGF rating list";
                    break;
                case RatingList.TYPE_FFG:
                    strType = "FFG rating list";
                    break;
                case RatingList.TYPE_AGA:
                    strType = "AGA rating list";
                    break;
            }
            lblRatingList.setText(strType + " " +
                    ratingList.getStrPublicationDate() +
                    " " + nbPlayersInRL + " players");
        }

    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetRatingListControls();
        resetPlayerControls();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        manageRankGradeAndRatingValues(); // Before anything else, fill unfilled grade/rank/rating fields
        txfFirstName.setText(normalizeCase(txfFirstName.getText()));
        txfName.setText(normalizeCase(txfName.getText()));

        Player p;

        String strRegistration = "FIN";
        if (grpRegistration.getSelection() == rdbPreliminary.getModel()) {
            strRegistration = "PRE";
        }

        int rating;
        int rank = Player.convertKDPToInt(txfRank.getText());

        String strOrigin;
        try{
            strOrigin = txfRatingOrigin.getText().substring(0, 3);
            rating = new Integer(txfRating.getText()).intValue();
        }catch(Exception e){
            strOrigin = "INI";
            rating = Player.ratingFromRank(rank);
        }
        
        int smmsCorrection;
        try {
            String strCorr = txfSMMSCorrection.getText();
            if (strCorr.substring(0, 1).equals("+")) strCorr = strCorr.substring(1);
            smmsCorrection = Integer.parseInt(strCorr);
        } catch (NumberFormatException ex) {
            smmsCorrection = 0;
        }

        try {
            p = new Player(
                    txfName.getText(),
                    txfFirstName.getText(),
                    ((String)cbxCountry.getSelectedItem()).trim(),
                    txfClub.getText().trim(),
                    txfEgfPin.getText(),
                    txfFfgLicence.getText(),
                    txfFfgLicenceStatus.getText(),
                    txfAgaId.getText(),
                    lblAgaExpirationDate.getText(),
                    rank,
                    rating,
                    strOrigin,
                    this.txfGrade.getText(),
                    smmsCorrection,
                    strRegistration);

            boolean[] bPart = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
            
            int nbRounds = 0;
            try {
                nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int i = 0; i < nbRounds; i++) {
                bPart[i] = tabCkbParticipation[i].isSelected();
            }
           for (int i = nbRounds; i < Gotha.MAX_NUMBER_OF_ROUNDS; i++) {
                bPart[i] = tabCkbParticipation[nbRounds - 1].isSelected();
            }
        p.setParticipating(bPart);
        } catch (PlayerException pe) {
            JOptionPane.showMessageDialog(this, pe.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (this.playerMode == JFrPlayersManager.PLAYER_MODE_NEW) {
            try {
                tournament.addPlayer(p);
                // Keep current registration status as default registration status
                strRegistration = "FIN";
                if (grpRegistration.getSelection() == rdbPreliminary.getModel()) strRegistration = "PRE";
                Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
                prefs.put("defaultregistration", strRegistration);

                resetRatingListControls();
                resetPlayerControls();
                this.tournamentChanged();
            } catch (TournamentException te) {
                JOptionPane.showMessageDialog(this, te.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
                resetRatingListControls();
                return;
            } catch (RemoteException ex) {
                resetRatingListControls();
                return;
            }

        } else if (this.playerMode == JFrPlayersManager.PLAYER_MODE_MODIF) {
            try {
                if (tournament.isPlayerImplied(p)){
                    p.setRegisteringStatus("FIN");
                }
                tournament.modifyPlayer(playerInModification, p);
                resetRatingListControls();
            } catch (RemoteException ex) {
                resetRatingListControls();
                Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TournamentException ex) {
                resetRatingListControls();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.tournamentChanged();
            resetPlayerControls();
            
        }
        // Print Welcome sheet
        if (this.ckbWelcomeSheet.isSelected()) {
//            LogElements.incrementElement("players.manager.welcomesheet", "");
            instanciateWelcomeSheet(new File(Gotha.runningDirectory, "welcomesheet/welcomesheet.html"), 
                    new File(Gotha.runningDirectory, "welcomesheet/actualwelcomesheet.html"), p);
            try {
                URL url = new File(Gotha.runningDirectory, "welcomesheet/actualwelcomesheet.html").toURI().toURL();
                txpWelcomeSheet.setPage(url);
            } catch (IOException ex) {
                Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            PageAttributes pa = new PageAttributes();
            pa.setPrinterResolution(100);
            pa.setOrigin(OriginType.PRINTABLE);
            PrintJob pj = getToolkit().getPrintJob(this, "Welcome Sheet", null, pa);
            if (pj != null) {
                Graphics pg = pj.getGraphics();
                txpWelcomeSheet.print(pg);
                pg.dispose();
                pj.end();
            }

        }
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
        Gotha.displayGothaHelp("Players Manager frame");
}//GEN-LAST:event_btnHelpActionPerformed

    private void mniCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCancelActionPerformed
        this.pupRegisteredPlayers.setVisible(false);
        this.tblRegisteredPlayers.removeRowSelectionInterval(0, tblRegisteredPlayers.getRowCount() - 1);
}//GEN-LAST:event_mniCancelActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.pupRegisteredPlayers.setVisible(false);
        
        this.ratingList = null;
        Runtime.getRuntime().gc();
    }//GEN-LAST:event_formWindowClosed

    private void btnChangeRatingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeRatingActionPerformed
        int oldRating;
        try{
            oldRating = Integer.parseInt(this.txfRating.getText());   
        }
        catch(NumberFormatException e){
            oldRating = 0;
        }
        
        String strMessage = "Enter new rating (" + Player.MIN_RATING + " <= rating <= " + Player.MAX_RATING + ")";
        String strResponse = JOptionPane.showInputDialog(strMessage);
        int newRating = oldRating;
        try{
            newRating = Integer.parseInt(strResponse);
            if (newRating < Player.MIN_RATING) newRating = Player.MIN_RATING;
            if (newRating > Player.MAX_RATING) newRating = Player.MAX_RATING;
        }catch(Exception e){
            newRating = oldRating;    
        }
        
        if (newRating != oldRating){
            this.txfRating.setText("" + newRating);
            this.txfRatingOrigin.setText("MAN");
        }
    }//GEN-LAST:event_btnChangeRatingActionPerformed

    private void txfRankFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfRankFocusLost
//        String strRank = this.txfRank.getText();
//        int rank = Player.convertKDPToInt(strRank);
//        this.txfRank.setText(Player.convertIntToKD(rank));
//        
//        // update rating from rank
//        if (this.txfRating.getText().equals("")){
//            int rating = rank * 100 + 2100;
//            this.txfRating.setText("" + rating);
//            this.txfRatingOrigin.setText("INI");
//        }
        
        this.manageRankGradeAndRatingValues();
    }//GEN-LAST:event_txfRankFocusLost

    private void rdbRankFromGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbRankFromGradeActionPerformed
        resetRatingListControls();
    }//GEN-LAST:event_rdbRankFromGradeActionPerformed

    private void rdbRankFromGoRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbRankFromGoRActionPerformed
        resetRatingListControls();
    }//GEN-LAST:event_rdbRankFromGoRActionPerformed

    private void processRatingListChangeEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processRatingListChangeEvent
        this.resetRatingListControls();
        

    }//GEN-LAST:event_processRatingListChangeEvent

    private void btnUpdateRatingListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateRatingListActionPerformed
        int rlType = RatingList.TYPE_UNDEFINED;
        if (!Gotha.isRatingListsDownloadEnabled()){
            String strMessage = "Access to Rating lists is currently disabled.\nSee Options .. Preferences menu item";
            JOptionPane.showMessageDialog(this, strMessage, "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (this.rdbEGF.isSelected()) rlType = RatingList.TYPE_EGF;
        if (this.rdbFFG.isSelected()) rlType = RatingList.TYPE_FFG;
        if (this.rdbAGA.isSelected()) rlType = RatingList.TYPE_AGA;

//        LogElements.incrementElement("players.manager.updateratinglist", "" + rlType);
        
        String strDefaultURL;
        File fDefaultFile;
        String strPrompt;
        
        switch(rlType){
            case RatingList.TYPE_EGF:
                strDefaultURL = "http://www.europeangodatabase.eu/EGD/EGD_2_0/downloads/allworld_lp.html";
                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/egf_db.txt");
                strPrompt = "Download EGF Rating List from :";
                break;
            case RatingList.TYPE_FFG:
//                strDefaultURL = "http://ffg.jeudego.org/echelle/echtxt/ech_ffg_new.txt";
                strDefaultURL = "http://ffg.jeudego.org/echelle/echtxt/ech_ffg_V3.txt";
//                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/ech_ffg_new.txt");
                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/ech_ffg_V3.txt");
                strPrompt = "Download FFG Rating List from :";
                break;
            case RatingList.TYPE_AGA:
                strDefaultURL = "https://usgo.org/mm/tdlista.txt";
                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/tdlista.txt");
                strPrompt = "Download AGA Rating List from :";
                break;
            default:
                System.out.println("btnUpdateRatingListActionPerformed : Internal error");
                return;
        }
        
        try {
            String str = JOptionPane.showInputDialog(strPrompt, strDefaultURL);
            if (str == null ) return;
            this.lblRatingList.setText("Download in progress");
            lblRatingList.paintImmediately(0, 0, lblRatingList.getWidth(), lblRatingList.getHeight());
            Gotha.download(this.pgbRatingList, str, fDefaultFile);
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "Malformed URL\nRating list could not be loaded", "Message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unreachable file\nRating list could not be loaded", "Message", JOptionPane.ERROR_MESSAGE);
        }
        useRatingList(rlType);        

    }//GEN-LAST:event_btnUpdateRatingListActionPerformed

    private void txfGradeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfGradeFocusLost
        this.txfGrade.setText(txfGrade.getText().toUpperCase());
        manageRankGradeAndRatingValues();
    }//GEN-LAST:event_txfGradeFocusLost

    private void mniSortByGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByGradeActionPerformed
        playersSortType = PlayerComparator.GRADE_ORDER;
        pupRegisteredPlayers.setVisible(false);
        try {
            updatePnlRegisteredPlayers(tournament.playersList());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }//GEN-LAST:event_mniSortByGradeActionPerformed

    private void mniSortByRatingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSortByRatingActionPerformed
        playersSortType = PlayerComparator.RATING_ORDER;
        pupRegisteredPlayers.setVisible(false);
        try {
            updatePnlRegisteredPlayers(tournament.playersList());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }//GEN-LAST:event_mniSortByRatingActionPerformed

    private void btnSearchIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchIdActionPerformed
        String strId = this.txfSearchId.getText();
        strId = strId.trim();
        int iRP = this.ratingList.getRatedPlayerByAGAID(strId);
        if (iRP < 0) return;
        this.updatePlayerControlsFromRatingList(iRP);
        
        
    }//GEN-LAST:event_btnSearchIdActionPerformed
    
    private void manageRankGradeAndRatingValues(){
        if (txfRank.getText().equals("") && !txfGrade.getText().equals("")){
            int r = Player.convertKDPToInt(txfGrade.getText());
            txfRank.setText(Player.convertIntToKD(r));
        }
        if (txfGrade.getText().equals("") && !txfRank.getText().equals("")){
            txfGrade.setText(txfRank.getText());
        }
        
        String strRank = this.txfRank.getText();
        if (strRank.equals("")) return;
        int rank = Player.convertKDPToInt(strRank);
        if (this.txfRating.getText().equals("")){
            int rating = rank * 100 + 2100;
            this.txfRating.setText("" + rating);
            this.txfRatingOrigin.setText("INI");
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeRating;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnHelp;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearchId;
    private javax.swing.JButton btnUpdateRatingList;
    private javax.swing.JComboBox cbxCountry;
    private javax.swing.JComboBox cbxRatingList;
    private javax.swing.JCheckBox ckbRatingList;
    private javax.swing.JCheckBox ckbWelcomeSheet;
    private javax.swing.ButtonGroup grpAlgo;
    private javax.swing.ButtonGroup grpRatingList;
    private javax.swing.ButtonGroup grpRegistration;
    private javax.swing.ButtonGroup grpSetRank;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JLabel lblAgaExpirationDate;
    private javax.swing.JLabel lblFfgLicenceStatus;
    private javax.swing.JLabel lblPhoto;
    private javax.swing.JLabel lblRatingList;
    private javax.swing.JList lstPlayerNameChoice;
    private javax.swing.JMenuItem mniCancel;
    private javax.swing.JMenuItem mniModifyPlayer;
    private javax.swing.JMenuItem mniRemovePlayer;
    private javax.swing.JMenuItem mniSortByGrade;
    private javax.swing.JMenuItem mniSortByName;
    private javax.swing.JMenuItem mniSortByRank;
    private javax.swing.JMenuItem mniSortByRating;
    private javax.swing.JProgressBar pgbRatingList;
    private javax.swing.JPanel pnlParticipation;
    private javax.swing.JPanel pnlPlayer;
    private javax.swing.JPanel pnlPlayersList;
    private javax.swing.JPanel pnlRegistration;
    private javax.swing.JPopupMenu pupRegisteredPlayers;
    private javax.swing.JRadioButton rdbAGA;
    private javax.swing.JRadioButton rdbEGF;
    private javax.swing.JRadioButton rdbFFG;
    private javax.swing.JRadioButton rdbFinal;
    private javax.swing.JRadioButton rdbFirstCharacters;
    private javax.swing.JRadioButton rdbLevenshtein;
    private javax.swing.JRadioButton rdbPreliminary;
    private javax.swing.JRadioButton rdbRankFromGoR;
    private javax.swing.JRadioButton rdbRankFromGrade;
    private javax.swing.JScrollPane scpPlayerNameChoice;
    private javax.swing.JScrollPane scpRegisteredPlayers;
    private javax.swing.JScrollPane scpWelcomeSheet;
    private javax.swing.JTable tblRegisteredPlayers;
    private javax.swing.JTextField txfAgaId;
    private javax.swing.JTextField txfClub;
    private javax.swing.JTextField txfEgfPin;
    private javax.swing.JTextField txfFfgLicence;
    private javax.swing.JTextField txfFfgLicenceStatus;
    private javax.swing.JTextField txfFirstName;
    private javax.swing.JTextField txfGrade;
    private javax.swing.JTextField txfName;
    private javax.swing.JTextField txfNbPlFin;
    private javax.swing.JTextField txfNbPlPre;
    private java.awt.TextField txfPlayerNameChoice;
    private javax.swing.JTextField txfRank;
    private javax.swing.JTextField txfRating;
    private javax.swing.JTextField txfRatingOrigin;
    private javax.swing.JTextField txfSMMSCorrection;
    private javax.swing.JTextField txfSearchId;
    private javax.swing.JTextPane txpWelcomeSheet;
    // End of variables declaration//GEN-END:variables
    // Custom variable declarations. Editable
    private javax.swing.JCheckBox[] tabCkbParticipation;
    // End of custom variables declaration

    public void resetControlsForFirstCharactersSearching() {
        this.txfPlayerNameChoice.setVisible(false);
        this.scpPlayerNameChoice.setVisible(false);
        this.lstPlayerNameChoice.setVisible(false);
        this.cbxRatingList.setVisible(true);
        this.cbxRatingList.setEnabled(true);
        cbxRatingList.setSelectedIndex(0);
        cbxRatingList.requestFocusInWindow();
    }

    public void resetControlsForLevenshteinSearching() {
        this.cbxRatingList.setVisible(false);
        this.txfPlayerNameChoice.setVisible(true);
        this.txfPlayerNameChoice.setEnabled(true);
        this.lstPlayerNameChoice.setVisible(true);
        this.lstPlayerNameChoice.setEnabled(true);
        String strInvite = "Enter approximate name and first name";
        this.txfPlayerNameChoice.setText(strInvite);
        txfPlayerNameChoice.selectAll();
        txfPlayerNameChoice.requestFocusInWindow();
    }

    public void updatePlayerControlsFromRatingList(int index) {
        this.resetPlayerControls();
        RatedPlayer rP = ratingList.getALRatedPlayers().get(index);
        txfName.setText(rP.getName());
        txfFirstName.setText(rP.getFirstName());
        int stdRating = rP.getStdRating();
        txfRating.setText("" + stdRating);
        String strRatingOrigin = rP.getRatingOrigin();
        if (strRatingOrigin.equals("FFG")) strRatingOrigin += " : " + rP.getStrRawRating();
        if (strRatingOrigin.equals("AGA")) strRatingOrigin += " : " + rP.getStrRawRating();
        txfRatingOrigin.setText(strRatingOrigin);
        this.txfSMMSCorrection.setText("" + 0);
        int rank = Player.rankFromRating(stdRating);
        if (this.rdbRankFromGrade.isSelected()) rank = Player.convertKDPToInt(rP.getStrGrade());
        txfRank.setText(Player.convertIntToKD(rank));
        txfGrade.setText(rP.getStrGrade());
        
        cbxCountry.setSelectedItem(rP.getCountry());
        txfClub.setText(rP.getClub());
        txfFfgLicence.setText(rP.getFfgLicence());
        txfFfgLicenceStatus.setText(rP.getFfgLicenceStatus());
//        if (rP.getFfgLicenceStatus().compareTo("-") == 0) {
//            lblFfgLicenceStatus.setText("Non licencié");
//        } else {
//            lblFfgLicenceStatus.setText("");
//        }
        if (rP.getFfgLicenceStatus().compareTo("-") == 0) {
            lblFfgLicenceStatus.setText("Non licencié");
            lblFfgLicenceStatus.setForeground(Color.RED);
        }
        else if (rP.getFfgLicenceStatus().compareTo("C") == 0){
            lblFfgLicenceStatus.setText("Licence loisir"); 
            lblFfgLicenceStatus.setForeground(Color.BLUE);
        }
        else {
            lblFfgLicenceStatus.setText("");
            lblFfgLicenceStatus.setForeground(Color.BLACK);
        }

        String strEGFPin = rP.getEgfPin(); 
        txfEgfPin.setText(strEGFPin);
        if (strEGFPin != null && strEGFPin.length() == 8 && Gotha.isPhotosDownloadEnabled())
            GothaImageLoader.loadImage("http://www.europeangodatabase.eu/EGD/Actions.php?key=" + strEGFPin, lblPhoto);

        this.txfAgaId.setText(rP.getAgaId());
        String strDate = rP.getAgaExpirationDate();
        lblAgaExpirationDate.setText(strDate);
        if (Gotha.isDateExpired(strDate)) lblAgaExpirationDate.setForeground(Color.red);

    }

    /**
     * Fills player controls with playerInModification fields
     */
    public void updatePlayerControlsFromPlayerInModification() {
        this.resetPlayerControls();
        this.playerMode = JFrPlayersManager.PLAYER_MODE_MODIF;
        txfName.setText(playerInModification.getName());
        txfFirstName.setText(playerInModification.getFirstName());

        int rating = playerInModification.getRating();
        txfRating.setText("" + rating);
        String strRatingOrigin = playerInModification.getRatingOrigin();
        if (strRatingOrigin.equals("FFG")) strRatingOrigin += " : " + playerInModification.getStrRawRating();
        if (strRatingOrigin.equals("AGA")) strRatingOrigin += " : " + playerInModification.getStrRawRating();
        txfRatingOrigin.setText(strRatingOrigin);
        txfGrade.setText(playerInModification.getStrGrade());
        
        int corr = playerInModification.getSmmsCorrection();
        String strCorr = "" + corr;
        if (corr > 0 ) strCorr = "+" + corr;
        this.txfSMMSCorrection.setText(strCorr);
        int rank = (playerInModification.getRank());
        txfRank.setText(Player.convertIntToKD(rank));
        cbxCountry.setSelectedItem(playerInModification.getCountry());
        txfClub.setText(playerInModification.getClub());
        txfFfgLicence.setText(playerInModification.getFfgLicence());
        txfFfgLicenceStatus.setText(playerInModification.getFfgLicenceStatus());

        if (playerInModification.getFfgLicenceStatus().compareTo("-") == 0) {
            lblFfgLicenceStatus.setText("Non licencié");
            lblFfgLicenceStatus.setForeground(Color.RED);
        }
        else if (playerInModification.getFfgLicenceStatus().compareTo("C") == 0){
            lblFfgLicenceStatus.setText("Licence loisir"); 
            lblFfgLicenceStatus.setForeground(Color.BLUE);
        }
        else {
            lblFfgLicenceStatus.setText("");
            lblFfgLicenceStatus.setForeground(Color.BLACK);
        }
        
        String strEGFPin = playerInModification.getEgfPin(); 
        txfEgfPin.setText(strEGFPin);
        if (strEGFPin != null && strEGFPin.length() == 8 && Gotha.isPhotosDownloadEnabled())
            GothaImageLoader.loadImage("http://www.europeangodatabase.eu/EGD/Actions.php?key=" + strEGFPin, lblPhoto);

        
        txfAgaId.setText(playerInModification.getAgaId());
        String strDate = playerInModification.getAgaExpirationDate();
        lblAgaExpirationDate.setText(strDate);
        if (Gotha.isDateExpired(strDate)) lblAgaExpirationDate.setForeground(Color.red);
       
        boolean[] bPart = playerInModification.getParticipating();
        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            tabCkbParticipation[r].setSelected(bPart[r]);
        }
        if (playerInModification.getRegisteringStatus().compareTo("FIN") == 0) {
            this.rdbFinal.setSelected(true);
        } else {
            this.rdbPreliminary.setSelected(true);
        }
        boolean bImplied = false;
        try {
            bImplied = tournament.isPlayerImplied(playerInModification);
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.rdbPreliminary.setEnabled(!bImplied);
        this.rdbFinal.setEnabled(!bImplied);

        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            try {
                tabCkbParticipation[r].setEnabled(!tournament.isPlayerImpliedInRound(playerInModification, r));
            } catch (RemoteException ex) {
                Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void instanciateWelcomeSheet(File templateFile, File actualFile, Player p) {
        Vector<String> vLines = new Vector<String>();
        try {
            FileInputStream fis = new FileInputStream(templateFile);
            BufferedReader d = new BufferedReader(new InputStreamReader(fis, java.nio.charset.Charset.forName("UTF-8")));

            String s;
            do {
                s = d.readLine();
                if (s != null) {
                    vLines.add(s);
                }
            } while (s != null);
            d.close();
            fis.close();
        } catch (Exception ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Replace player tags
        Vector<String> vActualLines = new Vector<String>();
        for (String strLine : vLines) {
            if (strLine.length() == 0) {
                continue;
            }
            strLine = strLine.replaceAll("<name>", p.getName());
            strLine = strLine.replaceAll("<firstname>", p.getFirstName());
            strLine = strLine.replaceAll("<country>", p.getCountry());
            strLine = strLine.replaceAll("<club>", p.getClub());
            strLine = strLine.replaceAll("<rank>", Player.convertIntToKD(p.getRank()));
            int rawRating = p.getRating();
            String ratingOrigin = p.getRatingOrigin();
            if (ratingOrigin.compareTo("FFG") == 0) {
                rawRating -= 2050;
            }
            strLine = strLine.replaceAll("<rating>", Integer.valueOf(rawRating).toString());
            strLine = strLine.replaceAll("<ratingorigin>", ratingOrigin);
            boolean[] bPart = p.getParticipating();
            String strPart = "";
            int nbRounds = 0;
            try {
                nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int r = 0; r < nbRounds; r++) {
                if (bPart[r]) {
                    strPart += " " + (r + 1);
                } else {
                    strPart += " -";
                }
            }
            strLine = strLine.replaceAll("<participation>", strPart);
            vActualLines.add(strLine);
        }

        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(actualFile));
        } catch (IOException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        for (String strLine : vActualLines) {
            try {
                output.write(strLine + "\n");
            } catch (IOException ex) {
                Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tournamentChanged() {
        try {
            tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateAllViews();
    }

    private void updateAllViews() {
        try {
            if (!tournament.isOpen()) dispose();
            this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();
            setTitle("Players Manager. " + tournament.getFullName());
            updatePnlRegisteredPlayers(tournament.playersList());
            setPnlParticipationVisibility();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}