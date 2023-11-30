package game;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;


public class Goal extends GameElement {
	private int value = 1;
	private Board board;
	public static final int MAX_VALUE = 5;

	public Goal(Board board2) {
		super();
		this.board = board2;
	}

	public int getValue() {
		return value;
	}

	public void incrementValue() throws InterruptedException {
		acquireLock();
		try {
			if (value < MAX_VALUE) {
				value++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseLock();
		}

	}

	public int captureGoal() {
		try {
			BoardPosition goalPosition = board.getGoalPosition();
			Cell myCell = board.getCell(goalPosition);
			Goal currentGoal = myCell.removeGoal();

			incrementValue();

			if(value < MAX_VALUE && !myCell.isOcupiedByObstacle()) {
			board.addGameElement(currentGoal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}

