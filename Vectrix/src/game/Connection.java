package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Connection {
	private Color color;
	private ArrayList<Node> attached;
	
	public Connection() {
		attached = new ArrayList<Node>();
	}
	
	public Connection(Node node1, Node node2) {
		attached = new ArrayList<Node>();
		attached.add(node1);
		attached.add(node2);
	}
	
	public ArrayList<Node> getAttached() {
		return attached;
	}

	public void draw(Graphics g) {
		
	}
}
