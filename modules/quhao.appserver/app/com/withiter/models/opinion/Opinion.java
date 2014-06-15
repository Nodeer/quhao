package com.withiter.models.opinion;

import java.util.List;

import org.bson.types.ObjectId;

import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.NoAutoTimestamp;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.admin.MerchantAccount;

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
	 * 分页查询，每页十条数据。
	 * @param page 第page页
	 * @param countPerPage 每页的记录数
	 * @return
	 */
	public static List<Opinion> nextNoHandle(int page, int countPerPage){
		MorphiaQuery q = Opinion.q();
		q.filter("handle", false);
		q.offset((page-1)*countPerPage).limit(countPerPage);
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

	public static int totalSize() {
		MorphiaQuery q = Opinion.q();
		return (int)q.count();
	}
	
}
