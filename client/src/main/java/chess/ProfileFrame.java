package chess;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by viacheslav koshchii on 24.01.2017.
 */
public class ProfileFrame extends Stage {
    public ProfileFrame(BufferedReader in, final PrintWriter out){
        this.setTitle("Шахматы онлайн");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(40, 25, 25, 25));
        Scene scene = new Scene(grid, 500, 500);
        this.setScene(scene);
        Text scenetitle = new Text("Мой Профиль");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 1, 0, 1, 1);
        Button btn = new Button("Играть");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                out.println("drag");
            }
        });
        this.show();
    }
}
