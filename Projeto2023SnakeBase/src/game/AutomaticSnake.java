package game;

import java.util.LinkedList;
import java.util.List;

import javax.swing.text.Position;

import environment.LocalBoard;
import gui.SnakeGui;
import environment.Cell;
import environment.Board;
import environment.BoardPosition;

public class AutomaticSnake extends Snake {

	public AutomaticSnake(int id, LocalBoard board) {
		super(id, board);

	}

	@Override
	public void run() {
		try {
			doInitialPositioning();
			System.err.println("initial size:" + cells.size());
			while (!isInterrupted()) {

				Thread.sleep(100);
				move(cells.getFirst());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
