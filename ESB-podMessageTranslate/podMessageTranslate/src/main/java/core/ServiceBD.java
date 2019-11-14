package core;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import dbConnector.MongoBD;

public class ServiceBD {

	
public static String COLLECTION_LOG_BUS_INTEGRATIONS ="log_bus_integrations";
public static String COLLECTION_ROUTE_MESSAGE ="routes_message";
public static String COLLECTION_LOG_SEQUENS ="secuencia_log";
public static String COLLECTION_INTEGRATIONS ="integrations";
public static String COLLECTION_RULES_MESSAGES ="rules_messages";
public static String COLLECTION_TAG_LOG_SEQUENS ="log_bus";

private static String COLLECTION_BD_NAME ="busManagerBD";	
public MongoBD m = new MongoBD();
public String bdname ="";
private BasicDBObject msgBson;

public ServiceBD()
{
	this.bdname=ServiceBD.COLLECTION_BD_NAME;
}
	
public Object getNextSeq(String name,String dbname,String colname)
{
	try {
		return m.getNextSequence(name,  dbname, colname);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}


public DBCursor getIntegrations()
{
	DBCursor cursor = null;
	
	m.createConnection(this.bdname);
	m.setCollectionBD(this.COLLECTION_INTEGRATIONS);
	DBCollection collection = m.getCollection();
/*	BasicDBObject whereQuery = new BasicDBObject();
	whereQuery.put("id_integracion", id_integracion);*/
	cursor = collection.find();
	return cursor;
}

public DBCursor getCollection(String name_collection)
{
	DBCursor cursor = null;
	
	m.createConnection(this.bdname);
	m.setCollectionBD(name_collection);
	DBCollection collection = m.getCollection();
	//BasicDBObject whereQuery = new BasicDBObject();
	//whereQuery.put("id_integracion", id_integracion);
	cursor = collection.find();
	
	return cursor;
}

public DBCursor getRoutesMessages(String id_integracion)
{
	DBCursor cursor = null;
	
	m.createConnection(this.bdname);
	m.setCollectionBD(this.COLLECTION_ROUTE_MESSAGE);
	DBCollection collection = m.getCollection();
	BasicDBObject whereQuery = new BasicDBObject();
	whereQuery.put("id_integracion", id_integracion);
	cursor = collection.find(whereQuery);
	
	return cursor;
}

public DBCursor getRulesByIntegration(String id_integracion)
{
	DBCursor cursor = null;
	
	m.createConnection(this.bdname);
	m.setCollectionBD(this.COLLECTION_RULES_MESSAGES);
	DBCollection collection = m.getCollection();
	BasicDBObject whereQuery = new BasicDBObject();
	whereQuery.put("id_integracion", id_integracion);
	cursor = collection.find(whereQuery);
	
	return cursor;
}

public void testBD()
{
	m.createConnection(this.bdname);
	m.setCollectionBD("log_bus_integrations");
	DBCollection collection = m.getCollection();
	DBCursor cursor = collection.find();
	while (cursor.hasNext())
	{
		 //System.out.println("nombre_aplicacion:" + cursor.next().get("nombre_aplicacion"));
		System.out.println("nombre_aplicacion:" + cursor.next());
	}
}


/**
 * =====================================================================================
 * writeLog: Permite escribir en el log de la BD de Mongo del BUS
 * =====================================================================================
 * @author andreagomez
 * @param o (Objeto que contiene la informacion del documento par escribir en el Log) 
 * @return
 */
public boolean writeLog(BasicDBObject o)
{
	return m.writeDocument(ServiceBD.COLLECTION_LOG_BUS_INTEGRATIONS, o);
}

public static void main(String args[])
{
	ServiceBD s = new ServiceBD();
	s.testBD();
	BasicDBObject o = new BasicDBObject();
	System.out.println("next sq " + s.getNextSeq("log_bus",ServiceBD.COLLECTION_BD_NAME , "secuencia_log") );

	//System.out.println("objeto " + o);
	//System.out.println(s.writeLog(o));
}

public BasicDBObject getMsgBson() {
	return msgBson;
}

public void setMsgBson(String key, String value) {
	this.msgBson.put(key, value);
}
	
}
