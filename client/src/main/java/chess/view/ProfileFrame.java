package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
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

import java.util.List;

/**
 * Created by viacheslav koshchii on 24.01.2017.
 */
public class ProfileFrame extends Stage {
    public ProfileFrame(XMLin xmLin, final XMLout xmlOut, List<String> freePlayers){
        this.setTitle("Шахматы онлайн");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(0, 25, 25, 25));
        Scene scene = new Scene(grid, 500, 500);
        this.setScene(scene);
        Text scenetitle = new Text("Мой Профиль");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 1, 1);
        for(int i=2; i<freePlayers.size(); i++){
            Text player = new Text(freePlayers.get(i));
            player.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(player, 0, i, 1, 1);
        }
        Button btn = new Button("Играть");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                //out.println("drag");
            }
        });
        this.show();
    }
}
