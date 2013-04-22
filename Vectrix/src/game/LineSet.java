package game;

import java.awt.Color;
import java.util.ArrayList;

public class LineSet {
	
	private Color color;
	private ArrayList<Node> list;
	
	public LineSet(Node node1, Node node2) {
		list = new ArrayList<Node>();
		list.add(node1);
		list.add(node2);
	}

	public ArrayList<Node> getList() {
		return list;
	}

	public void setList(ArrayList<Node> list) {
		this.list = list;
	}
	
	
}
