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

public class Rest_Objeto {
	
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
	AreaConhecimentoCurso areaConhecimentoCurso = new AreaConhecimentoCurso();
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
													@QueryParam("areaConhecimentoCurso") String areaConhecimentoCursoPar,
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
	
	if (usuarioPar == null && objetivoPar == null && habilidadePar == null && cursoPar == null && areaAtuacaoPar == null && areaConhecimentoPar == null && areaConhecimentoCursoPar == null && badgePar == null) {
		for (int i = 0; i < arrayItens.length; i++) {
			switch (arrayItens[i]) {
			case "Usuarios":
				if (full.equals("0")) {
					finalResult.put("Usuarios", commons.controlaLimite(usuario.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
						finalResult.put("Usuarios", commons.controlaLimite(usuario.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "Objetivos":
				if (full.equals("0")) {
					finalResult.put("Objetivos", commons.controlaLimite(objetivo.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
						finalResult.put("Objetivos", commons.controlaLimite(objetivo.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "Habilidades":
				if (full.equals("0")) {
					finalResult.put("Habilidades", commons.controlaLimite(habilidade.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
						finalResult.put("Habilidades", commons.controlaLimite(habilidade.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "Cursos":
				if (full.equals("0")) {
					finalResult.put("Cursos", commons.controlaLimite(curso.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
  					finalResult.put("Cursos", commons.controlaLimite(curso.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "AreaAtuacao":
				if (full.equals("0")) {
					finalResult.put("AreaAtuacao", commons.controlaLimite(areaAtuacao.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
  					finalResult.put("AreaAtuacao", commons.controlaLimite(areaAtuacao.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "AreaConhecimento":
				if (full.equals("0")) {
					finalResult.put("AreaConhecimento", commons.controlaLimite(areaConhecimento.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
  					finalResult.put("AreaConhecimento", commons.controlaLimite(areaConhecimento.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "AreaConhecimentoCurso":
				if (full.equals("0")) {
					finalResult.put("AreaConhecimentoCurso", commons.controlaLimite(areaConhecimentoCurso.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
  					finalResult.put("AreaConhecimentoCurso", commons.controlaLimite(areaConhecimentoCurso.getAll(usuarioParametro, mongo), limite, start));
					};
				};
				break;
			case "Badges":
				if (full.equals("0")) {
					finalResult.put("Badges", commons.controlaLimite(badge.getIdNome(usuarioParametro, mongo), limite, start));
				}else {
					if (full.equals("1")) {
  					finalResult.put("Badges", commons.controlaLimite(badge.getAll(usuarioParametro, mongo), limite, start));
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

	String[] arrayHabilidades = new String[100]; 
	if (habilidadePar != null) {
		arrayHabilidades = habilidadePar.split(";");
	};
	
	if (habilidadePar != null) {
		ArrayList<Object> habilidades = new ArrayList<>();
		for (int k = 0; k < arrayHabilidades.length; k++) {
			BasicDBObject habilidadeDoc = new BasicDBObject();
			String habilidadeId =  arrayHabilidades[k];
			if (full.equals("2")){
				finalResult.put("habilidade", habilidade.getId(habilidadeId, usuarioParametro, mongo));
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
									habilidadeDoc.put("habilidadesUsuariosPossui", commons.controlaLimite(habilidade.getUsuarios(habilidadeId, usuarioParametro, "habilidades",full, mongo), limite, start));							
									break;
								case "interesse":
									habilidadeDoc.put("habilidadesUsuariosInteresse", commons.controlaLimite(habilidade.getUsuarios(habilidadeId, usuarioParametro, "habilidadesInteresse",full, mongo), limite, start));							
									break;
								default:
									System.out.println("Assunto invalido:" + arrayAssuntos[j]);
									break;
								}
							};
		 				};
						break;
					case "Cursos":
						habilidadeDoc.put("habilidadesCursos", commons.controlaLimite(habilidade.getCursos(habilidadeId, usuarioParametro, "cursos", full, mongo), limite, start));							
						break;
					case "Objetivos":
						habilidadeDoc.put("habilidadesObjetivos", commons.controlaLimite(habilidade.getObjetivos(habilidadeId, usuarioParametro, "objetivos", full, mongo), limite, start));				
						break;
					case "AreaConhecimento":
						habilidadeDoc.put("objetivoAreasAtuacao", commons.controlaLimite(habilidade.getAreaConhecimento(habilidadeId, usuarioParametro, "areaConhecimento", full, mongo), limite, start));							
					default:
						break;
					};
				};
			};
			habilidades.add(habilidadeDoc);
		};
		finalResult.put("habilidades", habilidades);
	};

	String[] arrayObjetivos = new String[100]; 
	if (objetivoPar != null) {
		arrayObjetivos = objetivoPar.split(";");
	};

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

	String[] arrayCursos = new String[100]; 
	if (cursoPar != null) {
		arrayCursos = cursoPar.split(";");
	};
	
	if (cursoPar != null) {
		ArrayList<Object> cursos = new ArrayList<>();
		for (int k = 0; k < arrayCursos.length; k++) {
			BasicDBObject cursoDoc = new BasicDBObject();
			String cursoId =  arrayCursos[k];
			if (full.equals("2")){
				finalResult.put("curso", curso.getId(cursoPar, usuarioParametro, mongo));
			}else {
				for (int i = 0; i < arrayItens.length; i++) {
					switch (arrayItens[i]) {
						case "Badges":
							cursoDoc.put("badgesCurso", commons.controlaLimite(curso.getBadges(cursoId, usuarioParametro, "carreiras",full, mongo), limite, start));							
						case "Habilidades":
							cursoDoc.put("habilidadesCurso", commons.controlaLimite(curso.getHabilidades(cursoId, usuarioParametro, "necessarios", full, mongo), limite, start));							
						case "AreaConhecimento":
							cursoDoc.put("areaConhecimentoCurso", commons.controlaLimite(curso.getAreaConhecimento(cursoId, usuarioParametro, "areaConhecimento", full, mongo), limite, start));							
						default:
							break;
						};
				};
			};
			cursos.add(cursoDoc);
		};
		finalResult.put("cursos", cursos);
	};
	
	mongo.close();
	return finalResult;
	}
};
