package chess;

import javafx.application.Application;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain extends Application {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public ClientMain() throws IOException {
        socket = new Socket("localhost", 2543);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Шахматы онлайн");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(40, 25, 25, 25));
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
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
                    out.println("auth");
                    out.println(userTextField.getText());
                    out.println(pwBox.getText());
                    try {
                        if(in.readLine().equals("Ok")){
                            primaryStage.close();
                            ProfileFrame profileFrame = new ProfileFrame(in, out);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        primaryStage.show();
    }

}
