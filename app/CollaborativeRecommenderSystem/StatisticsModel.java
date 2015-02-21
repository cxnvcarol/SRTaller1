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

    public StatisticsModel(ResultModel[] results)
    {
        maxDistance=-1;
        minDistance=100000;
        averageDistance=0;
        for (ResultModel result : results) {
            double dist = result.distance;
            averageDistance += dist;
            if (dist > maxDistance)
                maxDistance = dist;
            else if (dist < minDistance)
                minDistance = dist;
        }
        if(results.length>0)averageDistance/=results.length;

        for (ResultModel result : results) {
            double dist = result.distance;
            double partial = dist > averageDistance ? dist - averageDistance : averageDistance - dist;
            variance += partial * partial;
        }
        standardDeviation=Math.sqrt(variance);
    }
}
