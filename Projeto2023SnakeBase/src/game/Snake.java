package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import environment.LocalBoard;
import gui.SnakeGui;
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

	protected synchronized void move(Cell cell) throws InterruptedException {

		//cabeça da cobra é a primeira posição da lista
		Cell head = cells.getFirst();
		//vai pegar no método e calcular para cada posição a posição mais perto do objetivo
		BoardPosition roadToGoal = getDistanceToGoal(cell);

		//Se a celula vizinha contiver a célula necessária para fazer a menor distancia do caminho para o objetivo
		//E se essa mesma célula não estiver ocupada por uma cobra
		if (board.getNeighboringPositions(cell).contains(roadToGoal) && !board.getCell(roadToGoal).isOcupiedBySnake()) {
			//A cabeça da cobra vai aumentar 1 posição
			head = board.getCell(roadToGoal);
			cells.addFirst(head);
			//Se a celula estiver ocupada por uma cobra vai dar wait() até que deixe de estar ocupada
			cells.getFirst().request(this);
	
			// Verifique se a cabeça da cobra atingiu o objetivo e captura o objetivo
			if (head.isOcupiedByGoal()) {
				//int sizeInicial = getSize();
				int valor = head.captureGoal();
				size += valor - 1;
				//int sizeFinal = getSize();
				//System.out.println("Valor de crescimento= " + (sizeFinal - sizeInicial));
			}
			if (cells.size() > size) {
				cells.getLast().release();
				cells.removeLast();
			}
		}
		board.setChanged();
		notifyAll();
	}

	public void resetSnake(Cell cell) throws InterruptedException {
		Cell head = cells.getFirst();
		head = cell;
		cells.addFirst(head);
		cell.request(this);
		if (cells.size() > size) {
				cells.getLast().release();
				cells.removeLast();
			}
		board.setChanged();
		//notifyAll();
	}


	// Método para calcular a distancia entre cada posição vizinha
	// e a posição do goal
	private synchronized BoardPosition getDistanceToGoal(Cell cell) {
		List<BoardPosition> neighboringPositions = board.getNeighboringPositions(cell);
		// Inicializo a distancia minima com um valor grande mesmo para que
		// na primeira comparação o valor da primeira posição vizinha seja logo a
		// minDistance, a partir deste momento vou comparar as outras posições vizinhas
		// com a posição vizinha que já lá está
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

}
