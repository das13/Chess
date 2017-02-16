package chess.model;

/**
 * <code>Cell</code> is class for cells ob game board.
 */
public class Cell {
    private Figure figure;
    private final int x;
    private final int y;
    private final Game game;
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
        return figure != null;
    }
}
