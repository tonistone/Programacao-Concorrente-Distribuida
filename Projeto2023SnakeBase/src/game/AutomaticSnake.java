package game;

import environment.LocalBoard;
import environment.Board;

public class AutomaticSnake extends Snake {

	public AutomaticSnake(int id, LocalBoard board) {
		super(id, board);

	}

	@Override
	public void run() {
		try {
			doInitialPositioning();
			sleep(10000);
			System.out.println(this.getId() + " acordei");
			//System.err.println("initial size:" + cells.size());
			while (!isInterrupted()) {
				Thread.sleep(Board.PLAYER_PLAY_INTERVAL);
				move(cells.getFirst());
			}
		} catch (InterruptedException e) {
			//System.out.println("Interrompi cobra automatica");
		}
	}
}
