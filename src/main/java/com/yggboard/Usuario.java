package com.yggboard;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class Usuario {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	SendEmailHtml sendEmailHtml = new SendEmailHtml();
	TemplateEmail templateEmail = new TemplateEmail(); 
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject usuarioGet = commons_db.getCollection(id, "usuarios", "_id", mongo, false);
		
		BasicDBObject usuario = new BasicDBObject();
		
		if (usuarioGet != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuarioGet.get("documento"));
			if (usuarioDoc != null) {
  			usuario.put("firstName", usuarioDoc.get("firstName"));
  			usuario.put("lastName", usuarioDoc.get("lastName"));
  			usuario.put("nome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
  			usuario.put("token", usuarioDoc.get("token"));
  			usuario.put("email", usuarioDoc.get("email"));
  			usuario.put("photo", usuarioDoc.get("photo"));
  			usuario.put("id", id);
  			return (BasicDBObject) usuario;
			}
		}
		usuario.put("firstName", "");
		usuario.put("lastName", "");
		usuario.put("nome", "");
		usuario.put("token", "");
		usuario.put("email", "");
		usuario.put("photo", "");
		usuario.put("id", id);
		
		return usuario;
	
	};
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject getEmail(String email, MongoClient mongo) {	
		
		BasicDBObject usuarioGet = commons_db.getCollection(email, "usuarios", "documento.email", mongo, false);
		
		BasicDBObject usuario = new BasicDBObject();
		
		if (usuarioGet != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuarioGet.get("documento"));
			if (usuarioDoc != null) {
  			usuario.put("firstName", usuarioDoc.get("firstName"));
  			usuario.put("lastName", usuarioDoc.get("lastName"));
  			usuario.put("nome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
  			usuario.put("token", usuarioDoc.get("token"));
  			usuario.put("email", usuario.get("email"));
  			usuario.put("id", usuario.get("_id").toString());
  			return (BasicDBObject) usuario;
			}
		}
		usuario.put("firstName", "");
		usuario.put("lastName", "");
		usuario.put("nome", "");
		usuario.put("token", "");
		usuario.put("email", "");
		usuario.put("id", "");
		
		return usuario;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean inoutCursosSelecionados(String cursoId, String usuarioId, MongoClient mongo)  {
		BasicDBObject usuario = new BasicDBObject();
		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);

		Boolean existeCurso = false;

		BasicDBObject usuarioDoc = new BasicDBObject();
		usuarioDoc.putAll((Map) usuario.get("documento"));

		ArrayList<Object> cursosSelecionadosNew = new ArrayList<>();
		if (usuarioDoc.get("cursosSelecionados") != null) {
			ArrayList<Object> cursosSelecionados = (ArrayList<Object>) usuarioDoc.get("cursosSelecionados");
	  		for (int i = 0; i < cursosSelecionados.size(); i++) {
	  			JSONObject curso = new JSONObject();
	  			curso.putAll((Map) cursosSelecionados.get(i));
	  			String cursoIdCompare = curso.get("id").toString();
	  			if (cursoIdCompare.equals(cursoId)) {
	  				existeCurso = true;	
	  			}else {
	  				cursosSelecionadosNew.add(cursosSelecionados.get(i));
	  			};
	  		};
		};
		
		if (!existeCurso) {
			JSONObject curso = new JSONObject();
			curso.put("status", "apendente");
			curso.put("id", cursoId);
			cursosSelecionadosNew.add(curso);
		};
		
		usuarioDoc.put("cursosSelecionados", cursosSelecionadosNew);
		
		usuario.put("documento", usuarioDoc);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", usuarioId);
		keysArray.add(key);
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento");
		field.put("value", usuarioDoc);
		fieldsArray.add(field);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", usuarioDoc);

		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, usuario, mongo, false);
		return true;	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean statusCursosSelecionados(String cursoId, String status, String usuarioId, MongoClient mongo) {
		BasicDBObject usuario = new BasicDBObject();
		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);

		BasicDBObject usuarioDoc = new BasicDBObject();
		usuarioDoc.putAll((Map) usuario.get("documento"));

		ArrayList<Object> cursosSelecionadosNew = new ArrayList<>();
		if (usuarioDoc.get("cursosSelecionados") != null){
  		ArrayList<Object> cursosSelecionados = (ArrayList<Object>) usuarioDoc.get("cursosSelecionados");
  		for (int i = 0; i < cursosSelecionados.size(); i++) {
  			JSONObject cursoCompare = new JSONObject();
  			cursoCompare.putAll((Map) cursosSelecionados.get(i));
  			String cursoIdCompare = cursoCompare.get("id").toString();
  			if (cursoIdCompare.equals(cursoId)) {
  				JSONObject curso = new JSONObject();
  				curso.put("status", status);
  				curso.put("id", cursoId);
  				cursosSelecionadosNew.add(curso);
  			}else {
  				cursosSelecionadosNew.add(cursosSelecionados.get(i));
  			};
  		};
		};
		
		usuarioDoc.put("cursosSelecionados", cursosSelecionadosNew);
		
		usuario.put("documento", usuarioDoc);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", usuarioId);
		keysArray.add(key);
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento");
		field.put("value", usuarioDoc);
		fieldsArray.add(field);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", usuarioDoc);

		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, usuario, mongo, false);
		return true;	
	}

	public BasicDBObject getUserPerfil(String usuarioPar, MongoClient mongo) {
		
		BasicDBObject usuario = commons_db.getCollectionDoc(usuarioPar, "usuarios", "_id", mongo, false);
		
		if (usuario != null) {
			if (usuario.get("email") != null) {
				return commons_db.getCollectionDoc(usuario.get("email").toString(), "userPerfil", "documento.usuario", mongo, false);
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public JSONArray getObjetivos(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "objetivos", "documento.id", mongo, false);
			if (objetivo != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", objetivo.get("_id"));
					item.put("id", objetivo.get("id"));
					item.put("nome", objetivo.get("nome"));
				}else {
					item.put("documento", objetivo);						
				}
				item.put("possui", "false");
				item.put("interesse", "false");
				if (userPerfil != null) {
					if (userPerfil.get("carreiras") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("carreiras");
		  				item.put("possui", commons.testaElementoArray(objetivo.get("id").toString(), itens));
					};
					if (userPerfil.get("carreirasInteresse") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("carreirasInteresse");
		  				item.put("interesse", commons.testaElementoArray(objetivo.get("id").toString(), itens));
					};
					//  **** calcula percentual de habilidades que o usuario possui dentro do objetivo
					ArrayList<String> necessarios = (ArrayList<String>) objetivo.get("necessarios");
					ArrayList<String> habilidadesPossui = (ArrayList<String>) userPerfil.get("habilidades");
					int qtdeHabilidadesPossui = commons.testaArrayElementosIguais(necessarios, habilidadesPossui);
					int qtdeNecessarios = necessarios.size();
					double percentual = ((double)qtdeHabilidadesPossui / (double)qtdeNecessarios) * 100;
					DecimalFormat formatador = new DecimalFormat("0.00");
					item.put("percentual", formatador.format(percentual).toString());
					item.put("percentual", formatador.format(percentual).toString());
				};
				result.add(item);
			};
		};
		return result;
	};

	@SuppressWarnings("unchecked")
	public BasicDBObject getHabilidades(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		BasicDBObject resultFinal = new BasicDBObject();
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject habilidade = commons_db.getCollectionDoc(array.get(i).toString(), "habilidades", "documento.id", mongo, false);
			if (habilidade != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", habilidade.get("_id"));
					item.put("id", habilidade.get("id"));
					item.put("nome", habilidade.get("nome"));
				}else {
					item.put("documento", habilidade);						
				}
				item.put("possui", "false");
				item.put("interesse", "false");
				if (userPerfil != null) {
					if (userPerfil.get("habilidades") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
		  				item.put("possui", commons.testaElementoArray(habilidade.get("id").toString(), itens));
					};
					if (userPerfil.get("habilidadesInteresse") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
		  				item.put("interesse", commons.testaElementoArray(habilidade.get("id").toString(), itens));
					};
				};
				result.add(item);
			};
		};

		ArrayList<String> itens = new ArrayList<>();
		itens = (ArrayList<String>) userPerfil.get("habilidades");
		resultFinal.put("qtdPossui", itens.size());
		itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
		resultFinal.put("qtdInteresse", itens.size());
		BasicDBObject resultNecessarias = getHabilidadesNecessarias(usuarioPar, full, mongo);
		resultFinal.put("qtdNecessarias", resultNecessarias.get("qtdNecessarias"));
		resultFinal.put("qtdObjetivos", resultNecessarias.get("qtdNecessarias"));							
		resultFinal.put("habilidades", result);
		return resultFinal;
	}

	@SuppressWarnings("unchecked")
	public BasicDBObject getHabilidadesNecessarias(String usuarioPar, String full,	MongoClient mongo) {

		BasicDBObject resultFinal = new BasicDBObject();
		
		JSONArray result = new JSONArray();
		JSONArray resultObjetivos = new JSONArray();
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		
		ArrayList<String> habilidadesPossui = (ArrayList<String>) userPerfil.get("habilidades");
		ArrayList<String> habilidadesNecessarias = new ArrayList<>();
		if (userPerfil.get("carreirasInteresse") != null) {
			ArrayList<String> array = (ArrayList<String>) userPerfil.get("carreirasInteresse");
			for (int i = 0; i < array.size(); i++) {
				BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i), "objetivos", "documento.id", mongo, false);
				if (objetivo != null) {
					ArrayList<String> necessarios = (ArrayList<String>) objetivo.get("necessarios");
					for (int j = 0; j < necessarios.size(); j++) {
						if (!commons.testaElementoArray(necessarios.get(j), habilidadesNecessarias)) {
							if (!commons.testaElementoArray(necessarios.get(j), habilidadesPossui)) {
								BasicDBObject habilidade = commons_db.getCollectionDoc(necessarios.get(j), "habilidades", "documento.id", mongo, false);
								if (habilidade != null) {
									BasicDBObject item = new BasicDBObject();
									if (full.equals("0")) {
										item.put("_id", habilidade.get("_id"));
										item.put("id", habilidade.get("id"));
										item.put("nome", habilidade.get("nome"));
									}else {
										item.put("documento", habilidade);						
									};
									item.put("possui", "false");
									item.put("interesse", "false");
									if (userPerfil != null) {
										if (userPerfil.get("habilidades") != null) {
							  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
							  				item.put("possui", commons.testaElementoArray(habilidade.get("id").toString(), itens));
										};
										if (userPerfil.get("habilidadesInteresse") != null) {
							  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
							  				item.put("interesse", commons.testaElementoArray(habilidade.get("id").toString(), itens));
										};
									};
									result.add(item);
									resultObjetivos.add("1");
									habilidadesNecessarias.add(necessarios.get(j));
								};
							};
						}else {
							int z = commons.indexElemento(habilidadesNecessarias, necessarios.get(j).toString());
							resultObjetivos.set(z, String.valueOf(Integer.valueOf(resultObjetivos.get(z).toString()) + 1));							
						};
					};
				};
			};
		};


		ArrayList<String> itens = new ArrayList<>();
		itens = (ArrayList<String>) userPerfil.get("habilidades");
		resultFinal.put("qtdPossui", itens.size());
		itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
		resultFinal.put("qtdInteresse", itens.size());
		resultFinal.put("qtdNecessarias", result.size());
		resultFinal.put("qtdObjetivos", resultObjetivos);
		resultFinal.put("habilidades", result);
		return resultFinal;
	}

	@SuppressWarnings("unchecked")
	public Object getCursos(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject cursos = commons_db.getCollectionDoc(array.get(i).toString(), "cursos", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", cursos.get("_id"));
				item.put("id", cursos.get("id"));
				item.put("nome", cursos.get("nome"));
			}else {
				item.put("documento", cursos);						
			}
			item.put("possui", "false");
			item.put("interesse", "false");
			if (userPerfil != null) {
				if (userPerfil.get("cursos") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursos");
	  				item.put("possui", commons.testaElementoArray(cursos.get("id").toString(), itens));
				};
				if (userPerfil.get("cursosInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInteresse");
	  				item.put("interesse", commons.testaElementoArray(cursos.get("id").toString(), itens));
				};
			};
			result.add(item);
		};
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getBadges(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject badge = commons_db.getCollectionDoc(array.get(i).toString(), "badges", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", badge.get("_id"));
				item.put("id", badge.get("id"));
				item.put("nome", badge.get("nome"));
			}else {
				item.put("documento", badge);						
			}
			item.put("possui", "false");
			item.put("interesse", "false");
			item.put("show", "false");
			if (userPerfil != null) {
				if (userPerfil.get("badges") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("badges");
	  				item.put("possui", commons.testaElementoArray(badge.get("id").toString(), itens));
				};
				if (userPerfil.get("badgesInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("badgesInteresse");
	  				item.put("interesse", commons.testaElementoArray(badge.get("id").toString(), itens));
				};
				if (userPerfil.get("showBadges") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("showBadges");
	  				item.put("show", commons.testaElementoArray(badge.get("id").toString(), itens));
				};
			};
			result.add(item);
		};
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAreaAtuacao(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "badges", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", objetivo.get("_id"));
				item.put("id", objetivo.get("id"));
				item.put("nome", objetivo.get("nome"));
			}else {
				item.put("documento", objetivo);						
			}
			result.add(item);
		};
		return result;
	}

	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		JSONArray result = commons_db.getCollectionListaNoKey("usuarios", mongo, false);
		
		return result;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getIdNome(String usuarioParametro, MongoClient mongo) {
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("usuarios", mongo, false);
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < resultAll.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			BasicDBObject obj = new BasicDBObject();
			obj.putAll((Map) resultAll.get(i));
			item.put("_id", obj.get("_id"));
			item.put("nome", obj.get("firstName").toString() + obj.get("lastName").toString());
			result.add(item);
		};
		
		return result;
	
	}

	@SuppressWarnings("rawtypes")
	public BasicDBObject criaUsuario(BasicDBObject usuarioIn, String empresaId, Boolean envioEmail, MongoClient mongo, boolean close) {

		BasicDBObject usuario = new BasicDBObject();
		BasicDBObject usuarioDoc = new BasicDBObject();

		String novaSenha = "mudar@123";
		byte[] tokenByte = commons.gerarHash(novaSenha);
		String pwmd5 = commons.stringHexa(tokenByte);
		
		usuarioDoc.put("firstName", usuarioIn.get("firstName"));
		usuarioDoc.put("lastName", usuarioIn.get("lastName"));
		usuarioDoc.put("institution", usuarioIn.get("institution"));
		usuarioDoc.put("gender", usuarioIn.get("gender"));
		usuarioDoc.put("city", usuarioIn.get("city"));
		usuarioDoc.put("celPhone", usuarioIn.get("celPhone"));
		usuarioDoc.put("photo", usuarioIn.get("photo"));
		usuarioDoc.put("birthDate", usuarioIn.get("birthDate"));
		usuarioDoc.put("email", usuarioIn.get("email"));
		usuarioDoc.put("perfil", "user");
		usuarioDoc.put("perfilEmpresa", "colaborador");
		usuarioDoc.put("password", pwmd5);
		usuarioDoc.put("status", usuarioIn.get("confirmado"));
		usuarioDoc.put("empresaId", empresaId);
		usuarioDoc.put("photo", usuarioIn.get("email") + ".jpg");
		usuario.put("documento", usuarioDoc);

		if (commons.getProperties().get("database").toString().equals("yggboard") && envioEmail){
			emailBemVindo ("Bem vindo a Yggboard", usuarioDoc, "mudar@123");
		};
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("usuarios", usuario, mongo, false).getEntity());
		return result;
		
	};

	private void emailBemVindo(String subject, BasicDBObject usuario, String senha) {

		String conteudo = "<h1>Bem vindo a YggBoard!</h1>";
				conteudo = conteudo + "<p>Sua empresa decidiu participar da revolução e a partir de agora você fará parte da mais poderosa plataforma de gestão de habilidades.</p>";
				conteudo = conteudo + "<p>Com YggBoard sua empresa consegue criar uma trilha de desenvolvimento com aquelas habilidades e os cursos específicos para você, alavancando sua carreira.</p>";
				conteudo = conteudo + "<p>Através deste e-mail você consegue já acessar a plataforma com as seguintes informações de login:</p>";
				conteudo = conteudo + "<p><b>E-mail: </b>" + usuario.get("email").toString() + "</p>";
				conteudo = conteudo + "<p><b>Senha: </b>" + senha + "<br />(recomendamos a você atualizar esta senha assim que possível)</p>";
				conteudo = conteudo + "<div style=\"margin-left:15px;\"><!--[if mso]><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"" +  commons.getProperties().get("host").toString() +  "\" style=\"height:35px;v-text-anchor:middle;width:200px;\" arcsize=\"12%\" stroke=\"f\" fill=\"t\"><v:fill type=\"tile\" src=\"https://www.yggboard.com/emkt/btn.jpg\" color=\"#79bd58\" /><w:anchorlock/><center style=\"color:#ffffff;font-family:sans-serif;font-size:13px;font-weight:bold;\">Acessar Plataforma</center></v:roundrect><![endif]--><a href=\""  +  commons.getProperties().get("host").toString() +  "\" target=\"_blank\" style=\"background-color:#79bd58;background-image:url(https://www.yggboard.com/emkt/btn.jpg);border-radius:4px;color:#ffffff;display:inline-block;font-family:sans-serif;font-size:13px;font-weight:bold;line-height:35px;text-align:center;text-decoration:none;width:200px;-webkit-text-size-adjust:none;mso-hide:all;\">Acessar plataforma</a></div>";
				conteudo = conteudo + "<p>Nossa sugestão é que você construa seu perfil de habilidades da melhor maneira possível. Assim, não apenas a sua empresa poderá te conhecer melhor mas também nós conseguiremos te ajudar da melhor maneira possível.</p>";
				conteudo = conteudo + "<p>Fique também à vontade para entrar em contato e tirar qualquer dúvida que tenha sobre a plataforma e seu funcionamento.</p>";
				conteudo = conteudo + "<p>Obrigado pela atenção e conte conosco para apoiar seu desenvolvimento.</p>";
				
				
		sendEmailHtml.sendEmailHtml(usuario.get("email").toString(), subject, templateEmail.emailYggboard(conteudo));
			
	};
	
	
	
};
