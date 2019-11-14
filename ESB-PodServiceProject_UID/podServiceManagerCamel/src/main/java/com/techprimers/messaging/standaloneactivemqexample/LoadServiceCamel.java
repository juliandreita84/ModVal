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
public class LoadServiceCamel {
	

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
		LoadServiceCamel l = new LoadServiceCamel();
		SpringApplication.run(LoadServiceCamel.class, args);	
		Config cfg = new Config();
		l.jmsTemplate=cfg.jmsTemplateManual();
		consumoInit.ConsumerStart();
		//l.loadConfig();
		//l.initServices(l.jmsTemplate);

	}
	
	
}
