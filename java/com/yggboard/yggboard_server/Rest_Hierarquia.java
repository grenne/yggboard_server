package com.yggboard.yggboard_server;

import java.util.ArrayList;
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
import com.mongodb.MongoClient;

@Singleton
//@Lock(LockType.READ)
@Path("/hierarquia")

public class Rest_Hierarquia {

	
	MongoClient mongo = new MongoClient();
	Rest_Avaliacao avaliacao = new Rest_Avaliacao(); 
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Hierarquia hierarquia = new Hierarquia();
	SendEmailHtml sendEmailHtml = new SendEmailHtml();
	TemplateEmail templateEmail = new TemplateEmail(); 

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/areas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Areas(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};

		ArrayList<String> areasAvaliacao = new ArrayList<String>();
		if (avaliacaoId != null) {
			BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
			if (avaliacao.get("documento") != null) {
				BasicDBObject avaliacaoDoc = new BasicDBObject();
				avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				if (avaliacaoDoc.get("areas") != null) {
					areasAvaliacao = (ArrayList<String>) avaliacaoDoc.get("areas");
				};
			};
		};

		ArrayList<Object> hierarquias = new ArrayList<Object>(); 
		hierarquias = commons_db.getCollectionLista(empresaId, "hierarquias", "documento.empresaId", mongo, false);

		JSONArray areas = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			if (hierarquia.get("area") != null) {
				BasicDBObject area = new BasicDBObject();
				area.put("nome", hierarquia.get("area"));
				if (commons.testaElementoArray(hierarquia.get("area").toString(), areasAvaliacao)) {
					area.put("select", "true");	
				}else {
					area.put("select", "false");
				};
				commons.addObjeto(areas, area);
			};
		};
		mongo.close();
		return areas;
	};

	@SuppressWarnings({"unchecked", "rawtypes" })
	@Path("/niveis")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Niveis(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};

		ArrayList<String> niveisAvaliacao = new ArrayList<String>();
		if (avaliacaoId != null) {
			BasicDBObject avaliacao = commons_db.getCollection(avaliacaoId, "avaliacoes", "_id", mongo, false);
			if (avaliacao.get("documento") != null) {
				BasicDBObject avaliacaoDoc = new BasicDBObject();
				avaliacaoDoc.putAll((Map) avaliacao.get("documento"));
				if (avaliacaoDoc.get("niveis") != null) {
					niveisAvaliacao = (ArrayList<String>) avaliacaoDoc.get("niveis");
				};
			};
		};

		ArrayList<Object> objetivos = new ArrayList<Object>(); 
		objetivos = commons_db.getCollectionLista(empresaId, "objetivosEmpresa", "documento.empresaId", mongo, false);

		JSONArray niveis = new JSONArray();
		for (int i = 0; i < objetivos.size(); i++) {
			BasicDBObject objetivoEmpresa = new BasicDBObject();
			objetivoEmpresa.putAll((Map) objetivos.get(i));
			if (objetivoEmpresa.get("objetivoId") != null) {
				BasicDBObject objetivo = commons_db.getCollection(objetivoEmpresa.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false);
				if (objetivo.get("documento") != null) {
					BasicDBObject objetivoDoc = new BasicDBObject();
					objetivoDoc.putAll((Map) objetivo.get("documento"));
					BasicDBObject nivel = new BasicDBObject();
					nivel.put("nome", objetivoDoc.get("nivelFiltro"));
					if (commons.testaElementoArray(objetivoDoc.get("nivelFiltro").toString(), niveisAvaliacao)) {
						nivel.put("select", "true");	
					}else {
						nivel.put("select", "false");
					};
					commons.addObjeto(niveis, nivel);
				};
			};
		};
		mongo.close();
		return niveis;
		
	};

	@Path("/colaboradores")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("usuarioId") String usuarioId, @QueryParam("perfil") String perfil)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = hierarquia.colaboradores(empresaId, usuarioId, perfil, mongo);
		mongo.close();
		return result;
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/importar")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response importaHierarquia(BasicDBObject hierarquiaJson)  {

		if (hierarquiaJson.get("token") == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(hierarquiaJson.get("token").toString(), "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (hierarquiaJson.get("empresaId") == null) {
			mongo.close();
			return null;
		};
		
		Boolean envioEmail = true;
		
		if (hierarquiaJson.get("envioEmail") == null) {
			envioEmail = (Boolean) hierarquiaJson.get("envioEmail");
		};		
		
		String empresaId = (String) hierarquiaJson.get("empresaId");
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);

		commons_db.removerCrudMany("hierarquias", keysArray, mongo, false);
		
		ArrayList<Object> colaboradores = (ArrayList<Object>) hierarquiaJson.get("colaboradores");

		String perfilEmpresa = "rh";
		for (int i = 0; i < colaboradores.size(); i++) {
			BasicDBObject colaborador = new BasicDBObject();
			colaborador.putAll((Map) colaboradores.get(i));
			BasicDBObject usuario = new BasicDBObject();
			usuario = commons_db.getCollection(colaborador.get("email").toString(), "usuarios", "documento.email", mongo, false);
			if (usuario == null){
				usuario = criaUsuario(colaborador, empresaId, envioEmail, mongo, false);
			}else {
				keysArray = new ArrayList<>();
				key = new JSONObject();
				key.put("key", "documento.email");
				key.put("value", colaborador.get("email").toString());
				keysArray.add(key);				
				ArrayList<JSONObject> fieldsArray = new ArrayList<>();
				JSONObject field = new JSONObject();				
				fieldsArray = new ArrayList<>();
				field = new JSONObject();
				field.put("field", "photo");
				field.put("value", colaborador.get("email") + ".jpg");
				fieldsArray.add(field);
				fieldsArray = new ArrayList<>();
				field = new JSONObject();
				field.put("field", "empresaId");
				field.put("value", empresaId);
				fieldsArray.add(field);
				field = new JSONObject();
				field.put("field", "perfilEmpresa");
				field.put("value", perfilEmpresa);
				fieldsArray.add(field);
				BasicDBObject documento = new BasicDBObject();
				documento.put("documento", colaborador);
				commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
			};
			BasicDBObject userPerfil = new BasicDBObject();
			userPerfil = commons_db.getCollection(colaborador.get("email").toString(), "userPerfil", "documento.usuario", mongo, false);
			if (userPerfil == null){
				userPerfil = criaUserPerfil(colaborador, empresaId, mongo);
			};
			perfilEmpresa = "colaborador";
		};
		
		for (int i = 0; i < colaboradores.size(); i++) {
			BasicDBObject colaborador = new BasicDBObject();
			colaborador.putAll((Map) colaboradores.get(i));
			criaHierarquia(colaborador, empresaId, mongo);
		};

		mongo.close();
		return Response.status(200).entity(true).build();	
	
	};	

	@SuppressWarnings("rawtypes")
	private BasicDBObject criaUsuario(BasicDBObject usuarioIn, String empresaId, Boolean envioEmail, MongoClient mongo, boolean close) {

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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BasicDBObject criaUserPerfil(BasicDBObject usuario, String empresaId, MongoClient mongo) {

		BasicDBObject userPerfil = new BasicDBObject();
		BasicDBObject userPerfilDoc = new BasicDBObject();
		
		ArrayList<String> arrayVazia = new ArrayList<String>();
		
		userPerfilDoc.put("usuario", usuario.get("email"));
		userPerfilDoc.put("seguindo", arrayVazia);
		userPerfilDoc.put("carreirasSugeridas", arrayVazia);
		userPerfilDoc.put("cursos", arrayVazia);
		userPerfilDoc.put("cursosInteresse", arrayVazia);
		userPerfilDoc.put("cursosInscrito", arrayVazia);
		userPerfilDoc.put("badgesInsteresse", arrayVazia);
		userPerfilDoc.put("habilidadesInteresse", arrayVazia);
		userPerfilDoc.put("cursosSugeridos", arrayVazia);
		userPerfilDoc.put("tags", arrayVazia);
		userPerfilDoc.put("badges", arrayVazia);
		userPerfilDoc.put("showBadges", arrayVazia);
		userPerfilDoc.put("ordemObjetivos", arrayVazia);
		userPerfilDoc.put("elementos", arrayVazia);
		userPerfilDoc.put("carreiras", arrayVazia);
		userPerfilDoc.put("badgesConquista", arrayVazia);
		userPerfilDoc.put("cursosAndamento", arrayVazia);
		userPerfil.put("documento", userPerfilDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("userPerfil", userPerfil, mongo, false).getEntity());

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();

		if (result.get("_id") != null) {
  		keysArray = new ArrayList<>();
  		key = new JSONObject();
  		key.put("key", "documento.email");
  		key.put("value", usuario.get("email").toString());
  		keysArray.add(key);				
  		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
  		JSONObject field = new JSONObject();				
  		fieldsArray = new ArrayList<>();
  		field = new JSONObject();
  		field.put("field", "userPerfil_id");
  		field.put("value", result.get("_id").toString());
  		fieldsArray.add(field);
  		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
		};

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

	@SuppressWarnings("rawtypes")
	private BasicDBObject criaHierarquia(BasicDBObject usuario, String empresaId, MongoClient mongo) {

		BasicDBObject objetivo = new BasicDBObject();
		BasicDBObject objetivoDoc = new BasicDBObject();
		if (usuario.get("objetivo") != null) {
  		objetivo = commons_db.getCollection(usuario.get("objetivo").toString(), "objetivos", "documento.id", mongo, false);
  		if (objetivo == null) {
  			return null;
  		};
  		objetivoDoc.putAll((Map) objetivo.get("documento"));
  		usuario.put("objetivoId", objetivoDoc.get("id").toString());
		};

		BasicDBObject hierarquia = new BasicDBObject();
		BasicDBObject hierarquiaDoc = new BasicDBObject();
		
		hierarquiaDoc.put("empresaId", empresaId);
		hierarquiaDoc.put("objetivoId", usuario.get("objetivoId"));
		hierarquiaDoc.put("area", usuario.get("area"));
		hierarquiaDoc.put("colaborador", commons_db.getCollection(usuario.get("email").toString(), "usuarios", "documento.email", mongo, false).get("_id").toString());
		if (usuario.get("superior") != null && usuario.get("superior") != "") {
			hierarquiaDoc.put("superior", commons_db.getCollection(usuario.get("superior").toString(), "usuarios", "documento.email", mongo, false).get("_id").toString());
			atualizaGestor(usuario.get("superior").toString());
		}else {
			hierarquiaDoc.put("superior","");
		};
		hierarquiaDoc.put("nivel", objetivoDoc.get("nivel").toString());
		hierarquia.put("documento", hierarquiaDoc);
		
		BasicDBObject result = new BasicDBObject();
		result.putAll((Map) commons_db.incluirCrud("hierarquias", hierarquia, mongo, false).getEntity());
		return result;
		
	}

	@SuppressWarnings("unchecked")
	private void atualizaGestor(String usuarioEmail) {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", usuarioEmail);
		keysArray.add(key);

		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();				
		fieldsArray = new ArrayList<>();
		field = new JSONObject();
		field.put("field", "perfilEmpresa");
		field.put("value", "gestor");
		fieldsArray.add(field);
		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
			
	};

};