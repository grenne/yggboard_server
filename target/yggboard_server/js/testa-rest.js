/**
 * 
 */

function testaObter() {
	var objJson = {
		collection : "usuarios",
		keys : [ {
			key : "documento.email",
			value : "grenneglr@gmail.com"
		} ]
	};
	$.ajax({
		type : "POST",
		url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/obter",
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
		collection : "usuarios",
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
		url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/incluir",
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

	 var doc = {
	        carreirasInteresse : [],
	        habilidadesInteresse : [],
	        cursosInteresse : [],
	        cursosSugeridos : [
	            659
	        ],
	        tags : [],
	        elementos : [],
	        usuario : "grenne@grenne.com",
	        carreiras : [],
	        habilidades : [ 
	 
	            "10417"
	        ],
	        badges : [],
	        badgesInteresse : [],
	        showBadges : [],
	        carreirasSugeridas : [ 
	            12
	        ]
	    };
	 
	 var doc1 =
				 {
				 teste :"ccc",
				 teste2 : "ddd"
				 };
	 
	var objJson = {
		collection : "userPerfil",
		keys : [ {
			key : "documento.usuario",
			value : "grenne@grenne.com"
		} ],
		update : [ {
			field : "grenne",
			value : doc1
		} ]
	};
	$.ajax({
		type : "POST",
		url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/atualizar",
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
		url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/lista",
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
						url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/userPerfil/atualizar/perfil",
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

	var objJson = [];
	var assunto = "";
	while (assunto !=  "fim") {
		assunto = prompt("Assunto");
		if (assunto != "fim"){
			var id = prompt("Id");
			var filtro = {
					assunto : assunto,
					id : id
			};
			objJson.push(filtro);
		};
	};
	$.ajax({
		type : "POST",
		url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/index/obter/filtro",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		global : false,
		async : false
	}).done(function(data) {
		if (data) {
			$.each(data.habilidades, function(i, item) {
				console.log ("habilidade:"  + item.id + " nome:" + item.nome);
			});
			$.each(data.objetivos, function(i, item) {
				console.log ("objetivo:"  + item.id + " nome:" + item.nome);
			});
			$.each(data.cursos, function(i, item) {
				console.log ("curso:"  + item.id + " nome:" + item.nome);
			});
			$.each(data.areaAtuacao, function(i, item) {
				console.log ("area atuação:" + item.id + " nome:" + item.nome);
			});
			$.each(data.areaConhecimento, function(i, item) {
				console.log ("area conheciento:"  + item.id + " nome:" + item.nome);
			});
		}
		;
	}).fail(function(data) {
	}).always(function(data) {
	});
};

function atualizaPreRequisitosProcess(habilidades) {

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$.each(habilidades, function(i, habilidade) {
		// habilidade = obterDependencias(habilidade, habilidade.id,
		// "habilidades", 1);
		console.log("id - " + habilidade.id);
		var cursos = [];
		$.each(habilidade.cursos, function(i, curso) {
			cursos.push(curso.idCurso);
		});
		delete habilidade["cursos"];
		habilidade.cursos = cursos;
		var objJson = {
			collection : "habilidades",
			keys : [ {
				key : "documento.id",
				value : habilidade.id
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
		habilidade = obterObjetivos(habilidade, habilidade.id);
		console.log("id - " + habilidade.id);
		var objJson = {
			collection : "habilidades",
			keys : [ {
				key : "documento.id",
				value : habilidade.id
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

	objJson.objetivos = [];

	$.each(objetivos, function(i, objetivo) {
		$.each(objetivo.necessarios, function(i, habilidade) {
			if (habilidade == objJson.id) {
				var existente = false;
				$.each(objJson.objetivos, function(i,
						objetivoCarregado) {
					if (objetivoCarregado == objetivo.nome) {
						existente = true;
					}
					;
				});
				if (!existente) {
					objJson.objetivos.push(objetivo.nome);
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
		habilidade = obterBadges(habilidade, habilidade.id);
		console.log("id - " + habilidade.id);
		var objJson = {
			collection : "habilidades",
			keys : [ {
				key : "documento.id",
				value : habilidade.id
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

	objJson.badges = [];

	$.each(badges, function(i, badge) {
		$.each(badge.habilidades, function(i, habilidade) {
			if (habilidade == objJson.id) {
				var existente = false;
				$.each(objJson.objetivos,
						function(i, badgeCarregado) {
							if (badgeCarregado == badge.id) {
								existente = true;
							}
							;
						});
				if (!existente) {
					objJson.badges.push(badge.id);
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
						if (habilidadeSource.target == habilidadeTarget) {
							if (tipo == "necessarios") {
								var existente = false;
								$
										.each(
												objJson.necessarios,
												function(i, habilidade) {
													if (habilidadeSource.source == habilidade) {
														existente = true;
													}
													;
												});
								if (!existente) {
									objJson.necessarios
											.push(habilidadeSource.source);
									objJson = obterDependencias(objJson,
											habilidadeSource.source,
											tipo);
								}
								;
							}
							;
							if (tipo == "recomendados") {
								var existente = false;
								$
										.each(
												objJson.recomendadas,
												function(i, habilidade) {
													if (habilidadeSource.source == habilidade) {
														existente = true;
													}
													;
												});
								if (!existente) {
									objJson.recomendados
											.push(habilidadeSource.source);
									objJson = obterDependencias(objJson,
											habilidadeSource.source,
											tipo);
								}
								;
							}
							;
							if (tipo == "habilidades") {
								var existente = false;
								$
										.each(
												objJson.preRequisitos,
												function(i, habilidade) {
													if (habilidadeSource.source == habilidade) {
														existente = true;
													}
													;
												});
								if (!existente) {
									var preRequisitos = {
										id : habilidadeSource.source,
										nivel : nivel
									};
									objJson.preRequisitos
											.push(preRequisitos);
									objJson = obterDependencias(objJson,
											habilidadeSource.source,
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

