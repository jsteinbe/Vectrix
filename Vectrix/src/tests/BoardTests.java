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
	
	////// SOLUTION TESTS //////

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
			System.out.println("Node's Connections: " + node.getConnections());
		}
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
		assertTrue(board.getSolution().getLineSets().size() == (oneConnectionNodes/2));
	}
	
	@Test
	public void nodesOfAllConnectionsAreAdjacent() {
		for ( Node node : board.getSolution().getNodes() ) {
			for (Node adj : node.getConnections() ) {
				assertTrue(board.areAdjacent(node, adj));
			}
		}
	}
	
	////// END SOLUTION TESTS //////
	
	////// INTERACTION TESTS //////
	
	@Test
	public void nodeSelectedToAdd() {
		Node toClick = board.getNodes().get(0);
		toClick.setSelected(selectType.NONE);
		board.nodeLeftClicked(toClick);
		assertTrue(toClick.getSelected() == selectType.ADD);
	}
	
	@Test
	public void createConnection() {
		Node toClick = board.getNodes().get(0);
		Node selected = board.getNodes().get( board.getAdjMtx().get(0).get(0) );
		board.setSelectedNode(selected);
		toClick.setSelected(selectType.NONE);
		selected.setSelected(selectType.ADD);
		board.nodeLeftClicked(toClick);
		assertTrue(toClick.getConnections().contains(selected));
		assertTrue(selected.getConnections().contains(toClick));
	}
	
	@Test
	public void deselectAddSelectedNode() {
		Node clicked = board.getNodes().get(0);
		clicked.setSelected(selectType.ADD);
		board.nodeLeftClicked(clicked);
		assertTrue(clicked.getSelected() == selectType.NONE);
	}
	
	@Test
	public void leftClickOnRightClickedNode() {
		Node deleteSelected = board.getNodes().get(0);
		deleteSelected.setSelected(selectType.DELETE);
		board.nodeLeftClicked(deleteSelected);
		assertTrue(deleteSelected.getSelected() == selectType.DELETE);
	}
	
	@Test
	public void nodePartOfConnectionSelectedToDelete() {
		Node toClick = new Node();
		Node otherNode = new Node();
		toClick.setSelected(selectType.NONE);
		board.addConnection(toClick, otherNode);
		board.nodeRightClicked(toClick);
		assertTrue(toClick.getSelected() == selectType.DELETE);
	}
	
	@Test
	public void nodeNotPartOfConnectionSelectedToDelete() {
		//Node toClick = board.getNodes().get(0);
		Node toClick = new Node();
		toClick.setSelected(selectType.NONE);
		board.nodeRightClicked(toClick);
		assertTrue(toClick.getSelected() == selectType.NONE);
	}
	
	@Test
	public void deletionOfConnection() {
		Node toClick = board.getNodes().get(0);
		Node otherNode = board.getNodes().get( board.getAdjMtx().get(0).get(0));
		toClick.setSelected(selectType.NONE);
		otherNode.setSelected(selectType.DELETE);
		board.setSelectedNode(otherNode);
		board.updateLineSetsAdd(board.getLineSets(), toClick, otherNode);
		board.addConnection(toClick, otherNode);
		board.nodeRightClicked(toClick);
		assertFalse(toClick.getConnections().contains(otherNode));
		assertFalse(otherNode.getConnections().contains(toClick));
	}
	
	@Test
	public void deselectDeleteSelectedNode() {
		Node toClick = board.getNodes().get(0);
		toClick.setSelected(selectType.DELETE);
		board.setSelectedNode(toClick);
		board.nodeRightClicked(toClick);
		assertTrue(toClick.getSelected() == selectType.NONE);
	}
	
	@Test
	public void rightClickOnLeftClickedNode() {
		Node toClick = board.getNodes().get(0);
		toClick.setSelected(selectType.ADD);
		board.nodeRightClicked(toClick);
		assertTrue(toClick.getSelected() == selectType.ADD);
	}
	////// END INTERACTION TESTS //////
}
