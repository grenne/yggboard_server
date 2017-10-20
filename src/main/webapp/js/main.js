//
//
//
var urlParams = new URLSearchParams(window.location.search);

var connection = window.navigator.connection    ||
window.navigator.mozConnection ||
null;
if (connection === null) {
// API not supported :(
} else {
// API supported! Let's start the fun :)
}

console.log(urlParams.has('post')); // true
console.log(urlParams.get('action')); // "edit"
console.log(urlParams.getAll('action')); // ["edit"]
console.log(urlParams.toString()); // "?post=1234&action=edit"
console.log(urlParams.append('active', '1')); // "?post=1234&action=edit&active=1"

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

