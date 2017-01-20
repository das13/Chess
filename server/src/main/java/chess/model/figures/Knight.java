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
public class Knight extends Figure {
    public Knight(Type type) {
        super(type);
    }

    public Knight(Type type, Cell cell) {
        super(type, cell);
    }

    public List<Cell> allAccessibleMove() throws ArrayIndexOutOfBoundsException {
        List<Cell> validCells = new ArrayList<Cell>();
        Game game = getCell().getParentGame();
        try {
            Cell cell1 = game.getCell(getCell().getX() + 2, getCell().getY() + 1);
            if(cell1!=null)
            validCells.add(cell1);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell2 = game.getCell(getCell().getX() + 2, getCell().getY() - 1);
            if(cell2!=null)
            validCells.add(cell2);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell3 = game.getCell(getCell().getX() - 2, getCell().getY() + 1);
            if(cell3!=null)
            validCells.add(cell3);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell4 = game.getCell(getCell().getX() - 2, getCell().getY() - 1);
            if(cell4!=null)
            validCells.add(cell4);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell5 = game.getCell(getCell().getX() + 1, getCell().getY() + 2);
            if(cell5!=null)
            validCells.add(cell5);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell6 = game.getCell(getCell().getX() + 1, getCell().getY() - 2);
            if(cell6!=null)
            validCells.add(cell6);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell7 = game.getCell(getCell().getX() - 1, getCell().getY() + 2);
            if(cell7!=null)
            validCells.add(cell7);
        } catch (NullPointerException e) {

        }
        try {
            Cell cell8 = game.getCell(getCell().getX() - 1, getCell().getY() - 2);
            if(cell8!=null)
            validCells.add(cell8);
        } catch (NullPointerException e) {

        }
        for (int i = 0; i < validCells.size(); i++) {
            if (getCell().isFriendlyCell(validCells.get(i).getFigure())) {
                validCells.remove(i);
            }
        }
        return validCells;
    }
}
