package net.rahimasif.apps.Taboo;

/*
 * Created by RahimAsif on 8/20/17.
 */

public final class Time
{
    private int hours;
    private int minutes;
    private int seconds;
    private int milliseconds;

    public int getHours()
    {
        return hours;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public int getSeconds()
    {
        return seconds;
    }

    public long getMilliseconds()
    {
        return milliseconds;
    }

    public float getTotalSeconds()
    {
        float value;

        value = 0.0f;
        value += (hours * 3600);
        value += (minutes * 60);
        value += seconds;
        value += (float) milliseconds / (float) 1000.0f;

        return value;
    }

    public long getTotalMilliseconds()
    {
        long value;

        value = 0;
        value += (hours * 3600 * 1000);
        value += (minutes * 60 * 1000);
        value += (seconds * 1000);
        value += milliseconds;

        return value;
    }

    public void setTotalSeconds(float seconds)
    {
        int _tempSeconds = (int) seconds;

        milliseconds = (int) ((seconds - (float) _tempSeconds) * 1000);
        hours = (_tempSeconds / 3600);
        minutes = (_tempSeconds / 60);
        this.seconds = _tempSeconds % 60;

        System.out.println("Total Seconds Set: " + this.toString());
    }

    public void setTotalMilliseconds(long milliseconds)
    {
        hours = (int) ((milliseconds / (1000*60*60)) % 24);
        minutes = (int) ((milliseconds / (1000*60)) % 60);
        seconds = (int) (milliseconds / 1000) % 60 ;
        this.milliseconds = (int) (milliseconds % 1000);
        System.out.println("Total Milliseconds Set: " + this.toString());
    }

    public Time(int hours, int minutes, int seconds, int milliseconds)
    {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }

    public Time(int hours, int minutes, int seconds)
    {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        milliseconds = 0;
    }

    public Time(int hours, int minutes)
    {
        this.hours = hours;
        this.minutes = minutes;
        seconds = 0;
        milliseconds = 0;
    }

    public Time(float seconds)
    {
        this.setTotalSeconds(seconds);
    }

    public Time(long milliseconds)
    {
        this.setTotalMilliseconds(milliseconds);
    }

    public Time(Time t)
    {
        this.hours = t.hours;
        this.minutes = t.minutes;
        this.seconds = t.seconds;
        this.milliseconds = t.milliseconds;
    }

    @Override
    public String toString()
    {
        String str = String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);

        return str;
    }

    public String toString(boolean tabooTimeFormat)
    {
        String str = String.format("%d:%02d.%02d", minutes, seconds, milliseconds / 10);

        return str;
    }

    public static Time parse(String s)
    {
        String[] pieces = s.split(":");
        int minutes = Integer.parseInt(pieces[0]);
        int seconds = Integer.parseInt(pieces[1]);

        Time t = new Time(0, minutes, seconds, 0);

        return t;
    }
}
