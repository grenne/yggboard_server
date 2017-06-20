package com.yggboard.yggboard_server;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;

public class Listas {
	
	Commons commons = new Commons();
	
	private JSONArray objetivos;
	private JSONArray habilidades;
	private JSONArray cursos;
	private JSONArray areasAtuacao;
	private JSONArray areasConhecimento;
	private JSONArray elementosFiltro;
	private JSONArray habilidadesCarregadas;

	public Listas() {
		objetivos = new JSONArray();
		habilidades =  new JSONArray();
		cursos =  new JSONArray();
		areasAtuacao =  new JSONArray();
		areasConhecimento =  new JSONArray();
		elementosFiltro =  new JSONArray();
		habilidadesCarregadas =  new JSONArray();
	};

	public Listas(
			JSONArray objetivos,
			JSONArray habilidades,
			JSONArray cursos,
			JSONArray areasAtuacao,
			JSONArray areasConhecimento,
			JSONArray elementosFiltro,
			JSONArray habilidadesCarregadas
			) {
		this.objetivos = objetivos;
		this.habilidades = habilidades;
		this.cursos = cursos;
		this.areasAtuacao = areasAtuacao;
		this.areasConhecimento = areasConhecimento;
		this.elementosFiltro = elementosFiltro;
		this.habilidadesCarregadas = habilidadesCarregadas;
	};
	
	public void setObjetivos(JSONArray objetivos) {
		this.objetivos = objetivos;
	};
	public JSONArray addObjetivos(BasicDBObject objetivo) {
		return commons.addObjeto(this.objetivos, objetivo);
	};
	public JSONArray objetivos() {
		return this.objetivos;
	};
	
	public void setHabilidades(JSONArray habilidades) {
		this.habilidades = habilidades;
	};
	public JSONArray addHabilidades(BasicDBObject habilidade) {
		return commons.addObjeto(this.habilidades, habilidade);
	};
	public JSONArray habilidades() {
		return this.habilidades;
	};
	
	public void setCursos(JSONArray cursos) {
		this.cursos = cursos;
	};
	public JSONArray addCursos(BasicDBObject curso) {
		return commons.addObjeto(this.cursos, curso);
	};
	public JSONArray cursos() {
		return this.cursos;
	};
	
	public void setAreasAtuacao(JSONArray areasAtuacao) {
		this.areasAtuacao = areasAtuacao;
	};
	public JSONArray addAreasAtuacao(BasicDBObject areaAtuacao) {
		return commons.addObjeto(this.areasAtuacao, areaAtuacao);
	};
	public JSONArray areasAtuacao() {
		return this.areasAtuacao;
	};
	
	public void setAreasConhecimento(JSONArray areasConhecimento) {
		this.areasConhecimento = areasConhecimento;
	};
	public JSONArray addAreasConhecimento(BasicDBObject areaConhecimento) {
		return commons.addObjeto(this.areasConhecimento, areaConhecimento);
	};
	public JSONArray areasConhecimento() {
		return this.areasConhecimento;
	};
	
	public void setElementosFiltro(JSONArray elementosFiltro) {
		this.elementosFiltro = elementosFiltro;
	};
	public JSONArray addElementosFiltro(BasicDBObject elementoFiltro) {
		return commons.addObjeto(this.elementosFiltro, elementoFiltro);
	};
	public JSONArray elementosFiltro() {
		return this.elementosFiltro;
	};
	
	public void setHabilidadesCarregadas(JSONArray habilidadesCarregadas) {
		this.habilidadesCarregadas = habilidadesCarregadas;
	};
	public JSONArray addHabilidadesCarregadas(BasicDBObject preRequisito) {
		return commons.addObjeto(this.habilidadesCarregadas, preRequisito);
	};
	public JSONArray habilidadesCarregadas() {
		return this.habilidadesCarregadas;
	};

};