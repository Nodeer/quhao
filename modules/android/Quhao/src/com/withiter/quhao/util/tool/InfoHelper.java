package com.withiter.quhao.util.tool;

import java.util.ArrayList;
import java.util.List;

import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.db.AccountInfoHelper;

import android.content.Context;
import android.net.ConnectivityManager;


public class InfoHelper
{
	/**
	 * ֻҪ������ݿ�������ݣ��ͱ�ʾ��¼��
	 * 
	 * @param mContext
	 * @return
	 */
	public static AccountInfo getAccountInfo(Context mContext) {
		List<AccountInfo> list = null;
		AccountInfoHelper accountDBHelper = new AccountInfoHelper(mContext);
		accountDBHelper.open();

		try {
			list = accountDBHelper.getAccountInfos();
		} finally {
			accountDBHelper.close();
		}
		return (list != null && list.size() != 0) ? list.get(0) : null;
	}

	public static boolean checkNetwork(Context context)
	{
		boolean flag = false;
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null)
		{
			flag = true;
		}
		
		return flag;
	}
}
