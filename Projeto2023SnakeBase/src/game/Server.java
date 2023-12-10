package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import environment.Board;
import environment.Cell;

public class Server {
	private final Board board;
	public static final int PORTO = 8080;

	public Server(Board board) {
		this.board = board;
	}

	private class DealWithClient extends Thread {
		private final Socket socket;
		private HumanSnake humanSnake;

		private DealWithClient(Socket socket) throws IOException {
			this.socket = socket;

			doConnections(socket);
		}

		private BufferedReader in;
		private ObjectOutputStream out;

		@Override
		public void run() {
			try {
				creatHumanSnake();
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
						try {
							Cell nextPos=null;
							if (receivedMessage.equals(null)) {
								break;
							} else if (receivedMessage.equals("UP")) {

								nextPos = board.getCell(humanSnake.getCells().getFirst().getPosition().getCellAbove());

							} else if (receivedMessage.equals("DOWN")) {

								nextPos = board.getCell(humanSnake.getCells().getFirst().getPosition().getCellBelow());

							} else if (receivedMessage.equals("LEFT")) {

								nextPos = board.getCell(humanSnake.getCells().getFirst().getPosition().getCellLeft());

							} else if (receivedMessage.equals("RIGHT")) {

								nextPos = board.getCell(humanSnake.getCells().getFirst().getPosition().getCellRight());
							}
/* 							if(nextPos == null){
								System.out.println("its null");
								return;
							}*/
							humanSnake.move(nextPos);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("Received: " + receivedMessage);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendMessages() throws IOException {
			try {
				while (!socket.isClosed()) {
					LoadGameServer load = board.creatGameServer();
					out.writeObject(load);
					out.flush();
					
					Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
					out.reset();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private synchronized void creatHumanSnake() {
			humanSnake = new HumanSnake(1, board);
			humanSnake.doInitialPositioning();
			humanSnake.start();
			board.addSnake(humanSnake);
			board.setChanged();
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