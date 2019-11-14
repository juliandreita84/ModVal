package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;
import org.springframework.jms.core.JmsTemplate;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class SendMessageTh implements Runnable{

	public static HttpExchange he;
	public static Queue queue;
	public static JmsTemplate jmsTemplate;
	 public  static Map <String, Object> route_data = new HashMap<String, Object>();
	 public  static Map <String, HttpExchange> response_objects_ht = new HashMap<String, HttpExchange>();
	 public  static Map <String, OutputStream> response_objects_os = new HashMap<String, OutputStream>();
	 public  static Map <String, Object> message_factory = new HashMap<String, Object>();
	 private static Map <String, Object> dataConfig =new HashMap<String, Object>();
	 private static HttpExchange heContext;
	 public static int id_message=0;
	 static OutputStream os;
	
	public SendMessageTh(HttpExchange h,Queue q, JmsTemplate j,  Map <String, Object>  rd,Map <String, HttpExchange> roht,  Map <String, OutputStream> roos, Map <String, Object> dc)
	{
		this.he=h;
		this.queue=q;
		this.jmsTemplate =j;
		this.route_data=rd;
		this.response_objects_ht=roht;
		this.response_objects_os=roos;
		this.dataConfig=dc;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		getMessageHttp();
	}

	
	public void getMessageHttp()
	{
		if (he.getRequestMethod().equalsIgnoreCase("POST")) {
			 
            try {

                // REQUEST Headers
                Headers requestHeaders = he.getRequestHeaders();
                requestHeaders.entrySet();

                int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));

                he.getRequestBody();
             // REQUEST Body
                InputStream is = he.getRequestBody();
                byte[] data = new byte[contentLength];
                int length = is.read(data);
                he.getResponseHeaders();
             // RESPONSE Headers
                Headers responseHeaders = he.getResponseHeaders();

                // Send RESPONSE Headers
                //he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength+1000);
                heContext=he;
                os = he.getResponseBody();
                response_objects_ht.put(id_message+"", heContext);
                response_objects_os.put(id_message+"", os);
                message_factory.put(id_message+"",new String(data));
                

                String port=""+he.getLocalAddress().getPort();
                Map<String, Object> data_send = new HashMap<String, Object>();
                
                data_send=constructMessageRules(port,new String(data),ParamDefinitions._CONST_TYPE_IN,id_message);
               // this.jmsTemplate.convertAndSend(this.queue, data_send);
                
       		 //Serializo por un Hilo para enviar a la cola
       		 SendQueue sq = new  SendQueue(queue, jmsTemplate, data_send);
       		 Thread nt = new Thread(sq);
       		 nt.start();
                
                sendMessageToRemote(new String(data),id_message+"");
                id_message++;
                //os.write(data);
                

                //he.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	
	@SuppressWarnings("unchecked")
	public void sendMessageToRemote(String message, String id_msg)
	{

    	try {
    		Map <String,String> detail=new HashMap<String, String>();
    		detail = (Map<String, String>) dataConfig.get("0");
			sendMessage(detail.get("ruta_salida"),message,id_msg,ParamDefinitions._CONST_TYPE_IN  );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String urlIn,String message, String id_msg ,String tipo)
			throws MalformedURLException,
			IOException {
		 
		//Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		String wsURL = urlIn ;
		String myEnv = System.getenv("env_name");
		if (tipo.equals(ParamDefinitions._CONST_TYPE_OUT ))
		{ 

            heContext=response_objects_ht.get(id_msg);
            os=response_objects_os.get(id_msg);
			
			heContext.sendResponseHeaders(HttpURLConnection.HTTP_OK, message.length()+1000);
			//System.out.println("mensaje a enviar al origen " + message);
			os.write(message.getBytes());
			
			heContext.close();
			response_objects_ht.remove(id_msg);
			response_objects_os.remove(id_msg);
			
		}
		else
		{
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
	
		//Devuelve la respuesta
		 Map<String, Object> data_send = new HashMap<String, Object>();
		 heContext=response_objects_ht.get(id_msg);
		 String port=""+heContext.getLocalAddress().getPort();
		 data_send=constructMessageRules(port,outputString,ParamDefinitions._CONST_TYPE_OUT ,id_message);
         //this.jmsTemplate.convertAndSend(this.queue, data_send);
		 
		 //Serializo por un Hilo para enviar a la cola
		 SendQueue sq = new  SendQueue(queue, jmsTemplate, data_send);
		 Thread nt = new Thread(sq);
		 nt.start();
		
	
		//Construye al log
 		Map <String,String> detail=new HashMap<String, String>();
 		detail = (Map<String, String>) dataConfig.get("0");

		sendMessage(detail.get("ruta_respuesta"),outputString, id_msg ,ParamDefinitions._CONST_TYPE_OUT);
	
		
		}
		
		/*if (outputString!="")//With Data
		{
			String puerto= (String) message_full.get("puerto").toString();
			String id_msg= (String) message_full.get("id_mensaje").toString();
			Map<String, Object> data_send = new HashMap<String, Object>();
			data_send=constructMessageRules(puerto,outputString,ParamDefinitions._CONST_TYPE_OUT,Integer.parseInt(id_msg));
			this.jmsTemplate.convertAndSend(this.queue, data_send);
			//System.out.println("Salida "+ outputString);
			//System.out.println("Mensaje enviado a la cola " + this.queue + " =>" + data_send  );
		}*/
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
        if (tipo.equals(ParamDefinitions._CONST_TYPE_IN))
        {
        	data_send.put("ruta_respuesta", detail.get("ruta_salida").toString());
            data_send.put("app_origen", detail.get("app_origen").toString() );
            data_send.put("app_destino", detail.get("app_destino").toString() );
        }else
        {
        	data_send.put("ruta_respuesta", detail.get("ruta_respuesta").toString());	
            data_send.put("app_origen", detail.get("app_destino").toString() );
            data_send.put("app_destino", detail.get("app_origen").toString() );
        }
        data_send.put("formato_msg", detail.get("formato").toString() );
        //System.out.println("envio " + data_send);
        //System.out.println("datos config msg " + detail);
        //System.out.println("jms" + this.jmsTemplate );
        return data_send;
	}
	
}
