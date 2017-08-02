package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

@Singleton
//@Lock(LockType.READ)
@Path("/objetivos/empresa")


public class Rest_Objetivos_Empresa {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();

	@SuppressWarnings({ "unchecked" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Lista(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		JSONArray cursor = commons_db.getCollectionLista(empresaId, "objetivosEmpresa", "documento.empresaId");
		JSONArray documentos = new JSONArray();
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject obj = (BasicDBObject) cursor.get(i);
				BasicDBObject docObj = new BasicDBObject();
				docObj = commons_db.getCollection(obj.getString("objetivoId"), "objetivos", "documento.id");
				documentos.add(docObj);				
			};
			return documentos;
		};
		return null;	
		};

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Path("/objetivo/listas")	
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject ObjetivoListas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId,@QueryParam("objetivoId") String objetivoId)  {
			if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
				return null;
			};
			JSONObject documentos = new JSONObject();
			JSONArray habilidades = new JSONArray();
			JSONArray habilidadesObjetivo = new JSONArray();
			BasicDBObject docObjetivo = new BasicDBObject();
			docObjetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id");
			if (docObjetivo != null) {
				BasicDBObject doc = new BasicDBObject();
				doc.putAll((Map) docObjetivo.get("documento"));
				ArrayList<String> areaAtuacaoArray = (ArrayList<String>) doc.get("areaAtuacao"); 
				for (int i = 0; i < areaAtuacaoArray.size(); i++) {
					JSONArray objetivoArray = commons_db.getCollectionLista(areaAtuacaoArray.get(i).toString(), "objetivos", "documento.areaAtuacao");
					for (int j = 0; j < objetivoArray.size(); j++) {
						BasicDBObject docObjetivoObj = new BasicDBObject();
						docObjetivoObj.putAll((Map) objetivoArray.get(i));
						ArrayList<String> habilidadesArray = (ArrayList<String>) docObjetivoObj.get("necessarios");
						for (int z = 0; z < habilidadesArray.size(); z++) {
							BasicDBObject docHabilidade = new BasicDBObject();
							docHabilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
							habilidades.add(docHabilidade);
						};
					};									
				};
				ArrayList<String> habilidadesArray = (ArrayList<String>) doc.get("necessarios");
				for (int z = 0; z < habilidadesArray.size(); z++) {
					BasicDBObject docHabilidade = new BasicDBObject();
					docHabilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
					habilidadesObjetivo.add(docHabilidade);
				};
				documentos.put("habilidades", habilidades);
				documentos.put("habilidadesObjetivo", habilidadesObjetivo);
				return documentos;
			};
			return null;	
			};
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Path("/inout")	
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Boolean AtualizarPerfil(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("objetivoId") String objetivoId)  {
			if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
				return null;
			};
			if (empresaId == null){
				return false;
			};
			if (objetivoId == null){
				return false;
			};
			BasicDBObject docObjetivo = new BasicDBObject();
			docObjetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id");
			if (docObjetivo == null){
				return false;
			};

			ArrayList<JSONObject> keysArray = new ArrayList<>();
			JSONObject key = new JSONObject();
			key.put("key", "documento.empresaId");
			key.put("value", empresaId);
			keysArray.add(key);
			key = new JSONObject();
			key.put("key", "documento.objetivoId");
			key.put("value", objetivoId);
			keysArray.add(key);

			Response response = commons_db.obterCrud("objetivosEmpresa", keysArray);
			if ((response.getStatus() == 200)){
				commons_db.removerCrud("objetivosEmpresa", keysArray);
				return true;
			};
			
			BasicDBObject documento = new BasicDBObject();
			BasicDBObject doc = new BasicDBObject();
			ArrayList<String> list = new ArrayList();
			doc.put("empresaId",empresaId);
			doc.put("objetivoId",objetivoId);
			doc.put("habilidadesIn",list);
			doc.put("habilidadesOut",list);
			documento.put("documento", doc);
			
			commons_db.incluirCrud("objetivosEmpresa", documento);
			return true;	
		};
		
};
