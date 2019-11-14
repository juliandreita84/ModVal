package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.sun.net.httpserver.HttpExchange;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class ConsumeBean {
	
	public String wsURL="";
    @Autowired
    public static JmsTemplate jmsTemplate;
    public  Queue queue;
    public static Queue queue_log;
    public static HttpExchange heContext;
    public static OutputStream os;
    public static int id_message=0;
    public  static Map <String, Object> route_data = new HashMap<String, Object>();
	String response_consume ="00";
	String response_consume_detail =""; 
    private String port =System.getenv("_VAR_CONFIG_03");
    
	public ConsumeBean(String w,JmsTemplate j,Queue q,HttpExchange h,OutputStream o,Queue q1)
	{
	this.wsURL=w;	
	this.jmsTemplate=j;
	this.queue=q;
	this.queue_log=q1;
	this.heContext=h;
	this.os=o;
	}
	
	public Map<String, Object> constructMessageRules(String port,String data,String tipo,int id_msg)
	{
		Map <String,String> detail=new HashMap<String, String>();
        detail=(Map<String, String>) route_data.get(port);
        Map<String, Object> data_send = new HashMap<String, Object>();
        data_send.put("id_integracion", detail.get("id_integracion").toString() );
        data_send.put("fecha_msg", "'"+ new Date()+"'" );
        data_send.put("mensaje", data );
        data_send.put("puerto", port);
        data_send.put("id_mensaje", id_msg);
        data_send.put("tipo_mensaje", "XML");
        data_send.put("ruta_respuesta", detail.get("ruta_salida").toString());
        data_send.put("app_origen", detail.get("app_origen").toString() );
        data_send.put("app_destino", detail.get("app_destino").toString() );
        
        /*if (tipo.equals(ParamDefinitions._CONST_TYPE_IN))
        {
        	data_send.put("ruta_respuesta", detail.get("ruta_salida").toString());
            data_send.put("app_origen", detail.get("app_origen").toString() );
            data_send.put("app_destino", detail.get("app_destino").toString() );
        }else
        {
        	data_send.put("ruta_respuesta", detail.get("ruta_respuesta").toString());	
            data_send.put("app_origen", detail.get("app_destino").toString() );
            data_send.put("app_destino", detail.get("app_origen").toString() );
        }*/
        data_send.put("formato_msg", detail.get("formato").toString() );

        return data_send;
	}
	
	public void sendResponse (String message)
	{
			try {
				heContext.sendResponseHeaders(HttpURLConnection.HTTP_OK, message.length()+1000);
				 port=""+heContext.getLocalAddress().getPort();
				os.write(message.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("mensaje a enviar al origen " + message);
			heContext.close();
			

			
			sendThreadLog(port,message,ParamDefinitions._CONST_TYPE_OUT,id_message);
	} 
	
	
	
	public void sendThreadLog(String port, String message,String tipo,int id_m)
	{
		 Map<String, Object> data_send = new HashMap<String, Object>();
		 data_send=constructMessageRules(port,new String(message),tipo,id_m);
		 if (tipo.equals(ParamDefinitions._CONST_TYPE_OUT))
		 {
		 data_send.put("respuesta_remota", response_consume);
		 data_send.put("respuesta_remota_detail", response_consume_detail);
		 }
		 SendQueue sq = new  SendQueue(queue_log, jmsTemplate, data_send);
		 Thread nt = new Thread(sq);
		 nt.start();

	}
	
	public void sendRequest(String message) {
			
		

		//Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		String myEnv = System.getenv("env_name");
				//System.out.println("entra "+ message);		
		try {
			id_message++;
			sendThreadLog(port,message,ParamDefinitions._CONST_TYPE_IN,id_message);
			response_consume="00";
			response_consume_detail="";
	URL url = new URL(wsURL);	
	URLConnection connection = url.openConnection();
	HttpURLConnection httpConn = (HttpURLConnection)connection;
	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	String xmlInput =message;
	 
	byte[] buffer = new byte[xmlInput.length()];
	buffer = xmlInput.getBytes();
	bout.write(buffer);
	byte[] b = bout.toByteArray();
	String SOAPAction =wsURL;
	// Set the appropriate HTTP parameters.
	//Poner como variables dentro del objeto de MONGODB OJO (type y el method)
	httpConn.setRequestProperty("Content-Length",
	String.valueOf(b.length));
	httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
	httpConn.setRequestProperty("SOAPAction", SOAPAction);
	httpConn.setRequestMethod("POST");
	httpConn.setReadTimeout(ParamDefinitions._CONST_TIMEOUT_READ );//Establece el tiempo de espera de la lectura de datos
	httpConn.setConnectTimeout(ParamDefinitions._CONST_TIMEOUT_CONN );//Establece el tiempo de espera de la conexion a la ruta remota
	httpConn.setDoOutput(true);
	httpConn.setDoInput(true);
	OutputStream out = httpConn.getOutputStream();
	//Write the content of the request to the outputstream of the HTTP Connection.
	out.write(b);
	out.close();
	//Ready with sending the request.
	 
	//Read the response.
	InputStreamReader isr =
	new InputStreamReader(httpConn.getInputStream());
	BufferedReader in = new BufferedReader(isr);
	 
	//Write the SOAP message response to a String.
	while ((responseString = in.readLine()) != null) {
	outputString = outputString + responseString;
	}
		}catch (Exception e)
		{
			response_consume =ParamDefinitions._03_TIME_OUT ; //Time Out
			response_consume_detail=e.getMessage();
			
		}
		
//System.out.println("llego "+ outputString);
this.jmsTemplate.convertAndSend(this.queue, outputString);
	/*//Devuelve la respuesta
	 Map<String, Object> data_send = new HashMap<String, Object>();
	 heContext=response_objects_ht.get(id_msg);
	 String port=_VAR_CONFIG_03;
	 if (heContext== null)
			 {
	  port=""+heContext.getLocalAddress().getPort();
			 }
	 data_send=constructMessageRules(port,outputString,ParamDefinitions._CONST_TYPE_OUT ,id_message);
     //this.jmsTemplate.convertAndSend(this.queue, data_send);
	 
	 
	 //colocar la respuesta de la accion
	 data_send.put("respuesta_remota", response_consume);
	 data_send.put("respuesta_remota_detail", response_consume_detail);
	 
	 //Serializo por un Hilo para enviar a la cola
	 SendQueue sq = new  SendQueue(queue, jmsTemplate, data_send);
	 Thread nt = new Thread(sq);
	 nt.start();
	

	//Construye al log
		Map <String,String> detail=new HashMap<String, String>();
		detail = (Map<String, String>) dataConfig.get("0");

	sendMessage(detail.get("ruta_respuesta"),outputString, id_msg ,ParamDefinitions._CONST_TYPE_OUT);

	*/
		
	}
}
