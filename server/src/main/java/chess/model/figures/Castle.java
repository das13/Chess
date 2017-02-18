package chess.model.figures;

import chess.Constants;
import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Castle</code> figure.
 */
public class Castle extends Figure {

    private List<Cell> validCells;

    public Castle(Type type, Cell cell) {
        super(type, cell);
    }
    public Castle() {
    }
    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {

        validCells = new ArrayList<>();

        validCells.clear();
        if (getCell() == null) return validCells;
        Game game = getCell().getParentGame();
        int a = getCell().getX();
        int b = getCell().getY();

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        for (int x = a - 1; x >= 0; x--) {
            if (x < 0) break;
            if (game.getCell(x, b).getFigure() == null) {
                validCells.add(game.getCell(x, b));
            } else if (!game.getCell(x, b).isFriendlyCell(this)) {
                validCells.add(game.getCell(x, b));
                break;
            } else {
                break;
            }
        }
        for (int x = a + 1; x <= Constants.BOARDSIZE-1; x++) {
            if (x > 7) break;
            if (game.getCell(x, b).getFigure() == null) {
                validCells.add(game.getCell(x, b));
            } else if (!game.getCell(x, b).isFriendlyCell(this)) {
                validCells.add(game.getCell(x, b));
                break;
            } else {
                break;
            }
        }
        for (int y = b - 1; y >= 0; y--) {
            if (y < 0) break;
            if (game.getCell(a, y).getFigure() == null) {
                validCells.add(game.getCell(a, y));
            } else if (!game.getCell(a, y).isFriendlyCell(this)) {
                validCells.add(game.getCell(a, y));
                break;
            } else {
                break;
            }
        }
        for (int y = b + 1; y <= Constants.BOARDSIZE-1; y++) {
            if (y > Constants.BOARDSIZE-1) break;
            if (game.getCell(a, y).getFigure() == null) {
                validCells.add(game.getCell(a, y));
            } else if (!game.getCell(a, y).isFriendlyCell(this)) {
                validCells.add(game.getCell(a, y));
                break;
            } else {
                break;
            }
        }
        return validCells;
    }
}
