package controllers;

import CollaborativeRecommenderSystem.*;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        //TODO JC: hay que crear una instancia por sesion (no tengo claro como)
        RecommenderSystem recommenderSystem=new CollaborativeRecommenderSystem();

        return ok(index.render("Your new application is ready."));
    }



}
