/**
 * 
 */
	$(function() {

		// FORM VALIDATION FEEDBACK ICONS
		// =================================================================
		var faIcon = {
			valid: 'fa fa-check-circle fa-lg text-success',
			invalid: 'fa fa-times-circle fa-lg',
			validating: 'fa fa-refresh'
		}


		var formValido = true;
		// FORM VALIDATION ON TABS
		// =================================================================
		$('#form-login').bootstrapValidator({
			excluded: [':disabled'],
			feedbackIcons: faIcon,
			fields: {
			email: {
				validators: {
					notEmpty: {
						message: 'Entre com email'
					},
					emailAddress: {
						message: 'Informe um email valido'
					}
				}
			},
			password: {
				validators: {
					notEmpty: {
						message: 'Informe a senhad'
					}
				}
			}
			}
		}).on('status.field.bv', function(e, data) {
			$('#btn-submit-login').attr("disabled", false);
			formValido = true;
			$('.msgErro').addClass("hide");
			var $form     = $(e.target),
			validator = data.bv,
			$tabPane  = data.element.parents('.tab-pane'),
			tabId     = $tabPane.attr('id');

			if (tabId) {
				var $icon = $('a[href="#' + tabId + '"][data-toggle="tab"]').parent().find('i');
	
				// Add custom class to tab containing the field
				if (data.status == validator.STATUS_INVALID) {
					$icon.removeClass(faIcon.valid).addClass(faIcon.invalid);
				} else if (data.status == validator.STATUS_VALID) {
					var isValidTab = validator.isValidContainer($tabPane);
					$icon.removeClass(faIcon.valid).addClass(isValidTab ? faIcon.valid : faIcon.invalid);
				}
			};
			if (data.status == "INVALID"){
				$('#btn-submit-login').attr("disabled", true);
				formValido = false;
			};	
		});
		$("#btn-submit-login").off('click');
		$("#btn-submit-login").on('click', function () {
			if (formValido){
				console.log ("ok");
				localStorage.loginOk = "true";
				executaLogin($('#email').val(), $('#password').val()); 
			}else{
				console.log ("notok");
			}
		});
		$(document).keypress(function(e) {
		    if(e.which == 13) $("#btn-submit-login").click();
		});
	});
