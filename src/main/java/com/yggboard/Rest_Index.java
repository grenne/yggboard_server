package com.yggboard;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

//
// @Lock(LockType.READ)
//@GetMapping("/index")

@RestController
@RequestMapping("/index")
public class Rest_Index {

	MongoClient mongo = new MongoClient();

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();

//	@GetMapping("/obter/itens")
//	
	@Produces(MediaType.APPLICATION_JSON)
	@GetMapping("/obter/itens")
	public BasicDBObject ObterItens(@RequestParam("assunto") String assunto, @RequestParam("id") String id,
									@RequestParam("usuario") String usuario, @RequestParam("empresaId") String empresaId) {

		System.out.println("Metodo deperecate");
		return null;
	};

	@PostMapping("/obter/filtro")
	@Consumes(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterItensFiltro(JSONArray objFiltros) throws  IOException {

		System.out.println("Metodo deperecate");
		return null;
	};

	@GetMapping("/lista")
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ObterLista(@RequestParam("characters") String characters,
			@RequestParam("planejamentoLista") String planejamentoLista, @RequestParam("usuario") String usuario,
			@RequestParam("empresaId") String empresaId) {

		System.out.println("Metodo deperecate");
		return null;
	};

};
