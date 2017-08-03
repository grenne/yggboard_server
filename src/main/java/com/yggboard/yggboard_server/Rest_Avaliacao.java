package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBObject;

@Singleton
//@Lock(LockType.READ)
@Path("/avaliacao")

public class Rest_Avaliacao {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Mapa(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId");
		if (docObjetivo != null) {
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) docObjetivo.get("documento"));

			ArrayList<String> objArray = new ArrayList<String>();

			ArrayList<Object> superioresArray =  new ArrayList<Object>();
			objArray = (ArrayList<String>) doc.get("superiores");
			carregaMapa (superioresArray, objArray);
			
			ArrayList<Object> parceirosArray =  new ArrayList<Object>();
			objArray =  new ArrayList<String>();
			objArray = (ArrayList<String>) doc.get("parceiros");
			carregaMapa (parceirosArray, objArray);
			
			ArrayList<Object> subordinadosArray =  new ArrayList<Object>();
			objArray =  new ArrayList<String>();
			objArray = (ArrayList<String>) doc.get("subordinados");
			carregaMapa (subordinadosArray, objArray);
			
			ArrayList<Object> clientesArray =  new ArrayList<Object>();
			objArray =  new ArrayList<String>();
			objArray = (ArrayList<String>) doc.get("clientes");
			carregaMapa (clientesArray, objArray);

			BasicDBObject documentos = new BasicDBObject();
			
			documentos.put("superiores", superioresArray);
			documentos.put("parceiros", parceirosArray);
			documentos.put("subordinados", subordinadosArray);
			documentos.put("clientes", clientesArray);
			return documentos;
		};
		return null;
	};
	private void carregaMapa(ArrayList<Object> outArray, ArrayList<String> inArray) {
		for (int i = 0; i < inArray.size(); i++) {
			BasicDBObject registro = commons_db.getCollection(inArray.get(i), "usuarios", "_id");
			if (registro != null) {
				BasicDBObject outObj = new BasicDBObject();
				BasicDBObject docObj = (BasicDBObject) registro.get("documento");
				outObj.put("nome", docObj.get("firstName") + " " + docObj.get("lastName"));
				outObj.put("id", registro.get("_id").toString());
				outArray.add(outObj);
			};
		};		
	};
};
