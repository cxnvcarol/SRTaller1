package CollaborativeRecommenderSystem;

import play.Play;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by carol on 19/02/15.
 */
public class User {
    private int id;
    private ArrayList<Rating> ratings;

    private static User[] allUsers;

    public static String USERS_PATH="data/users.csv";

    public static int lastUser=0;

    public User(int idp)
    {
        id=idp;
        ratings=new ArrayList<Rating>();
    }
    public User(ArrayList<Rating> ratparam)
    {
        ratings=ratparam;
    }

    public static User[] getAll() throws IOException {
        if(allUsers==null||allUsers.length==0)
        {
            System.out.print("Finding all movies...");
            allUsers=new User[CollaborativeRecommenderSystem.countLines(Play.application().getFile(USERS_PATH))];
            lastUser=allUsers.length;
            for (int i = 0; i < lastUser; i++) {
                allUsers[i]=new User(i+1);
            }
            BufferedReader br=new BufferedReader(new FileReader(Play.application().getFile(
                    CollaborativeRecommenderSystem.RATINGS_TRAINING_PATH)));
            String lin="";
            while((lin=br.readLine())!=null&&lin.length()>0)
            {
                String[] spl=lin.split(",");
                //could be more efficient?
                int movieid=Integer.parseInt(spl[1]);
                double rr=Double.parseDouble(spl[2]);
                User.find(Integer.parseInt(spl[0])).addRating(movieid,rr);
                Movie.find(movieid).addRating(rr);
            }
            br.close();
        }
        return allUsers;

    }
    public static User find(int userId) {
        User[] allm= new User[0];
        try {
            allm = getAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (User m:allm)
        {
            if(m.getId()==userId)
            {
                return m;
            }
        }
        return null;
    }

    public int getId()
    {
        return id;
    }
    public ArrayList<Rating> getRatings()
    {
        return ratings;
    }


    /**
     * @precondition movieId exists
     * @param movieId
     * @param rating
     */
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
        ratings.add(new Rating(movieId,rating));
    }

}
