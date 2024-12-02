package fib.asw.waslab03;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

public class Tasca_5 {

	public static void main(String[] args) {
		String TOKEN = Token.get();

		try {
			String URI0 = "https://mastodont.cat/api/v1/accounts/search?q=fib_asw";
			String output0 = Request.get(URI0)
					.addHeader("Authorization","Bearer "+TOKEN)
					.execute()
					.returnContent()
					.asString();
			JSONArray resultArray0 = new JSONArray(output0);
			JSONObject result0 = resultArray0.getJSONObject(0);
			String fib_asw = result0.getString("id");
			
			String URI = "https://mastodont.cat/api/v1/accounts/" + fib_asw + "/statuses?limit=1";
			String output = Request.get(URI)
					.addHeader("Authorization","Bearer "+TOKEN)
					.execute()
					.returnContent()
					.asString();
			JSONArray resultArray = new JSONArray(output);
			JSONObject result = resultArray.getJSONObject(0);
			String content = result.getString("content");
			System.out.println(content);
			
			String idStatus = result.getString("id");
			String URI2 = "https://mastodont.cat/api/v1/statuses/" + idStatus + "/reblog";
			String output2 = Request.post(URI2)
					.addHeader("Authorization","Bearer "+TOKEN)
					.execute()
					.returnContent()
					.asString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
