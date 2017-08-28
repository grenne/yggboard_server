package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;


public class Avaliacao {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean criaMapaAvaliacao(String empresaId, String avaliacaoId) {

		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		commons_db.removerCrudMany("mapaAvaliacao", keysArray);
		
		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId");

		for (int i = 0; i < hierarquias.size(); i++) {

			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));

			ArrayList<JSONObject> avaliacoes = new ArrayList<JSONObject>();
			ArrayList<String> areas = new ArrayList<>();
			ArrayList<String> niveis = new ArrayList<>();
			Boolean existeMapa = false;
			BasicDBObject mapa = new BasicDBObject();
			mapa = commons_db.getCollection(hierarquia.get("colaborador").toString(), "mapaAvaliacao", "documento.usuarioId");
			BasicDBObject mapaDoc = new BasicDBObject();
			if (mapa != null){
				mapaDoc.putAll((Map) mapa.get("documento"));
				existeMapa = true;
				avaliacoes = (ArrayList<JSONObject>) mapaDoc.get("avaliacoes");
				areas = (ArrayList<String>) mapaDoc.get("areas");
				niveis= (ArrayList<String>) mapaDoc.get("niveis");
			};

			if (colaboradorValido(hierarquia, areas, niveis)) {
  
  			JSONObject avaliacao = new JSONObject();
  			avaliacao.put("id", avaliacaoId);
  			avaliacao.put("superiores", hierarquia(hierarquia.get("colaborador").toString(), "colaborador", "superior", hierarquia.get("colaborador").toString()));
  			avaliacao.put("subordinados", hierarquia(hierarquia.get("colaborador").toString(), "superior", "colaborador", hierarquia.get("colaborador").toString()));
  			avaliacao.put("parceiros", hierarquia(hierarquia.get("superior").toString(), "superior","colaborador", hierarquia.get("colaborador").toString()));
  			avaliacao.put("clientes", hierarquia(hierarquia.get("colaborador").toString(), "clientes", "colaborador", hierarquia.get("colaborador").toString()));
  			avaliacao.put("objetivoId", hierarquia.get("objetivoId").toString());
  			ArrayList<JSONObject> arrayVazia = new ArrayList<>();
  			avaliacao.put("habilidades", arrayVazia);
  			avaliacao.put("resultados", arrayVazia);
  
  			ArrayList<JSONObject> avaliacoesNew = new ArrayList<JSONObject>();
  			if (existeMapa) {
  				for (int j = 0; j < avaliacoes.size(); j++) {
  					JSONObject avaliacaoTeste = new JSONObject();
  					avaliacaoTeste.putAll(avaliacoes.get(j));
  					if (!avaliacaoId.equals(avaliacaoTeste.get("id").toString())){
  						avaliacoesNew.add(avaliacaoTeste);			
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
  
  			keysArray = new ArrayList<>();
  			key = new JSONObject();
  			key.put("key", "documento.usuarioId");
  			key.put("value", hierarquia.get("colaborador").toString());
  			keysArray.add(key);
  
  			if (existeMapa) {
  				commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, null);
  			}else {
  				commons_db.incluirCrud("mapaAvaliacao", criaMapaDoc(hierarquia.get("colaborador").toString(), empresaId, avaliacoesNew));
  			};
			};
		};
		return true;
		
	};

	private boolean colaboradorValido(BasicDBObject hierarquia, ArrayList<String> areas, ArrayList<String> niveis) {
		
		if (areas != null) {
			if (commons.testaElementoArray(hierarquia.get("area").toString(), areas)) {
				return false;
			}
		};
		if (niveis != null) {
			if (commons.testaElementoArray(hierarquia.get("nivel").toString(), niveis)) {
				return false;
			}
		};
		return true;
	}

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
	private ArrayList<String> hierarquia(String usuarioId, String tipo, String resultado, String colaboradorId) {

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(usuarioId, "hierarquias", "documento." + tipo);

		JSONArray arrayColaboradores = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			if (!colaboradorId.equals(hierarquia.get(resultado).toString()) && !hierarquia.get(resultado).toString().equals("")) {
				commons.addString(arrayColaboradores, hierarquia.get(resultado).toString());
			};
		};
		return arrayColaboradores;
	};
	
	@SuppressWarnings({ "unchecked" })
	public BasicDBObject mapa(String usuarioId, String empresaId, String avaliacaoId)  {
				
		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, usuarioId);
		
		if (avaliacao == null) {
			return null;
		};

		ArrayList<String> objArray = new ArrayList<String>();

		ArrayList<Object> superioresArray =  new ArrayList<Object>();
		objArray = (ArrayList<String>) avaliacao.get("superiores");
		carregaMapa (avaliacaoId, superioresArray, objArray,"in");

		objArray = (ArrayList<String>) avaliacao.get("superioresOut");
		carregaMapa (avaliacaoId, superioresArray, objArray,"out");
		
		ArrayList<Object> parceirosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("parceiros");
		carregaMapa (avaliacaoId, parceirosArray, objArray,"in");
		
		objArray = (ArrayList<String>) avaliacao.get("parceiros");
		carregaMapa (avaliacaoId, parceirosArray, objArray,"out");

		ArrayList<Object> subordinadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("subordinados");
		carregaMapa (avaliacaoId, subordinadosArray, objArray,"in");

		objArray = (ArrayList<String>) avaliacao.get("subordinados");
		carregaMapa (avaliacaoId, subordinadosArray, objArray,"out");
		
		ArrayList<Object> clientesArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientes");
		carregaMapa (avaliacaoId, clientesArray, objArray,"in");

		BasicDBObject documentos = new BasicDBObject();
		
		documentos.put("superiores", superioresArray);
		documentos.put("parceiros", parceirosArray);
		documentos.put("subordinados", subordinadosArray);
		documentos.put("clientes", clientesArray);
		if (avaliacao != null) {
  		documentos.put("habilidades", carregaHabilidades(avaliacao, empresaId).get("habilidades"));
  		documentos.put("avaliacoes", carregaAvaliacoes(avaliacao).get("avaliacoes"));
  		documentos.put("resultados", carregaResultados(avaliacao).get("resultados"));
  		documentos.put("objetivo", avaliacao.get("objetivoNome"));
		};
		return documentos;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaHabilidades(BasicDBObject avaliacao, String empresaId) {
		
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id"); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, empresaId, avaliacao.getString("objetivoId").toString());

		JSONArray habilidades = new JSONArray();
		
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			BasicDBObject habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
			BasicDBObject habilidadeDoc = new BasicDBObject();
			habilidadeDoc.putAll((Map) habilidade.get("documento"));
			if (habilidadeDoc != null) {
				BasicDBObject habilidadeResult = new BasicDBObject();
				habilidadeResult.put("habilidadeId", habilidadeDoc.get("id"));
				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
  			if (habilidadeDoc != null) {
  				commons.addObjeto(habilidades, habilidadeResult);
  			};
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		return documento;
	}

	public JSONArray carregaAvaliados(String avaliadorId, String avaliacaoId) {
	
		JSONArray avaliacoesResult = new JSONArray();
		
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "auto-avaliacao", "in");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "superiores", "in");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "superioresOut", "out");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "subordinados", "in");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "subordinadosOut", "out");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "parceiros", "in");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "parceirosOut", "out");
		carregaAvaliados(avaliacoesResult, avaliadorId, avaliacaoId, "clientes", "in");
		
		return avaliacoesResult;
		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void carregaAvaliados(JSONArray avaliacoesResult, String avaliadorId, String avaliacaoId, String tipo, String inout) {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		if (tipo.equals("auto-avaliacao")) {
			key.put("key", "documento.usuarioId");
		}else {
			key.put("key", "documento.avaliacoes." + tipo);
		};
		key.put("value", avaliadorId);
		keysArray.add(key);
		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray);

		JSONArray avaliados = (JSONArray) response.getEntity();
		for (int i = 0; i < avaliados.size(); i++) {
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliados.get(i));
			BasicDBObject avaliacao = getAvaliacao(avaliacaoId, avaliado.get("usuarioId").toString());
			BasicDBObject habilidades = carregaHabilidadesAvaliacao(avaliado.get("usuarioId").toString(), avaliadorId, avaliacaoId); 
			BasicDBObject avaliacaoResult = new BasicDBObject();
			avaliacaoResult.put("tipo", tipo);
			avaliacaoResult.put("inout", inout);
			avaliacaoResult.put("avaliado", avaliacao);
			avaliacaoResult.put("habilidades", habilidades.get("habilidades"));
			avaliacoesResult.add(avaliacaoResult);
		};
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BasicDBObject carregaHabilidadesAvaliacao(String colaboradorId, String avaliadorId, String avaliacaoId) {

		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, colaboradorId);
		
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id"); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, avaliacao.get("empresaId").toString(), avaliacao.get("objetivoId").toString());

		JSONArray habilidades = new JSONArray();
		
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			BasicDBObject habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
			BasicDBObject habilidadeDoc = new BasicDBObject();
			habilidadeDoc.putAll((Map) habilidade.get("documento"));
			if (habilidadeDoc != null) {
				BasicDBObject habilidadeResult = new BasicDBObject();
				habilidadeResult.put("habilidadeId", habilidadeDoc.get("id"));
				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
				BasicDBObject avaliacaoHabilidade = getAvaliacaoHabilidade(avaliacao, habilidadeDoc.get("id").toString(), avaliadorId);
				if (avaliacaoHabilidade != null) {
  				habilidadeResult.put("avaliadorId", avaliacaoHabilidade.get("avaliadorId"));
  				habilidadeResult.put("avaliadorNome", avaliacaoHabilidade.get("avaliadorNome"));
  				habilidadeResult.put("nota", avaliacaoHabilidade.get("nota"));
				};
  			if (habilidadeDoc != null) {
  				commons.addObjeto(habilidades, habilidadeResult);
  			};
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		return documento;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BasicDBObject carregaResultados(BasicDBObject avaliacao) {
		
		ArrayList<Object> resultadosArray = (ArrayList<Object>) avaliacao.get("resultados");

		JSONArray resultados = new JSONArray();
		
		for (int z = 0; z < resultadosArray.size(); z++) {
			BasicDBObject resultado = new BasicDBObject(); 
			resultado.putAll((Map) resultadosArray.get(z)); 
			BasicDBObject habilidade = new BasicDBObject(); 
			habilidade = commons_db.getCollection(resultado.get("id").toString(), "habilidades", "documento.id");
			BasicDBObject habilidadeDoc = new BasicDBObject();
			habilidadeDoc.putAll((Map) habilidade.get("documento"));
			if (habilidade != null) {
				BasicDBObject resultadoResult = new BasicDBObject();
				resultadoResult.put("habilidadeId", habilidadeDoc.get("id"));
				resultadoResult.put("habilidadeNome", habilidadeDoc.get("nome"));
				resultadoResult.put("nota", resultado.get("nota").toString());
				resultados.add(resultadoResult);
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("resultados", resultados);
		return documento;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaAvaliacoes(BasicDBObject avaliacao) {
		
		ArrayList<Object> avaliacoesArray = (ArrayList<Object>) avaliacao.get("habilidades");

		JSONArray avaliacoes = new JSONArray();
		
		for (int z = 0; z < avaliacoesArray.size(); z++) {
			BasicDBObject avaliacaoDoc = new BasicDBObject(); 
			avaliacaoDoc.putAll((Map) avaliacoesArray.get(z)); 
			BasicDBObject habilidade = new BasicDBObject(); 
			habilidade = commons_db.getCollection(avaliacaoDoc.get("id").toString(), "habilidades", "documento.id");
			BasicDBObject habilidadeDoc = new BasicDBObject();
			habilidadeDoc.putAll((Map) habilidade.get("documento"));
			if (habilidade != null) {
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.put("habilidadeId", habilidadeDoc.get("id"));
				avaliacaoResult.put("habilidadeNome", habilidadeDoc.get("nome"));
				avaliacaoResult.put("nota", avaliacaoDoc.get("nota").toString());
				avaliacaoResult.put("avaliadorId", avaliacaoDoc.get("avaliadorId").toString());
  			BasicDBObject usuario = commons_db.getCollection(avaliacaoDoc.get("avaliadorId").toString(), "usuarios", "_id");
  			if (usuario != null) {
  				BasicDBObject usuarioDoc = new BasicDBObject();
  				usuarioDoc.putAll((Map) usuario.get("documento"));
  				avaliacaoResult.put("avaliadorNome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
  			}else {
  				avaliacaoResult.put("avaliadorNome", " ");
  			};
				avaliacoes.add(avaliacaoResult);
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("avaliacoes", avaliacoes);
		return documento;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })	
	private BasicDBObject getAvaliacao(String avaliacaoId, String usuarioId) {
		
		if (avaliacaoId == null) {
			return null;
		};

		String colaboradorNome = "";
		String colaboradorEmail = "";
		BasicDBObject usuario = commons_db.getCollection(usuarioId, "usuarios", "_id");
		if (usuario != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuario.get("documento"));
			colaboradorNome = usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName");
			colaboradorEmail = usuarioDoc.get("email").toString();
		};

		String area = "";
		BasicDBObject usuarioHierarquia = commons_db.getCollection(usuarioId, "hierarquias", "documento.colaborador");
		if (usuarioHierarquia != null) {
			BasicDBObject usuarioHierarquiaDoc = new BasicDBObject();
			usuarioHierarquiaDoc.putAll((Map) usuarioHierarquia.get("documento"));
			area = usuarioHierarquiaDoc.get("area").toString();
		};

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId");
		if (mapa == null) {
			return null;
		};
		BasicDBObject mapaDoc = new BasicDBObject();
		mapaDoc.putAll((Map) mapa.get("documento"));
		
		ArrayList<BasicDBObject> avaliacoes = (ArrayList<BasicDBObject>) mapaDoc.get("avaliacoes");
		BasicDBObject avaliacaoResult = new BasicDBObject();
		avaliacaoResult = null;
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacaoId.equals(avaliacao.get("id").toString())){
				avaliacaoResult = avaliacao;
				avaliacaoResult.put("empresaId", mapaDoc.get("empresaId"));
				avaliacaoResult.put("status", "Pendente");
				avaliacaoResult.put("colaboradorNome", colaboradorNome);
				avaliacaoResult.put("colaboradorId", usuarioId);
				avaliacaoResult.put("colaboradorEmail", colaboradorEmail);
				avaliacaoResult.put("colaboradorArea", area);
				BasicDBObject objetivo = new BasicDBObject(); 
				objetivo.putAll((Map) commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id"));
				avaliacaoResult.put("objetivoNome", "");
				if (objetivo != null) {
					BasicDBObject objetivoDoc = new BasicDBObject();
					objetivoDoc.putAll((Map) objetivo.get("documento"));
					avaliacaoResult.put("objetivoNome", objetivoDoc.get("nome"));
				};
				return avaliacaoResult;
			};
		};
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BasicDBObject getAvaliacaoHabilidade(BasicDBObject avaliacao, String habilidadeId, String avaliadorId) {
		
		if (avaliacao == null) {
			return null;
		};
		ArrayList<Object> habilidades = (ArrayList<Object>) avaliacao.get("habilidades");
		for (int j = 0; j < habilidades.size(); j++) {
			BasicDBObject habilidade = new BasicDBObject();
			habilidade.putAll((Map) habilidades.get(j));
			if (habilidade.get("id").equals(habilidadeId) && habilidade.get("avaliadorId").equals(avaliadorId)) {
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.put("nota", habilidade.get("nota"));
				avaliacaoResult.put("avaliadorId", habilidade.get("avaliadorId"));
  			BasicDBObject usuario = commons_db.getCollection(habilidade.get("avaliadorId").toString(), "usuarios", "_id");
  			if (usuario != null) {
  				BasicDBObject usuarioDoc = new BasicDBObject();
  				usuarioDoc.putAll((Map) usuario.get("documento"));
  				avaliacaoResult.put("avaliadorNome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
  			}else {
  				avaliacaoResult.put("avaliadorNome", " ");
  			};
				return avaliacaoResult;
			};
		};
		return null;
	};

	@SuppressWarnings("rawtypes")
	private void carregaMapa(String avaliacaoId, ArrayList<Object> outArray, ArrayList<String> inArray, String inout) {

		if (inArray != null) {
			for (int i = 0; i < inArray.size(); i++) {
				BasicDBObject registro = commons_db.getCollection(inArray.get(i), "usuarios", "_id");
				if (registro != null) {
					BasicDBObject outObj = new BasicDBObject();
					BasicDBObject docObj = (BasicDBObject) registro.get("documento");
					outObj.put("nome", docObj.get("firstName") + " " + docObj.get("lastName"));
					outObj.put("photo", docObj.get("photo"));
					outObj.put("id", registro.get("_id").toString());
					outObj.put("inout", inout);
					BasicDBObject avaliacao = getAvaliacao(avaliacaoId, registro.get("_id").toString());
					if (avaliacao != null) {
  					String objetivoId = avaliacao.get("objetivoId").toString();
  					BasicDBObject objetivoDoc = new BasicDBObject(); 
  					objetivoDoc.putAll((Map) commons_db.getCollection(objetivoId, "objetivos", "documento.id").get("documento"));
  					outObj.put("objetivo", objetivoDoc.get("nome"));
					};
					outArray.add(outObj);
				};
			};		
		};
	};

	@SuppressWarnings({ "unchecked" })
	public JSONArray colaboradores(String empresaId, String avaliacaoId)  {

		if (avaliacaoId == null){
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
			docOut.put("id", doc.getString("usuarioId"));
			docOut.put("objetivo", getAvaliacao(avaliacaoId, doc.getString("usuarioId")).get("objetivoNome"));
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

		if (avaliacaoId == null){
			return false;
		};
	
		BasicDBObject mapaDoc = new BasicDBObject();
		mapaDoc.putAll((Map) mapa.get("documento"));
		
		ArrayList<BasicDBObject> avaliacoes =  (ArrayList<BasicDBObject>) mapaDoc.get("avaliacoes");
		ArrayList<BasicDBObject> avaliacoesNew =  new ArrayList<BasicDBObject>();
		BasicDBObject avaliacao = new BasicDBObject();
		
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacaoObj = new BasicDBObject();
			avaliacaoObj.putAll((Map) avaliacoes.get(i));
			if (avaliacaoId.equals(avaliacaoObj.get("id").toString())){
				avaliacao = avaliacaoObj;
			}else {
				avaliacoesNew.add(avaliacoes.get(i));
			};
		};
		
		JSONObject arrays = new JSONObject(); 
		
		if (assunto != null) {
			ArrayList<String> array = new ArrayList<>();
			array = (ArrayList<String>) avaliacao.get(assunto);
			if (commons.testaElementoArray(colaboradorObjetoId, array)) {
				arrays.put("out", assunto);
			}else {
				arrays.put("in", assunto);
			};
		}else {
			arrays = localizaColaborador(colaboradorObjetoId, avaliacao);
		};
		
		// *** retira da array origem
		ArrayList<String> arrayOut = new ArrayList<>();
		if (arrays.get("out") != null) {
  		arrayOut = (ArrayList<String>) avaliacao.get(arrays.get("out"));
  		if (arrayOut == null) {
  			return null;
  		};
  		arrayOut = commons.removeString(arrayOut, colaboradorObjetoId);
  		avaliacao.remove(arrays.get("out"));
  		avaliacao.put(arrays.get("out").toString(), arrayOut);
  		avaliacoesNew.add(avaliacao);
		};
		
		// *** coloca na array destino
		ArrayList<String> arrayIn = new ArrayList<>();
		if (arrays.get("in") != null) {
  		if (avaliacao.get(arrays.get("in")) == null) {
  			avaliacao.put(arrays.get("in").toString(), arrayIn);
  		};
  		arrayIn.add(colaboradorObjetoId);
		};
		
		// *** remove habilidade avaliada
		ArrayList<Object> habilidadesNew = new ArrayList<>();
		habilidadesNew = (ArrayList<Object>) avaliacao.get("habilidades");		
		habilidadesNew = removeAvaliacao((ArrayList<Object>) avaliacao.get("habilidades"), colaboradorObjetoId);
		avaliacao.remove("habilidades");
		avaliacao.put("habilidades", habilidadesNew);
		
		// *** atualiza
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
	
	@SuppressWarnings("unchecked")
	private JSONObject localizaColaborador(String colaboradorObjetoId, BasicDBObject avaliacao) {
		JSONObject arrays = new JSONObject();
		
		ArrayList<String> array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get("superiores");
		if (commons.testaElementoArray(colaboradorObjetoId, array)) {
			arrays.put("out", "superiores");
			arrays.put("in", "superioresOut");
		};
		array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get("superioresOut");
		if (commons.testaElementoArray(colaboradorObjetoId, array)) {
			arrays.put("out", "superioresOut");
			arrays.put("in", "superiores");
		};
		array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get("subordinados");
		if (commons.testaElementoArray(colaboradorObjetoId, array)) {
			arrays.put("out", "subordinados");
			arrays.put("in", "subordinadosOut");
		};
		array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get("subordinadosOut");
		if (commons.testaElementoArray(colaboradorObjetoId, array)) {
			arrays.put("out", "subordinadosOut");
			arrays.put("in", "subordinados");
		};
		array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get("parceiros");
		if (commons.testaElementoArray(colaboradorObjetoId, array)) {
			arrays.put("out", "parceiros");
			arrays.put("in", "parceirosOut");
		};
		array = new ArrayList<>();
		array = (ArrayList<String>) avaliacao.get("parceirosOut");
		if (commons.testaElementoArray(colaboradorObjetoId, array)) {
			arrays.put("out", "parceirosOut");
			arrays.put("in", "parceiros");
		};

		return arrays;
	}

	@SuppressWarnings("rawtypes")
	private ArrayList<Object> removeAvaliacao(ArrayList<Object> habilidades, String colaboradorObjetoId) {
		ArrayList<Object> hablidadesNew = new ArrayList<>();
		for (int i = 0; i < habilidades.size(); i++) {
			BasicDBObject habilidade = new BasicDBObject();
			habilidade.putAll((Map) habilidades.get(i));
			if (!habilidade.get("avaliadorId").equals(colaboradorObjetoId)) {
				hablidadesNew.add(habilidade);
			};
		};
		return hablidadesNew;
	}

	@SuppressWarnings({ })
	public Boolean montaHabilidades(String colaboradorId, String empresaId, String avaliacaoId, String habilidadeId)  {
		return null;
/*
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
			BasicDBObject avaliacaoObj = new BasicDBObject();
			avaliacaoObj.putAll((Map) avaliacoes.get(i));
			if (avaliacaoId.equals(avaliacaoObj.get("id").toString())){
				avaliacao = avaliacaoObj;
			}else {
				avaliacoesNew.add(avaliacoes.get(i));
			};
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
		*/	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean atualizaNota(String avaliadorId, String colaboradorId, String habilidadeId, String nota, String avaliacaoId)  {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId");
		if (mapa == null){
			return false;
		};
		BasicDBObject mapaDoc = new BasicDBObject();
		mapaDoc.putAll((Map) mapa.get("documento"));
		
		ArrayList<Object> avaliacoes = (ArrayList<Object>) mapaDoc.get("avaliacoes");
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
				avaliacao.remove("resultados");
				avaliacao.put("resultados", calculaResultados(habilidadesNew));
			};
			avaliacoesNova.add(avaliacao);
		};

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", mapaDoc);

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
	private ArrayList<BasicDBObject> calculaResultados(ArrayList<BasicDBObject> habilidades) {
		
		ArrayList<BasicDBObject> resultados = new ArrayList<>();
		ArrayList<String> habilidadesTratadas = new ArrayList<>();
		
		for (int i = 0; i < habilidades.size(); i++) {
			if (!(commons.testaElementoArray(habilidades.get(i).get("id").toString(), habilidadesTratadas))){
  			int qtde = 0;
  			Double notas = 0.00;
  			for (int j = 0; j < habilidades.size(); j++) {
  				if (habilidades.get(i).get("id").toString().equals(habilidades.get(j).get("id").toString())) {
  					qtde++;
  					notas = notas + Double.valueOf(habilidades.get(j).get("nota").toString());
  				};
  			};
  			BasicDBObject resultado = new BasicDBObject();
  			resultado.put("id", habilidades.get(i).get("id").toString());
  			resultado.put("nota", String.valueOf((notas/qtde)));
  			resultados.add(resultado);
  			habilidadesTratadas.add(habilidades.get(i).get("id").toString());
			};
		};
		return resultados;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<Object> lista(String empresaId)  {
	
		BasicDBObject empresa = new BasicDBObject();
		empresa.putAll((Map) commons_db.getCollection(empresaId, "empresas", "_id"));
		BasicDBObject empresaDoc = new BasicDBObject();
		empresaDoc.putAll((Map) empresa.get("documento"));

		JSONArray avaliacoesResult = new JSONArray();

		String lastAvalId = "";
		if (empresaDoc.get("lastAval") != null) {
  		BasicDBObject avaliacaoLast = new BasicDBObject();
  		avaliacaoLast.putAll((Map) commons_db.getCollection(empresaDoc.get("lastAval").toString(), "avaliacoes", "_id"));
  		BasicDBObject avaliacaoLastDoc = new BasicDBObject();
  		avaliacaoLastDoc.putAll((Map) avaliacaoLast.get("documento"));
  		BasicDBObject avaliacaoResult = new BasicDBObject();
  		avaliacaoResult.put("_id", avaliacaoLast.get("_id").toString());
  		avaliacaoResult.put("documento", avaliacaoLastDoc);
  		avaliacoesResult.add(avaliacaoResult);
  		lastAvalId = avaliacaoLast.get("_id").toString();
		};
		
		ArrayList<Object> avaliacoes = new ArrayList<Object>(); 
		avaliacoes = commons_db.getCollectionLista(empresaId, "avaliacoes", "documento.empresaId");

		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("status") != null){
  			if (avaliacao.get("status").equals("emandamento")){
  				BasicDBObject avaliacaoResult = new BasicDBObject();
  				avaliacaoResult.put("_id", avaliacao.get("_id").toString());
  				avaliacaoResult.put("documento", avaliacao);
  				if (!avaliacao.get("_id").toString().equals(lastAvalId)) {
  					avaliacoesResult.add(avaliacaoResult);
  				};
  			};
			}else {
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.put("_id", avaliacao.get("_id").toString());
				avaliacaoResult.put("documento", avaliacao);
				if (!avaliacao.get("_id").toString().equals(lastAvalId)) {
					avaliacoesResult.add(avaliacaoResult);
				};				
			};
		};	
		return avaliacoesResult;
	};

};
