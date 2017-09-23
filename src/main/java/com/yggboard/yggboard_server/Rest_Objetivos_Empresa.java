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
import com.mongodb.MongoClient;

@Singleton
//@Lock(LockType.READ)
@Path("/objetivos/empresa")


public class Rest_Objetivos_Empresa {

	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();

	@SuppressWarnings({ "unchecked" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Lista(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		JSONArray cursor = commons_db.getCollectionLista(empresaId, "objetivosEmpresa", "documento.empresaId", mongo, false);
		JSONArray documentos = new JSONArray();
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject obj = (BasicDBObject) cursor.get(i);
				BasicDBObject docObj = new BasicDBObject();
				docObj = commons_db.getCollection(obj.getString("objetivoId"), "objetivos", "documento.id", mongo, false);
				documentos.add(docObj);				
			};
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;			
	};

	@SuppressWarnings({ "unchecked" })
	@Path("/areaAtuacao/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ListaAreaAtuacao(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		JSONArray cursor = commons_db.getCollectionListaNoKey("areaAtuacao", mongo, false);
		JSONArray documentos = new JSONArray();
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				documentos.add(cursor.get(i));				
			};
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;			
	};

	@SuppressWarnings({ "unchecked" })
	@Path("/areaConhecimento/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ListaAreaConhecimento(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		JSONArray cursor = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		JSONArray documentos = new JSONArray();
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				documentos.add(cursor.get(i));				
			};
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;			
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/objetivo/listas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObjetivoListas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId,@QueryParam("objetivoId") String objetivoId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		JSONObject documentos = new JSONObject();
		JSONArray habilidades = new JSONArray();
		JSONArray habilidadesObjetivo = new JSONArray();
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false);
		if (objetivo != null) {
			BasicDBObject objetivoDoc = new BasicDBObject();
			objetivoDoc.putAll((Map) objetivo.get("documento"));
			ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
			ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, empresaId, objetivoId, mongo);
			if (habilidadesFinal != null) {
				for (int z = 0; z < habilidadesFinal.size(); z++) {
					BasicDBObject habilidade = new BasicDBObject();
					habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id", mongo, false);
					BasicDBObject habilidadeDoc = new BasicDBObject();
					habilidadeDoc.putAll((Map) habilidade.get("documento"));
					BasicDBObject habilidadeOut = new BasicDBObject();
					habilidadeOut.put("nome", habilidadeDoc.get("nome"));
					habilidadeOut.put("id", habilidadeDoc.get("id").toString());
					BasicDBObject habilidadeDocOut = new BasicDBObject();
					habilidadeDocOut.put("documento", habilidadeOut);
					if (habilidade != null) {
						if (!commons.testaElementoArrayObject(habilidade, habilidadesObjetivo)){
							habilidadesObjetivo.add(habilidadeDocOut);
						};
					};
				};
			};

			ArrayList<String> areaAtuacaoArray = (ArrayList<String>) objetivoDoc.get("areaAtuacao"); 
			for (int i = 0; i < areaAtuacaoArray.size(); i++) {
				JSONArray objetivoArray = commons_db.getCollectionLista(areaAtuacaoArray.get(i).toString(), "objetivos", "documento.areaAtuacao", mongo, false);
				for (int j = 0; j < objetivoArray.size(); j++) {
					BasicDBObject docObjetivoObj = new BasicDBObject();
					docObjetivoObj.putAll((Map) objetivoArray.get(j));
					if (!docObjetivoObj.get("_id").toString().equals(objetivo.get("_id").toString())) {
						habilidadesArray = new ArrayList<String>();
						habilidadesArray = (ArrayList<String>) docObjetivoObj.get("necessarios");
						for (int z = 0; z < habilidadesArray.size(); z++) {
							BasicDBObject habilidade = new BasicDBObject();
							habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id", mongo, false);
							if (habilidade != null) {
								BasicDBObject habilidadeDoc = new BasicDBObject();
								habilidadeDoc.putAll((Map) habilidade.get("documento"));
								BasicDBObject habilidadeOut = new BasicDBObject();
								habilidadeOut.put("nome", habilidadeDoc.get("nome"));
								habilidadeOut.put("id", habilidadeDoc.get("id").toString());
								BasicDBObject habilidadeDocOut = new BasicDBObject();
								habilidadeDocOut.put("documento", habilidadeOut);
								if (!commons.testaElementoArrayObject(habilidade, habilidades)){
									commons.addObjeto(habilidades, habilidadeDocOut);
								};
							};
						};
					};
				};									
			};
			documentos.put("habilidades", habilidades);
			documentos.put("habilidadesObjetivo", habilidadesObjetivo);
			mongo.close();
			return documentos;
		};
		return null;		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/habilidades/areaAtuacao")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject HabildadesAreaAtuacao(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("objetivoId") String objetivoId, @QueryParam("areaAtuacaoId") String areaAtuacaoId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		JSONObject documentos = new JSONObject();
		JSONArray habilidades = new JSONArray();
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false);
		if (docObjetivo != null) {
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) docObjetivo.get("documento"));
			JSONArray objetivoArray = commons_db.getCollectionLista(areaAtuacaoId, "objetivos", "documento.areaAtuacao", mongo, false);
			for (int j = 0; j < objetivoArray.size(); j++) {
				BasicDBObject docObjetivoObj = new BasicDBObject();
				docObjetivoObj.putAll((Map) objetivoArray.get(j));
				if (!docObjetivoObj.get("_id").toString().equals(docObjetivo.get("_id").toString())) {
					ArrayList<String> habilidadesArray = (ArrayList<String>) docObjetivoObj.get("necessarios");
					for (int z = 0; z < habilidadesArray.size(); z++) {
						BasicDBObject docHabilidade = new BasicDBObject();
						docHabilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id", mongo, false);
						if (docHabilidade != null) {
							commons.addObjeto(habilidades, docHabilidade);
						};
					};
				};
			};									
			documentos.put("habilidades", habilidades);
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/habilidades/areaConhecimento")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject HabildadesAreaConhecimento(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("objetivoId") String objetivoId, @QueryParam("areaConhecimentoId") String areaConhecimentoId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		JSONObject documentos = new JSONObject();
		JSONArray habilidades = new JSONArray();
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false);
		if (docObjetivo != null) {
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) docObjetivo.get("documento"));
			ArrayList<String> habilidadesObjetivoArray = (ArrayList<String>) doc.get("necessarios");
			JSONArray habilidadesArray = commons_db.getCollectionLista(areaConhecimentoId, "habilidades", "documento.areaConhecimento", mongo, false);
			for (int j = 0; j < habilidadesArray.size(); j++) {
				BasicDBObject docHabilidadeObj = new BasicDBObject();
				docHabilidadeObj.putAll((Map) habilidadesArray.get(j));
				if (!(commons.testaElementoArray(docHabilidadeObj.get("id").toString(), habilidadesObjetivoArray))) {
					commons.addObjeto(habilidades, docHabilidadeObj);
				};
			};									
			documentos.put("habilidades", habilidades);
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/habilidades/todas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject HabildadesTodas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("objetivoId") String objetivoId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		JSONObject documentos = new JSONObject();
		JSONArray habilidades = new JSONArray();
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false);
		if (docObjetivo != null) {
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) docObjetivo.get("documento"));
			ArrayList<String> habilidadesObjetivoArray = (ArrayList<String>) doc.get("necessarios");
			JSONArray habilidadesArray = commons_db.getCollectionListaNoKey("habilidades", mongo, false);
			for (int j = 0; j < habilidadesArray.size(); j++) {
				BasicDBObject docHabilidadeObj = new BasicDBObject();
				docHabilidadeObj.putAll((Map) habilidadesArray.get(j));
				if (!(commons.testaElementoArray(docHabilidadeObj.get("id").toString(), habilidadesObjetivoArray))) {
					commons.addObjeto(habilidades, docHabilidadeObj);
				};
			};									
			documentos.put("habilidades", habilidades);
			mongo.close();
			return documentos;
		};
		return null;		
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/inout")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaObjetivo(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("objetivoId") String objetivoId, @QueryParam("habilidadeId") String habilidadeId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return false;
		};
		if (objetivoId == null){
			mongo.close();
			return false;
		};
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false);
		if (docObjetivo == null){
			mongo.close();
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

		Response response = commons_db.obterCrud("objetivosEmpresa", keysArray, mongo, false);
		if ((response.getStatus() != 200)){
			mongo.close();
			return false;
		};
		BasicDBObject objetivoEmpresaObj = new BasicDBObject();
		objetivoEmpresaObj.putAll((Map) response.getEntity());
		BasicDBObject objetivoEmpresaDoc = new BasicDBObject();
		objetivoEmpresaDoc.putAll((Map) objetivoEmpresaObj.get("documento"));
		
		
		Boolean existeHabilidadeIn = false;
		Boolean existeHabilidadeOut = false;
		Boolean existeHabilidadeNecessarios = false;
		Boolean removerHabilidadeIn = false;
		Boolean removerHabilidadeOut = false;
		Boolean incluirHabilidadeIn = false;
		Boolean incluirHabilidadeOut = false;

		BasicDBObject objetivoObj = commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false);
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivoObj.get("documento"));

		ArrayList<Object> habilidadesIn = (ArrayList<Object>) objetivoEmpresaDoc.get("habilidadesIn");
		for (int i = 0; i < habilidadesIn.size(); i++) {
			if (habilidadesIn.get(i).equals(habilidadeId)) {
				existeHabilidadeIn = true;	
			};
		};
		
		ArrayList<Object> habilidadesOut = (ArrayList<Object>) objetivoEmpresaDoc.get("habilidadesOut");
		for (int i = 0; i < habilidadesOut.size(); i++) {
			if (habilidadesOut.get(i).equals(habilidadeId)) {
				existeHabilidadeOut = true;	
			};
		};

		ArrayList<String> necessarios = (ArrayList<String>) objetivoDoc.get("necessarios");
		for (int i = 0; i < necessarios.size(); i++) {
			if (necessarios.get(i).equals(habilidadeId)) {
				existeHabilidadeNecessarios = true;	
			};
		};
		
		if (!existeHabilidadeNecessarios) {
			if (existeHabilidadeIn) {
				removerHabilidadeIn = true;
			}else {
				incluirHabilidadeIn = true;
			};
		}else {
			if (existeHabilidadeOut) {
				removerHabilidadeOut = true;
			}else {
				incluirHabilidadeOut = true;
			};				
		};
		
		if (incluirHabilidadeIn) {
			habilidadesIn.add(habilidadeId);
		};
		if (incluirHabilidadeOut) {
			habilidadesOut.add(habilidadeId);
		};

		if (removerHabilidadeIn) {
			habilidadesIn =commons.removeObjeto(habilidadesIn, habilidadeId.toString());
		};
		if (removerHabilidadeOut) {
			habilidadesOut = commons.removeObjeto(habilidadesOut, habilidadeId.toString());
		};
		
		objetivoEmpresaDoc.put("habilidadesIn", habilidadesIn);
		objetivoEmpresaDoc.put("habilidadesOut", habilidadesOut);
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento");
		field.put("value", objetivoEmpresaDoc);
		fieldsArray.add(field);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", objetivoEmpresaDoc);

		commons_db.atualizarCrud("objetivosEmpresa", fieldsArray, keysArray, documento, mongo, false);
		mongo.close();
		return true;	
	};
	
};
