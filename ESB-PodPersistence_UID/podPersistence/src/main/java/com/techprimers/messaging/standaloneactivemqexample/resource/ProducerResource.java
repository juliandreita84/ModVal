package com.techprimers.messaging.standaloneactivemqexample.resource;

import java.util.Map;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;
import com.techprimers.messaging.standaloneactivemqexample.listener.Consumer;

import core.runQuery;
@RestController
@Controller
@RequestMapping("/")
public class ProducerResource {

	private Consumer consumo = new Consumer();
	private runQuery runquery = new runQuery();
    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    Queue queue;
 
    @RequestMapping(value = "/rest/podPersistence/", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> createLog(@RequestParam Map<String,String>  info) {
    	BasicDBObject json= new BasicDBObject();
       	try {
           //jmsTemplate.convertAndSend(queue, info);
       		 json =consumo.consume(info);
    	}
    	catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
        return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
    	//return "OK";
    }
 
    @RequestMapping(value = "/rest/podPersistence/", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> createLogGET(@RequestParam Map<String,String>  info) {
    	BasicDBObject json= new BasicDBObject();
    	
    	try {
            //jmsTemplate.convertAndSend(queue, info);
        		 json =consumo.consume(info);
     	}
     	catch (Exception e) {
             return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
         //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
         return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
     	//return "OK";
    }    
    
    /**
     * Permite consultar la informacion de la BD para la interface
     * @param info
     * @return
     */
    @RequestMapping(value = "/rest/podDataInterface/", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> serviceInfo(@RequestParam Map<String,String>  info) {
    	BasicDBObject json= new BasicDBObject();
    	String result_query="";
    	try {
            //jmsTemplate.convertAndSend(queue, info);
        		 String type_query = info.get("TYPE_QUERY");
        		 String content_query = info.get("CONTENT_QUERY");
        		 String param_query = info.get("PARAM_QUERY");
        		 
        		 if (type_query.equals(ParamDefinitions._CONST_TYPE_01_LOAD_FULL_COLLECTION))
        		 {
        			result_query=runquery.loadListCollection(content_query);
        		 }
        		 if (type_query.equals(ParamDefinitions._CONST_TYPE_02_CUSTOM_FILTER))
        		 {
        			result_query=runquery.loadCustomCollection(content_query,param_query,type_query);
        		 }
        		 if (type_query.equals(ParamDefinitions._CONST_TYPE_03_CUSTOM_PROJECTION))
        		 {
        			result_query=runquery.loadCustomCollection(content_query,param_query,type_query);
        		 }
        		 if (type_query.equals(ParamDefinitions._CONST_TYPE_04_NEXT_SEQ))
        		 {
        			result_query=runquery.loadCustomCollection(content_query,param_query,type_query);
        		 }
        		 if (type_query.equals(ParamDefinitions._CONST_TYPE_05_INSERT_DOC)||type_query.equals(ParamDefinitions._CONST_TYPE_06_DELETE_DOC))
        		 {
        			result_query=runquery.loadCustomCollection(content_query,param_query,type_query);
        		 }
        		// System.out.println("param " + type_query + " =" + param_query);
        		//System.out.println("result_query " + type_query + " =" + result_query);
        		 
     	}
     	catch (Exception e) {
     		e.printStackTrace();
             return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
         //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
         return new ResponseEntity<>(result_query, HttpStatus.ACCEPTED);
       	
    }    
    
    
    public String goutesToJSON(String jsonString)
    {
    	jsonString = jsonString.replaceAll("([\\w]+)[ ]*=", "\"$1\" ="); // to quote before = value
	    jsonString = jsonString.replaceAll("=[ ]*([\\w@\\.]+)", "= \"$1\""); // to quote after = value, add special character as needed to the exclusion list in regex
	    jsonString = jsonString.replaceAll("=[ ]*\"([\\d]+)\"", "= $1"); // to un-quote decimal value
	    jsonString = jsonString.replaceAll("\"true\"", "true"); // to un-quote boolean
	    jsonString = jsonString.replaceAll("\"false\"", "false"); // to un-quote boolean
	    return jsonString.replaceAll("\"", "'").replaceAll("=", ":");
    }
    
    
    @RequestMapping(value = "/rest/podDataInterfacePost/", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> serviceInfoPost(@RequestBody  Map<String ,Object>  info) {
    	BasicDBObject json= new BasicDBObject();
    	BasicDBObject param_query= new BasicDBObject();
    	String result_query="";
    	try {
            //jmsTemplate.convertAndSend(queue, info);
        		  String type_query = info.get("TYPE_QUERY").toString();
        		 String content_query =  info.get("CONTENT_QUERY").toString();
        		 String l =goutesToJSON( info.get("PARAM_QUERY").toString());

        		 try {
        		 if (l!="")
        		 {
        		 param_query=(BasicDBObject) JSON.parse(l);
        		 }
        		 }catch(Exception e)
        		 {
        		 DBObject dbObject = (DBObject)JSON.parse(l);
        		 result_query=runquery.loadCustomCollection(content_query,(BasicDBObject) dbObject, type_query);
        		 return new ResponseEntity<>(result_query, HttpStatus.ACCEPTED);
        		 }
        		 //BasicDBObject param_query=(BasicDBObject) JSON.parse(l);
        		 //BasicDBObject param_query = (BasicDBObject) JSON.parse(jobj.toString());
        		 
        		 
        		 if (type_query.equals(ParamDefinitions._CONST_TYPE_05_INSERT_DOC)||type_query.equals(ParamDefinitions._CONST_TYPE_06_DELETE_DOC))
        		 {
        			result_query=runquery.loadCustomCollection(content_query,param_query, type_query);
        		 }else
        		 {
        			result_query=runquery.loadCustomCollection(content_query,param_query, type_query);
        		 }
        		// System.out.println("param " + type_query + " =" + param_query);
        		 //System.out.println("result_query " + type_query + " =" + result_query);
        		 
     	}
     	catch (Exception e) {
     		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
         //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
         return new ResponseEntity<>(result_query, HttpStatus.ACCEPTED);
       	
    }  
    
    
    /*@GetMapping("/{message}")
    @RequestMapping(method = RequestMethod.GET,value="/rest/podPersistence/{message}")
    @ResponseBody("")
    public String getMessage(@PathVariable("message") String message) {
    	System.out.println("msg rev=" + message);
    	  jmsTemplate.convertAndSend(queue, message);

          return "Published Successfully Persistence";
    }**/
   /* @RequestMapping(method = RequestMethod.GET,value="/rest/podPersistence/{message}")
    //@GetMapping("/{message}")
    public String publish(@PathVariable("message")
                          final String message) {

        jmsTemplate.convertAndSend(queue, message);

        return "Published Successfully Persistence";
    }*/
}
