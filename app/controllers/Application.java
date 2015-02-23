package controllers;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;

import CollaborativeRecommenderSystem.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
	public static final CollaborativeRecommenderSystem recommenderSystem=CollaborativeRecommenderSystem.getInstance();
	public static final int user = -1;

    public static Result index() {
        //TODO JC: hay que crear una instancia por sesion (no tengo claro como) (coger una instancia estática?)
        //RecommenderSystem recommenderSystem=new CollaborativeRecommenderSystem();
        /**CollaborativeRecommenderSystem recommenderSystem=new CollaborativeRecommenderSystem();
        try {
            recommenderSystem.testMahout();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }
    	
    	StatisticsModel estadisticas = recommenderSystem.evaluateModel();
    	double averageDistance = estadisticas.averageDistance;
    	double standardDeviation = estadisticas.standardDeviation;
    	double maxdistance = estadisticas.maxDistance;
    	double mindistance = estadisticas.minDistance;
    	*/
    	double averageDistance = 21;
    	double standardDeviation = 22;
    	double maxdistance = 23;
    	double mindistance = 24;
    	
        return ok(index.render(averageDistance+"", standardDeviation+"", maxdistance+"", mindistance+""));
    }
    public static Result evaluate(){
    	String evaluar = Form.form().get("evaluar");
    	if(evaluar!=null)
        	System.out.println("EVALUO!!!!");
    	else
        	System.out.println("NO EVALUO :(");
    		
    	//TODO setEn el modelo
    	String userID = Form.form().bindFromRequest().get("userid");
    	boolean itemBase = Boolean.parseBoolean(Form.form().bindFromRequest().get("modeltype"));
    	String recommendationmethod = Form.form().bindFromRequest().get("similaritymethod");
    	String neighbors = Form.form().bindFromRequest().get("neighborsquantity");
    	String trainingP = Form.form().bindFromRequest().get("trainingset");
    	
    	System.out.println("Datos: \n Usuario:"+userID+"\n itemBase:"+itemBase+
    			"\n recommendationMethod: "+recommendationmethod+
    			"\n neighbors: "+neighbors+
    			"\n training%: "+trainingP);
    	
    	/**
    	int userI = Integer.parseInt(userID);
    	int neighborsQ = Integer.parseInt(neighbors);
    	int similarityM = Integer.parseInt(recommendationmethod);
    	double trainingPercentP = Double.parseDouble(trainingP);
    	*/
    	
    	/**
    	recommenderSystem.setItemBased(itemBase);
    	recommenderSystem.setNeighborsQuantity(neighborsQ);
    	recommenderSystem.setRecommendationMethod(similarityM);
    	recommenderSystem.setTrainingPercent(trainingPercentP);
    	
    	*/
		return redirect(routes.Application.index());
    	
    }
    
    public static Result loadUser(){
    	/**
    	String userID = Form.form().get("userid");
    	long userIDLong = Long.parseLong(userID);
    	
    	if(recommenderSystem.loadUser(userIDLong)==null)
    		recommenderSystem.registerUser();
    	//TODO recomendar con el nuevo userID
    	 * */
    	System.out.println("Busca el usuario!");
    	return redirect(routes.Application.index());
    }
    
    public static Result table()
    {
    	int maxNum = 10;
    	ResultModel[] resultados = recommenderSystem.evaluateModelDetail();
    	String[][] chart = new String[resultados.length][];
    	//String[] renderedTable = new String[resultados.length];
    	
    	
    	TableResult resultsfortable;
    	TableResult[] resultaditos = new TableResult[resultados.length];
    	
    	for( int i = 0; i <resultados.length ; i++)
    	{
    		ResultModel modelo = resultados[i];
    		resultsfortable = new TableResult();
    		resultsfortable.itemID = modelo.itemId+"";
    		resultsfortable.userID = modelo.userId+"";
    		resultsfortable.estimatedRating = modelo.estimatedRating+"";
    		resultsfortable.realRating = modelo.realRating+"";
    		resultsfortable.distance = modelo.distance+"";
    		
    		resultaditos[i] = resultsfortable;
    		
    		/**
    		chart[i][0] = modelo.itemId+"";
    		chart[i][1] = modelo.userId+"";
    		chart[i][2] = modelo.estimatedRating+"";
    		chart[i][3] = modelo.realRating+"";
    		chart[i][4] = modelo.distance+"";
    		
    		renderedTable[i] = chart[i][0]+"    "+chart[i][1]+"    "+chart[i][2]+"    "+chart[i][3]+"    "+chart[i][4];
    		*/
    	}
    	
		return ok(table.render(resultaditos));    	
    }
    public static class TableResult{
    	public String itemID;
    	public String userID;
    	public String estimatedRating;
    	public String realRating;
    	public String distance;
    	
    }

}
