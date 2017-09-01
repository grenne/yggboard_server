package com.yggboard.yggboard_server;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

@Singleton
//@Lock(LockType.READ)
@Path("/avaliacao")

public class Rest_Avaliacao {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Avaliacao avaliacao = new Avaliacao();

	@Path("/cria/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject criaMapa(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
	
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		
		return avaliacao.criaMapaAvaliacao(empresaId, avaliacaoId);
		
	};
	@Path("/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject Mapa(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null) {
			return null;
		};
		
		return avaliacao.mapa(usuarioId, empresaId, avaliacaoId);
	};

	@Path("/colaboradores")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("usuarioId") String usuarioId, @QueryParam("perfil") String perfil)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null) {
			return null;
		};
		if (avaliacaoId == null) {
			return null;
		};
		
		return avaliacao.colaboradores(empresaId, avaliacaoId, usuarioId, perfil);
	};
	
	@Path("/inout")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapa(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("colaboradorObjetoId") String colaboradorObjetoId, @QueryParam("assunto") String assunto, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (colaboradorId == null){
			return false;
		};
		if (colaboradorObjetoId == null){
			return false;
		};
		
		return avaliacao.montaMapa(colaboradorId, colaboradorObjetoId, assunto, empresaId, avaliacaoId);
	};
	
	@Path("/inout/cliente")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapaCliente(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("colaboradorObjetoId") String colaboradorObjetoId, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("status") String status)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (colaboradorId == null){
			return false;
		};
		if (colaboradorObjetoId == null){
			return false;
		};
		
		return avaliacao.montaMapaCliente(colaboradorId, colaboradorObjetoId, empresaId, avaliacaoId, status);
	};
	
	@Path("/inout/habilidade")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaMapaHabilidade(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (usuarioId == null){
			return false;
		};
		if (habilidadeId == null){
			return false;
		};
		
		return avaliacao.montaHabilidades(usuarioId, empresaId, avaliacaoId, habilidadeId);
	};
	
	@Path("/atualiza/nota")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean AtualizaNota(@QueryParam("token") String token, @QueryParam("colaboradorId") String colaboradorId, @QueryParam("avaliadorId") String avaliadorId, @QueryParam("habilidadeId") String habilidadeId, @QueryParam("nota") String nota, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return false;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return false;
		};
		if (colaboradorId == null){
			return false;
		};
		if (habilidadeId == null){
			return false;
		};
		if (nota == null){
			return false;
		};
		if (avaliadorId == null){
			return false;
		};

		return avaliacao.atualizaNota(avaliadorId, colaboradorId, habilidadeId, nota, avaliacaoId);
	};
	
	@Path("/avaliacoes")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Avaliacoes(@QueryParam("token") String token, @QueryParam("avaliadorId") String avaliadorId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (avaliadorId == null){
			return null;
		};
		if (avaliacaoId == null){
			return null;
		};

		return avaliacao.carregaAvaliados(avaliadorId, avaliacaoId);
	};
	
	@Path("/resultados")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject Resultados(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (usuarioId == null){
			return null;
		};
		if (avaliacaoId == null){
			return null;
		};

		return avaliacao.resultados(usuarioId, avaliacaoId);
	};

	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Object> Lista(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("usuarioId") String usuarioId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null){
			return null;
		};

		return avaliacao.lista(empresaId, usuarioId);
	};

	@Path("/fecha/mapa")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject FechaMapa(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId, @QueryParam("usuarioId") String usuarioId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null){
			return null;
		};
		if (avaliacaoId == null){
			return null;
		};
		if (usuarioId == null){
			return null;
		};

		return avaliacao.fechaMapa(empresaId, avaliacaoId, usuarioId);
	};

	@Path("/estatistica")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject EstatisticaMapa(@QueryParam("token") String token, @QueryParam("empresaId") String empresaId, @QueryParam("avaliacaoId") String avaliacaoId)  {
		if (token == null) {
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token")) == null) {
			return null;
		};
		if (empresaId == null){
			return null;
		};
		if (avaliacaoId == null){
			return null;
		};
		return avaliacao.estatisticaMapa(empresaId, avaliacaoId);
	};
	
};
