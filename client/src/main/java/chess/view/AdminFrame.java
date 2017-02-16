package chess.view;

import chess.ClientMain;
import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>AdminFrame</code> is a administrative window for a player with
 * additional abilities. Such user can see other players statuses,
 * IP addresses and also can ban/un-ban any player excluding himself.
 * User of <code>AdminFrame</code> can play chess with other players as well.
 */
class AdminFrame extends Stage {
    private final XMLin xmLin;
    private final XMLout xmLout;
    private String firstConf;
    private String secondConf;
    private List<String> listIn;
    private final ObservableList<PlayerRow> players;
    private List<String> info;
    private TableView<PlayerRow> table;
    private final static Logger logger = Logger.getLogger(AdminFrame.class.getClass());

    /**
     * Creates <code>AdminFrame</code> with given XMLin and XMLout
     * for communicating with server and list of superuser information.
     *
     * @param xmLin     for receiving messages from server.
     * @param xmlOut    for sending messages to server.
     * @param adminInfo current user information.
     */
    AdminFrame(final XMLin xmLin, final XMLout xmlOut, List<String> adminInfo) {
        Stage stage = this;
        this.xmLin = xmLin;
        this.xmLout = xmlOut;
        this.setTitle("Admin access");
        info = adminInfo;
        players = FXCollections.observableArrayList();
        /*Setting login column*/
        TableColumn<PlayerRow, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setMinWidth(100);
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        /*Setting rank column*/
        TableColumn<PlayerRow, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setMinWidth(100);
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        /*Setting status column*/
        TableColumn<PlayerRow, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setMinWidth(100);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        /*Setting IP column*/
        TableColumn<PlayerRow, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setMinWidth(100);
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        /*Adding buttons*/
        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(e -> refreshButtonClicked());
        Button banButton = new Button("Забанить/разбанить");
        banButton.setOnAction(e -> banButtonClicked());
        banButton.setDisable(true);
        Button offerButton = new Button("Играть");
        offerButton.setOnAction(e -> offerButtonClicked());
        Button exitButton = new Button("Выход");
        exitButton.setOnAction(e -> exitButtonClicked());

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(refreshButton, banButton, offerButton, exitButton);

        table = new TableView<>();
        table.getColumns().addAll(loginColumn, rankColumn, statusColumn, ipColumn);
        table.setItems(players);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(table, hBox);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);
        this.show();
        logger.info("Administrator window build successfully.");
        refreshButton.fire();

        MyTask<Void> task = new MyTask<>();

        /**In case of different possible answers from server*/
        class MyHandler implements EventHandler {
            @Override
            public void handle(Event event) {
                if ("reg".equals(listIn.get(0))) {
                    Platform.runLater(() -> {
                        table.setItems(getPlayers());
                    });
                    MyTask myTask = new MyTask<Void>();
                    myTask.setOnSucceeded(new MyHandler());
                    Thread thread1 = new Thread(myTask);
                    thread1.setDaemon(true);
                    thread1.start();
                }
                if ("admin_getPlayers".equals(listIn.get(0))) {
                    Platform.runLater(() -> {
                        table.setItems(getPlayers());
                    });
                    MyTask myTask = new MyTask<Void>();
                    myTask.setOnSucceeded(new MyHandler());
                    Thread thread1 = new Thread(myTask);
                    thread1.setDaemon(true);
                    thread1.start();
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
                    new AdminFrame(xmLin, xmlOut, info);
                }
                if ("confirmresponse".equals(firstConf)) {
                    if ("Ok".equals(secondConf)) {
                        stage.close();
                        new GameFrame(xmLin, xmlOut, true, info);
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
                if ("refresh".equals(firstConf)) {
                    stage.close();
                    new AdminFrame(xmLin, xmlOut, info);
                }
            }
        }
        task.setOnSucceeded(new MyHandler());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Class for receiving List of String values from XMLin
     */
    class MyTask<Void> extends Task<Void> {
        @Override
        public Void call() throws Exception {
            try {
                List<String> list = xmLin.receive();
                firstConf = list.get(0);
                secondConf = list.get(1);
                listIn = list;
            } catch (ParserConfigurationException | SAXException | IOException e) {
                logger.error("Error receiving data from server on AdminFrame", e);
            }
            return null;
        }
    }

    private ObservableList<PlayerRow> getPlayers() {
        players.clear();
        if ("admin_getPlayers".equals(listIn.get(0))) {
            for (int i = 2; i < listIn.size(); i += 4) {
                players.add(new PlayerRow(listIn.get(i), listIn.get(i + 1), listIn.get(i + 2), listIn.get(i + 3)));
            }
        }
        return players;
    }

    public class PlayerRow {

        private String login;
        private String rank;
        private String status;
        private String ip;

        PlayerRow(String login, String rank, String status, String ip) {
            this.login = login;
            this.rank = rank;
            this.status = status;
            this.ip = ip;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }


    private void refreshButtonClicked() {
        players.clear();
        List<String> list = new ArrayList<>();
        list.add("admin_getPlayers");
        try {
            xmLout.sendMessage(list);
        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
            logger.error("Error requesting freeplayers from server on AdminFrame", e1);
        }
    }

    private void banButtonClicked() {
        System.out.println("BAN!");
    }

    private void offerButtonClicked() {
        List<String> list = new ArrayList<>();
        list.add("callPlayer");
        PlayerRow row = table.getSelectionModel().getSelectedItem();
        String name = row.getLogin();
        list.add(name);
        try {
            xmLout.sendMessage(list);
            System.out.println("offer sent");
        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
            logger.error("Error sending game offer to" + name + "from server on AdminFrame", e1);
        }
    }

    private void exitButtonClicked() {
        List<String> list = new ArrayList<>();
        list.add("logout");
        try {
            xmLout.sendMessage(list);
        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
            logger.error("Error logging out from AdminFrame", e1);
        }
    }
}
