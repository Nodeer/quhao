package com.withiter.quhao.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

public class IOUtils {
	private static final int BUFFER_SIZE = 1024; // 流转换的缓存大小
	private static final int CONNECT_TIMEOUT = 3000; // 从网络下载文件时的连接超时时间

	/**
	 * 从Assets读取文字
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String readStringFromAssets(Context context, String fileName) {
		return readStringFromAssets(context, fileName, "UTF-8");
	}

	/**
	 * 
	 * 从Assets读取文字
	 * 
	 * @param context
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static String readStringFromAssets(Context context, String fileName,
			String encoding) {
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = context.getAssets().open(fileName);
			byte[] buffer = new byte[BUFFER_SIZE];

			baos = new ByteArrayOutputStream();
			while (true) {
				int read = is.read(buffer);
				if (read == -1) {
					break;
				}
				baos.write(buffer, 0, read);
			}
			String result = baos.toString(encoding);
			return result;
		} catch (Exception e) {
			return "";
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从资源中读取文字
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static String readStringFromRes(Context context, int resId) {
		return readStringFromRes(context, resId, "UTF-8");
	}

	/**
	 * 从资源中读取文字
	 * 
	 * @param context
	 * @param resId
	 * @param encoding
	 * @return
	 */
	public static String readStringFromRes(Context context, int resId,
			String encoding) {
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = context.getResources().openRawResource(resId);
			byte[] buffer = new byte[BUFFER_SIZE];

			baos = new ByteArrayOutputStream();
			while (true) {
				int read = is.read(buffer);
				if (read == -1) {
					break;
				}
				baos.write(buffer, 0, read);
			}
			String result = baos.toString(encoding);
			return result;
		} catch (Exception e) {
			return "";
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从指定路径的文件中读取Bytes
	 */
	public static byte[] readBytes(String path) {
		File file = new File(path);
		return readBytes(file);
	}

	/**
	 * 从指定资源中读取Bytes
	 */
	public static byte[] readBytes(Context context, int resId) {
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(resId);
			return readBytes(is);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 从File中读取Bytes
	 */
	public static byte[] readBytes(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return readBytes(fis);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 从Url中读取Bytes
	 */
	public static byte[] readBytes(URL url) {
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.connect();
			is = conn.getInputStream();
			return readBytes(is);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (conn != null) {
					conn.disconnect();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 从InputStream中读取Bytes
	 */
	public static byte[] readBytes(InputStream is) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = 0;
			while ((length = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
				baos.write(buffer, 0, length);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 将InputStream写入File
	 */
	public static boolean writeToFile(File file, InputStream is) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = 0;
			while ((length = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
				fos.write(buffer, 0, length);
				fos.flush();
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static boolean writeToFile(File file, String text) {
		return writeToFile(file, text, "UTF-8");
	}

	public static boolean writeToFile(File file, String text, String encoding) {
		try {
			return writeToFile(file, text.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	public static boolean writeToFile(File file, byte[] buffer) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, true);
			fos.write(buffer);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static boolean writeToSD(String fileName, String text) {
		try {
			String strPath = Environment.getExternalStorageDirectory() + "/"
					+ fileName;
			File fFile = new File(strPath);
			if (!fFile.exists()) {
				fFile.createNewFile();
			}
			return writeToFile(fFile, text);
		} catch (IOException e) {
			return false;
		}
	}

	public static InputStream readFromSD(String dirPath, String saveName) {
		String strPath = Environment.getExternalStorageDirectory() + "/"
				+ dirPath;
		File f = new File(strPath, saveName);
		if (f.exists()) {
			try {
				return new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 下载文件至存储卡
	 */
	public static File downloadFileToSD(String strUrl, String dirPath) {
		return downloadFile(strUrl, android.os.Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ dirPath, null);
	}

	/**
	 * 下载文件至存储卡
	 */
	public static File downloadFileToSD(String strUrl, String dirPath,
			String saveName) {
		return downloadFile(strUrl, android.os.Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ dirPath, saveName);
	}

	/**
	 * 下载文件至指定目录
	 */
	public static File downloadFile(String strUrl, String dirPath) {
		return downloadFile(strUrl, dirPath, null);
	}

	/**
	 * 下载文件至指定目录
	 * 
	 * @param strUrl
	 *            文件的url
	 * @param dirPath
	 *            存储文件的目录
	 * @param saveName
	 *            存储的文件名
	 */
	public static File downloadFile(String strUrl, String dirPath,
			String saveName) {
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			String fileEx = strUrl.substring(strUrl.lastIndexOf(".") + 1,
					strUrl.length()).toLowerCase();
			String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1,
					strUrl.lastIndexOf("."));

			URL myURL = new URL(strUrl);
			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.connect();
			is = conn.getInputStream();

			if (saveName == null) {
				saveName = fileName + "." + fileEx;
			}
			File file = new File(dirPath, saveName);
			writeToFile(file, is);
			return file;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (conn != null) {
					conn.disconnect();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 保存文件至内存 data/data/packagename/file
	 */
	public static boolean writeToCache(Context context, String fileName,
			InputStream is) {
		fileName = String.valueOf(fileName.hashCode());
		FileOutputStream outStream = null;
		try {
			outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = 0;
			while ((length = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
				outStream.write(buffer, 0, length);
				outStream.flush();
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 保存文件至内存 data/data/packagename/file
	 */
	public static boolean writeToCache(Context context, String fileName,
			String text) {
		fileName = String.valueOf(fileName.hashCode());
		FileOutputStream outStream = null;
		try {
			outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] buffer = text.getBytes("UTF-8");
			outStream.write(buffer);
			outStream.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 读取内存中文件 data/data/packagename/file
	 */
	public static byte[] readByCache(Context context, String fileName) {
		fileName = String.valueOf(fileName.hashCode());

		FileInputStream inStream = null;
		try {
			inStream = context.openFileInput(fileName);
			return readBytes(inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static JSONObject fromByteToJson(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		} else {
			String jsonString;
			JSONObject job = null;
			try {
				jsonString = new String(bytes, "UTF-8");
				job = new JSONObject(jsonString);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

			return job;
		}
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			// Log.e("CopyStream出错:[池负载："+ImageUtil.poolLoad+"]","--->",ex);
		}
	}

	/**
	 * 删除内存资源
	 */
	public static void deleteCache(Context context, String fileName) {
		fileName = String.valueOf(fileName.hashCode());

		File file = context.getFileStreamPath(fileName);
		if (file != null) {
			file.delete();
		}
	}

	/**
	 * 将Assets的数据库文件转移到data/data/packagename/database中
	 */
	public static void AssetsToDataBase(Context context) {
		String dbDirPath = "/data/data/com.withiter.quhao.android/databases";

		// 打开静态数据库文件的输入流
		InputStream is;
		try {
			is = context.getAssets().open("csbus.db");
			// 通过Context类来打开目标数据库文件的输出流，这样可以避免将路径写死。
			FileOutputStream os = new FileOutputStream(dbDirPath + "/csbus.db");
			byte[] buffer = new byte[1024];
			int count = 0;
			// 将静态数据库文件拷贝到目的地
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
