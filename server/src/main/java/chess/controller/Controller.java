package chess.controller;

import chess.ServerMain;
import chess.model.Game;
import chess.model.Player;
import chess.model.Status;
import chess.services.GameService;
import chess.services.PlayerService;
import chess.services.xmlService.XMLReciever;
import chess.services.xmlService.XMLSender;
import chess.services.xmlService.XMLsaveLoad;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
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
    private LocalTime timer = LocalTime.now();

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
        Thread thread = new Thread(){
            @Override
            public void run() {
                while(true) {
                    if(player.getCurrentGame()!=null) {
                        if(player.getType() != getCurrentGame().getCurrentStep()){
                            timer=LocalTime.now();
                        }else if (LocalTime.now().getMinute() - timer.getMinute() >= 5 ) {
                            List<String> list = new ArrayList<>();
                            List<String> listrival = new ArrayList<>();
                            list.add("5minute");
                            listrival.add("5minuteRival");
                            try {
                                list.add("Ok");
                                listrival.add("Ok");
                                otherPlayer = getCurrentGame().getOtherPlayer(player);
                                player.setRank(player.getRank() - 5);
                                otherPlayer.setRank(otherPlayer.getRank() + 5);
                                list.add(String.valueOf(player.getId()));
                                list.add(player.getLogin());
                                list.add(player.getPassword());
                                list.add(String.valueOf(player.getRank()));
                                listrival.add(String.valueOf(otherPlayer.getId()));
                                listrival.add(otherPlayer.getLogin());
                                listrival.add(otherPlayer.getPassword());
                                listrival.add(String.valueOf(otherPlayer.getRank()));
                                XMLsaveLoad.savePlayers();
                                player.setCurrentGame(null);
                                otherPlayer.setCurrentGame(null);
                                synchronized (ServerMain.inGamePlayers) {
                                    ServerMain.inGamePlayers.remove(player);
                                    ServerMain.inGamePlayers.remove(otherPlayer);
                                }
                                synchronized (ServerMain.freePlayers) {
                                    ServerMain.freePlayers.add(otherPlayer);
                                    ServerMain.freePlayers.add(player);
                                }
                                synchronized (ServerMain.games) {
                                    ServerMain.games.remove(getCurrentGame());
                                }
                                for (Player p : ServerMain.freePlayers) {
                                    if (!player.equals(p)) {
                                        list.add(p.getLogin());
                                        list.add(String.valueOf(p.getRank()));
                                    }
                                    if (!otherPlayer.equals(p)) {
                                        listrival.add(p.getLogin());
                                        listrival.add(String.valueOf(p.getRank()));
                                    }
                                }
                                otherPlayer.getController().getSender().send(listrival);
                                sender.send(list);
                                XMLsaveLoad.savePlayers();
                                timer = LocalTime.now();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (TransformerConfigurationException e) {
                                e.printStackTrace();
                            } catch (TransformerException e) {
                                e.printStackTrace();
                            }
                        } else if (LocalTime.now().getMinute() - timer.getMinute() >= 4 && player.getType() == getCurrentGame().getCurrentStep()) {
                            List<String> list = new ArrayList<>();
                            list.add("4minute");
                            try {
                                sender.send(list);
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (TransformerConfigurationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        sleep(60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void run() {
        try {
            List<String> str = null;
            while (true) {
                try {
                    str = reciever.receive();
                    timer=LocalTime.now();
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
                    List<String> outList = new ArrayList<String>();
                    outList.add("logout");
                    outList.add("logout");
                    sender.send(outList);
                    player = new Player(this);
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
                if ("offerDraw".equals(str.get(0))) {
                    Player otherPlayer = player.getCurrentGame().getOtherPlayer(player);
                    XMLSender otherSender = otherPlayer.getController().getSender();
                    otherSender.send(str);
                }
                if ("acceptDraw".equals(str.get(0))) {
                    GameService.draw(player);
                }
                if ("resign".equals(str.get(0))) {
                    GameService.endGame(str.get(0), player, player.getCurrentGame().getOtherPlayer(player));
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