package dbConnector;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoBD {

private static String COLLECTION_URL_BD ="mongodb://localhost:27017";
private MongoClient mongoClient;
private DB database;
private DBCollection collection;


/**
 * =====================================================================================
 * createConnection: Permite establecer una conexion con una base de datos
 * =====================================================================================
 * @author Entelgy
 * @param dbname (El nombre de la base de datos)
 */
public void createConnection(String dbname)
{
	try {
		 mongoClient = new MongoClient(new MongoClientURI(MongoBD.COLLECTION_URL_BD));
		 database = mongoClient.getDB(dbname);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

/**
 * =====================================================================================
 * writeDocument: Permite escribir un Objeto en la BD de MongoDB
 * =====================================================================================
 * @author Entelgy
 * @param colname (Nombre del Objeto)
 * @param o (Objeto a Insertar)
 * @return Retorna (true,false) si fue exitoso la escritura en el Objeto
 */
public boolean writeDocument(String colname,BasicDBObject o)
{
	try {
		setCollectionBD(colname);
		DBCollection collection = getCollection();
		collection.insert(o);
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

	
}
