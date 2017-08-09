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
	@Path("/cria/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public void CriaMapa(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("empresaId") String empresaId)  {
	

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId");

		JSONArray areas = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			ArrayList<String> subordinados = superior(hierarquia.get("colaborador").toString());
			ArrayList<String> parceiros = superior(hierarquia.get("superior").toString());
			ArrayList<String> habilidades = habilidades(hierarquia.get("colaborador").toString(), empresaId);
			
		};	
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<String> habilidades(String usuarioId, String empresaId) {

		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId");
		BasicDBObject doc = new BasicDBObject();
		doc.putAll((Map) docObjetivo.get("documento"));
		return (ArrayList<String>) carregaHabilidades(doc, empresaId, obterObjetivoColaborador(doc, usuarioId)).get("habilidades");
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<String> superior(String superior) {

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(superior, "hierarquias", "documento.superior");

		JSONArray arrayColaboradores = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			commons.addString(arrayColaboradores, hierarquia.get("superior").toString());
		};
		return arrayColaboradores;
	}
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Mapa(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null) {
			return null;
		};
		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId");
		if (docObjetivo == null) {
			return null;
		};
		BasicDBObject doc = new BasicDBObject();
		doc.putAll((Map) docObjetivo.get("documento"));

		ArrayList<String> objArray = new ArrayList<String>();

		ArrayList<Object> superioresArray =  new ArrayList<Object>();
		objArray = (ArrayList<String>) doc.get("superiores");
		carregaMapa (doc, superioresArray, objArray);
		
		ArrayList<Object> parceirosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) doc.get("parceiros");
		carregaMapa (doc, parceirosArray, objArray);
		
		ArrayList<Object> subordinadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) doc.get("subordinados");
		carregaMapa (doc, subordinadosArray, objArray);
		
		ArrayList<Object> clientesArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) doc.get("clientes");
		carregaMapa (doc, clientesArray, objArray);

		BasicDBObject documentos = new BasicDBObject();
		
		documentos.put("superiores", superioresArray);
		documentos.put("parceiros", parceirosArray);
		documentos.put("subordinados", subordinadosArray);
		documentos.put("clientes", clientesArray);
		documentos.put("habilidades", carregaHabilidades(doc, empresaId, obterObjetivoColaborador(doc, usuarioId)).get("habilidades"));
		documentos.put("objetivo", carregaHabilidades(doc, empresaId, obterObjetivoColaborador(doc, usuarioId)).get("nome"));
		return documentos;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String obterObjetivoColaborador(BasicDBObject doc, String usuarioId) {
		
		String avaliacaoId = "5983d292d11f72ed9e7b4319";
		
		ArrayList<Object> avaliacoes = (ArrayList<Object>) doc.get("avaliacoes");
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();	
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("id").equals(avaliacaoId)) {
				return (String) avaliacao.get("objetivoId");
			};
		};
		return null;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaHabilidades(BasicDBObject doc, String empresaId, String objetivoId) {
		

		BasicDBObject obj = new BasicDBObject();
		obj = commons_db.getCollection(objetivoId, "objetivos", "documento.id"); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) obj.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, empresaId, objetivoId);
		JSONArray habilidades = new JSONArray();
		
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			BasicDBObject docHabilidade = new BasicDBObject();
			docHabilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
			BasicDBObject avaliacao = getAvaliacao(doc,habilidadesArray.get(z));
			if (docHabilidade != null) {
  			docHabilidade.put("nota", avaliacao.get("nota"));
  			docHabilidade.put("avaliadorId", avaliacao.get("avaliadorId"));
  			docHabilidade.put("avaliadorNome", avaliacao.get("avaliadorNome"));
  			if (docHabilidade != null) {
  				commons.addObjeto(habilidades, docHabilidade);
  			};
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		documento.put("nome", objetivoDoc.get("nome"));
		return documento;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BasicDBObject getAvaliacao(BasicDBObject doc, String habilidadeId) {
		
		String avaliacaoId = "5983d292d11f72ed9e7b4319";
		ArrayList<Object> avaliacoes = (ArrayList<Object>) doc.get("avaliacoes");
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("id").equals(avaliacaoId)) {
				ArrayList<Object> habilidades = (ArrayList<Object>) avaliacao.get("habilidades");
				for (int j = 0; j < habilidades.size(); j++) {
					BasicDBObject habilidade = new BasicDBObject();
					habilidade.putAll((Map) habilidades.get(j));
					if (habilidade.get("id").equals(habilidadeId)) {
						BasicDBObject avaliacaoResult = new BasicDBObject();
						avaliacaoResult.put("nota", habilidade.get("nota"));
						avaliacaoResult.put("avaliadorId", habilidade.get("avaliadorId"));
		  			BasicDBObject usuario = commons_db.getCollection(habilidade.get("avaliadorId").toString(), "usuario", "_id");
		  			if (usuario != null) {
		  				avaliacaoResult.put("avaliadorNome", usuario.get("fisrtName") + " " + usuario.get("lastName"));
		  			}else {
		  				avaliacaoResult.put("avaliadorNome", " ");
		  			};
						return avaliacaoResult;
					};
				};
			};
		};
		return null;
	};

	@SuppressWarnings("rawtypes")
	private void carregaMapa(BasicDBObject doc, ArrayList<Object> outArray, ArrayList<String> inArray) {

		if (inArray != null) {
			for (int i = 0; i < inArray.size(); i++) {
				BasicDBObject registro = commons_db.getCollection(inArray.get(i), "usuarios", "_id");
				if (registro != null) {
					BasicDBObject outObj = new BasicDBObject();
					BasicDBObject docObj = (BasicDBObject) registro.get("documento");
					outObj.put("nome", docObj.get("firstName") + " " + docObj.get("lastName"));
					outObj.put("photo", docObj.get("photo"));
					outObj.put("id", registro.get("_id").toString());
					String objetivoId = obterObjetivoColaborador(doc, doc.getString("usuarioId"));
					BasicDBObject objetivoDoc = new BasicDBObject(); 
					objetivoDoc.putAll((Map) commons_db.getCollection(objetivoId, "objetivos", "documento.id").get("documento"));
					outObj.put("objetivo", objetivoDoc.get("nome"));
					outArray.add(outObj);
				};
			};		
		};
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/colaboradores")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null) {
			return null;
		};
		JSONArray cursor = commons_db.getCollectionLista(empresaId, "mapaAvaliacao", "documento.empresaId");
		JSONArray documentos = new JSONArray();
		if (cursor == null){
			return null;
		};
		for (int i = 0; i < cursor.size(); i++) {
			BasicDBObject doc = (BasicDBObject) cursor.get(i);
			BasicDBObject docUsu = new BasicDBObject();
			docUsu = commons_db.getCollection(doc.getString("usuarioId"), "usuarios", "_id");
			BasicDBObject docOut = (BasicDBObject) docUsu.get("documento");
			docOut.remove("password");
			docOut.remove("token");
			String objetivoId = obterObjetivoColaborador(doc, doc.getString("usuarioId"));
			docOut.put("id", doc.getString("usuarioId"));
			BasicDBObject objetivoDoc = new BasicDBObject(); 
			objetivoDoc.putAll((Map) commons_db.getCollection(objetivoId, "objetivos", "documento.id").get("documento"));
			docOut.put("objetivo", objetivoDoc.get("nome"));
			documentos.add(docOut);
		};
		
		return documentos;
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/inout")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapa(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("colaboradorObjetoId") String colaboradorObjetoId, @QueryParam("assunto") String assunto, @QueryParam("empresaId") String empresaId)  {
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
		
		BasicDBObject doc = new BasicDBObject();
		doc.putAll((Map) docObjetivo.get("documento"));
		ArrayList<String> array = new ArrayList<>();
		array = (ArrayList<String>) doc.get(assunto);
		if (array == null) {
			return null;
		};
		Boolean existeColaboradorObjeto = false;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).equals(colaboradorObjetoId)) {
				existeColaboradorObjeto = true;	
			};
		};
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		if (existeColaboradorObjeto) {
			array = commons.removeString(array, colaboradorObjetoId);
		}else {
			ArrayList<String> superiores = (ArrayList<String>) doc.get("superiores");
			superiores = commons.removeString(superiores, colaboradorObjetoId);
			fieldsArray = new ArrayList<>();
			field = new JSONObject();
			field.put("field", "superiores");
			field.put("value", superiores);
			fieldsArray.add(field);
			ArrayList<String> subordinados = (ArrayList<String>) doc.get("subordinados");
			subordinados = commons.removeString(subordinados, colaboradorObjetoId);
			fieldsArray = new ArrayList<>();
			field = new JSONObject();
			field.put("field", "subordinados");
			field.put("value", subordinados);
			fieldsArray.add(field);
			ArrayList<String> parceiros = (ArrayList<String>) doc.get("parceiros");
			parceiros = commons.removeString(parceiros, colaboradorObjetoId);
			fieldsArray = new ArrayList<>();
			field = new JSONObject();
			field.put("field", "parceiros");
			field.put("value", parceiros);
			fieldsArray.add(field);
			ArrayList<String> clientes = (ArrayList<String>) doc.get("clientes");
			clientes = commons.removeString(clientes, colaboradorObjetoId);
			fieldsArray = new ArrayList<>();
			field = new JSONObject();
			field.put("field", "clientes");
			field.put("value", clientes);
			fieldsArray.add(field);
			array.add(colaboradorObjetoId);
		};
		
		doc.put(assunto, array);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", doc);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.usuarioId");
		key.put("value", colaboradorId);
		keysArray.add(key);
		
		fieldsArray = new ArrayList<>();
		field = new JSONObject();
		field.put("field", assunto);
		field.put("value", array);
		fieldsArray.add(field);

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
		return true;	
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/atualiza/nota")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean AtualizaNota(@QueryParam("token") String token, @QueryParam("colaboradorId") String avaliadorId, @QueryParam("avaliadorId") String colaboradorId, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("nota") String nota, @QueryParam("empresaId") String empresaId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token")) == null) {
			return null;
		};
		if (colaboradorId == null){
			return false;
		};
		if (habilidadeId == null){
			return false;
		};
		if (nota == null){
			return false;
		};
		if (avaliadorId == null){
			return false;
		};

		BasicDBObject docObjetivo = new BasicDBObject();
		docObjetivo = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId");
		if (docObjetivo == null){
			return false;
		};
		BasicDBObject doc = new BasicDBObject();
		doc.putAll((Map) docObjetivo.get("documento"));
		
		String avaliacaoId = "5983d292d11f72ed9e7b4319";
		
		ArrayList<Object> avaliacoes = (ArrayList<Object>) doc.get("avaliacoes");
		ArrayList<Object> avaliacoesNova = new ArrayList<Object>();
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();	
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("id").equals(avaliacaoId)) {
				ArrayList<BasicDBObject> habilidades = (ArrayList<BasicDBObject>) avaliacao.get("habilidades");
				ArrayList<BasicDBObject> habilidadesNew = new ArrayList<BasicDBObject>();
				BasicDBObject habilidade = new BasicDBObject();
				Boolean existeAvaliacao = false;
				for (int j = 0; j < habilidades.size(); j++) {
					habilidade = new BasicDBObject();
					habilidade.putAll((Map) habilidades.get(j));
					if (habilidade.get("id").equals(habilidadeId) && habilidade.get("avaliadorId").equals(avaliadorId)) {
						habilidade.put("nota", nota);
						habilidadesNew.add(habilidade);
						existeAvaliacao = true;
					}else {
						habilidadesNew.add(habilidade);
					};
				};
				if (!existeAvaliacao) {
					habilidade = new BasicDBObject();
					habilidade.put("id", habilidadeId);
					habilidade.put("nota", nota);
					habilidade.put("avaliadorId", avaliadorId);
					habilidadesNew.add(habilidade);
				};
				avaliacao.remove("habilidades");
				avaliacao.put("habilidades", habilidadesNew);
			};
			avaliacoesNova.add(avaliacao);
		};

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", doc);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.usuarioId");
		key.put("value", colaboradorId);
		keysArray.add(key);
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field = new JSONObject();
		field.put("field", "avaliacoes");
		field.put("value", avaliacoesNova);
		fieldsArray.add(field);

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
		return true;	
	};

	@SuppressWarnings({"unchecked" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Object> Lista(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId)  {
	

		ArrayList<Object> avaliacoes = new ArrayList<Object>(); 
		avaliacoes = commons_db.getCollectionLista(empresaId, "avaliacoes", "documento.empresaId");

		JSONArray avaliacoesResult = new JSONArray();
		for (int i = 0; i < avaliacoes.size(); i++) {
			avaliacoesResult.add(avaliacoes.get(i));
		};	
		return avaliacoesResult;
	};
	
};
