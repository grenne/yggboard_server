package com.yggboard;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;


// @Lock(LockType.READ)
@RestController
@RequestMapping("/userPerfil")

public class Rest_UserPerfil {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	UserPerfil userPerfil = new UserPerfil();
	@GetMapping("/obter")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterUsuario(@RequestParam("usuario") String usuario, @RequestParam("usuarioConsultaId") String usuarioConsultaId){
		Commons_DB commons_db = new Commons_DB();
		return commons_db.getCollection(usuario, "userPerfil", "documento.token", mongo, true);
	};

	@GetMapping("/obter-estatistica")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterEstatistica(@RequestParam("token") String token, @RequestParam("id") String id, @RequestParam("item") String item){
	
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
	
	@GetMapping("/obter/itens")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCarreiras(@RequestParam("usuario") String usuario, @RequestParam("usuarioConsultaId") String usuarioConsultaId, @RequestParam("userPerfilConsultaId") String userPerfilConsultaId, @RequestParam("item") String item, @RequestParam("elemento") String elemento){
		
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

	@PostMapping("/atualizar/perfil")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AtualizarPerfil(JSONObject newPerfil){
		System.out.println("Método deprecate");
		return null;
	};
	
	@PostMapping("/cursosSugeridos")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CursosSugeridos(JSONObject inputCursosSugeridos)  {
				
		mongo.close();
		return userPerfil.atualizaSugestaoColetiva (inputCursosSugeridos, "cursosSugeridos", mongo);
		
	};
	@PostMapping("/carreirasSugeridas")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CarreirasSugeridos(JSONObject inputCarreirasSugeridas)  {
		
		mongo.close();
		return userPerfil.atualizaSugestaoColetiva (inputCarreirasSugeridas, "carreirasSugeridas", mongo);
		
	};	
	
};
