package com.withiter.quhao.util.tool;

import android.content.Context;
import android.widget.Toast;

public class CommonTool
{

	public static void hintDialog(Context context, String msg)
	{
		System.out.println("hintDialog: " + msg);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		
	}

}
