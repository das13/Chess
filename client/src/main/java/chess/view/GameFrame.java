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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * <code>GameFrame</code> is a chessboard realization, with timer, moves notation and everything needed.
 * Initiator of a game plays white pieces, the opponent plays black. In case of playing black pieces
 * board is considered turned upside down, so server understands moves correctly. While opponent
 * makes his move the board of current player doesn't let pick, drag pieces. Opponent's pieces
 * are not available to move at any time.
 */
public class GameFrame extends Stage implements Observer {
    private Scene scene = null;
    private Timer count;
    private final Label turnLabel;
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
    private final static Logger logger = Logger.getLogger(GameFrame.class);
    private LastMove lastMove;
    private LastMove lastMoveTemp;
    /**
     * Creates <code>GameFrame</code> with XMLin and XMLout for transfering data from
     * and to server. Also boolean parameter defines what color of pieces player gets,
     * if he accepted offer to play, pieces will be black, initiator of game gets white
     * pieces.
     *
     * @param xmLin   for receiving messages from server.
     * @param xmlOut  for sending messages to server.
     * @param isWhite defines color of player's pieces on current game
     * @param info    player's information, used to display new rank and additional info
     *                after game is over. Also is used for returning back to <code>ProfileFrame</code>
     */
    public GameFrame(XMLin xmLin, final XMLout xmlOut, boolean isWhite, List<String> info, String opponent) {
        String white;
        String black;
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
            queen.setId("queenWhite1");
            castle.setId("castleWhite1");
            knight.setId("knightWhite1");
            bishop.setId("bishopWhite1");
            rivalqueen.setId("queenBlack1");
            rivalcastle.setId("castleBlack1");
            rivalknight.setId("knightBlack1");
            rivalbishop.setId("bishopBlack1");
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
            queen.setId("queenBlack1");
            castle.setId("castleBlack1");
            knight.setId("knightBlack1");
            bishop.setId("bishopBlack1");
            rivalqueen.setId("queenWhite1");
            rivalcastle.setId("castleWhite1");
            rivalknight.setId("knightWhite1");
            rivalbishop.setId("bishopWhite1");
        }
        Pane root = null;
        try {
            root = loader.load();
            if (isWhite) {
                white = playerInfo.get(3);
                black = opponent;
            } else {
                white = opponent;
                black = playerInfo.get(3);
            }
            this.setTitle("Белые: " + white + ", черные: " + black);
            scene = new Scene(root, 700, 600);
            scene.getStylesheets().add("Skin.css");
            this.setScene(scene);
            this.setMinWidth(750);
            this.setMinHeight(650);
            this.show();
        } catch (IOException e) {
            logger.error("Error loading root pane for playing board", e);
        }

        opponentTimer = (Label) loader.getNamespace().get("opponentTimer");
        yourTimer = (Label) scene.lookup("#yourTimer");

        grid = (GridPane) scene.lookup("#grid");
        Button offerDrawButton = (Button) scene.lookup("#offerDrawButton");
        offerDrawButton.setOnMouseClicked(e -> {
            List<String> list = new ArrayList<>();
            list.add("offerDraw");
            try {
                xmlOut.sendMessage(list);
            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                logger.error("Failed to send draw offer from GameFrame", e1);
            }
        });
        Button undoMove = (Button) scene.lookup("#requestMoveCancel");
        undoMove.setOnMouseClicked(e-> {
            if(lastMove!=null) {
                List<String> list = new ArrayList<>();
                list.add("allowRestoreMove");
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    logger.error("Failed to send resign from GameFrame", e1);
                }
            }
        });
        undoMove.setDisable(true);
        Button resignButton = (Button) scene.lookup("#resignButton");
        resignButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(stage);
            alert.getDialogPane().getStylesheets().add("Skin.css");
            alert.setTitle("Сдаться?");
            alert.setHeaderText(null);
            alert.setContentText("Хотите сдаться? (Рейтинг -10)");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                List<String> list = new ArrayList<>();
                list.add("resign");
                try {
                    xmlOut.sendMessage(list);
                } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                    logger.error("Failed to send resign from GameFrame", e1);
                }
            } else {
                alert.close();
            }
        });

        count = new Timer(this);
        Thread clock = new Thread(count);
        clock.setDaemon(true);
        clock.start();
        if (!isWhitePlayer) count.stopTimer();
        whiteMiniBox = (HBox) loader.getNamespace().get("whiteMiniBox");
        blackMiniBox = (HBox) loader.getNamespace().get("blackMiniBox");
        turnLabel = (Label) scene.lookup("#turn");
        movesRecord = (ListView<String>) scene.lookup("#movesRecord");
        if (!isWhitePlayer) {
            grid.setDisable(true);
            turnLabel.setText("Сейчас ходит: " + opponent);
        } else {
            turnLabel.setText("Сейчас ходит: " + playerInfo.get(3));
        }


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
                    lastMoveTemp = lastMove;
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

                    list.add(String.valueOf(x));
                    list.add(String.valueOf(y));
                    list.add(String.valueOf(x1));
                    list.add(String.valueOf(y1));
                    list.add(count.getTime());
                    try {
                        xmlOut.sendMessage(list);
                    } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                        logger.error("Failed to send move message from GameFrame", e1);
                    }
                    moveFromTo(lastMoveFromX, lastMoveFromY, lastMoveToX, lastMoveToY);
                     /*if king is making castling, castle automatically moves to the appropriate position*/
                    if (lastMovedFigure.getId().contains("king") && lastMoveFromY == lastMoveToY
                            && (lastMoveToX - lastMoveFromX == 2 || lastMoveFromX - lastMoveToX == 2)) {
                        String where = "";
                        Pane targetTemp = lastMove.getTarget();
                        ImageView figureTemp = lastMove.getFigure();

                        if (lastMoveToX == 6) {
                            moveFromTo(7, lastMoveToY, 5, lastMoveToY);
                            lastMove.setTarget1(findPane(7, lastMoveToY));
                            lastMove.setSource1(findPane(5, lastMoveToY));
                            lastMove.setTarget(targetTemp);
                            lastMove.setFigure(figureTemp);
                            lastMove.setCastling(true);
                            for (Node node : findPane(5, lastMoveToY).getChildren()) {
                                if (node instanceof ImageView) {
                                    lastMove.setFigure1((ImageView) node);
                                }
                            }
                        }
                        if (lastMoveToX == 2) {
                            moveFromTo(0, lastMoveToY, 3, lastMoveToY);
                            lastMove.setTarget1(findPane(0, lastMoveToY));
                            lastMove.setSource1(findPane(3, lastMoveToY));
                            lastMove.setTarget(targetTemp);
                            lastMove.setFigure(figureTemp);
                            lastMove.setCastling(true);
                            for (Node node : findPane(3, lastMoveToY).getChildren()) {
                                if (node instanceof ImageView) {
                                    lastMove.setFigure1((ImageView) node);
                                }
                            }
                            where = "-O";
                        }
                        movesRecord.getItems().remove(movesRecord.getItems().size() - 1);
                        movesRecord.getItems().add("O-O" + where);
                        movesRecord.scrollTo(movesRecord.getItems().size() - 1);

                    }
                    count.stopTimer();
                    undoMove.setDisable(false);
                    success = true;
                    grid.setDisable(false);
                    turnLabel.setText("Сейчас ходит: " + opponent);
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
                    logger.error("Failed to send drag figure data from GameFrame", e1);
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
                newFigure.setId(figure.getId());
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
                    logger.error("Failed to send replace pawn message from GameFrame", e1);
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
                    logger.error("Failed to receive a message from server on GameFrame", e);
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
                switch (listIn.get(0)) {
                    case "rivalMove": {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ход");
                        alert.setHeaderText(null);
                        alert.setContentText(opponent + " сделал ход");
                        alert.showAndWait();
                        grid.setDisable(false);
                        count.startTimer();
                        moveFromTo(parseInt(listIn.get(1)), parseInt(listIn.get(2)), parseInt(listIn.get(3)), parseInt(listIn.get(4)));
                        movesRecord.getItems().add(opponent + ": " + movesRecord(parseInt(listIn.get(1)), parseInt(listIn.get(2)), parseInt(listIn.get(3)), parseInt(listIn.get(4))));
                        opponentTimer.setText(listIn.get(5));
                        turnLabel.setText("Сейчас ходит: " + playerInfo.get(3));
                        undoMove.setDisable(true);
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "steps": {
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
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "cancel": {
                        grid.setDisable(false);
                        lastMove.revertMove();
                        lastMove = lastMoveTemp;
                        undoMove.setDisable(true);
                        movesRecord.getItems().remove(movesRecord.getItems().size() - 1);
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "checkmate": {
                        String message;
                        String rank = "";
                        String winner;
                        if (playerInfo.get(3).equals(listIn.get(3))) {
                            rank = listIn.get(5);
                            playerInfo.set(5, rank);
                        }
                        if (playerInfo.get(3).equals(listIn.get(4))) {
                            rank = listIn.get(8);
                            playerInfo.set(5, rank);
                        }
                        if ("WHITE".equals(listIn.get(1))) {
                            winner = isWhite ? opponent : playerInfo.get(3);
                            message = "Мат! "+ winner + " победил, ваш новый рейтинг: " + rank;
                        } else {
                            winner = isWhite ? playerInfo.get(3) : opponent;
                            message = "Мат! "+ winner + " победил, ваш новый рейтинг: " + rank;
                        }
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Конец игры");
                        alert.setHeaderText(null);
                        alert.setContentText(message);
                        alert.showAndWait();
                        stage.close();
                        new ProfileFrame(xmLin, xmlOut, listIn);
                        break;
                    }
                    case "replacePawn": {
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
                        lastMove.setReplace(true);
                        undoMove.setDisable(false);
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "rivalReplace": {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ход");
                        alert.setHeaderText(null);
                        alert.setContentText(opponent + " сделал ход");
                        alert.showAndWait();
                        grid.setDisable(false);
                        count.startTimer();
                        moveFromTo(parseInt(listIn.get(2)), parseInt(listIn.get(1)), parseInt(listIn.get(3)), parseInt(listIn.get(4)));
                        opponentTimer.setText(listIn.get(5));
                        ImageView figure = null;
                        if (listIn.get(5).equals("Castle")) figure = rivalcastle;
                        if (listIn.get(5).equals("Knight")) figure = rivalknight;
                        if (listIn.get(5).equals("Bishop")) figure = rivalbishop;
                        if (listIn.get(5).equals("Queen")) figure = rivalqueen;
                        lastMove.setReplace(true);
                        ImageView newFigure = new ImageView(figure != null ? figure.getImage() : null);
                        newFigure.setOnDragDetected(new DragDetected(newFigure));
                        findPane(parseInt(listIn.get(3)), parseInt(listIn.get(4))).getChildren().clear();
                        findPane(parseInt(listIn.get(3)), parseInt(listIn.get(4))).getChildren().add(newFigure);
                        undoMove.setDisable(true);
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "4minute": {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("время вышло");
                        alert.setHeaderText(null);
                        alert.setContentText("Вы бездействуете 4 минут, через 1 минуту вам будет защитан проиграш");
                        alert.showAndWait();
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "5minute": {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("время вышло");
                        alert.setHeaderText(null);
                        alert.setContentText("Время вышло! Вы проиграли");
                        alert.showAndWait();
                        stage.close();
                        new ProfileFrame(xmLin, xmlOut, listIn);
                        break;
                    }
                    case "5minuteRival": {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("время вышло");
                        alert.setHeaderText(null);
                        alert.setContentText("У " + opponent + " вышло время! Вы выиграли!");
                        alert.showAndWait();
                        stage.close();
                        new ProfileFrame(xmLin, xmlOut, listIn);
                        break;
                    }
                    case "offerDraw": {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ничья");
                        alert.setHeaderText(null);
                        alert.setContentText(opponent + " предлагает ничью, согласны? (Рейтинг -5)");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            List<String> list = new ArrayList<>();
                            list.add("acceptDraw");
                            try {
                                xmlOut.sendMessage(list);
                            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e) {
                                logger.error("Failed to send draw accept from GameFrame", e);
                            }
                        } else {
                            alert.close();
                        }
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "draw": {
                        String message;
                        String rank = "";
                        if (playerInfo.get(3).equals(listIn.get(3))) {
                            rank = listIn.get(5);
                            playerInfo.set(5, rank);
                        }
                        if (playerInfo.get(3).equals(listIn.get(6))) {
                            rank = listIn.get(8);
                            playerInfo.set(5, rank);
                        }
                        if ("WHITE".equals(listIn.get(1))) {
                            message = "Ничья, ваш новый рейтинг: " + rank;
                        } else {
                            message = "Ничья, ваш новый рейтинг: " + rank;
                        }
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Конец игры");
                        alert.setHeaderText(null);
                        alert.setContentText(message);
                        alert.showAndWait();
                        stage.close();
                        new ProfileFrame(xmLin, xmlOut, listIn);
                        break;
                    }
                    case "resign": {
                        String rank = "";
                        String message;
                        int was;
                        int became = 0;
                        was = Integer.parseInt(playerInfo.get(5));
                        if (playerInfo.get(3).equals(listIn.get(3))) {
                            rank = listIn.get(5);
                            became = Integer.parseInt(rank);
                            playerInfo.set(5, rank);
                        }
                        if (playerInfo.get(3).equals(listIn.get(6))) {
                            rank = listIn.get(8);
                            became = Integer.parseInt(rank);
                            playerInfo.set(5, rank);
                        }
                        if (became < was) {
                            message = "Вы решили сдаться, ваш новый рейтинг: " + rank;
                        } else {
                            message = opponent + " решил сдаться, ваш новый рейтинг: " + rank;
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Конец игры");
                        alert.setHeaderText(null);
                        alert.setContentText(message);
                        alert.showAndWait();
                        stage.close();
                        new ProfileFrame(xmLin, xmlOut, listIn);
                        break;
                    }
                    case "castling": {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Ход");
                        alert.setHeaderText(null);
                        alert.setContentText(opponent + " выполнил рокировку");
                        alert.showAndWait();
                        grid.setDisable(false);
                        count.startTimer();
                        if ("white".equals(listIn.get(1))) {
                            if ("kingside".equals(listIn.get(2))) {
                                moveFromTo(4, 7, 6, 7);
                                moveFromTo(7, 7, 5, 7);
                                lastMove.setTarget(findPane(4, 7));
                                lastMove.setTarget1(findPane(7, 7));
                                lastMove.setSource(findPane(6, 7));
                                lastMove.setSource1(findPane(5, 7));
                                lastMove.setFigure((ImageView)findPane(6, 7).getChildren().get(0));
                                lastMove.setFigure1((ImageView)findPane(5, 7).getChildren().get(0));
                                movesRecord.getItems().add(opponent + ": O-O");
                            } else {
                                moveFromTo(4, 7, 2, 7);
                                moveFromTo(0, 7, 3, 7);
                                lastMove.setTarget(findPane(4, 7));
                                lastMove.setTarget1(findPane(0, 7));
                                lastMove.setSource(findPane(2, 7));
                                lastMove.setSource1(findPane(3, 7));
                                lastMove.setFigure((ImageView)findPane(2, 7).getChildren().get(0));
                                lastMove.setFigure1((ImageView)findPane(3, 7).getChildren().get(0));
                                movesRecord.getItems().add(opponent + ": О-O-O");
                            }
                        } else {
                            if ("kingside".equals(listIn.get(2))) {
                                moveFromTo(4, 0, 6, 0);
                                moveFromTo(7, 0, 5, 0);
                                lastMove.setTarget(findPane(4, 0));
                                lastMove.setTarget1(findPane(7, 0));
                                lastMove.setSource(findPane(6, 0));
                                lastMove.setSource1(findPane(5, 0));
                                lastMove.setFigure((ImageView)findPane(5, 0).getChildren().get(0));
                                lastMove.setFigure1((ImageView)findPane(6, 0).getChildren().get(0));
                                movesRecord.getItems().add(opponent + ": O-O");
                            } else {
                                moveFromTo(4, 0, 2, 0);
                                moveFromTo(0, 0, 3, 0);
                                lastMove.setTarget(findPane(4, 0));
                                lastMove.setTarget1(findPane(0, 0));
                                lastMove.setSource(findPane(2, 0));
                                lastMove.setSource1(findPane(3, 0));
                                lastMove.setFigure((ImageView)findPane(2, 0).getChildren().get(0));
                                lastMove.setFigure1((ImageView)findPane(3, 0).getChildren().get(0));
                                movesRecord.getItems().add(opponent + ": О-O-O");
                            }
                        }
                        lastMove.setTake(false);
                        lastMove.setCastling(true);
                        movesRecord.scrollTo(movesRecord.getItems().size() - 1);
                        opponentTimer.setText(listIn.get(3));
                        undoMove.setDisable(true);
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "allowRestoreMove": {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.initOwner(stage);
                        alert.getDialogPane().getStylesheets().add("Skin.css");
                        alert.setTitle("Запрос на отмену хода");
                        alert.setHeaderText(null);
                        alert.setContentText("Соперник просит отменить последний ход");
                        List<String> list = new ArrayList<>();
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.OK) {
                            list.add("acceptRestore");
                            try {
                                xmlOut.sendMessage(list);
                            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                                logger.error("Error confirming cancel move", e1);
                            }
                            grid.setDisable(false);
                            count.stopTimer();
                        }
                        if (alert.getResult() == ButtonType.CANCEL) {
                            list.add("cancelRestore");
                            try {
                                xmlOut.sendMessage(list);
                            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                                logger.error("Error confirming cancel move", e1);
                            }
                        }
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    case "restore": {
                        lastMove.revertMove();
                        lastMove=null;
                        if(!undoMove.isDisable()) {
                            grid.setDisable(false);
                            undoMove.setDisable(true);
                            count.startTimer();
                        }
                        movesRecord.getItems().remove(movesRecord.getItems().size() - 1);
                        MyTask myTask = new MyTask<Void>();
                        myTask.setOnSucceeded(new MyHandler());
                        Thread thread1 = new Thread(myTask);
                        thread1.setDaemon(true);
                        thread1.start();
                        break;
                    }
                    default: {
                        logger.error("Error understanding message from server " + listIn.get(0));
                    }
                }
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
        lastMove = new LastMove();
        ImageView mini = null;
        ImageView image = new ImageView();
        Label label = null;
        lastMove.setTarget(source);
        lastMove.setSource(target);
        for (Node node : target.getChildren()) {
            if (node instanceof Label) {
                label = (Label) node;
            } else if (node instanceof ImageView) {
                lastMove.setTake(true);
                mini = (ImageView) node;
                lastMove.setFigure1(mini);
            }
        }
        target.getChildren().clear();
        sendToMiniBox(mini);
        if (label != null) target.getChildren().add(label);

        for (Node node : source.getChildren()) {
            if (node instanceof ImageView) {
                image = (ImageView) node;
                lastMove.setFigure(image);
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
