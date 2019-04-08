package net.rahimasif.apps.Taboo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by RahimAsif on 8/20/17.
 */

public class TabooCard
{
    private int id;
    private String wordOrPhrase;
    private List<String> forbiddenWords;

    public int getId()
    {
        return this.id;
    }

    public String getWordOrPhrase()
    {
        return this.wordOrPhrase;
    }

    public List<String> getForbiddenWords()
    {
        return this.forbiddenWords;
    }

    public TabooCard(int id, String wordOrPhrase, String forbiddenWords)
    {
        // Set the id
        this.id = id;
        // Set the word/phrase
        this.wordOrPhrase = wordOrPhrase.trim();
        // Set the forbidden words
        this.forbiddenWords = new ArrayList<String>();
        String[] items = forbiddenWords.split(Pattern.quote("|"));
        for (String item: items)
        {
            this.forbiddenWords.add(Utility.toTitleCase(item.trim().toLowerCase()));
        }
    }

    @Override
    public String toString()
    {
        String str = "";

        // ID
        str += "#";
        str += Integer.toString(id);
        // Word
        str += ": ";
        str += wordOrPhrase.toUpperCase();
        // Forbidden Words
        str += " => ";
        str += forbiddenWords;

        return str;
    }
}