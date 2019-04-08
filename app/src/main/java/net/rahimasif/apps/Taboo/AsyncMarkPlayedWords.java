package net.rahimasif.apps.Taboo;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by MRahim on 9/6/2017.
 */
public class AsyncMarkPlayedWords extends AsyncTask<TabooCard, Void, String>
{
    // Constants
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public final String BASE_WEB_FOLDER = "http://rahimasif.net/apps/games/taboo/";
    public final String PAGE = "mark_played_word.php";
    public final String BASE_URL = BASE_WEB_FOLDER + PAGE;

    // Private member variables
    ProgressDialog pdLoading;
    HttpURLConnection connection;
    URL url = null;
    AppCompatActivity activity;

    public AsyncMarkPlayedWords(AppCompatActivity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute()               // Runs in UI Thread
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(TabooCard... params)   // Runs on separate thread
    {
        String result = "";
        int successMarked;

        successMarked = 0;
        TabooCard t = params[0];

        // Form the URL
        try
        {
            String urlString = "";
            urlString = BASE_URL;
            urlString += "?";
            urlString += ("userID=" + Integer.toString(Settings.getUserID()));
            urlString += "&";
            urlString += ("wordID=" + t.getId());
            urlString += "&";
            urlString += ("tableName=" + Settings.getTableName());

            url = new URL(urlString);
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Malformed URL Exception";
        }

        // Connect to the URL
        try
        {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Make the connection
            connection.connect();
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "IO Exception on Connect";
        }

        // Receive the response
        try
        {
            int response_code = connection.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK)
            {
                successMarked++;
            }
            else
            {
                // Do something
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "IO Exception during connecting";
        }
        finally
        {
            connection.disconnect();
            try
            {
                Thread.sleep(50);
            }
            catch(Exception ex)
            {

            }
        }

        result = Integer.toString(successMarked);

        return result;
    }

    @Override
    protected void onPostExecute(String result)     // Runs on UI Thread
    {
        try
        {

        }
        catch(Exception ex)
        {

        }
    }
}
