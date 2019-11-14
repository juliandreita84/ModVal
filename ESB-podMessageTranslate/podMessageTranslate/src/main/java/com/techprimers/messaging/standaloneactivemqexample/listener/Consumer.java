package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.Queue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
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
import com.techprimers.route.MessageRouteBuilder;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

@Component
public class Consumer extends CamelTestSupport {
	private ConsumeWebService consumeWebService = new ConsumeWebService();
	private String detail_error = "";
	public static Map<String, BasicDBObject> cursor_reglas = new HashMap<String, BasicDBObject>();
	@Autowired
	JmsTemplate jmsTemplate;
	public CamelContext context = new DefaultCamelContext();
	public MessageRouteBuilder routeBuilder = new MessageRouteBuilder();
	
	@Autowired
	Queue queue;

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new MessageRouteBuilder();
	}

	@JmsListener(destination = "BusService_In")
	@Test
	public void consumeBusService_In(Map<String,String> message) throws Exception {

		try {
		//System.out.println("Received Message: " + message);
		@SuppressWarnings("deprecation")
		Config con = new Config();
		String msg=message.get("mensaje");
		String tipo_msg=message.get("tipo_mensaje");
		if (tipo_msg.equals("NO_XML"))//Pass True
		{
			message.put("mensaje_rest", "");
			jmsTemplate.convertAndSend(con.getQueueAMQ_OUT(), message);
			return;
		}
		
		
		String response = "";
		if (context.getRoutes().size()==0)
		{
		context.addRoutes(routeBuilder);
		
		context.start();
		}
		try {
			ProducerTemplate producer = context.createProducerTemplate();
			response = producer.requestBody("direct:marshalObjectxml2json", msg.replaceAll("</.*?:", "</").replaceAll("<.*?:", "<"), String.class);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			//context.stop();
		}
		//System.out.println("response 1 is : " + response);
		
		message.put("mensaje_rest", response);
		jmsTemplate.convertAndSend(con.getQueueAMQ_OUT(), message);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static Document convertStringToXMLDocument(String xmlString) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean validateMessage(String doc, Map<String, BasicDBObject> cursor) {
		int cont_error = 0;
		detail_error = "";
		try {
			for (int w = 0; w < cursor.size(); w++) {
				BasicDBObject o = (BasicDBObject) cursor.get(w + "");
				String nombre_campo = (String) o.get("nombre_campo");
				String tipo_campo = (String) o.get("tipo_campo");
				String servicio = (String) o.get("servicio");
				int lon_min = (int) Double.parseDouble(o.get("longitud_minima").toString());
				int lon_max = (int) Double.parseDouble(o.get("longitud_maxima").toString());

				// Validar el Servicio
				String valid_service = getElementXMLString(doc, servicio);
				if (valid_service.equals(servicio) || valid_service.length() > 0) {

					String valor_campo = getElementXMLString(doc, nombre_campo);
					if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_STRING)) {
						if (!StringValid(valor_campo, lon_min, lon_max)) {
							cont_error++;
							detail_error += "FORMATO INVALIDO CAMPO [" + nombre_campo + "],";
							// return false;
						}
					}
					if (tipo_campo.equals(ParamDefinitions._CONST_FORMAT_NUMBER)) {
						if (!NumberValid(valor_campo, lon_min, lon_max)) {
							cont_error++;
							detail_error += "FORMATO INVALIDO CAMPO [" + nombre_campo + "],";
							// return false;
						}
					}
					// System.out.println("campo =>"+nombre_campo +"="+valor_campo);
				}
				w++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cont_error == 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean StringValid(String valor, int lon_min, int lon_max) {
		if (valor.length() == 0) {
			return false;
		}

		if (valor.length() < lon_min || valor.length() > lon_max) {
			return false;
		}
		return true;
	}

	private boolean NumberValid(String valor, int lon_min, int lon_max) {

		try {
			double d = Double.parseDouble(valor);
		} catch (Exception e) {
			return false;
		}

		if (valor.length() < lon_min || valor.length() > lon_max) {
			return false;
		}
		return true;
	}

	private String getElementXMLString(String texto, String campo) {
		String texto_work = texto.replaceAll("</typ:", "</");
		texto_work = texto_work.replaceAll("<typ:", "<");
		String tag_ini = "<" + campo + ">";
		String tag_fin = "</" + campo + ">";

		int i = texto_work.indexOf(tag_ini);
		int j = texto_work.indexOf(tag_fin);

		if (i == -1) {
			return "";
		}

		String valor = texto_work.substring(i + tag_ini.length(), j);
		return valor;
	}

	private Map<String, String> ConstructMsgLog(JSONObject msg, BasicDBObject result) {
		Map<String, String> Msg = new HashMap<String, String>();

		try {
			String _id = "FX_AUTO_INCREMENT";
			String fecha_log = "FX_CREATE_DATE";
			String id_integracion = msg.getString("id_integracion").toString();
			String resultado = result.getString(ParamDefinitions._CONST_RESPONSE);
			String resultado_det = result.getString(ParamDefinitions._CONST_RESPONSE_DETAIL);
			String app_origen = msg.getString("app_origen").toString();
			String app_destino = msg.getString("app_destino").toString();
			String texto_log = result.getString("mensaje").replaceAll("\n", "");
			Msg.put("_id", _id);
			Msg.put("fecha_log", fecha_log);
			Msg.put("id_integracion", id_integracion);
			Msg.put("resultado", resultado.substring(1, 3));
			Msg.put(ParamDefinitions._CONST_RESPONSE_DETAIL, resultado_det);
			Msg.put("app_origen", app_origen);
			Msg.put("app_destino", app_destino);
			Msg.put("texto_log", texto_log);
			Msg.put("origen", ParamDefinitions._CONST_ORIGEN_DATA);

			// Msg="_id=" +_id + "&" + "fecha_log=" +fecha_log + "&" +"id_integracion="
			// +id_integracion + "&" +"resultado=" +resultado + "&" +"app_origen="
			// +app_origen + "&" + "app_destino=" +app_destino + "&" + "texto_log="
			// +texto_log ;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Msg;
	}

	private void sendLogMessage(JSONObject msg, BasicDBObject result) {
		Map<String, String> message = ConstructMsgLog(msg, result);
		// System.out.println("enviar a la cola" +queue +" =>" +message );
		jmsTemplate.convertAndSend(queue, message);

	}

	private Map<String, String> ConstructMsgResponse(JSONObject msg, BasicDBObject result) {
		Map<String, String> Msg = new HashMap<String, String>();

		try {
			RandomString session = new RandomString();
			String _id = session.nextString();
			String id_integracion = msg.getString("id_integracion").toString();
			String resultado = result.getString(ParamDefinitions._CONST_RESPONSE);
			String app_origen = msg.getString("app_origen").toString();
			String app_destino = msg.getString("app_destino").toString();
			String texto_log = result.getString("mensaje").replaceAll("\n", "");
			String puerto = result.getString("puerto");
			String ruta_respuesta = result.getString("ruta_respuesta");
			Msg.put("_id", _id);
			Msg.put("fecha_repuesta", new Date() + "");
			Msg.put("id_integracion", id_integracion);
			Msg.put("resultado", resultado.substring(1, 3));
			Msg.put("id_mensaje", msg.getString("id_mensaje").toString());
			Msg.put("app_origen", app_origen);
			Msg.put("app_destino", app_destino);
			Msg.put("mensaje", texto_log);
			Msg.put("puerto", puerto);
			Msg.put("ruta_salida", ruta_respuesta);
			Msg.put("origen", ParamDefinitions._CONST_ORIGEN_DATA);

			// Msg="_id=" +_id + "&" + "fecha_log=" +fecha_log + "&" +"id_integracion="
			// +id_integracion + "&" +"resultado=" +resultado + "&" +"app_origen="
			// +app_origen + "&" + "app_destino=" +app_destino + "&" + "texto_log="
			// +texto_log ;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Msg;
	}
}
