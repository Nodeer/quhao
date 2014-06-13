package com.withiter.quhao.vo;

public class Paidui implements Comparable<Paidui> {
	public String seatNo;
	public Integer currentNumber = 0;
	public Integer canceled = 0;
	public Integer expired = 0;
	public Integer finished = 0;
	public Integer maxNumber;
	public boolean enable = false;
	public boolean isChecked = false;

	public Paidui(String seatNo, Integer currentNumber, Integer maxNumber,Integer canceled,
			Integer expired, Integer finished, boolean enable) {
		this.seatNo = seatNo;
		this.currentNumber = currentNumber;
		this.maxNumber = maxNumber;
		this.canceled = canceled;
		this.expired = expired;
		this.finished = finished;
		this.enable = enable;
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o instanceof Paidui) {
			Paidui paidui = (Paidui) o;
			if (null != paidui.seatNo && !"".equals(paidui.seatNo)) {
				if (seatNo.equals(paidui.seatNo)) {
					if (null != currentNumber) {
						if (currentNumber.equals(paidui.currentNumber)) {
							if (null != canceled) {
								if (canceled.equals(paidui.canceled)) {
									if (null != expired) {
										if (expired.equals(paidui.expired)) {
											if (null != finished) {
												if (finished
														.equals(paidui.finished)) {
													if (enable == paidui.enable) {
														return true;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

		}
		return flag;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + seatNo.hashCode();
		result = 37 * result + currentNumber.hashCode();
		result = 37 * result + canceled.hashCode();
		result = 37 * result + expired.hashCode();
		result = 37 * result + finished.hashCode();
		result = 37 * result + (enable ? 0 : 1);
		return result;
	}

	@Override
	public int compareTo(Paidui another) {
		return Integer.parseInt(seatNo) - Integer.parseInt(another.seatNo);
	}
}
