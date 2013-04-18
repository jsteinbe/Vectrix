package tests;

import static org.junit.Assert.*;
import game.*;
import game.Node.selectType;

import org.junit.Before;
import org.junit.Test;

public class BoardTests {
	Board board;
	
	@Before
	public void setup() {
		board = new Board();
	}
	
	//Testing the solution.

	@Test
	public void testConnectionsPerNode() {
		for ( Node node : board.getSolution().getNodes() ) {
			assertFalse(node.getConnections().size() > 2 );
		}
	}
	
	@Test
	public void testSinglyConnectedNodes() {
		for ( Node node : board.getSolution().getNodes() ) {
			if (node.getConnections().size() == 1 ) {
				assertTrue(node.getType() != DrawType.DOT);
			}
		}
	}
	
	@Test
	public void allNodesHaveConnections() {
		for ( Node node : board.getSolution().getNodes() ) {
			assertTrue(node.getConnections().size() != 0);
		}
	}
	
	@Test
	public void allCirclesHaveArrows() {
		int arrowCount = 0;
		int circleCount = 0;
		
		for ( Node node : board.getSolution().getNodes() ) {
			if ( node.isArrow() ) arrowCount++;
			if ( node.isCircle() ) circleCount++;
		}
		
		assertTrue(arrowCount == circleCount);
	}
	
	@Test
	public void checkArrowDirection() {
		for ( Node node : board.getSolution().getNodes() ) {
			if ( node.isArrow() ) {
				Node connected = node.getConnections().get(0);
				assertTrue( node.getType() == 
						board.checkNodeDirection(node, connected));
			}
		}
	}
	
	@Test
	public void correctNumberOfPaths() {
		int oneConnectionNodes = 0;
		for ( Node node : board.getSolution().getNodes() ) {
			if ( node.isArrow() || node.isCircle() ) {
				oneConnectionNodes++;
			}
		}
		assertTrue(board.getPaths().size() == (oneConnectionNodes/2));
	}
	
	@Test
	public void noOneConnectionPaths() {
		for ( Connection connection : board.getSolution().getConnections()) {
			Node node1 = connection.getAttached().get(0);
			Node node2 = connection.getAttached().get(1);
			assertFalse(( node1.isCircle() && node2.isArrow() ) || ( node2.isCircle() && node1.isArrow() ));
		}
	}
	
	@Test
	public void firstNodeAddSelected() {
		Node toClick = board.getNodes().get(0);
		toClick.setSelected(selectType.NONE);
		board.nodeLeftClicked(toClick);
		assertTrue(toClick.getSelected() == selectType.ADD);
	}
	
	@Test
	public void createConnection() {
		Node toClick = board.getNodes().get(0);
		Node selected = board.getNodes().get( board.getAdjMtx().get(0).get(0) );
		int numOfConnections = board.getConnections().size();
		board.nodeLeftClicked(toClick);
		assertTrue(board.getConnections().size() == (numOfConnections + 1));
		boolean foundConnection = false;
		for (Connection connection : board.getConnections() ) {
			if (connection.getAttached().contains(toClick) && connection.getAttached().contains(selected) ) {
				foundConnection = true;
				break;
			}
		}
		assertTrue(foundConnection);
	}
}
