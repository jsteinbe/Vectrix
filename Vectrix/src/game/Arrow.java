package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Arrow {
	private int row;
	private int col;
	private DrawType direction;
	
	public Arrow() {
		
	}
	
	public Arrow(int row, int col, DrawType direction) {
		this.row = row;
		this.col = col;
		this.direction = direction;
	}
	
	public DrawType getDirection() {
		return direction;
	}
	
	public JLabel getArrowImage() throws IOException {
		String url;
		BufferedImage image;
		JLabel icon;
		if ( direction == DrawType.UP ) {
			url = "/images/arrow_up.png";
		} else if ( direction == DrawType.RIGHT ) {
			url = "/images/arrow_right.png";
		} else if ( direction == DrawType.DOWN ) {
			url = "/images/arrow_down.png";
		} else if ( direction == DrawType.LEFT ) {
			url = "/images/arrow_left.png";
		} else if ( direction == DrawType.UPLEFT ) {
			url = "/images/arrow_upleft.png";
		} else if ( direction == DrawType.DOWNLEFT ) {
			url = "/images/arrow_downleft.png";
		} else if ( direction == DrawType.UPRIGHT ) {
			url = "/images/arrow_upright.png";
		} else if ( direction == DrawType.DOWNRIGHT ) {
			url = "/images/arrow_downright.png";
		} else {
			url = "/images/dot.png";
		}
		image = ImageIO.read(this.getClass().getResource(url));
		icon = new JLabel(new ImageIcon(image));
		return icon;
	}
}
