package CollaborativeRecommenderSystem;

/**
 * Created by carol on 19/02/15.
 */
public class CollaborativeRecommenderSystem implements RecommenderSystem{

    private boolean itemBased;
    private int similarityMethod;
    private double trainingPercent;
    private int neighborsQuantity;
    private boolean evaluationUpdated;

    private ResultModel[] resultsModelList;
    private StatisticsModel statisticsModel;

    
    public CollaborativeRecommenderSystem()
    {
        setDefaultParameters();
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
        //TODO update model and recalculate statistics and result

        evaluationUpdated =true;
    }
    private void updateModel() {
        //TODO update model and recalculate statistics and result

        evaluationUpdated =false;
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
        return new Recommendation[0];
    }

    @Override
    public User loadUser(int userId) {
        return null;
    }

    @Override
    public void rate(int userId, int itemId, double rating) {

    }

    @Override
    public User registerUser() {
        return null;
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
        //TODO Separate file in 2
    }
}