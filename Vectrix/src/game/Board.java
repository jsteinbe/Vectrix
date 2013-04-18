package game;

import java.util.ArrayList;

import javax.swing.JPanel;

public class Board extends JPanel{
	private Node selectedNode;
	private ArrayList<Node> nodes;
	private Solution solution;
	private ArrayList<Path> paths;
	
	public Board() {
		
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
	
	public ArrayList<Node> getNodes() {
		return new ArrayList<Node>();
	}
	
	public Solution getSolution() {
		return new Solution();
	}
}
