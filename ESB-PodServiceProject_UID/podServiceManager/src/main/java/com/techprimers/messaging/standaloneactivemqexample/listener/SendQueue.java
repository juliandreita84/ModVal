package com.techprimers.messaging.standaloneactivemqexample.listener;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;
import org.springframework.jms.core.JmsTemplate;

public class SendQueue implements Runnable {

	public static Queue queue;
	public static JmsTemplate jmsTemplate;
	public Map<String, Object> data_send = new HashMap<String, Object>();
	public SendQueue(Queue q, JmsTemplate j, Map<String, Object> d )
	{
		this.queue = q;
		this.jmsTemplate=j;
		this.data_send =d;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
		this.jmsTemplate.convertAndSend(this.queue, data_send);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
