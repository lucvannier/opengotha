package info.vannier.gotha;

import static info.vannier.gotha.IPLoc.readStringFromURL;
import info.vannier.util.GothaDate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Luc
 */
public class JFrRemoteTournaments extends javax.swing.JFrame {
    private static final int RVM_COL = 0;
    private static final int SHORTNAME_COL = RVM_COL + 1;
    private static final int BEGINDATE_COL = SHORTNAME_COL + 1;
    private static final int SAVEDT_COL = BEGINDATE_COL + 1;
    private static final int LOCATION_COL = SAVEDT_COL + 1;
    private static final int DIRECTOR_COL = LOCATION_COL + 1;
    private static final int NBROUNDS_COL = DIRECTOR_COL + 1;
    private static final int NBPLAYERS_COL = NBROUNDS_COL + 1;
    private static final int IP_COL = NBPLAYERS_COL + 1;
    private static final int IPLOC_COL = IP_COL + 1;
    
    private JFrGotha jfrG; // The JFrGotha object that has created the JFrRemoteTournaments object
    
    private ArrayList<TournamentInterface> alTournaments;
    /**
     * Creates new form JFrRemoteTournaments
     */
    public JFrRemoteTournaments() {
        this.alTournaments = new ArrayList<TournamentInterface>();
        initComponents();
        customInitComponents();

    }
        public JFrRemoteTournaments(JFrGotha jfrG) {
        this.jfrG = jfrG;
        this.alTournaments = new ArrayList<TournamentInterface>();
        initComponents();
        customInitComponents();
    }
 
    private void customInitComponents(){
        setIconImage(Gotha.getIconImage());
        setTitle("Remote tournaments");
        try {
            initPnlTournaments();
            
       } catch (RemoteException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        private void initPnlTournaments() throws RemoteException {    
            JFrGotha.formatColumn(tblTournaments, RVM_COL,       "RVM",             60,  JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, SHORTNAME_COL, "Short name",      100, JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, BEGINDATE_COL, "Begin date",      60,  JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, SAVEDT_COL,    "Save date/time",  100, JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, LOCATION_COL,  "Location",        80,  JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, DIRECTOR_COL,  "Director",        80,  JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, NBROUNDS_COL,  "Nb rounds",       20,  JLabel.CENTER, JLabel.CENTER);
            JFrGotha.formatColumn(tblTournaments, NBPLAYERS_COL, "Nb players",      40,  JLabel.CENTER, JLabel.CENTER);
            JFrGotha.formatColumn(tblTournaments, IP_COL,        "IP",              80,  JLabel.LEFT, JLabel.LEFT);
            JFrGotha.formatColumn(tblTournaments, IPLOC_COL,     "IP Location",     120, JLabel.LEFT, JLabel.LEFT);
            
        // Single selection
        tblTournaments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        downloadTournaments();     
        this.updateAllViews();
    }

    private void updatePnlTournaments() { 
//        System.out.println("updatePnlTournaments. Debut");
        DefaultTableModel model = (DefaultTableModel) tblTournaments.getModel();
        
        int nbT = this.alTournaments.size();
//        System.out.println("nbT = " + nbT);
        model.setRowCount(this.alTournaments.size());
        
        String strPreviousSN = "";
        String strPreviousBD = "";

        for (TournamentInterface t : alTournaments) {
            int line = alTournaments.indexOf(t);
            try {
                String strRRM = "---";
                strRRM = t.getRemoteRunningMode();
                String strRVM = t.getRemoteFullVersionNumber()+ "_" + strRRM;
                model.setValueAt(strRVM, line, JFrRemoteTournaments.RVM_COL);    
                GeneralParameterSet gps = t.getTournamentParameterSet().getGeneralParameterSet();
                String shortName = t.getShortName();
                String strBeginDate = (new SimpleDateFormat("yyyy-MM-dd")).format(gps.getBeginDate());  

                model.setValueAt("", line, JFrRemoteTournaments.SHORTNAME_COL);
                model.setValueAt("", line, JFrRemoteTournaments.BEGINDATE_COL);    
                 
                if(!(shortName.equals(strPreviousSN)) || !(strBeginDate.equals(strPreviousBD))){
                    strPreviousSN = shortName;
                    strPreviousBD = strBeginDate;
                    model.setValueAt(t.getShortName(), line, JFrRemoteTournaments.SHORTNAME_COL);
                    model.setValueAt(strBeginDate, line, JFrRemoteTournaments.BEGINDATE_COL);    
                }
                String strSaveDT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(t.getSaveDT());
                String externalIPAddress = t.getExternalIPAddress();
                model.setValueAt(strSaveDT, line, JFrRemoteTournaments.SAVEDT_COL);    
                model.setValueAt(gps.getLocation(), line, JFrRemoteTournaments.LOCATION_COL);
                model.setValueAt(gps.getDirector(), line, JFrRemoteTournaments.DIRECTOR_COL);
                model.setValueAt(gps.getNumberOfRounds(), line, JFrRemoteTournaments.NBROUNDS_COL);
                model.setValueAt(t.numberOfPlayers(), line, JFrRemoteTournaments.NBPLAYERS_COL);
                model.setValueAt(externalIPAddress, line, JFrRemoteTournaments.IP_COL);
                
                // Constituer un ArrayList
                String strLoc = readStringFromURL(externalIPAddress);

                String strCity;        
                String strCountry;        

                if (strLoc == null){
                    strCity = "???";
                    strCountry = "???";
                }
                else{
                    strCity = IPLoc.getCityFromLoc(strLoc);
                    strCountry = IPLoc.getCountryFromLoc(strLoc); 
                }
                String strCityCountry = strCity + "," + strCountry;
                
                model.setValueAt(strCityCountry, line, JFrRemoteTournaments.IPLOC_COL);

            } catch (RemoteException ex) {
                Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
 
    /**
     * Downloads the file specified by strURL and copies it to fDestination
     * returns the tounament found in this file 
     */
    private TournamentInterface downloadTournament(String strURL, File fDestination){
        PrintWriter out = null;
        try {
            out = new PrintWriter(fDestination);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            URL urlTournament = new URL(strURL);
            try(BufferedReader in = new BufferedReader(new InputStreamReader(urlTournament.openStream(), StandardCharsets.UTF_8))){
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    if (inputLine.length() < 3) continue;
                    out.println(inputLine);
                }
            } catch (IOException ex) {
                Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        out.close();
        TournamentInterface t = null;
        try {
            t = new Tournament();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ExternalDocument.importTournamentFromXMLFile(fDestination, t, true, true, true, true, true);
        
        return t;
    }
    
/**
     * scans http://vannier.info/opengotha/tournaments and gets directory names
     * scans The directories and gets tournament files 
     * downloads tournament files and gathers them into alTournaments.
     * However, the tournaments is already present in /remote, no actual download
     */
    
    private void downloadTournaments(){
        alTournaments.clear();
        String strDir = "tournaments";
        String strURL = "http://vannier.info/opengotha/fileslist.php?dirName=" + strDir;

        ArrayList<String> alDir;
        alDir = new ArrayList<>();
        try {
            URL urlTournament = new URL(strURL);
            try(BufferedReader in = new BufferedReader(new InputStreamReader(urlTournament.openStream()))){
                String inputLine = "";
                while ((inputLine = in.readLine()) != null){
                    if(inputLine.indexOf(' ', 0) != -1) continue;
                    if(inputLine.length() <= 9) continue; // because yyyymmdd_ = 9
                    if (!inputLine.substring(0, 1).equals("2")) continue;
                    // also eliminate tournaments with an old begin date
                    if (this.rdbRecentTournaments.isSelected()){
                        String strBG = inputLine.substring(0, 8);
                        Date bgDate = GothaDate.parse(strBG, "yyyyMMdd");
                        Date curDate = new Date();
                        long diff = curDate.getTime() - bgDate.getTime();
                        long nbDays = diff / 1000 / 60 / 60 / 24;
                        if (nbDays >= 8) continue;
                    }
                    
                    alDir.add(inputLine);
                }
            } catch(java.net.UnknownHostException ex){
                JOptionPane.showMessageDialog(this, "Host not found.\nCheck your Internet connection", "Message", JOptionPane.ERROR_MESSAGE);

            } catch (IOException ex) {
                Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }catch (MalformedURLException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        // sort alDir
        Collections.sort(alDir);
        
        for(String strSubDir : alDir){
            strURL = "http://vannier.info/opengotha/fileslist.php?dirName=tournaments/" + strSubDir;
            ArrayList<String> alTournamentsNames = new ArrayList<>();
            try{
                URL urlTournament = new URL(strURL);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlTournament.openStream()));
                long mostRecent = 0;
                String inputLine;
                while ((inputLine = in.readLine()) != null){ 
                    int length = inputLine.length();
                    if (length <= 19) continue; // because yyyymmddhhmmss_ = 15 + .xml = 4 
                    if (!inputLine.substring(length-4, length).equals(".xml")) continue;
                    if (!inputLine.substring(0, 1).equals("2")) continue;

                    String strTournament = inputLine;
                    if(this.rdbLastVersions.isSelected()){
                        String strDT = strTournament.substring(0, 14);
                        long dt;
                        try{
                            dt = Long.parseLong(strDT);
                        }
                        catch(NumberFormatException ex)
                        {
                            System.out.println("NumberFormatException. strDT = " + strDT);
                            return;
                        }
                        if (dt > mostRecent){
                          mostRecent = dt;
                          alTournamentsNames = new ArrayList<>();
                          alTournamentsNames.add(strTournament);
                      }
                   }
                   else{
                      alTournamentsNames.add(strTournament);
                   }
                }
            } catch (IOException ex) {
                 Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (String strTournament: alTournamentsNames) {
                boolean success = (new File(Gotha.runningDirectory + "/remote")).mkdirs();
                String strTournamentURL = "http://opengotha.info/tournaments/" + strSubDir + "/" + strTournament;
                
                File fDestination = new File(Gotha.runningDirectory + "/remote/" + strTournament);
                if (fDestination.exists()){    
                    TournamentInterface t = null;
                    try {
                        t = Gotha.getTournamentFromFile(fDestination);
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    alTournaments.add(t);
                }
                else{
                    TournamentInterface t = downloadTournament(strTournamentURL, fDestination);
                    alTournaments.add(t);
                }
            }
        }
        return;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpVersions = new javax.swing.ButtonGroup();
        grpTournaments = new javax.swing.ButtonGroup();
        pnlTournaments = new javax.swing.JScrollPane();
        tblTournaments = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        pnlWhatTournaments = new javax.swing.JPanel();
        rdbAllVersions = new javax.swing.JRadioButton();
        rdbLastVersions = new javax.swing.JRadioButton();
        rdbAllTournaments = new javax.swing.JRadioButton();
        rdbRecentTournaments = new javax.swing.JRadioButton();
        btnOpenTournament = new javax.swing.JButton();
        btnHelp = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        tblTournaments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "RVM", "Short name", "Begin date", "Save date/time", "Location", "Director", "Nb rounds", "Nb players", "IP", "IP Location"
            }
        ));
        pnlTournaments.setViewportView(tblTournaments);

        getContentPane().add(pnlTournaments);
        pnlTournaments.setBounds(20, 170, 950, 320);

        btnRefresh.setText("Refresh tournaments list");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        getContentPane().add(btnRefresh);
        btnRefresh.setBounds(370, 30, 240, 120);

        pnlWhatTournaments.setBorder(javax.swing.BorderFactory.createTitledBorder("What tournaments ?"));
        pnlWhatTournaments.setLayout(null);

        grpVersions.add(rdbAllVersions);
        rdbAllVersions.setText("All versions");
        pnlWhatTournaments.add(rdbAllVersions);
        rdbAllVersions.setBounds(20, 80, 300, 23);

        grpVersions.add(rdbLastVersions);
        rdbLastVersions.setSelected(true);
        rdbLastVersions.setText("Last version ");
        rdbLastVersions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLastVersionsActionPerformed(evt);
            }
        });
        pnlWhatTournaments.add(rdbLastVersions);
        rdbLastVersions.setBounds(20, 100, 300, 23);

        grpTournaments.add(rdbAllTournaments);
        rdbAllTournaments.setText("All tournaments");
        pnlWhatTournaments.add(rdbAllTournaments);
        rdbAllTournaments.setBounds(20, 20, 300, 23);

        grpTournaments.add(rdbRecentTournaments);
        rdbRecentTournaments.setSelected(true);
        rdbRecentTournaments.setText(" Recent tournaments (<= 7 days)");
        pnlWhatTournaments.add(rdbRecentTournaments);
        rdbRecentTournaments.setBounds(20, 40, 300, 20);

        getContentPane().add(pnlWhatTournaments);
        pnlWhatTournaments.setBounds(20, 20, 340, 130);

        btnOpenTournament.setText("Open the selected tournament");
        btnOpenTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTournamentActionPerformed(evt);
            }
        });
        getContentPane().add(btnOpenTournament);
        btnOpenTournament.setBounds(620, 130, 350, 23);

        btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info/vannier/gotha/gothalogo16.jpg"))); // NOI18N
        btnHelp.setText("help");
        btnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpActionPerformed(evt);
            }
        });
        getContentPane().add(btnHelp);
        btnHelp.setBounds(20, 500, 360, 30);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose);
        btnClose.setBounds(390, 500, 580, 30);

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Experimental");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(680, 40, 220, 50);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        downloadTournaments();
         this.updateAllViews(); 
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void rdbLastVersionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLastVersionsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbLastVersionsActionPerformed

    private void btnOpenTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTournamentActionPerformed
        int row = this.tblTournaments.getSelectedRow();
        if (row < 0){
            JOptionPane.showMessageDialog(null, "Please, select a tournament", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
       
        TournamentInterface curTournament = this.jfrG.getTournament();

        TournamentInterface t = alTournaments.get(row);
        
        if (curTournament != null){
            String strFN = null;
            try {
                strFN = curTournament.getFullName();
            } catch (RemoteException ex) {
                Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
            }
            int response = JOptionPane.showConfirmDialog(this,
                "Current tournament : " + strFN + " will be closed\nDo you want to carry on ?",
                    "Message",
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION);
            if (response == JOptionPane.CANCEL_OPTION) return;                
        }

        try {
            if (curTournament != null) curTournament.close();
        } catch (RemoteException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jfrG.setTournament(t);
        
        try {
            t.setLastTournamentModificationTime(t.getCurrentTournamentTime());
        } catch (RemoteException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            String strFN = t.getFullName();
            JOptionPane.showMessageDialog(this, strFN + " is now the current tournament");
        } catch (RemoteException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnOpenTournamentActionPerformed

    private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
        Gotha.displayGothaHelp("Remote tournaments frame");
    }//GEN-LAST:event_btnHelpActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        cleanClose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cleanClose();
    }//GEN-LAST:event_formWindowClosing
    
    private void cleanClose(){
        dispose();
    }

    private void updateAllViews() {
        this.updatePnlTournaments();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnHelp;
    private javax.swing.JButton btnOpenTournament;
    private javax.swing.JButton btnRefresh;
    private javax.swing.ButtonGroup grpTournaments;
    private javax.swing.ButtonGroup grpVersions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane pnlTournaments;
    private javax.swing.JPanel pnlWhatTournaments;
    private javax.swing.JRadioButton rdbAllTournaments;
    private javax.swing.JRadioButton rdbAllVersions;
    private javax.swing.JRadioButton rdbLastVersions;
    private javax.swing.JRadioButton rdbRecentTournaments;
    private javax.swing.JTable tblTournaments;
    // End of variables declaration//GEN-END:variables
}
