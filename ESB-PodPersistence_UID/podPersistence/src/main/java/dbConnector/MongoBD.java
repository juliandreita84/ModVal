package dbConnector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BasicBSONObject;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

import core.ServiceBD;
import jdk.nashorn.internal.parser.JSONParser;

public class MongoBD   implements Runnable {

private static String COLLECTION_URL_BD =ParamDefinitions._CONST_MONGODB_SERVER;
private static MongoClient mongoClient;
private static DB database;
private static DBCollection collection;
public static BasicDBObject obj;
public static List<DBObject> obj_arr;
public static String coll="";
public static Map<String,Object>  info = new  HashMap<String,Object>();
public static int contador=0;
private Integer maxConnectionIdleTime=1000;
private Integer maxConnectionTimeout=1000;
private Integer minConnectionsPerHost=50;
private Integer maxConnectionsPerHost=1000;
private Boolean socketKeepAlive=true;
private static String file_name="";

public MongoBD()
{
}


public MongoBD(DB d, MongoClient c , BasicDBObject o, String collec)
{
	this.mongoClient=c;
	this.database=d;
	this.obj=o;
	this.coll=collec;
	
	this.file_name =  System.getenv("_CONST_ROUTE_LOG_DB") + "log_broker.bmi";
}

public MongoBD(DB d, MongoClient c , List<DBObject> o, String collec)
{
	this.mongoClient=c;
	this.database=d;
	this.obj_arr=o;
	this.coll=collec;
	
	this.file_name =  System.getenv("_CONST_ROUTE_LOG_DB") + "log_broker.bmi";
}



/**
 * =====================================================================================
 * createConnection: Permite establecer una conexion con una base de datos
 * =====================================================================================
 * @author andreagomez
 * @param dbname (El nombre de la base de datos)
 */
public boolean createConnection(String dbname)
{
	try {
		if (mongoClient!=null)
		{
		if (!mongoClient.getConnector().isOpen())
		{

			MongoClientOptions.Builder builder = MongoClientOptions.builder().maxConnectionIdleTime(maxConnectionIdleTime)
					 .connectTimeout(maxConnectionTimeout)
					 .autoConnectRetry( true)
					 .minConnectionsPerHost(minConnectionsPerHost).connectionsPerHost(maxConnectionsPerHost)
					 .socketKeepAlive(socketKeepAlive);
			
		 MongoClientURI mongoUri = new MongoClientURI(MongoBD.COLLECTION_URL_BD, builder);
		 mongoClient = new MongoClient(mongoUri);
		 
		 //mongoClient = new MongoClient(new MongoClientURI(MongoBD.COLLECTION_URL_BD));
		 database = mongoClient.getDB(dbname);
		 System.out.println("Abrio Conn");
		}
		}else
		{
			mongoClient = new MongoClient(new MongoClientURI(MongoBD.COLLECTION_URL_BD));
			 database = mongoClient.getDB(dbname);
			 System.out.println("Abrio Conn");
		}
		 return true;
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
}

/**
 * =====================================================================================
 * writeDocument: Permite escribir un Objeto en la BD de MongoDB
 * =====================================================================================
 * @author andreagomezr
 * @param colname (Nombre del Objeto)
 * @param o (Objeto a Insertar)
 * @return Retorna (true,false) si fue exitoso la escritura en el Objeto
 */
public boolean writeDocument(String dbname,String colname,BasicDBObject o)
{
	try {
		createConnection(dbname);
		setCollectionBD(colname);
		DBCollection collection = getCollection();
		collection.insert(o);
		//Cuando hay conexion escribe los mensajes almacenados en memoria
		writeMessageBackUp(dbname);
		return true;
	}catch (Exception e)
	{
		//En caso de fallo los guardo en memoria
		saveMessageBackUp(colname, o);
		return false;
	}
}


public boolean writeDocumentMany(String dbname,String colname,List<DBObject> o)
{
	try {
		createConnection(dbname);
		setCollectionBD(colname);
		DBCollection collection = getCollection();
		collection.insert(o);
		//Cuando hay conexion escribe los mensajes almacenados en memoria
		writeMessageBackUp(dbname);
		return true;
	}catch (Exception e)
	{
		//En caso de fallo los guardo en memoria
		//saveMessageBackUp(colname, o);
		return false;
	}
}

public Map<String,Object> StringToMap(String value)
{
	value = value.substring(1, value.length()-1);           //remove curly brackets
	String[] keyValuePairs = value.split(",",2);              //split the string to creat key-value pairs
	Map<String,Object> map = new HashMap<>();               

	for(String pair : keyValuePairs)                        //iterate over the pairs
	{
	    String[] entry = pair.split("=",2);                   //split the pairs to get key and value 
	    map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
	}	
	
	return map;
}

public void writeMessageBackUp(String dbname)
{
	
	if (!new File(file_name).exists())
	{
		return;
	}
	
	File file = new File(file_name); 
	  
	 
	  
	  String st; 
	  try {
		  BufferedReader br = new BufferedReader(new FileReader(file)); 
		while ((st = br.readLine()) != null) {
		    //System.out.println(st); 
		  @SuppressWarnings("unchecked")
		Map<String,Object> hw = StringToMap(st);
		  String  colname= hw.get("colname").toString();
		  //System.out.println(hw.get("message"));
			BasicDBObject o= (BasicDBObject) JSON.parse((String) hw.get("message"));
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ParamDefinitions._CONST_FORMAT_ID_DATA);
   		    String id = simpleDateFormat.format(new Date());
			//Object k = getNextSequence(ServiceBD.COLLECTION_TAG_LOG_SEQUENS, dbname, ServiceBD.COLLECTION_LOG_SEQUENS);
			//Object k = getNextSequence(ServiceBD.COLLECTION_TAG_LOG_SEQUENS, dbname, ServiceBD.COLLECTION_LOG_SEQUENS);
			o.put("_id", id);
			createConnection(dbname);
			setCollectionBD(colname);
			DBCollection collection = getCollection();
			collection.insert(o);
		  }
	} catch (Exception  e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} 

	  FileWriter fw;
	try {
		fw = new FileWriter(file_name,false);
		 fw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //the true will append the new data
     
	    
	  /*
	//System.out.println("Escribiendo log del Bk");
	for (int k=0; k<info.size(); k++)
	{try {
		@SuppressWarnings("unchecked")
		Map<String,Object> hw =(Map<String, Object>) info.get(k+"");
		String  colname= hw.get("colname").toString();
		BasicDBObject o= (BasicDBObject) hw.get("message");
		createConnection(dbname);
		setCollectionBD(colname);
		DBCollection collection = getCollection();
		collection.insert(o);
	}catch(Exception e)
		{
		e.getMessage();
		}
	}
	info.clear();
	contador=0;*/
	//System.out.println("Fin Escribiendo log del Bk");
}

public void saveMessageBackUp(String colname,BasicDBObject o)
{
	//System.out.println("Guardando en momoria log del Bk " + o);
	Map<String,Object>  info_s = new  HashMap<String,Object>();
	info_s.put("colname", "\"" + colname + "\"");
	info_s.put("message", o);
	//info.put(contador+"",info_s);
	//contador++;
	
	try
	{
	    FileWriter fw = new FileWriter(file_name,true); //the true will append the new data
	    fw.write(info_s + "\n");//appends the string to the file
	    fw.close();
	}
	catch(IOException ioe)
	{
	    System.err.println("IOException: " + ioe.getMessage());
	}
	
}



public DBCursor findCollection(String dbname,String colname,BasicDBObject o)
{
	DBCursor cursor1 = null;
	try {
		createConnection(dbname);
		setCollectionBD(colname);
		DBCollection collection = getCollection();
		DBCursor cursor=collection.find(o);
		return cursor;
	}catch (Exception e)
	{
		return cursor1;
	}
}



public boolean deleteDocument(String dbname,String colname,BasicDBObject o)
{
	try {
		createConnection(dbname);
		setCollectionBD(colname);
		DBCollection collection = getCollection();
		BasicDBObject document = new BasicDBObject();
		collection.findAndRemove(o);
		return true;
	}catch (Exception e)
	{
		return false;
	}
}


public void setCollectionBD(String collname)
{
	 collection = database.getCollection(collname);
}

public MongoClient getMongoClient() {
	return mongoClient;
}

public void setMongoClient(MongoClient mongoClient) {
	this.mongoClient = mongoClient;
}

public DB getDatabase() {
	return database;
}

public void setDatabase(DB database) {
	this.database = database;
}

public DBCollection getCollection() {
	return collection;
}

public void setCollection(DBCollection collection) {
	this.collection = collection;
}

public Object getNextSequence(String name,String dbname,String colname) throws Exception{
    MongoClient mongoClient = new MongoClient(new MongoClientURI(MongoBD.COLLECTION_URL_BD));
    // Now connect to your databases
    DB db = mongoClient.getDB(dbname);
    DBCollection collection = db.getCollection(colname);
    BasicDBObject find = new BasicDBObject();
    find.put("_id", name);
    BasicDBObject update = new BasicDBObject();
    update.put("$inc", new BasicDBObject("seq", 1));
    DBObject obj =  collection.findAndModify(find, update);
    return obj.get("seq");
}


@Override
public void run() {
	// TODO Auto-generated method stub
	writeDocument("", coll, obj);
}


public static void main(String ars[])
{
	
	/*String value = "{colname=\"log_bus_integrations\", message={ \"app_origen\" : \"1\" , \"resultado\" : \"00\" , \"id_mensaje\" : \"10\" , \"id_integracion\" : \"1\" , \"app_destino\" : \"2\" , \"texto_log\" : \"<soapenv:Envelope xmlns:soapenv=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\" xmlns:typ=\\\"http://localhost:8088/WsFXF.wsdl/types/\\\">\\r<soapenv:Header/>\\r<soapenv:Body>\\r<typ:prcFdValidarSaldoElement>\\r<moviCtcoNegocio>hoy</moviCtcoNegocio>\\r<moviCtcoCiasNego>hoy</moviCtcoCiasNego>\\r<moviEncargo>55555</moviEncargo>\\r</typ:prcFdValidarSaldoElement>\\r</soapenv:Body>\\r</soapenv:Envelope>\" , \"_id\" :  null  , \"origen\" : \"CONSUME\" , \"fecha_log\" : { \"$date\" : \"2019-06-10T16:17:51.732Z\"} , \"response_detail\" : \"\" , \"response_save_log\" : \"_00_MESSAGE_OK\"}}";
	value = value.substring(1, value.length()-1);           //remove curly brackets
	String[] keyValuePairs = value.split(",",2);              //split the string to creat key-value pairs
	Map<String,String> map = new HashMap<>();               

	for(String pair : keyValuePairs)                        //iterate over the pairs
	{
	    String[] entry = pair.split("=");                   //split the pairs to get key and value 
	    map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
	}	
	
System.out.println( map);*/

	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YMdHmsS");

	 String date = simpleDateFormat.format(new Date());
 System.out.println(date);
}
	
}
