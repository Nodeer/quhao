package controllers;

import org.json.JSONException;
import org.json.JSONObject;

public class TestController extends BaseController {

	
	public static void main(String[] args) {
//		String strUrl = "http://localhost:9081/testcontroller/test1?arg=2222";
//
//        URL url = null;
//        String result = "";
//        try {
//			url = new URL(strUrl);
//			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
//			BufferedReader br = new BufferedReader(in);
//			
//			String readerLine = null;
//			while((readerLine=br.readLine())!=null){
//				result += readerLine;
//			}
//			in.close();
//			urlConn.disconnect();
//			System.out.println("result:"+result);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        
        try {
//			String str = "{\"a\":\"b\", \"c\":\"d\"}";
        	String str = "{    \"status\":\"OK\",    \"result\":{        \"location\":{            \"lng\":121.371053,            \"lat\":31.187143        },        \"precise\":1,        \"confidence\":80,        \"level\":\"\u9053\u8def\"    }}";
			JSONObject a = new JSONObject(str);
			
			System.out.println(a.optString("lng"));
			
			
			System.out.println(a); // {"c":"d","a":"b"}
			System.out.println(a.get("status")); // d
			System.out.println(a.get("result")); // d
			System.out.println(new JSONObject(a.get("result").toString()).get("location")); // d
			JSONObject xyJSON = new JSONObject(new JSONObject(a.get("result").toString()).get("location").toString());
			System.out.println(xyJSON.get("lng"));
			System.out.println(xyJSON.get("lat"));
			
//			String jsonStr = result;
//			JSONObject object = new JSONObject(jsonStr);
//			System.out.println(object.get("name"));
//			System.out.println(object.get("age"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
