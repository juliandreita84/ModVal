package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.ws.rs.Produces;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Consumer extends RouteBuilder implements HttpHandler {
	private String detail_error="";
	private static HttpExchange heContext;
	public static Map<String,HttpServer> tcpServer = new HashMap<String,HttpServer>();
	 public  static Map <String, Object> route_data = new HashMap<String, Object>();
	 public  static Map <String, HttpExchange> response_objects_ht = new HashMap<String, HttpExchange>();
	 public  static Map <String, OutputStream> response_objects_os = new HashMap<String, OutputStream>();
	 public  static Map <String, CamelContext> camel_objects = new HashMap<String, CamelContext>();
	 
	 
	 public  static Map <String, Object> message_factory = new HashMap<String, Object>();
	 public static Map<String,BasicDBObject> schedulerTask = new HashMap<String,BasicDBObject>();
	 private static Map <String, Object> dataConfig =new HashMap<String, Object>();
	 public static Map<String , BasicDBObject>  cursor_routes = new HashMap<String , BasicDBObject> ();
	 private static ConsumeWebService consumeWebService = new ConsumeWebService();
	 public static int id_message=0;
	 
	 public static boolean flag_task=false;
	 
	 private static Config config = new Config();
	 private static CamelContext camelContext[];
	 private static CamelContext camelContext_OUT;
	 public static int id_ruta_camel=0;
		ScheduledExecutorService scheduler
	    = Executors.newSingleThreadScheduledExecutor();
		 ControllerScheduler sq_tmp = new  ControllerScheduler();
	 
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
    public  static Map <String, Queue> queue_arr = new HashMap<String, Queue>();
    public  static Map <String, ConsumeBean> beantoConsume_arr = new HashMap<String, ConsumeBean>();
    static Queue queue_message;
    
    static OutputStream os;
    public ConsumeBean beantoConsume;
    public ConsumeBean beantoConsume_OUT;
    
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
    	this.queue = config.queue();
		this.jmsTemplate=config.jmsTemplateManual();
		this.queue_message=config.queue();
    	
    	loadConfig();
    	
		try {
			configure();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    
    private void loadConfigTaskservices()
    {
		int l=0;
    	//Get Integrations and Config Routes0
    			if (cursor_routes.size()==0)
    	    	{
    				cursor_routes = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_TASK_RULES);
    	    	}
    	    	//System.out.println("cursor_routes=" + cursor_routes);
    			
    			if (cursor_routes.size()>0)
    			{
    			for (int w=0; w<cursor_routes.size(); w++)
    			{
    				BasicDBObject o_d = cursor_routes.get(w+"");
    				BasicDBObject k = new BasicDBObject();
    				
//    				if (/*_VAR_CONFIG_02.equals("ALL")||*/_VAR_CONFIG_02.equals(o_d.get("id_integracion").toString()))
    				{
    					
    					k.put("_id", o_d.get("_id").toString());
    					k.put("id_integracion", o_d.get("id_integracion").toString());
    					k.put("nombre_tarea", o_d.get("nombre_tarea").toString());
    					k.put("ruta_tarea", o_d.get("ruta_tarea").toString());
    					k.put("tarea_puerto", o_d.get("tarea_puerto").toString());
    					k.put("tarea_usuario",o_d.get("tarea_usuario").toString());
    					k.put("tarea_clave", o_d.get("tarea_clave").toString());
    					k.put("tarea_patron", o_d.get("tarea_patron").toString());
    					k.put("tipo_tarea", o_d.get("tipo_tarea").toString());
    					k.put("dias_ejecucion",o_d.get("dias_ejecucion").toString());
    					k.put("hora_inicio_ejecucion", o_d.get("hora_inicio_ejecucion").toString());
    					k.put("hora_siguiente_ejecucion", o_d.get("hora_siguiente_ejecucion").toString());
    					k.put("tiempo_siguiente_intento", o_d.get("tiempo_siguiente_intento").toString());
    					k.put("numero_intentos", o_d.get("numero_intentos").toString());
    					k.put("numero_intentos_ejecutados", o_d.get("numero_intentos_ejecutados").toString());
    					k.put("ejecutando", "N");
	    				
	    				schedulerTask.put(l+"",k);
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
    				try {
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
    				}catch(Exception e)
    				{}
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
    		initServices(jmsTemplate);
    	}
    	
    	if (_VAR_CONFIG_01.equals(ParamDefinitions._CONST_TYPE_SERVICES_FILE ))
    	{
    		loadConfigTaskservices();
    		initServiceTask(jmsTemplate);
    	}
			
	}


@SuppressWarnings({ "unchecked", "deprecation" })
public void initServiceTask(JmsTemplate jmsTemplate)
{
	 ControllerScheduler sq = new  ControllerScheduler();
	 sq_tmp.stop();
	 try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Thread nt = new Thread();
	
	sq.queue_log=config.queue_IN();
	sq.jmsTemplate=jmsTemplate;
	sq.executedTask.clear();
	sq.schedulerTask=this.schedulerTask;
	
	SimpleDateFormat time_compare_1 = new SimpleDateFormat("HH:mm:ss");
	
	sq.name_run="Hilo="+time_compare_1.format(new Date());
	sq.flag_run=true;
System.out.println("tareas "  +  sq.schedulerTask);
System.out.println("corre con "  +  sq.name_run);
	
	// nt=null;
	 nt=new Thread(sq);
	 nt.start();
     sq_tmp=sq;
	
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
				this.jmsTemplate.convertAndSend(this.queue_message, message);
		
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
		this.queue = config.queue();
		this.jmsTemplate=config.jmsTemplateManual();
		
		//System.out.println("jms " + jmsTemplate);
		HttpServer server = HttpServer.create(new InetSocketAddress(port_number), 0);

	    server.createContext("/PodService", new Consumer());
	    server.setExecutor(null); // creates a default executor
	    server.start();
	    
	
	}
	
	
	@Override
	    public void handle(HttpExchange he) throws IOException {
	   
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
	                   //int length = is.read(data);
	                    
	                    
	                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	                    int read;
	                    //byte[] input = new byte[contentLength];
	                    while ( -1 != ( read = is.read( data ) ) ) {
	                        buffer.write( data, 0, read );
	                    }
	                    data = buffer.toByteArray();
	    
	                    
	                    he.getResponseHeaders();
	                 // RESPONSE Headers
	                    Headers responseHeaders = he.getResponseHeaders();
	 
	                    // Send RESPONSE Headers
	                    //he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength+1000);
	                    heContext=he;
	                    os = he.getResponseBody();
	                    
	                    String pt = he.getLocalAddress().getPort()+"";
	                    
	                    ConsumeBean beantoConsume_te;
	                    beantoConsume_te = beantoConsume_arr.get(pt+"");
	                    beantoConsume_te.heContext=heContext;
	                    beantoConsume_te.route_data=route_data;
	                    beantoConsume_te.os=os;
	                  //  System.out.println("tx 1 "+ new String(data));
               
	                    this.jmsTemplate.convertAndSend(queue_arr.get(he.getLocalAddress().getPort()+""), new String(data));
	                    
	 
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

	
		
	@SuppressWarnings({ "null", "unchecked" })
	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		//Se pega a la cola de AMQ con Camel
		id_ruta_camel=0;
 		try {
 			 JndiContext jndiContext = new JndiContext();
 			JndiContext jndiContext_arr =  new JndiContext() ;
 			JndiContext jndiContext_OUT = new JndiContext();
 				
 			ConsumeBean beantoConsume_tmp = null;
 			CamelContext camelContext_arr;
 			//System.out.println("Rutas leidas " + dataConfig);
 			//Destruit los objetos de camel existentes
 			for (int w=0; w<camel_objects.size(); w++) {
 				
 				camelContext_arr=camel_objects.get(""+w);
 				camelContext_arr.stop();
 			}
 			for (int i =0; i< dataConfig.size(); i++)
 			{
 			
 			jndiContext_arr=new JndiContext(); 
 			Map <String,String> detail=new HashMap<String, String>();
 			detail = (Map<String, String>) dataConfig.get(""+i);
 			String rt=detail.get("ruta_salida");
 			String pt=detail.get("puerto");
 			String nombre=detail.get("nombre_ruta");
 			beantoConsume_tmp = new ConsumeBean(rt,jmsTemplate,new ActiveMQQueue("Message_OUT_"+id_ruta_camel),heContext,os,config.queue_IN());
 			beantoConsume_arr.put(pt, beantoConsume_tmp);
 			jndiContext_arr.bind("testBean"+i, beantoConsume_tmp);
 			queue_arr.put(pt, new ActiveMQQueue("Message_IN_"+id_ruta_camel));
 			

 			DefaultCamelContext context = new DefaultCamelContext(jndiContext_arr);
 			context.setName(id_ruta_camel + ". " + nombre);
 			camelContext_arr = context;

 			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER_USER,ParamDefinitions._CONST_AQM_SERVER_USER, ParamDefinitions._CONST_AQM_SERVER );
 			camelContext_arr.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
 			camelContext_arr.addRoutes(new RouteBuilder() {
 				public void configure() {
 					from("jms:queue:Message_IN_"+id_ruta_camel+"?asyncConsumer=true&concurrentConsumers=10").routeId(id_ruta_camel + ". " +"Envio del Mensaje al Destino ")
 							//.to("bean:testBean?method=hello")
 					       // .to("log:Message_IN_"+id_ruta_camel)
 							.to("direct:Message_IN_"+id_ruta_camel);
 					
 					from("direct:Message_IN_"+id_ruta_camel).routeId(id_ruta_camel + ". " +"Consumo del Mensaje ")
 					//  .transform(simple("direct:queueResult output: ${body}"))
 					//.to("stream:out")
 					//.to("http://127.0.0.1/busmanager/resp.php")
 					.to("bean:testBean"+id_ruta_camel+"?method=sendRequest");
 					
 					from("jms:queue:Message_OUT_"+id_ruta_camel+"?asyncConsumer=true&concurrentConsumers=10").routeId(id_ruta_camel + ". " +"Retorno de la Respuesta " )
						.to("bean:testBean"+id_ruta_camel+"?method=sendResponse");

 				}
 			});
 			camel_objects.put(id_ruta_camel+"", camelContext_arr);
 			id_ruta_camel++;
 			camelContext_arr.start();
 			
 			System.out.println("Creo ruta " + camelContext_arr.getName());
 			}
 			
/*
 			 beantoConsume = new ConsumeBean(_VAR_CONFIG_04,jmsTemplate,config.queue2(),heContext,os,config.queue_IN());
 			beantoConsume_OUT = new ConsumeBean(_VAR_CONFIG_04,jmsTemplate,config.queue2(),heContext,os,config.queue_IN());
 			 jndiContext.bind("testBean", beantoConsume);
 			jndiContext_OUT.bind("testBean_OUT", beantoConsume_OUT);
 			camelContext = new DefaultCamelContext(jndiContext);
 			camelContext_OUT = new DefaultCamelContext(jndiContext_OUT);
 			
 			camelContext.addRoutes(new RouteBuilder() {
 				public void configure() {
 					from("activemq:queue:Message_IN?asyncConsumer=true&concurrentConsumers=10")
 							//.to("bean:testBean?method=hello")
 					        .to("log:Message_IN")
 							.to("direct:Message_IN");
 					
 					from("direct:Message_IN")
 					//  .transform(simple("direct:queueResult output: ${body}"))
 					//.to("stream:out")
 					//.to("http://127.0.0.1/busmanager/resp.php")
 					.to("bean:testBean?method=sendRequest");
 					
 					from("activemq:queue:Message_OUT?asyncConsumer=true&concurrentConsumers=10")
						.to("bean:testBean?method=sendResponse");

 				}
 			});
 			
 			camelContext_OUT.addRoutes(new RouteBuilder() {
 				public void configure() {
 					from("activemq:queue:MessageB_IN?asyncConsumer=true&concurrentConsumers=10")
 							//.to("bean:testBean?method=hello")
 					        .to("log:MessageB_IN")
 							.to("direct:MessageB_IN");
 					
 					from("direct:MessageB_IN")
 					//  .transform(simple("direct:queueResult output: ${body}"))
 					//.to("stream:out")
 					//.to("http://127.0.0.1/busmanager/resp.php")
 					.to("bean:testBean_OUT?method=sendRequest");
 					
 					from("activemq:queue:MessageB_OUT?asyncConsumer=true&concurrentConsumers=10")
						.to("bean:testBean_OUT?method=sendResponse");

 				}
 			});
 			//roducerTemplate template = camelContext.createProducerTemplate();
 			camelContext.start();
 			camelContext_OUT.start();*/
 		}catch (Exception e
 				)
 		{
 			e.printStackTrace();
 		}	
	}

}
