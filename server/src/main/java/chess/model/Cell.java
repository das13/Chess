package chess.model;

/**
 * Created by viacheslav koshchii on 17.01.2017.
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
        if (this.figure == null) return null;
        else return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public boolean isFriendlyCell(Figure figure) {
        if (figure == null) return false;
        if(this.figure == null) return false;
        return this.getFigure().getType().equals(figure.getType());
    }

    public Game getParentGame() {
        return game;
    }
    public boolean isFigure(){
        if(figure==null) return false;
        return true;
    }
}
