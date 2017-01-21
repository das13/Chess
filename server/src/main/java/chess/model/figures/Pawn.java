package chess.model.figures;

import chess.exceptions.ReplacePawnException;
import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 17.01.2017.
 */
public class Pawn extends Figure {

    public Pawn(Type type) {
        super(type);
    }

    public Pawn(Type type, Cell cell) {
        super(type, cell);
    }

    @Override
    public void move(Cell destination) throws ReplacePawnException {
        super.move(destination);
        if ((destination.getY() == 7 && getType() == Type.WHITE) || (destination.getY() == 0 && getType() == Type.BLACK)) {
            throw new ReplacePawnException();
        }
    }

    public List<Cell> allAccessibleMove() {
        List<Cell> validCells = new ArrayList<Cell>();
        Game game = getCell().getParentGame();
        int koef = 1;
        if (getType() == Type.BLACK) {
            koef = -1;
        }

        if (getCell().getY() + 1 * koef >= 0 && getCell().getY() + 1 * koef < 8) {
            if (!game.getCell(getCell().getX(), getCell().getY() + 1 * koef).isFigure()) {
                validCells.add(game.getCell(getCell().getX(), getCell().getY() + 1 * koef));
            }
        }
        if (isFirstMove()) {
            if (getCell().getY() + 2 * koef >= 0 && getCell().getY() + 2 * koef < 8) {
                if (!game.getCell(getCell().getX(), getCell().getY() + 2 * koef).isFigure() && !game.getCell(getCell().getX(), getCell().getY() + 1 * koef).isFigure()) {
                    validCells.add(game.getCell(getCell().getX(), getCell().getY() + 2 * koef));
                }
            }
        }
        if (getCell().getX() + 1 * koef >= 0 && getCell().getX() + 1 * koef < 8 && getCell().getY() + 1 * koef >= 0 && getCell().getY() + 1 * koef < 8) {
            if (!getCell().isFriendlyCell(game.getCell(getCell().getX() + 1 * koef, getCell().getY() + 1 * koef).getFigure()) && game.getCell(getCell().getX() + 1 * koef, getCell().getY() + 1 * koef).getFigure() != null) {
                validCells.add(game.getCell(getCell().getX() + 1 * koef, getCell().getY() + 1 * koef));
            }
        }
        if (getCell().getX() - 1 * koef >= 0 && getCell().getX() - 1 * koef < 8 && getCell().getY() + 1 * koef >= 0 && getCell().getY() + 1 * koef < 8) {
            if (!getCell().isFriendlyCell(game.getCell(getCell().getX() - 1 * koef, getCell().getY() + 1 * koef).getFigure()) && game.getCell(getCell().getX() - 1 * koef, getCell().getY() + 1 * koef).getFigure() != null) {
                validCells.add(game.getCell(getCell().getX() - 1 * koef, getCell().getY() + 1 * koef));
            }
        }
        return validCells;
    }

}
