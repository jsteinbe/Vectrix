package board;

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class GuessDialog extends JDialog {
	public String roomName;
	public JComboBox roomList;
	public JComboBox playerList;
	public JComboBox weaponList;
	
	public GuessDialog(String roomName, boolean isAccusation, ArrayList<Card> cards, ClueGame game, ControlBottomPanel bottom) {
		this.roomName = roomName;
		setTitle("Make a Guess");
		setSize(300,200);
		setLayout(new GridLayout(4,2));
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		JLabel yourRoom = new JLabel("Your room");
		add(yourRoom);
		if (isAccusation) {
			roomList = new JComboBox();
			for (Card c : cards) {
				if (c.getCardType() == Card.CardType.ROOM){
					roomList.addItem(c.getName());
				}
			}
			add(roomList);
		} else {
			JLabel roomBox = new JLabel(roomName);
			add(roomBox);
		}
		
		JLabel yourPerson = new JLabel("Person");
		add(yourPerson);
		playerList = new JComboBox();
		for (Card c : cards) {
			if (c.getCardType() == Card.CardType.PERSON){
				playerList.addItem(c.getName());
			}
		}
		add(playerList);
		
		JLabel yourWeap = new JLabel("Weapon");
		add(yourWeap);
		weaponList = new JComboBox();
		for (Card c : cards) {
			if (c.getCardType() == Card.CardType.WEAPON){
				weaponList.addItem(c.getName());
			}
		}
		add(weaponList);
		
		class SubmitButtonListener implements ActionListener {
			ClueGame game;
			boolean isAccusation;
			GuessDialog gd;
			ControlBottomPanel bottomPanel;
			Solution solution;
			
			public SubmitButtonListener(ClueGame game, boolean isAccusation, GuessDialog gd, ControlBottomPanel bottom) {
				super();
				this.game = game;
				this.isAccusation = isAccusation;
				this.gd = gd;
				this.bottomPanel = bottom;
				
			}
			public void actionPerformed(ActionEvent e) {
				Player accusingPlayer = game.getPlayerList().get(game.getTurnIndicator());
				if ( isAccusation ) {
					game.handleSuggestion((String) gd.playerList.getSelectedItem(),(String)  gd.weaponList.getSelectedItem(), 
							(String) gd.roomList.getSelectedItem(), accusingPlayer);
					dispose();
				}
				else {
					game.handleSuggestion((String) gd.playerList.getSelectedItem(),(String) gd.weaponList.getSelectedItem(), 
							gd.roomName, accusingPlayer);
					System.out.println("Player: " + gd.playerList.getSelectedItem() + 
							"Weapon: " + gd.weaponList.getSelectedItem() + "Room: " + 
							gd.roomName);
					solution = new Solution((String) gd.playerList.getSelectedItem(),(String) gd.weaponList.getSelectedItem(), gd.roomName);
					bottomPanel.setGuess(solution);
					if (game.getResponse() != null) {
						System.out.println("Hello");
						bottomPanel.setResponse(game.getResponse().getName());
						System.out.println(game.getResponse().getName());
					} else {
						bottomPanel.setResponse("No new card");
					}
					dispose();
				}
			}
		}
		JButton submit = new JButton("Submit");
		submit.addActionListener(new SubmitButtonListener(game, isAccusation, this, bottom));
		add(submit);
		
		class CancelButtonListener implements ActionListener {
					
			public CancelButtonListener() {
				super();
			}
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new CancelButtonListener());
		add(cancel);
	}

}
