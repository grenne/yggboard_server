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
				email : "1",
				nome : "nome",
				password : "pw"

			}
		}
	};
	var objUserPerfil =
		{
			token: sessionStorage.token,
			collection : "~userPerfil",
			insert : {
				documento : {
			        carreirasInteresse : [],
			        habilidadesInteresse : [],
			        cursosInteresse : [],
			        cursosSugeridos : [],
			        tags : [],
			        elementos : [],
			        usuario : "grenne",
			        carreiras : [],
			        habilidades : [],
			        badges : [],
			        badgesInteresse : [],
			        badgesConquistados : [],
			        showBadges : [],
			        carreirasSugeridas : [],
			        relacionamentos : []
				}
			}
		};
	rest_incluir (objJson, restOk, semAcao);
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
		token: sessionStorage.token,
		collection : "userPerfil",
		keys : [ {
			key : "documento.email",
			value : "grenne@grenne.com"
		} ],
		update : 
			[ 
				{
					field : "city",
					value : ""
				}, 
				{
					field : "phone",
					value : ""
				}, 
				{
					field : "escolaridade",
					value : ["","","",""]
				}				
			]
	};
	var a = {"collection":"usuarios","keys":[{"key":"documento.usuario","value":"testeCerto@teste.com"}],"update":[{"field":"city","value":""},{"field":"birthDate","value":""},{"field":"celPhone","value":""},{"field":"escolaridade","value":[]},{"field":"cursos","value":["mais teste","teste","1900","1"]},{"field":"empresas","value":["teste","teste","Comercial","Infraestrutura","teste2","teste2","Tecnologia da Informação","Varejo"]},{"field":"email","value":"testeCerto@teste.com"}]}
	var teste = 
				{
					"collection":"usuarios",
					"keys":
						[
							{
								"key":"documento.email",
								"value":"testeCerto@teste.com"
							}
						],
					"update":
						[
							{"field":"city","value":""},
							{"field":"birthDate","value":""},
							{"field":"celPhone","value":""},
							{"field":"escolaridade","value":[]},
							{"field":"cursos","value":["mais teste","teste","1900","1"]},
							{"field":"empresas","value":
								[{"teste":"teste","Comercial":"Infraestrutura","teste2":"teste2","Tecnologia da Informação":"Varejo"},
									{"teste":"teste","Comercial":"Infraestrutura","teste2":"teste2","Tecnologia da Informação":"Varejo"},
									{"teste":"teste","Comercial":"Infraestrutura","teste2":"teste2","Tecnologia da Informação":"Varejo"}]
							},
							{"field":"email","value":"testeCerto@teste.com"}
							]
				};
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/crud/atualizar",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(teste),
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
		usuario : "grenneglr@gmail.com",
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
	var assunto = "objetivos";
	var usuario = "grenneglr@gmail.com";
	var id = "1";
	var filtro = {
			usuario : usuario,
			assunto : assunto,
			id : id
	};
	objJson.push(filtro);
	assunto = "areaAtuacao";
	usuario = "grenneglr@gmail.com";
	id = "1";
	filtro = {
			usuario : usuario,
			assunto : assunto,
			id : id
	};
	objJson.push(filtro);

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
			token: "1170706277ae0af0486017711353ee73",
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
				}
				;
			}
			;
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
