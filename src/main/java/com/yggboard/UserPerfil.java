package com.yggboard;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

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
			Boolean temHabilidade = false;
			BasicDBObject preRequisito = new BasicDBObject();
			String preRequisitosString = preRequisitos.get(i).toString().split(":")[0].toString().replace("|", "&");
			String [] preRequisitosArray = preRequisitosString.split("&");
			for (int j = 0; j < preRequisitosArray.length; j++) {
				if (commons.testaElementoArray(preRequisitosArray[j].toString(), habilidadesUser)) {
					temHabilidade = true;
				};	
			};
			if (!temHabilidade) {
				preRequisito.put("id", preRequisitos.get(i));
				preRequisito.put("nome", preRequisitosNome.get(i));
				result.add(preRequisito);
			};
		};
		return result;
	};
	
	@SuppressWarnings({ "unchecked" })
	public BasicDBObject obterEstatistica(String id, String item, MongoClient mongo) {
		
		BasicDBObject userPerfil = commons_db.getCollectionDoc(id, "userPerfil", "_id", mongo, false);

		ArrayList<String> objetivosInteresse = (ArrayList<String>) userPerfil.get("carreirasInteresse");
		ArrayList<String> habilidadesInteresse = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
		ArrayList<String> cursosInteresse = (ArrayList<String>) userPerfil.get("cursosInteresse");
		ArrayList<String> habilidades = (ArrayList<String>) userPerfil.get("habilidades");

		BasicDBObject result = new BasicDBObject();
		
		result.put("objetuvosInteresse", Integer.toString(objetivosInteresse.size()));
		result.put("habilidadesInteresse", Integer.toString(habilidadesInteresse.size()));
		result.put("cursosInteresse", Integer.toString(cursosInteresse.size()));
		result.put("habilidadesNecessarias", Integer.toString(habilidadesNecessarias(objetivosInteresse,habilidades, mongo).size()));
		return result;
	
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> habilidadesNecessarias(ArrayList<String> objetivosInteresse, ArrayList<String> habilidades, MongoClient mongo) {
		
		ArrayList<String> result = new ArrayList<>();
		
		for (int i = 0; i < objetivosInteresse.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(objetivosInteresse.get(i).toString(), "objetivos", "documento.id", mongo, false);
			if (objetivo != null) {
				ArrayList<String> habilidadesNecessarias = (ArrayList<String>) objetivo.get("necessarios");
				if (habilidadesNecessarias != null) {
					for (int j = 0; j < habilidadesNecessarias.size(); j++) {
						if (!commons.testaElementoArray(habilidadesNecessarias.get(j), habilidades)) {
							commons.addStringArrayList(result, habilidadesNecessarias.get(j));
						};
					};
				};
			};
		};
		
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response atualizaSugestaoColetiva(JSONObject inputCursosSugeridos, String nameArray, MongoClient mongo) {
		
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
		List arrayCursosSugestao = (List) inputCursosSugeridos.get(nameArray);
		for (int i = 0; i < arrayCursosSugestao.size(); i++) {
			JSONObject cursosSugestao = new JSONObject();
			cursosSugestao.putAll((Map) arrayCursosSugestao.get(i));
			String usuario = cursosSugestao.get("usuario").toString();
			BasicDBObject cursor = commons_db.getCollection(usuario, "userPerfil", "documento.usuario", mongo, false);
			if (cursor != null){
				BasicDBObject objUserPerfilUpdate = (BasicDBObject) cursor.get("documento");
				objUserPerfilUpdate.remove(nameArray);
				objUserPerfilUpdate.put(nameArray, cursosSugestao.get("cursos"));
				BasicDBObject objUserPerfilDocumento = new BasicDBObject();
				objUserPerfilDocumento.put("documento", objUserPerfilUpdate);
				Response atualizacao = commons_db.atualizaDocumento(objUserPerfilDocumento,  "cursos", "documento.token", usuario.toString(), mongo, false);
				if (atualizacao.getStatus() == 200) {
					// incluir evento
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", usuario);
					evento.put("evento", "userPerfil");
					evento.put("idEvento", nameArray);
					evento.put("motivo", "inclusao");
					evento.put("elemento", nameArray);
					evento.put("idElemento", cursosSugestao.get("cursos"));
					commons.insereEvento(evento, mongo);
				};
			};
		};
		return Response.status(200).build();		
	};
	
};
