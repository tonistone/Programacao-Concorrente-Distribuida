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

import environment.Board;
import environment.LocalBoard;
import gui.SnakeGui;

/**
 * Remore client, only for part II
 * 
 * @author luismota
 *
 */

public class Client {

    private ObjectInputStream in;
    private PrintWriter out;
    private Socket socket;
    private RemoteBoard remoteBoard;

    public void runClient() {
        try {
            // conectar ao servidor
            connectToServer();
            // enviar e receber
            handleConnection();
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

    private void connectToServer() throws IOException {
        InetAddress endereco = InetAddress.getByName(null);
        System.out.println("Endereco:" + endereco);
        socket = new Socket(endereco, game.Server.PORTO);
        System.out.println("Socket:" + socket);
        // Recebe objetos vindos do servidor
        in = new ObjectInputStream(socket.getInputStream());
        // Envia texto para o servidor
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        //Cria uma instância de RemoteBoard
        remoteBoard = new RemoteBoard();
        //inicializa
        remoteBoard.init();
    }

    private void handleConnection() throws IOException {
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

    }

    private void receiveMessages() throws IOException {
        try {
            while (true) {
                //se existir alguma coisa para ler
                if (in.available() > 0) {
                // Recebe o estado do jogo do servidor
                Object receivedObject = in.readObject();

                // Se o objeto recebido for do tipo RemoteBoard
                if (receivedObject instanceof RemoteBoard) {
                    RemoteBoard receivedRemoteBoard = (RemoteBoard) receivedObject;

                    // Atualiza a instância existente da RemoteBoard
                    remoteBoard.updateBoardState(receivedRemoteBoard);
                }
                Thread.sleep(100);
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMessages() throws IOException {
        try {
            while (true) {
                // String str = remoteBoard.handleKeyPress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client().runClient();
    }
}
