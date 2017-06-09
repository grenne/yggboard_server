package com.yggboard.yggboard_server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
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
				processaHabilidades(id, objetivos, habilidades, cursos, areaAtuacao, areaConhecimento, false, null, true);
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

	@SuppressWarnings({ "rawtypes", "unchecked"})
	@Path("/obter/filtro")	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItensFiltro(JSONArray objFiltros) throws MongoException, JsonParseException, JsonMappingException, IOException {
		
		JSONArray objetivos = new JSONArray();
		JSONArray habilidades = new JSONArray();
		JSONArray cursos = new JSONArray();
		JSONArray areasAtuacao = new JSONArray();
		JSONArray areasConhecimento = new JSONArray();

		JSONArray elementosFiltro = new JSONArray();

		Boolean filtro = true;
		
		int sizeFiltros = objFiltros.size();
		if (sizeFiltros < 2 ){
			filtro = false;
		};
			
		for (int i = 0; i < objFiltros.size(); i++) {
			JSONObject objItemFiltro = new JSONObject();
			objItemFiltro.putAll((Map) objFiltros.get(i));
			switch (objItemFiltro.get("assunto").toString()) {
			case "objetivo":
				processaObjetivos(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
				if (filtro){
					processaObjetivos(objItemFiltro.get("id").toString(), objetivos, null, null, areasAtuacao, areasConhecimento, false, null);
				};
				break;
			case "habilidade":
				processaHabilidades(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro, true);
				if (filtro){
					processaHabilidades(objItemFiltro.get("id").toString(), null, habilidades, null, null, areasConhecimento, false, null, false);
				};
				break;
			case "curso":
				processaCursos(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
				if (filtro){
					processaCursos(objItemFiltro.get("id").toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, false, null);
				};
				break;
			case "areaAtuacao":
				processaAreaAtuacao(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
				if (filtro){
					processaAreaAtuacao(objItemFiltro.get("id").toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, false, null);
				};
				break;
			case "areaConhecimento":
				processaAreaConhecimento(objItemFiltro.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
				if (filtro){
					processaAreaConhecimento(objItemFiltro.get("id").toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, false, null);
				};
				break;
			default:
				break;
			};			
		};
		
		if (filtro){
			Object filtros[] = objFiltros.toArray();
			Boolean filtroObjetivo = false;
			Boolean filtroHabilidade = false;
			Boolean filtroCurso = false;
			Boolean filtroAreaAtuacao = false;
			Boolean filtroAreaConhecimento = false;
			for (int i = 0; i < filtros.length; i++) {
				JSONObject filtroObj = new JSONObject();
				filtroObj.putAll((Map) filtros[i]);				
				switch (filtroObj.get("assunto").toString()) {
				case "objetivo":
					filtroObjetivo = true;
					break;
				case "habilidade":
					filtroHabilidade = true;
					break;
				case "curso":
					filtroCurso = true;
					break;
				case "areaAtuacao":
					filtroAreaAtuacao = true;
					break;
				case "areaConhecimento":
					filtroAreaConhecimento = true;
					break;
				default:
					break;
				};								
			};
			Object elementos[] = elementosFiltro.toArray();
			for (int i = 0; i < elementos.length; i++) {
				if (selecionaFiltro(elementos[i], objFiltros)){
					JSONObject selecionado = new JSONObject();
					selecionado.putAll((Map) elementos[i]);
					switch (selecionado.get("assunto").toString()) {
					case "objetivo":
						if (filtroHabilidade && filtroAreaAtuacao){
							processaObjetivos(selecionado.get("value").toString(), objetivos, null, cursos, null, areasConhecimento, false, null);
						}else{
							if (filtroHabilidade){
								processaObjetivos(selecionado.get("value").toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, false, null);
							}else{
								if (filtroAreaAtuacao){
									processaObjetivos(selecionado.get("value").toString(), objetivos, habilidades, cursos, null, areasConhecimento, false, null);
								}else{
									processaObjetivos(selecionado.get("value").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
								};
							};
						};
						break;
					case "habilidade":
						if (filtroCurso && filtroObjetivo && filtroAreaConhecimento){
							processaHabilidades(selecionado.get("value").toString(), null, habilidades, null, areasAtuacao, null, false, null, false);
						}else{
							if (filtroCurso && filtroObjetivo){
								processaHabilidades(selecionado.get("value").toString(), null, habilidades, null, areasAtuacao, areasConhecimento, false, null, false);
							}else{
								if (filtroCurso && filtroAreaConhecimento){
									processaHabilidades(selecionado.get("value").toString(), objetivos, habilidades, null, areasAtuacao, null, false, null, false);
								}else{
									if (filtroObjetivo && filtroAreaConhecimento){
										processaHabilidades(selecionado.get("value").toString(), null, habilidades, cursos, areasAtuacao, null, false, null, false);
									}else{
										if (filtroCurso){
											processaHabilidades(selecionado.get("value").toString(), objetivos, habilidades, null, areasAtuacao, areasConhecimento, false, null, false);										
										}else{
											if (filtroObjetivo){
												processaHabilidades(selecionado.get("value").toString(), null, habilidades, cursos, areasAtuacao, areasConhecimento, false, null, false);										
											}else{
												if (filtroAreaConhecimento){
													processaHabilidades(selecionado.get("value").toString(), objetivos, habilidades, cursos, areasAtuacao, null, false, null, false);										
												}else{
													processaHabilidades(selecionado.get("value").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null, false);																							
												}												
											}
										}
									};
								};
							};
						};
						break;
					case "curso":
						if (filtroHabilidade){
							processaCursos(selecionado.get("value").toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, false, null);
						}else{
							processaCursos(selecionado.get("value").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);							
						};
						break;
					case "areaAtuacao":
						if (filtroObjetivo){
							processaAreaAtuacao(selecionado.get("value").toString(), null, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);
						}else{
							processaAreaAtuacao(selecionado.get("value").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null);							
						};
						break;
					case "areaConhecimento":
						if (filtroHabilidade){
							processaAreaConhecimento(selecionado.get("value").toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);
						}else{
							processaAreaConhecimento(selecionado.get("value").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro);							
						};
						break;
					default:
						break;
					};								
				};
			};
		};

//		if (filtro){
//			BasicDBObject filtros = new BasicDBObject();
//			filtros.put("filtros", elementosFiltro);
//			return filtros;
//		}

		BasicDBObject listas = new BasicDBObject();
		
		listas.put("objetivos", objetivos);
		listas.put("habilidades", habilidades);
		listas.put("cursos", cursos);
		listas.put("areaAtuacao", areasAtuacao);
		listas.put("areaConhecimento", areasConhecimento);
		
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Boolean selecionaFiltro(Object elemento, JSONArray objFiltros) {
		
		Object filtros[] = objFiltros.toArray();
		Boolean selecionado = true;
		Boolean testouElemento = false;
		for (int i = 0; i < filtros.length; i++) {
			JSONObject filtro = new JSONObject();
			filtro.putAll((Map) filtros[i]);
			JSONObject elementoJson = new JSONObject();
			elementoJson.putAll((Map) elemento);
			if (elementoJson.get("elemento").toString().equals(filtro.get("assunto"))){
				testouElemento = true;
				JSONArray elementos = new JSONArray();
				elementos.addAll((Collection) elementoJson.get("elementos"));
				Boolean filtroSelecionado = false;
				for (int j = 0; j < elementos.size(); j++) {
					if (filtro.get("id").equals(elementos.get(j).toString())){
						filtroSelecionado = true;
					};
				};
				if (!filtroSelecionado){
					selecionado = false;
				};
			};
		};
		if (!testouElemento){
			selecionado = false;			
		};
		return selecionado;
	}

	@SuppressWarnings({ "unchecked" })
	private void processaObjetivos(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("objetivos");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objetivo = (BasicDBObject) cursor.get("documento");
				ArrayList<?> arrayListNecessarios = new ArrayList<Object>(); 
		    	arrayListNecessarios = (ArrayList<?>) objetivo.get("necessarios");
				
				ArrayList<?> arrayListAreaAtuacao = new ArrayList<Object>(); 
				arrayListAreaAtuacao = (ArrayList<?>) objetivo.get("areaAtuacao");

				if (arrayListNecessarios != null && habilidades != null){
					Object arrayNecessarios[] = arrayListNecessarios.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayNecessarios.length) {
						processaHabilidades(arrayNecessarios[z].toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro, true);
						++z;
					};
				};

				if (!filtro){
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
					if (arrayListNecessarios != null){
						BasicDBObject elemento = new BasicDBObject();
						Object arrayNecessarios[] = arrayListNecessarios.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayNecessarios.length; i++) {
							arrayJson.add(arrayNecessarios[i]);
						};
						elemento.put("elemento", "habilidade");
						elemento.put("assunto", "objetivo");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
					};
					if (arrayListAreaAtuacao != null){
						BasicDBObject elemento = new BasicDBObject();
						Object arrayAreaAtuacao[] = arrayListAreaAtuacao.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayAreaAtuacao.length; i++) {
							processaAreaAtuacao(arrayAreaAtuacao[i].toString(), null, habilidades, cursos, areasAtuacao, areasConhecimento, true, elementosFiltro);
							arrayJson.add(arrayAreaAtuacao[i].toString());
						};
						elemento.put("elemento", "areaAtuacao");
						elemento.put("assunto", "objetivo");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e1) {
			e1.printStackTrace();
		};
	};

	@SuppressWarnings("unchecked")
	private void processaHabilidades(String id, JSONArray objetivos, JSONArray habilidades, JSONArray cursos, JSONArray areasAtuacao, JSONArray areasConhecimento, Boolean filtro, JSONArray elementosFiltro, Boolean carregaPreRequisitos) {
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
					carregaHabilidade(habilidades, cursos, objetivos, areasAtuacao, areasConhecimento, id, carregaPreRequisitos, null, filtro, elementosFiltro);
	
				}else{
					//
					// ***		carrega filtros
					//			
			    	if (arrayListCursos != null){
						BasicDBObject elemento = new BasicDBObject();
				    	Object[] arrayCursos = arrayListCursos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayCursos.length; i++) {
							processaCursos(arrayCursos[i].toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, true, elementosFiltro);
							arrayJson.add(arrayCursos[i].toString());
						};
						elemento.put("elemento", "curso");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
			    	};
			    	if (arrayListObjetivos != null){
						BasicDBObject elemento = new BasicDBObject();
				    	Object arrayObjetivos[] = arrayListObjetivos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayObjetivos.length; i++) {
							arrayJson.add(arrayObjetivos[i]);
						};
						elemento.put("elemento", "objetivo");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
			    	};
			    	if (arrayListAreaConhecimento != null){
						BasicDBObject elemento = new BasicDBObject();
				    	Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayAreaConhecimento.length; i++) {
							processaAreaConhecimento(arrayAreaConhecimento[i].toString(), objetivos, null, cursos, areasAtuacao, areasConhecimento, true, elementosFiltro);
							arrayJson.add(arrayAreaConhecimento[i].toString());
						};
						elemento.put("elemento", "areaConhecimento");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
			    	};
					//
					// ***		carrega pré requisitos
					//			
					ArrayList<?> arrayList = new ArrayList<Object>(); 
			    	arrayList = (ArrayList<?>) habilidade.get("preRequisitos");

			    	if (arrayList != null){
			    		Object arrayPreRequisitos[] = arrayList.toArray();
						int z = 0;
						while (z < arrayPreRequisitos.length) {
							carregaPreRequisitosFiltro(arrayPreRequisitos[z].toString(), elementosFiltro, filtro );
							++z;
						};
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
				if (arrayListHabilidades != null && habilidades != null){
			    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayHabilidades.length) {
						processaHabilidades(arrayHabilidades[z].toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, filtro, elementosFiltro, true);
						++z;
					};
				};
		    	if (!filtro){
					List arrayParent = (List) curso.get("parents");
					if (arrayParent.size() == 0){
						if (addObjeto(cursos, curso)){
							cursos.add(cursor.get("documento"));
						};
					};
		    	}else{
					if (arrayListHabilidades != null){
				    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayHabilidades.length; i++) {
							arrayJson.add(arrayHabilidades[i]);
						};
						//
						// ***		carrega filtros
						//			
						BasicDBObject elemento = new BasicDBObject();
						elemento.put("elemento", "habilidade");
						elemento.put("assunto", "curso");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
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
				if (arrayListHabilidades != null && habilidades != null){
					Object arrayHabilidades[] = arrayListHabilidades.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayHabilidades.length) {
						processaHabilidades(arrayHabilidades[z].toString(), objetivos, habilidades, cursos, areasAtuacao, null, filtro, elementosFiltro, true);							
						++z;
					};
				};
				if (!filtro){
					//
					// ***		carrega areas conhecimento
					//			
					if (addObjeto(areasConhecimento, areaConhecimento)){
						areasConhecimento.add(areaConhecimento);
					};
				}else{
					if (arrayListHabilidades != null){
						Object arrayHabilidades[] = arrayListHabilidades.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayHabilidades.length; i++) {
							arrayJson.add(arrayHabilidades[i]);
						};
						//
						// ***		carrega filtros
						//			
						BasicDBObject elemento = new BasicDBObject();
						elemento.put("elemento", "habilidade");
						elemento.put("assunto", "areaConhecimento");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
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
				if (!filtro){
					//
					// ***		carrega areas atuacao
					//			
					if (addObjeto(areasAtuacao, areaAtuacao)){
						areasAtuacao.add(areaAtuacao);
					};
				}else{
					if (arrayListObjetivos != null  && objetivos != null){
						Object arrayObjetivos[] = arrayListObjetivos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayObjetivos.length; i++) {
							processaObjetivos(arrayObjetivos[i].toString(), objetivos, null, cursos, null, areasConhecimento, true, elementosFiltro);
							arrayJson.add(arrayObjetivos[i].toString());
						};
						//
						// ***		carrega filtros
						//			
						BasicDBObject elemento = new BasicDBObject();
						elemento.put("elemento", "objetivo");
						elemento.put("assunto", "areaAtuacao");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
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
	
	@SuppressWarnings("unchecked")
	private void carregaPreRequisitosFiltro(String id, JSONArray elementosFiltro, Boolean filtro) {
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

				if (filtro){
					//
					// ***		carrega filtros
					//			
			    	if (arrayListCursos != null){
						BasicDBObject elemento = new BasicDBObject();
				    	Object[] arrayCursos = arrayListCursos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayCursos.length; i++) {
							arrayJson.add(arrayCursos[i]);
						};
						elemento.put("elemento", "curso");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
			    	};
			    	if (arrayListObjetivos != null){
						BasicDBObject elemento = new BasicDBObject();
				    	Object arrayObjetivos[] = arrayListObjetivos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayObjetivos.length; i++) {
							arrayJson.add(arrayObjetivos[i]);
						};
						elemento.put("elemento", "objetivo");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
			    	};
			    	if (arrayListAreaConhecimento != null){
						BasicDBObject elemento = new BasicDBObject();
				    	Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayAreaConhecimento.length; i++) {
							arrayJson.add(arrayAreaConhecimento[i]);
						};
						elemento.put("elemento", "areaConhecimento");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						if (addObjeto(elementosFiltro, elemento)){
							elementosFiltro.add(elemento);
						};
			    	};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
	}

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
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
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
			DBCollection collection = db.getCollection("areaConhecimento");
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
									processaHabilidades(jsonObject.get("id").toString(), objetivos, habilidades, cursos, areasAtuacao, areasConhecimento, false, null, true);
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
