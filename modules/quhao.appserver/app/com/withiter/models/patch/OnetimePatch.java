package com.withiter.models.patch;

import java.util.List;

import play.Logger;
import play.Play;
import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.models.BaseModel;

public abstract class OnetimePatch extends BaseModel {
	public boolean isExecuted;

	public static void registerAllOnetimePatch() {
		for (Class c : Play.classloader.getAllClasses()) {
			if (OnetimePatch.class.equals(c.getSuperclass())) {
				Logger.info("the class %s will be registed ", c.getSuperclass());
				register(c);
			} else {
//				Logger.info("the class %s already be patched ", c.getSuperclass());
			}
		}
	}

	public static void register(Class c) {
		try {
			MorphiaQuery q = (MorphiaQuery) c.getMethod("q").invoke(null);
			if (q.first() == null) {
				OnetimePatch patch = (OnetimePatch) c.newInstance();
				patch.save();
				Logger.info("Registed patch job %s", c);
			}
		} catch (Exception e) {
			Logger.error(e, "Failed to register patch job %s", c);
		}
	}

	public void execute() {
		Logger.info("Start patch job %s", this.getClass());
		try {
			run();
			isExecuted = true;
			save();
			Logger.info("Finished patch job %", this.getClass());
		} catch (Exception e) {
			Logger.error(e, "Failed patch job %s", this.getClass());
		}
	}

	public abstract void run() throws Exception;

	public static void executeAll() {
		List<OnetimePatch> patches = OnetimePatch.filter("isExecuted", false)
				.asList();
		for (OnetimePatch patch : patches) {
			patch.execute();
		}
	}
}
