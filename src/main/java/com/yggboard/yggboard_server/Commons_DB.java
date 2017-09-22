package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Commons_DB {
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response obterCrud(String collectionName, Object arrayQueryInput, MongoClient mongo, Boolean close) {
		Commons commons = new Commons();
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
		Document  searchQuery = new Document ();
		List arraySetQuery = (List) arrayQueryInput;
		Boolean login = false;
		for (int i = 0; i < arraySetQuery.size(); i++) {
			JSONObject setQuery = new JSONObject();
			setQuery.putAll((Map) arraySetQuery.get(i));
			if (setQuery.get("key").equals("_id")){
				if (setQuery.get("value") instanceof Object){
					ObjectId id = new ObjectId(setQuery.get("value").toString());
					searchQuery.put((String) setQuery.get("key"), id);
				}else {
					searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));	
				};
			}else{
				searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
			};
			if (setQuery.get("tipo") != null){
				if (setQuery.get("tipo").equals("login")){
					login = true; 
				};
			};
		};
		FindIterable<Document> cursor = collection.find(searchQuery);
		
		if (cursor.first() != null) {
			if (login){
				BasicDBObject doc = new BasicDBObject();
				doc.putAll((Map) cursor.first());
				mongo.close();
				return Response.status(200).entity(doc).build();
			}else{
				BasicDBObject doc = new BasicDBObject();
				doc.putAll((Map) cursor.first().get("documento"));
//				doc.remove("password");
//				doc.remove("token");
				BasicDBObject docReturn = new BasicDBObject();
				docReturn.put("documento", doc);
				docReturn.put("_id", cursor.first().get("_id"));
				mongo.close();
				return Response.status(200).entity(docReturn).build();
			}
		};
		mongo.close();
		return Response.status(400).entity(null).build();
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Response incluirCrud(String collectionName, Object insertInput, MongoClient mongo, Boolean close) {
		Index index = new Index();
		Commons commons = new Commons();
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
		Document insert = new Document();
		insert.putAll((Map) insertInput);
		collection.insertOne(insert);
		insert.put("_id", insert.get( "_id" ).toString());
		mongo.close();
		
		index.gravaIndex(collectionName, insertInput, insert.get( "_id" ).toString(), mongo);
		return Response.status(200).entity(insert).build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Response atualizarCrud(String collectionName, Object updateInput, Object keysInput, BasicDBObject documento, MongoClient mongo, Boolean close) {
		Commons commons = new Commons();
		@SuppressWarnings("resource")
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
		BasicDBObject searchQuery = new BasicDBObject();
		List arraySetQuery = (List) keysInput;
		ArrayList<BasicDBObject> keysObter = new ArrayList<BasicDBObject>();
		for (int i = 0; i < arraySetQuery.size(); i++) {
			JSONObject setQuery = new JSONObject();
			setQuery.putAll((Map) arraySetQuery.get(i));
			if (setQuery.get("key").toString().equals("_id")){
				if (setQuery.get("value") instanceof Object){
					ObjectId id = new ObjectId(setQuery.get("value").toString());
					searchQuery.put((String) setQuery.get("key"), id);
				}else {
					searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));	
				};
			}else{
				searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
			};
			BasicDBObject setQueryKeyObter = new BasicDBObject();
			setQueryKeyObter.put("key", setQuery.get("key"));
			setQueryKeyObter.put("value", setQuery.get("value"));
			setQueryKeyObter.put("tipo", setQuery.get("login"));
			keysObter.add(setQueryKeyObter);
		};
		
		BasicDBObject objDocumento = new BasicDBObject();
		if (documento == null) {
			Response response = obterCrud(collectionName, keysObter, mongo, false);
			if ((response.getStatus() == 200)){
				BasicDBObject cursor = new BasicDBObject();
				cursor.putAll((Map) response.getEntity());
				if (cursor != null){
					objDocumento.putAll((Map) cursor.get("documento"));
				};
			};
		}else {
			objDocumento.putAll((Map) documento.get("documento"));
		};
		
		if (objDocumento != null) {
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
			Document objDocumentoUpdate = new Document();
			objDocumentoUpdate.put("documento", objDocumento);
			collection.replaceOne(searchQuery,objDocumentoUpdate);
			mongo.close();
			return Response.status(200).entity(objDocumento).build();
		};
		mongo.close();
		return Response.status(200).entity(objDocumento).build();
	};

	public Response removerAllCrud(String collectionName, MongoClient mongo, Boolean close) {
		Commons commons = new Commons();
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
		collection.deleteMany(new Document());
		mongo.close();
		return Response.status(200).build();
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response removerCrud(String collectionName, Object keysInput, MongoClient mongo, Boolean close) {
		Commons commons = new Commons();
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
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
		collection.deleteOne(searchQuery);
		mongo.close();
		return Response.status(200).build();
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response removerCrudMany(String collectionName, Object keysInput, MongoClient mongo, Boolean close) {
		Commons commons = new Commons();
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
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
		collection.deleteMany(searchQuery);
		mongo.close();
		return Response.status(200).build();
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response listaCrud(String collectionName, Object arrayQueryInput, MongoClient mongo, Boolean close) {
		Commons commons = new Commons();
		MongoDatabase db = mongo.getDatabase(commons.getProperties().get("database").toString());
//		boolean auth = db.authenticate("username", "password".toCharArray());
		MongoCollection<Document> collection = db.getCollection(collectionName);
		BasicDBObject searchQuery = new BasicDBObject();
		List arraySetQuery = (List) arrayQueryInput;
		for (int i = 0; i < arraySetQuery.size(); i++) {
			JSONObject setQuery = new JSONObject();
			setQuery.putAll((Map) arraySetQuery.get(i));
			searchQuery.put((String) setQuery.get("key"), (String) setQuery.get("value"));
		};
		FindIterable<Document> cursor = collection.find(searchQuery);
		JSONArray documentos = new JSONArray();
		System.out.println("chamou lista:" + collectionName);
		if (cursor.first() != null) {
			for (Document current : cursor) {
				BasicDBObject doc = new BasicDBObject();
				doc.putAll((Map) current.get("documento"));
				doc.put("_id", current.get("_id").toString());
				doc.remove("password");
				doc.remove("token");
	//			BasicDBObject docReturn = new BasicDBObject();
	//			docReturn.put("documento", doc);
	//			doc.put("_id", current.get("_id").toString());
				documentos.add(doc);
		    };
		};
	    mongo.close();
		return Response.status(200).entity(documentos).build();
	};

	@SuppressWarnings({ "unchecked" })
	public BasicDBObject getCollection(String value, String collectionName, String keyInput, MongoClient mongo, Boolean close) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", keyInput);
		key.put("value", value);
		keysArray.add(key);

		Response response = obterCrud(collectionName, keysArray, mongo, close);
		if ((response.getStatus() == 200)){
			return (BasicDBObject) response.getEntity();
		};
		return null;
	};

	@SuppressWarnings({ "unchecked" })
	public JSONArray getCollectionLista(String value, String collectionName, String keyInput, MongoClient mongo, Boolean close) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", keyInput);
		key.put("value", value);
		keysArray.add(key);
		
		Response response = listaCrud(collectionName, keysArray, mongo, close);
		if ((response.getStatus() == 200)){
			return (JSONArray) response.getEntity();
		};
		return null;

	};

	@SuppressWarnings({ })
	public JSONArray getCollectionListaNoKey(String collectionName, MongoClient mongo, Boolean close) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();

		Response response = listaCrud(collectionName, keysArray, mongo, close);
		if ((response.getStatus() == 200)){
			return (JSONArray) response.getEntity();
		};
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public Response atualizaDocumento(BasicDBObject objUpdate, String collection, String keyInput, String id, MongoClient mongo, Boolean close) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", keyInput);
		key.put("value", id);
		keysArray.add(key);

		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento");
		field.put("value", objUpdate.get("documento"));
		fieldsArray.add(field);

		Response atualizacao = atualizarCrud(collection, fieldsArray, keysArray, objUpdate, mongo, close);
		
		return atualizacao;
	};
};
