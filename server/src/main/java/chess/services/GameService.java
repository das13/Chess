package chess.services;

import chess.ServerMain;
import chess.controller.Controller;
import chess.exceptions.ReplacePawnException;
import chess.model.*;
import chess.model.figures.King;
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
 * <code>GameService</code> is service class with all methods which need for the game of chess.
 * There are method for a call player, all accessible steps, move figure and looks for a checkmate situation
 */
public class GameService {
    /**
     * <code>callPlayer</code> is method which a call player from a list with free players for a game
     * @param playerWhite is calling players object
     * @param nickName is called players name
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     */
    public static void callPlayer(Player playerWhite, String nickName) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        List<String> out = new ArrayList<String>();
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
            outList.addAll(PlayerService.refresh(playerWhite,  sender));
            sender.send(outList);
        }

    }

    /**
     *
     * @param thisPlayer
     * @param str
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     */
    public static void confirmGame(Player thisPlayer, List<String> str) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        List<String> out = new ArrayList<String>();
        out.add("confirmresponse");
        XMLSender otherSender = null;
        Game game = thisPlayer.getCurrentGame();
        if (str.get(1).equals("Ok")) {

                if (game.getBlackPlayer().equals(thisPlayer)) {
                    synchronized (ServerMain.waitingGames) {
                        ServerMain.waitingGames.remove(game);
                    }
                    synchronized (ServerMain.games) {
                        ServerMain.games.add(game);
                    }
                    Player otherPlayer = game.getOtherPlayer(thisPlayer);
                    Controller otherController = otherPlayer.getController();
                    otherSender = otherController.getSender();
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
            System.out.println("нет фигуры");
            throw new NullPointerException();
        }
    }

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
                game.setLastFigureTaken(game.getBoard()[x2][y2].getFigure());
                figure.move(game.getBoard()[x2][y2]);
                game.getBoard()[x2][y2].setFigure(figure);
                game.setLastFigureMoved(figure);
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
                                    tempFig.setCell(kingAccessibleCell);
                                    game.getKing(otherType).allAccessibleMove().remove(kingAccessibleCell);
                                }
                                else {
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

                                }
                                else {
                                    enemyFigure.setCell(startCell);
                                    startCell.setFigure(enemyFigure);

                                    cell.setFigure(tempFigure);
                                    if (tempFigure != null) tempFigure.setCell(cell);
                                    cell.setFigure(tempFigure);

                                    isCheckmate = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (isCheckmate) {
                        answer.add("checkmate");
                        out.add("checkmate");
                        answer.add("Ok");
                        out.add("Ok");
                        player.setRank(player.getRank() + 5);
                        otherPlayer.setRank(otherPlayer.getRank() - 5);
                        answer.add(String.valueOf(player.getId()));
                        answer.add(player.getLogin());
                        answer.add(player.getPassword());
                        answer.add(String.valueOf(player.getRank()));
                        out.add(String.valueOf(otherPlayer.getId()));
                        out.add(otherPlayer.getLogin());
                        out.add(otherPlayer.getPassword());
                        out.add(String.valueOf(otherPlayer.getRank()));
                        PlayerService.updatePlayer(player);
                        PlayerService.updatePlayer(otherPlayer);
                        XMLsaveLoad.savePlayers();
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
                            ServerMain.games.remove(game);
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

                        otherSender.send(out);
                        sender.send(answer);
                        return answer;

                    }
                }

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
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (TransformerConfigurationException e1) {
                e1.printStackTrace();
            }
            return answer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
