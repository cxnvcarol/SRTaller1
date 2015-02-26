package models;

import CollaborativeRecommenderSystem.CollaborativeRecommenderSystem;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import play.Play;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by carol on 19/02/15.
 */
@Entity
public class User extends Model{

    @Id
	public long id;
	public ArrayList<Rating> ratings;
	private static ArrayList<User> allUsers;

	public static long lastUser = 0;
    @Transient
	public PreferenceArray preferenceArray;
    private boolean isNewUser;

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
		if (isNewUser) {
			preferenceArray = new GenericUserPreferenceArray(30);
		}
	}


	public User(long idp) {
		id = idp;
		ratings = new ArrayList<Rating>();
		isNewUser = false;
	}

	public static void addUser(User u) {
		u.setNewUser(true);
		allUsers.add(u);
	}

	public static User[] getAll() throws IOException {
		// if(allUsers==null||allUsers.length==0)
		if (allUsers == null || allUsers.size() == 0) {
			System.out.print("Finding all movies...");
			// allUsers=new
			// User[CollaborativeRecommenderSystem.countLines(Play.application().getFile(USERS_PATH))];
			allUsers = new ArrayList<User>();
			// lastUser=allUsers.length;
			lastUser = CollaborativeRecommenderSystem.countLines(Play
                    .application().getFile(
                            CollaborativeRecommenderSystem.USERS_PATH));
			for (int i = 0; i < lastUser; i++) {
				// allUsers[i]=new User(i+1);
				allUsers.add(new User(i + 1));
			}
			BufferedReader br = new BufferedReader(
					new FileReader(
							Play.application()
									.getFile(
											CollaborativeRecommenderSystem.RATINGS_TRAINING_PATH)));
			String lin;
			while ((lin = br.readLine()) != null && lin.length() > 0) {
				String[] spl = lin.split(",");
				// could be more efficient?
				int movieid = Integer.parseInt(spl[1]);
				if (spl[2] != null) {
					double rr = Double.parseDouble(spl[2]);
					User.find(Integer.parseInt(spl[0])).addRating(movieid, rr);
					if(Movie.find(movieid)!=null)
						Movie.find(movieid).addRating(rr);
				}
			}
			br.close();
		}
		return allUsers.toArray(new User[(int) lastUser]);

	}

	public static User find(long userId) {

		try {
			User[] allm = getAll();
			for (User m : allm) {
				if (m.id == userId) {
					return m;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @precondition movieId exists
	 * @param movieId
	 * @param rating
	 */
	public void addRating(long movieId, double rating) {
		Rating rat;
		for (int i = 0; i < ratings.size(); i++) {
			rat = ratings.get(i);
			if(rat.movie!=null){
			if (rat.movie.id == movieId) {
				rat.setRating(rating);
				if (isNewUser) {
					preferenceArray.setUserID(i, id);
					preferenceArray.setItemID(i, movieId);
					preferenceArray.setValue(i, (float) rating);
				}
				break;
			}
			}
		}
		if (isNewUser) {
			preferenceArray.setUserID(ratings.size(), id);
			preferenceArray.setItemID(ratings.size(), movieId);
			preferenceArray.setValue(ratings.size(), (float) rating);
		}
		ratings.add(new Rating(movieId, rating));
	}

}
