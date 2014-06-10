package com.withiter.models.opinion;

import java.util.List;

import org.bson.types.ObjectId;

import play.modules.morphia.Model.NoAutoTimestamp;

import com.google.code.morphia.annotations.Entity;

@Entity
@NoAutoTimestamp
public class Opinion extends OpinionEntityDef {
	
	private static int DEFAULT_NUMBER_PER_PAGE = 10;
	
	public static List<Opinion> nextNoHandle(int page){
		MorphiaQuery q = Opinion.q();
		q.filter("handle", false);
		q = paginate(q, page);
		return q.asList();
	}
	
	/**
	 * 分页获取所有用户反馈
	 * @param page 第page页
	 * @return
	 */
	public static List<Opinion> next(int page){
		MorphiaQuery q = Opinion.q();
		q = paginate(q, page);
		
		return q.asList();
	}
	
	/**
	 * 通用分页
	 * @param q MorphiaQuery 对象
	 * @param page 第page页
	 * @return
	 */
	private static MorphiaQuery paginate(MorphiaQuery q, int page){
		q.offset(DEFAULT_NUMBER_PER_PAGE*(page -1)).limit(DEFAULT_NUMBER_PER_PAGE);
		return q;
	}
	
	/**
	 * 处理建议
	 * @param oid
	 */
	public static void handle(String oid){
		Opinion o = Opinion.findById(new ObjectId(oid));
		o.handle = true;
		o.save();
	}
	
}
