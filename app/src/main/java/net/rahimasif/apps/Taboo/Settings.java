package net.rahimasif.apps.Taboo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by MRahim on 9/8/2017.
 */

public final class Settings
{
    public static final String IS_FIRST_RUN_KEY = "isFirstRun";
    public static final String USER_ID_KEY = "userID";
    public static final String TABLE_NAME_KEY = "tableName";
    public static final String MAX_WORDS_PER_PLAYER_KEY = "wordsPerPlayer";
    public static final String TIME_PER_PLAYER_KEY = "timePerPlayer";
    public static final String ALLOW_DIRTY_WORDS_KEY = "allowDirtyWords";

    public static final boolean DEFAULT_IS_FIRST_RUN = true;
    public static final int DEFAULT_USER_ID = 1;
    public static final String DEFAULT_TABLE_NAME = "testTable";
    public static final int DEFAULT_MAX_WORDS_PER_PLAYER = 50;
    public static final String DEFAULT_TIME_PER_PLAYER = "1:30";
    public static final boolean DEFAULT_ALLOW_DIRTY_WORDS = true;

    public static boolean getIsFirstRun()
    {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext).getBoolean(IS_FIRST_RUN_KEY, DEFAULT_IS_FIRST_RUN);
    }

    public static int getUserID()
    {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext).getInt(USER_ID_KEY, DEFAULT_USER_ID);
    }

    public static String getTableName()
    {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext).getString(TABLE_NAME_KEY, DEFAULT_TABLE_NAME);
    }

    public static int getMaxWordsPerPlayer()
    {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext).getInt(MAX_WORDS_PER_PLAYER_KEY, DEFAULT_MAX_WORDS_PER_PLAYER);
    }

    public static String getTimePerPlayer()
    {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext).getString(TIME_PER_PLAYER_KEY, DEFAULT_TIME_PER_PLAYER);
    }

    public static boolean getAllowDirtyWords()
    {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext).getBoolean(ALLOW_DIRTY_WORDS_KEY, DEFAULT_ALLOW_DIRTY_WORDS);
    }


    public static void setDefaults()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext);

        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(IS_FIRST_RUN_KEY, false);
        editor.putInt(USER_ID_KEY, DEFAULT_USER_ID);
        editor.putString(TABLE_NAME_KEY, DEFAULT_TABLE_NAME);
        editor.putInt(MAX_WORDS_PER_PLAYER_KEY, DEFAULT_MAX_WORDS_PER_PLAYER);
        editor.putString(DEFAULT_TIME_PER_PLAYER, DEFAULT_TIME_PER_PLAYER);
        editor.putBoolean(ALLOW_DIRTY_WORDS_KEY, DEFAULT_ALLOW_DIRTY_WORDS);

        editor.commit();
    }
}
