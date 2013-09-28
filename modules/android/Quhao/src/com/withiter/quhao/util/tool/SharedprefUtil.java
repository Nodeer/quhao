package com.withiter.quhao.util.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 手机应用的共享数据
 * 
 * @author ASUS
 *
 */
public class SharedprefUtil {
	
	private static final String CONFIG_CACHE = "QUHAO_CACHE";
	
	/**
	 * 
	 * 根据key移除共享属性
	 * 
	 * @param context Context
	 * @param key key
	 */
	public static void remove(Context context, String key)
	{
		SharedPreferences settings = context.getSharedPreferences(CONFIG_CACHE, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 共享数据中存储数据
	 * 
	 * @param context Context
	 * @param key key 
	 * @param value value
	 */
	public static void put(Context context, String key, String value)
	{
		SharedPreferences settings = context.getSharedPreferences(CONFIG_CACHE, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * 共享数据中获取数据
	 * 
	 * @param context Context
	 * @param key key
	 */
	public static String get(Context context, String key,String defaultValue)
	{
		SharedPreferences settings = context.getSharedPreferences(CONFIG_CACHE, Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}
	
	/**
	 * 清楚共享数据
	 * 
	 * @param context
	 */
	public static void clear(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(CONFIG_CACHE, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}

}
