package chess.model;

import chess.model.figures.*;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by viacheslav koshchii on 17.01.2017.
 */
public class Game extends Thread{
    private Player whitePlayer;
    private Player blackPlayer;
    private King whiteKing;
    private King blackKing;
    private Cell[][] board = new Cell[8][8];
    private Set<Cell> allWhiteMoves = new TreeSet<Cell>(new Comparator<Cell>() {
        public int compare(Cell c1, Cell c2){
            if (c1 == c2) return 1;
            if (c1.getX() != c2.getX()) return c1.getX() - c2.getX();
            else return c1.getY() - c2.getY();
        }
    });
    private Set<Cell> allBlackMoves = new TreeSet<Cell>(new Comparator<Cell>() {
        public int compare(Cell c1, Cell c2){
            if (c1 == c2) return 1;
            if (c1.getX() != c2.getX()) return c1.getX() - c2.getX();
            else return c1.getY() - c2.getY();
        }
    });
    private Type currentStep = Type.WHITE;

    public Game(Player whitePlayer, Player blackPlayer){
        this.currentStep = Type.WHITE;
        this.whitePlayer=whitePlayer;
        this.blackPlayer=blackPlayer;
        this.blackPlayer.setCurrentGame(this);
        this.whitePlayer.setCurrentGame(this);

        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                board[i][j] = new Cell(i, j, this);
            }
        }
        blackKing = new King(Type.BLACK, board[4][0]);
        whiteKing = new King(Type.WHITE, board[4][7]);
        board[0][0].setFigure(new Castle(Type.BLACK, board[0][0]));
        board[1][0].setFigure(new Knight(Type.BLACK, board[1][0]));
        board[2][0].setFigure(new Bishop(Type.BLACK, board[2][0]));
        board[3][0].setFigure(new Queen(Type.BLACK, board[3][0]));
        board[4][0].setFigure(blackKing);
        board[5][0].setFigure(new Bishop(Type.BLACK, board[5][0]));
        board[6][0].setFigure(new Knight(Type.BLACK, board[6][0]));
        board[7][0].setFigure(new Castle(Type.BLACK, board[7][0]));
        board[0][7].setFigure(new Castle(Type.WHITE, board[0][7]));
        board[1][7].setFigure(new Knight(Type.WHITE, board[1][7]));
        board[2][7].setFigure(new Bishop(Type.WHITE, board[2][7]));
        board[3][7].setFigure(new Queen(Type.WHITE, board[3][7]));
        board[4][7].setFigure(whiteKing);
        board[5][7].setFigure(new Bishop(Type.WHITE, board[5][7]));
        board[6][7].setFigure(new Knight(Type.WHITE, board[6][7]));
        board[7][7].setFigure(new Castle(Type.WHITE, board[7][7]));
        for(int i=0; i<8; i++){
            board[i][1].setFigure(new Pawn(Type.BLACK, board[i][1]));
            board[i][6].setFigure(new Pawn(Type.WHITE, board[i][6]));
        }
        setAllWhiteMoves();
        setAllBlackMoves();
    }

    public Game() {
        this.currentStep = Type.WHITE;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j, this);
            }
        }
        blackKing = new King(Type.BLACK, board[4][0]);
        whiteKing = new King(Type.WHITE, board[4][7]);
        board[0][0].setFigure(new Castle(Type.BLACK, board[0][0]));
        board[1][0].setFigure(new Knight(Type.BLACK, board[1][0]));
        board[2][0].setFigure(new Bishop(Type.BLACK, board[2][0]));
        board[3][0].setFigure(new Queen(Type.BLACK, board[3][0]));
        board[4][0].setFigure(blackKing);
        board[5][0].setFigure(new Bishop(Type.BLACK, board[5][0]));
        board[6][0].setFigure(new Knight(Type.BLACK, board[6][0]));
        board[7][0].setFigure(new Castle(Type.BLACK, board[7][0]));
        board[0][7].setFigure(new Castle(Type.WHITE, board[0][7]));
        board[1][7].setFigure(new Knight(Type.WHITE, board[1][7]));
        board[2][7].setFigure(new Bishop(Type.WHITE, board[2][7]));
        board[3][7].setFigure(new Queen(Type.WHITE, board[3][7]));
        board[4][7].setFigure(whiteKing);
        board[5][7].setFigure(new Bishop(Type.WHITE, board[5][7]));
        board[6][7].setFigure(new Knight(Type.WHITE, board[6][7]));
        board[7][7].setFigure(new Castle(Type.WHITE, board[7][7]));
        for (int i = 0; i < 8; i++) {
            board[i][1].setFigure(new Pawn(Type.BLACK, board[i][1]));
            //board[1][2].setFigure(new Pawn(Type.BLACK, board[0][2]));
            //board[0][6].setFigure(new Pawn(Type.WHITE, board[0][6]));
            board[i][6].setFigure(new Pawn(Type.WHITE, board[i][6]));
        }
//        for (Cell[] cells: board) {
//            for (Cell cell: cells) {
//                System.out.print(cell.getX() + "." + cell.getY() + " ");
//            }
//            System.out.println();
//        }
        setAllWhiteMoves();
        setAllBlackMoves();

    }
    public Cell getCell(int x, int y) {
        return board[x][y];
    }

    public Type getCurrentStep() {
        return currentStep;
    }

    public void changeCurrentStep() {
        if(currentStep == Type.WHITE) {
            setAllWhiteMoves();
            this.currentStep = Type.BLACK;
        }else{
            setAllBlackMoves();
            this.currentStep = Type.WHITE;
        }
    }

    public Cell[][] getBoard() {
        return board;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public Player getOtherPlayer(Player player){
        if(player.equals(whitePlayer)){
            return blackPlayer;
        }else{
            return whitePlayer;
        }
    }

    // возвращает true если белый король под шахом
    public boolean isWhiteKingAttacked() {
        return allBlackMoves.contains(whiteKing.getCell());
    }

    // возвращает true если черный король под шахом
    public boolean isBlackKingAttacked() {
        return allWhiteMoves.contains(blackKing.getCell());
    }

    // возвращает множество потенциальных ходов соперника
    public Set<Cell> getEnemyMoves(Type type) {
        if (type == Type.WHITE) {
            return allBlackMoves;
        } else {
            return allWhiteMoves;
        }
    }

    // составляет множество всех потенциальных ходов игрока белыми
    public void setAllWhiteMoves() {
        allWhiteMoves.clear();
        for (Cell[] cells: board) {
            for (Cell cell: cells) {
                if ((cell.getFigure() != null) && cell.getFigure().getType() == Type.WHITE) {
                    allWhiteMoves.addAll(cell.getFigure().allAccessibleMove());
                }
            }
        }
    }

    // составляет множество всех потенциальных ходов игрока черными
    public void setAllBlackMoves() {
        allBlackMoves.clear();
        for (Cell[] cells: board) {
            for (Cell cell: cells) {
                if (cell.getFigure() != null && cell.getFigure().getType() == Type.BLACK) {
                    allBlackMoves.addAll(cell.getFigure().allAccessibleMove());
                }
            }
        }
    }
}
