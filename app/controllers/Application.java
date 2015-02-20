package controllers;

import CollaborativeRecommenderSystem.*;
import org.apache.mahout.cf.taste.common.TasteException;
import play.*;
import play.mvc.*;

import views.html.*;

import java.io.IOException;

public class Application extends Controller {

    public static Result index() {
        //TODO JC: hay que crear una instancia por sesion (no tengo claro como)
        //RecommenderSystem recommenderSystem=new CollaborativeRecommenderSystem();
        CollaborativeRecommenderSystem recommenderSystem=new CollaborativeRecommenderSystem();
        try {
            recommenderSystem.testMahout();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }

        return ok(index.render("Your new application is ready."));
    }



}
