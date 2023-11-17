package game;

import java.util.concurrent.CountDownLatch;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;


public class Goal extends GameElement {
	private int value = 1;
	private Board board;
	public static final int MAX_VALUE = 10;
	private CountDownLatch cdl = new CountDownLatch(9);

	public Goal(Board board2) {
		this.board = board2;
	}

	public int getValue() {
		return value;
	}

	public synchronized void incrementValue() throws InterruptedException {
		try {
			if (value < MAX_VALUE) {
				value++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cdl.countDown();
		}

	}

	public CountDownLatch getcountDown() {
		return cdl;
	}

	public int captureGoal() {
		try {
			BoardPosition goalPosition = board.getGoalPosition();
			Cell myCell = board.getCell(goalPosition);
			Goal currentGoal = myCell.removeGoal();

			incrementValue();

			if(value < MAX_VALUE) {
			board.addGameElement(currentGoal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}

