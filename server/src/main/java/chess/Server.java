package chess;


import chess.controller.Controller;
import chess.model.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    private static List<Player> players =
            Collections.synchronizedList(new ArrayList<Player>());
    private ServerSocket server;

    public Server() {
        try {
            server = new ServerSocket(2544);

            while (true) {
                Socket socket = server.accept();
                Controller controller = new Controller(socket);
                controller.start();
//                Player player = new Player(socket);
//                player.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();
//            synchronized(players) {
//                Iterator<Player> iter = players.iterator();
//                while(iter.hasNext()) {
//                    ((Player) iter.next()).close();
//                }
//            }
        } catch (Exception e) {
            System.err.println("Threads did not close!");
        }
    }

    public static List<Player> getPlayers() {
        return players;
    }
}