
	function executaLogin(email, senha) {
		var senhaMd5 = $.md5(senha);
		var data = rest_login(email, senhaMd5);
		if (data){
			sessionStorage.usuarioEmail = data.email;
			sessionStorage.usuarioFirstName = data.firstName;
			sessionStorage.usuarioLastName = data.lastName;
			sessionStorage.usuarioPerfil = data.perfil;
			sessionStorage.usuarioGender = data.gender;
			sessionStorage.usuarioPhoto = data.photo;
			sessionStorage.token = data.token;
			sessionStorage.loginOk = "true";
			$(window.document.location).attr('href','home.html');
		}else{
			$('.msgErro').removeClass("hide");
			localStorage.loginOk = "false";
		};
		$('.msgErro').removeClass("hide");
		localStorage.loginOk = "false";
	};