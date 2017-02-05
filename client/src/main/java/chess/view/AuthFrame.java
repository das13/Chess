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
 * Created by Admin on 26.01.2017.
 */
public class AuthFrame extends Stage {
    Stage stage = this;

    public AuthFrame(XMLin xmLin, XMLout xmlOut) {
        stage.setTitle("Шахматы онлайн");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(40, 25, 25, 25));
        Scene scene = new Scene(grid, 300, 200);
        this.setScene(scene);
        Text scenetitle = new Text("Авторизация");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 1, 0, 1, 1);
        Label userName = new Label("Логин:");
        grid.add(userName, 0, 1);
        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        Label pw = new Label("Пароль:");
        grid.add(pw, 0, 2);
        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        Button btn = new Button("Войти");
        btn.setMinWidth(70);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);


        Button btnReg = new Button("Создать");
        btnReg.setMinWidth(70);
        hbBtn.getChildren().addAll(btnReg, btn);
        grid.add(hbBtn, 1, 4);
        btnReg.setOnAction((e) -> {
            stage.close();
            new RegFrame(xmLin, xmlOut);
        });
        btn.setOnAction(e -> {
            if (userTextField.getText().equals("") || pwBox.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add("Skin.css");
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Заполните поля логин/пароль");
                alert.showAndWait();
            } else {
                List<String> list = new ArrayList<String>();
                list.add("auth");
                list.add(userTextField.getText());
                list.add(pwBox.getText());
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    e1.printStackTrace();
                }
                List<String> listIn = null;
                try {
                    listIn = xmLin.receive();
                    if ("Ok".equals(listIn.get(1))) {
                        stage.close();
                        ProfileFrame profileFrame = new ProfileFrame(xmLin, xmlOut, listIn);
                    } else if ("admin".equals(listIn.get(1))) {
                        stage.close();
                        new AdminFrame(xmLin, xmlOut);
                    } else if ("error".equals(listIn.get(1))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Такой пользователь не зарегистрирован");
                        alert.showAndWait();
                    }
                } catch (ParserConfigurationException | SAXException | IOException | TransformerConfigurationException e1) {
                    e1.printStackTrace();
                }
            }
        });
        // при нажатии Enter срабатывает кнопка "Войти"
        this.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                btn.fire();
                ev.consume();
            }
        });
        this.show();
    }
}
