package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
 * RegFrame is a window for registering a new player.
 * It connects to server and tries to register a new player,
 * if player with such login isn't registered yet, registration
 * goes successfully. In case when such user exists or login or
 * password isn't entered, registration doesn't proceed.
 *
 */
public class RegFrame extends Stage {

    private final static Logger logger = Logger.getLogger(RegFrame.class);
    /**
     * Creating <code>RegFrame</code> with given XMLin and XMLout
     * for communicating with remote server.
     *
     * @param xmLin for receiving messages from server.
     * @param xmlOut for sending messages to server.
     */
    public RegFrame(final XMLin xmLin, final XMLout xmlOut) {
        this.setTitle("Регистрация нового игрока");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20, 10, 20, 10));
        grid.setVgap(15);
        grid.setHgap(10);

        /*Setting title for the frame*/
        Text scenetitle = new Text("Новый игрок");
        scenetitle.setId("scenetitle");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        /*Setting login input*/
        Label loginLabel = new Label("Логин:");
        GridPane.setConstraints(loginLabel, 0, 0);
        TextField loginInput = new TextField();
        loginInput.setMinWidth(180);
        loginInput.setPromptText("будет виден другим игрокам");
        GridPane.setConstraints(loginInput, 1, 0);

        /*Setting password input*/
        Label passLabel = new Label("Пароль:");
        GridPane.setConstraints(passLabel, 0, 1);
        PasswordField passInput = new PasswordField();
        passInput.setFocusTraversable(false);
        passInput.setPromptText("ваш пароль");
        GridPane.setConstraints(passInput, 1, 1);

        /*Setting buttons*/
        HBox hBox = new HBox();
        hBox.setSpacing(40);
        hBox.setAlignment(Pos.CENTER);
        Button yesButton = new Button("Создать");
        yesButton.setPrefWidth(90);
        yesButton.setOnAction(e -> {
            if (loginInput.getText().isEmpty() || passInput.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add("Skin.css");
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Заполните поля логин/пароль");
                alert.showAndWait();
            } else {
                List<String> list = new ArrayList<>();
                list.add("reg");
                list.add(loginInput.getText());
                list.add(passInput.getText());
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    logger.error("Failed to send login/password to server from RegFrame", e1);
                }
                List<String> listIn = null;
                try {
                    listIn = xmLin.receive();
                    if("accepted".equals(listIn.get(1))){
                        this.close();
                        new AuthFrame(xmLin, xmlOut);
                    } else if ("denied".equals(listIn.get(1))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Пользователь с таким логином уже существует");
                        alert.showAndWait();
                    } else if ("banned".equals(listIn.get(1))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Бан!");
                        alert.setHeaderText(null);
                        alert.setContentText("Ваш IP-адрес в бан-листе. Обратитесь к администратору.");
                        alert.showAndWait();
                    }
                } catch (ParserConfigurationException | SAXException | IOException e1) {
                    logger.error("Error on receiving message from server in RegFrame", e1);
                }
            }
        });
        Button noButton = new Button("Отмена");
        noButton.setPrefWidth(90);
        noButton.setOnAction(e -> {
            this.close();
            new AuthFrame(xmLin, xmlOut);
        });
        hBox.getChildren().addAll(yesButton, noButton);

        //Add everything to grid
        grid.getChildren().addAll(loginLabel, loginInput, passLabel, passInput);
        vBox.getChildren().addAll(scenetitle, grid, hBox);
        Scene scene = new Scene(vBox, 300, 200);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);
        this.setResizable(false);
        this.show();
        logger.info("Registration window build successfully.");

        /*On pressing "Enter" yesButton fires automatically*/
        this.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                yesButton.fire();
                ev.consume();
            }
        });
        /*Pressing TAB switches between login and password text fields*/
        loginInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                passInput.requestFocus();
                event.consume();
            }
        });
        passInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                loginInput.requestFocus();
                event.consume();
            }
        });
    }
}



