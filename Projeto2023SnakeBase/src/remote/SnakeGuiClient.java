package remote;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
/**
 *  Class to create and configure GUI.
 *  Only the listener to the button should be edited, see TODO below.
 * 
 * @author luismota
 *
 */
public class SnakeGuiClient implements Observer {
	public static final int BOARD_WIDTH = 700;
	public static final int BOARD_HEIGHT = 700;
	public static final int NUM_COLUMNS = 40;
	public static final int NUM_ROWS = 30;
	private JFrame frame;
	private BoardComponentClient boardGui;
	private RemoteBoard board;

	public SnakeGuiClient(RemoteBoard board, int x,int y) {
		super();
		this.board=board;
		frame= new JFrame("The Snake Game: Remote");
		frame.setLocation(x, y);
		buildGui();
	}

	private void buildGui() {
		frame.setLayout(new BorderLayout());
		
		boardGui = new BoardComponentClient(board);
		boardGui.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
		frame.add(boardGui,BorderLayout.CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		frame.setVisible(true);
		board.addObserver(this);
		board.init();
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}
}

