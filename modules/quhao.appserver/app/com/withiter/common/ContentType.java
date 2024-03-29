package com.withiter.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContentType {

	private static Map<String, String> holder = new HashMap<String, String>();
	public static Set<String> images = new HashSet<String>();
	public static Set<String> audios = new HashSet<String>();
	public static Set<String> videos = new HashSet<String>();

	public static String get(String suffix) {
		String found = holder.get(suffix);
		if (found != null) {
			return found;
		} else {
			return null;
		}
	}

	static {
		images.add(".bmp");
		images.add(".jpg");
		images.add(".png");
		images.add(".gif");

		audios.add(".mp3");
		audios.add(".wav");
		audios.add(".wma");
		audios.add(".mid");
		audios.add(".mka");
		audios.add(".wv");

		videos.add(".avi");
		videos.add(".wmv");
		videos.add(".flv");
		videos.add(".mkv");
		videos.add(".mov");
		videos.add(".3gp");
		videos.add(".mpg");
		videos.add(".mpeg");
		videos.add(".rm");
		videos.add(".rmvb");
		videos.add(".ts");
		videos.add(".swf");

		holder.put(".*", "application/octet-stream");
		holder.put(".001", "application/x-001");
		holder.put(".301", "application/x-301");
		holder.put(".323", "text/h323");
		holder.put(".906", "application/x-906");
		holder.put(".907", "drawing/907");
		holder.put(".a11", "application/x-a11");
		holder.put(".acp", "audio/x-mei-aac");
		holder.put(".ai", "application/postscript");
		holder.put(".aif", "audio/aiff");
		holder.put(".aifc", "audio/aiff");
		holder.put(".aiff", "audio/aiff");
		holder.put(".anv", "application/x-anv");
		holder.put(".asa", "text/asa");
		holder.put(".asf", "video/x-ms-asf");
		holder.put(".asp", "text/asp");
		holder.put(".asx", "video/x-ms-asf");
		holder.put(".au", "audio/basic");
		holder.put(".avi", "video/avi");
		holder.put(".awf", "application/vnd.adobe.workflow");
		holder.put(".biz", "text/xml");
		holder.put(".bmp", "application/x-bmp");
		holder.put(".bot", "application/x-bot");
		holder.put(".c4t", "application/x-c4t");
		holder.put(".c90", "application/x-c90");
		holder.put(".cal", "application/x-cals");
		holder.put(".cat", "application/s-pki.seccat");
		holder.put(".cdf", "application/x-netcdf");
		holder.put(".cdr", "application/x-cdr");
		holder.put(".cel", "application/x-cel");
		holder.put(".cer", "application/x-x509-ca-cert");
		holder.put(".cg4", "application/x-g4");
		holder.put(".cgm", "application/x-cgm");
		holder.put(".cit", "application/x-cit");
		holder.put(".class", "java/*");
		holder.put(".cml", "text/xml");
		holder.put(".cmp", "application/x-cmp");
		holder.put(".cmx", "application/x-cmx");
		holder.put(".cot", "application/x-cot");
		holder.put(".crl", "application/pkix-crl");
		holder.put(".crt", "application/x-x509-ca-cert");
		holder.put(".csi", "application/x-csi");
		holder.put(".css", "text/css");
		holder.put(".cut", "application/x-cut");
		holder.put(".dbf", "application/x-dbf");
		holder.put(".dbm", "application/x-dbm");
		holder.put(".dbx", "application/x-dbx");
		holder.put(".dcd", "text/xml");
		holder.put(".dcx", "application/x-dcx");
		holder.put(".der", "application/x-x509-ca-cert");
		holder.put(".dgn", "application/x-dgn");
		holder.put(".dib", "application/x-dib");
		holder.put(".dll", "application/x-msdownload");
		holder.put(".doc", "application/msword");
		holder.put(".dot", "application/msword");
		holder.put(".drw", "application/x-drw");
		holder.put(".dtd", "text/xml");
		holder.put(".dwf", "Model/vnd.dwf");
		holder.put(".dwf", "application/x-dwf");
		holder.put(".dwg", "application/x-dwg");
		holder.put(".dxb", "application/x-dxb");
		holder.put(".dxf", "application/x-dxf");
		holder.put(".edn", "application/vnd.adobe.edn");
		holder.put(".emf", "application/x-emf");
		holder.put(".eml", "message/rfc822");
		holder.put(".ent", "text/xml");
		holder.put(".epi", "application/x-epi");
		holder.put(".eps", "application/x-ps");
		holder.put(".eps", "application/postscript");
		holder.put(".etd", "application/x-ebx");
		holder.put(".exe", "application/x-msdownload");
		holder.put(".fax", "image/fax");
		holder.put(".fdf", "application/vnd.fdf");
		holder.put(".fif", "application/fractals");
		holder.put(".fo", "text/xml");
		holder.put(".frm", "application/x-frm");
		holder.put(".g4", "application/x-g4");
		holder.put(".gbr", "application/x-gbr");
		holder.put(".gcd", "application/x-gcd");
		holder.put(".gif", "image/gif");
		holder.put(".gl2", "application/x-gl2");
		holder.put(".gp4", "application/x-gp4");
		holder.put(".hgl", "application/x-hgl");
		holder.put(".hmr", "application/x-hmr");
		holder.put(".hpg", "application/x-hpgl");
		holder.put(".hpl", "application/x-hpl");
		holder.put(".hqx", "application/mac-binhex40");
		holder.put(".hrf", "application/x-hrf");
		holder.put(".hta", "application/hta");
		holder.put(".htc", "text/x-component");
		holder.put(".htm", "text/html");
		holder.put(".html", "text/html");
		holder.put(".htt", "text/webviewhtml");
		holder.put(".htx", "text/html");
		holder.put(".icb", "application/x-icb");
		holder.put(".ico", "image/x-icon");
		holder.put(".ico", "application/x-ico");
		holder.put(".iff", "application/x-iff");
		holder.put(".ig4", "application/x-g4");
		holder.put(".igs", "application/x-igs");
		holder.put(".iii", "application/x-iphone");
		holder.put(".img", "application/x-img");
		holder.put(".ins", "application/x-internet-signup");
		holder.put(".isp", "application/x-internet-signup");
		holder.put(".IVF", "video/x-ivf");
		holder.put(".java", "java/*");
		holder.put(".jfif", "image/jpeg");
		holder.put(".jpe", "image/jpeg");
		holder.put(".jpe", "application/x-jpe");
		holder.put(".jpeg", "image/jpeg");
		holder.put(".jpg", "image/jpeg");
		holder.put(".js", "application/x-javascript");
		holder.put(".jsp", "text/html");
		holder.put(".la1", "audio/x-liquid-file");
		holder.put(".lar", "application/x-laplayer-reg");
		holder.put(".latex", "application/x-latex");
		holder.put(".lavs", "audio/x-liquid-secure");
		holder.put(".lbm", "application/x-lbm");
		holder.put(".lmsff", "audio/x-la-lms");
		holder.put(".ls", "application/x-javascript");
		holder.put(".ltr", "application/x-ltr");
		holder.put(".m1v", "video/x-mpeg");
		holder.put(".m2v", "video/x-mpeg");
		holder.put(".m3u", "audio/mpegurl");
		holder.put(".m4e", "video/mpeg4");
		holder.put(".mac", "application/x-mac");
		holder.put(".man", "application/x-troff-man");
		holder.put(".math", "text/xml");
		holder.put(".mdb", "application/msaccess");
		holder.put(".mdb", "application/x-mdb");
		holder.put(".mfp", "application/x-shockwave-flash");
		holder.put(".mht", "message/rfc822");
		holder.put(".mhtml", "message/rfc822");
		holder.put(".mi", "application/x-mi");
		holder.put(".mid", "audio/mid");
		holder.put(".midi", "audio/mid");
		holder.put(".mil", "application/x-mil");
		holder.put(".mml", "text/xml");
		holder.put(".mnd", "audio/x-musicnet-download");
		holder.put(".mns", "audio/x-musicnet-stream");
		holder.put(".mocha", "application/x-javascript");
		holder.put(".movie", "video/x-sgi-movie");
		holder.put(".mp1", "audio/mp1");
		holder.put(".mp2", "audio/mp2");
		holder.put(".mp2v", "video/mpeg");
		holder.put(".mp3", "audio/mp3");
		holder.put(".mp4", "video/mp4");
		holder.put(".mpa", "video/x-mpg");
		holder.put(".mpd", "application/-project");
		holder.put(".mpe", "video/x-mpeg");
		holder.put(".mpeg", "video/mpg");
		holder.put(".mpg", "video/mpg");
		holder.put(".mpga", "audio/rn-mpeg");
		holder.put(".mpp", "application/-project");
		holder.put(".mps", "video/x-mpeg");
		holder.put(".mpt", "application/-project");
		holder.put(".mpv", "video/mpg");
		holder.put(".mpv2", "video/mpeg");
		holder.put(".mpw", "application/s-project");
		holder.put(".mpx", "application/-project");
		holder.put(".mtx", "text/xml");
		holder.put(".mxp", "application/x-mmxp");
		holder.put(".net", "image/pnetvue");
		holder.put(".nrf", "application/x-nrf");
		holder.put(".nws", "message/rfc822");
		holder.put(".odc", "text/x-ms-odc");
		holder.put(".out", "application/x-out");
		holder.put(".p10", "application/pkcs10");
		holder.put(".p12", "application/x-pkcs12");
		holder.put(".p7b", "application/x-pkcs7-certificates");
		holder.put(".p7c", "application/pkcs7-mime");
		holder.put(".p7m", "application/pkcs7-mime");
		holder.put(".p7r", "application/x-pkcs7-certreqresp");
		holder.put(".p7s", "application/pkcs7-signature");
		holder.put(".pc5", "application/x-pc5");
		holder.put(".pci", "application/x-pci");
		holder.put(".pcl", "application/x-pcl");
		holder.put(".pcx", "application/x-pcx");
		holder.put(".pdf", "application/pdf");
		holder.put(".pdx", "application/vnd.adobe.pdx");
		holder.put(".pfx", "application/x-pkcs12");
		holder.put(".pgl", "application/x-pgl");
		holder.put(".pic", "application/x-pic");
		holder.put(".pko", "application-pki.pko");
		holder.put(".pl", "application/x-perl");
		holder.put(".plg", "text/html");
		holder.put(".pls", "audio/scpls");
		holder.put(".plt", "application/x-plt");
		holder.put(".png", "image/png");
		holder.put(".pot", "applications-powerpoint");
		holder.put(".ppa", "application/vs-powerpoint");
		holder.put(".ppm", "application/x-ppm");
		holder.put(".pps", "application-powerpoint");
		holder.put(".ppt", "applications-powerpoint");
		holder.put(".ppt", "application/x-ppt");
		holder.put(".pr", "application/x-pr");
		holder.put(".prf", "application/pics-rules");
		holder.put(".prn", "application/x-prn");
		holder.put(".prt", "application/x-prt");
		holder.put(".ps", "application/x-ps");
		holder.put(".ps", "application/postscript");
		holder.put(".ptn", "application/x-ptn");
		holder.put(".pwz", "application/powerpoint");
		holder.put(".r3t", "text/vnd.rn-realtext3d");
		holder.put(".ra", "audio/vnd.rn-realaudio");
		holder.put(".ram", "audio/x-pn-realaudio");
		holder.put(".ras", "application/x-ras");
		holder.put(".rat", "application/rat-file");
		holder.put(".rdf", "text/xml");
		holder.put(".rec", "application/vnd.rn-recording");
		holder.put(".red", "application/x-red");
		holder.put(".rgb", "application/x-rgb");
		holder.put(".rjs", "application/vnd.rn-realsystem-rjs");
		holder.put(".rjt", "application/vnd.rn-realsystem-rjt");
		holder.put(".rlc", "application/x-rlc");
		holder.put(".rle", "application/x-rle");
		holder.put(".rm", "application/vnd.rn-realmedia");
		holder.put(".rmf", "application/vnd.adobe.rmf");
		holder.put(".rmi", "audio/mid");
		holder.put(".rmj", "application/vnd.rn-realsystem-rmj");
		holder.put(".rmm", "audio/x-pn-realaudio");
		holder.put(".rmp", "application/vnd.rn-rn_music_package");
		holder.put(".rms", "application/vnd.rn-realmedia-secure");
		holder.put(".rmvb", "application/vnd.rn-realmedia-vbr");
		holder.put(".rmx", "application/vnd.rn-realsystem-rmx");
		holder.put(".rnx", "application/vnd.rn-realplayer");
		holder.put(".rp", "image/vnd.rn-realpix");
		holder.put(".rpm", "audio/x-pn-realaudio-plugin");
		holder.put(".rsml", "application/vnd.rn-rsml");
		holder.put(".rt", "text/vnd.rn-realtext");
		holder.put(".rtf", "application/msword");
		holder.put(".rtf", "application/x-rtf");
		holder.put(".rv", "video/vnd.rn-realvideo");
		holder.put(".sam", "application/x-sam");
		holder.put(".sat", "application/x-sat");
		holder.put(".sdp", "application/sdp");
		holder.put(".sdw", "application/x-sdw");
		holder.put(".sit", "application/x-stuffit");
		holder.put(".slb", "application/x-slb");
		holder.put(".sld", "application/x-sld");
		holder.put(".slk", "drawing/x-slk");
		holder.put(".smi", "application/smil");
		holder.put(".smil", "application/smil");
		holder.put(".smk", "application/x-smk");
		holder.put(".snd", "audio/basic");
		holder.put(".sol", "text/plain");
		holder.put(".sor", "text/plain");
		holder.put(".spc", "application/x-pkcs7-certificates");
		holder.put(".spl", "application/futuresplash");
		holder.put(".spp", "text/xml");
		holder.put(".ssm", "application/streamingmedia");
		holder.put(".sst", "application-pki.certstore");
		holder.put(".stl", "application/-pki.stl");
		holder.put(".stm", "text/html");
		holder.put(".sty", "application/x-sty");
		holder.put(".svg", "text/xml");
		holder.put(".swf", "application/x-shockwave-flash");
		holder.put(".tdf", "application/x-tdf");
		holder.put(".tg4", "application/x-tg4");
		holder.put(".tga", "application/x-tga");
		holder.put(".tif", "image/tiff");
		holder.put(".tif", "application/x-tif");
		holder.put(".tiff", "image/tiff");
		holder.put(".tld", "text/xml");
		holder.put(".top", "drawing/x-top");
		holder.put(".torrent", "application/x-bittorrent");
		holder.put(".tsd", "text/xml");
		holder.put(".txt", "text/plain");
		holder.put(".uin", "application/x-icq");
		holder.put(".uls", "text/iuls");
		holder.put(".vcf", "text/x-vcard");
		holder.put(".vda", "application/x-vda");
		holder.put(".vdx", "application/vnd.visio");
		holder.put(".vml", "text/xml");
		holder.put(".vpg", "application/x-vpeg005");
		holder.put(".vsd", "application/vnd.visio");
		holder.put(".vsd", "application/x-vsd");
		holder.put(".vss", "application/vnd.visio");
		holder.put(".vst", "application/vnd.visio");
		holder.put(".vst", "application/x-vst");
		holder.put(".vsw", "application/vnd.visio");
		holder.put(".vsx", "application/vnd.visio");
		holder.put(".vtx", "application/vnd.visio");
		holder.put(".vxml", "text/xml");
		holder.put(".wav", "audio/wav");
		holder.put(".wax", "audio/x-ms-wax");
		holder.put(".wb1", "application/x-wb1");
		holder.put(".wb2", "application/x-wb2");
		holder.put(".wb3", "application/x-wb3");
		holder.put(".wbmp", "image/vnd.wap.wbmp");
		holder.put(".wiz", "application/msword");
		holder.put(".wk3", "application/x-wk3");
		holder.put(".wk4", "application/x-wk4");
		holder.put(".wkq", "application/x-wkq");
		holder.put(".wks", "application/x-wks");
		holder.put(".wm", "video/x-ms-wm");
		holder.put(".wma", "audio/x-ms-wma");
		holder.put(".wmd", "application/x-ms-wmd");
		holder.put(".wmf", "application/x-wmf");
		holder.put(".wml", "text/vnd.wap.wml");
		holder.put(".wmv", "video/x-ms-wmv");
		holder.put(".wmx", "video/x-ms-wmx");
		holder.put(".wmz", "application/x-ms-wmz");
		holder.put(".wp6", "application/x-wp6");
		holder.put(".wpd", "application/x-wpd");
		holder.put(".wpg", "application/x-wpg");
		holder.put(".wpl", "application/-wpl");
		holder.put(".wq1", "application/x-wq1");
		holder.put(".wr1", "application/x-wr1");
		holder.put(".wri", "application/x-wri");
		holder.put(".wrk", "application/x-wrk");
		holder.put(".ws", "application/x-ws");
		holder.put(".ws2", "application/x-ws");
		holder.put(".wsc", "text/scriptlet");
		holder.put(".wsdl", "text/xml");
		holder.put(".wvx", "video/x-ms-wvx");
		holder.put(".xdp", "application/vnd.adobe.xdp");
		holder.put(".xdr", "text/xml");
		holder.put(".xfd", "application/vnd.adobe.xfd");
		holder.put(".xfdf", "application/vnd.adobe.xfdf");
		holder.put(".xhtml", "text/html");
		holder.put(".xls", "application/-excel");
		holder.put(".xls", "application/x-xls");
		holder.put(".xlw", "application/x-xlw");
		holder.put(".xml", "text/xml");
		holder.put(".xpl", "audio/scpls");
		holder.put(".xq", "text/xml");
		holder.put(".xql", "text/xml");
		holder.put(".xquery", "text/xml");
		holder.put(".xsd", "text/xml");
		holder.put(".xsl", "text/xml");
		holder.put(".xslt", "text/xml");
		holder.put(".xwd", "application/x-xwd");
		holder.put(".x_b", "application/x-x_b");
		holder.put(".x_t", "application/x-x_t");
	}
}
