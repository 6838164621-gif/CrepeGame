package game;

import javax.swing.Timer;
import java.awt.event.ActionListener;

public class GameTimer {
    private Timer timer;        // create timer object that we can use
    private int timeLeft;

    public void start(int seconds, ActionListener onTick, ActionListener onEnd) {       
        this.timeLeft = seconds;                                                        
                                                                                        
        if (timer != null && timer.isRunning()) {
            timer.stop();   // if theres a timer existing, we stop it
        }

        timer = new Timer(1000, e -> {  // create a new timer
            timeLeft--;
            onTick.actionPerformed(e);  // activates every second, updates the timeLeft in the UI

            if (timeLeft <= 0) {
                timer.stop();
                onEnd.actionPerformed(e);   // acvitates when the timer is stopped
            }
        });

        timer.start();
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void stop() {
        if (timer != null) timer.stop();
    }
}