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

        if (isFirstMove()
                && game.getCell(1, b).getFigure() == null
                && game.getCell(2, b).getFigure() == null
                && game.getCell(3, b).getFigure() == null) {
            validCells.add(game.getCell(2, b));
        }
        if (isFirstMove()
                && game.getCell(5, b).getFigure() == null
                && game.getCell(5, b).getFigure() == null) {
            validCells.add(game.getCell(6, b));
        }

        for (int x = a - 1; x <= a + 1; x++) {
            for (int y = b -1; y <= b + 1; y++) {
                if (x < 0 || x > 7) continue;
                if (y < 0 || y > 7) continue;
                if (game.getCell(x,y).getFigure() == this) continue;
                if (game.getCell(x,y).isFriendlyCell(this)) continue;
                validCells.add(game.getCell(x,y));
            }
        }
        return validCells;
    }

    @Override
    public void move(Cell destination) throws ReplacePawnException {
        if (allAccessibleMove().contains(destination)) {
            if (castlingKingSideAllowed(destination)) castlingKingside();
            if (castlingQueenSideAllowed(destination)) castlingQueenside();
        } else {
            this.getCell().setFigure(null);
            this.setCell(destination);
            this.getCell().setFigure(this);
            setFirstMove(false);
            getCell().getParentGame().changeCurrentStep();
        }
    }

    public boolean castlingKingSideAllowed(Cell destination) {
        // if destination cell is proper for castling
        if (!(destination.getX() == 6 && destination.getY() == this.getCell().getY())) return false;
        // if castle moved
        if (!game.getCell(7, this.getCell().getY()).getFigure().isFirstMove()) return false;
        // if cell king is going to pass is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(5, this.getCell().getY()))) return false;
        // if king's destination cell is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(6, this.getCell().getY()))) return  false;
        return true;
    }


    public boolean castlingQueenSideAllowed(Cell destination) {
        // if destination cell is proper for castling
        if (!(destination.getX() == 2 && destination.getY() == this.getCell().getY())) return false;
        // if castle moved
        if (!game.getCell(0, this.getCell().getY()).getFigure().isFirstMove()) return false;
        // if cell king is going to pass is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(3, this.getCell().getY()))) return false;
        // if king's destination cell is under attack
        if (game.getEnemyMoves(this.getType()).contains(game.getCell(2, this.getCell().getY()))) return  false;
        return true;
    }

    public void castlingKingside() {
            try {
                this.move(game.getCell(6, this.getCell().getY()));
                game.getCell(7, this.getCell().getY()).getFigure().move(game.getCell(5, this.getCell().getY()));
                System.out.println("CASTLING KINGSIDE");
            } catch (ReplacePawnException e) {
                //ignore
            }
    }

    public void castlingQueenside() {
            try {
                this.move(game.getCell(2, this.getCell().getY()));
                game.getCell(0, this.getCell().getY()).getFigure().move(game.getCell(3, this.getCell().getY()));
                System.out.println("CASTLING QUEENSIDE");
            } catch (ReplacePawnException e) {
                //ignore
            }
    }
}
