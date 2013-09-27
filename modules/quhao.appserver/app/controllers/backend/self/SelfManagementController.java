package controllers.backend.self;

import java.io.File;
import java.io.IOException;
import java.util.List;

import vo.BackendMerchantInfoVO;
import vo.HaomaVO;
import cn.bran.japid.util.StringUtils;

import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.models.account.Account;
import com.withiter.models.account.Reservation;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;

import controllers.BaseController;
import controllers.MerchantController;
import controllers.UploadController;

public class SelfManagementController extends BaseController {
	/*
	 * 1) account information(included information:email or phone...) 2)
	 * Merchant information(included information:name, address...)
	 */

	/**
	 * 登录成功，通过uid查询出当前account的对应的merchant信息
	 * 
	 * @param uid
	 */
	public static void index(String uid) {
		Account account = Account.findById(uid);
		List<MerchantAccountRel> relList = MerchantAccountRel
				.getMerchantAccountRelList(uid);
		Merchant merchant = null;
		if (relList == null || relList.isEmpty()) {

		} else {
			MerchantAccountRel rel = relList.get(0);
			String mid = rel.mid;
			merchant = Merchant.findById(mid);
		}
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant,
				account);
		renderJapid(bmivo);
	}

	public static void editMerchant(String uid, String mid) {

		String merchantName = params.get("merchantName");
		String description = params.get("description");
		String address = params.get("address");
		String tel = params.get("tel");
		String cateType = params.get("cateType");
		String openTime = params.get("openTime");
		String closeTime = params.get("closeTime");
		String merchantImage = params.get("merchantImage");
		
		String[] seatType = params.getAll("seatType");
		for(int i=0; i< seatType.length; i++){
			System.out.print(seatType[i]+",");
		}

		System.out.println("==========");
		
		System.out.println(merchantName);
		System.out.println(address);
		System.out.println(tel);
		System.out.println(cateType);
		System.out.println(openTime);
		System.out.println(closeTime);
		System.out.println(merchantImage);

		if (StringUtils.isEmpty(mid)) {
			Merchant m = new Merchant();
			m.name = merchantName;
			m.description = description;
			m.address = address;
			m.telephone = tel.split(",");
			m.cateType = cateType;
			m.openTime = openTime;
			m.closeTime = closeTime;
			m.enable = true;
			m.seatType = seatType;
			m.save();
			if (!StringUtils.isEmpty(merchantImage)) {
				GridFSInputFile file = uploadFirst(merchantImage, m.id());
				if (file != null) {
					m.merchantImageSet.add(file.getFilename());
					m.save();
				}
			}

			MerchantAccountRel rel = new MerchantAccountRel();
			rel.mid = m.id();
			rel.uid = uid;
			rel.save();
			// BackendMerchantInfoVO bmivo =
			// BackendMerchantInfoVO.build(merchant, account);
			// renderJapidWith("japidviews.backend.self.SelfManagementController.index",
			// args);
		} else {
			Merchant m = Merchant.findById(mid);
			m.name = merchantName;
			m.description = description;
			m.address = address;
			m.telephone = tel.split(",");
			m.cateType = cateType;
			m.openTime = openTime;
			m.closeTime = closeTime;
			m.seatType = seatType;
			m.enable = true;
			m.save();
			if (!StringUtils.isEmpty(merchantImage)) {
				GridFSInputFile file = uploadFirst(merchantImage, m.id());
				if (file != null) {
					m.merchantImageSet.add(file.getFilename());
					m.save();
				}
			}
		}
		index(uid);
	}

	public static void goPaiduiPage() {
		String mid = params.get("mid");
		Haoma haoma = Haoma.findByMerchantId(mid);
		HaomaVO haomaVO = HaomaVO.build(haoma);
		renderJapid(haomaVO);
	}

	public static void goPersonalPage() {
		String aid = params.get("aid");
		System.out.println(aid);
	}
	
	public static void paiduiPageAutoRefresh(){
		String mid = params.get("mid");
		Haoma haoma = Haoma.findByMerchantId(mid);
		HaomaVO haomaVO = HaomaVO.build(haoma);
		renderJapidWith("japidviews.backend.self.SelfManagementController.goPaiduiPageRefresh", haomaVO);
	}
	
	/**
	 * finish one reservation by merchant
	 */
	public static void finishByMerchant(){
		String cNumber = params.get("currentNumber");
		String sNumber = params.get("seatNumber");
		String mid = params.get("mid");
		int currentNumber = Integer.parseInt(cNumber);
		int seatNumber = Integer.parseInt(sNumber);
		
		Reservation r = Reservation.findReservationFinishByMerchant(seatNumber, currentNumber, mid);
		if(r != null){
			Reservation.finish(r.id());
			renderJSON(true);
		}else{
			renderJSON(false);
		}
	}

	private static GridFSInputFile uploadFirst(String param, String mid) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				gfsFile = UploadController.saveBinary(file, mid);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}

}
