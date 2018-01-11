package com.yggboard;


import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class Index {
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void criaIndice(String collectionName, MongoClient mongo) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();

		JSONObject key = new JSONObject();
		key.put("key", "documento.assunto");
		key.put("value", "index");
		keysArray.add(key);

		commons_db.removerCrud("index", keysArray, mongo, false);
		
		JSONArray lista = commons_db.getCollectionListaNoKey(collectionName, mongo, false);
		
		for (int i = 0; i < lista.size(); i++) {
			BasicDBObject elemento = new BasicDBObject();
			elemento.putAll((Map) lista.get(i));
			BasicDBObject elementoDoc = new BasicDBObject();
			elementoDoc.put("documento", elemento);
			gravaIndex("usuarios", elementoDoc, elemento.get("_id").toString(), mongo);
		};
				
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void gravaIndex(String assunto, Object collection, String id, MongoClient mongo) {

		BasicDBObject documento = new BasicDBObject();
		documento.putAll((Map) collection);
		
		BasicDBObject documentoDoc = new BasicDBObject();
		documentoDoc.putAll((Map) documento.get("documento"));

		String entidade = "";
		if (documentoDoc.get("nome") != null) {
			entidade = documentoDoc.get("nome").toString();
		};
		if (documentoDoc.get("firstName") != null && documentoDoc.get("lastName") != null) {
			entidade = documentoDoc.get("firstName").toString() + " " + documentoDoc.get("lastName");
		};
		String descricao = "";
		if (documentoDoc.get("descricao") != null) {
			descricao = documentoDoc.get("descricao").toString();
		};

		JSONArray texto = new JSONArray();
		
		String[] words = entidade.split(" ");
		
		for (int i = 0; i < words.length; i++) {
			texto.add(words[i]);
		};
		
		words = descricao.split(" ");
		
		for (int i = 0; i < words.length; i++) {
			texto.add(words[i]);
		};

		if (documentoDoc.get("escola") != null) {			
			words = documentoDoc.get("escola").toString().split(" ");
			for (int i = 0; i < words.length; i++) {
				texto.add(words[i]);
			};
		};
		
		if (texto.size() > 0 && assunto != "index" && assunto != "eventos") {
  		BasicDBObject documentoInsert = new BasicDBObject();
  		documentoInsert.put("assunto", assunto);
  		documentoInsert.put("entidade", entidade);
  		documentoInsert.put("id", id);
  		documentoInsert.put("descricao", descricao);
  		documentoInsert.put("texto", texto);
  		BasicDBObject documentoInsertDoc = new BasicDBObject();
  		documentoInsertDoc.put("documento", documentoInsert);
  		commons_db.incluirCrud("index", documentoInsertDoc, mongo, false);
		};

	};

	@SuppressWarnings({ })
	public void obterTexto(String assunto, Object collection, String id, MongoClient mongo) {
	
	};
}
