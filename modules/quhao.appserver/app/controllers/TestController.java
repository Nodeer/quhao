package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        try {
			url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			String result = "";
			String readerLine = null;
			while((readerLine=br.readLine())!=null){
				result += readerLine;
			}
			in.close();
			urlConn.disconnect();
			System.out.println("result:"+result);
//			TextView textView = (TextView)this.findViewById(R.id.result);
//			textView.setText(result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
