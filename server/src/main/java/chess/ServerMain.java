package chess;

import chess.model.Game;
import chess.model.Player;
import chess.services.xmlService.XMLsaveLoad;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ServerMain {
    private final static Logger logger = Logger.getLogger(ServerMain.class.getClass());
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
    public static void main(String[] args) {
        logger.info("Server launched");
        try {
            XMLsaveLoad.loadPlayers();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Failed loading players from file ", e);
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
