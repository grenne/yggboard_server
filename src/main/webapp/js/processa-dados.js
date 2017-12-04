
function carregaDados(data){
	switch (sessionStorage.escolha) {
	case "habilidades":
		processaHabilidades(data);
		break;
	case "objetivos":
		processaObjetivos(data);
		break;
	case "cursos":
		processaCursos(data);
		break;
	case "badges":
		processaBadges(data);
		break;
	case "areaConhecimento":
		processaAreaConhecimento(data);
		break;
	case "areaAtuacao":
		processaAreaAtuacao(data);
		break;
	default:
		break;
	}
};

function processaCursos (data){
	
	var fields = data.split(";");
	
	if (fields[9] == "" | fields[9] == null){
		fields[9] = "0.00";
	};
	if (fields[16] != null){
		fields[16].replace ('"', "'")
	};
	var objJson = 
		{
			token: sessionStorage.token,
			async : false,
			collection : "cursos",
			insert :
				{
				documento : 
					{
					id:fields[0],
					escola:fields[1],
					siteEscola:fields[2],
					nome:fields[3],
					preRequisitos:fields[4],
					materia:fields[5],
					habilidades:[],
					habilidadesNome:[],
					cargaHoraria:fields[7],
					duracao:fields[8],
					custo:fields[9],
					link:fields[10],
					nivel:fields[11],
					periodicidade:fields[12],
					frequencia:fields[13],
					certificacao:fields[14],
					formato:fields[15],
					descricao:fields[16],
					badges:[],
					parents:[],
					tags:[],
					logo:fields[20],
					classificacao:fields[21],
					areaConhecimento:[],
					}
				}
		};
	if (fields[6]){
		var array = fields[6].split(",");
		for (var i = 0; i < array.length; i++) {
			var nome = nomeHabilidade(array[i]);
			if (nome != ""){
				objJson.insert.documento.habilidades.push(array[i]);
				objJson.insert.documento.habilidadesNome.push(nome);
			}else{
				objJson.insert.documento.habilidades.push(array[i]);
				console.log ("habilidade curso não encontrado: " + array[i]);
			}				
		}
	};
	if (fields[17]){
		var array = fields[17].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.badges.push(array[i]);
		}
	};
	if (fields[18]){
		var array = fields[18].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.parents.push(array[i]);
		}
	};
	if (fields[19]){
	var array = fields[19].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i]);
		}
	};
	if (fields[23]){
		var array = fields[23].split(",");
			for (var i = 0; i < array.length; i++) {
				objJson.insert.documento.tags.push(array[i]);
			}
		};
	
	if (fields[0]){
		rest_incluir (objJson, restOk, semAcao);
		$("#registros").prepend('<li class="output"><strong class="label">Id:</strong><small class="field">' + fields[0] + '</small><strong class="label"> Nome:</strong><small class="field">' + fields[3] + '</small></li>');
	};
	
};

function processaHabilidades (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
			token: sessionStorage.token,
			async: false,
			collection : "habilidades",
			insert :
				{
				documento : 
					{
					id:fields[0],
					area:fields[1],
					campo:fields[2],
					categoria:fields[3],
					nome:fields[4],
					descricao:fields[5],
					areaConhecimento:[],
					areaConhecimentoNome:[],
					preRequisitos:[],
					preRequisitosNome:[],
					preRequisitosGeral:[],
					tags:[],
					wiki:fields[9],
					amazon:fields[10],
					video:fields[11],
					}
				}
		};
	if (fields[6]){
		var array = fields[6].split(",");
		for (var i = 0; i < array.length; i++) {
			var nome = nomeAreaConhecimento(array[i].replace (" ",""));
			objJson.insert.documento.areaConhecimento.push(array[i].replace (" ",""));
			if (nome != ""){
				objJson.insert.documento.areaConhecimentoNome.push(nome);
			};
		}
	};
	if (fields[7]){
		var array = fields[7].split(",");
		for (var i = 0; i < array.length; i++) {
			var arrayPipe = array[i].split("|");
			objJson.insert.documento.preRequisitos.push(array[i]);
			objJson.insert.documento.preRequisitosGeral.push(array[i] + ":" + "0");
			var nomes = "";
			var pipe = "";
			for (var z = 0; z < arrayPipe.length; z++) {
				nomes = nomes + pipe + nomeHabilidade(arrayPipe[z]);
				pipe = "|";
			};
			objJson.insert.documento.preRequisitosNome.push(nomes);
		}
		$.each(objJson.insert.documento.preRequisitos, function(i, preRequisito) {
			objJson.insert.documento = obterDependenciasPreReq(objJson.insert.documento, preRequisito, 1);
		});
	};
	if (fields[8]){
		var array = fields[8].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i].replace (" ",""));			
		}
	};
	
	if (fields[0]){
		rest_incluir (objJson, restOk, semAcao, fields[0], fields[4]);
		$("#registros").prepend('<li class="output"><strong class="label">Id:</strong><small class="field">' + fields[0] + '</small><strong class="label"> Nome:</strong><small class="field">' + fields[4] + '</small></li>');
	};
	
};

function processaObjetivos (data){
	
	var fields = data.split(";");

	if (fields[8] == "" | fields[9] == null){
		fields[8] = "0.00";
	};
	if (fields[9] == "" | fields[9] == null){
		fields[9] = "0.00";
	};
	if (fields[10] == "" | fields[9] == null){
		fields[10] = "0.00";
	};
	
	var objJson = 
		{
			token: sessionStorage.token,
			async : false,
			collection : "objetivos",
			insert :
				{
				documento : 
					{
					id:fields[0],
					nome:fields[1],
					nivel:fields[2],
					nivelFiltro:fields[3],
					segmentoEconomico:fields[4],
					areaAtuacao:[],
					areaAtuacaoNome:[],
					responsabilidades:fields[6],
					atividades:fields[7],
					salarioMinimo:fields[8],
					salarioMaximo:fields[9],
					salarioMedio:fields[10],
					tags:[],
					necessarios:[],
					necessariosNome:[],
					preRequisitosGeral:[],
					recomendados:[],
					recomendadosNome:[],
					classificacao:fields[16]
					}
				}
		};
	if (fields[5]){
		var array = fields[5].split(",");
		for (var i = 0; i < array.length; i++) {
			var nome = nomeAreaAtuacao(array[i]);
			objJson.insert.documento.areaAtuacao.push(array[i].replace (" ",""));			
			objJson.insert.documento.areaAtuacaoNome.push(nome);
		}
	};
	if (fields[11]){
		var array = fields[11].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i].replace (" ",""));			
		}
	};
	if (fields[13]){
		var array = fields[13].split(",");
		for (var i = 0; i < array.length; i++) {
			var nome = nomeHabilidade(array[i]);
			if (nome != ""){
				objJson.insert.documento.necessarios.push(array[i]);
				objJson.insert.documento.necessariosNome.push(nome);
				objJson.insert.documento.preRequisitosGeral.push(array[i] + ":" + "0");
			}else{
				console.log ("necessarios objetivo não encontrado: " + array[i]);
			}				
		};
		$.each(objJson.insert.documento.necessarios, function(i, habilidade) {
			objJson.insert.documento = obterDependenciasPreReq(objJson.insert.documento, habilidade, 1);
		});
	};
	if (fields[15]){
		var array = fields[15].split(",");
		for (var i = 0; i < array.length; i++) {
			var nome = nomeHabilidade(array[i]);
			if (nome != ""){
				objJson.insert.documento.recomendados.push(array[i]);
				objJson.insert.documento.recomendadosNome.push(nome);
			}else{
				console.log ("recomendado objetivo não encontrado: " + array[i]);
			}				
		}
	};
	
	if (fields[0]){
		rest_incluir (objJson, restOk, semAcao);
		$("#registros").prepend('<li class="output"><strong class="label">Id:</strong><small class="field">' + fields[0] + '</small><strong class="label"> Nome:</strong><small class="field">' + fields[1] + '</small></li>');
	};
	
};

function processaBadges (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
			token: sessionStorage.token,
			asybc : false,
			collection : "badges",
			insert :
				{
				documento : 
					{
					id:fields[0],
					nome:fields[1],
					comoGanhar:fields[2],
					entidadeCertificadora:fields[3],
					habilidades:[],
					habilidadesNome:[],
					preRequisitosGeral:[],
					tags:[],
					tipo:fields[6],
					parametro:fields[7],
					quantidade:fields[8],
					badge:fields[9],
					titulo:fields[10],
					textoPost:fields[11],
					popup:fields[12],
					habilidadesNecessarias:[]

					}
				}
		};
	if (fields[4]){
		var array = fields[4].split(",");
		for (var i = 0; i < array.length; i++) {
			var nome = nomeHabilidade(array[i]);
			if (nome != ""){
				objJson.insert.documento.habilidades.push(array[i]);
				objJson.insert.documento.habilidadesNome.push(nome);
				objJson.insert.documento.preRequisitosGeral.push(array[i] + ":" + "0");
			}else{
				console.log ("habilidade curso não encontrado: " + array[i]);
			}				
		};
		$.each(objJson.insert.documento.habilidades, function(i, habilidade) {
			objJson.insert.documento = obterDependenciasPreReq(objJson.insert.documento, habilidade, 1);
		});
	};
	if (fields[5]){
		var array = fields[5].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i].replace (" ",""));			
		}
	};
	if (fields[13]){
		var array = fields[13].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.habilidadesNecessarias.push(array[i].replace (" ",""));			
		}
	};
	
	if (fields[0]){
		rest_incluir (objJson, restOk, semAcao);
		$("#registros").prepend('<li class="output"><strong class="label">Id:</strong><small class="field">' + fields[0] + '</small><strong class="label"> Nome:</strong><small class="field">' + fields[1] + '</small></li>');
	};
};

function processaAreaConhecimento (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
			token: sessionStorage.token,
			async : false,
			collection : "areaConhecimento",
			insert :
				{
				documento : 
					{
					id:fields[0],
					area:fields[1],
					campo:fields[2],
					categoria:fields[3],
					nome:fields[4],
					descricao:fields[5],
					parent:fields[6],
					wiki:fields[7],
					tags:[],
					amazon:fields[9],
					youtube:fields[10]
					}
				}
		};
	if (fields[8]){
		var array = fields[8].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i].replace (" ",""));			
		}
	};
	
	if (fields[0]){
		rest_incluir (objJson, semAcao, semAcao);
		$("#registros").prepend('<li class="output"><strong class="label">Id:</strong><small class="field">' + fields[0] + '</small><strong class="label"> Nome:</strong><small class="field">' + fields[4] + '</small></li>');
	};
};

function processaAreaAtuacao (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
			token: sessionStorage.token,
			async : false,
			collection : "areaAtuacao",
			insert :
				{
				documento : 
					{
					id:fields[0],
					nome:fields[1]
					}
				}
		};
	
	if (fields[0]){
		rest_incluir (objJson, restOk, semAcao);
		$("#registros").prepend('<li class="output"><strong class="label">Id:</strong><small class="field">' + fields[0] + '</small><strong class="label"> Nome:</strong><small class="field">' + fields[1] + '</small></li>');
	};
};

function nomeHabilidade (id){
	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));
	var nome = " ";
	$.each(habilidades, function( i, habilidade) {
		if (id.trim() == habilidade.id){
			nome = habilidade.nome;
		};
	});

	return nome;
};

function nomeAreaAtuacao (id){
	areasAtuacao = JSON.parse(sessionStorage.getItem("areaAtuacao"));
	var nome = "";
	$.each(areasAtuacao, function( i, areaAtuacao) {
		if (id.trim() == areaAtuacao.id){
			nome =areaAtuacao.nome
		}
	});
	
	if (nome == ""){
		console.log ("nome habilidade não econtrado:" + id);
	};
	
	return nome;
	
	
};

function nomeAreaConhecimento (id){
	areasConhecimento = JSON.parse(sessionStorage.getItem("areaConhecimento"));
	var nome = "";
	$.each(areasConhecimento, function( i, areaConhecimento) {
		if (id.trim() == areaConhecimento.id){
			nome = areaConhecimento.nome
		}
	});

	return nome;
};
function obterDependenciasPreReq(objJson, habilidadeTarget,nivel) {

	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));

	$.each(habilidades,function(i, habilidadeSource) {
		if (habilidadeTarget == habilidadeSource.id){
			$.each(habilidadeSource.preRequisitos,function(i, preRequisito) {
				var existente = false;
				$.each(objJson.preRequisitos,function(i, habilidade) {
					if (preRequisito == habilidade) {
						existente = true;
					};
				});
				if (!existente) {
					objJson.preRequisitosGeral = addArray (preRequisito + ":" + nivel, objJson.preRequisitosGeral);
					if (nivel > 20){
						alert("estourou o nivel de pré requisito: " + nivel  + " - existe algum erro na sua composição da habilidade:" + habilidadeTarget );
					}
					objJson = obterDependenciasPreReq(objJson,preRequisito, (parseInt(nivel) + 1));
				};
			});
		};
	});

	return objJson;
};
