package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
			documentos.put("habilidades", carregaHabilidades(doc).get("habilidades"));
			documentos.put("objetivo", carregaHabilidades(doc).get("objetivo"));
			return documentos;
		};
		return null;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaHabilidades(BasicDBObject doc) {
		
		String avaliacaoId = "5983d292d11f72ed9e7b4319";
		
		String objetivoId = null;
		ArrayList<Object> avaliacoes = (ArrayList<Object>) doc.get("avaliacoes");
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();	
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("id").equals(avaliacaoId)) {
				objetivoId = (String) avaliacao.get("objetivoId");
			};
		};
		
		if (objetivoId != null) {
			BasicDBObject obj = new BasicDBObject();
			obj = commons_db.getCollection(objetivoId, "objetivos", "documento.id"); 
			BasicDBObject objetivoDoc = new BasicDBObject();
			objetivoDoc.putAll((Map) obj.get("documento"));
			ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
			JSONArray habilidades = new JSONArray();
			for (int z = 0; z < habilidadesArray.size(); z++) {
				BasicDBObject docHabilidade = new BasicDBObject();
				docHabilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
				if (docHabilidade != null) {
					commons.addObjeto(habilidades, docHabilidade);
				};
			};
			BasicDBObject documento = new BasicDBObject();
			documento.put("habilidades", habilidades);
			documento.put("objetivo", objetivoDoc.get("nome"));
			return documento;
		};
		return null;
	}

	private void carregaMapa(ArrayList<Object> outArray, ArrayList<String> inArray) {

		if (inArray != null) {
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

	@SuppressWarnings({ "unchecked" })
	@Path("/colaboradores")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		JSONArray cursor = commons_db.getCollectionLista(empresaId, "mapaAvaliacao", "documento.empresaId");
		JSONArray documentos = new JSONArray();
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject doc = (BasicDBObject) cursor.get(i);
				BasicDBObject docUsu = new BasicDBObject();
				docUsu = commons_db.getCollection(doc.getString("usuarioId"), "usuarios", "_id");
				BasicDBObject docOut = (BasicDBObject) docUsu.get("documento");
				docOut.remove("password");
				docOut.remove("token");
				documentos.add(docOut);				
			};
			return documentos;
		};
		return null;			
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/inout")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapa(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("colaboradorObjetoId") String colaboradorObjetoId, @QueryParam("assunto") String assunto)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		if (colaboradorId == null){
			return false;
		};
		if (colaboradorObjetoId == null){
			return false;
		};
		if (assunto == null){
			return false;
		};
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId");
		if (docObjetivo == null){
			return false;
		};

		if (docObjetivo != null) {
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) docObjetivo.get("documento"));
			ArrayList<Object> array = new ArrayList<>();
			array = (ArrayList<Object>) doc.get(assunto);
			if (array == null) {
				return null;
			};
			Boolean existeColaboradorObjeto = false;
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i).equals(colaboradorObjetoId)) {
					existeColaboradorObjeto = true;	
				};
			};
			
			if (existeColaboradorObjeto) {
				array =commons.removeString(array, colaboradorObjetoId);
			}else {
				array.add(colaboradorObjetoId);
			};
			
			BasicDBObject docOut = new BasicDBObject();			
			doc.put(assunto, array);
	
			BasicDBObject documento = new BasicDBObject();
			documento.put("documento", doc);

			ArrayList<JSONObject> keysArray = new ArrayList<>();
			JSONObject key = new JSONObject();
			key.put("key", "documento.usuarioId");
			key.put("value", colaboradorId);
			keysArray.add(key);
			
			ArrayList<JSONObject> fieldsArray = new ArrayList<>();
			JSONObject field = new JSONObject();
			field.put("field", assunto);
			field.put("value", array);
			fieldsArray.add(field);
	
			commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
			return true;	
		}
		return false;
	};
	
};
