package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.withiter.models.test.Test;

public class TestController extends BaseController {

	public static void test1(String arg){
		System.out.println(arg);
		Test t = new Test("cross","26");
		renderJSON(t);
		renderText("aaaaaa");
	}
	
	public static void main(String[] args) {
		String strUrl = "http://localhost:9081/testcontroller/test1?arg=2222";

        URL url = null;
        String result = "";
        try {
			url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			
			String readerLine = null;
			while((readerLine=br.readLine())!=null){
				result += readerLine;
			}
			in.close();
			urlConn.disconnect();
			System.out.println("result:"+result);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			String str = "{\"a\":\"b\", \"c\":\"d\"}";
			JSONObject a = new JSONObject(str);
			System.out.println(a); // {"c":"d","a":"b"}
			System.out.println(a.get("c")); // d
			
			String jsonStr = result;
			JSONObject object = new JSONObject(jsonStr);
			System.out.println(object.get("name"));
			System.out.println(object.get("age"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
