package dbConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.techprimers.messaging.standaloneactivemqexample.listener.ConsumeWebService;

public class GenerateAlerts implements Runnable {
	
	private Map<String,String> message;
	private int umbral;
	private String ULR;

	public static Map<String , String>  cursor_alertas = new HashMap<String , String> (); 
	
 	public  void checkMessage(Map<String,String> message,int umbral, String ULR)
 	{
 		String id_integracion=message.get("id_integracion");
 		String resultado=message.get("resultado");
 		String men=message.get("texto_log");
 		String sentido_mensaje=message.get("sentido_mensaje");
 		
 		String est="RUN";
 		//Para monitoreo de las tareas
 		//if (men.indexOf("FILE")>=0 && men.substring(0, 50).indexOf("Verificacion")==-1)
 		if (men.indexOf("FILE")>=0 && sentido_mensaje.equals("OUT"))
 		{
 			if (men.indexOf("Termino:")>=0)
 			{
 				est="END";
 			}
 			
 			ConsumeWebService c = new ConsumeWebService();
				try {
					JSONObject consume_dat0 = new JSONObject();
					consume_dat0.put("RQ", id_integracion);
					consume_dat0.put("TIPO", "TAREA");
					consume_dat0.put("VAL", men);
					consume_dat0.put("EST", est);
					consume_dat0.put("RES", resultado);
				c.GETRequest(ULR ,consume_dat0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 			
 		
 		if (!resultado.equals("00")&& men.indexOf("FILE")==0)
 		{
 			int contador=0;
 			try
 			{
 				String ct= cursor_alertas.get(id_integracion);
 				//System.out.println("ct= " +ct);
 				contador=Integer.parseInt(ct);
 			}catch(Exception e)
 			{
 				
 			}
 			if (contador >= umbral)//Generar alerta
 			{
 				ConsumeWebService c = new ConsumeWebService();
 				try {
 					JSONObject consume_dat0 = new JSONObject();
 					consume_dat0.put("RQ", id_integracion);
 					consume_dat0.put("TIPO", "MENSAJE");
 					consume_dat0.put("VAL", contador+"");
					c.GETRequest(ULR ,consume_dat0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 				cursor_alertas.put(id_integracion, "0");
 			}else
 			{
 			contador++;
 			cursor_alertas.put(id_integracion, contador+"");
 			}
 		}
 	}
 	
 	public GenerateAlerts(Map<String,String> m, int um,String ul)
 	{
 		this.message=m;
 		this.umbral=um;
 		this.ULR=ul;
 	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		checkMessage(message,umbral,ULR);
	}
	
}
