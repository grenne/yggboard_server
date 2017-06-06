package com.yggboard.yggboard_server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

	
@Singleton
// @Lock(LockType.READ)
@Path("/usuario")

public class Rest_Usuario {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/confirma")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response ConfirmaSenha(@QueryParam("id") String id) {
		
		Commons_DB commons_db = new Commons_DB();
		
		ObjectId idObject = new ObjectId(id);
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", idObject);
		keysArray.add(key);
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento.status");
		field.put("value", "condirmado");
		fieldsArray.add(field);
		
		Response status = commons_db.atualizarCrud("usuarios", fieldsArray, keysArray);
		return status;

	};

	@SuppressWarnings("unchecked")
	@Path("/reseta")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response ResetaSenha(@QueryParam("email") String email) {
	
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();

		Long time = commons.currentTime();
		String novaSenha = "ygg" + time;

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", email);
		keysArray.add(key);
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "password");
		field.put("value", novaSenha);
		fieldsArray.add(field);
		
		Response status = commons_db.atualizarCrud("usuarios", fieldsArray, keysArray);
		return status;

	};
};
