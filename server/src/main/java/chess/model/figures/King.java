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
public class King extends Figure {
    public King(Type type) {
        super(type);
    }
    public King(Type type, Cell cell) {
        super(type, cell);
    }

    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {
        List<Cell> validCells = new ArrayList<Cell>();

        int a = cell.getX();
        int b = cell.getY();

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */
        for (int x = a - 1; x <= a + 1; x++) {
            for (int y = b -1; y <= b + 1; y++) {
                if (getCell().getParentBoard()[x][y].getFigure() == this) continue;
                validCells.add(getCell().getParentBoard()[x][y]);
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

    // overriding method to specialize castling both sides
    @Override
    public void move(Cell destination) {
        if ((allAccessibleMove().contains(destination)) && hasMove()) {
            this.cell.setFigure(null);
            this.cell = destination;
            this.cell.setFigure(this);
        } else if ((allAccessibleMove().contains(destination))
        && (true /*destination is castling Kingside cell*/)) {
            castlingKingside();
        } else if ((allAccessibleMove().contains(destination))
                && (true /*destination is castling Queenside cell*/)) {
            castlingQueenside();
        }
    }

    public boolean castlingKingsideAllowed() {
        return ((true /*king never moved*/)
                && (true /*castle never moved*/)
                && (true /*king is not under attack*/)
                && (true /*cells for king to pass are safe*/)
                && (true /*position after castling is safe*/));
    }


    public boolean castlingQueensideAllowed() {
        return ((true /*king never moved*/)
                && (true /*castle never moved*/)
                && (true /*king is not under attack*/)
                && (true /*cells for king to pass are safe*/)
                && (true /*position after castling is safe*/));
    }

    public void castlingKingside() {
        if (castlingKingsideAllowed()) {
            this.move(null /*Cell destination*/);
            /*and castle (by it's unique number) moves too*/
        }
    }

    public void castlingQueenside() {
        if (castlingQueensideAllowed()) {
            this.move(null /*Cell destination*/);
            /*and castle (by it's unique number) moves too*/
        }
    }
}
