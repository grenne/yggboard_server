package com.yggboard;


import java.util.ArrayList;

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
//@Lock(LockType.READ)
@Path("/avaliacao")

public class Rest_Avaliacao {

	MongoClient mongo = new MongoClient();
	
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Avaliacao avaliacao = new Avaliacao();
	Usuario usuario = new Usuario();

	@Path("/cria/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject CriaMapa(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
	
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
	@Path("/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Mapa(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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

	@Path("/colaboradores")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("usuarioId") String usuarioId, @QueryParam("perfil") String perfil)  {
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

	@Path("/colaboradores/ultimas-5-avaliacoes")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ColaboradoresUltimas5(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = avaliacao.colaboradoresUltimas5Avaliacoes(usuarioId, mongo);
		mongo.close();
		return result;
	};

	@Path("/colaboradores/avaliacao")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ColaboradoresAvaliacao(@QueryParam("token") String token,  @QueryParam("avaliacaoId") String avaliacaoId)  {
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

	@Path("/colaboradores/avaliacao/notas")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ColaboradoresAvaliacaoNotas(@QueryParam("token") String token,  @QueryParam("avaliacaoId") String avaliacaoId)  {
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

	@Path("/colaborador")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Colaborador(@QueryParam("token") String token, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("usuarioId") String usuarioId)  {
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
	
	@Path("/inout")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapa(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("colaboradorObjetoId") String colaboradorObjetoId, @QueryParam("assunto") String assunto, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	
	@Path("/inout/cliente")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapaCliente(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("colaboradorObjetoId") String colaboradorObjetoId, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("status") String status)  {
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
	
	@Path("/inout/habilidade")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapaHabilidade(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	
	@Path("/atualiza/nota")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean AtualizaNota(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("avaliadorId") String avaliadorId, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("nota") String nota, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	
	@Path("/avaliacoes")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Avaliacoes(@QueryParam("token") String token, @QueryParam("avaliadorId") String avaliadorId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	
	@Path("/resultados")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Resultados(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	
	@Path("/ultimos/resultados")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject UltimosResultados(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId)  {
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

	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Object> Lista(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("usuarioId") String usuarioId)  {
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

	@Path("/fecha/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject FechaMapa(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("usuarioId") String usuarioId)  {
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

	@Path("/convites")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Convites(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("usuarioId") String usuarioId)  {
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

	@Path("/estatistica")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject EstatisticaMapa(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
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
	@Path("/emails")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Emails(@QueryParam("token") String token)  {
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
	@Path("/importar-historico")
	@POST
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
