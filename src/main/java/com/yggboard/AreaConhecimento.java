package com.yggboard;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class AreaConhecimento {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollection(id, "areaConhecimento", "_id", mongo, false);
		
		return result;
	
	};
	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		JSONArray result = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		
		return result;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getIdNome(String usuarioParametro, MongoClient mongo) {
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < resultAll.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			BasicDBObject obj = new BasicDBObject();
			obj.putAll((Map) resultAll.get(i));
			item.put("_id", obj.get("_id"));
			item.put("id", obj.get("id"));
			item.put("nome", obj.get("nome"));
			result.add(item);
		};
		
		return result;
	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray primarias(MongoClient mongo) {

		JSONArray result = new JSONArray();
		JSONArray array = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject areaConhecimento = new BasicDBObject();
			areaConhecimento.putAll((Map) array.get(i));
			BasicDBObject item = new BasicDBObject();
			if (areaConhecimento.get("parent").toString().equals("") ){
				item.put("id", areaConhecimento.get("id").toString());
				item.put("nome", areaConhecimento.get("nome").toString());
				result.add(item);
			};
		};
		
		return result;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray parents(String primariasSource, MongoClient mongo) {

		String[] primariasStringArray =  new String[100];
		List<String> primariasList = new ArrayList<String>();
		if (primariasSource != null) {
			primariasStringArray = primariasSource.split(";");
			primariasList = Arrays.asList(primariasStringArray);
		};
		ArrayList<String> primarias = new ArrayList<String>(primariasList);
		
		JSONArray result = new JSONArray();
		JSONArray array = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject areaConhecimento = new BasicDBObject();
			areaConhecimento.putAll((Map) array.get(i));
			BasicDBObject item = new BasicDBObject();
			if (primariasSource != null && commons.testaElementoArray(areaConhecimento.get("parent").toString(), primarias)){
				item.put("id", areaConhecimento.get("id").toString());
				item.put("nome", areaConhecimento.get("nome").toString());
				item.put("parentId", areaConhecimento.get("parent").toString());
				item.put("parentName", commons_db.getCollectionDoc(areaConhecimento.get("parent").toString(), "areaConhecimento", "documento.id", mongo, false).get("nome"));
				result.add(item);
			};
		};
		
		return result;
	};
	
};
