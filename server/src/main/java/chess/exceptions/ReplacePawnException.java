package chess.exceptions;

/**
 * Exception that may occur when a pawn reaches
 * the end of the board and player wants to change it for another figure.
 */
public class ReplacePawnException extends Exception {
    public ReplacePawnException(){
        super();
    }
}
