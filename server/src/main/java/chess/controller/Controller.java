package chess.controller;

import chess.ServerMain;
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
    private Player otherPlayer;
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
                    PlayerService.auth(player, str.get(1), str.get(2), sender);
                }
                if ("reenter".equals(str.get(0))) {
                    PlayerService.reenter(player, str.get(1), str.get(2), sender);
                }
                if (str.get(0).equals("refresh")) {
                    List<String> outList = new ArrayList<String>();
                    outList.add("refresh");
                    outList.addAll(PlayerService.refresh(player,  sender));
                    sender.send(outList);
                }
                if (str.get(0).equals("logout")) {
                    synchronized (ServerMain.freePlayers) {
                        ServerMain.freePlayers.remove(player);
                    }
                }
                if("saveProfile".equals(str.get(0))){
                    sender.send(PlayerService.saveProfile(str.get(2), str.get(3), Integer.parseInt(str.get(1))));
                }
                if (str.get(0).equals("callPlayer")) {
                    GameService.callPlayer(getPlayer(), str.get(1));
                }
                if (str.get(0).equals("confirm")) {
                    GameService.confirmGame(getPlayer(), str);
                }
                if (str.get(0).equals("drag")) {
                    sender.send(GameService.steps(getCurrentGame(), Integer.parseInt(str.get(1)), Integer.parseInt(str.get(2))));
                }
                if (str.get(0).equals("move")) {
                    GameService.move(getCurrentGame(), str, player);
                }
                if ("replacePawn".equals(str.get(0))) {
                    getCurrentGame().replacePawn(str.get(5), Integer.parseInt(str.get(4)), Integer.parseInt(str.get(3)));
                    XMLSender otherSender = getCurrentGame().getOtherPlayer(player).getController().getSender();
                    List<String> out = new ArrayList<String>();
                    out.add("rivalReplace");
                    out.add(str.get(1));
                    out.add(str.get(2));
                    out.add(str.get(3));
                    out.add(str.get(4));
                    out.add(str.get(5));
                    otherSender.send(out);
                }
                if ("admin_getPlayers".equals(str.get(0))) {
                    PlayerService.adminGetPlayers(sender);
                }
            }

        } catch (IOException e) {
            System.out.println("close");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public XMLSender getSender() {
        return sender;
    }

    public void setSender(XMLSender sender) {
        this.sender = sender;
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
            synchronized (ServerMain.freePlayers) {
                ServerMain.freePlayers.remove(player);
            }
            synchronized (ServerMain.inGamePlayers) {
                ServerMain.inGamePlayers.remove(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Thread did not close!");
        }
    }

    public void setCurrentGame(Game game) {
        player.setCurrentGame(game);
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOtherPlayer() {
        return otherPlayer;
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