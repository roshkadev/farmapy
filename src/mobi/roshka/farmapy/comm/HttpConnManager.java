package mobi.roshka.farmapy.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mobi.roshka.farmapy.exception.AcataAppException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


import android.util.Log;


class HttpConnManager {
	
	public enum RequestType {
		HTTP_POST, HTTP_GET
	}
	
	public static String get(String url) throws AcataAppException{
		return getContent(RequestType.HTTP_GET, url, null);
	}

	public static String get(String url, Map<String, String> params) throws AcataAppException{
		return getContent(RequestType.HTTP_GET, url, params);
	}

	public static String post(String url) throws AcataAppException{
		return getContent(RequestType.HTTP_POST, url, null);
	}

	public static String post(String url, Map<String, String> params) throws AcataAppException{
		return getContent(RequestType.HTTP_POST, url, params);
	}
	
//	public static byte[] getContentByteArray(String url)throws AcataAppException{
//		HttpConnectionFactory factory = new HttpConnectionFactory(url);
//		while( true ) {
//			try {
//				HttpConnection connection = factory.getNextConnection();
//				try {
//					connection.setRequestMethod("GET");
//					connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");										
//								
//					
//					InputStream is = connection.openInputStream();
//					//do something with the input stream
//					
//					byte[] result = streamToByteArray(is);					
//
//					if(true) {
//						return result;
//					}
//				}catch(IOException e) {
//					//Log the error or store it for displaying to
//					//the end user if no transports succeed
//					throw new AcataAppException(AcataAppException.COMMUNICATION_EXCEPTION, AcataAppException.TIME_OUT);
//				}
//			}catch( NoMoreTransportsException e ) {
//				throw new AcataAppException(AcataAppException.COMMUNICATION_EXCEPTION, AcataAppException.CONECTION_METHOD_EXAUSTED);
//			}
//		}				
//		
//	}
	
	private static String getContent(RequestType method, String url, Map<String, String> params) throws AcataAppException{
		Log.d("HttpConnManager", String.format("Calling %s [%s] begins", method, url));
		switch (method) {
		case HTTP_POST:
			String postResult;
			postResult = getPostContent(url, params); 
			Log.d("HttpConnManager", String.format("Calling [%s] ends", url));
			return postResult;
		case HTTP_GET:			
			String getResult;
			getResult = getGetContent(url, params); 
			Log.d("HttpConnManager", String.format("Calling [%s] ends", url));
			Log.d("HttpConnManager", String.format("result", getResult));
			return getResult;
		default:
			throw new AcataAppException();
		}
	}
	
	private static String getPostContent(String url, Map<String, String> params) throws AcataAppException{
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	    	if(params != null && params.size()>0){
		        // Add your data
	    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
		    	for(String key : params.keySet()){			        
			        nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
		    	}
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    	}

	    	// Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        return inputStreamToString(response.getEntity().getContent());
	        
	    } catch (ClientProtocolException e) {
	    	throw new AcataAppException("ClientProtocolException");
	    } catch (IOException e) {
	    	throw new AcataAppException("IOException");
	    }
		
	}
	
	private static String getGetContent(String url, Map<String, String> params) throws AcataAppException{
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

	    //HttpClient httpclient = new DefaultHttpClient();
	    String queryString = "";
	    
	    
    	if(params != null && params.size()>0){
    		int i = 0;
    		queryString = "?";
	    	for(String key : params.keySet()){
	    		queryString = queryString + key + "=" + params.get(key);
	    		i++;
		        if(i<params.size()){
		        	queryString = queryString + "&";
		        }
		        
	    	}
    	}


	    try {
	    	// Execute HTTP Get Request
	    	Log.i("HttpConnManager", url + queryString);
		    HttpGet httpget = new HttpGet(url + queryString);

	    	HttpResponse response = httpClient.execute(httpget);
	        return inputStreamToString(response.getEntity().getContent());
	        
	    } catch (ClientProtocolException e) {
	    	Log.e("HttpsConnManager", e.getMessage(), e);
	    	throw new AcataAppException("ClientProtocolException");
	    } catch (IOException e) {
	    	Log.e("HttpsConnManager", e.getMessage(), e);
	    	throw new AcataAppException("No se encontr� el servidor o se cort� la comunicaci�n(IOException).");
	    }
		
	}
	
	// Fast Implementation
	private static String inputStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    try {
			while ((line = rd.readLine()) != null) {				
			    total.append(line); 
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    // Return full string	    
	    return total.toString() ;
	}


}
