/**
 * 
 */

function atualizaCursosHabilidade (){
	
		var objJson = 
			{	
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
		    	if (habilidade.documento.id == cursoIdHabilidade){
		    		var curso = cursos[i]
		    		habilidadadesCursos = testaDuplicidade(curso.id, habilidadadesCursos);
		    		habilidadadesCursosNome = testaDuplicidade(curso.nome, habilidadadesCursosNome);
		    	};
		    });
		});
    	delete habilidade.documento["cursos"];
    	delete habilidade.documento["cursosNome"];
    	habilidade.documento.cursos = habilidadadesCursos;
    	habilidade.documento.cursosNome = habilidadadesCursosNome;
    	var habililadeUpdate = habilidade.documento;
		var objJson = 
		{	
			collection : "habilidades",
			keys : 
				[
					{
						key : "documento.id",
						value : habilidade.documento.id
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
		    	if (habilidade.documento.id == objetivoIdHabilidade){
		    		var objetivo = objetivos[i]
		    		habilidadadesObjetivos = testaDuplicidade(objetivo.id, habilidadadesObjetivos);
		    		habilidadadesObjetivosNome = testaDuplicidade(objetivo.nome, habilidadadesObjetivosNome);
		    	};
		    });
		});
    	delete habilidade.documento["objetivos"];
    	delete habilidade.documento["objetivosNome"];
    	habilidade.documento.objetivos = habilidadadesObjetivos;
    	habilidade.documento.objetivosNome = habilidadadesObjetivosNome;
    	var habililadeUpdate = habilidade.documento;
		var objJson = 
		{	
			collection : "habilidades",
			keys : 
				[
					{
						key : "documento.id",
						value : habilidade.documento.id
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
		    	if (badge.documento.nome == userBadge){
		    		badges.push(badge.documento.id);
		    	};
		    });
		});
    	delete userPerfil["badges"];
    	userPerfil.badges = badges;
		var badgesInteresse = [];
	    $.each(userPerfil.badgesInteresse, function (i, userBadge) {
		    $.each(badgesInput, function (i, badge) {
		    	if (badge.documento.nome == userBadge){
		    		badgesInteresse.push(badge.documento.id);
		    	};
		    });
		});
    	delete userPerfil["badgesInteresse"];
    	userPerfil.badgesInteresse = badgesInteresse;
    	userPerfil.showBadges = [];
		var objJson = 
			{	
				collection : "userPerfil",
				insert :
					{
						documento: userPerfil
					}
			};	    
		rest_incluir (objJson, semAcao, semAcao);
	});
	console.log ("terminou user perfil");
};
