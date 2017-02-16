package chess.model;

import chess.exceptions.ReplacePawnException;

import java.util.List;

/**
 * <code>Figure</code> is base class for all pieces.
 */
public abstract class Figure {
    protected Figure(Type type){
        this.type=type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private Type type;
    private Cell cell;
    private boolean firstMove = true;
    public abstract List<Cell> allAccessibleMove();
    protected Figure() {
    }
    protected Figure(Type type, Cell cell) {
        this.type = type;
        this.cell = cell;
    }
    public boolean hasMove() {
        return allAccessibleMove().size() > 0;
    }

    /**
     * Method checks if destination cell is available and moves a figure to it if so
     * @param destination target Cell to move on.
     * @throws ReplacePawnException in case when pawn got to the end of board, but error
     * happened when it was replaced by another figure.
     */
    public void move(Cell destination) throws ReplacePawnException {
        if (allAccessibleMove().contains(destination)) {
            this.cell.setFigure(null);
            this.cell = destination;
            this.cell.setFigure(this);
            firstMove = false;
            cell.getParentGame().changeCurrentStep();
        }
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Type getType() {
        return this.type;
    }

    public Cell getCell() {
        return cell;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

}
