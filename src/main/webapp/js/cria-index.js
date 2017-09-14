
function carregaIndex (){

	var objJson = 
 	  	{
   			token: sessionStorage.token,
			collection : "index",
			async : false
 	  	};
 	rest_remover (objJson, semAcao, semAcao); 	   

	index = rest_listaReturn ("habilidades");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "habilidades")
	};
	index = rest_listaReturn ("objetivos");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "objetivos")
	};
	index = rest_listaReturn ("cargos");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "cargos")
	};
	index = rest_listaReturn ("badges");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "badges")
	};
	index = rest_listaReturn ("areaAtuacao");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "areaAtuacao")
	};
	index = rest_listaReturn ("areaConhecimento");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "areaConhecimento")
	};
	index = rest_listaReturn ("usuarios");
	for (var i = 0; i < index.length; i++) {
		criaIndice(index[i], "usuarios")
	};
	
	sessionStorage.setItem("processo", "encerra-set-interval");

};

function criaIndices(assunto, rotina, processo, proximoAssunto){

	lines = rest_listaReturn (assunto)
	sessionStorage.setItem("lines", lines);	

	sessionStorage.setItem("assunto", assunto);

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", rotina);
	sessionStorage.setItem("processo", processo);
	sessionStorage.setItem("proximoAssunto", proximoAssunto);
	
	console.log ("iniciou cria indice " + assunto);
	
	criaIndice(lines, assunto)
};	

function criaIndicesObjetivos(){

	lines = rest_listaReturn ("objetivos")
	sessionStorage.setItem("lines", lines);	

	sessionStorage.setItem("assunto", "objetivos");

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "criaIndicesBadgesMsg");
	sessionStorage.setItem("processo", "criaIndicesObjetivos");
	
	console.log ("iniciou cria indice objetivos");
};	

function criaIndicesBadges(){

	lines = rest_listaReturn ("badges")
	sessionStorage.setItem("lines", lines);	

	sessionStorage.setItem("assunto", "badges");

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "criaIndicesCursosMsg");
	sessionStorage.setItem("processo", "criaIndicesBadges");
	
	console.log ("iniciou cria indice badges");
};	

function criaIndicesCursos(){

	lines = rest_listaReturn ("cursos")
	sessionStorage.setItem("lines", lines);	

	sessionStorage.setItem("assunto", "cursos");

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "criaIndicesAreaAtuacaoMsg");
	sessionStorage.setItem("processo", "criaIndicesCursos");
	
	console.log ("iniciou cria indice cursos");
};	

function criaIndicesAreaAtuacao(){

	lines = rest_listaReturn ("areaAtuacao")
	sessionStorage.setItem("lines", lines);	

	sessionStorage.setItem("assunto", "areaAtuacao");

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "criaIndicesareaConhecimentoMsg");
	sessionStorage.setItem("processo", "criaIndicesAreaAtuacao");
	
	console.log ("iniciou cria indice area atuacao");
};	

function criaIndicesAreaConhecimento(){

	lines = rest_listaReturn ("areaConhecimento")
	sessionStorage.setItem("lines", lines);	

	sessionStorage.setItem("assunto", "areaConhecimento");

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "criaIndicesUsuariosMsg");
	sessionStorage.setItem("processo", "criaIndicesAreaConhecimento");
	
	console.log ("iniciou cria indice area conhecimento");
};	

function criaIndicesUsuarios(){

	lines = rest_listaReturn ("usuarios")

	sessionStorage.setItem("assunto", "usuario");

	sessionStorage.setItem("index", 1);
    sessionStorage.setItem("totalRecords", lines.length);

	sessionStorage.setItem("rotina", "atualizaCursosHabilidadeMsg");
	sessionStorage.setItem("processo", "criaIndicesUsuarios");
	
	console.log ("iniciou cria indice usuários");
};	

function criaIndice(index, assunto){
	
	var texto = "";
	if (index){
		var entidade = "";
		var id = "";
		var descricao = "";
		if (index){
			if (index.firstName){
				texto  = texto + carregaTextoIndex (index.firstName);
				entidade = index.firstName;
				id = index._id;
				descricao = index.firstName + " " + index.lastName
			};
			if (index.lastName){
				texto  = texto + "," + carregaTextoIndex (index.lastName);
				entidade = index.lastName;
				id = index._id;
				descricao = index.firstName + " " + index.lastName
			};
			if (index.nome){
				texto  = texto + carregaTextoIndex (index.nome);
				entidade = index.nome;
			};
			if (index.id){
				id = index.id;
			}
			if (index.descricao) {
				texto  = texto + "," + carregaTextoIndex (index.descricao);
				descricao = index.descricao;
			};
			if (index.tags){
				$.each(index.tags, function (i, tag) {
					texto  = texto + "," + carregaTextoIndex (tag);
				});
			};
		};
	};
	if (texto != ""){
		var textoArray = texto.split(",");
		
		var objJson = 
			{
				token: sessionStorage.token,
				collection : "index",
				insert :
					{
					documento : 
						{
						texto : textoArray,
						assunto : assunto,
						entidade : entidade,
						id : id,
						descricao : descricao				
						}
					}
			};
		
		rest_incluir (objJson, restOk, semAcao);
	};	
};
function carregaTextoIndex (texto){
	if (texto){
		texto = texto.toLowerCase();
		texto = limpaTexto (texto);
		var textoArray = texto.split(" ");
		return textoArray;
	};
	
	return "";
};

function limpaTexto (texto){
	if (texto){
		var i = 0;
		var textoOut = "";
		while (i < texto.length) {
			var char = texto.substring(i, i + 1);
			switch(char) {
			case "ã":
		        char = "a"
		        break;
			case "á":
		        char = "a"
		        break;
			case "à":
		        char = "a"
		        break;
			case "â":
		        char = "a"
		        break;
		    case "é":
		    	char = "e"
		        break;
		    case "ê":
		    	char = "e"
		        break;
		    case "í":
		    	char = "i"
		        break;
		    case "ô":
		    	char = "o"
		        break;
		    case "õ":
		    	char = "o"
		        break;
		    case "ô":
		    	char = "o"
		        break;
		    case "ó":
		    	char = "o"
		        break;
		    case "ú":
		    	char = "u"
		        break;
		    case "ç":
		    	char = "c"
		        break;
		    case "(":
		    	char = ""
		        break;
		    case ")":
		    	char = ""
		        break;
		    case "-":
		    	char = ""
		        break;
		    case "  ":
		    	char = " "
		        break;
		    default:
		    	break;
			}			
			textoOut = textoOut + char;
			++i;
		};
		return textoOut;
	};
	
	return "";
		
};
