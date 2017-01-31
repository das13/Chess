package chess.view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by bobnewmark on 30.01.2017
 */
public class GameFrame extends Application {

    @FXML
    GridPane grid;

    //    public GameFrame() throws IOException {
//    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/Board.fxml"));
        window.setTitle("Game");

        Scene scene = new Scene(root, 700, 600);
        window.setScene(scene);
        window.setMinWidth(750);
        window.setMinHeight(650);
        window.show();
    }


    @FXML
    public void handle(MouseEvent event) {
        //Drag was detected, start drap-and-drop gesture
        //Allow any transfer node
        ImageView image = (ImageView) event.getSource();
        Dragboard db = image.startDragAndDrop(TransferMode.ANY);

        //Put ImageView on dragboard
        ClipboardContent cbContent = new ClipboardContent();
        cbContent.putImage(image.getImage());
        //cbContent.put(DataFormat.)
        db.setContent(cbContent);
        image.setVisible(false);
        event.consume();
    }


    @FXML
    public void handleDrop(DragEvent event) {
        System.out.println("inside drop method");
        Dragboard db = event.getDragboard();

        Node node = (Node) event.getSource();

        System.out.println("X: " + GridPane.getColumnIndex(node));
        System.out.println("Y: " + GridPane.getRowIndex(node));
        System.out.println(node.getParent());

//        boolean success = false;
        Pane pane = (Pane) event.getTarget();
        pane.getChildren().add(new ImageView(db.getImage()));
//        ImageView image = new ImageView(db.getImage());
//        if (db.hasImage()) {
//            System.out.println(event.getTarget().getClass());
//            event.setDropCompleted(true);
//        }

//        Node node = event.getPickResult().getIntersectedNode();
//        if(db.hasImage()){
//
//            Integer cIndex = GridPane.getColumnIndex(node);
//            Integer rIndex = GridPane.getRowIndex(node);
//            int x = cIndex == null ? 0 : cIndex;
//            int y = rIndex == null ? 0 : rIndex;
//            ImageView image = new ImageView(db.getImage());
//
//            grid.add(image, x, y);
//            success = true;
//        }
//
//event.setDropCompleted(success);


        event.consume();
    }

    @FXML
    public void handleMouse(MouseEvent event) {

        ImageView temp = (ImageView) event.getTarget();
        Node node = (Node) temp.getParent();
        System.out.println("X: " + GridPane.getColumnIndex(node));
        System.out.println("Y: " + GridPane.getRowIndex(node));
    }


}


