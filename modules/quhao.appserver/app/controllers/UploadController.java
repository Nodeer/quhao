package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bson.types.ObjectId;

import play.modules.morphia.Model.MorphiaQuery;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.ContentType;

public class UploadController extends BaseController {
	private static String MERCHANT_IMAGE = "MerchantImage";

	@SuppressWarnings("unused")
	private static GridFSInputFile uploadFirst(String param) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				gfsFile = UploadController.saveBinary(file, null);
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

	/**
	 * Save file
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static GridFSInputFile saveBinary(File file, String mid) throws IOException {
		GridFS gfs = new GridFS(MorphiaQuery.ds().getDB(), MERCHANT_IMAGE);
		GridFSInputFile gfsFile = gfs.createFile(file);
		String fName = file.getName();
		String suffix = fName.replaceFirst("^.*\\.", ".");
		gfsFile.setContentType(ContentType.get(suffix));
		gfsFile.put("ext", suffix);
		gfsFile.put("mid", mid);
		gfsFile.setFilename(mid+"_"+fName);
		gfsFile.save();
		return gfsFile;
	}

	/**
	 * find file by file name
	 * @param fileName
	 */
	public static void find(String fileName) {
		GridFSDBFile gfsFile = findOne(fileName);
		if (gfsFile.getContentType() == null) {
			response.contentType = "";
		} else {
			response.contentType = gfsFile.getContentType();
		}
		renderBinary(gfsFile.getInputStream(), gfsFile.getFilename());
	}
	
	private static GridFSDBFile findOne(String fileName) {
		GridFSDBFile rtn = null;
		try {
			rtn = UploadController.findBinary(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}
	
	public static GridFSDBFile findBinary(String fileName) throws IOException {
		GridFS gfs = new GridFS(MorphiaQuery.ds().getDB(), MERCHANT_IMAGE);
//		GridFSDBFile file = gfs.find(ObjectId.massageToObjectId(fileName));
		GridFSDBFile file = gfs.findOne(fileName);
		return file;
	}
	
	public static List<GridFSDBFile> findBinaryByMid(String mid) throws IOException {
		GridFS gfs = new GridFS(MorphiaQuery.ds().getDB(), MERCHANT_IMAGE);
		List<GridFSDBFile> files = gfs.find("mid="+mid);
		return files;
	}

}
