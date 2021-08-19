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
@RequestMapping("/habilidades")

public class Rest_Habilidade {

	MongoClient mongo = new MongoClient();
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Habilidade habilidade = new Habilidade();
	Usuario usuario = new Usuario();

	@GetMapping("/lista")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidades() {
		System.out.println("Metodo deperecate");
		return null;
	};

	@GetMapping("/cursos")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterHabilidade(@RequestParam("token") String token, @RequestParam("habilidadeId") String habilidadeId, @RequestParam("usuarioId") String usuarioId) {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (habilidadeId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray results =  habilidade.getCurso(usuarioId, habilidadeId, mongo);
		mongo.close();
		return results;

	};
	
	@GetMapping("/filtros")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray filtros(@RequestParam("token") String token, 
							@RequestParam("areasConhecimento") String areaConhecimento,
							@RequestParam("usuarioParametro") String usuarioParametro,
							@RequestParam("limite") int limite,
							@RequestParam("start") int start)  {
	
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};

		if (limite == 0) {
			limite = 999999999;
		};
		
		JSONArray result = commons.controlaLimite(habilidade.filtros(areaConhecimento, usuarioParametro, mongo),limite, start);
		mongo.close();
		return result;
	};

}

