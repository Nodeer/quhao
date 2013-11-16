package controllers;

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
	public static void updateComment(String rid,float kouwei,float huanjing,float fuwu,float xingjiabi) {
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
			Comment cm=new Comment();
			cm.kouwei=kouwei;
			cm.huanjing=huanjing;
			cm.fuwu=fuwu;
			cm.xingjiabi=xingjiabi;
			cm.save();
			
			reservation.isAppraise=true;
			reservation.save();
			renderText("评价成功");
		}
	}
}
