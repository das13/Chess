package chess;

import chess.model.Game;
import chess.model.Player;
import chess.services.xmlService.XMLsaveLoad;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <code>ServerMain</code> is the main class for server application,
 * it creates lists for players, restores saved players from file,
 * and starts <code>Server</code> for communicating with players.
 */
public class ServerMain {
    private final static Logger logger = Logger.getLogger(ServerMain.class);
    public static final List<Player> freePlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static List<Player> allPlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static final List<Player> inGamePlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static final List<Game> games =
            Collections.synchronizedList(new ArrayList<Game>());
    public static final List<Game> waitingGames =
            Collections.synchronizedList(new ArrayList<Game>());
    public static final List<String> bannedIP =
            Collections.synchronizedList(new ArrayList<String>());
    public static String loginAdmin;
    public static String passwordAdmin;
    public static int serverPort;
    public static void main(String[] args) {
        logger.info("Server launched");
        try {
            XMLsaveLoad.loadPlayers();
            XMLsaveLoad.loadBanned();
            XMLsaveLoad.loadSettings();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Failed loading from file ", e);
        } catch (TransformerException e) {
            logger.error("Failed reading XML from file ", e);
        }
        new Server();
    }
    public static List<Player> getFreePlayers() {
        return freePlayers;
    }

    public static List<Player> getAllPlayers() {
        return allPlayers;
    }

    public static void setAllPlayers(List<Player> allPlayers) {
        ServerMain.allPlayers = allPlayers;
    }
}
