package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.*;

import CollaborativeRecommenderSystem.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import static play.libs.Json.toJson;

public class Application extends Controller {
	public static final CollaborativeRecommenderSystem recommenderSystem = CollaborativeRecommenderSystem.getInstance();
	public static long user =-1;

    public static Result allmovies()
    {
        try {
            return ok(toJson(Movie.getAll()));
        } catch (IOException e) {
            //e.printStackTrace();
            return ok(toJson(new ArrayList()));
        }
    }
    public static Result ratings(Long userid)
    {
        User u=recommenderSystem.loadUser(userid);
        if(u!=null)
            return ok(toJson(u.ratings));
        else return ok(toJson(new ArrayList()));

    }
    public static Result userZone(Long id)
    {
        return ok(userRecommendations.render(id));//user does not exist by default
    }

    public static Result evaluate() {

        String userID = Form.form().bindFromRequest().get("userid");
        boolean itemBase = Boolean.parseBoolean(Form.form().bindFromRequest()
                .get("modeltype"));
        String recommendationmethod = Form.form().bindFromRequest()
                .get("similaritymethod");
        String neighbors = Form.form().bindFromRequest()
                .get("neighborsquantity");
        String trainingP = Form.form().bindFromRequest().get("trainingset");

        System.out.println("Datos: \n Usuario:" + userID + "\n itemBased:"
                + itemBase + "\n recommendationMethod: " + recommendationmethod
                + "\n neighbors: " + neighbors + "\n training%: " + trainingP);

        long userI = Long.parseLong(userID);
        int neighborsQ = Integer.parseInt(neighbors);
        int similarityM = Integer.parseInt(recommendationmethod);
        double trainingPercentP = (Double.parseDouble(trainingP)) / 100;

        user = userI;

        recommenderSystem.setItemBased(itemBase);
        recommenderSystem.setNeighborsQuantity(neighborsQ);
        recommenderSystem.setRecommendationMethod(similarityM);
        recommenderSystem.setTrainingPercent(trainingPercentP);

        List<Recommendation> recomendaciones = recommenderSystem
                .getUserRecommendation(userI, 10);
        String recomendacionStr = "";
        if (user != -1) {
            recomendaciones = recommenderSystem.getUserRecommendation(user, 10);
        }
        for (int i = 0; i < recomendaciones.size(); i++) {
            recomendacionStr += recomendaciones.get(i).toString() + "|";
        }
        System.out.println("Recomendaciones encontradas: "
                + recomendaciones.size());
        System.out.println(recomendacionStr);

        StatisticsModel estadisticas = recommenderSystem.evaluateModel();
        System.out.println("Estadisticas de evaluacion: "
                + estadisticas.averageDistance + " " + estadisticas.maxDistance
                + " " + estadisticas.minDistance + " "
                + estadisticas.standardDeviation + " ");
        double averageDistance = estadisticas.averageDistance;
        double standardDeviation = estadisticas.standardDeviation;
        double maxdistance = estadisticas.maxDistance;
        double mindistance = estadisticas.minDistance;

        return ok(index.render(averageDistance + "", standardDeviation + "",
                maxdistance + "", mindistance + "", estadisticas.resultsLength
                        + "", recomendacionStr));

    }

    public static Result getRecommendations(Long id) {
        //long userid=Long.parseLong(Form.form().bindFromRequest().get("userid"));
        List<Recommendation> recomendaciones = recommenderSystem.getUserRecommendation(id, 10);
        return ok(toJson(recomendaciones));
    }
    public static Result rate() {
        long usid=Long.parseLong(Form.form().bindFromRequest().get("userid"));
        double rating=Double.parseDouble(Form.form().bindFromRequest().get("new_rating"));
        long movid=Long.parseLong(Form.form().bindFromRequest().get("movieid"));
        //TODO
        return(ok());

    }
    public static Result table()
    {
    	//ResultModel[] resultados = recommenderSystem.evaluateModelDetail();
    	ResultModel[] resultados = new ResultModel [ 0];
    	ArrayList<ArrayList<String>> arr = new ArrayList<>();
    	
    	/**
    	String[] items = new String[resultados.length];
    	String[] usuarios = new String[resultados.length];
    	String[] estimados = new String[resultados.length];
    	String[] reales = new String[resultados.length];
    	String[] distancias = new String[resultados.length];
    	//String[] renderedTable = new String[resultados.length];
    	*/
    	
		ArrayList<String> procesando =  new ArrayList<>();
    	
    	for( int i = 0; i <resultados.length ; i++)
    	{
    		ResultModel modelo = resultados[i];
    		
    		procesando.add(modelo.itemId+"");
    		procesando.add(modelo.userId+"");
    		procesando.add(modelo.estimatedRating+"");
    		procesando.add(modelo.realRating+"");
    		procesando.add(modelo.distance+"");
    		
    		arr.add(procesando);
        	//result.put("Registro"+i, modelo.itemId + " "+modelo.userId+ " "+modelo.estimatedRating +" "+modelo.realRating + " "+modelo.distance);
        	/**result.put("userID"+i, modelo.userId);
        	result.put("estimatedrating"+i, modelo.estimatedRating);
        	result.put("realrating"+i, modelo.realRating);
        	result.put("distance"+i, modelo.distance);*/

    		/**
    		items[i] = +"";
    		usuarios[i] = modelo.userId+"";
    		estimados[i] = modelo.estimatedRating+"";
    		reales[i] = modelo.realRating+"";
    		distancias[i] = modelo.distance+"";

    		resultsfortable = new TableResult();
    		resultsfortable.itemID = modelo.itemId+"";
    		resultsfortable.userID = modelo.userId+"";
    		resultsfortable.estimatedRating = modelo.estimatedRating+"";
    		resultsfortable.realRating = modelo.realRating+"";
    		resultsfortable.distance = modelo.distance+"";
    		
    		resultaditos[i] = resultsfortable;
    		
    		
    		renderedTable[i] = chart[i][0]+"    "+chart[i][1]+"    "+chart[i][2]+"    "+chart[i][3]+"    "+chart[i][4];
    		*/
    	}
    	
		return ok(table.render(arr));    	
    }

	public static Result index() {
		// TODO JC: hay que crear una instancia por sesion (no tengo claro como)
		StatisticsModel estadisticas = recommenderSystem.evaluateModel();
		System.out.println("Estadisticas de evaluacion: "
				+ estadisticas.averageDistance + " " + estadisticas.maxDistance
				+ " " + estadisticas.minDistance + " "
				+ estadisticas.standardDeviation + " ");
		double averageDistance = estadisticas.averageDistance;
		double standardDeviation = estadisticas.standardDeviation;
		double maxdistance = estadisticas.maxDistance;
		double mindistance = estadisticas.minDistance;

		Recommendation[] recomendaciones = new Recommendation[0];
		String recomendacionStr = "";
		/**if (user != -1) {
			recomendaciones = recommenderSystem.getUserRecommendation(user, 10);
		}
		for (int i = 0; i < recomendaciones.length; i++) {
			recomendacionStr += recomendaciones[i].toString() + "\n";
		}
		System.out.println("Recomendaciones encontradas: "
				+ recomendaciones.length);
		System.out.println(recomendacionStr);
		
		 * double averageDistance = 21; double standardDeviation = 22; double
		 * maxdistance = 23; double mindistance = 24;
		 */
		return ok(index.render(averageDistance + "", standardDeviation + "",
				maxdistance + "", mindistance + "", estadisticas.resultsLength
						+ "", recomendacionStr));
	}

	public static Result loadUser() {
		/**
		 * String userID = Form.form().get("userid"); long userIDLong =
		 * Long.parseLong(userID);
		 * 
		 * if(recommenderSystem.loadUser(userIDLong)==null)
		 * recommenderSystem.registerUser(); //TODO recomendar con el nuevo
		 * userID
		 * */
		System.out.println("Busca el usuario!");
		return redirect(routes.Application.index());
	}


}
