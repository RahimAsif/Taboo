package net.rahimasif.apps.Taboo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RahimAsif on 8/20/17.
 */

public class Timer
{
    public static interface TimerEventsListener
    {
        public void onTimerStarted();
        public void onTimerPaused();
        public void onTimerResumed();
        public void onTimerStopped();
        public void onTimerReset();
        public void onUpdated();
        public void onElapsed();
    }

    public static enum STATE
    {
        NOT_STARTED,
        RUNNING,
        PAUSED,
        ELAPSED,
        STOPPED
    };

    private static class TimerUpdateTask implements Runnable
    {
        private final long PAUSE_SLEEP_MS = 1000;

        private final long initialWait;
        private final long periodicWait;
        private boolean taskCompleted;
        private final Timer timer;

        public TimerUpdateTask(long initialWait, long periodicWait, Timer timer)
        {
            this.initialWait = initialWait;
            this.periodicWait = periodicWait;
            this.timer = timer;
        }

        @Override
        public void run()
        {
            try
            {
                Thread.sleep(this.initialWait);
            }
            catch(InterruptedException ex)
            {
                System.err.println(ex.getMessage());
            }

            this.taskCompleted = false;
            while(!this.taskCompleted)
            {
                try
                {
                    switch(this.timer.getState())
                    {
                        case NOT_STARTED:
                            // Won't come here
                            break;
                        case RUNNING:
                            timer.update();
                            Thread.sleep(this.periodicWait);
                            break;
                        case PAUSED:
                            Thread.sleep(this.PAUSE_SLEEP_MS);
                            break;
                        case ELAPSED:
                            this.taskCompleted = true;
                            break;
                        case STOPPED:
                            this.taskCompleted = true;
                            break;

                        default:
                            break;
                    }


                }
                catch(InterruptedException ex)
                {
                    System.err.println(ex.getMessage());
                }
            }

            System.out.println("Timer background task finished!");
        }
    }

    private final TimerUpdateTask updateTask;
    private Thread updateTaskThread;
    // List of event listeners to this timer
    private final List<TimerEventsListener> eventsListeners;
    // Denotes whether the timer is running
    private STATE currentState;
    // Total time on the timer
    private final Time totalTime;
    // Time elapsed so far
    private final Time timeElapsed;
    // Time left on the timer
    private final Time timeLeft;
    // Holds an array of time elapsed (to calculate pause/resume)
    private final List<Time> elapsedTimeList;
    // The first time the timer was started
    private long initialStartTime;
    // The last time timer wars started
    private long lastStartTime;

    public Timer(Time totalTime, long initialWait, long periodicWait)
    {
        this.currentState = STATE.NOT_STARTED;

        this.totalTime = totalTime;
        this.timeElapsed = new Time(0);
        this.timeLeft = new Time(this.totalTime);

        this.elapsedTimeList = new ArrayList<>();
        this.initialStartTime = 0;
        this.lastStartTime = 0;

        this.updateTask = new TimerUpdateTask(initialWait, periodicWait, this);
        this.eventsListeners = new ArrayList<>();
    }

    public STATE getState()
    {
        return this.currentState;
    }

    public void start()
    {
        if(this.currentState == STATE.NOT_STARTED)
        {
            this.initialStartTime = System.currentTimeMillis();
            this.lastStartTime = initialStartTime;
            this.currentState = STATE.RUNNING;

            this.updateTaskThread = new Thread(this.updateTask);
            this.updateTaskThread.start();

            for(int i = 0; i<this.eventsListeners.size(); i++)
            {
                this.eventsListeners.get(i).onTimerStarted();
            }
        }
    }

    public void pause()
    {
        if(this.currentState == STATE.RUNNING)
        {
            elapsedTimeList.add(new Time(this.getTimeElapsedThisRound()));

            this.currentState = STATE.PAUSED;

            for(int i = 0; i<this.eventsListeners.size(); i++)
            {
                this.eventsListeners.get(i).onTimerPaused();
            }
        }
    }

    public void resume()
    {
        if(this.currentState == STATE.PAUSED)
        {
            this.lastStartTime = System.currentTimeMillis();
            this.currentState = STATE.RUNNING;

            for(int i = 0; i<this.eventsListeners.size(); i++)
            {
                this.eventsListeners.get(i).onTimerResumed();
            }

            update();
        }
    }

    public void stop()
    {
        if(this.currentState != STATE.NOT_STARTED)
        {
            if(this.currentState == STATE.RUNNING)
            {
                elapsedTimeList.add(new Time(this.getTimeElapsedThisRound()));
            }

            this.currentState = STATE.STOPPED;

            for(int i = 0; i<this.eventsListeners.size(); i++)
            {
                this.eventsListeners.get(i).onTimerStopped();
            }
        }
    }

    public void reset()
    {
        if(this.currentState == STATE.ELAPSED ||
                this.currentState == STATE.PAUSED  ||
                this.currentState == STATE.STOPPED)
        {
            this.initialStartTime = 0;
            this.lastStartTime = 0;
            this.elapsedTimeList.clear();
            this.timeElapsed.setTotalMilliseconds(0);
            this.timeLeft.setTotalMilliseconds(this.totalTime.getTotalMilliseconds());

            this.currentState = STATE.NOT_STARTED;

            for(int i = 0; i<this.eventsListeners.size(); i++)
            {
                this.eventsListeners.get(i).onTimerReset();
            }
        }
    }

    private void update()
    {
        // Update time elapsed
        long totalTimeElapsed = 0;
        totalTimeElapsed += this.getPreviouslyElapsedTime();
        totalTimeElapsed += this.getTimeElapsedThisRound();
        if(totalTimeElapsed > this.totalTime.getTotalMilliseconds())
        {
            totalTimeElapsed = this.totalTime.getTotalMilliseconds();
        }
        this.timeElapsed.setTotalMilliseconds(totalTimeElapsed);


        // Update time left
        long timeLeft = this.totalTime.getTotalMilliseconds() - totalTimeElapsed;
        if(timeLeft < 0)
        {
            timeLeft = 0;
        }
        this.timeLeft.setTotalMilliseconds(timeLeft);

        // Do the onUpdated callback
        System.out.println(String.format("Time Elapsed: %s, Time Left: %s", this.timeElapsed, this.timeLeft));
        for(int i = 0; i<this.eventsListeners.size(); i++)
        {
            this.eventsListeners.get(i).onUpdated();
        }

        // Do the time elapsed callback
        if(timeLeft == 0)
        {
            this.currentState = STATE.ELAPSED;

            for(int i = 0; i<this.eventsListeners.size(); i++)
            {
                this.eventsListeners.get(i).onElapsed();
            }
        }
    }

    public void addListener(Timer.TimerEventsListener listener)
    {
        this.eventsListeners.add(listener);
    }

    public void removeListener(Timer.TimerEventsListener listener)
    {
        this.eventsListeners.remove(listener);
    }



    public Time getTotalTime()
    {
        return this.totalTime;
    }

    public Time getTimeLeft()
    {
        return this.timeLeft;
    }

    public Time getTimeElapsed()
    {
        return this.timeElapsed;
    }

    public void setTimeRemaining(Time t)
    {
        if(this.currentState == STATE.PAUSED)
        {
            this.timeLeft.setTotalMilliseconds(t.getTotalMilliseconds());
            this.timeElapsed.setTotalMilliseconds(this.totalTime.getTotalMilliseconds() - this.timeLeft.getTotalMilliseconds());
            this.elapsedTimeList.clear();
            this.elapsedTimeList.add(new Time(this.timeElapsed.getTotalMilliseconds()));
        }
    }

    private long getTimeElapsedThisRound()
    {
        long currentElapsedTime = 0;
        if(currentState == STATE.RUNNING)
        {
            currentElapsedTime = System.currentTimeMillis() - this.lastStartTime;
        }

        return currentElapsedTime;
    }

    private long getPreviouslyElapsedTime()
    {
        long totalTimeElapsed = 0;
        for(int i = 0; i<this.elapsedTimeList.size(); i++)
        {
            totalTimeElapsed += elapsedTimeList.get(i).getTotalMilliseconds();
        }

        return totalTimeElapsed;
    }
}
