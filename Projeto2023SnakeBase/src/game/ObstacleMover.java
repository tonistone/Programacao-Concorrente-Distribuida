package game;

import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

public class ObstacleMover extends Thread {
	private Obstacle obstacle;
	private LocalBoard board;
	
	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		super();
		this.obstacle = obstacle;
		this.board = board;
	}

	@Override
	public void run() {
		while (obstacle.getRemainingMoves()>0) {
			move();
			obstacle.decreaseRemainingMoves();
		}
	}

	public void move(){
		try {
			//getOBSTACLE_MOVE_INTERVAL()
			sleep(3000);
			Cell pos =randomPos(obstacle);
			pos.request(null, obstacle);
			obstacle.setnextCell(pos);
			pos.setGameElement(obstacle);
			obstacle.getOriginalCell().release();
			obstacle.setOriginalCell(pos);
			board.setChanged();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Cell randomPos(Obstacle o){
		Cell pos= board.getRandomCell();
		
		board.setChanged();
		return pos;
	}
}
