package com.withiter.models.merchant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import cn.bran.japid.util.StringUtils;

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
		q.order("modified");
		return q.first();
	}
	
	/**
	 * get the latest comment one
	 * @return latest comment object
	 */
	public static Comment latestOne(String mid) {
		MorphiaQuery q = Comment.q();
		q.filter("mid", mid);
		q.order("modified");
		return q.first();
	}
	
	/**
<<<<<<< Updated upstream
	 * 通用分页
	 * @param q
	 * @param page
	 * @return
	 */
	private static List<Comment> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * 10).limit(10);
		return q.asList();
	}
	
	/**
	 * 通用排序
	 * @param q
	 * @param sortBy
	 * @return
	 */
	private static MorphiaQuery sortBy(MorphiaQuery q, String sortBy) {
		q.order(sortBy);
		return q;
	}

	public static List<Comment> findbyMid(int page, String mid, String sortBy) {

		MorphiaQuery q = Comment.q();
		q.filter("mid",mid);
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}
		return paginate(q, page);
	}
	
	public static Comment getLatestComment(String rid) {
		MorphiaQuery q = Comment.q();
		q.filter("rid", rid);
		q.order("-date");
		return q.first();
	}
}
