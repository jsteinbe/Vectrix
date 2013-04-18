package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;

public class Board extends JPanel{
	private Node selectedNode;
	private ArrayList<Node> nodes;
	private ArrayList<Connection> connections;
	private Map<Integer, LinkedList<Integer>> adjMtx;
	private Solution solution;
	private ArrayList<Path> paths;
	
	public Board() {
		
	}
	
	public void calcAdjacencies() {
		
	}
	
	public boolean checkSolution() {
		return false;
	}
	
	public void nodeLeftClicked( Node node ) {
		
	}
	
	public void nodeRightClicked( Node node ) {
		
	}
	
	public void generateSolution() {
		
	}
	
	public void generatePaths() {
		
	}
	
	public DrawType checkNodeDirection(Node nodeOne, Node nodeTwo) {
		return DrawType.DOT;
	}
	
	public boolean areAdjacent( Node node1, Node node2) {
		return false;
	}
	
	public ArrayList<Node> getNodes() {
		return new ArrayList<Node>();
	}
	
	public ArrayList<Path> getPaths() {
		return new ArrayList<Path>();
	}
	
	public Solution getSolution() {
		return new Solution();
	}

	public Map<Integer, LinkedList<Integer>> getAdjMtx() {
		return adjMtx;
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}


}
