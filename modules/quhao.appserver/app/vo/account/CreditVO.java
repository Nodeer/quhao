package vo.account;

import java.util.Date;

import com.withiter.models.account.Credit;

public class CreditVO {

	public String id;
	/**
	 * 用户帐号ID
	 */
	public String accountId;
	
	/**
	 * 酒店ID
	 */
	public String merchantId;
	
	/**
	 * 酒店名称
	 */
	public String merchantName;
	
	/**
	 * 酒店地址
	 */
	public String merchantAddress;
	
	/**
	 * 关联的预定的Id
	 */
	public String reservationId;
	
	/**
	 * 座位类型
	 */
	public int seatNumber;
	
	/**
	 * 我的座位号码
	 */
	public int myNumber;
	
	/**
	 * true代表增加积分， false代表减少积分
	 */
	public boolean cost;
	
	/**
	 * 增加或减少积分
	 */
	public int jifen;
	
	/**
	 * 积分消费状态 finished, getNumber
	 */
	public String status;
	
	public Date created = new Date();

	public void build(Credit credit) {
		this.id = credit.id();
		this.accountId = credit.accountId;
		this.merchantId = credit.merchantId;
		this.reservationId = credit.reservationId;
		this.cost = credit.cost;
		this.jifen=credit.jifen;
		this.created = credit.created;
		this.status = credit.status.toString();
	}

}
