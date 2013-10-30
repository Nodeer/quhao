package controllers;

import java.util.Date;

import com.withiter.models.opinion.Opinion;


public class OpinionController extends BaseController{

	/**
	 * add feedback
	 * 
	 * @param phone
	 * @param email
	 * @param feedback
	 */
	public static void createOpinion(String opinion, String contact) {
		
		Opinion opinionTO = new Opinion();
		opinionTO.contact = contact;
		opinionTO.opinion = opinion;
		opinionTO.created = new Date();
		opinionTO.modified = new Date();
		opinionTO.create();
		
		renderJSON("success");
	}
	
}
