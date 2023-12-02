package remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import environment.LocalBoard;

/** Remore client, only for part II
 * 
 * @author luismota
 *
 */

public class Client {

    private ObjectInputStream in;
	private PrintWriter out;
	private Socket socket;
	private RemoteBoard remoteBoard = new RemoteBoard();

	public static void main(String[] args) {
		new Client().runClient();
	}

	public void runClient() {
        try {
			//conectar ao servidor
            connectToServer();
			//receber mensagens do servidor
            receiveMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void connectToServer() throws IOException {
        InetAddress endereco = InetAddress.getByName(null);
        System.out.println("Endereco:" + endereco);
        socket = new Socket(endereco, game.Server.PORTO);
        System.out.println("Socket:" + socket);

        // Recebe objetos vindos do servidor
        in = new ObjectInputStream(socket.getInputStream());
        // Envia texto
       	out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        
    }

    void receiveMessages() throws IOException {
        try {
            while (true) {
				 // Recebe o estado do jogo do servidor
				 LocalBoard data = (LocalBoard) in.readObject();

				 // Atualiza a instância de RemoteBoard com os dados recebidos
				 remoteBoard.setLocalBoardData(data);
	 
				 // Notifica a mudança para atualizar a interface gráfica
				 remoteBoard.setChanged();
	 
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
	
	/*  public static void main(String[] args) {
        RemoteBoard remoteBoard = new RemoteBoard();

        // Iniciar thread para recepção do estado do mundo
        Thread receiveThread = new Thread(() -> {
            while (true) {
                // Lógica para receber estado do mundo do servidor (como antes)
                updateRemoteBoard(remoteBoard);
                remoteBoard.setChanged();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Iniciar thread para enviar direções
        Thread sendDirectionThread = new Thread(() -> {
            while (true) {
                try {
                    // Retirar a direção da fila e enviá-la para o servidor (implementação necessária)
                    String direction = remoteBoard.getDirectionQueue().take();
                    sendDirectionToServer(direction);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Iniciar thread para outro comportamento independente (se necessário)
        // ...

        // Iniciar as threads
        receiveThread.start();
        sendDirectionThread.start();
        // Outras threads, se necessário
    }

    private static void sendDirectionToServer(String direction) {
        // Implementar a lógica para enviar a direção para o servidor
        // Pode envolver a comunicação por sockets, HTTP, etc.
        // ...
    }

    private static void updateRemoteBoard(RemoteBoard remoteBoard) {
        // Implementar a lógica de recepção do estado do mundo do servidor
        // e atualização da instância da RemoteBoard
    } */
