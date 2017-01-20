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

        Game game = getCell().getParentGame();
        int y = getCell().getY() - 1;
        int y2 = getCell().getY() + 1;
        int y3 = getCell().getY() - 1;
        int y4 = getCell().getY() + 1;

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        for (int x = getCell().getX() - 1; x >= 0; x--) {
            if (x < 0) break;
            if (y >= 0) {
                validCells.add(game.getCell(x,y));
                y--;
                if (game.getCell(x,y+1).getFigure() != null) break;
            }
            if (y2 <= 7) {
                validCells.add(game.getCell(x,y2));
                y2++;
                if(game.getCell(x,y2-1).getFigure() != null) break;
            }
        }

        for (int x = getCell().getX() + 1; x <= 7; x++) {
            if(x > 7) break;
            if (y3 >= 0) {
                    validCells.add(game.getCell(x,y3));
                    y3--;
                if (game.getCell(x,y3+1).getFigure() != null) break;
            }
            if (y4 <= 7) {
                    validCells.add(game.getCell(x,y4));
                    y4++;
                if (game.getCell(x,y4-1).getFigure() != null) break;
            }
        }

        //all cells that have figures of the same Type as this are removed here
        for (int i = 0; i < validCells.size(); i++) {
            if (validCells.get(i).isFriendlyCell(this)) {
                validCells.remove(i);
            }
        }
        return validCells;
    }
}
