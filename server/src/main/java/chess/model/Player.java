package chess.model;

/**
 * Created by Admin on 17.01.2017.
 */
public class Player {
    private String login;
    private String password;
    private String nickname;
    private int rank;
    private Status status;

    public Player(String login, String password, String nickname, int rank) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.rank = 50;   // MAGIC NUMBER! NEED TO BE REPLACED BY A CONSTANT
        this.status = Status.FREE;
    }
}
