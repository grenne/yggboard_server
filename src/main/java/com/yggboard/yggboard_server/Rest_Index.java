package com.yggboard.yggboard_server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

	
@Singleton
// @Lock(LockType.READ)
@Path("/index")

public class Rest_Index {
	
	@Path("/obter/itens")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItens(@QueryParam("assunto") String assunto, @QueryParam("id") String id ) throws UnknownHostException, MongoException {
		
		Commons commons = new Commons();

		JSONArray objetivos = new JSONArray();
		JSONArray habilidades = new JSONArray();
		JSONArray cursos = new JSONArray();
		JSONArray areaAtuacao = new JSONArray();
		JSONArray areaConhecimento = new JSONArray();

		if (!assunto.equals("todos")){
			switch (assunto) {
			case "objetivo":
				processaObjetivos(id, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false, null);
				break;
			case "habilidade":
				processaHabilidades(id, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false, null);
				break;
			case "curso":
				processaCursos(id, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false, null);
				break;
			case "areaAtuacao":
				processaAreaAtuacao(id, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false, null);
				break;
			case "areaConhecimento":
				processaAreaConhecimento(id, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false, null);
				break;
			default:
				break;
			};
		}else{
			carregaTudo(objetivos, habilidades, cursos, areaAtuacao, areaConhecimento);
		};
		
		BasicDBObject listas = new BasicDBObject();
		
		listas.put("objetivos", objetivos);
		listas.put("habilidades", habilidades);
		listas.put("cursos", cursos);
		listas.put("areaAtuacao", areaAtuacao);
		listas.put("areaConhecimento", areaConhecimento);
		listas.put("todaysDate", commons.todaysDate("inv_month_number"));
		
		return listas;
			
			// qdo escolhida uma habilidade trazer todos os pré-requisitos, objetivos, cursos, area de atuação da habiidade e área e conhecimento dos objetivos
			//
			// qdo escolhida uma objetivo trazer todas habilidades e seus pré-requisitos, objetivos da mesma aréa de atuação , cursos das habilidades, area de atuação das habilidades e área e conhecimento das habilidades
			//
			// qdo escolhida uma curso trazer todas habilidades, objetivos das habilidades , cursos só ele, area de atuação dos objetivos e área e conhecimento das habilidades
			//
			// qdo escolhida uma area de atuação trazer todos objetivos, as habilidades, cursos das habilidades, area de atuação só a selecionada e área e conhecimento das habilidades
			//
			// qdo escolhida uma area de conhecimento trazer todas habilidades, objetivos das habilidades, cursos das habilidades, area de conhecimento só a selecionada e área de atuação de todos os objetivos
	};
	
	private BasicDBObject carregaTudo(JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areaAtuacao, JSONArray areaConhecimento) {
		
		carregaObjetivos(objetivos);
		carregaHabilidades(habilidades);
		carregaCursos(cursos);
		carregaAreasAtuacao(areaAtuacao);
		carregaAreasConhecimento(areaConhecimento);
		
		BasicDBObject listas = new BasicDBObject();
		
		listas.put("objetivos", objetivos);
		listas.put("habilidades", habilidades);
		listas.put("cursos", cursos);
		listas.put("areaAtuacao", areaAtuacao);
		listas.put("areaConhecimento", areaConhecimento);
		
		return listas;
		
	};

	@SuppressWarnings({ "rawtypes"})
	@Path("/obter/filtro")	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItensFiltro(JSONArray objFiltros) throws MongoException, JsonParseException, JsonMappingException, IOException {
		
		JSONArray objetivos = new JSONArray();
		JSONArray habilidades = new JSONArray();
		JSONArray cursos = new JSONArray();
		JSONArray areaAtuacao = new JSONArray();
		JSONArray areaConhecimento = new JSONArray();

		JSONArray elementosFiltro = new JSONArray();

		Boolean filtro = true;
		
		int sizeFiltros = objFiltros.size();
		if (sizeFiltros < 2 ){
			filtro = false;
		}
			
		for (int i = 0; i < objFiltros.size(); i++) {
			JSONParser parser = new JSONParser(); 
			String documento = JSONObject.toJSONString((Map) objFiltros.get(i));
			JSONObject objItemFiltro;
			try {
				objItemFiltro = (JSONObject) parser.parse(documento);
				switch (objItemFiltro.get("assunto").toString()) {
				case "objetivo":
					processaObjetivos(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, filtro, elementosFiltro);
					break;
				case "habilidade":
					processaHabilidades(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, filtro, elementosFiltro);
					break;
				case "curso":
					processaCursos(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, filtro, elementosFiltro);
					break;
				case "areaAtuacao":
					processaAreaAtuacao(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, filtro, elementosFiltro);
					break;
				case "areaConhecimento":
					processaAreaConhecimento(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, filtro, elementosFiltro);
					break;
				default:
					break;
				};			
			} catch (ParseException e) {
				e.printStackTrace();
			}
		};
/*
		ArrayList arrayListSelecionados = new ArrayList<String>();
		arrayListSelecionados = selecionaFiltro(elementosFiltro);
		Object arraySelecionados[] = arrayListSelecionados.toArray();		
		for (int i = 0; i < arraySelecionados.length; i++) {
			carregaObjetivo(objetivos, arraySelecionados[i].toString());
		};
*/		
		BasicDBObject listas = new BasicDBObject();
		
		listas.put("objetivos", objetivos);
		listas.put("habilidades", habilidades);
		listas.put("cursos", cursos);
		listas.put("areaAtuacao", areaAtuacao);
		listas.put("areaConhecimento", areaConhecimento);
		
		return listas;
			
			// qdo escolhida uma habilidade trazer todos os pré-requisitos, objetivos, cursos, area de atuação da habiidade e área e conhecimento dos objetivos
			//
			// qdo escolhida uma objetivo trazer todas habilidades e seus pré-requisitos, objetivos da mesma aréa de atuação , cursos das habilidades, area de atuação das habilidades e área e conhecimento das habilidades
			//
			// qdo escolhida uma curso trazer todas habilidades, objetivos das habilidades , cursos só ele, area de atuação dos objetivos e área e conhecimento das habilidades
			//
			// qdo escolhida uma area de atuação trazer todos objetivos, as habilidades, cursos das habilidades, area de atuação só a selecionada e área e conhecimento das habilidades
			//
			// qdo escolhida uma area de conhecimento trazer todas habilidades, objetivos das habilidades, cursos das habilidades, area de conhecimento só a selecionada e área de atuação de todos os objetivos
	};

	@SuppressWarnings("rawtypes")
	private JSONArray selecionaFiltro(JSONArray matrizFiltro) {

		Object matriz[] = matrizFiltro.toArray();
		ArrayList arrayElemento_0 = new ArrayList<String>();
		arrayElemento_0 = (ArrayList) matrizFiltro.get(0);				
		Object elemento_0[] = arrayElemento_0.toArray();
		
		for (int z = 0; z < elemento_0.length; z++) {
			Boolean coincide = true;
			for (int i = 1; i < matriz.length && coincide; i++) {		
				ArrayList arrayElementos = new ArrayList<String>();
				arrayElementos = (ArrayList) matriz[i];		
				Object elementos[] = arrayElementos.toArray();
				Boolean coincideElemento = false;
				for (int j = 0; j < elementos.length; j++) {
					if (elementos[j].equals(elemento_0[z]) ){
						coincideElemento = true;
					}
				}
				if (!coincideElemento){
					coincide = false;
				}
			};
		}
		
		
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private void processaObjetivos(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("objetivos");
			BasicDBObject searchQuery = new BasicDBObject();
			if (filtro){
				searchQuery = new BasicDBObject("documento.id", id);
			};
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objetivo = (BasicDBObject) cursor.get("documento");

				ArrayList<?> arrayListNecessarios = new ArrayList<Object>(); 
		    	arrayListNecessarios = (ArrayList<?>) objetivo.get("necessarios");
				
				ArrayList<?> arrayListAreaAtuacao = new ArrayList<Object>(); 
				arrayListAreaAtuacao = (ArrayList<?>) objetivo.get("areaAtuacao");

		    	if (!filtro){
					if (arrayListNecessarios != null && habilidades != null){
						Object arrayNecessarios[] = arrayListNecessarios.toArray();
						//
						// ***		carrega habilidades
						//
						int z = 0;
						while (z < arrayNecessarios.length) {
							processaHabilidades(arrayNecessarios[z].toString(), null, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
							++z;
						};
					};
					if (arrayListAreaAtuacao != null && areasAtuacao != null){
						Object arrayAreaAtuacao[] = arrayListAreaAtuacao.toArray();
						//
						// ***		carrega área de atuação
						//			
						int w = 0;
						while (w < arrayAreaAtuacao.length) {
							carregaAreaAtuacao(areasAtuacao, arrayAreaAtuacao[w].toString());
							++w;
						};
					};
					//
					// ***		carrega objetivo
					//			
					if (addObjeto(objetivos, objetivo)){
						objetivos.add(objetivo);
					};
				}else{
					//
					// ***		carrega filtros
					//
					JSONObject elemento = new JSONObject();
					if (arrayListNecessarios != null  && habilidades != null){
						Object arrayNecessarios[] = arrayListNecessarios.toArray();
						elemento.put("elemento", "objetivo");
						elemento.put("value", id);
						elemento.put("habilidades", arrayNecessarios);
						elementosFiltro.add(elemento);
					};
					elemento.clear();
					if (arrayListAreaAtuacao != null  && areasAtuacao != null){
						Object arrayAreaAtuacao[] = arrayListAreaAtuacao.toArray();
						elemento.put("elemento", "objetivo");
						elemento.put("value", id);
						elemento.put("areaAtuacao", arrayAreaAtuacao);
						elementosFiltro.add(elemento);
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e1) {
			e1.printStackTrace();
		};
	};

	@SuppressWarnings("unchecked")
	private void processaHabilidades(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");
				//
				// ***		array objetivos
				//			
				ArrayList<?> arrayListObjetivos = new ArrayList<Object>(); 
		    	arrayListObjetivos = (ArrayList<?>) habilidade.get("objetivos");
				//
				// ***		array cursos
				//			
				ArrayList<?> arrayListCursos = new ArrayList<Object>(); 
		    	arrayListCursos = (ArrayList<?>) habilidade.get("cursos");
				//
				// ***		array area conhecimento
				//			
				ArrayList<?> arrayListAreaConhecimento = new ArrayList<Object>(); 
				arrayListAreaConhecimento = (ArrayList<?>) habilidade.get("areaConhecimento");

				if (!filtro){
			    	if (arrayListCursos != null && cursos != null){
				    	Object[] arrayCursos = arrayListCursos.toArray();
						//
						// ***		carrega cursos
						//
						int i = 0;
						while (i < arrayCursos.length) {
							carregaCurso(cursos, arrayCursos[i].toString());
							++i;
						};
			    	};
					//
					// ***		carrega objetivos
					//
			    	if (arrayListObjetivos != null  && objetivos != null){
				    	Object arrayObjetivos[] = arrayListObjetivos.toArray();
						int w = 0;
						while (w < arrayObjetivos.length) {
							processaObjetivos(arrayObjetivos[w].toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
							++w;
						};
			    	};
					//
					// ***		carrega área de conhecimento
					//			
					if (arrayListAreaConhecimento != null && areasConhecimento != null){
						Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
						int w = 0;
						while (w < arrayAreaConhecimento.length) {
							carregaAreaConhecimento(areasConhecimento, arrayAreaConhecimento[w].toString());
							++w;
						};
					};
						//
						// ***		carrega habilidade
						//			
					carregaHabilidade(habilidades, cursos, objetivos, areasAtuacao, areasConhecimento, id, true, null, filtro, elementosFiltro);
	
				}else{
					JSONObject elemento = new JSONObject();
					//
					// ***		carrega filtros
					//			
			    	if (arrayListCursos != null   && cursos != null){
				    	Object[] arrayCursos = arrayListCursos.toArray();
						elemento.put("elemento", "habilidade");
						elemento.put("value", id);
						elemento.put("cursos", arrayCursos);
						elementosFiltro.add(elemento);
			    	};
					elemento.clear();
			    	if (arrayListObjetivos != null && objetivos != null){
				    	Object arrayObjetivos[] = arrayListObjetivos.toArray();
						elemento.put("elemento", "habilidade");
						elemento.put("value", id);
						elemento.put("objetivos", arrayObjetivos);
						elementosFiltro.add(elemento);
			    	};
					elemento.clear();
			    	if (arrayListAreaConhecimento != null  && areasConhecimento != null){
				    	Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
						elemento.put("elemento", "habilidade");
						elemento.put("value", id);
						elemento.put("areaConhecimento", arrayAreaConhecimento);
						elementosFiltro.add(elemento);
			    	};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e1) {
			e1.printStackTrace();
		};
	
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processaCursos(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("cursos");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject curso = (BasicDBObject) cursor.get("documento");
				ArrayList<?> arrayListHabilidades = new ArrayList<Object>(); 
				arrayListHabilidades = (ArrayList<?>) curso.get("habilidades");
		    	if (!filtro){
					List arrayParent = (List) curso.get("parents");
					if (arrayParent.size() == 0){
						if (addObjeto(cursos, curso)){
							cursos.add(cursor.get("documento"));
						};
					};
					if (arrayListHabilidades != null && habilidades != null){
				    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
						//
						// ***		carrega habilidades
						//
						int z = 0;
						while (z < arrayHabilidades.length) {
							processaHabilidades(arrayHabilidades[z].toString(), objetivos, habilidades, null, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
							++z;
						};
					};
		    	}else{
					if (arrayListHabilidades != null && habilidades != null){
				    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
						//
						// ***		carrega filtros
						//			
						JSONObject elemento = new JSONObject();
						elemento.put("elemento", "curso");
						elemento.put("value", id);
						elemento.put("habilidades", arrayHabilidades);
						elementosFiltro.add(elemento);
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e1) {
			e1.printStackTrace();
		}
	};

	@SuppressWarnings("unchecked")
	private void processaAreaConhecimento(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro) {

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("areaConhecimento");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject areaConhecimento = (BasicDBObject) cursor.get("documento");
				ArrayList<?> arrayListHabilidades = new ArrayList<Object>(); 
				arrayListHabilidades = (ArrayList<?>) areaConhecimento.get("habilidades");
				if (!filtro){
					//
					// ***		carrega areas conhecimento
					//			
					if (addObjeto(areasConhecimento, areaConhecimento)){
						areasConhecimento.add(areaConhecimento);
					};
					if (arrayListHabilidades != null && habilidades != null){
						Object arrayHabilidades[] = arrayListHabilidades.toArray();
						//
						// ***		carrega habilidades
						//
						int z = 0;
						while (z < arrayHabilidades.length) {
							processaHabilidades(arrayHabilidades[z].toString(), objetivos, habilidades, cursos, areasAtuacao, null, filtro, elementosFiltro);							carregaHabilidade(habilidades, cursos, objetivos, areasAtuacao, areasConhecimento, arrayHabilidades[z].toString(), true, null, filtro, elementosFiltro);
							++z;
						};
					};
				}else{
					if (arrayListHabilidades != null  && habilidades != null){
						Object arrayHabilidades[] = arrayListHabilidades.toArray();
						//
						// ***		carrega filtros
						//			
						JSONObject elemento = new JSONObject();
						elemento.put("elemento", "areaConhecimento");
						elemento.put("value", id);
						elemento.put("habilidades", arrayHabilidades);
						elementosFiltro.add(elemento);
					};
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
	};
	@SuppressWarnings("unchecked")
	private void processaAreaAtuacao(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro) {		

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("areaAtuacao");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject areaAtuacao = (BasicDBObject) cursor.get("documento");
				//
				// ***		carrega objetivos
				//			
				ArrayList<?> arrayListObjetivos = new ArrayList<Object>(); 
				arrayListObjetivos = (ArrayList<?>) areaAtuacao.get("objetivos");
				if (!filtro){
					//
					// ***		carrega areas atuacao
					//			
					if (addObjeto(areasAtuacao, areaAtuacao)){
						areasAtuacao.add(areaAtuacao);
					};
					if (arrayListObjetivos != null && objetivos != null){
						Object arrayObjetivos[] = arrayListObjetivos.toArray();
						//
						// ***		carrega objetivos
						//
						int z = 0;
						while (z < arrayObjetivos.length) {
							processaObjetivos(arrayObjetivos[z].toString(), objetivos, habilidades, cursos, null, areasConhecimento, filtro, elementosFiltro);
							++z;
						};
					};
				}else{
					if (arrayListObjetivos != null  && objetivos != null){
						Object arrayObjetivos[] = arrayListObjetivos.toArray();
						//
						// ***		carrega filtros
						//			
						JSONObject elemento = new JSONObject();
						elemento.put("elemento", "areaAtuacao");
						elemento.put("value", id);
						elemento.put("objetivos", arrayObjetivos);
						elementosFiltro.add(elemento);
					};
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
	};


	@SuppressWarnings("unchecked")
	private void carregaAreasConhecimento(JSONArray areasConhecimento) {

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("areaConhecimento");
			BasicDBObject searchQuery = new BasicDBObject();
			DBCursor cursor = collection.find(searchQuery);
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject objAreaConhecimento = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				//
				// ***		carrega areas atuacao
				//			
				if (addObjeto(areasConhecimento, objAreaConhecimento)){
					areasConhecimento.add(objAreaConhecimento);
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
		
	}

	@SuppressWarnings("unchecked")
	private void carregaAreasAtuacao(JSONArray areasAtuacao) {

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("areaAtuacao");
			BasicDBObject searchQuery = new BasicDBObject();
			DBCursor cursor = collection.find(searchQuery);
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject objAreaAtuacao = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				//
				// ***		carrega areas atuacao
				//			
				if (addObjeto(areasAtuacao, objAreaAtuacao)){
					areasAtuacao.add(objAreaAtuacao.get("documento"));
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void carregaCursos(JSONArray cursos) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("cursos");
			BasicDBObject searchQuery = new BasicDBObject();
			BasicDBObject setSort = new BasicDBObject();
			setSort.put("documento.classificacao", 1);
			DBCursor cursor = collection.find(searchQuery).sort(setSort);
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject objCursos = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				//
				// ***		carrega cursos
				//			
				if (addObjeto(cursos, objCursos)){
					BasicDBObject curso = (BasicDBObject) objCursos.get("documento");
					List arrayParent = (List) curso.get("parents");
					if (arrayParent.size() == 0){
						cursos.add(curso);
					};
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
		
	};

	@SuppressWarnings("unchecked")
	private void carregaHabilidades(JSONArray habilidades) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject();
			DBCursor cursor = collection.find(searchQuery);
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject objHabilidades = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				//
				// ***		carrega habilidades
				//			
				if (addObjeto(habilidades, objHabilidades)){
					BasicDBObject habilidade = (BasicDBObject) objHabilidades.get("documento");
					habilidades.add(habilidade);
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
		
	};

	@SuppressWarnings("unchecked")
	private void carregaObjetivos(JSONArray objetivos) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			DBCollection collection = db.getCollection("objetivos");
			BasicDBObject searchQuery = new BasicDBObject();
			BasicDBObject setSort = new BasicDBObject();
			setSort.put("documento.classificacao", 1);
			DBCursor cursor = collection.find(searchQuery).sort(setSort);
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject objCarreiras = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				//
				// ***		carrega objetivo
				//			
				if (addObjeto(objetivos, objCarreiras)){
					BasicDBObject objetivo = (BasicDBObject) objCarreiras.get("documento");				
					//
					// ***		carrega objetivo
					//			
					if (addObjeto(objetivos, objetivo)){
						objetivos.add(objetivo);
					};
				};
			};
			mongo.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		};
		
	};

	@SuppressWarnings("unchecked")
	private void carregaHabilidade(JSONArray habilidades, JSONArray cursos, JSONArray objetivos, JSONArray areasAtuacao,
			JSONArray areasConhecimento, String id, Boolean carregaPreRequisitos, Object nomeHabilidade, Boolean filtro,
			JSONArray elementosFiltro) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");

				ArrayList<?> arrayList = new ArrayList<Object>(); 
		    	arrayList = (ArrayList<?>) habilidade.get("preRequisitos");

		    	if (arrayList != null){
		    		Object arrayPreRequisitos[] = arrayList.toArray();
					nomeHabilidade = habilidade.get("nome").toString();
					habilidade.put ("nivel", "0");
					//
					// ***		carrega habilidade
					//			
					if (addObjeto(habilidades, habilidade)){
						habilidades.add (habilidade);
					};
					//
					// ***		carrega pré requisitos
					//			
					if (carregaPreRequisitos){
						int z = 0;
						while (z < arrayPreRequisitos.length) {
//							BasicDBObject preRequisito = (BasicDBObject) array[z];
//							carregaPreRequisitos(habilidades, cursos, objetivos, areaAtuacao, areaConhecimento, (String) preRequisito.get("id"), (String) preRequisito.get("nivel"), filtro );
							String preRequisito =arrayPreRequisitos[z].toString();
							carregaPreRequisitos(habilidades, cursos, objetivos, areasAtuacao, areasConhecimento, (String) preRequisito, "0", filtro );
							++z;
						};
					};
	    		};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
	};

	@SuppressWarnings("unchecked")
	private void carregaPreRequisitos(JSONArray habilidades, JSONArray cursos,JSONArray objetivos, JSONArray areasAtuacao,JSONArray areasConhecimento, String id, String nivel, Boolean filtro) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");
				habilidade.put ("nivel", nivel);
				if (addObjeto(habilidades, habilidade)){
					habilidades.add (habilidade);
				};
				//
				// ***		carrega cursos
				//
				ArrayList<?> arrayListCursos = new ArrayList<Object>(); 
		    	arrayListCursos = (ArrayList<?>) habilidade.get("cursos");
		    	if (arrayListCursos != null){
			    	Object arrayCursos[] = arrayListCursos.toArray();
					int z = 0;
					while (z < arrayCursos.length) {
						String curso = arrayCursos[z].toString();
						carregaCurso(cursos, curso);
						++z;
					};
		    	};
				//
				// ***		carrega objetivos
				//
				ArrayList<?> arrayListObjetivo = new ArrayList<Object>(); 
		    	arrayListObjetivo = (ArrayList<?>) habilidade.get("objetivos");
		    	if (arrayListObjetivo != null){
			    	Object arrayObjetivo[] = arrayListObjetivo.toArray();
					int w = 0;
					while (w < arrayObjetivo.length) {
						String objetivo = arrayObjetivo[w].toString();
						carregaObjetivo(objetivos, areasAtuacao, objetivo);
						++w;
					};
		    	};
				//
				// ***		carrega área de conhecimento
				//			
				ArrayList<?> arrayListAreaConhecimento = new ArrayList<Object>(); 
				arrayListAreaConhecimento = (ArrayList<?>) habilidade.get("areaConhecimento");
				if (arrayListAreaConhecimento != null){
					Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
					int w = 0;
					while (w < arrayAreaConhecimento.length) {
						carregaAreaConhecimento(areasConhecimento, arrayAreaConhecimento[w].toString());
						++w;
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void carregaCurso(JSONArray cursos, String id) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("cursos");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject curso = (BasicDBObject) cursor.get("documento");
				if (addObjeto(cursos, curso)){
					List arrayParent = (List) curso.get("parents");
					if (arrayParent.size() == 0){
						cursos.add (curso);
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};
	
	@SuppressWarnings("unchecked")
	private void carregaAreaAtuacao(JSONArray areasAtuacao, String id) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("areaAtuacao");
			BasicDBObject searchQuery = new BasicDBObject("documento.nome", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject areaAtuacao = (BasicDBObject) cursor.get("documento");
				if (addObjeto(areasAtuacao, areaAtuacao)){
					areasAtuacao.add (areaAtuacao);
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};
	@SuppressWarnings("unchecked")
	private void carregaAreaConhecimento(JSONArray areasConhecimento, String id) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("cursos");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject areaConhecimento = (BasicDBObject) cursor.get("documento");
				if (addObjeto(areasConhecimento, areaConhecimento)){
					areasConhecimento.add (areaConhecimento);
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};

	@SuppressWarnings("unchecked")
	private void carregaObjetivo(JSONArray objetivos, JSONArray areasAtuacao, String id) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("objetivos");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objetivo = (BasicDBObject) cursor.get("documento");
				//
				// ***		carrega objetivo
				//			
				if (addObjeto(objetivos, objetivo)){
					objetivos.add(objetivo);
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};

	private boolean addObjeto(JSONArray array, BasicDBObject elemento) {

		if (array != null){
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i).equals(elemento)){
					return false;
				};
			};
		}else{
			return false;
		};
		return true;
	};

	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterCursos(@QueryParam("characters") String characters, @QueryParam("planejamentoLista") String planejamentoLista) {
		
		BasicDBObject listas = new BasicDBObject();
		if (planejamentoLista.equals("true")){			
			JSONArray objetivos = new JSONArray();
			JSONArray habilidades = new JSONArray();
			JSONArray cursos = new JSONArray();
			JSONArray areaAtuacao = new JSONArray();
			JSONArray areaConhecimento = new JSONArray();
			carregaIndex("Objetivo", objetivos, characters, true, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false);
			carregaIndex("Habilidade", habilidades, characters, true, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false);
			carregaIndex("Curso", cursos, characters, true, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false);
			carregaIndex("Área Atuação", areaAtuacao, characters, true, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false);
			carregaIndex("Área Conhecimento", areaConhecimento, characters, true, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false);
			listas.put("objetivos", objetivos);
			listas.put("habilidades", habilidades);
			listas.put("cursos", cursos);
			listas.put("areaAtuacao", areaAtuacao);
			listas.put("areaConhecimento", areaConhecimento);
		}else{
			if (planejamentoLista.equals("objetivos")){			
				JSONArray objetivos = new JSONArray();
				JSONArray habilidades = new JSONArray();
				JSONArray cursos = new JSONArray();
				JSONArray areaAtuacao = new JSONArray();
				JSONArray areaConhecimento = new JSONArray();
				carregaIndex("Objetivo", objetivos, characters, true, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false);
				listas.put("objetivos", objetivos);
			}else{
				JSONArray documentos = new JSONArray();
				carregaIndex("Objetivo", documentos, characters, false, null, null, null, null, null, false);
				carregaIndex("Habilidade", documentos, characters, false, null, null, null, null, null, false);;
				carregaIndex("Curso", documentos, characters, false, null, null, null, null, null, false);
				carregaIndex("Área Atuação", documentos, characters, false, null, null, null, null, null, false);
				carregaIndex("Área Conhecimento", documentos, characters, false, null, null, null, null, null,false);
				listas.put("pesquisa", documentos);
			};
		};
		return listas;			
	};

	@SuppressWarnings("unchecked")
	private void carregaIndex(String assunto, JSONArray documentos, String characters, Boolean lista, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro) {
		Mongo mongo;
			try {
				mongo = new Mongo();
				DB db = (DB) mongo.getDB("yggboard");
				BasicDBObject setQuery = new BasicDBObject();
				DBCollection collection = db.getCollection("index");
				setQuery.put("documento.assunto", assunto);			
				DBCursor cursor = collection.find(setQuery);
				int i = 0;
				while (((Iterator<DBObject>) cursor).hasNext()) {
					JSONParser parser = new JSONParser(); 
					BasicDBObject objUserPerfil = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
					String documento = objUserPerfil.getString("documento");
					try {
						JSONObject jsonObject; 
						jsonObject = (JSONObject) parser.parse(documento);
						JSONObject jsonDocumento = new JSONObject();
						String [] wordsSource = limpaChar (characters).split(" ");
						List<?> wordsCompare = (List<?>) jsonObject.get("texto");
						if (wordsoK (wordsSource, wordsCompare)){
							if (lista){
								switch (assunto) {
								case "Objetivo":
									processaObjetivos(jsonObject.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
									break;
								case "Habilidade":
									processaHabilidades(jsonObject.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
								break;
								case "Curso":
									processaCursos(jsonObject.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
								break;
								case "Área Atuação":
									processaAreaAtuacao(jsonObject.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
								break;
								case "Área Conhecimento":
									processaAreaConhecimento(jsonObject.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
								break;

								default:
									break;
								}
							}else{
								jsonDocumento.put("assunto", jsonObject.get("assunto"));
								jsonDocumento.put("entidade", jsonObject.get("entidade"));
								jsonDocumento.put("id", jsonObject.get("id"));
								jsonDocumento.put("descricao", jsonObject.get("descricao"));
								documentos.add(jsonDocumento);
								if (i > 3){
									mongo.close();
									return;
								};
							};
							++i;
						};
					} catch (ParseException e) {
						e.printStackTrace();
					}
				};		
			} catch (UnknownHostException | MongoException e1) {
				e1.printStackTrace();
			}
	};
	
	private boolean wordsoK(String[] wordsSource, List<?> wordsCompare) {
		int i = 0;
		int palavraIgual = 0;
		while (i < wordsSource.length) {
			char[] letrasSource = wordsSource[i].toCharArray();
			if (letrasSource.length > 0){
				int w = 0;
				Boolean achouPalavra = false;
				while (w < wordsCompare.size()) {
					int charIgual = 0;
					char[] letrasCompare = limpaChar((String) wordsCompare.get(w)).toCharArray();
					if (letrasCompare.length > 0 && !achouPalavra){
						int z = 0;
						int jSalvo = 0;
						if (letrasSource[0] == letrasCompare[0]){
							while (z < letrasSource.length) {
								int j = jSalvo;
								Boolean letraIgual = false;
								int pulaLetra = 0;
								while (j < letrasCompare.length) {
									if (letrasSource[z] == letrasCompare[j]){
										if (!letraIgual && pulaLetra < 2){
											++charIgual;
											letraIgual = true;
											jSalvo = j;
										};
									}else{
										++pulaLetra;
									};
									++j;
								};
								++z;
							};
						};
						if (charIgual != 0 && !achouPalavra) {
							if (((charIgual / letrasSource.length) * 100) > 70){
								++palavraIgual;
								achouPalavra = true;
								if (palavraIgual >= wordsSource.length){
									return true;	
								};
							};
						};
					};
					++w;
				};	
			};
			++i;
		};
		if (palavraIgual >= wordsSource.length){
			return true;	
		};
		return false;
	}
	private String limpaChar(String characters) {
		characters = characters.toLowerCase();
		switch (characters) {
		case "ã":
			characters = "a";
	        break;
		case "à":
			characters = "a";
	        break;
		case "á":
	        characters = "a";
	        break;
		case "â":
	        characters = "a";
	        break;
	    case "é":
	    	characters = "e";
	        break;
	    case "ê":
	    	characters = "e";
	        break;
	    case "í":
	    	characters = "i";
	        break;
	    case "ô":
	    	characters = "o";
	        break;
	    case "õ":
	    	characters = "o";
	        break;
	    case "ó":
	    	characters = "o";
	        break;
	    case "ú":
	    	characters = "u";
	        break;
	    case "ç":
	    	characters = "c";
	        break;
		default:
			break;
		};
		return characters;

	};

};
