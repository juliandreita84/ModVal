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
	private String QueueAMQ=ParamDefinitions._CONST_AQM_QUEUE_DB; 
	
    //@Value(ParamDefinitions._CONST_AQM_SERVER)
    private String brokerUrl=ParamDefinitions._CONST_AQM_SERVER;

    @Bean
    public Queue queue() {
        return new ActiveMQQueue(QueueAMQ);
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ParamDefinitions._CONST_AQM_SERVER_USER,ParamDefinitions._CONST_AQM_SERVER_PASS, ParamDefinitions._CONST_AQM_SERVER );
//        factory.setBrokerURL(brokerUrl+ "?initialReconnectDelay=2000&maxReconnectAttempts=2000");
        factory.setBrokerURL(brokerUrl);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }
    
    
}
