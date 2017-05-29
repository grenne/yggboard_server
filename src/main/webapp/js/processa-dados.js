
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
	
	var objJson = 
		{
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
					logo:fields[20]
					}
				}
		};
	if (fields[6]){
		var array = fields[6].split(",");
		for (var i = 0; i < array.length; i++) {
			var array_1 = array[i].split("|");
			for (var z = 0; z < array_1.length; z++) {
				var nome = nomeHabilidade(array_1[z]);
				if (nome != ""){
					objJson.insert.documento.habilidades.push(array_1[z]);
					objJson.insert.documento.habilidadesNome.push(nome);
				}else{
					console.log ("habilidade curso não encontrado: " + array_1[z]);
				}				
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
	
	rest_incluir (objJson, semAcao, semAcao);
	
};

function processaHabilidades (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
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
					preRequisitos:[],
					preRequisitosNome:[],
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
			objJson.insert.documento.areaConhecimento.push(array[i].replace (" ",""));			
		}
	};
	if (fields[7]){
		var array = fields[7].split(",");
		for (var i = 0; i < array.length; i++) {
			var array_1 = array[i].split("|");
			for (var z = 0; z < array_1.length; z++) {
				var nome = nomeHabilidade(array_1[z]);
				if (nome != ""){
					objJson.insert.documento.preRequisitos.push(array_1[z]);
					objJson.insert.documento.preRequisitosNome.push(nome);
				}else{
					console.log ("pré requisitos não encontrado: " + array_1[z]);
				}				
			}
		}
	};
	if (fields[8]){
		var array = fields[8].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i].replace (" ",""));			
		}
	};
	
	rest_incluir (objJson, semAcao, semAcao);
	
};

function processaObjetivos (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
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
					responsabilidades:fields[6],
					atividades:fields[7],
					salarioMinimo:fields[8],
					salarioMaximo:fields[9],
					salarioMedio:fields[10],
					tags:[],
					necessarios:[],
					necessariosNome:[],
					recomendados:[],
					recomendadosNome:[]
					}
				}
		};
	if (fields[5]){
		var array = fields[5].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.areaAtuacao.push(array[i].replace (" ",""));			
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
			var array_1 = array[i].split("|");
			for (var z = 0; z < array_1.length; z++) {
				var nome = nomeHabilidade(array_1[z]);
				if (nome != ""){
					objJson.insert.documento.necessarios.push(array_1[z]);
					objJson.insert.documento.necessariosNome.push(nome);
				}else{
					console.log ("necessarios objetivo não encontrado: " + array_1[z]);
				}				
			}
		}
	};
	if (fields[15]){
		var array = fields[15].split(",");
		for (var i = 0; i < array.length; i++) {
			var array_1 = array[i].split("|");
			for (var z = 0; z < array_1.length; z++) {
				var nome = nomeHabilidade(array_1[z]);
				if (nome != ""){
					objJson.insert.documento.recomendados.push(array_1[z]);
					objJson.insert.documento.recomendadosNome.push(nome);
				}else{
					console.log ("recomendado objetivo não encontrado: " + array_1[z]);
				}				
			}
		}
	};
	
	rest_incluir (objJson, semAcao, semAcao);
	
};

function processaBadges (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
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
					tags:[],
					tipo:fields[6],
					parametro:fields[7],
					quantidade:fields[8],
					badge:fields[9],
					titulo:fields[10],
					popup:fields[11],
					}
				}
		};
	if (fields[4]){
		var array = fields[4].split(",");
		for (var i = 0; i < array.length; i++) {
			var array_1 = array[i].split("|");
			for (var z = 0; z < array_1.length; z++) {
				var nome = nomeHabilidade(array_1[z]);
				if (nome != ""){
					objJson.insert.documento.habilidades.push(array_1[z]);
					objJson.insert.documento.habilidadesNome.push(nome);
				}else{
					console.log ("habilidade curso não encontrado: " + array_1[z]);
				}				
			}
		}
	};
	if (fields[5]){
		var array = fields[5].split(",");
		for (var i = 0; i < array.length; i++) {
			objJson.insert.documento.tags.push(array[i].replace (" ",""));			
		}
	};
	
	rest_incluir (objJson, semAcao, semAcao);
	
};

function processaAreaConhecimento (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
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
	
	rest_incluir (objJson, semAcao, semAcao);
	
};

function processaAreaAtuacao (data){
	
	var fields = data.split(";");
	
	var objJson = 
		{
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
	
	rest_incluir (objJson, semAcao, semAcao);
	
};

function nomeHabilidade (id){
	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));
	var nome = "";
	$.each(habilidades, function( i, habilidade) {
		if (id == habilidade.documento.id){
			nome =habilidade.documento.nome
		}
	});

	return nome;
};
