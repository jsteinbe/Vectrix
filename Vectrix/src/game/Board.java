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
	private int numRows, numCols;
	
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
		for ( int row = 0; row < numRows; ++row ) {
			for ( int column = 0; column < numCols; ++column ) {
				adj = new LinkedList<Integer>();
				//check up
				if ( row > 0 )
					adj.add(calcIndex(row - 1,column));
				//check down
				if ( row < (numRows - 2) )
					adj.add(calcIndex(row + 1,column));
				//check left
				if ( column > 0 )
					adj.add(calcIndex(row,column - 1));
				//check right
				if ( column < (numCols - 2) )
					adj.add(calcIndex(row,column + 1));
				//check up-left
				if ( (row > 0) && (column > 0) )
					adj.add(calcIndex(row - 1, column - 1));
				//check down-left
				if ( (row < (numRows - 2)) && (column > 0) ) 
					adj.add(calcIndex(row + 1, column - 1));
				//check up-right
				if ( (row > 0) && (column < (numCols - 2)) )
					adj.add(calcIndex(row - 1, column + 1));
				//check down-right
				if ( (row < (numRows - 2)) && (column < (numCols - 2)) )
					adj.add(calcIndex(row + 1, column + 1));

				adjMtx.put(calcIndex(row,column), adj);
			}
		}
	}
	
	public int calcIndex(int row, int col){
		return (numCols*row) + col;
	}
	
	public DrawType checkNodeDirection ( Node nodeOne, Node nodeTwo ) {
		return DrawType.DOT;
	}
	
	public boolean areAdjacent( Node node1, Node node2 ) {
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
