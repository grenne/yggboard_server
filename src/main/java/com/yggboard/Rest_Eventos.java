package com.yggboard;


import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


//@Lock(LockType.READ)
@RestController
@RequestMapping("/evento")

public class Rest_Eventos {

	MongoClient mongo = new MongoClient();
	
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Eventos eventos = new Eventos();

	@GetMapping("/feed")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@RequestParam("token") String token, @RequestParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (usuarioId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = eventos.feed(usuarioId, mongo);
		mongo.close();
		return result;
	};
	
};
