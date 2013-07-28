package com.withiter.quhao.util.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.withiter.quhao.vo.Category;

public class ParseJson
{

	public static Collection<? extends Category> getCategorys(String buf)
	{
		List<Category> categroys = new ArrayList<Category>();
		
		if(null == buf || "".equals(buf))
		{
			return categroys;
		}
		
		try
		{
			JSONArray jsonArrays = new JSONArray(buf);

			for (int i = 0; i < jsonArrays.length(); i++)
			{
				JSONObject obj = jsonArrays.getJSONObject(i);
				long count = 0L;
				String categoryType = "";
				String url = "";
				if(obj.has("count"))
				{
					count = Long.valueOf(obj.getString("count"));
				}
				if(obj.has("cateType"))
				{
					categoryType = obj.getString("cateType");
				}
				
				if(obj.has("url"))
				{
					url = obj.getString("url");
				}
				
				Category category = new Category(count, categoryType,url);
				categroys.add(category);
			}
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return categroys;
	}

}
