package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;


/**
 * Base class for representing Snakes.
 * Will be extended by HumanSnake and AutomaticSnake.
 * Common methods will be defined here.
 * 
 * @author luismota
 *
 */
public abstract class Snake extends Thread implements Serializable {
	protected LinkedList<Cell> cells = new LinkedList<Cell>();
	protected int size = 5;
	private int id;
	public Board board;
	private int keyCode;
	public Lock sharedLock = new ReentrantLock();
	public Condition snakeMoved = sharedLock.newCondition();

	public Snake(int id, Board board) {
		this.id = id;
		this.board = board;
	}

	public int getSize() {
		return size;
	}

	public int getIdentification() {
		return id;
	}

	public int getLength() {
		return cells.size();
	}

	public LinkedList<Cell> getCells() {
		return cells;
	}

	protected void move(Cell cell) throws InterruptedException {
	}

	public void goalReachedCheck(Cell head){
		if (head.isOcupiedByGoal()) {
			int valor = head.captureGoal();
			size += valor - 1;
			if (valor == Goal.MAX_VALUE && head.getPosition().equals(board.getGoalPosition())) {
				System.out.println("Goal reached! All snakes are interrupted.");
				board.markGoalReached();
				System.out.println(board.hasReachedGoal());
				interruptAllSnakes(); // Interrompe todas as cobras
			}
		}
	}
	

	public synchronized LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		
			for (Cell cell : new LinkedList<>(cells)) {
				coordinates.add(cell.getPosition());
			}
		return coordinates;
	}


	protected void doInitialPositioning() {
		// Random position on the first column.
		// At startup, snake occupies a single cell
		int posX = 0;
		int posY = (int) (Math.random() * Board.NUM_ROWS);
		BoardPosition at = new BoardPosition(posX, posY);

		try {
			board.getCell(at).request(this);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cells.add(board.getCell(at));
		//System.err.println("Snake " + getIdentification() + " starting at:" + getCells().getLast());
	}

	public Board getBoard() {
		return board;
	}

	private void interruptAllSnakes() {
		for (Snake snake : board.getSnakes()) {
			snake.interrupt();
		}
	}
}
