package chess.model;

import chess.model.figures.*;

/**
 * Created by Admin on 17.01.2017.
 */
public class Game {
    private Player whitePlayer;
    private Player blackPlayer;
    private static Cell[][] board = new Cell[8][8];
    public Game(){
        board[0][0]= new Cell(new Castle(Type.WHITE),0, 0);
        board[1][0]= new Cell(new Knight(Type.WHITE),1, 0);
        board[2][0]= new Cell(new Bishop(Type.WHITE),2, 0);
        board[3][0]= new Cell(new King(Type.WHITE),3, 0);
        board[4][0]= new Cell(new Queen(Type.WHITE),4, 0);
        board[5][0]= new Cell(new Bishop(Type.WHITE),5, 0);
        board[6][0]= new Cell(new Knight(Type.WHITE),6, 0);
        board[7][0]= new Cell(new Castle(Type.WHITE),7, 0);
        board[0][7]= new Cell(new Castle(Type.BLACK),0, 7);
        board[1][7]= new Cell(new Knight(Type.BLACK),1, 7);
        board[2][7]= new Cell(new Bishop(Type.BLACK),2, 7);
        board[3][7]= new Cell(new King(Type.BLACK),3, 7);
        board[4][7]= new Cell(new Queen(Type.BLACK),4, 7);
        board[5][7]= new Cell(new Bishop(Type.BLACK),5, 7);
        board[6][7]= new Cell(new Knight(Type.BLACK),6, 7);
        board[7][7]= new Cell(new Castle(Type.BLACK),7, 7);
        for(int i=0; i<8; i++){
            board[i][1]= new Cell(new Pawn(Type.WHITE),i, 1);
            board[i][6]= new Cell(new Pawn(Type.BLACK),i, 6);
        }
        for(int i = 0; i<8; i++){
            for(int j=2;j<6; j++){
                board[i][j]=new Cell(null,i, j);
            }
        }
    }
    public static Cell getCell(int x, int y) {
        return board[x][y];
    }


}
