  var reader;
  var progress = document.querySelector('.percent');

  document.getElementById('files').addEventListener('change', handleFileSelect, false);

  var myVar = setInterval(function(){ setIntervalObject() }, 30);	  
  
  function setIntervalObject() {
  	
	lines = JSON.parse(sessionStorage.getItem("lines"));
  	assunto = JSON.parse(sessionStorage.getItem("assunto"));
  	i = sessionStorage.getItem("index");
  	totalRecords = sessionStorage.getItem("totalRecords");
	var percentLoaded = Math.round((i / totalRecords) * 100);
	if (percentLoaded < 100) {
	     progress.style.width = percentLoaded + '%';
	     progress.textContent = percentLoaded + '%';
	};
    $('.progress-bar').css('width', percentLoaded + '%').attr('aria-valuenow', percentLoaded);

    if (lines[i]) {
		switch (sessionStorage.getItem("processo")) {
		case "carregaDados":
			carregaDados(lines[i]);
			break;
		default:
			break;
		};
	};
    if (sessionStorage.getItem("processo") == "carrega-dados"){
	  	if (lines[i]){
	  		carregaDados(lines[i]);
	  	};
    };
    if (sessionStorage.getItem("processo") == "carrega-indice"){
	  	if (lines[i]){
	  		carregaDados(lines[i]);
	  	};
    };
  	i++;
  	if (i > totalRecords){
  		if (sessionStorage.getItem("excutaPrepend") == "true"){
  			$(".final").show();
	  		$("#labelRegistros").text("Registros processados:");
	  		$("#totalRegistros").text(totalRecords);
	  		var objArrays = {
	  				arrayOrigem : ["necessarios","habilidades"],
	  				arrayDestino : ["objetivos","cursos"],
	  				arrayCollection : ["objetivos","cursos"]  				
	  		};
	  		var objJson = {
	  				collection : "habilidades",
	  				arrays : objArrays
	  			};
	  		rest_atualizaIndiceCollection (objJson, semAcao, semAcao);
	  		var objArrays = {
	  				arrayOrigem : ["areaAtuacao"],
	  				arrayDestino : ["objetivos"],
	  				arrayCollection : ["objetivos"]  				
	  		};
	  		var objJson = {
	  				collection : "areaAtuacao",
	  				arrays : objArrays
	  			};
	  		rest_atualizaIndiceCollection (objJson, semAcao, semAcao);
	  		var objArrays = {
	  				arrayOrigem : ["areaConhecimento"],
	  				arrayDestino : ["habilidades"],
	  				arrayCollection : ["habilidades"]  				
	  		};
	  		var objJson = {
	  				collection : "areaConhecimento",
	  				arrays : objArrays
	  			};
	  		rest_atualizaIndiceCollection (objJson, semAcao, semAcao);
	  	    sessionStorage.setItem("excutaPrepend", "false");
  		};
  	};
    sessionStorage.setItem("index", i);
  };
   
  function stopIntervalObject() {
    clearInterval(myVar);
  };    

  stopIntervalObject();
  
  function abortRead() {
    reader.abort();
  }

  function errorHandler(evt) {
    switch(evt.target.error.code) {
      case evt.target.error.NOT_FOUND_ERR:
        alert('File Not Found!');
        break;
      case evt.target.error.NOT_READABLE_ERR:
        alert('File is not readable');
        break;
      case evt.target.error.ABORT_ERR:
        break; // noop
      default:
        alert('An error occurred reading this file.');
    };
  }

  function updateProgress(evt) {
    // evt is an ProgressEvent.
/*    if (evt.lengthComputable) {
      var percentLoaded = Math.round((evt.loaded / evt.total) * 100);
      // Increase the progress bar length.
      if (percentLoaded < 100) {
        progress.style.width = percentLoaded + '%';
        progress.textContent = percentLoaded + '%';
      }else{
    	  percentLoaded = 100;
          progress.style.width = percentLoaded + '%';
          progress.textContent = percentLoaded + '%';
      }
    }
*/
  }

  function handleFileSelect(evt) {
    // Reset progress indicator on new file selection.
    progress.style.width = '0%';
    progress.textContent = '0%';

    reader = new FileReader();
    reader.onerror = errorHandler;
    reader.onprogress = updateProgress;
    reader.onabort = function(e) {
      alert('File read cancelled');
    };
    reader.onloadstart = function(e) {
      document.getElementById('progress_bar').className = 'loading';
    };
    reader.onload = function(e) {
      // Ensure that the progress bar displays 100% at the end.
       var text = e.target.result;
       var lines = text.split(/[\r\n]+/g); // tolerate both Windows and Unix linebreaks
       // limpar tabela antes de carregar
       var objJson = 
 	  	{
   			token: "1170706277ae0af0486017711353ee73",
			collection : sessionStorage.escolha
 	  	};
 	   rest_remover (objJson, processaRegistros, semAcao, lines);
    }

    // Read in the image file as a binary string.
    reader.readAsBinaryString(evt.target.files[0]);
  }
  
  function processaRegistros (data, lines){
     sessionStorage.setItem("lines", JSON.stringify(lines));
     sessionStorage.setItem("index", 1);
     sessionStorage.setItem("totalRecords", lines.length);
     sessionStorage.setItem("excutaPrepend", "true");
     sessionStorage.setItem("rotina", "carregaIndexMsg");
     sessionStorage.setItem("processo", "carrega-dados");
     $(".registros" ).show();
     $("#labelRegistros").text("Registros carregando...");
     $("#totalRegistros").text("");
     $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
     var myVar = setInterval(function(){ setIntervalObject() }, 30);
     progress.style.width = '100%';
     progress.textContent = '100%';
     setTimeout("document.getElementById('progress_bar').className='';", 2000);
     $( ".reader" ).hide();
  };
