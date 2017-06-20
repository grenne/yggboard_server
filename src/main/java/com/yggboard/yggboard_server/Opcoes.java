package com.yggboard.yggboard_server;

public class Opcoes {
	
	private Boolean filtro;
	private Boolean carregaPreRequisitos;
	private Boolean carregaCursosPreRequisitos;
	private Boolean carregaObjetivosPreRequisitos;
	private Boolean carregaAreasConhecimentoPreRequisitos;
	private Boolean carregaAreasAtuacaoObjetivos;
	private Boolean carregaHabilidadesObjetivos;
	private Boolean carregaHabilidadesCursos;
	private Boolean carregaHabilidadesAreaConhecimento;
	private Boolean carregaAreaConhecimentoHabilidades;
	private Boolean carregaCursosHabilidades;
	private Boolean carregaObjetivosHabilidades;
	private Boolean carregaObjetivosAreasAtuacao;

	public Opcoes() {
		filtro = false;
		carregaPreRequisitos = false;
		carregaCursosPreRequisitos = false;
		carregaObjetivosPreRequisitos = false;
		carregaAreasConhecimentoPreRequisitos = false;
		carregaAreasAtuacaoObjetivos = false;
		carregaHabilidadesObjetivos = false;
		carregaHabilidadesCursos = false;
		carregaHabilidadesAreaConhecimento = false;
		carregaAreaConhecimentoHabilidades = false;
		carregaCursosHabilidades = false;
		carregaObjetivosHabilidades = false;
		carregaObjetivosAreasAtuacao = false;
	};

	public Opcoes(
			Boolean filtro,
			Boolean carregaPreRequisitos,
			Boolean carregaCursosPreRequisitos,
			Boolean carregaObjetivosPreRequisitos,
			Boolean carregaAreasConhecimentoPreRequisitos,
			Boolean carregaAreasAtuacaoObjetivos,
			Boolean carregaHabilidadesObjetivos,
			Boolean carregaHabilidadesCursos,
			Boolean carregaHabilidadesAreaConhecimento,
			Boolean carregaAreaConhecimentoHabilidades,
			Boolean carregaCursosHabilidades,
			Boolean carregaObjetivosHabilidades,
			Boolean carregaObjetivosAreasAtuacao		

			) {
		this.filtro = filtro;
		this.carregaPreRequisitos = carregaPreRequisitos;
		this.carregaCursosPreRequisitos = carregaCursosPreRequisitos;
		this.carregaObjetivosPreRequisitos = carregaObjetivosPreRequisitos;
		this.carregaAreasConhecimentoPreRequisitos = carregaAreasConhecimentoPreRequisitos;
		this.carregaAreasAtuacaoObjetivos = carregaAreasAtuacaoObjetivos;
		this.carregaHabilidadesObjetivos = carregaHabilidadesObjetivos;
		this.carregaHabilidadesCursos = carregaHabilidadesCursos;
		this.carregaHabilidadesAreaConhecimento = carregaHabilidadesAreaConhecimento;
		this.carregaAreaConhecimentoHabilidades = carregaAreaConhecimentoHabilidades;
		this.carregaCursosHabilidades = carregaCursosHabilidades;
		this.carregaObjetivosHabilidades = carregaObjetivosHabilidades;		
		this.carregaObjetivosAreasAtuacao = carregaObjetivosAreasAtuacao;		
	};
	
	public void setFiltro(Boolean filtro) {
		this.filtro = filtro;
	};
	public Boolean filtro() {
		return this.filtro;
	};
	
	public void setCarregaPreRequisitos(Boolean carregaPreRequisitos) {
		this.carregaPreRequisitos = carregaPreRequisitos;
	};
	public Boolean carregaPreRequisitos() {
		return this.carregaPreRequisitos;
	};
	
	public void setCarregaCursosPreRequisitos(Boolean carregaCursosPreRequisitos) {
		this.carregaCursosPreRequisitos = carregaCursosPreRequisitos;
	};
	public Boolean carregaCursosPreRequisitos() {
		return this.carregaCursosPreRequisitos;
	};
	
	public void setCarregaObjetivosPreRequisitos(Boolean carregaObjetivosPreRequisitos) {
		this.carregaObjetivosPreRequisitos = carregaObjetivosPreRequisitos;
	};
	public Boolean carregaObjetivosPreRequisitos() {
		return this.carregaObjetivosPreRequisitos;
	};
	
	public void setCarregaAreasConhecimentoPreRequisitos(Boolean carregaAreasConhecimentoPreRequisitos) {
		this.carregaAreasConhecimentoPreRequisitos = carregaAreasConhecimentoPreRequisitos;
	};
	public Boolean carregaAreasConhecimentoPreRequisitos() {
		return this.carregaAreasConhecimentoPreRequisitos;
	};
	
	public void setCarregaAreasAtuacaoObjetivos(Boolean carregaAreasAtuacaoObjetivos) {
		this.carregaAreasAtuacaoObjetivos = carregaAreasAtuacaoObjetivos;
	};
	public Boolean carregaAreasAtuacaoObjetivos() {
		return this.carregaAreasAtuacaoObjetivos;
	};
	
	public void setCarregaHabilidadesObjetivos(Boolean carregaHabilidadesObjetivos) {
		this.carregaHabilidadesObjetivos = carregaHabilidadesObjetivos;
	};
	public Boolean carregaHabilidadesObjetivos() {
		return this.carregaHabilidadesObjetivos;
	};
	
	public void setCarregaHabilidadesCursos(Boolean carregaHabilidadesCursos) {
		this.carregaHabilidadesCursos = carregaHabilidadesCursos;
	};
	public Boolean carregaHabilidadesCursos() {
		return this.carregaHabilidadesCursos;
	};
	
	public void setCarregaHabilidadesAreaConhecimento(Boolean carregaHabilidadesAreaConhecimento) {
		this.carregaHabilidadesAreaConhecimento = carregaHabilidadesAreaConhecimento;
	};
	public Boolean carregaHabilidadesAreaConhecimento() {
		return this.carregaHabilidadesAreaConhecimento;
	};
	
	public void setCarregaAreaConhecimentoHabilidades(Boolean carregaAreaConhecimentoHabilidades) {
		this.carregaAreaConhecimentoHabilidades = carregaAreaConhecimentoHabilidades;
	};
	public Boolean carregaAreaConhecimentoHabilidades() {
		return this.carregaAreaConhecimentoHabilidades;
	};
	
	public void setCarregaCursosHabilidades(Boolean carregaCursosHabilidades) {
		this.carregaCursosHabilidades = carregaCursosHabilidades;
	};
	public Boolean carregaCursosHabilidades() {
		return this.carregaCursosHabilidades;
	};
	
	public void setCarregaObjetivosHabilidades(Boolean carregaObjetivosHabilidades) {
		this.carregaObjetivosHabilidades = carregaObjetivosHabilidades;
	};
	public Boolean carregaObjetivosHabilidades() {
		return this.carregaObjetivosHabilidades;
	};
	
	public void setCarregaObjetivosAreasAtuacao(Boolean carregaObjetivosAreasAtuacao) {
		this.carregaObjetivosAreasAtuacao = carregaObjetivosAreasAtuacao;
	};
	public Boolean carregaObjetivosAreasAtuacao() {
		return this.carregaObjetivosAreasAtuacao;
	};

};