package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bson.types.ObjectId;

import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.common.Constants;
import com.withiter.common.ContentType;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.models.account.Account;

import controllers.rsecure.Secure;
import controllers.rsecure.SecurityHelper;

public class LandingController extends BaseController {
	public static void index(){
		renderJapid();
	}
}
