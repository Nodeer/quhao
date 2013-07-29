package com.withiter.models.account;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.withiter.common.Constants;
import com.withiter.models.BaseModel;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

public abstract class ReservationEntityDef extends BaseModel {
	public String accountId;
	public String merchantId;
	public int seatNumber;
	public int myNumber;
}
