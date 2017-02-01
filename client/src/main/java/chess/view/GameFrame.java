package chess.view;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by bobnewmark on 30.01.2017
 */
public class GameFrame extends Application {

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
    public void handleOnDrag(MouseEvent event) {

        Node node = (Node) event.getSource();
        node = node.getParent();
        System.out.print("Drag from cell: ");
        System.out.print("X:" + GridPane.getColumnIndex(node));
        System.out.println(" Y:" + GridPane.getRowIndex(node));

        ImageView image = (ImageView) event.getSource();
        Dragboard db = image.startDragAndDrop(TransferMode.ANY);

        ClipboardContent cbContent = new ClipboardContent();
        cbContent.putImage(image.getImage());
        db.setContent(cbContent);
        image.setVisible(false);
        event.consume();
    }

    @FXML
    public void handleDragOver(DragEvent de) {
        Dragboard board = de.getDragboard();
        if (board.hasImage()) {
            de.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleDrop(DragEvent event) {

        Dragboard db = event.getDragboard();
        Node node = (Node) event.getSource();
        Pane pane;

        System.out.print("Drop on cell: ");
        System.out.print("X:" + GridPane.getColumnIndex(node));
        System.out.println(" Y:" + GridPane.getRowIndex(node));

        if (event.getTarget() instanceof ImageView) {
            ImageView temp = (ImageView) event.getTarget();
            pane = (Pane) temp.getParent();
        } else {
            pane = (Pane) event.getTarget();
        }

        if (pane.getChildren().size() > 0) {
            pane.getChildren().clear();
        }

        ImageView image = new ImageView(db.getImage());
        image.setOnDragDetected(event1 -> {
            ImageView image1 = (ImageView) event1.getSource();
            Dragboard db1 = image1.startDragAndDrop(TransferMode.ANY);

            ClipboardContent cbContent = new ClipboardContent();
            cbContent.putImage(image1.getImage());
            db1.setContent(cbContent);
            image1.setVisible(false);
            event1.consume();
        });

        pane.getChildren().add(image);
        event.consume();
    }

    @FXML
    public void handleClick(MouseEvent event) {
        ImageView temp = (ImageView) event.getTarget();
        Node node = (Node) temp.getParent();
        System.out.println("X: " + GridPane.getColumnIndex(node));
        System.out.println("Y: " + GridPane.getRowIndex(node));
    }
}


