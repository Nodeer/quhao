package com.withiter.quhao.util.tool;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class SDTool {

	private static SDTool tool = null;
	// 检查SD卡是否挂载
	public boolean SD_EXIST = false;

	private SDTool() {

	}

	public static SDTool instance() {
		if (tool == null) {
			synchronized (SDTool.class) {
				if (tool == null) {
					tool = new SDTool();
					tool.SD_EXIST = Environment.MEDIA_MOUNTED
							.equals(Environment.getExternalStorageState());
				}
			}
		}
		return tool;
	}

	public long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		return freeBlocks * blockSize; // 单位Byte
	}

	public long getSDAllSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 获取所有数据块数
		long allBlocks = sf.getBlockCount();
		// 返回SD卡大小
		return allBlocks * blockSize; // 单位Byte
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}
