package chess.view;

import chess.Timer;
import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Created by bobnewmark on 30.01.2017
 */
public class GameFrame extends Stage implements Observer {
    private FXMLLoader loader;
    private Button offerDrawButton;
    private Button resignButton;
    private Scene scene;
    private Timer count;
    private Label opponentTimer;
    private Label yourTimer;
    private GridPane grid;
    private List<Pane> targets = new ArrayList<Pane>();
    private Map<String, Pane> board = Collections.synchronizedSortedMap(new TreeMap<>());
    private boolean isWhitePlayer;
    private int lastMoveFromX;
    private int lastMoveFromY;
    private int lastMoveToX;
    private int lastMoveToY;
    private ImageView lastMovedFigure;
    private ImageView lastTakenFigure;
    private List<String> listIn;
    private ImageView dragFigure;
    private MouseEvent currentEvent;
    private HBox blackMiniBox;
    private HBox whiteMiniBox;
    private ArrayList<String> playerInfo;

    public GameFrame(XMLin xmLin, final XMLout xmlOut, boolean isWhite, List<String> info) {
        ImageView castle;
        ImageView knight;
        ImageView bishop;
        ImageView queen;
        ImageView rivalcastle;
        ImageView rivalknight;
        ImageView rivalbishop;
        ImageView rivalqueen;
        playerInfo = (ArrayList<String>) info;
        Stage stage = this;
        isWhitePlayer = isWhite;
//      Для игрока белыми и черными подгружаются разные fxml
        if (isWhitePlayer) {
            loader = new FXMLLoader(getClass().getResource("/WhitePlayerBoard.fxml"));
            castle = new ImageView(new Image("/icons/castleWHITE.png"));
            knight = new ImageView(new Image("/icons/knightWHITE.png"));
            bishop = new ImageView(new Image("/icons/bishopWHITE.png"));
            queen = new ImageView(new Image("/icons/queenWHITE.png"));
            rivalcastle = new ImageView(new Image("/icons/castleBLACK.png"));
            rivalknight = new ImageView(new Image("/icons/knightBLACK.png"));
            rivalbishop = new ImageView(new Image("/icons/bishopBLACK.png"));
            rivalqueen = new ImageView(new Image("/icons/queenBLACK.png"));
        } else {
            loader = new FXMLLoader(getClass().getResource("/BlackPlayerBoard.fxml"));
            castle = new ImageView(new Image("/icons/castleBLACK.png"));
            knight = new ImageView(new Image("/icons/knightBLACK.png"));
            bishop = new ImageView(new Image("/icons/bishopBLACK.png"));
            queen = new ImageView(new Image("/icons/queenBLACK.png"));
            rivalcastle = new ImageView(new Image("/icons/castleWHITE.png"));
            rivalknight = new ImageView(new Image("/icons/knightWHITE.png"));
            rivalbishop = new ImageView(new Image("/icons/bishopWHITE.png"));
            rivalqueen = new ImageView(new Image("/icons/queenWHITE.png"));
        }

        Pane root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setTitle("Chess board");
        scene = new Scene(root, 700, 600);
        scene.getStylesheets().add("Skin.css");
        this.setScene(scene);
        this.setMinWidth(750);
        this.setMinHeight(650);
        this.show();
//      таймер выводит оставшееся время, кнопки ставят таймер на паузу и возобновляют
        opponentTimer = (Label) loader.getNamespace().get("opponentTimer");
        yourTimer = (Label) scene.lookup("#yourTimer");


        offerDrawButton = (Button) scene.lookup("#offerDrawButton");
        offerDrawButton.setOnMouseClicked(e -> {
            //grid.setDisable(true);
            count.stopTimer();
        });

//      пока что на кнопку для тестов повесил перемещение фигуры (для противника или рокировки)
        resignButton = (Button) scene.lookup("#resignButton");
        resignButton.setOnMouseClicked(e -> {
            //grid.setDisable(false);
            moveFromTo(lastMoveToX, lastMoveToY, lastMoveFromX, lastMoveFromY);
        });

//        предыдущий код для кнопки resignButton, возобновляет таймер
//        resignButton = (Button) scene.lookup("#resignButton");
//        resignButton.setOnMouseClicked(e -> {
//            count.startTimer();
//        });


        count = new Timer(this);
        Thread clock = new Thread(count);
        clock.setDaemon(true);
        clock.start();
        if (!isWhitePlayer) count.stopTimer();

        grid = (GridPane) scene.lookup("#grid");
        System.out.println("IS GRID NULL " + grid);
        if (!isWhitePlayer) grid.setDisable(true);
        whiteMiniBox = (HBox) loader.getNamespace().get("whiteMiniBox");
        blackMiniBox = (HBox) loader.getNamespace().get("blackMiniBox");


        List<ImageView> sources = new ArrayList<ImageView>();
        class DragOver implements EventHandler<DragEvent> {
            private Pane target;

            DragOver(Pane target) {
                this.target = target;
            }

            public void handle(DragEvent event) {
                if (event.getGestureSource() != target &&
                        event.getDragboard().hasImage()) {
                    event.acceptTransferModes(TransferMode.MOVE);

                }
                event.consume();
            }
        }
        class DragDropped implements EventHandler<DragEvent> {
            private Pane target;


            DragDropped(Pane target) {
                this.target = target;

            }

            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasImage()) {
                    for (Pane pane : targets) {
                        pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                    }
                    Pane pane = board.get(db.getString());
                    List<String> list = new ArrayList<String>();
                    list.add("move");
                    String x = getCoordinateX(pane);
                    String y = getCoordinateY(pane);
                    String x1 = getCoordinateX(target);
                    String y1 = getCoordinateY(target);

                    lastMoveFromX = Integer.parseInt(getCoordinateX(pane));
                    lastMoveFromY = Integer.parseInt(getCoordinateY(pane));
                    lastMoveToX = Integer.parseInt(getCoordinateX(target));
                    lastMoveToY = Integer.parseInt(getCoordinateY(target));

                    System.out.print("Drag from cell: ");
                    System.out.print("X:" + x);
                    System.out.println(" Y:" + y);
                    System.out.print("Drop on cell: ");
                    System.out.print("X:" + x1);
                    System.out.println(" Y:" + y1);
                    list.add(String.valueOf(x));
                    list.add(String.valueOf(y));
                    list.add(String.valueOf(x1));
                    list.add(String.valueOf(y1));
                    list.add(count.getTime());
                    grid.setDisable(true);
                    try {
                        xmlOut.sendMessage(list);
                    } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                        e1.printStackTrace();
                    }
                    Label label = null;
                    lastTakenFigure = null;

                    for (Node node : target.getChildren()) {
                        if (node instanceof Label) {
                            label = (Label) node;
                        } else if (node instanceof ImageView) {
                            lastTakenFigure = (ImageView) node;
                        }
                    }
                    target.getChildren().clear();
                    if (label != null) target.getChildren().add(label);
                    for (int i = 0; i < pane.getChildren().size(); i++) {
                        if (pane.getChildren().get(i).getClass().getSimpleName().equals("ImageView")) {
                            target.getChildren().add(pane.getChildren().get(i));
                            sendToMiniBox(lastTakenFigure);
                            count.stopTimer();
                        }
                    }
                    success = true;
                }
                resetSelected();
                event.setDropCompleted(success);
                event.consume();
            }
        }

        class DragDroppedOut implements EventHandler<DragEvent> {

            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                success = true;
                resetSelected();
                event.setDropCompleted(success);
                event.consume();
            }
        }
        class DragDetected implements EventHandler<MouseEvent> {
            private ImageView source;

            DragDetected(ImageView source) {
                this.source = source;
                lastMovedFigure = source;
            }

            public void handle(MouseEvent event) {
                currentEvent = event;
                List<String> list = new ArrayList<String>();
                list.add("drag");
                String x = getCoordinateX(source.getParent());
                String y = getCoordinateY(source.getParent());
                System.out.print("accessible x " + x);
                System.out.println(" accessible y " + y);
                list.add(x);
                list.add(y);
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    e1.printStackTrace();
                }
                Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(y + "" + x);
                content.putImage(source.getImage());
                System.out.println(y + "" + x);
                db.setContent(content);
                currentEvent.consume();

            }
        }

        root.setOnDragOver(new DragOver(root));
        root.setOnDragDropped(new DragDroppedOut());
        for (Node node : grid.getChildren()) {
            if (node.getClass().getSimpleName().equals("Pane")) {
                Pane pane = (Pane) node;
                String x = getCoordinateX(pane);
                String y = getCoordinateY(pane);

//                String x = getXforServer(pane);
//                String y = getYforServer(pane);
                board.put(y + "" + x, pane);
                for (Node n : pane.getChildren()) {
                    if (n.getClass().getSimpleName().equals("ImageView")
                            && (GridPane.getRowIndex(pane) != null)
                            && (GridPane.getRowIndex(pane) != 1)) {
                        ImageView source = (ImageView) n;
                        source.setOnDragDetected(new DragDetected(source));
                        sources.add(source);
                    }
                }
                System.out.print(GridPane.getColumnIndex(node) + " ");
                System.out.println(GridPane.getRowIndex(node));
            }
        }
        this.setScene(scene);
        this.show();
        Iterator<Map.Entry<String, Pane>> iter = board.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Pane> entry = iter.next();
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        class ReplaceButtonHandler implements EventHandler<ActionEvent> {
            private Pane pane;
            private ImageView figure;
            private String x;
            private String y;
            private String name;
            private Stage stage;
            ReplaceButtonHandler(ImageView figure, Pane pane, String x, String y, String name, Stage stage){
                this.figure = figure;
                this.pane = pane;
                this.x=x;
                this.y=y;
                this.name = name;
                this.stage = stage;
            }
            @Override
            public void handle(ActionEvent event) {
                pane.getChildren().clear();
                ImageView newFigure = new ImageView(figure.getImage());
                newFigure.setOnDragDetected(new DragDetected(newFigure));
                pane.getChildren().add(newFigure);
                List<String> list = new ArrayList<String>();
                list.add("replacePawn");
                String x1 = getCoordinateX(pane);
                String y1 = getCoordinateY(pane);
                list.add(x);
                list.add(y);
                list.add(x1);
                list.add(y1);
                list.add(name);
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    e1.printStackTrace();
                }
                stage.close();
            }

        }
        class MyTask<Void> extends Task<Void>

        {
            @Override
            public Void call() throws Exception {
                try {
                    listIn = xmLin.receive();
                    //firstConf = listIn.get(0);
                    //secondConf = listIn.get(1);
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        MyTask<Void> task = new MyTask<Void>();
        class MyHandler implements EventHandler {
            @Override
            public void handle(Event event) {
                if ("rivalMove".equals(listIn.get(0))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(stage);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("Ход");
                    alert.setHeaderText(null);
                    alert.setContentText("Соперник сделал ход");
                    alert.showAndWait();
                    grid.setDisable(false);
                    count.startTimer();
                    moveFromTo(Integer.parseInt(listIn.get(1)), Integer.parseInt(listIn.get(2)), Integer.parseInt(listIn.get(3)), Integer.parseInt(listIn.get(4)));
                    //opponentTimer.setText(listIn.get(5));
                } else if ("steps".equals(listIn.get(0))) {
                    targets.clear();
                    System.out.println("client sizeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+listIn.size());
                    for (String s : listIn) {
                        if (!s.equals("steps")) {
                            targets.add(board.get(s));
                        }
                    }
                    for (Pane pane : targets) {
                        pane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
                        pane.setOnDragOver(new DragOver(pane));
                        pane.setOnDragDropped(new DragDropped(pane));
                    }
                } else if ("cancel".equals(listIn.get(0))) {
                    grid.setDisable(false);
                    cancelLastMove();
                } else if ("checkmate".equals(listIn.get(0))) {
                    String message = "";
                    String rank = "";
                    if(playerInfo.get(3).equals(listIn.get(2))) {
                        int result = Integer.parseInt(playerInfo.get(5));
                        int correction = Integer.parseInt(listIn.get(3));
                        result += correction;
                        rank = String.valueOf(result);
                        playerInfo.set(5, rank);
                    }
                    if(playerInfo.get(3).equals(listIn.get(4))) {
                        int result = Integer.parseInt(playerInfo.get(5));
                        int correction = Integer.parseInt(listIn.get(5));
                        result += correction;
                        rank = String.valueOf(result);
                        playerInfo.set(5, rank);
                    }

                    if ("WHITE".equals(listIn.get(1))) {
                        message = "Мат! Игрок белыми победил, ваш новый рейтинг: " + rank;
                    } else {
                        message = "Мат! Игрок черными победил, ваш новый рейтинг: " + rank;
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(stage);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("Конец игры");
                    alert.setHeaderText(null);
                    alert.setContentText(message);
                    alert.showAndWait();
                    stage.close();
                    new ProfileFrame(xmLin, xmlOut, playerInfo);
                } else if ("replacePawn".equals(listIn.get(0))) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.setTitle("Выберите фигуру для замены");
                    Button castlebutton = new Button("Тура");
                    Button knightbutton = new Button("Конь");
                    Button bishopbutton = new Button("Офицер");
                    Button queenbutton = new Button("Ферзь");
                    castlebutton.setOnAction(new ReplaceButtonHandler(castle, board.get(listIn.get(3)), listIn.get(1), listIn.get(2), "Castle", dialogStage));
                    knightbutton.setOnAction(new ReplaceButtonHandler(knight, board.get(listIn.get(3)), listIn.get(1), listIn.get(2), "Knight", dialogStage));
                    bishopbutton.setOnAction(new ReplaceButtonHandler(bishop, board.get(listIn.get(3)), listIn.get(1), listIn.get(2), "Bishop", dialogStage));
                    queenbutton.setOnAction(new ReplaceButtonHandler(queen, board.get(listIn.get(3)), listIn.get(1), listIn.get(2), "Queen", dialogStage));
                    VBox vbox1 = new VBox(castle, castlebutton);
                    VBox vbox2 = new VBox(knight, knightbutton);
                    VBox vbox3 = new VBox(bishop, bishopbutton);
                    VBox vbox4= new VBox(queen, queenbutton);
                    vbox1.setAlignment(Pos.CENTER);
                    vbox1.setPadding(new Insets(15));
                    vbox2.setAlignment(Pos.CENTER);
                    vbox2.setPadding(new Insets(15));
                    vbox3.setAlignment(Pos.CENTER);
                    vbox3.setPadding(new Insets(15));
                    vbox4.setAlignment(Pos.CENTER);
                    vbox4.setPadding(new Insets(15));
                    HBox hbox= new HBox(vbox1, vbox2, vbox3, vbox4);
                    dialogStage.setScene(new Scene(hbox));
                    dialogStage.initOwner(stage);
                    dialogStage.show();
                } else if ("rivalReplace".equals(listIn.get(0))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(stage);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("Ход");
                    alert.setHeaderText(null);
                    alert.setContentText("Соперник сделал ход");
                    alert.showAndWait();
                    grid.setDisable(false);
                    count.startTimer();
                    moveFromTo(Integer.parseInt(listIn.get(2)), Integer.parseInt(listIn.get(1)), Integer.parseInt(listIn.get(4)), Integer.parseInt(listIn.get(3)));
                    //opponentTimer.setText(listIn.get(5));
                    ImageView figure = null;
                    if(listIn.get(5).equals("Castle")) figure = rivalcastle;
                    if(listIn.get(5).equals("Knight")) figure = rivalknight;
                    if(listIn.get(5).equals("Bishop")) figure = rivalbishop;
                    if(listIn.get(5).equals("Queen")) figure = rivalqueen;
                    board.get(listIn.get(3)+listIn.get(4)).getChildren().clear();
                    ImageView newFigure = new ImageView(figure.getImage());
                    newFigure.setOnDragDetected(new DragDetected(newFigure));
                    findPane(Integer.parseInt(listIn.get(3)), Integer.parseInt(listIn.get(4))).getChildren().add(newFigure);
                }
                MyTask myTask = new MyTask<Void>();
                myTask.setOnSucceeded(new MyHandler());
                Thread thread1 = new Thread(myTask);
                thread1.setDaemon(true);
                thread1.start();
            }
        }


        task.setOnSucceeded(new MyHandler());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    private void resetSelected() {
        for (Pane pane : targets) {
            pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            pane.setOnDragOver(null);
            pane.setOnDragDropped(null);
            pane.setOnDragExited(null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            yourTimer.setText((String) arg);
        });
    }

    // Переводит координаты клетки в формат для сервера, пока выводит просто в консоль
    private String getCoordinateX(Node node) {
        int x;
        if (GridPane.getColumnIndex(node) == null) {
            x = 0;
        } else {
            x = GridPane.getColumnIndex(node);
        }
        if (!isWhitePlayer) {
            x = translateForBlack(x);
        }

        return String.valueOf(x);
    }

    private String getCoordinateY(Node node) {
        int y;
        if (GridPane.getRowIndex(node) == null) {
            y = 0;
        } else {
            y = GridPane.getRowIndex(node);
        }
        if (!isWhitePlayer) {
            y = translateForBlack(y);
        }
        return String.valueOf(y);
    }

    private String getXforServer(Node node) {
        int x;
        if (GridPane.getColumnIndex(node) == null) {
            x = 0;
        } else {
            x = GridPane.getColumnIndex(node);
        }
        return String.valueOf(x);
    }

    private String getYforServer(Node node) {
        int y;
        if (GridPane.getRowIndex(node) == null) {
            y = 0;
        } else {
            y = GridPane.getRowIndex(node);
        }
        return String.valueOf(y);
    }

    // Переводит координаты клетки в формат для сервера, пока выводит просто в консоль
    private void getCoordinates(Pane pane /*, black or white player*/) {
        int x;
        int y;
        if (GridPane.getColumnIndex(pane) == null) {
            x = 0;
        } else {
            x = GridPane.getColumnIndex(pane);
        }
        if (GridPane.getRowIndex(pane) == null) {
            y = 0;
        } else {
            y = GridPane.getRowIndex(pane);
        }
//  FOR BLACK PLAYER
//        if (true /*This is black player*/) {
//            x = translateForBlack(x);
//            y = translateForBlack(y);
//        }
        System.out.print("X:" + x);
        System.out.println(" Y:" + y);
    }

    private int translateForBlack(int coordinate) {
        return 7 - coordinate;
    }

    // Перемещает фигуру согласно заданным координатам
    private void moveFromTo(int fromX, int fromY, int toX, int toY) {
        Pane source = findPane(fromX, fromY);
        Pane target = findPane(toX, toY);

        ImageView mini = null;
        ImageView image = new ImageView();
        Label label = null;
        System.out.println("TARGET CHILDREN: " + target.getChildren().size());

        for (Node node : target.getChildren()) {
            if (node instanceof Label) {
                label = (Label) node;
            } else if (node instanceof ImageView) {
                System.out.println("THIS IS IMAGE");
                mini = (ImageView) node;
            }
        }
        target.getChildren().clear();
        sendToMiniBox(mini);
        if (label != null) target.getChildren().add(label);

        for (Node node : source.getChildren()) {
            if (node instanceof ImageView) {
                image = (ImageView) node;
            }
        }
        target.getChildren().add(image);

    }

    // находит и возвращает Pane на доске по заданным координатам согласно положению на сервере
    private Pane findPane(int x, int y) {
        if (!isWhitePlayer) {
            x = translateForBlack(x);
            y = translateForBlack(y);
        }
        System.out.println("looking for pane with X:" + x + " Y:" + y);
        GridPane grid = (GridPane) scene.lookup("#grid");
        Pane pane = null;
        ObservableList<Node> children = grid.getChildren();
        if (x == 0 && y == 0) {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == null && GridPane.getRowIndex(node) == null) {
                    System.out.println(GridPane.getColumnIndex(node));
                    System.out.println(GridPane.getRowIndex(node));
                    pane = (Pane) node;
                    break;
                }
            }
        } else if (x == 0) {
            for (Node node : children) {
                if (GridPane.getRowIndex(node) == null) continue;
                if (GridPane.getColumnIndex(node) == null && GridPane.getRowIndex(node) == y) {
                    pane = (Pane) node;
                    break;
                }
            }
        } else if (y == 0) {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == null) continue;
                if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == null) {
                    pane = (Pane) node;
                    break;
                }
            }
        } else {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == null || GridPane.getRowIndex(node) == null) continue;
                if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                    pane = (Pane) node;
                    break;
                }
            }
        }
        return pane;
    }

    public void cancelLastMove() {
        moveFromTo(lastMoveToX, lastMoveToY, lastMoveFromX, lastMoveFromY);
        if (lastTakenFigure != null) {
            lastTakenFigure.fitHeightProperty().unbind();
            lastTakenFigure.setFitHeight(60);
            lastTakenFigure.setFitWidth(60);
            findPane(lastMoveToX, lastMoveToY).getChildren().add(lastTakenFigure);
            lastTakenFigure.setLayoutY(1);
            lastTakenFigure.setLayoutX(1);
        }

    }


    public void sendToMiniBox(ImageView figure) {
        System.out.println("inside of sendtominibox");
        if (figure == null) return;
        figure.setPreserveRatio(true);
//        if (figure.getId().contains("White") && isWhitePlayer) return;
//        if (figure.getId().contains("Black") && !isWhitePlayer) return;
        if (figure.getId().contains("Black")) {
            figure.fitHeightProperty().bind(whiteMiniBox.heightProperty());
            whiteMiniBox.getChildren().add(figure);
        } else if (figure.getId().contains("White")) {
            figure.fitHeightProperty().bind(blackMiniBox.heightProperty());
            blackMiniBox.getChildren().add(figure);
        }
    }
}




