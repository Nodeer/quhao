package com.withiter.quhao.util.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static int FILE_SIZE = 4 * 1024;
	private static String TAG = "FileUtil";

	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static boolean createPath(String path) {
		File f = new File(path);
		if (!f.exists()) {
			Boolean o = f.mkdirs();
			Log.i(TAG, "create dir:" + path + ":" + o.toString());
			return o;
		}
		return true;
	}

	public static boolean exists4ImageUrl(String imageUrl)
	{
		String fileName = getFileName(imageUrl);
		try {
			return exists(Environment.getExternalStorageDirectory().getCanonicalPath() + "/"
					+ QuhaoConstant.IMAGES_SD_URL + "/" + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean exists(String file) {
		return new File(file).exists();
	}

	public static File saveFile(String file, InputStream inputStream) {
		File f = null;
		OutputStream outSm = null;

		try {
			f = new File(file);
			String path = f.getParent();
			if (!createPath(path)) {
				Log.e(TAG, "can't create dir:" + path);
				return null;
			}

			if (!f.exists()) {
				f.createNewFile();
			}

			outSm = new FileOutputStream(f);
			byte[] buffer = new byte[FILE_SIZE];
			while ((inputStream.read(buffer)) != -1) {
				outSm.write(buffer);
			}
			outSm.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;

		} finally {
			try {
				if (outSm != null)
					outSm.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		Log.v(TAG, "[FileUtil]save file:" + file + ":"
				+ Boolean.toString(f.exists()));

		return f;
	}

	public static Drawable getImageDrawable(String file) {
		if (!exists(file))
			return null;
		try {
			InputStream inp = new FileInputStream(new File(file));
			return BitmapDrawable.createFromStream(inp, "img");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getImageBitmap(String path) {
		Bitmap bitmap = null;
		if (!exists4ImageUrl(path))
			return null;
		try {
			bitmap = ImageUtil.decodeFile(Environment.getExternalStorageDirectory().getCanonicalPath() + "/"
					+ QuhaoConstant.IMAGES_SD_URL + "/" + getFileName(path), -1, 128*128);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	public static String saveLogo(Context context)
	{
		String path = "";
		File cacheFile = null;
		InputStream is = null;
		FileOutputStream os = null;
		try {
			File dir = StorageUtils.getCacheDirectory(context);
			cacheFile = new File(dir, "logo.png");
			is = context.getAssets().open("logo.png");
//				File sdCardDir = Environment.getExternalStorageDirectory();
//				File dir = new File(sdCardDir.getCanonicalPath() + "/"
//						+ QuhaoConstant.IMAGES_SD_URL);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				
//				cacheFile = new File(dir, "logo.png");
			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			else
			{
				return cacheFile.getCanonicalPath();
			}
			
			os = new FileOutputStream(cacheFile);
			Log.i(TAG, "write file to " + cacheFile.getCanonicalPath());
			path = cacheFile.getCanonicalPath();
			byte[] buf = new byte[1024];
			int len = 0;
			// 将网络上的图片存储到本地
			while ((len = is.read(buf)) > 0) {
				os.write(buf, 0, len);
			}
			os.flush();
			Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir
					+ ",file:" + "logo.png");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "getCacheFileError:" + e.getMessage());
			return "";
		}finally
		{
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return path;
	}
	
	public static File getCacheFile(String imageUri) {
		File cacheFile = null;
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				String fileName = getFileName(imageUri);
				File dir = new File(sdCardDir.getCanonicalPath() + "/"
						+ QuhaoConstant.IMAGES_SD_URL);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				
				cacheFile = new File(dir, fileName);
				if (!cacheFile.exists()) {
//					cacheFile.createNewFile();
				}
				Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir
						+ ",file:" + fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "getCacheFileError:" + e.getMessage());
			return cacheFile;
		}

		return cacheFile;
	}

	public static String getFileName(String path) {
		int index = path.lastIndexOf("=");
		return path.substring(index + 1);
	}
}