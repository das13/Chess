package chess.services;

import chess.Server;
import chess.ServerMain;
import chess.controller.Controller;
import chess.model.Player;
import chess.model.Status;
import chess.services.xmlService.XMLSender;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
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

    public static void reg(String login, String password, String ipadress, XMLSender sender) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        Player player;
        List<String> list = new ArrayList<String>();
        list.add("reg");
        if ((player = findPlayer(login)) != null) {
            list.add("denied");
            list.add("exists");
        } else {
            player = new Player(login, password, Status.OFFLINE, ipadress);
            ServerMain.getFreePlayers().add(player); // фактически мы добавляем в список не FREE а OFFLINE плеера
            list.add("accepted");
        }
        sender.send(list);
    }

    // дополнительный метод для поиска, стоит использовать при авторизации, регистрации и т.д.
    public static Player findPlayer(String login) {
        for (Player p : ServerMain.freePlayers) {
            if (p.getLogin().equals(login)) {
                return p;
            }
        }
        return null;
    }

}
