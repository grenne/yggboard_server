package com.yggboard;


import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;

import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/habilidades")

public class Rest_Habilidade {

	MongoClient mongo = new MongoClient();
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Habilidade habilidade = new Habilidade();
	Usuario usuario = new Usuario();

	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidades() {
		System.out.println("Metodo deperecate");
		return null;
	};

	@Path("/cursos")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidade(@QueryParam("token") String token, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("usuarioId") String usuarioId) {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (habilidadeId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray results =  habilidade.getCurso(usuarioId, habilidadeId, mongo);
		mongo.close();
		return results;

	};
	
	@Path("/filtros")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray filtros(@QueryParam("token") String token, 
							@QueryParam("areasConhecimento") String areaConhecimento,
							@QueryParam("usuarioParametro") String usuarioParametro,
							@QueryParam("limite") int limite,
							@QueryParam("start") int start)  {
	
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};

		if (limite == 0) {
			limite = 999999999;
		};
		
		JSONArray result = commons.controlaLimite(habilidade.filtros(areaConhecimento, usuarioParametro, mongo),limite, start);
		mongo.close();
		return result;
	};

}

