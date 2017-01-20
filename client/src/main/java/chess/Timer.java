package chess;

/**
 * Created by bobnewmark on 20.01.2017
 */
public class Timer extends Thread {

    private int time = 30 * 60;
    private boolean active = true;

    public void run() {
        do {
            while (!getActivity()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int minutes = time / 60;
            int seconds = time % 60;
            String sec = String.valueOf(seconds);
            if(sec.length() < 2) {
                sec = "0" + sec;
            }
            System.out.println(minutes + ":" + sec);

            try {
                Thread.sleep(1000);
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

    public void setActivity(boolean active) {
        if (!active) {
            // TODO: send time to server
        }
        this.active = active;
    }
}
