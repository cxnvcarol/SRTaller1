package models;

/**
 * Created by carol on 19/02/15.
 */
public class Recommendation {


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

}
