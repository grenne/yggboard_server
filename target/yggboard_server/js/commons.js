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
		    		habilidadadesCursos.push(curso.id);
		    		habilidadadesCursosNome.push(curso.nome);
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
		    $.each(objetivo.habilidades, function (i, objetvoIdHabilidade) {
		    	if (habilidade.documento.id == objetivoIdHabilidade){
		    		var objetivo = objetivos[i]
		    		habilidadadesObjetivos.push(objetivo.id);
		    		habilidadadesObjetivosNome.push(objetivo.nome);
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
