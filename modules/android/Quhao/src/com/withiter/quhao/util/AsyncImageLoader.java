package com.withiter.quhao.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SDTool;

public class AsyncImageLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache;
	private static String TAG = AsyncImageLoader.class.getName();

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	/**
	 * load drawable only for topmerchant
	 * 
	 * @param imageUrl
	 * @param position
	 * @param imageCallback
	 * @return
	 */
	public Drawable loadDrawable(final String imageUrl, final int position,
			final ImageCallback imageCallback) {
		try {

			// get cached image from memory
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					return drawable;
				}
			}

			// get cached image from SD card
			if (SDTool.instance().SD_EXIST) {
				Drawable drawable = Drawable.createFromPath(ImageUtil
						.getInstance().getFilePath(imageUrl));
				if (null != drawable) {
					return drawable;
				}
			}

			// get image from URL
			// Drawable drawable = loadImageFromUrl(imageUrl);
			// imageCache.put(imageUrl, new SoftReference<Drawable>(
			// drawable));
			// return drawable;
			

			final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                        if (message != null && imageCallback != null) {
                                imageCallback.imageLoaded((Drawable) message.obj,
                                                imageUrl, position);
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
		return null;
	}

	/**
	 * load drawable from imageUrl
	 * 
	 * @param imageUrl
	 * @param imageCallback
	 * @return
	 */
	public Drawable loadDrawable(String imageUrl) {

		if(QuhaoConstant.test){
			imageUrl = imageUrl.replace("localhost", QuhaoConstant.HTTP_URL);
		}
		
		QuhaoLog.i(TAG, "imageUrl: " + imageUrl);
		
		// get cached image from memory
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}

		// get cached image from SD card
		Drawable drawable = null;
		if (SDTool.instance().SD_EXIST) {
			drawable = Drawable.createFromPath(ImageUtil.getInstance().getFilePath(
					imageUrl));
			if (null != drawable) {
				return drawable;
			}
		}
		drawable = loadImageFromUrl(imageUrl);
		imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
		return drawable;
	}

	public static Drawable loadImageFromUrl(String url) {
		URL picUrl;
		Drawable d = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			picUrl = new URL(url);
			conn = (HttpURLConnection) picUrl.openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.connect();
			// 获取图片大小
			int picSize = conn.getContentLength();
			is = conn.getInputStream();
			
			if(SDTool.instance().SD_EXIST && picSize < SDTool.instance().getSDFreeSize()){
				File file = ImageUtil.getInstance().saveFile(url, is);
				d = Drawable.createFromPath(file.getPath());
				return d;
			}
			
			d = Drawable.createFromStream(is, "image");
			return d;
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
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl,
				int position);
	}
}
