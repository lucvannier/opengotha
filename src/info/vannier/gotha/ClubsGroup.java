
package info.vannier.gotha;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ClubsGroup is 
 * @author Luc
 */
public class ClubsGroup implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private String name;
    private HashMap<String, Club> hmClubs;

    public ClubsGroup(String name){
        this.name = name;
        hmClubs = new HashMap<String, Club>();
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void cleraAll(){
        hmClubs.clear();
    }
    
    public void put(Club club){
        hmClubs.put(club.getName(), club);
    }

    public Club get(String clubName){
        return hmClubs.get(clubName);
    }
    
    public void remove(String clubName){
        hmClubs.remove(clubName);
    }

    /**
     * @return the hmClubs
     */
    public HashMap<String, Club> getHmClubs() {
        return hmClubs;
    }   
}
