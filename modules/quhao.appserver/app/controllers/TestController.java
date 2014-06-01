package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notifiers.MailsController;
import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Haoma;

public class TestController extends BaseController {

	private static void newTestAccount() {

		MorphiaQuery q = Account.q();
		q.filter("nickname", "quhaotest");
		if (q.first() != null) {
			return;
		}

		Account a = null;
		for (int i = 0; i < 10000; i++) {
			a = new Account();
			a.phone = 10000000000l + (long) i + "";
			a.enable = true;
			a.jifen = 500;
			a.nickname = "quhaotest";
			a.save();
		}
	}

	/**
	 * 删除reservation，haoma，credit
	 */
	private static void clear() {
		String mid = "5367accb0cf2c147bc369a16";
		MorphiaQuery q = Reservation.q();
		q.filter("merchantId", mid);
		q.delete();

		MorphiaQuery qq = Haoma.q();
		qq.filter("merchantId", mid);
		qq.delete();

		MorphiaQuery qqq = Credit.q();
		qqq.filter("merchantId", mid);
		qqq.delete();
	}

	public static void quhaoConcurrentTest() {

		clear();
		newTestAccount();

		final String mid = "5367accb0cf2c147bc369a16";
		final int seatType = 2;

		MorphiaQuery q = Account.q();
		final List<Account> list = q.filter("nickname", "quhaotest").asList();
		final List<String> results = new ArrayList<String>();
		Thread t = new Thread() {
			@Override
			public void run() {
				for (int j = 0; j < 100; j++) {
					String url = "/nahao?accountId=" + list.get(j).id() + "&mid=" + mid + "&seatNumber=" + seatType;
					String result = CommonHTTPRequest.get(url);
					results.add(result);
				}
				super.run();
			}
		};
		Thread t1 = new Thread() {
			@Override
			public void run() {
				for (int j = 100; j < 200; j++) {
					String url = "/nahao?accountId=" + list.get(j).id() + "&mid=" + mid + "&seatNumber=" + seatType;
					String result = CommonHTTPRequest.get(url);
					results.add(result);
				}
				super.run();
			}
		};
		Thread t2 = new Thread() {
			@Override
			public void run() {
				for (int j = 200; j < 300; j++) {
					String url = "/nahao?accountId=" + list.get(j).id() + "&mid=" + mid + "&seatNumber=" + seatType;
					String result = CommonHTTPRequest.get(url);
					results.add(result);
				}
				super.run();
			}
		};
		Thread t3 = new Thread() {
			@Override
			public void run() {
				for (int j = 300; j < 400; j++) {
					String url = "/nahao?accountId=" + list.get(j).id() + "&mid=" + mid + "&seatNumber=" + seatType;
					String result = CommonHTTPRequest.post(url);
					results.add(result);
				}
				super.run();
			}
		};
		Thread t4 = new Thread() {
			@Override
			public void run() {
				for (int j = 400; j < 500; j++) {
					String url = "/nahao?accountId=" + list.get(j).id() + "&mid=" + mid + "&seatNumber=" + seatType;
					String result = CommonHTTPRequest.get(url);
					results.add(result);
				}
				super.run();
			}
		};

		t.start();
		t1.start();
		t2.start();
		t3.start();
		t4.start();

		try {
			t.join();
			t1.join();
			t2.join();
			t3.join();
			t4.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		renderJSON(results);

	}

	public static void mailtest() {
		MailsController.sendTo("mag_lee@126.com");
	}

	public static void list() {
		renderJapid();
	}

	public static void poi021() {
		renderJapid();
	}

	public static void poi020() {
		renderJapid();
	}

	public static void poi010() {
		renderJapid();
	}

	public static void poi0755() {
		renderJapid();
	}

	public static void m020() {
		renderJapid();
	}

	public static void m021() {
		renderJapid();
	}

	public static void m010() {
		renderJapid();
	}

	public static void m0755() {
		renderJapid();
	}

	public static void main(String[] args) {
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
