package com.withiter.models.merchant;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Comment extends CommentEntityDef {

	/**
	 * get all comments
	 * @return List<Comment>
	 */
	public static List<Comment> getAllComments(){
		MorphiaQuery q = Comment.q();
		return q.asList();
	}
	
	/**
	 * get comments by merchant id
	 * @param mid
	 * @return the comments list with unique merchant id
	 */
	public static List<Comment> getCommentsByMid(String mid){
		MorphiaQuery q = Comment.q();
		q.filter("mid", mid);
		return q.asList();
	}
	
	public Comment(){
	}

	/**
	 * get the latest comment one
	 * @return latest comment object
	 */
	public static Comment latestOne() {
		MorphiaQuery q = Comment.q();
		q.order("date");
		return q.first();
	}
	
	/**
	 * get the latest comment one
	 * @return latest comment object
	 */
	public static Comment latestOne(String mid) {
		MorphiaQuery q = Comment.q();
		q.filter("mid", mid);
		q.order("date");
		return q.first();
	}
}
