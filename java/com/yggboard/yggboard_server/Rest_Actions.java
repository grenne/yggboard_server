package com.yggboard.yggboard_server;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/actions")

public class Rest_Actions {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	UserPerfil userPerfil = new UserPerfil();
	Objetivo objetivo = new Objetivo();
	Habilidade habilidade = new Habilidade();
	Curso curso = new Curso();
	AreaAtuacao areaAtuacao = new AreaAtuacao();
	AreaConhecimento areaConhecimento = new AreaConhecimento();
	Badge badge = new Badge();

	@SuppressWarnings({ "unchecked" })
	@Path("/set")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject obter(@QueryParam("token") String token, 
													@QueryParam("action") String action, 
													@QueryParam("objeto") String objeto ,
													@QueryParam("usuario") String usuarioPar,
													@QueryParam("id") String ids,
													@QueryParam("atualizacaoDireta") Boolean atualizacaoDireta)
	{
		
	System.out.println("actions");
	if (token == null) {
		System.out.println("token não informado");
		mongo.close();
		return null;
	};
	if (action == null) {
		System.out.println("action não informada");
		mongo.close();
		return null;
	};
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	if (usuarioPar == null ) {
		System.out.println("usuario não informado");
		mongo.close();
		return null;
	};
	
	if (ids == null ) {
		System.out.println("id não informado");
		mongo.close();
		return null;
	};
	
	if (atualizacaoDireta == null ) {
		atualizacaoDireta = false;
	};
	
	BasicDBObject result = new BasicDBObject();

	String[] arrayIds = ids.split(";");

	for (int i = 0; i < arrayIds.length; i++) {
		String id = arrayIds[i].toString();
		switch (objeto) {
			case "habilidade":
				switch (action) {
				case "possui":
					if (atualizacaoDireta) {
						result.put("resultado", (userPerfil.setAction(usuarioPar, "habilidades", id, mongo).toString()));
					}else {
						ArrayList<BasicDBObject> preRequisitosFaltantes = userPerfil.preRequsitosFaltantes(usuarioPar, id, mongo);
						if (preRequisitosFaltantes.size() == 0) {
							result.put("resultado", (userPerfil.setAction(usuarioPar, "habilidades", id, mongo).toString()));	
						}else {
							result.put("resultado", "missing");
							result.put("preRequisitos", preRequisitosFaltantes);
						};
					};
					break;
				case "interesse":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "habilidadesInteresse", id, mongo).toString()));							
					break;
				case "conquista":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "habilidadesConquista", id, mongo).toString()));							
					break;
				case "sugeridas":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "habilidadesSugeridas", id, mongo).toString()));							
					break;
				default:
					System.out.println("Action invalido:" + action);
					break;
				}
				break;
			case "objetivo":
				switch (action) {
				case "possui":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "carreiras", id, mongo).toString()));							
					break;
				case "interesse":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "carreirasInteresse", id, mongo).toString()));							
					break;
				case "conquista":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "carreirasConquistas", id, mongo).toString()));							
					break;
				case "sugeridos":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "carreirasSugeridos", id, mongo).toString()));							
					break;
				default:
					System.out.println("Action invalido:" + action);
					break;
				}
				break;
			case "curso":
				switch (action) {
				case "possui":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "cursos", id, mongo).toString()));							
					break;
				case "interesse":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "cursosInteresse", id, mongo).toString()));							
					break;
				case "conquista":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "cursosConquistas", id, mongo).toString()));							
					break;
				case "sugeridos":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "cursosSugeridos", id, mongo).toString()));							
					break;
				case "andamento":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "cursosAndamento", id, mongo).toString()));							
					break;
				default:
					System.out.println("Action invalido:" + action);
					break;
				}
				break;
			case "badge":
				switch (action) {
				case "possui":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "badges", id, mongo).toString()));							
					break;
				case "interesse":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "badgesInteresse", id, mongo).toString()));							
					break;
				case "conquista":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "badgesConquistas", id, mongo).toString()));							
					break;
				case "show":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "showBadges", id, mongo).toString()));							
					break;
				default:
					System.out.println("Action invalido:" + action);
				}
				break;
			case "areaAtuacao":
				switch (action) {
				case "possui":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "areasAtuacao", id, mongo).toString()));							
					break;
				case "interesse":
					result.put("resultado", (userPerfil.setAction(usuarioPar, "areasAtuacaoInteresse", id, mongo).toString()));							
					break;
				default:
					System.out.println("Action invalido:" + action);
				}
				break;
			default:
				System.out.println("Objeto invalido:" + objeto);
				break;
			};
	};
	mongo.close();
	return result;
	};
	
};
