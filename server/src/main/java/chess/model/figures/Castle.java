package chess.model.figures;

import chess.model.Cell;
import chess.model.Figure;
import chess.model.Type;

import java.util.List;

/**
 * Created by Admin on 17.01.2017.
 */
public class Castle extends Figure {
    public Castle(Type type) {
        super(type);
    }

    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {
        return null;
    }
}
