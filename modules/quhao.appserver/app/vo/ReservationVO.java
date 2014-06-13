package vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.withiter.common.Constants;
import com.withiter.models.account.Reservation;
import com.withiter.utils.DistanceUtils;

public class ReservationVO {
	public String id;
	public boolean tipKey = false;					// true means success, false means failure
	public String tipValue = "";
	public boolean isCommented = false;
	public String accountId;
	public String merchantId;
	public int seatNumber;
	public int myNumber;
	public int beforeYou;
	public long version;
	public int currentNumber;
	public Constants.ReservationStatus status;
	
	public String merchantName;
	
	public String merchantAddress;
	public String merchantImage;
	public Date created = new Date();
	//用户和商家之间距离
	public double distance;
	/**
	 * 检查优惠时间
	 */
	public boolean youhui;
	/**
	 * 大众点评评分
	 */
	public String dianpingFen;
	public float averageCost;
	public void build(Reservation r){
		this.id = r.id();
		this.accountId = r.accountId;
		this.merchantId = r.merchantId;
		this.myNumber = r.myNumber;
		this.seatNumber = r.seatNumber;
		this.status = r.status;
		this.isCommented=r.isCommented;
		this.created = r.created;
	}
	
	public void build(Reservation r, boolean youhui){
		build(r);
		this.youhui = youhui;
	}
	
	public static List<ReservationVO> build(List<Reservation> rList){
		List<ReservationVO> voList = new ArrayList<ReservationVO>();
		ReservationVO vo = null;
		for(Reservation r : rList){
			vo = new ReservationVO();
			vo.build(r);
			voList.add(vo);
		}
		return voList;
	}
}
