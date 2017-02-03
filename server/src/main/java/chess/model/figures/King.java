package chess.model.figures;

import chess.exceptions.ReplacePawnException;
import chess.model.Cell;
import chess.model.Figure;
import chess.model.Game;
import chess.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viacheslav koshchii on 17.01.2017.
 */
public class King extends Figure {
    Game game;
    public King(Type type) {
        super(type);
    }
    public King(Type type, Cell cell) {
        super(type, cell);
    }

    public boolean hasMove() {
        return false;
    }

    public List<Cell> allAccessibleMove() {
        List<Cell> validCells = new ArrayList<Cell>();

        game = getCell().getParentGame();
        int a = getCell().getX();
        int b = getCell().getY();

        /* adding all cells to the validCells list as long as they don't have figures
        * and as we have a cell with a figure we add it and check another direction */


        for (int x = a - 1; x <= a + 1; x++) {
            for (int y = b -1; y <= b + 1; y++) {
                if (x < 0 || x > 7) continue;
                if (y < 0 || y > 7) continue;
                if (game.getCell(x,y).getFigure() == this) continue;
                if (game.getCell(x,y).isFriendlyCell(this)) continue;
                validCells.add(game.getCell(x,y));
            }
        }

        /* all cells that have figures of the same Type as this are removed here*/
//        for (int i = 0; i < validCells.size(); i++) {
//            if (validCells.get(i).isFriendlyCell(this)) {
//                validCells.remove(i);
//            }
//        }
        return validCells;
    }

    // overriding method to specialize castling both sides
//    @Override
//    public void move(Cell destination) {
//        if ((allAccessibleMove().contains(destination)) && hasMove()) {
//            this.getCell().setFigure(null);
//            this.cell = destination;
//            this.getCell().setFigure(this);
//        } else if ((allAccessibleMove().contains(destination))
//                && (true /*destination is castling Kingside cell*/)) {
//            castlingKingside();
//        } else if ((allAccessibleMove().contains(destination))
//                && (true /*destination is castling Queenside cell*/)) {
//            castlingQueenside();
//        }
//    }

    public boolean castlingKingSideAllowed() {
        // if king moved
        if (!this.isFirstMove()) return false;
        // if castle moved
        if (!game.getCell(7, this.getCell().getY()).getFigure().isFirstMove()) return false;
        // if cell king is going to pass is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(5, this.getCell().getY()))) return false;
        // if king's destination cell is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(6, this.getCell().getY()))) return  false;
        return true;
    }


    public boolean castlingQueenSideAllowed() {
        // if king moved
        if (!this.isFirstMove()) return false;
        // if castle moved
        if (!game.getCell(0, this.getCell().getY()).getFigure().isFirstMove()) return false;
        // if cell king is going to pass is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(3, this.getCell().getY()))) return false;
        // if king's destination cell is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(2, this.getCell().getY()))) return  false;
        return true;
    }

    public void castlingKingside() {
        if (castlingKingSideAllowed()) {
            try {
                this.move(game.getCell(6, this.getCell().getY()));
                game.getCell(7, this.getCell().getY()).getFigure().move(game.getCell(5, this.getCell().getY()));
            } catch (ReplacePawnException e) {
                //ignore
            }
        }
    }

    public void castlingQueenside() {
        if (castlingQueenSideAllowed()) {
            try {
                this.move(game.getCell(2, this.getCell().getY()));
                game.getCell(0, this.getCell().getY()).getFigure().move(game.getCell(3, this.getCell().getY()));
            } catch (ReplacePawnException e) {
                //ignore
            }
        }
    }
}
