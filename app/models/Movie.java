package models;

import CollaborativeRecommenderSystem.CollaborativeRecommenderSystem;
import play.Play;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;



import javax.persistence.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by carol on 19/02/15.
 */
@Entity
public class Movie extends Model{

    @Id
    public long id;
    public double averageRating;
    public int numRatings;
    public String name;
    public String categories;
    private static Movie[] allMovies;

    public static Finder<Long,Movie> find = new Finder<Long,Movie>(
            Long.class, Movie.class
    );

    @Override
    public String toString() {
        return name==null?"no name":name;
    }

    public Movie(long idp) throws Exception {
        numRatings=0;
        averageRating=0;
        Movie[] allm=getAll();
        for (Movie m:allm)
        {
            if(m.id==idp)
            {
                id=idp;
                name=m.name;
                categories=m.categories;
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

    public static Movie getMovie(long movieId) {
        try {
            Movie[] allm = getAll();
            for (Movie m : allm) {
                if (m.id == movieId) {
                    return m;
                }
            }
            return null;
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
            /*//TODO
            List<Movie> varv = Movie.find.all();
            if(varv.size()>0)
            {
                allMovies=varv.toArray(new Movie[varv.size()]);
                for (Movie m1:allMovies)
                {
                    System.out.println("movie "+m1.toString());
                }
                return allMovies;
            }
            */

            //if(Movie.getMovie() .all())
            BufferedReader br=new BufferedReader(new FileReader(Play.application().getFile(CollaborativeRecommenderSystem.MOVIES_PATH)));
            String ln;
            String[] splited;
            while((ln=br.readLine())!=null&&ln.length()>0)
            {
                splited=ln.split(",");
                //System.out.println("Lenght: "+splited.length);
                try
                {
                    Movie toadd = null;
                	if(splited.length==3)
                		toadd=new Movie(Integer.parseInt(splited[0]),splited[1],splited[2]);
                    else if(splited.length==2)
                        toadd=new Movie(Integer.parseInt(splited[0]),splited[1],"");
                    else if(splited.length==1)
                    toadd=new Movie(Integer.parseInt(splited[0]),"","");

                    //toadd.save();//TODO
                    System.out.println("saving movie... "+movies.size());
                    movies.add(toadd);
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
