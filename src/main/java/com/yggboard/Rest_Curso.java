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
@RequestMapping("/cursos")

public class Rest_Curso {

	MongoClient mongo = new MongoClient();
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Curso curso = new Curso();

	@GetMapping("/filtros")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray filtros(@RequestParam("token") String token,
							@RequestParam("areasConhecimento") String areasConhecimento, 
							@RequestParam("niveis") String niveis,  
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
		
		JSONArray result = commons.controlaLimite(curso.filtros(areasConhecimento, niveis, usuarioParametro, mongo),limite, start);
		mongo.close();
		return result;
	};
	
	@GetMapping("/habilidades")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray filtros(@RequestParam("token") String token, 
							@RequestParam("cursoId") String cursoId,
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
		
		JSONArray result = commons.controlaLimite(curso.getHabilidades(cursoId, usuarioParametro, "habilidades", "0", mongo), limite, start);
		mongo.close();
		return result;
	};
	
	
	@GetMapping("/obter")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCurso(@RequestParam("mail") String id)  {
		System.out.println("Metodo deperecate");
		return null;
	};

	@GetMapping("/lista")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCursos() {
		System.out.println("Metodo deperecate");
		return null;
	};

}
