package game;

import java.io.Serializable;
import java.util.LinkedList;

import environment.BoardPosition;
import environment.Cell;

public class LoadGameServer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private BoardPosition goalPosition;
    private LinkedList<Snake> snakes;
    private LinkedList<Obstacle> obstacles;
    private Cell[][] cells;
    
    
    public void setSnakes(LinkedList<Snake> snakes) {
        this.snakes = snakes;
    }

    public LinkedList<Snake> getSnakes() {
        return snakes;
    }

    public void setObstacles(LinkedList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public LinkedList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}
    
    public BoardPosition getGoalPosition() {
        return goalPosition;
    }
}
