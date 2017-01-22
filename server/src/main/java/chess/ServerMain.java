package chess;

import chess.model.Cell;
import chess.model.Game;
import chess.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerMain {
    public static List<Player> freePlayers =
            Collections.synchronizedList(new ArrayList<Player>());
    public static List<Game> games =
            Collections.synchronizedList(new ArrayList<Game>());
    public static List<Game> waitingGames =
            Collections.synchronizedList(new ArrayList<Game>());
    public static void main(String[] args) {
        Game game = new Game();
        List<Cell> cells = game.getBoard()[1][0].getFigure().allAccessibleMove();
        for(Cell c: cells)
        System.out.println(c.getX()+" "+c.getY());
       new Server();
    }

    public static List<Player> getFreePlayers() {
        return freePlayers;
    }
}
