package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;

public class Board extends JPanel{
	private Node selectedNode;
	private ArrayList<Node> nodes;
	private Solution solution;
	private ArrayList<Path> paths;
	private int numRow, numCol;
	private Map<Integer, LinkedList<Integer>> adjMtx;
	
	public Board() {
		nodes = new ArrayList<Node>();
		paths = new ArrayList<Path>();
		adjMtx = new HashMap<Integer, LinkedList<Integer>>();
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
	
	public void calcAdjacencies() {
		LinkedList<Integer> adj;
		for ( int row = 0; row < numRow; ++row ) {
			for ( int col = 0; col < numCol; ++col ) {
				adj = new LinkedList<Integer>();
			}
		}
	}
	
	public int calcIndex(int row, int col){
		return (numCol*row) + col;
	}
	
	public DrawType checkNodeDirection(Node nodeOne, Node nodeTwo) {
		return DrawType.DOT;
	}
	
	public ArrayList<Node> getNodes() {
		return new ArrayList<Node>();
	}
	
	public Solution getSolution() {
		return new Solution();
	}
}
