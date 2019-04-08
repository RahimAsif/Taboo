package net.rahimasif.apps.Taboo;

/**
 * Created by MRahim on 8/29/2017.
 */

public class Utility
{
    public static String toTitleCase(String input)
    {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray())
        {
            if (Character.isSpaceChar(c))
            {
                nextTitleCase = true;
            }
            else if (nextTitleCase)
            {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static Time getTimeDifference(Time t1, Time t2)
    {
        Time t;

        t = new Time(t1.getTotalMilliseconds() - t2.getTotalMilliseconds());

        return t;
    }
}
