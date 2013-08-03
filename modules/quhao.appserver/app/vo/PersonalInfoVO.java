package vo;

import java.util.ArrayList;
import java.util.List;

import vo.account.LoginVO;

public class PersonalInfoVO
{

	public String msg;
	
	public int errorCode;
	
	public LoginVO loginVO;
	
	public List<ReservationVO> currentReservationVOs = new ArrayList<ReservationVO>();
	
	public List<MerchantVO> currentMerchantVOs = new ArrayList<MerchantVO>();
	
	public List<ReservationVO> historyntReservationVOs = new ArrayList<ReservationVO>();
	
	public List<MerchantVO> historyMerchantVOs = new ArrayList<MerchantVO>();

	public void build(LoginVO loginVO,
			List<ReservationVO> currentReservationVOs,
			List<MerchantVO> currentMerchantVOs,
			List<ReservationVO> histroytReservationVOs,
			List<MerchantVO> histroyMerchantVOs)
	{
		this.loginVO = loginVO;
		this.currentReservationVOs = currentReservationVOs;
		this.currentMerchantVOs = currentMerchantVOs;
		this.historyntReservationVOs = histroytReservationVOs;
		this.historyMerchantVOs = histroyMerchantVOs;
		
	}

}
