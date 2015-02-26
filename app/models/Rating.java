package models;

import play.db.ebean.Model;

import javax.persistence.Entity;

/**
 * Created by carol on 19/02/15.
 */
@Entity
public class Rating extends Model{

    public double rating;
    public Movie movie;
    public long timestamp;
    public static Finder<Long,Rating> find = new Finder<Long,Rating>(
            Long.class, Rating.class
    );


    public void setRating(double rating) {
        this.rating = rating;
    }
    public Rating(long movieP,double newRating)
    {
        movie=Movie.getMovie(movieP);
        if(movie==null)
        {
            System.err.println("movie null.. id: "+movieP);
        }
        else movie.addRating(newRating);
        rating=newRating;
    }
    public void setTimestamp(long ts)
    {
        timestamp=ts;
    }
}
