package com.yggboard;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import java.util.ArrayList;

public class Actions {
	
	UserPerfil userPerfil = new UserPerfil();

	@SuppressWarnings("unchecked")
	public BasicDBObject processaAction(String action, String objeto, String usuarioPar, String ids, Boolean atualizacaoDireta, MongoClient mongo) {
	
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
		return result;
	};

}
