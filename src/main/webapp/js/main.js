//
//
//
localStorage.urlServidor = window.location.hostname;
if (localStorage.urlServidor == "localhost"){
	localStorage.mainHost = "www.yggboard.com";
	localStorage.mainUrl = "http://localhost:8080/";
}else{
	localStorage.mainHost = localStorage.urlServidor;
	localStorage.mainUrl = "https://www.yggboard.com:rest/";
};

localStorage.APP = "yggboard_server";

