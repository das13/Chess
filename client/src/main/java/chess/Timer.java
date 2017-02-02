package chess;

import chess.view.GameFrame;

import java.util.Observable;
import java.util.Observer;

import static java.lang.Thread.sleep;

/**
 * Created by bobnewmark on 20.01.2017
 */
public class Timer extends Observable implements Runnable {

    private int time = 30 * 60;
    private boolean active = true;
    private GameFrame game;
    int minutes;
    int seconds;
    String sec;

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
            //System.out.println(minutes + ":" + sec);

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time = time - 1;
        }
        while (time > 0);
        System.out.println("You lose!");
    }

    public boolean getActivity() {
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

    public int getTimeForServer(){
        return time;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }
}
