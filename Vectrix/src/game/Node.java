package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

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
		color = Color.CYAN;
	}
	
	public Node() {
		connections = new ArrayList<Node>();
		type = DrawType.DOT;
		color = Color.CYAN;
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
	
	public void setSelected( selectType type) {
		selected = type;
	}
	
	public selectType getSelected () {
		return selected;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public Color getColor() {
		return color;
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
	
	public void draw(Graphics g, Board board) throws IOException {
		String url;
		BufferedImage image;
		BufferedImage imageTwo;
		Color imageColor = board.getColorImage().get(type);
		Color imageTwoColor;
		String typeString;
		if ( type == DrawType.UP ) {
			image = board.getArrowImages().get(DrawType.UP);
			imageTwo = board.getHighlightImages().get("up");
			imageTwoColor = board.getHightlightColors().get("up");
			typeString = "up";
		} else if ( type == DrawType.RIGHT ) {
			image = board.getArrowImages().get(DrawType.RIGHT);
			imageTwo = board.getHighlightImages().get("right");
			imageTwoColor = board.getHightlightColors().get("right");
			typeString = "right";
		} else if ( type == DrawType.DOWN ) {
			image = board.getArrowImages().get(DrawType.DOWN);
			imageTwo = board.getHighlightImages().get("down");
			imageTwoColor = board.getHightlightColors().get("down");
			typeString = "down";
		} else if ( type == DrawType.LEFT ) {
			image = board.getArrowImages().get(DrawType.LEFT);
			imageTwo = board.getHighlightImages().get("left");
			imageTwoColor = board.getHightlightColors().get("left");
			typeString = "left";
		} else if ( type == DrawType.UPLEFT ) {
			image = board.getArrowImages().get(DrawType.UPLEFT);
			imageTwo = board.getHighlightImages().get("upleft");
			imageTwoColor = board.getHightlightColors().get("upleft");
			typeString = "upleft";
		} else if ( type == DrawType.DOWNLEFT ) {
			image = board.getArrowImages().get(DrawType.DOWNLEFT);
			imageTwo = board.getHighlightImages().get("downleft");
			imageTwoColor = board.getHightlightColors().get("downleft");
			typeString = "downleft";
		} else if ( type == DrawType.UPRIGHT ) {
			image = board.getArrowImages().get(DrawType.UPRIGHT);
			imageTwo = board.getHighlightImages().get("upright");
			imageTwoColor = board.getHightlightColors().get("upright");
			typeString = "upright";
		} else if ( type == DrawType.DOWNRIGHT ) {
			image = board.getArrowImages().get(DrawType.DOWNRIGHT);
			imageTwo = board.getHighlightImages().get("downright");
			imageTwoColor = board.getHightlightColors().get("downright");
			typeString = "downright";
		} else if ( type == DrawType.CIRCLE) {
			image = board.getArrowImages().get(DrawType.CIRCLE);
			imageTwo = board.getHighlightImages().get("circle");
			imageTwoColor = board.getHightlightColors().get("circle");
			typeString = "circle";
		} else {
			image = board.getArrowImages().get(DrawType.DOT);
			imageTwo = board.getHighlightImages().get("dot");
			imageTwoColor = board.getHightlightColors().get("dot");
			typeString = "dot";
		}
		
		board.changeColor(image, imageColor, color);
		board.getColorImage().put(type, color);
		g.drawImage(image, col*50, row*50, null);
		
		if (selected == selectType.ADD) {
			board.changeColor(imageTwo, imageTwoColor, Color.GREEN);
			board.getHightlightColors().put(typeString, Color.GREEN);
			g.drawImage(imageTwo, col*50, row*50, null);
		} else if (selected == selectType.DELETE) {
			board.changeColor(imageTwo, imageTwoColor, Color.RED);
			board.getHightlightColors().put(typeString, Color.RED);
			g.drawImage(imageTwo, col*50, row*50, null);
		}
	}
	
	public boolean containsClicked(Point click) {
		Rectangle rectangle = new Rectangle(col*50 + 4,row*50 + 4,50, 50);
		if (rectangle.contains(click)) {
			return true;
		}
		return false;
	}
}
