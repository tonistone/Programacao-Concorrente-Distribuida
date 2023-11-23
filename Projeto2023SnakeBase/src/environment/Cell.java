package environment;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.SysexMessage;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;
import game.AutomaticSnake;

/**
 * Main class for game representation.
 * 
 * @author luismota
 *
 */
public class Cell {
	private BoardPosition position;
	private Snake ocuppyingSnake = null;
	private GameElement gameElement = null;
	private Lock sharedLock = new ReentrantLock();
	private Condition snakeMoved = sharedLock.newCondition();
	

	public synchronized GameElement getGameElement() {
		return gameElement;
	}

	public Cell(BoardPosition position) {
		super();
		this.position = position;
	}

	public BoardPosition getPosition() {
		return position;
	}

	public void request(Snake snake) throws InterruptedException {
		sharedLock.lock();
		try {
			while (isOcupied() && (ocuppyingSnake != snake || ocuppyingSnake == null)) {
				System.out.println("WAITING");
				snakeMoved.await();
			}
			ocuppyingSnake = snake;
			snakeMoved.signalAll();
		} finally {
			sharedLock.unlock();
		}
	}

	public void release() {
		sharedLock.lock();
		try {
			ocuppyingSnake = null;
			snakeMoved.signalAll();
		} finally {
			sharedLock.unlock();
		}
	}

	public boolean isOcupiedBySnake() {
		return ocuppyingSnake != null;
	}

	public synchronized void setGameElement(GameElement element) {
		if (!isOcupiedByObstacle())
			gameElement = element;
	}

	public boolean isOcupied() {
		return isOcupiedBySnake() || (gameElement != null && gameElement instanceof Obstacle);
	}

	public Snake getOcuppyingSnake() {
		return ocuppyingSnake;
	}

	public synchronized Goal removeGoal() {
		if (isOcupiedByGoal()) {
			Goal goal = getGoal();
			setGameElement(null);
			return goal;
		}
		return null;
	}

	// se a célula estiver ocupada por um objetivo então capturamos, se não não
	// fazemos nada (-1)
	public synchronized int captureGoal() {
		return isOcupiedByGoal() ? getGoal().captureGoal() : -1;
	}

	public void removeObstacle() {
		sharedLock.lock();
		try {
			gameElement = null;
			snakeMoved.signalAll();
		} finally {
			sharedLock.unlock();
		}
	}


	public Goal getGoal() {
		return (Goal) gameElement;
	}

	public synchronized boolean isOcupiedByGoal() {
		return (gameElement != null && gameElement instanceof Goal);
	}

	public synchronized boolean isOcupiedByObstacle() {
		return (gameElement != null && gameElement instanceof Obstacle);
	}

	public synchronized boolean isOcupiedByDeadObstacle(){
		Obstacle o = convertObstacle();
		return (isOcupiedByObstacle() && o.getRemainingMoves()==0);
	}

	@Override
	public String toString() {
		return "Cell [position=" + position + "]";
	}

	public Obstacle convertObstacle(){
		if(!(gameElement instanceof Obstacle)){
			return null;
		}
		return (Obstacle)gameElement;
	}

}
