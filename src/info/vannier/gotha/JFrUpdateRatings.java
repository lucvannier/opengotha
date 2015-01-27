/*
 * JFrUpdateRatings.java
 */
package info.vannier.gotha;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.*;

/**
 *
 * @author Luc
 */
public class JFrUpdateRatings extends javax.swing.JFrame {
    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;

    private static final int NAME_COL           = 0;
    private static final int FIRSTNAME_COL      = NAME_COL + 1;
    private static final int COUNTRY_COL        = FIRSTNAME_COL + 1;
    private static final int CLUB_COL           = COUNTRY_COL + 1;
    private static final int RANK_COL           = CLUB_COL + 1;
    public static  final int RATINGORIGIN_COL   = RANK_COL + 1;
    public static  final int RATING_COL         = RATINGORIGIN_COL + 1;
    public static  final int NEWRATING_COL      = RATING_COL + 1;
    public static  final int EGFPIN_COL         = NEWRATING_COL + 1;
    public static  final int RATINGLIST_COL     = EGFPIN_COL + 1;
    
    private int playersSortType = PlayerComparator.NAME_ORDER;
    private ArrayList<Player> alSelectedPlayersToKeepSelected = new ArrayList<Player>();     
    
    private TournamentInterface tournament;
    
    /** Rating List */
    private RatingList ratingList = new RatingList();
    
    int activeRow = 0;


    /** Creates new form JFrUpdateRatings */
    public JFrUpdateRatings(TournamentInterface tournament) throws RemoteException{
//        LogElements.incrementElement("players.ur", "");
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
        int w = JFrGotha.MEDIUM_FRAME_WIDTH;
        int h = JFrGotha.MEDIUM_FRAME_HEIGHT;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - w) / 2, (dim.height - h) / 2, w, h);

        setIconImage(Gotha.getIconImage());

        this.pgbRatingList.setVisible(false);
 
        updateRatingList(RatingList.TYPE_EGF);
        initPnlPlayers();

        updateAllViews();
        
        getRootPane().setDefaultButton(this.btnUpdateSelRatings);
    }

    private void initPnlPlayers()throws RemoteException{
 
        TableColumnModel tcm = this.tblPlayers.getColumnModel();
        JFrGotha.formatColumn(this.tblPlayers, NAME_COL, "Last name", 110, JLabel.LEFT, JLabel.LEFT); 
        JFrGotha.formatColumn(this.tblPlayers, FIRSTNAME_COL, "First name", 70, JLabel.LEFT, JLabel.LEFT); 
        JFrGotha.formatColumn(this.tblPlayers, COUNTRY_COL, "Co", 30, JLabel.LEFT, JLabel.LEFT); 
        JFrGotha.formatColumn(this.tblPlayers, CLUB_COL, "Club", 40, JLabel.LEFT, JLabel.LEFT); 
        JFrGotha.formatColumn(this.tblPlayers, RANK_COL, "Rk", 30, JLabel.RIGHT, JLabel.RIGHT); 
        JFrGotha.formatColumn(this.tblPlayers, RATINGORIGIN_COL, "Ori", 30, JLabel.RIGHT, JLabel.RIGHT); 
        JFrGotha.formatColumn(this.tblPlayers, RATING_COL, "Rt", 40, JLabel.RIGHT, JLabel.RIGHT); 
        JFrGotha.formatColumn(this.tblPlayers, NEWRATING_COL, "EGF Rt", 40, JLabel.RIGHT, JLabel.RIGHT); 
        JFrGotha.formatColumn(this.tblPlayers, EGFPIN_COL, "Pin",70, JLabel.LEFT, JLabel.LEFT);                
        JFrGotha.formatColumn(this.tblPlayers, RATINGLIST_COL, "EGF Rating List", 220, JLabel.CENTER, JLabel.CENTER); 

        this.cbxRatingList.setVisible(false);
    }

    private void updateRatingList(int typeRatingList) {
        ratingList = new RatingList(RatingList.TYPE_EGF, new File(Gotha.runningDirectory, "ratinglists/egf_db.txt"));
        cbxRatingList.removeAllItems();
        cbxRatingList.addItem("");
        ArrayList<RatedPlayer> alRP = ratingList.getALRatedPlayers(); 
        for (RatedPlayer rP : alRP) {
            cbxRatingList.addItem(rP.getName() + " " + rP.getFirstName() + " " +
                    rP.getCountry() + " " + rP.getClub() + " " + rP.getStrRawRating());
        }
        
        if (alRP.isEmpty()) {
            ratingList.setRatingListType(RatingList.TYPE_UNDEFINED);
            lblRatingList.setText("No rating list has been loaded yet");
        } else {
            String strType = "EGF rating list";
            lblRatingList.setText(strType + " " +
                    ratingList.getStrPublicationDate() +
                    " " + alRP.size() + " players");
        }

    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnHelp = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        pnlPlayersList = new javax.swing.JPanel();
        cbxRatingList = new javax.swing.JComboBox();
        btnPrint = new javax.swing.JButton();
        scpPlayers = new javax.swing.JScrollPane();
        tblPlayers = new javax.swing.JTable();
        btnUpdateAllRatings = new javax.swing.JButton();
        btnUpdateSelRatings = new javax.swing.JButton();
        btnUpdateEGFRatingList = new javax.swing.JButton();
        pgbRatingList = new javax.swing.JProgressBar();
        lblRatingList = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Update ratings");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(null);

        btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/gothalogo16.jpg"))); // NOI18N
        btnHelp.setText("help");
        btnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpActionPerformed(evt);
            }
        });
        getContentPane().add(btnHelp);
        btnHelp.setBounds(50, 470, 110, 30);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose);
        btnClose.setBounds(200, 470, 550, 30);

        pnlPlayersList.setBorder(javax.swing.BorderFactory.createTitledBorder("Players"));
        pnlPlayersList.setLayout(null);

        cbxRatingList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxRatingList.setFocusCycleRoot(true);
        cbxRatingList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxRatingListItemStateChanged(evt);
            }
        });
        pnlPlayersList.add(cbxRatingList);
        cbxRatingList.setBounds(10, 410, 220, 20);

        btnPrint.setText("Print ...");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnlPlayersList.add(btnPrint);
        btnPrint.setBounds(10, 430, 690, 20);

        tblPlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Last name", "First name", "Co", "Club", "Rk", "Rating", "Rating origin", "New Rating", "EGFPin", "RatingList"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPlayers.setToolTipText("");
        tblPlayers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPlayersMouseClicked(evt);
            }
        });
        tblPlayers.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                tblPlayersComponentMoved(evt);
            }
        });
        scpPlayers.setViewportView(tblPlayers);

        pnlPlayersList.add(scpPlayers);
        scpPlayers.setBounds(10, 60, 690, 290);

        btnUpdateAllRatings.setText("For all players, update obsolete ratings (red) with EGF rating (blue)");
        btnUpdateAllRatings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAllRatingsActionPerformed(evt);
            }
        });
        pnlPlayersList.add(btnUpdateAllRatings);
        btnUpdateAllRatings.setBounds(10, 360, 690, 20);

        btnUpdateSelRatings.setText("For selected players, update obsolete ratings (red) with EGF rating (blue)");
        btnUpdateSelRatings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSelRatingsActionPerformed(evt);
            }
        });
        pnlPlayersList.add(btnUpdateSelRatings);
        btnUpdateSelRatings.setBounds(10, 390, 690, 20);

        btnUpdateEGFRatingList.setText("update EGF rating list from ...");
        btnUpdateEGFRatingList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateEGFRatingListActionPerformed(evt);
            }
        });
        pnlPlayersList.add(btnUpdateEGFRatingList);
        btnUpdateEGFRatingList.setBounds(450, 20, 250, 20);

        pgbRatingList.setStringPainted(true);
        pnlPlayersList.add(pgbRatingList);
        pgbRatingList.setBounds(450, 40, 250, 17);

        lblRatingList.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblRatingList.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRatingList.setText("No rating list has been loaded yet");
        pnlPlayersList.add(lblRatingList);
        lblRatingList.setBounds(480, 40, 220, 14);

        getContentPane().add(pnlPlayersList);
        pnlPlayersList.setBounds(43, 10, 710, 460);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
        Gotha.displayGothaHelp("Update ratings frame");
}//GEN-LAST:event_btnHelpActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        TournamentPrinting.printPlayersList(tournament, playersSortType);
}//GEN-LAST:event_btnPrintActionPerformed

    private void tblPlayersComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblPlayersComponentMoved
             cbxRatingList.setVisible(false);
    }//GEN-LAST:event_tblPlayersComponentMoved

    private void tblPlayersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlayersMouseClicked
        int r = tblPlayers.rowAtPoint(evt.getPoint());
        int c = tblPlayers.columnAtPoint(evt.getPoint());
        
        activeRow = r;
        if (c != JFrUpdateRatings.RATINGLIST_COL ||
                r < 0){
            cbxRatingList.setVisible(false);
            return;
        }
        
        Rectangle rect = this.tblPlayers.getCellRect(r, c, true);
        int headerHeight = tblPlayers.getTableHeader().getHeight();
        Point pCel_TBL = new Point(rect.x, rect.y + headerHeight);
        
        Point pTBL_SCP = tblPlayers.getLocation();
        Point pCel_SCP = new Point(pCel_TBL.x + pTBL_SCP.x, pCel_TBL.y + pTBL_SCP.y);
        
        Point pSCP_PNL = scpPlayers.getLocation();
        Point pCel_PNL = new Point(pCel_SCP.x + pSCP_PNL.x, pCel_SCP.y + pSCP_PNL.y);
        
        cbxRatingList.setLocation(pCel_PNL);
        cbxRatingList.setVisible(true);
        
        // Search for a rated player
        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();
        String egfPin = (String)model.getValueAt(r, JFrUpdateRatings.EGFPIN_COL);
        RatedPlayer rp = ratingList.getRatedPlayer(egfPin);
        if (rp == null){
            String name = (String)model.getValueAt(r, JFrUpdateRatings.NAME_COL);
            String firstName = (String)model.getValueAt(r, JFrUpdateRatings.FIRSTNAME_COL);
            rp = ratingList.getRatedPlayer(name, firstName);
            updateRLCellsWithRP(r, rp);
        }        
        int index = 0;
        if (rp!= null)index = ratingList.indexOf(rp) + 1;
        cbxRatingList.setSelectedIndex(index);
        
        cbxRatingList.setEnabled(true);
        cbxRatingList.requestFocusInWindow();
    }//GEN-LAST:event_tblPlayersMouseClicked

    private void updateRLCellsWithRP(int row, RatedPlayer rp){
        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();

        String strRating = "????";
        if (rp!=null) strRating = "" + rp.getStdRating();
        
        String strPin = "";
        if (rp!=null) strPin = rp.getEgfPin();
        
        String strRatedPlayerString = "--Click here to search for the player--";
        if (rp != null) strRatedPlayerString = ratingList.getRatedPlayerString(rp);
        
        model.setValueAt(strRating, row, JFrUpdateRatings.NEWRATING_COL);
        model.setValueAt(strPin, row, JFrUpdateRatings.EGFPIN_COL);
        model.setValueAt(strRatedPlayerString, row, JFrUpdateRatings.RATINGLIST_COL);
    }
    
    private ArrayList<Player> selectedPlayersList(JTable tbl){
        ArrayList<Player> alSelectedPlayers = new ArrayList<Player>();
       
        // gather selected players into alSelectedPlayers
        for ( int iRow = 0; iRow < tbl.getModel().getRowCount(); iRow++){
            if (tbl.isRowSelected(iRow)){
                String name = (String)tbl.getModel().getValueAt(iRow, NAME_COL);
                String firstName = (String)tbl.getModel().getValueAt(iRow, FIRSTNAME_COL);
                Player p = null;
                try {
                    p = tournament.getPlayerByKeyString(name + firstName);
                } catch (RemoteException ex) {
                    Logger.getLogger(JFrPlayersQuickCheck.class.getName()).log(Level.SEVERE, null, ex);
                }
                alSelectedPlayers.add(p);
            }
        }
        return alSelectedPlayers;
    }


    private void btnUpdateAllRatingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateAllRatingsActionPerformed
        cbxRatingList.setVisible(false);
        ArrayList<Player> alP = null;
        try {
            alP = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrUpdateRatings.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (alP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please, select at least one player", "Message",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.updateRatings(alP);
        
        this.tournamentChanged();
    }//GEN-LAST:event_btnUpdateAllRatingsActionPerformed

    private void updateRatings(ArrayList<Player> alP){
        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();

        for (Player p : alP){
            // Find the player's row
            int row = -1;
            for (int r = 0; r < tblPlayers.getRowCount(); r++){
                String name = (String)model.getValueAt(r, JFrUpdateRatings.NAME_COL);
                if (!name.equals(p.getName())) continue;
                String firstName = (String)model.getValueAt(r, JFrUpdateRatings.FIRSTNAME_COL);
                if (!firstName.equals(p.getFirstName())) continue;
                row = r;
                break;
            }
            String egfPin = (String)model.getValueAt(row, JFrUpdateRatings.EGFPIN_COL);
            if (egfPin.equals("")){
                continue;
            }
            try{
                String strNewRating = (String)model.getValueAt(row, JFrUpdateRatings.NEWRATING_COL);
                int newRating = Integer.parseInt(strNewRating);
                if (newRating == p.getRating()){
                    p.setEgfPin(egfPin);               
                    p.setRatingOrigin("EGF");
                }
                else{                
                    p.setEgfPin(egfPin);               
                    p.setRatingOrigin("EGF");
                    p.setRating(newRating);
                }
                tournament.modifyPlayer(p, p);
            }catch(Exception e){
                continue;
            }
        }
    }
    
    
    private void btnUpdateSelRatingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSelRatingsActionPerformed
        this.cbxRatingList.setVisible(false);
        ArrayList<Player> alP = this.selectedPlayersList(this.tblPlayers);
        // Keep a track of selected Players
        alSelectedPlayersToKeepSelected = new ArrayList<Player>(alP);
        
        if (alP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please, select at least one player", "Message",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.updateRatings(alP);
        
        this.tournamentChanged();
    }//GEN-LAST:event_btnUpdateSelRatingsActionPerformed

    private void cbxRatingListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxRatingListItemStateChanged
        // What rated player?
        int index = cbxRatingList.getSelectedIndex();  
        RatedPlayer rp = ratingList.getRatedPlayer(index - 1);

        this.updateRLCellsWithRP(activeRow, rp);
        this.cbxRatingList.setEnabled(true);
    }//GEN-LAST:event_cbxRatingListItemStateChanged

    private void btnUpdateEGFRatingListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateEGFRatingListActionPerformed
        try {
            String strDefaultEGFURL = "http://www.europeangodatabase.eu/EGD/EGD_2_0/downloads/allworld_lp.html";
            File fDefaultEGFFile = new File(Gotha.runningDirectory, "ratinglists/egf_db.txt");
            String str = JOptionPane.showInputDialog("Download EGF Rating List from :", strDefaultEGFURL);
            this.lblRatingList.setText("Download in progress");
            lblRatingList.paintImmediately(0, 0, lblRatingList.getWidth(), lblRatingList.getHeight());
            Gotha.download(this.pgbRatingList, str, fDefaultEGFFile);
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "Malformed URL\nRating list could not be loaded", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unreachable file\nRating list could not be loaded", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        updateRatingList(RatingList.TYPE_EGF);
        this.updateAllViews();
    }//GEN-LAST:event_btnUpdateEGFRatingListActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.ratingList = null;
        Runtime.getRuntime().gc();
    }//GEN-LAST:event_formWindowClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnHelp;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnUpdateAllRatings;
    private javax.swing.JButton btnUpdateEGFRatingList;
    private javax.swing.JButton btnUpdateSelRatings;
    private javax.swing.JComboBox cbxRatingList;
    private javax.swing.JLabel lblRatingList;
    private javax.swing.JProgressBar pgbRatingList;
    private javax.swing.JPanel pnlPlayersList;
    private javax.swing.JScrollPane scpPlayers;
    private javax.swing.JTable tblPlayers;
    // End of variables declaration//GEN-END:variables


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
            setTitle("Update ratings. " + tournament.getFullName());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrPlayersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateComponents();
    }
    
    private void updateComponents(){
        ArrayList<Player> playersList = null;
        try {
            playersList = tournament.playersList();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrUpdateRatings.class.getName()).log(Level.SEVERE, null, ex);
        }
        updatePnlPlayers(playersList);
               
        AutoCompletion.enable(cbxRatingList);

    }
    
    private void updatePnlPlayers(ArrayList<Player> playersList){
        
        this.pnlPlayersList.setVisible(true);

        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();
     
        ArrayList<Player> displayedPlayersList = new ArrayList<Player>(playersList);
        
        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(displayedPlayersList, playerComparator);

        model.setRowCount(displayedPlayersList.size());

        for (Player p:displayedPlayersList){
            int line = displayedPlayersList.indexOf(p); 
            model.setValueAt(p.getName(), line, JFrUpdateRatings.NAME_COL);
            model.setValueAt(p.getFirstName(), line, JFrUpdateRatings.FIRSTNAME_COL);
            model.setValueAt(p.getCountry(), line, JFrUpdateRatings.COUNTRY_COL);
            model.setValueAt(p.getClub(), line, JFrUpdateRatings.CLUB_COL);           
            model.setValueAt(Player.convertIntToKD(p.getRank()), line, JFrUpdateRatings.RANK_COL);
            model.setValueAt(p.getRating(), line, JFrUpdateRatings.RATING_COL); 
            model.setValueAt(p.getRatingOrigin(), line, JFrUpdateRatings.RATINGORIGIN_COL); 
            
            //Find the player in EGF rating list
            RatedPlayer rp = ratingList.getRatedPlayer(p);
            updateRLCellsWithRP(line, rp);
        }
        
        for (int nCol = 0; nCol < this.tblPlayers.getColumnCount(); nCol++){
            TableColumn col = tblPlayers.getColumnModel().getColumn(nCol);
            col.setCellRenderer(new PlayersURTableCellRenderer());
        }
        
        // Reselect players that may have been deselected by this update
        for (Player p:alSelectedPlayersToKeepSelected){
            int iSel = displayedPlayersList.indexOf(p);
            if ( iSel >= 0) tblPlayers.addRowSelectionInterval(iSel, iSel);
        }             
    }
}

class PlayersURTableCellRenderer extends JLabel implements TableCellRenderer {
    // This method is called each time a cell in a column
    // using this renderer needs to be rendered.
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {

        if (value instanceof Component) 
            return (Component)value;
               
        Component comp = new DefaultTableCellRenderer().getTableCellRendererComponent(table,  value, isSelected, hasFocus, rowIndex, colIndex);
        
        TableModel model = table.getModel();
        if (colIndex == JFrUpdateRatings.RATING_COL){           
            Integer nRating = (Integer)model.getValueAt(rowIndex, JFrUpdateRatings.RATING_COL);
            int rating = nRating.intValue();
            String ratingOrigin = (String)model.getValueAt(rowIndex, JFrUpdateRatings.RATINGORIGIN_COL);
            
            int newRating = -9999;
            try{
                String strNewRating = (String)model.getValueAt(rowIndex, JFrUpdateRatings.NEWRATING_COL);
                newRating = Integer.parseInt(strNewRating);
            }catch(Exception e){
                newRating = -9999;
            }
            if (newRating != -9999){            
                boolean bSame = true;
                if (newRating != rating) bSame = false;
                if (!ratingOrigin.equals("EGF")) bSame = false;
                if (bSame) 
                    comp.setForeground(Color.BLACK);
                else 
                    comp.setForeground(Color.RED);
            }
        }

       if (colIndex == JFrUpdateRatings.NEWRATING_COL){           
            Integer nRating = (Integer)model.getValueAt(rowIndex, JFrUpdateRatings.RATING_COL);
            int rating = nRating.intValue();
            String ratingOrigin = (String)model.getValueAt(rowIndex, JFrUpdateRatings.RATINGORIGIN_COL);
            
            int newRating = -9999;
            try{
                String strNewRating = (String)model.getValueAt(rowIndex, JFrUpdateRatings.NEWRATING_COL);
                newRating = Integer.parseInt(strNewRating);
            }catch(Exception e){
                newRating = -9999;
            }
            if (newRating != -9999){            
                boolean bSame = true;
                if (newRating != rating) bSame = false;
                if (!ratingOrigin.equals("EGF")) bSame = false;
                if (bSame) 
                    comp.setForeground(Color.BLACK);
                else 
                    comp.setForeground(Color.BLUE);
            }
            else{
                comp.setForeground(Color.RED);
            }
        }
        
        
        if (colIndex == JFrUpdateRatings.NEWRATING_COL ||
            colIndex == JFrUpdateRatings.EGFPIN_COL ||
            colIndex == JFrUpdateRatings.RATINGLIST_COL)
                comp.setBackground(Color.LIGHT_GRAY);
        
        return comp;
    }
}