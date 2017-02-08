package chess.services;

import chess.ServerMain;
import chess.exceptions.ReplacePawnException;
import chess.exceptions.RivalFigureException;
import chess.model.*;
import chess.services.xmlService.XMLSender;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
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

    public static List<String> move(Game game, List<String> str, Player player, Player otherPlayer) throws RivalFigureException {
        List<String> answer = new ArrayList<String>();
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

        if (game.getBoard()[x1][y1].isFigure()) {
            try {
                Figure figure = game.getBoard()[x1][y1].getFigure();
                Type type = figure.getType();
                Type otherType = null;
                if(type == Type.WHITE) {
                    otherType = Type.BLACK;
                } else {
                    otherType = Type.WHITE;
                }
                if (game.isPlayersKingAttacked(type) && game.getPlayerMoves(type).size() > 0) {
                    System.out.println("CANCEL MOVE");
                    answer.add("cancel");
                    return answer;
                } else if (game.isPlayersKingAttacked(otherType)
                        && game.getKing(otherType).allAccessibleMove().size() == 0
                        && !game.getEnemyMoves(otherType).contains(game.getLastFigureMoved().getCell())) {
                    System.out.println("CHECKMATE");
                    answer.add("checkmate");
                    answer.add(String.valueOf(type));
                    player.setRank(player.getRank()+5);
                    otherPlayer.setRank(otherPlayer.getRank()-5);
                    answer.add(player.getLogin());
                    answer.add(String.valueOf(player.getRank()));
                    answer.add(otherPlayer.getLogin());
                    answer.add(String.valueOf(otherPlayer.getRank()));
                    return answer;
                } else if (game.getEnemyMoves(type).size() == 0) {
                    System.out.println("STALEMATE");
                    answer.add("stalemate");
                    return answer;
                }
                if (!figure.allAccessibleMove().contains(game.getBoard()[x2][y2])) {
                    System.out.println("CANCEL MOVE");
                    answer.add("cancel");
                    return answer;
                }
                if (figure.allAccessibleMove().contains(game.getBoard()[x2][y2])) {
                    System.out.println("MOVING");
                    System.out.println("begin"+x2+" "+y2);
                    game.getBoard()[x1][y1].getFigure().move(game.getBoard()[x2][y2]);
                    game.setLastFigureMoved(game.getBoard()[x2][y2].getFigure());
                    System.out.println("done"+x2+" "+y2);
                    System.out.println(game.getCurrentStep());
                    //game.changeCurrentStep();
                    System.out.println(game.getCurrentStep());
                    answer.add("moving");
                    return answer;
                }

            } catch (ReplacePawnException e) {
                //out.println("pick figure");
                e.printStackTrace();
            }
        }

        return answer;
    }
}
