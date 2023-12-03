package gui;

import java.io.Console;
import java.io.IOException;

import javax.net.ssl.StandardConstants;

import environment.LocalBoard;
import game.Server;

public class Main {
	public static void main(String[] args) {
		LocalBoard localBoard=new LocalBoard();
		SnakeGui game = new SnakeGui(localBoard, 600,0);
		game.init();
		// Launch server
		new Thread(() -> {
            try {
                Server server = new Server();
                server.startServing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
	}
}
