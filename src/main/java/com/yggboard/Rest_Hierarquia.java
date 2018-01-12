package com.yggboard;


import java.util.ArrayList;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

@Singleton
//@Lock(LockType.READ)
@Path("/hierarquia")

public class Rest_Hierarquia {

	
	MongoClient mongo = new MongoClient();
	Rest_Avaliacao avaliacao = new Rest_Avaliacao(); 
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Hierarquia hierarquia = new Hierarquia();
	Usuario usuario = new Usuario();
	SendEmailHtml sendEmailHtml = new SendEmailHtml();
	TemplateEmail templateEmail = new TemplateEmail();
	

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/areas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray areas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};

		ArrayList<String> areasAvaliacao = new ArrayList<String>();
		if (avaliacaoId != null) {
			BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
			if (avaliacao.get("documento") != null) {
				BasicDBObject avaliacaoDoc = new BasicDBObject();
				avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				if (avaliacaoDoc.get("areas") != null) {
					areasAvaliacao = (ArrayList<String>) avaliacaoDoc.get("areas");
				};
			};
		};

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId", mongo, false);

		JSONArray areas = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			if (hierarquia.get("area") != null) {
				BasicDBObject area = new BasicDBObject();
				area.put("nome", hierarquia.get("area"));
				if (commons.testaElementoArray(hierarquia.get("area").toString(), areasAvaliacao)) {
					area.put("select", "true");	
				}else {
					area.put("select", "false");
				};
				commons.addObjeto(areas, area);
			};
		};
		mongo.close();
		return areas;
	};

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/niveis")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Niveis(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};

		ArrayList<String> niveisAvaliacao = new ArrayList<String>();
		if (avaliacaoId != null) {
			BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
			if (avaliacao.get("documento") != null) {
				BasicDBObject avaliacaoDoc = new BasicDBObject();
				avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				if (avaliacaoDoc.get("niveis") != null) {
					niveisAvaliacao = (ArrayList<String>) avaliacaoDoc.get("niveis");
				};
			};
		};

		ArrayList<Object> objetivos = new ArrayList<Object>(); 
		objetivos = commons_db.getCollectionLista(empresaId, "objetivosEmpresa", "documento.empresaId", mongo, false);

		JSONArray niveis = new JSONArray();
		for (int i = 0; i < objetivos.size(); i++) {
			BasicDBObject objetivoEmpresa = new BasicDBObject();
			objetivoEmpresa.putAll((Map) objetivos.get(i));
			if (objetivoEmpresa.get("objetivoId") != null) {
				BasicDBObject objetivo = commons_db.getCollection(objetivoEmpresa.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false);
				if (objetivo.get("documento") != null) {
					BasicDBObject objetivoDoc = new BasicDBObject();
					objetivoDoc.putAll((Map) objetivo.get("documento"));
					BasicDBObject nivel = new BasicDBObject();
					nivel.put("nome", objetivoDoc.get("nivelFiltro"));
					if (commons.testaElementoArray(objetivoDoc.get("nivelFiltro").toString(), niveisAvaliacao)) {
						nivel.put("select", "true");	
					}else {
						nivel.put("select", "false");
					};
					commons.addObjeto(niveis, nivel);
				};
			};
		};
		mongo.close();
		return niveis;
		
	};

	@Path("/colaboradores")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("usuarioId") String usuarioId, @QueryParam("perfil") String perfil)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = hierarquia.colaboradores(empresaId, usuarioId, perfil, mongo);
		mongo.close();
		return result;
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/importar")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response importaHierarquia(BasicDBObject hierarquiaJson)  {

		if (hierarquiaJson.get("token") == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(hierarquiaJson.get("token").toString(), "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (hierarquiaJson.get("empresaId") == null) {
			mongo.close();
			return null;
		};
		
		Boolean envioEmail = true;
		
		if (hierarquiaJson.get("envioEmail") == null) {
			envioEmail = (Boolean) hierarquiaJson.get("envioEmail");
		};		
		
		String empresaId = (String) hierarquiaJson.get("empresaId");
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		commons_db.removerCrudMany("hierarquias", keysArray, mongo, false);
		
		ArrayList<Object> colaboradores = (ArrayList<Object>) hierarquiaJson.get("colaboradores");

		String perfilEmpresa = "rh";
		for (int i = 0; i < colaboradores.size(); i++) {
			BasicDBObject colaborador = new BasicDBObject();
			colaborador.putAll((Map) colaboradores.get(i));
			BasicDBObject usuarioIn = new BasicDBObject();
			usuarioIn = commons_db.getCollection(colaborador.get("email").toString(), "usuarios", "documento.email", mongo, false);
			if (usuarioIn == null){
				usuarioIn = usuario.criaUsuario(colaborador, empresaId, envioEmail, mongo, false);
			}else {
				keysArray = new ArrayList<>();
				key = new JSONObject();
				key.put("key", "documento.email");
				key.put("value", colaborador.get("email").toString());
				keysArray.add(key);				
				ArrayList<JSONObject> fieldsArray = new ArrayList<>();
				JSONObject field = new JSONObject();				
				fieldsArray = new ArrayList<>();
				field = new JSONObject();
				field.put("field", "photo");
				field.put("value", colaborador.get("email") + ".jpg");
				fieldsArray.add(field);
				fieldsArray = new ArrayList<>();
				field = new JSONObject();
				field.put("field", "empresaId");
				field.put("value", empresaId);
				fieldsArray.add(field);
				field = new JSONObject();
				field.put("field", "perfilEmpresa");
				field.put("value", perfilEmpresa);
				fieldsArray.add(field);
				BasicDBObject documento = new BasicDBObject();
				documento.put("documento", colaborador);
				commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
			};
			BasicDBObject userPerfil = new BasicDBObject();
			userPerfil = commons_db.getCollection(colaborador.get("email").toString(), "userPerfil", "documento.usuario", mongo, false);
			if (userPerfil == null){
				userPerfil = hierarquia.criaUserPerfil(colaborador, empresaId, mongo);
			};
			perfilEmpresa = "colaborador";
		};
		
		for (int i = 0; i < colaboradores.size(); i++) {
			BasicDBObject colaborador = new BasicDBObject();
			colaborador.putAll((Map) colaboradores.get(i));
			hierarquia.criaHierarquia(colaborador, empresaId, mongo);
		};

		mongo.close();
		return Response.status(200).entity(true).build();	
	
	};	

};