package fib.asw.waslab01_ss;

import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(value = "/")
public class WoTServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TweetDAO tweetDAO;
	private Locale currentLocale = new Locale("en");
    private String ENCODING = "ISO-8859-1";

    public void init() {
    	tweetDAO = new TweetDAO((java.sql.Connection) this.getServletContext().getAttribute("connection"));
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	String header = request.getHeader("Accept");

        List<Tweet> tweets = tweetDAO.getAllTweets();
        
        if (header.equals("text/plain")) printPLAINresults(response,tweets);
        else printHTMLresults(response, tweets);

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	if (request.getParameter("newTweet") != null) {
    		String author= request.getParameter("author");
    		String tweet = request.getParameter("tweet_text");
    		long idTweet = 0;
    		try {
    			idTweet = tweetDAO.insertTweet(author, tweet);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		String idTweetString = Long.toString(idTweet);
    		Cookie cookie = new Cookie("authorOfTweet", MD5.getMd5(idTweetString));
    		cookie.setMaxAge(60 * 60);
    		response.addCookie(cookie);
    		
    		String header = request.getHeader("Accept");
        
    		if (header.equals("text/plain")) {
    			response.setContentType("text/plain");
    			PrintWriter out = response.getWriter();
    			out.print(idTweet);
    		}
    		else response.sendRedirect(request.getContextPath()); // This method does NOTHING but redirect to the main page
    	}
    	else if (request.getParameter("deleteTweet") != null) {
    		String idTweetString = request.getParameter("deleteTweet");
    		Cookie[] cookies = request.getCookies();
    		boolean cookieFound = false;
    		if (cookies != null) {
    			for (int i = 0; i < cookies.length && !cookieFound; ++i) {
    				if (cookies[i].getName().equals("authorOfTweet") && cookies[i].getValue().equals(MD5.getMd5(idTweetString))) cookieFound = true;
    			}
    		}
    		if (cookieFound) {
    			int idTweet = Integer.parseInt(idTweetString);
    			tweetDAO.deleteTweet(idTweet);
    		}
    		String header = request.getHeader("Accept");
    		if (!header.equals("text/plain")) response.sendRedirect(request.getContextPath()); // This method does NOTHING but redirect to the main page
    	}
    }
    
    private void printPLAINresults (HttpServletResponse response, List<Tweet> tweets) throws IOException {
    
    response.setContentType("text/plain");
    PrintWriter out = response.getWriter();
    for (Tweet tweet : tweets) {
    	out.println("tweet #" + tweet.getTwid() + ": " + tweet.getAuthor() + ": " + tweet.getText() + " [" + tweet.getCreated_at() + "]");
    	
    }
    }

    private void printHTMLresults (HttpServletResponse response, List<Tweet> tweets) throws IOException {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, currentLocale);
        response.setContentType ("text/html");
        response.setCharacterEncoding(ENCODING); 

        PrintWriter out = response.getWriter();


        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Wall of Tweets</title>");
        out.println("<link href=\"wot.css\" rel=\"stylesheet\" type=\"text/css\" />");
        out.println("</head>");
        out.println("<body class=\"wallbody\">");
        out.println("<h1>Wall of Tweets</h1>");
        out.println("<div class=\"walltweet\">");
        out.println("<form method=\"post\">");
        out.println("<table border=0 cellpadding=2>");
        out.println("<tr><td>Your name:</td><td><input name=\"author\" type=\"text\" size=70></td><td></td></tr>");
        out.println("<tr><td>Your tweet:</td><td><textarea name=\"tweet_text\" rows=\"2\" cols=\"70\" wrap></textarea></td>");
        out.println("<td><input type=\"submit\" name=\"newTweet\" value=\"Tweet!\"></td></tr>");
        out.println("</table></form></div>");
        String currentDate = "None";
        for (Tweet tweet: tweets) {
            String messDate = dateFormatter.format(tweet.getCreated_at());
            if (!currentDate.equals(messDate)) {
                out.println("<br><h3>...... " + messDate + "</h3>");
                currentDate = messDate;
            }
            out.println("<div class=\"wallitem\">");
            out.println("<form method=\"post\">");
            out.println("<h4><em>" + tweet.getAuthor() + "</em> @ "+ timeFormatter.format(tweet.getCreated_at()) +"</h4>");
            out.println("<p>" + tweet.getText() + "</p>");
            out.println("<button type=\"submit\" name=\"deleteTweet\" value=" + tweet.getTwid() + ">Delete</button></form>");
            out.println("</div>");
        }
        out.println ( "</body></html>" );
    }
}