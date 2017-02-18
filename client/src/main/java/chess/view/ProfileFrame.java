package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.concurrent.Task;
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
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>ProfileFrame</code> is main user window with general info
 * on user, as login and password (both editable) and rank among
 * other players. <code>ProfileFrame</code> lets browse free players
 * on server and offer/accept offers on playing. After finishing
 * a game user is returned to this window.
 */
public class ProfileFrame extends Stage {
    private String firstConf;
    private String secondConf;
    private List<String> listIn;
    private final static Logger logger = Logger.getLogger(ProfileFrame.class.getClass());

    /**
     * Creates <code>ProfileFrame</code> with given XMLin and XMLout
     * for communicating with server and list of free players available.
     *
     * @param xmLin for receiving messages from server.
     * @param xmlOut for sending messages to server.
     * @param freePlayers current user data + list of free players.
     */
    public ProfileFrame(XMLin xmLin, final XMLout xmlOut, List<String> freePlayers) {
        this.setTitle("Шахматы онлайн");
        Stage stage = this;
        Pane grid = new Pane();
        grid.setPadding(new Insets(0, 25, 25, 50));
        Scene scene = new Scene(grid, 480, 350);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);

        //Setting main title
        Text profiletitle = new Text("Мой Профиль");
        profiletitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        profiletitle.relocate(70, 10);

        //And title for free players list
        Text freetitle = new Text("Свободные игроки");
        freetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        freetitle.relocate(260, 10);

        grid.getChildren().add(profiletitle);
        grid.getChildren().add(freetitle);

        //Creating pane for current player's profile data
        Pane profile = new Pane();
        profile.relocate(10, 30);
        profile.setPrefSize(230, 180);
        profile.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));
        profile.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(3), Insets.EMPTY)));

        //Player's nickname (editable)
        Text loginname = new Text("Никнейм");
        loginname.setFont(Font.font(14));
        loginname.relocate(15, 20);
        TextField login = new TextField(freePlayers.get(3));
        String myName = login.getText();
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        login.relocate(85, 15);
        login.setPrefWidth(130);

        //Player's password (editable)
        Text passwordname = new Text("Пароль");
        passwordname.setFont(Font.font(14));
        passwordname.relocate(15, 60);
        TextField password = new TextField(freePlayers.get(4));
        String myPass = password.getText();
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        password.relocate(85, 55);
        password.setPrefWidth(130);

        //Player's rank
        Text myrankname = new Text("Рейтинг");
        myrankname.setFont(Font.font(14));
        myrankname.relocate(15, 100);
        Text myrank = new Text(freePlayers.get(5));
        myrank.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        myrank.relocate(130, 100);

        /*Button for saving player's data if it is edited
        and player wants to save changes*/
        Button saveBtn = new Button("Сохранить");
        saveBtn.setMinWidth(90);
        saveBtn.setMinHeight(25);
        saveBtn.relocate(70, 140);
        saveBtn.setDisable(true);
        saveBtn.setOnAction(e -> {
            List<String> list = new ArrayList<>();
            list.add("saveProfile");
            list.add(freePlayers.get(2));
            list.add(login.getText());
            list.add(password.getText());
            try {
                xmlOut.sendMessage(list);
            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                logger.error("Failed to send login/password to server from ProfileFrame for saving", e1);
            }
        });

        //Adding all set elements to profile Pane
        profile.getChildren().add(loginname);
        profile.getChildren().add(login);
        profile.getChildren().add(passwordname);
        profile.getChildren().add(password);
        profile.getChildren().add(myrankname);
        profile.getChildren().add(myrank);
        profile.getChildren().add(saveBtn);

        //Creating pane for displaying free players
        Pane freeplayer = new Pane();
        freeplayer.relocate(250, 30);
        freeplayer.setPrefSize(220, 280);
        freeplayer.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));
        freeplayer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(3), Insets.EMPTY)));

        //Filling free players pane with free players' logins and ratings
        for (int i = 6; i < freePlayers.size(); i += 2) {
            final String name = freePlayers.get(i);

            //Setting each free player's name
            Text player = new Text(freePlayers.get(i));
            player.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
            player.relocate(20, 12 * (i - 5));

            //Setting each free player's rank
            Text rank = new Text(freePlayers.get(i + 1));
            rank.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
            rank.setFill(Color.valueOf("#47484a"));
            rank.relocate(95, 12 * (i - 5));

            //Setting button to call particular player to play
            Button btn = new Button("Играть");
            btn.setPadding(new Insets(0));
            btn.setPrefWidth(60.0);
            btn.setPrefHeight(20.0);
            btn.relocate(150, 12 * (i - 5));
            btn.setOnAction(e -> {
                List<String> list = new ArrayList<>();
                list.add("callPlayer");
                list.add(name);
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    logger.error("Failed to send a game offer to " + name + " from ProfileFrame", e1);
                }
            });

            //Adding elements to freeplayer Pane
            freeplayer.getChildren().add(player);
            freeplayer.getChildren().add(rank);
            freeplayer.getChildren().add(btn);
        }
        //Button for refreshing freeplayer Pane
        Button refreshButton = new Button("Обновить список игроков");
        refreshButton.setMinWidth(100);
        refreshButton.setMinHeight(20);
        refreshButton.relocate(270, 320);
        refreshButton.setOnAction(e -> {
            List<String> list = new ArrayList<>();
            list.add("refresh");
            try {
                xmlOut.sendMessage(list);
            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                logger.error("Failed to refresh players list on ProfileFrame", e1);
            }
        });
        //Button for logout
        Button logout = new Button("выйти");
        logout.setMinWidth(60);
        logout.setMinHeight(20);
        logout.relocate(100, 220);
        logout.setOnAction(e -> {
            List<String> list = new ArrayList<>();
            list.add("logout");
            try {
                xmlOut.sendMessage(list);
            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                logger.error("Error logging out from ProfileFrame", e1);
            }
        });
        grid.getChildren().add(freeplayer);
        grid.getChildren().add(profile);
        grid.getChildren().add(refreshButton);
        grid.getChildren().add(logout);
        this.setResizable(false);
        this.show();
        logger.info("Player profile window build successfully.");


        //Class for receiving List of String values from XMLin
        class MyTask<Void> extends Task<Void> {
            @Override
            public Void call() throws Exception {
                try {
                    List<String> list = xmLin.receive();
                    firstConf = list.get(0);
                    secondConf = list.get(1);
                    listIn = list;
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    logger.error("Error receiving data from server on ProfileFrame", e);
                }
                return null;
            }
        }

        MyTask<Void> task = new MyTask<>();

        //In case of different possible answers from server
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
                    List<String> list = new ArrayList<>();
                    list.add("confirm");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        list.add("Ok");
                        try {
                            xmlOut.sendMessage(list);
                        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                            logger.error("Error confirming game with " + secondConf + " on ProfileFrame", e1);
                        }
                        stage.close();
                        new GameFrame(xmLin, xmlOut, false, freePlayers);
                    }
                    if (alert.getResult() == ButtonType.CANCEL) {
                        list.add("No");
                        try {
                            xmlOut.sendMessage(list);
                        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                            logger.error("Error denying game with " + secondConf + " on ProfileFrame", e1);
                        }
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                    }

                }
                if ("notconfirm".equals(firstConf)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(stage);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("игрок уже не активен");
                    alert.setHeaderText(null);
                    alert.setContentText("игрок уже не активен");
                    alert.showAndWait();
                    stage.close();
                    new ProfileFrame(xmLin, xmlOut, listIn);
                }
                if ("confirmresponse".equals(firstConf)) {
                    if ("Ok".equals(secondConf)) {
                        stage.close();
                        new GameFrame(xmLin, xmlOut, true, freePlayers);
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
                if ("saveconfirm".equals(firstConf)) {
                    if ("Ok".equals(secondConf)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Сохранено");
                        alert.setHeaderText(null);
                        alert.setContentText("Изменения сохранены");
                        saveBtn.setDisable(true);
                        alert.showAndWait();
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                    }
                    if ("error".equals(secondConf)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Пользователь с таким логином уже существует");
                        alert.showAndWait();
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                    }
                }
                if ("refresh".equals(firstConf)) {
                    stage.close();
                    new ProfileFrame(xmLin, xmlOut, listIn);
                }
                if ("logout".equals(firstConf)) {
                    stage.close();
                    new AuthFrame(xmLin, xmlOut);
                }
                if ("banned".equals(firstConf)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("Бан!");
                    alert.setHeaderText(null);
                    alert.setContentText("Ваш IP-адрес в бан-листе. Обратитесь к администратору.");
                    alert.showAndWait();
                    stage.close();
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