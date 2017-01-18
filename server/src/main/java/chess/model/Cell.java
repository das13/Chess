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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public boolean isFriendlyCell(Figure figure) {
        return this.getFigure().getType() == figure.getType();
    }
}
