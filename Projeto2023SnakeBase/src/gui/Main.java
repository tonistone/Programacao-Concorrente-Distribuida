package gui;

import java.io.Console;
import java.io.IOException;
import java.rmi.Remote;

import javax.net.ssl.StandardConstants;

import environment.Board;
import environment.LocalBoard;
import game.Server;
import remote.RemoteBoard;

public class Main {
	public static void main(String[] args) {
		LocalBoard localBoard=new LocalBoard();
		SnakeGui game = new SnakeGui(localBoard, 600,0);
		game.init();
		// Launch server
            try {
                Server server = new Server(localBoard);
                server.startServing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        
	}
}
