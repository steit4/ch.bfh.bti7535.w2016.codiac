package classifiers;

import interfaces.Classifier;
import main.Document;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.Random;

// We used the Naive Bayes implementation of the Weka data mining library

//This is a wrapper class for the Weka Naive Bayes implementation

public class NaiveBayes implements Classifier, Serializable
{

    private FilteredClassifier filteredBayes = new FilteredClassifier();
    private Instances data = null;

    /**
     * configures the classifier and the filters for document pre-processing
     */
    public void setup()
    {
        //Load document data from ARFF file (WEKA specific file format)
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("arff/IMDB_unedited.arff"));
            ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
            this.data = arff.getData();
            this.data.setClassIndex(data.numAttributes() - 1);
            reader.close();
        } catch(FileNotFoundException e)
        {
            e.printStackTrace();
        } catch(IOException e)
        {
            e.printStackTrace();
        }

        //Configure vector filter
        StringToWordVector vectorFilter = new StringToWordVector();
        vectorFilter.setWordsToKeep(100000);
        vectorFilter.setDoNotOperateOnPerClassBasis(true);
        vectorFilter.setLowerCaseTokens(false);

        try
        {
            vectorFilter.setInputFormat(this.data);
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        //Configure tokenizer
        NGramTokenizer tokenizer = new NGramTokenizer();
        tokenizer.setNGramMinSize(1);
        tokenizer.setNGramMaxSize(3);

        vectorFilter.setTokenizer(tokenizer);

        //configure feature selection filter
        AttributeSelection as = new AttributeSelection();
        Ranker ranker = new Ranker();
        //The rankers selects the 1000 most significant features
        ranker.setNumToSelect(1000);
        as.setEvaluator(new InfoGainAttributeEval());
        as.setSearch(ranker);


        //Combine filters
        MultiFilter multiFilter = new MultiFilter();

        Filter[] filters = new Filter[2];
        filters[0] = vectorFilter;
        filters[1] = as;

        multiFilter.setFilters(filters);

        this.filteredBayes.setClassifier(new weka.classifiers.bayes.NaiveBayes());
        this.filteredBayes.setFilter(multiFilter);
        try
        {
            multiFilter.setInputFormat(this.data);
        } catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     *     Construct the classifier (for example needed for serialization)
     */
    public void build()
    {
        try
        {
            this.filteredBayes.buildClassifier(this.data);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *  10-fold-cross-validates the classifier
     *  @param numOfFolds
     *  @return Cross-validation result as string
     */
    public String crossValidate(int numOfFolds)
    {

        Evaluation evaluation = null;

        try
        {
            evaluation = new Evaluation(this.data);
            // 10-fold-cross-validation
            evaluation.crossValidateModel(this.filteredBayes, this.data, numOfFolds, new Random(1));

        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return evaluation.toSummaryString();
    }

    /**
     *     Classifies a single document instance
     *     @return document class (pos or neg)
     */
    public String classify(Document doc)
    {
        try
        {
            return doc.getInstance().classAttribute().value((int) this.filteredBayes.classifyInstance(doc.getInstance()));
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Serializes the classifier and writes to disk
     * @param path
     */
    public void serialize(String path)
    {
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reads the serialized classifier from disk
     * @param path
     * @return classifier object
     */
    public static NaiveBayes deserialize(String path)
    {
        try
        {
            return (NaiveBayes) new ObjectInputStream(new FileInputStream(path)).readObject();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Weka classifier object
     */

    public weka.classifiers.Classifier getClassifier()
    {
        return this.filteredBayes;
    }


}
