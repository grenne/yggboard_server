//
//
//
localStorage.urlServidor = window.location.hostname;
if (localStorage.urlServidor == "localhost"){
	localStorage.mainHost = "52.67.61.142";
}else{
	localStorage.mainHost = localStorage.urlServidor;
};

localStorage.APP = "yggboard_server";

var objJson = 
	{	
		asybc : false,
		collection : "habilidades",
		keys : 
			[
			]
	};

rest_lista (objJson, salvaSessionStore, semAcao, "habilidades");

var objJson = 
{	
	asybc : false,
	collection : "badges",
	keys : 
		[
		]
};

rest_lista (objJson, salvaSessionStore, semAcao, "badges");

var objJson = 
{	
	asybc : false,
	collection : "areaAtuacao",
	keys : 
		[
		]
};

rest_lista (objJson, salvaSessionStore, semAcao, "areaAtuacao");


$( ".escolha" ).click(function() {
    $( ".output" ).remove();
    $( ".reader" ).show();
	sessionStorage.escolha = $(this).attr("data-escolha");
});

//$( ".tools" ).hide();
$( ".reader" ).hide();

$( ".teste" ).click(function() {
	switch ($(this).attr("data-teste")) {
	case "testaIncluir":
		testaIncluir();
		break;
	case "testaObter":
		testaObter();
		break;
	case "testaAtualizar":
		testaAtualizar();
		break;
	case "testaLista":
		testaLista();
		break;
	case "testaAtualizaPerfil":
		
		break;
	case "atualizaCursosHabilidade":
		atualizaCursosHabilidade();
		break;
	case "atualizaObjetivosHabilidade":
		atualizaObjetivosHabilidade();
		break;
	case "atualizaBadges":
		atualizaBadges();
		break;
	case "atualizaAreaAtuacaoObjetivos":
		atualizaAreaAtuacaoObjetivos();
		break;
	case "atualizaAreaConhecimentoHabilidades":
		atualizaAreaConhecimentoHabilidades();
		break;
	case "testaFiltro":
		testaFiltro();
		break;
	case "usuarios":
		copiaUsuarios();
		break;
	case "userPerfil":
		copiaUserPerfil();
		break;
	case "setup":
		copiaSetup();
		break;
	case "index":
		copiaIndex();
		break;
	case "index-cria":
		carregaIndex();
		break;
		
	default:
		break;
	}
});

