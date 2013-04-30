package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class GameControl extends JFrame {
	private Board board;
	private boolean newPuzzle = false;
	private JButton giveUpButton;
	private JFrame self = this;	
	
	public GameControl() throws IOException {
		setSize(760, 740);
		setTitle("Vectrix");
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		board = new Board();
		try {
			createPathsPanel();
			createButtonsPanel();
			createLogo();
			add(board);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		setVisible(true);
	}
	
	public void createPathsPanel() throws IOException {
		JPanel pathPanel = new JPanel();
		pathPanel.setBackground(Color.BLACK);
		pathPanel.setLayout(new GridLayout(board.getPaths().size() + 1, 0));
		BufferedImage image = ImageIO.read(this.getClass().getResource("/images/paths_label.png"));
		JLabel icon = new JLabel(new ImageIcon(image));
		pathPanel.add(icon);
		for ( Path path : board.getPaths() ) {
			pathPanel.add(path.getPathPanel());
		}
		add(pathPanel, BorderLayout.EAST);
	}
	
	public void createButtonsPanel() throws IOException {
		JPanel buttons = new JPanel();
		buttons.setBackground(Color.BLACK);
		buttons.setLayout(new GridLayout(0, 3));
		BufferedImage img;
		
		
		//Give up.
		img = ImageIO.read(this.getClass().getResource("/images/giveup_label.png"));
		giveUpButton = new JButton(new ImageIcon(img));
		giveUpButton.setBackground(Color.BLACK);
		giveUpButton.setBorderPainted(false);
		giveUpButton.setFocusPainted(false);
		giveUpButton.setBorder(null);
		//Give up listener
		giveUpButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e){
					if (!newPuzzle) {
						board.reset();
						board.setDrawSolution(true);
						newPuzzle = true;
						/////////////////////FINISH CHANGING IMAGE OF GIVEUPBUTTON//////////////////////////////////
						board.repaint();
					} else {
						board.newPuzzle();
						newPuzzle = false;
						board.repaint();
					}
				}
			}
		);
		buttons.add(giveUpButton);
		
		//Hint
		img = ImageIO.read(this.getClass().getResource("/images/hint_label.png"));
		JButton hintButton = new JButton(new ImageIcon(img));
		hintButton.setBackground(Color.BLACK);
		hintButton.setBorderPainted(false);
		hintButton.setFocusPainted(false);
		hintButton.setBorder(null);
		//hintButton listener
		hintButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e){
					board.newHint();
					board.repaint();
				}
			}
		);
		buttons.add(hintButton);
		
		//Reset
		img = ImageIO.read(this.getClass().getResource("/images/reset_label.png"));
		JButton resetButton = new JButton(new ImageIcon(img));
		resetButton.setBackground(Color.BLACK);
		resetButton.setBorderPainted(false);
		resetButton.setFocusPainted(false);
		resetButton.setBorder(null);
		//Give up listener
		resetButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					board.reset();
					board.repaint();
				}
			}
		);
		buttons.add(resetButton);
		
		add(buttons, BorderLayout.SOUTH);
	}
	
	public void createLogo() throws IOException {
		JPanel logo = new JPanel();
		logo.setBackground(Color.BLACK);
		
		BufferedImage image = ImageIO.read(this.getClass().getResource("/images/vectrix_logo.png"));
		JLabel icon = new JLabel(new ImageIcon(image));
		logo.add(icon);
		add(logo, BorderLayout.NORTH);
	}
	
	public static void main(String[] args) {
		try {
			GameControl game = new GameControl();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
