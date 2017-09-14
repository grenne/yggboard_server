function rest_obter(objJson, action_ok, action_not_ok, var1, var2, var3) {
	
	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	objJson.token = sessionStorage.token;
	$.ajax({
		type : "POST",
		url : localStorage.mainUrl + "yggboard_server/rest/crud/obter",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		async : async

	}).done(function(data) {
		action_ok(data, var1, var2, var3);
	}).fail(function(data) {
		action_not_ok(data, var1, var2, var3);
	}).always(function(data) {
	});
};

function rest_incluir (objJson, action_ok, action_not_ok, var1, var2, var3){
	
	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};
	objJson.token = sessionStorage.token;

	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/crud/incluir",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
		async : async
	})        	
	.done(function( data ) {
		action_ok (data, var1, var2, var3);
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2, var3);
	})
	.always(function(data) {
	});
};
function rest_remover (objJson, action_ok, action_not_ok, var1, var2, var3){

	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};
	objJson.token = sessionStorage.token;
	
	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/crud/remover/all",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
    	async : async
	})        	
	.done(function(data) {
	})
	.fail(function(data) {
	})
	.always(function(data) {
		if (data.status == 200){
			action_ok (data, var1, var2, var3);
		};
	});
};
function rest_remover_select (objJson, action_ok, action_not_ok, var1, var2, var3){

	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};
	objJson.token = sessionStorage.token;
	
	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/crud/remover",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
    	async : async
	})        	
	.done(function(data) {
	})
	.fail(function(data) {
	})
	.always(function(data) {
		if (data.status == 200){
			action_ok (data, var1, var2, var3);
		};
	});
};

function rest_atualizar (objJson, action_ok, action_not_ok, var1, var2, var3){

	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	objJson.token = sessionStorage.token;
	
	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/crud/atualizar",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
    	async : async
	
	})        	
	.done(function(data) {
		action_ok (data, var1, var2, var3);
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2, var3);
	})
	.always(function(data) {
	});
};

function rest_lista (objJson, action_ok, action_not_ok, var1, var2, var3){

	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	objJson.token = sessionStorage.token;
	
	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/crud/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
    	async : async
	
	})        	
	.done(function( data ) {
		action_ok (data, var1, var2, var3);
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2, var3);
	})
	.always(function(data) {
	});

};

function rest_listaReturn (collection, action_ok, action_not_ok, var1, var2, var3){

	var listas = null;
	var objJson = 
	{	
		token: sessionStorage.token,
		asybc : false,
		collection : collection,
		keys : []
	};
	objJson.token = sessionStorage.token;

	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/crud/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
    	async : false
	
	})        	
	.done(function( data ) {
		listas = data;
	})
	.fail(function(data) {
		listas = null;
	})
	.always(function(data) {
	});

	return listas;
};

function rest_login (email, senha){
	var returnData = null;
	$.ajax({
        url: localStorage.mainUrl + "yggboard_server/rest/usuario/login?email=" + email + "&password=" + senha,
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
       	async : false
	})        	
	.done(function( data ) {
		returnData = data;
	})
	.fail(function(data) {
		returnData = null;
	})
	.always(function(data) {
	});

	return returnData;
};

function rest_atualizaIndiceCollection (objJson, proximo_processo){

	objJson.token = sessionStorage.token;
	
	$.ajax({
		type: "POST",
        url: localStorage.mainUrl + "yggboard_server/rest/batch/atualiza/indices/collection",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data : JSON.stringify(objJson),
    	async : true
	
	})        	
	.done(function(data) {
	})
	.fail(function(data) {
	})
	.always(function(data) {
		if (data.status == 200){
			sessionStorage.setItem("processo", proximo_processo);
		};
	});
};

function semAcao(){
};

function restOk(){
};

