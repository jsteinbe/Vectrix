package game;

import java.io.IOException;

public class ApplicationControl {

	private GameControl game;
	
	public ApplicationControl() {
		
	}
	
	public void newPuzzle() {
		try {
			game = new GameControl();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		game.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				System.exit(0);
			}
		});
	}
	
	public static void main(String[] args) {
		ApplicationControl app = new ApplicationControl();
		app.newPuzzle();
		boolean wakawaka = true;
		while (true) {
			while(!app.game.getBoard().getNewGame()) {
				wakawaka = true;
			}
			app.game.dispose();
			app.newPuzzle();
		}

	}

}
