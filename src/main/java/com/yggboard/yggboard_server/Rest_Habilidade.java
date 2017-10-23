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
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/habilidades")

public class Rest_Habilidade {

	MongoClient mongo = new MongoClient();
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidades() {
		Commons_DB commons_db = new Commons_DB();
		JSONArray cursor = commons_db.getCollectionListaNoKey("habilidades", mongo, false);
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject obj = new BasicDBObject();
				obj.putAll((Map) cursor.get(i));
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", obj.getString("_id"));
				jsonDocumento.put("documento", obj);
				documentos.add(jsonDocumento);
			};
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;
	};
	@SuppressWarnings({ "unchecked", "rawtypes"})
	@Path("/cursos")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidade(@QueryParam("token") String token, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("usuarioId") String usuarioId) {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (habilidadeId == null) {
			mongo.close();
			return null;
		};
		
		ArrayList<Object> cursosSelecionados = new ArrayList<>();
		if (usuarioId != null) {
  		BasicDBObject usuario = new BasicDBObject();
  		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);
  		BasicDBObject usuarioDoc = new BasicDBObject();
  		usuarioDoc.putAll((Map) usuario.get("documento"));  
  		if (usuarioDoc.get("cursosSelecionados") != null){
    		cursosSelecionados = (ArrayList<Object>) usuarioDoc.get("cursosSelecionados");
  		};
		};

		BasicDBObject habilidade = commons_db.getCollectionDoc(habilidadeId, "habilidades", "documento.id", mongo, false);
		
		ArrayList<String> cursos =  (ArrayList<String>) habilidade.get("cursos");
		
		JSONArray results = new JSONArray();
		for (int i = 0; i < cursos.size(); i++) {
			BasicDBObject curso = commons_db.getCollectionDoc(cursos.get(i).toString(), "cursos", "documento.id", mongo, false);
			BasicDBObject result = new BasicDBObject();
			Boolean cursoSelecionado = false;
			for (int j = 0; j < cursosSelecionados.size(); j++) {
  			JSONObject cursoCompare = new JSONObject();
  			cursoCompare.putAll((Map) cursosSelecionados.get(j));
  			String cursoIdCompare = cursoCompare.get("id").toString();
  			if (cursoIdCompare.equals(cursos.get(i).toString())) {
  				cursoSelecionado = true;
  			};
			};
			if (!cursoSelecionado){
  			if (curso.get("parents") != null) {
    			ArrayList<String> parent =  (ArrayList<String>) curso.get("parents");
    			if (parent.size() == 0) {
      			result.put("nome", curso.get("nome"));
      			result.put("id", curso.get("id"));
      			result.put("escola", curso.get("escola"));
      			result.put("logo", curso.get("logo"));
      			result.put("duracao", curso.get("duracao"));
      			result.put("cargaHoraria", curso.get("cargaHoraria"));
      			result.put("formato", curso.get("formato"));
      			result.put("nivel", curso.get("nivel"));
      			result.put("periodicidade", curso.get("periodicidade"));
      			result.put("descricao", curso.get("descricao"));
      			result.put("custo", curso.get("custo"));
      			result.put("link", curso.get("link"));
      			ArrayList<String> cursoHabilidades =  (ArrayList<String>) curso.get("habilidades");
      			result.put("qtdeHabilidades", Integer.toString(cursoHabilidades.size()));
      			results.add(result);
    			};
  			};
			};
		};

		mongo.close();
		return results;
	};

}
