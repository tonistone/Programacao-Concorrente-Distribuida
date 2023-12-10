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
	private Lock sharedLock = new ReentrantLock();
	private Condition snakeMoved = sharedLock.newCondition();

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
		sharedLock.lock();
		try {
			Cell head = cells.getFirst();
			if(this instanceof AutomaticSnake) {
			BoardPosition roadToGoal = getDistanceToGoal(cell);
			head = board.getCell(roadToGoal);
			}

			if (this instanceof HumanSnake) {
				BoardPosition road = this.getCells().getFirst().getPosition().getCellRight();
				head = board.getCell(road);				
			}
			
			head.request(this);
			cells.addFirst(head);

			if (head.isOcupiedByGoal()) {
				int valor = head.captureGoal();
				size += valor - 1;

				if (valor == Goal.MAX_VALUE && head.getPosition().equals(board.getGoalPosition())) {
					//System.out.println("Goal reached! All snakes are interrupted.");
					board.markGoalReached();
					//System.out.println(board.hasReachedGoal());
					interruptAllSnakes(); // Interrompe todas as cobras
				}
			}

			if (cells.size() > size) {
				cells.getLast().release();
				cells.removeLast();
			}
			snakeMoved.signalAll();
			board.setChanged();

		} catch (InterruptedException e) {
			//System.out.println("FUI INTERROMPIDA ");
            //System.out.println(board.hasReachedGoal());
            if (!board.hasReachedGoal()) {
                resetDirectionInInterrupt();
            } else {
				interruptAllSnakes(); //se for o último goal
			}
		} finally {
			sharedLock.unlock();
		}
	}

	// Método para calcular a distancia entre cada posição vizinha e a posição do goal
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
			sharedLock.lock();
			try {
				if ((!board.getCell(vizinho).isOcupiedByDeadObstacle()) && (!board.getCell(vizinho).isOcupiedBySnake())) {
					double distance = vizinho.distanceTo(goalPosition);
					double minDistance = distance;
					nextPosition = vizinho;
					
					if (distance < minDistance) {
						minDistance = distance;
						nextPosition = vizinho;
					}
				}
			} finally {
				sharedLock.unlock();
			}
		}
		return nextPosition;
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

	private synchronized void resetDirectionInInterrupt() throws InterruptedException {
		// Verifica as posições vizinhas após a interrupção
		Cell head = cells.getFirst();
		BoardPosition nextPosition = getDistanceToUnoccupiedGoal(head);

		if (nextPosition != null) {
			Cell newHead = board.getCell(nextPosition);
			sharedLock.lock();
			try {
				// Verificar se a nova posição não está ocupada pela cobra que atingiu o objetivo
				if (!newHead.isOcupiedByGoal()) {
					// Verificar se a nova posição não está ocupada por outra cobra
					if (newHead != cells.getLast() || isCollisionWithOtherSnake(newHead)) {
						if (!this.equals(newHead.getOcuppyingSnake())) {
							head = newHead;
							head.request(this);
							cells.addFirst(head);
							move(head);
						}
					}
				}
			} finally {
				sharedLock.unlock();
			}
			cells.getLast().release();
			cells.removeLast();
			board.setChanged();
		}
	}

	private boolean isCollisionWithOtherSnake(Cell newHead) {
		// Iterar sobre as outras cobras e verificar se a nova posição da cabeça colide com alguma outra cabeça
		for (Snake otherSnake : board.getSnakes()) {
			if (newHead.getPosition().equals(otherSnake.getPath().getFirst())) {
				return true;
			}
		}
		return false;
	}

	private void interruptAllSnakes() {
		for (Snake snake : board.getSnakes()) {
			snake.interrupt();
		}
	}
}
