package chess.view;

import chess.*;
import chess.Timer;
import chess.services.xmlService.XMLin;
import chess.services.xmlService.XMLout;
import javafx.application.Platform;
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
    private Timer count;
    Label yourTimer;
    private List<Pane> targets = new ArrayList<Pane>();
    private Map<String, Pane> board = new TreeMap<>();
    private boolean isWhitePlayer;

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
        Scene scene = new Scene(root, 700, 600);
        this.setScene(scene);
        this.setMinWidth(750);
        this.setMinHeight(650);
        this.show();

//      таймер выводит оставшееся время, кнопки ставят таймер на паузу и возобновляют
        yourTimer = (Label) scene.lookup("#yourTimer");

        offerDrawButton = (Button) scene.lookup("#offerDrawButton");
        offerDrawButton.setOnMouseClicked(e -> {
            count.stopTimer();
        });

        resignButton = (Button) scene.lookup("#resignButton");
        resignButton.setOnMouseClicked(e -> {
            count.startTimer();
        });


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
                    int x;
                    int y;
                    if (GridPane.getRowIndex(source.getParent()) == null) {
                        x = 0;
                    } else {
                        x = GridPane.getRowIndex(source.getParent());
                    }
                    if (GridPane.getColumnIndex(source.getParent()) == null) {
                        y = 0;
                    } else {
                        y = GridPane.getColumnIndex(source.getParent());
                    }
                    int x1;
                    int y1;
                    if (GridPane.getRowIndex(target) == null) {
                        x1 = 0;
                    } else {
                        x1 = GridPane.getRowIndex(target);
                    }
                    if (GridPane.getColumnIndex(target) == null) {
                        y1 = 0;
                    } else {
                        y1 = GridPane.getColumnIndex(target);
                    }
                    System.out.print("Drag from cell: ");
                    System.out.print("X:" + y);
                    System.out.println(" Y:" + x);
                    System.out.print("Drop on cell: ");
                    System.out.print("X:" + y1);
                    System.out.println(" Y:" + x1);
                    list.add(String.valueOf(y));
                    list.add(String.valueOf(x));
                    list.add(String.valueOf(y1));
                    list.add(String.valueOf(x1));
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
            }

            public void handle(MouseEvent event) {
                List<String> list = new ArrayList<String>();
                list.add("drag");
                int x;
                int y;
                if (GridPane.getRowIndex(source.getParent()) == null) {
                    x = 0;
                } else {
                    x = GridPane.getRowIndex(source.getParent());
                }
                if (GridPane.getColumnIndex(source.getParent()) == null) {
                    y = 0;
                } else {
                    y = GridPane.getColumnIndex(source.getParent());
                }
                System.out.print("accessible x " + y);
                System.out.println(" accessible y " + x);
                list.add(String.valueOf(y));
                list.add(String.valueOf(x));
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
            }
        }
        GridPane grid = (GridPane) scene.lookup("#grid");
        root.setOnDragOver(new DragOver(root));
        root.setOnDragDropped(new DragDroppedOut());
        for (Node node : grid.getChildren()) {
            if (node.getClass().getSimpleName().equals("Pane")) {
                Pane pane = (Pane) node;
                int x;
                int y;
                if (GridPane.getRowIndex(pane) == null) {
                    x = 0;
                } else {
                    x = GridPane.getRowIndex(pane);
                }
                if (GridPane.getColumnIndex(pane) == null) {
                    y = 0;
                } else {
                    y = GridPane.getColumnIndex(pane);
                }
                board.put(x + "" + y, pane);
                for (Node n : pane.getChildren()) {
                    if (n.getClass().getSimpleName().equals("ImageView")) {
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
//            x = 7 - x;
//            y = 7 - y;
//        }
        System.out.print("X:" + x);
        System.out.println(" Y:" + y);
    }


  /*  public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/Board.fxml"));
        window.setTitle("Game");

        Scene scene = new Scene(root, 700, 600);
        window.setScene(scene);
        window.setMinWidth(750);
        window.setMinHeight(650);
        window.show();
    }

    @FXML
    public void handleOnDrag(MouseEvent event) {

        Node node = (Node) event.getSource();
        node = node.getParent();
        System.out.print("Drag from cell: ");
        System.out.print("X:" + GridPane.getColumnIndex(node));
        System.out.println(" Y:" + GridPane.getRowIndex(node));

        ImageView image = (ImageView) event.getSource();
        Dragboard db = image.startDragAndDrop(TransferMode.ANY);

        ClipboardContent cbContent = new ClipboardContent();
        cbContent.putImage(image.getImage());
        db.setContent(cbContent);
        image.setVisible(false);
        event.consume();
    }

    @FXML
    public void handleDragOver(DragEvent de) {
        Dragboard board = de.getDragboard();
        if (board.hasImage()) {
            de.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleDrop(DragEvent event) {

        Dragboard db = event.getDragboard();
        Node node = (Node) event.getSource();
        Pane pane;

        System.out.print("Drop on cell: ");
        System.out.print("X:" + GridPane.getColumnIndex(node));
        System.out.println(" Y:" + GridPane.getRowIndex(node));

        if (event.getTarget() instanceof ImageView) {
            ImageView temp = (ImageView) event.getTarget();
            pane = (Pane) temp.getParent();
        } else {
            pane = (Pane) event.getTarget();
        }

        if (pane.getChildren().size() > 0) {
            pane.getChildren().clear();
        }

        ImageView image = new ImageView(db.getImage());
        image.setOnDragDetected(event1 -> {
            ImageView image1 = (ImageView) event1.getSource();
            Dragboard db1 = image1.startDragAndDrop(TransferMode.ANY);

            ClipboardContent cbContent = new ClipboardContent();
            cbContent.putImage(image1.getImage());
            db1.setContent(cbContent);
            image1.setVisible(false);
            event1.consume();
        });

        pane.getChildren().add(image);
        event.consume();
    }

    @FXML
    public void handleClick(MouseEvent event) {
        ImageView temp = (ImageView) event.getTarget();
        Node node = (Node) temp.getParent();
        System.out.println("X: " + GridPane.getColumnIndex(node));
        System.out.println("Y: " + GridPane.getRowIndex(node));
    }*/
}


