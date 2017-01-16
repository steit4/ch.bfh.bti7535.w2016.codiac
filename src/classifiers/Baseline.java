package classifiers;

import java.util.ArrayList;
import java.util.List;

import interfaces.Classifier;
import main.Dictionary;
import main.Document;

import java.util.Arrays;
import java.util.stream.Collectors;


public class Baseline implements Classifier
{
    private List<String> tokensList = new ArrayList<>();
    private int currentPolarity = 0;
    private float currentRate = 0.0F;
    private String choice;
    private Dictionary dict;


    /**
     * This is our baseline algorithm it predicts the class of a document according to information obtained from a dictionary
     *
     * @param choice (polarity or rating)
     * @param dict
     */
    public Baseline(String choice, Dictionary dict)
    {
        this.choice = choice;
        this.dict = dict;
    }

    /**
     * the method splits the words of the document to tokens and adds them into ArrayList
     *
     * @param doc
     * @return
     */
    public ArrayList<String> tokenizeDoc(Document doc)
    {
        tokensList.clear();
        String docText = doc.getText();
        String[] words = docText.split("\\s");
        tokensList.addAll(Arrays.asList(words));

        List<String> minwordsList = tokensList.stream().filter(w -> w.length() > 1).collect(Collectors.toList());
        ArrayList<String> retwords = new ArrayList<String>(minwordsList);

        return retwords;
    }

    /**
     * the method compares the tokens with words from the dictionary and defines the rating of the word
     *
     * @param docName
     * @param tokensList
     * @return
     */
    public float analyzeDocRate(String docName, ArrayList<String> tokensList)
    {

        currentRate = 0.0F;
        for(String word : tokensList)
        {
            if(this.dict.isInDict(word))
            {
                currentRate = currentRate + this.dict.getRating(word);
            }
        }
        return currentRate;
    }

    /**
     * the method defines polarity of the document and accumulates the results in HashMap
     *
     * @param docName
     * @param tokensList
     * @param
     * @return
     */
    public int analyzeDocPolarity(String docName, ArrayList<String> tokensList)
    {

        currentPolarity = 0;

        for(String word : tokensList)
        {
            if(this.dict.isInDict(word))
            {
                currentPolarity = currentPolarity + this.dict.getPolarity(word);
            }
        }

        return currentPolarity;
    }

    /**
     * Classifies a single document
     *
     * @param doc
     * @return document class (pos or neg)
     */
    @Override
    public String classify(Document doc)
    {
        ArrayList<String> tokens = tokenizeDoc(doc);
        int polarity;
        float rate;
        String classification = "";

        if(choice == "polarity")
        {
            polarity = analyzeDocPolarity(doc.getName(), tokens);
            if(polarity > 0)
                classification = "pos";
            else if(polarity < 0) classification = "neg";
        }
        else if(choice == "rating")
        {
            rate = analyzeDocRate(doc.getName(), tokens);
            if(rate > 0)
                classification = "pos";
            else if(rate < 0) classification = "neg";
        }
        return classification;
    }
}
