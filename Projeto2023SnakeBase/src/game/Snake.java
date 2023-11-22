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
	private Board board;
	private Lock snakeLock = new ReentrantLock();
	private Lock resetLock = new ReentrantLock();
	private Condition cellAvailable = snakeLock.newCondition();

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

	protected void move(Cell cell) {
		snakeLock.lock();
		try {
			Cell head = cells.getFirst();
			BoardPosition roadToGoal = getDistanceToGoal(cell);
			head = board.getCell(roadToGoal);
			head.request(this);
			cells.addFirst(head);

			if (head.isOcupiedByGoal()) {
				int valor = head.captureGoal();
				size += valor - 1;
			}
			if (cells.size() > size) {
				cells.getLast().release();
				cells.removeLast();
			}

			board.setChanged();
			cellAvailable.signalAll();

		} catch (InterruptedException e) {
			System.out.println("FUI INTERROMPIDA " + threadId());
			try {
				resetDirection();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			snakeLock.unlock();
		}
	}

	// Método para calcular a distancia entre cada posição vizinha
	// e a posição do goal
	private synchronized BoardPosition getDistanceToGoal(Cell cell) {
		List<BoardPosition> neighboringPositions = board.getNeighboringPositions(cell);
		double minDistance = Double.MAX_VALUE;
		BoardPosition nextPosition = null;
		BoardPosition goalPosition = board.getGoalPosition();

		// Calcule a distância entre cada posição vizinha e o objetivo
		for (BoardPosition vizinho : neighboringPositions) {
			// Verifique se a posição vizinha não está ocupada pela cobra
			if (!getPath().contains(vizinho)) {
				double distance = vizinho.distanceTo(goalPosition);
				if (distance < minDistance) {
					minDistance = distance;
					nextPosition = vizinho;
				}
			}
		}
		return nextPosition;
	}

	private BoardPosition getDistanceToUnoccupiedGoal(Cell cell) {
		List<BoardPosition> neighboringPositions = board.getNeighboringPositions(cell);
		BoardPosition nextPosition = null;
		BoardPosition goalPosition = board.getGoalPosition();

		// Calcule a distância entre cada posição vizinha e o objetivo
		for (BoardPosition vizinho : neighboringPositions) {
			//System.out.println("for - " + vizinho);
			// Verifique se a posição vizinha não está ocupada pela cobra
			//System.out.println(!board.getCell(vizinho).isOcupiedByDeadObstacle());
			resetLock.lock();
			try {
				if ((!board.getCell(vizinho).isOcupiedByDeadObstacle())
						&& (!board.getCell(vizinho).isOcupiedBySnake())) {
					//System.out.println("ENTREi");
					double distance = vizinho.distanceTo(goalPosition);
					double minDistance = distance;
					nextPosition = vizinho;
					//System.out.println(vizinho);
					if (distance < minDistance) {
						minDistance = distance;
						nextPosition = vizinho;
					}
				}
			} finally {
				resetLock.unlock();
			}
		}
		System.out.println("NEXT POS in distance: " + nextPosition);
		return nextPosition;
	}

	public synchronized LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		for (Cell cell : cells) {
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
		System.err.println("Snake " + getIdentification() + " starting at:" + getCells().getLast());
	}

	public Board getBoard() {
		return board;
	}

	public void resetDirection() throws InterruptedException {
		// Verifica as posições vizinhas após a interrupção
		Cell head = cells.getFirst();
		System.out.println("HEAD : " + head);
		BoardPosition nextPosition = getDistanceToUnoccupiedGoal(head);
		//System.out.println("NEXT POS resetdirection : " + nextPosition);
		if(nextPosition==null){
			System.out.println("TOU A NULL CRL");
		}
		if (nextPosition != null) {
			resetLock.lock();
			try {
				Cell newHead = board.getCell(nextPosition);
				head = newHead;
				//System.out.println("HEAD : " + head);
				//System.out.println("eu estou no reset e esta é a posição onde quero ir " + head);
				head.request(this);
				cells.addFirst(head);
				move(head);
			} finally {
				resetLock.unlock();
			}
		}
		cells.getLast().release();
		cells.removeLast();
		board.setChanged();
	}

}
