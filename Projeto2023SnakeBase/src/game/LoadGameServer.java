package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LoadGameServer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private LinkedList<ClientSnake> snakes;
    private List<ClientObstacle> obs;
    private ClientGoal goal;
  
    public LoadGameServer(LinkedList<ClientSnake> snakes, List<ClientObstacle> obs, ClientGoal goal) {
        this.snakes = snakes;
        this.obs = obs;
        this.goal = goal;
    }

    public LinkedList<ClientSnake> getSnakes() {
        return snakes;
    }


    public List<ClientObstacle> getObs() {
        return obs;
    }


    public ClientGoal getGoal() {
        return goal;
    }
}
