/**
 * 
 */
package quhao.appserver.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import play.Logger;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.utils.ExceptionUtil;
import com.withiter.utils.StringUtils;

/**
 * @author user
 * 
 */
public class TestMerchantController {// extends FunctionalTest{
	public static void main(String[] args) {

		DBAddress addr = null;
		try {
			addr = new DBAddress("localhost", "quhao-dev-db");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Logger.debug("QuhaoException: %s", ExceptionUtil.getTrace(e));
		}
		DB db = Mongo.connect(addr);
		DBCollection col = db.getCollection("Account");
		DBCursor cursor = col.find();
		List<String> accounts = new ArrayList<String>();
		String testPhone = "";
		while (cursor.hasNext()) {
			DBObject account = cursor.next();
			String phone = account.get("phone").toString();
			if (phone.equals("18817261072")) {
				String aid = account.get("_id").toString();
				testPhone = aid;
				if (accounts.size() == 5) {
					break;
				} else {
					continue;
				}
			}
			int jifen = Integer.parseInt(account.get("jifen").toString());
			if (jifen > 0) {
				if (accounts.size() == 5) {
					continue;
				}
				String aid = account.get("_id").toString();
				accounts.add(aid);
			}
		}
		accounts.add(testPhone);
		String merchantId = "52e3c726036431505d9a9e20";
		int seatNo = 13; // hard code for test

		for (int i = 0; i < accounts.size(); i++) {
			String accountId = accounts.get(i);

			String buf = get("/nahao?accountId=" + accountId + "&mid=" + merchantId + "&seatNumber=" + seatNo);
			if (StringUtils.isEmpty(buf)) {
			} else {
			}
		}
	}

	/**
	 * A HTTP request(GET) with given URL
	 * 
	 * @param strUrl
	 *            the URL you want to request
	 * @return
	 */
	public static String get(String url) {
		String result = "";
		String httpUrl = "http://localhost:9081" + url;
		System.out.println(httpUrl);
		try {
			HttpGet request = new HttpGet(httpUrl);
			request.setHeader("user-agent", "QuhaoAndroid");

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			// Set the default socket timeout in milliseconds which is the
			// timeout
			// for waiting for data.
			int timeoutSocket = 10 * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Logger.debug("QuhaoException: %s", ExceptionUtil.getTrace(e));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Logger.debug("QuhaoException: %s", ExceptionUtil.getTrace(e));
		} catch (ParseException e) {
			e.printStackTrace();
			Logger.debug("QuhaoException: %s", ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			Logger.debug("QuhaoException: %s", ExceptionUtil.getTrace(e));
		}

		return result;
	}

}
