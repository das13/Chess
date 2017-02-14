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
import java.util.Iterator;
import java.util.List;

/**
 * Created by viacheslav koshchii on 20.01.2017.
 */
public class GameService {
    public static void callPlayer(Player playerWhite, String nickName) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        List<String> out = new ArrayList<String>();
        Controller controller = playerWhite.getController();
        XMLSender sender = controller.getSender();
        for (Player playerBlack : ServerMain.freePlayers) {
            if (playerBlack.getLogin().equals(nickName)) {
                Game game = new Game(playerWhite, playerBlack);
                ServerMain.waitingGames.add(game);
                Controller otherController = playerBlack.getController();
                XMLSender otherSender = otherController.getSender();
                out.add("confirm");
                out.add(playerWhite.getLogin());
                otherSender.send(out);
                break;
            }
                List<String> outList = new ArrayList<String>();
                outList.add("notconfirm");
                outList.addAll(PlayerService.refresh(playerWhite,  sender));
                sender.send(outList);

        }

    }

    public static void confirmGame(Player thisPlayer, List<String> str) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        List<String> out = new ArrayList<String>();
        out.add("confirmresponse");
        XMLSender otherSender = null;
        if (str.get(1).equals("Ok")) {
            Iterator<Game> iter = ServerMain.waitingGames.iterator();
            while (iter.hasNext()) {
                Game game = iter.next();
                if (game.getBlackPlayer().equals(thisPlayer)) {
                    synchronized (ServerMain.waitingGames) {
                        iter.remove();
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
            }
        } else {
            out.add("No");
            Iterator<Game> iter = ServerMain.waitingGames.iterator();
            while (iter.hasNext()) {
                Game game = iter.next();
                if (game.getBlackPlayer().equals(thisPlayer)) {
                    synchronized (ServerMain.waitingGames) {
                        iter.remove();
                    }

                }
            }

        }
        otherSender.send(out);
    }

    public static List<String> steps(Game game, int x, int y) {
        Cell cell = game.getCell(x, y);
        //System.out.println(cell.getFigure());
        //System.out.println(cell.getFigure().allAccessibleMove().size() * 2);
        /*if (game.getCurrentStep() != game.getBoard()[x][y].getFigure().getType()) {
            throw new RivalFigureException();
        }*/
        if (cell.isFigure()) {
            List<String> array = new ArrayList<String>();
            array.add("steps");
//            System.out.println(game.getCurrentStep());
//            System.out.println(game.getBoard()[x][y].getFigure().getType());
            if (game.getCurrentStep() != game.getBoard()[x][y].getFigure().getType()) {
                return array;
            }
            int i = 0;
            for (Cell c : cell.getFigure().allAccessibleMove()) {
                array.add(c.getY() + "" + c.getX());
            }
            //`System.out.println(array.size());
            return array;
        } else {
            System.out.println("нет фигуры");
            throw new NullPointerException();
        }
    }

    public static void move(Game game, List<String> str, Player player) {
        List<String> answer = new ArrayList<String>();
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
            Type type = figure.getType(); // ЦВЕТ ФИГУРЫ КОТОРОЙ ХОДИМ
            Type otherType = null;        // ЦВЕТ ФИГУРЫ СОПЕРНИКА
            if (type == Type.WHITE) {
                otherType = Type.BLACK;
            } else {
                otherType = Type.WHITE;
            }

            // ЕСЛИ ПЫТАЕМСЯ ПОЙТИ НА НЕРАЗРЕШЕННУЮ КЛЕТКУ
            if (!figure.allAccessibleMove().contains(game.getBoard()[x2][y2])) {
                answer.add("cancel");
                sender.send(answer);
            }
            // В ОСТАЛЬНЫХ СЛУЧАЯХ
            else {

                // ПЕРЕМЕЩАЕМ ФИГУРУ
                game.setLastFigureTaken(game.getBoard()[x2][y2].getFigure());
                figure.move(game.getBoard()[x2][y2]);
                game.getBoard()[x2][y2].setFigure(figure);
                game.setLastFigureMoved(figure);
                if (game.getLastFigureTaken() != null) {
                    game.getLastFigureTaken().setCell(null);
                }
                //ЕСЛИ РОКИРОВКА, ТО ФОРМИРУЕМ ОСОБОЕ СООБЩЕНИЕ
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
                }

                game.setAllWhiteMoves();
                game.setAllBlackMoves();

                // ЕСЛИ ОКАЗЫВАЕТСЯ, ЧТО КОРОЛЬ ИГРОКА ОТКРЫВАЕТСЯ ДЛЯ АТАКИ, ОТМЕНЯЕМ ХОД
                if (game.isPlayersKingAttacked(type)) {
                    game.getBoard()[x1][y1].setFigure(game.getLastFigureMoved());
                    game.getLastFigureMoved().setCell(game.getBoard()[x1][y1]);
                    game.getLastFigureMoved().setFirstMove(isFirstMove);
                    game.getBoard()[x2][y2].setFigure(game.getLastFigureTaken());

                    if (game.getLastFigureTaken() != null) {
                        game.getLastFigureTaken().setCell(game.getBoard()[x2][y2]);
                    }
                    answer.add("cancel");
                    sender.send(answer);
                }

                // ЕСЛИ КОРОЛЬ СОПЕРНИКА АТАКОВАН, ПРОВЕРЯЕМ, ЕСТЬ ЛИ МАТ
                else if (game.isPlayersKingAttacked(otherType)) {
                    isCheckmate = true; // ПО УМОЛЧАНИЮ СЧИТАЕМ, ЧТО РАЗ НАПАЛИ НА КОРОЛЯ, ТО ДА
                    // ПРОВЕРЯЕМ ВСЕ ФИГУРЫ СОПЕРНИКА, МОГУТ ЛИ ОНИ УБРАТЬ ШАХ СВОИМИ ХОДАМИ
                    for (Figure enemyFigure : game.getFigures(otherType)) {
                        if (!isCheckmate) break;
                        // ЕСЛИ ДАННАЯ ФИГУРА ОКАЗАЛАСЬ КОРОЛЕМ, ПРОВОДИМ ОТДЕЛЬНУЮ ПРОВЕРКУ
                        if (enemyFigure instanceof King) {
                            if (!isCheckmate) break;
                            List<Cell> countingList = enemyFigure.allAccessibleMove();
                            for (Cell kingAccessibleCell : countingList) {
                                Figure tempFig = null;
                                if (kingAccessibleCell.isFigure()) {
                                    tempFig = kingAccessibleCell.getFigure();
                                    tempFig.setCell(null);
                                }
                                // УБИРАЕМ ФИГУРУ ИЗ КЛЕТКИ, ЧТОБ КЛЕТКА МОГЛА ПОПАСТЬ В ВОЗМОЖНЫЕ ХОДЫ СОПЕРНИКА (ТЕКУЩЕГО ИГРОКА)
                                kingAccessibleCell.setFigure(null);
                                synchronized (GameService.class) {
                                    game.getEnemyMoves(otherType);
                                }
                                // ПЕРЕСЧИТВЫАЕМ ХОДЫ
                                game.setAllBlackMoves();
                                game.setAllWhiteMoves();

                                // ЕСЛИ КЛЕТКА ПОД АТАКОЙ ПРОТИВНИКА (ТЕКУЩЕГО ИГРОКА) УБИРАЕМ КЛЕТКУ ИЗ СПИСКА КОРОЛЯ
                                if (game.getEnemyMoves(otherType).contains(kingAccessibleCell)) {
                                    kingAccessibleCell.setFigure(tempFig);
                                    tempFig.setCell(kingAccessibleCell);
                                    game.getKing(otherType).allAccessibleMove().remove(kingAccessibleCell);
                                }
                                // В ОСТАЛЬНЫХ СЛУЧАЯХ ПРЕРЫВАЕМ ЦИКЛ
                                else {
                                    isCheckmate = false;
                                    break;
                                }
                            }
                        } else {
                            Cell startCell = enemyFigure.getCell();
                            // СТАВИМ ФИГУРУ НА КАЖДУЮ КЛЕТКУ
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

                                //ЕСЛИ ХОД НЕ СПАСАЕТ КОРОЛЯ, ВОЗВРАЩАЕМ ФИГУРУ ОБРАТНО
                                if (game.isPlayersKingAttacked(otherType)) {
                                    enemyFigure.setCell(startCell);
                                    startCell.setFigure(enemyFigure);
                                    cell.setFigure(tempFigure);

                                }
                                // ЕСЛИ СПАСАЕТ, ОТМЕНЯЕМ МАТ И ПРЕРЫВАЕМ ЦИКЛ
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
                    // В СЛУЧАЕ МАТА ПОНИЖАЕМ/ПОВЫШАЕМ РЕЙТИНГИ ИГРОКОВ И РАССЫЛАЕМ
                    if (isCheckmate) {
                        answer.add("checkmate");
                        answer.add(String.valueOf(type));
                        player.setRank(player.getRank() + 5);
                        otherPlayer.setRank(otherPlayer.getRank() - 5);
                        answer.add(player.getLogin());
                        answer.add(String.valueOf(player.getRank()));
                        answer.add(otherPlayer.getLogin());
                        answer.add(String.valueOf(otherPlayer.getRank()));
                        try {
                            XMLsaveLoad.savePlayers();
                        } catch (ParserConfigurationException | FileNotFoundException | TransformerException e) {
                            e.printStackTrace();
                        }
                        otherSender.send(answer);
                        sender.send(answer);
                    }
                }

                // ВО ВСЕХ ДРУГИХ СЛУЧАЯХ ЗАВЕРШАЕМ ХОД КАК ОБЫЧНО
                game.setCurrentStep(otherType);
                answer.add("moving");
                out.add("rivalMove");
                out.add(str.get(1));
                out.add(str.get(2));
                out.add(str.get(3));
                out.add(str.get(4));
                out.add(str.get(5));
                otherSender.send(out);
            }
        } catch (ReplacePawnException e) {
            answer.add("replacePawn");
            answer.add(String.valueOf(y1));
            answer.add(String.valueOf(x1));
            answer.add(y2 + "" + x2);
        } catch (TransformerConfigurationException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }
}
