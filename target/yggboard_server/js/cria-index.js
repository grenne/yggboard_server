
function carregaIndex (){
       var objJson = 
 	  	{
			  collection : "index"
 	  	};
 	   rest_remover (objJson, carregaIndexProcesso, semAcao); 	   
};

function carregaIndexProcesso (){
	
	
	console.log ("iniciou cria index");

	carregaIndexElemento(rest_listaReturn ("habilidades"),"habilidades" );
	carregaIndexElemento(rest_listaReturn ("objetivos"), "objetivos");
	carregaIndexElemento(rest_listaReturn ("badges"), "badges");
	carregaIndexElemento(rest_listaReturn ("cursos"), "cursos");
	carregaIndexElemento(rest_listaReturn ("areaAtuacao"), "areaAtuacao");
	carregaIndexElemento(rest_listaReturn ("areaConhecimento"), "areaConhecimento");
	carregaIndexElemento(rest_listaReturn ("usuarios"), "usuarios");
	
	console.log ("terminou cria index");
}; 

function carregaIndexElemento(data, assunto){

	
	$.each(data, function (i, index) {
		var texto = "";
		if (index){
			var entidade = "";
			var id = "";
			var descricao = "";
			if (index){
				if (index.name){
					texto  = texto + carregaTextoIndex (index.name);
					entidade = index.name;
					id = index.idHabilidade;
					descricao = index.descricao;
				};
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
					id = index.idHabilidade;
				};
				if (index.idCurso){
					id = index.idCurso;
				};
				if (index.id){
					id = index.id;
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
			
			rest_incluir (objJson, restOk, semAcao);
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
