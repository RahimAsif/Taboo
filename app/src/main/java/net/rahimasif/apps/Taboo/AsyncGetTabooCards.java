package net.rahimasif.apps.Taboo;

/**
 * Created by RahimAsif on 8/29/17.
 */
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.support.v7.app.AppCompatActivity;

public class AsyncGetTabooCards extends AsyncTask<Void, Void, String>
{
    // Constants
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public final String BASE_WEB_FOLDER = "http://rahimasif.net/apps/games/taboo/";
    public final String PAGE = "get_words.php";
    public final String BASE_URL = BASE_WEB_FOLDER + PAGE;

    // Private member variables
    ProgressDialog pdLoading;
    HttpURLConnection connection;
    URL url = null;
    AppCompatActivity activity;

    public AsyncGetTabooCards(AppCompatActivity activity)
    {
        this.activity = activity;
        pdLoading = new ProgressDialog(this.activity);
    }

    @Override
    protected void onPreExecute()               // Runs in UI Thread
    {
        super.onPreExecute();

        // Display a progress dialog
        pdLoading.setMessage("Getting Taboo Words...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    @Override
    protected String doInBackground(Void... params)   // Runs on separate thread
    {
        // Set the URL
        try
        {
            String urlString = "";
            urlString = BASE_URL;
            urlString += "?";
            urlString += ("userID=" + Integer.toString(Settings.getUserID()));
            urlString += "&";
            urlString += ("numItems=" + Integer.toString(Settings.getMaxWordsPerPlayer()));
            urlString += "&";
            urlString += ("tableName=" + Settings.getTableName());
            if(Settings.getAllowDirtyWords() == false)
            {
                urlString += "&";
                urlString += ("allowDirty=0");
            }

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
            connection = (HttpURLConnection)url.openConnection();
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

                // Read data sent from server
                InputStream input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
                // Pass data to onPostExecute method
                return(result.toString());
            }
            else
            {
                return("unsuccessful");
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
        }
    }

    @Override
    protected void onPostExecute(String result)     // Runs on UI Thread
    {
        // Dismiss the dialog
        pdLoading.dismiss();

        try
        {
            JSONObject taboo = new JSONObject(result);
            JSONArray tabooWords = taboo.getJSONArray("items");
            Game.clearTabooCardList();
            for(int i=0; i<tabooWords.length(); i++)
            {
                JSONObject tabooItem = tabooWords.getJSONObject(i).getJSONObject("tabooItem");
                String id = tabooItem.getString("id");
                String word_phrase = tabooItem.getString("word_phrase");
                String forbidden_words = tabooItem.getString("forbidden_words");
                String date_created = tabooItem.getString("date_created");

                TabooCard t = new TabooCard(Integer.valueOf(id), word_phrase.trim(), forbidden_words.trim());
                Game.addTabooCard(t);
                Log.i("API", t.toString());
            }

            Toast.makeText(this.activity, "Successfully retrieved " + Integer.toString(tabooWords.length()) + " words" , Toast.LENGTH_SHORT).show();
            ((MainActivity)this.activity).doGotNewCards();
        }
        catch(Exception ex)
        {

        }

    }
}