package chess;


import chess.controller.Controller;
import chess.model.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Server {

    private static final List<Player> players =
            Collections.synchronizedList(new ArrayList<Player>());
    private ServerSocket server;

    public Server() {
        try {
            server = new ServerSocket(Constants.PORT);

            while (true) {
                Socket socket = server.accept();
                Controller controller = new Controller(socket);
                controller.start();
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
        } catch (Exception e) {
            System.err.println("Threads did not close!");
        }
    }
}