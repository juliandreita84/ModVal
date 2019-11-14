package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.Queue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;
import com.techprimers.messaging.standaloneactivemqexample.RandomString;
import com.techprimers.messaging.standaloneactivemqexample.config.Config;

import core.ServiceBD;
import net.minidev.json.parser.JSONParser;
@Component
public class Consumer {
	private ServiceBD s = new ServiceBD();
	private String detail_error="";
    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    Queue queue;
	private BasicDBObject resolveRules(JSONObject  json)
	{
		BasicDBObject outMsg= new BasicDBObject();
		//0. Extraer los campos requeridos de consulta y referencia
		
		String id_integracion = "";
		String mensaje="";
		String formato_msg="";
		try {
			id_integracion = (String)json.getString("id_integracion");
			mensaje = (String)json.getString("mensaje");
			formato_msg = (String)json.getString("formato_msg");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//1. Traer las reglas que existan para el mensaje
		DBCursor cursor=s.getRulesByIntegration(id_integracion);

		//No hay reglas para el mensaje y lo deja pasar	
		if (cursor==null)
		{
			try {
				json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return BasicDBObjectToJson(json);
		}
		
		
		if (formato_msg.equals(ParamDefinitions._CONST_FORMAT_XML)){
			//Document doc = convertStringToXMLDocument( mensaje );
			//System.out.println("Nombre Nodo "+ doc.getDocumentElement().getNodeName());
			if (validateMessage(mensaje,cursor))
			{
				try {
					json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else
			{
				try {
					json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._01_ERROR_TIPO_DATO);
					json.put(ParamDefinitions._CONST_RESPONSE_DETAIL, detail_error);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return BasicDBObjectToJson(json);
		}
		//JSONArray array = json.getJSONArray("*");

		
		
		return BasicDBObjectToJson(json);
	}
	
	@JmsListener(destination = "BusService_In")
	public void consumeBusService_In(Map<String,String> message)
	{

        System.out.println("Received Message: " + message);    	
    	@SuppressWarnings("deprecation")
		JSONParser parser = new JSONParser();
    	
    	BasicDBObject json = new BasicDBObject();
    	JSONObject jo= new JSONObject(message);	
		json=resolveRules(jo);
		sendLogMessage( jo,json);
		Config con = new Config();
		Map<String,String> message_res=ConstructMsgResponse( jo,json);
		jmsTemplate.convertAndSend(con.getQueueAMQ_OUT(), message_res);
	}
	
	// @JmsListener(destination = "BusPersistence")
	    public BasicDBObject consume(Map<String,String> message,Queue cola ,JmsTemplate jm) {
	    	
	    	this.queue=cola;
	    	this.jmsTemplate=jm;		
	        System.out.println("Received Message: " + message);    	
	    	@SuppressWarnings("deprecation")
			JSONParser parser = new JSONParser();
	    	
	    	BasicDBObject json = new BasicDBObject();
	    	JSONObject jo= new JSONObject(message);	
			json=resolveRules(jo);
			sendLogMessage( jo,json);
			/*if (json.get(ParamDefinitions._CONST_RESPONSE).equals(ParamDefinitions._00_MESSAGE_OK))
			{
				s.m.createConnection(s.bdname);
		    	boolean result= s.writeLog(json);
		    	System.out.println("Resultado de la operacion " + result);
			}*/
			System.out.println("retorno al control");
			return json;
	    	//json=resolveFunctionMessage(com.mongodb.util.JSON.parse(message));
//				json = BasicDBObject.parse( parser.parse(message));
	    }
	 
	 private BasicDBObject BasicDBObjectToJson(JSONObject  json)
	 {
		    BasicDBObject outMsg= new BasicDBObject();
			Iterator<?> permisos = json.keys();
			while(permisos.hasNext() ){
				String key = (String)permisos.next();
				try {
					outMsg.put(key, json.getString(key));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return outMsg;
	 }
	 
	 private static Document convertStringToXMLDocument(String xmlString)
	    {
	        //Parser that produces DOM object trees from XML content
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	         
	        //API to obtain DOM Document instance
	        DocumentBuilder builder = null;
	        try
	        {
	            //Create DocumentBuilder with default configuration
	            builder = factory.newDocumentBuilder();
	             
	            //Parse the content to Document object
	            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
	            return doc;
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 
	 private boolean validateMessage(String doc,DBCursor cursor)
	 {
			while (cursor.hasNext())
			{
				DBObject o = cursor.next();
				String nombre_campo =(String) o.get("nombre_campo");
				String tipo_campo =(String) o.get("tipo_campo");
				String servicio =(String) o.get("servicio");
				int lon_min= (int) Double.parseDouble( o.get("longitud_minima").toString());
				int lon_max= (int) Double.parseDouble( o.get("longitud_maxima").toString());
				
				//Validar el Servicio
				String valid_service=getElementXMLString(doc,servicio);
				if (valid_service.equals(servicio)) {
				
				String valor_campo=getElementXMLString(doc,nombre_campo);
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_STRING))
				{
					if (!StringValid(valor_campo,lon_min,lon_max))
					{
						detail_error="FORMATO INVALIDO CAMPO [" + nombre_campo + "]";
						return false;
					}
				}
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_NUMBER))
				{
					if (!NumberValid(valor_campo,lon_min,lon_max))
					{
						detail_error="FORMATO INVALIDO CAMPO [" + nombre_campo + "]";
						return false;
					}
				}
				System.out.println("campo =>"+nombre_campo +"="+valor_campo);
				}
			}
		 return true;
	 }
	 
	 private boolean StringValid(String valor,int lon_min, int lon_max)
	 {
		 if (valor.length()==0)
		 {
			 return false;
		 }
		 
		 if (valor.length()<lon_min || valor.length()>lon_max)
		 {
			 return false;
		 }
		 return true;
	 }
	 
	 private boolean NumberValid(String valor,int lon_min, int lon_max)
	 {
		 
		 try {
			 double d=Double.parseDouble( valor) ;
		 }catch(Exception e)
		 {
			 return false;
		 }
		 
		 if (valor.length()<lon_min || valor.length()>lon_max)
		 {
			 return false;
		 }
		 return true;
	 }
	 
	 private String getElementXMLString(String texto,String campo)
	 {
		 String texto_work=texto.replaceAll("</typ:", "</");
		 String tag_ini="<" + campo + ">";
		 String tag_fin="</" + campo + ">";
		 
		 int i=texto_work.indexOf(tag_ini);
		 int j=texto_work.indexOf(tag_fin);
		 
		 if (i==-1)
		 {
			 return "";
		 }

		 String valor = texto_work.substring(i + tag_ini.length(), j);
		 return valor;
	 }
	 
	 private Map<String,String> ConstructMsgLog(JSONObject  msg, BasicDBObject result)
	 {
		 Map<String,String> Msg = new HashMap<String, String>();
		 
		 try {
		 String _id="FX_AUTO_INCREMENT";
		 String fecha_log="FX_CREATE_DATE";
		 String id_integracion=msg.getString("id_integracion").toString();
		 String resultado=result.getString(ParamDefinitions._CONST_RESPONSE);
		 String app_origen=msg.getString("app_origen").toString();
		 String app_destino=msg.getString("app_destino").toString();
		 String texto_log=result.getString("mensaje").replaceAll("\n","");
		 Msg.put("_id", _id);
		 Msg.put("fecha_log", fecha_log);
		 Msg.put("id_integracion", id_integracion);
		 Msg.put("resultado", resultado.substring(1, 3));
		 Msg.put("app_origen", app_origen);
		 Msg.put("app_destino", app_destino);
		 Msg.put("texto_log", texto_log);
		 Msg.put("origen", ParamDefinitions._CONST_ORIGEN_DATA);
		
		 //Msg="_id=" +_id + "&" + "fecha_log=" +fecha_log + "&" +"id_integracion=" +id_integracion + "&" +"resultado=" +resultado + "&" +"app_origen=" +app_origen + "&" + "app_destino=" +app_destino + "&" + "texto_log=" +texto_log ;
		 }catch (Exception e){
			 e.printStackTrace();
		 }
		 
		 
		 return Msg;
	 }
	 
	 private void sendLogMessage(JSONObject  msg, BasicDBObject result)
	 {
		 Map<String,String> message=ConstructMsgLog( msg,  result);
		 System.out.println("enviar a la cola" +queue +" =>" +message );
		 jmsTemplate.convertAndSend(queue, message);
		 
	 }
	 
	 private Map<String,String> ConstructMsgResponse(JSONObject  msg, BasicDBObject result)
	 {
		 Map<String,String> Msg = new HashMap<String, String>();
		 
		 try {
	     RandomString session = new RandomString();
		 String _id=session.nextString();
		 String id_integracion=msg.getString("id_integracion").toString();
		 String resultado=result.getString(ParamDefinitions._CONST_RESPONSE);
		 String app_origen=msg.getString("app_origen").toString();
		 String app_destino=msg.getString("app_destino").toString();
		 String texto_log=result.getString("mensaje").replaceAll("\n","");
		 String puerto=result.getString("puerto");
		 String ruta_respuesta=result.getString("ruta_respuesta");
		 Msg.put("_id", _id);
		 Msg.put("fecha_repuesta", new Date()+"" );
		 Msg.put("id_integracion", id_integracion);
		 Msg.put("resultado", resultado.substring(1, 3));
		 Msg.put("app_origen", app_origen);
		 Msg.put("app_destino", app_destino);
		 Msg.put("mensaje", texto_log);
		 Msg.put("puerto", puerto);
		 Msg.put("ruta_salida",ruta_respuesta);
		 Msg.put("origen", ParamDefinitions._CONST_ORIGEN_DATA);
		
		 //Msg="_id=" +_id + "&" + "fecha_log=" +fecha_log + "&" +"id_integracion=" +id_integracion + "&" +"resultado=" +resultado + "&" +"app_origen=" +app_origen + "&" + "app_destino=" +app_destino + "&" + "texto_log=" +texto_log ;
		 }catch (Exception e){
			 e.printStackTrace();
		 }
		 
		 
		 return Msg;
	 }
}
