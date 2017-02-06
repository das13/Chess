package chess.view;

import chess.*;
import chess.Timer;
import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    FXMLLoader loader;
    Button offerDrawButton;
    Button resignButton;
    Scene scene;
    private Timer count;
    Label opponentTimer;
    Label yourTimer;
    GridPane grid;
    private List<Pane> targets = new ArrayList<Pane>();
    private Map<String, Pane> board = new TreeMap<>();
    private boolean isWhitePlayer;
    int lastMoveFromX;
    int lastMoveFromY;
    int lastMoveToX;
    int lastMoveToY;
    ImageView lastMovedFigure;
    ImageView lastTakenFigure;

    public GameFrame(XMLin xmLin, final XMLout xmlOut, boolean isWhite) {

        isWhitePlayer = isWhite;
//      Для игрока белыми и черными подгружаются разные fxml
        if(isWhitePlayer) {
            loader = new FXMLLoader(getClass().getResource("/WhitePlayerBoard.fxml"));
        } else {
            loader = new FXMLLoader(getClass().getResource("/BlackPlayerBoard.fxml"));
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
        opponentTimer = (Label) scene.lookup("opponentTimer");
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
            private ImageView source;

            DragDropped(Pane target, ImageView source) {
                this.target = target;
                this.source = source;
            }

            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasImage()) {
                    for (Pane pane : targets) {
                        pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                    }
                    List<String> list = new ArrayList<String>();
                    list.add("move");
                    String x = getCoordinateX(source.getParent());
                    String y = getCoordinateY(source.getParent());
                    String x1 = getCoordinateX(target);
                    String y1 = getCoordinateY(target);

                    lastMoveFromX = Integer.parseInt(getCoordinateX(source.getParent()));
                    lastMoveFromY = Integer.parseInt(getCoordinateY(source.getParent()));
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
                    try {
                        xmlOut.sendMessage(list);
                    } catch (ParserConfigurationException | TransformerConfigurationException | IOException e1) {
                        e1.printStackTrace();
                    }
                    target.getChildren().add(source);
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
                List<String> listIn = null;
                try {
                    listIn = xmLin.receive();
                } catch (ParserConfigurationException | SAXException | IOException | TransformerConfigurationException e1) {
                    e1.printStackTrace();
                }
                Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                targets.clear();
                if ("steps".equals(listIn.get(0))) {
                    for (String s : listIn) {
                        if (!s.equals("steps")) {
                            targets.add(board.get(s));
                        }
                    }
                    for (Pane pane : targets) {
                        pane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
                        pane.setOnDragOver(new DragOver(pane));
                        pane.setOnDragDropped(new DragDropped(pane, source));
                    }
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(source.getImage());
                    db.setContent(content);
                    event.consume();
                } else {
                    for (String s: listIn) {
                        System.out.println("FROM SERVER: " + s);
                    }
                }

            }
        }
        grid = (GridPane) scene.lookup("#grid");
        root.setOnDragOver(new DragOver(root));
        root.setOnDragDropped(new DragDroppedOut());
        for (Node node : grid.getChildren()) {
            if (node.getClass().getSimpleName().equals("Pane")){
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
            yourTimer.setText((String)arg);
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
        ImageView image = new ImageView();
        for (Node node: source.getChildren()) {
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
}


