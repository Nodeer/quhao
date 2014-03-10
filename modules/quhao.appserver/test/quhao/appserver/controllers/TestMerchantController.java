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

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.utils.StringUtils;

/**
 * @author user
 *
 */
public class TestMerchantController {//extends FunctionalTest{
	public static void main(String[] args) {
		
		DBAddress addr = null;
		try {
			addr = new DBAddress("localhost", "quhao-dev-db");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = Mongo.connect(addr);
		DBCollection col = db.getCollection("Account");
		DBCursor cursor = col.find();
		List<String> accounts = new ArrayList<String>();
		String testPhone = "";
		while (cursor.hasNext()) {
			DBObject account = cursor.next();
			String phone = account.get("phone").toString();
			System.out.println(phone);
			if(phone.equals("18817261072")){
				String aid = account.get("_id").toString();
				testPhone = aid;
				if(accounts.size() == 5){
					break;
				} else {
					continue;
				}
			}
			int jifen = Integer.parseInt(account.get("jifen").toString());
			if(jifen > 0){
				if(accounts.size() == 5){
					continue;
				}
				String aid = account.get("_id").toString();
				accounts.add(aid);
			}
		}
		accounts.add(testPhone);
		System.out.println(accounts.size());
		
//		MorphiaQuery q = Account.q();
//		q.filter("enable", true).filter("jifen > ", 0);
//		if(q.count() >  6){
//			accounts = (List<Account>) q.limit(6);
//		} else {
//			accounts = q.asList();
//		}
		
//		MorphiaQuery qMerchant = Merchant.q();
//		qMerchant.filter("name", "望湘园").limit(1); // hard code for test
//		Merchant m = qMerchant.first();
		String merchantId = "52e3c726036431505d9a9e20";
		int seatNo = 13; // hard code for test
		
		for(int i = 0; i < accounts.size(); i++){
			String accountId = accounts.get(i);
			
			String buf = get("/nahao?accountId=" + accountId + "&mid=" + merchantId + "&seatNumber=" + seatNo);
			System.out.println(buf);
			if (StringUtils.isEmpty(buf)) {
			} else {
			}
		}
	}

//	@Before
//	public void setUp() {
////	    Fixtures.deleteDatabase();
//	    Fixtures.loadModels("/quhao/appserver/controllers/merchant.yml");
//	}
	
	/**
	 * Test method for {@link controllers.MerchantController#nahao(java.lang.String, java.lang.String, int)}.
	 */
//	@Test
//	public void testNahao() {
//		
////		Response response = GET("/");
////        assertIsOk(response);
////        assertContentType("text/html", response);
////        assertCharset(play.Play.defaultWebEncoding, response);
//        
//        
//        
//        MorphiaQuery q = Account.q();
////        System.out.println(q.count());
//		q.filter("enable", true).filter("jifen > ", 0);
//		List<Account> accounts = null;
//		if(q.count() >  6){
//			accounts = q.limit(6).asList();
//		} else {
//			accounts = q.asList();
//		}
//		
////		MorphiaQuery qMerchant = Merchant.q();
////		System.out.println(qMerchant.count());
////		qMerchant.criteria("name").equal("望湘园"); // hard code for test
////		qMerchant.limit(1);
////		System.out.println(qMerchant.count());
////		Merchant m = qMerchant.first();
//		String merchantId = "52e3c726036431505d9a9e20";
//		String seatNo = "1"; // hard code for test
//		
//		for(int i = 0; i < accounts.size(); i++){
//			Account account =  accounts.get(i);
//			System.out.println(account.phone);
//			String accountId = account.id();
//			System.out.println(accountId);
//			
////			Map<String, String> args = new HashMap<String, String>();
////			args.put("accountId", accountId);
////			args.put("mid", merchantId);
////			args.put("seatNumber", seatNo);
////			args.put("user-agent", "QuhaoAndroid");
////			
//			
////			Response response = POST("/nahao", args);
//			Request r = newRequest();
//			Http.Header header = new Http.Header("user-agent", "QuhaoAndroid");
//			r.headers.put("user-agent", header);
//			Response response = GET(r,"/nahao?accountId=" + accountId + "&mid=" + merchantId + "&seatNumber=" + seatNo);
//			assertStatus(200, response);
//			String buf = response.toString();
////			String buf = CommonHTTPRequest.get("nahao?accountId=" + accountId + "&mid=" + merchantId + "&seatNumber=" + seatNo);
//			System.out.println(buf);
//			if (StringUtils.isEmpty(buf)) {
//			} else {
//			}
//		}
//		
//	}
	
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
//			httpUrl = encodeURL(httpUrl);
			HttpGet request = new HttpGet(httpUrl);
			request.setHeader("user-agent", "QuhaoAndroid");

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 10 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			// Set the default socket timeout in milliseconds which is the timeout
			// for waiting for data.
			int timeoutSocket = 10 * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


}
