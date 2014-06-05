package models.chat;

import com.google.code.morphia.annotations.Entity;

@Entity
public class MerchantPort extends MerchantPortEntigy {

	/**
	 * 根据mid查找MerchantPort
	 * @param mid
	 * @return
	 */
	public static MerchantPort findByMid(String mid) {
		MorphiaQuery q = MerchantPort.q();
		q.filter("mid", mid);
		return q.first();
	}
}
