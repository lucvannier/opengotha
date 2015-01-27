package info.vannier.gotha;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Only static members
 * @author Luc
 */
public class GothaRMIServer {

    private static final int MAX_NUMBER_OF_CLIENTS = 10;
    private static final int MAX_NUMBER_OF_TOURNAMENTS = 10;
    private static HashMap<String, GothaRMIClient> hmClients = new HashMap<String, GothaRMIClient>();

    /**
     * creates a Registry if it does not yet exist
     *
     */
    private static Registry getOrCreateARMIRegistry() {
        int port = 1099;
        Registry reg = null;
        try {
            reg = LocateRegistry.createRegistry(port);
            return reg;
        } catch (RemoteException ex) {
//            Logger.getLogger(Gotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            // Creation did not work. Locate an existing registry
            reg = LocateRegistry.getRegistry(port);
            return reg;
        } catch (RemoteException ex) {
            Logger.getLogger(Gotha.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reg;
    }

    public static ArrayList<GothaRMIClient> clientsList() {
        return new ArrayList<GothaRMIClient>(hmClients.values());
    }

    public static String[] tournamentNamesList() {
        Registry reg = getOrCreateARMIRegistry();
        if (reg == null) {
            return null;
        }
        String[] tnList = null;

        try {
            tnList = reg.list();
        } catch (RemoteException ex) {
            Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tnList;
    }

    public static boolean addTournament(TournamentInterface tournament) {
        if (tournament == null) {
            return false;
        }
        Registry reg = getOrCreateARMIRegistry();
        if (reg == null) {
            return false;
        }
        String[] tnList = tournamentNamesList();
        if (tnList.length > MAX_NUMBER_OF_TOURNAMENTS) {
            System.out.println("Too many tournaments");
            return false;
        }
        String tKN = "";
        try {
            tKN = tournament.getShortName();
            if (getTournament(tKN) != null) {
                System.out.println("A tournament with same name already exists in Registry");
                return false;
            }
            reg.rebind(tournament.getShortName(), tournament);
            return true;
        } catch (RemoteException ex) {
            Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public static TournamentInterface getTournament(String tKN) {
        Registry reg = getOrCreateARMIRegistry();
        if (reg == null) {
            return null;
        }
        TournamentInterface t = null;
        try {
            t = (TournamentInterface) reg.lookup(tKN);
        } catch (RemoteException ex) {
//            Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NotBoundException ex) {
//            Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return t;
    }

    public static boolean removeTournament(String tKN) {
        Registry reg = getOrCreateARMIRegistry();
        if (reg == null) {
            return false;
        }
        try {
            reg.unbind(tKN);
        } catch (NotBoundException ex) {
            System.out.println(tKN + " not currently bound");
        } catch (AccessException ex) {
            Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(GothaRMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }
    private static int nbClients = 0;
    /**
     * Adds a client into hmClients.
     * @param strClient
     * @return the key Name.
     * The key Name is typically strClient as supplied as input parameter.
     * But, if necessary, addClient will add a suffix to the supplied strClient
     * in order to avoid homonymy between 2 clients
     */
    public static String addClient(String strClient, String strTournament) {
        nbClients++;

        if (clientsList().size() > MAX_NUMBER_OF_CLIENTS) {
            System.out.println("Too many clients");
            return "";
        }
        if (strClient.trim().equals("")) {
            strClient = "client";
        }
        String kCN = strClient;

        int discriminant = 0;
        while (hmClients.get(kCN) != null) {
            discriminant++;
            kCN = strClient + "_" + discriminant;
        }
        GothaRMIClient cl = new GothaRMIClient(kCN, strTournament);
        hmClients.put(kCN, cl);
        return kCN;
    }

    public static boolean removeClient(String strClient) {
        GothaRMIClient cl = hmClients.remove(strClient);
        if (cl == null) {
            System.out.println("No client with this name has been found");
            return false;
        } else {
            System.out.println("Client removed");
            return true;
        }
    }

    public static void clockIn(String strClient) {
        GothaRMIClient cl = hmClients.get(strClient);
        if (cl == null) {
            return;
        }
        cl.setLastSignOfLife(System.currentTimeMillis());
    }

}
