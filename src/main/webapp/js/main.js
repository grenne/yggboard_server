//
//
//
localStorage.urlServidor = window.location.hostname;
if (localStorage.urlServidor == "localhost"){
	localStorage.mainHost = "52.67.61.142";
}else{
	localStorage.mainHost = localStorage.urlServidor;
};

	var objJson = 
		{	
			collection : "habilidades",
			keys : 
				[
				]
		};
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/habilidades/lista?diagrama=habilidades&semCursos=true",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			sessionStorage.setItem("habilidades", JSON.stringify(data));
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		console.log ("erro");
	})
	.always(function(data) {
	});
	
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/badges/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			sessionStorage.setItem("badges", JSON.stringify(data));
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		console.log ("erro");
	})
	.always(function(data) {
	});

localStorage.APP = "yggboard_server";

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
	case "atualizaPerfil":
		atualizaPerfil();
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
