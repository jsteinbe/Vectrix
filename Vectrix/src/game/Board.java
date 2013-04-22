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
	private ArrayList<LineSet> lineSets;
	private int numRows, numCols;
	
	public Board() {
		nodes = new ArrayList<Node>();
		paths = new ArrayList<Path>();
		lineSets = new ArrayList<LineSet>();
		connections = new ArrayList<Connection>();
		adjMtx = new HashMap<Integer, LinkedList<Integer>>();
		selectedNode = null;
		numRows = 10;
		numCols = 10;
		generateSolution();
		generatePaths();
	}
	
	public boolean checkSolution() {
		return false;
	}
	
	public void nodeLeftClicked( Node node ) {
		if ( node.getSelected() == Node.selectType.DELETE ) {
			return;
		}
		else if ( node.getSelected() == Node.selectType.ADD ) {
			selectedNode = null;
			node.setSelected(Node.selectType.NONE);
			return;
		} else {
			if (selectedNode == null) {
				if (node.getConnections().size() < 2) {
					selectedNode = node;
					node.setSelected(Node.selectType.ADD);
					return;
				} else {
////// SHOW MESSAGE THAT NODE ALREADY HAS TWO CONNECTIONS //////
				}
			} else {
				int selectedIndex = calcIndex(selectedNode.getRow(), selectedNode.getCol());
				int clickedIndex = calcIndex(node.getRow(),node.getCol());
				if (adjMtx.get(selectedIndex).contains(clickedIndex) ) {
					if (node.getConnections().size() < 2 ) {
						if (!diagonalConflict(selectedNode, node)) {
							updateLineSets(selectedNode, node);
							connections.add(new Connection(node, selectedNode));
							node.getConnections().add(selectedNode);
							selectedNode.getConnections().add(node);
							selectedNode = null;
						} else {
////// SHOW MESSAGE THAT LINES CAN'T CROSS //////
						}
					} else {
////// SHOW MESSAGE THAT NODE ALREADY HAS TWO CONNECTIONS //////						
					}
				} else {
////// ADD MESSAGE FOR NON-ADJACENT NODE //////
					return;
				}
			}
		}
////// REPAINT!!! //////
	}
	
	public void nodeRightClicked( Node node ) {
		
		if ( node.getSelected() == Node.selectType.ADD ) {
			return;
		} else if ( node.getSelected() == Node.selectType.DELETE ) {
			node.setSelected(Node.selectType.NONE);
			selectedNode = null;
			return;
		} else {
			if (selectedNode == null) {
				selectedNode = node;
				node.setSelected(Node.selectType.DELETE);
				return;
			} else {
				
				
				int selectedIndex = calcIndex(selectedNode.getRow(), selectedNode.getCol());
				int clickedIndex = calcIndex(node.getRow(),node.getCol());
				if (adjMtx.get(selectedIndex).contains(clickedIndex) ) {
					if (node.getConnections().contains(selectedNode)) {
						Connection toDelete = null;
						for (Connection c : connections) {
							if ((c.getAttached().contains(node)) && (c.getAttached().contains(selectedNode))) {
								toDelete = c;
								break;
							}
						}
						if (toDelete != null) {
							connections.remove(toDelete);
							node.getConnections().remove(selectedNode);
							selectedNode.getConnections().remove(node);
						}
					} else {
////// SHOW MESSAGE THAT NO CONNECTION EXISTS TO DELETE //////						
					}
				} else {
////// ADD MESSAGE FOR NON-ADJACENT NODE //////
					return;
				}
			}
		}
		
////// REPAINT!!! //////
	}
	
	public void generateSolution() {
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numRows; j++) {
				nodes.add(new Node(i, j));
			}
		}
	}
	
	public void generatePaths() {
		
	}
	
	public boolean diagonalConflict(Node node1, Node node2) {
		if ((node1.getRow() != node2.getRow()) && (node1.getCol() != node2.getCol())) {
			int index1 = calcIndex(node1.getRow() + (node1.getRow() - node2.getRow()), node1.getCol());
			int index2 = calcIndex(node1.getRow(), node1.getCol() + (node1.getCol() - node2.getCol()));
			if (nodes.get(index1).getConnections().contains(nodes.get(index2))) {
				return true;
			}
		}
		return false;
	}
	
	public void updateLineSets(Node node1, Node node2) {
		if ((node1.getConnections().size() == 0) && (node2.getConnections().size() == 0)) {
			lineSets.add(new LineSet(node1, node2));
		} else if ((node1.getConnections().size() > 0) && (node2.getConnections().size() == 0)) {
			node1.getLineSet().getList().add(node2);
			node2.setLineSet(node1.getLineSet());
		} else if ((node1.getConnections().size() == 0) && (node2.getConnections().size() > 0)) {
			node2.getLineSet().getList().add(node1);
			node1.setLineSet(node2.getLineSet());
		} else {
			for (Node n : node2.getLineSet().getList()) {
				node1.getLineSet().getList().add(n);
				n.setLineSet(node1.getLineSet());
			}
			lineSets.remove(node2.getLineSet());
		}
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
