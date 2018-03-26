package com.yggboard;


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
// @Lock(LockType.READ)
@Path("/userPerfil")

public class Rest_UserPerfil {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	UserPerfil userPerfil = new UserPerfil();
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterUsuario(@QueryParam("usuario") String usuario, @QueryParam("usuarioConsultaId") String usuarioConsultaId){
		Commons_DB commons_db = new Commons_DB();
		return commons_db.getCollection(usuario, "userPerfil", "documento.token", mongo, true);
	};

	@Path("/obter-estatistica")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterEstatistica(@QueryParam("token") String token, @QueryParam("id") String id, @QueryParam("item") String item){
	
		UserPerfil userPerfil = new UserPerfil();
		
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (id == null){
			mongo.close();
			return null;
		};
		if (item == null){
			mongo.close();
			return null;
		};

		return userPerfil.obterEstatistica(id, item, mongo); 
	};
	
	@Path("/obter/itens")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCarreiras(@QueryParam("usuario") String usuario, @QueryParam("usuarioConsultaId") String usuarioConsultaId, @QueryParam("userPerfilConsultaId") String userPerfilConsultaId, @QueryParam("item") String item, @QueryParam("elemento") String elemento){
		
		System.out.println("Método deprecate" );
		return null;
	};
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject obterUserPerfil(String userPerfilConsultaId, MongoClient mongo) {
		
		BasicDBObject usuario = commons_db.getCollection(userPerfilConsultaId, "usuarios", "_id", mongo, false);
		if (usuario != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuario.get("documento"));
			if (usuarioDoc != null) {
				return commons_db.getCollection(usuarioDoc.get("email").toString(), "userPerfil", "documento.usuario", mongo, false);
			}
		};
		return null;
	};

	@Path("/atualizar/perfil")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AtualizarPerfil(JSONObject newPerfil){
		System.out.println("Método deprecate");
		return null;
	};
	
	@Path("/cursosSugeridos")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CursosSugeridos(JSONObject inputCursosSugeridos)  {
				
		mongo.close();
		return userPerfil.atualizaSugestaoColetiva (inputCursosSugeridos, "cursosSugeridos", mongo);
		
	};
	@Path("/carreirasSugeridas")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CarreirasSugeridos(JSONObject inputCarreirasSugeridas)  {
		
		mongo.close();
		return userPerfil.atualizaSugestaoColetiva (inputCarreirasSugeridas, "carreirasSugeridas", mongo);
		
	};	
	
};
