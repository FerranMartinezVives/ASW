package fib.asw.waslab01_cs;

import java.util.Scanner;

import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;

//This code uses the Fluent API
 
public class SimpleFluentClient {

    private static String URI = "http://localhost:8080/waslab01_ss/";

    public final static void main(String[] args) throws Exception {

      /* Insert code for Task #4 here */

        String idTweet = Request.post(URI).addHeader("Accept", "text/plain")
                .bodyForm(Form.form().add("author",  "pep").add("tweet_text",  "Tweet de prova.").add("newTweet",  "Tweet!").build())
                .execute().returnContent().asString();
        System.out.println(idTweet);

      System.out.println(Request.get(URI).addHeader("Accept", "text/plain").execute().returnContent());

      /* Insert code for Task #5 here */

      System.out.println("Vols esborrar el tweet de prova? (yes/no)");

      Scanner sc = new Scanner(System.in);
      String answer = sc.nextLine();
      sc.close();

      if (answer.equals("yes")) {
          Request.post(URI).addHeader("Accept", "text/plain").bodyForm(Form.form().add("deleteTweet",  idTweet).build()).execute();
      }



  }
}