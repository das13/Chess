package chess.model;

import chess.Constants;
import chess.model.figures.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Game</code> is the base class for playing.
 */
public class Game extends Thread {
    private Player whitePlayer;
    private Player blackPlayer;
    private final King whiteKing;
    private final King blackKing;
    private Figure currentFigure;
    private Figure lastFigureMoved;
    private Figure exLastFigureMoved;
    private Figure lastFigureTaken = null;
    private Figure exLastFigureTaken;
    private Cell exFromCell;
    private Cell exToCell = null;
    private Cell lastFromCell;
    private Cell lastToCell = null;
    private Cell currentCell = null;
    private final Cell[][] board = new Cell[8][8];
    private final List<Figure> whiteFigures = new ArrayList<Figure>();
    private final List<Figure> blackFigures = new ArrayList<Figure>();
    private final List<Cell> allWhiteMoves = new ArrayList<Cell>();
    private final List<Cell> allBlackMoves = new ArrayList<Cell>();
    private Type currentStep = Type.WHITE;
    private final static Logger logger = Logger.getLogger(Game.class);

    /**
     * Creates instance of Game with two given players.
     * @param whitePlayer Player who initiated the game.
     * @param blackPlayer Player who accepted the offer to play.
     */
    public Game(Player whitePlayer, Player blackPlayer) {
        this.currentStep = Type.WHITE;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.blackPlayer.setCurrentGame(this);
        this.whitePlayer.setCurrentGame(this);

        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            for (int j = 0; j < Constants.BOARDSIZE; j++) {
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
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            board[i][1].setFigure(new Pawn(Type.BLACK, board[i][1]));
            board[i][6].setFigure(new Pawn(Type.WHITE, board[i][6]));
        }
        for (Cell [] cells: board) {
            for (Cell cell: cells) {
                if (cell.isFigure()) {
                    if (cell.getFigure().getType() == Type.WHITE) {
                        whiteFigures.add(cell.getFigure());
                    } else {
                        blackFigures.add(cell.getFigure());
                    }
                }
            }
        }
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
        if (currentStep == Type.WHITE) {
            setAllWhiteMoves();
            this.currentStep = Type.BLACK;
        } else {
            setAllBlackMoves();
            this.currentStep = Type.WHITE;
        }
    }

    public void setCurrentStep(Type type) {
        this.currentStep = type;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Player getOtherPlayer(Player player) {
        if (player.equals(whitePlayer)) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
    }

    public List<Figure> getFigures(Type type) {
        if (type == Type.WHITE) {
            return whiteFigures;
        } else {
            return blackFigures;
        }
    }

    /**
     * Returns true when player's king is under attack.
     * @param type Type of the player's figures.
     * @return true if attacked.
     */
    public boolean isPlayersKingAttacked(Type type) {
        if (type == Type.WHITE) {
            return allBlackMoves.contains(whiteKing.getCell());
        } else {
            return allWhiteMoves.contains(blackKing.getCell());
        }
    }

    public King getKing(Type type) {
        if (type == Type.WHITE) {
            return whiteKing;
        } else {
            return blackKing;
        }
    }

    public Figure getLastFigureMoved() {
        return lastFigureMoved;
    }

    public void setLastFigureMoved(Figure lastFigureMoved) {
        this.lastFigureMoved = lastFigureMoved;
    }

    public Figure getLastFigureTaken() {
        return lastFigureTaken;
    }

    public void setLastFigureTaken(Figure lastFigureTaken) {
        this.lastFigureTaken = lastFigureTaken;
    }

    public Figure getExLastFigureMoved() {
        return exLastFigureMoved;
    }

    public void setExLastFigureMoved(Figure exLastFigureMoved) {
        this.exLastFigureMoved = exLastFigureMoved;
    }

    public Figure getExLastFigureTaken() {
        return exLastFigureTaken;
    }

    public void setExLastFigureTaken(Figure exLastFigureTaken) {
        this.exLastFigureTaken = exLastFigureTaken;
    }

    public Cell getExToCell() {
        return exToCell;
    }

    public void setExToCell(Cell exToCell) {
        this.exToCell = exToCell;
    }

    public Cell getLastToCell() {
        return lastToCell;
    }

    public void setLastToCell(Cell lastToCell) {
        this.lastToCell = lastToCell;
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell currentCell) {
        this.currentCell = currentCell;
    }

    public Cell getExFromCell() {
        return exFromCell;
    }

    public void setExFromCell(Cell exFromCell) {
        this.exFromCell = exFromCell;
    }

    public Cell getLastFromCell() {
        return lastFromCell;
    }

    public void setLastFromCell(Cell lastFromCell) {
        this.lastFromCell = lastFromCell;
    }

    public Figure getCurrentFigure() {
        return currentFigure;
    }

    public void setCurrentFigure(Figure currentFigure) {
        this.currentFigure = currentFigure;
    }

    /**
     * Returns moves of opponent's figures.
     * @param type Type of current player figures.
     * @return List of Cells that can be taken by opponent.
     */
    public List<Cell> getEnemyMoves(Type type) {
        if (type == Type.WHITE) {
            return allBlackMoves;
        } else {
            return allWhiteMoves;
        }
    }

    /**
     * Sets up all white figures moves.
     */
    public void setAllWhiteMoves() {
        allWhiteMoves.clear();
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if ((cell.getFigure() != null) && cell.getFigure().getType() == Type.WHITE) {
                    allWhiteMoves.addAll(cell.getFigure().allAccessibleMove());
                }
            }
        }
    }
    /**
     * Sets up all black figures moves.
     */
    public void setAllBlackMoves() {
        allBlackMoves.clear();
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (cell.getFigure() != null && cell.getFigure().getType() == Type.BLACK) {
                    allBlackMoves.addAll(cell.getFigure().allAccessibleMove());
                }
            }
        }
    }
    public void replacePawn(String figureName, int x, int y){
        try {
            Figure figure = (Figure) Class.forName("chess.model.figures."+figureName).newInstance();
            figure.setCell(board[y][x]);
            figure.setType(board[y][x].getFigure().getType());
            board[y][x].setFigure(figure);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            logger.error("Error replacing pawn", e);
        }
    }
}


