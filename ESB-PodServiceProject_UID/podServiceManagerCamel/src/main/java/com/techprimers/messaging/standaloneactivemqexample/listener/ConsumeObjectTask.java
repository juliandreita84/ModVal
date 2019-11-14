package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.jms.Queue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.jms.core.JmsTemplate;

import com.mongodb.BasicDBObject;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class ConsumeObjectTask  implements  Processor{
	
public JmsTemplate jmsTemplate;
public BasicDBObject data;
public static Queue queue_log;
public static int id_message=0; 
public String id_task="";
public boolean flag_executed=false;
public static ControllerScheduler controller;

ScheduledExecutorService scheduler
= Executors.newSingleThreadScheduledExecutor();


public  ConsumeObjectTask(JmsTemplate j,Queue q ,BasicDBObject k, ControllerScheduler c,String i)
{
	jmsTemplate=j;
	queue_log=q;
	data = k;
	controller=c;
	id_task=i;
	System.out.println("creo contexto");
}


public void findFile()
{
	data=controller.schedulerTask.get(id_task);
	String hora_siguiente_ejecucion=data.get("hora_siguiente_ejecucion").toString();
	SimpleDateFormat time_compare = new SimpleDateFormat("HH:mm");
	String timeString = time_compare.format(new Date());
	String ejecutando = data.get("ejecutando").toString();
	Calendar now = Calendar.getInstance();
	int dia = now.get(Calendar.DAY_OF_WEEK)-1;
	if (hora_siguiente_ejecucion.equals(timeString)&& !controller.searchTaskExecuted(id_task,dia+"")&& ejecutando.equals("N"))
	{
		id_message++;
		data.put("ejecutando", "S");
		controller.schedulerTask.put(id_task, data);
		controller.response_consume=ParamDefinitions._00_MESSAGE_OK;
		String name_task = data.get("_id").toString() + "," +data.get("nombre_tarea").toString() + "," + data.get("tipo_tarea").toString();
		String message="Verificacion del "+ data.get("tipo_tarea") + " Para la Integracion {" +  name_task + "}";
		//Enviar Hilo de 
		controller.sendThreadLog(id_task,message,ParamDefinitions._CONST_TYPE_IN,id_message,name_task);
		
		ftpClientConsume sq = new  ftpClientConsume();
		sq.Server=data.get("ruta_tarea").toString();
		sq.Puerto=data.get("tarea_puerto").toString();
		sq.Usuario=data.get("tarea_usuario").toString();
		sq.Clave=data.get("tarea_clave").toString();
		sq.id=data.get("_id").toString();
		sq.id_key=id_task;
		sq.id_msg=id_message;
		sq.controller=controller;
		sq.file_search=data.get("tarea_patron").toString();
   		 Thread nt = new Thread(sq);
   		 nt.start();
		
		/*BasicDBObject k1 = new BasicDBObject();
		k1.put("estado", "00");
		k1.put("id", k.get("id"));
		k1.put("dia_ejecutado", tU);
		int indice = executedTask.size();
		executedTask.put(""+indice, k1);*/
	}
}

/*
public void createService()
{
	System.out.println("llego-----");
	Runnable task = new ConsumeObjectTask(jmsTemplate,queue_log,data,controller,id_task);
    int initialDelay = 0;
    int periodicDelay = 5;
    scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay,
            TimeUnit.SECONDS
    );
    //loadSchedulerTask();
}


@Override
public void run() {
	// TODO Auto-generated method stub
	System.out.println("corre");
	findFile();
}
*/

@Override
public void process(Exchange arg0) throws Exception {
	// TODO Auto-generated method stub
	findFile();
}



}
