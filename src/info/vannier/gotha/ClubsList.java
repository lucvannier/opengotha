/*
 * list of all known clubs
 */
package info.vannier.gotha;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Luc Vannier
 */
public class ClubsList {
    private final HashMap<String, Club> hmClubs; 
    
    public ClubsList(){
    hmClubs = new HashMap<>();
    }
    
    public void importClubsFromRatingList(RatingList rl){
        ArrayList<RatedPlayer> alRatedPlayers = rl.getALRatedPlayers();
        int i= 0;
        for (RatedPlayer rp : alRatedPlayers){
            Club c = new Club(rp.getClub());
            String clubName = c.getName();
            Club previousClub = hmClubs.get(clubName);
            String previousClubName = null;
            if (previousClub != null) previousClubName = previousClub.getName();
            i++;

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



