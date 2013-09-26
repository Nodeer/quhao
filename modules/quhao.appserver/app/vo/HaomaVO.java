package vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Paidui;

public class HaomaVO {

	public String merchantId;
	public Map<Integer, PaiduiVO> haomaVOMap = new HashMap<Integer, PaiduiVO>();
	public List<PaiduiVO> paiduiVOList = new ArrayList<PaiduiVO>();
	
	public static HaomaVO build(Haoma haoma) {
		HaomaVO vo = new HaomaVO();
		vo.merchantId = haoma.merchantId;

		Iterator ite = haoma.haomaMap.keySet().iterator();
		while(ite.hasNext()){
			Integer key = (Integer)ite.next();
			Paidui p = haoma.haomaMap.get(key);
			PaiduiVO pvo = new PaiduiVO();
			pvo.numberOfSeat = key;
			pvo.currentNumber = p.currentNumber;
			pvo.maxNumber = p.maxNumber;
			pvo.canceled = p.canceled;
			pvo.expired = p.expired;
			pvo.finished = p.finished;
			pvo.enable = p.enable;
			vo.haomaVOMap.put(key, pvo);
			vo.paiduiVOList.add(pvo);
		}
		
		return vo;
	}
	
}
