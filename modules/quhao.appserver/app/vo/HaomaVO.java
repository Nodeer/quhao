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
			pvo.currentWait = p.currentNumber;
			pvo.enable = p.enable;
			vo.haomaVOMap.put(key, pvo);
		}
		
		return vo;
	}
	
	public class PaiduiVO {
		public int currentWait = 0;
		public boolean enable = false;
	}
	
}
