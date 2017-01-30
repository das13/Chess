package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viacheslav koshchii on 24.01.2017.
 */
public class ProfileFrame extends Stage {
    String firstConf;
    String secondConf;
    public ProfileFrame(XMLin xmLin, final XMLout xmlOut, List<String> freePlayers) {
        this.setTitle("Шахматы онлайн");
        Stage stage=this;
        Pane grid = new Pane();
        //grid.setAlignment(Pos.TOP_LEFT);
        //grid.setHgap(0);
        //grid.setVgap(0);
        grid.setPadding(new Insets(0, 25, 25, 25));
        Scene scene = new Scene(grid, 500, 350);
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
        TextField login = new TextField(freePlayers.get(3));
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        login.relocate(85, 20);
        login.setPrefWidth(120);
        profile.getChildren().add(login);
        Text passwordname = new Text("Пароль");
        passwordname.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        passwordname.relocate(15, 50);
        profile.getChildren().add(passwordname);
        TextField password = new TextField(freePlayers.get(4));
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        password.relocate(85, 50);
        password.setPrefWidth(120);
        profile.getChildren().add(password);
        Text myrankname = new Text("Рейтинг");
        myrankname.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        myrankname.relocate(15, 80);
        profile.getChildren().add(myrankname);
        Text myrank = new Text(freePlayers.get(5));
        myrank.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        myrank.relocate(85, 80);
        profile.getChildren().add(myrank);
        Button saveBtn = new Button("Сохранить");
        saveBtn.setPadding(new Insets(0));
        saveBtn.setPrefWidth(70.0);
        saveBtn.setPrefHeight(20.0);
        saveBtn.relocate(135, 85);
        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                List<String> list = new ArrayList<String>();
                list.add("saveProfile");
                list.add(freePlayers.get(2));
                list.add(login.getText());
                list.add(password.getText());
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    e1.printStackTrace();
                }
                List<String> listIn = null;
                try {
                    listIn = xmLin.receive();
                    if ("Ok".equals(listIn.get(0))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Сохранено");
                        alert.setHeaderText(null);
                        alert.setContentText("Изменения сохранены");
                        alert.showAndWait();
                    }
                    if ("error".equals(listIn.get(0))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Пользователь с таким логином уже существует");
                        alert.showAndWait();
                    }

                } catch (ParserConfigurationException | SAXException | IOException | TransformerConfigurationException e1) {
                    e1.printStackTrace();
                }
            }
        });
        profile.getChildren().add(saveBtn);
        for (int i = 6; i < freePlayers.size(); i += 2) {
            final String name = freePlayers.get(i);
            Text player = new Text(freePlayers.get(i));
            player.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
            player.relocate(20, 12 * (i - 3));
            freeplayer.getChildren().add(player);
            Text rank = new Text(freePlayers.get(i + 1));
            rank.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            rank.setFill(Color.GREEN);
            rank.relocate(95, 12 * (i - 3));
            freeplayer.getChildren().add(rank);
            Button btn = new Button("Играть");
            btn.setPadding(new Insets(0));
            btn.setPrefWidth(60.0);
            btn.setPrefHeight(20.0);
            btn.relocate(150, 12 * (i - 3));
            //grid.getChildren().add(hbBtn);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    List<String> list = new ArrayList<String>();
                    list.add("callPlayer");
                    list.add(name);
                    try {
                        xmlOut.sendMessage(list);
                    } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            freeplayer.getChildren().add(btn);
        }
        grid.getChildren().add(freeplayer);
        grid.getChildren().add(profile);
        this.show();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                    try {
                        List<String> listIn = xmLin.receive();
                        firstConf = listIn.get(0);
                        secondConf = listIn.get(1);
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerConfigurationException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }

                return null ;
            }
        };

        task.setOnSucceeded(event -> {
            if ("confirm".equals(firstConf)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(stage);
                alert.getDialogPane().getStylesheets().add("Skin.css");
                alert.setTitle("Приглашение");
                alert.setHeaderText(null);
                alert.setContentText("Вас приглашает " + secondConf);
                alert.showAndWait();
                if(alert.getResult()==ButtonType.OK){
                    System.out.println("ok");
                    try {
                        stage.close();
                        new GameFrame();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(alert.getResult()==ButtonType.CANCEL){
                    System.out.println("cancel");
                    new Thread(task).start();
                }

            }
        });
        new Thread(task).start();
    }

}
