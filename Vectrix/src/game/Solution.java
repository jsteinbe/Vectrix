package game;

import java.util.ArrayList;

public class Solution {
	private ArrayList<Node> nodes;
	private ArrayList<LineSet> lineSets;
	
	public Solution() {
		lineSets = new ArrayList<LineSet>();
		nodes = new ArrayList<Node>();
	}
	
	public Solution(ArrayList<LineSet> lineSets, ArrayList<Node> nodes) {
		this.lineSets = lineSets;
		this.nodes = nodes;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public ArrayList<LineSet> getLineSets() {
		return lineSets;
	}
}
