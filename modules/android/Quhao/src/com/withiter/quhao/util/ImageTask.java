package com.withiter.quhao.util;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ImageUtil;

public class ImageTask extends AsyncTask<Runnable, Void, Bitmap> {
	private Runnable mRunnable;

	private ImageView mImageView;
	private String mUrl;
	private boolean mIsSmall;
	private Context mContext;

	private int mWidth;
	public SoftReference<Bitmap> bitmap;

	public ImageTask(ImageView iamgeView, String url, boolean isSmall,
			Context context) {
		mImageView = iamgeView;
		mUrl = url;
		mIsSmall = isSmall;
		mContext = context;
	}

	/**
	 * 该方法将在执行实际的后台操作前被UI thread调用。 可以在该方法中做一些准备工作，如在界面上显示一个进度条
	 */
	@Override
	protected void onPreExecute() {
		if (mIsSmall) {
			mWidth = mImageView.getMeasuredWidth();
			mImageView.setImageResource(R.drawable.no_logo);
		} else {
			mWidth = ActivityUtil.getWindowsPixels((Activity) mContext).widthPixels;
			mImageView.setImageResource(R.drawable.no_logo);
		}
	}

	/**
	 * 执行那些很耗时的后台计算工作。 可以调用publishProgress方法来更新实时的任务进度
	 */
	@Override
	protected Bitmap doInBackground(Runnable... runnables) {
		Bitmap bitmap = null;
		bitmap = ImageUtil.getInstance(mContext).getBitmap(mUrl, true, mWidth);
		mRunnable = runnables[0];

		return bitmap;
	}

	/**
	 * 在doInBackground 执行完成后，onPostExecute 方法将被UI thread调用 后台的计算结果将通过该方法传递到UI
	 * thread.
	 */
	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			mImageView.setImageBitmap(result);
			bitmap = new SoftReference<Bitmap>(result);
		}
		mRunnable.run();
	}

}
