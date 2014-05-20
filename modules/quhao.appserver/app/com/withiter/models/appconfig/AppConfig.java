package com.withiter.models.appconfig;

import java.util.List;

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
}
