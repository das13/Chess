package chess.model.figures;

import chess.exceptions.ReplacePawnException;
import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>King</code> figure.
 */
public class King extends Figure {
    private Game game;

    public King(Type type, Cell cell) {
        super(type, cell);
    }
    public King() {}
    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {
        List<Cell> validCells = new ArrayList<>();
        game = getCell().getParentGame();
        int a = getCell().getX();
        int b = getCell().getY();

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        validCells.clear();
        if (isFirstMove()
                && !game.isPlayersKingAttacked(this.getType())
                && game.getCell(1, b).getFigure() == null
                && !game.getEnemyMoves(this.getType()).contains(game.getCell(1, b))
                && game.getCell(2, b).getFigure() == null
                && !game.getEnemyMoves(this.getType()).contains(game.getCell(2, b))
                && game.getCell(3, b).getFigure() == null
                && !game.getEnemyMoves(this.getType()).contains(game.getCell(3, b))) {
            validCells.add(game.getCell(2, b));
        }
        if (isFirstMove()
                && !game.isPlayersKingAttacked(this.getType())
                && game.getCell(5, b).getFigure() == null
                && !game.getEnemyMoves(this.getType()).contains(game.getCell(5, b))
                && game.getCell(6, b).getFigure() == null
                && !game.getEnemyMoves(this.getType()).contains(game.getCell(5, b))) {
            validCells.add(game.getCell(6, b));
        }
        for (int x = a - 1; x <= a + 1; x++) {
            for (int y = b - 1; y <= b + 1; y++) {
                if (x < 0 || x > 7) {
                    continue;
                }
                if (y < 0 || y > 7) {
                    continue;
                }
                if (game.getCell(x, y).getFigure() == this) {
                    continue;
                }
                if (game.getCell(x, y).isFriendlyCell(this)) {
                    continue;
                }
                if (game.getEnemyMoves(this.getType()).contains(game.getCell(x, y))) {
                    continue;
                }
                validCells.add(game.getCell(x, y));
            }
        }
        return validCells;
    }


    @Override
    public void move(Cell destination) throws ReplacePawnException {
        if (allAccessibleMove().contains(destination) && castlingKingSideAllowed()
                && (destination.getX() - getCell().getX() == 2)) {
            castlingKingside();
            setFirstMove(false);
        } else if (allAccessibleMove().contains(destination) && castlingQueenSideAllowed()
                && (getCell().getX() - destination.getX() == 2)) {
            castlingQueenside();
            setFirstMove(false);
        } else {
            this.getCell().setFigure(null);
            this.setCell(destination);
            this.getCell().setFigure(this);
            setFirstMove(false);
        }
    }

    private boolean castlingKingSideAllowed() {
        //if castle is not on place
        if (game.getCell(7, this.getCell().getY()).getFigure() == null) return false;
        // if castle moved
        if (!game.getCell(7, this.getCell().getY()).getFigure().isFirstMove()) return false;
        return true;
    }


    private boolean castlingQueenSideAllowed() {
        //if castle is not on place
        if (game.getCell(0, this.getCell().getY()).getFigure() == null) return false;
        // if castle moved
        if (!game.getCell(0, this.getCell().getY()).getFigure().isFirstMove()) return false;
        return true;
    }

    private void castlingKingside() {
        try {
            Castle castle = (Castle) game.getCell(7, this.getCell().getY()).getFigure();
            castle.getCell().setFigure(null);
            castle.setCell(game.getCell(5, this.getCell().getY()));
            game.getCell(5, this.getCell().getY()).setFigure(castle);
            castle.setFirstMove(false);
            this.move(game.getCell(6, this.getCell().getY()));
        } catch (ReplacePawnException e) {
            //ignore
        }
    }

    private void castlingQueenside() {
        try {
            Castle castle = (Castle) game.getCell(0, this.getCell().getY()).getFigure();
            castle.getCell().setFigure(null);
            castle.setCell(game.getCell(3, this.getCell().getY()));
            game.getCell(3, this.getCell().getY()).setFigure(castle);
            castle.setFirstMove(false);
            this.move(game.getCell(2, this.getCell().getY()));
        } catch (ReplacePawnException e) {
            //ignore
        }
    }
}
