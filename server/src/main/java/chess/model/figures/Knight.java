package chess.model.figures;

import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Knight</code> figure.
 */
public class Knight extends Figure {

    public Knight(Type type, Cell cell) {
        super(type, cell);
    }
    public Knight() {
    }
    public List<Cell> allAccessibleMove()  {
        List<Cell> validCells = new ArrayList<Cell>();
            validCells.clear();
            if(getCell() == null) return validCells;
            Game game = getCell().getParentGame();
            if(getCell().getX() + 2<8 && getCell().getY() + 1<8)
                validCells.add(game.getCell(getCell().getX() + 2, getCell().getY() + 1));
            if(getCell().getX() + 2<8 && getCell().getY() - 1>=0)
                validCells.add(game.getCell(getCell().getX() + 2, getCell().getY() - 1));
            if(getCell().getX() - 2>=0 && getCell().getY() + 1<8)
                validCells.add(game.getCell(getCell().getX() - 2, getCell().getY() + 1));
            if(getCell().getX() - 2>=0 && getCell().getY() - 1>=0)
                validCells.add(game.getCell(getCell().getX() - 2, getCell().getY() - 1));
            if(getCell().getX() + 1<8 && getCell().getY() + 2<8)
                validCells.add(game.getCell(getCell().getX() + 1, getCell().getY() + 2));
            if(getCell().getX() + 1<8 && getCell().getY() - 2>=0)
                validCells.add(game.getCell(getCell().getX() + 1, getCell().getY() - 2));
            if(getCell().getX() - 1>=0 && getCell().getY() + 2<8)
                validCells.add(game.getCell(getCell().getX() - 1, getCell().getY() + 2));
            if(getCell().getX() - 1>=0 && getCell().getY() - 2>=0)
                validCells.add(game.getCell(getCell().getX() - 1, getCell().getY() - 2));
            for (int i = 0; i < validCells.size(); i++) {
                if (getCell().isFriendlyCell(validCells.get(i).getFigure())) {
                    validCells.remove(i);
                }
            }
            for (int i = 0; i < validCells.size(); i++) {
                if (getCell().isFriendlyCell(validCells.get(i).getFigure())) {
                    validCells.remove(i);
                }
            }
            return validCells;
    }
}
