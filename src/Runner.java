import classifiers.Baseline;
import classifiers.NaiveBayes;
import main.Dictionary;
import tester.Tester;

import java.sql.SQLException;

public class Runner
{

    public static void main(String args[])
    {

        try
        {
            //Baseline rating test
            Baseline baseline = new Baseline("rating", new Dictionary());
            System.out.println("Baseline accuracy (based on rating): " + Tester.testBaseline(baseline, 10));

            //Baseline polarity test
            baseline = new Baseline("polarity", new Dictionary());
            System.out.println("Baseline accuracy (based on polarity only): " + Tester.testBaseline(baseline, 10));
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        //NaiveBayes test
        NaiveBayes naiveBayes = new NaiveBayes();

        naiveBayes.setup();

        System.out.println("----------------------------------------------------------");
        System.out.println("Naive Bayes 10-fold-cross-validation result:");
        System.out.println(naiveBayes.crossValidate(10));

    }
}