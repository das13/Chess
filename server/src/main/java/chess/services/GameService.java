package chess.services;

import chess.ServerMain;
import chess.exceptions.ReplacePawnException;
import chess.exceptions.RivalFigureException;
import chess.model.*;
import chess.model.figures.King;
import chess.services.xmlService.XMLsaveLoad;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
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
                array.add(c.getY() + "" + c.getX());
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
        array[0] = x1;
        int y1 = Integer.parseInt(str.get(2));
        array[1] = y1;
        //out.println("enter coordinates of your step - x and y");
        int x2 = Integer.parseInt(str.get(3));
        array[2] = x2;
        int y2 = Integer.parseInt(str.get(4));
        array[3] = y2;

        boolean isCheckmate = false;

        if (game.getBoard()[x1][y1].isFigure()) {
            try {
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
                    System.out.println("CANCEL MOVE");
                    answer.add("cancel");
                    return answer;
                }
                // В ОСТАЛЬНЫХ СЛУЧАЯХ
                else {
                    System.out.println("begin" + x2 + " " + y2);

                    // ПЕРЕМЕЩАЕМ ФИГУРУ
                    game.setLastFigureTaken(game.getBoard()[x2][y2].getFigure());
                    figure.move(game.getBoard()[x2][y2]);
                    game.getBoard()[x2][y2].setFigure(figure);
                    game.setLastFigureMoved(figure);
                    if (game.getLastFigureTaken() != null) {
                        game.getLastFigureTaken().setCell(null);
                    }

                    game.setAllWhiteMoves();
                    game.setAllBlackMoves();

                    // ЕСЛИ ОКАЗЫВАЕТСЯ, ЧТО КОРОЛЬ ИГРОКА ОТКРЫВАЕТСЯ ДЛЯ АТАКИ, ОТМЕНЯЕМ ХОД
                    if (game.isPlayersKingAttacked(type)) {
                        System.out.println(type + " KING IS ATTACKED");

                        game.getBoard()[x1][y1].setFigure(game.getLastFigureMoved());
                        game.getLastFigureMoved().setCell(game.getBoard()[x1][y1]);
                        game.getLastFigureMoved().setFirstMove(isFirstMove);
                        game.getBoard()[x2][y2].setFigure(game.getLastFigureTaken());

                        if (game.getLastFigureTaken() != null) {
                            game.getLastFigureTaken().setCell(game.getBoard()[x2][y2]);
                        }
                        answer.add("cancel");
                        return answer;
                    }


                    // ЕСЛИ КОРОЛЬ СОПЕРНИКА АТАКОВАН, ПРОВЕРЯЕМ, ЕСТЬ ЛИ МАТ
                    else if (game.isPlayersKingAttacked(otherType)) {

                        System.out.println(otherType + " KING IS ATTACKED");

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
//                                    // ПЕРЕСЧИТЫВАЕМ ХОДЫ КОРОЛЯ
//                                    game.getKing(otherType).allAccessibleMove();

                                    // ПЕРЕСЧИТВЫАЕМ ХОДЫ ТЕКУЩЕГО ИГРОКА
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
                            System.out.println("CHECKMATE");
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
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (TransformerException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            return answer;
                        }
                    }

                    // ВО ВСЕХ ДРУГИХ СЛУЧАЯХ ЗАВЕРШАЕМ ХОД КАК ОБЫЧНО
                    System.out.println("done" + x2 + " " + y2);
                    game.setCurrentStep(type);
                    System.out.println("CURRENT STEP " + game.getCurrentStep());
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
