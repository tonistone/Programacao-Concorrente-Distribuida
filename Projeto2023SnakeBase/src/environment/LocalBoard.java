package environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import game.Obstacle;
import game.ObstacleMover;
import game.Snake;
import game.AutomaticSnake;

/**
 * Class representing the state of a game running locally
 * 
 * @author luismota
 *
 */
public class LocalBoard extends Board {

	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 25;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3;
	private transient ExecutorService pool = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);

	public LocalBoard() {

		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}
		addObstacles(NUM_OBSTACLES);
		addGoal();
		// System.err.println("All elements placed");
	}

	public void init() {
		for (Snake s : snakes) {
			s.start();
		}
		// thread pool
		for (Obstacle o : getObstacles()) {
			ObstacleMover om = new ObstacleMover(o, this);
			pool.submit(om);
		}
		pool.shutdown();
		setChanged();
	}

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}
}
