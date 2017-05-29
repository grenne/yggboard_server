package com.yggboard.yggboard_server;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;


@Singleton
//@Lock(LockType.READ)
@Path("/crud")

public class Rest_Crud {
	
	@Path("/obter")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Obter(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("keys") != null && queryParam.get ("collection").toString() != null){
			return commons_db.obterCrud(queryParam.get ("collection").toString(), queryParam.get("keys"));
		}else{
			return Response.status(400).entity(null).build();	
		}
	};

	@Path("/incluir")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Incluir(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get ("insert") != null && queryParam.get ("collection").toString() != null){
			return commons_db.incluirCrud(queryParam.get ("collection").toString(), queryParam.get ("insert"));
		}else{
			return Response.status(400).entity(null).build();	
		}
	};

	@Path("/atualizar")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Atualizar(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("update") != null && queryParam.get ("collection").toString() != null && queryParam.get("keys") != null){
			return commons_db.atualizarCrud(queryParam.get ("collection").toString(), queryParam.get("update"), queryParam.get("keys"));
		}else{
			return Response.status(400).entity(null).build();	
		}
	};

	
	@Path("/lista")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Lista(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("keys") != null && queryParam.get ("collection").toString() != null){
			return commons_db.listaCrud(queryParam.get ("collection").toString(), queryParam.get("keys"));
		}else{
			return Response.status(400).entity(null).build();	
		}
	};

	@Path("/remover/all")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Remover(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get ("collection").toString() != null){
			return commons_db.removerAllCrud(queryParam.get ("collection").toString());
		}else{
			return Response.status(400).entity(null).build();	
		}
	};
}
