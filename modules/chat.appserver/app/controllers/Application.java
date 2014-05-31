package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void enterDemo(@Required String user, @Required String demo) {        
        if(validation.hasErrors()) {
            flash.error("Please choose a nick name and the demonstration type…");
            index();
        }
        
        if(demo.equals("websocket")) {
            WebSocket.room(user);
        }        
    }

}