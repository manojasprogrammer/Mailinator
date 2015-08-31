package com.mailinator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class Mailinator {
	
	private static final String MAILINATOR_API_ENDPOINT = "https://api.mailinator.com/api";
    private static final String MAILINATOR_INBOX_TEMPLATE_URL = MAILINATOR_API_ENDPOINT + "/inbox?token=%s&to=%s";
    private static final String MAILINATOR_EMAIL_TEMPLATE_URL = MAILINATOR_API_ENDPOINT + "/email?token=%s&msgid=%s";
	
	public static void main(String[] args){
				
   	 	//Register with Mailinator and receive the API_KEY
		String apikey = "5f0ba7637a2049578607c1bb37c3f822";
		// Your email@mailinator.com
        String emailAddress = "email";        
        String myLastMessage = getLastMailinatorMessage(apikey,emailAddress);
        System.out.println("Your Last Message is as follows");
        System.out.println(myLastMessage);
		
	}
	
	private static String getJSONInbox(String apikey, String emailAddress) {
		String emailUrl = String.format(MAILINATOR_INBOX_TEMPLATE_URL, apikey, emailAddress);
        return getStringResponse(emailUrl);
		
	}
	
	private static String getJSONEmail(String apikey, String emailId){
		String emailUrl = String.format(MAILINATOR_EMAIL_TEMPLATE_URL, apikey, emailId);
        return getStringResponse(emailUrl);
		
	}
	
	
	private static String getStringResponse(String url) {
    	System.out.println("URL " + url);
    	String read="error";
    	try{
    		URL urlValue = new URL(url);
    		HttpURLConnection connection = (HttpURLConnection) urlValue.openConnection();
    		connection.setRequestMethod("GET");
    		connection.connect();
    		int code = connection.getResponseCode();
    		if(code == 200){
    			InputStream response = connection.getInputStream();
    			InputStreamReader is = new InputStreamReader(response);
    			BufferedReader br = new BufferedReader(is);
    			read = br.readLine();
    			}
    	}
    	catch(MalformedURLException e){
    		System.out.println("Check the URL and try one more time "+e.toString());
    	}
    	catch(IOException e){
    		System.out.println("Error "+e.toString());
    	}
        return read;
    }
	private static String getLastMailinatorMessage(String apikey, String emailAddress){
		 Map<Long, String> map = new TreeMap<Long, String>();
	        String jsonContent ="";
	        Object obj = new JSONParser();
	        JSONObject jsonObj = null;
	        JSONArray jArray = new JSONArray();
	        
	        jsonContent =getJSONInbox(apikey,emailAddress);
	        if(!jsonContent.equals("error")){
	        	try{
	        	obj = ((JSONParser)obj).parse(jsonContent);
	        	}
	        	catch(Exception e){
	        		System.out.println("Error in parsing the JSON Content "+e);
	        	}
	        jsonObj = (JSONObject)obj;
	        jArray = (JSONArray)jsonObj.get("messages");
	        }
	        
	        String sec_ago ="";
	        Long sec_long =0L;
	        String id="";
	        
	        for(int i =0; i< jArray.size();i++){
	        	jsonObj= (JSONObject)jArray.get(i);
	        	sec_ago = jsonObj.get("seconds_ago").toString();
	        	sec_long = Long.parseLong(sec_ago);
	        	id = jsonObj.get("id").toString();
	        	map.put(sec_long, id);        	
	        }       
	         
	        Map.Entry<Long,String> entry = (map.size() == 0)? null: map.entrySet().iterator().next();
	        
	        String emailId= Boolean.valueOf(entry == null)? "" :entry.getValue();
	        String output ="";
	        
	        if(!emailId.equals("")){
	        jsonContent = getJSONEmail(apikey, emailId);
	        jsonObj = (JSONObject)JSONValue.parse(jsonContent);
	        jsonObj = (JSONObject)(jsonObj).get("data");
	        jArray= (JSONArray)jsonObj.get("parts");        
	        jsonObj = (JSONObject) jArray.get(0);
	        output = jsonObj.get("body").toString();
	        }
	        
	        return output;
	}

}
