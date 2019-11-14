package core;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.techprimers.messaging.standaloneactivemqexample.config.Config;

@Component
public class WebServer implements HttpHandler {
    @Autowired
    static JmsTemplate jmsTemplate;

    @Autowired
    static Queue queue;
    
    private Config config = new Config();
   
    public  static Map <String, Object> route_data = new HashMap<String, Object>();
    
	public void createService( Map<String, Object> index_route,int port_number,Queue queue,JmsTemplate jmsTemplate) throws IOException
	{
		route_data =index_route;
		this.queue = config.queue_IN();
		this.jmsTemplate=jmsTemplate;
		
		System.out.println("jms " + jmsTemplate);
		HttpServer server = HttpServer.create(new InetSocketAddress(port_number), 0);

	    server.createContext("/PodService", new WebServer());
	    server.setExecutor(null); // creates a default executor
	    server.start();
	}
	
	
	@Override
	    public void handle(HttpExchange he) throws IOException {
	    /*	InputStream  a =  t.getRequestBody();
	    	String b= a.read()
	      byte [] response = "Welcome Real's HowTo test page".getBytes();
	      t.sendResponseHeaders(200, response.length);
	      OutputStream os = t.getResponseBody();
	      os.write(response);
	      os.close();
			InputStream is = t.getRequestBody();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len;
			while ((len = is.read(buffer))>0) {
				bos.write(buffer, 0, len);
			}
			bos.close();
			String data = new String(bos.toByteArray(), Charset.forName("UTF-8"));

			OutputStream os = t.getResponseBody();
		      os.write(data.getBytes());
		      os.close();*/

	    	 System.out.println("Serving the request");
	    	 
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
	                    int length = is.read(data);
	                    he.getResponseHeaders();
	                 // RESPONSE Headers
	                    Headers responseHeaders = he.getResponseHeaders();
	 
	                    // Send RESPONSE Headers
	                    he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
	 
	                    he.getResponseBody();
	                    Map <String,String> detail=new HashMap<String, String>();
	                    String port=""+he.getLocalAddress().getPort();
	                    detail=(Map<String, String>) route_data.get(port);
	                    Map<String, Object> data_send = new HashMap<String, Object>();
	                    
	                    data_send=constructMessageRules(port,new String(data));
	                    this.jmsTemplate.convertAndSend(this.queue, data_send);
	                    
	                    //os.write(data);
	 
	                    he.close();
	 
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	 
	        }	
	  
	public Map<String, Object> constructMessageRules(String port,String data)
	{
		Map <String,String> detail=new HashMap<String, String>();
        detail=(Map<String, String>) route_data.get(port);
        Map<String, Object> data_send = new HashMap<String, Object>();
        data_send.put("id_integracion", detail.get("id_integracion").toString() );
        data_send.put("app_origen", detail.get("app_origen").toString() );
        data_send.put("app_destino", detail.get("app_destino").toString() );
        data_send.put("fecha_msg", "'"+ new Date()+"'" );
        data_send.put("mensaje", data );
        data_send.put("puerto", port);
        data_send.put("ruta_respuesta", detail.get("ruta_salida").toString());
        data_send.put("formato_msg", detail.get("formato").toString() );
        System.out.println("envio " + data_send);
        System.out.println("datos config msg " + detail);
        System.out.println("jms" + this.jmsTemplate );
        return data_send;
	}
	
}
