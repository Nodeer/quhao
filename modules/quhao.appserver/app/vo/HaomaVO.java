package vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Paidui;

public class HaomaVO {

	public String merchantId;
	public Map<Integer, PaiduiVO> haomaVOMap = new HashMap<Integer, PaiduiVO>();
	
	public static HaomaVO build(Haoma haoma) {
		HaomaVO vo = new HaomaVO();
		vo.merchantId = haoma.merchantId;

		Iterator ite = haoma.haomaMap.keySet().iterator();
		while(ite.hasNext()){
			Integer key = (Integer)ite.next();
			Paidui p = haoma.haomaMap.get(key);
			PaiduiVO pvo = vo.new PaiduiVO();
			pvo.currentNumber = p.currentNumber;
			pvo.canceled = p.canceled;
			pvo.expired = p.expired;
			pvo.finished = p.finished;
			pvo.enable = p.enable;
			vo.haomaVOMap.put(key, pvo);
		}
		
		return vo;
	}
	
	public class PaiduiVO {
		public int currentNumber = 0;
		public int canceled = 0;
		public int expired = 0;
		public int finished = 0;
		public boolean enable = false;
	}
	
}
