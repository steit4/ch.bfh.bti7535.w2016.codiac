package main;

import lib.DB;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Document
{

    private static final int numOfDocsPerCat = 1000;
    private static final int k = 10;

    private String name;
    private int cvt;
    private String category;
    private String text;
    private Instance instance;

    private Document(String name, int cvt, String category, String text, Instance instance) throws SQLException
    {
        this.name = name;
        this.cvt = cvt;
        this.category = category;
        this.text = text;
        this.instance = instance;
    }


    /**
     * Gets a single document from the database
     * @param name Unique name of the document according to the movie review data set
     * @return A single document
     */
    public static Document getDocument(String name)
    {
        String queryString = "SELECT Name, CVT, Class, Text FROM Reviews WHERE Name =" + name;
        try
        {
            InstanceQuery query = new InstanceQuery();
            query.setQuery(queryString);
            query.setUsername("dataScience");
            query.setPassword("dataScience");
            Instances result = query.retrieveInstances();
            result.setClassIndex(2);

            if(!result.isEmpty())
            {
                return new Document(result.instance(0).stringValue(0), (int) result.instance(0).value(1), result.instance(0).stringValue(2), result.instance(0).stringValue(3), result.instance(0));
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Get test fold from the database
     *
     * @param fold Number of fold (1-10)
     * @return Arraylist of test documents of given fold
     */
    public static ArrayList<Document> getTestFold(int fold)
    {
        int fromCVT = (fold - 1) * numOfDocsPerCat / k;
        int toCVT = fromCVT + numOfDocsPerCat / k - 1;

        String queryString = "SELECT Name, CVT, Class, Text FROM Reviews WHERE CVT >= " + fromCVT + " AND CVT <= " + toCVT + " ORDER BY CVT";

        try
        {
            InstanceQuery query = new InstanceQuery();
            query.setQuery(queryString);
            query.setUsername("dataScience");
            query.setPassword("dataScience");
            Instances result = query.retrieveInstances();
            result.setClassIndex(2);

            ArrayList<Document> docs = new ArrayList<>();

            for(int i = 0; i < result.size(); i++)
            {
                docs.add(new Document(result.instance(i).stringValue(0), (int) result.instance(i).value(1), result.instance(i).stringValue(2), result.instance(i).stringValue(3), result.instance(i)));
            }

            return docs;

        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Checks the classification results against the gold standard
     *
     * @param classificationResults A Hashmap consisting of pairs <String [name of review], String [category]>.
     *                              category is either "pos" or "neg".
     * @return Floating Point value between 0 and 1 denoting the accuracy of the results
     * @throws SQLException
     */
    public static float checkAccuracy(HashMap<String, String> classificationResults) throws SQLException
    {
        int numOfResults = classificationResults.size();
        int correctResults = 0;

        DB.connect();

        for(String nameOfReview : classificationResults.keySet())
        {
            String query = "SELECT Class FROM Reviews WHERE Name = ?";
            ResultSet result = DB.doQuery(query, nameOfReview);
            if(result.isBeforeFirst())
            {
                result.next();
                if(result.getString("Class").equals(classificationResults.get(nameOfReview)))
                {
                    correctResults++;
                }
            }
        }
        return (float) correctResults / numOfResults;
    }


    public String getName()
    {
        return this.name;
    }

    public int getCVT()
    {
        return this.cvt;
    }

    public String getCategory()
    {
        return this.category;
    }

    public String getText()
    {
        return this.text;
    }

    public Instance getInstance()
    {
        return this.instance;
    }


}
