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
    public static final int PORTO = 8080;
    private BufferedReader in;
    private ObjectOutputStream out;
    //private RemoteBoard remote;

    public class DealWithClient extends Thread {
        public final Socket socket;

        public DealWithClient(Socket socket) throws IOException {
            this.socket = socket;
            doConnections(socket);
        }

        @Override
        public void run() {
            try {
                //criar um humanPlayer
                //HumanSnake humanSnake;
                //adicionar esse Player ao board
                //remote.addSnake(humanSnake);
                //movimento da cobra human
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
            // Iniciar thread para receber dados
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        receiveMessages();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // Iniciar thread para enviar dados
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        sendMessages();
                    } catch (Exception e) {
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
                        if (receivedMessage.equals("FIM"))
                            break;
                        System.out.println("Received: " + receivedMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMessages() throws IOException, InterruptedException {

            while (!socket.isClosed()) {
                // Lógica para enviar o estado do jogo
                
                
                out.flush();
                // Reset ao objeto para garantir a serialização correta do mesmo
                out.reset();

                // Intervalo antes de enviar
                Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
            }
        }
    }

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
