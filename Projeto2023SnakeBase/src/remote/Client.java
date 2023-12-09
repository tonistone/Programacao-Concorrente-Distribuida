package remote;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import environment.Board;
import environment.BoardPosition;
import game.LoadGameServer;
import game.Server;
 
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
        }
    }

    private void connectToServer() throws IOException {
        InetAddress endereco = InetAddress.getByName(null);
        System.out.println("Endereco no client:" + endereco);
        socket = new Socket(endereco, Server.PORTO);
        System.out.println("Socket:" + socket);

        in = new ObjectInputStream(socket.getInputStream());
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        remoteBoard = new RemoteBoard();
        // inicializa
        remoteBoard.init();

        int numberOfObservers = remoteBoard.countObservers();

        if (numberOfObservers > 0) {
            System.out.println("Existem observadores registrados.");
        } else {
            System.out.println("Não existem observadores registrados.");
        }
    }

    private void handleConnection() throws IOException {
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
        while (!socket.isClosed()) {
            try {
                System.out.println(in.readObject());
                LoadGameServer loadMessage = (LoadGameServer) in.readObject();
                System.out.println("Mensagem recebida do servidor: " + loadMessage);
                remoteBoard.setLoad(loadMessage);
                remoteBoard.setChanged();
            } catch (IOException | ClassNotFoundException e) {
                try {
                    socket.close();
                    System.out.println("Aconteceu");
                    e.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }

    private void sendMessages() throws IOException {

        try {
            while (!socket.isClosed()) {
                // System.out.println("sending");
                    if (remoteBoard.getnewDirectionPressed()) {
                        out.println(remoteBoard.getKeyPressed());
                        remoteBoard.handleKeyRelease();
                        //System.out.println(remoteBoard.getKeyPressed());
                    }
                    //remoteBoard.clearKeyPressed();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Client().runClient();
    }
}
