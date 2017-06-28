//
//
//
localStorage.urlServidor = window.location.hostname;
if (localStorage.urlServidor == "localhost"){
	localStorage.mainHost = "www.yggboard.com";
	localStorage.mainUrl = "http://localhost:8080/";
}else{
	localStorage.mainHost = localStorage.urlServidor;
	localStorage.mainUrl = "https://www.yggboard.com:8443/";
};

localStorage.APP = "yggboard_server";

sessionStorage.setItem("habilidades", JSON.stringify(rest_listaReturn ("habilidades")));
sessionStorage.setItem("objetivos", JSON.stringify(rest_listaReturn ("objetivos")));
sessionStorage.setItem("cursos", JSON.stringify(rest_listaReturn ("cursos")));
sessionStorage.setItem("badges", JSON.stringify(rest_listaReturn ("badges")));
sessionStorage.setItem("areaAtuacao", JSON.stringify(rest_listaReturn ("areaAtuacao")));
sessionStorage.setItem("areaConhecimento", JSON.stringify(rest_listaReturn ("areaConhecimento")));
sessionStorage.setItem("usuarios", JSON.stringify(rest_listaReturn ("usuarios")));

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
	case "testaLista":
		testaLista();
		break;
	case "testaAtualizaPerfil":	
		testaAtualizaPerfil();
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
	case "mostraPrerequisitos":
		mostraPrerequisitos();
		break;
		
	default:
		break;
	}
});

