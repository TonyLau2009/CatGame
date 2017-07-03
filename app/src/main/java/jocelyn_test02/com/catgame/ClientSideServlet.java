package jocelyn_test02.com.catgame;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.*;
/**
 * Created by Jocelyn on 22/9/2016.
 */

public class ClientSideServlet {


    public HttpURLConnection connecter(String urlAddress){
        HttpURLConnection urlConnection = null;
      try {
          URL url = new URL(urlAddress);
          urlConnection = (HttpURLConnection) url.openConnection();
          urlConnection.setRequestMethod("POST");
          urlConnection.setConnectTimeout(20000);
      }catch (Exception e){
          e.printStackTrace();
      }
    }

}
