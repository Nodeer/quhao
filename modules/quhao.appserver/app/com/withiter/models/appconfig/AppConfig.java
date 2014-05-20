package com.withiter.models.appconfig;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;

@Entity
public class AppConfig extends AppConfigEntityDef {
	public static AppConfig android(){
		MorphiaQuery q = AppConfig.q();
		q.filter("type", "android");
		return q.first();
	}
	
	public static List<AppConfig> allConfig(){
		MorphiaQuery q = AppConfig.q();
		return q.asList();
	}

	/**
	 * 更新version
	 * @param id
	 * @param version
	 */
	public static void update(String id, String version) {
		MorphiaQuery q = AppConfig.q();
		q.filter("_id", new ObjectId(id));
		AppConfig config = q.first();
		if(config != null){
			config.version = version;
			config.save();
		}
	}
}
