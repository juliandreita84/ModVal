package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;
import dbConnector.MongoBD;

public class ServiceBD {

	
public static String COLLECTION_LOG_BUS_INTEGRATIONS ="log_bus_integrations";
public static String COLLECTION_ROUTE_MESSAGE ="routes_message";
public static String COLLECTION_LOG_SEQUENS ="secuencia_log";
public static String COLLECTION_INTEGRATIONS ="integrations";
public static String COLLECTION_RULES_MESSAGES ="rules_messages";
public static String COLLECTION_TAG_LOG_SEQUENS ="log_bus";

private static String COLLECTION_BD_NAME =System.getenv("_CONST_DATABASE_NAME");//"busManagerBD";	
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

@SuppressWarnings("unlikely-arg-type")
public DBCursor getCustomQuery(String name_collection,  Map<String,String> param,String type_query)
{
	DBCursor cursor = null;
	
	m.createConnection(this.bdname);
	m.setCollectionBD(name_collection);
	DBCollection collection = m.getCollection();
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	BasicDBObject whereQuery = new BasicDBObject();
	BasicDBObject orderBy = new BasicDBObject();
	boolean flag_fd=false;
	boolean others=false;
	orderBy.put("_id", 1);
	for (int i =0; i < param.size(); i++)
	{
		others=true;	

			String [] conten =param.get(i+"").toString().split(";");
			if (conten[1].equals("or"))
			{
				others=false;
			    List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			    obj.add(new BasicDBObject(conten[2], conten[3]));
			    obj.add(new BasicDBObject(conten[5], conten[6]));
				
				whereQuery.put("$"+conten[1], obj);
			}
			
			if (conten[0].equals("sort"))
			{
				others=false;
				orderBy.append(conten[1], Integer.parseInt(conten[2]));
			}
			
			if (conten[1].length()==0 ||conten[1].equals("gt"))
			{
				whereQuery.put(conten[0], conten[2]);
			}
			else
			{
				try {
					String [] inf = conten[2].replaceAll("_"," ").split("btw");
	
					if (inf.length>1)
					{
						whereQuery.put(conten[0], 
								new BasicDBObject("$gte", format.parse(inf[0])).append("$lte", format.parse(inf[1])));
					}
					else
					{
						if (conten[1].equals("fd")) {
							if (flag_fd==false)
							{
								whereQuery.put(conten[0], new BasicDBObject("$regex", conten[3] +".*"+conten[2]+".*").append("$options", "i"));
							}
							else
							{
								BasicDBObject data_q=(BasicDBObject) whereQuery.get(conten[0]);
								String dr=data_q.getString("$regex");
								whereQuery.put(conten[0], new BasicDBObject("$regex", dr + conten[3] +".*"+conten[2]+".*").append("$options", "i"));
							}
							flag_fd=true;
							
						}else {
						if (others)
						{
						whereQuery.put(conten[0], new BasicDBObject("$"+conten[1], conten[2]));
						}
						//whereQuery.put(conten[0], new BasicDBObject("$"+conten[1], Integer.parseInt(conten[2])));
						}
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}
	//System.out.println("consulta " + whereQuery);
	cursor = collection.find(whereQuery).sort(orderBy);
	
	//System.out.println("cursor " +cursor.count());
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
		//System.out.println("nombre_aplicacion:" + cursor.next());
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
	return m.writeDocument(this.bdname,ServiceBD.COLLECTION_LOG_BUS_INTEGRATIONS, o);
}

public static void main(String args[])
{
	/*ServiceBD s = new ServiceBD();
	Map<String,String> key = new HashMap<String, String>();
	key.put("0", "match:fecha_log;btw;2019-05-01btw2019-05-31*id_integracion;;1");
	key.put("1", "project:fecha;d;fecha_log*resultado;;resultado");
	key.put("2", "sort:fecha;;-1");
	key.put("3", "group:_id;;fecha*sum;sum;1");
	s.getCustomQuery("log_bus_integrations", key, "03");*/
	
	String l="".replaceAll("=", ":");
	BasicDBObject param_query=(BasicDBObject) JSON.parse(l);
	System.out.println(param_query);
	

}




}
