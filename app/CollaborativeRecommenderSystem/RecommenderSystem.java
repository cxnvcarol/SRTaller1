package CollaborativeRecommenderSystem;

/**
 * Created by carol on 19/02/15.
 */
public interface RecommenderSystem {


    public final static int SIMILARITY_METHOD_JACCARD=1;
    public final static int SIMILARITY_METHOD_COSINE=2;
    public final static int SIMILARITY_METHOD_PEARSON=3;


    public StatisticsModel evaluateModel();
    public ResultModel[] evaluateModelDetail();
    public Recommendation[] getUserRecommendation(int userId, int numMax);
    public User loadUser(int userId);
    public void rate(int userId, int itemId, double rating);
    public User registerUser();
    public void setItemBased(boolean itemBasedBool);
    public void setNeighborsQuantity(int neighborsQuantity);
    public void setRecommendationMethod(int similarityMethod);
    public void setTrainingPercent(double trainingPercent);

}