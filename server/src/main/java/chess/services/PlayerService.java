package chess.services;

import chess.ServerMain;
import chess.model.Player;
import chess.model.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Admin on 20.01.2017.
 */
public class PlayerService {
    public static void reg(Player player, BufferedReader in, PrintWriter out) throws IOException {
        out.println("enter your nickname");
        player.setNickname(in.readLine());
        out.println("enter your login");
        player.setLogin(in.readLine());
        out.println("enter your password");
        player.setPassword(in.readLine());
        out.println("You are offline. Get auth");
        player.setStatus(Status.OFFLINE);
    }
    public static void auth(Player player){
        player.setStatus(Status.FREE);
        synchronized(ServerMain.freePlayers) {
            ServerMain.freePlayers.add(player);
        }
    }

}
