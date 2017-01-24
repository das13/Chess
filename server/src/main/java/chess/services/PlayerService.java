package chess.services;

import chess.ServerMain;
import chess.controller.Controller;
import chess.model.Player;
import chess.model.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by viacheslav koshchii on 20.01.2017.
 */
public class PlayerService {
    public static void reg(Controller controller, BufferedReader in, PrintWriter out) throws IOException {

        /*
        * Изменил аргументы метода, вместо Player теперь Controller
        *
        * bobnewmark 22.01
        *
        * */

        out.println("enter your nickname");
        controller.setPlayerNickname(in.readLine());
        out.println("enter your login");
        controller.setPlayerLogin(in.readLine());
        out.println("enter your password");
        controller.setPlayerPassword(in.readLine());
        out.println("You are offline. Get auth");
        controller.getPlayer().setStatus(Status.OFFLINE);
    }
    public static void auth(Player player, BufferedReader in, PrintWriter out) throws IOException {
        player.setLogin(in.readLine());
        player.setPassword(in.readLine());
        System.out.println(player.getLogin()+" "+player.getPassword());
        out.println("Ok");
        player.setStatus(Status.FREE);
        synchronized(ServerMain.freePlayers) {
            ServerMain.freePlayers.add(player);
        }
    }

}
