package com.yggboard.yggboard_server;

import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

 
@Singleton
//@Lock(LockType.READ)
@Path("/batch")
public class Rest_ProcessosBatch {
	
	public Commons commons = new Commons();
	public Commons_DB commons_db = new Commons_DB();
	public ProcessosBatch processosBatch = new ProcessosBatch();
		
	@SuppressWarnings("rawtypes")
	@Path("/atualiza/indices/collection")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AtualizaIndiceColletion(JSONObject param)  {
		if (commons.testaToken(param.get("token").toString()).getStatus() != 200) {
			return Response.status(401).entity("invalid token").build();	
		};
		BasicDBObject objParam = new BasicDBObject();
		objParam.putAll(param);
		BasicDBObject objArrays = new BasicDBObject();
		objArrays.putAll((Map) objParam.get("arrays"));
		processosBatch.CriaIndices(param.get("collection").toString(), objArrays);
		return Response.status(200).entity("Processo encerrado").build();	
	};
};
