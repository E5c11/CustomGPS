package calculations;

public class Stopwatch {
    private long startTime = 0;
    private boolean running = false;
    private long currentTime = 0;
    private long hours, minutes, seconds, milli;

    public void start() {
        this.startTime = System.nanoTime();
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public void pause() {
        this.running = false;
        currentTime = System.nanoTime() - startTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void resume() {
        this.running = true;
        this.startTime = System.nanoTime() - currentTime;
    }

    //elaspsed time in milliseconds
    public long getElapsedTimeMili() {
        if (running) {
            milli = 0;
            milli =((System.nanoTime() - startTime)/100) % 1000 ;
        }
        return milli;
    }

    public long getMilliSeconds() {
        if (running) {
            seconds = 0;
            seconds = ((System.nanoTime() - startTime) / 1000000);
        }
        return (int) seconds;
    }

    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            seconds = 0;
            seconds = ((System.nanoTime() - startTime) / 1000000000)%60;
        }
        return (int) seconds;
    }

    //elaspsed time in minutes
    public long getElapsedTimeMin() {
        if (running) {
            minutes = 0;
            minutes = (((System.nanoTime() - startTime) / 1000000000) / 60 )%60;
        }
        return (int) minutes;
    }

    //elaspsed time in hours
    public long getElapsedTimeHour() {
        if (running) {
            hours = 0;
            hours = ((((System.nanoTime() - startTime) / 1000000000) / 60 ) / 60);
        }
        return (int) hours;
    }

    public String toString() {
        return getElapsedTimeHour() + ":" + getElapsedTimeMin() + ":"
                + getElapsedTimeSecs();
    }
    public String formatTime() {
        return String.format("%02d",getElapsedTimeHour()) + ":" + String.format("%02d",getElapsedTimeMin()) + ":" + String.format("%02d",getElapsedTimeSecs());
    }
}
