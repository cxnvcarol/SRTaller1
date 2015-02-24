package CollaborativeRecommenderSystem;

import play.Play;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by carol on 19/02/15.
 */
public class Movie {
    private long id;

    public double getAverageRating() {
        return averageRating;
    }

    private double averageRating;
    private int numRatings;
    public String getName() {
        return name;
    }

    public String getCategories() {
        return categories;
    }

    private String name;
    private String categories;
    private static Movie[] allMovies;

    public long getId()
    {
        return id;
    }
    public Movie(long idp) throws Exception {
        numRatings=0;
        averageRating=0;
        Movie[] allm=getAll();
        for (Movie m:allm)
        {
            if(m.getId()==idp)
            {
                id=idp;
                name=m.getName();
                categories=m.getCategories();
                return;
            }
        }
        throw new Exception("Movie not found");
    }
    public Movie(int idp,String nameP, String categoriesP){
        id=idp;
        name=nameP;
        categories=categoriesP;
    }

    public static Movie find(long movieId) {
        try {
            return new Movie(movieId);
        } catch (Exception e) {
            return null;
        }
    }

    public static Movie[] getOrderedByRating() throws IOException {
        Movie[] ordered=Arrays.copyOf(getAll(),getAll().length);
        Arrays.sort(ordered, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie, Movie t1) {
                return movie.averageRating>t1.averageRating?1:movie.averageRating<t1.averageRating?-1:0;
            }
        });
        return ordered;
    }

    public static Movie[] getOrderedByPopularity() throws IOException {
        Movie[] ordered=Arrays.copyOf(getAll(),getAll().length);
        Arrays.sort(ordered, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie, Movie t1) {
                return movie.numRatings>t1.numRatings?1:movie.numRatings<t1.numRatings?-1:0;
            }
        });
        return ordered;
    }
    public static Movie[] getAll() throws IOException {
        if(allMovies==null||allMovies.length==0)
        {
            System.out.print("Finding all movies...");
            ArrayList<Movie> movies=new ArrayList<Movie>();

            BufferedReader br=new BufferedReader(new FileReader(Play.application().getFile(CollaborativeRecommenderSystem.MOVIES_PATH)));
            String ln;
            String[] splited;
            while((ln=br.readLine())!=null&&ln.length()>0)
            {
                splited=ln.split(",");
                System.out.println("Lenght: "+splited.length);
                try
                {
                	//TODO esto se debe retirar, fireproof
                	if(splited.length==3)
                    movies.add(new Movie(Integer.parseInt(splited[0]),splited[1],splited[2]));
                }
                catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }
            }
            allMovies=movies.toArray(new Movie[movies.size()]);
            br.close();
        }
        return allMovies;
    }

    public void addRating(double rr) {
        averageRating*=numRatings;
        numRatings++;
        averageRating+=rr;
        averageRating/=numRatings;
    }
}
