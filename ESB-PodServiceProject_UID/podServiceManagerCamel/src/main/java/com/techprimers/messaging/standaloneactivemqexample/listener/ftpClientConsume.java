package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.mongodb.BasicDBObject;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class ftpClientConsume implements Runnable{
	
	
	public String Puerto;
	public String id;
	public int id_msg;
	public String id_key;
	public String Usuario; 
	public String Clave;
	public static ControllerScheduler controller;
	public String Server;
	public String file_search;
	public boolean result_check=false;
	public String data_info="";
	public String general_error="";
	
	public static void main(String arg[])
	{
		ftpClientConsume f = new ftpClientConsume();
		//f.checkFILE();
	}
	
	
	public void setExecutedTask(String estado)
	{
		String isoDatePattern = "EEEE";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
		String dateString = simpleDateFormat.format(new Date());
		String tU=dateString.toUpperCase();
		Calendar now = Calendar.getInstance();
		int dia = now.get(Calendar.DAY_OF_WEEK)-1;
		BasicDBObject k1 = new BasicDBObject();
		k1.put("estado", estado);
		k1.put("_id", id);
		k1.put("dia_ejecutado", dia+"");
		int indice = controller.executedTask.size();
		controller.executedTask.put(""+indice, k1);
		
		//Activar siguiente intento
		BasicDBObject g = new BasicDBObject();
		g=controller.schedulerTask.get(""+id_key);
		String index_task = g.get("_id").toString();
		String name_task = index_task + "," +g.get("nombre_tarea").toString() + "," + g.get("tipo_tarea").toString();
		if (!estado.equals(ParamDefinitions._00_MESSAGE_OK))
		{
				if (Integer.parseInt(g.get("numero_intentos_ejecutados").toString())>0)
				{
					//Poner la siguiente hora de ejecucion
					String tm=g.get("tiempo_siguiente_intento").toString();
					
					 String myTime = g.get("hora_siguiente_ejecucion").toString();
					 SimpleDateFormat df = new SimpleDateFormat("HH:mm");
					 Date d = null;
					try {
						d = df.parse(myTime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					 Calendar cal = Calendar.getInstance();
					 cal.setTime(d);
					 cal.add(Calendar.MINUTE, Integer.parseInt(tm));
					 String newTime = df.format(cal.getTime());
					 
					 //Asignar nueva hora de ejecucion
					 g.put("hora_siguiente_ejecucion", newTime);
					 //Disminuir en 1 los intentos
					 g.put("numero_intentos_ejecutados", Integer.parseInt(g.get("numero_intentos_ejecutados").toString())-1);
					 g.put("ejecutando", "N");
					 controller.schedulerTask.put(""+id_key,g);
					 int intento=Integer.parseInt(g.get("numero_intentos_ejecutados").toString());
					 String message="Resultado de la verificacion de la tarea fallido, se programa la siguiente ejecucion para las " + newTime + " Quedan " +  intento + " Intentos en la ejecucion de la tarea " + "{"+ name_task + "}" ;
					 controller.response_consume=ParamDefinitions._04_ERROR_FTP;
					 controller.response_consume_detail=message +  general_error;
					 controller.sendThreadLog(id_key,  message,ParamDefinitions._CONST_TYPE_OUT, id_msg,name_task);
					 System.out.println("Fallo intento " + controller.schedulerTask);
				}else
				{
					g.put("numero_intentos_ejecutados", g.get("numero_intentos").toString());
					g.put("hora_siguiente_ejecucion", g.get("hora_inicio_ejecucion").toString());
					g.put("ejecutando", "N");
					controller.schedulerTask.put(""+id_key,g);
					System.out.println("Termino intentos " + controller.schedulerTask);
					String message="Termino: Resultado de la verificacion de la tarea fallido, se ejecutaron todos los intentos posibles de la tarea " + "{"+ name_task + "}";
					 controller.response_consume=ParamDefinitions._04_ERROR_FTP;
					 controller.response_consume_detail=message + general_error;
					 controller.sendThreadLog(id_key,  message,ParamDefinitions._CONST_TYPE_OUT, id_msg,name_task);
					 //Remover la Ruta
					 controller.removeRoute(index_task);//
				}
		}else
		{
			g.put("numero_intentos_ejecutados", g.get("numero_intentos").toString());
			g.put("hora_siguiente_ejecucion", g.get("hora_inicio_ejecucion").toString());
			g.put("ejecutando", "N");
			controller.schedulerTask.put(""+id_key,g);
			System.out.println("Reiniciar Contadores " + controller.schedulerTask);
			String message="Termino: Resultado de la verificacion de la tarea "+ "{"+ name_task + "}" + " exitosa, Informacion del archivo(s): "+ data_info ;
			 controller.response_consume=ParamDefinitions._00_MESSAGE_OK;
			 //controller.response_consume_detail=message;
			 controller.sendThreadLog(id_key,  message,ParamDefinitions._CONST_TYPE_OUT, id_msg,name_task);
			//Remover la Ruta
			 controller.removeRoute(index_task);//

		}
	}
	
	public boolean checkFILE(String Puerto, String Usuario, String Clave, String Server)
	{
		String server = Server;
        int port = Integer.parseInt(Puerto);
        String user = Usuario;
        String pass = Clave;
        data_info="";
        general_error="";
        FTPClient ftpClient = new FTPClient();
        try {
 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            FTPFile[] listFiles = ftpClient.listFiles();
            
            for (int i=0; i<listFiles.length;i++)
            {
            	if (file_search.toUpperCase().equals(listFiles[i].getName().toUpperCase()))
            	{
            		//Aqui se debera colocar las validaciones adicionales al archivo
            		FTPFile archivo = listFiles[i];
            		data_info+="<br> Nombre: " +archivo.getName() + "  " + "<br> Tam: " + " KB (" + archivo.getSize()+ " bytes)"  + " <br> Informacion General: " + archivo.getRawListing();
            		setExecutedTask(ParamDefinitions._00_MESSAGE_OK);
            		System.out.println(listFiles[i]);
            		result_check=true;   
            		return result_check;
            	}
            
            }
            ftpClient.disconnect();
        }catch (Exception e)
        {
        	result_check=false;	
        	general_error=", " +e.getMessage();
        }
        setExecutedTask(ParamDefinitions._04_ERROR_FTP);
        result_check=false;
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		checkFILE( Puerto,  Usuario,  Clave,  Server);
	}

}
