package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Node {
	private int x;
	private int y;
	private Color color;
	private DrawType type;
	public enum selectType {ADD, DELETE, NONE};
	private selectType selected;
	private ArrayList<Node> connections;
	
	public Node() {
		
	}
	
	public ArrayList<Node> getConnections() {
		return new ArrayList<Node>();
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
}
