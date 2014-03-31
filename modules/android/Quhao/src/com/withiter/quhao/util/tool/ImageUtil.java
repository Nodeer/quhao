package com.withiter.quhao.util.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;

/**
 * 
 * 从服务器上获取的图片，存储在本地
 * 
 * 采用单例模式
 * 
 * @author ASUS
 * 
 */
public class ImageUtil {
	
	private static final String TAG = ImageUtil.class.getName();

	private static File cacheDir;
	private static ImageUtil instance;
	
	// TODO update this singleton implementation due to unsafe thread problem
	public static ImageUtil getInstance() {
		if (null == instance) {
			instance = new ImageUtil();
		}
		return instance;
	}
	
	public static ImageUtil getInstance(Context context) {
		if (instance == null) {
			instance = new ImageUtil();
		}
		return instance;
	}

	private ImageUtil() {
		// 判断SD卡是否存在
		if (SDTool.instance().SD_EXIST) {
			// 在sd卡上建立图片存放空间
			cacheDir = new File(Environment.getExternalStorageDirectory(),
					QuhaoConstant.IMAGES_SD_URL);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}
	}

	public File getFile(String imageUrl) {
		String fileName = imageUrl.split("\\?fileName=")[1];
		File file = new File(cacheDir, fileName);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public String getFilePath(String imageUrl) {
		QuhaoLog.d(TAG, "imageUrl : " + imageUrl);
		String path = cacheDir.getPath() + "/"
				+ imageUrl.split("\\?fileName=")[1];
		QuhaoLog.d(TAG, "file on sd:"+path);
		return path;
	}

	public File saveFile(String imageUrl, InputStream is) {
		QuhaoLog.i(TAG, "start to save image to SD card, the image url is : " + imageUrl);
		if (StringUtils.isNull(imageUrl)) {
			return null;
		}
		
		String fileName = imageUrl.split("\\?fileName=")[1];
//		String newFileName = DesUtils.byteArr2HexStr(fileName.getBytes());
		File file = null;
		try {
			file = new File(cacheDir, fileName);
			if (!file.exists()) {
				file.createNewFile();
				OutputStream os = new FileOutputStream(file);

				final int buffer_size = 1024;
				byte[] bytes = new byte[buffer_size];
				for (;;) {
					int count = is.read(bytes, 0, buffer_size);
					if (count == -1)
						break;
					os.write(bytes, 0, count);
				}
				os.close();
			}

		} catch (Throwable e) {
			Log.e(fileName, e.getMessage());
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 删除SD卡文件信息
	 */
	public void cleanPictureCache() throws IOException {
		/**
		 * 遍历所有的然后删除
		 */
		if (SDTool.instance().SD_EXIST && cacheDir.exists()) {
			File files[] = cacheDir.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {

					} else {
						f.delete();
					}
				}
			}
			// fFile.delete();
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
		{
			return null;
			// 缓存中不存在的场合
		}

		if (bitmap == null) {

			URL imgUrl;
			HttpURLConnection conn = null;
			InputStream is = null;
			if (bitmap == null && isNet) {
				// sdcard中不存在的场合
				// 从服务器获得bitmap对象
				try {
					// if (DEBUG) Log.w(TAG, "从网络下载["+rand+"]"+start);
					imgUrl = new URL(url);
					conn = (HttpURLConnection) imgUrl.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);

					is = conn.getInputStream();
					if(null!=is)
					{
						bitmap = BitmapFactory.decodeStream(is);
					}
					
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
				finally
				{
					if(null!=is)
					{
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(null!=conn)
					{
						conn.disconnect();
					}
				}
			}

		}
		return bitmap;
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
