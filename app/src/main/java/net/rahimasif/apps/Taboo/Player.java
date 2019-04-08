package net.rahimasif.apps.Taboo;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RahimAsif on 8/20/17.
 */

public class Player
{
    private Game game;
    private String name;
    private int score;
    private List<Round> roundList;
    private Round currentRound;
    private final List<PlayerListener> _eventListeners;

    public interface  PlayerListener
    {
        public void onScoreUpdated(int newScore);
        public void onTurnEnded(Game.GAME_OVER_REASON reason);
    }

    public Player(Game game, String name)
    {
        this.game = game;
        this.name = name;
        this.score = 0;
        this.roundList = new ArrayList<>();
        this._eventListeners = new ArrayList<>();
    }

    public String getName()
    {
        return this.name;
    }

    public int getScore()
    {
        return this.score;
    }

    public void setScore(int newScore)
    {
        if(this.score != newScore)
        {
            this.score = newScore;
            for (int i = 0; i < this._eventListeners.size(); i++)
            {
                this._eventListeners.get(i).onScoreUpdated(newScore);
            }
        }
    }

    public Round getCurrentRound()
    {
        return this.currentRound;
    }

    public List<Round> getRoundList()
    {
        return this.roundList;
    }


    public void startPlaying()
    {
        this.startNewRound();
    }

    private void startNewRound()
    {
        TabooCard t = Game.getNextTabooCard();
        if(t != null)
        {
            this.currentRound = new Round(t, game.getTimeRemaining());
        }
    }

    public void finishCurrentRound(Round.RESULT result)
    {
        Game.GAME_OVER_REASON reason = Game.GAME_OVER_REASON.NONE;

        this.currentRound.setResult(result);
        this.currentRound.setEndTime(game.getTimeRemaining());

        boolean endOfTurn = false;

        switch (result)
        {
            case GOT_IT:
                this.setScore(this.score + 1);
                break;
            case SKIP:
                break;
            case CAUGHT:
                endOfTurn = true;
                reason = Game.GAME_OVER_REASON.CAUGHT;
                break;
            case TIMED_OUT:
                endOfTurn = true;
                reason = Game.GAME_OVER_REASON.TIMED_OUT;
                break;
            default:
                break;
        }

        this.roundList.add(this.currentRound);
        Log.i("Round Info", this.currentRound.toString());

        // See if we have more cards
        if(!endOfTurn && Game.IsOutOfCards())
        {
            endOfTurn = true;
            reason = Game.GAME_OVER_REASON.OUT_OF_CARDS;
        }

        if(!endOfTurn)
        {
            startNewRound();
        }
        else
        {
            for (int i = 0; i < this._eventListeners.size(); i++)
            {
                this._eventListeners.get(i).onTurnEnded(reason);
            }
        }
    }

    public void addListener(PlayerListener listener)
    {
        this._eventListeners.add(listener);
    }

    public void removeListener(PlayerListener listener)
    {
        this._eventListeners.remove(listener);
    }
}

class Round
{
    public enum RESULT
    {
        NOT_ASSIGNED,
        GOT_IT,
        SKIP,
        CAUGHT,
        TIMED_OUT,
    };

    private TabooCard tabooCard;
    private Time startTime;
    private Time endTime;
    private RESULT result;

    public Round(TabooCard tabooCard, Time startTime)
    {
        this.tabooCard = tabooCard;
        this.startTime = new Time(startTime);
        this.result = RESULT.NOT_ASSIGNED;
    }

    public TabooCard getTabooCard()
    {
        return this.tabooCard;
    }

    public Time getStartTime()
    {
        return this.startTime;
    }

    public Time getEndTime()
    {
        return this.endTime;
    }

    public void setEndTime(Time endTime)
    {
        this.endTime = new Time(endTime);
    }

    public RESULT getResult()
    {
        return this.result;
    }

    public void setResult(RESULT result)
    {
        this.result = result;
    }

    @Override
    public String toString()
    {
        String str = "";

        str += "TabooCard: ";
        str += this.tabooCard;
        str += ", ";
        str += "Start Time: ";
        str += this.startTime.toString(true);
        str += ", ";
        str += "End Time: ";
        str += this.endTime.toString(true);
        str += "[";
        str += Utility.getTimeDifference(this.startTime, this.endTime).toString(true);
        str += "]";

        return str;
    }
}



