package com.yggboard;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/hierarquia")
public class Rest_Hierarquia {

	
	MongoClient mongo = new MongoClient();
	Rest_Avaliacao avaliacao = new Rest_Avaliacao(); 
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Hierarquia hierarquia = new Hierarquia();
	Usuario usuario = new Usuario();
	SendEmailHtml sendEmailHtml = new SendEmailHtml();
	TemplateEmail templateEmail = new TemplateEmail();
	
	@Produces(MediaType.APPLICATION_JSON)
	@GetMapping("/areas")
	public JSONArray areas(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
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

	@GetMapping("/niveis")	

	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Niveis(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
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

	@GetMapping("/colaboradores")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("usuarioId") String usuarioId, @RequestParam("perfil") String perfil)  {
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
	
	@PostMapping("/importar")
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