package chess.services;

import chess.ServerMain;
import chess.exceptions.ReplacePawnException;
import chess.exceptions.RivalFigureException;
import chess.model.Cell;
import chess.model.Game;
import chess.model.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by viacheslav koshchii on 20.01.2017.
 */
public class GameService {
    public static Player callPlayer(Player playerWhite, String nickName) {
        for (Player playerBlack : ServerMain.freePlayers) {
            if (playerBlack.getLogin().equals(nickName)) {
                Game game = new Game(playerWhite, playerBlack);
                ServerMain.waitingGames.add(game);
                return playerBlack;
            }
        }
        return null;
    }

    public static Game confirmGame(Player blackPlayer, String confirm) {
        if (confirm.equals("Ok")) {
            Iterator<Game> iter = ServerMain.waitingGames.iterator();
            while (iter.hasNext()) {
                Game game = iter.next();
                if (game.getBlackPlayer().equals(blackPlayer)) {
                    synchronized (ServerMain.waitingGames) {
                        iter.remove();
                    }
                    synchronized (ServerMain.games) {
                        ServerMain.games.add(game);
                    }
                    return game;
                }
            }
        } else {
            Iterator<Game> iter = ServerMain.waitingGames.iterator();
            while (iter.hasNext()) {
                Game game = iter.next();
                if (game.getBlackPlayer().equals(blackPlayer)) {
                    synchronized (ServerMain.waitingGames) {
                        iter.remove();
                    }
                    return game;
                }
            }

        }
        return null;
    }

    public static List<String> steps(Game game, int x, int y) throws RivalFigureException {
        Cell cell = game.getCell(x, y);
        System.out.println(cell.getFigure());
        //System.out.println(cell.getFigure().allAccessibleMove().size() * 2);
        /*if (game.getCurrentStep() != game.getBoard()[x][y].getFigure().getType()) {
            throw new RivalFigureException();
        }*/
        if (cell.isFigure()) {
            List<String> array = new ArrayList<String>();
            array.add("steps");
            int i = 0;
            for (Cell c : cell.getFigure().allAccessibleMove()) {
                array.add(c.getY()+""+c.getX());
            }
            System.out.println(array.size());
            return array;
        } else {
            System.out.println("нет фигуры");
            throw new NullPointerException();
        }
    }

    public static int[] move(Game game, List<String> str) throws RivalFigureException {
        int[] array = new int[4];
        System.out.println("move");
            //out.println("enter coordinates of figure - x and y");
            int x1 = Integer.parseInt(str.get(1));
        System.out.println(str.get(1));
        System.out.println(str.get(2));
        System.out.println(str.get(3));
        System.out.println(str.get(4));
            array[0] = x1;
            int y1 = Integer.parseInt(str.get(2));
            array[1] = y1;
            //out.println("enter coordinates of your step - x and y");
            int x2 = Integer.parseInt(str.get(3));
            array[2] = x2;
            int y2 = Integer.parseInt(str.get(4));
            array[3] = y2;
           /* if (game.getCurrentStep() != game.getBoard()[x1][y1].getFigure().getType()) {
                throw new RivalFigureException();
            }*/
            if (game.getBoard()[x1][y1].isFigure()) {
                try {
                    System.out.println("begin"+x2+" "+y2);
                    game.getBoard()[x1][y1].getFigure().move(game.getBoard()[x2][y2]);
                    System.out.println("done"+x2+" "+y2);
                } catch (ReplacePawnException e) {
                    //out.println("pick figure");
                    e.printStackTrace();
                }
            }
        return array;
    }
}
