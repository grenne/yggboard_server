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
import java.util.ArrayList;


//@Lock(LockType.READ)
@RestController
@RequestMapping("/avaliacao")

public class Rest_Avaliacao {

	MongoClient mongo = new MongoClient();
	
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Avaliacao avaliacao = new Avaliacao();
	Usuario usuario = new Usuario();

	@GetMapping("/cria/mapa")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject CriaMapa(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
	
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		
		JSONObject result = avaliacao.criaMapaAvaliacao(empresaId, avaliacaoId, mongo);
		mongo.close();
		return result;
	};
	@GetMapping("/mapa")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Mapa(@RequestParam("token") String token, @RequestParam("usuarioId") String usuarioId, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
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
		
		BasicDBObject result = avaliacao.mapa(usuarioId, empresaId, avaliacaoId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/colaboradores")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId, @RequestParam("usuarioId") String usuarioId, @RequestParam("perfil") String perfil)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null) {
			mongo.close();
			return null;
		};
		if (avaliacaoId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = avaliacao.colaboradores(empresaId, avaliacaoId, usuarioId, perfil, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/colaboradores/ultimas-5-avaliacoes")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ColaboradoresUltimas5(@RequestParam("token") String token, @RequestParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = avaliacao.colaboradoresUltimas5Avaliacoes(usuarioId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/colaboradores/avaliacao")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ColaboradoresAvaliacao(@RequestParam("token") String token,  @RequestParam("avaliacaoId") String avaliacaoId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (avaliacaoId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = avaliacao.colaboradoresAvaliacao(avaliacaoId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/colaboradores/avaliacao/notas")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ColaboradoresAvaliacaoNotas(@RequestParam("token") String token,  @RequestParam("avaliacaoId") String avaliacaoId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (avaliacaoId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = avaliacao.colaboradoresAvaliacaoNotas(avaliacaoId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/colaborador")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Colaborador(@RequestParam("token") String token, @RequestParam("avaliacaoId") String avaliacaoId, @RequestParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (usuarioId == null) {
			mongo.close();
			return null;
		};
		if (avaliacaoId == null) {
			mongo.close();
			return null;
		};

		BasicDBObject result = avaliacao.colaborador(avaliacaoId, usuarioId, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/inout")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapa(@RequestParam("token") String token, @RequestParam("colaboradorId") String colaboradorId, @RequestParam("colaboradorObjetoId") String colaboradorObjetoId, @RequestParam("assunto") String assunto, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (colaboradorId == null){
			mongo.close();
			return false;
		};
		if (colaboradorObjetoId == null){
			mongo.close();
			return false;
		};

		Boolean result = avaliacao.montaMapa(colaboradorId, colaboradorObjetoId, assunto, empresaId, avaliacaoId, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/inout/cliente")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapaCliente(@RequestParam("token") String token, @RequestParam("colaboradorId") String colaboradorId, @RequestParam("colaboradorObjetoId") String colaboradorObjetoId, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId, @RequestParam("status") String status)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (colaboradorId == null){
			mongo.close();
			return false;
		};
		if (colaboradorObjetoId == null){
			mongo.close();
			return false;
		};

		Boolean result = avaliacao.montaMapaCliente(colaboradorId, colaboradorObjetoId, empresaId, avaliacaoId, status, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/inout/habilidade")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapaHabilidade(@RequestParam("token") String token, @RequestParam("usuarioId") String usuarioId, @RequestParam("habilidadeId") String habilidadeId, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (usuarioId == null){
			mongo.close();
			return false;
		};
		if (habilidadeId == null){
			mongo.close();
			return false;
		};

		Boolean result = avaliacao.montaHabilidades(usuarioId, empresaId, avaliacaoId, habilidadeId, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/atualiza/nota")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean AtualizaNota(@RequestParam("token") String token, @RequestParam("colaboradorId") String colaboradorId, @RequestParam("avaliadorId") String avaliadorId, @RequestParam("habilidadeId") String habilidadeId, @RequestParam("nota") String nota, @RequestParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return false;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return false;
		};
		if (colaboradorId == null){
			mongo.close();
			return false;
		};
		if (habilidadeId == null){
			mongo.close();
			return false;
		};
		if (nota == null){
			mongo.close();
			return false;
		};
		if (avaliadorId == null){
			mongo.close();
			return false;
		};

		Boolean result = avaliacao.atualizaNota(avaliadorId, colaboradorId, habilidadeId, nota, avaliacaoId, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/avaliacoes")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Avaliacoes(@RequestParam("token") String token, @RequestParam("avaliadorId") String avaliadorId, @RequestParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (avaliadorId == null){
			mongo.close();
			return null;
		};
		if (avaliacaoId == null){
			mongo.close();
			return null;
		};

		JSONArray result = avaliacao.carregaAvaliados(avaliadorId, avaliacaoId, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/resultados")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Resultados(@RequestParam("token") String token, @RequestParam("usuarioId") String usuarioId, @RequestParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (usuarioId == null){
			mongo.close();
			return null;
		};
		if (avaliacaoId == null){
			mongo.close();
			return null;
		};

		JSONObject result = avaliacao.resultados(usuarioId, avaliacaoId, mongo);
		mongo.close();
		return result;
	};
	
	@GetMapping("/ultimos/resultados")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject UltimosResultados(@RequestParam("token") String token, @RequestParam("usuarioId") String usuarioId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (usuarioId == null){
			mongo.close();
			return null;
		};

		JSONObject result = avaliacao.ultimosResultados(usuarioId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/lista")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Object> Lista(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("usuarioId") String usuarioId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};

		ArrayList<Object> result = avaliacao.lista(empresaId, usuarioId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/fecha/mapa")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject FechaMapa(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId, @RequestParam("usuarioId") String usuarioId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		if (avaliacaoId == null){
			mongo.close();
			return null;
		};
		if (usuarioId == null){
			mongo.close();
			return null;
		};

		JSONObject result = avaliacao.fechaMapa(empresaId, avaliacaoId, usuarioId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/convites")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Convites(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId, @RequestParam("usuarioId") String usuarioId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		if (avaliacaoId == null){
			mongo.close();
			return null;
		};
		if (usuarioId == null){
			mongo.close();
			return null;
		};

		JSONObject result = avaliacao.convites(empresaId, avaliacaoId, usuarioId, mongo);
		mongo.close();
		return result;
	};

	@GetMapping("/estatistica")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject EstatisticaMapa(@RequestParam("token") String token, @RequestParam("empresaId") String empresaId, @RequestParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (empresaId == null){
			mongo.close();
			return null;
		};
		if (avaliacaoId == null){
			mongo.close();
			return null;
		};

		JSONObject result = avaliacao.estatisticaMapa(empresaId, avaliacaoId, mongo);
		mongo.close();
		return result;
	};

	@SuppressWarnings("unchecked")
	@GetMapping("/emails")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Emails(@RequestParam("token") String token)  {
		JSONObject result = new JSONObject();
		if (token == null) {
			mongo.close();
			result.put("Resultado", "Não informado token");
			return result;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			result.put("Resultado", "Token inválido");
			return result;
		};

		result = avaliacao.emailsFechamento(mongo);
		mongo.close();
		return result;
	};
	
	@SuppressWarnings({ })
	@PostMapping("/importar-historico")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response importaHistorico(BasicDBObject historicosJson)  {

		if (historicosJson.get("token") == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(historicosJson.get("token").toString(), "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (historicosJson.get("empresaId") == null) {
			mongo.close();
			return null;
		};
		System.out.println("historicos - " + historicosJson.toString());
		
		avaliacao.criaHistorico(historicosJson, mongo);

		mongo.close();
		return Response.status(200).entity(true).build();	
	
	}
	
	
};
