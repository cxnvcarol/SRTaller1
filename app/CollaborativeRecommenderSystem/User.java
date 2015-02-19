package CollaborativeRecommenderSystem;

import java.util.ArrayList;

/**
 * Created by carol on 19/02/15.
 */
public class User {
    private int id;
    private ArrayList<Rating> ratings;

    public User()
    {
        ratings=new ArrayList<Rating>();
    }

    public int getId()
    {
        return id;
    }
    public ArrayList<Rating> getRatings()
    {
        return ratings;
    }
    public void addRating(int movieId, double rating)
    {
        Rating rat;
        for (int i = 0; i < ratings.size(); i++) {
            rat=ratings.get(i);
            if(rat.getMovie().getId()==movieId)
            {
                rat.setRating(rating);
                return;
            }
        }

        Movie toAdd=new Movie();//TODO find existing movie, else do nothing
        ratings.add(new Rating(toAdd,rating));
    }

}
