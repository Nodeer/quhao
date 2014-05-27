package com.withiter.quhao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.withiter.quhao.util.tool.QuhaoConstant;

public class ImageUtil {

	// 图片缓存
	private HashMap<String, SoftReference<Bitmap>> mImageViewReference;

	//
	// 图片载入timeout时间
	private static final int TIME_OUT = 5000;

	private File cacheDir;
	private static Context mContext;
	private static ImageUtil instance;

	public static ImageUtil getInstance(Context context) {
		mContext = context;
		if (instance == null) {
			instance = new ImageUtil();
		}
		return instance;
	}

	private ImageUtil() {

		mImageViewReference = new HashMap<String, SoftReference<Bitmap>>();

		// 判断存储卡是否存在
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// 在sd卡上建立图片存放空间
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					QuhaoConstant.IMAGES_SD_URL);
			File folder = cacheDir.getParentFile();
			if (!folder.exists()) {
				folder.mkdirs();
			}
		} else {
			// 如没有存储卡，则在私有存储路径中开辟空间
			cacheDir = mContext.getCacheDir();
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	/**
	 * 获得bitmap对象
	 */
	public Bitmap getBitmap(String url, boolean isScaleByWidth,
			int widthOrHeight) {
		return getBitmap(url, isScaleByWidth, widthOrHeight, true);
	}

	/**
	 * 获得图片
	 * 
	 * @param url
	 * @param isScaleByWidth
	 * @param widthOrHeight
	 * @param isNet
	 *            没有网络不需要下载
	 * @return
	 */
	public Bitmap getBitmap(String url, boolean isScaleByWidth,
			int widthOrHeight, boolean isNet) {

		Bitmap bitmap = null;

		if (TextUtils.isEmpty(url))
			return null;
		// 缓存中不存在的场合
		if (mImageViewReference.containsKey(url))
			bitmap = mImageViewReference.get(url).get();

		if (bitmap == null) {
			// 从sdcard中获得bitmap对象
			String filename = String.valueOf(url.hashCode());
			File f = new File(cacheDir, filename);
			// from SD cache
			if (f.exists()) {
				bitmap = decodeFile(url, f, isScaleByWidth, widthOrHeight);
			} else {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			URL imgUrl;
			HttpURLConnection conn = null;
			if (bitmap == null && isNet) {
				// sdcard中不存在的场合
				// 从服务器获得bitmap对象
				PatchInputStream pis = null;
				try {
					// if (DEBUG) Log.w(TAG, "从网络下载["+rand+"]"+start);
					imgUrl = new URL(url);
					conn = (HttpURLConnection) imgUrl.openConnection();
					conn.setConnectTimeout(TIME_OUT);
					conn.setReadTimeout(TIME_OUT);

					InputStream is = conn.getInputStream();
					pis = new PatchInputStream(is);
					OutputStream os = new FileOutputStream(f);
					IOUtils.CopyStream(pis, os);
					os.close();
					bitmap = decodeFile(url, f, isScaleByWidth, widthOrHeight);
				} catch (SocketTimeoutException e) {
					bitmap = null;
				} catch (Exception ex) {
					try {
					} catch (Exception e) {

					}
					if (bitmap != null) {
						bitmap = null;
					}
				}
			}

			if (bitmap != null) {
				mImageViewReference.put(url, new SoftReference<Bitmap>(bitmap));
			}
		}
		return bitmap;
	}

	public InputStream getGifInputStream(String url, boolean isNet) {
		InputStream inputStream = null;
		if (TextUtils.isEmpty(url))
			return null;

		// 从sdcard中获得inputStream
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		// from SD cache
		if (f.exists()) {
			try {
				inputStream = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		URL imgUrl;
		HttpURLConnection conn = null;
		if (inputStream == null && isNet) {
			// sdcard中不存在的场合
			// 从服务器获得bitmap对象
			PatchInputStream pis = null;
			try {
				// if (DEBUG) Log.w(TAG, "从网络下载["+rand+"]"+start);
				imgUrl = new URL(url);
				conn = (HttpURLConnection) imgUrl.openConnection();
				conn.setConnectTimeout(TIME_OUT);
				conn.setReadTimeout(TIME_OUT);

				InputStream is = conn.getInputStream();
				pis = new PatchInputStream(is);
				OutputStream os = new FileOutputStream(f);
				IOUtils.CopyStream(pis, os);
				os.close();
				inputStream = new FileInputStream(f);
			} catch (SocketTimeoutException e) {
				inputStream = null;
			} catch (Exception ex) {
				try {
				} catch (Exception e) {

				}
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return inputStream;
	}

	/**
	 * 清除图片缓存
	 */
	public void cleanCache() {
		Set<String> keys = mImageViewReference.keySet();
		Iterator<String> key_iterator = keys.iterator();

		while (key_iterator.hasNext()) {
			SoftReference<Bitmap> soft = mImageViewReference.get(key_iterator
					.next());
			soft.clear();
		}

	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @return
	 */
	private Bitmap decodeFile(String url, File f, boolean isScaleByWidth,
			int widthOrHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// 获取这个图片的宽和高
			bitmap = BitmapFactory.decodeFile(f.getPath(), options); // 此时返回bm为空
			options.inJustDecodeBounds = false;
			// 计算缩放比
			float scale = 0;
			if (widthOrHeight != 0) {
				// if (DEBUG) Log.d(TAG, "make thumbnail");
				if (isScaleByWidth) {
					scale = options.outWidth / (float) widthOrHeight;
				} else {
					scale = options.outHeight / (float) widthOrHeight;
				}
			} else {
				// if (DEBUG) Log.d(TAG, "use org");
			}
			if (scale > 1.5 && scale < 2)
				scale = 2;
			if (scale <= 1)
				scale = 1;

			options.inSampleSize = (int) scale;
			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦

			bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null,
					options);
		} catch (OutOfMemoryError e) {
			System.gc();
			// cleanCache();
			if (bitmap != null) {
				bitmap = null;
			}
		} catch (Exception e) {
			System.gc();
			// cleanCache();
			if (bitmap != null) {
				bitmap = null;
			}
		}

		return bitmap;
	}

}
