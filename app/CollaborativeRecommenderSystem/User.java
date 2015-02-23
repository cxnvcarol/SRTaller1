package CollaborativeRecommenderSystem;

import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import play.Play;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by carol on 19/02/15.
 */
public class User {
	private long id;
	private ArrayList<Rating> ratings;

	private static ArrayList<User> allUsers;

	public static long lastUser = 0;

	public PreferenceArray getPreferenceArray() {
		return preferenceArray;
	}

	private PreferenceArray preferenceArray;

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
		if (isNewUser) {
			preferenceArray = new GenericUserPreferenceArray(30);
		}
	}

	private boolean isNewUser;

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
				if (m.getId() == userId) {
					return m;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getId() {
		return id;
	}

	public ArrayList<Rating> getRatings() {
		return ratings;
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
			if(rat.getMovie()!=null){
			if (rat.getMovie().getId() == movieId) {
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
