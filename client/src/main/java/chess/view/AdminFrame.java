package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
public class AdminFrame extends Stage {
    TableView<PlayerRow> table;
    XMLin xmLin;
    XMLout xmLout;
    ObservableList<PlayerRow> players;

    public AdminFrame(final XMLin xmLin, final XMLout xmlOut) {
        this.xmLin = xmLin;
        this.xmLout = xmlOut;
        this.setTitle("Admin access");
        players = FXCollections.observableArrayList();

        TableColumn<PlayerRow, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setMinWidth(100);
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<PlayerRow, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setMinWidth(100);
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));

        TableColumn<PlayerRow, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setMinWidth(100);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<PlayerRow, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setMinWidth(100);
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(e -> refreshButtonClicked());
        Button banButton = new Button("Забанить/разбанить");
        banButton.setOnAction(e -> banButtonClicked());
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
        table.setItems(getPlayers());
        table.getColumns().addAll(loginColumn, rankColumn, statusColumn, ipColumn);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(table, hBox);

        Scene scene = new Scene(vBox);
        this.setScene(scene);
        this.show();


    }

    public ObservableList<PlayerRow> getPlayers() {
        players.clear();
        List<String> list = new ArrayList<String>();
        list.add("admin_getPlayers");
        try {
            xmLout.sendMessage(list);
        } catch (ParserConfigurationException | TransformerConfigurationException | IOException e) {
            e.printStackTrace();
        }
        List<String> listIn = null;
        try {
            listIn = xmLin.receive();
        } catch (ParserConfigurationException | SAXException | IOException | TransformerConfigurationException e) {
            e.printStackTrace();
        }
        if ("admin_getPlayers".equals(listIn.get(0))) {
            for (int i = 1; i < listIn.size(); i += 4) {
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

        public PlayerRow() {
            login = "empty";
            rank = "0";
            status = "offline";
            ip = "127...";
        }

        public PlayerRow(String login, String rank, String status, String ip) {
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


    public void refreshButtonClicked() {
        players.clear();
    }

    public void banButtonClicked() {
        System.out.println("BAN!");
    }

    public void offerButtonClicked() {
        this.close();
        try {
            GameFrame game = new GameFrame(xmLin, xmLout);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("OFFER GAME");
    }

    public void exitButtonClicked() {
        this.close();
    }
}
