package com.techprimers.messaging.standaloneactivemqexample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParamDefinitions {
//PARAMS FOR RESPONSE TYPES
	public static String _00_MESSAGE_OK ="_00_MESSAGE_OK";
	public static String _01_WITHOUT_AUTOINCREMENT ="_01_WITHOUT_AUTOINCREMENT";
	public static String _02_WITHOUT_DATA ="_02_WITHOUT_DATA";

//PARAMS FOR CONSTANS VALUES
	public static String _CONST_RESPONSE ="response_save_log";
	public static String _CONST_ORIGEN_DATA ="CONSUME";
	public static String _CONST_FORMAT_ID_DATA ="YMMddHHmmssS";
	
	
//PARAMS FOR CONSTANS CREDENTIALS
	public static final String _CONST_MONGODB_SERVER ="mongodb://localhost:27017";
	public final static String _CONST_AQM_SERVER ="tcp://localhost:61616";
	public static String _CONST_CONSUME_WEBSERVICE_POST="http://localhost:8080/rest/podDataInterfacePost/";
	public static String _CONST_AQM_SERVER_USER="admin";
	public static String _CONST_AQM_SERVER_PASS="admin";
	public static String _CONST_AQM_QUEUE_IN="BusService_In";
	public static String _CONST_AQM_QUEUE_OUT="BusService_OUT";
	public static String _CONST_AQM_QUEUE_DB="BusPersistence";
	
	public static String _CONST_UMBRAL="10";
	public static String _CONST_URL_ALERTAS=System.getenv("_CONST_URL_ALERTAS");
	
	
	
//PARAMS FOR TYPES QUERYS	
	public static final String _CONST_TYPE_01_LOAD_FULL_COLLECTION ="01";
	public static final String _CONST_TYPE_02_CUSTOM_FILTER ="02";
	public static final String _CONST_TYPE_03_CUSTOM_PROJECTION ="03";
	public static final String _CONST_TYPE_04_NEXT_SEQ ="04";
	public static final String _CONST_TYPE_05_INSERT_DOC ="05";
	public static final String _CONST_TYPE_06_DELETE_DOC ="06";
	
		
}
