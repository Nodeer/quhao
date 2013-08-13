package controllers;

import org.json.JSONException;
import org.json.JSONObject;

public class TestController extends BaseController {

	
	public static void main(String[] args) {
        
        try {
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
