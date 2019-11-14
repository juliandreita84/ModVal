package core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

import dbConnector.MongoBD;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.jms.*;
import org.json.JSONException;
import org.json.JSONObject;
import com.mongodb.BasicDBObject;

public class Reciver implements ExceptionListener{
    private final Logger logger = LoggerFactory.getLogger(Reciver.class);
    private static ServiceBD s = new ServiceBD();
    public static ActiveMQConnectionFactory connectionFactory;
    public static Connection connection;
    public static MessageConsumer consumer;
    public static  Session session;
    public static Destination destination;
    public Reciver()
    {
    	try {
    		connectionFactory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER);
        	connection = connectionFactory.createConnection();
            connection.start();
			connection.setExceptionListener(this);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("BusPersistence");
            consumer = session.createConsumer(destination);

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void createRecieveTask() {
        Runnable recTask = () -> {
        	while (true) { 
            try {
                
                Message message = consumer.receive(100);
                //if (message instanceof TextMessage) {
                if (message!=null) {
                    /*TextMessage textMessage = (TextMessage) message;
                	Map<String,String> messageIN = (Map<String, String>) textMessage;
                	BasicDBObject json = new BasicDBObject();
                	JSONObject jo= new JSONObject(messageIN);	
            		json=resolveFunctionMessage(jo);
            		if (json.get(ParamDefinitions._CONST_RESPONSE).equals(ParamDefinitions._00_MESSAGE_OK))
            		{
            			 MongoBD sq = new  MongoBD(s.m.getDatabase(),s.m.getMongoClient(),json,s.COLLECTION_LOG_BUS_INTEGRATIONS);
            			 Thread nt = new Thread(sq);
            			 nt.start();
            		}
            		
                    //String text = textMessage.getText();*/
                   logger.info("Received TextMessage object: " + message);
                }
               /* } else {
                    logger.info("Received other object type with message: " + message);
                }
               */

            } catch (JMSException e) {
                logger.error("Reciver createRecieveTask method error", e);
            }
            
            
           
        	}
        	
        };
        new Thread(recTask).start();
    }

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
					outMsg.put(key, s.getNextSeq(s.COLLECTION_TAG_LOG_SEQUENS, s.bdname, s.COLLECTION_LOG_SEQUENS));
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
    
    
    @Override
    public void onException(JMSException exception) {
        logger.error("Recieve error occured.");
    }
    
    public void closeConn()
    {
    	try {
        	consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
}
