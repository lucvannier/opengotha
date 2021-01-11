package info.vannier.gotha;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * ScoredTeamSet is dedicated to maintain data about team matches abd team scores
 * <br>An instance of ScoredTeamSet is related to a given TeamPlacementParameterSet and a given round number
 * <br>It is the responsability of the programmer to be sure that the TeamParameterSet and round number
 * <br>are set according too his needs.
 * <br>It is normally done by the Tournament.getScoredTeamSet(TeamPlacementParameterSet tps, int roundNumber) method
 * <br>getScoredTeamSet, when called, will look internally for a proper and up-to-date ScoredTeamSet
 * <br>(and will build it if necessary) before returning it
 * <br>4 utility methods to retrieve :
 * <br>- getTeamPlacementCriteria()
 * <br>- getCritValue
 * <br>- getOrderedTeamsList
 * <br>- getHalfMatchString
 * <br>
 * @author Luc Vannier
 */
public class ScoredTeamsSet implements java.io.Serializable{
//    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;
    private TournamentInterface tournament;
    private long lastUpdateTimeMillis = 0;

    private TeamPlacementParameterSet tpps = null;
    private int roundNumber = -1;

    // matches up to round roundNumber
    private ArrayList<Match> alMatches;
    
    private PlacementCriterion[] tabPlacementCriteria = null;
    private ArrayList<ScoredTeam> alOrderedScoredTeams = null;
    private String[][] tabHalfMatchString = null;

    public ScoredTeamsSet(TournamentInterface tournament){
        this.tournament = tournament;
    }
    public PlacementCriterion getTeamPlacementCriterion(int critNumber){
        return tabPlacementCriteria[critNumber];
   }

   public ArrayList<ScoredTeam> getOrderedScoredTeamsList(){
       ArrayList<ScoredTeam> alOSTL = new ArrayList<ScoredTeam>(alOrderedScoredTeams);
       return alOSTL;
   }

   /**
    * returns a "tttr(ww)" string where
    * "ttt" is team the opponent team index as ordered in orderedTeamsList() + 1
    * "r" is the result : "-", "=" or "+"
    * "ww" is the number of wins, for instance "2 " or "3½"
    * @param it
    * @param roundNumber
    * @return
    */
   public String getHalfMatchString(ScoredTeam st, int roundNumber){
       int iTeam = this.alOrderedScoredTeams.indexOf(st);
       return tabHalfMatchString[iTeam][roundNumber];
   }

   /**
    * returns a string representing the teamPostition "1", or "2", ...
    * if the team is ex-aequo with previous team in the order of alOrderedScoredTeams, returns ""
    * @param st
    * @return
    */
    public String getTeamPositionString (ScoredTeam st){
        int pos = alOrderedScoredTeams.indexOf(st);
        if (pos == 0) return "" + (pos + 1);
        ScoredTeam previousST = alOrderedScoredTeams.get(pos - 1);
        for (int ic = 0; ic < this.tabPlacementCriteria.length; ic++){
            if (st.getCritValue(ic) != previousST.getCritValue(ic)) return "" + (pos + 1);                
        }
        return "";
   }

   public boolean isUpToDate(long lastTournamentUpdate){
       if (lastUpdateTimeMillis > lastTournamentUpdate) return true;
       else return false;
   }

   public boolean isOKWith(TeamPlacementParameterSet tpps, int roundNumber){
       if (this.roundNumber != roundNumber) return false;
       if (this.tpps == null) return false;
       if (!this.tpps.equals(tpps)) return false;
       return true;
   }

   /**
    * Should be called by tournament.updateScoredTeamSet(TeamPlacementParameterSet tpps, int roundNumber).
    * And only if either isUpToDate() or isOKWith() is false
    */
    public void update(TeamPlacementParameterSet tpps, int roundNumber){
        this.tpps = tpps.deepCopy();
        this.roundNumber = roundNumber;
        try {
            updateAlMatches(); // build all matches and alMatches
            updateTabPlacementCriteria();
            updateOrderedScoredTeamsList();     
            updateTabHalfMatchString();
        } catch (RemoteException ex) {
            Logger.getLogger(ScoredTeamsSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.lastUpdateTimeMillis = System.currentTimeMillis();
    }

    private void updateAlMatches()throws RemoteException{
        alMatches = tournament.matchesListUpTo(roundNumber);
    }

    private void updateTabPlacementCriteria(){
        tabPlacementCriteria = new PlacementCriterion[TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];
        PlacementCriterion[] tabPC = TeamPlacementParameterSet.allPlacementCriteria;
        for(int iac = 0; iac < TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA; iac++){
            int crit = tpps.getPlaCriterion(iac);
            for(int ic = 0; ic < tabPC.length; ic++){
                if (tabPC[ic].uid == crit){
                    tabPlacementCriteria[iac] = tabPC[ic];
                    break;
                }
            }
        }
    }

    private void updateOrderedScoredTeamsList() throws RemoteException{
        ArrayList<Team> alTeams = tournament.teamsList();
        int nbTeams = alTeams.size();
        int teamSize = tournament.getTeamSize();

        int[][] tabCritValues = new int[nbTeams][TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];

        // 1 - Order teams by team number
        TeamComparator teamComparator = new TeamComparator(TeamComparator.TEAM_NUMBER_ORDER, teamSize);
        Collections.sort(alTeams, teamComparator);

        // 2- fill tabWX2byTB, tabCumWX2UBByTB, tabTeamPoints, tabSOST
        int[][] tabWX2ByTB = new int[nbTeams][Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM];
        int[][] tabCumWX2UBByTB = new int[nbTeams][Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM];
        int[] tabTeamPoints = new int[nbTeams];
        int[] tabSOST = new int[nbTeams];
        for (int it = 0; it < nbTeams; it++){
            tabTeamPoints[it] = 0;
            tabSOST[it] = 0;
            for(int ib = 0; ib < teamSize; ib++){
                tabWX2ByTB[it][ib] = 0;
                tabCumWX2UBByTB[it][ib] = 0;
            }
        }

        for (Match m : alMatches){
            Team wt = m.getWhiteTeam();
            Team bt = m.getBlackTeam();
            int iwt = alTeams.indexOf(wt);
            int ibt = alTeams.indexOf(bt);
            tabTeamPoints[iwt] += m.getTeamScore(wt);
            tabTeamPoints[ibt] += m.getTeamScore(bt);

            for(int ib = 0; ib < teamSize; ib++){
                tabWX2ByTB[iwt][ib] += m.getWBoardResult(ib);
                tabWX2ByTB[ibt][ib] += m.getBBoardResult(ib);
            }
        }

        for (int it = 0; it < nbTeams; it++){
            for(int ibCum = 0; ibCum < teamSize; ibCum++){
                tabCumWX2UBByTB[it][ibCum] = 0;
                for (int ib = 0; ib <= ibCum; ib++){
                    tabCumWX2UBByTB[it][ibCum] += tabWX2ByTB[it][ib];
                }
            }
            for (int ibCum = teamSize; ibCum < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ibCum++){
                tabCumWX2UBByTB[it][ibCum] = tabCumWX2UBByTB[it][teamSize - 1];
            }
        }
        // tabSOST
        for (Match m : alMatches){
            Team wt = m.getWhiteTeam();
            Team bt = m.getBlackTeam();
            int iwt = alTeams.indexOf(wt);
            int ibt = alTeams.indexOf(bt);
            tabSOST[iwt] += tabTeamPoints[ibt];
            tabSOST[ibt] += tabTeamPoints[iwt];
        }

        // 3- tabCritValues
        for (int it = 0; it < nbTeams; it++){
            Team t = alTeams.get(it);
            for (int ic = 0; ic < TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA; ic++){
                int val = 0;
                switch(tabPlacementCriteria[ic].uid){
                    case TeamPlacementParameterSet.TPL_CRIT_NUL :           val = 0; break;
                    case TeamPlacementParameterSet.TPL_CRIT_TEAMPOINTS :    val = tabTeamPoints[it]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_SOST :          val = tabSOST[it]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS :     val = tabCumWX2UBByTB[it][9]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_9UB : val = tabCumWX2UBByTB[it][8]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_8UB : val = tabCumWX2UBByTB[it][7]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_7UB : val = tabCumWX2UBByTB[it][6]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_6UB : val = tabCumWX2UBByTB[it][5]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_5UB : val = tabCumWX2UBByTB[it][4]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_4UB : val = tabCumWX2UBByTB[it][3]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_3UB : val = tabCumWX2UBByTB[it][2]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_2UB : val = tabCumWX2UBByTB[it][1]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_BOARDWINS_1UB : val = tabCumWX2UBByTB[it][0]; break;
                    case TeamPlacementParameterSet.TPL_CRIT_MEAN_RATING :   val = t.meanRating(tournament.getTeamSize()); break;
                    default: val = 0;
                }
                tabCritValues[it][ic] = val;

            }
        }

        alOrderedScoredTeams = new ArrayList<ScoredTeam>();
        for (int it = 0; it < alTeams.size(); it++){
            Team team = alTeams.get(it);
            ScoredTeam scoredTeam = new ScoredTeam(team, tabCritValues[it]);
            alOrderedScoredTeams.add(scoredTeam);
        }
        ScoredTeamComparator scoredTeamComparator = new ScoredTeamComparator(false);
        Collections.sort(alOrderedScoredTeams, scoredTeamComparator);
    }

    private void updateTabHalfMatchString(){
        int nbTeams = alOrderedScoredTeams.size();
        int nbRounds = this.roundNumber + 1;

        int[][] tabTSByTR = new int[nbTeams][nbRounds];
        int[][] tabsNBWX2byTR = new int[nbTeams][nbRounds];
        for(int it = 0; it < nbTeams; it++){
            for(int ir = 0; ir < nbRounds; ir++){
                tabTSByTR[it][ir] = 0;
                tabsNBWX2byTR[it][ir] = 0;
            }
        }

        for (Match m : alMatches){
            Team wt = m.getWhiteTeam();
            Team bt = m.getBlackTeam();
            int iwt = this.findTeamOutOf(wt, alOrderedScoredTeams);
            int ibt = this.findTeamOutOf(bt, alOrderedScoredTeams);
            int ir = m.getRoundNumber();
            tabTSByTR[iwt][ir] = m.getTeamScore(wt);
            tabTSByTR[ibt][ir] = m.getTeamScore(bt);
            tabsNBWX2byTR[iwt][ir] = m.getWX2(wt);
            tabsNBWX2byTR[ibt][ir] = m.getWX2(bt);
        }

        tabHalfMatchString = new String[nbTeams][nbRounds];
        for(int it = 0; it < nbTeams; it++){
            for(int ir = 0; ir < nbRounds; ir++){
                tabHalfMatchString[it][ir] = "0-";
            }
        }

        for(Match m : alMatches){
            Team wt = m.getWhiteTeam();
            Team bt = m.getBlackTeam();
            int iwt = this.findTeamOutOf(wt, alOrderedScoredTeams);
            int ibt = this.findTeamOutOf(bt, alOrderedScoredTeams);
            int ir = m.getRoundNumber();

            String strOpp;
            String strTS;
            String strNBW;
            // white Team
            strOpp = "" + (ibt + 1);
            if ((ibt + 1) < 100) strOpp = " " + strOpp;
            if ((ibt + 1) < 10) strOpp = " " + strOpp;
            strTS = "?";
            switch(tabTSByTR[iwt][ir]){
                case 0: strTS = "-"; break;
                case 1: strTS = "="; break;
                case 2: strTS = "+"; break;
                default: strTS = "?"; break;
            }
            strNBW = Gotha.formatFractNumber(tabsNBWX2byTR[iwt][ir], 2);
            this.tabHalfMatchString[iwt][ir] = strOpp + strTS + "(" + strNBW + ")";
            // black Team
            strOpp = "" + (iwt + 1);
            if ((iwt + 1) < 100) strOpp = " " + strOpp;
            if ((iwt + 1) < 10) strOpp = " " + strOpp;
            strTS = "?";
            switch(tabTSByTR[ibt][ir]){
                case 0: strTS = "-"; break;
                case 1: strTS = "="; break;
                case 2: strTS = "+"; break;
                default: strTS = "?"; break;
            }
            strNBW = Gotha.formatFractNumber(tabsNBWX2byTR[ibt][ir], 2);
            this.tabHalfMatchString[ibt][ir] = strOpp + strTS + "(" + strNBW + ")";
        }
    }

    public int findTeamOutOf(Team team, ArrayList<ScoredTeam> alScoredTeams){
        String strTN = team.getTeamName();
        for (int it = 0; it < alScoredTeams.size(); it++){
            Team t = alScoredTeams.get(it);
            if (t.getTeamName().equals(strTN)) return it;
        }
        return -1;
    }


}
