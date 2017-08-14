package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;


public class Avaliacao {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean criaMapaAvaliacao(String usuarioId, String empresaId, String avaliacaoId) {
		
		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId");

		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));

			Boolean existeMapa = false;
			ArrayList<JSONObject> avaliacoes = new ArrayList<>();
			BasicDBObject docMapa = new BasicDBObject();
			docMapa = commons_db.getCollection(hierarquia.get("colaborador").toString(), "mapaAvaliacao", "documento.usuarioId");
			if (docMapa != null){
				existeMapa = true;
				avaliacoes = (ArrayList<JSONObject>) docMapa.get("avaliacoes");
			};

			JSONObject avaliacao = new JSONObject();
			avaliacao.put("id", avaliacaoId);
			avaliacao.put("superiores", hierarquia(hierarquia.get("colaborador").toString(), "colaborador"));
			avaliacao.put("subordinados", hierarquia(hierarquia.get("colaborador").toString(), "superior"));
			avaliacao.put("parceiros", hierarquia(hierarquia.get("superior").toString(), "superior"));
			avaliacao.put("clientes", hierarquia(hierarquia.get("colaborador").toString(), "clientes"));
			ArrayList<JSONObject> habilidades = new ArrayList<>();
			avaliacao.put("habilidades", habilidades);

			ArrayList<JSONObject> avaliacoesNew = new ArrayList<>();
			if (existeMapa) {
				for (int j = 0; j < avaliacoes.size(); j++) {
					if (!avaliacaoId.equals(avaliacoes.get(i).get("id").toString())){
						avaliacoesNew.add(avaliacoes.get(i));			
					};
				};
			};
			
			avaliacoesNew.add(avaliacao);

			ArrayList<JSONObject> fieldsArray = new ArrayList<>();
			JSONObject field = new JSONObject();
			fieldsArray = new ArrayList<>();
			field = new JSONObject();
			field.put("field", "avaliacoes");
			field.put("value", avaliacoesNew);
			fieldsArray.add(field);

			ArrayList<JSONObject> keysArray = new ArrayList<>();
			JSONObject key = new JSONObject();
			key.put("key", "documento.usuarioId");
			key.put("value", usuarioId);
			keysArray.add(key);

			if (existeMapa) {
				commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, null);
			}else {
				commons_db.incluirCrud("mapaAvaliacao", criaMapaDoc(usuarioId, empresaId, avaliacoesNew));
			};
		};
		return true;
		
	};

	private Object criaMapaDoc(String usuarioId, String empresaId, ArrayList<JSONObject> avaliacoes) {

		BasicDBObject mapaAvaliacao = new BasicDBObject();
		BasicDBObject mapaAvaliacaoDoc = new BasicDBObject();
		
		mapaAvaliacaoDoc.put("usuarioId", usuarioId);
		mapaAvaliacaoDoc.put("empresaId", empresaId);
		mapaAvaliacaoDoc.put("avaliacoes", avaliacoes);
		mapaAvaliacao.put("documento", mapaAvaliacaoDoc);
		return mapaAvaliacao;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<String> hierarquia(String usuarioId, String tipo) {

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(usuarioId, "hierarquias", "documento." + tipo);

		JSONArray arrayColaboradores = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			commons.addString(arrayColaboradores, hierarquia.get("colaborador").toString());
		};
		return arrayColaboradores;
	};
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BasicDBObject mapa(String usuarioId, String empresaId, String avaliacaoId)  {
		
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId");
		if (objetivo == null) {
			return null;
		};
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		
		ArrayList<BasicDBObject> avaliacoes =  new ArrayList<BasicDBObject>();
		BasicDBObject avaliacao = new BasicDBObject();
		
		for (int i = 0; i < avaliacoes.size(); i++) {
			if (avaliacaoId.equals(avaliacoes.get(i).get("id").toString())){
				avaliacao = (BasicDBObject) avaliacoes.get(i);
			};
		};
		if (avaliacao == null) {
			return null;
		};

		ArrayList<String> objArray = new ArrayList<String>();

		ArrayList<Object> superioresArray =  new ArrayList<Object>();
		objArray = (ArrayList<String>) avaliacao.get("superiores");
		carregaMapa (avaliacao, superioresArray, objArray);
		
		ArrayList<Object> parceirosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("parceiros");
		carregaMapa (avaliacao, parceirosArray, objArray);
		
		ArrayList<Object> subordinadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("subordinados");
		carregaMapa (avaliacao, subordinadosArray, objArray);
		
		ArrayList<Object> clientesArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientes");
		carregaMapa (avaliacao, clientesArray, objArray);

		BasicDBObject documentos = new BasicDBObject();
		
		documentos.put("superiores", superioresArray);
		documentos.put("parceiros", parceirosArray);
		documentos.put("subordinados", subordinadosArray);
		documentos.put("clientes", clientesArray);
		documentos.put("habilidades", carregaHabilidades(avaliacao, empresaId, obterObjetivoColaborador(avaliacao, usuarioId)).get("habilidades"));
		documentos.put("objetivo", carregaHabilidades(avaliacao, empresaId, obterObjetivoColaborador(avaliacao, usuarioId)).get("nome"));
		return documentos;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String obterObjetivoColaborador(BasicDBObject doc, String usuarioId) {
		
		String avaliacaoId = "5983d292d11f72ed9e7b4319";
		
		ArrayList<BasicDBObject> avaliacoes = (ArrayList<BasicDBObject>) doc.get("avaliacoes");
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();	
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacaoId.equals(avaliacoes.get(i).get("id").toString())){
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
	public JSONArray colaboradores(String empresaId)  {

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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean montaMapa(String colaboradorId, String colaboradorObjetoId, String assunto, String empresaId, String avaliacaoId)  {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId");
		if (mapa == null){
			return false;
		};

		BasicDBObject mapaDoc = new BasicDBObject();
		mapaDoc.putAll((Map) mapa.get("documento"));
		
		ArrayList<BasicDBObject> avaliacoes =  (ArrayList<BasicDBObject>) mapaDoc.get("avaliacoes");
		ArrayList<BasicDBObject> avaliacoesNew =  new ArrayList<BasicDBObject>();
		BasicDBObject avaliacao = new BasicDBObject();
		
		for (int i = 0; i < avaliacoes.size(); i++) {
			if (avaliacoes.get(i).equals(avaliacaoId)) {
				avaliacao = (BasicDBObject) avaliacoes.get(i).get("avaliacoes");
			}else {
				avaliacoesNew.add(avaliacoes.get(i));
			};
		};
		
		if (avaliacao == null) {
			return null;
		};
		
		ArrayList<String> array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get(assunto);
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
			array = commons.removeString(array, colaboradorObjetoId);
		}else {
			ArrayList<String> superiores = (ArrayList<String>) avaliacao.get("superiores");
			superiores = commons.removeString(superiores, colaboradorObjetoId);
			avaliacao.remove("superiores");
			avaliacao.put("superiores", superiores);
			ArrayList<String> subordinados = (ArrayList<String>) avaliacao.get("subordinados");
			subordinados = commons.removeString(subordinados, colaboradorObjetoId);
			avaliacao.remove("subordinados");
			avaliacao.put("subordinados", subordinados);
			ArrayList<String> parceiros = (ArrayList<String>) avaliacao.get("parceiros");
			parceiros = commons.removeString(parceiros, colaboradorObjetoId);
			avaliacao.remove("parceiros");
			avaliacao.put("parceiros", parceiros);
			ArrayList<String> clientes = (ArrayList<String>) avaliacao.get("clientes");
			clientes = commons.removeString(clientes, colaboradorObjetoId);
			avaliacao.remove("clientes");
			avaliacao.put("parceiros", clientes);
			array.add(colaboradorObjetoId);
		};
		
		avaliacao.remove(assunto);
		avaliacao.put(assunto, array);
		avaliacoesNew.add(avaliacao);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.usuarioId");
		key.put("value", colaboradorId);
		keysArray.add(key);

		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		
		fieldsArray = new ArrayList<>();
		field = new JSONObject();
		field.put("field", "avaliacoes");
		field.put("value", avaliacoesNew);
		fieldsArray.add(field);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", mapaDoc);

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
		return true;	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean atualizaNota(String avaliadorId, String colaboradorId, String habilidadeId, String nota, String empresaId)  {

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
	@SuppressWarnings("unchecked")
	public ArrayList<Object> lista(String empresaId)  {
	

		ArrayList<Object> avaliacoes = new ArrayList<Object>(); 
		avaliacoes = commons_db.getCollectionLista(empresaId, "avaliacoes", "documento.empresaId");

		JSONArray avaliacoesResult = new JSONArray();
		for (int i = 0; i < avaliacoes.size(); i++) {
			avaliacoesResult.add(avaliacoes.get(i));
		};	
		return avaliacoesResult;
	};

};
