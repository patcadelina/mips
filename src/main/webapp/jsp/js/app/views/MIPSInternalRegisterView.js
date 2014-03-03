var MIPSInternalRegisterView = Backbone.View.extend({
	
	render: function(){
		var html = '';
		$.ajax({
			   url: App.pipeRegisterUrl,
			   type: 'GET',
			   contentType: "application/json",
			   dataType: "json",
			   success: function(response) {
				   response.forEach(function(reg){
					   var value = reg.value!=undefined ? reg.value : "0000000000000000"; 
						html += '<tr>';
							html += '<td>' +reg.name+'</td>';
							html += '<td>';
								html += '<input disabled type="text" name="register" id="'+reg.name+'" value="'+toHex(value,16)+'">';
							html += '</td>';
						html += '</tr>';
				   });
				$('#internalRegistersTable').html(html);
			  }
		});		

	}
	
});