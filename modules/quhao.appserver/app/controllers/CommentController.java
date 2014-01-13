package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vo.CommentVO;

import com.withiter.common.Constants.CreditStatus;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Merchant;
import com.withiter.utils.StringUtils;

public class CommentController  extends BaseController{
	/**
	 *  商家评价
	 * 
	 * @param rid id of reservation
	 * @param kouwei 口味 
	 * @param huanjing 环境
	 * @param fuwu 服务
	 * @param xingjiabi 性价比
	 * @return  String
	 */
	public static void updateComment(String rid,int kouwei,int huanjing,int fuwu,int xingjiabi,String content,int grade,String averageCost,String cost) {
		if (StringUtils.isEmpty(rid)||rid.equals("(null)")) {
			renderJSON("服务器错误");
		}
		Reservation reservation = Reservation.findByRid(rid);
		if (reservation == null) {
			renderText("服务器错误");
		}else{
			if(!reservation.isCommented){
				Account account = Account.findById(reservation.accountId);
				account.jifen=account.jifen+1;
				account.dianping=account.dianping+1;
				account.save();
				
				// 增加积分消费
				Credit credit = new Credit();
				credit.accountId = reservation.accountId;
				credit.merchantId = reservation.merchantId;
				credit.reservationId = reservation.id();
				credit.cost = false;
				credit.jifen=1;
				credit.status = CreditStatus.comment;
				credit.created = new Date();
				credit.modified = new Date();
				credit.save();
			}
			Comment cm=Comment.getComment(rid);
			if(cm==null){
			    cm=new Comment();
			}
			cm.rid=reservation.id();
			cm.mid=reservation.merchantId;
			cm.accountId=reservation.accountId;
			Account account = Account.findById(reservation.accountId);
			cm.nickName = account.nickname;
			cm.kouwei=kouwei;
			cm.huanjing=huanjing;
			cm.fuwu=fuwu;
			cm.grade=grade;
			cm.xingjiabi=xingjiabi;
			cm.averageCost = averageCost;
			cm.content=content;
			if(StringUtils.isNotEmpty(cost))
			{
				cm.averageCost=cost.trim();
			}
			cm.save();
			
			reservation.isCommented=true;
			reservation.save();
			renderText("success");
		}
	}
	/**
	 * 返回最新的评价
	 * 
	 * @param id  id of  reservation
	 * 
	 */
	public static void getComment(String rid) {
		Comment c = Comment.getComment(rid);
		renderJSON(CommentVO.build(c));
	}
	
	/**
	 *  获取用户评论
	 * @return 评论数量
	 * @param  id of account
	 */
	public static void getCommentsById(String accountId) {
		long count= Comment.getCommentCountByAccountId(accountId);
		renderText(count);
	}
	
	/**
	 *  根据商家ID获取评论
	 * @return 评论
	 * @param  date 日期
	 */
	public static void getCommentsByMid(int page,String mid,String sortBy) {
		page = (page == 0) ? 1 : page;

		List<Comment> comments = Comment.findbyMid(page,mid,sortBy);
		List<CommentVO> commentVOs = new ArrayList<CommentVO>();
		for (Comment comment : comments) {
			commentVOs.add(CommentVO.build(comment));
		}
		renderJSON(commentVOs);
		
	}
	
	/**
	 *  根据商家ID获取评论
	 * @return 评论
	 * @param  date 日期
	 */
	public static void getCommentsByAccountId(int page,String accountId,String sortBy) {
		page = (page == 0) ? 1 : page;

		List<Comment> comments = Comment.findbyAccountId(page,accountId,sortBy);
		List<CommentVO> commentVOs = new ArrayList<CommentVO>();
		for (Comment comment : comments) {
			CommentVO vo = CommentVO.build(comment);
			Merchant merchant = Merchant.findByMid(comment.mid);
			vo.merchantName = merchant.name;
			vo.merchantAddress = merchant.address;
			commentVOs.add(vo);
		}
		renderJSON(commentVOs);
		
	}
	
}

