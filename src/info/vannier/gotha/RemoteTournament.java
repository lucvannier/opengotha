
package info.vannier.gotha;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luc
 */
public class RemoteTournament {
    public static ArrayList<String> filesList(String strURL){
        ArrayList<String> alF = null;
        try {
            alF = new ArrayList<>();
            URL url = null;
            try {
                url = new URL(strURL);
            } catch (MalformedURLException ex) {
                Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
            }
            URLConnection urlc = url.openConnection();
            
            BufferedInputStream bis = new BufferedInputStream(urlc.getInputStream());
            int i;                        
            StringBuffer sbFN = new StringBuffer();
            while ((i = bis.read()) != -1) {
                char c = (char)i;
                if (c != '\n') sbFN.append(c);
                else{
                    if (sbFN.length() > 0) alF.add(new String(sbFN));
                    sbFN = new StringBuffer();
                }
            }
            bis.close();
        } catch (IOException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alF;
    }
    
    public static ArrayList<String> tournamentFilesList(){
        String strURL = "http://opengotha.info/fileslist.php?dirName=./tournaments";
        ArrayList<String> alTN = new ArrayList<>();
        ArrayList<String> alD = filesList(strURL);
        for(String s : alD){
            ArrayList<String>alSF = null; 
            try {
                alSF = filesList(strURL + "/" + URLEncoder.encode(s, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (String tn:alSF){
                alTN.add(s + "/" + tn);
            }
        }
        return alTN;
       
    }
   
    private static String downloadFileIntoString(String strURL){
        URL url = null;
        try {
            url = new URL(strURL);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        StringBuffer sbContent = new StringBuffer();
        try {
            URLConnection urlc = url.openConnection();
                        BufferedInputStream bis = new BufferedInputStream(urlc.getInputStream());
            int i;                        
    
            while ((i = bis.read()) != -1) {
                char c = (char)i;
                sbContent.append(c);
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        String str = new String(sbContent);
            
        return str;
    }

    public static void downloadFileIntoFile(String strURL, String strFN){
        String str = downloadFileIntoString(strURL);
//        System.out.println("str = " + str);
        PrintWriter out = null;
        try {
            out = new PrintWriter(strFN);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.print(str);
        out.close();
    }
 
    public static TournamentInterface downloadTournament(String strURL){
        new File(Gotha.runningDirectory + "/remote").mkdirs();
        File f = new File(Gotha.runningDirectory, "remote/essai3h43.xml");
        String strFN = f.toString();
        downloadFileIntoFile(strURL, strFN);
        TournamentInterface t = null;
        try {
            t = new Tournament();
        } catch (RemoteException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        ExternalDocument.importTournamentFromXMLFile(f, t, true, true, true, true, true);
        return t;
    }
        
    public static ArrayList<TournamentInterface> downloadTournaments(boolean bAllTournaments, boolean bAllCopies){
        ArrayList<TournamentInterface> alT = new ArrayList<>();
        // What Directories ?
        String strURL = "http://opengotha.info/tournaments/";
        ArrayList<String> alTN = RemoteTournament.tournamentFilesList();
        for(String strT:alTN){
            String strDir = strT.substring(0, strT.indexOf('/'));
            String strFil = strT.substring(strT.indexOf('/')+1);
            String strEncT = null;
           try {
                
               String strEncDir = URLEncoder.encode(strDir, "UTF-8");
//

        String strEncFil = URLEncoder.encode(strFil, "UTF-8");
//               String strEncFil = strFil;
 
               strEncT = strURL + strEncDir + "/" + strEncFil;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
            }
           File dirDestination = new File(Gotha.runningDirectory + "/remote/" + strDir);
           dirDestination.mkdir();
           File fDestination = new File(Gotha.runningDirectory + "/remote/" + strT);
//            File fDestination = new File(Gotha.runningDirectory + "/remote/" + "essaibidon.xml");
            TournamentInterface t = downloadTournament(strEncT, fDestination);
            alT.add(t);     
        }

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
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(urlTournament.openStream()));
            InputStream is = urlTournament.openStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                out.println(inputLine);                    
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
