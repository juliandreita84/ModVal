package com.techprimers.messaging.standaloneactivemqexample.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.techprimers.messaging.standaloneactivemqexample.GenerateMessageAuthenticationCode;
import com.techprimers.messaging.standaloneactivemqexample.KeyGeneratorService;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;
import com.techprimers.messaging.standaloneactivemqexample.listener.ConsumeWebService;
@RestController
@Controller
@RequestMapping("/")
public class ProducerResource {
 
	public static Map<String , BasicDBObject>  cursor_keys = new HashMap<String , BasicDBObject> ();
	private static ConsumeWebService consumeWebService = new ConsumeWebService();	
    @RequestMapping(value = "/rest/podGetKeys/", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> serviceGetKeys(@RequestBody  Map<String ,Object>  info) {
     	BasicDBObject json= new BasicDBObject();
     	String keysToChiper="";
     	
    	try {
    		json.put("message_id_app", info.get("message_id_app"));
    		json.put("message_key_op", info.get("message_key_op"));
    		if ( info.get("message_key_op").equals(ParamDefinitions._CONST_TYPE_OPERATION_QUERY))
    		{
    			keysToChiper=getKeys(info.get("message_id_app").toString());
    			if (keysToChiper.length()==0)
    			{
    				json.put("message_key1","");
            		json.put("message_key2","");	
            		json.put("message_result","FAIL : No hay llaves para este message_id_app=" + info.get("message_id_app").toString());
    			}else
    			{
    				String l[]=keysToChiper.split(",");
    				json.put("message_key1",l[0]);
    				json.put("message_key2",l[1]);
    				json.put("message_result","OK");
    			}
    		}else {
    		KeyGeneratorService kc = new KeyGeneratorService();
    		Map<String, String> k = kc.keyGeneratorCustom(32);
    		String K1=k.get("K1");
    		String K2=k.get("K2");
    		json.put("message_key1",K1);
    		json.put("message_key2",K2);
    		//Borrar la llave actual
    		JSONObject h= new JSONObject();
    		//Map<String , BasicDBObject>  result_query = new HashMap<String , BasicDBObject> ();
    		String result_query="";
    		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    		h.put("_id", "'" + info.get("message_id_app")+"'");
    		result_query=consumeWebService.constructMessageConsumeString(h, ParamDefinitions.COLLECTION_KEYS_APP,ParamDefinitions.COLLECTION_OPERATION_DELETE);
    		//Crear la llave
    		h.put("key_01", "'" +K1+"'");
    		h.put("key_02", "'" + K2+"'");
    		h.put("time_keys", "'" + sm.format(new Date())+"'");
    		result_query=consumeWebService.constructMessageConsumeString(h, ParamDefinitions.COLLECTION_KEYS_APP,ParamDefinitions.COLLECTION_OPERATION_INSERT);
    		//Refrescar la data
    		cursor_keys = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_KEYS_APP,ParamDefinitions.COLLECTION_OPERATION_QUERY);
    		json.put("message_result","OK");
    		}
    		
    	}
    	catch (Exception e) {
    		json.put("message_result","FAIL: " + e.getMessage());
            return new ResponseEntity<>( json, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
        return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
    	//return "OK";
    }
    
    
    @RequestMapping(value = "/rest/podGetMAC/", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<?> serviceGetMAC(@RequestBody  Map<String ,Object>  info) {
     	BasicDBObject json= new BasicDBObject();
     	
    	try {
    		json.put("message_id_app", info.get("message_id_app"));
         	json.put("message_id_content", info.get("message_id_content"));
         	String message_content= (String) info.get("message_content");
         	String message_time= (String) info.get("message_content");
         	//Aqui debe traer llaves del APP
         	
         	String keysToChiper=getKeys(info.get("message_id_app").toString());
                                      		/*KeyGeneratorService kc = new KeyGeneratorService();
    		Map<String, String> k = kc.keyGeneratorCustom(32);
    		String K1=k.get("K1");
    		String K2=k.get("K2");*/
    		StringBuilder message = new StringBuilder();
    		message.append(message_content);
    		message.append(message_time);
    		GenerateMessageAuthenticationCode g = new GenerateMessageAuthenticationCode();
            String message_mac = g.getMac(message.toString(), keysToChiper.replaceAll(",", ""));
             
	         json.put("message_mac", message_mac);
			 json.put("message_result","OK");
    	}
    	catch (Exception e) {
    		json.put("message_result","FAIL: " + e.getMessage());
            return new ResponseEntity<>( json, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //headers.setLocation(ucBuilder.path("/rest/podPersistence/").buildAndExpand(info.get_id()).toUri());
        return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
    	//return "OK";
    }
    
    
    public String getKeys(String id_app)
    {
    	String result="";
    	if (cursor_keys.size()==0)
    	{
    		cursor_keys = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_KEYS_APP,ParamDefinitions.COLLECTION_OPERATION_QUERY);
    		
    	}
    	
    	
    	try
    	{
    		for (int i=0; i<cursor_keys.size();i++) {
        		BasicDBObject jo =  cursor_keys.get(i+"");
        		if (jo.get("_id").equals(id_app)) {
        		String k1=(String) jo.get("key_01");
        		String k2=(String) jo.get("key_02");
        		result=k1+","+k2;
        		}
    		}
  		
    		
    	}catch(Exception e)
    	{
    		//Va al consumo si no existe la llave para el app
    		cursor_keys = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_KEYS_APP,ParamDefinitions.COLLECTION_OPERATION_QUERY);
    	}
    	
    	return  result;
    }
    
    
}
