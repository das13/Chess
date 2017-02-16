package chess.model.figures;

import chess.Constants;
import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Bishop</code> figure.
 */
public class Bishop extends Figure {
    private List<Cell> validCells;

    public Bishop(Type type, Cell cell) {
        super(type, cell);
    }
    public Bishop() {

    }
    public boolean hasMove() {
        return allAccessibleMove().size() > 0;
    }

    public List<Cell> allAccessibleMove() {
        validCells = new ArrayList<Cell>();

        validCells.clear();
        if (getCell() == null) return validCells;
        Game game = getCell().getParentGame();

        int row = getCell().getX();
        int column = getCell().getY();

        //all possible moves in the down positive diagonal
        for (int j = column + 1, i = row + 1; j < Constants.BOARDSIZE && i < Constants.BOARDSIZE; j++, i++) {
            Cell cell = game.getCell(i, j);
            if (cell.getFigure() == null) {
                validCells.add(cell);
            } else if (cell.getFigure() != null && !cell.isFriendlyCell(this)) {
                validCells.add(cell);
                break;
            } else {
                break;
            }
        }
        //all possible moves in the up positive diagonal
        for (int j = column - 1, i = row + 1; j > -1 && i < Constants.BOARDSIZE; j--, i++) {
            Cell cell = game.getCell(i, j);
            if (cell.getFigure() == null) {
                validCells.add(cell);
            } else if (cell.getFigure() != null && !cell.isFriendlyCell(this)) {
                validCells.add(cell);
                break;
            } else {
                break;
            }
        }
        //all possible moves in the up negative diagonal
        for (int j = column - 1, i = row - 1; j > -1 && i > -1; j--, i--) {
            Cell cell = game.getCell(i, j);
            if (cell.getFigure() == null) {
                validCells.add(cell);
            } else if (cell.getFigure() != null && !cell.isFriendlyCell(this)) {
                validCells.add(cell);
                break;
            } else {
                break;
            }
        }
        //all possible moves in the down negative diagonal
        for (int j = column + 1, i = row - 1; j < Constants.BOARDSIZE && i > -1; j++, i--) {
            Cell cell = game.getCell(i, j);
            if (cell.getFigure() == null) {
                validCells.add(cell);
            } else if (cell.getFigure() != null && !cell.isFriendlyCell(this)) {
                validCells.add(cell);
                break;
            } else {
                break;
            }
        }
        for (int i = 0; i < validCells.size(); i++) {
            if (validCells.get(i).isFriendlyCell(this)) {
                validCells.remove(i);
            }
        }
        return validCells;
    }
}
