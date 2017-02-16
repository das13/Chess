package chess.model;

import chess.Constants;
import chess.controller.Controller;

import java.net.Socket;

/**
 * <code>Player</code> is the main unit for user.
 */
public class Player {
    private int id;
    private String login;
    private String password;
    private int rank;
    private String ipadress;
    private Status status;
    private Socket socket;
    private Game currentGame;
    private Controller controller;
    private Type type;

    /**
     * Creates <code>Player</code> with given information as parameters.
     */
    public Player(String login, String password, Status status, String ipadress) {
        this.login = login;
        this.password = password;
        this.rank = Constants.START_RANK;
        this.status = status;
        this.ipadress = ipadress;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Player(Controller controller) {
        this.controller = controller;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public String getIpadress() {
        return ipadress;
    }

    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return login.equals(player.login);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }
}
