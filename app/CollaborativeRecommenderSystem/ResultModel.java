package CollaborativeRecommenderSystem;

/**
 * Created by carol on 19/02/15.
 */
public class ResultModel {
    public double distance;
    public double estimatedRating;
    public double realRating;
    public long itemId;
    public long userId;
    public ResultModel(long uid,long iid,double realR,double estimatedR)
    {
        userId=uid;
        itemId=iid;
        realRating=realR;
        estimatedRating=estimatedR;
        distance=realRating-estimatedR;
        if(distance<0) distance*=-1;
    }
}
