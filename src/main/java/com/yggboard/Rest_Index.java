package com.yggboard;

import java.io.IOException;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

@Singleton
// @Lock(LockType.READ)
@Path("/index")

public class Rest_Index {

	MongoClient mongo = new MongoClient();

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();

	@Path("/obter/itens")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItens(@QueryParam("assunto") String assunto, @QueryParam("id") String id,
			@QueryParam("usuario") String usuario, @QueryParam("empresaId") String empresaId) {

		System.out.println("Metodo deperecate");
		return null;
	};

	@Path("/obter/filtro")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItensFiltro(JSONArray objFiltros)
			throws JsonParseException, JsonMappingException, IOException {

		System.out.println("Metodo deperecate");
		return null;
	};

	@Path("/lista")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterLista(@QueryParam("characters") String characters,
			@QueryParam("planejamentoLista") String planejamentoLista, @QueryParam("usuario") String usuario,
			@QueryParam("empresaId") String empresaId) {

		System.out.println("Metodo deperecate");
		return null;
	};

};
