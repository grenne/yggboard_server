
function copiaUserPerfil() {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard/rest/userPerfil/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			copiaBase (data, "userPerfil");
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2);	
	})
	.always(function(data) {
	});
};

function copiaUsuarios() {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard/rest/usuario/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			copiaBase (data, "usuarios");
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2);	
	})
	.always(function(data) {
	});
};

function copiaSetup() {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard/rest/setup/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			copiaBase (data, "setup");
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2);	
	})
	.always(function(data) {
	});
};

function copiaIndex() {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard/rest/index/lista/all",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			copiaBase (data, "index");
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2);	
	})
	.always(function(data) {
	});
};

function copiaBase(data, collection){
	
    $.each(data, function (i, item) {    	
    	var objJson = 
    		{
    			collection : collection,
    			insert :
    				{
    				documento : item.documento}
    		};
    	rest_incluir (objJson, semAcao, semAcao);
    });
	

};
