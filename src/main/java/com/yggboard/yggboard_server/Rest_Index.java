package com.yggboard.yggboard_server;

import java.io.IOException;
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
import com.mongodb.DBObject;

@Singleton
// @Lock(LockType.READ)
@Path("/index")

public class Rest_Index {
	
	@SuppressWarnings({ "rawtypes" })
	@Path("/obter/itens")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItens(@QueryParam("assunto") String assunto, @QueryParam("id") String id, @QueryParam("usuario") String usuario ) {
	
		System.out.println("chamada index:" + assunto );
		
		if (id == null && !assunto.equals("todos")){
			return null;
		};
		Commons commons = new Commons();
		Listas listas = new Listas();
		Opcoes opcoes = new Opcoes();
		Commons_DB commons_db = new Commons_DB();	
		BasicDBObject doc = commons_db.getCollection(usuario, "userPerfil", "documento.token");	
		BasicDBObject objUserPerfil = new BasicDBObject();
		if (doc != null){
			objUserPerfil.putAll((Map) doc.get("documento"));
		}else{
			return null;
		};

		listas.setUserPerfil(objUserPerfil);
		
		BasicDBObject results = new BasicDBObject();
		
		if (!assunto.equals("todos")){
			switch (assunto) {
			case "objetivos":
				opcoes.setCarregaObjetivosAreasAtuacao(true);
				opcoes.setCarregaObjetivosHabilidades(true);
				opcoes.setCarregaHabilidadesAreaConhecimento(true);
				opcoes.setCarregaHabilidadesCursos(true);
				opcoes.setCarregaPreRequisitos(true);
				opcoes.setCarregaCursosPreRequisitos(true);
				opcoes.setCarregaAreasConhecimentoPreRequisitos(true);
				processaObjetivos(id, listas, opcoes);
				break;
			case "habilidades":
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
			results = carregaTudo(listas);
			System.out.println("saida carrega tudo:" + assunto );
			return results;
		};
		
		results.put("objetivos", listas.objetivos());
		results.put("habilidades", listas.habilidades());
		results.put("cursos", listas.cursos());
		results.put("areaAtuacao", listas.areasAtuacao());
		results.put("areaConhecimento", listas.areasConhecimento());
		results.put("todaysDate", commons.todaysDate("inv_month_number"));

		System.out.println("saida chamada index:" + assunto );

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
		
		carregaLista(listas.objetivos(), listas, "objetivos");
		carregaLista(listas.habilidades(), listas, "habilidades");
		carregaLista(listas.cursos(), listas, "cursos");
		carregaLista(listas.areasAtuacao(), listas, "areaAtuacao");
		carregaLista(listas.areasConhecimento(), listas, "areaConhecimento");
		
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
	public BasicDBObject ObterItensFiltro(JSONArray objFiltros) throws JsonParseException, JsonMappingException, IOException {
		
		Listas listas = new Listas();
		Opcoes opcoes = new Opcoes();
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject objUserPerfil = new BasicDBObject();
	
		JSONObject objItemFiltro_0 = new JSONObject();
		if (objFiltros == null){
			return objUserPerfil;
		};

		objItemFiltro_0.putAll((Map) objFiltros.get(0));

		BasicDBObject doc = commons_db.getCollection(objItemFiltro_0.get("usuario").toString(), "userPerfil", "documento.token");
	
		if (doc != null){
			objUserPerfil.putAll((Map) doc.get("documento"));
		}else{
			return null;
		};

		listas.setUserPerfil(objUserPerfil);

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
			case "objetivos":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setAllTrue();
					opcoes.setFiltro(true);
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setObjetivo();
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);					
				}
				break;
			case "objetivo":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setAllTrue();
					opcoes.setFiltro(true);
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setObjetivo();
					processaObjetivos(objItemFiltro.get("id").toString(), listas, opcoes);					
				}
				break;
			case "habilidades":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaHabilidades(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setAllTrue();
					opcoes.setFiltro(true);
					processaHabilidades(objItemFiltro.get("id").toString(), listas, opcoes);
				}else{
					opcoes.setHabilidade();
					processaHabilidades(objItemFiltro.get("id").toString(), listas, opcoes);					
				}
				break;
			case "cursos":
				if (filtro){
					opcoes.setAllFalse();
					opcoes.setFiltro(false);
					processaCursos(objItemFiltro.get("id").toString(), listas, opcoes);
					opcoes.setAllTrue();
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
					opcoes.setAllTrue();
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
					opcoes.setAllTrue();
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
				case "objetivos":
					filtroObjetivo = true;
					break;
				case "objetivo":
					filtroObjetivo = true;
					break;
				case "habilidades":
					filtroHabilidade = true;
					break;
				case "cursos":
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
						opcoes.setCarregaHabilidadesObjetivos(false);
						opcoes.setCarregaCursosHabilidades(false);
						opcoes.setCarregaCursosPreRequisitos(false);
						opcoes.setCarregaAreaConhecimentoHabilidades(false);
					};
					if (filtroObjetivo){
						opcoes.setCarregaAreasAtuacaoObjetivos(false);
						opcoes.setCarregaObjetivosPreRequisitos(false);
						opcoes.setCarregaAreasAtuacaoObjetivos(false);
						opcoes.setCarregaHabilidadesObjetivos(false);
					};
					if (filtroCurso){
						opcoes.setCarregaHabilidadesCursos(false);
						opcoes.setCarregaHabilidadesCursos(false);
					};
					if (filtroAreaAtuacao){
						opcoes.setCarregaObjetivosAreasAtuacao(false);
						opcoes.setCarregaObjetivosAreasAtuacao(false);
					};
					if (filtroAreaConhecimento){
						opcoes.setCarregaHabilidadesAreaConhecimento(false);
						opcoes.setCarregaAreaConhecimentoHabilidades(false);
						opcoes.setCarregaAreasConhecimentoPreRequisitos(false);
					};
					opcoes.setFiltro(false);
					switch (selecionado.get("assunto").toString()) {
					case "objetivos":
						processaObjetivos(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "objetivo":
						processaObjetivos(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "habilidades":
						processaHabilidades(selecionado.get("value").toString(), listas, opcoes);
						break;
					case "cursos":
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

	@SuppressWarnings({ "unchecked", "rawtypes"})
	private Boolean selecionaFiltro(Object elemento, JSONArray objFiltros) {
		
		Boolean filtroSelecionado = false;
		Boolean selecionado = true;
		Boolean testouElemento = false;
		Boolean testouAssunto = false;
		Object filtros[] = objFiltros.toArray();
		/*
		 * 				se assunto do elemento igual a algum assunto do filtro carregar somente elementos do filtro 
		 */
		for (int i = 0; i < filtros.length; i++) {
			JSONObject filtro = new JSONObject();
			filtro.putAll((Map) filtros[i]);
			JSONObject elementoJson = new JSONObject();
			elementoJson.putAll((Map) elemento);
			if (elementoJson.get("assunto").toString().equals(filtro.get("assunto"))){
				testouElemento = true;
				testouAssunto = true;
				if (elementoJson.get("value").toString().equals(filtro.get("id"))){
					filtroSelecionado = true;					
				};
			};			
		};		
		if (!testouAssunto){
			for (int i = 0; i < filtros.length; i++) {
				JSONObject filtro = new JSONObject();
				filtro.putAll((Map) filtros[i]);
				JSONObject elementoJson = new JSONObject();
				elementoJson.putAll((Map) elemento);
				if (elementoJson.get("elemento").toString().equals(filtro.get("assunto"))){
					testouElemento = true;
					JSONArray elementos = new JSONArray();
					elementos.addAll((Collection) elementoJson.get("elementos"));
					for (int j = 0; j < elementos.size(); j++) {
						if (filtro.get("id").equals(elementos.get(j).toString())){
							filtroSelecionado = true;
						};
					};
				};
			};
		};
		if (!filtroSelecionado){
			selecionado = false;
		};
		if (!testouElemento){
			selecionado = false;			
		};
		return selecionado;
	};

	@SuppressWarnings({ "unchecked" })
	private void processaObjetivos(String id, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "objetivos", "documento.id");
		
		if (cursor != null){
			BasicDBObject objetivo = (BasicDBObject) cursor.get("documento");
			ArrayList<?> arrayListNecessarios = new ArrayList<Object>(); 
	    	arrayListNecessarios = (ArrayList<?>) objetivo.get("necessarios");
			
			ArrayList<?> arrayListAreaAtuacao = new ArrayList<Object>(); 
			arrayListAreaAtuacao = (ArrayList<?>) objetivo.get("areaAtuacao");

			if (!opcoes.filtro()){
				//
				// ***		carrega habilidades necessarias
				//
				if (arrayListNecessarios != null && opcoes.carregaObjetivosHabilidades()){
					Object arrayNecessarios[] = arrayListNecessarios.toArray();
					int z = 0;
					while (z < arrayNecessarios.length) {
						if (!opcoes.veioHabilidades()){
							opcoes.setVeioObjetivos();
							processaHabilidades(arrayNecessarios[z].toString(), listas, opcoes);
							opcoes.setVeioObjetivos();
						};
						++z;
					};
				};
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
				if (listas.userPerfil().get("carreirasInteresse") != null){
					objetivo.put("interesse", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("carreirasInteresse")));
				};
				if (listas.userPerfil().get("carreiras") != null){
					objetivo.put("possui", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("carreiras")));
				};
				if (objetivo.get("necessarios") != null){
					objetivo.put("necessariosPerfil", commons.montaArrayPerfil(listas.userPerfil().get("habilidades"), objetivo.get("necessarios")));
				};
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
						arrayJson.add(arrayNecessarios[i].toString());
						if (!opcoes.veioHabilidades()){
							opcoes.setVeioObjetivos();
							processaHabilidades(arrayNecessarios[i].toString(), listas, opcoes);
							opcoes.setVeioObjetivos();
						};
					};
					elemento.put("elemento", "habilidades");
					elemento.put("assunto", "objetivos");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
				};
				if (arrayListAreaAtuacao != null && opcoes.carregaObjetivosAreasAtuacao()){
					BasicDBObject elemento = new BasicDBObject();
					Object arrayAreaAtuacao[] = arrayListAreaAtuacao.toArray();
					JSONArray arrayJson = new JSONArray();
					for (int i = 0; i < arrayAreaAtuacao.length; i++) {
						arrayJson.add(arrayAreaAtuacao[i].toString());
						if (!opcoes.veioAreaAtuacao()){
							opcoes.setVeioObjetivos();
							processaAreaAtuacao(arrayAreaAtuacao[i].toString(), listas, opcoes);
							opcoes.setVeioObjetivos();
						};
					};
					elemento.put("elemento", "areaAtuacao");
					elemento.put("assunto", "objetivos");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
				};
			};
		};
	};

	@SuppressWarnings({ "unchecked" })
	private void processaHabilidades(String id, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "habilidades", "documento.id");
		
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

			if (!opcoes.filtro()){
				//
				// ***		carrega objetivos
				//
		    	if (arrayListObjetivos != null  && opcoes.carregaHabilidadesObjetivos()){
			    	Object arrayObjetivos[] = arrayListObjetivos.toArray();
					int w = 0;
					while (w < arrayObjetivos.length) {
						if (!opcoes.veioObjetivos()){
							opcoes.setVeioHabilidades();
							processaObjetivos(arrayObjetivos[w].toString(), listas, opcoes);
							opcoes.setVeioHabilidades();
						};
						++w;
					};
		    	};
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
						arrayJson.add(arrayCursos[i].toString());
						if (!opcoes.veioCursos()){
							opcoes.setVeioHabilidades();
							processaCursos(arrayCursos[i].toString(), listas, opcoes);
							opcoes.setVeioHabilidades();
						};
					};
					elemento.put("elemento", "cursos");
					elemento.put("assunto", "habilidades");
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
						if (!opcoes.veioObjetivos()){
							opcoes.setVeioHabilidades();
							processaObjetivos(arrayObjetivos[i].toString(), listas, opcoes);
							opcoes.setVeioHabilidades();
						};
					};
					elemento.put("elemento", "objetivos");
					elemento.put("assunto", "habilidades");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
		    	};
		    	if (arrayListAreaConhecimento != null && opcoes.carregaHabilidadesAreaConhecimento()){
					BasicDBObject elemento = new BasicDBObject();
			    	Object arrayAreaConhecimento[] = arrayListAreaConhecimento.toArray();
					JSONArray arrayJson = new JSONArray();
					for (int i = 0; i < arrayAreaConhecimento.length; i++) {
						arrayJson.add(arrayAreaConhecimento[i].toString());
						if (!opcoes.veioAreaConhecimento()){
							opcoes.setVeioHabilidades();
							processaAreaConhecimento(arrayAreaConhecimento[i].toString(), listas, opcoes);
							opcoes.setVeioHabilidades();
						};
					};
					elemento.put("elemento", "areaConhecimento");
					elemento.put("assunto", "habilidades");
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
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes"})
	private void processaCursos(String id, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "cursos", "documento.id");
		
		if (cursor != null){
			BasicDBObject curso = (BasicDBObject) cursor.get("documento");
			ArrayList<?> arrayListHabilidades = new ArrayList<Object>(); 
			arrayListHabilidades = (ArrayList<?>) curso.get("habilidades");
	    	if (!opcoes.filtro()){
				if (arrayListHabilidades != null && opcoes.carregaCursosHabilidades()){
			    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
					//
					// ***		carrega habilidades
					//
					int z = 0;
					while (z < arrayHabilidades.length) {
						if (!opcoes.veioHabilidades()){
							opcoes.setVeioCursos();
							processaHabilidades(arrayHabilidades[z].toString(), listas, opcoes);
							opcoes.setVeioCursos();
						};
						++z;
					};
				};
				List arrayParent = (List) curso.get("parents");
				if (arrayParent.size() == 0){
					if (listas.userPerfil().get("cursosInteresse") != null){
						curso.put("interesse", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("cursosInteresse")));
					};
					if (listas.userPerfil().get("cursos") != null){
						curso.put("possui", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("cursos")));
					};
					if (curso.get("habilidades") != null){
						curso.put("habilidadesPerfil", commons.montaArrayPerfil(listas.userPerfil().get("habilidades"), curso.get("habilidades")));
					};
					listas.addCursos(curso);
				};
	    	}else{
				if (arrayListHabilidades != null && opcoes.carregaCursosHabilidades()){
			    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
					JSONArray arrayJson = new JSONArray();
					for (int i = 0; i < arrayHabilidades.length; i++) {
						arrayJson.add(arrayHabilidades[i].toString());
						if (!opcoes.veioHabilidades()){
							opcoes.setVeioCursos();
							processaHabilidades(arrayHabilidades[i].toString(), listas, opcoes);
							opcoes.setVeioCursos();
						};
					};
					//
					// ***		carrega filtros
					//			
					BasicDBObject elemento = new BasicDBObject();
					elemento.put("elemento", "habilidades");
					elemento.put("assunto", "cursos");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
				};
			};
		};
	};

	@SuppressWarnings({ "unchecked" })
	private void processaAreaConhecimento(String id, Listas listas, Opcoes opcoes) {
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "areaConhecimento", "documento.id");
		
		if (cursor != null){
			BasicDBObject areaConhecimento = (BasicDBObject) cursor.get("documento");
			ArrayList<?> arrayListHabilidades = new ArrayList<Object>(); 
			arrayListHabilidades = (ArrayList<?>) areaConhecimento.get("habilidades");
			if (!opcoes.filtro()){
				//
				// ***		carrega habilidades
				//
				if (arrayListHabilidades != null && opcoes.carregaAreaConhecimentoHabilidades()){
					Object arrayHabilidades[] = arrayListHabilidades.toArray();
					int z = 0;
					while (z < arrayHabilidades.length) {
						if (!opcoes.veioHabilidades()){
							opcoes.setVeioAreaConhecimento();
							processaHabilidades(arrayHabilidades[z].toString(), listas, opcoes);
							opcoes.setVeioAreaConhecimento();
						};
						++z;
					};
				};
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
						if (!opcoes.veioHabilidades()){
							opcoes.setVeioAreaConhecimento();
							processaHabilidades(arrayHabilidades[i].toString(), listas, opcoes);
							opcoes.setVeioAreaConhecimento();
						};
					};
					//
					// ***		carrega filtros
					//			
					BasicDBObject elemento = new BasicDBObject();
					elemento.put("elemento", "habilidades");
					elemento.put("assunto", "areaConhecimento");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
				};
			};
		};
	};
	@SuppressWarnings({ "unchecked" })
	private void processaAreaAtuacao(String id, Listas listas, Opcoes opcoes) {		
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "areaAtuacao", "documento.id");
		
		if (cursor != null){
			BasicDBObject areaAtuacao = (BasicDBObject) cursor.get("documento");
			//
			// ***		carrega objetivos
			//			
			ArrayList<?> arrayListObjetivos = new ArrayList<Object>(); 
			arrayListObjetivos = (ArrayList<?>) areaAtuacao.get("objetivos");
			if (!opcoes.filtro()){
				//
				// ***		carrega objetivos
				//
				if (arrayListObjetivos != null && opcoes.carregaAreasAtuacaoObjetivos()){
					Object arrayObjetivos[] = arrayListObjetivos.toArray();
					int z = 0;
					while (z < arrayObjetivos.length) {
						if (!opcoes.veioObjetivos()){
							opcoes.setVeioAreaAtuacao();
							processaObjetivos(arrayObjetivos[z].toString(), listas, opcoes);
							opcoes.setVeioAreaAtuacao();
						};
						++z;
					};
				};
				//
				// ***		carrega areas atuacao
				//
				listas.addAreasAtuacao(areaAtuacao);
			}else{
				if (arrayListObjetivos != null  && opcoes.carregaAreasAtuacaoObjetivos()){
					Object arrayObjetivos[] = arrayListObjetivos.toArray();
					JSONArray arrayJson = new JSONArray();
					for (int i = 0; i < arrayObjetivos.length; i++) {
						arrayJson.add(arrayObjetivos[i].toString());
						if (!opcoes.veioObjetivos()){
							opcoes.setVeioAreaAtuacao();
							processaObjetivos(arrayObjetivos[i].toString(), listas, opcoes);
							opcoes.setVeioAreaAtuacao();
						};
					};
					//
					// ***		carrega filtros
					//			
					BasicDBObject elemento = new BasicDBObject();
					elemento.put("elemento", "objetivos");
					elemento.put("assunto", "areaAtuacao");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
				};
			};
		};
	};

	@SuppressWarnings({ "unchecked"})
	private void carregaHabilidade(String id, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "habilidades", "documento.id");
		
		if (cursor != null){
			BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");
			//
			// ***		carrega habilidade
			//			
			BasicDBObject idObj = new BasicDBObject();
			idObj.put("id", id);
			habilidade.put ("nivel", "0");
			if (listas.userPerfil().get("habilidadesInteresse") != null){
				habilidade.put("interesse", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("habilidadesInteresse")));
			};
			if (listas.userPerfil().get("habilidades") != null){
				habilidade.put("possui", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("habilidades")));
			};
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
	};

	@SuppressWarnings({ "unchecked"})
	private void carregaPreRequisitos(String id, String nivel, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "habilidades", "documento.id");
		
		if (cursor != null){
			BasicDBObject habilidade = (BasicDBObject) cursor.get("documento");
			Boolean carregouHabilidade = false;
			BasicDBObject idObj = new BasicDBObject();
			idObj.put("id", id);
			habilidade.put ("nivel", nivel);
			if (listas.userPerfil().get("habilidadesInteresse") != null){
				habilidade.put("interesse", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("habilidadesInteresse")));
			};
			if (listas.userPerfil().get("habilidades") != null){
				habilidade.put("possui", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("habilidades")));
			};
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
		};
	};
	
	@SuppressWarnings({ "unchecked"})
	private void carregaPreRequisitosFiltro(String id, Listas listas, Opcoes opcoes) {
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "habilidades", "documento.id");
		
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
					elemento.put("elemento", "cursos");
					elemento.put("assunto", "habilidades");
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
					elemento.put("elemento", "objetivos");
					elemento.put("assunto", "habilidades");
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
					elemento.put("assunto", "habilidades");
					elemento.put("value", id);
					elemento.put("elementos", arrayJson);
					listas.addElementosFiltro(elemento);
		    	};
			};
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void carregaCurso(String id, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "cursos", "documento.id");
		
		if (cursor != null){
			BasicDBObject curso = (BasicDBObject) cursor.get("documento");
			if (addObjeto(listas.cursos(), curso)){
				List arrayParent = (List) curso.get("parents");
				if (listas.userPerfil().get("cursosInteresse") != null){
					curso.put("interesse", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("cursosInteresse")));
				};
				if (listas.userPerfil().get("cursos") != null){
					curso.put("possui", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("cursos")));
				};
				if (curso.get("habilidades") != null){
					curso.put("habilidadesPerfil", commons.montaArrayPerfil(listas.userPerfil().get("habilidades"), curso.get("habilidades")));
				};
				if (arrayParent.size() == 0){
					listas.addCursos(curso);
				};
			};
		};
		
	};
	
	private void carregaAreaAtuacao(String id, Listas listas, Opcoes opcoes) {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(id, "areaAtuacao", "documento.id");		
		if (cursor != null){
			BasicDBObject areaAtuacao = (BasicDBObject) cursor.get("documento");
			listas.addAreasAtuacao(areaAtuacao);
		};					
	};
	
	private void carregaAreaConhecimento(String id, Listas listas, Opcoes opcoes) {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(id, "areaConhecimento", "documento.id");
		if (cursor != null){
			BasicDBObject areaConhecimento = (BasicDBObject) cursor.get("documento");
			listas.addAreasConhecimento(areaConhecimento);
		};					
	};

	@SuppressWarnings({ "unchecked"})
	private void carregaObjetivo(String id, Listas listas, Opcoes opcoes) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(id, "objetivos", "documento.id");
		if (cursor != null){
			BasicDBObject objetivo = (BasicDBObject) cursor.get("documento");
			//
			// ***		carrega objetivo
			//
			if (listas.userPerfil().get("carreirasInteresse") != null){
				objetivo.put("interesse", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("carreirasInteresse")));
			};
			if (listas.userPerfil().get("carreiras") != null){
				objetivo.put("possui", commons.testaElementoArray(id, (ArrayList<String>) listas.userPerfil().get("carreiras")));
			};
			if (objetivo.get("necessarios") != null){
				objetivo.put("necessariosPerfil", commons.montaArrayPerfil(listas.userPerfil().get("habilidades"), objetivo.get("necessarios")));
			};
			listas.addObjetivos(objetivo);
		};					
	};

	@SuppressWarnings({ "unchecked", "rawtypes"})
	private void carregaLista(JSONArray arrayObj, Listas listas, String collection) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
		JSONArray cursor = commons_db.getCollectionListaNoKey(collection);	
		if (cursor != null){
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject obj = (BasicDBObject) cursor.get(i);
				BasicDBObject objOut = new BasicDBObject();
				//
				// ***		carrega lista
				//			
				if (addObjeto(arrayObj, obj)){
					if (collection.equals("cursos")) {
						List arrayParent = (List) obj.get("parents");
						if (arrayParent.size() == 0){
							if (listas.userPerfil().get("cursosInteresse") != null){
								objOut.put("interesse", commons.testaElementoArray(obj.get("id").toString(), (ArrayList<String>) listas.userPerfil().get("cursosInteresse")));
							};
							if (listas.userPerfil().get("cursos") != null){
								obj.put("possui", commons.testaElementoArray(obj.get("id").toString(), (ArrayList<String>) listas.userPerfil().get("cursos")));
							};
							if (obj.get("habilidades") != null){
								objOut.put("habilidadesPerfil", commons.montaArrayPerfil(listas.userPerfil().get("habilidades"), obj.get("habilidades")));
							};
							objOut.put("documento", obj);
						};
					};
					if (collection.equals("habilidades")) {
						if (listas.userPerfil().get("habilidadesInteresse") != null){
							objOut.put("interesse", commons.testaElementoArray(obj.get("id").toString(), (ArrayList<String>) listas.userPerfil().get("habilidadesInteresse")));
						};
						if (listas.userPerfil().get("habilidades") != null){
							objOut.put("possui", commons.testaElementoArray(obj.get("id").toString(), (ArrayList<String>) listas.userPerfil().get("habilidades")));
						};
						objOut.put("documento", obj);
					};
					if (collection.equals("objetivos")) {
						if (listas.userPerfil().get("carreirasInteresse") != null){
							objOut.put("interesse", commons.testaElementoArray(obj.get("id").toString(), (ArrayList<String>) listas.userPerfil().get("carreirasInteresse")));
						};
						if (listas.userPerfil().get("carreiras") != null){
							objOut.put("possui", commons.testaElementoArray(obj.get("id").toString(), (ArrayList<String>) listas.userPerfil().get("carreiras")));
						};
						if (obj.get("necessarios") != null){
							objOut.put("necessariosPerfil", commons.montaArrayPerfil(listas.userPerfil().get("habilidades"), obj.get("necessarios")));
						};
						objOut.put("documento", obj);
					};
					arrayObj.add(objOut);
				};
			};
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

	@SuppressWarnings("rawtypes")
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterLista(@QueryParam("characters") String characters, @QueryParam("planejamentoLista") String planejamentoLista, @QueryParam("usuario") String usuario) {
		
		BasicDBObject results = new BasicDBObject();
		Listas listas = new Listas();
		Opcoes opcoes = new Opcoes();
		Commons_DB commons_db = new Commons_DB();
		
		BasicDBObject doc = commons_db.getCollection(usuario, "userPerfil", "documento.token");
		BasicDBObject objUserPerfil = new BasicDBObject();
		if (doc != null){
			objUserPerfil.putAll((Map) doc.get("documento"));
		}else{
			return null;
		};

		listas.setUserPerfil(objUserPerfil);
		
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

	@SuppressWarnings({ "unchecked"})
	private void carregaIndex(String assunto, JSONArray documentos, String characters, Boolean lista, Listas listas, Opcoes opcoes, int qtdeItens) {
		Commons_DB commons_db = new Commons_DB();
		JSONArray cursor = commons_db.getCollectionLista("documento.assunto", "index", assunto);		
		if (cursor != null){
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
							case "curso":
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
								return;
							};
						};
						++i;
					};
				} catch (ParseException e) {
					e.printStackTrace();
				}
			};		
		};
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
