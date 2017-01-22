package chess.model;

import chess.controller.Controller;
import chess.exceptions.RivalFigureException;
import chess.services.GameService;
import chess.services.PlayerService;
import chess.services.xmlService.XMLSender;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.*;
import java.net.Socket;

/**
 * Created by Admin on 17.01.2017.
 */
public class Player {
    private String login;
    private String password;
    private String nickname;
    private int rank;
    private Status status;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private OutputStream output;
    private Game currentGame;
    private Controller controller;

    /*
    * Убрал "extends Thread", остальное закомментил на всякий случай.
    *
    * bobnewmark 22.01
    *
    * */


//    public Player(Socket socket) {
//        this.socket = socket;
//
//        try {
//            in = new BufferedReader(new InputStreamReader(
//                    socket.getInputStream()));
//            out = new PrintWriter(socket.getOutputStream(), true);
//            output = new DataOutputStream(socket.getOutputStream());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            close();
//        }
//    }

    public Player(Controller controller) {
        this.controller = controller;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

//    @Override
//    public void run() {
//        try {
//            String str = "";
//            while (true) {
//                str = in.readLine();
//                if (str.equals("exit")) break;
//                if (str.equals("reg")) {
//                    PlayerService.reg(this, in, out);
//                }
//                if (str.equals("auth")) {
//                    PlayerService.auth(this);
//                }
//                if (str.equals("callPlayer")) {
//                    out.println("enter nickname your rival");
//                    Player player = GameService.callPlayer(this, in.readLine());
//                    PrintWriter otherOut = new PrintWriter(player.getSocket().getOutputStream(), true);
//                    otherOut.println("confirm");
//                }
//                if (str.equals("confirm")) {
//                    out.println("You are invited. enter Ok or No");
//                    currentGame = GameService.confirmGame(this, in.readLine());
//                }
//                if (str.equals("drag")) {
//                    out.println("enter coordinates of figure - x and y");
//                    try {
//                        int[] steps = GameService.steps(currentGame, Integer.parseInt(in.readLine()), Integer.parseInt(in.readLine()));
//                        out.println("steps");
//                        for (int i : steps) {
//                            out.println(i);
//                        }
//                    } catch (RivalFigureException e) {
//                        out.println("you try taking rivals figure");
//                        e.printStackTrace();
//                    }
//                }
//                if (str.equals("move")) {
//                    int[] steps = new int[0];
//                    try {
//                        steps = GameService.move(currentGame, in, out);
//                    } catch (RivalFigureException e) {
//                        out.println("you try taking rivals figure");
//                        e.printStackTrace();
//                    }
//                    Player otherPlayer = currentGame.getOtherPlayer(this);
//                    System.out.println(otherPlayer.getNickname());
//                    PrintWriter outOther = new PrintWriter(otherPlayer.getSocket().getOutputStream(), true);
//                    outOther.println("step");
//                    for (int i : steps) {
//                        outOther.println(i);
//                    }
//                }
//                if(str.equals("FREEPLAYERS")) {
//                    XMLSender test = new XMLSender();
//                    try {
//                        test.sendFreePlayers(output);
//                    } catch (ParserConfigurationException e) {
//                        e.printStackTrace();
//                    } catch (TransformerConfigurationException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            close();
//        }
//    }
//
//    public void close() {
//        try {
//            in.close();
//            out.close();
//            socket.close();
//        } catch (Exception e) {
//            System.err.println("Thread did not close!");
//        }
//    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;

        Player player = (Player) o;

        if (!login.equals(player.login)) return false;
        return nickname.equals(player.nickname);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + nickname.hashCode();
        return result;
    }
}
