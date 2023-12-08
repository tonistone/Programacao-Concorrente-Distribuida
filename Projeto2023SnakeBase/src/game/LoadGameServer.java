package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import environment.BoardPosition;
import environment.Cell;

public class LoadGameServer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private LinkedList<ClientSnake> snakes;
    private List<ObstacleClient> obs;
    private ClientGoal goal;
  
    public LoadGameServer(LinkedList<ClientSnake> snakes, List<ObstacleClient> obs, ClientGoal goal) {
        this.snakes = snakes;
        this.obs = obs;
        this.goal = goal;
    }

    public LinkedList<ClientSnake> getSnakes() {
        return snakes;
    }


    public List<ObstacleClient> getObs() {
        return obs;
    }


    public ClientGoal getGoal() {
        return goal;
    }
}
