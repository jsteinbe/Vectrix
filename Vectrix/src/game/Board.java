package game;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;

public class Board extends JPanel{
	private Node selectedNode;
	private ArrayList<Node> nodes;
	private Map<Integer, LinkedList<Integer>> adjMtx;
	private Solution solution;
	private ArrayList<Path> paths;
	private ArrayList<LineSet> lineSets;
	private int numRows, numCols;
	private int minNumPaths;
	
	public Board() {
		nodes = new ArrayList<Node>();
		paths = new ArrayList<Path>();
		lineSets = new ArrayList<LineSet>();
		adjMtx = new HashMap<Integer, LinkedList<Integer>>();
		selectedNode = null;
		numRows = 10;
		numCols = 10;
		minNumPaths = (int) Math.ceil(numRows*numCols/10);
////// THIS WILL BE DIFFERENT, JUST GETS THE TESTS TO COMPILE //////
		solution = new Solution();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numRows; j++) {
				Node node = new Node(i,j);
				node.setType(DrawType.DOT);
				nodes.add(node);
				solution.getNodes().add(new Node(i,j));
			}
		}
		calcAdjacencies();
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
						if (!diagonalConflict(selectedNode, node, nodes)) {
							updateLineSetsAdd(lineSets, selectedNode, node);
							addConnection(selectedNode, node);
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
			if ( node.getConnections().size() == 0 ) {
				return;
			}
			if (selectedNode == null) {
				selectedNode = node;
				node.setSelected(Node.selectType.DELETE);
				return;
			} else {
				int selectedIndex = calcIndex(selectedNode.getRow(), selectedNode.getCol());
				int clickedIndex = calcIndex(node.getRow(),node.getCol());
				if (adjMtx.get(selectedIndex).contains(clickedIndex) ) {
					if (node.getConnections().contains(selectedNode)) {
						deleteConnection(selectedNode, node);
						updateLineSetsDelete(lineSets, selectedNode, node);
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
		ArrayList<LineSet> sets = new ArrayList<LineSet>();
		ArrayList<Node> solutionNodes = solution.getNodes();
		ArrayList<Integer> shuffled = new ArrayList<Integer>();
		for(int i = 0; i < solutionNodes.size(); i++) {
			shuffled.add(i);
		}
		Collections.shuffle(shuffled);
		LinkedList<Integer> nodeAdjList = new LinkedList<Integer>();
		boolean madeConnection;
		Node adj;
		
		//Generates an approximate solution.
		for ( Integer index : shuffled ) {
			Node node = solutionNodes.get(index);
			
			if (node.getConnections().size() < 2) {
				nodeAdjList = new LinkedList<Integer>(adjMtx.get(index));
				Collections.shuffle(nodeAdjList);
				madeConnection = false;
				
				for ( Integer indexAdj : nodeAdjList ) {
					adj = solutionNodes.get(indexAdj);
					if (!diagonalConflict(node, adj, solutionNodes)) {
						if ( adj.getConnections().size() < 2 && !nodesInSameSet(sets, node, adj) ) {
							updateLineSetsAdd(sets, node, adj);
							addConnection(node, adj);
							
							madeConnection = true;
							break;
						}
					}
				}
				
				
				if ( !madeConnection && node.getConnections().size() == 0) {
					boolean finished = false;
					for ( Integer indexAdj : nodeAdjList ) {
						adj = solutionNodes.get(indexAdj);
						for ( Node adjAdj : adj.getConnections() ) {
							if ( adjAdj.getConnections().size() == 2 ) {
								if (!diagonalConflict(adj,node, solutionNodes)) {
									deleteConnection(adj, adjAdj);
									updateLineSetsDelete(sets, adj, adjAdj);
									updateLineSetsAdd(sets, adj, node);
									addConnection(adj, node);
									finished = true;
									break;
								}
							}
						}
						if (finished) {
							break;
						}
					}
				}	
			}
		}
		
		
		/*
		
		// Fix too small paths	
		ArrayList<LineSet> copyOfSets = new ArrayList<LineSet>(sets);
		for (LineSet set : copyOfSets) {
			if (set.size() < 4) {
				madeConnection = false;
				Node endNode = set.getNodes().get(0);
				shuffled = new ArrayList<Integer>(adjMtx.get(solutionNodes.indexOf(endNode)));
				Collections.shuffle(shuffled);
				// Loop over adjacent nodes
				for (Integer index : shuffled) {
					Node nodeAdj = solutionNodes.get(index);
					if (!diagonalConflict(endNode, nodeAdj, solutionNodes)) {
						if (nodeAdj.getConnections().size() == 1 && !nodesInSameSet(sets, endNode, nodeAdj)) {
							updateLineSetsAdd(sets, endNode, nodeAdj);
							addConnection(endNode, nodeAdj);
							madeConnection = true;
						}
					}
				}
				// All adjacent nodes have two connections already
				if (!madeConnection) {
					for (Integer index : shuffled) {
						Node nodeAdj = solutionNodes.get(index);
						Node currentNode = nodeAdj.getConnections().get(0);
						Node prevNode = nodeAdj;
						int lengthOne = 1;
						if (!nodesInSameSet(sets, endNode, nodeAdj)) {
							while(currentNode.getConnections().size() == 2) {
								for (Node nextNode : currentNode.getConnections()) {
									if (nextNode != prevNode) {
										prevNode = currentNode;
										currentNode = nextNode;
										break;
									}
								}
								lengthOne++;
							}
							currentNode = nodeAdj.getConnections().get(1);
							prevNode = nodeAdj;
							int lengthTwo = 1;
							while(currentNode.getConnections().size() == 2) {
								for (Node nextNode : currentNode.getConnections()) {
									if (nextNode != prevNode) {
										prevNode = currentNode;
										currentNode = nextNode;
										break;
									}
								}
								lengthTwo++;
							}
							if (lengthOne < 3 || lengthTwo < 3) {
								continue;
							} else {
								if (lengthOne > 3) {
									if (!diagonalConflict(nodeAdj, endNode, solutionNodes)) {
										deleteConnection(nodeAdj, nodeAdj.getConnections().get(0));
										updateLineSetsDelete(sets, nodeAdj, nodeAdj.getConnections().get(0));
										updateLineSetsAdd(sets, nodeAdj, endNode);
										addConnection(nodeAdj, endNode);
										madeConnection = true;
										break;
									}
								} else if (lengthTwo > 3) {
									if (!diagonalConflict(nodeAdj, endNode, solutionNodes)) {
										Node newNodeAdj = nodeAdj.getConnections().get(1);
										deleteConnection(nodeAdj, nodeAdj.getConnections().get(1));
										updateLineSetsDelete(sets, nodeAdj, newNodeAdj);
										updateLineSetsAdd(sets, nodeAdj, endNode);
										addConnection(nodeAdj, endNode);
										madeConnection = true;
										break;
									}
								} else {
									// Both equal to 3
									continue;
								}
							}
						}
					}
					
					
					
					// All hope is lost, try again
					if (!madeConnection) {
						while (!solution.getNodes().isEmpty()) {
							solution.getNodes().remove(0);
						}
						for (int i = 0; i < numRows; i++) {
							for (int j = 0; j < numRows; j++) {
								Node node = new Node(i,j);
								node.setType(DrawType.DOT);
								solution.getNodes().add(node);
							}
						}
						generateSolution();
						return;
					}				
				}
			}
		}
		
		*/
		
		
		/*
		
		// Fix paths that are too long
		LineSet longSet = thisRightHereIsALongSet(sets);
		while (longSet != null) {
			int range = longSet.getNodes().size() - 9;
			int splitIndex = (int) Math.random()*range;
			Node splitOne = longSet.getNodes().get(splitIndex);
			Node splitTwo = longSet.getNodes().get(splitIndex + 1);
			deleteConnection(splitOne, splitTwo);
			updateLineSetsDelete(sets, splitOne, splitTwo);
			longSet = thisRightHereIsALongSet(sets);
		}
		
		*/
		
		
		// Too few paths
		while (sets.size() < minNumPaths) {
			LineSet longestSet = sets.get(0);
			for (LineSet set : sets) {
				if (set.getNodes().size() > longestSet.getNodes().size()) {
					longestSet = set;
				}
			}
			int range = longestSet.getNodes().size() - 9;
			int splitIndex = 4 + ((int) (Math.random()*range));
			
			Node splitOne = longestSet.getNodes().get(splitIndex);
			Node splitTwo = longestSet.getNodes().get(splitIndex + 1);
			deleteConnection(splitOne, splitTwo);
			updateLineSetsDelete(sets, splitOne, splitTwo);
		}
		
		for (LineSet set : sets) {
			int rand = (int) Math.round(Math.random());
			if (rand == 0) {
				set.getNodes().get(0).setType(DrawType.CIRCLE);
				DrawType arrowDirection = checkNodeDirection(set.getNodes().get(set.getNodes().size() - 1), set.getNodes().get(set.getNodes().size() - 2));
				set.getNodes().get(set.size() - 1).setType(arrowDirection);
			} else {
				set.getNodes().get(set.size() - 1).setType(DrawType.CIRCLE);
				DrawType arrowDirection = checkNodeDirection(set.getNodes().get(0), set.getNodes().get(1));
				set.getNodes().get(0).setType(arrowDirection);
			}	
		}
		
		// Check if too short of path, regenerate
		for (LineSet set : sets) {
			if (set.getNodes().size() < 3) {
				while (!solution.getNodes().isEmpty()) {
					solution.getNodes().remove(0);
				}
				for (int i = 0; i < numRows; i++) {
					for (int j = 0; j < numRows; j++) {
						Node node = new Node(i,j);
						node.setType(DrawType.DOT);
						solution.getNodes().add(node);
					}
				}
				generateSolution();
				return;
			}
		}
		
		// Check if isolated node, regenerate
		for (Node n : solutionNodes) {
			if (n.getConnections().size() == 0) {
				while (!solution.getNodes().isEmpty()) {
					solution.getNodes().remove(0);
				}
				for (int i = 0; i < numRows; i++) {
					for (int j = 0; j < numRows; j++) {
						Node node = new Node(i,j);
						node.setType(DrawType.DOT);
						solution.getNodes().add(node);
					}
				}
				generateSolution();
				return;
			}
		}
		
		solution = new Solution(sets, solutionNodes);
	}
	
	
	public LineSet thisRightHereIsALongSet(ArrayList<LineSet> sets) {
		for (LineSet set : sets) {
			if (set.size() > (int)(nodes.size()*.25)) {
				return set;
			}
		}
		return null;
	}
	
	
	public boolean nodesInSameSet( ArrayList<LineSet> sets, Node node, Node checkNode ) {
		for ( LineSet set : sets ) {
			if ( set.getNodes().contains(node) && set.getNodes().contains(checkNode) ) {
				return true;
			}
		}
		return false;
	}
	
	public void addConnection(Node nodeOne, Node nodeTwo) {
		if (nodeOne.getConnections().size() < 2 && nodeTwo.getConnections().size() < 2 ) {
			if (!nodeOne.getConnections().contains(nodeTwo)) {
				nodeOne.getConnections().add(nodeTwo);
			}
			if (!nodeTwo.getConnections().contains(nodeOne)) {
				nodeTwo.getConnections().add(nodeOne);
			}
		}
	}
	
	public void deleteConnection( Node nodeOne, Node nodeTwo ) {
		nodeTwo.getConnections().remove(nodeOne);
		nodeOne.getConnections().remove(nodeTwo);
	}
	
	public void generatePaths() {
		
	}
	
	public boolean diagonalConflict(Node node1, Node node2, ArrayList<Node> nodes) {
		
		int index1 = 0;
		int index2 = 0;
		
		if ((node1.getRow() != node2.getRow()) && (node1.getCol() != node2.getCol())) {
			index1 = calcIndex(node1.getRow(), node2.getCol());
			index2 = calcIndex(node2.getRow(), node1.getCol());
		}

		if (nodes.get(index1).getConnections().contains(nodes.get(index2))) {
			return true;
		}
		return false;
	}
	
	public void updateLineSetsAdd( ArrayList<LineSet> sets, Node node1, Node node2) {
		LineSet setOne = setWithNode(sets, node1);
		LineSet setTwo = setWithNode(sets, node2);
		
		if ((node1.getConnections().size() == 0) && (node2.getConnections().size() == 0)) {
			sets.add(new LineSet(node1, node2));
		} else if ((node1.getConnections().size() > 0) && (node2.getConnections().size() == 0)) {
			if (setOne.getNodes().get(0) == node1) {
				Collections.reverse(setOne.getNodes());
			}
			setOne.getNodes().add(node2);
		} else if ((node1.getConnections().size() == 0) && (node2.getConnections().size() > 0)) {
			if (setTwo.getNodes().get(0) == node2) {
				Collections.reverse(setTwo.getNodes());
			}
			setTwo.getNodes().add(node1);
		} else {
			// Prepare for combining of sets
			if (setOne.getNodes().get(0) == node1) {
				Collections.reverse(setOne.getNodes());
			}
			if (setTwo.getNodes().get(0) != node2) {
				Collections.reverse(setTwo.getNodes());
			}
			
			setOne.getNodes().addAll(setTwo.getNodes());
			
			sets.remove(setTwo);
		}
	}
	
	public void updateLineSetsDelete( ArrayList<LineSet> sets, Node node1, Node node2) {
		LineSet set = setWithNode(sets, node1);		
		
		if (node1.getConnections().size() == 0 && node2.getConnections().size() == 0) {
			sets.remove(set);
		} else if (node1.getConnections().size() > 0 && node2.getConnections().size() == 0) {
			set.getNodes().remove(node2);
		} else if (node1.getConnections().size() == 0 && node2.getConnections().size() > 0) {
			set.getNodes().remove(node1);
		} else {
			// Split into two sets
			int firstIndex = set.getNodes().indexOf(node1);
			int secondIndex = set.getNodes().indexOf(node2);
			int splitIndex = Math.min(firstIndex, secondIndex);
			
			LineSet newSet = new LineSet();
			// Copy nodes to new set
			for (int i = 0; i <= splitIndex; i++) {
				newSet.getNodes().add(set.getNodes().get(i));
			}
			// Remove nodes from old set
			for (int i = splitIndex; i >= 0; i--) {
				set.getNodes().remove(i);
			}
			
			sets.add(newSet);
		}
	}
	
	public LineSet setWithNode( ArrayList<LineSet> sets, Node node) {
		for (LineSet set : sets) {
			if (set.getNodes().contains(node)) {
				return set;
			}
		}
		return null;
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
				if ( row < (numRows - 1) )
					adj.add(calcIndex(row + 1,column));
				//check left
				if ( column > 0 )
					adj.add(calcIndex(row,column - 1));
				//check right
				if ( column < (numCols - 1) )
					adj.add(calcIndex(row,column + 1));
				//check up-left
				if ( (row > 0) && (column > 0) )
					adj.add(calcIndex(row - 1, column - 1));
				//check down-left
				if ( (row < (numRows - 1)) && (column > 0) ) 
					adj.add(calcIndex(row + 1, column - 1));
				//check up-right
				if ( (row > 0) && (column < (numCols - 1)) )
					adj.add(calcIndex(row - 1, column + 1));
				//check down-right
				if ( (row < (numRows - 1)) && (column < (numCols - 1)) )
					adj.add(calcIndex(row + 1, column + 1));

				adjMtx.put(calcIndex(row,column), adj);
			}
		}
		
	}
	
	public int calcIndex(int row, int col){
		return (numCols*row) + col;
	}
	
	public DrawType checkNodeDirection ( Node nodeTo, Node nodeFrom ) {
		int rowTo = nodeTo.getRow();
		int colTo = nodeTo.getCol();
		int rowFrom = nodeFrom.getRow();
		int colFrom = nodeFrom.getCol();
		//Right and Left
		if ( rowTo == rowFrom ) {
			if ( colTo > colFrom ) {
				return DrawType.RIGHT;
			}
			if ( colFrom > colTo ){
				return DrawType.LEFT;
			}
		}
		
		//Up and Down
		if ( colTo == colFrom ) {
			if ( rowTo > rowFrom ) {
				return DrawType.DOWN;
			}
			if ( rowFrom > rowTo ) {
				return DrawType.UP;
			}
		}
		
		//Up diagonals
		if ( rowTo < rowFrom ) {
			if ( colTo > colFrom ) {
				return DrawType.UPRIGHT;
			}
			if ( colTo < colFrom ) {
				return DrawType.UPLEFT;
			}
		}
		
		//Down diagonals
		if ( rowTo > rowFrom ) {
			if (colTo > colFrom ) {
				return DrawType.DOWNRIGHT;
			}
			if (colTo < colFrom ) {
				return DrawType.DOWNLEFT;
			}
		}
		
		return DrawType.DOT;
	}
	
	public boolean areAdjacent( Node node1, Node node2 ) {
		int indexOne = calcIndex(node1.getRow(), node1.getCol());
		int indexTwo = calcIndex(node2.getRow(), node2.getCol());
		return (adjMtx.get(indexOne).contains(indexTwo) && adjMtx.get(indexTwo).contains(indexOne));
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public ArrayList<Path> getPaths() {
		return paths;
	}
	
	public Solution getSolution() {
		return solution;
	}

	public Map<Integer, LinkedList<Integer>> getAdjMtx() {
		return adjMtx;
	}
	
	public void printAdjList ( Node node ) {
		int index = calcIndex(node.getRow(), node.getCol() );
		System.out.print("Adj list for " + index + ": ");
		for( int i : adjMtx.get(index) ) {
			System.out.print(i + " ");
		}
		System.out.println();
	}
	
	public void printAdjMtx() {
		for (Node node : nodes) {
			printAdjList(node);
		}
	}
	
	public ArrayList<LineSet> getLineSets() {
		return lineSets;
	}
	
	public void setSelectedNode(Node node) {
		selectedNode = node;
	}
}
