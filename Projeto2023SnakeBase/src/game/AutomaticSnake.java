package game;

import environment.LocalBoard;

import java.util.List;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;

public class AutomaticSnake extends Snake {

	public AutomaticSnake(int id, LocalBoard board) {
		super(id, board);

	}

	@Override
	public void run() {
		try {
			doInitialPositioning();
			//System.err.println("initial size:" + cells.size());
			while (!isInterrupted()) {
				Thread.sleep(Board.PLAYER_PLAY_INTERVAL);
				move(cells.getFirst());
			}
		} catch (InterruptedException e) {
			//System.out.println("Interrompi cobra automatica");
		}
	}

	protected void move(Cell cell) throws InterruptedException{
		sharedLock.lock();
		try {
			Cell head = cells.getFirst();
			BoardPosition roadToGoal = getDistanceToGoal(cell);
			head = board.getCell(roadToGoal);		
			head.request(this);

			cells.addFirst(head);
			super.goalReachedCheck(head);

			if (cells.size() > size) {
				cells.getLast().release();
				cells.removeLast();
			}
			snakeMoved.signalAll();
			board.setChanged();

		} catch (InterruptedException e) {
			System.out.println("NEXT IS WHAT YOU WANT TO CHECK ");
            System.out.println(!board.hasReachedGoal());
            if (!board.hasReachedGoal()) {
				System.out.println("entrei no if em 63");
                resetDirectionInInterrupt();
            }
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
			// System.out.println(!board.getCell(vizinho).isOcupiedByDeadObstacle());
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


	private void resetDirectionInInterrupt() throws InterruptedException {
		// Verifica as posições vizinhas após a interrupção
		Cell head = cells.getFirst();

		BoardPosition nextPosition = getDistanceToUnoccupiedGoal(head);

		if (nextPosition != null) {
			Cell newHead = board.getCell(nextPosition);
			sharedLock.lock();
			try {
				// Verificar se a nova posição não está ocupada pela cobra que atingiu o
				// objetivo
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
		// Iterar sobre as outras cobras e verificar se a nova posição da cabeça colide
		// com alguma outra cabeça
		for (Snake otherSnake : board.getSnakes()) {
			if (newHead.getPosition().equals(otherSnake.getPath().getFirst())) {
				return true; // Colisão com outra cobra
			}
		}
		return false; // Não há colisão com outras cobras
	}
}
