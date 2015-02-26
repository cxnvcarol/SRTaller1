package models;

/**
 * Created by carol on 19/02/15.
 */
public class Rating {
    public double rating;
    public Movie movie;

    public void setRating(double rating) {
        this.rating = rating;
    }
    public Rating(long movieP,double newRating)
    {
        movie=Movie.find(movieP);
        rating=newRating;
    }
}
