package com.withiter.quhao.util.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

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
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 在sd卡上建立图片存放空间
			cacheDir = new File(Environment.getExternalStorageDirectory(),
					QuhaoConstant.IMAGES_SD_URL);

			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}
	}

	public File getFile(String imageUrl) {
		String fileName = String.valueOf(imageUrl.hashCode());
		File file = new File(cacheDir, fileName);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public String getFilePath(String imageUrl) {
		String path = cacheDir.getPath() + "/"
				+ String.valueOf(imageUrl.hashCode());
		return path;
	}

	public File saveFile(String imageUrl, InputStream is) {
		String fileName = String.valueOf(imageUrl.hashCode());
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}
