package com.techprimers.messaging.standaloneactivemqexample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import com.mongodb.BasicDBObject;
import com.techprimers.messaging.standaloneactivemqexample.config.Config;
import com.techprimers.messaging.standaloneactivemqexample.listener.ConsumeWebService;
import com.techprimers.messaging.standaloneactivemqexample.listener.Consumer;

@SpringBootApplication// same as 
@Configuration 
@EnableAutoConfiguration 
@ComponentScan 
public class LoadService {
	

	public Consumer tcpServer [] = new Consumer[100];
	public static Consumer consumoInit = new Consumer();
	private Map <String, Object> dataConfig =new HashMap<String, Object>();
	private ConsumeWebService consumeWebService = new ConsumeWebService();
	private Map <String, Object> configRoutes =new HashMap<String, Object>();
	public static Map<String , BasicDBObject>  cursor_routes = new HashMap<String , BasicDBObject> ();
    @Autowired
	static
    JmsTemplate jmsTemplate;
    
	public static void main(String[] args) {
		LoadService l = new LoadService();
		SpringApplication.run(LoadService.class, args);	
		Config cfg = new Config();
		l.jmsTemplate=cfg.jmsTemplateManual();
		consumoInit.ConsumerStart();
		//l.loadConfig();
		//l.initServices(l.jmsTemplate);

	}
	
	/*private void loadConfig()
	{
		//Get Integrations and Config Routes0
		if (cursor_routes.size()==0)
    	{
			cursor_routes = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_INTEGRATIONS);
    	}
    	//System.out.println("cursor_routes=" + cursor_routes);
		
		int l=0;
		for (int w=0; w<cursor_routes.size(); w++)
		{
			BasicDBObject o_d = cursor_routes.get(w+"");
			Map <String,String> detail=new HashMap<String, String>();
			detail.put("id_integracion", o_d.get("id_integracion").toString());
			detail.put("nombre_ruta", o_d.get("nombre_ruta").toString());
			detail.put("puerto", o_d.get("puerto").toString());
			detail.put("ruta_salida", o_d.get("ruta_salida").toString());
			detail.put("ruta_respuesta", o_d.get("ruta_respuesta").toString());
			detail.put("formato", o_d.get("formato").toString());
			detail.put("app_origen", o_d.get("app_origen").toString());
			detail.put("app_destino", o_d.get("app_destino").toString());
			dataConfig.put(l+"",detail);
			l++;
		}
		
		/*DBCursor cursor_g=s.getCollection("routes_config");
		while (cursor_g.hasNext())
		{
			
			DBObject o = cursor_g.next();
			configRoutes.put( o.get("port_in").toString(), o.get("port_out").toString());
		}*
		//System.out.println("salid " +dataConfig );
		
	}


	@SuppressWarnings("unchecked")
	public void initServices(JmsTemplate jmsTemplate)
	{
		 Map <String, Object> dataConfig_local =new HashMap<String, Object>();
		for (int k =0 ; k<dataConfig.size();k++) {
			Map <String,String> detail=new HashMap<String, String>();
			detail = (Map<String, String>) dataConfig.get(""+k);
			String port = detail.get("puerto");
			dataConfig_local.put(port, detail);
		}
		
		for (int k =0 ; k<dataConfig.size();k++) {
			Map <String,String> detail=new HashMap<String, String>();
			detail = (Map<String, String>) dataConfig.get(""+k);
			int port_service = Integer.parseInt(detail.get("puerto"));
			tcpServer[k] = new Consumer();
			try {
				tcpServer[k].setRoute_data(dataConfig_local);
				tcpServer[k].createService(dataConfig_local,port_service,null,jmsTemplate);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}
