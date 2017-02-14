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
import javafx.scene.control.ListView;
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

import static java.lang.Integer.*;

/**
 * GameFrame is a chessboard realization, with timer, moves notation and everything needed.
 * Initiator of a game plays white pieces, the opponent plays black. In case of playing black pieces
 * board is considered turned upside down, so server understands moves correctly. While opponent
 * makes his move the board of current player doesn't let pick, drag pieces. Opponent's pieces
 * are not available to move at any time.
 */
public class GameFrame extends Stage implements Observer {
    private final Scene scene;
    private Timer count;
    private final Label opponentTimer;
    private final Label yourTimer;
    private final GridPane grid;
    private final List<Pane> targets = new ArrayList<>();
    private final Map<String, Pane> board = Collections.synchronizedSortedMap(new TreeMap<>());
    private final boolean isWhitePlayer;
    private int lastMoveFromX;
    private int lastMoveFromY;
    private int lastMoveToX;
    private int lastMoveToY;
    private ImageView lastMovedFigure;
    private ImageView lastTakenFigure;
    private List<String> listIn;
    private ImageView dragFigure;
    private MouseEvent currentEvent;
    private final HBox blackMiniBox;
    private final HBox whiteMiniBox;
    private final ArrayList<String> playerInfo;
    private final ListView<String> movesRecord;

    GameFrame(XMLin xmLin, final XMLout xmlOut, boolean isWhite, List<String> info) {
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

        /*Depending on white or black pieces player is playing, GameFrame loads different fxml files*/
        FXMLLoader loader;
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

        opponentTimer = (Label) loader.getNamespace().get("opponentTimer");
        yourTimer = (Label) scene.lookup("#yourTimer");
        grid = (GridPane) scene.lookup("#grid");
        Button offerDrawButton = (Button) scene.lookup("#offerDrawButton");
        offerDrawButton.setOnMouseClicked(e -> {
            count.stopTimer();
        });

        Button resignButton = (Button) scene.lookup("#resignButton");
        resignButton.setOnMouseClicked(e -> {
            moveFromTo(lastMoveToX, lastMoveToY, lastMoveFromX, lastMoveFromY);
        });

        count = new Timer(this);
        Thread clock = new Thread(count);
        clock.setDaemon(true);
        clock.start();
        if (!isWhitePlayer) count.stopTimer();

        if (!isWhitePlayer) grid.setDisable(true);
        whiteMiniBox = (HBox) loader.getNamespace().get("whiteMiniBox");
        blackMiniBox = (HBox) loader.getNamespace().get("blackMiniBox");
        movesRecord = (ListView<String>) scene.lookup("#movesRecord");


        List<ImageView> sources = new ArrayList<>();

        /*Serving class for drag and drop of pieces*/
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

        /*Serving class for drag and drop of pieces*/
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
                    List<String> list = new ArrayList<>();
                    list.add("move");
                    String x = getCoordinateX(pane);
                    String y = getCoordinateY(pane);
                    String x1 = getCoordinateX(target);
                    String y1 = getCoordinateY(target);

                    lastMoveFromX = parseInt(getCoordinateX(pane));
                    lastMoveFromY = parseInt(getCoordinateY(pane));
                    lastMoveToX = parseInt(getCoordinateX(target));
                    lastMoveToY = parseInt(getCoordinateY(target));

                    movesRecord.getItems().add(movesRecord(lastMoveFromX, lastMoveFromY, lastMoveToX, lastMoveToY));
                    movesRecord.scrollTo(movesRecord.getItems().size() - 1);

                    /*if king is making castling, castle automatically moves to the appropriate position*/
                    if (lastMovedFigure.getId().contains("king") && lastMoveFromY == lastMoveToY
                            && (lastMoveToX - lastMoveFromX == 2 || lastMoveFromX - lastMoveToX == 2)) {
                        String where = "";
                        if (lastMoveToX == 6) {
                            moveFromTo(7, lastMoveToY, 5, lastMoveToY);
                        }
                        if (lastMoveToX == 2) {
                            moveFromTo(0, lastMoveToY, 3, lastMoveToY);
                            where = "-O";
                        }
                        movesRecord.getItems().remove(movesRecord.getItems().size() - 1);
                        movesRecord.getItems().add("O-O" + where);
                        movesRecord.scrollTo(movesRecord.getItems().size() - 1);
                    }

                    list.add(String.valueOf(x));
                    list.add(String.valueOf(y));
                    list.add(String.valueOf(x1));
                    list.add(String.valueOf(y1));
                    list.add(count.getTime());
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
                    grid.setDisable(true);
                }
                resetSelected();
                event.setDropCompleted(success);
                event.consume();
            }
        }
        /*Serving class for drag and drop of pieces*/
        class DragDroppedOut implements EventHandler<DragEvent> {
            public void handle(DragEvent event) {
                boolean success;
                success = true;
                resetSelected();
                event.setDropCompleted(success);
                event.consume();
            }
        }

        /*Serving class for drag and drop of pieces*/
        class DragDetected implements EventHandler<MouseEvent> {
            private ImageView source;

            DragDetected(ImageView source) {
                this.source = source;
            }

            public void handle(MouseEvent event) {
                currentEvent = event;
                List<String> list = new ArrayList<String>();
                list.add("drag");
                String x = getCoordinateX(source.getParent());
                String y = getCoordinateY(source.getParent());
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
                lastMovedFigure = source;
                db.setContent(content);
                currentEvent.consume();
            }
        }
        root.setOnDragOver(new DragOver(root));
        root.setOnDragDropped(new DragDroppedOut());

        /*adding all ImageViews to "sources" List to make them available
        * for picking and drag-and-dropping*/
        for (Node node : grid.getChildren()) {
            if (node.getClass().getSimpleName().equals("Pane")) {
                Pane pane = (Pane) node;
                String x = getCoordinateX(pane);
                String y = getCoordinateY(pane);
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
            }
        }
        this.setScene(scene);
        this.show();

        /*Serving class for replacing the pawn that reached the end of board*/
        class ReplaceButtonHandler implements EventHandler<ActionEvent> {
            private Pane pane;
            private ImageView figure;
            private String x;
            private String y;
            private String name;
            private Stage stage;

            ReplaceButtonHandler(ImageView figure, Pane pane, String x, String y, String name, Stage stage) {
                this.figure = figure;
                this.pane = pane;
                this.x = x;
                this.y = y;
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

        /*Serving class for receiving messages from server*/
        class MyTask<Void> extends Task<Void> {
            @Override
            public Void call() throws Exception {
                try {
                    listIn = xmLin.receive();
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        /*Creating MyTask for receiving messages from server*/
        MyTask<Void> task = new MyTask<Void>();

        /*Serving class for reacting according to what server sent*/
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
                    moveFromTo(parseInt(listIn.get(1)), parseInt(listIn.get(2)), parseInt(listIn.get(3)), parseInt(listIn.get(4)));
                    movesRecord.getItems().add("соперник: " + movesRecord(parseInt(listIn.get(1)), parseInt(listIn.get(2)), parseInt(listIn.get(3)), parseInt(listIn.get(4))));
                    opponentTimer.setText(listIn.get(5));
                } else if ("steps".equals(listIn.get(0))) {
                    targets.clear();
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
                    MyTask<Void> task = new MyTask<Void>();

                } else if ("checkmate".equals(listIn.get(0))) {
                    String message;
                    String rank = "";
                    if (playerInfo.get(3).equals(listIn.get(2))) {
                        int result = parseInt(playerInfo.get(5));
                        int correction = parseInt(listIn.get(3));
                        result += correction;
                        rank = String.valueOf(result);
                        playerInfo.set(5, rank);
                    }
                    if (playerInfo.get(3).equals(listIn.get(4))) {
                        int result = parseInt(playerInfo.get(5));
                        int correction = parseInt(listIn.get(5));
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
                    stage.close();

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
                    VBox vbox4 = new VBox(queen, queenbutton);
                    vbox1.setAlignment(Pos.CENTER);
                    vbox1.setPadding(new Insets(15));
                    vbox2.setAlignment(Pos.CENTER);
                    vbox2.setPadding(new Insets(15));
                    vbox3.setAlignment(Pos.CENTER);
                    vbox3.setPadding(new Insets(15));
                    vbox4.setAlignment(Pos.CENTER);
                    vbox4.setPadding(new Insets(15));
                    HBox hbox = new HBox(vbox1, vbox2, vbox3, vbox4);
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
                    moveFromTo(parseInt(listIn.get(2)), parseInt(listIn.get(1)), parseInt(listIn.get(4)), parseInt(listIn.get(3)));
                    opponentTimer.setText(listIn.get(5));
                    ImageView figure = null;
                    if (listIn.get(5).equals("Castle")) figure = rivalcastle;
                    if (listIn.get(5).equals("Knight")) figure = rivalknight;
                    if (listIn.get(5).equals("Bishop")) figure = rivalbishop;
                    if (listIn.get(5).equals("Queen")) figure = rivalqueen;
                    board.get(listIn.get(3) + listIn.get(4)).getChildren().clear();
                    ImageView newFigure = new ImageView(figure != null ? figure.getImage() : null);
                    newFigure.setOnDragDetected(new DragDetected(newFigure));
                    findPane(parseInt(listIn.get(3)), parseInt(listIn.get(4))).getChildren().add(newFigure);
                } else if ("castling".equals(listIn.get(0))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(stage);
                    alert.getDialogPane().getStylesheets().add("Skin.css");
                    alert.setTitle("Ход");
                    alert.setHeaderText(null);
                    alert.setContentText("Соперник выполнил рокировку");
                    alert.showAndWait();
                    grid.setDisable(false);
                    count.startTimer();

                    if ("white".equals(listIn.get(1))) {
                        if ("kingside".equals(listIn.get(2))) {
                            moveFromTo(4, 7, 6, 7);
                            moveFromTo(7, 7, 5, 7);
                            movesRecord.getItems().add("соперник: O-O");
                        } else {
                            moveFromTo(4, 7, 2, 7);
                            moveFromTo(0, 7, 3, 7);
                            movesRecord.getItems().add("соперник: О-O-O");
                        }
                    } else {
                        if ("kingside".equals(listIn.get(2))) {
                            moveFromTo(4, 0, 6, 0);
                            moveFromTo(7, 0, 5, 0);
                            movesRecord.getItems().add("соперник: O-O");
                        } else {
                            moveFromTo(4, 0, 2, 0);
                            moveFromTo(0, 0, 3, 0);
                            movesRecord.getItems().add("соперник: О-O-O");
                        }
                    }
                    movesRecord.scrollTo(movesRecord.getItems().size() - 1);
                    opponentTimer.setText(listIn.get(3));
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

    /**
     * Removes selection from Panes that were marked
     * as accessible for moving a piece
     */
    private void resetSelected() {
        for (Pane pane : targets) {
            pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            pane.setOnDragOver(null);
            pane.setOnDragDropped(null);
            pane.setOnDragExited(null);
        }
    }

    /**
     * Updates player's timer approximately every second
     * as yourTimer updates it's value
     *
     * @param o   Timer in a parallel process
     * @param arg updated value sent by Timer
     */
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            yourTimer.setText((String) arg);
        });
    }

    /**
     * Returns String value of a number of a column of a given Node
     * for sending it to server.
     *
     * @param node Node which column number is needed
     * @return String value of a number of a column of node
     */
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

    /**
     * Returns String value of a number of a row of a given Node
     * for sending it to server.
     *
     * @param node Node which row number is needed
     * @return String value of a number of a row of node
     */
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

    /**
     * Converts coordinate from current GridPane for board on server.
     * It is needed as player who plays with black pieces has his board
     * turned upside down.
     *
     * @param coordinate int of coordinate
     * @return int of coordinate on server board
     */
    private int translateForBlack(int coordinate) {
        return 7 - coordinate;
    }

    /**
     * Moves ImageView of piece from one pane to another.
     * If there's another ImageView on the target Pane it goes to
     * one of miniboxes according to what color of pieces the player is playing
     *
     * @param fromX int column number of source Pane
     * @param fromY int row number of source Pane
     * @param toX   int column number of target Pane
     * @param toY   int row number of target Pane
     */
    private void moveFromTo(int fromX, int fromY, int toX, int toY) {
        Pane source = findPane(fromX, fromY);
        Pane target = findPane(toX, toY);

        ImageView mini = null;
        ImageView image = new ImageView();
        Label label = null;

        for (Node node : target.getChildren()) {
            if (node instanceof Label) {
                label = (Label) node;
            } else if (node instanceof ImageView) {
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

    /**
     * Finds a Pane by its coordinates according to board coordinates on server
     *
     * @param x int column number
     * @param y int row number
     * @return Pane that matches coordinates above
     */
    private Pane findPane(int x, int y) {
        if (!isWhitePlayer) {
            x = translateForBlack(x);
            y = translateForBlack(y);
        }
        GridPane grid = (GridPane) scene.lookup("#grid");
        Pane pane = null;
        ObservableList<Node> children = grid.getChildren();
        if (x == 0 && y == 0) {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == null && GridPane.getRowIndex(node) == null) {
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

    /**
     * Cancels last made move and returns pieces on their positions
     */
    private void cancelLastMove() {
        moveFromTo(lastMoveToX, lastMoveToY, lastMoveFromX, lastMoveFromY);
        if (lastTakenFigure != null) {
            lastTakenFigure.fitHeightProperty().unbind();
            lastTakenFigure.setFitHeight(60);
            lastTakenFigure.setFitWidth(60);
            findPane(lastMoveToX, lastMoveToY).getChildren().add(lastTakenFigure);
            lastTakenFigure.setLayoutY(1);
            lastTakenFigure.setLayoutX(1);
        }
        grid.setDisable(false);
    }

    /**
     * Puts record notes of both players moves on the list.
     *
     * @param fromX int column number of a cell from where the piece moved
     * @param fromY int row number of a cell from where the piece moved
     * @param toX   int column number of a cell where the piece moved on
     * @param toY   int row number of a cell where the piece moved on
     * @return String value of standard chess move notation
     */
    private String movesRecord(int fromX, int fromY, int toX, int toY) {
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int y1 = 8 - fromY;
        int y2 = 8 - toY;
        return letters[fromX] + y1 + " - " + letters[toX] + y2;
    }


    /**
     * Removes taken piece image from the board
     * and puts it on the side of player who took it.
     *
     * @param figure ImageView of taken figure
     */
    private void sendToMiniBox(ImageView figure) {
        if (figure == null) return;
        figure.setPreserveRatio(true);
        if (figure.getId().contains("Black")) {
            figure.fitHeightProperty().bind(whiteMiniBox.heightProperty());
            whiteMiniBox.getChildren().add(figure);
        } else if (figure.getId().contains("White")) {
            figure.fitHeightProperty().bind(blackMiniBox.heightProperty());
            blackMiniBox.getChildren().add(figure);
        }
    }
}
