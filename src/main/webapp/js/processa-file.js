  var reader;
  var progress = document.querySelector('.percent');

  document.getElementById('files').addEventListener('change', handleFileSelect, false);

  var myVar = setInterval(function(){ setIntervalObject() }, 30);	  
  function setIntervalObject() {
  	lines = JSON.parse(sessionStorage.getItem("lines"));
  	i = sessionStorage.getItem("index");
  	totalRecords = sessionStorage.getItem("totalRecords");
	var percentLoaded = Math.round((i / totalRecords) * 100);
	if (percentLoaded < 100) {
	     progress.style.width = percentLoaded + '%';
	     progress.textContent = percentLoaded + '%';
	};
    $('.progress-bar').css('width', percentLoaded + '%').attr('aria-valuenow', percentLoaded);
  	if (lines[i]){
  		carregaDados(lines[i]);
  	};
  	i++;
  	if (i > totalRecords){
  		$(".final").show();
  		$("#labelRegistros").text("Registros processados:");
  		$("#totalRegistros").text(totalRecords);
  		if (sessionStorage.getItem("excutaPrepend") == "true"){
	  		if (sessionStorage.getItem("rotina") == "carregaIndex"){
	  			sessionStorage.setItem("rotina", "pulaExecucao");  	
	  			carregaIndex();
	  		};
	  		if (sessionStorage.getItem("rotina") == "carregaIndexMsg"){
	  			sessionStorage.setItem("rotina", "carregaIndex");  	
	  			$("#textoAtualizando").remove();
	  			$("#registros").prepend('<li id="textoAtualizando" class="output "><strong class="label executando">Criando indices...</strong></li>');
	  		    $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaCursosHabilidade"){
	  			sessionStorage.setItem("rotina", "pulaExecucao");  	
	  			atualizaCursosHabilidade();
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaCursosHabilidadeMsg"){
	  			sessionStorage.setItem("rotina", "atualizaCursosHabilidade");  	
	  			$("#textoAtualizando").remove();
	  			$("#registros").prepend('<li class="output"><strong class="label">Indices criados</strong></li>');
	  			$("#registros").prepend('<li id="textoAtualizando" class="output "><strong class="label executando">Atualizando index habilidades...</strong></li>');
	  		    $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaObjetivosHabilidade"){
	  			sessionStorage.setItem("rotina", "pulaExecucao");  	
		  		atualizaObjetivosHabilidade();
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaObjetivosHabilidadeMsg"){
	  			sessionStorage.setItem("rotina", "atualizaObjetivosHabilidade");  	
	  			$("#textoAtualizando").remove();
	  			$("#registros").prepend('<li class="output"><strong class="label">Indices habilidades criados</strong></li>');
	  			$("#registros").prepend('<li id="textoAtualizando" class="output "><strong class="label executando">Atualizando index objetivos...</strong></li>');
	  		    $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaAreaAtuacaoObjetivos"){
	  			sessionStorage.setItem("rotina", "pulaExecucao");  	
		  		atualizaAreaAtuacaoObjetivos();
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaAreaAtuacaoObjetivosMsg"){
	  			sessionStorage.setItem("rotina", "atualizaAreaAtuacaoObjetivos");  	
	  			$("#textoAtualizando").remove();
	  			$("#registros").prepend('<li class="output"><strong class="label">Indices objetivos criados</strong></li>');
	  			$("#registros").prepend('<li id="textoAtualizando" class="output"><strong class="label executando">Atualizando index objetivos...</strong></li>');
	  		    $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaAreaConhecimentoHabilidades"){
	  			sessionStorage.setItem("rotina", "pulaExecucao");  	
	  			atualizaAreaConhecimentoHabilidades();
	  		};
	  		if (sessionStorage.getItem("rotina") == "atualizaAreaConhecimentoHabilidadesMsg"){
	  			sessionStorage.setItem("rotina", "atualizaAreaConhecimentoHabilidades");  	
	  			$("#textoAtualizando").remove();
	  			$("#registros").prepend('<li class="output"><strong class="label">Indices habilidades criados</strong></li>');
	  			$("#registros").prepend('<li id="textoAtualizando" class="output "><strong class="label executando">Atualizando index area conhecimento...</strong></li>');
	  		    $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
	  		};
	  		if (sessionStorage.getItem("rotina") == "ultimaRotina"){	  			
		  		$("#textoAtualizando").remove();
				$("#registros").prepend('<li class="output"><strong class="label">Indices area conhecimento criados</strong></li>');
				$("#registros").prepend('<li class="output"><strong class="label">**** Processo encerrado ****</strong></li>');
				sessionStorage.setItem("excutaPrepend", "false");  	
		  		stopIntervalObject();
	  		};
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
