package chess.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Created by slava on 20.02.17.
 */
public class LastMove {
    private Pane source;
    private Pane source1;
    private Pane target;
    private Pane target1;
    private ImageView figure;
    private ImageView figure1;
    private boolean castling;
    private boolean take;
    private boolean replace;
    public boolean isTake() {
        return take;
    }

    public void setTake(boolean take) {
        this.take = take;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public void revertMove(){
        if(replace){
            source.getChildren().clear();
        }
        if(!castling && !take){
            target.getChildren().add(figure);
        }

        if(castling && !take){
            target1.getChildren().add(figure1);
            target.getChildren().add(figure);

        }
        if(!castling && take){
            target.getChildren().add(figure);
            figure1.fitHeightProperty().bind(source.heightProperty());
            figure1.setPreserveRatio(false);
            figure1.setLayoutX(2.0);
            figure1.setLayoutY(1.0);
            source.getChildren().add(figure1);
        }
    }
    public Pane getSource() {
        return source;
    }

    public void setSource(Pane source) {
        this.source = source;
    }

    public Pane getSource1() {
        return source1;
    }

    public void setSource1(Pane source1) {
        this.source1 = source1;
    }

    public Pane getTarget() {
        return target;
    }

    public void setTarget(Pane target) {
        this.target = target;
    }

    public Pane getTarget1() {
        return target1;
    }

    public void setTarget1(Pane target1) {
        this.target1 = target1;
    }

    public ImageView getFigure() {
        return figure;
    }

    public void setFigure(ImageView figure) {
        this.figure = figure;
    }

    public ImageView getFigure1() {
        return figure1;
    }

    public void setFigure1(ImageView figure1) {
        this.figure1 = figure1;
    }

    public boolean isCastling() {
        return castling;
    }

    public void setCastling(boolean castling) {
        this.castling = castling;
    }
}
