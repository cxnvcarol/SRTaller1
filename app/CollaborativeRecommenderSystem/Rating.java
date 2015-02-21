package CollaborativeRecommenderSystem;

/**
 * Created by carol on 19/02/15.
 */
public class Rating {
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    private double rating;

    public Movie getMovie() {
        return movie;
    }

    private Movie movie;
    public Rating(long movieP,double newRating)
    {
        movie=Movie.find(movieP);
        rating=newRating;
    }
}
