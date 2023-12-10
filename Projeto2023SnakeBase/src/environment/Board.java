package environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import game.GameElement;
import game.Goal;
import game.LoadGameServer;
import game.Obstacle;
import game.Snake;
import remote.ClientGoal;
import remote.ClientObstacle;
import remote.ClientSnake;

public abstract class Board extends Observable implements Serializable {
	protected Cell[][] cells;
	private BoardPosition goalPosition;
	public static final long PLAYER_PLAY_INTERVAL = 100;
	public static final long REMOTE_REFRESH_INTERVAL = 20;
	public static final int NUM_COLUMNS = 30;
	public static final int NUM_ROWS = 30;
	protected LinkedList<Snake> snakes = new LinkedList<Snake>();
	private LinkedList<Obstacle> obstacles= new LinkedList<Obstacle>();
	protected boolean isFinished = false;
	private Goal goal; 

	public Board() {
		cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}
	}

	public Cell getCell(BoardPosition cellCoord) {
		return cells[cellCoord.x][cellCoord.y];
	}

	public Cell[][] getCells() {
		return cells;
	}

	protected BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() *NUM_ROWS),(int) (Math.random() * NUM_ROWS));
	}

	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}
	
	public void addGameElement(GameElement gameElement) {
		boolean placed=false;
		while(!placed) {
			BoardPosition pos=getRandomPosition();
			if(!getCell(pos).isOcupied() && !getCell(pos).isOcupiedByGoal()) {
				getCell(pos).setGameElement(gameElement);
				gameElement.setMyCell(getCell(pos));
				if(gameElement instanceof Goal) {
					setGoalPosition(pos);
					//System.out.println("Goal placed at:"+pos);
				}
				placed=true;
			}
		}
	}

	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possibleCells=new ArrayList<BoardPosition>();
		BoardPosition pos=cell.getPosition();
		if(pos.x>0)
			possibleCells.add(pos.getCellLeft());
		if(pos.x<NUM_COLUMNS-1)
			possibleCells.add(pos.getCellRight());
		if(pos.y>0)
			possibleCells.add(pos.getCellAbove());
		if(pos.y<NUM_ROWS-1)
			possibleCells.add(pos.getCellBelow());
		return possibleCells;
	}

	protected Goal addGoal() {
		goal=new Goal(this);
		addGameElement(goal);
		return goal;
	}

	protected void addObstacles(int numberObstacles) {
		// clear obstacle list , necessary when resetting obstacles.
		getObstacles().clear();
		while(numberObstacles>0) {
			Obstacle obs=new Obstacle();
			addGameElement( obs);
			getObstacles().add(obs);
			numberObstacles--;
		}
	}
	
	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

    public boolean hasReachedGoal() {
        return isFinished;
    }

    public void markGoalReached() {
        isFinished = true;
    }

	 public LoadGameServer creatGameServer() {
		LinkedList<ClientSnake> snakes = new LinkedList<>();
		List<ClientObstacle> obs = new ArrayList<>();
		ClientGoal clientGoal = new ClientGoal(goalPosition, goal.getValue());

		for (Snake s : getSnakes()) {
				//boolean isHuman = (s instanceof HumanSnake);
				ClientSnake clientSnake = new ClientSnake(s.getPath(), s.getIdentification());
				snakes.add(clientSnake);
		}

		for(Obstacle o : getObstacles()) {
			ClientObstacle clientObstacle = new ClientObstacle(o.getMyCell().getPosition(), o.getRemainingMoves());
			obs.add(clientObstacle);
		}

		return new LoadGameServer(snakes, obs, clientGoal);
	} 

	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	public LinkedList<Obstacle> getObstacles() {
		return obstacles;
	}
	
	public abstract void init(); 
	
	public abstract void handleKeyPress(int keyCode);

	public abstract void handleKeyRelease();
	
	public void addSnake(Snake snake) {
		snakes.add(snake);
	}
}
