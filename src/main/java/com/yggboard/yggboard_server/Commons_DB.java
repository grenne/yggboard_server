package com.yggboard.yggboard_server;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
				searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
			};
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null) {
				mongo.close();
				return Response.status(200).entity(cursor).build();
			}else{
				mongo.close();
				return Response.status(404).entity(null).build();				
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
				searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
			};
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objDocumento = new BasicDBObject();
				objDocumento = (BasicDBObject) cursor.get("documento");
				mongo = new Mongo();
				List arrayUpdate = (List) updateInput;
				for (int i = 0; i < arrayUpdate.size(); i++) {
					BasicDBObject setUpdate = new BasicDBObject();
					BasicDBObject docUpdate = new BasicDBObject();
					setUpdate.putAll((Map) arrayUpdate.get(i));
					docUpdate.putAll((Map) setUpdate.get("value"));
					if (setUpdate.get("field").toString().equals("documento")){
						objDocumento.clear();
						objDocumento.putAll((Map) docUpdate);
					}else{
						objDocumento.remove(setUpdate.get("field"));
						objDocumento.put((String) setUpdate.get("field"), docUpdate);
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
				return Response.status(200).entity(cursor).build();
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
	}

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
					documentos.add(jsonDocumento);
				};
				return Response.status(200).entity(documentos).build();
			}else{
				mongo.close();
				return Response.status(404).entity(null).build();				
			}
		} catch (UnknownHostException | MongoException e) {
			return Response.status(406).entity(e).build();
		}
	};
};