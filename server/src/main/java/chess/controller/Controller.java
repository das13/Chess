package chess.controller;

import chess.exceptions.RivalFigureException;
import chess.model.Game;
import chess.model.Player;
import chess.model.Status;
import chess.services.GameService;
import chess.services.PlayerService;
import chess.services.xmlService.XMLReciever;
import chess.services.xmlService.XMLSender;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobnewmark on 22.01.2017
 */
public class Controller extends Thread {

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private DataInputStream input;
    private DataOutputStream output;
    private Player player;
    private XMLSender sender;
    private XMLReciever reciever;

    public Controller(Socket socket) {
        this.socket = socket;
        player = new Player(this);
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            sender = new XMLSender(out);
            reciever=new XMLReciever(in);

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    @Override
    public void run() {
        try {
            List<String> str = null;
            List<String> strOut = new ArrayList();
            while (true) {
                try {
                    str = reciever.receive();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }catch(SocketException e){
                    break;
                }
                if (str.get(0).equals("exit")) break;
                if (str.get(0).equals("reg")) {
                    String ip = socket.getInetAddress().toString();
                    PlayerService.reg(str.get(1), str.get(2), ip, sender);
                }
                if (str.get(0).equals("auth")) {
                    strOut.add("auth");
                    PlayerService.auth(player, str.get(1), str.get(2), strOut);
                    sender.send(strOut);
                }
                if (str.get(0).equals("callPlayer")) {
                    //out.println("enter nickname your rival");

                    Player player = GameService.callPlayer(getPlayer(), str.get(1));
                    if (player != null) {
                        Controller otherController = player.getController();
                        PrintWriter otherOut = new PrintWriter(otherController.getSocket().getOutputStream(), true);
                        otherOut.println("confirm");
                    } else {
                        //out.println("This player is out of reach ");
                    }

                }
                if (str.get(0).equals("confirm")) {
                    //out.println("You are invited. enter Ok or No");
                    Game thisGame = GameService.confirmGame(getPlayer(), str.get(1));
                    setCurrentGame(thisGame);
                }
                if (str.get(0).equals("drag")) {
                    //out.println("enter coordinates of figure - x and y");
                    try {
                        int[] steps = GameService.steps(getCurrentGame(), Integer.parseInt(str.get(1)), Integer.parseInt(str.get(2)));
                        //out.println("steps");
                        for (int i : steps) {
                            //out.println(i);
                        }
                    } catch (RivalFigureException e) {
                        //out.println("you try taking rivals figure");
                        e.printStackTrace();
                    }
                }
                if (str.get(0).equals("move")) {
                    int[] steps = new int[0];
                    try {
                        steps = GameService.move(getCurrentGame(), str);
                    } catch (RivalFigureException e) {
                        //out.println("you try taking rivals figure");
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
                    XMLSender test = new XMLSender(out);
                   /* try {
                        test.sendFreePlayers();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerConfigurationException e) {
                        e.printStackTrace();
                    }*/
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
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
