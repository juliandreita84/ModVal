package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class ConsumeWebService {
	
	
public Map <String, BasicDBObject>	getInfoConsume(JSONObject consume_data,String collection, String Url)
{
	String data ="";
	try {
		data = POSTRequest(consume_data,collection,Url);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	/*JSONArray k = new JSONArray();

	BasicDBObject f = (BasicDBObject) JSON.parse(data);
	System.out.println("hell"+f);
	*/
	data=data.replaceAll("\\\"", "'");
	String [] y = data.replaceAll("},", "};").split(";");
	 Map <String, BasicDBObject> k = new  HashMap <String, BasicDBObject>();
	for(int i=0; i<y.length;i++ )
	{
		k.put(i+"", (BasicDBObject) JSON.parse(y[i]));
	}

	return k;
}


public String  POSTRequest(JSONObject consume_dat,String collection,String Url) throws IOException {
    final String POST_PARAMS = consume_dat.toString();
    //System.out.println(POST_PARAMS);
    URL obj = new URL(Url);
    HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
    postConnection.setRequestMethod("POST");
    //postConnection.setRequestProperty("userId", "a1bcdefgh");
    postConnection.setRequestProperty("Content-Type", "application/json");
    postConnection.setDoOutput(true);
    OutputStream os = postConnection.getOutputStream();
    os.write(POST_PARAMS.getBytes());
    os.flush();
    os.close();
    int responseCode = postConnection.getResponseCode();
    //if (responseCode == HttpURLConnection.HTTP_CREATED) 
    { //success
        BufferedReader in = new BufferedReader(new InputStreamReader(
            postConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in .readLine()) != null) {
            response.append(inputLine);
        } in .close();
        // print result
        return response.toString();
    } /*else {
        System.out.println("POST NOT WORKED");
    }*/
}


public Map<String , BasicDBObject> constructMessageConsume(JSONObject consume_dat,String collection)
{
	Map<String , BasicDBObject>  objeto= new HashMap<String , BasicDBObject> ();
	JSONObject consume_dat0 = new JSONObject();
	try {
		if (consume_dat==null)
		{
			consume_dat0.put("PARAM_QUERY", "");
		}else
		{
			consume_dat0.put("PARAM_QUERY", consume_dat);
		}
		consume_dat0.put("TYPE_QUERY", "00");
		consume_dat0.put("CONTENT_QUERY", collection);
		//System.out.println(consume_dat0);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	objeto=getInfoConsume(consume_dat0, collection, ParamDefinitions._CONST_CONSUME_WEBSERVICE_POST);
	return objeto;
}


public static void main(String args[])
{
//	ConsumeWebService c = new ConsumeWebService();
//	System.out.println(c.getInfoConsume(consume_dat, collection, Url));
	String l="".replaceAll("},", "};");
	String [] y = l.split(";");
	JSONArray k = new JSONArray();
	for(int i=0; i<y.length;i++ )
	{
		try {
			k.put(i, JSON.parse(y[i]));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
System.out.println(k);
System.out.println(k.length());
} 

}
