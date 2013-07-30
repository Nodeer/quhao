package vo;

import com.withiter.models.account.Reservation;

public class ReservationVO {

	public boolean tipKey = false;
	public String tipValue = "";
	
	public String accountId;
	public String merchantId;
	public int seatNumber;
	public int myNumber;
	public boolean valid;
	
	public void build(Reservation r){
		this.accountId = r.accountId;
		this.merchantId = r.merchantId;
		this.myNumber = r.myNumber;
		this.seatNumber = r.seatNumber;
		this.valid = r.valid;
	}
}
