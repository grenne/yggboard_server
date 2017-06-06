/**
 * 
 */

function atualizaCursosHabilidade (){
	
		var objJson = 
			{
				async : false,
				collection : "cursos",
				keys : 
					[
					]
			};

		rest_lista (objJson, atualizaCursoHablidadeProcess, semAcao);
};


function atualizaCursoHablidadeProcess (cursos){
	
	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));
	
	$.each( habilidades, function( i, habilidade) {		
		var habilidadadesCursos = [];
		var habilidadadesCursosNome = [];
	    $.each(cursos, function (i, curso) {
		    $.each(curso.habilidades, function (i, cursoIdHabilidade) {
		    	if (habilidade.id == cursoIdHabilidade){
		    		var cursoInput = cursos[i]
		    		habilidadadesCursos = testaDuplicidade(cursoInput.id, habilidadadesCursos);
		    		habilidadadesCursosNome = testaDuplicidade(cursoInput.nome, habilidadadesCursosNome);
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
		rest_atualizar (objJson, semAcao, semAcao);
	});
	console.log ("terminou cursos");
};

function atualizaObjetivosHabilidade (){
	
		var objJson = 
			{	
				async : false,
				collection : "objetivos",
				keys : 
					[
					]
			};

		rest_lista (objJson, atualizaObjetivosHablidadeProcess, semAcao);
};


function atualizaObjetivosHablidadeProcess (objetivos){
	
	habilidades = JSON.parse(sessionStorage.getItem("habilidades"));
	
	$.each( habilidades, function( i, habilidade) {		
		var habilidadadesObjetivos = [];
		var habilidadadesObjetivosNome = [];
	    $.each(objetivos, function (i, objetivo) {
		    $.each(objetivo.necessarios, function (i, objetivoIdHabilidade) {
		    	if (habilidade.id == objetivoIdHabilidade){
		    		var objetivo = objetivos[i]
		    		habilidadadesObjetivos = testaDuplicidade(objetivo.id, habilidadadesObjetivos);
		    		habilidadadesObjetivosNome = testaDuplicidade(objetivo.nome, habilidadadesObjetivosNome);
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
		rest_atualizar (objJson, semAcao, semAcao);
	});
	console.log ("terminou objetivos");
};

function atualizaAreaAtuacaoObjetivos (){
	
		var objJson = 
			{
				async : false,
				collection : "objetivos",
				keys : 
					[
					]
			};

		rest_lista (objJson, salvaSessionStore, semAcao, "objetivos");
		
		var objJson = 
			{
				async : false,
				collection : "areaAtuacao",
				keys : 
					[
					]
			};

		rest_lista (objJson, atualizaAreaAtuacaoObjetivosProcess, semAcao);

};


function atualizaAreaAtuacaoObjetivosProcess (areasAtuacao){
	
	objetivos = JSON.parse(sessionStorage.getItem("objetivos"));
	
	$.each( areasAtuacao, function( i, areaAtuacao) {		
		var objetivosArray = [];
		var objetivosArrayNome = [];
	    $.each(objetivos, function (i, objetivo) {
		    $.each(objetivo.areaAtuacao, function (i, areasAtuacaoInput) {
		    	if (areaAtuacao.nome == areasAtuacaoInput){
		    		objetivosArray = testaDuplicidade(objetivo.id, objetivosArray);
		    		objetivosArrayNome = testaDuplicidade(objetivo.nome, objetivosArrayNome);
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
		rest_atualizar (objJson, semAcao, semAcao);
	});
	
	console.log ("terminou area atuacao");
	
};

function testaDuplicidade (id, array){

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


function atualizaPerfil (){
	
		var objJson = 
			{	
				async : false,
				collection : "userPerfil",
				keys : 
					[
					]
			};

		rest_lista (objJson, atualizaPerfilProcess, semAcao);
};


function atualizaPerfilProcess (usersPerfil){
	
	var badgesInput = JSON.parse(sessionStorage.getItem("badges"));
	
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
				async : false,
				collection : "userPerfil",
				insert :
					{
						documento: userPerfil
					}
			};	    
		rest_incluir (objJson, semAcao, semAcao);
	});
};

function salvaSessionStore (objJson, entidade){

	sessionStorage.setItem(entidade, JSON.stringify(objJson));


}

