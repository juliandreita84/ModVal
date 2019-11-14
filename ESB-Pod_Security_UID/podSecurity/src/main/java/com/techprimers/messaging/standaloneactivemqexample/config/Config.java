package com.techprimers.messaging.standaloneactivemqexample.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

import javax.jms.Queue;

@Configuration
public class Config {
	
	private String QueueAMQ="BusPersistence"; 
	private String QueueAMQ_IN="BusService_In";
	private String QueueAMQ_OUT="BusService_OUT";
	
    @Value(ParamDefinitions._CONST_AQM_SERVER)
    private String brokerUrl;

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
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }
}
