package com.withiter.quhao.util.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.withiter.quhao.activity.GetNumberActivity;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.encrypt.DesUtils;

import android.os.Environment;
import android.util.Log;

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
}
