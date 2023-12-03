package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedList;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import game.Goal;
import game.Obstacle;
import game.Snake;
import gui.BoardComponent;
import gui.SnakeGui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * @author luismota
 *
 */
public class RemoteBoard extends Board implements Serializable {
	

	@Override
	public void handleKeyPress(int keyCode) {
		//TODO
	}

	@Override
	public void handleKeyRelease() {
		// TODO
	}

	@Override
	public void init() {
		// TODO 
		SnakeGui game = new SnakeGui(this, 600, 0);
        game.init();	
	
	}

	public void updateBoardState(RemoteBoard receiveRemoteBoard) {
		//limpar cobras existentes do remoteBoard
		getSnakes().clear();

		//limpar obstáculos
		//getObstacles().clear();
		
		//adicionar cobras vindas do servidor
		for(Snake receivedSnake : receiveRemoteBoard.getSnakes()) {
			addSnake(receivedSnake);
		}

		// Notifica a mudança para atualizar a interface gráfica
		setChanged();
		notifyObservers();
	}
}
