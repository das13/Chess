package chess.model.figures;

import chess.Constants;
import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viacheslav koshchii on 17.01.2017.
 */
public class Queen extends Figure {
    List<Cell> validCells;
    public Queen(Type type) {
        super(type);
    }
    public Queen() {

    }
    public Queen(Type type, Cell cell) {
        super(type, cell);
    }

    public void checkPath(List<Cell> validCells, int x, int y, int stepX, int stepY) {
        Game game = getCell().getParentGame();
        int i = x + stepX;
        int j = y + stepY;
        while (i < Constants.BOARDSIZE && j < Constants.BOARDSIZE && i >= 0 && j >= 0) {
            if (game.getCell(i, j) != null) {
                if (!getCell().isFriendlyCell(game.getCell(i, j).getFigure())) {
                    validCells.add(game.getCell(i, j));
                    if (game.getCell(i, j).isFigure()) {
                        //System.out.println(game.getCell(i, j).getFigure().getClass().getName());
                        break;
                    }
                }else{
                    break;
                }
            }
            i += stepX;
            j += stepY;
        }
    }

    public List<Cell> allAccessibleMove() {
        validCells = new ArrayList<Cell>();

        validCells.clear();
        if (getCell() == null) return validCells;
        //if(!getCell().getParentGame().getCurrentStep().equals(getType())) return validCells;
        //Game game = getCell().getParentGame();
        checkPath(validCells, getCell().getX(), getCell().getY(), 1, 0);
        checkPath(validCells, getCell().getX(), getCell().getY(), -1, 0);
        checkPath(validCells, getCell().getX(), getCell().getY(), 0, 1);
        checkPath(validCells, getCell().getX(), getCell().getY(), 0, -1);
        checkPath(validCells, getCell().getX(), getCell().getY(), 1, 1);
        checkPath(validCells, getCell().getX(), getCell().getY(), 1, -1);
        checkPath(validCells, getCell().getX(), getCell().getY(), -1, 1);
        checkPath(validCells, getCell().getX(), getCell().getY(), -1, -1);
        return validCells;
    }
}
