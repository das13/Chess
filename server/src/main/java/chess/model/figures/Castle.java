package chess.model.figures;

import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viacheslav koshchii on 17.01.2017.
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
        validCells.clear();
        if(!getCell().getParentGame().getCurrentStep().equals(getType())) return validCells;
        Game game = getCell().getParentGame();
        int a = getCell().getX();
        int b = getCell().getY();

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        for (int x = a - 1; x >= 0; x--) {
            if (x < 0) break;
            if (game.getCell(x, b).getFigure() == null) {
                validCells.add(game.getCell(x, b));
                continue;
            } else if (!game.getCell(x, b).isFriendlyCell(this)) {
                validCells.add(game.getCell(x, b));
                break;
            } else {
                break;
            }
        }
        for (int x = a + 1; x <= 7; x++) {
            if (x > 7) break;
            if (game.getCell(x, b).getFigure() == null) {
                validCells.add(game.getCell(x, b));
                continue;
            } else if (!game.getCell(x, b).isFriendlyCell(this)) {
                validCells.add(game.getCell(x, b));
                break;
            } else {
                break;
            }
        }
        for (int y = b - 1; y >= 0; y--) {
            if(y < 0) break;
            if (game.getCell(a, y).getFigure() == null) {
                validCells.add(game.getCell(a, y));
                continue;
            } else if (!game.getCell(a, y).isFriendlyCell(this)) {
                validCells.add(game.getCell(a, y));
                break;
            } else {
                break;
            }
        }
        for (int y = b + 1; y <= 7; y++) {
            if (y > 7) break;
            if (game.getCell(a, y).getFigure() == null) {
                validCells.add(game.getCell(a, y));
                continue;
            } else if (!game.getCell(a, y).isFriendlyCell(this)) {
                validCells.add(game.getCell(a, y));
                break;
            } else {
                break;
            }
        }

        //all cells that have figures of the same Type as this are removed here
//        for (int i = 0; i < validCells.size(); i++) {
//            if (validCells.get(i).getFigure() != null && validCells.get(i).isFriendlyCell(this)) {
//                validCells.remove(i);
//            }
//        }
        return validCells;
    }
}
