package chess;

import chess.model.Cell;
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
    final static Logger logger = Logger.getLogger(ServerMain.class.getClass());
    public static List<Player> freePlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static List<Player> allPlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static List<Player> inGamePlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static List<Game> games =
            Collections.synchronizedList(new ArrayList<Game>());
    public static List<Game> waitingGames =
            Collections.synchronizedList(new ArrayList<Game>());
    public static void main(String[] args) {
        logger.info("Server launched");
        try {
            XMLsaveLoad.loadPlayers();
            //System.out.println("players restored from file");
//            for (Player p: freePlayers) {
//                System.out.println("player restored from file: " + p.getLogin());
//            }
        } catch (ParserConfigurationException e) {
            logger.error("Failed loading players from file ", e);
        } catch (IOException e) {
            logger.error("Failed loading players from file ", e);
        } catch (SAXException e) {
            logger.error("Failed loading players from file ", e);
        }
        Game game = new Game();
        List<Cell> cells = game.getBoard()[3][0].getFigure().allAccessibleMove();
        //System.out.println(game.getBoard()[3][0].getFigure().getClass().getName());
        /*try {
            game.getBoard()[0][6].getFigure().move(game.getBoard()[0][7]);
        } catch (ReplacePawnException e) {
            logger.debug("ReplacePawnException, probably it's fine ", e);
            System.out.println("pick figure");
        }*/
//        for(Cell c: cells)
//            System.out.println(c.getX()+"."+c.getY());
        new Server();

    }
    public static List<Player> getFreePlayers() {
        return freePlayers;
    }

    public static List<Player> getInGamePlayers() {
        return inGamePlayers;
    }

    public static List<Player> getAllPlayers() {
        return allPlayers;
    }

    public static void setAllPlayers(List<Player> allPlayers) {
        ServerMain.allPlayers = allPlayers;
    }
}
