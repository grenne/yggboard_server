/**
 * 
 */

function atualizaCursosHabilidade (){

	lines = rest_listaReturn ("habilidades");
	
	sessionStorage.setItem("lines", JSON.stringify(lines));
	sessionStorage.setItem("cursos", JSON.stringify(rest_listaReturn ("cursos")));	
	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "atualizaObjetivosHabilidadeMsg");
	sessionStorage.setItem("processo", "atualizaCursosHabilidade");
	
	console.log ("iniciou habilidades/cursos");
};

function processaAtualizaCursosHabilidade (habilidade){

	cursos = JSON.parse(sessionStorage.getItem("cursos"));
	
	var habilidadadesCursos = [];
	var habilidadadesCursosNome = [];
    $.each(cursos, function (z, curso) {
	    $.each(curso.habilidades, function (w, cursoIdHabilidade) {
	    	if (habilidade.id == cursoIdHabilidade){
	    		var cursoInput = cursos[z]
	    		if (!testaDuplicidadeArray(cursoInput.id, habilidadadesCursos)){
		    		habilidadadesCursos.push(cursoInput.id);
		    		habilidadadesCursosNome.push(cursoInput.nome);
	    		};
	    	};
	    });
	});
	delete habilidade["cursos"];
	delete habilidade["cursosNome"];
	habilidade.cursos = habilidadadesCursos;
	habilidade.cursosNome = habilidadadesCursosNome;
	var habililadeUpdate = habilidade;
	var objJson = {
		token : sessionStorage.token,
		collection : "habilidades",
		keys : [ {
			key : "documento.id",
			value : habilidade.id
		} ],
		update : [ {
			field : "documento",
			value : habililadeUpdate

		} ]
	};
	rest_atualizar (objJson, restOk, semAcao);	
};

function atualizaObjetivosHabilidade (){
	
	lines = rest_listaReturn ("habilidades");		

	sessionStorage.setItem("lines", JSON.stringify(lines));
	sessionStorage.setItem("objetivos", JSON.stringify(rest_listaReturn ("objetivos")));	
	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);
	
	sessionStorage.setItem("rotina", "atualizaAreaAtuacaoObjetivosMsg");  	
	sessionStorage.setItem("processo", "atualizaObjetivosHabilidade");  			

	console.log ("iniciou habilidades/objetivos");
};

function processaAtualizaObjetivosHabilidade (habilidade){

	objetivos = JSON.parse(sessionStorage.getItem("objetivos"));

	var habilidadadesObjetivos = [];
	var habilidadadesObjetivosNome = [];
    $.each(objetivos, function (z, objetivo) {
	    $.each(objetivo.necessarios, function (w, objetivoIdHabilidade) {
	    	if (habilidade.id == objetivoIdHabilidade){
	    		var objetivo = objetivos[z]
	    		if (!testaDuplicidadeArray(objetivo.id, habilidadadesObjetivos)){
	    			habilidadadesObjetivos.push(objetivo.id);
	    			habilidadadesObjetivosNome.push(objetivo.nome);
	    		};
	    	};
	    });
	});
	delete habilidade["objetivos"];
	delete habilidade["objetivosNome"];
	habilidade.objetivos = habilidadadesObjetivos;
	habilidade.objetivosNome = habilidadadesObjetivosNome;
	var habililadeUpdate = habilidade;
	var objJson = {
		token : sessionStorage.token,
		collection : "habilidades",
		keys : [ {
			key : "documento.id",
			value : habilidade.id
		} ],
		update : [ {
			field : "documento",
			value : habililadeUpdate

		} ]
	};
	rest_atualizar (objJson, restOk, semAcao);	
};

function atualizaAreaAtuacaoObjetivos (){
		
	lines = rest_listaReturn ("objetivos");		

	sessionStorage.setItem("lines", JSON.stringify(lines));
	sessionStorage.setItem("areasAtuacao", JSON.stringify(rest_listaReturn ("areasAtuacao")));	
	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);
	
	sessionStorage.setItem("rotina", "atualizaAreaConhecimentoHabilidadesMsg");  		
	sessionStorage.setItem("processo", "atualizaAreaAtuacaoObjetivos");
	
	console.log ("Iniciou objetivos/area atuacao");
	
};

function processaAtualizaAreaAtuacaoObjetivos (areaAtuacao){
	
	var objetivosArray = [];
	var objetivosArrayNome = [];
    $.each(objetivos, function (z, objetivo) {
	    $.each(objetivo.areaAtuacao, function (w, areasAtuacaoInput) {
	    	if (areaAtuacao.id == areasAtuacaoInput){
	    		if (!testaDuplicidadeArray(objetivo.id, objetivosArray)){
	    			objetivosArray.push(objetivo.id);
	    			objetivosArrayNome.push(objetivo.nome);
	    		};
	    	};
	    });
	});
	delete areaAtuacao["objetivos"];
	delete areaAtuacao["objetivosNome"];
	areaAtuacao.objetivos = objetivosArray;
	areaAtuacao.objetivosNome = objetivosArrayNome;
	var areaAtuacaoUpdate = areaAtuacao;
	var objJson = {
		token : sessionStorage.token,
		collection : "areaAtuacao",
		keys : [ {
			key : "documento.id",
			value : areaAtuacao.id
		} ],
		update : [ {
			field : "documento",
			value : areaAtuacaoUpdate

		} ]
	};
	rest_atualizar (objJson, restOk, semAcao);	
};

function atualizaAreaConhecimentoHabilidades (){
	
	lines = rest_listaReturn ("habilidades");		

	sessionStorage.setItem("lines", JSON.stringify(lines));
	sessionStorage.setItem("areasConhecimento", JSON.stringify(rest_listaReturn ("areasConhecimento")));	
	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", areasConhecimento.length);
	
	sessionStorage.setItem("rotina", "ultimaRotina");  			
	sessionStorage.setItem("processo", "atualizaAreaConhecimentoHabilidades");  			
	
	console.log ("iniviou  area conhecimento/habilidade");
	
};

function processaAtualizaAreaConhecimentoHabilidades (areaConhecimento){

	var habilidadesArray = [];
	var habilidadesArrayNome = [];
    $.each(habilidades, function (z, habilidade) {
	    $.each(habilidade.areaConhecimento, function (w, areasConhecimentoInput) {
	    	if (areaConhecimento.id == areasConhecimentoInput){
	    		if (!testaDuplicidadeArray(habilidade.id, habilidadesArray)){
	    			habilidadesArray.push(habilidade.id);
	    			habilidadesArrayNome.push(habilidade.nome);
	    		};
	    	};
	    });
	});
	delete areaConhecimento["habilidades"];
	delete areaConhecimento["habilidadesNome"];
	areaConhecimento.habilidades = habilidadesArray;
	areaConhecimento.habilidadesNome = habilidadesArrayNome;
	var areaConhecimentoUpdate = areaConhecimento;
	var objJson = {
		token : sessionStorage.token,
		collection : "areaConhecimento",
		keys : [ {
			key : "documento.id",
			value : areaConhecimento.id
		} ],
		update : [ {
			field : "documento",
			value : areaConhecimentoUpdate

		} ]
	};
	rest_atualizar (objJson, restOk, semAcao);
};

function addArray (id, array){

	var existe = false;
	for (var i = 0; i < array.length; i++) {
		if (array[i] == id){
			existe = true;
		}
	};
	
	if (!existe){
		array.push(id);
	}
	
	return array;
};

function testaDuplicidadeArray (id, array){

	var existe = false;
	for (var i = 0; i < array.length; i++) {
		if (array[i] == id){
			existe = true;
		}
	};
	
	return existe;
};


function atualizaPerfil (){
	
		var objJson = 
			{	
				token: sessionStorage.token,
				async : false,
				collection : "userPerfil",
				keys : 
					[
					]
			};

		rest_lista (objJson, atualizaPerfilProcess, semAcao);
};


function atualizaPerfilProcess (usersPerfil){
	
	var badgesInput = rest_listaReturn ("badges");
	
	$.each( usersPerfil, function( i, userPerfil) {		
		var badges = [];
	    $.each(userPerfil.badges, function (i, userBadge) {
		    $.each(badgesInput, function (i, badge) {
		    	if (badge.nome == userBadge){
		    		badges.push(badge.id);
		    	};
		    });
		});
    	delete userPerfil["badges"];
    	userPerfil.badges = badges;
		var badgesInteresse = [];
	    $.each(userPerfil.badgesInteresse, function (i, userBadge) {
		    $.each(badgesInput, function (i, badge) {
		    	if (badge.nome == userBadge){
		    		badgesInteresse.push(badge.id);
		    	};
		    });
		});
    	delete userPerfil["badgesInteresse"];
    	userPerfil.badgesInteresse = badgesInteresse;
    	userPerfil.showBadges = [];
		var objJson = 
			{	
				token: sessionStorage.token,
				async : false,
				collection : "userPerfil",
				insert :
					{
						documento: userPerfil
					}
			};	    
		rest_incluir (objJson, restOk, semAcao);
	});
};

function salvaSessionStore (objJson, entidade){

	sessionStorage.setItem(entidade, JSON.stringify(objJson));

};

