package com.techprimers.messaging.standaloneactivemqexample.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;

@Configuration
public class Config {
	
	private String QueueAMQ="Message_IN"; 
	private String QueueAMQ_2="Message_OUT";
	private String QueueAMQ_IN=ParamDefinitions._CONST_AQM_QUEUE_IN;
	private String QueueAMQ_OUT=ParamDefinitions._CONST_AQM_QUEUE_OUT;
	
   //@Value(ParamDefinitions._CONST_AQM_SERVER)
    private String brokerUrl = ParamDefinitions._CONST_AQM_SERVER;

    public String getQueueAMQ() {
		return QueueAMQ;
	}
    
    public String getQueueAMQ_IN() {
		return QueueAMQ_IN;
	}

    public String getQueueAMQ_OUT() {
		return QueueAMQ_OUT;
	}


	public void setQueueAMQ(String queueAMQ) {
		QueueAMQ = queueAMQ;
	}

	@Bean
    public Queue queue() {
        return new ActiveMQQueue(QueueAMQ);
    }

	@Bean
    public Queue queue2() {
        return new ActiveMQQueue(QueueAMQ_2);
    }
	@Bean
    public Queue queue_IN() {
        return new ActiveMQQueue(QueueAMQ_IN);
    }
	
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER_USER,ParamDefinitions._CONST_AQM_SERVER_USER, ParamDefinitions._CONST_AQM_SERVER );
        factory.setBrokerURL(brokerUrl);
        return factory;
    }


    public ActiveMQConnectionFactory activeMQConnectionFactoryManual() {
    	 ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER_USER,ParamDefinitions._CONST_AQM_SERVER_USER, ParamDefinitions._CONST_AQM_SERVER );
         //factory.setBrokerURL(brokerUrl);
         Connection connection;
 		try {
 			connection = factory.createConnection();
 			 connection.start();
 		} catch (JMSException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         
         return factory;
    }
    
    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }
    

    public JmsTemplate jmsTemplateManual() {
        return new JmsTemplate(activeMQConnectionFactoryManual());
    }
}
