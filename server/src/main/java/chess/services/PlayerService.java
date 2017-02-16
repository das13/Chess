package chess.services;

import chess.Constants;
import chess.ServerMain;
import chess.controller.Controller;
import chess.model.Player;
import chess.model.Status;
import chess.services.xmlService.XMLSender;
import chess.services.xmlService.XMLsaveLoad;
import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>PlayerService</code> serves to handle user requests
 * from client, as logging/signing in, getting list of players.
 * Also it provides service methods ob playersthat are used
 * by server itself.
 */
public class PlayerService {

    private final static Logger logger = Logger.getLogger(PlayerService.class.getClass());
    public static void reg(Controller controller, String login, String password, String ipadress) {
        controller.setPlayerLogin(login);
        controller.setPlayerPassword(password);
        controller.setPlayerIpadress(ipadress);
        controller.setPlayerStatus(Status.OFFLINE);
    }

    /**
     * Is used to authenticate player from remote client.
     * @param player dummy Player instance given by Controller.
     * @param login login entered by remote user.
     * @param password password entered by remote user.
     * @param sender XMLSender for current remote client.
     * @throws IOException when cannot read from saved players.
     * @throws ParserConfigurationException in case of configuration error.
     * @throws TransformerConfigurationException in case of transforming xml data error.
     */
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

    /**
     * Sends most new information about free players
     * to the remote client for refreshing the list.
     *
     * @param player current player.
     * @param sender XMLSender for current remote client.
     * @return List of String values of logins and ranks.
     */
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

    /**
     * Saves edited profile of current player by unique id.
     * @param login new login.
     * @param password new password.
     * @param id unique id number of current player.
     * @return confirmation message.
     */
    public static List<String> saveProfile(String login, String password, int id) {
        Player player;
        List<String> list = new ArrayList<>();
        list.add("saveconfirm");
        try {
            player = findPlayerByIdAll(id);
            list.add("Ok");
            if (player != null) {
                player.setLogin(login);
                player.setPassword(password);
            }
            try {
                XMLsaveLoad.savePlayers();
            } catch (TransformerException | ParserConfigurationException | FileNotFoundException e) {
                logger.error("Error saving slayers to file", e);
            }
        } catch (NullPointerException e) {
            logger.error("Could not find player", e);
        }
        return list;
    }

    /**
     * Registers new player. New player needs to have unique login,
     * though it may be changed later, but only for another unique login.
     *
     * @param login user entered login.
     * @param password user entered password.
     * @param ipadress user IP address.
     * @param sender XMLSender for current remote client.
     * @throws IOException when cannot save players to file.
     * @throws ParserConfigurationException in case of configuration error.
     * @throws TransformerConfigurationException in case of transforming xml data error.
     */
    public static void reg(String login, String password, String ipadress, XMLSender sender) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        System.out.println("inside reg method");
        Player player;
        List<String> list = new ArrayList<String>();
        list.add("reg");
        if ((findPlayer(login)) != null) {
            list.add("denied");
            list.add("exists");
        } else {
            player = new Player(login, password, Status.OFFLINE, ipadress);
            player.setId(ServerMain.getFreePlayers().size() + 1);
            ServerMain.getAllPlayers().add(player); // фактически мы добавляем в список не FREE а OFFLINE плеера
            list.add("accepted");
            try {
                XMLsaveLoad.savePlayers();
            } catch (TransformerException e) {
                logger.error("Error saving slayers to file", e);
            }
        }
        sender.send(list);
    }

    /**
     * Finds player by login.
     * @param login unique login.
     * @return Player if found, or null.
     */
    private static Player findPlayer(String login) {
        for (Player p : ServerMain.allPlayers) {
            if (p.getLogin().equals(login)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Updates player rank, used after game is over.
     * @param fresh Player from current controller with updated rank.
     */
    static void updatePlayer(Player fresh) {
        for (int i = 0; i < ServerMain.allPlayers.size(); i++) {
            if (ServerMain.allPlayers.get(i).getLogin().equals(fresh.getLogin())) {
                ServerMain.allPlayers.get(i).setRank(fresh.getRank());
            }
        }
    }

    /**
     * Finds player by id.
     * @param id unique uneditable id.
     * @return Player if found, or null.
     */
    private static Player findPlayerByIdAll(int id) {
        for (Player p : ServerMain.allPlayers) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Sends list of all players to remote admin user.
     * @param sender XMLSender for current remote admin.
     * @throws IOException when cannot read saved players from file.
     * @throws ParserConfigurationException in case of configuration error.
     * @throws TransformerConfigurationException in case of transforming xml data error.
     */
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
