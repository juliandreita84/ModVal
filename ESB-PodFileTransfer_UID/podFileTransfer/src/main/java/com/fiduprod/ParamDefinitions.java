package com.fiduprod;

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
	public static String _CONST_FORMAT_DATE ="DATE";
	public static String _CONST_FORMAT_STRING ="STRING";
	public static String _CONST_FORMAT_NUMBER ="NUMBER";
	public static String _CONST_FORMAT_DOUBLE ="DOUBLE";
	public static String _CONST_ORIGEN_DATA ="CONSUME";
	public static String COLLECTION_RULES_MESSAGES ="rules_messages";
	
	//PARAMS FOR CONSTANS CREDENTIALS 
	//Desarrollo

		public static String _CONST_AQM_SERVER = System.getenv("_CONST_AQM_SERVER");
		public static String _CONST_CONSUME_WEBSERVICE_POST=System.getenv("_CONST_CONSUME_WEBSERVICE_POST");
		public static String _CONST_AQM_SERVER_USER=System.getenv("_CONST_AQM_SERVER_USER");
		public static String _CONST_AQM_SERVER_PASS=System.getenv("_CONST_AQM_SERVER_PASS");
		public static String _CONST_AQM_QUEUE_IN=System.getenv("_CONST_AQM_QUEUE_IN");
		public static String _CONST_AQM_QUEUE_OUT=System.getenv("_CONST_AQM_QUEUE_OUT");
		public static String _CONST_RESPONSE_DEFAULT=System.getenv("_CONST_RESPONSE_DEFAULT");
			
		public static String _CONST_FTP_SERVER = System.getenv("_CONST_FTP_SERVER");
		public static String _CONST_FTP_SERVER_USER=System.getenv("_CONST_FTP_SERVER_USER");
		public static String _CONST_FTP_SERVER_PASS=System.getenv("_CONST_FTP_SERVER_PASS");
		
		
	//OPenshift
	/*	public static final String _CONST_AQM_SERVER ="tcp://amqbusmanager-amq-tcp:61616";
		public static final String _CONST_MONGODB_SERVER ="mongodb://admin:whKBcBNPFERy4XUp@mongodb:27017";
		public static final String _CONST_CONSUME_WEBSERVICE_POST ="http://podpersistencedb-route-fiduprevisoradev.cloudappsdesa.fiduprevisora.loc/rest/podDataInterfacePost/";
		*/

		
}
