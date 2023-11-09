package environment;

import java.io.Serializable;

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
	private Board board;

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

	public synchronized void request(Snake snake) throws InterruptedException {
		// TODO coordination and mutual exclusion

		// enquanto está a ocupar essa posição, qualquer outra cobra
		// que queira aceder a essa posição, ao fazer o pedido tem de ficar em wait()
		try {
		while (isOcupiedBySnake() && ocuppyingSnake != snake) {
			System.err.println("Existe cobra na posição");
			wait();
		}
		// a cobra ocupa uma posição
		ocuppyingSnake = snake;
		// quando essa posição fica a null todas as outras threads são notificadas.
		notifyAll();
	} catch (InterruptedException e) {
        // Handle or log the InterruptedException as needed
        Thread.currentThread().interrupt(); // Restore the interrupted status
    }
	}

	public synchronized void release() {
		// TODO
		if (isOcupiedBySnake()) {
			ocuppyingSnake = null;
			notifyAll();
		}
	}

	public boolean isOcupiedBySnake() {
		return ocuppyingSnake != null;
	}

	public synchronized void setGameElement(GameElement element) {
		// TODO coordination and mutual exclusion
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
			//System.out.println("O goal foi removido.");
			Goal goal = getGoal();
			setGameElement(null); // Remove o Goal da célula atual e coloca-a como null
			return goal; //retorna o goal removido
		} 
		return null; // Nenhum Goal estava presente na célula
	}

	//se a célula estiver ocupada por um objetivo então capturamos, se não não fazemos nada (-1)
	public synchronized int captureGoal() {
		return isOcupiedByGoal() ? getGoal().captureGoal() : -1;
	}

	public void removeObstacle() {
		// TODO
	}

	public Goal getGoal() {
		return (Goal) gameElement;
	}

	public synchronized boolean isOcupiedByGoal() {
		return (gameElement != null && gameElement instanceof Goal);
	}

	@Override
	public String toString() {
		return "Cell [position=" + position + "]";
	}

}
