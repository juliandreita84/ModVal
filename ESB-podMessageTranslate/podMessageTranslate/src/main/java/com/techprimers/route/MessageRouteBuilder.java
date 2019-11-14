package com.techprimers.route;

import org.apache.camel.builder.RouteBuilder;

public class MessageRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
        from("direct:marshalObjectxml2json")
        .to("log:?level=INFO&showBody=true")
        .marshal().xmljson()
        .log(" 2. Mensaje en JSON = ${body}")
        .to("log:?level=INFO&showBody=true");
        
	}

}
