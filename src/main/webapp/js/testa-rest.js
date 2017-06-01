/**
 * 
 */

function testaObter() {
	var objJson = {
		collection : "usuarios",
		keys : [ {
			key : "documento.email",
			value : "email a ser cadastrado"
		} ]
	};
	$.ajax({
		type : "POST",
		url : "http://localhost:8080/yggboard_server/rest/crud/obter",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		async : true

	}).done(function(data) {
		console.log("done: " + data);
	}).fail(function(data) {
		console.log("fail: " + data);
	}).always(function(data) {
		console.log("always: " + data);
	});
};
function testaIncluir() {
	var objJson = {
		collection : "usuario",
		insert : {
			documento : {
				email : "1",
				nome : "nome",
				password : "pw"

			}
		}
	};
	$.ajax({
		type : "POST",
		url : "http://localhost:8080/yggboard_server/rest/crud/incluir",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		async : false

	}).done(function(data) {
		console.log("done: " + data);
	}).fail(function(data) {
		console.log("fail: " + data);
	}).always(function(data) {
		console.log("always: " + data);
	});
};
function testaAtualizar() {

	var objJson = {
		collection : "teste",
		keys : [ {
			key : "documento.id",
			value : "1"
		} ],
		update : [ {
			field : "documento.nome",
			value : "super novo"
		} ]
	};
	$.ajax({
		type : "POST",
		url : "http://localhost:8080/yggboard_server/rest/crud/atualizar",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		async : false

	}).done(function(data) {
		console.log("done: " + data);
	}).fail(function(data) {
		console.log("fail: " + data);
	}).always(function(data) {
		console.log("always: " + data);
	});
};

function testaLista() {

	var objJson = {
		collection : "cursos",
		keys : []
	};
	$.ajax({
		type : "POST",
		url : "http://localhost:8080/yggboard_server/rest/crud/lista",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		async : false

	}).done(function(data) {
		console.log("done: " + data);
	}).fail(function(data) {
		console.log("fail: " + data);
	}).always(function(data) {
		console.log("always: " + data);
	});
};

function testaAtualizaPerfil() {

	var tipo = prompt("Tipo");
	var inout = prompt("Inout");
	var id = prompt("Id");
	var objJson = {
		usuario : "grenneglr@gmail.com",
		tipo : tipo,
		id : id,
		inout : inout
	};
	$
			.ajax(
					{
						type : "POST",
						url : "http://localhost:8080/yggboard_server/rest/userPerfil/atualizar/perfil",
						contentType : "application/json; charset=utf-8",
						dataType : 'json',
						data : JSON.stringify(objJson),
						global : false,
						async : false

					}).done(function(data) {
			}).fail(function(data) {
			}).always(function(data) {
				if (data.status == 200) {
					console.log("atualizou")
				} else {
					console.log("deu pau")
				}
				;
			});
};

function testaFiltro() {

	var assunto = prompt("Assunto");
	var id = prompt("Id");
	var objJson = [ {
		assunto : assunto,
		id : id
	} ];
	$.ajax({
		type : "POST",
		url : "http://localhost:8080/yggboard_server/rest/index/obter/filtro",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		global : false,
		async : false
	}).done(function(data) {
		if (data) {
			console.log("ok");
		}
		;
	}).fail(function(data) {
	}).always(function(data) {
	});
};

function atualizaPreRequisitosProcess(habilidades) {

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$.each(habilidades, function(i, habilidade) {
		// habilidade = obterDependencias(habilidade, habilidade.documento.id,
		// "habilidades", 1);
		console.log("id - " + habilidade.documento.id);
		var cursos = [];
		$.each(habilidade.documento.cursos, function(i, curso) {
			cursos.push(curso.idCurso);
		});
		delete habilidade.documento["cursos"];
		habilidade.documento.cursos = cursos;
		var objJson = {
			collection : "habilidade",
			keys : [ {
				key : "documento.id",
				value : habilidade.documento.id
			} ],
			update : [ {
				field : "documento",
				value : habilidade
			} ]
		};
		rest_atualizar(objJson, semAcao, semAcao);
	});
	console.log("terminou pre reuisitos");
};

function atualizaObjetivos() {

	lerObjetivos();

};

function lerObjetivos() {

	rest_obterCarreiras(atualizaObjetivosProcess, semAcao);
};

function atualizaObjetivosProcess(objetivos) {

	localStorage.setItem("objetivos", JSON.stringify(objetivos));

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$.each(habilidades, function(i, habilidade) {
		habilidade = obterObjetivos(habilidade, habilidade.documento.id);
		console.log("id - " + habilidade.documento.id);
		var objJson = {
			collection : "habilidade",
			keys : [ {
				key : "documento.id",
				value : habilidade.documento.id
			} ],
			update : [ {
				field : "documento",
				value : habilidade
			} ]
		};
		rest_atualizar(objJson, semAcao, semAcao);
	});
	console.log("terminou objetivos");
};

function obterObjetivos(objJson, habilidadeTarget) {

	objetivos = JSON.parse(localStorage.getItem("objetivos"));

	objJson.documento.objetivos = [];

	$.each(objetivos, function(i, objetivo) {
		$.each(objetivo.necessarios, function(i, habilidade) {
			if (habilidade == objJson.documento.id) {
				var existente = false;
				$.each(objJson.documento.objetivos, function(i,
						objetivoCarregado) {
					if (objetivoCarregado == objetivo.nome) {
						existente = true;
					}
					;
				});
				if (!existente) {
					objJson.documento.objetivos.push(objetivo.nome);
				}
				;
			}
			;
		});
	});

	return objJson;
};

function atualizaBadges() {

	rest_obterHabilidades(lerBadges, semAcao, "false");

};

function lerBadges(habilidades) {

	rest_obterBadges(atualizaBadgesProcess, semAcao);
};

function atualizaBadgesProcess(badges) {

	localStorage.setItem("badges", JSON.stringify(badges));

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$.each(habilidades, function(i, habilidade) {
		habilidade = obterBadges(habilidade, habilidade.documento.id);
		console.log("id - " + habilidade.documento.id);
		var objJson = {
			collection : "habilidade",
			keys : [ {
				key : "documento.id",
				value : habilidade.documento.id
			} ],
			update : [ {
				field : "documento",
				value : habilidade
			} ]
		};
		rest_atualizar(objJson, semAcao, semAcao);
	});
	console.log("terminou badges");
};

function obterBadges(objJson, habilidadeTarget) {

	badges = JSON.parse(localStorage.getItem("badges"));

	objJson.documento.badges = [];

	$.each(badges, function(i, badge) {
		$.each(badge.habilidades, function(i, habilidade) {
			if (habilidade == objJson.documento.id) {
				var existente = false;
				$.each(objJson.documento.objetivos,
						function(i, badgeCarregado) {
							if (badgeCarregado == badge.id) {
								existente = true;
							}
							;
						});
				if (!existente) {
					objJson.documento.badges.push(badge.id);
				}
				;
			}
			;
		});
	});

	return objJson;
};

function obterDependencias(objJson, habilidadeTarget, tipo, nivel) {

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$
			.each(
					habilidades,
					function(i, habilidadeSource) {
						if (habilidadeSource.documento.target == habilidadeTarget) {
							if (tipo == "necessarios") {
								var existente = false;
								$
										.each(
												objJson.documento.necessarios,
												function(i, habilidade) {
													if (habilidadeSource.documento.source == habilidade) {
														existente = true;
													}
													;
												});
								if (!existente) {
									objJson.documento.necessarios
											.push(habilidadeSource.documento.source);
									objJson = obterDependencias(objJson,
											habilidadeSource.documento.source,
											tipo);
								}
								;
							}
							;
							if (tipo == "recomendados") {
								var existente = false;
								$
										.each(
												objJson.documento.recomendadas,
												function(i, habilidade) {
													if (habilidadeSource.documento.source == habilidade) {
														existente = true;
													}
													;
												});
								if (!existente) {
									objJson.documento.recomendados
											.push(habilidadeSource.documento.source);
									objJson = obterDependencias(objJson,
											habilidadeSource.documento.source,
											tipo);
								}
								;
							}
							;
							if (tipo == "habilidades") {
								var existente = false;
								$
										.each(
												objJson.documento.preRequisitos,
												function(i, habilidade) {
													if (habilidadeSource.documento.source == habilidade) {
														existente = true;
													}
													;
												});
								if (!existente) {
									var preRequisitos = {
										id : habilidadeSource.documento.source,
										nivel : nivel
									};
									objJson.documento.preRequisitos
											.push(preRequisitos);
									objJson = obterDependencias(objJson,
											habilidadeSource.documento.source,
											tipo, (parseInt(nivel) + 1));
								}
								;
							}
							;
						}
						;
					});

	return objJson;
};

