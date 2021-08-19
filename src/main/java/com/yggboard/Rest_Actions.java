package com.yggboard;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


// @Lock(LockType.READ)
@RestController
@RequestMapping("/actions")
public class Rest_Actions {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Actions actions = new Actions();

	@GetMapping("/set")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject obter(@RequestParam("token") String token,
													@RequestParam("action") String action,
													@RequestParam("objeto") String objeto ,
													@RequestParam("usuario") String usuarioPar,
													@RequestParam("id") String ids,
													@RequestParam("atualizacaoDireta") Boolean atualizacaoDireta)
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
	
		BasicDBObject result = actions.processaAction(action, objeto, usuarioPar, ids,atualizacaoDireta, mongo);
		mongo.close();
		return result;
	};
	
};
