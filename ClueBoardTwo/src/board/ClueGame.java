package board;

import java.awt.Color;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class ClueGame {

	private Solution solution = new Solution();
	private Solution currGuess = new Solution();
	private Solution unDeniedGuess = new Solution();
	private Player humanPlayer = new HumanPlayer();
	private ArrayList<Card> cards = new ArrayList<Card>();
	private ArrayList<Card> cardsDealt = new ArrayList<Card>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private Board board;
	private Card response;
	private int turnIndicator;
	private int roll;
	private boolean humanMustFinish;
	private GUI frame;
	
	/************************************************************************************************************
 	* Default constructor
 	************************************************************************************************************/
	public ClueGame() {
		loadConfigFiles("people.txt", "cards.txt");
		board = new Board("Clue Board.csv", "Legend.txt");
		board.loadConfigFiles();
		board.calcAdjacencies();
		board.setPlayers(players);
	}
	
	public ClueGame(GUI frame) {
		loadConfigFiles("people.txt", "cards.txt");
		board = new Board("Clue Board.csv", "Legend.txt", this, frame);
		board.loadConfigFiles();
		board.calcAdjacencies();
		board.setPlayers(players);
	}
	
	/************************************************************************************************************
 	* Parameterized constructor
 	************************************************************************************************************/
	public ClueGame(String peopleFileName, String cardFileName) {
		loadConfigFiles(peopleFileName, cardFileName);
		board = new Board("Clue Board.csv", "Legend.txt");
		board.loadConfigFiles();
		board.calcAdjacencies();
		board.setPlayers(players);
	}
	
	/************************************************************************************************************
 	* Distributes cards among the players as evenly as possible (maximum difference between the number of cards
 	* 	of two players is one.
 	************************************************************************************************************/
	public void deal() {
		Card card;
		String person = "", weapon = "", room = "";
		Random r = new Random();
		ArrayList<Card> c = new ArrayList<Card>(cards);
		int rand = 0;
		while ( true ) {
			rand = r.nextInt(c.size());
			card = c.get(rand);
			if ( card.getCardType() == Card.CardType.PERSON && person == "") {
				person = card.getName();
				c.remove(rand);
			}
			else if ( card.getCardType() == Card.CardType.ROOM && room == "") {
				room = card.getName();
				c.remove(rand);
			}
			else if ( card.getCardType() == Card.CardType.WEAPON && weapon == "") {
				weapon = card.getName();
				c.remove(rand);
			}
			if ( person != "" && room != "" && weapon != "" )
				break;			
		}
		
		solution = new Solution(person, room, weapon);
			
		int i = 0;
		while( c.size() > 0) {
			rand = r.nextInt(c.size());
			players.get(i).getCardList().add(c.get(rand));
			c.remove(rand);
			i = (i + 1) % 9;
		}
	
	}
	
	/************************************************************************************************************
 	* Parameters: String peopleFileName, String cardFileName
 	* 
 	* 	Calls helper functions loadPeopleFileName(String peopleFileName) and loadCardFileName(cardFileName).
	 * @throws BadConfigFormatException 
 	************************************************************************************************************/
	public void loadConfigFiles( String peopleFileName, String cardFileName ) {
		try {
			loadPeopleFile( peopleFileName );
			loadCardFile( cardFileName );
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (BadConfigFormatException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/************************************************************************************************************
 	* Parameter: String peopleFileName
 	* 
 	* 	Opens and extracts the people info from the given people file name.
 	************************************************************************************************************/
	public void loadPeopleFile( String peopleFileName ) throws FileNotFoundException {
		FileReader reader = new FileReader(peopleFileName);
		Scanner input = new Scanner(reader);
		String line, name, color;
		Point p;
		String[] parts;
		int playerCount = 0;
		
		while (input.hasNextLine()) {
			line = input.nextLine();
			parts = line.split(", ");
			name = parts[0];
			color = parts[1];
			p = new Point( Integer.parseInt(parts[2]), Integer.parseInt(parts[3]) );
			if ( playerCount == 0 ) {
				humanPlayer = new HumanPlayer(name, color, p);
				players.add(humanPlayer);
			} else {
				players.add(new ComputerPlayer(name, color, p));
			}
			playerCount++;
		}

	}
	
	/************************************************************************************************************
 	* Parameter: String cardFileName
 	* 
 	* 	Opens and extracts the card info from the given card file name.
 	************************************************************************************************************/
	public void loadCardFile( String cardFileName ) throws FileNotFoundException, BadConfigFormatException {
		FileReader reader = new FileReader(cardFileName);
		Scanner input = new Scanner(reader);
		String line;
		String[] parts;
		Card newCard;
		int numOfPeople, numOfWeps, numOfRooms;
		
		if (input.hasNextLine()) {
			 line = input.nextLine();
			 parts = line.split(", ");
			 numOfPeople = Integer.parseInt( parts[0] );
			 numOfWeps = Integer.parseInt( parts[1] );
			 numOfRooms = Integer.parseInt( parts[2] );
		} else {
			throw new BadConfigFormatException( "Card file is empty" );
		}
		
		while (numOfPeople != 0) {
			if (input.hasNextLine()) {
				line = input.nextLine();
				newCard = new Card( line, Card.CardType.PERSON);
				cards.add( newCard );
			} else {
				throw new BadConfigFormatException( "Specified number of lines in Card file is invalid" );
			}
			numOfPeople--;
		}
		
		while (numOfWeps != 0) {
			if (input.hasNextLine()) {
				line = input.nextLine();
				newCard = new Card( line, Card.CardType.WEAPON);
				cards.add( newCard );
			} else {
				throw new BadConfigFormatException( "Specified number of lines in Card file is invalid" );
			}
			numOfWeps--;
		}
		
		while (numOfRooms != 0) {
			if (input.hasNextLine()) {
				line = input.nextLine();
				newCard = new Card( line, Card.CardType.ROOM);
				cards.add( newCard );
			} else {
				throw new BadConfigFormatException( "Specified number of lines in Card file is invalid" );
			}
			numOfRooms--;
		}
				
		for (Player p : players) {
			p.setCardsOfGameList(cards);
		}
		//System.out.println( cards );
	}
	
	/************************************************************************************************************
 	* 
 	************************************************************************************************************/
	public void selectAnswer() {
		
	}
	
	/************************************************************************************************************
 	* Parameter: String person, String room, String weapon, String accusingPlayer
 	* 
 	* 	
 	************************************************************************************************************/
	public Card handleSuggestion( String person, String weapon, String room, Player accusingPlayer ) {
		ArrayList<Player> players2 = new ArrayList<Player>(players);
		players2.remove(accusingPlayer);
		long seed = System.nanoTime();
		Collections.shuffle(players2, new Random(seed));
		//int randIndex;
		//boolean matchFound = false;
		Card matchedCard = new Card();
		
		for ( Player p : players2 ) {
			//randIndex = (int) (Math.random() * players2.size());
			matchedCard = p.disproveSuggestion(person, weapon, room);
			
			if ( matchedCard != null) {
				for ( Player pl : players2 ) {
					if (pl instanceof ComputerPlayer) {
						((ComputerPlayer) pl).updateSeen(matchedCard);
					}
				}
				/////////////////////////////////////////////////////////////////////////
				//MAY NEED TO UPDATE THE HUMAN PLAYER'S SEEN CARDS LIST HERE AS WELL
				//
				/////////////////////////////////////////////////////////////////////////
				return matchedCard;
			} 
//			else {
//				players2.remove(randIndex);
//			}
		}

		return null;
	}
	
	/************************************************************************************************************
 	* Parameter: Solution solution
 	* 
 	* 	
 	************************************************************************************************************/
	public boolean checkAccusation( Solution solution ) {
		if ( (solution.getPerson() != this.solution.getPerson()) || 
				(solution.getWeapon() != this.solution.getWeapon()) ||
						(solution.getRoom() != this.solution.getRoom()) ) {
			return false;
		} else {
			return true;
		}
	}
	
	public void nextPlayer() {
		roll();
		Player currentPlayer = players.get(turnIndicator);
		int row = (int) currentPlayer.getLocation().getY();
		int col = (int) currentPlayer.getLocation().getX();
		int location = board.calcIndex(row, col);
		int index;
		ArrayList<BoardCell> cells = board.getCells();
				
		if ( currentPlayer instanceof ComputerPlayer ) {
			
			if (response == null) {
				
				if( checkAccusation(currGuess) ) {
					board.endGame(currentPlayer.getName(), "You lose.");
				} else {
					response = new Card();
				}
				
			} else {
			
				board.startTargets(location, roll);
				((ComputerPlayer) currentPlayer).makeMove(board.getTargets());
				board.repaint();
				
				row = (int) currentPlayer.getLocation().getY();
				col = (int) currentPlayer.getLocation().getX();
				location = board.calcIndex(row, col);
				
				
				if ( cells.get(location).isRoom() ) {
					currGuess = ((ComputerPlayer) currentPlayer).createSuggestion();
					for ( Player p : players ) {
						if (p.getName().equals(currGuess.getPerson())) { 
							p.setLocation(currentPlayer.getLocation());
						}
					}
					
					RoomCell tempCell = (RoomCell) cells.get(location);
					((ComputerPlayer) currentPlayer).setLastRoomVisited(tempCell.getInitial());
					
					board.repaint();
					
					response = handleSuggestion(currGuess.getPerson(), currGuess.getWeapon(), 
									currGuess.getRoom(), currentPlayer);
					if (response == null) {
						unDeniedGuess = currGuess; 
					}
					
				}
			}
			
		} else if ( currentPlayer instanceof HumanPlayer) {
			board.setHumanMustFinish(true);
			board.startTargets(location, roll);
			
			for ( BoardCell cell : board.getTargets() ) {
				index = board.calcIndex(cell.getRow(), cell.getColumn());
				cells.get(index).setIsHumanTarget(true);
			}
			
			board.repaint();
		}
		if ( !board.getHumanMustFinish() ) {
			turnIndicator = (turnIndicator + 1) % players.size();
		}
	}
	
	public void roll() {
		Random rand = new Random();
		roll = rand.nextInt(6) + 1;
	}
	
	public Player getHumanPlayer() {
		return humanPlayer;
	}
	
	public ArrayList<Card> getCardList() {
		return cards;
	}
	
	public ArrayList<Player> getPlayerList() {
		return players;
	}
	
	
	//For Test purposes
	public void setSolution( Solution newSolution ) {
		//this.solution = newSolution;
		this.solution.setPerson(newSolution.getPerson());
		this.solution.setWeapon(newSolution.getWeapon());
		this.solution.setRoom(newSolution.getRoom());
	}
	
	public Board getBoard() {
		return board;
	}
	
	public int getTurnIndicator() {
		return turnIndicator;
	}
	
	public int getRoll() {
		return roll;
	}
	
	public boolean getHumanMustFinish() {
		return humanMustFinish;
	}
	
	public Solution getLastGuess() {
		return currGuess;
	}
	
	public Card getResponse() {
		return response;
	}
	
	public void setTurnIndicator(int newIndicator) {
		turnIndicator = newIndicator;
	}
	
	public void setCurrentGuess(Solution guess) {
		currGuess = guess;
	}
	
	public GUI getFrame() {
		return frame;
	}
}
