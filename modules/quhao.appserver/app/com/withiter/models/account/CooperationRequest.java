package com.withiter.models.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.Logger;

public class CooperationRequest extends CooperationRequestEntityDef{
	
	private static int DEFAULT_NUMBER_PER_PAGE = 10;
	
	public CooperationRequest(String companyName, String peopleName, String peopleContact, String peopleEmail){
		this.companyName = companyName;
		this.peopleName = peopleName;
		this.peopleContact = peopleContact;
		this.peopleEmail = peopleEmail;
	}
	
	public CooperationRequest(){
		
	}
	
	/**
	 * 分页获取没有处理的合作申请
	 * @param page 第page页
	 * @return
	 */
	public static List<CooperationRequest> nextNoHandle(int page){
		MorphiaQuery q = CooperationRequest.q();
		Logger.debug("q1 size is: " + q.count());
		q.filter("handle", false);
		Logger.debug("q2 size is: " + q.count());
		q = paginate(q, page);
		return q.asList();
	}
	
	/**
	 * 分页获取所有合作申请
	 * @param page 第page页
	 * @return
	 */
	public static List<CooperationRequest> next(int page){
		List<CooperationRequest> list = new ArrayList<CooperationRequest>();
		MorphiaQuery q = CooperationRequest.q();
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
		Logger.debug("q size is: " + q.count());
		q.offset(DEFAULT_NUMBER_PER_PAGE*(page -1)).limit(DEFAULT_NUMBER_PER_PAGE);
		
		Logger.debug("q size is: " + q.count());
		
		return q;
	}
	
	/**
	 * 格式化日期
	 * @return
	 */
	public String dateFormat(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.createTime);
	}

	/**
	 * 标记id为rid的CooperationRequest为成功处理状态
	 * @param rid
	 * @return
	 */
	public static boolean markHandled(String rid) {
		CooperationRequest c = CooperationRequest.findById(new ObjectId(rid));
		c.handle = true;
		c.save();
		return true;
	}
}
