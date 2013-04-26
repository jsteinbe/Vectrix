package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Node {
	public enum selectType {ADD, DELETE, NONE};
	
	private int row;
	private int col;
	private Color color;
	private DrawType type;
	private selectType selected;
	private ArrayList<Node> connections;
	
	public Node(int row, int col) {
		this.row = row;
		this.col = col;
		connections = new ArrayList<Node>();
		type = DrawType.DOT;
	}
	
	public Node() {
		connections = new ArrayList<Node>();
		type = DrawType.DOT;
	}
	
	public ArrayList<Node> getConnections() {
		return connections;
	}
	
	public DrawType getType() {
		return type;
	}
	
	public boolean isArrow() {
		return ( type != DrawType.CIRCLE && type != DrawType.DOT );
	}
	
	public boolean isDot() {
		return ( type == DrawType.DOT );
	}
	
	public boolean isCircle() {
		return ( type == DrawType.CIRCLE );
	}
	
	public void draw( Graphics g ) {
		
	}
	
	public void setSelected( selectType type) {
		selected = type;
	}
	
	public selectType getSelected () {
		return selected;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	public void setType(DrawType type) {
		this.type = type;
	}
	
	public void setConnections(ArrayList<Node> connections) {
		this.connections = connections;
	}
}
