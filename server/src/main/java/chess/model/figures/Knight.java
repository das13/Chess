package chess.model.figures;

import chess.model.Cell;
import chess.model.Figure;
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

    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {
        List<Cell> validCells=new ArrayList<Cell>();
        Cell[][] board = getCell().getParentBoard();
        if(getCell().getX()+3<board.length && getCell().getY()+1<board[0].length &&
                (board[getCell().getX()+3][getCell().getY()+1].getFigure()==null ||
                        !getCell().isFriendlyCell(board[getCell().getX()+3][getCell().getY()+1].getFigure()))){
            validCells.add(board[getCell().getX()+3][getCell().getY()+1]);

        }
        return null;
    }
}
