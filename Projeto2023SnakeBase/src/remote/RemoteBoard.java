package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import game.Goal;
import game.LoadGameServer;
import game.Obstacle;
import game.ObstacleMover;
import game.Snake;
import gui.BoardComponent;
import gui.SnakeGui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * @author luismota
 *
 */
public class RemoteBoard extends Board {

	private SnakeGui game;
    private String keyPressed;
    private boolean newDirectionPressed = false;
    
	@Override
	public void handleKeyPress(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_UP:
                keyPressed="UP";
                newDirectionPressed = true;
                break;

            case KeyEvent.VK_DOWN:
                keyPressed="DOWN";
                newDirectionPressed = true;
                break;
            case KeyEvent.VK_LEFT:
                keyPressed="LEFT"; 
                newDirectionPressed = true;        
                break;

            case KeyEvent.VK_RIGHT:   
                keyPressed="RIGHT";
                newDirectionPressed = true;         
                break;
		}	
	}
 
    @Override
    public void handleKeyRelease() {
    }

    public boolean getnewDirectionPressed() {
        return newDirectionPressed;
    }

    public String getKeyPressed() {
        return keyPressed;
    }

    public void clearKeyPressed(){
        keyPressed = null;
    }

    @Override
    public void init() {
        if (game == null) {
            game = new SnakeGui(this, 600, 0);
            game.init();
        }
        
    }

    //TODO: Rever lógica toda, o objetivo é ele ler do ficheiro, não é adicionar.
    public void updateFromLoadGameServer(LoadGameServer load) {
        System.out.println("Vou dar update");
        this.addCellsToClient(load.getCells());
        this.addSnakesToClient(load.getSnakes());
        this.addObstaclesToClient(load.getObstacles());
        this.setGoalPosition(load.getGoalPosition());
        
    }
}
  