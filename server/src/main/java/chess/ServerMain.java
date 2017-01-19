package chess;

import chess.model.MiniServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
       String a=null;
       ServerSocket serverSocket = null;
        boolean listeningSocket = true;
        try {
            serverSocket = new ServerSocket(2543);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 2543");
        }
        try {
            while (listeningSocket) {
                Socket clientSocket = serverSocket.accept();
                MiniServer mini = new MiniServer(clientSocket);
                mini.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
