package info.vannier.gotha;

import java.io.Serializable;
import java.util.Comparator;

public class PlayerComparator implements Comparator<Player>, Serializable{
    public final static int NO_ORDER = 0;
    public final static int NUMBER_ORDER = 1;
    public final static int NAME_ORDER   = 2;
    public final static int RANK_ORDER   = 3;
    public final static int GRADE_ORDER  = 4;
    public final static int RATING_ORDER = 5;
    public final static int AGAID_ORDER  = 11;
    public final static int SCORE_ORDER  = 101; // Not used in PlayerComparator itself. Used by JFrGamesPair
    
    int playerOrderType = PlayerComparator.NO_ORDER;
    public PlayerComparator(int playerOrderType){
        this.playerOrderType = playerOrderType;
    }

    @Override
    public int compare(Player p1, Player p2){
        switch (playerOrderType){
            case NAME_ORDER :
                int c = p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
                if (c != 0) return c;
                else return p1.getFirstName().toLowerCase().compareTo(p2.getFirstName().toLowerCase());
            case RANK_ORDER :
                if (p1.getRank() < p2.getRank()) return 1;
                if (p1.getRank() > p2.getRank()) return -1;
                c = p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
                if (c != 0) return c;
                else return p1.getFirstName().toLowerCase().compareTo(p2.getFirstName().toLowerCase());
            case GRADE_ORDER :
                int range1 = 0; // -30 to -1 for kyu, 0 to 8 for dan, 100 to 108  for pro
                String str1 = p1.getStrGrade();
                str1 = str1.trim();
                String grp1 = str1.substring(str1.length() - 1, str1.length());
                grp1 = grp1.toLowerCase();
                int n1 = new Integer(str1.substring(0, str1.length() - 1)).intValue();
                if (grp1.equals("p")) range1 = 100 + n1 - 1;
                if (grp1.equals("d")) range1 = n1 - 1;
                if (grp1.equals("k")) range1 = -n1;

                int range2 = 0; // -30 to -1 for kyu, 0 to 8 for dan, 100 to 108  for pro
                String str2 = p2.getStrGrade();
                str2 = str2.trim();
                String grp2 = str2.substring(str2.length() - 1, str2.length());
                grp2 = grp2.toLowerCase();
                int n2 = new Integer(str2.substring(0, str2.length() - 1)).intValue();
                if (grp2.equals("p")) range2 = 100 + n2 - 1;
                if (grp2.equals("d")) range2 = n2 - 1;
                if (grp2.equals("k")) range2 = -n2;
                    
                if (range1 < range2) return 1;
                if (range1 > range2) return -1;
                c = p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
                if (c != 0) return c;
                else return p1.getFirstName().toLowerCase().compareTo(p2.getFirstName().toLowerCase());
            case RATING_ORDER :
                if (p1.getRating() < p2.getRating()) return 1;
                if (p1.getRating() > p2.getRating()) return -1;
                c = p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
                if (c != 0) return c;
                else return p1.getFirstName().toLowerCase().compareTo(p2.getFirstName().toLowerCase());
            case AGAID_ORDER :
                int agaId1 = 0;
                try{
                    agaId1 = Integer.parseInt(p1.getAgaId());
                }
                catch(NumberFormatException e){
                    agaId1 = 0;
                }
                int agaId2 = 0;
                try{
                    agaId2 = Integer.parseInt(p2.getAgaId());
                }
                catch(NumberFormatException e){
                    agaId2 = 0;
                }
                if (agaId1 > agaId2) return 1;
                if (agaId1 < agaId1) return -1;
                return 0;
            default :
                return 0;
        }
    }
}

