package com.withiter.quhao.task;

import java.net.SocketTimeoutException;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;

public abstract class BaseTask extends AsyncTask<Runnable, Void, JsonPack> {

	// 进度提示框
	private static ProgressDialog progressDialog = null;
	// 进度提示文字
	private int preDialogMessage = 0;

	// 是否需要返回值
	private boolean isReturn = true;
	// error处理
	private Runnable errorRunnable = null;

	protected Context mContext = null;
	
	public JsonPack result;

	// 显示进度提示
	public BaseTask(int preDialogMessage, Context context) {
		this.preDialogMessage = preDialogMessage;
		this.mContext = context;
		this.isReturn = true;
	}

	// 不显示进度提示
	public BaseTask(Context context) {
		this.mContext = context;
		this.isReturn = true;
	}

	public BaseTask(Context context, boolean isReturn) {
		this.mContext = context;
		this.isReturn = isReturn;
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
			progressDialog.setMessage(getString(preDialogMessage));
			progressDialog.setIndeterminate(true);
			if (mContext != null && !((Activity) mContext).isFinishing()) {
				progressDialog.show();
			}
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							BaseTask.this.cancel(true);
						}
					});
		}

		onPreStart();
	}

	/**
	 * 执行那些很耗时的后台计算工作。 可以调用publishProgress方法来更新实时的任务进度
	 */
	@Override
	protected JsonPack doInBackground(Runnable... runnables) {
		JsonPack result = new JsonPack();
		try {
			if (!ActivityUtil.isNetWorkAvailable(mContext)) {
				result.setRe(400);
				result.setMsg(mContext.getString(R.string.text_no_network));
				return result;
			}
			if (isReturn) {
				// 需要返回值的场合，获得json数据
				result = getData();
				if (result.getRe() == 200) {
					// 设置回调函数
					if (runnables.length > 0) {
						result.setCallBack(runnables[0]);
					}
				}
			} else {
				getData();
				if (runnables.length > 0) {
					result.setCallBack(runnables[0]);
				}
			}
		} catch (SocketTimeoutException e) {
			result.setRe(400);
			result.setMsg(mContext.getString(R.string.text_timeout_error));
			return result;
		} catch (JSONException e) {
			result.setRe(400);
			result.setMsg(e.getMessage());
			return result;
		} catch (Exception e) {
			Log.e("BaseTask", e.getMessage(), e);
			result.setRe(400);
			result.setMsg(mContext.getString(R.string.text_error_net));
			return result;
		} finally {
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
	protected void onPostExecute(JsonPack result) {
		closeProgressDialog();
		this.result = result;
		if (!this.isCancelled()) {
			if (result.getRe() == 200) {
				onStateFinish(result);
				if (result.getCallBack() != null) {
					result.getCallBack().run();
				}
			} else {
				onStateError(result);
				if (errorRunnable != null) {
					errorRunnable.run();
				}
			}
		}
	}

	/**
	 * 获取数据
	 */
	abstract public JsonPack getData() throws Exception;

	/**
	 * 正常结束，调用回调函数
	 */
	abstract public void onStateFinish(JsonPack result);

	/**
	 * error
	 */
	abstract public void onStateError(JsonPack result);

	/**
	 * 
	 */
	abstract public void onPreStart();

	// 关闭进度提示
	public void closeProgressDialog() {
		if (preDialogMessage > 0 && BaseTask.progressDialog.isShowing()) {
			if (mContext != null && !((Activity) mContext).isFinishing()) {
				BaseTask.progressDialog.dismiss();
			}
		}
	}

	// 得到资源文件的值
	public String getString(int resId) {
		return mContext.getResources().getString(resId);
	}

}
