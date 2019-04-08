package net.rahimasif.apps.Taboo;

import android.os.Bundle;
import java.util.*;

/**
 * Created by RahimAsif on 8/20/17.
 */
public class Game implements Player.PlayerListener, Timer.TimerEventsListener {
    // Constant declarations
    public final long INITIAL_TIMER_DELAY = 0; // in milliseconds
    public final long TIMER_CHECK_PERIOD = 125; // in milliseconds

    private STATE state;
    private Timer gameTimer;
    private Player currentPlayer;
    private List<Player> playerList;
    private List<GameListener> eventListeners;

    private static int cardIndex;
    private static List<TabooCard> tabooCardList;

    public enum GAME_OVER_REASON
    {
        NONE,
        CAUGHT,
        TIMED_OUT,
        OUT_OF_CARDS
    }

    public enum STATE
    {
        WAITING_FOR_NEW_PLAYER,
        PLAYING,
        PAUSED,
        PLAYER_TURN_ENDED
    }

    public interface GameListener {
        // Timer related events
        public void onGameTimerStarted(Time timeElapsed, Time timeRemaining);

        public void onGameTimerPaused(Time timeElapsed, Time timeRemaining);

        public void onGameTimerResumed(Time timeElapsed, Time timeRemaining);

        public void onGameTimerStopped(Time timeElapsed, Time timeRemaining);

        public void onGameTimerUpdated(Time timeElapsed, Time timeRemaining);

        public void onGameTimerReset(Time timeElapsed, Time timeRemaining);

        public void onGameTimerElapsed(Time timeElapsed, Time timeRemaining);

        // Player related events
        public void onPlayerScoreUpdated(Player p, int newScore);

        public void onPlayerTurnEnded(Player p, GAME_OVER_REASON reason);
    }

    public Game()
    {
        // Initialize the state
        this.state = STATE.WAITING_FOR_NEW_PLAYER;

        // Player List and current player initialization
        this.currentPlayer = null;
        this.playerList = new ArrayList<>();
        // Event Listener initialization
        this.eventListeners = new ArrayList<>();

        // Populate the list of taboo words
        cardIndex = -1;
        tabooCardList = new ArrayList<>();
    }

    public void start()
    {
        if (state == STATE.WAITING_FOR_NEW_PLAYER || state == STATE.PLAYER_TURN_ENDED)
        {
            this.state = STATE.PLAYING;

            cardIndex = 0;

            // Create the game timer
            if (this.gameTimer != null) {
                // Remove the listener
                this.gameTimer.removeListener(this);
                // Mark the object for garbage collection
                this.gameTimer = null;
            }

            Time t = new Time(Time.parse(Settings.getTimePerPlayer()));
            this.gameTimer = new Timer(t, INITIAL_TIMER_DELAY, TIMER_CHECK_PERIOD);
            this.gameTimer.addListener(this);


            this.gameTimer.reset();

            this.currentPlayer = new Player(this, "");
            this.currentPlayer.addListener(this);
            this.currentPlayer.startPlaying();

            this.gameTimer.start();
        }
    }

    public void pause()
    {
        if (state == STATE.PLAYING) {
            this.state = STATE.PAUSED;

            this.gameTimer.pause();
        }
    }

    public void resume()
    {
        if (state == STATE.PAUSED) {
            this.state = STATE.PLAYING;

            this.gameTimer.resume();
        }
    }

    public void setRoundResult(Round.RESULT result)
    {
        if (currentPlayer != null) {
            currentPlayer.finishCurrentRound(result);
        }
    }

    public void setTimeRemaining(Time timeLeft) {
        this.gameTimer.setTimeRemaining(timeLeft);
    }

    public Time getTimeRemaining() {
        return this.gameTimer.getTimeLeft();
    }

    public Time getTimeElapsed() {
        return this.gameTimer.getTimeElapsed();
    }


    public STATE getState() {
        return this.state;
    }

    public TabooCard getTabooCard()
    {
        TabooCard card = null;

        if (this.currentPlayer != null && this.currentPlayer.getCurrentRound() != null)
        {
            card = this.currentPlayer.getCurrentRound().getTabooCard();
        }

        return card;
    }

    @Override
    public void onScoreUpdated(int newScore) {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onPlayerScoreUpdated(this.currentPlayer, newScore);
        }
    }

    @Override
    public void onTurnEnded(GAME_OVER_REASON reason)
    {
        if (currentPlayer != null) {
            this.gameTimer.stop();
            this.state = STATE.PLAYER_TURN_ENDED;
            this.currentPlayer.removeListener(this);
            playerList.add(this.currentPlayer);
            for (int i = 0; i < this.eventListeners.size(); i++)
            {
                this.eventListeners.get(i).onPlayerTurnEnded(this.currentPlayer, reason);
            }
        }
    }

    @Override
    public void onTimerStarted() {
        for (int i = 0; i < this.eventListeners.size(); i++)
        {
            this.eventListeners.get(i).onGameTimerStarted(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    @Override
    public void onTimerPaused() {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onGameTimerPaused(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    @Override
    public void onTimerResumed() {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onGameTimerResumed(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    @Override
    public void onTimerStopped() {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onGameTimerStopped(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    @Override
    public void onTimerReset() {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onGameTimerReset(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    @Override
    public void onUpdated() {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onGameTimerUpdated(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    @Override
    public void onElapsed() {
        for (int i = 0; i < this.eventListeners.size(); i++) {
            this.eventListeners.get(i).onGameTimerElapsed(this.gameTimer.getTimeElapsed(), this.gameTimer.getTimeLeft());
        }
    }

    public void addListener(GameListener listener) {
        this.eventListeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        this.eventListeners.remove(listener);
    }


    public static void clearTabooCardList()
    {
        tabooCardList.clear();
    }

    public static void addTabooCard(TabooCard t)
    {
        tabooCardList.add(t);
    }

    public Time getTotalTime()
    {
        return this.gameTimer.getTotalTime();
    }


    public static TabooCard getNextTabooCard()
    {
        TabooCard t = null;

        if(cardIndex >= 0 && cardIndex < tabooCardList.size())
        {
            t = tabooCardList.get(cardIndex);
            cardIndex++;
        }

        return t;
    }

    public static boolean IsOutOfCards()
    {
        return (cardIndex >= tabooCardList.size());
    }

    public void saveGameState(Bundle bundle)
    {
        // Current Player Info
        {
            // Timer info
            bundle.putLong("TotalTime", gameTimer.getTotalTime().getTotalMilliseconds());
            bundle.putLong("TimeElapsed", gameTimer.getTimeElapsed().getTotalMilliseconds());
            bundle.putLong("TimeLeft", gameTimer.getTimeLeft().getTotalMilliseconds());

            bundle.putString("CurrentPlayerName", currentPlayer.getName());
            bundle.putInt("CurrentPlayerScore", currentPlayer.getScore());
            bundle.putString("CurrentPlayerTabooCard", currentPlayer.getCurrentRound().getTabooCard().toString());
            // Previous round info
            bundle.putInt("CurrentPlayerNumRounds", currentPlayer.getRoundList().size());
            int roundIndex = 0;
            for (Round round : currentPlayer.getRoundList())
            {
                bundle.putString("CurrentPlayerRound" + roundIndex + "TabooCard", round.getTabooCard().toString());
                bundle.putString("CurrentPlayerRound" + roundIndex + "StartTime", round.getStartTime().toString(true));
                bundle.putString("CurrentPlayerRound" + roundIndex + "EndTime", round.getEndTime().toString(true));
                bundle.putInt("CurrentPlayerRound" + roundIndex + "Result", round.getResult().ordinal());

                roundIndex++;
            }
        }
        // Previous Player Info
        int playerIndex = 0;
        bundle.putInt("NumPlayers", this.playerList.size());
        for(Player p : this.playerList)
        {
            bundle.putString("Player" + playerIndex + "Name", p.getName());
            bundle.putInt("Player" + playerIndex + "Score", p.getScore());
            bundle.putInt("Player" + playerIndex + "NumRounds", p.getRoundList().size());
            int roundIndex = 0;
            for (Round round : p.getRoundList())
            {
                bundle.putString("Player" + playerIndex + "Round" + roundIndex + "TabooCard", round.getTabooCard().toString());
                bundle.putString("Player" + playerIndex + "Round" + roundIndex + "StartTime", round.getStartTime().toString(true));
                bundle.putString("Player" + playerIndex + "Round" + roundIndex + "EndTime", round.getEndTime().toString(true));
                bundle.putInt("Player" + playerIndex + "Round" + roundIndex + "Result", round.getResult().ordinal());

                roundIndex++;
            }
            playerIndex++;
        }
    }
}

