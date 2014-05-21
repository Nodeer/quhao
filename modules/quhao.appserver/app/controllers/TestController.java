package controllers;

import java.util.Date;

import notifiers.MailsController;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.merchant.Comment;

public class TestController extends BaseController {
	
	public static void quhaoConcurrentTest(){
		String mobile = params.get("mobile");
		String mid = "5367accb0cf2c147bc369a16";
		int seatType = 2;
		
		if(StringUtils.isEmpty(mobile)){
			renderJSON("请添加手机参数");
		}
		
		Account account = Account.findByPhone(mobile);
		String aid = account.id();
		String url = "http://quhao.la/nahao?accountId="+aid+"&mid="+mid+"&seatNumber="+seatType;
		String result = CommonHTTPRequest.get(url);
		
	}
	
	public static void mailtest(){
		MailsController.sendTo("mag_lee@126.com");
	}
	
	public static void list(){
		renderJapid();
	}
	
	public static void poi021(){
		renderJapid();
	}
	public static void poi020(){
		renderJapid();
	}
	public static void poi010(){
		renderJapid();
	}
	public static void poi0755(){
		renderJapid();
	}
	public static void m020(){
		renderJapid();
	}
	public static void m021(){
		renderJapid();
	}
	public static void m010(){
		renderJapid();
	}
	public static void m0755(){
		renderJapid();
	}

	public static void main(String[] args) {

		try {
			String str = "{    \"status\":\"OK\",    \"result\":{        \"location\":{            \"lng\":121.371053,            \"lat\":31.187143        },        \"precise\":1,        \"confidence\":80,        \"level\":\"\u9053\u8def\"    }}";
			JSONObject a = new JSONObject(str);

			System.out.println(a.optString("lng"));
			System.out.println(a); // {"c":"d","a":"b"}
			System.out.println(a.get("status")); // d
			System.out.println(a.get("result")); // d
			System.out.println(new JSONObject(a.get("result").toString())
					.get("location")); // d
			JSONObject xyJSON = new JSONObject(new JSONObject(a.get("result")
					.toString()).get("location").toString());
			System.out.println(xyJSON.get("lng"));
			System.out.println(xyJSON.get("lat"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void insertTestCommectsData() {
		Comment comment = new Comment();
		comment.accountId = "5291bc7378a34c9ba73d1a3f";
		comment.nickName = "jazze";
		comment.mid = "5291bd8478a34c9ba73d1a40";
		comment.averageCost = "50";
		comment.xingjiabi = 2;
		comment.kouwei = 3;
		comment.fuwu = 5;
		comment.huanjing = 4;
		comment.content = "比我想象中便宜一点。。。牛肉火锅很好吃~不过不管哪家店的这种豆腐肥牛锅我都很喜欢~一口牛肉也是我觉得最好吃的~还没撒胡椒粉什么的就已经觉得味道满进去了~而且肉不老不塞牙~三文鱼刺身没什么大感觉。。。倒是芥末酱给的好少。。而且感觉干掉了芝士焗年糕。。。筷子弄起来困难。。。而且其实并没什么好吃的~";
		comment.created = new Date();
		comment.modified = new Date();
		comment.save();

		Comment comment1 = new Comment();
		comment1.accountId = "5291bc7378a34c9ba73d1a3f";
		comment1.nickName = "jazze";
		comment1.mid = "5291ac9978a30fc8b8e54d9d";
		comment1.averageCost = "50";
		comment1.xingjiabi = 5;
		comment1.kouwei = 4;
		comment1.fuwu = 5;
		comment1.huanjing = 3;
		comment1.content = "“很像日本的居酒屋”。服务态度超赞，点餐的时候都“半蹲”着，上菜的时候“会提醒你”趁热吃或小心烫。菜都“很精致”，不过量“很小”，种类也“不是很多”。环境挺好，座位空间比较大，也“不是那么嘈杂”，“两三个人小聚、随便聊聊，挺合适的”。";
		comment1.created = new Date();
		comment1.modified = new Date();
		comment1.save();

		for (int i = 0; i < 20; i++) {
			Comment comment3 = new Comment();
			comment3.accountId = "5291bc7378a34c9ba73d1a3f";
			comment3.nickName = "jazze";
			comment3.mid = "5291bd8478a34c9ba73d1a40";
			comment3.averageCost = "50";
			comment3.xingjiabi = 5;
			comment3.kouwei = 4;
			comment3.fuwu = 5;
			comment3.huanjing = 3;
			comment3.content = "“很像日本的居酒屋”。服务态度超赞，点餐的时候都“半蹲”着，上菜的时候“会提醒你”趁热吃或小心烫。菜都“很精致”，不过量“很小”，种类也“不是很多”。环境挺好，座位空间比较大，也“不是那么嘈杂”，“两三个人小聚、随便聊聊，挺合适的”。";
			comment3.created = new Date();
			comment3.modified = new Date();
			comment3.save();
		}
	}

	public static void insertTestCreditsData() throws InterruptedException {
		Credit credit1 = new Credit();
		credit1.accountId = "5291bc7378a34c9ba73d1a3f";
		credit1.merchantId = "5291bd8478a34c9ba73d1a40";
		credit1.reservationId = "529200ac78a34c9ba73d1a46";
		credit1.cost = false;
		credit1.jifen = -1;
		credit1.status = CreditStatus.getNumber;
		credit1.created = new Date();
		credit1.modified = new Date();
		credit1.save();

		Thread.sleep(5000);

		Credit credit2 = new Credit();
		credit2.accountId = "5291bc7378a34c9ba73d1a3f";
		credit2.merchantId = "";
		credit2.reservationId = "";
		credit2.cost = true;
		credit2.jifen = 1;
		credit2.status = CreditStatus.exchange;
		credit2.created = new Date();
		credit2.modified = new Date();
		credit2.save();

		Thread.sleep(5000);

		Credit credit3 = new Credit();
		credit3.accountId = "5291bc7378a34c9ba73d1a3f";
		credit3.merchantId = "5291bd8478a34c9ba73d1a40";
		credit3.reservationId = "529200ac78a34c9ba73d1a46";
		credit3.cost = true;
		credit3.jifen = 1;
		credit3.status = CreditStatus.finished;
		credit3.created = new Date();
		credit3.modified = new Date();
		credit3.save();
	}
}
