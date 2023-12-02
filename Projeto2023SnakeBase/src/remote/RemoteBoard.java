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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * @author luismota
 *
 */
public class RemoteBoard extends Board implements Serializable{
	
	@Override
	public void handleKeyPress(int keyCode) {
		//TODO
		switch (keyCode) {
			case KeyEvent.VK_UP:
            // Lógica quando a tecla UP é pressionada
            System.out.println("UP Pressionado");
            
            break;
        case KeyEvent.VK_DOWN:
            // Lógica quando a tecla DOWN é pressionada
            System.out.println("DOWN Pressionado");
            
            break;
        case KeyEvent.VK_LEFT:
            
            System.out.println("LEFT Pressionado");
            
            break;
        case KeyEvent.VK_RIGHT:
            // Lógica quando a tecla RIGHT é pressionada
            System.out.println("RIGHT Pressionado");
            
            break;
		}	
	}

	@Override
	public void handleKeyRelease() {
		// TODO
	}

	@Override
	public void init() {
		// TODO 		
	}

	 // Adicione este método para atualizar o estado do tabuleiro
	 public void setLocalBoardData(LocalBoard data) {
        // Atualize a instância de RemoteBoard com os dados recebidos do servidor
        // Exemplo: this.boardData = boardData;
        // Certifique-se de que o método setChanged() seja chamado para atualizar a interface gráfica
        setChanged();
    }

    // Adicione este método para obter o estado do tabuleiro
    public LocalBoard getLocalBoardData() {
        // Retorne o estado atual do tabuleiro para ser enviado ao servidor
        // Exemplo: return this.boardData;
        return null;
    }
}
