package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class LineSet {
	
	private Color color;
	private ArrayList<Node> nodes;
	
	public LineSet(Node node1, Node node2) {
		nodes = new ArrayList<Node>();
		nodes.add(node1);
		nodes.add(node2);
		color = Color.cyan;
	}

	public LineSet() {
		nodes = new ArrayList<Node>();
		color = Color.cyan;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public boolean hasArrow() {
		for (Node n : nodes) {
			if (n.isArrow()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasCircle() {
		for (Node n : nodes) {
			if (n.isCircle()) {
				return true;
			}
		}
		return false;
	}
	
	public int size() {
		return nodes.size();
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void draw(Graphics g, Board board) {
		Node nodeOne, nodeTwo;
		for ( int i = 0; i < (nodes.size() - 1); i++ ) {
			nodeOne = nodes.get(i);
			nodeTwo = nodes.get(i + 1);
			g.setColor(color);
			DrawType direction = board.checkNodeDirection(nodeTwo, nodeOne);
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
	
}
