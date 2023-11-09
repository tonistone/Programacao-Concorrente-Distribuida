package game;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;


public class Goal extends GameElement {
	private int value = 1;
	private Board board;
	public static final int MAX_VALUE = 10;

	public Goal(Board board2) {
		this.board = board2;
	}

	public int getValue() {
		return value;
	}

	public synchronized void incrementValue() throws InterruptedException {
		if (value < MAX_VALUE) {
			value++;
		} 
	}

	public int captureGoal() {
		try {
			BoardPosition goalPosition = board.getGoalPosition();
			// remover o goal
			Cell myCell = board.getCell(goalPosition);
			Goal currentGoal = myCell.removeGoal();
			// incrementar o valor do goal
			incrementValue();
			// adicionar goal a outra posição
			board.addGameElement(currentGoal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
