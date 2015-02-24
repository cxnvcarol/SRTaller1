package CollaborativeRecommenderSystem;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.PlusAnonymousConcurrentUserDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import play.Play;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carol on 19/02/15.
 */
public class CollaborativeRecommenderSystem implements RecommenderSystem {

	private static final String RATINGS_PATH = "data/ratings.csv";
	public static final String RATINGS_TRAINING_PATH = "data/ratingsTraining.csv";
	private static final String RATINGS_TEST_PATH = "data/ratingsTest.csv";
	public static final String USERS_PATH = "data/users.csv";
	public static final String MOVIES_PATH = "data/movies.csv";
	private static CollaborativeRecommenderSystem sInstance = null;

	private boolean itemBased;
	private int similarityMethod;
	private double trainingPercent;
	private int neighborsQuantity;
	private boolean evaluationUpdated;
	private boolean modelUpdated;

	private ResultModel[] resultsModelList;
	private StatisticsModel statisticsModel;

	private DataModel dataModel;
	private PlusAnonymousConcurrentUserDataModel plusDataModel;
	private Recommender recommender;

	public CollaborativeRecommenderSystem() {
		setDefaultParameters();
		reloadData();
		updateModelEvaluation();
	}

	public static CollaborativeRecommenderSystem getInstance() {
		if (sInstance == null) {
			sInstance = new CollaborativeRecommenderSystem();
		}
		return sInstance;
	}

	private void setDefaultParameters() {
		itemBased = false;
		trainingPercent = 0.1;
		neighborsQuantity = 20;
		similarityMethod = SIMILARITY_METHOD_JACCARD;
		evaluationUpdated = false;
	}

	@Override
	public StatisticsModel evaluateModel() {
		if (!evaluationUpdated) {
			updateModelEvaluation();
		}
		System.out.println("Statistics from Model: "+statisticsModel.averageDistance+" "+statisticsModel.maxDistance+" "+statisticsModel.minDistance+" "+statisticsModel.standardDeviation+" ");
		return statisticsModel;
	}

	private void updateModelEvaluation() {
		updateModel();

		File tt = Play.application().getFile(RATINGS_TEST_PATH);
		try {
			DataModel dataModelTest = new FileDataModel(tt);
			System.out.println("Number of Users: "+dataModelTest.getNumUsers());
			LongPrimitiveIterator iterator = dataModelTest.getUserIDs();
			while(iterator.hasNext()){	
				User u = User.find(iterator.next());
			//for (User u : User.getAll()) {
				if (!u.isNewUser() && u.getRatings().size() > 0)  {
					if (dataModelTest.getPreferencesFromUser(u.getId()) != null) {
						PreferenceArray prefs = dataModelTest
								.getPreferencesFromUser(u.getId());
						long[] prefIds = prefs.getIDs();

						PreferenceArray prefsOrig = dataModel
								.getPreferencesFromUser(u.getId());
						long[] prefIdsOrig = prefsOrig.getIDs();
						//TODO esto no puede ser negativo, ojo en la resta, inverti las respuestas
						resultsModelList = new ResultModel[prefIds.length > prefIdsOrig.length ?0 :  prefIdsOrig.length
								- prefIds.length];
						int j = 0;
						for (int i = 0; i < prefIds.length
								&& j < resultsModelList.length; i++) {
							int isIt = Arrays.binarySearch(prefIdsOrig,
									prefs.getItemID(0));

							if (isIt < 0) {
								double estimated = recommender
										.estimatePreference(u.getId(),
												prefs.getItemID(i));
								resultsModelList[j] = new ResultModel(
										u.getId(), prefs.getItemID(i),
										prefs.getValue(i), estimated);
								j++;
							}
						}
						if (j != resultsModelList.length) {
							System.err
									.print("Sizes for evaluation arrays does not match for user id "
											+ u.getId() + "...\n");
						}
					}
				}
			}

		} catch (IOException | TasteException e) {
			e.printStackTrace();
		}
		statisticsModel = new StatisticsModel(resultsModelList);
		evaluationUpdated = true;
	}

	private void updateModel() {
		try {
			ItemSimilarity similarity = null;
			switch (similarityMethod) {
			case SIMILARITY_METHOD_JACCARD:
				similarity = new TanimotoCoefficientSimilarity(dataModel);
				break;
			case SIMILARITY_METHOD_COSINE:
				similarity = new UncenteredCosineSimilarity(dataModel);
				break;
			case SIMILARITY_METHOD_PEARSON:
				similarity = new PearsonCorrelationSimilarity(dataModel);
				break;
			}
			if (itemBased) {
				recommender = new GenericItemBasedRecommender(dataModel,
						similarity);
			} else {
				UserSimilarity similarityU = (UserSimilarity) similarity;
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(
						neighborsQuantity, similarityU, dataModel);
				recommender = new GenericUserBasedRecommender(dataModel,
						neighborhood, similarityU);
			}

			modelUpdated = true;
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ResultModel[] evaluateModelDetail() {
		if (!evaluationUpdated) {
			updateModelEvaluation();
		}
		return resultsModelList;
	}

	@Override
	public Recommendation[] getUserRecommendation(int userId, int numMax) {
		if (!modelUpdated) {
			updateModel();
		}
		try {
			User found = loadUser(userId);
			if (found != null) {
				if (found.isNewUser()) {
					plusDataModel.setTempPrefs(found.getPreferenceArray(),
							found.getId());
				}
				List<RecommendedItem> recommendations = recommender.recommend(
						userId, numMax);
				Recommendation[] returned = new Recommendation[recommendations
						.size()];

				for (int i = 0; i < recommendations.size(); i++) {
					RecommendedItem recommendation = recommendations.get(i);
					returned[i] = new Recommendation(
							recommendation.getItemID(),
							recommendation.getValue());
				}
				return returned;
			}
			return findBetterMovies(numMax);
		} catch (TasteException e) {
			e.printStackTrace();
			return new Recommendation[0];
		}
	}

	private Recommendation[] findBetterMovies(int nmovies) {
		try {
			Movie[] top = Movie.getOrderedByRating();
			int nmovs = (nmovies < top.length ? nmovies : top.length);
			Recommendation[] result = new Recommendation[nmovs];
			for (int i = 0; i < nmovs; i++) {
				result[i] = new Recommendation(top[i].getId(),
						top[i].getAverageRating());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return new Recommendation[0];
		}
	}

	@Override
	public User loadUser(long userId) {
		return User.find(userId);
	}

	@Override
	public void rate(long userId, long itemId, double rating) {
		User myus = User.find(userId);
		if (myus != null) {
			User u = User.find(userId);
			u.addRating(itemId, rating);
			if (!u.isNewUser()) {
				try {
					dataModel.setPreference(userId, itemId, (float) rating);
				} catch (TasteException e) {
					e.printStackTrace();
				}
			}
		}
		System.err.print("User not found for rating...\n");

	}

	@Override
	public User registerUser() {
		long id = plusDataModel.takeAvailableUser();
		User added = new User(id);
		User.addUser(added);
		return added;
	}

	@Override
	public void setItemBased(boolean itemBasedBool) {
		if (itemBasedBool != itemBased) {
			itemBased = itemBasedBool;
			updateModel();
		}
	}

	@Override
	public void setNeighborsQuantity(int neighborsQuantityP) {
		if (neighborsQuantity != neighborsQuantityP) {
			neighborsQuantity = neighborsQuantityP;
			updateModel();
		}
	}

	@Override
	public void setRecommendationMethod(int recommendationMethod) {
		if (similarityMethod != recommendationMethod) {
			similarityMethod = recommendationMethod;
			updateModel();
		}
	}

	@Override
	public void setTrainingPercent(double trainingPercentP) {
		if (trainingPercent != trainingPercentP) {
			trainingPercent = trainingPercentP;
			reloadData();
			evaluationUpdated = false;
		}
	}

	private void reloadData() {
		try {
			File src = Play.application().getFile(RATINGS_PATH);
			int totalLines = countLines(src);
			int lnTraining = (int) (totalLines * trainingPercent);

			BufferedReader br = new BufferedReader(new FileReader(src));
			String parentPath = src.getParentFile().getAbsolutePath();

			File trainingFile = new File(parentPath + "/ratingsTrainig.csv");
			FileWriter fw = new FileWriter(trainingFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < lnTraining; i++) {
				bw.write(br.readLine() + "\n");
			}
			bw.close();

			File testFile = new File(parentPath + "/ratingsTest.csv");
			fw = new FileWriter(testFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			for (int i = lnTraining; i < totalLines; i++) {
				bw.write(br.readLine() + "\n");
			}
			bw.close();
			br.close();

			File tt = Play.application().getFile(RATINGS_TRAINING_PATH);
			dataModel = new FileDataModel(tt);
			plusDataModel = new PlusAnonymousConcurrentUserDataModel(dataModel,
					100);

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Movie.getAll();
			User.getAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateModel();

	}

	public static int countLines(File file) throws IOException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if ('\n' == c[i]) {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		}
	}

	public void testMahout() throws IOException, TasteException {

		long start = System.currentTimeMillis();
		setTrainingPercent(0.05);
		reloadData();
		long diff = System.currentTimeMillis() - start;
		System.out.println("Data separated...  Ellapsed in " + diff + "ms.");

		File tt = Play.application().getFile("data/test.csv");
		DataModel model = new FileDataModel(tt);
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1,
				similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(
				model, neighborhood, similarity);

		List<RecommendedItem> recommendations = recommender.recommend(2, 3);
		for (RecommendedItem recommendation : recommendations) {
			System.out.println(recommendation);
		}
	}
}