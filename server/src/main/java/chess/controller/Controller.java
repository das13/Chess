package chess.controller;

import chess.ServerMain;
import chess.model.Game;
import chess.model.Player;
import chess.model.Status;
import chess.services.GameService;
import chess.services.PlayerService;
import chess.services.xmlService.XMLReceiver;
import chess.services.xmlService.XMLSender;
import chess.services.xmlService.XMLsaveLoad;
import org.apache.log4j.Logger;
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
 * <code>Controller</code> is the main communication unit that
 * handles all remote client messages and provides proper answers.
 * Though <code>Controller</code> user logs in, registers, finds
 * other players and plays game.
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
    private XMLReceiver receiver;
    private LocalTime timer = LocalTime.now();
    public final static Logger logger = Logger.getLogger(Controller.class);

    public Controller(Socket socket) {
        this.socket = socket;
        player = new Player(this);
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            sender = new XMLSender(out);
            receiver = new XMLReceiver(in);
        } catch (IOException e) {
            logger.error("Error establishing connection with client", e);
            close();
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (player.getCurrentGame() != null) {
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
                            } catch (ParserConfigurationException | TransformerException | IOException e) {
                                logger.error("Error sending " + list.get(0) + " ", e);
                            }
                        } else if (LocalTime.now().getMinute() - timer.getMinute() >= 4 && player.getType() == getCurrentGame().getCurrentStep()) {
                            List<String> list = new ArrayList<>();
                            list.add("4minute");
                            try {
                                sender.send(list);
                            } catch (ParserConfigurationException | TransformerConfigurationException | IOException e) {
                                logger.error("Error sending " + list.get(0) + " ", e);
                            }
                        }
                    }
                    try {
                        sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        logger.info("Waiting for player activity was interrupted. Probably no error");
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
                    str = receiver.receive();
                    timer = LocalTime.now();
                } catch (ParserConfigurationException | SAXException | TransformerConfigurationException e) {
                    logger.error("Error getting time from xml", e);
                } catch (SocketException e) {
                    logger.error("Error getting data from socket", e);
                    break;
                }
                switch (str.get(0)) {
                    case "exit": {
                        System.exit(0);
                        break;
                    }
                    case "reg": {
                        String ip = socket.getInetAddress().toString();
                        PlayerService.reg(player, str.get(1), str.get(2), ip, sender);
                        break;
                    }
                    case "auth": {
                        PlayerService.auth(player, str.get(1), str.get(2), sender);
                        break;
                    }
                    case "refresh": {
                        List<String> outList = new ArrayList<String>();
                        outList.add("refresh");
                        outList.addAll(PlayerService.refresh(player, sender));
                        sender.send(outList);
                        break;
                    }
                    case "logout": {
                        synchronized (ServerMain.freePlayers) {
                            ServerMain.freePlayers.remove(player);
                        }
                        List<String> outList = new ArrayList<String>();
                        outList.add("logout");
                        outList.add("logout");
                        sender.send(outList);
                        player = new Player(this);
                        break;
                    }
                    case "saveProfile": {
                        sender.send(PlayerService.saveProfile(str.get(2), str.get(3), Integer.parseInt(str.get(1))));
                        break;
                    }
                    case "callPlayer": {
                        GameService.callPlayer(getPlayer(), str.get(1));
                        break;
                    }
                    case "confirm": {
                        GameService.confirmGame(getPlayer(), str);
                        break;
                    }
                    case "drag": {
                        sender.send(GameService.steps(getCurrentGame(), Integer.parseInt(str.get(1)), Integer.parseInt(str.get(2))));
                        break;
                    }
                    case "move": {
                        GameService.move(getCurrentGame(), str, player);
                        break;
                    }
                    case "replacePawn": {
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
                        break;
                    }
                    case "admin_getPlayers": {
                        PlayerService.adminGetPlayers(sender);
                        break;
                    }
                    case "offerDraw": {
                        Player otherPlayer = player.getCurrentGame().getOtherPlayer(player);
                        XMLSender otherSender = otherPlayer.getController().getSender();
                        otherSender.send(str);
                        break;
                    }
                    case "acceptDraw": {
                        GameService.draw(player);
                        break;
                    }
                    case "resign": {
                        GameService.endGame(str.get(0), player, player.getCurrentGame().getOtherPlayer(player));
                        break;
                    }
                    case "ban": {
                        PlayerService.ban(str, sender);
                        break;
                    }
                    case "allowRestoreMove": {
                        Player otherPlayer = player.getCurrentGame().getOtherPlayer(player);
                        XMLSender otherSender = otherPlayer.getController().getSender();
                        otherSender.send(str);
                        break;
                    }
                    case "acceptRestore": {
                        Player otherPlayer = player.getCurrentGame().getOtherPlayer(player);
                        GameService.restoreLastMove(player, otherPlayer, getCurrentGame());
                        break;
                    }
                    default:
                        logger.error("Client sent message that server doesn't understand " + str.get(0));
                }
            }
        } catch (IOException e) {
            logger.error("Error saving players", e);
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            logger.error("Error working with xml", e);
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
            logger.error("Thread didn't close", e);
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