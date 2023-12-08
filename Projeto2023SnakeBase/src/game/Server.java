package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;
import gui.BoardComponent;
import remote.RemoteBoard;
import game.HumanSnake;

public class Server {
	private final Board board;
	public static final int PORTO = 8080;

	public Server(Board board) {
		this.board = board;
	}

	private class DealWithClient extends Thread {
		private final Socket socket;
		private HumanSnake humanSnake;

		public DealWithClient(Socket socket) throws IOException {
			this.socket = socket;

			// Só para lhe dar um ID.

			doConnections(socket);
		}

		private BufferedReader in;
		private ObjectOutputStream out;

		@Override
		public void run() {
			try {
				synchronized (this) {
                humanSnake = new HumanSnake(1, board);
				humanSnake.start();
				board.addSnake(humanSnake);
				board.setChanged();
				}  
				serve();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void doConnections(Socket socket) throws IOException {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		private void serve() throws IOException {

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						sendMessages();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						receiveMessages();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

		private void receiveMessages() throws IOException {
			try {
				while (!socket.isClosed()) {
					if (in.ready()) {
						String receivedMessage = in.readLine();
						System.out.println(receivedMessage);
						if (receivedMessage.equals(null))
							break;
						System.out.println("Received: " + receivedMessage);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendMessages() throws IOException {
			String message = "Olá Cliente!";
			LoadGameServer load = new LoadGameServer();
			load.setSnakes(board.getSnakes());
			load.setObstacles(board.getObstacles());
			load.setCells(board.getCells());
			load.setGoalPosition(board.getGoalPosition());
			try {
				while (!socket.isClosed()) {
					System.out.println("Aqui estou eu");
					out.writeObject(load);
					out.flush();
					out.reset();
					Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
					//Thread.sleep(1000);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void startServing() throws IOException {
		ServerSocket ss = new ServerSocket(PORTO);
		try {
			while (true) {
				Socket socket = ss.accept();
				new DealWithClient(socket).start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			ss.close();
		}
	}
}