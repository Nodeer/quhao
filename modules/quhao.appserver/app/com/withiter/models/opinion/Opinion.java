package com.withiter.models.opinion;

import japidviews._javatags.I18nKeys;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.modules.morphia.Model.NoAutoTimestamp;
import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.exceptions.ValidationException;

@Entity
@NoAutoTimestamp
public class Opinion extends OpinionEntityDef {
	
	
	
}
