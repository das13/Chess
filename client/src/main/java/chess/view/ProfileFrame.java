package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by viacheslav koshchii on 24.01.2017.
 */
public class ProfileFrame extends Stage {
    public ProfileFrame(XMLin xmLin, final XMLout xmlOut, List<String> freePlayers) {
        this.setTitle("Шахматы онлайн");
        Pane grid = new Pane();
        //grid.setAlignment(Pos.TOP_LEFT);
        //grid.setHgap(0);
        //grid.setVgap(0);
        grid.setPadding(new Insets(0, 25, 25, 25));
        Scene scene = new Scene(grid, 500, 500);
        this.setScene(scene);
        Text scenetitle = new Text("Мой Профиль");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        scenetitle.relocate(200, 10);
        grid.getChildren().add(scenetitle);
        Pane profile = new Pane();
        profile.relocate(10, 40);
        profile.setPrefSize(230, 120);
        profile.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(3))));
        profile.setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(15), Insets.EMPTY)));
        Pane freeplayer = new Pane();
        freeplayer.relocate(250, 40);
        freeplayer.setPrefSize(230, 300);
        freeplayer.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(3))));
        freeplayer.setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(15), Insets.EMPTY)));
        Text loginname = new Text("Никнейм");
        loginname.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        loginname.relocate(15, 20);
        profile.getChildren().add(loginname);
        TextField login = new TextField(freePlayers.get(2));
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        login.relocate(85, 20);
        login.setPrefWidth(120);
        profile.getChildren().add(login);
        Text passwordname = new Text("Пароль");
        passwordname.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        passwordname.relocate(15, 50);
        profile.getChildren().add(passwordname);
        TextField password = new TextField(freePlayers.get(3));
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        password.relocate(85, 50);
        password.setPrefWidth(120);
        profile.getChildren().add(password);
        Text myrankname = new Text("Рейтинг");
        myrankname.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        myrankname.relocate(15, 80);
        profile.getChildren().add( myrankname);
        Text myrank = new Text(freePlayers.get(4));
        myrank.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        myrank.relocate(85, 80);
        profile.getChildren().add(myrank);
        for (int i = 5; i < freePlayers.size(); i += 2) {
            Text player = new Text(freePlayers.get(i));
            player.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
            player.relocate(20, 12 * (i-3));
            freeplayer.getChildren().add(player);
            Text rank = new Text(freePlayers.get(i + 1));
            rank.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            rank.setFill(Color.GREEN);
            rank.relocate(95, 12 * (i-3));
            freeplayer.getChildren().add(rank);
            Button btn = new Button("Играть");
            btn.setPadding(new Insets(0));
            btn.setPrefWidth(60.0);
            btn.setPrefHeight(20.0);
            btn.relocate(150, 12 * (i-3));
            //grid.getChildren().add(hbBtn);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    //out.println("drag");
                }
            });
            freeplayer.getChildren().add(btn);
        }
        grid.getChildren().add(freeplayer);
        grid.getChildren().add(profile);
        this.show();
    }
}
