package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vo.CommentVO;

import com.withiter.models.account.Account;
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
	public static void updateComment(String rid,float kouwei,float huanjing,float fuwu,float xingjiabi,String content) {
		if (StringUtils.isEmpty(rid)||rid.equals("(null)")) {
			renderJSON("服务器错误");
		}
		Reservation reservation = Reservation.findByRid(rid);
		if (reservation == null) {
			renderText("服务器错误");
		}else{
			if(reservation.isAppraise==false){
				Account account = Account.findById(reservation.accountId);
				account.jifen=account.jifen+1;
				account.save();
			}
			Comment cm=Comment.getComment(rid);
			if(cm==null){
			    cm=new Comment();
			}
			cm.rid=reservation.id();
			cm.mid=reservation.merchantId;
			cm.accountId=reservation.accountId;
			cm.kouwei=kouwei;
			cm.huanjing=huanjing;
			cm.fuwu=fuwu;
			cm.xingjiabi=xingjiabi;
			cm.content=content;
			cm.save();
			
			reservation.isAppraise=true;
			reservation.save();
			renderText("评价成功");
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
	
	public static void insertTestCommectsData()
	{
		Comment comment = new Comment();
		comment.accountId = "5280f208e02427a1741c417b";
		comment.nickName = "jazze";
		comment.mid = "526fc9e478a3c576667fc14c";
		comment.averageCost="50";
		comment.xingjiabi = 2;
		comment.kouwei = 3;
		comment.fuwu = 5;
		comment.huanjing=4;
		comment.content = "比我想象中便宜一点。。。牛肉火锅很好吃~不过不管哪家店的这种豆腐肥牛锅我都很喜欢~一口牛肉也是我觉得最好吃的~还没撒胡椒粉什么的就已经觉得味道满进去了~而且肉不老不塞牙~三文鱼刺身没什么大感觉。。。倒是芥末酱给的好少。。而且感觉干掉了芝士焗年糕。。。筷子弄起来困难。。。而且其实并没什么好吃的~";
		comment.created = new Date();
		comment.modified = new Date();
		comment.save();
		
		Comment comment1 = new Comment();
		comment1.accountId = "5280f208e02427a1741c417b";
		comment1.nickName = "jazze";
		comment1.mid = "526fc9e478a3c576667fc14c";
		comment1.averageCost="50";
		comment1.xingjiabi = 5;
		comment1.kouwei = 4;
		comment1.fuwu = 5;
		comment1.huanjing=3;
		comment1.content = "“很像日本的居酒屋”。服务态度超赞，点餐的时候都“半蹲”着，上菜的时候“会提醒你”趁热吃或小心烫。菜都“很精致”，不过量“很小”，种类也“不是很多”。环境挺好，座位空间比较大，也“不是那么嘈杂”，“两三个人小聚、随便聊聊，挺合适的”。";
		comment1.created = new Date();
		comment1.modified = new Date();
		comment1.save();
		
		for (int i = 0; i < 20; i++) {
			Comment comment3 = new Comment();
			comment3.accountId = "5280f208e02427a1741c417b";
			comment3.nickName = "jazze";
			comment3.mid = "526fc9e478a3c576667fc14c";
			comment3.averageCost="50";
			comment3.xingjiabi = 5;
			comment3.kouwei = 4;
			comment3.fuwu = 5;
			comment3.huanjing=3;
			comment3.content = "“很像日本的居酒屋”。服务态度超赞，点餐的时候都“半蹲”着，上菜的时候“会提醒你”趁热吃或小心烫。菜都“很精致”，不过量“很小”，种类也“不是很多”。环境挺好，座位空间比较大，也“不是那么嘈杂”，“两三个人小聚、随便聊聊，挺合适的”。";
			comment3.created = new Date();
			comment3.modified = new Date();
			comment3.save();
		}
	}
}

