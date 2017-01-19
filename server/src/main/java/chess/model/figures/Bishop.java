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
public class Bishop extends Figure {
    public Bishop(Type type) {
        super(type);
    }
    public Bishop(Type type, Cell cell) {
        super(type, cell);
    }

    public boolean hasMove() {
        return allAccessibleMove().size() > 0;
    }

    public List<Cell> allAccessibleMove() {
        List<Cell> validCells = new ArrayList<Cell>();

        int y = cell.getY() - 1;
        int y2 = cell.getY() + 1;
        int y3 = cell.getY() - 1;
        int y4 = cell.getY() + 1;

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        for (int x = cell.getX() - 1; x >= 0; x--) {
            if (y >= 0) {
                do {
                    validCells.add(Game.getCell(x, y));
                    y--;
                }
                while (Game.getCell(x, y + 1).getFigure() == null);
            }
            if (y2 <= 7) {
                do {
                    validCells.add(Game.getCell(x, y2));
                    y2++;
                } while (Game.getCell(x, y2 - 1).getFigure() == null);
            }
        }
        for (int x = cell.getX() + 1; x <= 7; x++) {
            if (y3 > 0) {
                do {
                    validCells.add(Game.getCell(x, y3));
                    y3--;
                } while (Game.getCell(x, y3 + 1).getFigure() == null);
            }
            if (y4 <= 7) {
                do {
                    validCells.add(Game.getCell(x, y4));
                    y4++;
                } while (Game.getCell(x, y4).getFigure() == null);
            }
        }

        /* all cells that have figures of the same Type as this are removed here*/
        for (int i = 0; i < validCells.size(); i++) {
            if (validCells.get(i).isFriendlyCell(this)) {
                validCells.remove(i);
            }
        }
        return validCells;
    }
}
