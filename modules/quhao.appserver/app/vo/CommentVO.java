package vo;

import java.util.Date;

import com.withiter.models.merchant.Comment;

public class CommentVO {

	public String uid;
	
	public String accountId;
	
	public String nickName;
	
	public String mid;
	
	public String averageCost = "0";
	public float xingjiabi = 0;
	public float kouwei = 0;
	public float huanjing = 0;
	public float fuwu = 0;

	public String content = "";

	public String location;
	
	public Date created = new Date();
	public Date modified = new Date();
	
	public static CommentVO build(Comment comment) {
		CommentVO commentVO = new CommentVO();
		commentVO.uid = comment.uid;
		commentVO.accountId = comment.accountId;
		commentVO.nickName = comment.nickName;
		commentVO.mid = comment.mid;
		commentVO.averageCost = comment.averageCost;
		commentVO.xingjiabi = comment.xingjiabi;
		commentVO.kouwei = comment.kouwei;
		commentVO.huanjing = comment.huanjing;
		commentVO.fuwu = comment.fuwu;
		commentVO.content = comment.content;
		commentVO.location = comment.location;
		commentVO.created = comment.created;
		commentVO.modified = comment.modified;
		return commentVO;
	}

}
