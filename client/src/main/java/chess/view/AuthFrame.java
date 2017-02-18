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
 * <code>AuthFrame</code> is a window for authentication user,
 * that was created before. This window opens to user first
 * as client is launched.
 */

public class AuthFrame extends Stage {
    private final Stage stage = this;
    private final static Logger logger = Logger.getLogger(AuthFrame.class.getClass());

    /**
     * Creating <code>AuthFrame</code> with given XMLin and XMLout
     * for communicating with remote server.
     *
     * @param xmLin for receiving messages from server.
     * @param xmlOut for sending messages to server.
     */
    public AuthFrame(XMLin xmLin, XMLout xmlOut) {
        this.setTitle("Шахматы онлайн");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20, 10, 20, 10));
        grid.setVgap(15);
        grid.setHgap(10);

        Text scenetitle = new Text("Авторизация");
        scenetitle.setId("scenetitle");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        Label loginLabel = new Label("Логин:");
        GridPane.setConstraints(loginLabel, 0, 0);

        TextField loginInput = new TextField();
        loginInput.setMinWidth(180);
        //loginInput.setFocusTraversable(false);
        loginInput.setPromptText("ваш логин");
        GridPane.setConstraints(loginInput, 1, 0);

        Label passLabel = new Label("Пароль:");
        GridPane.setConstraints(passLabel, 0, 1);

        PasswordField passInput = new PasswordField();
        passInput.setFocusTraversable(false);
        passInput.setPromptText("ваш пароль");
        GridPane.setConstraints(passInput, 1, 1);

        HBox hBox = new HBox();
        hBox.setSpacing(40);
        hBox.setAlignment(Pos.CENTER);
        Button createButton = new Button("Создать");
        createButton.setPrefWidth(90);
        createButton.setOnAction((e) -> {
            stage.close();
            new RegFrame(xmLin, xmlOut);
        });
        Button enterButton = new Button("Войти");
        enterButton.setPrefWidth(90);
        enterButton.setOnAction(e -> {
            if (loginInput.getText().equals("") || passInput.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add("Skin.css");
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Заполните поля логин/пароль");
                alert.showAndWait();
            } else {
                List<String> list = new ArrayList<>();
                list.add("auth");
                list.add(loginInput.getText());
                list.add(passInput.getText());
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    logger.error("Failed to send login/password to server from AuthFrame", e1);
                }
                List<String> listIn = null;
                try {
                    listIn = xmLin.receive();
                    if ("Ok".equals(listIn.get(1))) {
                        stage.close();
                        ProfileFrame profileFrame = new ProfileFrame(xmLin, xmlOut, listIn);
                    } else if ("admin".equals(listIn.get(1))) {
                        stage.close();
                        new AdminFrame(xmLin, xmlOut, listIn);
                    } else if ("error".equals(listIn.get(1))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Такой пользователь не зарегистрирован");
                        alert.showAndWait();
                    } else if ("online".equals(listIn.get(1))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Вы уже вошли из другого приложения");
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
                    logger.error("Error on receiving message from server in AuthFrame", e1);
                }
            }
        });
        hBox.getChildren().addAll(createButton, enterButton);
        //Add everything to grid
        grid.getChildren().addAll(loginLabel, loginInput, passLabel, passInput);

        //Scene scene = new Scene(grid, 300, 200);
        vBox.getChildren().addAll(scenetitle, grid, hBox);
        Scene scene = new Scene(vBox, 300, 200);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);
        this.setResizable(false);
        // при нажатии Enter срабатывает кнопка "Войти"
        this.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode().equals(KeyCode.ENTER)) {
                enterButton.fire();
                ev.consume();
            }
        });
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
        this.show();
        logger.info("Authentication window build successfully.");
    }
}