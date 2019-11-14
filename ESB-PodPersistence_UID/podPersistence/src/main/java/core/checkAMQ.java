package core;

import org.springframework.jms.core.JmsTemplate;

public class checkAMQ  implements Runnable {

	public static JmsTemplate jms;
	
	public checkAMQ(JmsTemplate j)
	{
		this.jms =j;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
