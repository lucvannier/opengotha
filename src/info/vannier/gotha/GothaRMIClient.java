package info.vannier.gotha;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luc Vannier
 */
public class GothaRMIClient {
    private final String clientUniqueId;
    private final String tournamentName; // name of the tournament to which this client is connected
    private long lastSignOfLife = System.currentTimeMillis();

    /**
     * @return the tournamentName
     */
    public String getTournamentName() {
        return tournamentName;
    }

    /**
     * @return the clientUniqueId
     */
    public String getClientUniqueId() {
        return clientUniqueId;
    }


    public GothaRMIClient(String strClient, String strTournament){
        this.clientUniqueId = strClient;
        this.tournamentName = strTournament;
        lastSignOfLife = System.currentTimeMillis();
    }

    private static Registry getRMIRegistry(String serverName){
        Registry reg = null;
        int port = 1099;
        try {
            reg = LocateRegistry.getRegistry(serverName);
        } catch (RemoteException ex) {
            Logger.getLogger(GothaRMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reg;
    }

    public static String[] tournamentNamesList(String serverName) {
        Registry reg = getRMIRegistry(serverName);
        if (reg == null) return null;

        String[] tnList = null;
        try {
            tnList = reg.list();
        } catch (RemoteException ex) {
            // Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tnList;
    }

    public static TournamentInterface getTournament(String serverName, String tKN) {
        Registry reg = getRMIRegistry(serverName);
        TournamentInterface t = null;
        try {
            t = (TournamentInterface) reg.lookup(tKN);
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(GothaRMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }

    /**
     * @return the lastSignOfLife
     */
    public long getLastSignOfLife() {
        return lastSignOfLife;
    }

    /**
     * @param lastSignOfLife the lastSignOfLife to set
     */
    public void setLastSignOfLife(long lastSignOfLife) {
        this.lastSignOfLife = lastSignOfLife;
    }

}
