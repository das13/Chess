package chess.controller;

import chess.exceptions.RivalFigureException;
import chess.model.Game;
import chess.model.Player;
import chess.model.Status;
import chess.services.GameService;
import chess.services.PlayerService;
import chess.services.xmlService.XMLReciever;
import chess.services.xmlService.XMLSender;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.*;
import java.net.Socket;

/**
 * Created by bobnewmark on 22.01.2017
 */
public class Controller extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private DataInputStream input;
    private DataOutputStream output;
    private Player player;
    private XMLSender sender;
    private XMLReciever reciever;

    public Controller(Socket socket) {
        this.socket = socket;
        player = new Player(this);

        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    @Override
    public void run() {
        try {
            String str = "";
            while (true) {
                str = in.readLine();
                if (str.equals("exit")) break;
                if (str.equals("reg")) {
                    //PlayerService.reg(this, in, out);
                }
                if (str.equals("auth")) {
                    PlayerService.auth(this.player);
                }
                if (str.equals("callPlayer")) {
                    out.println("enter nickname your rival");

                    Player player = GameService.callPlayer(getPlayer(), in.readLine());
                    if (player != null) {
                        Controller otherController = player.getController();
                        PrintWriter otherOut = new PrintWriter(otherController.getSocket().getOutputStream(), true);
                        otherOut.println("confirm");
                    } else {
                        out.println("This player is out of reach ");
                    }

                }
                if (str.equals("confirm")) {
                    out.println("You are invited. enter Ok or No");
                    Game thisGame = GameService.confirmGame(getPlayer(), in.readLine());
                    setCurrentGame(thisGame);
                }
                if (str.equals("drag")) {
                    out.println("enter coordinates of figure - x and y");
                    try {
                        int[] steps = GameService.steps(getCurrentGame(), Integer.parseInt(in.readLine()), Integer.parseInt(in.readLine()));
                        out.println("steps");
                        for (int i : steps) {
                            out.println(i);
                        }
                    } catch (RivalFigureException e) {
                        out.println("you try taking rivals figure");
                        e.printStackTrace();
                    }
                }
                if (str.equals("move")) {
                    int[] steps = new int[0];
                    try {
                        steps = GameService.move(getCurrentGame(), in, out);
                    } catch (RivalFigureException e) {
                        out.println("you try taking rivals figure");
                        e.printStackTrace();
                    }
                    Player otherPlayer = getCurrentGame().getOtherPlayer(getPlayer());
                    System.out.println(otherPlayer.getNickname());
                    PrintWriter outOther = new PrintWriter(otherPlayer.getSocket().getOutputStream(), true);
                    outOther.println("step");
                    for (int i : steps) {
                        outOther.println(i);
                    }
                }
                if (str.equals("FREEPLAYERS")) {
                    XMLSender test = new XMLSender();
                    try {
                        test.sendFreePlayers(output);
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Thread did not close!");
        }
    }

    public void setCurrentGame(Game game) {
        player.setCurrentGame(game);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getCurrentGame() {
        return getPlayer().getCurrentGame();
    }

    public void setPlayerLogin(String login) {
        player.setLogin(login);
    }

    public void setPlayerPassword(String password) {
        player.setPassword(password);
    }

    public void setPlayerNickname(String nickname) {
        player.setNickname(nickname);
    }

    public void setPlayerIpadress(String ipadress) {
        player.setIpadress(ipadress);
    }

    public void setPlayerStatus(Status status) {
        player.setStatus(status);
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
        return output;
    }
}
