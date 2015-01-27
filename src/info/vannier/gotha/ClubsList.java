/*
 * list of all known clubs
 */
package info.vannier.gotha;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Luc
 */
public class ClubsList {
    private HashMap<String, Club> hmClubs; 
    
    public ClubsList(){
    hmClubs = new HashMap<String, Club>();
    }
    
    public void importClubsFromRatingList(RatingList rl){
        ArrayList<RatedPlayer> alRatedPlayers = rl.getALRatedPlayers();
        int i= 0;
        for (RatedPlayer rp : alRatedPlayers){
            Club c = new Club(rp.getClub());
            String clubName = c.getName();
            Club previousClub = hmClubs.get(clubName);
            String previousClubName = "";
            if (previousClub == null) previousClubName ="null";
            else previousClubName = previousClub.getName();
            i++;
//            System.out.println("" + i + " previousClub.name = " + previousClubName + "c.name= " + c.getName());

            hmClubs.put(c.getName(), c);
        }
    }
    
    public void println(){
        int i = 0;
    
        for (Club c : hmClubs.values()){
            i++;
            System.out.println("" + i + " c.name = " + c.getName());
        }
    }

    /**
     * @return the hmClubs
     */
    public HashMap<String, Club> getHmClubs() {
        return hmClubs;
    }

}



