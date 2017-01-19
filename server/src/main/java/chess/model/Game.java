package chess.model;

import chess.model.figures.*;

/**
 * Created by Admin on 17.01.2017.
 */
public class Game {
    private Player whitePlayer;
    private Player blackPlayer;
    private Cell[][] board = new Cell[8][8];
    public Game(Player whitePlayer, Player blackPlayer){
        this.whitePlayer=whitePlayer;
        this.blackPlayer=blackPlayer;
        for(int i=0; i<7; i++) {
            for(int j=0; j<7; j++) {
                board[0][0] = new Cell(0, 0, this);
            }
        }
        board[0][0].setFigure(new Castle(Type.WHITE, board[0][0]));
        board[1][0].setFigure(new Knight(Type.WHITE, board[1][0]));
        board[2][0].setFigure(new Bishop(Type.WHITE, board[2][0]));
        board[3][0].setFigure(new King(Type.WHITE, board[3][0]));
        board[4][0].setFigure(new Queen(Type.WHITE, board[4][0]));
        board[5][0].setFigure(new Bishop(Type.WHITE, board[5][0]));
        board[6][0].setFigure(new Knight(Type.WHITE, board[6][0]));
        board[7][0].setFigure(new Castle(Type.WHITE, board[7][0]));
        board[0][7].setFigure(new Castle(Type.BLACK, board[0][7]));
        board[1][7].setFigure(new Knight(Type.BLACK, board[1][7]));
        board[2][7].setFigure(new Bishop(Type.BLACK, board[2][7]));
        board[3][7].setFigure(new King(Type.BLACK, board[3][7]));
        board[4][7].setFigure(new Queen(Type.BLACK, board[4][7]));
        board[5][7].setFigure(new Bishop(Type.BLACK, board[5][7]));
        board[6][7].setFigure(new Knight(Type.BLACK, board[6][7]));
        board[7][7].setFigure(new Castle(Type.BLACK, board[7][7]));
        for(int i=0; i<8; i++){
            board[i][1].setFigure(new Pawn(Type.WHITE, board[i][1]));
            board[i][6].setFigure(new Pawn(Type.BLACK, board[i][6]));
        }
    }
    public Cell getCell(int x, int y) {
        Cell cell=null;
        try {
            cell = board[x][y];
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("This cell is not on the board");
        }
        return cell;
    }

    public Cell[][] getBoard() {
        return board;
    }
}
