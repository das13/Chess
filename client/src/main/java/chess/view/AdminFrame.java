package chess.view;

import javafx.geometry.Insets;
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

    public AdminFrame() {
        this.setTitle("Admin access");

        TableColumn<String, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setMinWidth(200);
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<String, Integer> rankColumn = new TableColumn<>("Rank");
        rankColumn.setMinWidth(50);
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));

        TableColumn<String, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setMinWidth(100);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<String, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setMinWidth(50);
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

        Button banButton = new Button("Забанить/разбанить");
        banButton.setOnAction(e -> banButtonClicked());
        Button offerButton = new Button("Играть");
        offerButton.setOnAction(e -> offerButtonClicked());
        Button exitButton = new Button("Выход");
        exitButton.setOnAction(e -> exitButtonClicked());

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(banButton, offerButton, exitButton);

        TableView table = new TableView<>();
        //TODO table.getPlayers();
        table.getColumns().addAll(loginColumn, rankColumn, statusColumn, ipColumn);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(table, hBox);

        Scene scene = new Scene(vBox);
        this.setScene(scene);
        this.show();
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
