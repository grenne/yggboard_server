package com.yggboard;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;


public class Avaliacao {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Usuario usuario = new Usuario();
	SendEmailHtml sendEmailHtml = new SendEmailHtml();
	TemplateEmail templateEmail = new TemplateEmail(); 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject criaMapaAvaliacao(String empresaId, String avaliacaoId, MongoClient mongo) {
	
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

//		commons_db.removerCrudMany("mapaAvaliacao", keysArray, mongo, false);
		
		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId", mongo, false);

		JSONArray gestores = new JSONArray();

		for (int i = 0; i < hierarquias.size(); i++) {

			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));

			ArrayList<JSONObject> avaliacoes = new ArrayList<JSONObject>();
			ArrayList<String> areas = new ArrayList<>();
			ArrayList<String> niveis = new ArrayList<>();
			Boolean existeMapa = false;
			BasicDBObject avaliacaoDoc = new BasicDBObject();
			avaliacaoDoc = commons_db.getCollectionDoc(avaliacaoId, "avaliacoes", "_id", mongo, false);
			if (avaliacaoDoc != null){
				areas = (ArrayList<String>) avaliacaoDoc.get("areas");
				niveis= (ArrayList<String>) avaliacaoDoc.get("niveis");
			};
			BasicDBObject mapa = new BasicDBObject();
			mapa = commons_db.getCollection(hierarquia.get("colaborador").toString(), "mapaAvaliacao", "documento.usuarioId", mongo, false);
			BasicDBObject mapaDoc = new BasicDBObject();
			if (mapa != null){
				mapaDoc.putAll((Map) mapa.get("documento"));
				existeMapa = true;
				avaliacoes = (ArrayList<JSONObject>) mapaDoc.get("avaliacoes");
			};
			if (colaboradorValido(hierarquia, areas, niveis)) {
	  			JSONObject avaliacao = new JSONObject();
	  			ArrayList<JSONObject> arrayVazia = new ArrayList<>();
	  			avaliacao.put("id", avaliacaoId);
	  			avaliacao.put("superiores", hierarquia(hierarquia.get("colaborador").toString(), "colaborador", "superior", hierarquia.get("colaborador").toString(), areas, niveis, empresaId, mongo));
	  			avaliacao.put("superioresOut", arrayVazia);
	  			avaliacao.put("subordinados", hierarquia(hierarquia.get("colaborador").toString(), "superior", "colaborador", hierarquia.get("colaborador").toString(), niveis, niveis, empresaId, mongo));
	  			avaliacao.put("subordinadosOut", arrayVazia);
	  			avaliacao.put("parceiros", hierarquia(hierarquia.get("superior").toString(), "superior","colaborador", hierarquia.get("colaborador").toString(), niveis, niveis, empresaId, mongo));
	  			avaliacao.put("parceirosOut", arrayVazia);
	  			avaliacao.put("clientes", hierarquia(hierarquia.get("colaborador").toString(), "clientes", "colaborador", hierarquia.get("colaborador").toString(), niveis, niveis, empresaId, mongo));
	  			avaliacao.put("objetivoId", hierarquia.get("objetivoId").toString());
	  			avaliacao.put("habilidades", arrayVazia);
	  			avaliacao.put("habilidadesOut", arrayVazia);
	  			avaliacao.put("resultados", arrayVazia);
	  			avaliacao.put("clientesConvitesAceitos", arrayVazia);
	  			avaliacao.put("clientesConvitesRecusados", arrayVazia);
				if (testaAvaliacaoFechada(avaliacaoId, mongo)){
					avaliacao.put("status", "mapa_fechado");
				}else {
					avaliacao.put("status", "mapa_aberto");
				};
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
	  				commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, null, mongo, false);
	  			}else {
	  				commons_db.incluirCrud("mapaAvaliacao", criaMapaDoc(hierarquia.get("colaborador").toString(), empresaId, avaliacoesNew), mongo, false);
	  			};
	  			carregaGestores((ArrayList<String>) avaliacao.get("superiores"), gestores, mongo);
			};
		};
		
		
		JSONObject result = new JSONObject();
		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
		BasicDBObject avaliacaoDoc = new BasicDBObject();
		avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
		
		result.put("avaliacaoNome", avaliacaoDoc.get("nome"));
		result.put("dataConclusao", avaliacaoDoc.get("dataConclusao"));
		result.put("dataMapa", avaliacaoDoc.get("dataEnvio").toString());
		result.put("gestores", gestores);
		return result;
		
	};

	@SuppressWarnings({ "rawtypes" })
	private void carregaGestores(ArrayList<String> superiores, JSONArray gestores, MongoClient mongo) {
		for (int i = 0; i < superiores.size(); i++) {
			BasicDBObject superior = new BasicDBObject();
			BasicDBObject usuario = commons_db.getCollection(superiores.get(i).toString(), "usuarios", "_id", mongo, false);
			if (usuario != null) {
				BasicDBObject usuarioDoc = new BasicDBObject();
				usuarioDoc.putAll((Map) usuario.get("documento"));
				if (usuarioDoc != null) {
					superior.put("gestorNome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
					superior.put("gestorEmail", usuarioDoc.get("email"));
					commons.addObjeto(gestores, superior);
				};
			}
		};
	};

	private boolean colaboradorValido(BasicDBObject hierarquia, ArrayList<String> areas, ArrayList<String> niveis) {
				
		if (areas.size() > 0) {
			if (!commons.testaElementoArray(hierarquia.get("area").toString(), areas)) {
				return false;
			}
		};
		if (niveis.size() > 0) {
			if (!commons.testaElementoArray(hierarquia.get("nivel").toString(), niveis)) {
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
	private ArrayList<String> hierarquia(String usuarioId, String tipo, String resultado, String colaboradorId, ArrayList<String> areas, ArrayList<String> niveis, String empresaId, MongoClient mongo) {

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(usuarioId, "hierarquias", "documento." + tipo, mongo, false);

		JSONArray arrayColaboradores = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			if (!colaboradorId.equals(hierarquia.get(resultado).toString()) && !hierarquia.get(resultado).toString().equals("") && hierarquia.get("empresaId").toString().equals(empresaId)) {
				if (colaboradorValido(hierarquia, areas, niveis)) {				
					commons.addString(arrayColaboradores, hierarquia.get(resultado).toString());
				};
			};
		};
		return arrayColaboradores;
	};
	
	@SuppressWarnings({ "unchecked" })
	public BasicDBObject mapa(String usuarioId, String empresaId, String avaliacaoId, MongoClient mongo)  {
				
		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, usuarioId, mongo);
		
		if (avaliacao == null) {
			return null;
		};

		ArrayList<String> objArray = new ArrayList<String>();

		ArrayList<Object> superioresArray =  new ArrayList<Object>();
		objArray = (ArrayList<String>) avaliacao.get("superiores");
		carregaMapa (avaliacaoId, superioresArray, objArray,"in", mongo);

		objArray = (ArrayList<String>) avaliacao.get("superioresOut");
		carregaMapa (avaliacaoId, superioresArray, objArray,"out", mongo);
		
		ArrayList<Object> parceirosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("parceiros");
		carregaMapa (avaliacaoId, parceirosArray, objArray,"in", mongo);
		
		objArray = (ArrayList<String>) avaliacao.get("parceirosOut");
		carregaMapa (avaliacaoId, parceirosArray, objArray,"out", mongo);

		ArrayList<Object> subordinadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("subordinados");
		carregaMapa (avaliacaoId, subordinadosArray, objArray,"in", mongo);

		objArray = (ArrayList<String>) avaliacao.get("subordinadosOut");
		carregaMapa (avaliacaoId, subordinadosArray, objArray,"out", mongo);
		
		ArrayList<Object> clientesArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientes");
		carregaMapa (avaliacaoId, clientesArray, objArray,"in", mongo);
		
		ArrayList<Object> clientesConvitesAceitosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientesConvitesAceitos");
		carregaMapa (avaliacaoId, clientesConvitesAceitosArray, objArray,"in", mongo);
		
		ArrayList<Object> clientesConvitesRecusadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientesConvitesRecusados");
		carregaMapa (avaliacaoId, clientesConvitesRecusadosArray, objArray,"in", mongo);

		BasicDBObject documentos = new BasicDBObject();
		
		documentos.put("superiores", superioresArray);
		documentos.put("parceiros", parceirosArray);
		documentos.put("subordinados", subordinadosArray);
		documentos.put("clientes", clientesArray);
		documentos.put("clientesConvitesAceitos", clientesConvitesAceitosArray);
		documentos.put("clientesConvitesRecusados", clientesConvitesRecusadosArray);
		if (avaliacao != null) {
	  		documentos.put("habilidades", carregaHabilidades(avaliacao, empresaId, usuarioId, mongo).get("habilidades"));
	  		documentos.put("avaliacoes", carregaAvaliacoes(avaliacao, mongo).get("avaliacoes"));
	  		documentos.put("objetivo", avaliacao.get("objetivoNome"));
		};
		return documentos;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaHabilidades(BasicDBObject avaliacao, String empresaId, String usuarioId, MongoClient mongo) {
		
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, empresaId, avaliacao.getString("objetivoId").toString(), mongo);
		ArrayList<String> habilidadesOut = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			habilidadesOut = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};
		JSONArray habilidades = new JSONArray();
		
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			BasicDBObject habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id", mongo, false);
			BasicDBObject habilidadeDoc = new BasicDBObject();
			if (habilidade != null) {
  			habilidadeDoc.putAll((Map) habilidade.get("documento"));
  			if (habilidadeDoc != null) {
  				BasicDBObject habilidadeResult = new BasicDBObject();
  				habilidadeResult.put("habilidadeId", habilidadeDoc.get("id"));
  				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
  				habilidadeResult.put("habilidadeDescricao", habilidadeDoc.get("descricao"));
  				if (commons.testaElementoArray(habilidadeDoc.get("id").toString(), habilidadesOut)) {
  					habilidadeResult.put("habilidadeInout", "");
  				}else {
  					habilidadeResult.put("habilidadeInout", "checked");
  				};
    			if (habilidadeDoc != null) {
    				commons.addObjeto(habilidades, habilidadeResult);
    			};
  			};
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		return documento;
	}

	public JSONArray carregaAvaliados(String avaliadorId, String avaliacaoId, MongoClient mongo) {
	
		JSONArray avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "auto-avaliacao", "in", mongo);
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "superiores", "in", mongo);
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "subordinados", "in", mongo);
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "parceiros", "in", mongo);
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "clientesConvitesAceitos", "in", mongo);
		
		return avaliacoesResult;
		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void carregaAvaliadosResult(JSONArray avaliacoesResult, String avaliadorId, String avaliacaoId, String tipo, String inout, MongoClient mongo) {

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

		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);

		JSONArray avaliados = (JSONArray) response.getEntity();
		for (int i = 0; i < avaliados.size(); i++) {
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliados.get(i));
			BasicDBObject avaliacao = getAvaliacao(avaliacaoId, avaliado.get("usuarioId").toString(), mongo);
			if (tipo.equals("auto-avaliacao")) {
	  			if (avaliacao.get("id").equals(avaliacaoId)) {
	    			BasicDBObject habilidades = carregaHabilidadesAvaliacao(avaliado.get("usuarioId").toString(), avaliadorId, avaliacaoId, avaliacao, mongo); 
	    			BasicDBObject avaliacaoResult = new BasicDBObject();
	    			avaliacaoResult.put("tipo", tipo);
	    			avaliacaoResult.put("status", habilidades.get("status"));
	    			avaliacaoResult.put("inout", inout);
	    			avaliacao.remove("resultados");
	    			avaliacaoResult.put("avaliado", avaliacao);
	    			avaliacaoResult.put("habilidades", habilidades.get("habilidades"));
	    			avaliacoesResult.add(avaliacaoResult);
	  			};				
			}else{
	  			ArrayList<String> array = (ArrayList<String>) avaliacao.get(tipo);
	  			if (avaliacao.get("id").equals(avaliacaoId) && commons.testaElementoArray(avaliadorId, array)) {
	    			BasicDBObject habilidades = carregaHabilidadesAvaliacao(avaliado.get("usuarioId").toString(), avaliadorId, avaliacaoId, avaliacao, mongo); 
	    			BasicDBObject avaliacaoResult = new BasicDBObject();
	    			avaliacaoResult.put("tipo", tipo);
	    			avaliacaoResult.put("status", habilidades.get("status"));
	    			avaliacaoResult.put("inout", inout);
	    			avaliacao.remove("resultados");
	    			avaliacaoResult.put("avaliado", avaliacao);
	    			avaliacaoResult.put("habilidades", habilidades.get("habilidades"));
	    			avaliacoesResult.add(avaliacaoResult);
	  			};
			};
		};
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BasicDBObject carregaHabilidadesAvaliacao(String colaboradorId, String avaliadorId, String avaliacaoId, BasicDBObject avaliacao2, MongoClient mongo) {

		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, colaboradorId, mongo);
		ArrayList<String> habilidadesOut = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			habilidadesOut = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, avaliacao.get("empresaId").toString(), avaliacao.get("objetivoId").toString(), mongo);

		JSONArray habilidades = new JSONArray();
		
		String status = "concluído";
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			if (!commons.testaElementoArray(habilidadesArray.get(z), habilidadesOut)) {
	  			BasicDBObject habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id", mongo, false);
	  			if (habilidade != null) {
		  			BasicDBObject habilidadeDoc = new BasicDBObject();
		  			habilidadeDoc.putAll((Map) habilidade.get("documento"));
		  			if (habilidadeDoc != null) {
		  				BasicDBObject habilidadeResult = new BasicDBObject();
		  				habilidadeResult.put("habilidadeId", habilidadeDoc.get("id"));
		  				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
		  				habilidadeResult.put("habilidadeDescricao", habilidadeDoc.get("descricao"));
		  				BasicDBObject avaliacaoHabilidade = getAvaliacaoHabilidade(avaliacao, habilidadeDoc.get("id").toString(), avaliadorId, mongo);
		  				if (avaliacaoHabilidade != null) {
		    				habilidadeResult.put("avaliadorId", avaliacaoHabilidade.get("avaliadorId"));
		    				habilidadeResult.put("avaliadorNome", avaliacaoHabilidade.get("avaliadorNome"));
		    				habilidadeResult.put("nota", avaliacaoHabilidade.get("nota"));
		  				}else {
		  					status = "pendente";
		  				}
		    			if (habilidadeDoc != null) {
		    				commons.addObjeto(habilidades, habilidadeResult);
		    			};
		  			};
	  			};
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		documento.put("status", status);
		return documento;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaAvaliacoes(BasicDBObject avaliacao, MongoClient mongo) {
		
		ArrayList<Object> habilidadesId = (ArrayList<Object>) avaliacao.get("habilidadesId");
		ArrayList<Object> habilidadesNome = (ArrayList<Object>) avaliacao.get("habilidades");
		ArrayList<Object> notas = (ArrayList<Object>) avaliacao.get("notas");
		ArrayList<Object> avaliadoresId = (ArrayList<Object>) avaliacao.get("avaliadoresId");
		ArrayList<Object> avaliadoresNome = (ArrayList<Object>) avaliacao.get("avaliadores");
		ArrayList<String> habilidadesOut = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			habilidadesOut = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};

		JSONArray avaliacoes = new JSONArray();
		
		for (int z = 0; z < habilidadesId.size(); z++) {
			if (!commons.testaElementoArray(habilidadesId.get(z).toString(), habilidadesOut)) {
  				BasicDBObject avaliacaoResult = new BasicDBObject();
  				avaliacaoResult.put("habilidadeId", habilidadesId.get(z).toString());
  				avaliacaoResult.put("habilidadeNome", habilidadesNome.get(z).toString());
  				avaliacaoResult.put("nota", notas.get(z).toString());
  				avaliacaoResult.put("avaliadorId", avaliadoresId.get(z).toString());
   				avaliacaoResult.put("avaliadorNome", avaliadoresNome.get(z).toString());
  				avaliacoes.add(avaliacaoResult);
			};
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("avaliacoes", avaliacoes);
		return documento;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject convites(String empresaId, String avaliacaoId, String usuarioId, MongoClient mongo) {
		
		
		JSONArray avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "superiores", "in", mongo);

		JSONArray convitesEnviadosPendentes = new JSONArray();
		JSONArray convitesEnviadosExpirados = new JSONArray();
		JSONArray convitesEnviadosAceitos = new JSONArray();
		JSONArray convitesEnviadosRecusados = new JSONArray();			
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			if (avaliacaoEncerrada(avaliacaoId, mongo)) {
				convitesEnviadosExpirados = carregaConvites(convitesEnviadosExpirados, avaliacaoId, avaliado, "clientes", mongo);
			}else {	
				convitesEnviadosPendentes = carregaConvites(convitesEnviadosPendentes, avaliacaoId, avaliado, "clientes", mongo);
			};
			convitesEnviadosAceitos = carregaConvites(convitesEnviadosAceitos, avaliacaoId, avaliado,  "clientesConvitesAceitos", mongo);
			convitesEnviadosRecusados = carregaConvites(convitesEnviadosRecusados, avaliacaoId, avaliado,  "clientesConvitesRecusados", mongo);			
		};

		avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "clientes", "in", mongo);

		JSONArray convitesRecebidosPendentes = new JSONArray();
		JSONArray convitesRecebidosExpirados = new JSONArray();
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			BasicDBObject convite = montaConvite(avaliado, usuarioId, mongo);
			if (avaliacaoEncerrada(avaliacaoId, mongo)) {
				convitesRecebidosExpirados.add(convite);
			}else {
				convitesRecebidosPendentes.add(convite);
			};
		};

		avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "clientesConvitesAceitos", "in", mongo);

		JSONArray convitesRecebidosAceitos = new JSONArray();
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			BasicDBObject convite = montaConvite(avaliado, usuarioId, mongo);
			convitesRecebidosAceitos.add(convite);
		};

		avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "clientesConvitesRecusados", "in", mongo);

		JSONArray convitesRecebidosRecusados = new JSONArray();
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			BasicDBObject convite = montaConvite(avaliado, usuarioId, mongo);
			convitesRecebidosRecusados.add(convite);
		};
		
		JSONObject convites = new JSONObject();
		
		convites.put("convitesEnviadosPendentes", convitesEnviadosPendentes);
		convites.put("convitesEnviadosExpirados", convitesEnviadosExpirados);
		convites.put("convitesEnviadosAceitos", convitesEnviadosAceitos);
		convites.put("convitesEnviadosRecusados", convitesEnviadosRecusados);
		convites.put("convitesRecebidosPendentes", convitesRecebidosPendentes);
		convites.put("convitesRecebidosExpirados", convitesRecebidosExpirados);
		convites.put("convitesRecebidosAceitos", convitesRecebidosAceitos);
		convites.put("convitesRecebidosRecusados", convitesRecebidosRecusados);
		
		return convites;
		
	};

	@SuppressWarnings("rawtypes")
	private boolean avaliacaoEncerrada(String avaliacaoId, MongoClient mongo) {
		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
		if (avaliacao != null) {
			BasicDBObject avaliacaoDoc = new BasicDBObject();
			avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
			if (avaliacaoDoc.get("dataConclusao") != null) {
				if (commons.calcTime(avaliacaoDoc.get("dataConclusao").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
					return true;
				};
			};
		};
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject emailsFechamento(MongoClient mongo) {
		
		JSONObject result = new JSONObject();
		JSONArray avaliacoes = commons_db.getCollectionListaNoKey("avaliacoes", mongo, false);
		JSONArray resultArray = new JSONArray();
		Boolean enviouEmail = false;
		result.put("Resultado", "Emails enviados");
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("dataEnvio") != null) {
				if (commons.calcTime(avaliacao.get("dataEnvio").toString().replace("-", "")).equals(commons.calcTime(commons.calcDate(-1, "yyyymmdd")))) {
					testaMapaAvaliacao(avaliacao.get("_id").toString(), false, "inicio-avaliacao", avaliacao, resultArray, mongo);
					enviouEmail = true;
				};
				if (commons.calcTime(avaliacao.get("dataConclusao").toString().replace("-", "")).equals(commons.calcTime(commons.calcDate(-1, "yyyymmdd")))) {
					testaMapaAvaliacao(avaliacao.get("_id").toString(), false, "conclusao-avaliacao", avaliacao, resultArray, mongo);
					enviouEmail = true;
				};
			};
		};
		if (!enviouEmail) {
			result.put("Resultado", " Sem emails para enviar");
		}
	
		result.put("Emails", resultArray);
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked",  })
	private void testaMapaAvaliacao(String idAvaliacao, Boolean gestores, String tipo, BasicDBObject avaliacao, JSONArray resultArray, MongoClient mongo) {
				
		JSONArray mapasAvaliacao = commons_db.getCollectionLista(idAvaliacao, "mapaAvaliacao", "documento.avaliacoes.id", mongo, false);
		ArrayList<String> emailsEnviados = new ArrayList<>();
		
		for (int i = 0; i < mapasAvaliacao.size(); i++) {
			BasicDBObject mapaAvaliacao = new BasicDBObject();
			mapaAvaliacao.putAll((Map) mapasAvaliacao.get(i));
			ArrayList<Object> avaliacoes = (ArrayList<Object>) mapaAvaliacao.get("avaliacoes");
			if (avaliacoes != null) {
				for (int j = 0; j < avaliacoes.size(); j++) {
					BasicDBObject avaliacaoMapa = new BasicDBObject();
					avaliacaoMapa.putAll((Map) avaliacoes.get(j));
					if (idAvaliacao.equals(avaliacao.get("_id"))){
						Boolean enviarEmail = false;
						if (gestores) {
							ArrayList<Object> subordinados = (ArrayList<Object>) avaliacaoMapa.get("subordinados");
							if (subordinados.size() != 0) {
								enviarEmail = true;
							}
						}else {
							enviarEmail = true;
						};
						if (enviarEmail) {
							enviarEmailAvaliacao(mapaAvaliacao.get("usuarioId").toString(), avaliacao, tipo, resultArray, emailsEnviados, mongo);
						};
					};
				}
			};
		};
		
	};

	private void enviarEmailAvaliacao(String usuarioId, BasicDBObject avaliacao, String tipo, JSONArray resultArray, ArrayList<String> emailsEnviados, MongoClient mongo) {
			
		BasicDBObject usuario = commons_db.getCollectionDoc(usuarioId, "usuarios", "_id", mongo, false);
		
		if (usuario == null){
			return;
		};

		switch (tipo) {
		case "inicio-avaliacao":
			emailInicioAvaliacao ("Yggboard - Inicie suas avaliações, conclua até " + avaliacao.get("dataConclusao"), usuario, avaliacao, resultArray, emailsEnviados);
			break;
		case "conclusao-avaliacao":
			emailConclusaoAvaliacao ("Yggboard - Veja os resultados da avaliação " + avaliacao.get("nome"), usuario, avaliacao, resultArray, emailsEnviados );
			break;
		default:
			break;
		};
	};

	@SuppressWarnings("unchecked")
	private void emailInicioAvaliacao(String subject, BasicDBObject usuario, BasicDBObject avaliacao, JSONArray resultArray, ArrayList<String> emailsEnviados) {

		String conteudo = "<h1>Olá, <b>" + usuario.get("firstName") + " " + usuario.get("lastName") + "<b></h1>";
				conteudo = conteudo + "<p>Foi iniciado o período de avaliação da <b>\"" + avaliacao.get("nome") + "<b> em Yggboard.</p>";
				conteudo = conteudo + "<p>Por favor complete as avaliações dos colaboaradoes listados em seu painel de avaliações até o dia " + avaliacao.get("dataConclusao") + ".</p>";
				conteudo = conteudo + "<p>Para avaliar simplesmente atribua o conceito de domínio de habilidade que mais se aproxima da realidade.</p>";
				conteudo = conteudo + "<p>Acesse as avaliações clicando na link abaixo.</p><br>";
				conteudo = conteudo + "<p><a href=\"" + commons.getProperties().get("host").toString() + "dashboard/?page=empresa#avaliacao\" target=\"_blank\" title=\"Mapa de avaliações\">Acesse aqui</a></p><br>";
				
		if (!commons.testaElementoArray(usuario.get("email").toString(), emailsEnviados)) {
			sendEmailHtml.sendEmailHtml(usuario.get("email").toString(), subject, templateEmail.emailYggboard(conteudo));		
			resultArray.add("Inicio avaliação-" + avaliacao.get("nome") + " " + usuario.get("firstName") + " " + usuario.get("lastName"));
			emailsEnviados.add(usuario.get("email").toString());
		};
			
	};

	@SuppressWarnings("unchecked")
	private void emailConclusaoAvaliacao(String subject, BasicDBObject  usuario, BasicDBObject avaliacao, JSONArray resultArray, ArrayList<String> emailsEnviados) {

		String conteudo = "<h1>Olá, <b>" + usuario.get("firstName") + " " + usuario.get("lastName") + "<b></h1>";
				conteudo = conteudo + "<p>Foi encerrado o ciclo da avaliação <b>\"" + avaliacao.get("nome") + "<b>.</p>";
				conteudo = conteudo + "<p>Para checar seus resultados por favor clique no link abaico.</p>";
				conteudo = conteudo + "<p>Obrigado por participar de mais im ciclo avaliação de habilidades em Yggboard.</p><br>";
				conteudo = conteudo + "<div style=\"margin-left:15px;\"><!--[if mso]><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"" +  commons.getProperties().get("host").toString() +  "dashboard/?page=empresa#avaliacao\" style=\"height:35px;v-text-anchor:middle;width:200px;\" arcsize=\"12%\" stroke=\"f\" fill=\"t\"><v:fill type=\"tile\" src=\"https://www.yggboard.com/emkt/btn.jpg\" color=\"#79bd58\" /><w:anchorlock/><center style=\"color:#ffffff;font-family:sans-serif;font-size:13px;font-weight:bold;\">Acessar avaliação</center></v:roundrect><![endif]--><a href=\"" +  commons.getProperties().get("host").toString() +  "dashboard/?page=empresa#avaliacao\" target=\"_blank\" style=\"background-color:#79bd58;background-image:url(https://www.yggboard.com/emkt/btn.jpg);border-radius:4px;color:#ffffff;display:inline-block;font-family:sans-serif;font-size:13px;font-weight:bold;line-height:35px;text-align:center;text-decoration:none;width:200px;-webkit-text-size-adjust:none;mso-hide:all;\">Acessar avaliação</a></div>";
		
		if (!commons.testaElementoArray(usuario.get("email").toString(), emailsEnviados)) {
			sendEmailHtml.sendEmailHtml(usuario.get("email").toString(), subject, templateEmail.emailYggboard(conteudo));
			resultArray.add("Conclusáo avaliação-" + avaliacao.get("nome") + " " + usuario.get("firstName") + " " + usuario.get("lastName"));
			emailsEnviados.add(usuario.get("email").toString());
		};
		
	};

	@SuppressWarnings({ "unchecked" })
	private JSONArray carregaConvites(JSONArray arrayResult, String avaliacaoId, BasicDBObject avaliacao, String arrayNome, MongoClient mongo) {
		
		ArrayList<String> convites = (ArrayList<String>) avaliacao.get(arrayNome);
		for (int i = 0; i < convites.size(); i++) {
			if (avaliacao.get("id").equals(avaliacaoId)) {
				if (!testaAvaliacaoFechada(avaliacaoId, mongo)){
					BasicDBObject convite = montaConvite(avaliacao, convites.get(i), mongo);
    			arrayResult.add(convite);
  			};
			};
		};
		
		return arrayResult;
	};

	private BasicDBObject montaConvite(BasicDBObject avaliacao, String avaliadorId, MongoClient mongo) {
		
		Usuario usuario = new Usuario();
		
		BasicDBObject convite = new BasicDBObject();
		BasicDBObject avaliado = new BasicDBObject();
		avaliado.put("nome", avaliacao.get("colaboradorNome"));
		avaliado.put("email", avaliacao.get("colaboradorEmail"));
		avaliado.put("area", avaliacao.get("colaboradorArea"));
		avaliado.put("id", avaliacao.get("colaboradorId"));
		convite.put("avaliado", avaliado);
		BasicDBObject avaliador = new BasicDBObject();
		avaliador.put("nome", usuario.get(avaliadorId, mongo).get("nome"));
		avaliador.put("email", usuario.get(avaliadorId, mongo).get("email"));
		avaliador.put("id", avaliadorId);
		convite.put("avaliador", avaliador);
		return convite;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject resultados(String usuarioId, String avaliacaoId, MongoClient mongo) {

		JSONObject result = new JSONObject();
		
		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, usuarioId, mongo);
		if (avaliacao != null) {
			result.put("meusresultados", carregaResultadosHabilidades(usuarioId, avaliacao, mongo));
		};
		
		JSONArray subordinados = getSubordinados(usuarioId, avaliacaoId, mongo);

		JSONArray subordinadosResult = new JSONArray();
		for (int i = 0; i < subordinados.size(); i++) {
			avaliacao = getAvaliacao(avaliacaoId, usuarioId, mongo);
			if (avaliacao != null) {
				BasicDBObject subordinadoResult = new  BasicDBObject();
				BasicDBObject subordinado = new  BasicDBObject();
				subordinado.putAll((Map) subordinados.get(i));
				subordinadoResult.put("id", subordinado.get("id"));
				subordinadoResult.put("nome", subordinado.get("nome"));
				avaliacao = getAvaliacao(avaliacaoId, subordinado.get("id").toString(), mongo);
				if (avaliacao != null) {
					subordinadoResult.put("resultados", carregaResultadosHabilidades(usuarioId, avaliacao, mongo));
				};
				subordinadosResult.add(subordinadoResult);
			};			
		};

		result.put("subordinados", subordinadosResult);

		return result;		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject ultimosResultados(String usuarioId, MongoClient mongo) {

		JSONObject result = new JSONObject();
		
		ArrayList<Object> mapaAvaliacoes = commons_db.getCollectionLista(usuarioId,"mapaAvaliacao", "documento.usuarioId", mongo, false);
		ArrayList<Object> resultadosResult = new ArrayList<>();
		String dataConclusao = "0000-00-00"; 
		
		for (int i = 0; i < mapaAvaliacoes.size(); i++) {
			BasicDBObject mapaAvaliacao = new BasicDBObject();
			mapaAvaliacao.putAll((Map) mapaAvaliacoes.get(i));
			ArrayList<Object> avaliacoesResult = (ArrayList<Object>) mapaAvaliacao.get("avaliacoes");
			for (int j = 0; j < avaliacoesResult.size(); j++) {
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.putAll((Map) avaliacoesResult.get(j));
				ArrayList<Object> resultados = (ArrayList<Object>) avaliacaoResult.get("resultados");
				if (resultados.size() != 0) {
					BasicDBObject avaliacao = commons_db.getCollectionDoc(avaliacaoResult.get("id").toString(), "avaliacoes", "_id", mongo, false);
					if (avaliacao != null) {
						if (Integer.valueOf(avaliacao.get("dataConclusao").toString().replace("-", "")) > Integer.valueOf(dataConclusao.replace("-", ""))) {
							resultadosResult = resultados;
						};
					};
				};
			};
		};

		ArrayList<Object> resultadosFinalResult = new ArrayList<>();
		
		for (int i = 0; i < resultadosResult.size(); i++) {
			BasicDBObject resultado = new BasicDBObject();
			resultado.putAll((Map) resultadosResult.get(i));
			BasicDBObject habilidade = commons_db.getCollectionDoc(resultado.get("id").toString(), "habilidades", "documento.id", mongo, false);
			if (habilidade != null) {
				resultado.put("nome", habilidade.get("nome"));
				resultadosFinalResult.add(resultado);
			};
		};

		result.put("resultados", resultadosFinalResult);

		return result;		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONArray getSubordinados(String usuarioId, String avaliacaoId, MongoClient mongo) {
		
		Usuario usuario = new Usuario();
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.avaliacoes.superiores");
		key.put("value", usuarioId);
		keysArray.add(key);
		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);

		JSONArray subordinados = new JSONArray();

		JSONArray mapas = (JSONArray) response.getEntity();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = new BasicDBObject();
			mapa.putAll((Map) mapas.get(i));
			if (mapa.get("usuarioId") != null) {
  			BasicDBObject avaliacao = getAvaliacao(avaliacaoId, mapa.get("usuarioId").toString(), mongo);
  			if (avaliacao.get("id").equals(avaliacaoId)) {
  					BasicDBObject subordinado = new BasicDBObject();
  					subordinado.put("id", mapa.get("usuarioId"));
  					subordinado.put("nome", usuario.get(mapa.get("usuarioId").toString(), mongo).get("nome"));
       			subordinados.add(subordinado);
     		};
			};
 		};
		return subordinados;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONArray carregaResultadosHabilidades(String usuarioId, BasicDBObject avaliacao, MongoClient mongo) {
		
		ArrayList<Object> resultadosArray = (ArrayList<Object>) avaliacao.get("resultados");
		ArrayList<Object> resultadosNotaArray = (ArrayList<Object>) avaliacao.get("resultadosNota");

		JSONArray resultados = new JSONArray();
		
		for (int z = 0; z < resultadosArray.size(); z++) {
			BasicDBObject habilidade = new BasicDBObject(); 
			habilidade = commons_db.getCollection(resultadosArray.get(z).toString(), "habilidades", "documento.id", mongo, false);
			BasicDBObject habilidadeDoc = new BasicDBObject();
			if (habilidade != null) {
	  			habilidadeDoc.putAll((Map) habilidade.get("documento"));
	  			if (habilidade != null) {
	  				BasicDBObject resultadoResult = new BasicDBObject();
	  				resultadoResult.put("habilidadeId", habilidadeDoc.get("id"));
	  				resultadoResult.put("habilidadeNome", habilidadeDoc.get("nome"));
	  				resultadoResult.put("nota", resultadosNotaArray.get(z).toString());
	  				resultadoResult.put("autoAvaliacao", mediaNotas(usuarioId, avaliacao, habilidadeDoc.get("id").toString(), "autoAvaliacao"));
	  				resultadoResult.put("mediaSubordinados", mediaNotas(usuarioId, avaliacao, habilidadeDoc.get("id").toString(), "subordinados"));
	  				resultadoResult.put("mediaSuperiores", mediaNotas(usuarioId, avaliacao, habilidadeDoc.get("id").toString(), "superiores"));
	  				resultadoResult.put("mediaParceiros", mediaNotas(usuarioId, avaliacao, habilidadeDoc.get("id").toString(), "parceiros"));
	  				resultadoResult.put("mediaClientes", mediaNotas(usuarioId, avaliacao, habilidadeDoc.get("id").toString(), "clientes"));
	  				resultados.add(resultadoResult);
	  			};
			}else {
				System.out.println("habilidade inexistente - " + resultadosArray.get(z).toString());
			};
		};

		return resultados;
		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String mediaNotas(String usuarioId, BasicDBObject avaliacao, String habilidadeId, String relacionamento) {
		
		ArrayList<String> resultadosArray = (ArrayList<String>) avaliacao.get(relacionamento);
		if (relacionamento.equals("autoAvaliacao")) {
			resultadosArray = new ArrayList<>();
			resultadosArray.add(usuarioId);
		};
		ArrayList<Object> habilidades = (ArrayList<Object>) avaliacao.get("habilidadesId");
		ArrayList<Object> notas = (ArrayList<Object>) avaliacao.get("notas");
		ArrayList<Object> avaliadores = (ArrayList<Object>) avaliacao.get("avaliadoresId");
		System.out.println("habilidadeId:" + habilidadeId);
		System.out.println("habilidadesId:" + habilidades);
		System.out.println("avaliadores:" + avaliadores);
		System.out.println("notas:" + notas);
		int qtde = 0;
		double totalNotas = 0.00;

		if (habilidades != null) {
			for (int z = 0; z < resultadosArray.size(); z++) {
				for (int i = 0; i < habilidades.size(); i++) {
					if (avaliadores.get(i).equals(resultadosArray.get(z)) && habilidades.get(i).equals(habilidadeId)) {
						totalNotas = totalNotas + Double.valueOf(notas.get(i).toString());
						qtde++;					
					};
				};
			};
		};
		
		if (qtde != 0 ) {
			DecimalFormat df = new DecimalFormat("#0.00"); 
			return String.valueOf(df.format(totalNotas / qtde));
		}else {
			return "0.00";
		}
	}

	@SuppressWarnings({ "rawtypes" })	
	private BasicDBObject getMapa(String usuarioId, MongoClient mongo) {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
		if (mapa == null) {
			return null;
		};
		BasicDBObject mapaDoc = new BasicDBObject();
		if (mapa.get("documento") == null) {
			return null;
		};
		mapaDoc.putAll((Map) mapa.get("documento"));
		
		return mapaDoc;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })	
	private BasicDBObject getAvaliacao(String avaliacaoId, String usuarioId, MongoClient mongo) {
		
		if (avaliacaoId == null) {
			return null;
		};

		String colaboradorNome = "";
		String colaboradorEmail = "";
		BasicDBObject usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);
		if (usuario != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuario.get("documento"));
			colaboradorNome = usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName");
			colaboradorEmail = usuarioDoc.get("email").toString();
		};

		String area = "";
		BasicDBObject usuarioHierarquia = commons_db.getCollection(usuarioId, "hierarquias", "documento.colaborador", mongo, false);
		if (usuarioHierarquia != null) {
			BasicDBObject usuarioHierarquiaDoc = new BasicDBObject();
			usuarioHierarquiaDoc.putAll((Map) usuarioHierarquia.get("documento"));
			area = usuarioHierarquiaDoc.get("area").toString();
		};

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
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
				avaliacaoResult.put("colaboradorNome", colaboradorNome);
				avaliacaoResult.put("colaboradorId", usuarioId);
				avaliacaoResult.put("colaboradorEmail", colaboradorEmail);
				avaliacaoResult.put("colaboradorArea", area);
				ArrayList<Object> resultados = (ArrayList<Object>) avaliacao.get("resultados");
				avaliacaoResult.put("resultadosNota", getResultadoNota(resultados, mongo));
				avaliacaoResult.put("resultados", getResultado(resultados, mongo));
				avaliacaoResult.put("resultadosObj", resultados);
				avaliacaoResult.put("resultadosNome", getResultadosNome(resultados, mongo));
				ArrayList<Object> notas = (ArrayList<Object>) avaliacao.get("habilidades");
				avaliacaoResult.put("notas", getNotas(notas, mongo));
				avaliacaoResult.put("avaliadores", getAvaliadores(notas, mongo));
				avaliacaoResult.put("avaliadoresId", getAvaliadoresId(notas, mongo));
				avaliacaoResult.put("habilidades", getHabilidades(notas, mongo));
				avaliacaoResult.put("habilidadesId", getHabilidadesId(notas, mongo));
				avaliacaoResult.put("relacao", getRelacao(avaliacao, notas, mongo));
				BasicDBObject objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false);
				avaliacaoResult.put("objetivoNome", "");
				if (objetivo != null) {
					BasicDBObject objetivoDoc = new BasicDBObject();
					objetivoDoc.putAll((Map) objetivo.get("documento"));
					avaliacaoResult.put("objetivoNome", objetivoDoc.get("nome"));
				}else{
					System.out.println("objetivo inexistente - " + avaliacao.get("objetivoId").toString());
				}
				return avaliacaoResult;
			};
		}; 
		return null;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getNotas(ArrayList<Object> notas, MongoClient mongo) {
		ArrayList<String> notasResult = new ArrayList<>();
		for (int i = 0; i < notas.size(); i++) {
			JSONObject nota = new JSONObject();
			nota.putAll((Map) notas.get(i));
			notasResult.add(nota.get("nota").toString());
		}
		return notasResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getHabilidades(ArrayList<Object> notas, MongoClient mongo) {
		ArrayList<String> habilidades = new ArrayList<>();
		for (int i = 0; i < notas.size(); i++) {
			JSONObject nota = new JSONObject();
			nota.putAll((Map) notas.get(i));
			BasicDBObject habilidade = commons_db.getCollectionDoc(nota.get("id").toString(), "habilidades", "documento.id", mongo, false);
			if (habilidade != null) {
				habilidades.add(habilidade.get("nome").toString());
			}else {
				habilidades.add("Habilildade inexistente");
			}
		}
		return habilidades;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getHabilidadesId(ArrayList<Object> notas, MongoClient mongo) {
		ArrayList<String> habilidades = new ArrayList<>();
		for (int i = 0; i < notas.size(); i++) {
			JSONObject nota = new JSONObject();
			nota.putAll((Map) notas.get(i));
			BasicDBObject habilidade = commons_db.getCollectionDoc(nota.get("id").toString(), "habilidades", "documento.id", mongo, false);
			if (habilidade != null) {
				habilidades.add(habilidade.get("id").toString());
			}else {
				habilidades.add("Habilildade inexistente");
			}
		}
		return habilidades;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getRelacao(BasicDBObject avaliacao, ArrayList<Object> notas, MongoClient mongo) {
		ArrayList<String> relacoes = new ArrayList<>();
		for (int i = 0; i < notas.size(); i++) {
			JSONObject nota = new JSONObject();
			nota.putAll((Map) notas.get(i));
			ArrayList<String> relacao = new ArrayList<>();
			relacao = (ArrayList<String>) avaliacao.get("superiores");
			if (commons.testaElementoArray(nota.get("avaliadorId").toString(), relacao)) {
				relacoes.add("superior");
			}
			relacao = (ArrayList<String>) avaliacao.get("subordinados");
			if (commons.testaElementoArray(nota.get("avaliadorId").toString(), relacao)) {
				relacoes.add("subordinado");
			}
			relacao = (ArrayList<String>) avaliacao.get("parceiros");
			if (commons.testaElementoArray(nota.get("avaliadorId").toString(), relacao)) {
				relacoes.add("parceiro");
			}
			relacao = (ArrayList<String>) avaliacao.get("clientes");
			if (commons.testaElementoArray(nota.get("avaliadorId").toString(), relacao)) {
				relacoes.add("cliente");
			}
		}
		return relacoes;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getAvaliadores(ArrayList<Object> notas, MongoClient mongo) {
		ArrayList<String> avaliadores = new ArrayList<>();
		for (int i = 0; i < notas.size(); i++) {
			JSONObject nota = new JSONObject();
			nota.putAll((Map) notas.get(i));
			BasicDBObject usuario = commons_db.getCollection(nota.get("avaliadorId").toString(), "usuarios", "_id", mongo, false);
			String nome = "";
			if (usuario != null) {
				BasicDBObject usuarioDoc = new BasicDBObject();
				usuarioDoc.putAll((Map) usuario.get("documento"));
				nome = usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName");
			};
			avaliadores.add(nome);
		}
		return avaliadores;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getAvaliadoresId(ArrayList<Object> notas, MongoClient mongo) {
		ArrayList<String> avaliadores = new ArrayList<>();
		for (int i = 0; i < notas.size(); i++) {
			JSONObject nota = new JSONObject();
			nota.putAll((Map) notas.get(i));
			avaliadores.add(nota.get("avaliadorId").toString());
		}
		return avaliadores;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getResultado(ArrayList<Object> resultados, MongoClient mongo) {
		ArrayList<String> resultadosResult = new ArrayList<>();
		for (int i = 0; i < resultados.size(); i++) {
			JSONObject resultado = new JSONObject();
			resultado.putAll((Map) resultados.get(i));
			resultadosResult.add(resultado.get("id").toString());
		}
		return resultadosResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getResultadoNota(ArrayList<Object> resultados, MongoClient mongo) {
		ArrayList<String> resultadosResult = new ArrayList<>();
		for (int i = 0; i < resultados.size(); i++) {
			JSONObject resultado = new JSONObject();
			resultado.putAll((Map) resultados.get(i));
			resultadosResult.add(resultado.get("nota").toString());
		}
		return resultadosResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<String> getResultadosNome(ArrayList<Object> resultados, MongoClient mongo) {
		ArrayList<String> resultadosNome = new ArrayList<>();
		for (int i = 0; i < resultados.size(); i++) {
			JSONObject resultado = new JSONObject();
			resultado.putAll((Map) resultados.get(i));
			BasicDBObject habilidade = commons_db.getCollectionDoc(resultado.get("id").toString(), "habilidades", "documento.id", mongo, false);
			if (habilidade != null) {
				resultadosNome.add(habilidade.get("nome").toString());
			}else {
				resultadosNome.add("Habilildade inexistente");
			}
		}
		return resultadosNome;
	}

	@SuppressWarnings({ "unchecked" })
	private BasicDBObject getAvaliacaoHabilidade(BasicDBObject avaliacao, String habilidadeId, String avaliadorId, MongoClient mongo) {
		
		if (avaliacao == null) {
			return null;
		};
		ArrayList<Object> habilidades = (ArrayList<Object>) avaliacao.get("habilidadesId");
		ArrayList<Object> notas = (ArrayList<Object>) avaliacao.get("notas");
		ArrayList<Object> avaliadoresId = (ArrayList<Object>) avaliacao.get("avaliadoresId");
		ArrayList<Object> avaliadoresNome = (ArrayList<Object>) avaliacao.get("avaliadores");
		for (int j = 0; j < habilidades.size(); j++) {
			if (habilidades.get(j).equals(habilidadeId) && avaliadoresId.get(j).equals(avaliadorId)) {
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.put("nota", notas.get(j));
				avaliacaoResult.put("avaliadorId", avaliadoresId.get(j));
  				avaliacaoResult.put("avaliadorNome", avaliadoresNome.get(j));
				return avaliacaoResult;
			};
		};
		return null;
	};

	@SuppressWarnings("rawtypes")
	private void carregaMapa(String avaliacaoId, ArrayList<Object> outArray, ArrayList<String> inArray, String inout, MongoClient mongo) {

		if (inArray != null) {
			for (int i = 0; i < inArray.size(); i++) {
				BasicDBObject registro = commons_db.getCollection(inArray.get(i).toString(), "usuarios", "_id", mongo, false);
				if (registro != null) {
					BasicDBObject outObj = new BasicDBObject();
					BasicDBObject docObj = (BasicDBObject) registro.get("documento");
					outObj.put("nome", docObj.get("firstName") + " " + docObj.get("lastName"));
					outObj.put("photo", docObj.get("photo"));
					outObj.put("email", docObj.get("email"));
					outObj.put("id", registro.get("_id").toString());
					outObj.put("inout", inout);
					BasicDBObject avaliacao = getAvaliacao(avaliacaoId, registro.get("_id").toString(), mongo);
					if (avaliacao != null) {
  					String objetivoId = avaliacao.get("objetivoId").toString();
  					BasicDBObject objetivoDoc = new BasicDBObject(); 
  					objetivoDoc.putAll((Map) commons_db.getCollection(objetivoId, "objetivos", "documento.id", mongo, false).get("documento"));
  					outObj.put("objetivo", objetivoDoc.get("nome"));
					};
					outArray.add(outObj);
				};
			};		
		};
	};

	@SuppressWarnings({ "unchecked" })
	public JSONArray colaboradores(String empresaId, String avaliacaoId, String usuarioId, String perfil, MongoClient mongo)  {

		if (avaliacaoId == null){
			return null;
		};
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);			

		if (perfil == null) {
			perfil = "";
		};
		
		if (!perfil.equals("rh")) {
			key = new JSONObject();
			key.put("key", "documento.avaliacoes.superiores");
			key.put("value", usuarioId);
			keysArray.add(key);
		};
		
		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);
		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray documentos = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
			if (getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("id").toString().equals(avaliacaoId)) {
	  			BasicDBObject usuario = new BasicDBObject();
	  			usuario = commons_db.getCollection(mapa.getString("usuarioId"), "usuarios", "_id", mongo, false);
	  			BasicDBObject usuarioDoc = (BasicDBObject) usuario.get("documento");
	  			usuarioDoc.remove("password");
	  			usuarioDoc.remove("token");
	  			usuarioDoc.put("id", mapa.getString("usuarioId"));
	  			usuarioDoc.put("objetivo", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("objetivoNome"));
	  			documentos.add(usuarioDoc);
			};
		};
/*		
		if (testaSuperiorColaborador(usuarioId, avaliacaoId)) {
			BasicDBObject usuario = new BasicDBObject();
			usuario = commons_db.getCollection(usuarioId, "usuarios", "_id");
			BasicDBObject usuarioDoc = (BasicDBObject) usuario.get("documento");
			usuarioDoc.remove("password");
			usuarioDoc.remove("token");
			usuarioDoc.put("id", usuarioId);
			usuarioDoc.put("objetivo", getAvaliacao(avaliacaoId, usuarioId).get("objetivoNome"));
			documentos.add(usuarioDoc);			
		};
*/		
		keysArray = new ArrayList<>();
		key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);			

		if (!perfil.equals("rh")) {
			key = new JSONObject();
			key.put("key", "documento.avaliacoes.superioresOut");
			key.put("value", usuarioId);
			keysArray.add(key);
  		response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);
  		mapas = new JSONArray();
  		mapas = (JSONArray) response.getEntity();
  		for (int i = 0; i < mapas.size(); i++) {
  			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
  			if (getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("id").toString().equals(avaliacaoId)) {
    			BasicDBObject doc = (BasicDBObject) mapas.get(i);
    			BasicDBObject docUsu = new BasicDBObject();
    			docUsu = commons_db.getCollection(doc.getString("usuarioId"), "usuarios", "_id", mongo, false);
    			BasicDBObject docOut = (BasicDBObject) docUsu.get("documento");
    			docOut.remove("password");
    			docOut.remove("token");
    			docOut.put("id", doc.getString("usuarioId"));
    			docOut.put("objetivo", getAvaliacao(avaliacaoId, doc.getString("usuarioId"), mongo).get("objetivoNome"));
    			documentos.add(docOut);
  			};
  		};
		};
		
		return documentos;
	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray colaboradoresUltimas5Avaliacoes(String usuarioId, MongoClient mongo)  {
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.usuarioId");
		key.put("value", usuarioId);
		keysArray.add(key);			
		
		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);
		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray documentos = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
  			BasicDBObject usuario = new BasicDBObject();
  			usuario = commons_db.getCollectionDoc(mapa.getString("usuarioId"), "usuarios", "_id", mongo, false);
  			ArrayList<Object> avaliacoes = (ArrayList<Object>) mapa.get("avaliacoes");
  			int inicio = 0;
  			if (avaliacoes.size() > 5) {
  				inicio = avaliacoes.size() - 4;
  			};
  			BasicDBObject usuarioDoc = new BasicDBObject();
  			usuarioDoc.put("Nome", usuario.getString("firstName") + " " + usuario.getString("lastName"));
  			usuarioDoc.put("id", mapa.getString("usuarioId"));
  			ArrayList<Object> notas = new ArrayList<>();
  			ArrayList<String> habilidadesId = new ArrayList<>();
  			ArrayList<String> habilidadesNome = new ArrayList<>();
  			ArrayList<String> avaliacoesNome = new ArrayList<>();
  			for (int j = inicio; j < avaliacoes.size(); j++) {
  				JSONObject avaliacao = new JSONObject();
  				avaliacao.putAll((Map) avaliacoes.get(j));
  				BasicDBObject avaliacaoNome = commons_db.getCollectionDoc(avaliacao.get("id").toString(), "avaliacoes", "_id", mongo, false);
  	  			ArrayList<Object> resultados = new ArrayList<>();
  	  			resultados =  (ArrayList<Object>) getAvaliacao(avaliacao.get("id").toString(), mapa.getString("usuarioId"), mongo).get("resultados");
  	  			if (resultados.size() > 0) {
	  				if (avaliacaoNome != null) {
	  					avaliacoesNome.add(avaliacaoNome.get("nome").toString());
	  				}else {
	  					avaliacoesNome.add("Avaliação inexistente");
	  				};
	  	  			usuarioDoc.put("objetivo", getAvaliacao(avaliacao.get("id").toString(), mapa.getString("usuarioId"), mongo).get("objetivoNome"));
	  	  			for (int k = 0; k < resultados.size(); k++) {
	  	  				if (!commons.testaElementoArray(resultados.get(k).toString(), habilidadesId)){
	  	  					BasicDBObject habilidade = commons_db.getCollectionDoc(resultados.get(k).toString(), "habilidades", "documento.id", mongo, false);
	  	  					habilidadesId.add(resultados.get(k).toString());
	  	  					if (habilidade != null) {
	  	  						habilidadesNome.add(habilidade.get("nome").toString());
	  	  					}else {
	  	  						habilidadesNome.add("Habilidade inexistente");
	  	  					};
	  	  				};
					};
  	  			};
			};
			for (int j = 0; j < habilidadesId.size(); j++) {
	  			ArrayList<Object> notasHabilidade = new ArrayList<>();
	  			for (int z = inicio; z < avaliacoes.size(); z++) {
	  				JSONObject avaliacao = new JSONObject();
	  				avaliacao.putAll((Map) avaliacoes.get(z));
	  	  			ArrayList<String> notasOrigem = new ArrayList<>();
	  	  			notasOrigem =  (ArrayList<String>) getAvaliacao(avaliacao.get("id").toString(), mapa.getString("usuarioId"), mongo).get("notas");
	  	  			ArrayList<Object> resultados = new ArrayList<>();
	  	  			resultados =  (ArrayList<Object>) getAvaliacao(avaliacao.get("id").toString(), mapa.getString("usuarioId"), mongo).get("resultados");
	  	  			if (resultados.size() > 0) {
		  	  			for (int k = 0; k < notasOrigem.size(); k++) {
		  	  				if (resultados.get(k).equals(habilidadesId.get(j))){
	  	  						notasHabilidade.add(notasOrigem.get(k));
		  	  				};
						};
	  	  			};
				};
				notas.add(notasHabilidade);
			};
  			usuarioDoc.put("avaliacoes", avaliacoesNome);
  			usuarioDoc.put("habilidades", habilidadesNome);
  			usuarioDoc.put("notas", notas);
  			documentos.add(usuarioDoc);				
		};
		
		return documentos;
	};

	@SuppressWarnings({ "unchecked" })
	public JSONArray colaboradoresAvaliacao(String avaliacaoId, MongoClient mongo)  {

		if (avaliacaoId == null){
			return null;
		};
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);			
		
		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);
		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray documentos = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
			if (getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("id").toString().equals(avaliacaoId)) {
	  			BasicDBObject usuario = new BasicDBObject();
	  			usuario = commons_db.getCollectionDoc(mapa.getString("usuarioId"), "usuarios", "_id", mongo, false);
	  			BasicDBObject usuarioDoc = new BasicDBObject();
	  			usuarioDoc.put("Nome", usuario.getString("firstName") + " " + usuario.getString("lastName"));
	  			usuarioDoc.put("id", mapa.getString("usuarioId"));
	  			usuarioDoc.put("objetivo", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("objetivoNome"));
	  			usuarioDoc.put("resultados", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("resultados"));
	  			usuarioDoc.put("resultadosNome", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("resultadosNome"));
	  			usuarioDoc.put("notas", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("notas"));
	  			usuarioDoc.put("habilidades", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("habilidades"));
	  			usuarioDoc.put("relacao", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("relacao"));
	  			usuarioDoc.put("resultadosNome", getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("resultadosNome"));
	  			documentos.add(usuarioDoc);
			};
		};
		
		return documentos;
	};

	@SuppressWarnings({ "unchecked" })
	public JSONArray colaboradoresAvaliacaoNotas(String avaliacaoId, MongoClient mongo)  {

		if (avaliacaoId == null){
			return null;
		};
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);			
		
		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);
		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray documentos = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
			if (getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("id").toString().equals(avaliacaoId)) {
	  			BasicDBObject usuario = new BasicDBObject();
	  			usuario = commons_db.getCollectionDoc(mapa.getString("usuarioId"), "usuarios", "_id", mongo, false);
	  			String avaliado = usuario.getString("firstName") + " " + usuario.getString("lastName");
	  			ArrayList<String> notas = (ArrayList<String>) getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("notas");
	  			ArrayList<String> habilidades = (ArrayList<String>) getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("habilidades");
	  			ArrayList<String> avaliadores = (ArrayList<String>) getAvaliacao(avaliacaoId, mapa.getString("usuarioId"), mongo).get("avaliadores");
	  			for (int j = 0; j < notas.size(); j++) {
	  				JSONObject result = new JSONObject();
	  				result.put("avaliadoNome", avaliado);
	  				result.put("habilidadeNome", habilidades.get(j));
	  				result.put("nota", notas.get(j));
	  				result.put("avaliadorNome", avaliadores.get(j));
					documentos.add(result);
				}
			};
		};
		
		return documentos;
	};

	@SuppressWarnings({ })
	public BasicDBObject colaborador(String avaliacaoId, String usuarioId, MongoClient mongo)  {

		if (avaliacaoId == null){
			return null;
		};

		BasicDBObject usuarioDoc = new BasicDBObject();
		BasicDBObject usuario = new BasicDBObject();
		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);
		if (usuario.get("documento") != null) {
			usuarioDoc = (BasicDBObject) usuario.get("documento");
			usuarioDoc.put("id", usuarioId);
			usuarioDoc.remove("password");
			usuarioDoc.remove("token");
			if (usuarioDoc != null) {
  			usuarioDoc.put("objetivo", getAvaliacao(avaliacaoId, usuarioId, mongo).get("objetivoNome"));
			};
		};
		
		return usuarioDoc;
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean montaMapa(String colaboradorId, String colaboradorObjetoId, String assunto, String empresaId, String avaliacaoId, MongoClient mongo)  {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
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

		if (avaliacao.get("status") != null) {
  		if (avaliacao.get("status").equals("mapa_fechado")) {
  			return false;
  		};
		};
		
		JSONObject arrays = new JSONObject(); 
		
		if (assunto.equals("cliente")) {
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
		};
		
		// *** coloca na array destino
		ArrayList<String> arrayIn = new ArrayList<>();
		if (arrays.get("in") != null) {
  		arrayIn = (ArrayList<String>) avaliacao.get(arrays.get("in"));
  		if (avaliacao.get(arrays.get("in")) == null) {
  			avaliacao.put(arrays.get("in").toString(), arrayIn);
  		};
  		arrayIn.add(colaboradorObjetoId);
  		avaliacao.remove(arrays.get("in"));
  		avaliacao.put(arrays.get("in").toString(), arrayIn);
		};
		
		avaliacoesNew.add(avaliacao);

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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento, mongo, false);
		return true;	
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean montaMapaCliente(String colaboradorId, String colaboradorObjetoId, String empresaId, String avaliacaoId, String status, MongoClient mongo)  {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
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
				avaliacoesNew.add(avaliacaoObj);
			};
		};

		ArrayList<String> avaliacaoClientes = new ArrayList<>();
		if (avaliacao.get("clientes") != null) {
			avaliacaoClientes = (ArrayList<String>) avaliacao.get("clientes");
		};

		ArrayList<String> avaliacaoclientesConvitesAceitos = new ArrayList<>();
		if (avaliacao.get("clientesConvitesAceitos") != null) {
			avaliacaoclientesConvitesAceitos = (ArrayList<String>) avaliacao.get("clientesConvitesAceitos");
		};

		ArrayList<String> avaliacaoclientesConvitesRecusados = new ArrayList<>();
		if (avaliacao.get("clientesConvitesRecusados") != null) {
			avaliacaoclientesConvitesRecusados = (ArrayList<String>) avaliacao.get("clientesConvitesRecusados");
		};

		Boolean existeCliente = false;
		Boolean incluiCliente = true;
		String assunto = "";
		if (commons.testaElementoArray(colaboradorObjetoId, avaliacaoClientes)) {
			existeCliente = true;
			incluiCliente = false;
			assunto = "clientes";
		};

		if (commons.testaElementoArray(colaboradorObjetoId, avaliacaoclientesConvitesAceitos)) {
			existeCliente = true;
			incluiCliente = false;
			assunto = "clientesConvitesAceitos";
		};

		if (commons.testaElementoArray(colaboradorObjetoId, avaliacaoclientesConvitesRecusados)) {
			existeCliente = true;
			incluiCliente = false;
			assunto = "clientesConvitesRecusados";
		};
			
		// *** retira da array origem
		ArrayList<String> arrayOut = new ArrayList<>();
		if (existeCliente) {
			arrayOut = (ArrayList<String>) avaliacao.get(assunto);
			if (arrayOut == null) {
				return null;
			};
			arrayOut = commons.removeString(arrayOut, colaboradorObjetoId);
			avaliacao.remove(avaliacao.get(assunto));
			avaliacao.put(assunto, arrayOut);
		};
			
		// *** coloca na array destino
		ArrayList<String> arrayIn = new ArrayList<>();
		if (status == null) {
			assunto ="clientes";
		}else {
  		switch (status) {
  		case "aceito":
  			assunto = "clientesConvitesAceitos";
  			break;
  		case "recusado":
  			assunto = "clientesConvitesRecusados";
  			break;
  
  		default:
  			break;
  		};
		};
		
		if (avaliacao.get(assunto) != null) {
			arrayIn = (ArrayList<String>) avaliacao.get(assunto);
		};
		
		if (assunto != "clientes" | incluiCliente) {
  		if (avaliacao.get(assunto) == null) {
  			avaliacao.put(assunto, arrayIn);
  		};
  		arrayIn.add(colaboradorObjetoId);
  		avaliacao.remove(assunto);
  		avaliacao.put(assunto, arrayIn);
		};
		
		avaliacoesNew.add(avaliacao);

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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento, mongo, false);
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
	};

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

	@SuppressWarnings({"unchecked", "rawtypes" })
	public Boolean montaHabilidades(String colaboradorId, String empresaId, String avaliacaoId, String habilidadeId, MongoClient mongo)  {
		
		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
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
				avaliacoesNew.add(avaliacaoObj);
			};
		};
		
		ArrayList<String> array = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			array = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};
		
		Boolean existeHabilidade = false;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).equals(habilidadeId)) {
				existeHabilidade = true;	
			};
		};
		
		if (existeHabilidade) {
			array = commons.removeString(array, habilidadeId);
		}else {
			array.add(habilidadeId);
		};
		
		avaliacao.remove("habilidadesOut");
		avaliacao.put("habilidadesOut", array);
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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento, mongo, false);
		return true;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean atualizaNota(String avaliadorId, String colaboradorId, String habilidadeId, String nota, String avaliacaoId, MongoClient mongo)  {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(colaboradorId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento, mongo, false);
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
  					if (!habilidades.get(j).get("nota").toString().equals("na")) {
  						qtde++;
  						notas = notas + Double.valueOf(habilidades.get(j).get("nota").toString());
  					};
  				};
  			};
  			BasicDBObject resultado = new BasicDBObject();
  			resultado.put("id", habilidades.get(i).get("id").toString());
  			if (qtde != 0) {
  				resultado.put("nota", String.valueOf((notas/qtde)));
  			}else {
  				resultado.put("nota", "na");
  			};
  			resultados.add(resultado);
  			habilidadesTratadas.add(habilidades.get(i).get("id").toString());
			};
		};
		return resultados;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getResultadoHabilidade(String avaliacaoId, String usuarioId, String habilidadeId, MongoClient mongo)  {
	
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.usuarioId");
		key.put("value", usuarioId);
		keysArray.add(key);
		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		Response response = commons_db.obterCrud("mapaAvaliacao", keysArray, mongo, false);
		
		BasicDBObject mapaAvaliacao = new BasicDBObject();
		if (response.getEntity().equals(false)) {
			return "NA";
		}else {
			mapaAvaliacao.putAll((Map) response.getEntity());
		};
		
		BasicDBObject mapaAvaliacaoDoc = new BasicDBObject();
		mapaAvaliacaoDoc.putAll((Map) mapaAvaliacao.get("documento"));

		ArrayList<Object> avaliacoes = (ArrayList<Object>) mapaAvaliacaoDoc.get("avaliacoes");
		
		for (int i = 0; i < avaliacoes.size(); i++) {
			JSONObject avaliacao = new JSONObject();
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("id").equals(avaliacaoId)) {
				ArrayList<Object> resultados = (ArrayList<Object>) avaliacao.get("resultados");
				for (int j = 0; j < resultados.size(); j++) {
					JSONObject resultado = new JSONObject();
					resultado.putAll((Map) resultados.get(j));
					if (resultado.get("id").equals(habilidadeId)) {
						return resultado.get("nota").toString();
					};
				};				
			};
		};
				
		return "NA";

	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<Object> lista(String empresaId, String usuarioId, MongoClient mongo)  {
	
		BasicDBObject empresa = new BasicDBObject();
		empresa.putAll((Map) commons_db.getCollection(empresaId, "empresas", "_id", mongo, false));
		BasicDBObject empresaDoc = new BasicDBObject();
		empresaDoc.putAll((Map) empresa.get("documento"));

		JSONArray avaliacoesResult = new JSONArray();

		BasicDBObject hierarquia = commons_db.getCollectionDoc(usuarioId, "hierarquias", "documento.colaborador", mongo, false);
		String area = "";
		if (hierarquia != null) {
			area = hierarquia.getString("area");
		};
		BasicDBObject usuario = commons_db.getCollectionDoc(usuarioId, "usuarios", "_id", mongo, false);
		String perfil = "";
		if (usuario != null) {
			perfil = usuario.getString("perfilEmpresa");
		};

		String lastAvalId = "";
		if (empresaDoc.get("lastAval") != null && !empresaDoc.get("lastAval").equals("none")) {
	  		BasicDBObject avaliacaoLast = new BasicDBObject();
	  		avaliacaoLast.putAll((Map) commons_db.getCollection(empresaDoc.get("lastAval").toString(), "avaliacoes", "_id", mongo, false));
	  		BasicDBObject avaliacaoLastDoc = new BasicDBObject();
	  		avaliacaoLastDoc.putAll((Map) avaliacaoLast.get("documento"));
	  		BasicDBObject avaliacaoResult = new BasicDBObject();
	  		avaliacaoResult.put("_id", avaliacaoLast.get("_id").toString());
	  		avaliacaoLastDoc.put("status", getStatusAvaliacao(avaliacaoLastDoc));
	  		avaliacaoResult.put("documento", avaliacaoLastDoc);
			if (commons.calcTime(avaliacaoLastDoc.get("dataEnvio").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
				avaliacaoResult.put("statusAvaliacao", "avaliacao_fechada");
				avaliacaoResult.put("statusMapa", "mapa_fechado");
			}else {
				avaliacaoResult.put("statusAvaliacao", "avaliacao_aberta");
				avaliacaoResult.put("statusMapa", "mapa_aberto");
			};
			if (avaliacaoValida(avaliacaoLastDoc, area, perfil)) {
		  		avaliacoesResult.add(avaliacaoResult);				
			};
	  		lastAvalId = avaliacaoLast.get("_id").toString();
		};
		
		ArrayList<Object> avaliacoes = new ArrayList<Object>(); 
		avaliacoes = commons_db.getCollectionLista(empresaId, "avaliacoes", "documento.empresaId", mongo, false);

		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoes.get(i));
			avaliacao.put("status", getStatusAvaliacao(avaliacao));
			if (avaliacao.get("status") != null){
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.put("_id", avaliacao.get("_id").toString());
				avaliacaoResult.put("documento", avaliacao);
				if (commons.calcTime(avaliacao.get("dataEnvio").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
					avaliacaoResult.put("statusAvaliacao", "avaliacao_fechada");
					avaliacaoResult.put("statusMapa", "mapa_fechado");
				}else {
					avaliacaoResult.put("statusAvaliacao", "avaliacao_aberta");
					avaliacaoResult.put("statusMapa", "mapa_aberto");
				};
				if (!avaliacao.get("_id").toString().equals(lastAvalId)) {
					if (avaliacaoValida(avaliacao, area, perfil)) {
						avaliacoesResult.add(avaliacaoResult);
					};
				};
			}else {
				BasicDBObject avaliacaoResult = new BasicDBObject();
				avaliacaoResult.put("_id", avaliacao.get("_id").toString());
				avaliacaoResult.put("documento", avaliacao);
				if (commons.calcTime(avaliacao.get("dataEnvio").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
					avaliacaoResult.put("statusAvaliacao", "avaliacao_fechada");
					avaliacaoResult.put("statusMapa", "mapa_fechado");
				}else {
					avaliacaoResult.put("statusAvaliacao", "avaliacao_aberta");
					avaliacaoResult.put("statusMapa", "mapa_aberto");
				};
				if (!avaliacao.get("_id").toString().equals(lastAvalId)) {
					if (avaliacaoValida(avaliacao, area, perfil)) {
						avaliacoesResult.add(avaliacaoResult);
					};
				};
			};
		};	
		return avaliacoesResult;
	};
	
	@SuppressWarnings("unchecked")
	private boolean avaliacaoValida(BasicDBObject avaliacao, String area, String perfil) {
		ArrayList<String> areas = new ArrayList<>();
		ArrayList<String> niveis = new ArrayList<>();
		if (avaliacao != null){
			areas = (ArrayList<String>) avaliacao.get("areas");
			niveis= (ArrayList<String>) avaliacao.get("niveis");
		};

		if (perfil.equals("rh")) {
			return true;
		};

		if (areas.size() == 0 && niveis.size() == 0) {
			return true;
		};
		
		if (areas.size() > 0) {
			if (!commons.testaElementoArray(area, areas)) {
				return false;
			}
		};
		
		return true;
	};

	private String getStatusAvaliacao(BasicDBObject avaliacao) {
		if (avaliacao.get("status").equals("suspensa")){
			return "suspensa";
		};
		if (commons.calcTime(avaliacao.get("dataConclusao").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
			return "encerrada";
		}else {
			return "emandamento";
		}
	};
/*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean testaMapaFechado(String empresaId, String usuarioId, String avaliacaoId) {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.superiores");
		key.put("value", usuarioId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.status");
		key.put("value", "mapa_aberto");
		keysArray.add(key);
		
		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray);
		
		if (response.getStatus() == 200) {
			JSONArray mapas = (JSONArray) response.getEntity();
			for (int i = 0; i < mapas.size(); i++) {
				BasicDBObject mapa = new BasicDBObject();
				mapa.putAll((Map) mapas.get(i));
				ArrayList<Object> avaliacoes = (ArrayList<Object>) mapa.get("avaliacoes");
				for (int j = 0; j < avaliacoes.size(); j++) {
					BasicDBObject avaliacao = new BasicDBObject();
					avaliacao.putAll((Map) avaliacoes.get(j));
					if (avaliacao.get("status").equals("mapa_aberto") && avaliacao.get("id").toString().equals(avaliacaoId)) {
						return false;
					};
				};
			};
		};
		
		return true;
	};
*/
	
	@SuppressWarnings("rawtypes")
	private boolean testaAvaliacaoFechada(String avaliacaoId, MongoClient mongo) {

		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
		BasicDBObject avaliacaoDoc = new BasicDBObject();
		avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
		if (commons.calcTime(avaliacaoDoc.get("dataEnvio").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
			return true;
		};
				
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject fechaMapa (String empresaId, String avaliacaoId, String gestorId, MongoClient mongo) {

		Usuario usuario = new Usuario();
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.avaliacoes.superiores");
		key.put("value", gestorId);
		keysArray.add(key);

		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray, mongo, false);

		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray colaboradores = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = new BasicDBObject();
			mapa.putAll((Map) mapas.get(i));
			if (mapa.get("usuarioId") != null){
				fechaMapaColaborador(mapa.get("usuarioId").toString(), avaliacaoId, mapa, mongo);
	  		JSONObject result = new JSONObject();
	 			result.put("nomeColaborador", usuario.get(mapa.get("usuarioId").toString(), mongo).get("nome"));
	 			result.put("emailColaborador", usuario.get(mapa.get("usuarioId").toString(), mongo).get("email"));
	 			colaboradores.add(result);
			};
		};
		
		// *** fecha mapa de gestor sem superior
		if (testaSuperiorColaborador(gestorId, avaliacaoId, mongo)) {
			BasicDBObject mapa = getMapa(gestorId, mongo);
			fechaMapaColaborador(gestorId, avaliacaoId, mapa, mongo);			
		};

		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
		BasicDBObject avaliacaoDoc = new BasicDBObject();
		avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				
		JSONObject result = new JSONObject();
		result.put("avaliacaoNome", avaliacaoDoc.get("nome"));
		result.put("dataConclusao", avaliacaoDoc.get("dataConclusao"));
		result.put("dataMapa", commons.calcNewDate(avaliacaoDoc.get("dataConclusao").toString(), Integer.valueOf(avaliacaoDoc.get("diasMapa").toString())));
		result.put("colaboradores", colaboradores); 
		return result;		
	}

	@SuppressWarnings("unchecked")
	private boolean testaSuperiorColaborador(String usuarioId, String avaliacaoId, MongoClient mongo) {
		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, usuarioId, mongo);
		if (avaliacao.get("superiores") == null) {
			return false;
		};
		ArrayList<String> superiores = (ArrayList<String>) avaliacao.get("superiores");
		if (superiores.size() == 0) {
			return true;
		};
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean fechaMapaColaborador(String colaboradorId, String avaliacaoId, BasicDBObject mapa, MongoClient mongo) {

		ArrayList<Object> avaliacoes = (ArrayList<Object>) mapa.get("avaliacoes");
		ArrayList<Object> avaliacoesNova = new ArrayList<Object>();
		for (int i = 0; i < avaliacoes.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();	
			avaliacao.putAll((Map) avaliacoes.get(i));
			if (avaliacao.get("id").equals(avaliacaoId)) {
				avaliacao.put("status", "mapa_fechado");
			};
			avaliacoesNova.add(avaliacao);
		};

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", mapa);

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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento, mongo, false);
				
		return true;
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject estatisticaMapa(String empresaId, String avaliacaoId, MongoClient mongo) {
		
		JSONObject estatistica = new JSONObject();
		
		
		ArrayList<Object> mapas = commons_db.getCollectionLista(avaliacaoId, "mapaAvaliacao", "documento.avaliacoes.id", mongo, false);
		
		int totalAvaliados = 0;
		int totalAvaliacoes = 0;
		int mapasAbertos = 0;
		int mapasFechados = 0;
		int avaliacoesIniciadas = 0;
		int avaliacoesEncerradas = 0;
		int avaliacoesNaoIniciadas = 0;
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = new BasicDBObject();
			mapa.putAll((Map) mapas.get(i));
			++totalAvaliados;
			ArrayList<Object> avaliacoes = (ArrayList<Object>) mapa.get("avaliacoes");
			for (int j = 0; j < avaliacoes.size(); j++) {
				BasicDBObject avaliacao = new BasicDBObject();
				avaliacao.putAll((Map) avaliacoes.get(j));
				if (avaliacao.get("id").equals(avaliacaoId)) {
					totalAvaliacoes = totalAvaliacoes + commons.tamanhoArray(avaliacao.get("superiores"));
					totalAvaliacoes = totalAvaliacoes + commons.tamanhoArray(avaliacao.get("subordinados"));
					totalAvaliacoes = totalAvaliacoes + commons.tamanhoArray(avaliacao.get("parceiros"));
					totalAvaliacoes = totalAvaliacoes + commons.tamanhoArray(avaliacao.get("clientes"));
					if (testaAvaliacaoFechada(avaliacaoId, mongo)){
						++mapasFechados;
					}else {
						++mapasAbertos;
					}
					BasicDBObject habilidades = carregaHabilidadesAvaliacao(mapa.getString("usuarioId"), null, avaliacaoId, avaliacao, mongo);
					if (commons.tamanhoArray(habilidades.get("habilidades")) == commons.tamanhoArray(avaliacao.get("habilidades"))) {
						++avaliacoesEncerradas;
					}else {
  					if (commons.tamanhoArray(avaliacao.get("habilidades")) > 0) {
  						++avaliacoesIniciadas;
  					};
  					if (commons.tamanhoArray(avaliacao.get("habilidades")) == 0) {
  						++avaliacoesNaoIniciadas;
  					};
					};
				};
			};
		};
		
		estatistica.put("totalAvaliados", totalAvaliados);
		estatistica.put("totalAvaliacoes", totalAvaliacoes);
		estatistica.put("mapasAbertos", mapasAbertos);
		estatistica.put("mapasFechados", mapasFechados);
		estatistica.put("avaliacoesIniciadas", avaliacoesIniciadas);
		estatistica.put("avaliacoesEncerradas", avaliacoesEncerradas);
		estatistica.put("avaliacoesNaoIniciadas", avaliacoesNaoIniciadas);
		return estatistica;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void criaHistorico(BasicDBObject historicosJson, MongoClient mongo) {

		String empresaId = (String) historicosJson.get("empresaId");
		
		ArrayList<Object> historicos = (ArrayList<Object>) historicosJson.get("historicos");

		for (int i = 0; i < historicos.size(); i++) {
			BasicDBObject historico = new BasicDBObject();
			historico.putAll((Map) historicos.get(i));
			if (historico.get("nome") != null && 
				historico.get("dataEnvio") != null && historico.get("dataConclusao") != null && 
				historico.get("email") != null && historico.get("objetivoId") != null && 
				historico.get("habilidadeId") != null && historico.get("nota") != null) {
				BasicDBObject usuarioIn = commons_db.getCollection(historico.get("email").toString(), "usuarios", "documento.email", mongo, false);
				if (usuarioIn != null) {
					BasicDBObject avaliacao = verificaAvaliacao(historico.get("nome").toString(), historico, empresaId, mongo);
					criaMapaHistorico(avaliacao, historico, empresaId, usuarioIn.get("_id").toString(), mongo);
				}else {
					System.out.println("Usuario inexistente-" + historico.get("nome") + " - " + historico.get("email")) ;	
				}
			}else {
				System.out.println("Dados de importação inválidos-" + historico.get("nome") + " - " + historico.get("email")) ;
			}
		};
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BasicDBObject verificaAvaliacao(String avaliacaoNome, BasicDBObject historico, String empresaId, MongoClient mongo) {
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		key = new JSONObject();
		key.put("key", "documento.nome");
		key.put("value", avaliacaoNome);
		keysArray.add(key);
	
		Response response = commons_db.obterCrud("avaliacoes", keysArray, mongo, false);
		BasicDBObject avaliacao = new BasicDBObject();
		if ((response.getStatus() == 200 && response.getEntity() != "false")){
			BasicDBObject avaliacaoDoc = new BasicDBObject();
			avaliacaoDoc.putAll((Map) response.getEntity());
			avaliacao = (BasicDBObject) avaliacaoDoc.get("documento");
			avaliacao.put("_id", avaliacaoDoc.get("_id"));
		}else {
			avaliacao = criaAvaliacao(historico, empresaId, mongo);
		};
		
		return avaliacao;
	}

	@SuppressWarnings("rawtypes")
	private BasicDBObject criaAvaliacao(BasicDBObject historico, String empresaId, MongoClient mongo) {

		BasicDBObject avaliacao = new BasicDBObject();
		BasicDBObject avaliacaoDoc = new BasicDBObject();

		avaliacaoDoc.put("nome", historico.get("nome"));
		avaliacaoDoc.put("empresaId", historico.get("empresaId"));
		avaliacaoDoc.put("dataInicio", commons.todaysDate("yyyy-mm-dd"));
		avaliacaoDoc.put("dataEnvio", historico.get("dataEnvio").toString());
		avaliacaoDoc.put("dataConclusao", historico.get("dataConclusao").toString());
		avaliacaoDoc.put("enviarEmail", "false");
		avaliacaoDoc.put("diasMapa", "0");
		avaliacaoDoc.put("areas", "");
		avaliacaoDoc.put("niveis", "");
		avaliacaoDoc.put("status", "encerrada");
		avaliacaoDoc.put("desNaosei", "false");
		avaliacaoDoc.put("importada", "true");
		avaliacao.put("documento", avaliacaoDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("avaliacoes", avaliacao, mongo, false).getEntity());
		return result;
	};

	private String verificaUsuario(BasicDBObject historico, String empresaId, MongoClient mongo) {

		BasicDBObject usuarioIn = new BasicDBObject();
		usuarioIn = commons_db.getCollection(historico.get("email").toString(), "usuarios", "documento.email", mongo, false);
		if (usuarioIn == null){
			historico.put("firstName", historico.get("email").toString().split("@")[0]);
			usuarioIn = usuario.criaUsuario(historico, empresaId, false, mongo, false);
		};
		
		return usuarioIn.get("_id").toString();
	};	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void criaMapaHistorico(BasicDBObject avaliacaoIn, BasicDBObject historico, String empresaId, String usuarioId, MongoClient mongo) {

		String avaliacaoId = avaliacaoIn.getString("_id");

		BasicDBObject mapaAvaliacaoDoc = new BasicDBObject();
		BasicDBObject mapaAvaliacao = new BasicDBObject();
		mapaAvaliacaoDoc = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
		if (mapaAvaliacaoDoc == null) {
			commons_db.incluirCrud("mapaAvaliacao", criaMapaDoc(usuarioId, empresaId, montaAvaliacaoUnica(historico, avaliacaoIn)), mongo, false);
			mapaAvaliacaoDoc = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId", mongo, false);
		};
		mapaAvaliacao = (BasicDBObject) mapaAvaliacaoDoc.get("documento");
		ArrayList<Object> avaliacoes = new ArrayList<>();
		ArrayList<Object> avaliacoesNew = new ArrayList<>();
		avaliacoes = (ArrayList<Object>) mapaAvaliacao.get("avaliacoes");
		BasicDBObject avaliacaoMapa = new BasicDBObject();
		Boolean existeAvaliacao = false;
		for (int i = 0; i < avaliacoes.size(); i++) {
			avaliacaoMapa.putAll((Map) avaliacoes.get(i));
			if (avaliacaoMapa.get("id").equals(avaliacaoId)) {
				existeAvaliacao = true;
			};
		};
		if (!existeAvaliacao) {
			JSONObject avaliacaoNew = montaAvaliacaoUnica(historico, avaliacaoIn).get(0);
			avaliacoes.add(avaliacaoNew);
		};
		for (int i = 0; i < avaliacoes.size(); i++) {
			avaliacaoMapa = new BasicDBObject();
			avaliacaoMapa.putAll((Map) avaliacoes.get(i));
			if (avaliacaoMapa.get("id").equals(avaliacaoId)) {
				ArrayList<BasicDBObject> resultados = new ArrayList<>();				
				resultados = (ArrayList<BasicDBObject>) avaliacaoMapa.get("resultados");
				BasicDBObject resultadoNew = new BasicDBObject();
				resultadoNew.put("id", historico.get("habilidadeId"));
				resultadoNew.put("nota", historico.get("nota"));
				ArrayList<BasicDBObject> resultadosNew = new ArrayList<>();				
				for (int j = 0; j < resultados.size(); j++) {
					BasicDBObject resultado = new BasicDBObject();
					resultado.putAll((Map) resultados.get(j));
					if (!resultado.get("id").toString().equals(historico.get("habilidadeId").toString())) {
						resultadosNew.add(resultado);
					};
				};
				resultadosNew.add(resultadoNew);
				avaliacaoMapa.put("resultados", resultadosNew);					
			};
			avaliacoesNew.add(avaliacaoMapa);
		};
		  
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		fieldsArray = new ArrayList<>();
		field = new JSONObject();
		field.put("field", "avaliacoes");
		field.put("value", avaliacoesNew);
		fieldsArray.add(field);
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		keysArray = new ArrayList<>();
		key = new JSONObject();
		key.put("key", "documento.usuarioId");
		key.put("value", usuarioId);
		keysArray.add(key);

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, null, mongo, false);
		
	};

	@SuppressWarnings({ "unchecked" })
	private ArrayList<JSONObject> montaAvaliacaoUnica(BasicDBObject historico, BasicDBObject avaliacaoIn) {

		String avaliacaoId = avaliacaoIn.getString("_id");

		JSONObject avaliacao = new JSONObject();
		ArrayList<JSONObject> arrayVazia = new ArrayList<>();
		avaliacao.put("id", avaliacaoId);
		avaliacao.put("superiores", arrayVazia);
		avaliacao.put("superioresOut", arrayVazia);
		avaliacao.put("subordinados", arrayVazia);
		avaliacao.put("subordinadosOut", arrayVazia);
		avaliacao.put("parceiros", arrayVazia);
		avaliacao.put("parceirosOut", arrayVazia);
		avaliacao.put("clientes", arrayVazia);
		avaliacao.put("objetivoId", historico.get("objetivoId").toString());
		avaliacao.put("habilidades", arrayVazia);
		avaliacao.put("habilidadesOut", arrayVazia);
		avaliacao.put("resultados", arrayVazia);
		avaliacao.put("clientesConvitesAceitos", arrayVazia);
		avaliacao.put("clientesConvitesRecusados", arrayVazia);
		avaliacao.put("status", "mapa_fechado");
		ArrayList<JSONObject> avaliacoesNew = new ArrayList<JSONObject>();
			
		avaliacoesNew.add(avaliacao);
		return avaliacoesNew;
	}
	
};
