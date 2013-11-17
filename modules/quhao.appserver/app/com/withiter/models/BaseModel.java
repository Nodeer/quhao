package com.withiter.models;

import java.util.Date;
import java.util.UUID;

import org.bson.types.ObjectId;

import play.modules.morphia.Model;
import play.modules.morphia.MorphiaPlugin;
import play.modules.morphia.Model.MorphiaQuery;

public abstract class BaseModel extends Model {
	public Date created = new Date();
	public Date modified = new Date();

	public String key;

	public String getKey() {
		return key;
	}

	public void setKey(String uuid) {
		this.key = uuid;
	}

	public String getKind() {
		return MorphiaPlugin.morphia().getMapper().getCollectionName(this);
	}

	public <T extends BaseModel> T getModelByKey(String key) {
		MorphiaQuery q = new MorphiaQuery(this.getClass()).filter("key", key);
		T object = (T) q.first();
		return object;
	}

	public <T extends BaseModel> T getModelByKey() {
		if (this.getKey() == null) {
			return null;
		}
		MorphiaQuery q = new MorphiaQuery(this.getClass()).filter("key",
				this.getKey());
		T object = (T) q.first();
		return object;
	}

	@OnAdd
	protected void fillInCreatedTimestamp() {
		BaseModel old = old();
		if (old != null) {
			created = old.created;
			modified = new Date();
		} else {
			created = new Date();
		}
	}

	@OnUpdate
	protected void fillInUpdatedTimestamp() {
		modified = new Date();
	}

	protected <T extends BaseModel> T old() {
		if (this.getId() == null) {
			return null;
		}
		if (this.getId() instanceof ObjectId) {
			ObjectId id = (ObjectId) this.getId();
			if (id.isNew()) {
				return null;
			}
		}
		MorphiaQuery q = new MorphiaQuery(this.getClass()).filter("_id",
				this.getId());
		T old = q.first();
		return old;
	}

	public String id() {
		return this.getId().toString();
	}

	public boolean saveModel() {
		if (this.key == null) {
			UUID uuid = UUID.randomUUID();
			this.setKey(uuid.toString());
		}
		if (super.save() == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isNeverChanged() {
		if (modified == null && modified.equals(created)) {
			return true;
		} else {
			return false;
		}
	}

}
