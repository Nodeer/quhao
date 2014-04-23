package com.withiter.quhao.util.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class PicUtil {
	private static final String TAG = "PicUtil";

	/**
	 * 根据一个网络连接(URL)获取bitmapDrawable图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static BitmapDrawable getfriendicon(URL imageUri) {

		BitmapDrawable icon = null;
		try {
			HttpURLConnection hp = (HttpURLConnection) imageUri
					.openConnection();
			icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
			hp.disconnect();// 关闭连接
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * 根据一个网络连接(String)获取bitmapDrawable图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static BitmapDrawable getcontentPic(String imageUri) {
		URL imgUrl = null;
		try {
			imgUrl = new URL(imageUri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		BitmapDrawable icon = null;
		try {
			HttpURLConnection hp = (HttpURLConnection) imgUrl.openConnection();
			icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
			hp.disconnect();// 关闭连接
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * 根据一个网络连接(URL)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static Bitmap getusericon(URL imageUri) {
		// 显示网络上的图片
		URL myFileUrl = imageUri;
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if(null != is)
			{
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			
			if(null != conn)
			{
				conn.disconnect();
			}
		}
		
		return bitmap;
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri, String roundedType) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			URL myFileUrl = new URL(imageUri);
			conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			if ("top".equals(roundedType)) {
				bitmap = getTopRoundedCornerBitmap(BitmapFactory
						.decodeStream(is), 5.0f);
			} else if ("left".equals(roundedType)) {
				bitmap = getLeftRoundedCornerBitmap(BitmapFactory
						.decodeStream(is), 5.0f);
			} else if ("rect".equals(roundedType)) {
				bitmap = BitmapFactory.decodeStream(is);
			} else {
				bitmap = getRoundedCornerBitmap(BitmapFactory.decodeStream(is),
						7.0f);
			}

			
			Log.i(TAG, "image download finished." + imageUri);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(null != is)
			{
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			
			if(null != conn)
			{
				conn.disconnect();
			}
		}
		return bitmap;
	}

	/**
	 * 下载图片 同时写道本地缓存文件中
	 * 
	 * @param context
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmapAndwrite(String imageUri) {
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			// 显示网络上的图片
			URL myFileUrl = new URL(imageUri);
			conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();

			is = conn.getInputStream();
			//TODO:wjzwjz file name problem --- cacheFile
			File cacheFile = FileUtil.getCacheFile(imageUri);
			if(null != cacheFile)
			{
				os = new FileOutputStream(cacheFile);
				Log.i(TAG, "write file to " + cacheFile.getCanonicalPath());

				byte[] buf = new byte[1024];
				int len = 0;
				// 将网络上的图片存储到本地
				while ((len = is.read(buf)) > 0) {
					os.write(buf, 0, len);
				}

				// 从本地加载图片
				bitmap = BitmapFactory.decodeFile(cacheFile.getCanonicalPath());
			}
			else
			{
				bitmap = BitmapFactory.decodeStream(is);
			}
			
			// String name = MD5Util.MD5(imageUri);

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if(null != is)
			{
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			
			if(null != os)
			{
				try {
					os.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			
			
			if(null != conn)
			{
				conn.disconnect();
			}
		}
		return bitmap;
	}

	public static boolean downpic(String picName, Bitmap bitmap) {
		boolean nowbol = false;
		try {
			File saveFile = new File(QuhaoConstant.IMAGES_SD_URL + picName
					+ ".png");
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			FileOutputStream saveFileOutputStream;
			saveFileOutputStream = new FileOutputStream(saveFile);
			nowbol = bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					saveFileOutputStream);
			saveFileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowbol;
	}

	public static void writeTofiles(Context context, Bitmap bitmap,
			String filename) {
		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(context.openFileOutput(
					filename, Context.MODE_PRIVATE));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将文件写入缓存系统中
	 * 
	 * @param filename
	 * @param is
	 * @return
	 */
	public static String writefile(Context context, String filename,
			InputStream is) {
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try {
			inputStream = new BufferedInputStream(is);
			outputStream = new BufferedOutputStream(context.openFileOutput(
					filename, Context.MODE_PRIVATE));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
		} catch (Exception e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return context.getFilesDir() + "/" + filename + ".jpg";
	}

	// 放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if (bitmap == null) {
			return null;
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	// 获得圆角图片的方法
	public static Bitmap getTopRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = null;
		if (bitmap == null) {
			return output;
		}
		output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight() + 5);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getLeftRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = null;
		if (bitmap == null) {
			return output;
		}
		output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth() + 5, bitmap
				.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// 获得带倒影的图片方法
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas
				.drawRect(0, height, width, height + reflectionGap,
						deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

}
