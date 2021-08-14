package com.yggboard;


import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


// @Lock(LockType.READ)
@RestController
@RequestMapping("/badges")

public class Rest_Badge {

	MongoClient  mongo = new MongoClient();
	
	@GetMapping("/obter")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterBadge(@RequestParam("id") String id)  {
		System.out.println("Metodo deperecate");
		return null;
	};
	
@GetMapping("/lista")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterBadges() {
		System.out.println("Metodo deperecate");
		return null;
	};
};
