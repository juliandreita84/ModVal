package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Queue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;
import com.techprimers.messaging.standaloneactivemqexample.RandomString;
import com.techprimers.messaging.standaloneactivemqexample.config.Config;


@Component
public class Consumer {
	//private ServiceBD s = new ServiceBD();
	private ConsumeWebService consumeWebService = new ConsumeWebService();
	private String detail_error="";
	//private  String service_used="";
	
	public static Map<String , String>  service_used = new HashMap<String , String> ();
	
	private  static String sentido_msg = "IN";
	private  String id_integracion_calculate="";
	public static Map<String , BasicDBObject>  cursor_reglas = new HashMap<String , BasicDBObject> ();
	public static Map<String , String>  cursor_reglas_servicio = new HashMap<String , String> ();
	public static Map<String , String>  cursor_keysID = new HashMap<String , String> ();
	private static boolean ok_var=true;
	private static boolean id_detected=true;
    @Autowired
    JmsTemplate jmsTemplate;

    public Consumer()
    {
    	cursor_reglas.clear();
    	//if (cursor_reglas.size()==0)
    	{
    	cursor_reglas = consumeWebService.constructMessageConsume(null, ParamDefinitions.COLLECTION_RULES_MESSAGES);
    	}
    	getReglasServicio();
    	//System.out.println("cursor_reglas=" + cursor_reglas);
    }
    
    
    private Map<String , BasicDBObject>  getCustomData(String id_integracion, String servicio_in)
    {
    	int w=0;
    	Map<String , BasicDBObject>  j= new HashMap<String , BasicDBObject> ();
    	for (int i=0; i<cursor_reglas.size();i++) {
    		try {
    		BasicDBObject jo =  cursor_reglas.get(i+"");
			if ((jo.get("id_integracion").equals(id_integracion) 
					&& jo.get("servicio").equals(servicio_in)
					&& !jo.get("servicio_principal").equals("E")
					)
					||
					(jo.get("id_integracion").equals(id_integracion) && 
							jo.get("servicio_principal").equals(servicio_in))
					)
					
			{
				j.put(w+"", jo);
				w++;
			}
    		}catch (Exception e)
    		{
    			//e.printStackTrace();
    		}
    		
    	}
    	return j;
    }
    
    public void getReglasServicio()
    {
    	//System.out.println(cursor_reglas);

    	for (int i=0; i<cursor_reglas.size();i++) {
    		try {
    		BasicDBObject jo =  cursor_reglas.get(i+"");
			String s= jo.get("servicio").toString();
			String s2= jo.get("servicio_asociado").toString();
			String s1= jo.get("servicio_principal").toString();
				//if (s1.equals("S")||s.trim().length()>0)
				if (s1.equals("S")/*|| s2.length()>0*/)
				{
					cursor_reglas_servicio.put(s, jo.get("id_integracion").toString());
				}
    		}
    		catch (Exception e)
    		{
    			//e.printStackTrace();
    		}
    	}
      	System.out.println("cursor reglas "+cursor_reglas_servicio);
    }
    
    public String getIdIntegracion(String message, String id)
    {
    	String result="NA";
    	/*for (int i=0; i<cursor_reglas_servicio.size();i++) {
    		
    	}*/
    	//service_used="";
    	service_used.clear();
		 String texto_work=message.replaceAll("</?:", "</").replaceAll("<?:", "<");
		 //texto_work=texto_work.replaceAll("<typ:", "<");
		 
    	for ( String key : cursor_reglas_servicio.keySet() ) {
/*    		if (key.indexOf("#0")>0)
    		{
    			root_service=1;
    		}*/
    		
    	    if ( texto_work.toUpperCase().indexOf("<"+key.toUpperCase()+">")>0
    	    	|| 
    	    	(texto_work.toUpperCase().indexOf(key.toUpperCase())>0 && key.indexOf("<")>=0)
    	    		) //Buscar el Nivel Superior
    	    {
    	    	
    	    	/*if (root_service==1)
    	    	{
    	    		result= cursor_reglas_servicio.get(key);
    	    		service_used=key;
    	    		root_service++;
    	    	}
    	    	else
    	    	{
    	    		su=key;
    	    		result= cursor_reglas_servicio.get(key);
    	    	}
    	    	*/
    	    	//service_used=key;  	
    	    	service_used.put(service_used.size()+"", key);
    	    	result=cursor_reglas_servicio.get(key);
    	    }
    	}

    	/*if (root_service==0)
    	{
    		service_used=su;
    	}*/
    	
    	return result;
    }
    
    @Autowired
    Queue queue;
	private BasicDBObject resolveRules(JSONObject  json)
	{
		BasicDBObject outMsg= new BasicDBObject();
		//System.out.println("reglas leidas "+ cursor_reglas_servicio);
		//0. Extraer los campos requeridos de consulta y referencia
		detail_error="";
    	id_integracion_calculate="";
    	id_detected=false;
    	sentido_msg = "IN";
		String id_integracion = "";
		String mensaje="";
		String mensaje_rest="";
		String formato_msg="";
		String resp_mot="00";
		try {
			id_integracion = (String)json.getString("id_integracion");
			mensaje = (String)json.getString("mensaje");
			mensaje_rest = (String)json.getString("mensaje_rest");

			formato_msg = (String)json.getString("formato_msg");
			resp_mot = (String)json.getString("respuesta_remota");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
		}
		
		try {
			String lop = (String)json.getString("respuesta_remota");
			sentido_msg="OUT";
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
		}
		
		
		/*
		//1. Traer las reglas que existan para el mensaje
		//DBCursor cursor=s.getRulesByIntegration(id_integracion);
		JSONObject consume_dat0 = new JSONObject();
		try {
			consume_dat0.put("_id", id_integracion);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cursor_reglas = consumeWebService.constructMessageConsume(consume_dat0, ParamDefinitions.COLLECTION_RULES_MESSAGES);
		*/

		if (formato_msg.equals(ParamDefinitions._CONST_FORMAT_NO_XML)&& !resp_mot.equals("00")){
			try {
				json.put(ParamDefinitions._CONST_RESPONSE, resp_mot );
				json.put(ParamDefinitions._CONST_RESPONSE_DETAIL , (String)json.getString("respuesta_remota_detail"));

				return BasicDBObjectToJson(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (formato_msg.equals(ParamDefinitions._CONST_FORMAT_NO_XML) && resp_mot.equals("00")){
			try {
				json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK );
				json.put(ParamDefinitions._CONST_RESPONSE_DETAIL , "");

				return BasicDBObjectToJson(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		id_integracion = getIdIntegracion(mensaje, id_integracion);
		
		
		if (!resp_mot.equals("00"))//Reporta los errores en el servicio expuesto
		{
			try {
				json.put(ParamDefinitions._CONST_RESPONSE, resp_mot );
				json.put(ParamDefinitions._CONST_RESPONSE_DETAIL , (String)json.getString("respuesta_remota_detail"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return BasicDBObjectToJson(json);
		}
		
		if (id_integracion.equals("NA"))//NO hay definicion para ese servicio asi que no lo valida
		{
			try {
				json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._02_WITHOUT_DATA);
				json.put(ParamDefinitions._CONST_RESPONSE_DETAIL , "NO hay Servicio definido para este mensaje");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return BasicDBObjectToJson(json);
		}
		try {
			json.put("id_integracion",id_integracion);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Recorrer el array de servicios asociados al mensaje
		service_used.put(service_used.size()+"", "E"); //Para los servicios principales
		for (int y=0 ; y<service_used.size(); y++) {
		
		String su=service_used.get(""+y);
		Map<String , BasicDBObject>  result_cursor= getCustomData(id_integracion,su);
		//No hay reglas para el mensaje y lo deja pasar	
/*		if (result_cursor.size()==0)
		{
			try {
				json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return BasicDBObjectToJson(json);
		}*/
		
		
		if (formato_msg.equals(ParamDefinitions._CONST_FORMAT_XML)){
			//Document doc = convertStringToXMLDocument( mensaje );
			//System.out.println("Nombre Nodo "+ doc.getDocumentElement().getNodeName());
			
			boolean result= validateMessage(mensaje,mensaje_rest,result_cursor);
			/*
			if (validateMessage(mensaje,mensaje_rest,result_cursor))
			{
				try {
					json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK);
					json.put(ParamDefinitions._CONST_RESPONSE_DETAIL, "");
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
			
			//Coloca el ID temporal para los que lo tienen
//			System.out.println("id_integracion_calculate=" + id_integracion_calculate );
	//		System.out.println("id_integracion=" + id_integracion  );
			if (id_detected && id_integracion_calculate!="" && sentido_msg.equals("OUT"))
			{
				try {
					json.put("id_integracion",id_integracion_calculate);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
			if (id_detected && id_integracion_calculate=="" && sentido_msg.equals("OUT"))
			{
				try {
					json.put("id_integracion",ParamDefinitions._CONST_RESPONSE_DEFAULT);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/

			
			//return BasicDBObjectToJson(json);
		}
		}
		
		
		if (formato_msg.equals(ParamDefinitions._CONST_FORMAT_XML)){
			//Document doc = convertStringToXMLDocument( mensaje );
			//System.out.println("Nombre Nodo "+ doc.getDocumentElement().getNodeName());
			
			
			if (detail_error.length()==0)
			{
				try {
					json.put(ParamDefinitions._CONST_RESPONSE, ParamDefinitions._00_MESSAGE_OK);
					json.put(ParamDefinitions._CONST_RESPONSE_DETAIL, "");
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
			
			//Coloca el ID temporal para los que lo tienen
//			System.out.println("id_integracion_calculate=" + id_integracion_calculate );
	//		System.out.println("id_integracion=" + id_integracion  );
			if (id_detected && id_integracion_calculate!="" && sentido_msg.equals("OUT"))
			{
				try {
					json.put("id_integracion",id_integracion_calculate);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
			if (id_detected && id_integracion_calculate=="" && sentido_msg.equals("OUT"))
			{
				try {
					json.put("id_integracion",ParamDefinitions._CONST_RESPONSE_DEFAULT);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//JSONArray array = json.getJSONArray("*");

		
		
		return BasicDBObjectToJson(json);
	}
	
	@JmsListener(destination = "BusService_OUT")
	public void consumeBusService_In(Map<String,String> message)
	{
try {
        //System.out.println("Received Message: " + message);    	
    	@SuppressWarnings("deprecation")
		    	
    	BasicDBObject json = new BasicDBObject();
    	JSONObject jo= new JSONObject(message);	
		json=resolveRules(jo);
		sendLogMessage( jo,json);
		//Config con = new Config();
		//Map<String,String> message_res=ConstructMsgResponse( jo,json);
		//jmsTemplate.convertAndSend(con.getQueueAMQ_OUT(), message_res);
}catch (Exception e)
{
	e.printStackTrace();
}
	}
	
	// @JmsListener(destination = "BusPersistence")
	    public BasicDBObject consume(Map<String,String> message,Queue cola ,JmsTemplate jm) {
	    	
	    	this.queue=cola;
	    	this.jmsTemplate=jm;		
	       // System.out.println("Received Message: " + message);    	
	    	@SuppressWarnings("deprecation")
	    	
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
			//System.out.println("retorno al control");
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
	 
	 private Object ExtractDataREST(String value, BasicDBObject o)
	 {
		 Object k= new Object();
		 for ( String key : o.keySet() ) {
			 try {
				 if (value.equals(key.replaceAll(".*?:", "")))
				 {
				 k=o.get(key);
				 ok_var=true;
				 return k;
				 }else
				 {
					 k=o.get(key);
					 ok_var =false;
					 return ExtractDataREST(value,  (BasicDBObject) k);
				 }
			 }catch(Exception e)
			 {
				 
			 }
		 }
		 if (ok_var)
			 return k;
		 else
			 return "NA";
	 }
	 
	 private boolean validateMessage(String doc,String doc_rest,Map<String , BasicDBObject>  cursor)
	 {
		 int cont_error=0;
		 
		 String tipo_dato="XML";
		 BasicDBObject k1 = new BasicDBObject();
		 BasicDBObject k2 = new BasicDBObject();
		 BasicDBObject k = new BasicDBObject();
		try {
			if (doc_rest!=null && doc_rest.trim().length()>0)
			{
			k = (BasicDBObject) JSON.parse(doc_rest);

			/*k1=(BasicDBObject) k2.get("soapenv:Body");
			k=(BasicDBObject) k1.get("typ:"+service_used);
			if (service_used.toUpperCase().indexOf("RESPONSE")>0)
			{
				k=(BasicDBObject) k.get("typ:result");
			}*/
			//k= ExtractDataREST(String value, BasicDBObject o)
			tipo_dato="REST";
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			tipo_dato="XML";
		}
		 
		 try {

			 for (int w=0; w<cursor.size();w++)
			{
				BasicDBObject o = (BasicDBObject) cursor.get(w+"");
				String nombre_campo =(String) o.get("nombre_campo");
				String id_integracion =(String) o.get("id_integracion");
				String tipo_campo =(String) o.get("tipo_campo");
				String servicio =(String) o.get("servicio").toString().replaceAll("#0", "");
				String mascara =(String) o.get("mascara");
				String servicio_principal =(String) o.get("servicio_principal");
				String servicio_asociado ="";
				try {
					servicio_asociado =(String) o.get("servicio_asociado");
				}catch(Exception e)
				{
					servicio_asociado ="";
				}
				
				if (servicio_asociado == null)
				{
					servicio_asociado ="";
				}
				
				String requerido =(String) o.get("requerido");
				int lon_min= (int) Double.parseDouble( o.get("longitud_minima").toString());
				int lon_max= (int) Double.parseDouble( o.get("longitud_maxima").toString());
				
				//Validar el Servicio
				String valid_service=getElementXMLString(doc,servicio);
				
				if (valid_service.equals("NA"))
				{
					valid_service=doc;
				}
				
				if (requerido.equals("S"))
				//if ((valid_service.equals(servicio)||valid_service.length()>0)&& requerido.equals("S") && !valid_service.equals("NA"))
				{
				
					String valor_campo="";	
					
				if (tipo_dato.equals("XML"))
				{
				valor_campo=getElementXMLString(valid_service,nombre_campo);
				}
				if (tipo_dato.equals("REST"))
				{
					try {
				valor_campo= (String) ExtractDataREST(nombre_campo, k);//k.getString(nombre_campo);
				//intentaremos con la extraccion tipo XML -> STRING
				if (valor_campo.equals("NA"))
				{
					valor_campo=getElementXMLString(valid_service,nombre_campo);
				}
				if (valor_campo == null)
				{
					valor_campo="";
				}
				
					}
					catch(Exception e) {
						valor_campo="NA";
					}
				}
				
				if (valor_campo.equals("NA")) {
					cont_error++;
					detail_error+="CAMPO [" + servicio + " <-> " +  nombre_campo + "] no presente en el mensaje,";
					//break;
				}
				
				
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_STRING))
				{
					if (!StringValid(valor_campo,lon_min,lon_max))
					{
						cont_error++;
						detail_error+="FORMATO INVALIDO CAMPO [" + servicio + " <-> "+ nombre_campo + "],";
						//return false;
					}else
					{
						CalculateIDKey(valor_campo,nombre_campo,id_integracion);
					}
				}
				
				
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_DATE))
				{
					if (!DateValid(valor_campo,mascara))
					{
						cont_error++;
						detail_error+="FORMATO INVALIDO CAMPO [" + servicio + " <-> "+ nombre_campo + "],";
						//return false;
					}
				}
				
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_DOUBLE))
				{
					if (!DoubleValid(valor_campo, lon_min, lon_max, mascara))
					{
						cont_error++;
						detail_error+="FORMATO INVALIDO CAMPO [" + servicio + " <-> "+ nombre_campo + "],";
						//return false;
					}
				}
				
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_NUMBER))
				{
					if (!NumberValid(valor_campo,lon_min,lon_max))
					{
						cont_error++;
						detail_error+="FORMATO INVALIDO CAMPO [" + servicio + " <-> "+ nombre_campo + "],";
						//return false;
					}else
					{
						CalculateIDKey(valor_campo,nombre_campo,id_integracion);
					}
				}
				
				if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_XML))
				{
					Map<String , BasicDBObject>  result_cursor= new HashMap<String , BasicDBObject> ();				
					
					if (servicio_asociado.equals(""))
					{
						servicio_asociado=nombre_campo;
					}
				    result_cursor=getCustomData(id_integracion, nombre_campo);
				    valor_campo=getElementXMLString(doc,nombre_campo);
					if (!validateMessage(valor_campo, null, result_cursor) && valor_campo.trim().length()>0)
					{
						cont_error++;
						detail_error+="FORMATO INVALIDO CAMPOS XML [" + servicio + " <-> " + nombre_campo + "] -> " + servicio_asociado + " => ";
						//return false;
					}
					
					if (valor_campo.trim().length()==0)
					{
						cont_error++;
						detail_error+="FORMATO INVALIDO CAMPO [" + servicio + " <-> "+ nombre_campo + "],";
					}
				//	System.out.println(nombre_campo + "=>" + valor_campo.length());
				}
				//System.out.println("campo =>"+nombre_campo +"="+valor_campo);
				}
				
			}
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
			if (cont_error==0)
			{
				return true;
			}
			else
			{
				return false;
			}
	 }
	 
	 private void  CalculateIDKey(String valor_campo,String nombre_campo,String id_integracion)
	 {
		
		 if (nombre_campo.toUpperCase().equals("ID"))
		 {
			 id_detected=true;
			 if (sentido_msg.equals("IN")) {
			 cursor_keysID.put(valor_campo, id_integracion);
			 }
			 if (sentido_msg.equals("OUT")) {
			 id_integracion_calculate=cursor_keysID.get(valor_campo);
			 if (id_integracion_calculate == null)
			 {
				 id_integracion_calculate="";
			 }
			 }
			  
		 }
	 }
	 
	 
	 private boolean StringValid(String valor,int lon_min, int lon_max)
	 {
		 /*if (valor.length()==0)
		 {
			 return false;
		 }*/
		 
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
	 
	 private boolean DoubleValid(String valor,int lon_min, int lon_max, String mascara)
	 {
		 valor = valor.replaceAll(",", ".");
		 try {
			 double d=Double.parseDouble( valor) ;
		 }catch(Exception e)
		 {
			 return false;
		 }
		 //Validar el formato del double
		 try {
			 DecimalFormat df = new DecimalFormat(mascara);
			 double d=Double.parseDouble( valor) ;
			 String salida = df.format(d);
			 if (!salida.replaceAll(",", ".").equals(valor))
			 {
				 return false;
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 return false;
		 }
		 
		 
		 
		 if (valor.length()<lon_min || valor.length()>lon_max)
		 {
			 return false;
		 }
		 return true;
	 }
	 
	 private boolean DateValid(String valor,String format)
	 {
		
		 try {
			 Date date1=new SimpleDateFormat(format).parse(valor); 
		 }catch(Exception e)
		 {
			 return false;
		 }
		 
		 
		 return true;
	 }
	 
	 
	 private String getElementXMLString(String texto,String campo)
	 {
/*		 String texto_work=texto.toUpperCase().replaceAll("</.*?:", "</").replaceAll("<.*?:", "<");
		 //texto_work=texto_work.toUpperCase().replaceAll("<typ:".toUpperCase(), "<");
		 String tag_ini="<" + campo.toUpperCase() + ">";
		 String tag_fin="</" + campo.toUpperCase() + ">";
	*/	 
		 if (campo.equals("BUSINESS_UNIT"))
		 {
			 System.out.println("hehe");
		 }
		 			
			int i=texto.lastIndexOf(campo+">");
			int j=texto.lastIndexOf(campo+">",i-1);
			
			 if (i==-1 || j==-1)//NO viene el Campo
			 {
				 return "NA";
			 }
		
			
		 
			 String valor = texto.substring(j+campo.length()+1, i);
			 int t=valor.lastIndexOf("</");
		        if (t>0)
		        {
		        	return  valor.substring(0, t);
		        }
		        
		        return "";
			 //return valor;
		 /*
		 String texto_work = texto.replaceAll("<?:", "<");
		 
		 if (campo.equals("BUSINESS_UNIT"))
		 {
			 System.out.println("hehe");
		 }

			String patron ="<.*?"+campo+">(.+?)<?"+campo+">";
			//   Pattern p = Pattern.compile("<.*?CM_TRANS_EXT>(.+?)<*CM_TRANS_EXT>", Pattern.DOTALL);
			Pattern p = Pattern.compile(patron, Pattern.DOTALL);
			//Pattern p = Pattern.compile(patron);
			Matcher m = p.matcher(texto);
			while(m.find()){
		        String b =  m.group(1);
		        int t=b.lastIndexOf("</");
		        if (t>0)
		        {
		        	return  b.substring(0, t);
		        }
		        System.out.println("salida=" +b);
		        return "";
		    }
			return "NA";
		 /*
		 int i=texto_work.indexOf(tag_ini);
		 int j=texto_work.indexOf(tag_fin);
		 
 		 if (i==-1 || j==-1)//NO viene el Campo
		 {
			 return "NA";
		 }

		 String valor = texto_work.substring(i + tag_ini.length(), j);
		 return valor;
		 */
	 }
	 
	 private Map<String,String> ConstructMsgLog(JSONObject  msg, BasicDBObject result)
	 {
		 Map<String,String> Msg = new HashMap<String, String>();
		 boolean sen=false;
		 String sentido="IN";
		 String f = "";
		 try {
		 f=result.get("respuesta_remota_detail").toString();
		 sentido="OUT";
		 sen=true;
		 }catch(Exception e)
		 {
			 sen=false;
		 }
		 
		 try {
		 String _id="FX_AUTO_INCREMENT";
		 String fecha_log="FX_CREATE_DATE";
		 String id_integracion=result.getString("id_integracion").toString();
		 String resultado=result.getString(ParamDefinitions._CONST_RESPONSE);
		 String resultado_det=result.getString(ParamDefinitions._CONST_RESPONSE_DETAIL);
		 String app_origen=msg.getString("app_origen").toString();
		 String app_destino=msg.getString("app_destino").toString();
		 String texto_log=result.getString("mensaje").replaceAll("\n","");
		 Msg.put("_id", _id);
		 Msg.put("fecha_log", fecha_log);
		 Msg.put("id_integracion", id_integracion);
		 Msg.put("resultado", resultado.substring(1, 3));
		 Msg.put("id_mensaje", msg.getString("id_mensaje").toString());
		 Msg.put("sentido_mensaje",sentido);
		 Msg.put(ParamDefinitions._CONST_RESPONSE_DETAIL, resultado_det);
		 if (sen)//de regreso
		 {
			 Msg.put("app_origen", app_destino);
			 Msg.put("app_destino", app_origen);
		 }
		 else
		 {
			 Msg.put("app_origen", app_origen);
			 Msg.put("app_destino", app_destino);
			 
		 }
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
		 //System.out.println("enviar a la cola" +queue +" =>" +message );
		 jmsTemplate.convertAndSend(queue, message);
		 
	 }
	 
	 private Map<String,String> ConstructMsgResponse(JSONObject  msg, BasicDBObject result)
	 {
		 Map<String,String> Msg = new HashMap<String, String>();
		 
		 try {
	     RandomString session = new RandomString();
		 String _id=session.nextString();
		 String id_integracion=result.getString("id_integracion").toString();
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
		 Msg.put("id_mensaje", msg.getString("id_mensaje").toString());
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
	 
	 public static void main (String ar[])
	 {
		String str ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://localhost:8088/WsFXF.wsdl/types/\">\n" + 
				"   <soapenv:Header/>\n" + 
				"   <soapenv:Body>\n" + 
				"      <typ:prcFdPrecancelacionElement>\n" + 
				"      </typ:prcFdPrecancelacionElement>\n" + 
				"   </soapenv:Body>\n" + 
				"</soapenv:Envelope>"; 
		str = str.replaceAll("</.*?:", "</").replaceAll("<.*?:", "<");
		System.out.println("salida=" + str);
		
		 //Consumer c = new Consumer();
		// System.out.println(c.DateValid("2019-06-01 12:00", "yyyy-M-dd h:mm"));
		// System.out.println(c.DoubleValid("22,5", 1, 6, "#.##"));
	 }
}
