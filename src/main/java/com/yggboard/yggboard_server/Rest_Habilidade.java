package com.yggboard.yggboard_server;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
		JSONArray cursor = commons_db.getCollectionLista("", "habilidades", "");
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject obj = new BasicDBObject();
				obj.putAll((Map) cursor.get(i));
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", obj.getString("_id"));
				BasicDBObject setUpdate = new BasicDBObject();
				setUpdate.putAll((Map) obj.get("documento"));
				jsonDocumento.put("documento", setUpdate);
				documentos.add(jsonDocumento);
			};
			return documentos;
		};
		return null;
	};

}
