package com.yggboard;

import java.io.IOException;
import java.util.Map;

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONArray filtraObjetivosEmpresa(JSONArray objetivos, String empresaId, MongoClient mongo) {

		JSONArray objetivosEmpresa = commons_db.getCollectionLista(empresaId, "objetivosEmpresa", "documento.empresaId",
				mongo, false);

		if (objetivosEmpresa == null) {
			return objetivos;
		}
		;

		JSONArray objetivosResult = new JSONArray();

		for (int i = 0; i < objetivos.size(); i++) {
			BasicDBObject objetivo = new BasicDBObject();
			objetivo.putAll((Map) objetivos.get(i));
			BasicDBObject objetivoDoc = new BasicDBObject();
			objetivoDoc.putAll((Map) objetivo.get("documento"));
			for (int j = 0; j < objetivosEmpresa.size(); j++) {
				BasicDBObject objetivoEmpresa = new BasicDBObject();
				objetivoEmpresa.putAll((Map) objetivosEmpresa.get(j));
				if (objetivoDoc.get("id").equals(objetivoEmpresa.get("objetivoId"))) {
					objetivosResult.add(objetivo);
				}
				;
			}
			;
		}
		;
		return objetivosResult;

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
