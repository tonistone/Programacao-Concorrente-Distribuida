package game;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import environment.Cell;
import environment.LocalBoard;

public class ObstacleMover extends Thread implements Serializable {
	private Obstacle obstacle;
	private LocalBoard board;
	private Lock cellLock = new ReentrantLock();

	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		super();
		this.obstacle = obstacle;
		this.board = board;
	}

	@Override
	public void run() {
		try {
			while (obstacle.getRemainingMoves() > 0) {
				Thread.sleep(obstacle.getOBSTACLE_MOVE_INTERVAL());
				Cell myCell = obstacle.getMyCell();
				myCell.removeObstacle();
				
				cellLock.lock();
				try {
					if (!myCell.isOcupiedByObstacle() || !myCell.isOcupiedBySnake()) {
						board.addGameElement(obstacle);
					}
					obstacle.decreaseRemainingMoves();
					board.setChanged();
				} finally {
					cellLock.unlock();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
