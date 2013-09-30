package vo;

import java.util.ArrayList;
import java.util.List;

import com.withiter.common.Constants;
import com.withiter.models.account.Reservation;

public class ReservationVO {

	public boolean tipKey = false;
	public String tipValue = "";
	
	public String accountId;
	public String merchantId;
	public int seatNumber;
	public int myNumber;
	public int beforeYou;
	public int currentNumber;
	public Constants.ReservationStatus status;
	
	public void build(Reservation r){
		this.accountId = r.accountId;
		this.merchantId = r.merchantId;
		this.myNumber = r.myNumber;
		this.seatNumber = r.seatNumber;
		this.status = r.status;
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
