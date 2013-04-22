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
	private LineSet lineSet;
	
	public Node(int row, int col) {
		this.row = row;
		this.col = col;
		connections = new ArrayList<Node>();
	}
	
	public Node() {
		connections = new ArrayList<Node>();
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

	public LineSet getLineSet() {
		return lineSet;
	}

	public void setLineSet(LineSet lineSet) {
		this.lineSet = lineSet;
	}

	public void setType(DrawType type) {
		this.type = type;
	}
	
	
}
