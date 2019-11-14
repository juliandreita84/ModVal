package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;
import com.techprimers.messaging.standaloneactivemqexample.config.Config;

@Component
public class Consumer implements HttpHandler{
	private String detail_error="";
	private static HttpExchange heContext;
	public static Map<String,HttpServer> tcpServer = new HashMap<String,HttpServer>();
	 public  static Map <String, Object> route_data = new HashMap<String, Object>();
	 public  static Map <String, HttpExchange> response_objects_ht = new HashMap<String, HttpExchange>();
	 public  static Map <String, OutputStream> response_objects_os = new HashMap<String, OutputStream>();
	 
	 public  static Map <String, Object> message_factory = new HashMap<String, Object>();
	 
	 private static Map <String, Object> dataConfig =new HashMap<String, Object>();
	 public static Map<String , BasicDBObject>  cursor_routes = new HashMap<String , BasicDBObject> ();
	 private static ConsumeWebService consumeWebService = new ConsumeWebService();
	 public static int id_message=0;
	 private static Config config = new Config();

	 
	 //Variables globales
	 /**
	  * _VAR_CONFIG_01 = tipo de servicio (01: Web Service, 02: Bases de datos, 03: Archivos, 04: dblinks)
	  * _VAR_CONFIG_02 = id de la ruta (ALL: trae e implementa todas, X: sube la especificada)
	  * _VAR_CONFIG_03 = puerto del servicio (se usa como respaldo en caso de que la bd no este disponible)
	  * _VAR_CONFIG_04 = url de la aplicacion destino a consumir
	  * _VAR_CONFIG_05 = formato de la mensajeria (XML, REST, FILE, BD)
	  * _VAR_CONFIG_06 = aplicacion origen (se usa de respaldo cuando no esta disponible la BD)
	  * _VAR_CONFIG_07 = aplicacion destino (se usa de respaldo cuando no esta disponible la BD)
	  * _VAR_CONFIG_08 = ruta de retorno de la respuesta (se usa de respaldo cuando no esta disponible la BD)
	  */
	 public static String _VAR_CONFIG_01="";
	 public static String _VAR_CONFIG_02="";
	 public static String _VAR_CONFIG_03="";
	 public static String _VAR_CONFIG_04="";
	 public static String _VAR_CONFIG_05="";
	 public static String _VAR_CONFIG_06="";
	 public static String _VAR_CONFIG_07="";
	 public static String _VAR_CONFIG_08="";
	 
    @Autowired
    static JmsTemplate jmsTemplate;

    @Autowired
    static Queue queue;
    
    static OutputStream os;
    
    public void ConsumerStart()
    {
    	_VAR_CONFIG_01 = System.getenv("_VAR_CONFIG_01");
    	_VAR_CONFIG_02 = System.getenv("_VAR_CONFIG_02");
    	_VAR_CONFIG_03 = System.getenv("_VAR_CONFIG_03");
    	_VAR_CONFIG_04 = System.getenv("_VAR_CONFIG_04");
    	_VAR_CONFIG_05 = System.getenv("_VAR_CONFIG_05");
    	_VAR_CONFIG_06 = System.getenv("_VAR_CONFIG_06");
    	_VAR_CONFIG_07 = System.getenv("_VAR_CONFIG_07");
    	_VAR_CONFIG_08 = System.getenv("_VAR_CONFIG_08");
    	loadConfig();
    	initServices(jmsTemplate);
		this.queue = config.queue_IN();
		this.jmsTemplate=config.jmsTemplateManual();
    }
    
    public void ConsumerStop()
    {
    	for (int k =0 ; k<dataConfig.size();k++) {
    		HttpServer server=tcpServer.get(k+"");
    		server.stop(0);
    		tcpServer.put(k+"", server);
    	}
    }
    
    
    @SuppressWarnings("unchecked")
	public void initServices(JmsTemplate jmsTemplate)
	{
		 Map <String, Object> dataConfig_local =new HashMap<String, Object>();
		for (int k =0 ; k<dataConfig.size();k++) {
			Map <String,String> detail=new HashMap<String, String>();
			detail = (Map<String, String>) dataConfig.get(""+k);
			String port = detail.get("puerto");
			route_data.put(port, detail);
		}
		
		for (int k =0 ; k<dataConfig.size();k++) {
			Map <String,String> detail=new HashMap<String, String>();
			detail = (Map<String, String>) dataConfig.get(""+k);
			int port_service = Integer.parseInt(detail.get("puerto") );
			
			HttpServer server = null;
			try {
				server = HttpServer.create(new InetSocketAddress(port_service), 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    server.createContext("/PodService", new Consumer());
		    server.setExecutor(null); // creates a default executor
		    server.start();
			
			tcpServer.put(k+"", server);
			
		}//System.out.println("servicios "+ tcpServer);
	}
    

    
    private void loadConfigWebservices()
    {
		int l=0;
    	//Get Integrations and Config Routes0
    			if (cursor_routes.size()==0)
    	    	{
    				cursor_routes = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_INTEGRATIONS);
    	    	}
    	    	//System.out.println("cursor_routes=" + cursor_routes);
    			
    			if (cursor_routes.size()>0)
    			{
    			for (int w=0; w<cursor_routes.size(); w++)
    			{
    				BasicDBObject o_d = cursor_routes.get(w+"");
    				Map <String,String> detail=new HashMap<String, String>();
    				
    				if (/*_VAR_CONFIG_02.equals("ALL")||*/_VAR_CONFIG_02.equals(o_d.get("id_integracion").toString()))
    				{
	    				detail.put("id_integracion", o_d.get("id_integracion").toString());
	    				detail.put("nombre_ruta", o_d.get("nombre_ruta").toString());
	    				detail.put("puerto", o_d.get("puerto").toString());
	    				detail.put("ruta_salida", o_d.get("ruta_salida").toString());
	    				detail.put("ruta_respuesta", o_d.get("ruta_respuesta").toString());
	    				detail.put("formato", o_d.get("formato").toString());
	    				detail.put("app_origen", o_d.get("app_origen").toString());
	    				detail.put("app_destino", o_d.get("app_destino").toString());
	    				dataConfig.put(l+"",detail);
	    				_VAR_CONFIG_02=o_d.get("id_integracion").toString();
	    				_VAR_CONFIG_03= o_d.get("puerto").toString();
	    				_VAR_CONFIG_04=o_d.get("ruta_salida").toString();
	    				_VAR_CONFIG_08=o_d.get("ruta_respuesta").toString();
	    				_VAR_CONFIG_05=o_d.get("formato").toString();
	    				_VAR_CONFIG_06=o_d.get("app_origen").toString();
	    				_VAR_CONFIG_07=o_d.get("app_destino").toString();
	    				l++;
    				}
    			}
    			}else //Usa la configuracion de respaldo para levantar el servicio
    			{
    				Map <String,String> detail=new HashMap<String, String>();
    				detail.put("id_integracion",_VAR_CONFIG_02);
    				detail.put("nombre_ruta", _VAR_CONFIG_02);
    				detail.put("puerto",_VAR_CONFIG_03);
    				detail.put("ruta_salida", _VAR_CONFIG_04);
    				detail.put("ruta_respuesta", _VAR_CONFIG_08);
    				detail.put("formato", _VAR_CONFIG_05);
    				detail.put("app_origen", _VAR_CONFIG_06);
    				detail.put("app_destino", _VAR_CONFIG_07);
    				dataConfig.put(l+"",detail);
    			}
    			
    			
    }
    
    private void loadConfig()
	{
    	
    	//Validamos que tipo de cargue de servicios haremos
    	if (_VAR_CONFIG_01.equals(ParamDefinitions._CONST_TYPE_SERVICES_WEBSERVICE ))
    	{
    		loadConfigWebservices();
    	}
    	
		
		
			
	}
    
    
    
    

		/*@JmsListener(destination = "BusService_OUT")
	public void consumeBusService_OUT(Map<String,String> message)
	{

        //System.out.println("Received Message from BusService_OUT: " + message);    	
    	@SuppressWarnings("deprecation")
    	
    	BasicDBObject json = new BasicDBObject();
    	JSONObject jo= new JSONObject(message);
    	String only_message = message.get("mensaje");
    	String ruta_respuesta = message.get("ruta_salida");
    	try {
			sendMessage(ruta_respuesta,only_message,message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
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
			String response_consume ="00";
			String response_consume_detail =""; 
			
			try {
				
			
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
	
		//Devuelve la respuesta
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

	/*public BasicDBObject consumeBusService(Map<String,String> message,Queue queue,JmsTemplate jmsTemplate)
	{

        //System.out.println("Received Message: " + message);    	
    	@SuppressWarnings("deprecation")
    	
    	BasicDBObject json = new BasicDBObject();
    	JSONObject jo= new JSONObject(message);	
		/*json=resolveRules(jo);
		sendLogMessage( jo,json);
		Config con = new Config();
		Map<String,String> message_res=ConstructMsgResponse( jo,json);
		jmsTemplate.convertAndSend(con.getQueueAMQ_OUT(), message_res);*
    	return json;
	}
	*/

	public void createService( Map<String, Object> index_route,int port_number,Queue queue,JmsTemplate jmsTemplate) throws IOException
	{
		route_data =index_route;
		this.queue = config.queue_IN();
		this.jmsTemplate=config.jmsTemplateManual();
		
		//System.out.println("jms " + jmsTemplate);
		HttpServer server = HttpServer.create(new InetSocketAddress(port_number), 0);

	    server.createContext("/PodService", new Consumer());
	    server.setExecutor(null); // creates a default executor
	    server.start();
	}
	
	
	@Override
	    public void handle(HttpExchange he) throws IOException {
	    /*	InputStream  a =  t.getRequestBody();
	    	String b= a.read()
	      byte [] response = "Welcome Real's HowTo test page".getBytes();
	      t.sendResponseHeaders(200, response.length);
	      OutputStream os = t.getResponseBody();
	      os.write(response);
	      os.close();
			InputStream is = t.getRequestBody();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len;
			while ((len = is.read(buffer))>0) {
				bos.write(buffer, 0, len);
			}
			bos.close();
			String data = new String(bos.toByteArray(), Charset.forName("UTF-8"));

			OutputStream os = t.getResponseBody();
		      os.write(data.getBytes());
		      os.close();*/

	    	 //System.out.println("Serving the request");
	    	 
	            // Serve for POST requests only
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
