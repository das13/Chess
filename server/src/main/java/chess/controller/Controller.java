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
                    List<String> out = new ArrayList<String>();
                    otherPlayer = GameService.callPlayer(getPlayer(), str.get(1));
                    if (otherPlayer != null) {
                        Controller otherController = otherPlayer.getController();
                        XMLSender otherSender = otherController.getSender();
                        out.add("confirm");
                        out.add(getPlayer().getLogin());
                        otherSender.send(out);
                    } else {
                        List<String> outList = new ArrayList<String>();
                        outList.add("notconfirm");
                        outList.addAll(PlayerService.refresh(player,  sender));
                        sender.send(outList);

                    }
                }
                if (str.get(0).equals("confirm")) {
                    Game thisGame = GameService.confirmGame(getPlayer(), str.get(1));
                    List<String> out = new ArrayList<String>();
                    out.add("confirmresponse");
                    otherPlayer = thisGame.getOtherPlayer(player);
                    Controller otherController = otherPlayer.getController();
                    XMLSender otherSender = otherController.getSender();
                    if("Ok".equals(str.get(1)) && thisGame!=null) {
                        out.add("Ok");
                        setCurrentGame(thisGame);
                        thisGame.getOtherPlayer(player).getController().setCurrentGame(thisGame);
                        synchronized (ServerMain.freePlayers) {
                            ServerMain.freePlayers.remove(player);
                            ServerMain.freePlayers.remove(thisGame.getOtherPlayer(player));
                        }
                        synchronized (ServerMain.inGamePlayers) {
                            ServerMain.inGamePlayers.add(player);
                            ServerMain.inGamePlayers.add(thisGame.getOtherPlayer(player));
                        }
                    }
                    if("No".equals(str.get(1)) && thisGame!=null) {
                        out.add("No");
                    }
                    otherSender.send(out);
                }
                if (str.get(0).equals("drag")) {
                    sender.send(GameService.steps(getCurrentGame(), Integer.parseInt(str.get(1)), Integer.parseInt(str.get(2))));
                }
                if (str.get(0).equals("move")) {
                    int[] steps = new int[0];
                        List<String> out = new ArrayList<String>();
                        List<String> result = GameService.move(getCurrentGame(), str, player, otherPlayer);
                        XMLSender otherSender = getCurrentGame().getOtherPlayer(player).getController().getSender();
                        if ("moving".equals(result.get(0))) {
                            System.out.println("CONTROLELR: MAKING MOVE");
                            out.add("rivalMove");
                            out.add(str.get(1));
                            out.add(str.get(2));
                            out.add(str.get(3));
                            out.add(str.get(4));
                            out.add(str.get(5));
                            System.out.println(out.get(0)+" "+out.get(1)+" "+ out.get(2)+" "+ out.get(3)+" "+ out.get(4));
                            otherSender.send(out);
                        } else if ("cancel".equals(result.get(0))) {
                            sender.send(result);
                        } else if ("replacePawn".equals(result.get(0))){
                            sender.send(result);
                        } else {
                            sender.send(result);
                            otherSender.send(result);
                        }
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
