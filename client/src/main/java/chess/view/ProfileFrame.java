package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
    String myName;
    String myPass;

    public ProfileFrame(XMLin xmLin, final XMLout xmlOut, List<String> freePlayers) {
        this.setTitle("Шахматы онлайн");
        Stage stage = this;
        Pane grid = new Pane();
        //grid.setAlignment(Pos.TOP_LEFT);
        //grid.setHgap(0);
        //grid.setVgap(0);
        grid.setPadding(new Insets(0, 25, 25, 50));
        Scene scene = new Scene(grid, 480, 350);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);
        Text scenetitle = new Text("Мой Профиль");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        scenetitle.relocate(70, 90);
//        Text scenetitle2 = new Text("Свободные игроки");
//        scenetitle2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
//        scenetitle2.relocate(250, 10);
        grid.getChildren().add(scenetitle);
        //grid.getChildren().add(scenetitle2);
        Pane profile = new Pane();
        profile.relocate(10, 120);
        profile.setPrefSize(230, 180);
        profile.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));
        profile.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(3), Insets.EMPTY)));
        Pane freeplayer = new Pane();
        freeplayer.relocate(250, 10);
        freeplayer.setPrefSize(230, 300);
        freeplayer.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));
        freeplayer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(3), Insets.EMPTY)));
        Text loginname = new Text("Никнейм");
        loginname.setFont(Font.font(14));
        loginname.relocate(15, 20);
        profile.getChildren().add(loginname);
        TextField login = new TextField(freePlayers.get(3));
        myName = login.getText();
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        login.relocate(85, 15);
        login.setPrefWidth(130);
        profile.getChildren().add(login);
        Text passwordname = new Text("Пароль");
        passwordname.setFont(Font.font(14));
        passwordname.relocate(15, 60);
        profile.getChildren().add(passwordname);
        TextField password = new TextField(freePlayers.get(4));
        myPass = password.getText();
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        password.relocate(85, 55);
        password.setPrefWidth(130);
        profile.getChildren().add(password);
        Text myrankname = new Text("Рейтинг");
        myrankname.setFont(Font.font(14));
        myrankname.relocate(15, 100);
        profile.getChildren().add(myrankname);
        Text myrank = new Text(freePlayers.get(5));
        myrank.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        myrank.relocate(130, 100);
        profile.getChildren().add(myrank);
        Button saveBtn = new Button("Сохранить");
        //saveBtn.setPadding(new Insets(0));
        saveBtn.setMinWidth(90);
        saveBtn.setMinHeight(25);
        saveBtn.relocate(70, 140);
        saveBtn.setDisable(true);
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
                        saveBtn.setDisable(true);
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
            rank.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
            rank.setFill(Color.valueOf("#47484a"));
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
        Button refreshButton = new Button("Обновить список игроков");
        refreshButton.setMinWidth(100);
        refreshButton.setMinHeight(20);
        refreshButton.relocate(280, 320);
        grid.getChildren().add(freeplayer);
        grid.getChildren().add(profile);
        grid.getChildren().add(refreshButton);

        this.setResizable(false);
        this.show();


        class MyTask<Void> extends Task<Void> {
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
                return null;
            }
        }
        MyTask<Void> task = new MyTask<Void>();
        class MyHandler implements EventHandler {
            @Override
            public void handle(Event event) {
                if ("confirm".equals(firstConf)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(stage);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("Приглашение");
                    alert.setHeaderText(null);
                    alert.setContentText("Вас приглашает " + secondConf);
                    List<String> list = new ArrayList<String>();
                    list.add("confirm");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        list.add("Ok");
                        try {
                            xmlOut.sendMessage(list);
                        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                            e1.printStackTrace();
                        }
                        stage.close();
                        new GameFrame(xmLin, xmlOut, false);

                    }
                    if (alert.getResult() == ButtonType.CANCEL) {
                        list.add("No");
                        try {
                            xmlOut.sendMessage(list);
                        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                            e1.printStackTrace();
                        }
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                    }

                }
                if ("confirmresponse".equals(firstConf)) {
                    if ("Ok".equals(secondConf)) {
                        stage.close();
                        new GameFrame(xmLin, xmlOut, true);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Отказ");
                        alert.setHeaderText(null);
                        alert.setContentText("Вам отказали");
                        alert.showAndWait();
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                    }
                }

            }
        }
        login.textProperty().addListener((observable, oldValue, newValue) -> {
            saveBtn.setDisable(false);
        });
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            saveBtn.setDisable(false);
        });
        task.setOnSucceeded(new MyHandler());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}
