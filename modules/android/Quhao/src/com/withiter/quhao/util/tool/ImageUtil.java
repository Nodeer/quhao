package com.withiter.quhao.util.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import android.media.ExifInterface;
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
	
	//  update this singleton implementation due to unsafe thread problem
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
		if (FileUtil.hasSdcard()) {
			// 在sd卡上建立图片存放空间
			cacheDir = new File(Environment.getExternalStorageDirectory(),
					QuhaoConstant.IMAGES_SD_URL);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}
	}
	
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return degree;
		}
		return degree;
	}

	/**
	 * 质量压缩方法
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>50) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	/**
	 * 图片按比例大小压缩方法（根据路径获取图片并压缩）：
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
		
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}
	
	/**
	 * 图片按比例大小压缩方法（根据Bitmap图片压缩）：
	 * @param image
	 * @return
	 */
	public static Bitmap comp(Bitmap image) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出	
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8 ) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :
	            (int) Math.min(Math.floor(w / minSideLength),
	            Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
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
		if (FileUtil.hasSdcard() && cacheDir.exists()) {
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
	public static Bitmap decodeFile(String url, File f, boolean isScaleByWidth,
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

	public static Bitmap decodeFile(String path, int  minSideLength, int maxNumOfPixels) {
		Bitmap bitmap = null;
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path,opts);
			opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(path, opts); 
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return bitmap;
	}
}
