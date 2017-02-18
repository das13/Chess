package chess;


import chess.controller.Controller;
import chess.model.Player;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <code>Server</code> creates server socket and gives every
 * connected user his own instance of <code>Controller</code>
 * to maintain data transferring.
 */
public class Server {

    private static final List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
    private ServerSocket server;
    private final static Logger logger = Logger.getLogger(Server.class.getClass());

    public Server() {
        try {
            server = new ServerSocket(Constants.PORT);
            while (true) {
                Socket socket = server.accept();
                Controller controller = new Controller(socket);
                controller.start();
            }
        } catch (IOException e) {
            logger.error("Error on Server establishing connection", e);
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();
        } catch (Exception e) {
            logger.error("Server error closing connections", e);
        }
    }
}