package vo.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vo.MerchantVO;

import com.withiter.common.Constants.MobileOSType;
import com.withiter.models.admin.MerchantAccount;

public class MerchantAccountVO {

	public String uid = "";
	public String email = "";
	public String password = "";
	public boolean enable = false;
	public Date lastLogin;
	
	public String error = "";
	
	public List<MerchantVO> mList = new ArrayList<MerchantVO>();
	public static MerchantAccountVO build(MerchantAccount account) {
		MerchantAccountVO avo = new MerchantAccountVO();
		avo.uid = account.id();
		avo.email = account.email;
		avo.enable = account.enable;
		avo.lastLogin = account.lastLogin;
		
		return avo;
	}
}
