package remote;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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
    }

    private void handleConnection() throws IOException {
       
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
                System.out.println("Mensagem recebida do servidor: " + loadMessage.toString());
                remoteBoard.setLoad(loadMessage);
                remoteBoard.setChanged();    
            } catch (ClassNotFoundException e) {
                socket.close();
                e.printStackTrace();
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
    }

    private void sendMessages() throws IOException {

        try {
            while (!socket.isClosed()) {
                    if (remoteBoard.getnewDirectionPressed()) {
                        out.println(remoteBoard.getKeyPressed());
                        remoteBoard.handleKeyRelease();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client().runClient();
    }
}
