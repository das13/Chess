package chess;

import chess.view.GameFrame;
import org.apache.log4j.Logger;

import java.util.Observable;

import static java.lang.Thread.sleep;

/**
 * <code>Timer</code> that is used while playing game. Using Observer patten
 * it refreshes time data every second on <code>GameFrame</code> where it
 * is created. Can be paused and started again.
 */
public class Timer extends Observable implements Runnable {

    private int time = Constants.TIME * 60;
    private boolean active = true;
    private int minutes;
    private int seconds;
    private String sec;
    private final static Logger logger = Logger.getLogger(Timer.class.getClass());

    /**
     * Creates <code>Timer</code> for a given <code>GameFrame</code>
     * where it sends time every second.
     *
     * @param game <code>GameFrame</code> of current player
     */
    public Timer(GameFrame game) {
        addObserver(game);
    }

    public void run() {
        do {
            while (!getActivity()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            minutes = time / 60;
            seconds = time % 60;
            sec = String.valueOf(seconds);
            if(sec.length() < 2) {
                sec = "0" + sec;
            }
            String message = minutes + ":" + sec;
            setChanged();
            notifyObservers(message);

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                logger.info("Interrupted timer, probably end of game.");
            }
            time = time - 1;
        }
        while (time > 0);
        logger.info("Time is up");
    }

    private boolean getActivity() {
        return active;
    }

    public void stopTimer() {
        active = false;
    }

    public void startTimer() {
        active = true;
    }

    public String getTime() {
        return minutes + ":" + sec;
    }
}
