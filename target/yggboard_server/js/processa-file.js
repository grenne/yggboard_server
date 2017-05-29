  var reader;
  var progress = document.querySelector('.percent');

  document.getElementById('files').addEventListener('change', handleFileSelect, false);
    
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
    if (evt.lengthComputable) {
      var percentLoaded = Math.round((evt.loaded / evt.total) * 100);
      // Increase the progress bar length.
      if (percentLoaded < 100) {
        progress.style.width = percentLoaded + '%';
        progress.textContent = percentLoaded + '%';
      }
    }
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
			  collection : sessionStorage.escolha
 	  	};
 	   rest_remover (objJson, processaRegistros, semAcao, lines);
    }

    // Read in the image file as a binary string.
    reader.readAsBinaryString(evt.target.files[0]);
  }
  
  function processaRegistros (data, lines){
     var output = [];
     for(var i = 0; i < lines.length; i++) {
    	 	if (i > 0){
              output.push('<li class="output">' + lines[i] + '<br>');
              carregaDados(lines[i]);
    	 	};
     };
     document.getElementById('records').innerHTML = "Registros processados : " + lines.length + "<br><ul class='output'>" + output.join("") + "</ul";
     progress.style.width = '100%';
     progress.textContent = '100%';
     setTimeout("document.getElementById('progress_bar').className='';", 2000);
     $( ".reader" ).hide();
  };
