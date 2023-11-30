package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
	private boolean interruptedByGoal = false;

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

    public boolean hasReachedGoal() {
        return interruptedByGoal;
    }

	protected void move(Cell cell) throws InterruptedException {
		sharedLock.lock();
		try {
			Cell head = cells.getFirst();
			BoardPosition roadToGoal = getDistanceToGoal(cell);
			head = board.getCell(roadToGoal);
			head.request(this);

			// aguardar o notify do request a dizer que a cobra foi para outra célula
			while (!head.isOcupied()) {
				System.out.println("WAITING FOR CONFIRMATION");
				snakeMoved.await();
			}
			cells.addFirst(head);

			if (head.isOcupiedByGoal()) {
				int valor = head.captureGoal();
				size += valor - 1;

				if (valor == Goal.MAX_VALUE && head.getPosition().equals(board.getGoalPosition())) {
					System.out.println("Goal reached! All snakes are interrupted.");
					interruptAllSnakes(); // Interrompe todas as cobras
					interruptedByGoal = true;
				}
			}
			if (cells.size() > size) {
				cells.getLast().release();
				cells.removeLast();
			}
			snakeMoved.signalAll();
			board.setChanged();

		} catch (InterruptedException e) {
			System.out.println("FUI INTERROMPIDA ");
				resetDirectionInInterrupt();
		} finally {
			sharedLock.unlock();
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
			// System.out.println("for - " + vizinho);
			// Verifique se a posição vizinha não está ocupada pela cobra
			System.out.println(!board.getCell(vizinho).isOcupiedByDeadObstacle());
			sharedLock.lock();
			try {
				if ((!board.getCell(vizinho).isOcupiedByDeadObstacle())
						&& (!board.getCell(vizinho).isOcupiedBySnake())) {
					// System.out.println("ENTREi");
					double distance = vizinho.distanceTo(goalPosition);
					double minDistance = distance;
					nextPosition = vizinho;
					// System.out.println(vizinho);
					if (distance < minDistance) {
						minDistance = distance;
						nextPosition = vizinho;
					}
				}
			} finally {
				sharedLock.unlock();
			}
		}
		// System.out.println("NEXT POS in distance: " + nextPosition);
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

	private void resetDirectionInInterrupt() throws InterruptedException {
		// Verifica as posições vizinhas após a interrupção
		Cell head = cells.getFirst();

		BoardPosition nextPosition = getDistanceToUnoccupiedGoal(head);

		if (nextPosition != null) {
			Cell newHead = board.getCell(nextPosition);
			sharedLock.lock();
			try {
				if (newHead != cells.getLast() || isCollisionWithOtherSnake(newHead)) {

					if (!this.equals(newHead.getOcuppyingSnake())) {
						head = newHead;
						head.request(this);
						cells.addFirst(head);
						move(head);
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
		// Iterar sobre as outras cobras e verificar se a nova posição da cabeça colide
		// com alguma outra cabeça
		for (Snake otherSnake : board.getSnakes()) {
			if (newHead.getPosition().equals(otherSnake.getPath().getFirst())) {
				return true; // Colisão com outra cobra
			}
		}
		return false; // Não há colisão com outras cobras
	}

	private void interruptAllSnakes() {
		for (Snake snake : board.getSnakes()) {
			if (!snake.hasReachedGoal()) {
                snake.interrupt();
            }
		}
	}
}
