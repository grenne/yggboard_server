package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class UserPerfil {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject userPerfil = commons_db.getCollection(id, "userPerfil", "_id", mongo, false);

		BasicDBObject userPerfilDoc = new BasicDBObject();
		if (userPerfil != null) {
			userPerfilDoc.putAll((Map) userPerfil.get("documento"));
			if (userPerfilDoc != null) {
				return null;
			}
		};
		
		return usuario.getEmail(userPerfilDoc.get("usuario").toString(), mongo);
	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean setAction(String usuarioPar, String objeto, String idPar, MongoClient mongo) {
		
		Usuario usuario = new Usuario();
		
		BasicDBObject userPerfil = usuario.getUserPerfil(usuarioPar, mongo);
		
		ArrayList array = (ArrayList) userPerfil.get(objeto);
		
		if (commons.testaElementoArray(idPar, array)) {
			commons.removeString(array, idPar);
		}else {
			array.add(idPar);
		};
		
		userPerfil.put(objeto, array);
		
		// *** atualiza
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", userPerfil.get("_id"));
		keysArray.add(key);

		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		
		fieldsArray = new ArrayList<>();
		field = new JSONObject();
		field.put("field", objeto);
		field.put("value", array);
		fieldsArray.add(field);

		if (commons_db.atualizarCrud("userPerfil", fieldsArray, keysArray, null, mongo, false).getStatus() == 200) {
			return true;
		}else {
			return false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList preRequsitosFaltantes(String usuarioPar, String id, MongoClient mongo) {
		

		Usuario usuario = new Usuario();
		
		ArrayList<BasicDBObject> result = new ArrayList<>();
		
		BasicDBObject userPerfil = usuario.getUserPerfil(usuarioPar, mongo);
		BasicDBObject habilidade = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);
		
		ArrayList habilidadesUser = (ArrayList) userPerfil.get("habilidades");
		ArrayList preRequisitos = (ArrayList) habilidade.get("preRequisitos");
		ArrayList preRequisitosNome = (ArrayList) habilidade.get("preRequisitosNome");
		
		for (int i = 0; i < preRequisitos.size(); i++) {
			if (!commons.testaElementoArray(preRequisitos.get(i).toString(), habilidadesUser)) {
				BasicDBObject preRequisito = new BasicDBObject();
				preRequisito.put("id", preRequisitos.get(i));
				preRequisito.put("nome", preRequisitosNome.get(i));
				result.add(preRequisito);
			};
		};
		return result;
	};
};
