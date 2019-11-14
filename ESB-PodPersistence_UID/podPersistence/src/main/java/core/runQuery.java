package core;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.techprimers.messaging.standaloneactivemqexample.ParamDefinitions;

public class runQuery {

	
	public String loadListCollection(String collection)
	{
		DBCursor cursor=getListObjets(collection);
		StringBuilder out_data= new StringBuilder();
		while (cursor.hasNext())
		{
			 //System.out.println("nombre_aplicacion:" + cursor.next().get("nombre_aplicacion"));
			out_data.append(cursor.next());
			out_data.append(",");
		}
		
		return out_data.toString();
	}
	
	public String loadCustomCollection(String collection, BasicDBObject params, String type_query)
	{
		ServiceBD s = new ServiceBD();
		Map<String,String> key = new HashMap<String, String>();
		StringBuilder out_data= new StringBuilder();
		
		if (type_query.equals(ParamDefinitions._CONST_TYPE_05_INSERT_DOC))
		{
			//BasicDBObject o = params;
			Object result =  s.m.writeDocument(s.bdname,collection, params);
			return "{\"response\":\"" + result  + "\"}";
		}
		
		if (type_query.equals(ParamDefinitions._CONST_TYPE_06_DELETE_DOC))
		{
			//BasicDBObject o = params;
			//System.out.println(o);
			Object result =  s.m.deleteDocument(s.bdname,collection, params);
			return "{\"response\":\"" + result  + "\"}";
		}
		
		DBCursor cursor=s.m.findCollection(s.bdname,collection, params);
		while (cursor.hasNext())
		{
			 //System.out.println("nombre_aplicacion:" + cursor.next().get("nombre_aplicacion"));
			out_data.append(cursor.next());
			out_data.append(",");
		}
		return out_data.toString();
	}
	
	public String loadCustomCollection(String collection, String params,String type_query)
	{
		ServiceBD s = new ServiceBD();
		Map<String,String> key = new HashMap<String, String>();
		String[] data = params.split("@");
		StringBuilder out_data= new StringBuilder();
		
		if (type_query.equals(ParamDefinitions._CONST_TYPE_04_NEXT_SEQ))
		{
			Object seq =  s.getNextSeq(data[0], s.bdname, data[1]);
			return "{\"seq\":\"" +seq  + "\"}";
		}
		
		
		for (int i= 0; i< data.length; i++)
		{
			key.put(i+"", data[i]);
			//System.out.println("i=" +data[i] );
		}
		
		DBCursor cursor=s.getCustomQuery(collection,key,type_query.toString());
		
		while (cursor.hasNext())
		{
			 //System.out.println("nombre_aplicacion:" + cursor.next().get("nombre_aplicacion"));
			out_data.append(cursor.next());
			out_data.append(",");
		}
		
		return out_data.toString();
	}
	
	public DBCursor getListObjets(String collection)
	{
		ServiceBD s = new ServiceBD();
		return s.getCollection(collection);
	}	
	
}
