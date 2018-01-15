/**
 * 
 */

function testaCriarHierarquia() {
	var objJson = {
		token: sessionStorage.token,
		
		empresaId : "598204e50c220b4b24c369ff",
		colaboradores : 
			[ 
			{
				email : "cola01@gmail.com",
				firstName : "cola01",
				lastName : "cola01",
				login : "mudar@123",
				objetivo : "2",
				area : "ara01",
				superior : ""
			} ,
			{
				email : "cola02@gmail.com",
				firstName : "cola02",
				lastName : "cola02",
				login : "mudar@123",
				objetivo : "2",
				area : "ara02",
				superior : "cola01@gmail.com"
			},
			{
				email : "cola03@gmail.com",
				firstName : "cola03",
				lastName : "cola03",
				login : "mudar@123",
				objetivo : "2",
				area : "ara02",
				superior : "cola01@gmail.com"
			},
			{
				email : "cola04@gmail.com",
				firstName : "cola04",
				lastName : "cola04",
				login : "mudar@123",
				objetivo : "2",
				area : "ara02",
				superior : "cola01@gmail.com"
			},
			{
				email : "cola05@gmail.com",
				firstName : "cola05",
				lastName : "cola05",
				login : "mudar@123",
				objetivo : "2",
				area : "ara03",
				superior : "cola02@gmail.com"
			},
			{
				email : "cola06@gmail.com",
				firstName : "cola06",
				lastName : "cola06",
				login : "mudar@123",
				objetivo : "2",
				area : "ara03",
				superior : "cola02@gmail.com"
			},
			{
				email : "cola07@gmail.com",
				firstName : "cola07",
				lastName : "cola07",
				login : "mudar@123",
				objetivo : "2",
				area : "ara04",
				superior : "cola03@gmail.com"
			} 
			]
	};
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/hierarquia/importar",
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

function importarHistorico() {
	var objJson = {
			token: sessionStorage.token,
			
			empresaId : "5a37e91ba0dddfbdf44fabff",
			historicos : 
				[ 
				{
					empresaId:"5a37e91ba0dddfbdf44fabff",
					nome:"2 tri 2015",
					dataEnvio:"2015-01-15",
					dataConclusao:"2015-02-15",
					email:"antonio.orlandi@rodobens.com",
					objetivoId:"91",
					habilidadeId:"10828",
					nota:"4.33"				}, 
				{
					empresaId:"5a37e91ba0dddfbdf44fabff",
					nome:"2 tri 2015",
					dataEnvio:"2015-01-15",
					dataConclusao:"2015-02-15",
					email:"antonio.orlandi@rodobens.com",
					objetivoId:"91",
					habilidadeId:"10040",
					nota:"4.33"				},
				{
					empresaId:"5a37e91ba0dddfbdf44fabff",
					nome:"2 tri 2015",
					dataEnvio:"2015-01-15",
					dataConclusao:"2015-02-15",
					email:"puts@rodobens.com",
					objetivoId:"91",
					habilidadeId:"10040",
					nota:"4.00"				}, 
				{
					empresaId:"5a37e91ba0dddfbdf44fabff",
					nome:"2 tri 2015",
					dataEnvio:"2015-01-15",
					dataConclusao:"2015-02-15",
					email:"taloco@rodobens.com",
					objetivoId:"91",
					habilidadeId:"10040",
					nota:"4.00"				} ,
					{
						empresaId:"5a37e91ba0dddfbdf44fabff",
						nome:"2 tri 2015",
						dataEnvio:"2015-01-15",
						dataConclusao:"2015-02-15",
						email:"inexistente@rodobens.com",
						objetivoId:"91",
						habilidadeId:"10040",
						nota:"4.00"				} 
				]
	};
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/avaliacao/importar-historico",
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

function testaInsert() {
	var date = new Date();
    var objJson = {
			atrUser: "Marcos.Lima@fit-tecnologia.org.br",
			atrToken: date.getTime() + 'grenne'
			};
	$.ajax({
		type: "POST",
		url: ' https://flex.populisservicos.com.br/populisII-web/rest/user/token',
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        async: false,
        data : JSON.stringify(objJson)
	})
  	.done(function( data ) {
   		result = true;
  	})
	.fail(function(data){
   		result = false;
   		console.log ("nao gravou token");
	})
	.always(function(data) {
   	});};

function testaObter() {
	var objJson = {
		token: sessionStorage.token,
		collection : "badges",
		keys : [ {
			key : "documento.id",
			value : "1"
		} ]
	};
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/crud/obter",
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
		token: sessionStorage.token,
		collection : "usuarios",
		insert : {
			documento : {
				email : "grenne@mail.com",
				firstName : "Grenne",
				lastName : "Grenne"
			}
		}
	};
	rest_incluir (objJson, restOk, semAcao);
};
function testaAtualizar() {
 
	var objJson = 
			{"token":"939c381184bac73457e3eca584fb1838","collection":"usuarios","keys":[{"key":"documento.email","value":"grenneglr@gmail.com"}],"update":[{"field":"password","value":"turi"}]};
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/crud/atualizar",
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
		token: sessionStorage.token,
		collection : "cursos",
		keys : []
	};
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/crud/lista",
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
		usuario : "939c381184bac73457e3eca584fb1838",
		tipo : tipo,
		id : id,
		assunto : "cadastro",
		inout : inout
	};
	$
			.ajax(
					{
						type : "POST",
						url : localStorage.mainUrl + "yggboard_server/rest/userPerfil/atualizar/perfil",
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
	var assunto = prompt("Assunto");
	var usuario = prompt("Usuario");
	var id = prompt("Id");
	var filtro = {
			usuario : usuario,
			assunto : assunto,
			id : id
	};
	objJson.push(filtro);
//	assunto = "areaConhecimento";
//	usuario = "939c381184bac73457e3eca584fb1838";
//	id = "9";
//	filtro = {
//			usuario : usuario,
//			assunto : assunto,
//			id : id
//	};
//	objJson.push(filtro);

/*
	while (assunto !=  "fim") {
		usuario = prompt("Usuario");
		assunto = prompt("Assunto");
		if (assunto != "fim"){
			var id = prompt("Id");
			var filtro = {
					usuario : usuario,
					assunto : assunto,
					id : id
			};
			objJson.push(filtro);
		};
	};
*/
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/index/obter/filtro",
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
				console.log ("curso:"  + item.id + " nome:" + item.nome + " coeficiente:" + item.coeficiente +" habilidades:" + item.qtdeHabilidades  );
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
		habilidade = obterDependencias(habilidade, habilidade.id,"habilidades", 1);
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
//		rest_atualizar(objJson, semAcao, semAcao);
	});
	console.log("terminou pre reuisitos");
};

function mostraPrerequisitos(habilidades) {

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$.each(habilidades, function(i, habilidade) {
		$.each(habilidade.preRequisitos, function(i, preRequisito) {
			habilidade = obterDependencias(habilidade, preRequisito,"habilidades", 1);
		});
		console.log("habilidade:" + habilidade.id + " preRequisitos:", habilidade.preRequisitos);
	});
};

function atualizaObjetivos() {

	objetivos = rest_listaReturn ("objetivos");

	habilidades = rest_listaReturn ("habilidades");

	$.each(habilidades, function(i, habilidade) {
		habilidade = obterObjetivos(habilidade, habilidade.id);
		console.log("id - " + habilidade.id);
		var objJson = {
			token: sessionStorage.token,
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

function atualizaHabilidadesDuplicadas() {

	userPerfis = rest_listaReturn ("userPerfil");
	habilidadesBase = rest_listaReturn ("habilidades");
	badgesBase = rest_listaReturn ("badges");

	$.each(userPerfis, function(i, userPerfil) {
		var habilidades = userPerfil.habilidades;
		var newHabilidades = [];		
		$.each(habilidades, function(i, habilidade) {
			var existe = false;
			$.each(habilidadesBase, function(i, habilidadeBase) {
				if (habilidadeBase.id == habilidade){
					existe = true;
				};
			});
			if (existe){
				addArray (habilidade, newHabilidades)
			};
		});
		userPerfil.habilidades = newHabilidades;
		
		var habilidadesInteresse = userPerfil.habilidadesInteresse;
		var newHabilidadesInteresse = [];		
		$.each(habilidadesInteresse, function(i, habilidadeInteresse) {
			var existe = false;
			$.each(habilidadesBase, function(i, habilidadeBase) {
				if (habilidadeBase.id == habilidadeInteresse){
					existe = true;
				};
			});
			if (existe){
				addArray (habilidadeInteresse, newHabilidadesInteresse);
			};
		});
		userPerfil.habilidadesInteresse = newHabilidadesInteresse;

		var badgesConquista = userPerfil.badgesConquista
		$.each(badgesConquista, function(i, badgeConquista) {
			$.each(badgesBase, function(i, badgeBase) {
				if (badgeBase.id == badgeConquista){
					$.each(badgeBase.habilidades, function(i, badgeBaseHabilidade) {
						addArray (badgeBaseHabilidade, userPerfil.habilidadesInteresse);
					});
				};
			});
		});
	
		var objJson = {
			token: sessionStorage.token,
			collection : "userPerfil",
			keys : [ {
				key : "documento.usuario",
				value : userPerfil.usuario
			} ],
			update : [ {
				field : "documento",
				value : userPerfil
			} ]
		};
		rest_atualizar(objJson, semAcao, semAcao);
	});
	
	console.log("terminou duplicidade perfil");
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
				};
			};
		});
	});

	return objJson;
};

function atualizaBadges() {

	badges = rest_listaReturn ("badges");

	habilidades = rest_listaReturn ("habilidades");

	$.each(habilidades, function(i, habilidade) {
		habilidade = obterBadges(habilidade, habilidade.id);
		console.log("id - " + habilidade.id);
		var objJson = {
			token: sessionStorage.token,
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
		rest_atualizar(objJson, restOk, semAcao);
	});
	console.log("terminou badges");
};

function atualizaEventos() {

	eventos = rest_listaReturn ("eventos");

	$.each(eventos, function(i, evento) {
		usuario = rest_obterChave("documento.email", evento.idEvento, "usuarios");
		console.log("id - " + evento.idUsuario);
		var objJson = {
				token: sessionStorage.token,
				collection : "eventos",
				keys : [ 
					{
					key : "documento.idUsuario",
					value : evento.idUsuario
					} 
					],
				update : [ 
//					{
//					field : "data",
//					value : converteData(evento.data)
//					}, 
					{
					field : "idSeguindo",
					value : evento.idUsuario
					} 
					]
			};
		if (usuario){
			var objJson = {
				token: sessionStorage.token,
				collection : "eventos",
				keys : [ 
					{
					key : "_id",
					value : evento._id
					} 
					],
				update : [ 
					{
					field : "idUsuario",
					value : usuario._id
				
					}, 
//					{
//					field : "data",
//					value : converteData(evento.data)
//					}, 
					{
					field : "idSeguindo",
					value : usuario._id
					} 
					]
			};
		};			
		rest_atualizar(objJson, restOk, semAcao);
	});
	console.log("terminou eventos");
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

	$.each(habilidades,function(i, habilidadeSource) {
		if (habilidadeTarget == habilidadeSource.id){
			if (tipo == "necessarios") {
				var existente = false;
				$.each(objJson.necessarios,function(i, habilidade) {
					if (habilidadeSource.source == habilidade) {
						existente = true;
					}
					;
				});
				if (!existente) {
					objJson.necessarios.push(habilidadeSource.source);
					objJson = obterDependencias(objJson,habilidadeSource.source,tipo);
				};
			};
			if (tipo == "recomendados") {
				var existente = false;
				$.each(objJson.recomendadas,function(i, habilidade) {
					if (habilidadeSource.source == habilidade) {
						existente = true;
					};
				});
				if (!existente) {
					objJson.recomendados.push(habilidadeSource.source);
					objJson = obterDependencias(objJson,habilidadeSource.source,tipo);
				}
				;
			}
			;
			if (tipo == "habilidades") {
				$.each(habilidadeSource.preRequisitos,function(i, preRequisito) {
					var existente = false;
					$.each(objJson.preRequisitos,function(i, habilidade) {
						if (preRequisito == habilidade) {
							existente = true;
						};
					});
					if (!existente) {
						objJson.preRequisitos.push(preRequisito + ":" + nivel);
						objJson = obterDependencias(objJson,preRequisito,tipo, (parseInt(nivel) + 1));
					};
				});
			};
		};
	});

	return objJson;
};
function converteData(str){
	return str.slice(0, 4) + "-" + str.slice(4, 6) + "-" + str.slice(6, 8);
};