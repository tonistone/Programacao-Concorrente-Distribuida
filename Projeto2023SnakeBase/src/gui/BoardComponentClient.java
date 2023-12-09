package gui;

import environment.LocalBoard;
import environment.BoardPosition;
import environment.Cell;
import game.ClientGoal;
import game.ClientObstacle;
import game.ClientSnake;
import game.HumanSnake;
import game.LoadGameServer;
import remote.RemoteBoard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Graphical representarion of the game. This class should not be edited.
 * 
 * @author luismota
 *
 */
public class BoardComponentClient extends JComponent implements KeyListener {

	private RemoteBoard board;
	private Image obstacleImage;

	public BoardComponentClient(RemoteBoard board) {
		this.board = board;
		obstacleImage = new ImageIcon(getClass().getResource("/obstacle.png")).getImage();
		// Necessary for key listener
		setFocusable(true);
		addKeyListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final double CELL_WIDTH = getHeight() / (double) SnakeGui.NUM_ROWS;
		LoadGameServer load = board.getLoad();

		for (int x = 0; x < LocalBoard.NUM_COLUMNS; x++) {
			for (int y = 0; y < LocalBoard.NUM_ROWS; y++) {
				Cell cell = board.getCell(new BoardPosition(x, y));
				Image image = null;
				if (cell.isOcupiedBySnake()) {
					// different color for human player...
					if (cell.getOcuppyingSnake() instanceof HumanSnake)
						g.setColor(Color.ORANGE);
					else
						g.setColor(Color.LIGHT_GRAY);
					g.fillRect((int) Math.round(cell.getPosition().x * CELL_WIDTH),
							(int) Math.round(cell.getPosition().y * CELL_WIDTH),
							(int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH));
				}
			}
			g.setColor(Color.BLACK);
			g.drawLine((int) Math.round(x * CELL_WIDTH), 0, (int) Math.round(x * CELL_WIDTH),
					(int) Math.round(LocalBoard.NUM_ROWS * CELL_WIDTH));
		}
		for (int y = 1; y < LocalBoard.NUM_ROWS; y++) {
			g.drawLine(0, (int) Math.round(y * CELL_WIDTH), (int) Math.round(LocalBoard.NUM_COLUMNS * CELL_WIDTH),
					(int) Math.round(y * CELL_WIDTH));
		}

		if (load != null) {
			for (ClientSnake s : load.getSnakes()) {
				if (s.getLength() > 0) {
					g.setColor(new Color(s.getId() * 1000));

					((Graphics2D) g).setStroke(new BasicStroke(5));
					LinkedList<BoardPosition> listPos = s.getListPos();

					BoardPosition prevPos = null;
					for (BoardPosition coordinate : listPos) {
						if (prevPos != null) {
							g.drawLine(
									(int) Math.round((prevPos.x + 0.5) * CELL_WIDTH),
									(int) Math.round((prevPos.y + 0.5) * CELL_WIDTH),
									(int) Math.round((coordinate.x + 0.5) * CELL_WIDTH),
									(int) Math.round((coordinate.y + 0.5) * CELL_WIDTH));
						}
						prevPos = coordinate;
					}

					((Graphics2D) g).setStroke(new BasicStroke(1));
				}
			}

			for (ClientObstacle co : load.getObs()) {
				g.setColor(Color.BLACK);
				g.drawImage(obstacleImage, (int) Math.round(co.getPos().x * CELL_WIDTH),
						(int) Math.round(co.getPos().y * CELL_WIDTH),
						(int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH), null);

				// write number of remaining moves
				g.setColor(Color.WHITE);
				g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
				g.drawString(co.getRemaining() + "", (int) Math.round((co.getPos().x + 0.15) * CELL_WIDTH),
						(int) Math.round((co.getPos().y + 0.9) * CELL_WIDTH));
			}

			ClientGoal goal = load.getGoal();

			g.setColor(Color.RED);
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
			g.drawString(goal.getValue() + "", (int) Math.round((goal.getPos().x + 0.15) * CELL_WIDTH),
					(int) Math.round((goal.getPos().y + 0.9) * CELL_WIDTH));
		}
	}

	// Only for remote clients: 2. part of the project
	// Methods keyPressed and keyReleased will react to user pressing and
	// releasing keys on the keyboard.
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Got key pressed.");
		if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
				e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
			return; // ignore
		board.handleKeyPress(e.getKeyCode());

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
				e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
			return; // ignore

		System.out.println("Got key released.");
		board.handleKeyRelease();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// ignore
	}

}
