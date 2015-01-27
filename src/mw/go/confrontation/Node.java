/*
 * Copyright 2007 Matthieu Walraet
 *
 * mw.go.confrontation package is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU General Public 
 * License version 2, as published by the Free Software Foundation.
 * 
 * Created: 2007/08/26  
 */

package mw.go.confrontation;

import java.util.*;

public class Node {
	public final int  id;
	public final float sc;
	
	public final Map<Integer, Edge> coming = new HashMap<Integer, Edge>();
	public final Map<Integer, Edge> outgoing = new HashMap<Integer, Edge>();
	public Edge loop = null;

	// when several nodes form a loop, they have the same looping class.
	public int cycleClass;
	public int rank = 0;
	
	public Node(int id, float sc)
	{
		this.id = id;
		this.sc = sc;
		
		cycleClass = id;
	}

	public String toString() {
		return Integer.toString(id);
	}

	/*
	 * Unregister all its coming and outgoing edge. (keep loop)
	 */
	public Set<Edge> isolate() {
		Set<Edge> edgeToClear = new HashSet<Edge>();
		
		// Can't iterate coming while edges are removed.
		edgeToClear.addAll(coming.values());
		edgeToClear.addAll(outgoing.values());
		
		for (Edge e : edgeToClear) {
			e.unregister();
		}
		
		return edgeToClear;
	}
	
}
