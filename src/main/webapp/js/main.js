//
//
//
localStorage.urlServidor = window.location.hostname;
if (localStorage.urlServidor == "localhost"){
	localStorage.mainHost = "www.yggboard.com";
	localStorage.mainUrl = "http://localhost:8080/";
}else{
	if (localStorage.urlServidor == "www.yggboard.com"){
		localStorage.mainHost = "www.yggboard.com";
		localStorage.mainUrl = "https://www.yggboard.com/rest/";
	}else{
		localStorage.mainHost = localStorage.urlServidor;
		localStorage.mainUrl = "https://www.yggjobs.com/rest/";
	};
};

localStorage.APP = "yggboard_server";

