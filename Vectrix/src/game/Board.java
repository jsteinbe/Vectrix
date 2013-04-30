package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Board extends JPanel{
	private ArrayList<Color> colors;
	private Node selectedNode;
	private ArrayList<Node> nodes;
	private Map<Integer, LinkedList<Integer>> adjMtx;
	private Solution solution;
	private ArrayList<Path> paths;
	private ArrayList<LineSet> lineSets;
	private int numRows, numCols;
	private int minNumPaths;
	private Map<DrawType, BufferedImage> arrowImages;
	private Map<DrawType, Color> arrowColors;
	private Map<String, BufferedImage> highlightImages;
	private Map<String, Color> highlightColors;
	private final int MAX_NUM_PATH_ARROWS = 11;
	private boolean drawSolution = false;
	private ArrayList<ArrayList<Node>> hints;
	private boolean showCongratsMessage = false;
	private boolean newGame = false;
	
	public Board() {
		drawSolution = false;
		initializeColors();
		nodes = new ArrayList<Node>();
		paths = new ArrayList<Path>();
		lineSets = new ArrayList<LineSet>();
		adjMtx = new HashMap<Integer, LinkedList<Integer>>();
		selectedNode = null;
		hints = new ArrayList<ArrayList<Node>>();
		numRows = 10;
		numCols = 10;
		minNumPaths = (int) Math.ceil(numRows*numCols/10);
		solution = new Solution();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numRows; j++) {
				Node node = new Node(i,j);
				node.setType(DrawType.DOT);
				nodes.add(node);
				solution.getNodes().add(new Node(i,j));
			}
		}
		addMouseListener(new BoardListener(this));
		try {
			loadImages();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		calcAdjacencies();
		generateSolution();
	}
	
	public void initializeColors() {
		colors = new ArrayList<Color>();
		colors.add(new Color(0,0,255));
		colors.add(new Color(0,255,0));
		colors.add(new Color(255,0,255));
		colors.add(new Color(255,255,0));
		colors.add(new Color(255,0,0));
		colors.add(new Color(255,69,0));
		colors.add(new Color(0,250,154));
		colors.add(new Color(153,50,204));
		colors.add(new Color(139,0,139));
		colors.add(new Color(255,20,147));
		colors.add(new Color(128,0,0));
		colors.add(new Color(255,165,0));
		colors.add(new Color(224,255,255));
		colors.add(new Color(95,158,160));
		colors.add(new Color(218,165,32));
		colors.add(new Color(0, 153, 0));
		colors.add(new Color(30, 144, 255));
		colors.add(new Color(253, 188, 180));
		Collections.shuffle(colors);
	}
	
	public boolean checkSolution() {
		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<Node> playerNodes = nodes.get(i).getConnections();
			ArrayList<Node> solutionNodes = solution.getNodes().get(i).getConnections();
			if (!(playerNodes.size() == solutionNodes.size())) {
				return false;
			} else {
				for (int j = 0; j < playerNodes.size(); j++) {
					if (!solutionNodes.contains(playerNodes.get(j))) {
						return false;
					}
				}
			}
		}
		
		showCongratsMessage = true;
		
		return true;
	}
	
	public void nodeLeftClicked( Node node ) {
		if (showCongratsMessage) {
			showCongratsMessage = false;
			newGame = true;
			return;
		}
		
		if ( node.getSelected() == Node.selectType.DELETE ) {
			return;
		} else if ( selectedNode != null ) {
			if ( selectedNode.getSelected() == Node.selectType.DELETE ) {
				return;
			}
		}
		if ( node.getSelected() == Node.selectType.ADD ) {
			selectedNode = null;
			node.setSelected(Node.selectType.NONE);
			return;
		} else {
			if (selectedNode == null) {
				if (node.getConnections().size() < 2) {
					if (node.getConnections().size() == 1 && (node.isArrow() || node.isCircle())) {
						return;
					} else {
						selectedNode = node;
						node.setSelected(Node.selectType.ADD);
						return;
					}
				}
			} else {
				int selectedIndex = calcIndex(selectedNode.getRow(), selectedNode.getCol());
				int clickedIndex = calcIndex(node.getRow(),node.getCol());
				if (adjMtx.get(selectedIndex).contains(clickedIndex) ) {
					if (node.getConnections().size() < 2 && !nodesInSameSet(lineSets, node, selectedNode)) {
						if (!diagonalConflict(selectedNode, node, nodes)) {
							if (correctArrowDirection(selectedNode, node)) {
								if (noDuplicateEnds(selectedNode, node)) {
									if (circleWithOneConnection(node, selectedNode)) {
										updateLineSetsAdd(lineSets, selectedNode, node);
										addConnection(selectedNode, node);
										selectedNode.setSelected(Node.selectType.NONE);
										selectedNode = null;
										
										if (node.getConnections().size() < 2) {
											if (node.isDot()) {
												node.setSelected(Node.selectType.ADD);
												selectedNode = node;
											} else {
												node.setSelected(Node.selectType.NONE);
												selectedNode = null;
											}
										}
									}
								}
							}
						} 
					} 
				} else {
					//NON-ADJACENT NODE
					return;
				}
			}
		}
		checkSolution();
	//	repaint();
	}
	
	public void nodeRightClicked( Node node ) {
		if (showCongratsMessage) {
			showCongratsMessage = false;
			newGame = true;
			return;
		}
		
		
		if ( node.getSelected() == Node.selectType.ADD ) {
			return;
		} else if ( selectedNode != null ) {
			if ( selectedNode.getSelected() == Node.selectType.ADD ) {
				return;
			}
		}
		if ( node.getSelected() == Node.selectType.DELETE ) {
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
				if ( node.getConnections().contains(selectedNode) ) {
					if ( nodesInSameSet(lineSets, selectedNode, node)) {
						deleteConnection(selectedNode, node);
						updateLineSetsDelete(lineSets, selectedNode, node);
						selectedNode.setSelected(Node.selectType.NONE);
						selectedNode = null;
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
		//repaint();
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
		
		generatePaths();
		
		for (Path path : paths) {
			if (path.getArrows().size() > MAX_NUM_PATH_ARROWS) {
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
				paths = new ArrayList<Path>();
				generateSolution();
				return;
			}
		}
		
		if (paths.size() > 18) {
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
			paths = new ArrayList<Path>();
			generateSolution();
			return;
		}
		
		for (int i = 0; i < solution.getNodes().size(); i++) {
			nodes.get(i).setType(solution.getNodes().get(i).getType());
		}
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
		int row = 0;
		Path path;
		ArrayList<Arrow> arrows;
		Arrow arrow;
		Arrow lastArrow = new Arrow();
		Boolean firstTime = true;
		Node nodeTo;
		Node nodeFrom;
		for (LineSet lineSet : solution.getLineSets()) {
			path = new Path(row);
			arrows = new ArrayList<Arrow>();
			if ( lineSet.getNodes().get(0).getType() != DrawType.CIRCLE ) {
				Collections.reverse(lineSet.getNodes());
			}
			firstTime = true;
			for ( int i = 0; i < (lineSet.getNodes().size() - 1); ++i) {
				nodeTo = lineSet.getNodes().get(i + 1);
				nodeFrom = lineSet.getNodes().get(i);
				arrow = new Arrow(row, i, checkNodeDirection(nodeTo, nodeFrom));
				if ( firstTime ) {
					arrows.add(arrow);
					firstTime = false;
				} else if ( !firstTime && (arrow.getDirection() != lastArrow.getDirection()) ) {
					arrows.add(arrow);
				} 
				lastArrow = arrow;
			}
			path.setArrows(arrows);
			paths.add(path);
			row++;
		}
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
	
	public void updateSetColor(LineSet set) {
		if (set.getNodes().get(0).isCircle() && set.getNodes().get(set.getNodes().size() - 1).isArrow() ) {
			int randomNum = (int)(Math.random() * (colors.size() - 2));
			set.setColor(colors.get(randomNum));
			for (Node node : set.getNodes() ) {
				node.setColor(colors.get(randomNum));
			}
			colors.remove(randomNum);
			return;
		}
		if (set.getNodes().get(0).isArrow() && set.getNodes().get(set.getNodes().size() - 1).isCircle() ) {
			int randomNum = (int)(Math.random() * (colors.size() - 2));
			set.setColor(colors.get(randomNum));
			for (Node node : set.getNodes() ) {
				node.setColor(colors.get(randomNum));
			}
			colors.remove(randomNum);
			return;
		}
		
		set.setColor(Color.CYAN);
		for (Node node : set.getNodes() ) {
			node.setColor(set.getColor());
		}

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
			updateSetColor(setOne);
		} else if ((node1.getConnections().size() == 0) && (node2.getConnections().size() > 0)) {
			if (setTwo.getNodes().get(0) == node2) {
				Collections.reverse(setTwo.getNodes());
			}
			setTwo.getNodes().add(node1);
			updateSetColor(setTwo);
		} else {
			// Prepare for combining of sets
			if (setOne.getNodes().get(0) == node1) {
				Collections.reverse(setOne.getNodes());
			}
			if (setTwo.getNodes().get(0) != node2) {
				Collections.reverse(setTwo.getNodes());
			}
			
			setOne.getNodes().addAll(setTwo.getNodes());
			updateSetColor(setOne);
			sets.remove(setTwo);
		}
	}
	
	public void updateLineSetsDelete( ArrayList<LineSet> sets, Node node1, Node node2) {
		LineSet set = setWithNode(sets, node1);		
		
		if (node1.getConnections().size() == 0 && node2.getConnections().size() == 0) {
			colors.add(set.getColor());
			sets.remove(set);
		} else if (node1.getConnections().size() > 0 && node2.getConnections().size() == 0) {
			set.getNodes().remove(node2);
			colors.add(set.getColor());
			updateSetColor(set);
			node2.setColor(Color.CYAN);
		} else if (node1.getConnections().size() == 0 && node2.getConnections().size() > 0) {
			set.getNodes().remove(node1);
			colors.add(set.getColor());
			updateSetColor(set);
			node1.setColor(Color.CYAN);
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
			colors.add(set.getColor());
			System.out.println(set.getColor());
			updateSetColor(newSet);
			updateSetColor(set);
			for (Node node : newSet.getNodes() ) {
				node.setColor(Color.CYAN);
			}
			for (Node node : set.getNodes() ) {
				node.setColor(Color.CYAN);
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
	
	
	public void loadImages() throws IOException {
		arrowImages = new HashMap<DrawType, BufferedImage>();
		arrowColors = new HashMap<DrawType, Color>();
		BufferedImage image;
		image = ImageIO.read(this.getClass().getResource("/images/point_up.png"));
		arrowImages.put(DrawType.UP, image);
		arrowColors.put(DrawType.UP, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_right.png"));
		arrowImages.put(DrawType.RIGHT, image);
		arrowColors.put(DrawType.RIGHT, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_down.png"));
		arrowImages.put(DrawType.DOWN, image);
		arrowColors.put(DrawType.DOWN, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_left.png"));
		arrowImages.put(DrawType.LEFT, image);
		arrowColors.put(DrawType.LEFT, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_upleft.png"));
		arrowImages.put(DrawType.UPLEFT, image);
		arrowColors.put(DrawType.UPLEFT, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_downleft.png"));
		arrowImages.put(DrawType.DOWNLEFT, image);
		arrowColors.put(DrawType.DOWNLEFT, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_upright.png"));
		arrowImages.put(DrawType.UPRIGHT, image);
		arrowColors.put(DrawType.UPRIGHT, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_downright.png"));
		arrowImages.put(DrawType.DOWNRIGHT, image);
		arrowColors.put(DrawType.DOWNRIGHT, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/circle.png"));
		arrowImages.put(DrawType.CIRCLE, image);
		arrowColors.put(DrawType.CIRCLE, Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/dot.png"));
		arrowImages.put(DrawType.DOT, image);
		arrowColors.put(DrawType.DOT, Color.WHITE);
		
		//Highlight
		highlightImages = new HashMap<String, BufferedImage>();
		highlightColors = new HashMap<String, Color>();
		image = ImageIO.read(this.getClass().getResource("/images/point_select_up.png"));
		highlightImages.put("up", image);
		highlightColors.put("up", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_select_right.png"));
		highlightImages.put("right", image);
		highlightColors.put("right", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_select_down.png"));
		highlightImages.put("down", image);
		highlightColors.put("down", Color.WHITE);;
		image = ImageIO.read(this.getClass().getResource("/images/point_select_left.png"));
		highlightImages.put("left", image);
		highlightColors.put("left", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_select_upleft.png"));
		highlightImages.put("upleft", image);
		highlightColors.put("upleft", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_select_downleft.png"));
		highlightImages.put("downleft", image);
		highlightColors.put("downleft", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_select_upright.png"));
		highlightImages.put("upright", image);
		highlightColors.put("upright", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/point_select_downright.png"));
		highlightImages.put("downright", image);
		highlightColors.put("downright", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/circle_select.png"));
		highlightImages.put("circle", image);
		highlightColors.put("circle", Color.WHITE);
		image = ImageIO.read(this.getClass().getResource("/images/dot_select.png"));
		highlightImages.put("dot", image);
		highlightColors.put("dot", Color.WHITE);
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
	
	public Map<DrawType, Color> getColorImage() {
		return arrowColors;
	}
	
	public Color changeColor(BufferedImage img, Color oldColor, Color newColor) {
		if (oldColor != newColor) {
			int width = img.getWidth();
			int height = img.getHeight();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (img.getRGB(i,j) == oldColor.getRGB()) {
						img.setRGB(i, j, newColor.getRGB());
					}
				}
			}
			return newColor;
		}
		return oldColor;
	}
	
	public boolean circleWithOneConnection(Node nodeOne, Node nodeTwo) {
		if ((nodeOne.isCircle()) && nodeOne.getConnections().size() > 0) return false;
		if ((nodeTwo.isCircle()) && nodeTwo.getConnections().size() > 0) return false;
		return true;
	}
	
	public boolean correctArrowDirection(Node node1, Node node2) {
		if (!node1.isArrow() && !node2.isArrow()) {
			return true;
		}
		if (node1.isArrow() && !node2.isArrow()) {
			if (checkNodeDirection(node1, node2) == node1.getType()) {
				return true;
			} else {
				return false;
			}
		}
		if (!node1.isArrow() && node2.isArrow()) {
			if (checkNodeDirection(node2, node1) == node2.getType()) {
				return true;
			} else {
				return false;
			}
		}
		if (node1.isArrow() && node2.isArrow()) {
			return false;
		}

		return false;
	}
	
	
	public boolean noDuplicateEnds(Node node1, Node node2) {
		LineSet set1 = setWithNode(lineSets, node1);
		LineSet set2 = setWithNode(lineSets, node2);
		
		if (set1 != null || set2 != null) {
			if (set1 != null) {
				if (set1.hasArrow() && node2.isArrow()) {
					return false;
				}
				if (set1.hasCircle() && node2.isCircle()) {
					return false;
				}
			}
			if (set2 != null) {
				if (set2.hasArrow() && node1.isArrow()) {
					return false;
				}
				
				if (set2.hasCircle() && node1.isCircle()) {
					return false;
				}
			}
			if (set1 != null && set2 != null) {
				if (set1.hasArrow() && set2.hasArrow()) {
					return false;
				}
				if (set1.hasCircle() && set2.hasCircle()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		if ( drawSolution ) {
			paintSolution(g); 
		} else {
			paintPlayerInput(g);
		}
		
		if ( showCongratsMessage ) {
			try {
				BufferedImage image;
				image = ImageIO.read(this.getClass().getResource("/images/congratulations_message.png"));
				g.drawImage(image, 16, 138, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	public void paintPlayerInput(Graphics g) {
		setBackground(Color.BLACK);
		drawHints(g);
		
		for (LineSet set : lineSets) {
			set.draw(g, this);
		}
		
		for (Node node : nodes) {
			try {
				node.draw(g, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void paintSolution(Graphics g) {
		setBackground(Color.BLACK);
		for (LineSet set : solution.getLineSets()) {
			set.setColor(colors.get(0));
			set.draw(g, this);
			for ( Node node : set.getNodes() ) {
				node.setColor(colors.get(0));
				try {
					node.draw(g, this);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			colors.remove(0);
		}
	}
	
	
	public void drawHints(Graphics g) {
		for (ArrayList<Node> pair : hints) {
			Node nodeOne = pair.get(0);
			Node nodeTwo = pair.get(1);
			g.setColor(new Color(120,120,120));
			DrawType direction = checkNodeDirection(nodeTwo, nodeOne);
			if (direction != DrawType.DOWNRIGHT && direction != DrawType.UPLEFT) {
				g.drawLine(nodeOne.getCol()*50 + 28, nodeOne.getRow()*50 + 28, 
						nodeTwo.getCol()*50 + 28, nodeTwo.getRow()*50 + 28);
				g.drawLine(nodeOne.getCol()*50 + 29, nodeOne.getRow()*50 + 29, 
						nodeTwo.getCol()*50 + 29, nodeTwo.getRow()*50 + 29);
				g.drawLine(nodeOne.getCol()*50 + 32, nodeOne.getRow()*50 + 32, 
						nodeTwo.getCol()*50 + 32, nodeTwo.getRow()*50 + 32);
				g.drawLine(nodeOne.getCol()*50 + 33, nodeOne.getRow()*50 + 33, 
						nodeTwo.getCol()*50 + 33, nodeTwo.getRow()*50 + 33);
			} else {
				g.drawLine(nodeOne.getCol()*50 + 32, nodeOne.getRow()*50 + 28, 
						nodeTwo.getCol()*50 + 32, nodeTwo.getRow()*50 + 28);
				g.drawLine(nodeOne.getCol()*50 + 28, nodeOne.getRow()*50 + 32, 
						nodeTwo.getCol()*50 + 28, nodeTwo.getRow()*50 + 32);
			}
		}
	}
	
	
	public void setDrawSolution(boolean bool) {
		drawSolution = bool;
	}
	
	public Map<DrawType, BufferedImage> getArrowImages() {
		return arrowImages;
	}
	
	public Map<String, BufferedImage> getHighlightImages() {
		return highlightImages;
	}
	
	public Map<String, Color> getHightlightColors() {
		return highlightColors;
	}
	
	public void reset() {
		for (Node node : nodes) {
			node.setConnections(new ArrayList<Node>());
			node.setColor(Color.CYAN);
		}
		lineSets = new ArrayList<LineSet>();
		initializeColors();
	}

	private class BoardListener implements MouseListener {
		private Board board;
		public BoardListener(Board board) {
			this.board = board;
		}
		
		public void mousePressed(MouseEvent e) {
			Point clicked = e.getPoint();
			
			for (Node node : nodes) {
				if (node.containsClicked(clicked)) {
					if (SwingUtilities.isRightMouseButton(e)) {
						nodeRightClicked(node);
					} else if (SwingUtilities.isLeftMouseButton(e)) {
						nodeLeftClicked(node);
					}
					repaint();
					return;
				}
			}
		}
		
		public void actionPerformed(ActionEvent e) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	public boolean getNewGame() {
		return newGame;
	}
	
	public void newHint() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < solution.getNodes().size(); i++) {
			indices.add(i);
		}
		Collections.shuffle(indices);
		for (Integer i : indices) {
			ArrayList<Node> playerNodes = nodes.get(i).getConnections();
			ArrayList<Node> solutionNodes = solution.getNodes().get(i).getConnections();
			for (Node n : solutionNodes) {
				if (!playerNodes.contains(n)) {
					boolean addHint = true;
					ArrayList<Node> newHint = new ArrayList<Node>();
					newHint.add(nodes.get(i));
					newHint.add(n);
					for (ArrayList<Node> prevHint : hints) {
						if (prevHint.contains(newHint.get(0)) && prevHint.contains(newHint.get(1))) {
							addHint = false;
						}
					}
					if (addHint) {
						hints.add(newHint);
						return;
					}
				}
			}
		}
	}
}
