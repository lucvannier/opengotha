/*
 * HandicapParameterSet.java
 *
 */

package info.vannier.gotha;

/**
 *
 * @author Luc Vannier
 */
public class HandicapParameterSet implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    /** 
     * if hdBasedOnMMS is false, hd will be based on rank
     */
    private boolean hdBasedOnMMS = true;
    
    /**
     * When one player in the game has a rank of at least hdNoHdRankThreshold,
     * then the game will be without handicap
     */
    private int hdNoHdRankThreshold = 0;
    
    /**
     * Handicap will be decreased by hdCorrection
     * Possible values are 0, 1, 2 or 3. Or -1
     */
    private int hdCorrection = 1;
    
    /**
     * Handicap Ceiling
     * Possible values are between 0 and 9;
     */
    private int hdCeiling;
    
    /** 
     * Creates a new instance of HandicapParameterSet 
     */
    public HandicapParameterSet() {
    }    

    /** 
     * Creates a new instance of HandicapParameterSet, clone of hps 
     */
    public HandicapParameterSet(HandicapParameterSet hps) {
        this.hdBasedOnMMS = hps.isHdBasedOnMMS();
        this.hdNoHdRankThreshold = hps.getHdNoHdRankThreshold();
        this.hdCorrection = hps.getHdCorrection();
        this.hdCeiling = hps.getHdCeiling();
    }
    
    public void initForMM(){
        hdBasedOnMMS = true;
        hdNoHdRankThreshold = 0;
        hdCorrection = 1;       
        hdCeiling = 9;
    }
    
    public void initForSwiss(){
        hdBasedOnMMS = false;
        hdNoHdRankThreshold = -30;
        hdCorrection = 0;       
        hdCeiling = 0;
    }
    
    public void initForSwissCat(){
        hdBasedOnMMS = true;
        hdNoHdRankThreshold = 8;
        hdCorrection = 1;       
        hdCeiling = 9;
    }
    
    public boolean isHdBasedOnMMS() {
        return hdBasedOnMMS;
    }

    public void setHdBasedOnMMS(boolean hdBasedOnMMS) {
        this.hdBasedOnMMS = hdBasedOnMMS;
    }

    public int getHdNoHdRankThreshold() {
        return hdNoHdRankThreshold;
    }

    public void setHdNoHdRankThreshold(int hdNoHdRankThreshold) {
        this.hdNoHdRankThreshold = hdNoHdRankThreshold;
    }

    public int getHdCorrection() {
        return hdCorrection;
    }

    public void setHdCorrection(int hdCorrection) {
        this.hdCorrection = hdCorrection;
    }

    public int getHdCeiling() {
        return hdCeiling;
    }

    public void setHdCeiling(int hdCeiling) {
        this.hdCeiling = hdCeiling;
    }
    
}
