package CollaborativeRecommenderSystem;

/**
 * Created by carol on 19/02/15.
 */
public class StatisticsModel {

    public double averageDistance;
    public double maxDistance;
    public double minDistance;
    public double standardDeviation;
    public double variance;
    public int resultsLength;

    public StatisticsModel(ResultModel[] results)
    {
        resultsLength =results.length;
        maxDistance=-1;
        minDistance=100000;
        averageDistance=0;
        int totEv=0;
        for (ResultModel result : results) {

            if(result==null)
                break;
            totEv++;
            double dist = result.distance;
            averageDistance += dist;
            if (dist > maxDistance)
                maxDistance = dist;
            else if (dist < minDistance)
                minDistance = dist;
        }
        resultsLength=totEv;
        if(results.length>0)averageDistance=averageDistance/resultsLength;

        for (ResultModel result : results) {
            if(result==null)
                break;
            double dist = result.distance;
            double partial = dist > averageDistance ? dist - averageDistance : averageDistance - dist;
            variance += partial * partial;
        }
        standardDeviation=Math.sqrt(variance);
    }
}
