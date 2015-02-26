package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Created by carol on 19/02/15.
 */
@Entity
public class Recommendation extends Model{


    public double predictedRating;
    public Movie movie;


    public Recommendation(long movieId,double r)
    {
        movie=Movie.getMovie(movieId);
        setPredictedRating(r);
    }
    public void setPredictedRating(double predictedRating) {
        this.predictedRating = predictedRating;
    }
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String toString(){
    	return "Te recomendamos: "+movie.name+",  rating predecido: "+new BigDecimal(predictedRating ).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

}
