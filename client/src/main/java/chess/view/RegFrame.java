package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
 * Created by bobnewmark on 26.01.2017
 */
public class RegFrame extends Stage {
    //Stage stage = this;
    public RegFrame(final XMLin xmLin, final XMLout xmlOut) {
        this.setTitle("Регистрация нового пользователя");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20, 10, 20, 10));
        grid.setVgap(15);
        grid.setHgap(10);

        Text scenetitle = new Text("Новый игрок");
        scenetitle.setId("scenetitle");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        Label loginLabel = new Label("Логин:");
        GridPane.setConstraints(loginLabel, 0, 0);

        TextField loginInput = new TextField();
        loginInput.setMinWidth(180);
        loginInput.setFocusTraversable(false);
        loginInput.setPromptText("будет виден другим игрокам");
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
        Button yesButton = new Button("Создать");
        yesButton.setOnAction(e -> {
            if (loginInput.getText().isEmpty() || passInput.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add("Skin.css");
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Заполните поля логин/пароль");
                alert.showAndWait();

            } else {
                List<String> list = new ArrayList<String>();
                list.add("reg");
                list.add(loginInput.getText());
                list.add(passInput.getText());
                System.out.println("creating palyer");
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    e1.printStackTrace();
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
                        System.out.println("Server cannot register such user, reason: " + listIn.get(2));
                    }

                } catch (ParserConfigurationException | SAXException | IOException | TransformerConfigurationException e1) {
                    e1.printStackTrace();
                }

            }

            //xmlOut.sendMessage();
        });
        Button noButton = new Button("Отмена");
        noButton.setOnAction(e -> {
            this.close();
            new AuthFrame(xmLin, xmlOut);
        });
        hBox.getChildren().addAll(yesButton, noButton);


        //Add everything to grid
        grid.getChildren().addAll(loginLabel, loginInput, passLabel, passInput);

        //Scene scene = new Scene(grid, 300, 200);
        vBox.getChildren().addAll(scenetitle, grid, hBox);
        Scene scene = new Scene(vBox, 300, 200);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);
        this.setResizable(false);
        this.show();
    }
}



