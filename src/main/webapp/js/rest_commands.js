function rest_obter(objJson, action_ok, action_not_ok, var1, var2, var3) {
	
	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	$.ajax({
		type : "POST",
		url : "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/obter",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		data : JSON.stringify(objJson),
		async : async

	}).done(function(data) {
		action_ok(data, var1, var2, var3);
	}).fail(function(data) {
		action_not_ok(data, var1, var2, var3);
	}).always(function(data) {
		action_ok(data, var1, var2, var3);
	});
};

function rest_incluir (objJson, action_ok, action_not_ok, var1, var2, var3){
	
	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	$.ajax({
		type: "POST",
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/incluir",
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

	$.ajax({
		type: "POST",
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/remover/all",
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
		action_ok (data, var1, var2, var3);		
	});
};

function rest_atualizar (objJson, action_ok, action_not_ok, var1, var2, var3){

	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	$.ajax({
		type: "POST",
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/atualizar",
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
		action_ok (data, var1, var2, var3);		
	});
};

function rest_lista (objJson, action_ok, action_not_ok, var1, var2, var3){

	var async = false;
	
	if (objJson.async != null){
		async = objJson.async
	};

	$.ajax({
		type: "POST",
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/crud/lista",
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

function rest_obterHabilidades(action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/habilidades/lista?diagrama=habilidades&semCursos=" + var1,
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok()
	})
	.always(function(data) {
	});
};

function rest_obterHabilidadesName(name, action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/habilidades/obter?name=" + name,
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok(null, var1, var2);
	})
	.always(function(data) {
	});
};

function rest_obterHabilidadesIdHabilidade(name, action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/habilidades/obter?idHabilidade=" + idHabilidade,
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
		}else{
			action_not_ok (data, var1, var2);	
		};
	})
	.fail(function(data) {
		action_not_ok(null, var1, var2)
	})
	.always(function(data) {
	});
};

function rest_obterCarreiras(action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/carreiras/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
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

function rest_obterCarreira(idCarreira, action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/carreiras/obter?idCarreira=" + idCarreira,
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		action_ok(data, var1, var2);
	})
	.fail(function(data) {
		action_not_ok(null, var1, var2);
	})
	.always(function(data) {
	});
};

function rest_obterBadges(action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/badges/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
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

function rest_obterBadge(idBadge, action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/carreiras/obter?idBadge=" + idBadge,
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		action_ok(data, var1, var2);
	})
	.fail(function(data) {
		action_not_ok(null, var1, var2);
	})
	.always(function(data) {
	});
};

function rest_obterCursos(action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/cursos/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
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

function rest_obterAreaAtuacao(action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/areaatuacao/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
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

function rest_obterAreaConhecimento(action_ok, action_not_ok, var1, var2) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/areaconhecimento/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2);
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

function rest_obterBadges(action_ok, action_not_ok, var1, var2, var3) {
	$.ajax({
        url: "http://" + localStorage.urlServidor + ":8080/yggboard_server/rest/badges/lista",
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
	})
	.done(function( data ) {
		if (data){
			action_ok (data, var1, var2, var3);
		}else{
			action_not_ok (data, var1, var2, var3);	
		};
	})
	.fail(function(data) {
		action_not_ok (data, var1, var2, var3);	
	})
	.always(function(data) {
	});
};

function semAcao(){
	
};

