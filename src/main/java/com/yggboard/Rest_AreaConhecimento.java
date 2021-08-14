package com.yggboard;


import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


// @Lock(LockType.READ)
@RestController
@RequestMapping("/areaConhecimento")
public class Rest_AreaConhecimento {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	UserPerfil userPerfil = new UserPerfil();
	AreaConhecimento areaConhecimento = new AreaConhecimento();

	@GetMapping("/lista/primarias")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listaPrimarias(@RequestParam("token") String token)
	{
		
	System.out.println("actions");
	if (token == null) {
		System.out.println("token não informado");
		mongo.close();
		return null;
	};
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	
	JSONArray result = areaConhecimento.primarias(mongo);
	mongo.close();
	return result;
	};

	@SuppressWarnings({ })
	@GetMapping("/lista/parents")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listaParents(@RequestParam("token") String token,@RequestParam("primarias") String primarias)
	{
		
	System.out.println("actions");
	if (primarias == null) {
		System.out.println("primarias não informado");
		mongo.close();
		return null;
	};
	if (token == null) {
		System.out.println("token não informado");
		mongo.close();
		return null;
	};
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	
	JSONArray result = areaConhecimento.parents(primarias, mongo);
	mongo.close();
	return result;
	};
	
};
