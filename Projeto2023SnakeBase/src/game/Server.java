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
		private HumanSnake humanSnake1;
		private HumanSnake humanSnake2;

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
		
						if (receivedMessage != null) {
							System.out.println("Received: " + receivedMessage);
							try {
								Cell nextPos1 = null;
								Cell nextPos2 = null;
		
								if (receivedMessage.equals("UP")) {
									nextPos1 = board.getCell(humanSnake1.getCells().getFirst().getPosition().getCellAbove());
								} else if (receivedMessage.equals("DOWN")) {
									nextPos1 = board.getCell(humanSnake1.getCells().getFirst().getPosition().getCellBelow());
								} else if (receivedMessage.equals("LEFT")) {
									nextPos1 = board.getCell(humanSnake1.getCells().getFirst().getPosition().getCellLeft());
								} else if (receivedMessage.equals("RIGHT")) {
									nextPos1 = board.getCell(humanSnake1.getCells().getFirst().getPosition().getCellRight());
								} else if (receivedMessage.equals("UP_W")) {
									nextPos2 = board.getCell(humanSnake2.getCells().getFirst().getPosition().getCellAbove());
								} else if (receivedMessage.equals("DOWN_S")) {
									nextPos2 = board.getCell(humanSnake2.getCells().getFirst().getPosition().getCellBelow());
								} else if (receivedMessage.equals("LEFT_A")) {
									nextPos2 = board.getCell(humanSnake2.getCells().getFirst().getPosition().getCellLeft());
								} else if (receivedMessage.equals("RIGHT_D")) {
									nextPos2 = board.getCell(humanSnake2.getCells().getFirst().getPosition().getCellRight());
								}
		
								if (nextPos1 != null) {
									humanSnake1.move(nextPos1);
								}
								if (nextPos2 != null) {
									humanSnake2.move(nextPos2);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
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
			humanSnake1 = new HumanSnake(1, board);
			humanSnake2 = new HumanSnake(2, board);
			humanSnake1.doInitialPositioning();
			humanSnake2.doInitialPositioning();
			humanSnake1.start();
			humanSnake2.start();
			board.addSnake(humanSnake1);
			board.addSnake(humanSnake2);
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