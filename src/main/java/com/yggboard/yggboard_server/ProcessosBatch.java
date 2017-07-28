package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

public class ProcessosBatch {
	
	public Commons_DB commons_db = new Commons_DB();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void CriaIndices(String collection, BasicDBObject arrays) {
		
		JSONArray cursor = new JSONArray();
		cursor = commons_db.getCollectionListaNoKey(collection);
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject obj = (BasicDBObject) cursor.get(i);
				ArrayList<String> arrayCollection = (ArrayList<String>) arrays.get("arrayCollection");
				ArrayList<String> arrayOrigem = (ArrayList<String>) arrays.get("arrayOrigem");
				ArrayList<String> arrayDestino = (ArrayList<String>) arrays.get("arrayDestino");
				for (int j = 0; j < arrayCollection.size(); j++) {
					JSONArray cursorArray = new JSONArray();
					cursorArray =commons_db.getCollectionLista(obj.get("id").toString(), arrayCollection.get(j), "documento." + arrayOrigem.get(j));
					JSONArray arrayUpdate = new JSONArray();
					JSONArray arrayNomeUpdate = new JSONArray();
					for (int k = 0; k < cursorArray.size(); k++) {
						BasicDBObject objArray = (BasicDBObject) cursorArray.get(k);
						arrayUpdate.add(objArray.get("id"));
						arrayNomeUpdate.add(objArray.get("nome"));
					};
					ArrayList<JSONObject> keysArray = new ArrayList<>();
					JSONObject key = new JSONObject();
					key.put("key", "documento.id");
					key.put("value", obj.get("id"));
					keysArray.add(key);

					ArrayList<JSONObject> fieldsArray = new ArrayList<>();
					JSONObject field = new JSONObject();
					field.put("field", arrayDestino.get(j).toString());
					field.put("value", arrayUpdate);
					fieldsArray.add(field);

					field = new JSONObject();
					field.put("field", arrayDestino.get(j).toString() + "Nome");
					field.put("value", arrayNomeUpdate);
					fieldsArray.add(field);
					
					BasicDBObject objUpdate = new BasicDBObject();
					objUpdate.put("documento", obj);
					Response atualizacao = commons_db.atualizarCrud(collection, fieldsArray, keysArray, objUpdate);
					obj.putAll((Map) atualizacao.getEntity());
					if (atualizacao.getStatus() != 200){
						System.out.println("Problemas na atualização - " +  collection + " - " + obj.get("id").toString());
					};
				};
			};
		};
	};
};
