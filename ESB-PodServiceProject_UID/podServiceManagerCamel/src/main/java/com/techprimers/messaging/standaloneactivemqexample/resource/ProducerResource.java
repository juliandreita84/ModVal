package com.techprimers.messaging.standaloneactivemqexample.resource;

import java.util.Map;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.techprimers.messaging.standaloneactivemqexample.LoadServiceCamel;
import com.techprimers.messaging.standaloneactivemqexample.listener.Consumer;
@RestController
@Controller
@RequestMapping("/")
public class ProducerResource {

	private Consumer consumo = new Consumer();
    @Autowired
    public static JmsTemplate jmsTemplate;

    @Autowired
    Queue queue;
 
    /*@RequestMapping(value = "/rest/podServiceManager/", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> ValidateMessage(@RequestParam Map<String,String>  info) {
    	BasicDBObject json= new BasicDBObject();
       	try {
           //jmsTemplate.convertAndSend(queue, info);
       		System.out.println("cola " + queue);
       		 json =consumo.consumeBusService(info,queue,jmsTemplate);
    	}
    	catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
        return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
    	//return "OK";
    }*/
 
    
    @RequestMapping(value = "/rest/ReSYCN/", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> getCommand(@RequestParam  Map<String ,Object>  info) {
    	BasicDBObject json= new BasicDBObject();
    	System.out.println("hizo resync");
       	try {
           //jmsTemplate.convertAndSend(queue, info);
       		//System.out.println("cola " + queue);
       		 consumo.cursor_routes.clear();
       		 consumo.ConsumerStop();
       		consumo.ConsumerStart();
       		//"{\"response\"':\"" + result  + "\"}"
       		
       		
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		json.put("response", "Fallo");
            return new ResponseEntity<>( json, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
        json.put("response", "Exitoso");
        return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
    	//return "OK";
    }
    
   
}
