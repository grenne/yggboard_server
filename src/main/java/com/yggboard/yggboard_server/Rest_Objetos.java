package com.yggboard.yggboard_server;

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
													@QueryParam("start") String start ,
													@QueryParam("limite") String limite, 
													@QueryParam("full") String full,
													@QueryParam("assuntos") String assuntos,
													@QueryParam("usuarioParametro") String usuarioParametro,
													@QueryParam("usuario") String usuarioPar,
													@QueryParam("objetivo") String objetivoPar,
													@QueryParam("habilidade") String habilidadePar,
													@QueryParam("curso") String cursoPar,
													@QueryParam("areaAtuacao") String areaAtuacaoPar,
													@QueryParam("areaConhecimento") String areaConhecimentoPar,
													@QueryParam("badge") String badgePar)  {
		
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
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	BasicDBObject finalResult = new BasicDBObject();
	
	String[] arrayItens = itens.split(";");
	String[] arrayAssuntos = new String[100]; 
	if (assuntos != null) {
		arrayAssuntos = assuntos.split(";");
	};
	
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
  							finalResult.put("usuarioObjetivosPossui", usuario.getObjetivos(usuarioPar, usuarioParametro, "carreiras",full, mongo));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioObjetivosInteresse", usuario.getObjetivos(usuarioPar, usuarioParametro, "carreirasInteresse",full, mongo));							
  							break;
  						case "sugeridos":
  							finalResult.put("usuarioObjetivosSugeridos", usuario.getObjetivos(usuarioPar, usuarioParametro, "carreirasSugeridas",full,  mongo));							
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
  						case "possui":
  							finalResult.put("usuarioHabilidades", usuario.getHabilidades(usuarioPar, usuarioParametro, "habilidades", full, mongo));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioHabilidadesInteresse", usuario.getHabilidades(usuarioPar, usuarioParametro, "habilidadesInteresse",full,  mongo));							
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
  							finalResult.put("usuarioCursosPossui", usuario.getCursos(usuarioPar, usuarioParametro, "cursos", full,  mongo));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioCursosInteresse", usuario.getCursos(usuarioPar, usuarioParametro, "cursosInteresse", full, mongo));							
  							break;
  						case "sugeridos":
  							finalResult.put("usuarioCursosSugeridos", usuario.getCursos(usuarioPar, usuarioParametro, "cursosSugeridos",full, mongo));							
  							break;
  						case "inscritos":
  							finalResult.put("usuarioCursosInscrito", usuario.getCursos(usuarioPar, usuarioParametro, "cursosInscritos",full,  mongo));							
  							break;
  						case "em andamento":
  							finalResult.put("usuarioCursosEmAndamento", usuario.getCursos(usuarioPar, usuarioParametro, "cursosAndamento", full, mongo));							
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
  							finalResult.put("usuarioBadgesPossui", usuario.getBadges(usuarioPar, usuarioParametro, "badges",full,  mongo));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioBadgesInteresse", usuario.getBadges(usuarioPar, usuarioParametro, "badgesInteresse",full,  mongo));							
  							break;
  						case "conquista":
  							finalResult.put("usuarioBadgesConquista", usuario.getBadges(usuarioPar, usuarioParametro, "badgesConquista",full,  mongo));							
  							break;
  						case "show":
  							finalResult.put("usuarioBadgesShow", usuario.getBadges(usuarioPar, usuarioParametro, "showBadges", full, mongo));							
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
  							finalResult.put("usuarioAreasAtuacaoPossui", usuario.getAreaAtuacao(usuarioPar, usuarioParametro,"areasAtuacao", full, mongo));							
  							break;
  						case "interesse":
  							finalResult.put("usuarioAreasAtuacaoInteresse", usuario.getAreaAtuacao(usuarioPar, usuarioParametro, "areasAtuacaoInteresse", full, mongo));							
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
								finalResult.put("habilidadesUsuariosPossui", habilidade.getUsuarios(habilidadePar, usuarioParametro, "habilidades",full, mongo));							
								break;
							case "interesse":
								finalResult.put("habilidadesUsuariosInteresse", habilidade.getUsuarios(habilidadePar, usuarioParametro, "habilidadesInteresse",full, mongo));							
								break;
							default:
								System.out.println("Assunto invalido:" + arrayAssuntos[j]);
								break;
							}
						};
	 				};
					break;
				case "Cursos":
					finalResult.put("habilidadesCursos", habilidade.getCursos(habilidadePar, usuarioParametro, "cursos", full, mongo));							
					break;
				case "Objetivos":
					finalResult.put("habilidadesObjetivos", habilidade.getObjetivos(habilidadePar, usuarioParametro, "objetivos", full, mongo));							
					break;
				case "AreaConhecimento":
					finalResult.put("objetivoAreasAtuacao", habilidade.getAreaConhecimento(habilidadePar, usuarioParametro, "areaConhecimento", full, mongo));							
				default:
					break;
				};
			};
		};
	};
	
	if (objetivoPar != null) {
		if (full.equals("2")){
			finalResult.put("objetivo", objetivo.getId(objetivoPar, usuarioParametro, mongo));
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
								finalResult.put("objetivoUsuariosPossui", objetivo.getUsuarios(objetivoPar, usuarioParametro, "carreiras",full, mongo));							
								break;
							case "interesse":
								finalResult.put("objetivoUsuariosInteresse", objetivo.getUsuarios(objetivoPar, usuarioParametro, "carreirasInteresse",full, mongo));							
								break;
							case "sugeridos":
								finalResult.put("objetivoUsuariosSugeridos", objetivo.getUsuarios(objetivoPar, usuarioParametro, "carreirasSugeridas",full,  mongo));							
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
								finalResult.put("objetivoHabilidades", objetivo.getHabilidades(objetivoPar, usuarioParametro, "necessarios", full, mongo));							
								break;
							case "recomendadas":
								finalResult.put("ObjetivoHabilidadesRecomendadas", objetivo.getHabilidades(objetivoPar, usuarioParametro, "recomendados",full,  mongo));							
								break;
							default:
								System.out.println("Assunto invalido:" + arrayAssuntos[j]);
								break;
							}
						};
		 				};
						break;
					case "AreaAtuacao":
						finalResult.put("objetivoAreasAtuacao", objetivo.getAreaAtuacao(objetivoPar, usuarioParametro, "areasAtuacao", full, mongo));							
					default:
						break;
					};
				};
			};
		};
	
	if (cursoPar != null) {
		if (full.equals("2")){
			finalResult.put("curso", curso.getId(cursoPar, usuarioParametro, mongo));
		}else {
			for (int i = 0; i < arrayItens.length; i++) {
				switch (arrayItens[i]) {
					case "Badges":
						finalResult.put("badgesCurso", curso.getBadges(cursoPar, usuarioParametro, "carreiras",full, mongo));							
					case "Habilidades":
						finalResult.put("badgesHabilidades", curso.getHabilidades(cursoPar, usuarioParametro, "necessarios", full, mongo));							
					default:
						break;
					};
			};
		};
	};
	
		mongo.close();
		return finalResult;
	};
};
