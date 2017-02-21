package chess.services;

import chess.ServerMain;
import chess.controller.Controller;
import chess.exceptions.ReplacePawnException;
import chess.model.*;
import chess.model.figures.King;
import chess.services.xmlService.XMLSender;
import chess.services.xmlService.XMLsaveLoad;
import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>GameService</code> is service class with all methods which need for the game of chess.
 * There are method for a call player, all accessible steps, move figure and looks for a checkmate situation
 */
public class GameService {

    private static boolean wasCastling;

    private final static Logger logger = Logger.getLogger(GameService.class);

    /**
     * <code>callPlayer</code> is method which a call player from a list with free players for a game
     * @param playerWhite is calling players object
     * @param nickName is called players name
     * @throws IOException when server cannot read from saved players.
     * @throws ParserConfigurationException in case of configuration error.
     * @throws TransformerConfigurationException in case of transforming xml data error.
     */
    public static void callPlayer(Player playerWhite, String nickName) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        String ipAddress = String.valueOf(playerWhite.getController().getSocket().getInetAddress());
        List<String> out = new ArrayList<String>();
        if (PlayerService.isBanned(ipAddress)) {
            out.add("banned");
            out.add("byAdmin");
            playerWhite.getController().getSender().send(out);
            return;
        }
        boolean check = false;
        Controller controller = playerWhite.getController();
        XMLSender sender = controller.getSender();
        for (Player playerBlack : ServerMain.freePlayers) {
            if (playerBlack.getLogin().equals(nickName)) {
                playerWhite.setType(Type.WHITE);
                playerBlack.setType(Type.BLACK);
                Game game = new Game(playerWhite, playerBlack);
                ServerMain.waitingGames.add(game);
                Controller otherController = playerBlack.getController();
                XMLSender otherSender = otherController.getSender();
                out.add("confirm");
                out.add(playerWhite.getLogin());
                otherSender.send(out);
                check = true;
                break;
            }
        }
        if(!check){
            List<String> outList = new ArrayList<String>();
            outList.add("notconfirm");
            outList.addAll(PlayerService.refresh(playerWhite, sender));
            sender.send(outList);
        }
    }

    /**
     * When game ends with a draw.
     * @param thisPlayer current player.
     */
    public static void draw(Player thisPlayer) {
        endGame("draw", thisPlayer, thisPlayer.getCurrentGame().getOtherPlayer(thisPlayer));
    }

    /**
     * Confirms that current player accept the request to play
     * from another player.
     *
     * @param thisPlayer current player.
     * @param str message sent from another player.
     * @throws IOException when server cannot read from saved players.
     * @throws ParserConfigurationException in case of configuration error.
     * @throws TransformerConfigurationException in case of transforming xml data error.
     */
    public static void confirmGame(Player thisPlayer, List<String> str) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        List<String> out = new ArrayList<String>();
        out.add("confirmresponse");
        Game game = thisPlayer.getCurrentGame();
        Player otherPlayer = game.getOtherPlayer(thisPlayer);
        Controller otherController = otherPlayer.getController();
        XMLSender otherSender = otherController.getSender();
        if (str.get(1).equals("Ok")) {
                if (game.getBlackPlayer().equals(thisPlayer)) {
                    synchronized (ServerMain.waitingGames) {
                        ServerMain.waitingGames.remove(game);
                    }
                    synchronized (ServerMain.games) {
                        ServerMain.games.add(game);
                    }
                    out.add("Ok");
                    thisPlayer.getController().setCurrentGame(game);
                    otherPlayer.getController().setCurrentGame(game);
                    synchronized (ServerMain.freePlayers) {
                        ServerMain.freePlayers.remove(thisPlayer);
                        ServerMain.freePlayers.remove(otherPlayer);
                    }
                    synchronized (ServerMain.inGamePlayers) {
                        ServerMain.inGamePlayers.add(thisPlayer);
                        ServerMain.inGamePlayers.add(otherPlayer);
                    }
                }
        } else {
            out.add("No");
            synchronized (ServerMain.waitingGames) {
                ServerMain.waitingGames.remove(game);
            }
        }
        otherSender.send(out);
    }

    /**
     * Builds List of string values for steps allowed to make
     * for figure that player wants to move.
     *
     * @param game current game.
     * @param x number of column of board.
     * @param y number of row of board.
     * @return all allowed steps.
     */
    public static List<String> steps(Game game, int x, int y) {

        Cell cell = game.getCell(x, y);
        if (cell.isFigure()) {
            List<String> array = new ArrayList<String>();
            array.add("steps");
            if (game.getCurrentStep() != game.getBoard()[x][y].getFigure().getType()) {
                return array;
            }
            int i = 0;
            for (Cell c : cell.getFigure().allAccessibleMove()) {
                array.add(c.getY() + "" + c.getX());
            }
            return array;
        } else {
            logger.error("Cannot find a figure on this cell");
            throw new NullPointerException();
        }
    }

    /**
     * Moving a figure. This action can lead to check, checkmate,
     * move can be not allowed or can produce castling. All possible
     * variations of effects of a move are handled here.
     *
     * @param game current game.
     * @param str List of String values for source cell and target cell.
     * @param player current player.
     * @return result of the move.
     */
    public static List<String> move(Game game, List<String> str, Player player) {
        List<String> answer = new ArrayList<String>();
        List<String> rivalanswer = new ArrayList<String>();
        List<String> out = new ArrayList<String>();
        Player otherPlayer = game.getOtherPlayer(player);
        XMLSender otherSender = otherPlayer.getController().getSender();
        XMLSender sender = player.getController().getSender();
        int[] array = new int[4];
        int x1 = Integer.parseInt(str.get(1));
        array[0] = x1;
        int y1 = Integer.parseInt(str.get(2));
        array[1] = y1;
        int x2 = Integer.parseInt(str.get(3));
        array[2] = x2;
        int y2 = Integer.parseInt(str.get(4));
        array[3] = y2;

        boolean isCheckmate = false;

        if (game.getBoard()[x1][y1].isFigure()) try {
            Figure figure = game.getBoard()[x1][y1].getFigure();
            boolean isFirstMove = figure.isFirstMove();
            Type type = figure.getType();
            Type otherType = null;
            if (type == Type.WHITE) {
                otherType = Type.BLACK;
            } else {
                otherType = Type.WHITE;
            }
            if (!figure.allAccessibleMove().contains(game.getBoard()[x2][y2])) {
                answer.add("cancel");
                game.setCurrentStep(type);
                sender.send(answer);
                return answer;
            }
            else {
                game.setExLastFigureTaken(game.getLastFigureTaken());
                game.setLastFigureTaken(game.getBoard()[x2][y2].getFigure());
                game.setExLastFigureMoved(game.getLastFigureMoved());
                game.setLastFigureMoved(figure);

                game.setExFromCell(game.getLastFromCell());
                game.setExToCell(game.getLastToCell());
                game.setLastFromCell(figure.getCell());
                game.setLastToCell(game.getBoard()[x2][y2]);
                game.setCurrentCell(game.getBoard()[x2][y2]);
                figure.move(game.getBoard()[x2][y2]);
                game.getBoard()[x2][y2].setFigure(figure);

                if (game.getLastFigureTaken() != null) {
                    game.getLastFigureTaken().setCell(null);
                }
                if (figure instanceof King && y1 == y2
                        && (x2 - x1 == 2 || x1 - x2 == 2)) {
                    answer.add("castling");
                    if (y2 == 0) {
                        answer.add("black");
                    } else {
                        answer.add("white");
                    }
                    if (x2 == 6) {
                        answer.add("kingside");
                    } else {
                        answer.add("queenside");
                    }
                    out.add("castling");
                    out.add(answer.get(1));
                    out.add(answer.get(2));
                    out.add(str.get(5));
                    otherSender.send(out);
                    wasCastling = true;
                    return answer;
                }

                game.setAllWhiteMoves();
                game.setAllBlackMoves();

                if (game.isPlayersKingAttacked(type)) {
                    game.getBoard()[x1][y1].setFigure(game.getLastFigureMoved());
                    game.getLastFigureMoved().setCell(game.getBoard()[x1][y1]);
                    game.getLastFigureMoved().setFirstMove(isFirstMove);
                    game.getBoard()[x2][y2].setFigure(game.getLastFigureTaken());

                    if (game.getLastFigureTaken() != null) {
                        game.getLastFigureTaken().setCell(game.getBoard()[x2][y2]);
                    }
                    answer.add("cancel");
                    game.setCurrentStep(type);
                    sender.send(answer);
                    return answer;
                }

                else if (game.isPlayersKingAttacked(otherType)) {
                    isCheckmate = true;
                    for (Figure enemyFigure : game.getFigures(otherType)) {
                        if (!isCheckmate) break;
                        if (enemyFigure instanceof King) {
                            if (!isCheckmate) break;
                            List<Cell> countingList = enemyFigure.allAccessibleMove();
                            for (Cell kingAccessibleCell : countingList) {
                                Figure tempFig = null;
                                if (kingAccessibleCell.isFigure()) {
                                    tempFig = kingAccessibleCell.getFigure();
                                    tempFig.setCell(null);
                                }
                                kingAccessibleCell.setFigure(null);
                                synchronized (GameService.class) {
                                    game.getEnemyMoves(otherType);
                                }
                                game.setAllBlackMoves();
                                game.setAllWhiteMoves();

                                if (game.getEnemyMoves(otherType).contains(kingAccessibleCell)) {
                                    kingAccessibleCell.setFigure(tempFig);
                                    if (tempFig != null) {
                                        tempFig.setCell(kingAccessibleCell);
                                    }
                                    game.getKing(otherType).allAccessibleMove().remove(kingAccessibleCell);
                                }
                                else {
                                    if (tempFig != null) {
                                        tempFig.setCell(kingAccessibleCell);
                                        kingAccessibleCell.setFigure(tempFig);
                                    }
                                    isCheckmate = false;
                                    break;
                                }
                            }
                        } else {
                            Cell startCell = enemyFigure.getCell();
                            for (Cell cell : enemyFigure.allAccessibleMove()) {
                                Figure tempFigure = null;
                                if (cell.isFigure()) {
                                    tempFigure = cell.getFigure();
                                    tempFigure.setCell(null);
                                }
                                cell.setFigure(enemyFigure);
                                enemyFigure.setCell(cell);
                                game.setAllBlackMoves();
                                game.setAllWhiteMoves();
                                if (game.isPlayersKingAttacked(otherType)) {
                                    enemyFigure.setCell(startCell);
                                    startCell.setFigure(enemyFigure);
                                    cell.setFigure(tempFigure);
                                    if (tempFigure != null) {
                                        tempFigure.setCell(cell);
                                    }
                                }
                                else {
                                    enemyFigure.setCell(startCell);
                                    startCell.setFigure(enemyFigure);
                                    cell.setFigure(tempFigure);
                                    if (tempFigure != null) tempFigure.setCell(cell);
                                    cell.setFigure(tempFigure);
                                    isCheckmate = false;
                                    game.setAllBlackMoves();
                                    game.setAllWhiteMoves();
                                    break;
                                }
                            }
                        }
                    }
                    if (isCheckmate) {
                        endGame("checkmate", player, otherPlayer);
                        return answer;
                    }
                }
                wasCastling = false;
                game.setCurrentStep(otherType);
                answer.add("moving");
                out.add("rivalMove");
                out.add(str.get(1));
                out.add(str.get(2));
                out.add(str.get(3));
                out.add(str.get(4));
                out.add(str.get(5));
                otherSender.send(out);
                return answer;
            }

        } catch (ReplacePawnException e) {
            answer.add("replacePawn");
            answer.add(String.valueOf(y1));
            answer.add(String.valueOf(x1));
            answer.add(y2 + "" + x2);
            try {
                sender.send(answer);
            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                logger.error("Error sending message", e1);
            }
            return answer;
        } catch (TransformerException | ParserConfigurationException | IOException e) {
            logger.error("Error finishing move", e);
        }
        return answer;
    }

    /**
     * Finishes current game with result depending on given reason.
     * @param reason reason of ending the game.
     * @param player current player.
     * @param otherPlayer opponent.
     */
    public static void endGame(String reason, Player player, Player otherPlayer) {
        List<String> answer = new ArrayList<String>();
        List<String> out = new ArrayList<String>();
        otherPlayer = player.getCurrentGame().getOtherPlayer(player);
        XMLSender otherSender = otherPlayer.getController().getSender();
        XMLSender sender = player.getController().getSender();
        answer.add(reason);
        out.add(reason);
        answer.add("Ok");
        out.add("Ok");
        if ("checkmate".equals(reason)) {
            player.setRank(player.getRank() + 10);
            otherPlayer.setRank(otherPlayer.getRank() - 10);
        } else if ("draw".equals(reason)) {
            player.setRank(player.getRank() - 5);
            otherPlayer.setRank(otherPlayer.getRank() - 5);
        } else if ("resign".equals(reason)) {
            player.setRank(player.getRank() - 10);
            otherPlayer.setRank(otherPlayer.getRank() + 5);
        }
        answer.add("WHITE");
        answer.add(player.getLogin());
        answer.add(player.getPassword());
        answer.add(String.valueOf(player.getRank()));
        out.add("BLACK");
        out.add(otherPlayer.getLogin());
        out.add(otherPlayer.getPassword());
        out.add(String.valueOf(otherPlayer.getRank()));
        PlayerService.updatePlayer(player);
        PlayerService.updatePlayer(otherPlayer);
        try {
            XMLsaveLoad.savePlayers();
        } catch (ParserConfigurationException | TransformerException e) {
            logger.error("Error saving players to file", e);
        }
        player.setCurrentGame(null);
        otherPlayer.setCurrentGame(null);
        synchronized (ServerMain.inGamePlayers) {
            ServerMain.inGamePlayers.remove(player);
            ServerMain.inGamePlayers.remove(otherPlayer);
        }
        synchronized (ServerMain.freePlayers) {
            ServerMain.freePlayers.add(otherPlayer);
            ServerMain.freePlayers.add(player);
        }
        synchronized (ServerMain.games) {
            ServerMain.games.remove(player.getCurrentGame());
        }
        for (Player p : ServerMain.freePlayers) {
            if (!player.equals(p)) {
                answer.add(p.getLogin());
                answer.add(String.valueOf(p.getRank()));
            }
            if (!otherPlayer.equals(p)) {
                out.add(p.getLogin());
                out.add(String.valueOf(p.getRank()));
            }
        }
        try {
            otherSender.send(out);
            sender.send(answer);
        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e) {
            logger.error("Error sending endgame message", e);
        }
    }

    public static void restoreLastMove(Player player, Player otherPlayer, Game game) {
        List<String> list = new ArrayList<>();
        list.add("restore");
        list.add(String.valueOf(game.getLastFigureMoved().getType()));

        if (wasCastling) {
            game.getLastFigureMoved().setCell(game.getLastFromCell());
            game.getLastFromCell().setFigure(game.getLastFigureMoved());

            if (game.getLastFigureTaken() != null) {
                game.getLastFigureTaken().setCell(game.getLastToCell());
            }
            game.getLastToCell().setFigure(game.getLastFigureTaken());
            game.getExLastFigureMoved().setCell(game.getExFromCell());
            game.getExFromCell().setFigure(game.getExLastFigureMoved());

            if (game.getExLastFigureTaken() != null) {
                game.getExLastFigureTaken().setCell(game.getExToCell());
            }
            game.getExToCell().setFigure(game.getExLastFigureTaken());

            if (game.getLastToCell().getX() == 6) {
                list.add("kingside");
            } else {
                list.add("queenside");
            }
        } else {
            game.getLastFigureMoved().setCell(game.getLastFromCell());
            game.getLastFromCell().setFigure(game.getLastFigureMoved());
            game.getLastToCell().setFigure(game.getLastFigureTaken());
            if (game.getLastFigureTaken() != null) {
                game.getLastFigureTaken().setCell(game.getLastToCell());
            }
        }
        try {
            player.getController().getSender().send(list);
            otherPlayer.getController().getSender().send(list);
        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e) {
            logger.error("Error sending message to one or both players ", e);
        }
        game.changeCurrentStep();
    }
}
