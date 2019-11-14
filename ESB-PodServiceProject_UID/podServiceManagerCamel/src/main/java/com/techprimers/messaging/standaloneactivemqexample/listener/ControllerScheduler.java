package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.util.concurrent.ScheduledExecutorService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Queue;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.springframework.jms.core.JmsTemplate;

import com.mongodb.BasicDBObject;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class ControllerScheduler implements Runnable {
	
	ScheduledExecutorService scheduler
    = Executors.newSingleThreadScheduledExecutor();
	
	public  Map<String,BasicDBObject> schedulerTask = new HashMap<String,BasicDBObject>();
	public static Map<String,BasicDBObject> executedTask = new HashMap<String,BasicDBObject>();
	public  static Map <String, CamelContext> camel_objects = new HashMap<String, CamelContext>();
	String response_consume =ParamDefinitions._00_MESSAGE_OK;
	String response_consume_detail ="";
	String nombre_patron_find =""; 
	public static Queue queue_log;
	public ConsumeObjectTask beantoConsume_tmp = null;
	public BasicDBObject data = new BasicDBObject();
	public static ControllerScheduler controller;
	public static JmsTemplate jmsTemplate;
	public static int id_message=0;
	public int id_key=0;
	public static boolean flag_run =true;
	public String name_run ="";
	
	
	public static void main(String rag[])
	{
		//ControllerScheduler c = new ControllerScheduler(); 
		//c.createService();
		Calendar now = Calendar.getInstance();
		int dia = now.get(Calendar.DAY_OF_WEEK)-1;
		System.out.println("Hoy es : " + dia);
		
	}
	
	public void loadSchedulerTask() {
		BasicDBObject k = new BasicDBObject();
		k.put("id", "1");
		k.put("id_integracion", "RQ001");
		k.put("nombre_tarea", "Prueba");
		k.put("ruta_tarea", "localhost");
		k.put("tarea_puerto", "21");
		k.put("tarea_usuario", "admin");
		k.put("tarea_clave", "admin");
		k.put("tarea_patron", "placeholder.png");
		k.put("tipo_tarea", "FILE");
		k.put("dias_ejecucion", "LUNES,MARTES,MIERCOLES,VIERNES");
		k.put("hora_inicio_ejecucion", "10:53");
		k.put("hora_siguiente_ejecucion", "10:53");
		k.put("tiempo_siguiente_intento", "2");
		k.put("numero_intentos", "2");
		k.put("numero_intentos_ejecutados", "2");
		schedulerTask.put("0",k);
	}

	
	public void createService()
	{
		/*Runnable task = new ControllerScheduler();
        int initialDelay = 0;
        int periodicDelay = 5;
        camel_objects.clear();
        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay,
                TimeUnit.SECONDS
        );*/
        //loadSchedulerTask();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//createService();
		searchResources();
	}
	
	
	public void stop() {
		// TODO Auto-generated method stub
		flag_run=false;
		System.out.println("detener hilo " + name_run);
		
	}
	
	
	
	public void sendThreadLog(String port, String message,String tipo,int id_m,String data_add)
	{
		 Map<String, Object> data_send = new HashMap<String, Object>();
		 data_send=constructMessageRules(port,new String(message),tipo,id_m);
		 data_send.put("info_tarea", data_add);
		 if (tipo.equals(ParamDefinitions._CONST_TYPE_OUT))
		 {
		 data_send.put("respuesta_remota", response_consume);
		 data_send.put("respuesta_remota_detail", response_consume_detail);
		 }
		 SendQueue sq = new  SendQueue(queue_log, jmsTemplate, data_send);
		 Thread nt = new Thread(sq);
		 nt.start();

	}
	
	public Map<String, Object> constructMessageRules(String port,String data,String tipo,int id_msg)
	{
		BasicDBObject k = new BasicDBObject();
		 Map<String, Object> data_send = new HashMap<String, Object>();
		k=schedulerTask.get(""+port);
        data_send.put("id_integracion", k.get("id_integracion").toString() );
        data_send.put("fecha_msg", "'"+ new Date()+"'" );
        data_send.put("mensaje", data );
        data_send.put("tipo_mensaje", "NO_XML");
        data_send.put("puerto", port);
        data_send.put("id_mensaje", id_msg);
        
        data_send.put("tarea_patron", k.get("tarea_patron").toString());
        
        if (tipo.equals(ParamDefinitions._CONST_TYPE_IN))
        {
        	data_send.put("ruta_respuesta", "NA");
            data_send.put("app_origen", "NA");
            data_send.put("app_destino", "NA" );
        }else
        {
        	data_send.put("ruta_respuesta", "NA");	
            data_send.put("app_origen", "NA" );
            data_send.put("app_destino", "NA" );
        }
        data_send.put("formato_msg", k.get("tipo_tarea").toString() );
        //System.out.println("envio " + data_send);
        //System.out.println("datos config msg " + detail);
        //System.out.println("jms" + this.jmsTemplate );
        return data_send;
	}
	
	
	
	public void searchResources() {
		
		while (flag_run)
		{
		
		try {
			Thread.sleep(5000);//Wait  build route	
		String isoDatePattern = "EEEE";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
		SimpleDateFormat time_compare = new SimpleDateFormat("HH:mm");
		SimpleDateFormat time_compare_1 = new SimpleDateFormat("HH:mm:ss");

		Calendar now = Calendar.getInstance();
		int dia = now.get(Calendar.DAY_OF_WEEK)-1;
		JndiContext jndiContext_arr =  new JndiContext() ;
		CamelContext camelContext_arr = new DefaultCamelContext();
		CamelContext camelContext_def;	
		
		String dateString = simpleDateFormat.format(new Date());
		System.out.println("Busqueda de Horarios:"+ name_run + " => " + time_compare_1.format(new Date()));
		SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
		for (int i=0; i<schedulerTask.size();i++) {
			BasicDBObject k = new BasicDBObject();
			k=schedulerTask.get(""+i);
			String hora_siguiente_ejecucion=k.get("hora_siguiente_ejecucion").toString();
			String dias_ejecucion=k.get("dias_ejecucion").toString();
			//String tU=dateString.toUpperCase();
			String tU=dia+"";
			String actual=time_compare.format(new Date());
			 Date userDate = parser.parse(actual);
			 Date comp =  parser.parse(hora_siguiente_ejecucion);
			if (userDate.before(comp) &&dias_ejecucion.indexOf(tU)>=0 && !searchTaskExecuted(k.get("_id").toString(), dia+""))
			{
				
				//Crear ruta Camel //Verifica si la ruta esta creada
				camelContext_def=camel_objects.get(""+k.get("_id").toString());
				if (camelContext_def==null)//No Creada toca crearla
				{
					int numero = (int)(Math.random()*10+1);
					String id_ruta_camel=k.get("_id").toString(); 
					camel_objects.put(""+id_ruta_camel, camelContext_arr);

					System.out.println("entra con "+ k + " y " + camel_objects);
					
					String nombre=(String) k.get("nombre_tarea");
					jndiContext_arr=new JndiContext();

		 			//beantoConsume_tmp = new ConsumeObjectTask(jmsTemplate,queue_log,k,this,i+"");
		 			//beantoConsume_tmp.createService();
		 			
		 			//jndiContext_arr.bind("testBeanTask"+id_ruta_camel, beantoConsume_tmp);
					DefaultCamelContext context = new DefaultCamelContext(jndiContext_arr);
		 			context.setName(id_ruta_camel + ". " + nombre);
		 			camelContext_arr = context;
		 			//Por si esta en ejecucion atrapado
		 			try
		 			{
		 				camelContext_arr.stop();
		 			}catch(Exception e)
		 			{}
		 			
		 			nombre_patron_find=k.get("tarea_patron").toString();
		 			data =k;
		 			controller=this;
		 			id_key=i;
		 			
	
		 			//ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER_USER,ParamDefinitions._CONST_AQM_SERVER_USER, ParamDefinitions._CONST_AQM_SERVER );
		 			//camelContext_arr.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		 			camelContext_arr.addRoutes(new RouteBuilder() {
		 				public void configure() {
		 					/*from("jms:queue:Message_IN_"+id_ruta_camel+"?asyncConsumer=true&concurrentConsumers=10").routeId(id_ruta_camel + ". " +"Envio del Mensaje al Destino ")
		 							//.to("bean:testBean?method=hello")
		 					       // .to("log:Message_IN_"+id_ruta_camel)
		 							.to("direct:Message_IN_"+id_ruta_camel);
		 					
		 					from("direct:Message_IN_"+id_ruta_camel).routeId(id_ruta_camel + ". " +"Consumo del Mensaje ")
		 					//  .transform(simple("direct:queueResult output: ${body}"))
		 					//.to("stream:out")
		 					//.to("http://127.0.0.1/busmanager/resp.php")
		 					.to("bean:testBean"+id_ruta_camel+"?method=sendRequest");
		 					
		 					from("jms:queue:Message_OUT_"+id_ruta_camel+"?asyncConsumer=true&concurrentConsumers=10").routeId(id_ruta_camel + ". " +"Retorno de la Respuesta " )
								.to("bean:testBean"+id_ruta_camel+"?method=sendResponse");*/
		 					from("timer:simple?period=5000").routeId(id_ruta_camel + ". Temporizador" )
		 					.to("direct:ProcesarArchivo").routeId(id_ruta_camel + ". " +"Monitoreo Archivo(s): " +  nombre_patron_find)
		 					.end();
		 					
		 					from("direct:ProcesarArchivo").routeId(id_ruta_camel + ". " +"Procesamiento del Archivo(s)")
		 					//.bean(beantoConsume_tmp,"createService");
		 					//.bean(beantoConsume_tmp,"createService");
		 					//.to("bean:testBeanTask"+id_ruta_camel+"?method=createService");
		 					.process(new ConsumeObjectTask(jmsTemplate,queue_log,data,controller,id_key+"")).routeId(id_ruta_camel + ". Chequeo FTP" );
	
		 				}
		 			});
		 			camel_objects.put(""+id_ruta_camel, camelContext_arr);
		 			camelContext_arr.start();
				}
				

			}
			
		}}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		}
		System.out.println("Termino Proceso " + name_run);
		
	}
	
	
	
	
	public void removeRoute(String id_key)
	{
		CamelContext camelContext_arr = camel_objects.get(id_key);
		if (camelContext_arr==null)
			return;
		try {
			camelContext_arr.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camel_objects.remove(id_key);
	}
	
	public boolean searchTaskExecuted(String id, String dia)
	{
		
		try {
		for (int i=0; i<executedTask.size();i++) {
			BasicDBObject k = new BasicDBObject();
			k=executedTask.get(""+i);
			if (k.get("_id").equals(id) &&  k.get("dia_ejecutado").equals(dia))
			{
			
				if (k.get("estado").toString().equals(ParamDefinitions._00_MESSAGE_OK))
					return true;
				else
					return false;	
			}
		}
		}catch(Exception e)
		{
			e.getMessage();
		}
		return false;
	}

}
