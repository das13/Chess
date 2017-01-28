package chess.view;

import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by bobnewmark on 26.01.2017
 */
public class AdminFrame extends Stage {
    TableView<PlayerRow> table;

    public AdminFrame(final XMLin xmLin, final XMLout xmlOut) {
        this.setTitle("Admin access");

        TableColumn<PlayerRow, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setMinWidth(100);
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<PlayerRow, Integer> rankColumn = new TableColumn<>("Rank");
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
        hBox.setPadding(new Insets(10,10,10,10));
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
        ObservableList<PlayerRow> players = FXCollections.observableArrayList();
        players.add(new PlayerRow("Test", 10, "offline", "125"));
        players.add(new PlayerRow("Test2", 20, "offline", "525"));
        players.add(new PlayerRow("Test3", 30, "offline", "1525"));
        return players;
    }

    public class PlayerRow {

        private String login;
        private int rank;
        private String status;
        private String ip;

        public PlayerRow() {
            login = "empty";
            rank = 0;
            status = "offline";
            ip = "127...";
        }

        public PlayerRow(String login, int rank, String status, String ip) {
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

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
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
        table.getItems().add(new PlayerRow("login", 40, "offline", "127.0.0.1"));
        System.out.println("REFRESH");
    }

    public void banButtonClicked(){
        System.out.println("BAN!");
    }

    public void offerButtonClicked(){
        System.out.println("OFFER GAME");
    }

    public void exitButtonClicked(){
        this.close();
    }
}
