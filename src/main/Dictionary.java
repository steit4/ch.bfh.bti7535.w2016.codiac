package main;

import lib.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * We used the VADER sentiment lexicon for our dictionary implementation
 * The original dictionary file can be found at: https://github.com/cjhutto/vaderSentiment/blob/master/vaderSentiment/vader_lexicon.txt
 */

public class Dictionary {

    private HashMap<String,Float> ratings = new HashMap<String, Float>();

    public Dictionary() throws SQLException
    {
        DB.connect();
        String query = "SELECT Word, Rating FROM Dict_VADER";
        ResultSet results = DB.doQuery(query);

        while (results.next())
        {
            this.ratings.put(results.getNString("Word"), results.getFloat("Rating"));
        }
    }

    /**
     *
     * @param word
     * @return rating of the word (ranges from -4 to 4)
     */
    public float getRating(String word)
    {
        if (this.ratings.containsKey(word))
        {
            return ratings.get(word);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     *
     * @param word
     * @return polarity of the word (-1 or 1)
     */
    public int getPolarity(String word)
    {
        float rating = this.getRating(word);
        if (rating < 0.0)
            return -1;
        else if (rating > 0.0)
            return 1;
        else
            return 0;
    }

    /**
     *
     * @param word
     * @return is the word present in our dictionary
     */
    public boolean isInDict(String word)
    {
        return this.ratings.containsKey(word);
    }
}
