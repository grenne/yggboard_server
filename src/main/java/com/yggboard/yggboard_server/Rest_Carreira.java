package com.yggboard.yggboard_server;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

	
@Singleton
// @Lock(LockType.READ)
@Path("/carreiras")

public class Rest_Carreira {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCarreira(@QueryParam("carreira") String id) throws UnknownHostException, MongoException {
		Commons_DB commons_db = new Commons_DB();

		Response response = commons_db.getCollection(id, "objetivos", "documento.id");
		
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject cursor = new BasicDBObject();
			cursor.putAll((Map) response.getEntity());
			if (cursor != null){
				JSONObject documento = new JSONObject();
				BasicDBObject obj = (BasicDBObject) cursor.get("documento");
				documento.put("documento", obj);
				return documento;
			}
		};
		return null;
	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCarreiras() {
		Commons_DB commons_db = new Commons_DB();
		Response response = commons_db.getCollectionLista("", "objetivos", "");
		
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject cursor = new BasicDBObject();
			cursor.putAll((Map) response.getEntity());
			if (cursor != null){
				JSONArray documentos = new JSONArray();
				while (((Iterator<DBObject>) cursor).hasNext()) {
					BasicDBObject objCarreiras = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
					JSONObject jsonDocumento = new JSONObject();
					jsonDocumento.put("_id", objCarreiras.getString("_id"));
					BasicDBObject setUpdate = new BasicDBObject();
					setUpdate.putAll((Map) objCarreiras.get("documento"));
					jsonDocumento.put("documento", setUpdate);
					documentos.add(jsonDocumento);
				};
				return documentos;
			}
		};
		return null;
	};
};
