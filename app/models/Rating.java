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

    public void setRating(double rating) {
        this.rating = rating;
    }
    public Rating(long movieP,double newRating)
    {
        movie=Movie.find(movieP);
        rating=newRating;
    }
}
