/*
 * Copyright 2007 Matthieu Walraet
 *
 * mw.go.confrontation package is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU General Public 
 * License version 2, as published by the Free Software Foundation.
 * 
 * Created: 2007/08/26
 * 
 */

package mw.go.confrontation;

/*
 * Edge of the directed graph of players
 * When it correspond to a game from is loser, to is winner.
 * 
 */

import java.util.*;

public class Edge {
	public final Node from;
	public final Node to;
	
	// used for loop detection algo, if there is one or more path 
	// from A to B, via(AB) is the set of nodes that are on these paths.
	// A -> N -> B     N is in via set of edge AB. 
	// if AB is not an edge of initial graph, it is created as "transitive"
	public final Set<Node> via = new HashSet<Node>();
	
    public enum Type { GAME, CYCLING, TRANSITIVE, TR };
    public Type type;
 
	
	public Edge(Node from, Node to, Type type) 
	{
		this.from = from;
		this.to = to;
		this.type = type;
		
		register();
	}
	
	public void register()
	{
		if (from == to) {
			to.loop = this;
		} else {
			from.outgoing.put(to.id, this);
			to.coming.put(from.id, this);
		}		
	}
	
	public void unregister()
	{
		if (from == to) {
			to.loop = null;
		} else {
			from.outgoing.remove(to.id);
			to.coming.remove(from.id);
		}
	}

	public String toString() {
		return type + " from " + from.id + " to " + to.id;
	}
	
	
}
