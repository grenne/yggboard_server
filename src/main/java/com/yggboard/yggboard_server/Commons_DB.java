package com.yggboard.yggboard_server;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Commons_DB {


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response obterCrud(String collectionName, Object arrayQueryInput) {

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection(collectionName);
			BasicDBObject searchQuery = new BasicDBObject();
			List arraySetQuery = (List) arrayQueryInput;
			for (int i = 0; i < arraySetQuery.size(); i++) {
				JSONObject setQuery = new JSONObject();
				setQuery.putAll((Map) arraySetQuery.get(i));
				if (setQuery.get("key").equals("_id")){
					ObjectId id = new ObjectId(setQuery.get("value").toString());
					searchQuery.put((String) setQuery.get("key"), id);
				}else{
					searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
				};
			};
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null) {
				mongo.close();
				return Response.status(200).entity(cursor).build();
			}else{
				mongo.close();
				return Response.status(200).entity(false).build();
			}
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	};

	@SuppressWarnings("rawtypes")
	public Response incluirCrud(String collectionName, Object insertInput) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection(collectionName);
			BasicDBObject insert = new BasicDBObject();
			insert.putAll((Map) insertInput);
			collection.insert(insert);
			insert.put("_id", insert.get( "_id" ).toString());
			mongo.close();
			return Response.status(200).entity(insert).build();
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Response atualizarCrud(String collectionName, Object updateInput, Object keysInput) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection(collectionName);
			BasicDBObject searchQuery = new BasicDBObject();
			List arraySetQuery = (List) keysInput;
			for (int i = 0; i < arraySetQuery.size(); i++) {
				JSONObject setQuery = new JSONObject();
				setQuery.putAll((Map) arraySetQuery.get(i));
				if (setQuery.get("value") instanceof Object){
					searchQuery.put((String) setQuery.get("key"), setQuery.get("value"));
				}else{
					searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
				};
			};
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objDocumento = new BasicDBObject();
				objDocumento = (BasicDBObject) cursor.get("documento");
				mongo = new Mongo();
				List arrayUpdate = (List) updateInput;
				for (int i = 0; i < arrayUpdate.size(); i++) {
					BasicDBObject setUpdate = new BasicDBObject();
					setUpdate.putAll((Map) arrayUpdate.get(i));
					Object value = setUpdate.get("value");
					if (value instanceof String){
						String docUpdate = setUpdate.get("value").toString();
						objDocumento.remove(setUpdate.get("field"));
						objDocumento.put((String) setUpdate.get("field"), docUpdate);
					}else{
						if (value instanceof ArrayList){
							ArrayList docUpdate = (ArrayList) setUpdate.get("value");
							objDocumento.remove(setUpdate.get("field"));
							JSONArray arrayField = new JSONArray();
							for (int j = 0; j < docUpdate.size(); j++) {
								if (docUpdate.get(j) instanceof String){
									arrayField.add(docUpdate.get(j));									
								}else{
									BasicDBObject docUpdateItem = new BasicDBObject();
									docUpdateItem.putAll((Map) docUpdate.get(j));
									arrayField.add(docUpdateItem);
								};
							};
							objDocumento.put((String) setUpdate.get("field"), arrayField);
						}else{
							BasicDBObject docUpdate = new BasicDBObject();
							docUpdate.putAll((Map) setUpdate.get("value"));
							if (setUpdate.get("field").equals("documento")){
								objDocumento.clear();
								objDocumento.putAll((Map) docUpdate);
							}else{
								objDocumento.remove(setUpdate.get("field"));
								objDocumento.put((String) setUpdate.get("field"), docUpdate);
							};
						};
					};
				};
				BasicDBObject objDocumentoUpdate = new BasicDBObject();
				objDocumentoUpdate.put("documento", objDocumento);
				BasicDBObject update = new BasicDBObject("$set", new BasicDBObject(objDocumentoUpdate));
				cursor = collection.findAndModify(searchQuery,
		                null,
		                null,
		                false,
		                update,
		                true,
		                false);
				mongo.close();
				return Response.status(200).entity(cursor.get("documento")).build();
			}else{
				mongo.close();
				return Response.status(400).entity(keysInput).build();				
			}
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	}

	public Response removerAllCrud(String collectionName) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection(collectionName);
			collection.remove(new BasicDBObject());
			mongo.close();
			return Response.status(200).build();
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	};

	@SuppressWarnings("unchecked")
	public Response removerCrud(String collectionName, Object keysInput) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection(collectionName);
			BasicDBObject searchQuery = new BasicDBObject();
			List arraySetQuery = (List) keysInput;
			for (int i = 0; i < arraySetQuery.size(); i++) {
				JSONObject setQuery = new JSONObject();
				setQuery.putAll((Map) arraySetQuery.get(i));
				if (setQuery.get("value") instanceof Object){
					searchQuery.put((String) setQuery.get("key"), setQuery.get("value"));
				}else{
					searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
				};
			};
			collection.remove(new BasicDBObject(searchQuery));
			mongo.close();
			return Response.status(200).build();
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response listaCrud(String collectionName, Object arrayQueryInput) {

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection(collectionName);
			BasicDBObject searchQuery = new BasicDBObject();
			List arraySetQuery = (List) arrayQueryInput;
			for (int i = 0; i < arraySetQuery.size(); i++) {
				JSONObject setQuery = new JSONObject();
				setQuery.putAll((Map) arraySetQuery.get(i));
				searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
			};
			DBCursor cursor = collection.find(searchQuery);
			if (cursor != null) {
				JSONArray documentos = new JSONArray();
				while (((Iterator<DBObject>) cursor).hasNext()) {
					BasicDBObject objDocumento = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
					JSONObject jsonDocumento = new JSONObject();
					jsonDocumento.putAll((Map) objDocumento.get("documento"));
					jsonDocumento.put("_id", objDocumento.getString("_id"));
					documentos.add(jsonDocumento);
				};
				mongo.close();
				return Response.status(200).entity(documentos).build();
			}else{
				mongo.close();
				return Response.status(404).entity(null).build();				
			}
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	};

	@SuppressWarnings("unchecked")
	public Response getCollection(String value, String collectionName, String keyInput) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", keyInput);
		key.put("value", value);
		keysArray.add(key);
		
		return obterCrud(collectionName, keysArray);
	};

	@SuppressWarnings("unchecked")
	public Response getCollectionLista(String value, String collectionName, String keyInput) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", keyInput);
		key.put("value", value);
		keysArray.add(key);
		
		return listaCrud(collectionName, keysArray);
	}
	
};
