package com.fiduprod.route;

import org.apache.camel.builder.RouteBuilder;

import com.fiduprod.ParamDefinitions;
import com.fiduprod.processor.FileTransferProcessor;

public class FileTransferRouteBuilder extends RouteBuilder {

	@Override
	public void configure() {

		from("timer:simple?period=1000")
		.log("Disparando procesamiento del archivo.")
//		.setHeader("Servidor", constant("localhost"))
//		.setHeader("Nombre de usuario", constant("andreita"))
//		.setHeader("Contraseña", constant("123456789"))
		.setHeader("Servidor", constant(ParamDefinitions._CONST_FTP_SERVER))
		.setHeader("Nombre de usuario", constant(ParamDefinitions._CONST_FTP_SERVER_USER))
		.setHeader("Contraseña", constant(ParamDefinitions._CONST_FTP_SERVER_PASS))
//		.setBody(constant("C:/entrada"))
		.to("direct:procesarArchivo")
		.end();
		
		from("direct:procesarArchivo")
		.log("inicio procesamiento del archivo")
		.process(new FileTransferProcessor())
		.end();
		
	}

}
