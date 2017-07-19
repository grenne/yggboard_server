package com.yggboard.yggboard_server;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

	
@Singleton
// @Lock(LockType.READ)
@Path("/habilidades")

public class Rest_Habilidade {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidadess() {
		Commons_DB commons_db = new Commons_DB();
		Response response = commons_db.getCollectionLista("", "habilidades", "");
		
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject cursor = new BasicDBObject();
			cursor.putAll((Map) response.getEntity());
			if (cursor != null){
				JSONArray documentos = new JSONArray();
				while (((Iterator<DBObject>) cursor).hasNext()) {
					BasicDBObject obj = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
					JSONObject jsonDocumento = new JSONObject();
					jsonDocumento.put("_id", obj.getString("_id"));
					BasicDBObject setUpdate = new BasicDBObject();
					setUpdate.putAll((Map) obj.get("documento"));
					jsonDocumento.put("documento", setUpdate);
					documentos.add(jsonDocumento);
				};
				return documentos;
			}
		};
		return null;
	};

}
