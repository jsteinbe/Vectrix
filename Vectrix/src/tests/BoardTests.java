package tests;

import static org.junit.Assert.*;
import game.*;

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
		
	}

}
