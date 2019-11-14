package com.techprimers.messaging.standaloneactivemqexample;

public class ParamDefinitions {
//PARAMS FOR RESPONSE TYPES
	public static String _ALGORITHM_TYPE ="AES";
	public static String _NO_MAC ="NO_GENERATE";
	public static String _01_ERROR_TIPO_DATO ="_01_ERROR_TIPO_DATO";
	public static String _00_MESSAGE_OK ="_01_ERROR_TIPO_DATO";
	public static String _02_WITHOUT_DATA ="_02_WITHOUT_DATA";

//PARAMS FOR CONSTANS VALUES
	public static String _CONST_RESPONSE ="response";
	public static String _CONST_RESPONSE_DETAIL ="response_detail";
	public static String _CONST_FORMAT_XML ="XML";
	public static String _CONST_FORMAT_STRING ="STRING";
	public static String _CONST_FORMAT_NUMBER ="NUMBER";
	public static String _CONST_ORIGEN_DATA ="CONSUME";
	public static String _CONST_TYPE_OPERATION_QUERY ="00";
	public static String _CONST_TYPE_OPERATION_CREATE ="01";
	
	public static String COLLECTION_KEYS_APP ="keys_applications";
	public static String COLLECTION_OPERATION_QUERY ="00";
	public static String COLLECTION_OPERATION_INSERT ="05";
	public static String COLLECTION_OPERATION_DELETE ="06";
	
	
	//PARAMS FOR CONSTANS CREDENTIALS 
	//Desarrollo
		/*public static final String _CONST_AQM_SERVER ="tcp://localhost:61616";
		public static final String _CONST_MONGODB_SERVER ="mongodb://localhost:27017";
		public static final String _CONST_CONSUME_WEBSERVICE_POST ="http://localhost:8080/rest/podDataInterfacePost/";
*/
	//OPenshift
		public static final String _CONST_AQM_SERVER ="tcp://amqbusmanager-amq-tcp:61616";
		public static final String _CONST_MONGODB_SERVER ="mongodb://admin:whKBcBNPFERy4XUp@mongodb:27017";
		public static final String _CONST_CONSUME_WEBSERVICE_POST ="http://podpersistencedb-route-fiduprevisoradev.cloudappsdesa.fiduprevisora.loc/rest/podDataInterfacePost/";
		

		
}
