package com.withiter.models.account;

public class CooperationRequset extends CooperationRequsetEntityDef{
	
	public CooperationRequset(String companyName, String peopleName, String peopleContact, String peopleEmail){
		this.companyName = companyName;
		this.peopleName = peopleName;
		this.peopleContact = peopleContact;
		this.peopleEmail = peopleEmail;
	}
	
	public CooperationRequset(){
		
	}
}
