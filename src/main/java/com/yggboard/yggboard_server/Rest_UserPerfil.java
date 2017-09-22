package com.yggboard.yggboard_server;

import java.util.ArrayList;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

	
@Singleton
// @Lock(LockType.READ)
@Path("/userPerfil")

public class Rest_UserPerfil {
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();

	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterUsuario(@QueryParam("usuario") String usuario, @QueryParam("usuarioConsultaId") String usuarioConsultaId){
		Commons_DB commons_db = new Commons_DB();
		return commons_db.getCollection(usuario, "userPerfil", "documento.token");
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/obter/itens")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCarreiras(@QueryParam("usuario") String usuario, @QueryParam("usuarioConsultaId") String usuarioConsultaId, @QueryParam("userPerfilConsultaId") String userPerfilConsultaId, @QueryParam("item") String item, @QueryParam("elemento") String elemento){
		
		System.out.println("chamada userperfil:" + item);
		if (item == null ){
			return null;
		};
		BasicDBObject cursor = new BasicDBObject();
		
		if (userPerfilConsultaId != null) {
			cursor = commons_db.getCollection(userPerfilConsultaId, "userPerfil", "_id");
		}else {
			if (usuarioConsultaId != null) {
				cursor = obterUserPerfil(userPerfilConsultaId);
			}else {
  			cursor = commons_db.getCollection(usuario, "userPerfil", "documento.token");
  		};
		};
		
		if (cursor != null){
			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
			JSONArray documentos = new JSONArray();
			BasicDBObject jsonPerfil = new BasicDBObject();
			jsonPerfil.putAll((Map) obj);
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
			    	JSONArray arrayListHabilidadesFaltantes = new JSONArray();
			    	JSONArray arrayListHabilidadesPossui = new JSONArray();
			    	JSONArray arrayListHabilidadesObjetivos = new JSONArray();
			    	JSONArray arrayListHabilidadesObjetivosReal = new JSONArray();
					for (int i = 0; i < arrayList.size(); i++) {
						BasicDBObject cursorCarreiras = commons_db.getCollection((String) arrayList.get(i), "objetivos", "documento.id");
						if (cursorCarreiras != null){
							BasicDBObject objCarreiras = new BasicDBObject();
							objCarreiras.put("documento", cursorCarreiras.get("documento"));
							objCarreiras.put("_id", cursorCarreiras.get("_id").toString());
							documentos.add(montaCarreira(objCarreiras, jsonPerfil, arrayListHabilidadesFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
						};
					};
				};
			};
			if (item.equals("carreiras-elemento")){
				BasicDBObject cursorCarreiras = commons_db.getCollection(usuario, "objetivos", "documento.id");
				if (cursorCarreiras != null){
					BasicDBObject objCarreiras = new BasicDBObject();
					objCarreiras.put("documento", cursorCarreiras.get("documento"));
					objCarreiras.put("_id", cursorCarreiras.get("_id").toString());
			    	JSONArray arrayListHabilidadesFaltantes = new JSONArray();			    			
			    	JSONArray arrayListHabilidadesPossui = new JSONArray();			    			
			    	JSONArray arrayListHabilidadesObjetivos = new JSONArray();
			    	JSONArray arrayListHabilidadesObjetivosReal = new JSONArray();
					documentos.add(montaCarreira(objCarreiras, jsonPerfil, arrayListHabilidadesPossui, arrayListHabilidadesFaltantes, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
				};
			};
			if (item.equals("badges") | item.equals("show-badges")){
				if (item.equals("badges")){
					carregaBadges((ArrayList) jsonPerfil.get("badges"), usuario, jsonPerfil, documentos, false);
					carregaBadges((ArrayList) jsonPerfil.get("badgesConquista"), usuario, jsonPerfil, documentos, false);
				}else{
					carregaBadges((ArrayList) jsonPerfil.get("showBadges"), usuario, jsonPerfil, documentos, false);
				};
				JSONArray cursorBadges = commons_db.getCollectionListaNoKey("badges");	
/*				if (cursorBadges != null){
					for (int i = 0; i < cursorBadges.size(); i++) {
						BasicDBObject objBadges = new BasicDBObject();
						objBadges.putAll((Map) cursorBadges.get(i));
						JSONObject jsonBadge = new JSONObject();
						jsonBadge.putAll((Map) objBadges.get("documento"));
						if (jsonBadge.get("tipo") !=null  && jsonBadge.get("id").toString() != null){
							if (jsonPerfil.get("badgesConquista") == null){
								JSONArray objBadgeConquista = new JSONArray();
								jsonPerfil.put("badgesConquista", objBadgeConquista);
							};
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
					};
				};*/
			};
			if (item.equals("habilidades") | item.equals("habilidades-interesse") |
				item.equals("cursos-necessarias-habilidades") | item.equals("cursos-interesse-habilidades")){
				ArrayList arrayList = new ArrayList(); 
				arrayList = (ArrayList) jsonPerfil.get("carreiras");
				ArrayList arrayListObjetivos = new ArrayList(); 
		    	JSONArray arrayListHabilidadesFaltantes = new JSONArray(); 
		    	JSONArray arrayListHabilidadesPossui = new JSONArray(); 
		    	JSONArray arrayListHabilidadesObjetivos = new JSONArray(); 
		    	JSONArray arrayListHabilidadesObjetivosReal = new JSONArray(); 
				if (arrayList != null){
			    	Object array[] = arrayList.toArray(); 
			    	for (int i = 0; i < array.length; i++) {
			    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), usuario, jsonPerfil, arrayListHabilidadesFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
					};
				};
				arrayList = (ArrayList) jsonPerfil.get("carreirasInteresse");
				if (arrayList != null){
			    	Object array[] = arrayList.toArray();
			    	for (int i = 0; i < array.length; i++) {
			    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), usuario, jsonPerfil, arrayListHabilidadesFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
					};
				};
				if (item.equals("habilidades")){
					arrayList = (ArrayList) jsonPerfil.get("habilidades");
				}else{
					arrayList = (ArrayList) jsonPerfil.get("habilidadesInteresse");
				}
				if (arrayList != null){
			    	Object array[] = arrayList.toArray(); 
					int w = 0;
					while (w < array.length) {
						BasicDBObject habilidade = commons_db.getCollection(array[w].toString(), "habilidades", "documento.id");
						if (habilidade != null){
							BasicDBObject habilidadeDoc = (BasicDBObject) habilidade.get("documento");
							if (item.equals("cursos-necessarias-habilidades") | item.equals("cursos-interesse-habilidades")){
								ObterCursosNecessarios (habilidadeDoc, documentos, jsonPerfil);
							}else{
								JSONObject jsonDocumento = new JSONObject();
								habilidadeDoc.put("interesse", commons.testaElementoArray(habilidadeDoc.get("id").toString(), (ArrayList<String>) jsonPerfil.get("habilidadesInteresse")));
								habilidadeDoc.put("possui", commons.testaElementoArray(habilidadeDoc.get("id").toString(), (ArrayList<String>) jsonPerfil.get("habilidades")));
							  jsonDocumento.put("documento", habilidadeDoc);
								JSONArray cursos = new JSONArray();
								ObterCursosNecessarios (habilidadeDoc, cursos, jsonPerfil);
								jsonDocumento.put("cursos", cursos);
								jsonDocumento.put("habilidadesGeral", commons.totalArray(jsonPerfil.get("habilidades")));
								jsonDocumento.put("habilidadesInteresseGeral", commons.totalArray(jsonPerfil.get("habilidadesInteresse")));
								jsonDocumento.put("habilidadesGeralPossui", commons.totalArray(arrayListHabilidadesPossui));
								jsonDocumento.put("habilidadesGeralFaltantes", commons.totalArray(arrayListHabilidadesFaltantes));
								jsonDocumento.put("habilidadesGeralObjetivos", commons.totalArray(arrayListHabilidadesObjetivos));
								jsonDocumento.put("habilidadesGeralObjetivosReal", commons.totalArray(arrayListHabilidadesObjetivosReal));
								documentos.add(jsonDocumento);
							};
						};
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
						BasicDBObject cursorHabilidades = commons_db.getCollection(array[w].toString(), "habilidades", "documento.id");
						if (cursorHabilidades != null){
							BasicDBObject objHabilidades = (BasicDBObject) cursorHabilidades.get("documento");
							JSONObject jsonDocumento = new JSONObject();
						    jsonDocumento.put("documento", objHabilidades);
							documentos.add(jsonDocumento);
						};
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
						String key = "";
						JSONObject jsonDocumento = new JSONObject();
						if (item.equals("cursos-inscrito")){
							List arrayInscritos = (List) jsonPerfil.get("cursosInscrito");
							BasicDBObject cursoInscrito = new BasicDBObject();
							cursoInscrito.putAll((Map) arrayInscritos.get(w));
							key = cursoInscrito.get("id").toString();
							jsonDocumento.put("inscricao", cursoInscrito.get("inscricao").toString());
							jsonDocumento.put("todaysDate", commons.todaysDate("inv_month_number"));
						}else{
							key = array[w].toString();
						};
						BasicDBObject cursorCursos = commons_db.getCollection(key, "cursos", "documento.id");
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
								JSONObject dadosUsuario = new JSONObject();
								JSONObject userResult = getUserPerfil(arraySeguindo[z].toString());
								dadosUsuario.put("usuario", userResult.get("usuario"));
								dadosUsuario.put("totalHabilidades", userResult.get("totalHabilidades"));
								dadosUsuario.put("totalPossuiHabilidades", userResult.get("totalPossuiHabilidades"));
								dadosUsuario.put("totalHabilidadesFaltantes", userResult.get("totalHabilidadesFaltantes"));
								dadosUsuario.put("totalHabilidadesObjetivos", userResult.get("totalHabilidadesObjetivos"));
								dadosUsuario.put("totalHabilidadesObjetivosReal", userResult.get("totalHabilidadesObjetivosReal"));
								documentos.add(dadosUsuario);									
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
				JSONObject jsonDocumento = new JSONObject();
				ArrayList arrayListObjetivos = new ArrayList(); 
		    	JSONArray arrayListHabilidadesFaltantes = new JSONArray(); 
		    	JSONArray arrayListHabilidadesPossui = new JSONArray(); 
		    	JSONArray arrayListHabilidadesObjetivos = new JSONArray(); 
		    	JSONArray arrayListHabilidadesObjetivosReal = new JSONArray(); 
				if (arrayList != null){
			    	Object array[] = arrayList.toArray(); 
			    	for (int i = 0; i < array.length; i++) {
			    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), usuario, jsonPerfil, arrayListHabilidadesFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
					};
				};
				arrayList = (ArrayList) jsonPerfil.get("carreirasInteresse");
				if (arrayList != null){
			    	Object array[] = arrayList.toArray();
			    	for (int i = 0; i < array.length; i++) {
			    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), usuario, jsonPerfil, arrayListHabilidadesFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
					};
				};
				jsonDocumento.put("objetivos", arrayListObjetivos);
				jsonDocumento.put("habilidadesGeral", commons.totalArray(jsonPerfil.get("habilidades")));
				jsonDocumento.put("habilidadesInteresseGeral", commons.totalArray(jsonPerfil.get("habilidadesInteresse")));
				jsonDocumento.put("habilidadesGeralPossui", commons.totalArray(arrayListHabilidadesPossui));
				jsonDocumento.put("habilidadesGeralFaltantes", commons.totalArray(arrayListHabilidadesFaltantes));
				jsonDocumento.put("habilidadesGeralObjetivos", commons.totalArray(arrayListHabilidadesObjetivos));
				jsonDocumento.put("habilidadesGeralObjetivosReal", commons.totalArray(arrayListHabilidadesObjetivosReal));
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
			return documentos;
		};
		return null;
	};
	@SuppressWarnings("rawtypes")
	private BasicDBObject obterUserPerfil(String userPerfilConsultaId) {
		
		BasicDBObject usuario = commons_db.getCollection(userPerfilConsultaId, "usuarios", "_id");
		if (usuario != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuario.get("documento"));
			if (usuarioDoc != null) {
				return commons_db.getCollection(usuarioDoc.get("email").toString(), "userPerfil", "documento.usuario");
			}
		};
		return null;
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject carregaObjetivos(String id, String usuario, BasicDBObject objDocPerfil, JSONArray arrayListElementosFaltantes, JSONArray arrayListHabilidadesPossui, JSONArray arrayListHabilidadesObjetivos, JSONArray arrayListHabilidadesObjetivosReal) {
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		BasicDBObject doc  = commons_db.getCollection(id, "objetivos", "documento.id");
		BasicDBObject objObjetivo = new BasicDBObject();
		if (doc != null){
			objObjetivo.putAll((Map) doc.get("documento"));
	    	JSONArray arrayListHabilidadesFaltantesObjetivo = new JSONArray();
	    	JSONArray arrayListHabilidadesPossuiObjetivo = new JSONArray();
	    	JSONArray arrayListHabilidadesObjetivosObjetivo = new JSONArray();
	    	JSONArray arrayListHabilidadesObjetivosRealObjetivo = new JSONArray();
			BasicDBObject objetivo = montaCarreira(doc, objDocPerfil, arrayListHabilidadesFaltantesObjetivo, arrayListHabilidadesPossuiObjetivo, arrayListHabilidadesObjetivosObjetivo, arrayListHabilidadesObjetivosRealObjetivo);
			objObjetivo.put("totalHabilidades", objetivo.get("totalHabilidades"));
			objObjetivo.put("totalPossuiHabilidades", objetivo.get("totalPossuiHabilidades"));
			objObjetivo.put("totalHabilidadesFaltantes", objetivo.get("totalHabilidadesFaltantes"));
			objObjetivo.put("totalHabilidadesObjetivos", objetivo.get("totalHabilidadesObjetivos"));
			objObjetivo.put("totalHabilidadesObjetivosReal", objetivo.get("totalHabilidadesObjetivosReal"));
	     	ArrayList arrayListElementos = new ArrayList(); 
			arrayListElementos = (ArrayList) objDocPerfil.get("habilidades");
			if (arrayListElementos != null){
		    	Object[] arrayElementos = arrayListElementos.toArray(); 
		    	ObterHabilidadesFaltantes(doc, arrayElementos, arrayListElementosFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal);
		    	objObjetivo.put("interesse", commons.testaElementoArray(objObjetivo.get("id").toString(), (ArrayList<String>) objDocPerfil.get("carreirasInteresse")));
		    	objObjetivo.put("possui", commons.testaElementoArray(objObjetivo.get("id").toString(), (ArrayList<String>) objDocPerfil.get("carreiras")));
		    	objObjetivo.put("necessariosPerfil", commons.montaArrayPerfil(objDocPerfil.get("habilidades"), objObjetivo.get("necessarios")));
			};
		};
		return objObjetivo;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void carregaBadges(ArrayList arrayList, String usuario, BasicDBObject jsonPerfil, JSONArray documentos, boolean atualizaPerfil) {
		Commons_DB commons_db = new Commons_DB();
		if (arrayList != null){
	    	Object array[] = arrayList.toArray(); 
			int w = 0;
			while (w < array.length) {
				BasicDBObject doc = commons_db.getCollection(array[w].toString(), "badges", "documento.id");
				if (doc != null){
					JSONObject objBadges = new JSONObject();
					objBadges.putAll((Map) doc.get("documento"));
					incluirBadge(objBadges, usuario, jsonPerfil, documentos, atualizaPerfil);
				};
				++w;
			};
		};
	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void incluirBadge(JSONObject jsonBadge, String usuario, BasicDBObject jsonPerfil, JSONArray documentos, Boolean atualizaPerfil) {

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
				BasicDBObject habilidade = commons_db.getCollection(arrayListElementosFaltantes.get(z), "habilidades", "documento.id");
				if (habilidade != null){
					BasicDBObject habilidadeDoc = (BasicDBObject) habilidade.get("documento");
					JSONObject jsonHabilidades = new JSONObject();
					jsonHabilidades.put("id", arrayListElementosFaltantes.get(z));
					jsonHabilidades.put("nome", habilidadeDoc.get("nome"));
					JSONArray cursos = new JSONArray();
					ObterCursosNecessarios (habilidadeDoc, cursos, jsonPerfil);
					jsonHabilidades.put("cursos", cursos);
					habilidadesArray.add (jsonHabilidades);
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
			AtualizarPerfil(newPerfil);
		};
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject montaCarreira(BasicDBObject objCarreirasSource, BasicDBObject jsonPerfil, JSONArray arrayListHabilidadesFaltantes, JSONArray arrayListHabilidadesPossui, JSONArray arrayListHabilidadesObjetivos, JSONArray arrayListHabilidadesObjetivosReal) {

		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
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
		    ObterHabilidadesFaltantes(objCarreirasSource, arrayElementos, arrayListElementosFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal);
		    jsonDocumento.put("totalHabilidades", commons.totalArray(arrayListHabilidadesObjetivosReal));
		    jsonDocumento.put("totalPossuiHabilidades", commons.totalArray(arrayListHabilidadesPossui));
		    jsonDocumento.put("totalHabilidadesFaltantes", commons.totalArray(arrayListElementosFaltantes));
		    jsonDocumento.put("totalHabilidadesObjetivos", commons.totalArray(arrayListHabilidadesObjetivos));
		    jsonDocumento.put("totalHabilidadesObjetivosReal", commons.totalArray(arrayListHabilidadesObjetivosReal));
		    int z = 0;
		    JSONArray necessariosArray = new JSONArray();
		    while (z < arrayListElementosFaltantes.size()) {
		    	BasicDBObject habilidades = commons_db.getCollection(arrayListElementosFaltantes.get(z).toString(), "habilidades", "documento.id");
		    	if (habilidades != null){
  					BasicDBObject habilidadeDoc = (BasicDBObject) habilidades.get("documento");
  					JSONObject jsonNecessarios = new JSONObject();
  					jsonNecessarios.put("id", arrayListElementosFaltantes.get(z));
  					jsonNecessarios.put("nome", habilidadeDoc.get("nome"));
  					jsonNecessarios.put("descricao", habilidadeDoc.get("descricao"));
  					jsonNecessarios.put("wiki", habilidadeDoc.get("wiki"));
  					jsonNecessarios.put("amazon", habilidadeDoc.get("amazon"));
  					jsonNecessarios.put("preRequisitos", habilidadeDoc.get("preRequisitos"));
  					jsonNecessarios.put("preRequisitosNome", habilidadeDoc.get("preRequisitosNome"));
  					JSONArray cursos = new JSONArray();
  					ObterCursosNecessarios (habilidadeDoc, cursos, jsonPerfil);
  					jsonNecessarios.put("cursos", cursos);
  					necessariosArray.add (jsonNecessarios);
  				};
  				++z;
  			};
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
	public Response AtualizarPerfil(JSONObject newPerfil){
		System.out.println("atualiza perfil:" + newPerfil.get("tipo") + "- inout:"  + newPerfil.get("inout") + " - id:"  + newPerfil.get("id"));
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		BasicDBObject doc = commons_db.getCollection(newPerfil.get("usuario").toString(), "userPerfil", "documento.token");
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
			if (newPerfil.get("assunto") != null){
				assunto = newPerfil.get("assunto").toString();
			};
			String elemento = "";
			if (newPerfil.get("id") != null){
				elemento = newPerfil.get("id").toString();
			}else{
				origemErro = true;
			};
			String inout = "in";
			if (newPerfil.get("inout") != null){
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
			ArrayList<String> array = new JSONArray();
			if (objUserPerfil.get(tipo) != null){
				array = (ArrayList<String>) objUserPerfil.get(tipo);
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
							ArrayList habilidadesUpdate = AtualizaUserPerfilArray(elemento, objUserPerfil.get("habilidadesInteresse"), "cursos", "habilidades");
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
			key.put("key", "documento.token");
			key.put("value", newPerfil.get("usuario").toString());
			keysArray.add(key);

			JSONObject field = new JSONObject();
			field.put("field", tipo);
			field.put("value", array);
			fieldsArray.add(field);
							
			Response atualizacao = commons_db.atualizarCrud("userPerfil", fieldsArray, keysArray, null);
			
			if (atualizacao.getStatus() == 200){
				BasicDBObject evento = new BasicDBObject();
				evento.put("idUsuario", newPerfil.get("usuario").toString());
				evento.put("evento", "userPerfil");
				evento.put("idEvento", newPerfil.get("usuario").toString());
				evento.put("motivo", newPerfil.get("inout").toString());
				evento.put("elemento", tipo);
				evento.put("idElemento", newPerfil.get("id").toString());
				atualizacao = commons.insereEvento(evento);
				return atualizacao;
			};

			return Response.status(200).entity("ok").build();	
		};
		return Response.status(401).entity("invalid token").build();	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList AtualizaUserPerfilArray(String elemento, Object objHabilidades, String collection, String arrayCollection) {
		
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		ArrayList<String> arrayUpdate = (ArrayList<String>) objHabilidades;

		BasicDBObject doc = commons_db.getCollection(elemento, collection, "documento.id");
		if (doc != null){
			BasicDBObject objDoc = (BasicDBObject) doc.get("documento");
			ArrayList<String> array = (ArrayList<String>) objDoc.get(arrayCollection);
			if (array != null){
				for (int i = 0; i < array.size(); i++) {
					if (!commons.testaElementoArray(array.get(i).toString(), arrayUpdate)){
						arrayUpdate.add(array.get(i).toString());
					};
				};
			};
		};
				
		return arrayUpdate;
	};
	
	private void atualizaDependencia(String elemento, ArrayList<String> array) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();	
		BasicDBObject cursor = commons_db.getCollection(elemento, "habilidades", "documento.id");	
		if (cursor != null){
			BasicDBObject objHabilidade = (BasicDBObject) cursor.get("documento");
			@SuppressWarnings("unchecked")
			List<String> arrayPreRequisitos = (List<String>) objHabilidade.get("preRequisitos");
			for (int i = 0; i < arrayPreRequisitos.size(); i++) {
				commons.addStringArrayList(array, commons.preRequisito(arrayPreRequisitos.get(i).toString()));
			};
		};
	};
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterUsersPerfil(@QueryParam("usuario") String usuario) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
		if ((commons_db.getCollection(usuario, "userPerfil", "documento.token")) == null){
			return null;
		};
		JSONArray cursor = commons_db.getCollectionListaNoKey("userPerfil");
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject objUserPerfil = new BasicDBObject();
				objUserPerfil.putAll((Map) cursor.get(i));
				BasicDBObject documento = new BasicDBObject();
				documento.putAll((Map) objUserPerfil.get("documento"));
				JSONObject jsonDocumento = new JSONObject();
			    jsonDocumento.put("documento", documento);
				documentos.add(jsonDocumento);
			};
			return documentos;
		};
		return null;
	};

	@SuppressWarnings("unchecked")
	private ArrayList<String> ObterHabilidadesCursosNecessarias(Object carreira, Object[] arrayElementos, JSONArray documentos, Boolean obterCursos) {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(carreira.toString(), "objetivos", "documento.id");
		if (cursor != null){
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
					BasicDBObject habilidade = commons_db.getCollection(arrayHabilidades[w].toString(), "habilidades", "documento.id");
					if (habilidade != null){
						BasicDBObject habilidadeDoc = (BasicDBObject) habilidade.get("documento");
						if (obterCursos){
							ObterCursosNecessarios (habilidadeDoc, documentos, null);
						}else{
							JSONObject jsonDocumento = new JSONObject();
						  jsonDocumento.put("documento", habilidadeDoc);
							documentos.add(jsonDocumento);
						}
					};
				};
				++w;
			};
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> ObterHabilidadesCursosNecessariasBadge(Object badge, Object[] arrayElementos, JSONArray documentos, Boolean obterCursos, BasicDBObject jsonPerfil) {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(badge.toString(), "badges", "documento.id");
		if (cursor != null) {
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
					BasicDBObject habilidade = commons_db.getCollection(arrayHabilidades[w].toString(), "habilidades", "documento.id");
					if (habilidade != null) {
						BasicDBObject habilidadeDoc = (BasicDBObject) habilidade.get("documento");
						if (obterCursos){
							ObterCursosNecessarios (habilidadeDoc, documentos, jsonPerfil);
						}else{
							JSONObject jsonDocumento = new JSONObject();
						  jsonDocumento.put("documento", habilidadeDoc);
							documentos.add(jsonDocumento);
						};
					};
				};
				++w;
			};
		};
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject ObterHabilidadesFaltantes (BasicDBObject carreira, Object[] arrayElementos, JSONArray arrayListElementosFaltantes, JSONArray arrayListHabilidadesPossui, JSONArray arrayListHabilidadesObjetivos, JSONArray arrayListHabilidadesObjetivosReal) {
		Commons commons = new Commons();
		BasicDBObject objCarreira = (BasicDBObject) carreira.get("documento");
		ArrayList<String> arrayListHabilidades = new ArrayList<String>(); 
		arrayListHabilidades = (ArrayList<String>) objCarreira.get("preRequisitosGeral");
    	Object arrayHabilidades[] = arrayListHabilidades.toArray();
		int w = 0;
		while (w < arrayHabilidades.length) {
			String preRequisitos = arrayHabilidades[w].toString().split(":")[0].toString().replace("|", "&");
			String [] array = preRequisitos.split("&");
			int i = 0;
			Boolean temHabilidade = false;
			while (i < array.length) {
				if (!(commons.testaElementoArray(arrayHabilidades[w].toString(), arrayListHabilidadesObjetivosReal))){
					if (!(commons.testaElementoArray(array[i], arrayListHabilidadesObjetivos))){
						arrayListHabilidadesObjetivosReal.add(arrayHabilidades[w]);
					};
				};
				if (!(commons.testaElementoArray(array[i], arrayListHabilidadesObjetivos))){
					arrayListHabilidadesObjetivos.add(array[i]);
				};
				int z = 0;
				while (z < arrayElementos.length) {
					if (array[i].equals(arrayElementos[z])){
						if (!temHabilidade){
							if (!(commons.testaElementoArray(array[i], arrayListHabilidadesPossui ))){
								arrayListHabilidadesPossui.add(array[i]);
							};
						};
						temHabilidade = true;
					};
					++z;
				};
				++i;
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
		return jsonQtdeHabilidades;
	};
	
	@SuppressWarnings("unchecked")
	private JSONObject ObterTotalHabilidadesBadges (Object id, Object[] arrayElementos, ArrayList<String> arrayListElementosFaltantes) {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(id.toString(), "badges", "documento.id");
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
			return jsonQtdeHabilidades;
		}else{
			JSONObject jsonQtdeHabilidades = new JSONObject();
			jsonQtdeHabilidades.put("totalHabilidades", 0);
			jsonQtdeHabilidades.put("totalPossuiHabilidades", 0);
			return jsonQtdeHabilidades;
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<String> ObterCursosNecessarios (BasicDBObject habilidadeDoc, JSONArray documentos, BasicDBObject jsonPerfil) {
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
		ArrayList<Object> cursos = (ArrayList<Object>) habilidadeDoc.get("cursos");
		
		if (cursos != null){
			for (int i = 0; i < cursos.size(); i++) {
				BasicDBObject curso = commons_db.getCollection(cursos.get(i).toString(), "cursos", "documento.id");
				if (curso != null) {
					BasicDBObject cursoDoc = new BasicDBObject();
					cursoDoc.putAll((Map) curso.get("documento"));
  				Boolean existeCurso = false;
  				if (commons.testaElementoArrayObject(cursoDoc, documentos)) {
  					existeCurso = true;
  				};
  				if (!existeCurso){
  					List arrayParent = (List) cursoDoc.get("parents");
  					if (arrayParent.size() == 0){
  						if (jsonPerfil.get("cursosInteresse") != null){
  							cursoDoc.put("interesse", commons.testaElementoArray(cursoDoc.get("id").toString(), (ArrayList<String>) jsonPerfil.get("cursosInteresse")));
  						};
  						if (jsonPerfil.get("cursos") != null){
  							cursoDoc.put("possui", commons.testaElementoArray(cursoDoc.get("id").toString(), (ArrayList<String>) jsonPerfil.get("cursos")));
  						};
  						if (cursoDoc.get("habilidades") != null){
  							cursoDoc.put("habilidadesPerfil", commons.montaArrayPerfil(jsonPerfil.get("habilidades"), cursoDoc.get("habilidades")));
  						};
  						documentos.add(cursoDoc);
  					};
  				};
				};
			};
			return documentos;
		}
		return null;
	};
	
	@Path("/cursosSugeridos")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CursosSugeridos(JSONObject inputCursosSugeridos)  {
				
		return AtualizaSugestaoColetiva (inputCursosSugeridos, "cursosSugeridos");
		
	};
	@Path("/carreirasSugeridas")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CarreirasSugeridos(JSONObject inputCarreirasSugeridas)  {
		
		return AtualizaSugestaoColetiva (inputCarreirasSugeridas, "carreirasSugeridas");
		
	};	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Response AtualizaSugestaoColetiva(JSONObject inputCursosSugeridos, String nameArray) {
		
		Commons commons = new Commons();
		Commons_DB commons_db = new Commons_DB();
		List arrayCursosSugestao = (List) inputCursosSugeridos.get(nameArray);
		for (int i = 0; i < arrayCursosSugestao.size(); i++) {
			JSONObject cursosSugestao = new JSONObject();
			cursosSugestao.putAll((Map) arrayCursosSugestao.get(i));
			String usuario = cursosSugestao.get("usuario").toString();
			BasicDBObject cursor = commons_db.getCollection(usuario, "userPerfil", "documento.usuario");
			if (cursor != null){
				BasicDBObject objUserPerfilUpdate = (BasicDBObject) cursor.get("documento");
				objUserPerfilUpdate.remove(nameArray);
				objUserPerfilUpdate.put(nameArray, cursosSugestao.get("cursos"));
				BasicDBObject objUserPerfilDocumento = new BasicDBObject();
				objUserPerfilDocumento.put("documento", objUserPerfilUpdate);
				Response atualizacao = commons_db.atualizaDocumento(objUserPerfilDocumento,  "cursos", "documento.token", usuario.toString());
				if (atualizacao.getStatus() == 200) {
					// incluir evento
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", usuario);
					evento.put("evento", "userPerfil");
					evento.put("idEvento", nameArray);
					evento.put("motivo", "inclusao");
					evento.put("elemento", nameArray);
					evento.put("idElemento", cursosSugestao.get("cursos"));
					commons.insereEvento(evento);
				};
			};
		};
		return Response.status(200).build();		
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONObject getUserPerfil(String id){
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		BasicDBObject doc = commons_db.getCollection(id, "usuarios", "_id");
		JSONObject jsonDocumento = new JSONObject();
    	JSONArray arrayListHabilidades = new JSONArray(); 
    	JSONArray arrayListHabilidadesFaltantes = new JSONArray(); 
		JSONArray arrayListHabilidadesPossui = new JSONArray();
    	JSONArray arrayListHabilidadesObjetivos = new JSONArray(); 
    	JSONArray arrayListHabilidadesObjetivosReal = new JSONArray(); 
		if (doc != null){
			BasicDBObject objDoc = (BasicDBObject) doc.get("documento");
			objDoc.put("_id", id);
			objDoc.remove("token");
			objDoc.remove("password");
			jsonDocumento.put("usuario", objDoc);
			String email = objDoc.get("email").toString();
			BasicDBObject objDocPerfil = new BasicDBObject();
			BasicDBObject docPerfil = commons_db.getCollection(email, "userPerfil", "documento.usuario");
			if (docPerfil != null){
				objDocPerfil = (BasicDBObject) docPerfil.get("documento");
				jsonDocumento.put("userPerfil", objDocPerfil);
				ArrayList arrayList = (ArrayList) objDocPerfil.get("habilidades");
				if (arrayList != null){
					arrayListHabilidades.addAll(arrayList);
				};
		    	arrayList = (ArrayList) objDocPerfil.get("carreirasInteresse");
				if (arrayList != null){
					ArrayList arrayListObjetivos = new ArrayList(); 
			    	Object array[] = arrayList.toArray(); 
			    	for (int i = 0; i < array.length; i++) {
			    		arrayListObjetivos.add(carregaObjetivos(array[i].toString(), email, objDocPerfil, arrayListHabilidadesFaltantes, arrayListHabilidadesPossui, arrayListHabilidadesObjetivos, arrayListHabilidadesObjetivosReal));
					};
				};
			};
		};
		jsonDocumento.put("totalHabilidades", commons.totalArray(arrayListHabilidades));
		jsonDocumento.put("totalPossuiHabilidades", commons.totalArray(arrayListHabilidadesPossui));
		jsonDocumento.put("totalHabilidadesFaltantes", commons.totalArray(arrayListHabilidadesFaltantes));
		jsonDocumento.put("totalHabilidadesObjetivos", commons.totalArray(arrayListHabilidadesObjetivos));
		jsonDocumento.put("totalHabilidadesObjetivosReal", commons.totalArray(arrayListHabilidadesObjetivosReal));
		return jsonDocumento;
		
	};
	
};
