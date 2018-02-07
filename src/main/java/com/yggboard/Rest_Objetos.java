package com.yggboard;


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
@Path("/objetos")

public class Rest_Objetos {
	
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

	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject obter(@QueryParam("token") String token, 
													@QueryParam("itens") String itens, 
													@QueryParam("start") int start ,
													@QueryParam("limite") int limite,
													@QueryParam("full") String full,
													@QueryParam("assuntos") String assuntos,
													@QueryParam("usuarioParametro") String usuarioParametro,
													@QueryParam("usuario") String usuarioPar,
													@QueryParam("objetivo") String objetivoPar,
													@QueryParam("habilidade") String habilidadePar,
													@QueryParam("curso") String cursoPar,
													@QueryParam("areaAtuacao") String areaAtuacaoPar,
													@QueryParam("areaConhecimento") String areaConhecimentoPar,
													@QueryParam("badge") String badgePar
													)  {
		
	System.out.println("lista objetos");
	if (token == null) {
		System.out.println("token não informado");
		mongo.close();
		return null;
	};
	if (itens == null) {
		System.out.println("itens não informados");
		mongo.close();
		return null;
	};
	if (full == null) {
		full = "1";
	};
	if (limite == 0) {
		limite = 999999999;
	};
	
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	BasicDBObject finalResult = new BasicDBObject();
	
	String[] arrayItens = itens.split(";");
	
	if (usuarioPar == null && objetivoPar == null && habilidadePar == null && cursoPar == null && areaAtuacaoPar == null && areaConhecimentoPar == null && badgePar == null) {
		for (int i = 0; i < arrayItens.length; i++) {
			switch (arrayItens[i]) {
			case "Usuarios":
				if (full.equals("0")) {
					finalResult.put("Usuarios", usuario.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
						finalResult.put("Usuarios", usuario.getAll(usuarioParametro, mongo));
					};
				};
				break;
			case "Objetivos":
				if (full.equals("0")) {
					finalResult.put("Objetivos", objetivo.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
						finalResult.put("Objetivos", objetivo.getAll(usuarioParametro, mongo));
					};
				};
				break;
			case "Habilidades":
				if (full.equals("0")) {
					finalResult.put("Habilidades", habilidade.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
						finalResult.put("Habilidades", habilidade.getAll(usuarioParametro, mongo));
					};
				};
				break;
			case "Cursos":
				if (full.equals("0")) {
				finalResult.put("Cursos", curso.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
  					finalResult.put("Cursos", curso.getAll(usuarioParametro, mongo));
					};
				};
				break;
			case "AreaAtuacao":
				if (full.equals("0")) {
				finalResult.put("AreaAtuacao", areaAtuacao.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
  					finalResult.put("AreaAtuacao", areaAtuacao.getAll(usuarioParametro, mongo));
					};
				};
				break;
			case "AreaConhecimento":
				if (full.equals("0")) {
				finalResult.put("AreaConhecimento", areaConhecimento.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
  					finalResult.put("AreaConhecimento", areaConhecimento.getAll(usuarioParametro, mongo));
					};
				};
				break;
			case "Badges":
				if (full.equals("0")) {
				finalResult.put("Badges", badge.getIdNome(usuarioParametro, mongo));
				}else {
					if (full.equals("1")) {
  					finalResult.put("Badges", badge.getAll(usuarioParametro, mongo));
					};
				}
				break;
			default:
				System.out.println("Item invalido:" + arrayItens[i]);
				break;
			}
		};
	};

	String[] arrayAssuntos = new String[100]; 
	if (assuntos != null) {
		arrayAssuntos = assuntos.split(";");
	};

	if (usuarioPar != null) {
		if (full.equals("2")){
			finalResult.put("Usuario", usuario.get(usuarioPar, mongo));
		}else {
			if (arrayItens == null) {
				System.out.println("informar itens");
				mongo.close();
				return null;					
			}else {
 			for (int i = 0; i < arrayItens.length; i++) {
				switch (arrayItens[i]) {
				case "Objetivos":
	 				if (arrayAssuntos == null) {
	 					System.out.println("informar assuntos");
	 					mongo.close();
	 					return null;					
	 				}else {
  					for (int j = 0; j < arrayAssuntos.length; j++) {
  						switch (arrayAssuntos[j]) {
  						case "possui":
  							finalResult.put("usuarioObjetivosPossui", commons.controlaLimite(usuario.getObjetivos(usuarioPar, usuarioPar, "carreiras",full, mongo), limite, start));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioObjetivosInteresse", commons.controlaLimite(usuario.getObjetivos(usuarioPar, usuarioPar, "carreirasInteresse",full, mongo), limite, start));							
  							break;
  						case "sugeridos":
  							finalResult.put("usuarioObjetivosSugeridos", commons.controlaLimite(usuario.getObjetivos(usuarioPar, usuarioPar, "carreirasSugeridas",full,  mongo), limite, start));							
  							break;
  						default:
  							System.out.println("Assunto invalido:" + arrayAssuntos[j]);
  							break;
  						}
  					};
	 				};
					break;
				case "Habilidades":
					BasicDBObject result = new BasicDBObject();
	 				if (arrayAssuntos == null) {
	 					System.out.println("informar assuntos");
	 					mongo.close();
	 					return null;					
	 				}else {
  					for (int j = 0; j < arrayAssuntos.length; j++) {
  						switch (arrayAssuntos[j]) {
  						case "possui":
  							result = usuario.getHabilidades(usuarioPar, usuarioPar, "habilidades", full, mongo);
  							finalResult.put("usuarioHabilidades", result.get("habilidades"));							
  							finalResult.put("qtdPossui", result.get("qtdPossui"));							
  							finalResult.put("qtdInteresse", result.get("qtdInteresse"));							
  							finalResult.put("qtdNecessarias", result.get("qtdNecessarias"));							
  							finalResult.put("qtdObjetivos", result.get("qtdObjetivos"));							
  							break;
  						case "interesse":
  							result = usuario.getHabilidades(usuarioPar, usuarioPar, "habilidadesInteresse", full, mongo);
  							finalResult.put("usuarioHabilidadesInteresse", result.get("habilidades"));							
  							finalResult.put("qtdPossui", result.get("qtdPossui"));							
  							finalResult.put("qtdInteresse", result.get("qtdInteresse"));							
  							finalResult.put("qtdNecessarias", result.get("qtdNecessarias"));							
  							finalResult.put("qtdObjetivos", result.get("qtdObjetivos"));							
  							break;
  						case "necessarias":
  							result = usuario.getHabilidadesNecessarias(usuarioPar, full,  mongo);
  							finalResult.put("usuarioHabilidadesNecessarias", result.get("habilidades"));							
  							finalResult.put("qtdPossui", result.get("qtdPossui"));							
  							finalResult.put("qtdInteresse", result.get("qtdInteresse"));							
  							finalResult.put("qtdNecessarias", result.get("qtdNecessarias"));							
  							finalResult.put("qtdObjetivos", result.get("qtdObjetivos"));							
  							break;
  						case "quantidades":
  							result = usuario.getHabilidadesNecessarias(usuarioPar, full,  mongo);
  							finalResult.put("qtdPossui", result.get("qtdPossui"));							
  							finalResult.put("qtdInteresse", result.get("qtdInteresse"));							
  							finalResult.put("qtdNecessarias", result.get("qtdNecessarias"));							
  							finalResult.put("qtdObjetivos", result.get("qtdObjetivos"));							
  							break;
  						default:
  							System.out.println("Assunto invalido:" + arrayAssuntos[j]);
  							break;
  						}
  					};
	 				};
  				break;
				case "Cursos":
	 				if (arrayAssuntos == null) {
	 					System.out.println("informar assuntos");
	 					mongo.close();
	 					return null;					
	 				}else {
  					for (int j = 0; j < arrayAssuntos.length; j++) {
  						switch (arrayAssuntos[j]) {
  						case "possui":
  							finalResult.put("usuarioCursosPossui", commons.controlaLimite(usuario.getCursos(usuarioPar, usuarioPar, "cursos", full,  mongo), limite, start));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioCursosInteresse", commons.controlaLimite(usuario.getCursos(usuarioPar, usuarioPar, "cursosInteresse", full, mongo), limite, start));							
  							break;
  						case "sugeridos":
  							finalResult.put("usuarioCursosSugeridos", commons.controlaLimite(usuario.getCursos(usuarioPar, usuarioPar, "cursosSugeridos",full, mongo), limite, start));							
  							break;
  						case "inscritos":
  							finalResult.put("usuarioCursosInscrito", commons.controlaLimite(usuario.getCursos(usuarioPar, usuarioPar, "cursosInscritos",full,  mongo), limite, start));							
  							break;
  						case "em andamento":
  							finalResult.put("usuarioCursosEmAndamento", commons.controlaLimite(usuario.getCursos(usuarioPar, usuarioPar, "cursosAndamento", full, mongo), limite, start));							
  							break;
  						default:
  							System.out.println("Assunto invalido:" + arrayAssuntos[j]);
  							break;
  						}
  					};
	 				};
					break;
				case "Badges":
	 				if (arrayAssuntos == null) {
	 					System.out.println("informar assuntos");
	 					mongo.close();
	 					return null;					
	 				}else {
  					for (int j = 0; j < arrayAssuntos.length; j++) {
  						switch (arrayAssuntos[j]) {
  						case "possui":
  							finalResult.put("usuarioBadgesPossui", commons.controlaLimite(usuario.getBadges(usuarioPar, usuarioPar, "badges",full,  mongo), limite, start));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioBadgesInteresse", commons.controlaLimite(usuario.getBadges(usuarioPar, usuarioPar, "badgesInteresse",full,  mongo), limite, start));							
  							break;
  						case "conquista":
  							finalResult.put("usuarioBadgesConquista", commons.controlaLimite(usuario.getBadges(usuarioPar, usuarioPar, "badgesConquista",full,  mongo), limite, start));							
  							break;
  						case "show":
  							finalResult.put("usuarioBadgesShow", commons.controlaLimite(usuario.getBadges(usuarioPar, usuarioPar, "showBadges", full, mongo), limite, start));							
  							break;
  						default:
  							System.out.println("Assunto invalido:" + arrayAssuntos[j]);
  							break;
  						}
  					};
	 				};
					break;
				case "AreaAtuacao":
	 				if (arrayAssuntos == null) {
	 					System.out.println("informar assuntos");
	 					mongo.close();
	 					return null;					
	 				}else {
  					for (int j = 0; j < arrayAssuntos.length; j++) {
  						switch (arrayAssuntos[j]) {
  						case "possui":
  							finalResult.put("usuarioAreasAtuacaoPossui", commons.controlaLimite(usuario.getAreaAtuacao(usuarioPar, usuarioPar,"areasAtuacao", full, mongo), limite, start));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioAreasAtuacaoInteresse", commons.controlaLimite(usuario.getAreaAtuacao(usuarioPar, usuarioPar, "areasAtuacaoInteresse", full, mongo), limite, start));						
  							break;
  						default:
  							System.out.println("Assunto invalido:" + arrayAssuntos[j]);
  							break;
  						}
  					};
	 				};
					break;
				default:
					System.out.println("Item invalido:" + arrayItens[i]);
					break;
				};
 			};
		};
		};
	};
	
	if (habilidadePar != null) {
		if (full.equals("2")){
			finalResult.put("habilidade", habilidade.getId(habilidadePar, usuarioParametro, mongo));
		}else {
		for (int i = 0; i < arrayItens.length; i++) {
			switch (arrayItens[i]) {
				case "Usuarios":
	 				if (arrayAssuntos == null) {
	 					System.out.println("informar assuntos");
	 					mongo.close();
	 					return null;					
	 				}else {
						for (int j = 0; j < arrayAssuntos.length; j++) {
							switch (arrayAssuntos[j]) {
							case "possui":
								finalResult.put("habilidadesUsuariosPossui", commons.controlaLimite(habilidade.getUsuarios(habilidadePar, usuarioParametro, "habilidades",full, mongo), limite, start));							
								break;
							case "interesse":
								finalResult.put("habilidadesUsuariosInteresse", commons.controlaLimite(habilidade.getUsuarios(habilidadePar, usuarioParametro, "habilidadesInteresse",full, mongo), limite, start));							
								break;
							default:
								System.out.println("Assunto invalido:" + arrayAssuntos[j]);
								break;
							}
						};
	 				};
					break;
				case "Cursos":
					finalResult.put("habilidadesCursos", commons.controlaLimite(habilidade.getCursos(habilidadePar, usuarioParametro, "cursos", full, mongo), limite, start));							
					break;
				case "Objetivos":
					finalResult.put("habilidadesObjetivos", commons.controlaLimite(habilidade.getObjetivos(habilidadePar, usuarioParametro, "objetivos", full, mongo), limite, start));				
					break;
				case "AreaConhecimento":
					finalResult.put("objetivoAreasAtuacao", commons.controlaLimite(habilidade.getAreaConhecimento(habilidadePar, usuarioParametro, "areaConhecimento", full, mongo), limite, start));							
				default:
					break;
				};
			};
		};
	};

	String[] arrayObjetivos = objetivoPar.split(";");

	if (objetivoPar != null) {
		ArrayList<Object> objetivos = new ArrayList<>();
		for (int i = 0; i < arrayObjetivos.length; i++) {
			BasicDBObject objetivoDoc = new BasicDBObject();
			String objetivoId =  arrayObjetivos[i];
			if (full.equals("2")){
				objetivoDoc.put("objetivo", objetivo.getId(objetivoId, usuarioParametro, mongo));
			}else {
				for (int k = 0; k < arrayItens.length; k++) {
					switch (arrayItens[k]) {
						case "Usuarios":
			 				if (arrayAssuntos == null) {
			 					System.out.println("informar assuntos");
			 					mongo.close();
			 					return null;					
			 				}else {
							for (int j = 0; j < arrayAssuntos.length; j++) {
								switch (arrayAssuntos[j]) {
								case "possui":
									objetivoDoc.put("objetivoUsuariosPossui", commons.controlaLimite(objetivo.getUsuarios(objetivoId, usuarioParametro, "carreiras",full, mongo), limite, start));							
									break;
								case "interesse":
									objetivoDoc.put("objetivoUsuariosInteresse", commons.controlaLimite(objetivo.getUsuarios(objetivoId, usuarioParametro, "carreirasInteresse",full, mongo), limite, start));					
									break;
								case "sugeridos":
									objetivoDoc.put("objetivoUsuariosSugeridos", commons.controlaLimite(objetivo.getUsuarios(objetivoId, usuarioParametro, "carreirasSugeridas",full,  mongo), limite, start));		
									break;
								default:
									System.out.println("Assunto invalido:" + arrayAssuntos[j]);
									break;
								}
							};
			 				};
			 				break;
						case "Habilidades":
			 				if (arrayAssuntos == null) {
			 					System.out.println("informar assuntos");
			 					mongo.close();
			 					return null;					
			 				}else {
							for (int j = 0; j < arrayAssuntos.length; j++) {
								switch (arrayAssuntos[j]) {
								case "necessarias":
									BasicDBObject result = objetivo.getHabilidades(objetivoId, usuarioParametro, "necessarios", full, mongo);
									objetivoDoc.put("objetivoHabilidades", result.get("objetivoHabilidades"));
									objetivoDoc.put("delta", result.get("delta"));
									objetivoDoc.put("percentual", result.get("percentual"));
									break;
								case "recomendadas":
									objetivoDoc.put("ObjetivoHabilidadesRecomendadas", objetivo.getHabilidades(objetivoId, usuarioParametro, "recomendados",full,  mongo));							
									break;
								default:
									System.out.println("Assunto invalido:" + arrayAssuntos[j]);
									break;
								}
							};
			 				};
							break;
						case "AreaAtuacao":
							objetivoDoc.put("objetivoAreasAtuacao", commons.controlaLimite(objetivo.getAreaAtuacao(objetivoId, usuarioParametro, "areasAtuacao", full, mongo), limite, start));							
						default:
							break;
						};
					};
				};
				objetivos.add(objetivoDoc);
		}
		finalResult.put("objetivos", objetivos);
	};
	
	if (cursoPar != null) {
		if (full.equals("2")){
			finalResult.put("curso", curso.getId(cursoPar, usuarioParametro, mongo));
		}else {
			for (int i = 0; i < arrayItens.length; i++) {
				switch (arrayItens[i]) {
					case "Badges":
						finalResult.put("badgesCurso", commons.controlaLimite(curso.getBadges(cursoPar, usuarioParametro, "carreiras",full, mongo), limite, start));							
					case "Habilidades":
						finalResult.put("badgesHabilidades", commons.controlaLimite(curso.getHabilidades(cursoPar, usuarioParametro, "necessarios", full, mongo), limite, start));							
					default:
						break;
					};
			};
		};
	};
	
	mongo.close();
	return finalResult;
	}
};
