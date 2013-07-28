package com.withiter.quhao.action;

public class LoginAction
{

	private final static String TABLENAME = "accountinfo";
	
	private final static String TAG = "LoginAction";
	
	private static LoginAction instance = new LoginAction();
	
	private LoginAction()
	{
		
	}
	
	public static LoginAction getInstance()
	{
		return instance;
	}

	public Object queryUserId()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
