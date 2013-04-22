package game;

import java.util.ArrayList;

public class Solution {
	private ArrayList<Connection> connections;
	private ArrayList<Node> nodes;
	
	public Solution() {
		connections = new ArrayList<Connection>();
		nodes = new ArrayList<Node>();
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public ArrayList<Connection> getConnections() {
		return connections;
	}
}
