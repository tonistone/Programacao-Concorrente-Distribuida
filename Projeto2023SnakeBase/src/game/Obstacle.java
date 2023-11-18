package game;

import environment.Board;
import environment.Cell;
import environment.LocalBoard;

public class Obstacle extends GameElement {
	
	
	private static final int NUM_MOVES=3;
	private static final int OBSTACLE_MOVE_INTERVAL = 400;
	private int remainingMoves=NUM_MOVES;
	private Board board;
	public Obstacle(Board board) {
		super();
		this.board = board;
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}

	public void decreaseRemainingMoves(){
		remainingMoves--;
	}

	public int getOBSTACLE_MOVE_INTERVAL() {
		return OBSTACLE_MOVE_INTERVAL;
	}

	public int getNumMoves() {
		return NUM_MOVES;
	}

}
