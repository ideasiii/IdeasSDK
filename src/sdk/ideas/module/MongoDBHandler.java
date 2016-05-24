package sdk.ideas.module;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.QueryBuilder;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class MongoDBHandler
{

	private MongoClient mongoClient = null;
	private MongoDatabase mongoDatabase = null;
	private MongoCollection<Document> mongoCollection = null;
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public MongoDBHandler(String ip, int port) throws Exception
	{

		if (null == ip || port < 0)
		{
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
		}
		else
		{
			mongoClient = new MongoClient(ip, port);
		}

	}

	public MongoDBHandler() throws Exception
	{

		mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
	

	}

	public void setDataBase(String dataBaseName)
	{
		if (null != mongoClient)
		{
			mongoDatabase = null;
			mongoDatabase = mongoClient.getDatabase(dataBaseName);
		}
	}

	public void setCollection(String collectionName)
	{
		if (null != collectionName && null != mongoDatabase)
		{
			mongoCollection = null;
			mongoCollection = mongoDatabase.getCollection(collectionName);
		}
	}

	public ArrayList<String> queryDataByDeviceID(String deviceID, String startDate, String endDate)
			throws ParseException
	{
		if (null != mongoCollection)
		{
			if (isValidFormat(TIME_FORMAT, startDate) && isValidFormat(TIME_FORMAT, endDate))
			{

				ArrayList<String> data = new ArrayList<String>();
				QueryBuilder builder = QueryBuilder.start();

				builder.and("ID").regex(Pattern.compile(deviceID));
				builder.and("create_date").greaterThanEquals(startDate);
				builder.and("create_date").lessThanEquals(endDate);
				BasicDBObject query = (BasicDBObject) builder.get();

				MongoCursor<Document> cursor = mongoCollection.find(query).iterator();

				while (cursor.hasNext())
				{
					Document doc = cursor.next();
					System.out.println(doc.toJson());
					data.add(doc.toJson());
				}

				return data;
			}
		}
		else
		{
			System.out.println("mongoCollection is null");
		}
		return null;

	}

	public void update(String oid, HashMap<String, Object> updateMapData) throws Exception
	{

		Document queryCondition = new Document();
		queryCondition.put("_id", new ObjectId(oid));

		Document updatedValue = new Document();

		for (Map.Entry<String, Object> entry : updateMapData.entrySet())
		{
			updatedValue.put(entry.getKey(), entry.getValue());
		}

		Document updateSetValue = new Document("$set", updatedValue);

		mongoCollection.updateOne(queryCondition, updateSetValue);
	}

	public void insert(String collectionName, String jsonData) throws MongoWriteException
	{
		String backupDBName = "backup" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));// +String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
		System.out.println(backupDBName);
		insert(backupDBName, collectionName, jsonData);

	}

	public void insert(String dataBaseName, String collectionName, String jsonData) throws MongoWriteException
	{
		MongoCollection<Document> mongoCollection = mongoClient.getDatabase(dataBaseName).getCollection(collectionName);
		Document dbObject = Document.parse(jsonData);

		mongoCollection.insertOne(dbObject);

	}

	public void delete(String dataBaseName, String collectionName, String oid) throws Exception
	{
		Document queryCondition = new Document();
		queryCondition.put("_id", new ObjectId(oid));
		MongoCollection<Document> mongoCollection = mongoClient.getDatabase(dataBaseName).getCollection(collectionName);

		mongoCollection.deleteOne(queryCondition);
	}

	public void delete(String oid) throws Exception
	{
		Document queryCondition = new Document();
		queryCondition.put("_id", new ObjectId(oid));
		mongoCollection.deleteOne(queryCondition);
	}

	private static boolean isValidFormat(String format, String value) throws ParseException
	{
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		date = sdf.parse(value);
		if (!value.equals(sdf.format(date)))
		{
			date = null;
		}
		return date != null;
	}

}