package com.yggboard;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;


//@Lock(LockType.READ)
@RestController
@RequestMapping("/batch")
public class Rest_ProcessosBatch {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	ProcessosBatch processosBatch = new ProcessosBatch();
		
	@SuppressWarnings("rawtypes")
	@PostMapping("/atualiza/indices/collection")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AtualizaIndiceColletion(JSONObject param)  {
		if (commons.testaToken(param.get("token").toString(), mongo).getStatus() != 200) {
			return Response.status(401).entity("invalid token").build();	
		};
		BasicDBObject objParam = new BasicDBObject();
		objParam.putAll(param);
		BasicDBObject objArrays = new BasicDBObject();
		objArrays.putAll((Map) objParam.get("arrays"));
		processosBatch.CriaIndices(param.get("collection").toString(), objArrays, mongo);
		return Response.status(200).entity("Processo encerrado").build();	
	};
};
