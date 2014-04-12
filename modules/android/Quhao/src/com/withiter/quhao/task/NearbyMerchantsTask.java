package com.withiter.quhao.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.withiter.quhao.util.ActivityUtil;

/**
 * 附近商家中查询商家
 * @author Jazze
 *
 */
public class NearbyMerchantsTask extends AsyncTask<Runnable, Void, PoiResult> {
	
	// 进度提示框
	private static ProgressDialog progressDialog = null;
	// 进度提示文字
	private int preDialogMessage = 0;

	private Runnable successRunnable = null;
	// error处理
	private Runnable errorRunnable = null;

	protected Context mContext = null;
	
	private PoiSearch poiSearch;
	
	public PoiResult poiResult;

	public NearbyMerchantsTask(int preDialogMessage, Context context,
			PoiSearch poiSearch) {
		this.preDialogMessage = preDialogMessage;
		this.mContext = context;
		this.poiSearch = poiSearch;
		
	}
	
	/**
	 * 该方法将在执行实际的后台操作前被UI thread调用。
	 * 
	 * 可以在该方法中做一些准备工作，如在界面上显示一个进度条
	 */
	@Override
	protected void onPreExecute() {
		if (preDialogMessage > 0) {
			progressDialog = new ProgressDialog(mContext);
			progressDialog.setTitle("");
			progressDialog.setMessage(mContext.getResources().getString(preDialogMessage));
			progressDialog.setIndeterminate(true);
			if (mContext != null && !((Activity) mContext).isFinishing()) {
				progressDialog.show();
			}
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							NearbyMerchantsTask.this.cancel(true);
						}
					});
		}

	}

	@Override
	protected PoiResult doInBackground(Runnable... runnables) {
		PoiResult result = null;
		try {
			if (!ActivityUtil.isNetWorkAvailable(mContext)) {
				return result;
			}
			long start = System.currentTimeMillis();
			// 需要返回值的场合，获得json数据
			poiResult = poiSearch.searchPOI();
			long end = System.currentTimeMillis();
			
			Log.e("wjzwjz : ", "the date : " + (end-start));
			
		} catch (Exception e) {
			Log.e("BaseTask", e.getMessage(), e);
			return result;
		} finally {
			successRunnable = runnables[0];
			if (runnables.length > 1) {
				errorRunnable = runnables[1];
			}
		}
		return result;
	}

	/**
	 * 在doInBackground 执行完成后，onPostExecute方法将被UI thread调用
	 * 
	 * 后台的计算结果将通过该方法传递到UI thread.
	 */
	@Override
	protected void onPostExecute(PoiResult result) {
		closeProgressDialog();
		if (!this.isCancelled()) {
			if (null != poiResult && null != poiResult.getQuery()) {
				successRunnable.run();
			} else {
				errorRunnable.run();
			}
		}
	}

	// 关闭进度提示
	public void closeProgressDialog() {
		if (preDialogMessage > 0 && progressDialog.isShowing()) {
			if (mContext != null && !((Activity) mContext).isFinishing()) {
				progressDialog.dismiss();
			}
		}
	}
}
