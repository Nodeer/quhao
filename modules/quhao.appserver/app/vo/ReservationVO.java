package vo;

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
	public boolean valid;
	public Constants.ReservationStatus status;
	
	
	public void build(Reservation r){
		this.accountId = r.accountId;
		this.merchantId = r.merchantId;
		this.myNumber = r.myNumber;
		this.seatNumber = r.seatNumber;
		this.beforeYou = r.beforeYou;
		this.valid = r.valid;
		this.status = r.status;
	}
}
