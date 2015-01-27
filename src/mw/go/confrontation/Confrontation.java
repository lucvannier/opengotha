/*
 * Copyright 2007 Matthieu Walraet
 *
 * mw.go.confrontation package is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU General Public 
 * License version 2, as published by the Free Software Foundation.
 * 
 * Created: 2007/08/26
 * 
 * Sort players according to their direct confrontation.
 * 
 * In a the end of tournament, you want to sort player according to
 * several criteria. 
 * - First criteria (before DC)
 * - Direct Confrontation
 * - Secondary criteria. 
 * 
 * Use the newPlayer(id, sc) method to add players ex-aequo after 
 * applying the first criteria
 * id: is player identifier (They don't need to be consecutive, 
 *     you can use ID from the whole tournament
 * sc: is the value of secondary criteria. It is needed to sort players
 *     when there is no game between them.
 * 
 * Then use the newGame(idWinner, idLoser) to enter games played between
 * this group of player.
 * 
 * Then use topologicalSort(boolean upWinners, boolean preserveTransitivity)
 * It return the sorted list of player IDs.
 * 
 * upWinners : 
 *    if A has sc 1, B has sc 3, X has sc 2, A has won against B (only game)
 *    A must be before B after sorting, so it will be ABX or XAB
 *    if upWinners is true, result is ABX (Winners are favorized)
 *    if upWinners is false, result is XAB (losers are unfavorized)
 *  
 *  preserveTransitivity:
 *    Normally a topological sort can only be performed on an acyclic graph.
 *    If A won against B, B won against C, and C won against A, there is a cycle.
 *  
 *  We ignore the games forming the cycle to be able to sort. 
 *  But we can choose to preserve transitivity between players not belonging to 
 *  the cycle, but having played againts them.
 *  
 *  For example BCDE form a cycle, 
 *              A has won against B and D, 
 *              C and E has won againt F
 *                
 *  If you feel A must be before C and E, and F must be after B and D
 *  (so BCDE is like a single node for the sort) 
 *  then use preserveTransitivity=true.
 *  
 *  If you feel that the cycle should be completly ignored 
 *  then use preserveTransitivity=false.
 *  
 *  
 * You can also get the rank of a player using:
 * getPlayer(id).rank
 * If two players have same sc, they may have equal ranking
 * (depending of games played) 
 * 
 * getPlayer(id).cycleClass
 * If two players have the same "cycleClass" number, they belong to the same cycle.
 * So they can't be decided by Direct Confrontation.
 * 
 */

package mw.go.confrontation;

import java.util.*;

public class Confrontation {

	private final Map<Integer, Node> nodes = new LinkedHashMap<Integer, Node>();
	private final Set<Edge> edges = new HashSet<Edge>();
	
	private final Set<Set<Node>> cycles = new HashSet<Set<Node>>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Confrontation conf = new Confrontation();
		
		conf.newPlayer(1, 4);
		conf.newPlayer(2, 3);
		conf.newPlayer(3, 4);
		conf.newPlayer(4, 4);
		conf.newPlayer(5, 1);
		conf.newPlayer(6, 2);
		
		// cycle fo 1,2,3
		conf.newGame(3, 1);
		conf.newGame(1, 2);
		conf.newGame(2, 3);
		
		conf.newGame(1, 4);
		conf.newGame(3, 4);
		conf.newGame(5, 2); 

		
		List<Integer> result = conf.topologicalSort(true, true);

		System.out.println("id\trk\tcc\tsc");
	
		for (Integer p : result) {
			Node n = conf.getPlayer(p);
			System.out.println(n.id + "\t" + n.rank + "\t" + n.cycleClass + "\t" + n.sc);
		}

	}

	public Confrontation()
	{
		
	}
	
	public void newPlayer(int id, float sc)
	{
		if (!nodes.containsKey(id)) {
			nodes.put(id, new Node(id,sc));
		}
	}
	
	public void newGame(int idWinner, int idLoser)
	{
		if (nodes.containsKey(idWinner) && nodes.containsKey(idLoser) && 
				(findEdge(idLoser, idWinner) == null)) {
			Node wn = nodes.get(idWinner);
			Node ln = nodes.get(idLoser);
			edges.add(new Edge(ln, wn, Edge.Type.GAME));
		}
	}
	
	private Edge findEdge(int from, int to) {
		Edge found = null;
		Node fn = nodes.get(from);
		
		if (fn != null) {
			if (from == to) {
				found = fn.loop;
			} else
			{
				found = fn.outgoing.get(to);
			}			
		}
	
		return found;
	}
	
	/*
	 * When detecting cycles, process this node, calculating the  
	 * edges obtained by transitivity and adding this node to their "via" set.
	 */
	private void transition(Node n)
	{
		for (Edge comingEdge : n.coming.values()) {
			for (Edge outgoingEdge : n.outgoing.values()) {
				
				// transitive edge: te = ce + oe, find if it already exist.
				Edge te = findEdge(comingEdge.from.id, outgoingEdge.to.id);
				
				if (te == null) {
					te = new Edge(comingEdge.from, outgoingEdge.to, Edge.Type.TRANSITIVE);
					edges.add(te);
				}
				
				te.via.add(n);
				te.via.addAll(comingEdge.via);
				te.via.addAll(outgoingEdge.via);
				if (n.loop != null) {
					te.via.addAll(n.loop.via);
				}
			}
		}
		
		n.isolate();
	}

	private void detectCycles() 
	{
		// set of nodes that have a loop
		final Set<Node> nodesWithLoop = new HashSet<Node>();
	
		for (Node node: nodes.values()) {
			transition(node);
			
			if (node.loop != null) {
				// Don't keep the smaller cycle.
				nodesWithLoop.removeAll(node.loop.via);
				nodesWithLoop.add(node);
			}
		}
		
		cycles.clear();
		
		for (Node nwl : nodesWithLoop) {
			Set<Node> cycle = new HashSet<Node>(); 
			cycle.add(nwl);

			for (Node l : nwl.loop.via) {
				l.cycleClass = nwl.id;
				cycle.add(l);
			}
			cycles.add(cycle);
		}

		reinitGraph();
//		System.out.println(cycles);		
	}
	
	private void reinitGraph()
	{
		// Remove transitive edges and re-register normal ones 
		Iterator<Edge> ei = edges.iterator();
		while (ei.hasNext()) {
			Edge e = ei.next();
			
			if (e.type == Edge.Type.TRANSITIVE  || e.type == Edge.Type.TR) {
				e.unregister();
				ei.remove();
			} else {
				e.register();
				e.via.clear();
			}
			
		}
	}
	
	public void displayPlayers() 
	{
		System.out.println("id\trk\tcc\tsc");
		
		for (Node n : nodes.values())  {
			System.out.println(n.id + "\t" + n.rank + "\t" + n.cycleClass + "\t" + n.sc);
		}
	}

	/*
	 * Make the directed graph acyclic 
	 * must detect cycle first.
	 */
	private void removeCyclingEdges(boolean addTransitiveEdges) {
		// actually, only unregister and tag them
		for (Edge edge : edges) {
			if (edge.from.cycleClass == edge.to.cycleClass) {
				edge.type = Edge.Type.CYCLING;
				edge.unregister();
			}
		}

		// Replace the cycling edges removed, so existence of pathes
		// between nodes that are not in the same cycle classe is preserved.
		// example: graph with edges AB, BC, CD, DB, CE    BCD is a cycle
		// edges BC, CD, DB are removed, edges AC, AD, BE, DE are added
		if (addTransitiveEdges) {
			for (Set<Node> cycle : cycles) {
				for (Node origin : cycle) {
					for (Node destination : cycle) {
						if (destination != origin) {
							// duplicate all edges from origin to destination 
							for (Edge c : origin.coming.values()) {
								if (!destination.coming.containsKey(c.from.id)) {
									edges.add(new Edge(c.from, destination, Edge.Type.TR));
								}
							}
							for (Edge c : origin.outgoing.values()) {
								if (!destination.outgoing.containsKey(c.to.id)) {
									edges.add(new Edge(destination, c.to, Edge.Type.TR));
								}
							}
						}
					}
				}
			}
		}

		// System.out.println(edges);
	}

	
	public List<Integer> topologicalSort(boolean upWinners, boolean preserveTransitivity) {
		Set<Node> toBeSorted = new HashSet<Node>();
		toBeSorted.addAll(nodes.values());
		
		LinkedList<Integer> sortedNodes = new LinkedList<Integer>();
		float v = 0;

		detectCycles();
		removeCyclingEdges(preserveTransitivity);
		
		Set<Node> candidates = new HashSet<Node>();
		boolean noCandidates = false;
		
		while (!toBeSorted.isEmpty() || noCandidates) {
			candidates.clear();
			
			for (Node n : toBeSorted) {
				if (upWinners) {
					// candidates are lower sc amongs node who has no coming edge.
					if (n.coming.isEmpty()) {
						if (candidates.isEmpty()) {
							candidates.add(n);
							v = n.sc;
						} else {
							if (n.sc == v) {
								candidates.add(n);
							} else {
								if (n.sc < v) {
									candidates.clear();
									candidates.add(n);
									v = n.sc;
								}
							}
						}
					}
				} else {
					// candidates are best sc amongs node who has no outgoing edge.
					if (n.outgoing.isEmpty()) {
						if (candidates.isEmpty()) {
							candidates.add(n);
							v = n.sc;
						} else {
							if (n.sc == v) {
								candidates.add(n);
							} else {
								if (n.sc > v) {
									candidates.clear();
									candidates.add(n);
									v = n.sc;
								}
							}
						}	
					}
				}
			}
			
			if (candidates.isEmpty()) {
				// not an acyclic graph 
				noCandidates = true;
			} 
			
			int currentRank;
			if (upWinners) {
				currentRank = 1 + toBeSorted.size() - candidates.size();
			} else {
				currentRank = 1 + nodes.size() - toBeSorted.size();
			}
			
			for (Node c : candidates) {
				c.isolate();
				c.rank = currentRank;
				toBeSorted.remove(c);
				if (upWinners) {
					sortedNodes.addFirst(c.id);
				} else {
					sortedNodes.addLast(c.id);
				}
			}
		} 
		
		reinitGraph();
		return sortedNodes;
	}
	
	public Node getPlayer(Integer i) {
		return nodes.get(i);
	}
}
