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
public class Knight extends Figure {
    public Knight(Type type) {
        super(type);
    }

    public Knight(Type type, Cell cell) {
        super(type, cell);
    }

    public List<Cell> allAccessibleMove()  {
        List<Cell> validCells = new ArrayList<Cell>();
        if(!getCell().getParentGame().getCurrentStep().equals(getType())) return validCells;
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
