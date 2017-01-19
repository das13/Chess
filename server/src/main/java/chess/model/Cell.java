package chess.model;

/**
 * Created by Admin on 17.01.2017.
 */
public class Cell {
    private Figure figure;
    private int x;
    private int y;
    private Game game;
    public Cell( int x, int y, Game game){
        this.x=x;
        this.y=y;
        this.game=game;
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

    public Game getParentGame() {
        return game;
    }
}
