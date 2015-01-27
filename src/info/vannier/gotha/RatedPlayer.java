package info.vannier.gotha;



public class RatedPlayer {
    private String egfPin;
    private String ffgLicence;
    private String ffgLicenceStatus;
    private String agaID;
    private String agaExpirationDate;
    private String name;
    private String firstName;
    private String country;
    private String club;
    private int rawRating;
    private String strGrade;  
    private String ratingOrigin;
    

    public RatedPlayer(
            String egfPin,
            String ffgLicence,
            String ffgLicenceStatus,
            String agaID,
            String agaExpirationDate,
            String name,
            String firstName,
            String country,
            String club,
            int rawRating,
            String strGrade,
            String ratingOrigin){
         this.egfPin = egfPin;
         this.ffgLicence = ffgLicence; 
         this.ffgLicenceStatus = ffgLicenceStatus;
         this.agaID = agaID; 
         this.agaExpirationDate = agaExpirationDate;
         this.name = name;
         this.firstName = firstName;
         this.country = country;
         this.club = club;
         this.rawRating = rawRating;
         this.strGrade = strGrade;
         this.ratingOrigin = ratingOrigin;
    }
      
    public String getEgfPin() {
        return egfPin;
    }

    public String getFfgLicence() {
        return ffgLicence;
    }
   
    public String getAgaId() {
        return agaID;
    }
    
    public String getAgaExpirationDate() {
        return agaExpirationDate;
    }
    
    public String getFfgLicenceStatus() {
        return ffgLicenceStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getClub() {
        return club;
    }

    private int getRawRating() {
        return rawRating;
    }
    
    public String getStrRawRating() {

        int rr = getRawRating();
        String strRR = "" + rawRating;
        
        if (getRatingOrigin().equals("AGA")){
            // Generate a eeee.ff string
            int e = rr /100;
            int f = Math.abs(rr %100);
            String strF = ".00";
            if (f > 9) strF = "." + f;
            else strF = ".0" + f; 
            
            strRR = "" + e + strF;
        }
        return "" + strRR;
    }
    
    public int getStdRating() {
        int stdRating = rawRating;
        if (ratingOrigin.compareTo("FFG") == 0) stdRating = rawRating + 2050;
        if(ratingOrigin.compareTo("AGA") == 0){
            if (rawRating >= 100) stdRating =  rawRating + 1950;
            if (rawRating <= -100) stdRating = rawRating + 2150;
            if (rawRating > -100 && rawRating < 100) stdRating = 2050;            
        }
            
        stdRating = Math.min(stdRating, Player.MAX_RATING);
        stdRating = Math.max(stdRating, Player.MIN_RATING);
        
        return stdRating;
    }
    
    public String getRatingOrigin() {
        return ratingOrigin;
    }
    
    /** returns Levenshtein between s and t
     */
    public static int distance_Levenshtein(String s, String t){
        //*****************************
        // Compute Levenshtein distance
        //*****************************
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost
        
        // Step 1
        n = s.length();
        m = t.length();
        if (n == 0)
            return m;
        if (m == 0)
            return n;
        d = new int[n+1][m+1];
        
        // Step 2
        for (i = 0; i <= n; i++)
            d[i][0] = i;
        for (j = 0; j <= m; j++)
            d[0][j] = j;
        
        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);
            
            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);
                
                // Step 5
                if (s_i == t_j)
                    cost = 0;
                else
                    cost = 1;
                
                // Step 6
                d[i][j] = Math.min(d[i-1][j]+1, d[i][j-1]+1);
                d[i][j] = Math.min(d[i][j], d[i-1][j-1] + cost);
            }
        }
        
        // Step 7
        return d[n][m];
    }

    /**
     * @return the strGrade
     */
    public String getStrGrade() {
        return strGrade;
    }
}