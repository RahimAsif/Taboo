package net.rahimasif.apps.Taboo;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.content.Intent;
import android.content.Context;

public class MainActivity extends AppCompatActivity implements Game.GameListener
{
    // Sound related items
    private SoundPool soundPool;
    private int soundID_GotIt;
    private int soundID_caught;
    private int soundID_Skip;
    private int soundID_timesUp;

    // UI Controls
    private Button btnPlay;
    private TextView tvScoreLabel;
    private TextView tvScoreValue;
    private Button btnCaught;
    private Button btnSkip;
    private Button btnGotIt;
    private TextView tvWordPhrase;
    private TextView tvForbiddenWord1;
    private TextView tvForbiddenWord2;
    private TextView tvForbiddenWord3;
    private TextView tvForbiddenWord4;
    private TextView tvForbiddenWord5;
    private TextView tvTimer;

    // The one and only Game object
    private Game theGame;

    public static SharedPreferences settings;
    public static Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("LifeCycle Method", "OnCreate() called");

        // Set the application context
        applicationContext = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.initializeSettings();
        this.initializeSound();
        this.initializeControls();
        this.initializeGame();
    }



    @Override
    protected void onStart()
    {
        Log.i("LifeCycle Method", "OnStart() called");
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        Log.i("LifeCycle Method", "OnResume() called");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.i("LifeCycle Method", "OnPause() called");

        theGame.pause();

        super.onPause();
    }

    @Override
    protected void onStop()
    {
        Log.i("LifeCycle Method", "OnStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("LifeCycle Method", "OnDestroy() called");
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        Log.i("LifeCycle Method", "OnRestart() called");
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent i = new Intent(this, MyPreferenceActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initializeSettings()
    {
        if(Settings.getIsFirstRun())
        {
            Settings.setDefaults();
        }
    }

    private void initializeSound()
    {
        // Load the sounds
        this.soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        this.soundID_GotIt = soundPool.load(this, R.raw.success, 1);
        this.soundID_Skip = soundPool.load(this, R.raw.skip, 1);
        this.soundID_caught = soundPool.load(this, R.raw.caught, 1);
        this.soundID_timesUp = soundPool.load(this, R.raw.times_up, 1);
    }

    private void initializeControls()
    {

        this.btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (theGame.getState())
                {
                    case WAITING_FOR_NEW_PLAYER:
                    case PLAYER_TURN_ENDED:
                        new AsyncGetTabooCards(MainActivity.this).execute();
                        break;
                    case PLAYING:
                        theGame.pause();
                        break;
                    case PAUSED:
                        theGame.resume();
                        break;
                    default:
                        break;
                }
            }
        });


        btnGotIt = (Button) findViewById(R.id.btnGotIt);
        btnGotIt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                soundPool.play(soundID_GotIt, (float) 1.0, (float) 1.0, 1, 0, (float) 1.0);
                theGame.setRoundResult(Round.RESULT.GOT_IT);
                updateTabooCard();
            }
        });


        this.btnSkip = (Button) findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                soundPool.play(soundID_Skip, (float) 1.0, (float) 1.0, 1, 0, (float) 1.0);
                theGame.setRoundResult(Round.RESULT.SKIP);
                updateTabooCard();
            }
        });

        this.btnCaught = (Button) findViewById(R.id.btnCaught);
        this.btnCaught.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                theGame.setRoundResult(Round.RESULT.CAUGHT);
            }
        });

        this.tvScoreLabel = (TextView) findViewById(R.id.tvScoreLabel);
        this.tvScoreValue = (TextView) findViewById(R.id.tvScoreValue);

        this.tvWordPhrase = (TextView) findViewById(R.id.tvTabooWordPhrase);
        this.tvForbiddenWord1 = (TextView) findViewById(R.id.tvForbiddenWord1);
        this.tvForbiddenWord2 = (TextView) findViewById(R.id.tvForbiddenWord2);
        this.tvForbiddenWord3 = (TextView) findViewById(R.id.tvForbiddenWord3);
        this.tvForbiddenWord4 = (TextView) findViewById(R.id.tvForbiddenWord4);
        this.tvForbiddenWord5 = (TextView) findViewById(R.id.tvForbiddenWord5);

        this.tvTimer = (TextView) findViewById(R.id.tvTimer);
        this.tvTimer.setFocusableInTouchMode(true);
        this.tvTimer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(theGame.getState() == Game.STATE.PAUSED)
                {
                    showDialog();
                }
            }
        });

        // Initialize the score to 0
        this.tvScoreValue.setText("0");
        // Hide the timer
        this.tvTimer.setVisibility(View.INVISIBLE);
        // Make the buttons disabled
        this.btnCaught.setEnabled(false);
        this.btnSkip.setEnabled(false);
        this.btnGotIt.setEnabled(false);
    }

    private void initializeGame()
    {
        this.theGame = new Game();
        theGame.addListener(this);
    }

    private void startNewGame()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                theGame.start();
                updatePlayPauseStopButton();
                updateScore(0);
                updateTabooCard();
            }
        });
    }

    public void showDialog()
    {
        Time t = theGame.getTimeRemaining();
        String timeLeftStr = String.format("%02d:%02d", t.getMinutes(), t.getSeconds());

        SetTimeDialog newFragment = SetTimeDialog.newInstance(timeLeftStr);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick(String results)
    {
        String [] s = results.split(":");

        int minutes = Integer.parseInt(s[0].trim());
        int seconds = Integer.parseInt(s[1].trim());
        long totalMilliseconds = (minutes * 60 * 1000) + (seconds * 1000) + theGame.getTimeRemaining().getMilliseconds();

        Time timeLeft = new Time(totalMilliseconds);
        theGame.setTimeRemaining(timeLeft);
        updateTimerText(timeLeft);
    }

    public void doNegativeClick()
    {

    }

    public void doGotNewCards()
    {
        this.startNewGame();
    }


    @Override
    public void onPlayerScoreUpdated(Player p, int newScore)
    {
        updateScore(newScore);
    }

    @Override
    public void onPlayerTurnEnded(Player p, Game.GAME_OVER_REASON reason)
    {
        updatePlayPauseStopButton();
        updateRoundResultButtons();
        updateTimerControls(true, false);

        String gameOverReason = "";
        switch(reason)
        {
            case CAUGHT:
                soundPool.play(soundID_caught, (float) 1.0, (float) 1.0, 1, 0, (float) 1.0);
                gameOverReason = "Game Over: CAUGHT";
                break;
            case TIMED_OUT:
                soundPool.play(soundID_timesUp, (float) 1.0, (float) 1.0, 1, 0, (float) 1.0);
                gameOverReason = "Game Over: Out of Time";
                break;
            case OUT_OF_CARDS:
                gameOverReason = "Game Over: Out of Taboo Cards";
                break;
        }

        // Display a snackbar at the end of the game
        final Snackbar snackBar = Snackbar.make(MainActivity.this.btnPlay, gameOverReason, Snackbar.LENGTH_INDEFINITE);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Create the snackbar
                // Set the dismiss button
                snackBar.setAction("Dismiss", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        snackBar.dismiss();
                    }
                });
                // Display the snackbar
                snackBar.show();
            }
        });
    }

    @Override
    public void onGameTimerStarted(Time timeElapsed, Time timeLeft)
    {
       updatePlayPauseStopButton();
       updateRoundResultButtons();
       updateTimerControls(true, false);
       updateTimerText(timeLeft);
    }

    @Override
    public void onGameTimerPaused(Time timeElapsed, Time timeLeft)
    {
        updatePlayPauseStopButton();
        updateRoundResultButtons();
        updateTimerControls(true, true);
        updateTimerText(timeLeft);
    }

    @Override
    public void onGameTimerResumed(Time timeElapsed, Time timeLeft)
    {
        updatePlayPauseStopButton();
        updateRoundResultButtons();
        updateTimerControls(true, false);
        updateTimerText(timeLeft);
    }

    @Override
    public void onGameTimerStopped(Time timeElapsed, Time timeLeft)
    {
        updateTimerControls(true, false);
        updateRoundResultButtons();
        updateTimerText(timeLeft);
    }

    @Override
    public void onGameTimerReset(Time timeElapsed, Time timeLeft)
    {
        updatePlayPauseStopButton();
        updateRoundResultButtons();
        updateTimerControls(true, false);
        updateTimerText(timeLeft);
    }

    @Override
    public void onGameTimerUpdated(Time timeElapsed, Time timeLeft)
    {
        this.updateTimerText(timeLeft);
    }

    @Override
    public void onGameTimerElapsed(Time timeElapsed, Time timeLeft)
    {
        theGame.setRoundResult(Round.RESULT.TIMED_OUT);
        this.updateTimerText(timeLeft);
    }

    private void updatePlayPauseStopButton()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                switch (theGame.getState())
                {
                    case WAITING_FOR_NEW_PLAYER:
                    case PLAYER_TURN_ENDED:
                        btnPlay.setText("Play");
                        break;
                    case PLAYING:
                        btnPlay.setText("Pause");
                        break;
                    case PAUSED:
                        btnPlay.setText("Resume");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void updateScore(final int score)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
               tvScoreValue.setText(String.valueOf(score));
            }
        });
    }



    private void updateRoundResultButtons()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                switch (theGame.getState())
                {
                    case WAITING_FOR_NEW_PLAYER:
                    case PLAYER_TURN_ENDED:
                        btnGotIt.setEnabled(false);
                        btnSkip.setEnabled(false);
                        btnCaught.setEnabled(false);
                        break;
                    case PLAYING:
                        btnGotIt.setEnabled(true);
                        btnSkip.setEnabled(true);
                        btnCaught.setEnabled(true);
                        break;
                    case PAUSED:
                        btnGotIt.setEnabled(false);
                        btnSkip.setEnabled(false);
                        btnCaught.setEnabled(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void updateTimerControls(final boolean visible, final boolean editEnable)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(visible)
                {
                    tvTimer.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvTimer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateTimerText(final Time timeLeft)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Time totalTime = theGame.getTotalTime();

                float percentLeft = timeLeft.getTotalSeconds() /  totalTime.getTotalSeconds() * 100.0f;
                if(percentLeft > 50)
                {
                    tvTimer.setTextColor(getResources().getColor(R.color.dark_green));
                }
                else if(percentLeft > 25)
                {
                    tvTimer.setTextColor(getResources().getColor(R.color.dark_orange));
                }
                else
                {
                    tvTimer.setTextColor(getResources().getColor(R.color.dark_red));
                }

                tvTimer.setText(timeLeft.toString(true));
            }
        });
    }

    private void updateTabooCard()
    {
        final TabooCard tabooCard = theGame.getTabooCard();

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(tabooCard != null)
                {
                    tvWordPhrase.setText(tabooCard.getWordOrPhrase());
                    tvForbiddenWord1.setText(Utility.toTitleCase((tabooCard.getForbiddenWords().get(0))));
                    tvForbiddenWord2.setText(Utility.toTitleCase((tabooCard.getForbiddenWords().get(1))));
                    tvForbiddenWord3.setText(Utility.toTitleCase((tabooCard.getForbiddenWords().get(2))));
                    tvForbiddenWord4.setText(Utility.toTitleCase((tabooCard.getForbiddenWords().get(3))));
                    tvForbiddenWord5.setText(Utility.toTitleCase((tabooCard.getForbiddenWords().get(4))));
                }
            }
        });

        if(tabooCard != null)
        {
            new AsyncMarkPlayedWords(MainActivity.this).execute(tabooCard);
        }
    }
}
