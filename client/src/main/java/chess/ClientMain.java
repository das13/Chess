package chess;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import chess.view.AuthFrame;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;


public class ClientMain extends Application {
    final static Logger logger = Logger.getLogger(ClientMain.class.getClass());
    private Socket socket;
    private XMLout xmlOut;
    private XMLin xmLin;
    public ClientMain() throws IOException {
        socket = new Socket(Constants.HOST, Constants.PORT);
        xmlOut = new XMLout(socket.getOutputStream());
        xmLin = new XMLin(socket.getInputStream());
    }

    public static void main(String[] args) throws IOException {
        launch(args);

    }

    public void start(Stage primaryStage) throws Exception {
           primaryStage=new AuthFrame(xmLin, xmlOut);
        logger.info("Client launched");
    }

}
