package com.fiduprod.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import com.fiduprod.ParamDefinitions;

import javax.jms.Queue;

@Configuration
public class Config {
	
	private String QueueAMQ="BusPersistence"; 
	private String QueueAMQ_IN=ParamDefinitions._CONST_AQM_QUEUE_IN;
	private String QueueAMQ_OUT=ParamDefinitions._CONST_AQM_QUEUE_OUT;
	
   // @Value(ParamDefinitions._CONST_AQM_SERVER)
    private String brokerUrl=ParamDefinitions._CONST_AQM_SERVER;

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
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER_USER,ParamDefinitions._CONST_AQM_SERVER_PASS, ParamDefinitions._CONST_AQM_SERVER );
        factory.setBrokerURL(brokerUrl);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }
}
