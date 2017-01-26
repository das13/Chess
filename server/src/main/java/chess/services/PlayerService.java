package chess.services;

import chess.ServerMain;
import chess.controller.Controller;
import chess.model.Player;
import chess.model.Status;

import java.io.IOException;
import java.util.List;

/**
 * Created by viacheslav koshchii on 20.01.2017.
 */
public class PlayerService {
    public static void reg(Controller controller, String login, String password, String ipadress) throws IOException {
        /*
        * Изменил аргументы метода, вместо Player теперь Controller
        *
        * bobnewmark 22.01
        *
        * */
        controller.setPlayerLogin(login);
        controller.setPlayerPassword(password);
        controller.setPlayerIpadress(ipadress);
        controller.setPlayerStatus(Status.OFFLINE);

//        out.println("enter your nickname");
//        controller.setPlayerNickname(in.readLine());
//        out.println("enter your login");
//        controller.setPlayerLogin(in.readLine());
//        out.println("enter your password");
//        controller.setPlayerPassword(in.readLine());
//        out.println("You are offline. Get auth");
//        controller.getPlayer().setStatus(Status.OFFLINE);
    }

    public static void auth(Player player, String login, String password, List<String> out) {
        out.add("Ok");
        for (Player p : ServerMain.freePlayers) {
            System.out.println("user" +p.getNickname());
            out.add(p.getNickname());
        }
        player.setLogin(login);
        player.setNickname(login);
        player.setPassword(password);
        player.setStatus(Status.FREE);
        synchronized (ServerMain.freePlayers) {
            ServerMain.freePlayers.add(player);
        }
    }

}
