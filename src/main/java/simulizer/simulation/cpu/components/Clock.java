package simulizer.simulation.cpu.components;

/**
 * Created by matthew on 13/09/16.
 */
public class Clock {
    public enum Status {
        STOPPED,
        RUNNING,
        PAUSED
    }
    private long tickPeriod; // in ns (10^-9 seconds)
    private Status status;

    private long lastTickns;
    private long ticks;

    Clock() {
        tickPeriod = 0;
        status = Status.STOPPED;
        lastTickns = 0;
        ticks = 0;
    }

    void setTickFrequency(double freq) {
        if (freq < 0.00001) {
            // practically zero => max speed
            tickPeriod = 0;
        } else {
            tickPeriod = (long) (1e9 / freq);
        }
    }

    double getTickFrequency() {
        return 1e9 / tickPeriod;
    }

    long getTicks() {
        return ticks;
    }

    synchronized void waitForNextTick() throws InterruptedException {
        while(status != Status.RUNNING) {
            wait(400); // notified when status changes or reasonable timeout in-case already stopped but missed the notify
            if(status == Status.STOPPED) {
                return;
            }
        }

        if (tickPeriod == 0) {
            ++ticks;
            return;
        }

        long tickDuration;
        for (; ; ) {
            tickDuration = System.nanoTime() - lastTickns;

            if (tickDuration >= tickPeriod)
                break;
            else if (tickPeriod - tickDuration > 10000) // >10ms left to wait
                Thread.sleep(8);
        }

        lastTickns = System.nanoTime();
        ++ticks;
    }


    synchronized void pause() {
        status = Status.PAUSED;
        notify();
    }
    synchronized void resume() {
        if(status == Status.STOPPED)
            throw new IllegalStateException("cannot resume stopped clock");
        status = Status.RUNNING;
        notify();
    }

    synchronized void stop() {
        status = Status.STOPPED;
        notify();
    }

    synchronized void start() {
        lastTickns = System.nanoTime();
        ticks = 0;
        status = Status.RUNNING;
        notify();
    }

    synchronized Status getStatus() {
        return status;
    }
}
