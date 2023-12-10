package remote;

import environment.Board;
import game.LoadGameServer;

import java.awt.event.KeyEvent;


/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * @author luismota
 *
 */
public class RemoteBoard extends Board {

	private SnakeGuiClient game;
    private String keyPressed;
    private boolean newDirectionPressed = false;
    private LoadGameServer load;
    
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
        newDirectionPressed = false;
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
            game = new SnakeGuiClient(this, 600, 0);
            game.init();
        }    
    }

    public LoadGameServer getLoad() {
        return load;
    }

    public void setLoad(LoadGameServer load) {
        this.load = load;
    }
}
  