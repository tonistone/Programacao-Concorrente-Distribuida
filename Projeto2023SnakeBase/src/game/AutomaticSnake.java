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
		doInitialPositioning();
		System.err.println("initial size:" + cells.size());
		while (!isInterrupted()) {
			try {
				move(cells.getFirst());
				Thread.sleep(100); // Aguardar um pouco para dar a ilusão de movimento
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
