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
public class Knight extends Figure {
    public Knight(Type type) {
        super(type);
    }

    public Knight(Type type, Cell cell) {
        super(type, cell);
    }

    public List<Cell> allAccessibleMove() throws ArrayIndexOutOfBoundsException{
        List<Cell> validCells = new ArrayList<Cell>();
        Game game = getCell().getParentGame();
        validCells.add(game.getCell(getCell().getX() + 2, getCell().getY() + 1));
        validCells.add(game.getCell(getCell().getX() + 2, getCell().getY() - 1));
        validCells.add(game.getCell(getCell().getX() - 2, getCell().getY() + 1));
        validCells.add(game.getCell(getCell().getX() - 2, getCell().getY() - 1));
        validCells.add(game.getCell(getCell().getX() + 1, getCell().getY() + 2));
        validCells.add(game.getCell(getCell().getX() + 1, getCell().getY() - 2));
        validCells.add(game.getCell(getCell().getX() - 1, getCell().getY() + 2));
        validCells.add(game.getCell(getCell().getX() - 1, getCell().getY() - 2));
        for (int i = 0; i < validCells.size(); i++) {
            if (validCells.get(i)==null || getCell().isFriendlyCell(validCells.get(i).getFigure())){
                validCells.remove(i);
            }
        }
        return validCells;
    }
}
