package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Text scenetitle = new Text("Новый игрок");
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
        hBox.setSpacing(30);
        hBox.setAlignment(Pos.CENTER);
        Button yesButton = new Button("Создать");
        yesButton.setOnAction(e -> {
            List<String> list = new ArrayList<String>();
            list.add("reg");
            list.add(loginInput.getText());
            list.add(passInput.getText());
            try {
                xmlOut.sendMessage(list);
            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                e1.printStackTrace();
            }
            List<String> listIn = null;
            try {
                listIn = xmLin.receive();
            } catch (ParserConfigurationException | SAXException | IOException | TransformerConfigurationException e1) {
                e1.printStackTrace();
            }
            if("accepted".equals(listIn.get(1))){
                this.close();
                new AuthFrame(xmLin, xmlOut);
            } else if ("denied".equals(list.get(1))) {
                System.out.println("Server cannot register such user, reason: " + list.get(2));
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
        this.setScene(scene);
        this.setResizable(false);
        this.show();
    }
}



