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
		Listas listas = new Listas();
		Opcoes opcoes = new Opcoes();

		if (!assunto.equals("todos")){
			switch (assunto) {
			case "objetivo":
				opcoes.setCarregaObjetivosAreasAtuacao(true);
				opcoes.setCarregaObjetivosHabilidades(true);
				opcoes.setCarregaHabilidadesAreaConhecimento(true);
				opcoes.setCarregaHabilidadesCursos(true);
				opcoes.setCarregaPreRequisitos(true);
				opcoes.setCarregaCursosPreRequisitos(true);
				opcoes.setCarregaAreasConhecimentoPreRequisitos(true);
				processaObjetivos(id, listas, opcoes);
				break;
			case "habilidade":
				opcoes.setCarregaHabilidadesAreaConhecimento(true);
				opcoes.setCarregaHabilidadesCursos(true);
				opcoes.setCarregaHabilidadesObjetivos(true);
				opcoes.setCarregaPreRequisitos(true);
				opcoes.setCarregaObjetivosAreasAtuacao(true);
				processaHabilidades(id, listas, opcoes);
				break;
			case "curso":
				opcoes.setCarregaCursosHabilidades(true);
				opcoes.setCarregaHabilidadesAreaConhecimento(true);
				opcoes.setCarregaHabilidadesObjetivos(true);
				opcoes.setCarregaObjetivosAreasAtuacao(true);
				processaCursos(id, listas, opcoes);
				break;
			case "areaAtuacao":
				opcoes.setCarregaAreasAtuacaoObjetivos(true);
				opcoes.setCarregaObjetivosHabilidades(true);
				opcoes.setCarregaHabilidadesCursos(true);
				opcoes.setCarregaHabilidadesAreaConhecimento(true);
				opcoes.setCarregaPreRequisitos(true);
				opcoes.setCarregaCursosPreRequisitos(true);
				opcoes.setCarregaAreasConhecimentoPreRequisitos(true);
				processaAreaAtuacao(id, listas, opcoes);
				break;
			case "areaConhecimento":
				opcoes.setCarregaAreaConhecimentoHabilidades(true);
				opcoes.setCarregaHabilidadesCursos(true);
				opcoes.setCarregaHabilidadesObjetivos(true);
				opcoes.setCarregaObjetivosAreasAtuacao(true);
				opcoes.setCarregaHabilidadesAreaConhecimento(true);
				processaAreaConhecimento(id, listas, opcoes);
				break;
			default:
				break;
			};
		}else{
			carregaTudo(listas);
		};
		
		BasicDBObject results = new BasicDBObject();
		
		results.put("objetivos", listas.objetivos());
		results.put("habilidades", listas.habilidades());
		results.put("cursos", listas.cursos());
		results.put("areaAtuacao", listas.areasAtuacao());
		results.put("areaConhecimento", listas.areasConhecimento());
		results.put("todaysDate", commons.todaysDate("inv_month_number"));
		
		return results;
			
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
	
	private BasicDBObject carregaTudo(Listas listas) {
		
		carregaObjetivos(listas.objetivos());
		carregaHabilidades(listas.habilidades());
		carregaCursos(listas.cursos());
		carregaAreasAtuacao(listas.areasAtuacao());
		carregaAreasConhecimento(listas.areasConhecimento());
		
		BasicDBObject results = new BasicDBObject();
		
		results.put("objetivos", listas.objetivos());
		results.put("habilidades", listas.habilidades());
		results.put("cursos", listas.cursos());
		results.put("areaAtuacao", listas.areasAtuacao());
		results.put("areaConhecimento", listas.areasConhecimento());
		
		return results;
		
	};

	@SuppressWarnings({ "rawtypes", "unchecked"})
	@Path("/obter/filtro")	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItensFiltro(JSONArray objFiltros) throws MongoException, JsonParseException, JsonMappingException, IOException {
		
		Listas listas = new Listas();
		Opcoes opcoes = new Opcoes();

		Boolean filtro = true;
		opcoes.setFiltro(true);
		int sizeFiltros = objFiltros.size();
		if (sizeFiltros < 2 ){
			filtro = false;
			opcoes.setFiltro(false);
		};
			
		for (int i = 0; i < objFiltros.size(); i++) {
			JSONObject objItemFiltro = new JSONObject();
			objItemFiltro.putAll((Map) objFiltros.get(i));
			switch (objItemFiltro.get("assunto").toString()) {
			case "objetivo":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setObjetivo();
					opcoes.setFiltro(true);
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setObjetivo();
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);					
				}
				break;
			case "habilidade":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaHabilidades(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setHabilidade();
					opcoes.setFiltro(true);
					processaHabilidades(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setHabilidade();
					processaHabilidades(objItemFiltro.get("id").toString(), listas, opcoes);					
				}
				break;
			case "curso":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaCursos(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setCurso();
					opcoes.setFiltro(true);
					processaCursos(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setCurso();
					processaCursos(objItemFiltro.get("id").toString(), listas, opcoes);					
				};
				break;
			case "areaAtuacao":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaAreaAtuacao(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setAreaAtuacao();
					opcoes.setFiltro(true);					
					processaAreaAtuacao(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setAreaAtuacao();
					processaAreaAtuacao(objItemFiltro.get("id").toString(), listas, opcoes);					
				}
				break;
			case "areaConhecimento":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaAreaConhecimento(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setAreaConhecimento();
					opcoes.setFiltro(true);					
					processaAreaConhecimento(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setAreaConhecimento();
					processaAreaConhecimento(objItemFiltro.get("id").toString(), listas, opcoes);					
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
			Object elementos[] = listas.elementosFiltro().toArray();
			opcoes.setAllTrue();
			for (int i = 0; i < elementos.length; i++) {
				if (selecionaFiltro(elementos[i], objFiltros)){
					JSONObject selecionado = new JSONObject();
					selecionado.putAll((Map) elementos[i]);
					if (filtroHabilidade){
						opcoes.setCarregaPreRequisitos(false);
						opcoes.setCarregaCursosPreRequisitos(false);
						opcoes.setCarregaObjetivosPreRequisitos(false);
						opcoes.setCarregaAreasConhecimentoPreRequisitos(false);
						opcoes.setCarregaAreaConhecimentoHabilidades(false);
						opcoes.setCarregaCursosHabilidades(false);
						opcoes.setCarregaObjetivosHabilidades(false);
					};
					if (filtroObjetivo){
						opcoes.setCarregaAreasAtuacaoObjetivos(false);
						opcoes.setCarregaHabilidadesObjetivos(false);
					};
					if (filtroCurso){
						opcoes.setCarregaHabilidadesCursos(false);
					};
					if (filtroAreaAtuacao){
						opcoes.setCarregaObjetivosAreasAtuacao(false);
					};
					if (filtroAreaConhecimento){
						opcoes.setCarregaAreaConhecimentoHabilidades(false);
					};
					switch (selecionado.get("assunto").toString()) {
					case "objetivo":
						processaObjetivos(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "habilidade":
						processaHabilidades(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "curso":
						processaCursos(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "areaAtuacao":
						processaAreaAtuacao(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "areaConhecimento":
						processaAreaConhecimento(selecionado.get("value").toString(), listas, opcoes);
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

		BasicDBObject results = new BasicDBObject();
		
		results.put("objetivos", listas.objetivos());
		results.put("habilidades", listas.habilidades());
		results.put("cursos", listas.cursos());
		results.put("areaAtuacao", listas.areasAtuacao());
		results.put("areaConhecimento", listas.areasConhecimento());
		
		return results;
			
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
	private void processaObjetivos(String id, Listas listas, Opcoes opcoes) {
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

				if (arrayListNecessarios != null && opcoes.carregaObjetivosHabilidades()){
					Object arrayNecessarios[] = arrayListNecessarios.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayNecessarios.length) {
						processaHabilidades(arrayNecessarios[z].toString(), listas, opcoes);
						++z;
					};
				};

				if (!opcoes.filtro()){
					if (arrayListAreaAtuacao != null && opcoes.carregaObjetivosAreasAtuacao()){
						Object arrayAreaAtuacao[] = arrayListAreaAtuacao.toArray();
						//
						// ***		carrega área de atuação
						//			
						int w = 0;
						while (w < arrayAreaAtuacao.length) {
							carregaAreaAtuacao(arrayAreaAtuacao[w].toString(), listas, opcoes);
							++w;
						};
					};
					//
					// ***		carrega objetivo
					//
					listas.addObjetivos(objetivo);
				}else{
					//
					// ***		carrega filtros
					//
					if (arrayListNecessarios != null && opcoes.carregaObjetivosHabilidades()){
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
						listas.addElementosFiltro(elemento);
					};
					if (arrayListAreaAtuacao != null && opcoes.carregaObjetivosAreasAtuacao()){
						BasicDBObject elemento = new BasicDBObject();
						Object arrayAreaAtuacao[] = arrayListAreaAtuacao.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayAreaAtuacao.length; i++) {
							carregaAreaAtuacao(arrayAreaAtuacao[i].toString(), listas, opcoes);
							arrayJson.add(arrayAreaAtuacao[i].toString());
						};
						elemento.put("elemento", "areaAtuacao");
						elemento.put("assunto", "objetivo");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						listas.addElementosFiltro(elemento);
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e1) {
			e1.printStackTrace();
		};
	};

	@SuppressWarnings("unchecked")
	private void processaHabilidades(String id, Listas listas, Opcoes opcoes) {
		Mongo mongo;
		Commons commons = new Commons();
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
		    	if (arrayListObjetivos != null  && opcoes.carregaHabilidadesObjetivos()){
			    	Object arrayObjetivos[] = arrayListObjetivos.toArray();
					int w = 0;
					while (w < arrayObjetivos.length) {
						processaObjetivos(arrayObjetivos[w].toString(), listas, opcoes);
						++w;
					};
		    	};

				if (!opcoes.filtro()){
					if (arrayListCursos != null && opcoes.carregaHabilidadesCursos()){
				    	Object[] arrayCursos = arrayListCursos.toArray();
						//
						// ***		carrega cursos
						//
						int i = 0;
						while (i < arrayCursos.length) {
							carregaCurso(arrayCursos[i].toString(), listas, opcoes);
							++i;
						};
			    	};
					//
					// ***		carrega área de conhecimento
					//			
					if (arrayListAreaConhecimento != null && opcoes.carregaHabilidadesAreaConhecimento()){
						Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
						int w = 0;
						while (w < arrayAreaConhecimento.length) {
							carregaAreaConhecimento(arrayAreaConhecimento[w].toString(), listas, opcoes);
							++w;
						};
					};
						//
						// ***		carrega habilidade
						//			
					carregaHabilidade(id, listas, opcoes);
	
				}else{
					//
					// ***		carrega filtros
					//			
			    	if (arrayListCursos != null && opcoes.carregaHabilidadesCursos()){
						BasicDBObject elemento = new BasicDBObject();
				    	Object[] arrayCursos = arrayListCursos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayCursos.length; i++) {
							carregaCurso(arrayCursos[i].toString(), listas, opcoes);
							arrayJson.add(arrayCursos[i].toString());
						};
						elemento.put("elemento", "curso");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						listas.addElementosFiltro(elemento);
			    	};
			    	if (arrayListObjetivos != null && opcoes.carregaHabilidadesObjetivos()){
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
						listas.addElementosFiltro(elemento);
			    	};
			    	if (arrayListAreaConhecimento != null && opcoes.carregaHabilidadesAreaConhecimento()){
						BasicDBObject elemento = new BasicDBObject();
				    	Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayAreaConhecimento.length; i++) {
							carregaAreaConhecimento(arrayAreaConhecimento[i].toString(), listas, opcoes);
							arrayJson.add(arrayAreaConhecimento[i].toString());
						};
						elemento.put("elemento", "areaConhecimento");
						elemento.put("assunto", "habilidade");
						elemento.put("value", id);
						elemento.put("elementos", arrayJson);
						listas.addElementosFiltro(elemento);
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
							carregaPreRequisitosFiltro(commons.preRequisito(arrayPreRequisitos[z].toString()), listas, opcoes);
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
	private void processaCursos(String id, Listas listas, Opcoes opcoes) {
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
				if (arrayListHabilidades != null && opcoes.carregaCursosHabilidades()){
			    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayHabilidades.length) {
						processaHabilidades(arrayHabilidades[z].toString(), listas, opcoes);
						++z;
					};
				};
		    	if (!opcoes.filtro()){
					List arrayParent = (List) curso.get("parents");
					if (arrayParent.size() == 0){
						listas.addCursos(curso);
					};
		    	}else{
					if (arrayListHabilidades != null && opcoes.carregaCursosHabilidades()){
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
						listas.addElementosFiltro(elemento);
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e1) {
			e1.printStackTrace();
		}
	};

	@SuppressWarnings("unchecked")
	private void processaAreaConhecimento(String id, Listas listas, Opcoes opcoes) {

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
				if (arrayListHabilidades != null && opcoes.carregaAreaConhecimentoHabilidades()){
					Object arrayHabilidades[] = arrayListHabilidades.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayHabilidades.length) {
						if (opcoes.filtro()){
							carregaHabilidade(arrayHabilidades[z].toString(), listas, opcoes);
						}else{
							processaHabilidades(arrayHabilidades[z].toString(), listas, opcoes);
						};
						++z;
					};
				};
				if (!opcoes.filtro()){
					//
					// ***		carrega areas conhecimento
					//
					listas.addAreasConhecimento(areaConhecimento);
				}else{
					if (arrayListHabilidades != null && opcoes.carregaAreaConhecimentoHabilidades()){
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
						listas.addElementosFiltro(elemento);
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
	private void processaAreaAtuacao(String id, Listas listas, Opcoes opcoes) {		

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
				if (arrayListObjetivos != null && opcoes.carregaAreasAtuacaoObjetivos()){
					Object arrayObjetivos[] = arrayListObjetivos.toArray();
					//
					// ***		carrega objetivos
					//
					int z = 0;
					while (z < arrayObjetivos.length) {
						processaObjetivos(arrayObjetivos[z].toString(), listas, opcoes);
						++z;
					};
				};
				if (!opcoes.filtro()){
					//
					// ***		carrega areas atuacao
					//
					listas.addAreasAtuacao(areaAtuacao);
				}else{
					if (arrayListObjetivos != null  && opcoes.carregaAreasAtuacaoObjetivos()){
						Object arrayObjetivos[] = arrayListObjetivos.toArray();
						JSONArray arrayJson = new JSONArray();
						for (int i = 0; i < arrayObjetivos.length; i++) {
							processaObjetivos(arrayObjetivos[i].toString(), listas, opcoes);
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
						listas.addElementosFiltro(elemento);
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

	private void carregaHabilidade(String id, Listas listas, Opcoes opcoes) {
		Mongo mongo;
		Commons commons = new Commons();
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");
				//
				// ***		carrega habilidade
				//			
				BasicDBObject idObj = new BasicDBObject();
				idObj.put("id", id);
				habilidade.put ("nivel", "0");
				Boolean carregouHabilidade = false;
				if (addObjeto(listas.habilidadesCarregadas(), idObj)){
					listas.addHabilidades(habilidade);					
					carregouHabilidade = true;
				};
				listas.addHabilidadesCarregadas(idObj);
				ArrayList<?> arrayList = new ArrayList<Object>(); 
		    	arrayList = (ArrayList<?>) habilidade.get("preRequisitos");
		    	if (arrayList != null && carregouHabilidade){
		    		Object arrayPreRequisitos[] = arrayList.toArray();
					//
					// ***		carrega pré requisitos
					//			
					if (opcoes.carregaPreRequisitos()){
						int z = 0;
						while (z < arrayPreRequisitos.length) {
							String preRequisito = commons.preRequisito(arrayPreRequisitos[z].toString());
							BasicDBObject idObjPreReq = new BasicDBObject();
							idObjPreReq.put("id", preRequisito);
							if (addObjeto(listas.habilidadesCarregadas(), idObjPreReq)){
								carregaPreRequisitos((String) preRequisito, "1", listas, opcoes);
							};
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

	private void carregaPreRequisitos(String id, String nivel, Listas listas, Opcoes opcoes) {
		Mongo mongo;
		Commons commons = new Commons();
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");
				Boolean carregouHabilidade = false;
				BasicDBObject idObj = new BasicDBObject();
				idObj.put("id", id);
				habilidade.put ("nivel", nivel);
				if (addObjeto(listas.habilidadesCarregadas(), idObj)){
					listas.addHabilidades(habilidade);
					carregouHabilidade = true;
				};
				listas.addHabilidadesCarregadas(idObj);
				//
				// ***		carrega cursos
				//
				ArrayList<?> arrayListCursos = new ArrayList<Object>(); 
		    	arrayListCursos = (ArrayList<?>) habilidade.get("cursos");
		    	if (arrayListCursos != null && opcoes.carregaCursosPreRequisitos()){
			    	Object arrayCursos[] = arrayListCursos.toArray();
					int z = 0;
					while (z < arrayCursos.length) {
						carregaCurso(arrayCursos[z].toString(), listas, opcoes);
						++z;
					};
		    	};
				//
				// ***		carrega objetivos
				//
				ArrayList<?> arrayListObjetivo = new ArrayList<Object>(); 
		    	arrayListObjetivo = (ArrayList<?>) habilidade.get("objetivos");
		    	if (arrayListObjetivo != null && opcoes.carregaObjetivosPreRequisitos()){
			    	Object arrayObjetivo[] = arrayListObjetivo.toArray();
					int w = 0;
					while (w < arrayObjetivo.length) {
						carregaObjetivo(arrayObjetivo[w].toString(), listas, opcoes);
						++w;
					};
		    	};
				//
				// ***		carrega área de conhecimento
				//			
				ArrayList<?> arrayListAreaConhecimento = new ArrayList<Object>(); 
				arrayListAreaConhecimento = (ArrayList<?>) habilidade.get("areaConhecimento");
				if (arrayListAreaConhecimento != null && opcoes.carregaAreasConhecimentoPreRequisitos()){
					Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
					int w = 0;
					while (w < arrayAreaConhecimento.length) {
						carregaAreaConhecimento(arrayAreaConhecimento[w].toString(), listas, opcoes);
						++w;
					};
				};

				mongo.close();

				if (carregouHabilidade){
					ArrayList<?> arrayList = new ArrayList<Object>(); 
			    	arrayList = (ArrayList<?>) habilidade.get("preRequisitos");
			    	if (arrayList != null){
						//
						// ***		carrega pré requisitos
						//			
						int z = 0;
			    		Object arrayPreRequisitos[] = arrayList.toArray();
						while (z < arrayPreRequisitos.length) {
							String preRequisito =commons.preRequisito(arrayPreRequisitos[z].toString());
							BasicDBObject idObjPreReq = new BasicDBObject();
							idObjPreReq.put("id", preRequisito);
							if (addObjeto(listas.habilidadesCarregadas(), idObjPreReq)){
								int nivelInt = Integer.valueOf(nivel);
								nivelInt++;
								String nivelString = String.valueOf(nivelInt);
								carregaPreRequisitos(preRequisito.toString(), nivelString, listas, opcoes);
							};
							++z;
				    	};
					};
				};
			}else{			
				mongo.close();
			};
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
	};
	
	@SuppressWarnings("unchecked")
	private void carregaPreRequisitosFiltro(String id, Listas listas, Opcoes opcoes) {
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

				if (opcoes.filtro()){
					//
					// ***		carrega filtros
					//			
			    	if (arrayListCursos != null && opcoes.carregaCursosPreRequisitos()){
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
						listas.addElementosFiltro(elemento);
			    	};
			    	if (arrayListObjetivos != null && opcoes.carregaObjetivosPreRequisitos()){
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
						listas.addElementosFiltro(elemento);
			    	};
			    	if (arrayListAreaConhecimento != null && opcoes.carregaAreasConhecimentoPreRequisitos()){
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
						listas.addElementosFiltro(elemento);
			    	};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
	}

	@SuppressWarnings({ "rawtypes" })
	private void carregaCurso(String id, Listas listas, Opcoes opcoes) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("cursos");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject curso = (BasicDBObject) cursor.get("documento");
				if (addObjeto(listas.cursos(), curso)){
					List arrayParent = (List) curso.get("parents");
					if (arrayParent.size() == 0){
						listas.addCursos(curso);
					};
				};
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};
	
	private void carregaAreaAtuacao(String id, Listas listas, Opcoes opcoes) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("areaAtuacao");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject areaAtuacao = (BasicDBObject) cursor.get("documento");
				listas.addAreasAtuacao(areaAtuacao);
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};
	private void carregaAreaConhecimento(String id, Listas listas, Opcoes opcoes) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("areaConhecimento");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject areaConhecimento = (BasicDBObject) cursor.get("documento");
				listas.addAreasConhecimento(areaConhecimento);
			};			
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
		
	};

	private void carregaObjetivo(String id, Listas listas, Opcoes opcoes) {
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
				listas.addObjetivos(objetivo);
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
	public BasicDBObject ObterLista(@QueryParam("characters") String characters, @QueryParam("planejamentoLista") String planejamentoLista) {
		
		BasicDBObject results = new BasicDBObject();
		Listas listas = new Listas();
		Opcoes opcoes = new Opcoes(); 
		
		if (planejamentoLista.equals("true")){			
			carregaIndex("objetivos", listas.objetivos(), characters, true, listas, opcoes, 0);
			carregaIndex("habilidades", listas.habilidades(), characters, true, listas, opcoes, 0);
			carregaIndex("cursos", listas.cursos(), characters, true, listas, opcoes, 0);
			carregaIndex("areaAtuacao", listas.areasAtuacao(), characters, true, listas, opcoes, 0);
			carregaIndex("areaConhecimento", listas.areasConhecimento(), characters, true, listas, opcoes, 0);
			results.put("objetivos", listas.objetivos());
			results.put("habilidades", listas.habilidades());
			results.put("cursos", listas.cursos());
			results.put("areaAtuacao", listas.areasAtuacao());
			results.put("areaConhecimento", listas.areasConhecimento());
		}else{
			if (planejamentoLista.equals("false")){			
				JSONArray documentos = new JSONArray();
				carregaIndex("objetivos", documentos, characters, false, listas, opcoes, 4);
				carregaIndex("habilidades", documentos, characters, false, listas, opcoes, 4);
				carregaIndex("cursos", documentos, characters, false, listas, opcoes, 4);
				carregaIndex("areaAtuacao", documentos, characters, false, listas, opcoes, 4);
				carregaIndex("areaConhecimento", documentos, characters, false, listas, opcoes, 4);
				results.put("pesquisa", documentos);				
			}else{
				JSONArray documentos = new JSONArray();
				carregaIndex(planejamentoLista, documentos, characters, false, listas, opcoes, 10);
				results.put("pesquisa", documentos);				
			};
		};
		return results;			
	};

	@SuppressWarnings("unchecked")
	private void carregaIndex(String assunto, JSONArray documentos, String characters, Boolean lista, Listas listas, Opcoes opcoes, int qtdeItens) {
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
					BasicDBObject objIndex = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
					String documento = objIndex.getString("documento");
					try {
						JSONObject jsonObject; 
						jsonObject = (JSONObject) parser.parse(documento);
						JSONObject jsonDocumento = new JSONObject();
						String [] wordsSource = limpaChar (characters).split(" ");
						List<?> wordsCompare = (List<?>) jsonObject.get("texto");
						if (wordsoK (wordsSource, wordsCompare)){
							if (lista){
								switch (assunto) {
								case "objetivos":
									processaObjetivos(jsonObject.get("id").toString(), listas, opcoes);
									break;
								case "objetivo":
									processaObjetivos(jsonObject.get("id").toString(), listas, opcoes);
									break;
								case "habilidades":
									processaHabilidades(jsonObject.get("id").toString(), listas, opcoes);
								break;
								case "cursos":
									processaCursos(jsonObject.get("id").toString(), listas, opcoes);
								break;
								case "areaAtuacao":
									processaAreaAtuacao(jsonObject.get("id").toString(), listas, opcoes);
								break;
								case "areaConhecimento":
									processaAreaConhecimento(jsonObject.get("id").toString(), listas, opcoes);
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
								if (i > qtdeItens){
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
							if (((charIgual  * 100) / letrasSource.length) > 70){
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
