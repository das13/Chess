package chess.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by bobnewmark on 30.01.2017
 */
public class GameFrame extends Stage {

    public GameFrame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Board.fxml"));
        this.setTitle("Hello World");

        Scene scene = new Scene(root, 700, 600);
        this.setScene(scene);
        this.setMinWidth(750);
        this.setMinHeight(650);
        this.show();
    }

}
