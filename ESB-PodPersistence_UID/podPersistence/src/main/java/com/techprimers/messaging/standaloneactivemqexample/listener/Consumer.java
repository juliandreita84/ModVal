package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

import core.ServiceBD;
import dbConnector.GenerateAlerts;
import dbConnector.MongoBD;

@Component
public class Consumer implements Runnable{
	
	public static int contador=0;
	    
	public Consumer(String a)
	{
		
	}
	
	public Consumer()
	{
		// Reciver reciver = new Reciver();
	     //reciver.createRecieveTask();
		if (s.m.getMongoClient()!= null)
		{
			if (!s.m.getMongoClient().getConnector().isOpen())
			{
				s.m.getMongoClient().close();
				s.m.createConnection(s.bdname);
			}
		}else
		{
			s.m.createConnection(s.bdname);
		}
		
	   /* ScheduledExecutorService scheduler
        = Executors.newSingleThreadScheduledExecutor();
		
	    if (contador==0)
	    {
		Runnable task = new Consumer("");
        int initialDelay = 5;
        int periodicDelay = 5;
        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay,
                TimeUnit.SECONDS
        );}
        contador++;
        
        System.out.println("Inicia Task");*/
	}
	
	private static ServiceBD s = new ServiceBD();
	private BasicDBObject resolveFunctionMessage(JSONObject  json)
	{
		BasicDBObject outMsg= new BasicDBObject();
		boolean flag_auto_inc=false;
		int l =0 ;
		//JSONArray array = json.getJSONArray("*");
		
		Iterator<?> permisos = json.keys();
		while(permisos.hasNext() ){
			l++;
		    String key = (String)permisos.next();
		    try {
				if (json.getString(key).equals("FX_CREATE_DATE"))
				{
					outMsg.put(key, new Date());
				}
				else if (json.getString(key).equals("FX_AUTO_INCREMENT"))
				{
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ParamDefinitions._CONST_FORMAT_ID_DATA);

					 String id = simpleDateFormat.format(new Date());
					 outMsg.put(key, id);
					//outMsg.put(key, s.getNextSeq(s.COLLECTION_TAG_LOG_SEQUENS, s.bdname, s.COLLECTION_LOG_SEQUENS));
					flag_auto_inc=true;
				}
				else
				{
					outMsg.put(key, json.getString(key));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		        //out.write(json.getJSONObject(key).getJSONArray(key2).toString()+"<br />");
		    //}
		}
		
		if (flag_auto_inc==false)
		{
			outMsg.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._01_WITHOUT_AUTOINCREMENT);
			return outMsg;
		}
		if (l==0)
		{
			outMsg.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._02_WITHOUT_DATA);
			return outMsg;
		}
		
		outMsg.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK);
		return outMsg;
	}
	
    @SuppressWarnings("deprecation")
	@JmsListener(destination = "BusPersistence")
    public void consumeWeb(Map<String,String> message) {
    	try {
        ///System.out.println("Received Message: " + message);    	
    	BasicDBObject json = new BasicDBObject();
    	JSONObject jo= new JSONObject(message);	
		json=resolveFunctionMessage(jo);
		if (json.get(ParamDefinitions._CONST_RESPONSE).equals(ParamDefinitions._00_MESSAGE_OK))
		{
			
			//json_arr.add(json);
			
			 MongoBD sq = new  MongoBD(s.m.getDatabase(),s.m.getMongoClient(),json,s.COLLECTION_LOG_BUS_INTEGRATIONS);
			 Thread nt = new Thread(sq);
			 nt.start();
			 
	    	//Hilo para las alertas
			 GenerateAlerts al = new GenerateAlerts(message, Integer.parseInt(ParamDefinitions._CONST_UMBRAL), ParamDefinitions._CONST_URL_ALERTAS);
			 Thread nt1 = new Thread(al);
			 nt1.start();

			 
			 //System.out.println("Resultado de la operacion " + result);
		}
    	//json=resolveFunctionMessage(com.mongodb.util.JSON.parse(message));
//			json = BasicDBObject.parse( parser.parse(message));
    	}
    	catch(Exception e )
    	{
    		e.printStackTrace();
    	}
    }
    
    
    
    
    
public BasicDBObject consume(Map<String,String> message) {
    	
     //   System.out.println("Received Message: " + message);    	
    	BasicDBObject json = new BasicDBObject();
    	JSONObject jo= new JSONObject(message);	
		json=resolveFunctionMessage(jo);
		if (json.get(ParamDefinitions._CONST_RESPONSE).equals(ParamDefinitions._00_MESSAGE_OK))
		{
			s.m.createConnection(s.bdname);
	    	boolean result= s.writeLog(json);
	    	//System.out.println("Resultado de la operacion " + result);
		}
		return json;
    	//json=resolveFunctionMessage(com.mongodb.util.JSON.parse(message));
//			json = BasicDBObject.parse( parser.parse(message));
    }

@SuppressWarnings("unchecked")
@Override
public void run() {
	// TODO Auto-generated method stub
	
	/* MongoBD sq = new  MongoBD(s.m.getDatabase(),s.m.getMongoClient(),json_arr,s.COLLECTION_LOG_BUS_INTEGRATIONS);
	 Thread nt = new Thread(sq);
	 nt.start();
	 System.out.println("ejecuta "+ json_arr.size());
	 json_arr.clear();*/
	 
		
}
}
