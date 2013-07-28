package com.withiter.quhao.util.tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

public class ProgressDialogUtil
{
	
	private ProgressDialog pd;
	
	public ProgressDialogUtil(final Context context, int title, int res,
			final boolean permitClose)
	{
		pd = new ProgressDialog(context)
		{

			@Override
			public boolean dispatchKeyEvent(KeyEvent event)
			{
				
				if(event.getAction() == KeyEvent.ACTION_DOWN)
				{
					if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
					{
						if(!permitClose)
						{
							return true;
						}
					}
					else if(event.getKeyCode() != KeyEvent.KEYCODE_HOME)
					{
						return true;
					}
				}
				
				return super.dispatchKeyEvent(event);
			}
			
		};
		pd.setTitle(title);
		pd.setMessage(context.getResources().getText(res));
		
	}

	public void setMessage(String message)
	{
		if(null != pd)
		{
			pd.setMessage(message);
		}
	}
	
	public void setTitle(String title)
	{
		if(null != pd)
		{
			pd.setTitle(title);
		}
	}
	
	public void showProgress()
	{
		if(null != pd && !pd.isShowing())
		{
			try
			{
				pd.show();
			} catch (Exception e)
			{
				Log.e("ProgressDialogUtil", "show progress error");
			}
		}
	}
	
	public void closeProgress()
	{
		if (pd != null && pd.isShowing()) {
			try {
				pd.dismiss();
			} catch (Exception e) {
				Log.e("ProgressDialogUtil", "close progress error");
			}
		}
	}
}
