package com.yggboard;


import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;


public class Hierarquia {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	MongoClient mongo = new MongoClient();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray colaboradores(String empresaId, String usuarioId, String perfil, MongoClient mongo) {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);
		if (perfil != null) {
  		if (!perfil.equals("rh")) {
    		key = new JSONObject();
    		key.put("key", "documento.superior");
    		key.put("value", usuarioId);
    		keysArray.add(key);
  		};
		};
		Response response = commons_db.listaCrud("hierarquias", keysArray, mongo, false);

		JSONArray hierarquias = (JSONArray) response.getEntity();
		JSONArray results = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			BasicDBObject result = new BasicDBObject();
			BasicDBObject usuario = commons_db.getCollectionDoc(hierarquia.get("colaborador").toString(), "usuarios", "_id", mongo, false);
			result.put("firstName", usuario.get("firstName"));
			result.put("lastName", usuario.get("lastName"));
			result.put("photo", usuario.get("photo"));
			result.put("id", usuario.get("_id").toString());
			BasicDBObject objetivo = commons_db.getCollectionDoc(hierarquia.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false);
			result.put("objetivo", objetivo.get("nome"));
			result.put("objetivoId", hierarquia.get("objetivoId").toString());
			results.add(result);
		};
		return results;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BasicDBObject criaUserPerfil(BasicDBObject usuario, String empresaId, MongoClient mongo) {

		BasicDBObject userPerfil = new BasicDBObject();
		BasicDBObject userPerfilDoc = new BasicDBObject();
		
		ArrayList<String> arrayVazia = new ArrayList<String>();
		
		userPerfilDoc.put("usuario", usuario.get("email"));
		userPerfilDoc.put("seguindo", arrayVazia);
		userPerfilDoc.put("carreirasSugeridas", arrayVazia);
		userPerfilDoc.put("cursos", arrayVazia);
		userPerfilDoc.put("cursosInteresse", arrayVazia);
		userPerfilDoc.put("cursosInscrito", arrayVazia);
		userPerfilDoc.put("badgesInsteresse", arrayVazia);
		userPerfilDoc.put("habilidadesInteresse", arrayVazia);
		userPerfilDoc.put("cursosSugeridos", arrayVazia);
		userPerfilDoc.put("tags", arrayVazia);
		userPerfilDoc.put("badges", arrayVazia);
		userPerfilDoc.put("showBadges", arrayVazia);
		userPerfilDoc.put("ordemObjetivos", arrayVazia);
		userPerfilDoc.put("elementos", arrayVazia);
		userPerfilDoc.put("carreiras", arrayVazia);
		userPerfilDoc.put("badgesConquista", arrayVazia);
		userPerfilDoc.put("cursosAndamento", arrayVazia);
		userPerfil.put("documento", userPerfilDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("userPerfil", userPerfil, mongo, false).getEntity());

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();

		if (result.get("_id") != null) {
  		keysArray = new ArrayList<>();
  		key = new JSONObject();
  		key.put("key", "documento.email");
  		key.put("value", usuario.get("email").toString());
  		keysArray.add(key);				
  		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
  		JSONObject field = new JSONObject();				
  		fieldsArray = new ArrayList<>();
  		field = new JSONObject();
  		field.put("field", "userPerfil_id");
  		field.put("value", result.get("_id").toString());
  		fieldsArray.add(field);
  		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
		};

		return result;
		
	};

	@SuppressWarnings("rawtypes")
	public BasicDBObject criaHierarquia(BasicDBObject usuario, String empresaId, MongoClient mongo) {

		BasicDBObject objetivo = new BasicDBObject();
		BasicDBObject objetivoDoc = new BasicDBObject();
		if (usuario.get("objetivo") != null) {
  		objetivo = commons_db.getCollection(usuario.get("objetivo").toString(), "objetivos", "documento.id", mongo, false);
  		if (objetivo == null) {
  			return null;
  		};
  		objetivoDoc.putAll((Map) objetivo.get("documento"));
  		usuario.put("objetivoId", objetivoDoc.get("id").toString());
		};

		BasicDBObject hierarquia = new BasicDBObject();
		BasicDBObject hierarquiaDoc = new BasicDBObject();
		
		hierarquiaDoc.put("empresaId", empresaId);
		hierarquiaDoc.put("objetivoId", usuario.get("objetivoId"));
		hierarquiaDoc.put("area", usuario.get("area"));
		hierarquiaDoc.put("colaborador", commons_db.getCollection(usuario.get("email").toString(), "usuarios", "documento.email", mongo, false).get("_id").toString());
		if (usuario.get("superior") != null && usuario.get("superior") != "") {
			hierarquiaDoc.put("superior", commons_db.getCollection(usuario.get("superior").toString(), "usuarios", "documento.email", mongo, false).get("_id").toString());
			atualizaGestor(usuario.get("superior").toString());
		}else {
			hierarquiaDoc.put("superior","");
		};
		hierarquiaDoc.put("nivel", objetivoDoc.get("nivel").toString());
		hierarquia.put("documento", hierarquiaDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("hierarquias", hierarquia, mongo, false).getEntity());
		return result;
		
	}

	@SuppressWarnings("unchecked")
	private void atualizaGestor(String usuarioEmail) {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", usuarioEmail);
		keysArray.add(key);

		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();				
		fieldsArray = new ArrayList<>();
		field = new JSONObject();
		field.put("field", "perfilEmpresa");
		field.put("value", "gestor");
		fieldsArray.add(field);
		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
			
	};
	
};
