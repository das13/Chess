package chess.model;

import java.util.List;

/**
 * Created by Admin on 17.01.2017.
 */
public abstract class Figure {
    public Figure(Type type){
        this.type=type;
    }
    private Type type;
    private Cell cell;
    private boolean firstMove = true;
    public abstract boolean hasMove();
    public abstract List<Cell> allAccessibleMove();


}
