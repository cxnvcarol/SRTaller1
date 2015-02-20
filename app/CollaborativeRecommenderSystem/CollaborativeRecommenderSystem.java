package CollaborativeRecommenderSystem;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;
import play.Play;

import java.io.*;
import java.util.List;

/**
 * Created by carol on 19/02/15.
 */
public class CollaborativeRecommenderSystem implements RecommenderSystem{

    private static final String RATINGS_PATH="data/ratings.csv";
    public static final String RATINGS_TRAINING_PATH="data/ratingsTraining.csv";
    private static final String RATINGS_TEST_PATH="data/ratingsTest.csv";
    private static final String USERS_PATH="data/users.csv";
    private static final String MOVIES_PATH="data/movies.csv";

    private boolean itemBased;
    private int similarityMethod;
    private double trainingPercent;
    private int neighborsQuantity;
    private boolean evaluationUpdated;
    private boolean modelUpdated;

    private ResultModel[] resultsModelList;
    private StatisticsModel statisticsModel;


    private DataModel dataModel;
    
    public CollaborativeRecommenderSystem()
    {
        setDefaultParameters();
        reloadData();
        updateModelEvaluation();
    }

    private void setDefaultParameters() {
        itemBased=false;
        trainingPercent=0.1;
        neighborsQuantity =20;
        similarityMethod=SIMILARITY_METHOD_JACCARD;
        evaluationUpdated =false;
    }

    @Override
    public StatisticsModel evaluateModel() {
        if(!evaluationUpdated)
        {
            updateModelEvaluation();
        }
        return statisticsModel;
    }

    private void updateModelEvaluation() {
        updateModel();
        //TODO recalculate statistics and result (1)
        evaluationUpdated =true;
    }
    private void updateModel() {
        File tt=Play.application().getFile(RATINGS_TRAINING_PATH);
        try {
            dataModel= new FileDataModel(tt);
            modelUpdated=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ResultModel[] evaluateModelDetail() {
        if(!evaluationUpdated)
        {
            updateModelEvaluation();
        }
        return resultsModelList;
    }

    @Override
    public Recommendation[] getUserRecommendation(int userId, int numMax) {
        if(!modelUpdated)
        {
            updateModel();
        }
        try {
            if(loadUser(userId)!=null) {

                ItemSimilarity similarity = null;
                switch (similarityMethod) {
                    case SIMILARITY_METHOD_JACCARD:
                        similarity = new TanimotoCoefficientSimilarity(dataModel);
                        break;
                    case SIMILARITY_METHOD_COSINE:
                        similarity = new UncenteredCosineSimilarity(dataModel);
                        break;
                    case SIMILARITY_METHOD_PEARSON:
                        similarity = new PearsonCorrelationSimilarity(dataModel);
                        break;
                }
                Recommender recommender=null;
                if (itemBased) {
                    recommender = new GenericItemBasedRecommender(dataModel, similarity);
                } else {
                    UserSimilarity similarityU= (UserSimilarity) similarity;
                    UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighborsQuantity, similarityU, dataModel);
                    recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarityU);
                }
                List<RecommendedItem> recommendations = recommender.recommend(userId, numMax);
                Recommendation[] returned=new Recommendation[recommendations.size()];

                for (int i = 0; i < recommendations.size(); i++) {
                    RecommendedItem recommendation=recommendations.get(i);
                    returned[i]=new Recommendation((int)recommendation.getItemID(),recommendation.getValue());
                }
                return returned;
            }
            return findBetterMovies(numMax);
        } catch (TasteException e) {
            e.printStackTrace();
            return new Recommendation[0];
        }
    }

    private Recommendation[] findBetterMovies(int nmovies) {
        try {
            Movie[] top=Movie.getOrderedByRating();
            int nmovs=(nmovies < top.length ? nmovies : top.length);
            Recommendation[] result=new Recommendation[nmovs];
            for (int i = 0; i < nmovs; i++) {
                result[i]=new Recommendation(top[i].getId(),top[i].getAverageRating());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new Recommendation[0];
        }
    }


    @Override
    public User loadUser(int userId) {
        return User.find(userId);
    }

    @Override
    public void rate(int userId, int itemId, double rating) {
        //TODO set new temporary rating (5)
    }

    @Override
    public User registerUser() {
        return new User(User.lastUser+1);
    }

    @Override
    public void setItemBased(boolean itemBasedBool) {
        if(itemBasedBool!=itemBased)
        {
            itemBased=itemBasedBool;
            updateModel();
        }
    }

    @Override
    public void setNeighborsQuantity(int neighborsQuantityP) {
        if(neighborsQuantity !=neighborsQuantityP)
        {
            neighborsQuantity =neighborsQuantityP;
            updateModel();
        }
    }

    @Override
    public void setRecommendationMethod(int recommendationMethod) {
        if(similarityMethod!=recommendationMethod)
        {
            similarityMethod=recommendationMethod;
            updateModel();
        }
    }

    @Override
    public void setTrainingPercent(double trainingPercentP) {
        if(trainingPercent!=trainingPercentP)
        {
            trainingPercent=trainingPercentP;
            reloadData();
            evaluationUpdated=false;
        }
    }

    private void reloadData() {
        try {
            File src = Play.application().getFile(RATINGS_PATH);
            int totalLines = countLines(src);
            int lnTraining= (int) (totalLines*trainingPercent);

            BufferedReader br=new BufferedReader(new FileReader(src));
            String parentPath=src.getParentFile().getAbsolutePath();

            File trainingFile=new File(parentPath+"/ratingsTrainig.csv");
            trainingFile.createNewFile();
            FileWriter fw = new FileWriter(trainingFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < lnTraining; i++) {
                bw.write(br.readLine()+"\n");
            }
            bw.close();

            File testFile=new File(parentPath+"/ratingsTest.csv");
            testFile.createNewFile();
            fw = new FileWriter(testFile.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            for (int i = lnTraining; i <totalLines ; i++) {
                bw.write(br.readLine()+"\n");
            }
            bw.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        updateModel();

    }

    public static int countLines(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    public void testMahout() throws IOException, TasteException {

        long start=System.currentTimeMillis();
        setTrainingPercent(0.05);
        reloadData();
        long diff=System.currentTimeMillis()-start;
        System.out.println("Data separated...  Ellapsed in "+diff+"ms.");

        //http://www.warski.org/blog/2013/10/creating-an-on-line-recommender-system-with-apache-mahout/

        File tt=Play.application().getFile("data/test.csv");
        DataModel model= new FileDataModel(tt);
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        List<RecommendedItem> recommendations = recommender.recommend(2, 3);
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }
}