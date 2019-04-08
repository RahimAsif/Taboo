package net.rahimasif.apps.Taboo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


/**
 * Created by MRahim on 9/6/2017.
 */
public class MyPreferenceActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String USER_ID_TITLE = "User ID";
    private static final String TABLE_NAME_TITLE = "Table Name";
    private static final String MAX_WORDS_PER_PLAYER_TITLE = "Max Words / Player";
    private static final String TIME_PER_PLAYER_TITLE = "Time / Player";

    private NumberPickerPreference prefUserID;
    private ListPreference prefTableName;
    private NumberPickerPreference prefMaxWordsPerPlayer;
    private TimePickerPreference prefTimePerPlayer;
    private CheckBoxPreference prefAllowDirty;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        initializePreferences();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Unregister change listener
        if(settings != null)
        {
            settings.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Register change listener
        if(settings != null)
        {
            settings.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        updatePreferences();
    }

    private void initializePreferences()
    {
        settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext);

        // User ID
        prefUserID = (NumberPickerPreference) findPreference(Settings.USER_ID_KEY);
        // Database Name
        prefTableName = (ListPreference) findPreference(Settings.TABLE_NAME_KEY);
        // Max words per player
        prefMaxWordsPerPlayer =  (NumberPickerPreference) findPreference(Settings.MAX_WORDS_PER_PLAYER_KEY);
        // Time per player
        prefTimePerPlayer = (TimePickerPreference) findPreference(Settings.TIME_PER_PLAYER_KEY);
        // Allow Dirty
        prefAllowDirty = (CheckBoxPreference) findPreference(Settings.ALLOW_DIRTY_WORDS_KEY);

        updatePreferences();
    }

    private void updatePreferences()
    {
        String prefValue;

        // Update the user ID
        prefValue = "[" + Integer.toString(prefUserID.getValue()) + "]";
        prefUserID.setTitle(USER_ID_TITLE + " " + prefValue);

        // Update the table name
        prefValue = "[" + prefTableName.getEntry() + "]";
        prefTableName.setTitle(TABLE_NAME_TITLE + " " + prefValue);

        // Update the max words / player
        prefValue = "[" + Integer.toString(prefMaxWordsPerPlayer.getValue()) + "]";
        prefMaxWordsPerPlayer.setTitle(MAX_WORDS_PER_PLAYER_TITLE + " " + prefValue);

        // Update the time / player
        prefValue = "[" + (prefTimePerPlayer.getTime() + "s") + "]";
        prefTimePerPlayer.setTitle(TIME_PER_PLAYER_TITLE + " " + prefValue);
    }
}
