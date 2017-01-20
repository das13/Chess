package chess.model.figures;

import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 17.01.2017.
 */
public class Castle extends Figure {
    public Castle(Type type) {
        super(type);
    }

    public Castle(Type type, Cell cell) {
        super(type, cell);
    }

    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {

        List<Cell> validCells = new ArrayList<Cell>();

        Game game = getCell().getParentGame();
        int a = getCell().getX();
        int b = getCell().getY();

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        for (int x = a - 1; x >= 0; x--) {
            if (x < 0) break;
            validCells.add(game.getCell(x, b));
            if (game.getCell(x, b).getFigure() != null) break;
        }
        for (int x = a + 1; x <= 7; x++) {
            if (x > 7) break;
            validCells.add(game.getCell(x, b));
            if (game.getCell(x, b).getFigure() != null) break;
        }
        for (int y = b - 1; y >= 0; y--) {
            if(y < 0) break;
            validCells.add(game.getCell(a, y));
            if (game.getCell(a, y).getFigure() != null) break;
        }
        for (int y = b + 1; y <= 7; y++) {
            if (y > 7) break;
            validCells.add(game.getCell(a, y));
            if (game.getCell(a, y).getFigure() != null) break;
        }

        //all cells that have figures of the same Type as this are removed here
        for (int i = 0; i < validCells.size(); i++) {
            if (validCells.get(i).getFigure() != null && validCells.get(i).isFriendlyCell(this)) {
                validCells.remove(i);
            }
        }
        return validCells;
    }
}
