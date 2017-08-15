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

@Singleton
//@Lock(LockType.READ)
@Path("/hierarquia")

public class Rest_Hierarquia {

	Rest_Avaliacao avaliacao = new Rest_Avaliacao(); 
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/areas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Areas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null) {
			return null;
		};

		ArrayList<String> areasAvaliacao = new ArrayList<String>();
		if (avaliacaoId != null) {
			BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id");
			if (avaliacao.get("documento") != null) {
				BasicDBObject avaliacaoDoc = new BasicDBObject();
				avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				if (avaliacaoDoc.get("areas") != null) {
					areasAvaliacao = (ArrayList<String>) avaliacaoDoc.get("areas");
				};
			};
		};

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId");

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
		return areas;
	};

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/niveis")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Niveis(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null) {
			return null;
		};

		ArrayList<String> niveisAvaliacao = new ArrayList<String>();
		if (avaliacaoId != null) {
			BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id");
			if (avaliacao.get("documento") != null) {
				BasicDBObject avaliacaoDoc = new BasicDBObject();
				avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				if (avaliacaoDoc.get("niveis") != null) {
					niveisAvaliacao = (ArrayList<String>) avaliacaoDoc.get("niveis");
				};
			};
		};

		ArrayList<Object> objetivos = new ArrayList<Object>(); 
		objetivos = commons_db.getCollectionLista(empresaId, "objetivosEmpresa", "documento.empresaId");

		JSONArray niveis = new JSONArray();
		for (int i = 0; i < objetivos.size(); i++) {
			BasicDBObject objetivoEmpresa = new BasicDBObject();
			objetivoEmpresa.putAll((Map) objetivos.get(i));
			if (objetivoEmpresa.get("objetivoId") != null) {
				BasicDBObject objetivo = commons_db.getCollection(objetivoEmpresa.get("objetivoId").toString(), "objetivos", "documento.id");
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
		return niveis;
		
	};
	

	@SuppressWarnings("unchecked")
	@Path("/importar/hierarquia")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Lista(JSONObject hierarquiaJson)  {
		Commons_DB commons_db = new Commons_DB();
		
		String empresaId = (String) hierarquiaJson.get("empresaId");
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		commons_db.removerCrud("hierarquias", keysArray);
		
		ArrayList<BasicDBObject> usuarios = (ArrayList<BasicDBObject>) hierarquiaJson.get("usuarios");

		for (int i = 0; i < usuarios.size(); i++) {
			BasicDBObject usuario = new BasicDBObject();
			usuario = commons_db.getCollection(usuarios.get(i).get("email").toString(), "usuarios", "documento.email");
			if (usuario == null){
				usuario = criaUsuario(usuarios.get(i), empresaId);
			};
			usuarios.get(i).put("usuarioId", usuario.get("_id"));

			BasicDBObject userPerfil = new BasicDBObject();
			userPerfil = commons_db.getCollection(usuarios.get(i).get("email").toString(), "userPerfil", "documento.usuario");
			if (userPerfil == null){
				userPerfil = criaUserPerfil(usuarios.get(i), empresaId);
			};
		};
		
		for (int i = 0; i < usuarios.size(); i++) {
			criaHierarquia(usuarios.get(i), empresaId);
		};

		return Response.status(200).entity(true).build();	
	
	};	

	private BasicDBObject criaUsuario(BasicDBObject usuarioIn, String empresaId) {

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
		usuarioDoc.put("perfilEmpresa", usuarioIn.get("colaborador"));
		usuarioDoc.put("login", usuarioIn.get("login"));
		usuarioDoc.put("empresaId", empresaId);
		usuario.put("documento", usuarioDoc);
		
		return (BasicDBObject) commons_db.incluirCrud("usuarios", usuario).getEntity();
		
	};
	
	private BasicDBObject criaUserPerfil(BasicDBObject usuario, String empresaId) {

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
		
		return (BasicDBObject) commons_db.incluirCrud("userPerfil", userPerfil).getEntity();
		
	};

	private BasicDBObject criaHierarquia(BasicDBObject usuario, String empresaId) {

		BasicDBObject superior = new BasicDBObject();
		superior = commons_db.getCollection(usuario.get("superior").toString(), "usuarios", "documento.email");
		if (superior == null){
			return null;
		};
		String superiorId = superior.get("_id").toString();

		BasicDBObject hierarquia = new BasicDBObject();
		BasicDBObject hierarquiaDoc = new BasicDBObject();
		
		hierarquiaDoc.put("empresaId", empresaId);
		hierarquiaDoc.put("objetivoId", usuario.get("objetivoId"));
		hierarquiaDoc.put("area", usuario.get("area"));
		hierarquiaDoc.put("colaborador", usuario.get("usuarioId"));
		hierarquiaDoc.put("superior", superiorId);
		hierarquia.put("documento", hierarquiaDoc);
		
		return (BasicDBObject) commons_db.incluirCrud("hierarquias", hierarquia).getEntity();
		
	};

};