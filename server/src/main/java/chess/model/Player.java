package chess.model;

import chess.services.GameService;
import chess.services.PlayerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Admin on 17.01.2017.
 */
public class Player extends Thread{
    private String login;
    private String password;
    private String nickname;
    private int rank;
    private Status status;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Game currentGame;
    public Player(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
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
    @Override
    public void run() {
        try {
            String str = "";
            while (true) {
                str = in.readLine();
                if(str.equals("exit")) break;
                if(str.equals("reg")){
                    PlayerService.reg(this, in, out);
                }
                if(str.equals("auth")){
                    PlayerService.auth(this);
                }
                if(str.equals("callPlayer")){
                    out.println("enter nickname your rival");
                    Player player = GameService.callPlayer(this, in.readLine());
                    PrintWriter otherOut  = new PrintWriter(player.getSocket().getOutputStream(), true);
                    otherOut.println("confirm");
                }
                if(str.equals("confirm")){
                    out.println("You are invited. enter Ok or No");
                    currentGame = GameService.confirmGame(this, in.readLine());
                }
                if(str.equals("drag")){
                    out.println("enter coordinates of figure - x and y");
                    int[] steps = GameService.steps(currentGame, Integer.parseInt(in.readLine()), Integer.parseInt(in.readLine()));
                    out.println("steps");
                    for(int i: steps) {
                        out.println(i);
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
