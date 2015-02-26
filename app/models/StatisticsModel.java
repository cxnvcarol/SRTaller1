package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by carol on 19/02/15.
 */
@Entity
public class StatisticsModel extends Model{

    public double averageDistance;
    public double maxDistance;
    public double minDistance;
    public double standardDeviation;
    public double variance;
    public int resultsLength;
    public String description;
    public static Finder<Long,StatisticsModel> find = new Finder<Long,StatisticsModel>(
            Long.class, StatisticsModel.class
    );

    public StatisticsModel(ResultModel[] results, String desc)
    {
        description=desc;
        resultsLength =results.length;
        averageDistance=0;
        if(results.length==0)
        {
            maxDistance=0;
            minDistance=0;
            variance=0;
            standardDeviation=0;
            return;
        }

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
        variance=variance/resultsLength;
        standardDeviation=Math.sqrt(variance);
    }

    public static StatisticsModel findDescription(String descriptionP) {
        List<StatisticsModel> allagain = StatisticsModel.find.all();
        for (StatisticsModel sm:allagain)
        {
            if(sm.description.equals(descriptionP))
                return sm;
        }
        return null;
    }
}
