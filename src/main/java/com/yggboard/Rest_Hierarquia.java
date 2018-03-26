package com.yggboard;


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

		JSONArray results = hierarquia.getAreas(empresaId, avaliacaoId, mongo);
		mongo.close();
		return results;
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

		JSONArray results = hierarquia.getNiveis(empresaId, avaliacaoId, mongo);
		mongo.close();
		return results;
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
		
		hierarquia.importar(hierarquiaJson, mongo);
		
		mongo.close();
		return Response.status(200).entity(true).build();	
	};
};