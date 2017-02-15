package chess.services;

import chess.Constants;
import chess.ServerMain;
import chess.controller.Controller;
import chess.model.Player;
import chess.model.Status;
import chess.services.xmlService.XMLSender;
import chess.services.xmlService.XMLsaveLoad;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viacheslav koshchii on 20.01.2017.
 */
public class PlayerService {
    public static void reg(Controller controller, String login, String password, String ipadress) {
        controller.setPlayerLogin(login);
        controller.setPlayerPassword(password);
        controller.setPlayerIpadress(ipadress);
        controller.setPlayerStatus(Status.OFFLINE);
    }

    public static void reenter(Player player, String login, String password, XMLSender sender) throws IOException, ParserConfigurationException, TransformerConfigurationException {

        List<String> out = new ArrayList<String>();

        if (ServerMain.inGamePlayers.contains(player)) {
            player.setStatus(Status.FREE);
            ServerMain.inGamePlayers.remove(player);
        }
        if (login.equals(Constants.ADMIN_NAME) && password.equals(Constants.ADMIN_PASS)) {
            out.add("admin");
            sender.send(out);
        } else {
            for (Player p : ServerMain.allPlayers) {
                if (p.getLogin().equals(login) && p.getPassword().equals(password)) {
                    player.setId(p.getId());
                    player.setRank(p.getRank());
                    out.add("Ok");
                    out.add(String.valueOf(p.getId()));
                    out.add(login);
                    out.add(password);
                    out.add(String.valueOf(p.getRank()));
                    p.setStatus(Status.FREE);
                    break;
                }
            }

            for (Player p : ServerMain.freePlayers) {
                out.add(p.getLogin());
                out.add(String.valueOf(p.getRank()));
            }
            synchronized (ServerMain.freePlayers) {
                ServerMain.freePlayers.add(player);
            }

        }
        sender.send(out);
    }

    public static void auth(Player player, String login, String password, XMLSender sender) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        boolean check = false;
        List<String> out = new ArrayList<String>();
        player.setLogin(login);
        player.setPassword(password);
        out.add("reg");
        if (login.equals("superuser") && password.equals("3141592")) {
            out.add("admin");
            for (Player p : ServerMain.getAllPlayers()) {
                out.add(p.getLogin());
                out.add(String.valueOf(p.getRank()));
                out.add(String.valueOf(p.getStatus()));
                out.add(p.getIpadress());
            }
            sender.send(out);
            return;
        }
        if (ServerMain.freePlayers.contains(player) || ServerMain.inGamePlayers.contains(player)) {
            out.add("online");
            sender.send(out);
            return;
        } else {
            for (Player p : ServerMain.allPlayers) {
                if (p.getLogin().equals(login) && p.getPassword().equals(password)) {
                    player.setId(p.getId());
                    player.setRank(p.getRank());
                    check = true;
                    out.add("Ok");
                    out.add(String.valueOf(p.getId()));
                    out.add(login);
                    out.add(password);
                    out.add(String.valueOf(p.getRank()));
                    p.setStatus(Status.FREE);
                    break;
                }
            }
            if (!check) {
                out.add("error");
            } else {
                for (Player p : ServerMain.freePlayers) {
                    out.add(p.getLogin());
                    out.add(String.valueOf(p.getRank()));
                }
                synchronized (ServerMain.freePlayers) {
                    ServerMain.freePlayers.add(player);
                }
            }
        }
        sender.send(out);
    }

    public static List<String> refresh(Player player, XMLSender sender) {
        List<String> out = new ArrayList<>();
        out.add("Ok");
        out.add(String.valueOf(player.getId()));
        out.add(player.getLogin());
        out.add(player.getPassword());
        out.add(String.valueOf(player.getRank()));
        player.setStatus(Status.FREE);
        for (Player p : ServerMain.freePlayers) {
            if (!player.equals(p)) {
                out.add(p.getLogin());
                out.add(String.valueOf(p.getRank()));
            }
        }
        return out;
    }

    public static List<String> saveProfile(String login, String password, int id) {
        Player player;
        List<String> list = new ArrayList<>();
        list.add("saveconfirm");
        try {
            player = findPlayerByIdAll(id);
            System.out.println(login + " " + password + " " + id);
            list.add("Ok");
            if (player != null) {
                player.setLogin(login);
                player.setPassword(password);
            }
            try {
                XMLsaveLoad.savePlayers();
                System.out.println("Players saved succsessfully");
            } catch (TransformerException | ParserConfigurationException | FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            list.add("error");
        }
        return list;
    }

    public static void reg(String login, String password, String ipadress, XMLSender sender) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        System.out.println("inside reg method");
        Player player;
        List<String> list = new ArrayList<String>();
        list.add("reg");
        if ((findPlayer(login)) != null) {
            System.out.println("such player found");
            list.add("denied");
            list.add("exists");
        } else {
            player = new Player(login, password, Status.OFFLINE, ipadress);
            player.setId(ServerMain.getFreePlayers().size() + 1);
            ServerMain.getAllPlayers().add(player); // фактически мы добавляем в список не FREE а OFFLINE плеера
            list.add("accepted");
            System.out.println("Player " + login + " " + password + " " + ipadress + " created");
            try {
                XMLsaveLoad.savePlayers();
                System.out.println("Players saved succsessfully");
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        sender.send(list);
    }

    // дополнительный метод для поиска, стоит использовать при авторизации, регистрации и т.д.
    public static Player findPlayer(String login) {
        for (Player p : ServerMain.allPlayers) {
            if (p.getLogin().equals(login)) {
                return p;
            }
        }
        return null;
    }

    public static Player findPlayerById(int id) {
        for (Player p : ServerMain.freePlayers) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static void updatePlayer(Player fresh) {
        for (int i = 0; i < ServerMain.allPlayers.size(); i++) {
            if (ServerMain.allPlayers.get(i).getLogin().equals(fresh.getLogin())) {
                ServerMain.allPlayers.get(i).setRank(fresh.getRank());
            }
        }
    }

    private static Player findPlayerByIdAll(int id) {
        for (Player p : ServerMain.allPlayers) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static void adminGetPlayers(XMLSender sender) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        List<String> list = new ArrayList<>();
        list.add("admin_getPlayers");
        list.add("all");
        for (Player p : ServerMain.getAllPlayers()) {
            list.add(p.getLogin());
            list.add(String.valueOf(p.getRank()));
            list.add(String.valueOf(p.getStatus()));
            list.add(p.getIpadress());
        }
        sender.send(list);
    }
}
