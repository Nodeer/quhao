package models;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.modules.morphia.Model;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;

public class MongoHelper {
	public static List<ObjectId> getKeyValues(List<Key<Model>> list) {
		List<ObjectId> result = new ArrayList<ObjectId>(list.size());
		for (Key key : list) {
			result.add((ObjectId) key.getId());
		}
		return result;
	}

	public static String getKind(Class<? extends Model> type) {
		Entity anno = type.getAnnotation(Entity.class);
		if (anno != null) {
			if (org.apache.commons.lang.StringUtils.isEmpty(anno.value())) {
				return anno.value();
			} else {
				return type.getSimpleName();
			}
		} else {
			return "";
		}
	}
}
