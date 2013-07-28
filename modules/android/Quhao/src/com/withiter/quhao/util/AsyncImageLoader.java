package com.withiter.quhao.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.withiter.quhao.util.tool.QuhaoConstant;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader
{

	private HashMap<String, SoftReference<Drawable>> imageCache;
	
	public AsyncImageLoader()
	{
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}
	
	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		// synchronized (imageUrl) {
		try {
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					return drawable;
				}
			}
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					if (message != null && imageCallback != null) {
						imageCallback.imageLoaded((Drawable) message.obj,
								imageUrl);
					}
				}
			};
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		URL picUrl;
		Drawable d = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			picUrl = new URL(url);
			conn = (HttpURLConnection) picUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.connect();
			// 获取图片大小
			int picSize = conn.getContentLength();

			// 如果图片大于限定大小，则不下载
			if (picSize > QuhaoConstant.ADVERTISE_PIC_MAX) {
				return d;
			}
			is = conn.getInputStream();
			d = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return d;
		} catch (IOException e) {
			e.printStackTrace();
			return d;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

	
}
