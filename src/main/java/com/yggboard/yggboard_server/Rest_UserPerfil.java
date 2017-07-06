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
import javax.ws.rs.core.Response;

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
@Path("/userPerfil")

public class Rest_UserPerfil {

	@SuppressWarnings("unchecked")
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterUsuario(@QueryParam("usuario") String usuario) throws UnknownHostException, MongoException {
		Mongo mongo = new Mongo();
		DB db = (DB) mongo.getDB("yggboard");
		DBCollection collection = db.getCollection("userPerfil");
		BasicDBObject searchQuery = new BasicDBObject("documento.usuario", usuario);
		DBObject cursor = collection.findOne(searchQuery);
		if (cursor != null){
			JSONObject documento = new JSONObject();
			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
			documento.put("documento", obj);
			mongo.close();
			return documento;
		}else{
			mongo.close();
			return null;
		}
	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/obter/itens")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCarreiras(@QueryParam("usuario") String usuario, @QueryParam("item") String item, @QueryParam("elemento") String elemento) throws UnknownHostException, MongoException {
		
		System.out.println("chamada userperfil:" + item);
		if (item == null ){
			return null;
		};
		Commons commons = new Commons();
		Mongo mongo = new Mongo();
		DB db = (DB) mongo.getDB("yggboard");
		DBCollection collection = db.getCollection("userPerfil");
		BasicDBObject searchQuery = new BasicDBObject("documento.usuario", usuario);
		DBObject cursor = collection.findOne(searchQuery);
		mongo.close();
		if (cursor != null){
			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
			JSONArray documentos = new JSONArray();
			String docUserPerfil = obj.toString();
			JSONObject jsonPerfil; 
			JSONParser parser = new JSONParser(); 
			try {
				jsonPerfil = (JSONObject) parser.parse(docUserPerfil);
				if (item.equals("carreiras") | item.equals("carreiras-interesse") | item.equals("carreiras-sugeridas")){
					ArrayList arrayList = new ArrayList(); 
					if (item.equals("carreiras")){
						arrayList = (ArrayList) jsonPerfil.get("carreiras");
					};
					if (item.equals("carreiras-interesse")){
						arrayList = (ArrayList) jsonPerfil.get("carreirasInteresse");
					};
					if (item.equals("carreiras-sugeridas")){				
						arrayList = (ArrayList) jsonPerfil.get("carreirasSugeridas");
					};
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						for (int i = 0; i < array.length; i++) {
							Mongo mongoCarreiras = new Mongo();
							DB dbCarreiras = (DB) mongoCarreiras.getDB("yggboard");
							DBCollection collectionCarreiras = dbCarreiras.getCollection("objetivos");
							BasicDBObject searchQueryCarreiras = new BasicDBObject("documento.id", array[i].toString());
							DBObject cursorCarreiras = collectionCarreiras.findOne(searchQueryCarreiras);
							BasicDBObject objCarreiras = new BasicDBObject();
							if (cursorCarreiras != null){
								objCarreiras.put("documento", cursorCarreiras.get("documento"));
								objCarreiras.put("_id", cursorCarreiras.get("_id").toString());
								documentos.add(montaCarreira(objCarreiras, jsonPerfil));
							};
							mongoCarreiras.close();
						};
					};
				};
				if (item.equals("carreiras-elemento")){
					Mongo mongoCarreiras = new Mongo();
					DB dbCarreiras = (DB) mongoCarreiras.getDB("yggboard");
					DBCollection collectionCarreiras = dbCarreiras.getCollection("objetivos");
					BasicDBObject searchQueryCarreiras = new BasicDBObject("documento.id", elemento.toString());
					DBObject cursorCarreiras = collectionCarreiras.findOne(searchQueryCarreiras);
					BasicDBObject objCarreiras = new BasicDBObject();
					if (cursorCarreiras != null){
						objCarreiras.put("documento", cursorCarreiras.get("documento"));
						objCarreiras.put("_id", cursorCarreiras.get("_id").toString());
						documentos.add(montaCarreira(objCarreiras, jsonPerfil));
					};
					mongoCarreiras.close();				
				};
				if (item.equals("badges") | item.equals("show-badges")){
					if (item.equals("badges")){
						carregaBadges((ArrayList) jsonPerfil.get("badges"), usuario, jsonPerfil, documentos, false);
						carregaBadges((ArrayList) jsonPerfil.get("badgesConquista"), usuario, jsonPerfil, documentos, false);
					}else{
						carregaBadges((ArrayList) jsonPerfil.get("showBadges"), usuario, jsonPerfil, documentos, false);
					};
					Mongo mongoBadge;
					try {
						mongoBadge = new Mongo();
						DB dbBadge = (DB) mongoBadge.getDB("yggboard");		
						DBCollection collectionBadge = dbBadge.getCollection("badges");
						BasicDBObject searchQueryBadge = new BasicDBObject();
						DBCursor cursorBadge = collectionBadge.find(searchQueryBadge);
						while (((Iterator<DBObject>) cursorBadge).hasNext()) {
							BasicDBObject objBadges = (BasicDBObject) ((Iterator<DBObject>) cursorBadge).next();
							String documento = objBadges.getString("documento");
							try {
								JSONObject jsonBadge; 
								jsonBadge = (JSONObject) parser.parse(documento);
								if (jsonBadge.get("tipo") !=null && jsonPerfil.get("badgesConquista") != null && jsonBadge.get("id").toString() != null){
									if (!commons.testaElementoArray(jsonBadge.get("id").toString(), (ArrayList<String>) jsonPerfil.get("badgesConquista"))){
										if (jsonBadge.get("tipo").equals("inicial")){
											incluirBadge(jsonBadge,  usuario, jsonPerfil, documentos, true);
										};
										if (jsonBadge.get("tipo").equals("numero")){
											ArrayList arrayListElementos = new ArrayList(); 
											arrayListElementos = (ArrayList) jsonPerfil.get("habilidades");
											if (Integer.valueOf((String) jsonBadge.get("quantidade")) < arrayListElementos.size() ){
												incluirBadge(jsonBadge, usuario, jsonPerfil, documentos, true);
											};
										};
										if (jsonBadge.get("tipo").equals("numero interesse")){
											ArrayList arrayListElementos = new ArrayList(); 
											arrayListElementos = (ArrayList) jsonPerfil.get("habilidadesInteresse");
											if (Integer.valueOf((String) jsonBadge.get("quantidade")) < arrayListElementos.size() ){
												incluirBadge(jsonBadge, usuario, jsonPerfil, documentos, true);
											};
										};
										if (jsonBadge.get("tipo").equals("numero objetivo")){
											ArrayList arrayListElementos = new ArrayList(); 
											arrayListElementos = (ArrayList) jsonPerfil.get("carreirasInteresse");
											if (Integer.valueOf((String) jsonBadge.get("quantidade")) < arrayListElementos.size() ){
												incluirBadge(jsonBadge, usuario, jsonPerfil, documentos, true);
											};
										};
									};
								};
							} catch (ParseException e) {
								e.printStackTrace();
							}
						};
						mongoBadge.close();
						return documentos;
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (MongoException e) {
						e.printStackTrace();
					};
				};
				if (item.equals("habilidades") | item.equals("habilidades-interesse") |
					item.equals("cursos-necessarias-habilidades") | item.equals("cursos-interesse-habilidades")){
					ArrayList arrayList = new ArrayList(); 
					if (item.equals("habilidades")){
						arrayList = (ArrayList) jsonPerfil.get("habilidades");
					}else{
						arrayList = (ArrayList) jsonPerfil.get("habilidadesInteresse");
					}
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						int w = 0;
						while (w < array.length) {
							Mongo mongoHabilidades = new Mongo();
							DB dbHabilidades = (DB) mongoHabilidades.getDB("yggboard");
							DBCollection collectionHabilidades = dbHabilidades.getCollection("habilidades");
							BasicDBObject searchQueryHabilidades = new BasicDBObject("documento.id", array[w]);
							DBObject cursorHabilidades = collectionHabilidades.findOne(searchQueryHabilidades);
							if (cursorHabilidades != null){
								BasicDBObject objHabilidades = (BasicDBObject) cursorHabilidades.get("documento");
								if (item.equals("cursos-necessarias-habilidades") | item.equals("cursos-interesse-habilidades")){
									ObterCursosNecessarios (objHabilidades.get("id"), documentos, jsonPerfil);
								}else{
									JSONObject jsonDocumento = new JSONObject();
									objHabilidades.put("interesse", commons.testaElementoArray(objHabilidades.get("id").toString(), (ArrayList<String>) jsonPerfil.get("habilidadesInteresse")));
									objHabilidades.put("possui", commons.testaElementoArray(objHabilidades.get("id").toString(), (ArrayList<String>) jsonPerfil.get("habilidades")));
								    jsonDocumento.put("documento", objHabilidades);
									JSONArray cursos = new JSONArray();
									ObterCursosNecessarios (objHabilidades.get("id"), cursos, jsonPerfil);
									jsonDocumento.put("cursos", cursos);
									documentos.add(jsonDocumento);
								};
							};
							mongoHabilidades.close();
							++w;
						};
					};
				};
				if (item.equals("habilidades-elementos")){
					ArrayList arrayList = new ArrayList(); 
					arrayList = (ArrayList) jsonPerfil.get("habilidades");
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						int w = 0;
						while (w < array.length) {
							Mongo mongoHabilidades = new Mongo();
							DB dbHabilidades = (DB) mongoHabilidades.getDB("yggboard");
							DBCollection collectionHabilidades = dbHabilidades.getCollection("habilidades");
							BasicDBObject searchQueryHabilidades = new BasicDBObject("documento.id", array[w].toString());
							DBObject cursorHabilidades = collectionHabilidades.findOne(searchQueryHabilidades);
							BasicDBObject objHabilidades = (BasicDBObject) cursorHabilidades.get("documento");
							JSONObject jsonDocumento = new JSONObject();
						    jsonDocumento.put("documento", objHabilidades);
							documentos.add(jsonDocumento);
							mongoHabilidades.close();
							++w;
						};
					};
				};
				if (item.equals("habilidades-necessarias-carreiras") | item.equals("habilidades-interesse-carreiras") |
						item.equals("cursos-necessarias-carreiras") | item.equals("cursos-interesse-carreiras") |
						item.equals("habilidades-necessarias-carreira")){
					ArrayList arrayList = new ArrayList(); 
					if (item.equals("habilidades-necessarias-carreiras") | item.equals("cursos-necessarias-carreiras")){
						arrayList = (ArrayList) jsonPerfil.get("carreiras");
					}else{
						arrayList = (ArrayList) jsonPerfil.get("carreirasInteresse");
					}
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						ArrayList arrayListElementos = new ArrayList(); 
						arrayListElementos = (ArrayList) jsonPerfil.get("habilidades");
				    	Object arrayElementos[] = arrayListElementos.toArray(); 
				    	if (!elemento.equals("undefined")){
				    		ObterHabilidadesCursosNecessarias(elemento, arrayElementos, documentos, false);
				    	}else{
							int w = 0;
							while (w < array.length) {
								if (item.equals("habilidades-necessarias-carreiras") | item.equals("habilidades-interesse-carreiras")){
									ObterHabilidadesCursosNecessarias(array[w], arrayElementos, documentos, false);
								}else{
									ObterHabilidadesCursosNecessarias(array[w], arrayElementos, documentos, true);
								};
								++w;
							};
				    	};
					};
				};
				if (item.equals("habilidades-necessarias-badges") | item.equals("habilidades-interesse-badges") |
						item.equals("cursos-necessarias-badges") | item.equals("cursos-interesse-badges") |
						item.equals("habilidades-necessarias-badge")){
					ArrayList arrayList = new ArrayList(); 
					if (item.equals("habilidades-necessarias-badges") | item.equals("cursos-necessarias-badges")){
						arrayList = (ArrayList) jsonPerfil.get("badges");
					}else{
						arrayList = (ArrayList) jsonPerfil.get("badgesInteresse");
					}
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						ArrayList arrayListElementos = new ArrayList(); 
						arrayListElementos = (ArrayList) jsonPerfil.get("habilidades");
				    	Object arrayElementos[] = arrayListElementos.toArray(); 
				    	if (!elemento.equals("undefined")){
				    		ObterHabilidadesCursosNecessariasBadge(elemento, arrayElementos, documentos, false, jsonPerfil);
				    	}else{
							int w = 0;
							while (w < array.length) {
								if (item.equals("habilidades-necessarias-badges") | item.equals("habilidades-interesse-badges")){
									ObterHabilidadesCursosNecessariasBadge(array[w], arrayElementos, documentos, false, jsonPerfil);
								}else{
									ObterHabilidadesCursosNecessariasBadge(array[w], arrayElementos, documentos, true, jsonPerfil);
								};
								++w;
							};
				    	};
					};
				};
				if (item.equals("cursos-interesse") | item.equals("cursos-andamento") | item.equals("cursos-inscrito") | item.equals("cursos-sugeridos") | item.equals("cursos")){
					ArrayList arrayList = new ArrayList(); 
					if (item.equals("cursos-interesse")){
						arrayList = (ArrayList) jsonPerfil.get("cursosInteresse");
					};
					if (item.equals("cursos-inscrito")){
						arrayList = (ArrayList) jsonPerfil.get("cursosInscrito");
					};
					if (item.equals("cursos-andamento")){
						arrayList = (ArrayList) jsonPerfil.get("cursosAndamento");
					};
					if (item.equals("cursos-sugeridos")){
						arrayList = (ArrayList) jsonPerfil.get("cursosSugeridos");
					};
					if (item.equals("cursos")){
						arrayList = (ArrayList) jsonPerfil.get("cursos");
					};
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						int w = 0;
						while (w < array.length) {
							Mongo mongoCursos = new Mongo();
							DB dbCursos = (DB) mongoCursos.getDB("yggboard");
							DBCollection collectionCursos = dbCursos.getCollection("cursos");
							BasicDBObject searchQueryCursos = new BasicDBObject();
							JSONObject jsonDocumento = new JSONObject();
							if (item.equals("cursos-inscrito")){
								List arrayInscritos = (List) jsonPerfil.get("cursosInscrito");
								JSONObject  cursoInscrito = (JSONObject) arrayInscritos.get(w);
								String id = cursoInscrito.get("id").toString();
								searchQueryCursos.put("documento.id", id);
								jsonDocumento.put("inscricao", cursoInscrito.get("inscricao").toString());
								jsonDocumento.put("todaysDate", commons.todaysDate("inv_month_number"));
							}else{
								searchQueryCursos = new BasicDBObject("documento.id", array[w].toString());
							};
							DBObject cursorCursos = collectionCursos.findOne(searchQueryCursos);
							if (cursorCursos != null){
								BasicDBObject objCursos = (BasicDBObject) cursorCursos.get("documento");
								List arrayParent = (List) objCursos.get("parents");
								if (arrayParent.size() == 0){
									if (jsonPerfil.get("cursosInteresse") != null){
										objCursos.put("interesse", commons.testaElementoArray(objCursos.get("id").toString(), (ArrayList<String>) jsonPerfil.get("cursosInteresse")));
									};
									if (jsonPerfil.get("cursos") != null){
										objCursos.put("possui", commons.testaElementoArray(objCursos.get("id").toString(), (ArrayList<String>) jsonPerfil.get("cursos")));
									};
									if (objCursos.get("habilidades") != null){
										objCursos.put("habilidadesPerfil", commons.montaArrayPerfil(jsonPerfil.get("habilidades"), objCursos.get("habilidades")));
									};
									jsonDocumento.put("documento", objCursos);
									documentos.add(jsonDocumento);
								};
							};
							mongoCursos.close();
							++w;
						};
					};
				};
				if (item.equals("relacionamentos")){
					ArrayList arrayList = new ArrayList(); 
					arrayList = (ArrayList) jsonPerfil.get("relacionamentos");
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						int w = 0;
						while (w < array.length) {
							documentos.add(getUserPerfil(array[w].toString()).get("usuario"));
							++w;
						};
					};
				};
				if (item.equals("seguindo")){
					ArrayList arrayList = new ArrayList(); 
					arrayList = (ArrayList) jsonPerfil.get("seguindo");
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						int w = 0;
						while (w < array.length) {
							documentos.add(getUserPerfil(array[w].toString()).get("usuario"));
							++w;
						};
					};
				};
				if (item.equals("sugestao-relacionamentos")){
					ArrayList arrayList = new ArrayList(); 
					arrayList = (ArrayList) jsonPerfil.get("seguindo");
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
						int w = 0;
						while (w < array.length) {
							BasicDBObject objDoc = (BasicDBObject) getUserPerfil(array[w].toString()).get("userPerfil");
							ArrayList arrayListSeguindo = new ArrayList();
							if (objDoc != null){
								arrayListSeguindo = (ArrayList) objDoc.get("seguindo");
							};
							if (arrayListSeguindo != null){
						    	Object arraySeguindo[] = arrayListSeguindo.toArray(); 
								int z = 0;
								while (z < arraySeguindo.length) {
									documentos.add(getUserPerfil(arraySeguindo[z].toString()).get("usuario"));									
									++z;
								};
							};
							++w;
						};
					};
				};
				if (item.equals("objetivos")){
					ArrayList arrayList = new ArrayList(); 
					arrayList = (ArrayList) jsonPerfil.get("carreiras");
					JSONObject totais = new JSONObject();
					int habilidadesGeralFaltantes = 0;
					int habilidadesGeralPossui = 0;
					totais.put("faltantes", habilidadesGeralFaltantes);
					totais.put("possui", habilidadesGeralPossui);
					JSONObject jsonDocumento = new JSONObject();
					ArrayList arrayListObjetivos = new ArrayList(); 
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
				    	for (int i = 0; i < array.length; i++) {
				    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), jsonDocumento, usuario, totais, jsonPerfil));
						};
					};
					arrayList = (ArrayList) jsonPerfil.get("carreirasInteresse");
					if (arrayList != null){
				    	Object array[] = arrayList.toArray(); 
				    	for (int i = 0; i < array.length; i++) {
				    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), jsonDocumento, usuario, totais, jsonPerfil));
						};
					};
					jsonDocumento.put("objetivos", arrayListObjetivos);
					habilidadesGeralFaltantes = (int) totais.get("faltantes");
					habilidadesGeralPossui = (int) totais.get("possui");
					jsonDocumento.put("habilidadesGeral", commons.totalArray(jsonPerfil.get("habilidades")));
					jsonDocumento.put("habilidadesInteresseGeral", commons.totalArray(jsonPerfil.get("habilidadesInteresse")));
					jsonDocumento.put("habilidadesGeralPossui", Integer.toString(habilidadesGeralPossui));
					jsonDocumento.put("habilidadesGeralFaltantes", Integer.toString(habilidadesGeralFaltantes));
					jsonDocumento.put("badgesGeral", commons.totalArray(jsonPerfil.get("badges")));
					jsonDocumento.put("badgesConquistaGeral", commons.totalArray(jsonPerfil.get("badgesConquista")));
					jsonDocumento.put("cursosAndamentoGeral", commons.totalArray(jsonPerfil.get("cursosAndamento")));
					jsonDocumento.put("cursosGeral", commons.totalArray(jsonPerfil.get("cursos")));
					jsonDocumento.put("cursosInteresseGeral", commons.totalArray(jsonPerfil.get("cursosInteresse")));
					jsonDocumento.put("cursosInscritoGeral", commons.totalArray(jsonPerfil.get("cursosInscrito")));
					jsonDocumento.put("cursosAndamentoGeral", commons.totalArray(jsonPerfil.get("cursosAndamento")));
					documentos.add(jsonDocumento);
				};
				System.out.println("chamada userperfil saida:" + item);

				mongo.close();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return documentos;
		};
		return null;
	};
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaObjetivos(String id, JSONObject jsonDocumento, String usuario, JSONObject totais, JSONObject jsonPerfil) {
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		Response response = commons_db.getCollection(id, "objetivos", "documento.id");
		BasicDBObject objObjetivo = new BasicDBObject();
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) response.getEntity());
			if (doc != null){
				objObjetivo.putAll((Map) doc.get("documento"));
		     	ArrayList arrayListNecessarios = new ArrayList();
		    	arrayListNecessarios = (ArrayList) objObjetivo.get("necessarios");
		     	ArrayList arrayListNecessariosNome = new ArrayList();
		    	arrayListNecessariosNome = (ArrayList) objObjetivo.get("necessariosNome");
		     	ArrayList arrayPreRequisitosGeral = new ArrayList();
		     	arrayPreRequisitosGeral = (ArrayList) objObjetivo.get("preRequisitosGeral");
				int totalHabilidades = arrayListNecessarios.size();
				if (arrayPreRequisitosGeral != null){
			    	Object objPreRequisitosGeral[] = arrayPreRequisitosGeral.toArray();
			    	totalHabilidades = totalHabilidades + carregaPreRequisistosNecessarios(arrayListNecessarios, arrayListNecessariosNome, objPreRequisitosGeral);
				};
				ArrayList arrayListElementos = new ArrayList(); 
				arrayListElementos = (ArrayList) jsonPerfil.get("habilidades");
				if (arrayListElementos != null){
			    	Object[] arrayElementos = arrayListElementos.toArray(); 
			    	JSONArray arrayListElementosFaltantes = new JSONArray(); 
			    	JSONObject jsonQtdeHabilidades = ObterHabilidadesFaltantes(doc, arrayElementos, arrayListElementosFaltantes);
			    	objObjetivo.put("interesse", commons.testaElementoArray(objObjetivo.get("id").toString(), (ArrayList<String>) jsonPerfil.get("carreirasInteresse")));
			    	objObjetivo.put("possui", commons.testaElementoArray(objObjetivo.get("id").toString(), (ArrayList<String>) jsonPerfil.get("carreiras")));
			    	objObjetivo.put("necessariosPerfil", commons.montaArrayPerfil(jsonPerfil.get("habilidades"), objObjetivo.get("necessarios")));
			    	objObjetivo.put("totalHabilidades", Integer.toString(totalHabilidades));
			    	objObjetivo.put("totalPossuiHabilidades", jsonQtdeHabilidades.get("totalPossuiHabilidades"));
			    	int habilidadesGeralPossui = (int) totais.get("possui");
			    	int habilidadesGeralFaltantes = (int) totais.get("faltantes");
			    	habilidadesGeralPossui = habilidadesGeralPossui + Integer.valueOf(jsonQtdeHabilidades.get("totalPossuiHabilidades").toString());
			    	habilidadesGeralFaltantes = habilidadesGeralFaltantes + (totalHabilidades - Integer.valueOf(jsonQtdeHabilidades.get("totalPossuiHabilidades").toString()));
					totais.put("faltantes", habilidadesGeralFaltantes);
					totais.put("possui", habilidadesGeralPossui);			    	
				};
			};
		};
		return objObjetivo;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void carregaBadges(ArrayList arrayList, String usuario, JSONObject jsonPerfil, JSONArray documentos, boolean atualizaPerfil) {
		Commons_DB commons_db = new Commons_DB();
		if (arrayList != null){
	    	Object array[] = arrayList.toArray(); 
			int w = 0;
			while (w < array.length) {
				Response response = commons_db.getCollection(array[w].toString(), "badges", "documento.id");
				if (!(response.getEntity() instanceof Boolean)){
					BasicDBObject doc = new BasicDBObject();
					doc.putAll((Map) response.getEntity());
					if (doc != null){
						JSONObject objBadges = new JSONObject();
						objBadges.putAll((Map) doc.get("documento"));
						incluirBadge(objBadges, usuario, jsonPerfil, documentos, atualizaPerfil);
					};
				};
				++w;
			};
		};
	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void incluirBadge(JSONObject jsonBadge, String usuario, JSONObject jsonPerfil, JSONArray documentos, Boolean atualizaPerfil) {

		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();

		JSONObject jsonDocumento = new JSONObject();
		jsonDocumento.put("_id", jsonBadge.get("_id"));
		jsonDocumento.put("id", jsonBadge.get("id"));
		jsonDocumento.put("nome", jsonBadge.get("nome"));
		jsonDocumento.put("comoGanhar", jsonBadge.get("comoGanhar"));
		jsonDocumento.put("titulo", jsonBadge.get("titulo"));
		jsonDocumento.put("textoPost", jsonBadge.get("textoPost"));
		jsonDocumento.put("popup", jsonBadge.get("popup"));
		jsonDocumento.put("badge", jsonBadge.get("badge"));
		jsonDocumento.put("entidadeCertificadora", jsonBadge.get("entidadeCertificadora"));
	    jsonDocumento.put("descricao", jsonBadge.get("descricao"));
	    jsonDocumento.put("habilidades", jsonBadge.get("habilidades")); 
	    jsonDocumento.put("tags", jsonBadge.get("tags"));
	    jsonDocumento.put("totalHabilidades", "");
	    jsonDocumento.put("totalPossuiHabilidades", "");
		jsonDocumento.put("arrayHabilidades", "");

		ArrayList arrayListElementos = new ArrayList(); 
		arrayListElementos = (ArrayList) jsonPerfil.get("habilidades");
    	Object arrayElementos[] = arrayListElementos.toArray(); 
		ArrayList <String> arrayListElementosFaltantes = new ArrayList();
	    JSONObject jsonQtdeHabilidades = ObterTotalHabilidadesBadges(jsonBadge.get("id"), arrayElementos, arrayListElementosFaltantes);
	    jsonDocumento.put("totalHabilidades", arrayListElementos.size());
	    jsonDocumento.put("totalPossuiHabilidades", jsonQtdeHabilidades.get("totalPossuiHabilidades"));
    	ArrayList arrayListHabilidades = new ArrayList(); 
    	arrayListHabilidades = (ArrayList) jsonBadge.get("habilidades");
    	if (arrayListHabilidades != null){
    		Object arrayHabilidades[] = arrayListHabilidades.toArray();
			JSONArray newHabilidades = new JSONArray();
			int z = 0;
			while (z < arrayHabilidades.length) {
				newHabilidades.add(commons.nomeHabilidade(arrayHabilidades[z].toString()));
				++z;
			};
			jsonDocumento.remove("habilidades");
			jsonDocumento.put("habilidades", newHabilidades);				
			z = 0;
			JSONArray habilidadesArray = new JSONArray();
			while (z < arrayListElementosFaltantes.size()) {
				Response response = commons_db.getCollection(arrayListElementosFaltantes.get(z), "habilidades", "documento.id");
				if (!(response.getEntity() instanceof Boolean)){
					BasicDBObject doc = new BasicDBObject();
					doc.putAll((Map) response.getEntity());
					if (doc != null){
						BasicDBObject objCarreira = (BasicDBObject) doc.get("documento");
						JSONObject jsonHabilidades = new JSONObject();
						jsonHabilidades.put("id", arrayListElementosFaltantes.get(z));
						jsonHabilidades.put("nome", objCarreira.get("nome"));
						JSONArray cursos = new JSONArray();
						ObterCursosNecessarios (arrayListElementosFaltantes.get(z), cursos, jsonPerfil);
						jsonHabilidades.put("cursos", cursos);
						habilidadesArray.add (jsonHabilidades);
					};
				};
				++z;
			};
			jsonDocumento.put("arrayHabilidades", habilidadesArray);
    	};
		
		documentos.add(jsonDocumento);		
		
		if (atualizaPerfil){
			JSONObject newPerfil = new JSONObject();				
			newPerfil.put("usuario", usuario);
			newPerfil.put("tipo", "badgesConquista");
			newPerfil.put("id", jsonBadge.get("id"));
			newPerfil.put("inout", "in");
			try {
				AtualizarPerfil(newPerfil);
			} catch (MongoException | IOException e) {
				e.printStackTrace();
			}
		};
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject montaCarreira(BasicDBObject objCarreirasSource, JSONObject jsonPerfil) {

		BasicDBObject objCarreiras = (BasicDBObject) objCarreirasSource.get("documento");
		BasicDBObject jsonDocumento = new BasicDBObject();
		jsonDocumento.put("_id", objCarreirasSource.get("_id"));
		jsonDocumento.put("nome", objCarreiras.get("nome"));
		jsonDocumento.put("nivel", objCarreiras.get("nivel"));
		jsonDocumento.put("nivelFiltro", objCarreiras.get("nivelFiltro"));
		jsonDocumento.put("id", objCarreiras.get("id"));
	    jsonDocumento.put("descricao", objCarreiras.get("nome"));
	    jsonDocumento.put("salarioMinimo", objCarreiras.get("salarioMinimo")); 
	    jsonDocumento.put("salarioMedio", objCarreiras.get("salarioMedio"));
	    jsonDocumento.put("salarioMaximo", objCarreiras.get("salarioMaximo"));
	    jsonDocumento.put("segmentoEconomico", objCarreiras.get("segmentoEconomico"));
	    jsonDocumento.put("responsabilidades", objCarreiras.get("responsabilidades"));
	    jsonDocumento.put("atividades", objCarreiras.get("atividades"));
	    jsonDocumento.put("areaAtuacao", objCarreiras.get("areaAtuacao")); 
	    jsonDocumento.put("recomendados", objCarreiras.get("recomendados"));
	    jsonDocumento.put("recomendadosNome", objCarreiras.get("recomendadosNome"));
	    jsonDocumento.put("tags", objCarreiras.get("tags"));
     	ArrayList arrayListNecessarios = new ArrayList();
    	arrayListNecessarios = (ArrayList) objCarreiras.get("necessarios");
     	ArrayList arrayListNecessariosNome = new ArrayList();
    	arrayListNecessariosNome = (ArrayList) objCarreiras.get("necessariosNome");
     	ArrayList arrayPreRequisitosGeral = new ArrayList();
     	arrayPreRequisitosGeral = (ArrayList) objCarreiras.get("preRequisitosGeral");
		int totalHabilidades = arrayListNecessarios.size();
		if (arrayPreRequisitosGeral != null){
	    	Object objPreRequisitosGeral[] = arrayPreRequisitosGeral.toArray();
	    	totalHabilidades = totalHabilidades + carregaPreRequisistosNecessarios(arrayListNecessarios, arrayListNecessariosNome, objPreRequisitosGeral);
		};
	    jsonDocumento.put("necessarios", arrayListNecessarios); 
	    jsonDocumento.put("necessariosNome", arrayListNecessariosNome); 
		ArrayList arrayListElementos = new ArrayList(); 
		arrayListElementos = (ArrayList) jsonPerfil.get("habilidades");
		if (arrayListElementos != null){
	    	Object[] arrayElementos = arrayListElementos.toArray(); 
			JSONArray arrayListElementosFaltantes = new JSONArray(); 
		    JSONObject jsonQtdeHabilidades = ObterHabilidadesFaltantes(objCarreirasSource, arrayElementos, arrayListElementosFaltantes);
		    jsonDocumento.put("totalHabilidades", Integer.toString(totalHabilidades));
		    jsonDocumento.put("totalPossuiHabilidades", jsonQtdeHabilidades.get("totalPossuiHabilidades"));
			int z = 0;
			JSONArray necessariosArray = new JSONArray();
			while (z < arrayListElementosFaltantes.size()) {
				Mongo mongoHabilidade;
				try {
					mongoHabilidade = new Mongo();
					DB dbHabilidade = (DB) mongoHabilidade.getDB("yggboard");
					DBCollection collectionHabilidade = dbHabilidade.getCollection("habilidades");
					BasicDBObject searchQueryHabilidade = new BasicDBObject("documento.id", arrayListElementosFaltantes.get(z));
					DBObject cursorHabilidade = collectionHabilidade.findOne(searchQueryHabilidade);
					if (cursorHabilidade != null){
						BasicDBObject objCarreira = (BasicDBObject) cursorHabilidade.get("documento");
						JSONObject jsonNecessarios = new JSONObject();
						jsonNecessarios.put("id", arrayListElementosFaltantes.get(z));
						jsonNecessarios.put("nome", objCarreira.get("nome"));
						jsonNecessarios.put("descricao", objCarreira.get("descricao"));
						jsonNecessarios.put("wiki", objCarreira.get("wiki"));
						jsonNecessarios.put("amazon", objCarreira.get("amazon"));
						jsonNecessarios.put("preRequisitos", objCarreira.get("preRequisitos"));
						jsonNecessarios.put("preRequisitosNome", objCarreira.get("preRequisitosNome"));
						JSONArray cursos = new JSONArray();
						ObterCursosNecessarios (arrayListElementosFaltantes.get(z), cursos, jsonPerfil);
						jsonNecessarios.put("cursos", cursos);
						necessariosArray.add (jsonNecessarios);
					};
					mongoHabilidade.close();
					++z;
				} catch (UnknownHostException | MongoException e) {
					e.printStackTrace();
				}
			}
			jsonDocumento.put("arrayNecessarios", necessariosArray);
		};
		return jsonDocumento;
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int carregaPreRequisistosNecessarios(ArrayList arrayListNecessarios, ArrayList arrayListNecessariosNome, Object[] objPreRequisitosGeral) {
		Commons commons = new Commons();
		int totalPreRequisitos = 0;
		int z = 0;
		while (z < objPreRequisitosGeral.length) {
			String preRequisitos = objPreRequisitosGeral[z].toString().split(":")[0].toString().replace("|", "&");
			String [] array = preRequisitos.split("&");
			int i = 0;
			Boolean preRequisitoValido = false;
			while (i < array.length) {
				if (!commons.testaElementoArray(array[i], arrayListNecessarios)){
					arrayListNecessarios.add(array[i]);
					arrayListNecessariosNome.add(commons.nomeHabilidade(array[i]));
					preRequisitoValido = true;
				};
				++i;
			};
			if (preRequisitoValido){
				++totalPreRequisitos;				
			};
			++z;
		};
		return totalPreRequisitos;

	};
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Path("/atualizar/perfil")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AtualizarPerfil(JSONObject newPerfil) throws MongoException, JsonParseException, JsonMappingException, IOException {
		System.out.println("atualiza perfil:" + newPerfil.get("tipo") + "- inout:"  + newPerfil.get("inout") + " - id:"  + newPerfil.get("id"));
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		Response response = commons_db.getCollection(newPerfil.get("usuario").toString(), "userPerfil", "documento.usuario");
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) response.getEntity());
			if (doc != null){
				BasicDBObject objUserPerfil = new BasicDBObject();
				objUserPerfil = (BasicDBObject) doc.get("documento");
				Boolean origemErro = false;
				String tipo = "";
				if (newPerfil.get("tipo").toString() != null){
					tipo = newPerfil.get("tipo").toString();
				}else{
					origemErro = true;
				};
				String assunto = "";
				if (newPerfil.get("assunto").toString() != null){
					assunto = newPerfil.get("assunto").toString();
				};
				String elemento = "";
				if (newPerfil.get("id").toString() != null){
					elemento = newPerfil.get("id").toString();
				}else{
					origemErro = true;
				};
				String inout = "in";
				if (newPerfil.get("inout").toString() != null){
					inout = newPerfil.get("inout").toString();
					if (!inout.equals("in") && !inout.equals("out")){
						origemErro = true;	
					};
				}else{
					origemErro = true;
				}
				if (origemErro){
					return Response.status(400).build();
				};
				Boolean existente = false;
				List<String> array = new ArrayList<String>();
				if (objUserPerfil.get(tipo) != null){
					array = (List<String>) objUserPerfil.get(tipo);
				};
				BasicDBObject objUserPerfilUpdate = (BasicDBObject) doc.get("documento");
				if (array != null){
					for (int i = 0; i < array.size(); i++) {
						if (elemento.equals(array.get(i).toString())){
							existente = true;
							if (inout.equals("out")){
								array.remove(i);
							};
						};
					};
				};
				ArrayList<JSONObject> fieldsArray = new ArrayList<>();
				if (!existente){
					if (inout.equals("in")){
						array.add(elemento);
						if (tipo.equals("habilidades") || tipo.equals("habilidadesInteresse")){
							atualizaDependencia(elemento, array);
						};
						if (tipo.equals("carreiras")){
							if (assunto.equals("cadastro")){
								ArrayList habilidadesUpdate = AtualizaUserPerfilArray(elemento, objUserPerfil.get("habilidadesInteresse"), "objetivos", "necessarios");
								JSONObject field = new JSONObject();
								field.put("field", "habilidadesInteresse");
								field.put("value", habilidadesUpdate);
								fieldsArray.add(field);
							}else{
								ArrayList habilidadesUpdate = AtualizaUserPerfilArray(elemento, objUserPerfil.get("habilidades"), "objetivos", "habilidades");
								JSONObject field = new JSONObject();
								field.put("field", "habilidades");
								field.put("value", habilidadesUpdate);
								fieldsArray.add(field);
							};
						};
						if (tipo.equals("cursos")){
							if (assunto.equals("cadastro")){
								ArrayList habilidadesUpdate = AtualizaUserPerfilArray(elemento, objUserPerfil.get("habilidadesInteresse"), "objetivos", "habilidades");
								JSONObject field = new JSONObject();
								field.put("field", "habilidadesInteresse");
								field.put("value", habilidadesUpdate);
								fieldsArray.add(field);
							};
						};
		
					};
				};
	
				ArrayList<JSONObject> keysArray = new ArrayList<>();
				JSONObject key = new JSONObject();
				key.put("key", "documento.usuario");
				key.put("value", newPerfil.get("usuario").toString());
				keysArray.add(key);

				JSONObject field = new JSONObject();
				field.put("field", tipo);
				field.put("value", array);
				fieldsArray.add(field);
								
				Response atualizacao = commons_db.atualizarCrud("userPerfil", fieldsArray, keysArray);
				
				if (!(response.getEntity() instanceof Boolean)){
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", newPerfil.get("usuario").toString());
					evento.put("evento", "userPerfil");
					evento.put("idEvento", newPerfil.get("usuario").toString());
					evento.put("motivo", newPerfil.get("inout").toString());
					evento.put("elemento", tipo);
					evento.put("idElemento", newPerfil.get("id").toString());
					atualizacao = commons.insereEvento(evento);
					return atualizacao;
				}else{
					return response;
				}
			};
			return null;
		};
		return response;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList AtualizaUserPerfilArray(String elemento, Object objHabilidades, String collection, String arrayCollection) {
		
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		ArrayList<String> arrayUpdate = (ArrayList<String>) objHabilidades;

		Response response = commons_db.getCollection(elemento, collection, "documento.id");
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) response.getEntity());
			if (doc != null){
				BasicDBObject objDoc = (BasicDBObject) doc.get("documento");
				ArrayList<String> array = (ArrayList<String>) objDoc.get(arrayCollection);
				if (array != null){
					for (int i = 0; i < array.size(); i++) {
						if (!commons.testaElementoArray(elemento, arrayUpdate)){
							arrayUpdate.add(elemento);
						};
					};
				};
			};
		};
				
		return arrayUpdate;
	};
	
	private void atualizaDependencia(String elemento, List<String> array) {
		Commons commons = new Commons();
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("habilidades");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", elemento);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objHabilidade = (BasicDBObject) cursor.get("documento");
				@SuppressWarnings("unchecked")
				List<String> arrayPreRequisitos = (List<String>) objHabilidade.get("preRequisitos");
				for (int i = 0; i < arrayPreRequisitos.size(); i++) {
					Boolean existente = false;
					for (int w = 0; w < array.size(); w++) {
						if (commons.preRequisito(arrayPreRequisitos.get(i).toString()) == array.get(i).toString()){
							existente = true;
						};
					};
					if (!existente){
						array.add(commons.preRequisito(arrayPreRequisitos.get(i).toString()));
					};					
				};
			};
			mongo.close();
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		};
	};
	@SuppressWarnings({ "unchecked", "unused" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterUsersPerfil() {

		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			BasicDBObject setQuery = new BasicDBObject();
			DBCollection collection = db.getCollection("userPerfil");			
			DBCursor cursor = collection.find(setQuery);
			JSONArray documentos = new JSONArray();
			while (((Iterator<DBObject>) cursor).hasNext()) {
				JSONParser parser = new JSONParser(); 
				BasicDBObject objUserPerfil = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				String documento = objUserPerfil.getString("documento");
				try {
					JSONObject jsonObject; 
					jsonObject = (JSONObject) parser.parse(documento);
					JSONObject jsonDocumento = new JSONObject();
				    jsonDocumento.put("documento", (JSONObject) parser.parse(documento));
					documentos.add(jsonDocumento);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			};
			mongo.close();
			return documentos;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	};

	@SuppressWarnings("unchecked")
	private ArrayList<String> ObterHabilidadesCursosNecessarias(Object carreira, Object[] arrayElementos, JSONArray documentos, Boolean obterCursos) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("objetivos");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", carreira);
			DBObject cursor = collection.findOne(searchQuery);
			BasicDBObject objCarreira = (BasicDBObject) cursor.get("documento");
			ArrayList<String> arrayListHabilidades = new ArrayList<String>(); 
			arrayListHabilidades = (ArrayList<String>) objCarreira.get("necessarios");
	    	Object arrayHabilidades[] = arrayListHabilidades.toArray(); 
			int w = 0;
			while (w < arrayHabilidades.length) {
				Boolean existeHabilidade = false;
				int z = 0;
				while (z < arrayElementos.length) {
					if (arrayHabilidades[w].equals(arrayElementos[z])){
						existeHabilidade = true;
					}
					++z;
				};
				if (!obterCursos){
					int i = 0;
					while (i < documentos.size()) {
						JSONObject jsonObject = (JSONObject) documentos.get(i);
						BasicDBObject objDocumento = (BasicDBObject) jsonObject.get("documento");
						if (arrayHabilidades[w].equals(objDocumento.getString("id"))){
							existeHabilidade = true;
						}
						++i;
					};
				};
				if (!existeHabilidade){
					Mongo mongoHabilidades = new Mongo();
					DB dbHabilidade = (DB) mongoHabilidades.getDB("yggboard");
					DBCollection collectionHabilidades = dbHabilidade.getCollection("habilidades");
					BasicDBObject searchQueryHabilidades = new BasicDBObject("documento.id", arrayHabilidades[w]);
					DBObject cursorHabilidades = collectionHabilidades.findOne(searchQueryHabilidades);
					BasicDBObject objHabilidades = (BasicDBObject) cursorHabilidades.get("documento");
					if (obterCursos){
						ObterCursosNecessarios (arrayHabilidades[w], documentos, null);
					}else{
						JSONObject jsonDocumento = new JSONObject();
					    jsonDocumento.put("documento", objHabilidades);
						documentos.add(jsonDocumento);
					}
					mongoHabilidades.close();					
				};
				++w;
			};
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> ObterHabilidadesCursosNecessariasBadge(Object badge, Object[] arrayElementos, JSONArray documentos, Boolean obterCursos, JSONObject jsonPerfil) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("badges");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", badge);
			DBObject cursor = collection.findOne(searchQuery);
			BasicDBObject objBadge = (BasicDBObject) cursor.get("documento");
			ArrayList<String> arrayListHabilidades = new ArrayList<String>(); 
			arrayListHabilidades = (ArrayList<String>) objBadge.get("habilidades");
	    	Object arrayHabilidades[] = arrayListHabilidades.toArray(); 
			int w = 0;
			while (w < arrayHabilidades.length) {
				Boolean existeHabilidade = false;
				int z = 0;
				while (z < arrayElementos.length) {
					if (arrayHabilidades[w].equals(arrayElementos[z])){
						existeHabilidade = true;
					}
					++z;
				};
				if (!obterCursos){
					int i = 0;
					while (i < documentos.size()) {
						JSONObject jsonObject = (JSONObject) documentos.get(i);
						BasicDBObject objDocumento = (BasicDBObject) jsonObject.get("documento");
						if (arrayHabilidades[w].equals(objDocumento.getString("id"))){
							existeHabilidade = true;
						}
						++i;
					};
				};
				if (!existeHabilidade){
					Mongo mongoHabilidades = new Mongo();
					DB dbHabilidade = (DB) mongoHabilidades.getDB("yggboard");
					DBCollection collectionHabilidades = dbHabilidade.getCollection("habilidades");
					BasicDBObject searchQueryHabilidades = new BasicDBObject("documento.id", arrayHabilidades[w]);
					DBObject cursorHabilidades = collectionHabilidades.findOne(searchQueryHabilidades);
					BasicDBObject objHabilidades = (BasicDBObject) cursorHabilidades.get("documento");
					if (obterCursos){
						ObterCursosNecessarios (arrayHabilidades[w], documentos, jsonPerfil);
					}else{
						JSONObject jsonDocumento = new JSONObject();
					    jsonDocumento.put("documento", objHabilidades);
						documentos.add(jsonDocumento);
					}
					mongoHabilidades.close();					
				};
				++w;
			};
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject ObterHabilidadesFaltantes (BasicDBObject carreira, Object[] arrayElementos, JSONArray arrayListElementosFaltantes) {
		Commons commons = new Commons();
		BasicDBObject objCarreira = (BasicDBObject) carreira.get("documento");
		ArrayList<String> arrayListHabilidadesPossui = new ArrayList<String>(); 
		ArrayList<String> arrayListHabilidades = new ArrayList<String>(); 
		arrayListHabilidades = (ArrayList<String>) objCarreira.get("preRequisitosGeral");
    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
    	int totalPossuiHabilidades = 0;
		int w = 0;
		while (w < arrayHabilidades.length) {
			String preRequisitos = arrayHabilidades[w].toString().split(":")[0].toString().replace("|", "&");
			String [] array = preRequisitos.split("&");
			int i = 0;
			Boolean temHabilidade = false;
			Boolean somarPossui = false;
			while (i < array.length) {
				int z = 0;
				while (z < arrayElementos.length) {
					if (array[i].equals(arrayElementos[z])){
						if (!(commons.testaElementoArray(array[i], arrayListHabilidadesPossui))){
							arrayListHabilidadesPossui.add(array[i]);
							somarPossui = true;
						};
						temHabilidade = true;
					};
					++z;
				};
				++i;
			};
			if (somarPossui){	
				++totalPossuiHabilidades;				
			};
			if (!temHabilidade){	
				int j = 0;
				while (j < array.length) {
					commons.addString(arrayListElementosFaltantes, array[j]);
					++j;
				};
			};
			++w;
		};
		JSONObject jsonQtdeHabilidades = new JSONObject();
		jsonQtdeHabilidades.put("totalPossuiHabilidades", totalPossuiHabilidades);
		return jsonQtdeHabilidades;
	};
	
	@SuppressWarnings("unchecked")
	private JSONObject ObterTotalHabilidadesBadges (Object id, Object[] arrayElementos, ArrayList<String> arrayListElementosFaltantes) {
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");
			DBCollection collection = db.getCollection("badges");
			BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
			DBObject cursor = collection.findOne(searchQuery);
			if (cursor != null){
				BasicDBObject objBadge = (BasicDBObject) cursor.get("documento");
				ArrayList<String> arrayListHabilidades = new ArrayList<String>(); 
				arrayListHabilidades = (ArrayList<String>) objBadge.get("habilidades");
		    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
		    	int totalHabilidades = 0;
		    	int totalPossuiHabilidades = 0;
				int w = 0;
				while (w < arrayHabilidades.length) {
					Boolean temHabilidade = false;
					int z = 0;
					while (z < arrayElementos.length) {
						if (arrayHabilidades[w].equals(arrayElementos[z])){
							++totalPossuiHabilidades;
							temHabilidade = true;
						}
						++z;
					};
					if (!temHabilidade){
						arrayListElementosFaltantes.add((String) arrayHabilidades[w]); 
					}
					++w;
					++totalHabilidades;
				};
				JSONObject jsonQtdeHabilidades = new JSONObject();
				jsonQtdeHabilidades.put("totalHabilidades", totalHabilidades);
				jsonQtdeHabilidades.put("totalPossuiHabilidades", totalPossuiHabilidades);
				mongo.close();
				return jsonQtdeHabilidades;
			}else{
				JSONObject jsonQtdeHabilidades = new JSONObject();
				jsonQtdeHabilidades.put("totalHabilidades", 0);
				jsonQtdeHabilidades.put("totalPossuiHabilidades", 0);
				mongo.close();
				return jsonQtdeHabilidades;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	};

	@SuppressWarnings("unchecked")
	private ArrayList<String> ObterCursosNecessarios (Object habilidade, JSONArray documentos, JSONObject jsonPerfil) {
		Commons commons = new Commons();
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB("yggboard");

			BasicDBObject setQuery = new BasicDBObject();
		    if (habilidade != null){
		    	setQuery.put("documento.habilidades", habilidade);
		    };
			DBCollection collection = db.getCollection("cursos");
			
			DBCursor cursor = collection.find(setQuery);
			while (((Iterator<DBObject>) cursor).hasNext()) {
				JSONParser parser = new JSONParser(); 
				BasicDBObject objCurso = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				String documento = objCurso.getString("documento");
				try {
					JSONObject jsonCurso = new JSONObject();
					jsonCurso.put("documento", (JSONObject) parser.parse(documento));
					Boolean existeCurso = false;
					int i = 0;
					while (i < documentos.size()) {
						JSONObject jsonElementoArray = (JSONObject) documentos.get(i);
						if (jsonCurso.get("documento").equals(jsonElementoArray.get("documento"))){
							existeCurso = true;
						}
						++i;
					};
					if (!existeCurso){
						JSONObject objCursos = (JSONObject) jsonCurso.get("documento");
						@SuppressWarnings("rawtypes")
						List arrayParent = (List) objCursos.get("parents");
						if (arrayParent.size() == 0){
							if (jsonPerfil.get("cursosInteresse") != null){
								objCursos.put("interesse", commons.testaElementoArray(objCursos.get("id").toString(), (ArrayList<String>) jsonPerfil.get("cursosInteresse")));
							};
							if (jsonPerfil.get("cursos") != null){
								objCursos.put("possui", commons.testaElementoArray(objCursos.get("id").toString(), (ArrayList<String>) jsonPerfil.get("cursos")));
							};
							if (objCursos.get("habilidades") != null){
								objCursos.put("habilidadesPerfil", commons.montaArrayPerfil(jsonPerfil.get("habilidades"), objCursos.get("habilidades")));
							};
							documentos.add(jsonCurso);
						};
					};
				} catch (ParseException e) {
					e.printStackTrace();
				}
			};
			mongo.close();
			return documentos;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Path("/cursosSugeridos")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CursosSugeridos(JSONObject inputCursosSugeridos)  {
		
		Commons commons = new Commons();
		Mongo mongo;
			try {
				mongo = new Mongo();
				DB db = (DB) mongo.getDB("yggboard");
				DBCollection collection = db.getCollection("userPerfil");
				List arrayCursosSugeridos = (List) inputCursosSugeridos.get("cursosSugeridos");
				for (int i = 0; i < arrayCursosSugeridos.size(); i++) {
					JSONObject cursosSugeridos = new JSONObject();
					cursosSugeridos.putAll((Map) arrayCursosSugeridos.get(i));
					String usuario = (String) cursosSugeridos.get("usuario");
					BasicDBObject searchQuery = new BasicDBObject("documento.usuario", usuario);
					DBObject cursor = collection.findOne(searchQuery);
					if (cursor == null){
						mongo.close();
						return Response.status(404).build();
					};
					BasicDBObject objUserPerfilUpdate = (BasicDBObject) cursor.get("documento");
					objUserPerfilUpdate.remove("cursosSugeridos");
					objUserPerfilUpdate.put("cursosSugeridos", cursosSugeridos.get("cursos"));
					BasicDBObject objUserPerfilDocumento = new BasicDBObject();
					objUserPerfilDocumento.put("documento", objUserPerfilUpdate);
					BasicDBObject update = new BasicDBObject("$set", new BasicDBObject(objUserPerfilDocumento));
					searchQuery = new BasicDBObject("documento.usuario", usuario);
					cursor = collection.findAndModify(searchQuery,
			                null,
			                null,
			                false,
			                update,
			                true,
			                false);
					// incluir evento
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", usuario);
					evento.put("evento", "userPerfil");
					evento.put("idEvento", "cursosSugeridos");
					evento.put("motivo", "inclusao");
					evento.put("elemento", "cursosSugeridos");
					evento.put("idElemento", cursosSugeridos.get("cursos"));
					commons.insereEvento(evento);
				};
				mongo.close();
				return Response.status(200).build();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			};
			return null;
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Path("/carreirasSugeridas")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CarreirasSugeridos(JSONObject inputCarreirasSugeridas)  {
		
		Commons commons = new Commons();
		Mongo mongo;
			try {
				mongo = new Mongo();
				DB db = (DB) mongo.getDB("yggboard");
				DBCollection collection = db.getCollection("userPerfil");
				List arrayCarreirasSugeridos = (List) inputCarreirasSugeridas.get("carreirasSugeridas");
				for (int i = 0; i < arrayCarreirasSugeridos.size(); i++) {
					JSONObject carreirasSugeridos = new JSONObject();
					carreirasSugeridos.putAll((Map) arrayCarreirasSugeridos.get(i));
					String usuario = (String) carreirasSugeridos.get("usuario");
					BasicDBObject searchQuery = new BasicDBObject("documento.usuario", usuario);
					DBObject cursor = collection.findOne(searchQuery);
					if (cursor == null){
						mongo.close();
						return Response.status(404).build();
					};
					BasicDBObject objUserPerfilUpdate = (BasicDBObject) cursor.get("documento");
					objUserPerfilUpdate.remove("carreirasSugeridas");
					objUserPerfilUpdate.put("carreirasSugeridas", carreirasSugeridos.get("carreiras"));
					BasicDBObject objUserPerfilDocumento = new BasicDBObject();
					objUserPerfilDocumento.put("documento", objUserPerfilUpdate);
					BasicDBObject update = new BasicDBObject("$set", new BasicDBObject(objUserPerfilDocumento));
					searchQuery = new BasicDBObject("documento.usuario", usuario);
					cursor = collection.findAndModify(searchQuery,
			                null,
			                null,
			                false,
			                update,
			                true,
			                false);
					// incluir evento
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", usuario);
					evento.put("evento", "userPerfil");
					evento.put("idEvento", "carreirasSugeridas");
					evento.put("motivo", "inclusao");
					evento.put("elemento", "carreirasSugeridas");
					evento.put("idElemento", carreirasSugeridos.get("carreiras"));
					commons.insereEvento(evento);
				};
				mongo.close();
				return Response.status(200).build();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			};
			return null;
	};
	@SuppressWarnings({ "rawtypes" })
	private BasicDBObject getUserPerfil(String id){
		Commons_DB commons_db = new Commons_DB();
		Response response = commons_db.getCollection(id, "usuarios", "_id");
		BasicDBObject doc = new BasicDBObject();
		BasicDBObject jsonDocumento = new BasicDBObject();
		if (!(response.getEntity() instanceof Boolean)){
			doc.putAll((Map) response.getEntity());
			if (doc != null){
				BasicDBObject objDoc = (BasicDBObject) doc.get("documento");
				jsonDocumento.put("usuario", objDoc);
				String email = objDoc.get("email").toString();
				Response responsePerfil = commons_db.getCollection(email, "userPerfil", "documento.usuario");
				BasicDBObject docPerfil = new BasicDBObject();
				BasicDBObject objDocPerfil = new BasicDBObject();
				if (!(responsePerfil.getEntity() instanceof Boolean)){
					docPerfil.putAll((Map) responsePerfil.getEntity());
					if (docPerfil != null){
						objDocPerfil = (BasicDBObject) docPerfil.get("documento");
					};
				};
				jsonDocumento.put("userPerfil", objDocPerfil);
			};
		};
		return jsonDocumento;
		
	};
	
};
