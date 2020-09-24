
package info.vannier.gotha;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luc
 */
public class RemoteTournament {
    
    public static void upload(TournamentInterface tournament){
	final String charset = "UTF-8";
        final String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        String tournamentShortName = null;        
        String tournamentFileName = null;        

        // No upload in Client mode
        if (Gotha.runningMode == Gotha.RUNNING_MODE_CLI) return;

        try {
            tournamentShortName = tournament.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
            tournamentFileName = tournamentShortName;
        
        File tournamentFile = new File(Gotha.runningDirectory, "tournamentfiles/work/" + tournamentFileName + ".xml"); // original tournament file
        if (!tournamentFile.exists()) return;

        String shrinkedShortName = shrinkedString(tournamentShortName);
        String targetURL = "http://opengotha.info/upload.php";
        
        Date beginDate = null;
        try {
            beginDate = tournament.getTournamentParameterSet().getGeneralParameterSet().getBeginDate();
        } catch (RemoteException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String strBeginDate = dateFormat.format(beginDate);
        Date currentDate = Calendar.getInstance().getTime();
        dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String strCurrentDate = dateFormat.format(currentDate);

	URL url;
        URLConnection connection = null;
        OutputStream output;
        PrintWriter writer;

        try {
            url = new URL(targetURL);
            connection = url.openConnection(); 
            connection.setDoOutput(true);
            output = connection.getOutputStream(); 
            writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true);
//            writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            writer.append("postcontent=").append(CRLF);
            writer.append("shrinkedshortname:" + shrinkedShortName + ";").append(CRLF);
            writer.append("begindate:" + strBeginDate + ";").append(CRLF);
            writer.append("currentdate:" + strCurrentDate + ";").append(CRLF);
            writer.append("filecontent:");

            List<String> lst = null;
            Path path = tournamentFile.toPath();
            lst = Files.readAllLines(path);

            for (String s : lst){
                writer.append(s).append(CRLF);
            }
            
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
           
        } catch (MalformedURLException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Get Response
        StringBuffer response = null;
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }

            in.close();
        } catch (IOException ex) {
            Logger.getLogger(RemoteTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Eliminate undesirable characters
    private static String shrinkedString(String str) {
       String shrinkedStr = str.replaceAll("[^A-Za-z0-9.-]", "");
       return shrinkedStr;     
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
                 
}
