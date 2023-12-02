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

import environment.Board;
import gui.BoardComponent;
import remote.RemoteBoard;

public class Server {
	// TODO
	private BufferedReader in;
	private ObjectOutputStream out;
	public static final int PORTO = 8080;
	
	public static void main(String[] args) {
		try {
			new Server().startServing();
		} catch (IOException e) {
			// ...
		}
	}

	public void startServing() throws IOException {
		ServerSocket ss = new ServerSocket(PORTO);
		try {
			while(true){
				Socket socket = ss.accept();
				System.out.println("Client connected: " + socket.getInetAddress());

				try {
					Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new DealWithClient(socket, remote).start();
			}			
		} finally {
			ss.close();
		}
	}

	private void sendGameState(Socket socket, RemoteBoard remoteBoard) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            // Envia o estado do jogo para o cliente
            out.writeObject(remoteBoard.getGameState());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private class DealWithClient extends Thread {
        private BufferedReader in;
        private ObjectOutputStream out;
        private RemoteBoard remoteBoard;

        public DealWithClient(Socket socket, RemoteBoard remoteBoard) throws IOException {
            this.remoteBoard = remoteBoard;
            doConnections(socket);
        }

        void doConnections(Socket socket) throws IOException {
            // Recebe texto
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Envia objetos
            out = new ObjectOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void serve() throws IOException {
            while (true) {
                String str = in.readLine();
                if (str.equals("FIM"))
                    break;
                System.out.println("Eco:" + str);
                MyObject myObject = new MyObject(str);
                out.writeObject(myObject);
                out.flush();
                // Object Streams usam cache: necessário fazer reset antes do reenvio se dados mudam;
                out.reset();
            }
        }
    }
}

