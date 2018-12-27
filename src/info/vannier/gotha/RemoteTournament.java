/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.vannier.gotha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luc
 */
public class RemoteTournament {
    public static ArrayList<TournamentInterface> downloadTournaments(boolean bAllTournaments, boolean bAllCopies){
        // What Directories ?
        
        
        String strURL = "http://opengotha.info/tournaments/20181101_Coupe%20Confucius%202018/20181104055946_Coupe%20Confucius%202018";
        File fDestination = new File(Gotha.runningDirectory +"/bidon.txt");
        ArrayList<TournamentInterface> alT = new ArrayList<>();
        TournamentInterface t = downloadTournament(strURL, fDestination);
        alT.add(t);
        return alT;
    }

    public static void downloadByHTTP(String strURL, File fDestination){
        PrintWriter out = null;
        try {
            out = new PrintWriter(fDestination);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }           
        try {
            URL urlTournament = new URL(strURL);
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlTournament.openStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    out.println(inputLine);                    
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);        
        }
         
        out.close();
    }
        
    private static TournamentInterface downloadTournament(String strURL, File fDestination){
        TournamentInterface t = null;
        try {                                            
            RemoteTournament.downloadByHTTP(strURL, fDestination);
            
            t = new Tournament();
            ExternalDocument.importTournamentFromXMLFile(fDestination, t, true, true, true, true, true);
            try {
                t.getShortName();
            } catch (RemoteException ex) {                
                Logger.getLogger(JFrGotha.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (RemoteException ex) {
            Logger.getLogger(JFrRemoteTournaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return t;
    }
}
