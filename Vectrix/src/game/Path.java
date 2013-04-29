package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Path {
	private int row;
	private ArrayList<Arrow> arrows;
	private final int MAX_NUM_PATH_ARROWS = 12;
	
	public Path() {
		arrows = new ArrayList<Arrow>();
	}
	
	public Path(int row) {
		this.row = row;
		arrows = new ArrayList<Arrow>();
	}
	
	public ArrayList<Arrow> getArrows() {
		return arrows;
	}
	
	public void setArrows(ArrayList<Arrow> arrows) {
		this.arrows = arrows;
	}
	
	public JPanel getPathPanel() throws IOException {
		BufferedImage image = ImageIO.read(this.getClass().getResource("/images/blank.png"));
		JLabel icon = new JLabel(new ImageIcon(image));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, MAX_NUM_PATH_ARROWS ));
		for (Arrow arrow : arrows ) {
			panel.add(arrow.getArrowImage());
		}
		for ( int i = arrows.size() - 1; i < MAX_NUM_PATH_ARROWS; i++ ) {
			panel.add(icon);
		}
		panel.setBackground(Color.BLACK);
		return panel;
	}
}
