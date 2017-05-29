
function carregaIndex (){
       var objJson = 
 	  	{
			  collection : "index"
 	  	};
 	   rest_remover (objJson, carregaIndexProcesso, semAcao); 	   
};

function carregaIndexProcesso (){
	
	rest_obterHabilidades (carregaIndexElemento, semAcao, "Habilidade");
	rest_obterCarreiras (carregaIndexElemento, semAcao,  "Objetivo");
	rest_obterBadges (carregaIndexElemento,semAcao,   "Badge");
	rest_obterCursos (carregaIndexElemento, semAcao,  "Curso");	
	rest_obterAreaAtuacao (carregaIndexElemento, semAcao,  "Área aAtuação");	
	rest_obterAreaConhecimento (carregaIndexElemento, semAcao,  "Área Conhecimento");	
}; 

function carregaIndexElemento(data, assunto){

	$.each(data, function (i, index) {
		var texto = "";
		if (index){
			var entidade = "";
			var id = "";
			var descricao = "";
			if (index.documento){
				if (index.documento.name){
					texto  = texto + carregaTextoIndex (index.documento.name);
					entidade = index.documento.name;
					id = index.documento.idHabilidade;
					descricao = index.documento.descricao;
				};
				if (index.documento.nome){
					texto  = texto + carregaTextoIndex (index.documento.nome);
					entidade = index.documento.nome;
					id = index.documento.idHabilidade;
				};
				if (index.documento.idCurso){
					id = index.documento.idCurso;
				};
				if (index.documento.id){
					id = index.documento.id;
				};
				if (index.documento.descricao) {
					texto  = texto + "," + carregaTextoIndex (index.documento.descricao);
					descricao = index.documento.descricao;
				};
				if (index.documento.tags){
					$.each(index.documento.tags, function (i, tag) {
						texto  = texto + "," + carregaTextoIndex (tag);
					});
				};
			};
			if (index.nome){
				texto  = texto + carregaTextoIndex (index.nome);
				entidade = index.nome;
			};
			if (index.descricao) {
				texto  = texto + "," + carregaTextoIndex (index.descricao);
				descricao = index.descricao;
			};
			if (index.tags){
				$.each(index.tags, function (i, tag) {
					texto  = texto + "," + carregaTextoIndex (tag);
				});
			};
			if (index.id){
				id = index.id;
			};
		};
		if (texto != ""){
			var textoArray = texto.split(",");
			
			var objJson = 
				{
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
			
			rest_incluir (objJson, semAcao, semAcao);
		};
	});
	
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
