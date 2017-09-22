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
	public JSONObject criaMapaAvaliacao(String empresaId, String avaliacaoId) {

		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.avaliacoes.id");
		key.put("value", avaliacaoId);
		keysArray.add(key);

		commons_db.removerCrudMany("mapaAvaliacao", keysArray);
		
		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId");

		JSONArray gestores = new JSONArray();

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
  			ArrayList<JSONObject> arrayVazia = new ArrayList<>();
  			avaliacao.put("id", avaliacaoId);
  			avaliacao.put("superiores", hierarquia(hierarquia.get("colaborador").toString(), "colaborador", "superior", hierarquia.get("colaborador").toString()));
  			avaliacao.put("superioresOut", arrayVazia);
  			avaliacao.put("subordinados", hierarquia(hierarquia.get("colaborador").toString(), "superior", "colaborador", hierarquia.get("colaborador").toString()));
  			avaliacao.put("subordinadosOut", arrayVazia);
  			avaliacao.put("parceiros", hierarquia(hierarquia.get("superior").toString(), "superior","colaborador", hierarquia.get("colaborador").toString()));
  			avaliacao.put("parceirosOut", arrayVazia);
  			avaliacao.put("clientes", hierarquia(hierarquia.get("colaborador").toString(), "clientes", "colaborador", hierarquia.get("colaborador").toString()));
  			avaliacao.put("objetivoId", hierarquia.get("objetivoId").toString());
  			avaliacao.put("habilidades", arrayVazia);
  			avaliacao.put("habilidadesOut", arrayVazia);
  			avaliacao.put("resultados", arrayVazia);
  			avaliacao.put("clientesConvitesAceitos", arrayVazia);
  			avaliacao.put("clientesConvitesRecusados", arrayVazia);
				if (testaAvaliacaoFechada(avaliacaoId)){
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
  				commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, null);
  			}else {
  				commons_db.incluirCrud("mapaAvaliacao", criaMapaDoc(hierarquia.get("colaborador").toString(), empresaId, avaliacoesNew));
  			};
  			carregaGestores((ArrayList<String>) avaliacao.get("superiores"), gestores);
			};
		};
		
		
		JSONObject result = new JSONObject();
		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id");
		BasicDBObject avaliacaoDoc = new BasicDBObject();
		avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
		
		result.put("avaliacaoNome", avaliacaoDoc.get("nome"));
		result.put("dataConclusao", avaliacaoDoc.get("dataConclusao"));
		result.put("dataMapa", commons.calcNewDate(avaliacaoDoc.get("dataConclusao").toString(), Integer.valueOf(avaliacaoDoc.get("diasMapa").toString())));
		result.put("gestores", gestores);
		return result;
		
	};

	@SuppressWarnings({ "rawtypes" })
	private void carregaGestores(ArrayList<String> superiores, JSONArray gestores) {
		for (int i = 0; i < superiores.size(); i++) {
			BasicDBObject superior = new BasicDBObject();
			BasicDBObject usuario = commons_db.getCollection(superiores.get(i).toString(), "usuarios", "_id");
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
		
		objArray = (ArrayList<String>) avaliacao.get("parceirosOut");
		carregaMapa (avaliacaoId, parceirosArray, objArray,"out");

		ArrayList<Object> subordinadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("subordinados");
		carregaMapa (avaliacaoId, subordinadosArray, objArray,"in");

		objArray = (ArrayList<String>) avaliacao.get("subordinadosOut");
		carregaMapa (avaliacaoId, subordinadosArray, objArray,"out");
		
		ArrayList<Object> clientesArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientes");
		carregaMapa (avaliacaoId, clientesArray, objArray,"in");
		
		ArrayList<Object> clientesConvitesAceitosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientesConvitesAceitos");
		carregaMapa (avaliacaoId, clientesConvitesAceitosArray, objArray,"in");
		
		ArrayList<Object> clientesConvitesRecusadosArray =  new ArrayList<Object>();
		objArray =  new ArrayList<String>();
		objArray = (ArrayList<String>) avaliacao.get("clientesConvitesRecusados");
		carregaMapa (avaliacaoId, clientesConvitesRecusadosArray, objArray,"in");

		BasicDBObject documentos = new BasicDBObject();
		
		documentos.put("superiores", superioresArray);
		documentos.put("parceiros", parceirosArray);
		documentos.put("subordinados", subordinadosArray);
		documentos.put("clientes", clientesArray);
		documentos.put("clientesConvitesAceitos", clientesConvitesAceitosArray);
		documentos.put("clientesConvitesRecusados", clientesConvitesRecusadosArray);
		if (avaliacao != null) {
  		documentos.put("habilidades", carregaHabilidades(avaliacao, empresaId, usuarioId).get("habilidades"));
  		documentos.put("avaliacoes", carregaAvaliacoes(avaliacao).get("avaliacoes"));
  		documentos.put("objetivo", avaliacao.get("objetivoNome"));
		};
		return documentos;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaHabilidades(BasicDBObject avaliacao, String empresaId, String usuarioId) {
		
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id"); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, empresaId, avaliacao.getString("objetivoId").toString());
		ArrayList<String> habilidadesOut = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			habilidadesOut = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};
		JSONArray habilidades = new JSONArray();
		
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			BasicDBObject habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
			BasicDBObject habilidadeDoc = new BasicDBObject();
			habilidadeDoc.putAll((Map) habilidade.get("documento"));
			if (habilidadeDoc != null) {
				BasicDBObject habilidadeResult = new BasicDBObject();
				habilidadeResult.put("habilidadeId", habilidadeDoc.get("id"));
				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
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
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		return documento;
	}

	public JSONArray carregaAvaliados(String avaliadorId, String avaliacaoId) {
	
		JSONArray avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "auto-avaliacao", "in");
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "superiores", "in");
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "subordinados", "in");
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "parceiros", "in");
		carregaAvaliadosResult(avaliacoesResult, avaliadorId, avaliacaoId, "clientesConvitesAceitos", "in");
		
		return avaliacoesResult;
		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void carregaAvaliadosResult(JSONArray avaliacoesResult, String avaliadorId, String avaliacaoId, String tipo, String inout) {

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
			if (tipo.equals("auto-avaliacao")) {
  			if (avaliacao.get("id").equals(avaliacaoId)) {
    			BasicDBObject habilidades = carregaHabilidadesAvaliacao(avaliado.get("usuarioId").toString(), avaliadorId, avaliacaoId, avaliacao); 
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
    			BasicDBObject habilidades = carregaHabilidadesAvaliacao(avaliado.get("usuarioId").toString(), avaliadorId, avaliacaoId, avaliacao); 
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
	public BasicDBObject carregaHabilidadesAvaliacao(String colaboradorId, String avaliadorId, String avaliacaoId, BasicDBObject avaliacao2) {

		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, colaboradorId);
		ArrayList<String> habilidadesOut = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			habilidadesOut = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};
		BasicDBObject objetivo = new BasicDBObject();
		objetivo = commons_db.getCollection(avaliacao.get("objetivoId").toString(), "objetivos", "documento.id"); 
		BasicDBObject objetivoDoc = new BasicDBObject();
		objetivoDoc.putAll((Map) objetivo.get("documento"));
		ArrayList<String> habilidadesArray = (ArrayList<String>) objetivoDoc.get("necessarios");
		ArrayList<String> habilidadesFinal = commons.montaObjetivoEmpresa(habilidadesArray, avaliacao.get("empresaId").toString(), avaliacao.get("objetivoId").toString());

		JSONArray habilidades = new JSONArray();
		
		String status = "concluído";
		for (int z = 0; z < habilidadesFinal.size(); z++) {
			if (!commons.testaElementoArray(habilidadesArray.get(z), habilidadesOut)) {
  			BasicDBObject habilidade = commons_db.getCollection(habilidadesArray.get(z), "habilidades", "documento.id");
  			BasicDBObject habilidadeDoc = new BasicDBObject();
  			habilidadeDoc.putAll((Map) habilidade.get("documento"));
  			if (habilidadeDoc != null) {
  				BasicDBObject habilidadeResult = new BasicDBObject();
  				habilidadeResult.put("habilidadeId", habilidadeDoc.get("id"));
  				habilidadeResult.put("habilidadeNome", habilidadeDoc.get("nome"));
  				habilidadeResult.put("habilidadeDescricao", habilidadeDoc.get("descricao"));
  				BasicDBObject avaliacaoHabilidade = getAvaliacaoHabilidade(avaliacao, habilidadeDoc.get("id").toString(), avaliadorId);
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
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("habilidades", habilidades);
		documento.put("status", status);
		return documento;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaAvaliacoes(BasicDBObject avaliacao) {
		
		ArrayList<Object> avaliacoesArray = (ArrayList<Object>) avaliacao.get("habilidades");
		ArrayList<String> habilidadesOut = new ArrayList<>();
		if (avaliacao.get("habilidadesOut") != null) {
			habilidadesOut = (ArrayList<String>) avaliacao.get("habilidadesOut");
		};

		JSONArray avaliacoes = new JSONArray();
		
		for (int z = 0; z < avaliacoesArray.size(); z++) {
			BasicDBObject avaliacaoDoc = new BasicDBObject(); 
			avaliacaoDoc.putAll((Map) avaliacoesArray.get(z)); 
			if (!commons.testaElementoArray(avaliacaoDoc.get("id").toString(), habilidadesOut)) {
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
		};
		
		BasicDBObject documento = new BasicDBObject();
		documento.put("avaliacoes", avaliacoes);
		return documento;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject convites(String empresaId, String avaliacaoId, String usuarioId) {
		
		
		JSONArray avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "superiores", "in");

		JSONArray convitesEnviadosPendentes = new JSONArray();
		JSONArray convitesEnviadosAceitos = new JSONArray();
		JSONArray convitesEnviadosRecusados = new JSONArray();			
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			convitesEnviadosPendentes = carregaConvites(convitesEnviadosPendentes, avaliacaoId, avaliado, "clientes");
			convitesEnviadosAceitos = carregaConvites(convitesEnviadosAceitos, avaliacaoId, avaliado,  "clientesConvitesAceitos");
			convitesEnviadosRecusados = carregaConvites(convitesEnviadosRecusados, avaliacaoId, avaliado,  "clientesConvitesRecusados");			
		};

		avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "clientes", "in");

		JSONArray convitesRecebidosPendentes = new JSONArray();
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			BasicDBObject convite = montaConvite(avaliado, usuarioId);
			convitesRecebidosPendentes.add(convite);
		};

		avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "clientesConvitesAceitos", "in");

		JSONArray convitesRecebidosAceitos = new JSONArray();
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			BasicDBObject convite = montaConvite(avaliado, usuarioId);
			convitesRecebidosAceitos.add(convite);
		};

		avaliacoesResult = new JSONArray();
		
		carregaAvaliadosResult(avaliacoesResult, usuarioId, avaliacaoId, "clientesConvitesRecusados", "in");

		JSONArray convitesRecebidosRecusados = new JSONArray();
		for (int i = 0; i < avaliacoesResult.size(); i++) {
			BasicDBObject avaliacao = new BasicDBObject();
			avaliacao.putAll((Map) avaliacoesResult.get(i));
			BasicDBObject avaliado = new BasicDBObject();
			avaliado.putAll((Map) avaliacao.get("avaliado"));
			BasicDBObject convite = montaConvite(avaliado, usuarioId);
			convitesRecebidosRecusados.add(convite);
		};
		
		JSONObject convites = new JSONObject();
		
		convites.put("convitesEnviadosPendentes", convitesEnviadosPendentes);
		convites.put("convitesEnviadosAceitos", convitesEnviadosAceitos);
		convites.put("convitesEnviadosRecusados", convitesEnviadosRecusados);
		convites.put("convitesRecebidosPendentes", convitesRecebidosPendentes);
		convites.put("convitesRecebidosAceitos", convitesRecebidosAceitos);
		convites.put("convitesRecebidosRecusados", convitesRecebidosRecusados);
		
		return convites;
		
	};

	@SuppressWarnings({ "unchecked" })
	private JSONArray carregaConvites(JSONArray arrayResult, String avaliacaoId, BasicDBObject avaliacao, String arrayNome) {
		
		ArrayList<String> convites = (ArrayList<String>) avaliacao.get(arrayNome);
		for (int i = 0; i < convites.size(); i++) {
			if (avaliacao.get("id").equals(avaliacaoId)) {
				if (!testaAvaliacaoFechada(avaliacaoId)){
					BasicDBObject convite = montaConvite(avaliacao, convites.get(i));
    			arrayResult.add(convite);
  			};
			};
		};
		
		return arrayResult;
	};

	private BasicDBObject montaConvite(BasicDBObject avaliacao, String avaliadorId) {
		
		Usuario usuario = new Usuario();
		
		BasicDBObject convite = new BasicDBObject();
		BasicDBObject avaliado = new BasicDBObject();
		avaliado.put("nome", avaliacao.get("colaboradorNome"));
		avaliado.put("email", avaliacao.get("colaboradorEmail"));
		avaliado.put("area", avaliacao.get("colaboradorArea"));
		avaliado.put("id", avaliacao.get("colaboradorId"));
		convite.put("avaliado", avaliado);
		BasicDBObject avaliador = new BasicDBObject();
		avaliador.put("nome", usuario.get(avaliadorId).get("nome"));
		avaliador.put("email", usuario.get(avaliadorId).get("email"));
		avaliador.put("id", avaliadorId);
		convite.put("avaliador", avaliador);
		return convite;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject resultados(String usuarioId, String avaliacaoId) {

		JSONObject result = new JSONObject();
		
		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, usuarioId);
		if (avaliacao != null) {
			result.put("meusresultados", carregaResultadosHabilidades(avaliacao));
		};
		
		JSONArray subordinados = getSubordinados(usuarioId, avaliacaoId);

		JSONArray subordinadosResult = new JSONArray();
		for (int i = 0; i < subordinados.size(); i++) {
			avaliacao = getAvaliacao(avaliacaoId, usuarioId);
			if (avaliacao != null) {
				BasicDBObject subordinadoResult = new  BasicDBObject();
				BasicDBObject subordinado = new  BasicDBObject();
				subordinado.putAll((Map) subordinados.get(i));
				subordinadoResult.put("id", subordinado.get("id"));
				subordinadoResult.put("nome", subordinado.get("nome"));
				avaliacao = getAvaliacao(avaliacaoId, subordinado.get("id").toString());
				if (avaliacao != null) {
					subordinadoResult.put("resultados", carregaResultadosHabilidades(avaliacao));
				};
				subordinadosResult.add(subordinadoResult);
			};			
		};

		result.put("subordinados", subordinadosResult);

		return result;		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONArray getSubordinados(String usuarioId, String avaliacaoId) {
		
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

		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray);

		JSONArray subordinados = new JSONArray();

		JSONArray mapas = (JSONArray) response.getEntity();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = new BasicDBObject();
			mapa.putAll((Map) mapas.get(i));
			if (mapa.get("usuarioId") != null) {
  			BasicDBObject avaliacao = getAvaliacao(avaliacaoId, mapa.get("usuarioId").toString());
  			if (avaliacao.get("id").equals(avaliacaoId)) {
  					BasicDBObject subordinado = new BasicDBObject();
  					subordinado.put("id", mapa.get("usuarioId"));
  					subordinado.put("nome", usuario.get(mapa.get("usuarioId").toString()).get("nome"));
       			subordinados.add(subordinado);
     		};
			};
 		};
		return subordinados;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONArray carregaResultadosHabilidades(BasicDBObject avaliacao) {
		
		ArrayList<Object> resultadosArray = (ArrayList<Object>) avaliacao.get("resultados");

		JSONArray resultados = new JSONArray();
		
		for (int z = 0; z < resultadosArray.size(); z++) {
			BasicDBObject resultado = new BasicDBObject(); 
			resultado.putAll((Map) resultadosArray.get(z)); 
			BasicDBObject habilidade = new BasicDBObject(); 
			habilidade = commons_db.getCollection(resultado.get("id").toString(), "habilidades", "documento.id");
			BasicDBObject habilidadeDoc = new BasicDBObject();
			if (habilidade.get("documento") != null) {
  			habilidadeDoc.putAll((Map) habilidade.get("documento"));
  			if (habilidade != null) {
  				BasicDBObject resultadoResult = new BasicDBObject();
  				resultadoResult.put("habilidadeId", habilidadeDoc.get("id"));
  				resultadoResult.put("habilidadeNome", habilidadeDoc.get("nome"));
  				resultadoResult.put("nota", resultado.get("nota").toString());
  				resultados.add(resultadoResult);
  			};
			};
		};

		return resultados;
		
	};

	@SuppressWarnings({ "rawtypes" })	
	private BasicDBObject getMapa(String usuarioId) {

		BasicDBObject mapa = new BasicDBObject();
		mapa = commons_db.getCollection(usuarioId, "mapaAvaliacao", "documento.usuarioId");
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
	};

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
				BasicDBObject registro = commons_db.getCollection(inArray.get(i).toString(), "usuarios", "_id");
				if (registro != null) {
					BasicDBObject outObj = new BasicDBObject();
					BasicDBObject docObj = (BasicDBObject) registro.get("documento");
					outObj.put("nome", docObj.get("firstName") + " " + docObj.get("lastName"));
					outObj.put("photo", docObj.get("photo"));
					outObj.put("email", docObj.get("email"));
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
	public JSONArray colaboradores(String empresaId, String avaliacaoId, String usuarioId, String perfil)  {

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
		
		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray);
		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray documentos = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
			if (getAvaliacao(avaliacaoId, mapa.getString("usuarioId")).get("id").toString().equals(avaliacaoId)) {
  			BasicDBObject usuario = new BasicDBObject();
  			usuario = commons_db.getCollection(mapa.getString("usuarioId"), "usuarios", "_id");
  			BasicDBObject usuarioDoc = (BasicDBObject) usuario.get("documento");
  			usuarioDoc.remove("password");
  			usuarioDoc.remove("token");
  			usuarioDoc.put("id", mapa.getString("usuarioId"));
  			usuarioDoc.put("objetivo", getAvaliacao(avaliacaoId, mapa.getString("usuarioId")).get("objetivoNome"));
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
  		response = commons_db.listaCrud("mapaAvaliacao", keysArray);
  		mapas = new JSONArray();
  		mapas = (JSONArray) response.getEntity();
  		for (int i = 0; i < mapas.size(); i++) {
  			BasicDBObject mapa = (BasicDBObject) mapas.get(i);
  			if (getAvaliacao(avaliacaoId, mapa.getString("usuarioId")).get("id").toString().equals(avaliacaoId)) {
    			BasicDBObject doc = (BasicDBObject) mapas.get(i);
    			BasicDBObject docUsu = new BasicDBObject();
    			docUsu = commons_db.getCollection(doc.getString("usuarioId"), "usuarios", "_id");
    			BasicDBObject docOut = (BasicDBObject) docUsu.get("documento");
    			docOut.remove("password");
    			docOut.remove("token");
    			docOut.put("id", doc.getString("usuarioId"));
    			docOut.put("objetivo", getAvaliacao(avaliacaoId, doc.getString("usuarioId")).get("objetivoNome"));
    			documentos.add(docOut);
  			};
  		};
		};
		
		return documentos;
	};

	@SuppressWarnings({ })
	public BasicDBObject colaborador(String avaliacaoId, String usuarioId)  {

		if (avaliacaoId == null){
			return null;
		};

		BasicDBObject usuarioDoc = new BasicDBObject();
		BasicDBObject usuario = new BasicDBObject();
		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id");
		if (usuario.get("documento") != null) {
			usuarioDoc = (BasicDBObject) usuario.get("documento");
			usuarioDoc.put("id", usuarioId);
			usuarioDoc.remove("password");
			usuarioDoc.remove("token");
			if (usuarioDoc != null) {
  			usuarioDoc.put("objetivo", getAvaliacao(avaliacaoId, usuarioId).get("objetivoNome"));
			};
		};
		
		return usuarioDoc;
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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
		return true;	
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean montaMapaCliente(String colaboradorId, String colaboradorObjetoId, String empresaId, String avaliacaoId, String status)  {

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
	public Boolean montaHabilidades(String colaboradorId, String empresaId, String avaliacaoId, String habilidadeId)  {
		
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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
		return true;
	
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
	public ArrayList<Object> lista(String empresaId, String usuarioId)  {
	
		BasicDBObject empresa = new BasicDBObject();
		empresa.putAll((Map) commons_db.getCollection(empresaId, "empresas", "_id"));
		BasicDBObject empresaDoc = new BasicDBObject();
		empresaDoc.putAll((Map) empresa.get("documento"));

		JSONArray avaliacoesResult = new JSONArray();

		String lastAvalId = "";
		if (empresaDoc.get("lastAval") != null && !empresaDoc.get("lastAval").equals("none")) {
  		BasicDBObject avaliacaoLast = new BasicDBObject();
  		avaliacaoLast.putAll((Map) commons_db.getCollection(empresaDoc.get("lastAval").toString(), "avaliacoes", "_id"));
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
  		avaliacoesResult.add(avaliacaoResult);
  		lastAvalId = avaliacaoLast.get("_id").toString();
		};
		
		ArrayList<Object> avaliacoes = new ArrayList<Object>(); 
		avaliacoes = commons_db.getCollectionLista(empresaId, "avaliacoes", "documento.empresaId");

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
					avaliacoesResult.add(avaliacaoResult);
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
					avaliacoesResult.add(avaliacaoResult);
				};
			};
		};	
		return avaliacoesResult;
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
	private boolean testaAvaliacaoFechada(String avaliacaoId) {

		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id");
		BasicDBObject avaliacaoDoc = new BasicDBObject();
		avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
		if (commons.calcTime(avaliacaoDoc.get("dataEnvio").toString().replace("-", "")) < commons.calcTime(commons.todaysDate("yyyymmdd"))) {
			return true;
		};
				
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject fechaMapa (String empresaId, String avaliacaoId, String gestorId) {

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

		Response response = commons_db.listaCrud("mapaAvaliacao", keysArray);

		JSONArray mapas = (JSONArray) response.getEntity();
		JSONArray colaboradores = new JSONArray();
		for (int i = 0; i < mapas.size(); i++) {
			BasicDBObject mapa = new BasicDBObject();
			mapa.putAll((Map) mapas.get(i));
			if (mapa.get("usuarioId") != null){
				fechaMapaColaborador(mapa.get("usuarioId").toString(), avaliacaoId, mapa);
	  		JSONObject result = new JSONObject();
	 			result.put("nomeColaborador", usuario.get(mapa.get("usuarioId").toString()).get("nome"));
	 			result.put("emailColaborador", usuario.get(mapa.get("usuarioId").toString()).get("email"));
	 			colaboradores.add(result);
			};
		};
		
		// *** fecha mapa de gestor sem superior
		if (testaSuperiorColaborador(gestorId, avaliacaoId)) {
			BasicDBObject mapa = getMapa(gestorId);
			fechaMapaColaborador(gestorId, avaliacaoId, mapa);			
		};

		BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id");
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
	private boolean testaSuperiorColaborador(String usuarioId, String avaliacaoId) {
		BasicDBObject avaliacao = getAvaliacao(avaliacaoId, usuarioId);
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
	private boolean fechaMapaColaborador(String colaboradorId, String avaliacaoId, BasicDBObject mapa) {

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

		commons_db.atualizarCrud("mapaAvaliacao", fieldsArray, keysArray, documento);
				
		return true;
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject estatisticaMapa(String empresaId, String avaliacaoId) {
		
		JSONObject estatistica = new JSONObject();
		
		
		ArrayList<Object> mapas = commons_db.getCollectionLista(avaliacaoId, "mapaAvaliacao", "documento.avaliacoes.id");
		
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
					if (testaAvaliacaoFechada(avaliacaoId)){
						++mapasFechados;
					}else {
						++mapasAbertos;
					}
					BasicDBObject habilidades = carregaHabilidadesAvaliacao(mapa.getString("usuarioId"), null, avaliacaoId, avaliacao);
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
};
