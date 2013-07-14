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

public class UploadController extends BaseController {
	private static String FILE_RESEARCH_ITEM = "FileResearchItem";

	public static void upload4Home() {
		GridFSInputFile file = uploadFirst("image");
		String title = params.get("title");
		String content = params.get("content");
		createItem(title,content,file);
		redirect("/home");
	}

	public static void upload4School() {
		
		File[] files = params.get("schoolImage", File[].class);
		GridFSInputFile file = null;
		
		String uniqueId = params.get("schoolUniqueID");
		String rid = params.get("rid");
		
//		ResearchItem item = ResearchItem.getResearchItemById(rid.trim());
		
//		String type = params.get("type");
//		String content = params.get("content");
//		String schoolName = params.get("schoolName");
		if(files != null && files.length > 0){
			 file = uploadFirst("schoolImage");
			 String thumbnailUrl = "/researchitemcontroller/getImageThumbnail?id="+file.getId();
//			 item.thumbnailUrl = thumbnailUrl;
//			 item.save();
//			 String result = SchoolController.addResearchItem(content, type, schoolName, thumbnailUrl);
		}else{
//			String result = SchoolController.addResearchItem(content, type, schoolName);
		}
		
		redirect("/page/"+uniqueId);
	}
	
	private static void createItem(String title, String content, GridFSInputFile file){
		
	}
	
//	public static void upload4HomeDrag(File file) {
//		String filename = uploadFirst(file);
//		String title = filename;
//		String content = "content";
//		createItem(title,content,filename);
//		redirect("/home");
//	}
	

	public static void find(String fileName) {
		GridFSDBFile gfsFile = findOne(fileName);
		if(gfsFile.getContentType() == null){
			response.contentType = "";
		}else{
			response.contentType = gfsFile.getContentType();
		}
		
		renderBinary(gfsFile.getInputStream(),gfsFile.getFilename());
	}

	private static GridFSInputFile uploadFirst(String param) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				gfsFile = UploadController.saveBinary(file);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}
	
//	private static String uploadFirst(File file) {
//		GridFSInputFile gfsFile = null;
//		try {
//			gfsFile = UploadController.saveBinary(file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (gfsFile == null) {
//			return null;
//		} else {
//			return gfsFile.getFilename();
//		}
//	}


	private static GridFSDBFile findOne(String fileName) {
		GridFSDBFile rtn = null;
		try {
			rtn = UploadController.findBinary(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}

	public static GridFSInputFile saveBinary(File file) throws IOException {
		GridFS gfs = new GridFS(MorphiaQuery.ds().getDB(), FILE_RESEARCH_ITEM);
		GridFSInputFile gfsFile = gfs.createFile(file);
		String fName = file.getName();
		String suffix = fName.replaceFirst("^.*\\.", ".");
		gfsFile.setContentType(ContentType.get(suffix));
//		gfsFile.setContentType(suffix);
		gfsFile.put("ext", suffix);
		gfsFile.put("aliases", SecurityHelper.user().getId());
//		gfsFile.put("filename", SecurityHelper.user().getId());
		gfsFile.save();
		return gfsFile;
	}

	public static GridFSDBFile findBinary(String fileName) throws IOException {
		GridFS gfs = new GridFS(MorphiaQuery.ds().getDB(), FILE_RESEARCH_ITEM);
		GridFSDBFile file = gfs.find(ObjectId.massageToObjectId(fileName));
		return file;
	}
	
}
