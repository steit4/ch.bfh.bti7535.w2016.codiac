package tester;

import classifiers.Baseline;
import main.Document;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Alain on 30.11.2016.
 */
public class Tester
{

    //Cross-validates the baseline algorithm
    public static double testBaseline(Baseline baseline, int numOfFolds)
    {
        HashMap<String, String> classifications = new HashMap<>();

        double accuracySum = 0;

        for(int i = 1; i <= numOfFolds; i++) {
            for (Document doc : Document.getTestFold(i)) {
                classifications.put(doc.getName(), baseline.classify(doc));
            }
            try {
                accuracySum += Document.checkAccuracy(classifications);
                classifications.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
            return accuracySum / numOfFolds;
    }




}

