package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
 * Created by Admin on 26.01.2017.
 */
public class AuthFrame extends Stage {
    Stage stage = this;
    public AuthFrame(final XMLin xmLin, final XMLout xmlOut){
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
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 4, 2, 1);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (userTextField.getText().equals("") || pwBox.getText().equals("")) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Enter login and password");
                } else {
                    actiontarget.setText("");
                    try {
                        List<String> list = new ArrayList<String>();
                        list.add("auth");
                        list.add(userTextField.getText());
                        list.add(pwBox.getText());
                        xmlOut.sendMessage(list);
                        List<String> listOut = xmLin.receive();
                        if(listOut.get(1).equals("Ok")){
                            stage.close();
                            ProfileFrame profileFrame = new ProfileFrame(xmLin, xmlOut, listOut);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (ParserConfigurationException e1) {
                        e1.printStackTrace();
                    } catch (TransformerConfigurationException e1) {
                        e1.printStackTrace();
                    } catch (SAXException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        this.show();
    }
}
