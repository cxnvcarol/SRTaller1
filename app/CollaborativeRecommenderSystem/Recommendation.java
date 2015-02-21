package CollaborativeRecommenderSystem;

/**
 * Created by carol on 19/02/15.
 */
public class Recommendation {
    public Recommendation(long movieId,double r)
    {
        movie=Movie.find(movieId);
        setPredictedRating(r);
    }
    public double getPredictedRating() {
        return predictedRating;
    }

    public void setPredictedRating(double predictedRating) {
        this.predictedRating = predictedRating;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    private double predictedRating;
    private Movie movie;
}
