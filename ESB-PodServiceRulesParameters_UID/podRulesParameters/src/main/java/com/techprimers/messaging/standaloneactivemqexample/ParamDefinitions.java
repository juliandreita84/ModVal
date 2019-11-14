package com.techprimers.messaging.standaloneactivemqexample;

public class ParamDefinitions {
//PARAMS FOR RESPONSE TYPES
	public static String _00_MESSAGE_OK ="_00_MESSAGE_OK";
	public static String _01_ERROR_TIPO_DATO ="_01_ERROR_TIPO_DATO";
	public static String _02_WITHOUT_DATA ="_02_WITHOUT_DATA";
	public static String _03_TIME_OUT ="_03_TIME_OUT";

//PARAMS FOR CONSTANS VALUES
	public static String _CONST_RESPONSE ="response";
	public static String _CONST_RESPONSE_DETAIL ="response_detail";
	public static String _CONST_FORMAT_XML ="XML";
	public static String _CONST_FORMAT_NO_XML ="FILE";
	public static String _CONST_FORMAT_DATE ="DATE";
	public static String _CONST_FORMAT_STRING ="STRING";
	public static String _CONST_FORMAT_NUMBER ="NUMBER";
	public static String _CONST_FORMAT_DOUBLE ="DOUBLE";
	public static String _CONST_ORIGEN_DATA ="CONSUME";
	public static String COLLECTION_RULES_MESSAGES ="rules_messages";
	
	//PARAMS FOR CONSTANS CREDENTIALS 
		public static String _CONST_AQM_SERVER = "tcp://localhost:61616";
		public static String _CONST_CONSUME_WEBSERVICE_POST="http://localhost:8080/rest/podDataInterfacePost/";
		public static String _CONST_AQM_SERVER_USER="admin";
		public static String _CONST_AQM_SERVER_PASS="admin";
		public static String _CONST_AQM_QUEUE_IN="BusService_In";
		public static String _CONST_AQM_QUEUE_OUT="BusService_OUT";
		public static String _CONST_RESPONSE_DEFAULT=System.getenv("_CONST_RESPONSE_DEFAULT");
		
}
