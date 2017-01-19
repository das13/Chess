package chess.model;

import java.util.List;

/**
 * Created by Admin on 17.01.2017.
 */
public abstract class Figure {
    public Figure(Type type){
        this.type=type;
    }
    private int id;
    private Type type;
    protected Cell cell;
    private boolean firstMove = true;
    public abstract List<Cell> allAccessibleMove();

    public Figure(Type type, Cell cell) {
        this.type = type;
        this.cell = cell;
    }
    public boolean hasMove() {
        return allAccessibleMove().size()==0 ? false:true;
    }
    // method checks if destination cell is available and moves a figure to it if so
    public void move(Cell destination) {
        if (allAccessibleMove().contains(destination)) {
            this.cell.setFigure(null);
            this.cell = destination;
            this.cell.setFigure(this);
            firstMove = false;
        }
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
}
