package chess.services;

import chess.ServerMain;
import chess.model.Cell;
import chess.model.Game;
import chess.model.Player;

import java.util.Iterator;

/**
 * Created by Admin on 20.01.2017.
 */
public class GameService {
    public static Player callPlayer(Player playerWhite, String nickName){
        for(Player playerBlack: ServerMain.freePlayers){
            if(playerBlack.getNickname().equals(nickName)){
                Game game = new Game(playerWhite, playerBlack);
                ServerMain.waitingGames.add(game);
                return playerBlack;
            }
        }
        return null;
    }
    public static Game confirmGame(Player blackPlayer, String confirm){
        if(confirm.equals("Ok")){
            Iterator<Game> iter = ServerMain.waitingGames.iterator();
            while (iter.hasNext()) {
                Game game = iter.next();
                if(game.getBlackPlayer().equals(blackPlayer)){
                    synchronized(ServerMain.waitingGames) {
                        iter.remove();
                    }
                    synchronized(ServerMain.games) {
                        ServerMain.games.add(game);
                    }
                    return game;
                }
            }
        }else{
            Iterator<Game> iter = ServerMain.waitingGames.iterator();
            while (iter.hasNext()) {
                Game game = iter.next();
                if(game.getBlackPlayer().equals(blackPlayer)){
                    synchronized(ServerMain.waitingGames) {
                        iter.remove();
                    }
                }
            }
        }
        return null;
    }
    public static int[] steps(Game game, int x, int y){
       Cell cell =  game.getBoard()[x][y];
        System.out.println(cell.getFigure().allAccessibleMove().size()*2);
       int[] array=new int[cell.getFigure().allAccessibleMove().size()*2];
       int i = 0;
       for(Cell c:cell.getFigure().allAccessibleMove()){
           array[i]=c.getX();
           i++;
           array[i]=c.getY();
           i++;
       }
       return array;
    }
}
