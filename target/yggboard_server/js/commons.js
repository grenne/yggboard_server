/**
 * 
 */

function atualizaCursosHabilidade (){

	habilidades = rest_listaReturn ("habilidades");
	cursos = rest_listaReturn ("cursos");
	
	$.each( habilidades, function( i, habilidade) {		
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
		var objJson = 
		{	
			token: "1170706277ae0af0486017711353ee73",
			collection : "habilidades",
			keys : 
				[
					{
						key : "documento.id",
						value : habilidade.id
					}
				],
			update : 
				[
					{
						field : "documento",
						value : habililadeUpdate
						
					}
				]
			};
		rest_atualizar (objJson, restOk, semAcao);
	});
	sessionStorage.setItem("rotina", "atualizaObjetivosHabilidadeMsg");  	
	console.log ("terminou cursos");
};

function atualizaObjetivosHabilidade (){
		
	habilidades = rest_listaReturn ("habilidades");
	objetivos = rest_listaReturn ("objetivos");
	
	$.each( habilidades, function( i, habilidade) {		
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
		var objJson = 
		{	
			token: "1170706277ae0af0486017711353ee73",
			collection : "habilidades",
			keys : 
				[
					{
						key : "documento.id",
						value : habilidade.id
					}
				],
			update : 
				[
					{
						field : "documento",
						value : habililadeUpdate
						
					}
				]
			};
		console.log ("habilidade id:" + habilidade.id)
		rest_atualizar (objJson, restOk, semAcao);
	});
	sessionStorage.setItem("rotina", "atualizaAreaAtuacaoObjetivosMsg");  	
	console.log ("terminou objetivos");
};

function atualizaAreaAtuacaoObjetivos (){
		
	objetivos = rest_listaReturn ("objetivos");
	areasAtuacao = rest_listaReturn ("areaAtuacao");
	
	$.each( areasAtuacao, function( i, areaAtuacao) {		
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
		var objJson = 
		{	
			token: "1170706277ae0af0486017711353ee73",
			collection : "areaAtuacao",
			keys : 
				[
					{
						key : "documento.id",
						value : areaAtuacao.id
					}
				],
			update : 
				[
					{
						field : "documento",
						value : areaAtuacaoUpdate
						
					}
				]
			};
		rest_atualizar (objJson, restOk, semAcao);
	});
	sessionStorage.setItem("rotina", "atualizaAreaConhecimentoHabilidadesMsg");  		
	console.log ("terminou area atuacao");
	
};

function atualizaAreaConhecimentoHabilidades (){
	
	$("#textoAtualizando").remove();
	$("#registros").prepend('<li class="output"><strong class="label">Indices objetivos criados</strong></li>');
	$("#registros").prepend('<li id="textoAtualizando" class="output"><strong class="label">Atualizando index area conhecimento...</strong></li>');

	habilidades = rest_listaReturn ("habilidades");
	areasConhecimento = rest_listaReturn ("areaConhecimento");
	
	$.each( areasConhecimento, function( i, areaConhecimento) {		
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
		var objJson = 
		{	
			token: "1170706277ae0af0486017711353ee73",
			collection : "areaConhecimento",
			keys : 
				[
					{
						key : "documento.id",
						value : areaConhecimento.id
					}
				],
			update : 
				[
					{
						field : "documento",
						value : areaConhecimentoUpdate
						
					}
				]
			};
		rest_atualizar (objJson, restOk, semAcao);
	});
	
	sessionStorage.setItem("rotina", "ultimaRotina");  			
	console.log ("terminou area conhecimento");
	
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
				token: "1170706277ae0af0486017711353ee73",
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
				token: "1170706277ae0af0486017711353ee73",
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

