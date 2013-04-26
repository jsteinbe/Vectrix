package game;

import java.awt.Color;
import java.util.ArrayList;

public class LineSet {
	
	private Color color;
	private ArrayList<Node> nodes;
	
	public LineSet(Node node1, Node node2) {
		nodes = new ArrayList<Node>();
		nodes.add(node1);
		nodes.add(node2);
	}
	
	public LineSet() {
		nodes = new ArrayList<Node>();
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public int size() {
		return nodes.size();
	}
	
	
}
