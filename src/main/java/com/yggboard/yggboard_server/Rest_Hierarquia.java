package com.yggboard.yggboard_server;

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

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/areas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Areas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/importar")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Lista(BasicDBObject hierarquiaJson)  {

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
		
		String empresaId = (String) hierarquiaJson.get("empresaId");
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		commons_db.removerCrudMany("hierarquias", keysArray, mongo, false);
		
		ArrayList<Object> colaboradores = (ArrayList<Object>) hierarquiaJson.get("colaboradores");

		for (int i = 0; i < colaboradores.size(); i++) {
			BasicDBObject colaborador = new BasicDBObject();
			colaborador.putAll((Map) colaboradores.get(i));
			BasicDBObject usuario = new BasicDBObject();
			usuario = commons_db.getCollection(colaborador.get("email").toString(), "usuarios", "documento.email", mongo, false);
			if (usuario == null){
				usuario = criaUsuario(colaborador, empresaId, mongo, false);
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
				BasicDBObject documento = new BasicDBObject();
				documento.put("documento", colaborador);
				commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
			};
			BasicDBObject userPerfil = new BasicDBObject();
			userPerfil = commons_db.getCollection(colaborador.get("email").toString(), "userPerfil", "documento.usuario", mongo, false);
			if (userPerfil == null){
				userPerfil = criaUserPerfil(colaborador, empresaId, mongo);
			};
		};
		
		for (int i = 0; i < colaboradores.size(); i++) {
			BasicDBObject colaborador = new BasicDBObject();
			colaborador.putAll((Map) colaboradores.get(i));
			criaHierarquia(colaborador, empresaId, mongo);
		};

		mongo.close();
		return Response.status(200).entity(true).build();	
	
	};	

	@SuppressWarnings("rawtypes")
	private BasicDBObject criaUsuario(BasicDBObject usuarioIn, String empresaId, MongoClient mongo, boolean close) {

		BasicDBObject usuario = new BasicDBObject();
		BasicDBObject usuarioDoc = new BasicDBObject();
		
		usuarioDoc.put("firstName", usuarioIn.get("firstName"));
		usuarioDoc.put("lastName", usuarioIn.get("lastName"));
		usuarioDoc.put("institution", usuarioIn.get("institution"));
		usuarioDoc.put("gender", usuarioIn.get("gender"));
		usuarioDoc.put("city", usuarioIn.get("city"));
		usuarioDoc.put("celPhone", usuarioIn.get("celPhone"));
		usuarioDoc.put("photo", usuarioIn.get("photo"));
		usuarioDoc.put("birthDate", usuarioIn.get("birthDate"));
		usuarioDoc.put("email", usuarioIn.get("email"));
		usuarioDoc.put("perfil", "user");
		usuarioDoc.put("perfilEmpresa", "colaborador");
		usuarioDoc.put("password", usuarioIn.get("login"));
		usuarioDoc.put("status", usuarioIn.get("confirmado"));
		usuarioDoc.put("empresaId", empresaId);
		usuarioDoc.put("photo", usuarioIn.get("email") + ".jpg");
		usuario.put("documento", usuarioDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("usuarios", usuario, mongo, false).getEntity());
		return result;
		
	};
	
	@SuppressWarnings("rawtypes")
	private BasicDBObject criaUserPerfil(BasicDBObject usuario, String empresaId, MongoClient mongo) {

		BasicDBObject userPerfil = new BasicDBObject();
		BasicDBObject userPefilDoc = new BasicDBObject();
		
		ArrayList<String> arrayVazia = new ArrayList<String>();
		
		userPefilDoc.put("usuario", usuario.get("email"));
		userPefilDoc.put("seguindo", arrayVazia);
		userPefilDoc.put("carreirasSugeridas", arrayVazia);
		userPefilDoc.put("cursos", arrayVazia);
		userPefilDoc.put("cursosInteresse", arrayVazia);
		userPefilDoc.put("cursosInscrito", arrayVazia);
		userPefilDoc.put("badgesInsteresse", arrayVazia);
		userPefilDoc.put("habilidadesInteresse", arrayVazia);
		userPefilDoc.put("cursosSugeridos", arrayVazia);
		userPefilDoc.put("tags", arrayVazia);
		userPefilDoc.put("badges", arrayVazia);
		userPefilDoc.put("showBadges", arrayVazia);
		userPefilDoc.put("ordemObjetivos", arrayVazia);
		userPefilDoc.put("elementos", arrayVazia);
		userPefilDoc.put("carreiras", arrayVazia);
		userPefilDoc.put("badgesConquista", arrayVazia);
		userPefilDoc.put("cursosAndamento", arrayVazia);
		userPerfil.put("documento", userPefilDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("userPerfil", userPerfil, mongo, false).getEntity());
		return result;
		
	};

	@SuppressWarnings("rawtypes")
	private BasicDBObject criaHierarquia(BasicDBObject usuario, String empresaId, MongoClient mongo) {

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