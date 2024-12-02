package fib.asw.waslab03;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.hc.client5.http.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class Tasca_6 {
	
	private static final String LOCALE = "ca";

	public static void main(String[] args) {

		String TOKEN = Token.get();
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM 'de' yyyy 'a les' HH:mm:ss", new Locale(LOCALE));
		String now = sdf.format(new Date());

		try {
			String URI0 = "https://mastodont.cat/api/v1/trends/tags";
			String output0 = Request.get(URI0)
					.addHeader("Authorization","Bearer "+TOKEN)
					.execute()
					.returnContent()
					.asString();
			JSONArray resultArray0 = new JSONArray(output0);
			System.out.println("Els 10 tags més populars a Mastodon ["+now+"]");
			for (int i = 0; i < resultArray0.length(); ++i) {
				JSONObject result0 = resultArray0.getJSONObject(i);
				if (result0 != null) {
					String hashtag = result0.getString("name");
					String URI1 = "https://mastodont.cat/api/v1/timelines/tag/"+hashtag+"?limit=5";
					String output1 = Request.get(URI1)
							.addHeader("Authorization","Bearer "+TOKEN)
							.execute()
							.returnContent()
							.asString();
					JSONArray resultArray1 = new JSONArray(output1);
					System.out.println("*************************************************");
					System.out.println("* Tag: "+hashtag);
					System.out.println("*************************************************");
					for (int j = 0; j < resultArray1.length(); ++j) {
						JSONObject result1 = resultArray1.getJSONObject(j);
						if (result1 != null) {
							JSONObject account = result1.getJSONObject("account");
							String display_name = account.getString("display_name");
							String acct = account.getString("acct");
							String textHTML = result1.getString("content");
							String text = Jsoup.parse(textHTML).text();
							System.out.println("- "+display_name+" (@"+acct+"): "+text);
							System.out.println("-------------------------------------------------");
						}
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
