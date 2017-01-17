package chess.model;

/**
 * Created by Admin on 17.01.2017.
 */
public class Cell {
    private Figure figure;
    private int x;
    private int y;
    public Cell(Figure figure, int x, int y){
        this.figure=figure;
        this.x=x;
        this.y=y;
    }
}
