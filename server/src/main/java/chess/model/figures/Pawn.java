package chess.model.figures;

import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 17.01.2017.
 */
public class Pawn extends Figure {
    private Figure replaceFigure;
    public Pawn(Type type) {
        super(type);
    }
    public Pawn(Type type, Cell cell) {
        super(type, cell);
    }
    public void replacePawn(Cell cell){
        if(replaceFigure==null){}
        cell.setFigure(replaceFigure);
    }
    @Override
    public void move(Cell destination) {
        super.move(destination);
        if(destination.getY()==7 || destination.getY()==0){
            replacePawn(destination);
        }
    }
    public List<Cell> allAccessibleMove() {
        List<Cell> validCells = new ArrayList<Cell>();
        Game game = getCell().getParentGame();
        if(!getCell().isFriendlyCell(game.getCell(getCell().getX(), getCell().getY() + 1).getFigure())) {
            validCells.add(game.getCell(getCell().getX(), getCell().getY() + 1));
        }
        if(isFirstMove()){
            if(!getCell().isFriendlyCell(game.getCell(getCell().getX(), getCell().getY() + 2).getFigure())) {
                validCells.add(game.getCell(getCell().getX(), getCell().getY() + 2));
            }
        }
        if(!getCell().isFriendlyCell(game.getCell(getCell().getX()+1, getCell().getY() + 1).getFigure())&& game.getCell(getCell().getX()+1, getCell().getY() + 1).getFigure()!=null) {
            validCells.add(game.getCell(getCell().getX()+1, getCell().getY() + 1));
        }
        if(!getCell().isFriendlyCell(game.getCell(getCell().getX()-1, getCell().getY() + 1).getFigure())&& game.getCell(getCell().getX()-1, getCell().getY() + 1).getFigure()!=null) {
            validCells.add(game.getCell(getCell().getX()-1, getCell().getY() + 1));
        }
        return validCells;
    }
}
