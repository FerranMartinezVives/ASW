package fib.asw.waslab02;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import fib.asw.waslab02.MD5;


@WebServlet(urlPatterns = {"/tweets", "/tweets/*"})
public class WoTServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private TweetDAO tweetDAO;
	private String TWEETS_URI = "/waslab02/tweets/";

    public void init() {
    	tweetDAO = new TweetDAO((java.sql.Connection) this.getServletContext().getAttribute("connection"));
    }

    @Override
	// Implements GET http://localhost:8080/waslab02/tweets
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    	response.setContentType("application/json");
		response.setHeader("Cache-control", "no-cache");
		List<Tweet> tweets= tweetDAO.getAllTweets();
		JSONArray job = new JSONArray();
		for (Tweet t: tweets) {
			JSONObject jt = new JSONObject(t);
			jt.remove("class");
			
			long idTweet = t.getId();	
			String idTweetString = Long.toString(idTweet);
			String token = MD5.getMd5(idTweetString);
			jt.put("token", token);
			
			job.put(jt);
		}
		response.getWriter().println(job.toString());

    }

    @Override
	// Implements POST http://localhost:8080/waslab02/tweets/:id/likes
	//        and POST http://localhost:8080/waslab02/tweets
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uri = request.getRequestURI();
		int lastIndex = uri.lastIndexOf("/likes");
		if (lastIndex > -1) {  // uri ends with "/likes"
			// Implements POST http://localhost:8080/waslab02/tweets/:id/likes
			long id = Long.valueOf(uri.substring(TWEETS_URI.length(),lastIndex));		
			response.setContentType("text/plain");
			response.getWriter().println(tweetDAO.likeTweet(id));
		}
		else { 
			// Implements POST http://localhost:8080/waslab02/tweets
			int max_length_of_data = request.getContentLength();
			byte[] httpInData = new byte[max_length_of_data];
			ServletInputStream  httpIn  = request.getInputStream();
			httpIn.readLine(httpInData, 0, max_length_of_data);
			String body = new String(httpInData);
			
			JSONObject json = new JSONObject(body);
			String aut = json.getString("author");
			String txt = json.getString("tweet");
			
			 Tweet tw = tweetDAO.insertTweet(aut, txt);
			 
			 long idTweet = tw.getId();	
			 String idTweetString = Long.toString(idTweet);
			 String token = MD5.getMd5(idTweetString);
			 
			 JSONObject json2 = new JSONObject(tw);
			 json2.put("token", token);
			 
			 response.getWriter().println(json2.toString());

			/*      ^
		      The String variable body contains the sent (JSON) Data. 
		      Complete the implementation below.*/
			
		}
	}
    
    @Override
	// Implements DELETE http://localhost:8080/waslab02/tweets/:id
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		//
    	//,TWEETS_URI.length()-1)
    	String uri;
    	try {
    		uri = req.getRequestURI();
    	} catch (Exception e) {
    		throw new ServletException("DELETE ha fallat");
    	}
    	
		String headerVal = req.getHeader("Authorization");

		
    	
		int lastIndex = uri.lastIndexOf("/");
		if (lastIndex > -1) {  // uri ends with "/likes"
			// Implements POST http://localhost:8080/waslab02/tweets/:id/likes
			long id = Long.valueOf(uri.substring(lastIndex+1));
			if (headerVal == MD5.getMd5(Long.toString(id))) {
				if (tweetDAO.deleteTweet(id) == false) throw new ServletException("DELETE ha fallat");
			}
		}
		

	}
}