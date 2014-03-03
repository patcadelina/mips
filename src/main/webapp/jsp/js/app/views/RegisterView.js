var RegisterView = Backbone.View.extend({
	
	render: function(){
		var html = '';
		$.ajax({
			   url: App.registerUrl,
			   type: 'GET',
			   contentType: "application/json",
			   dataType: "json",
			   success: function(response) {
				   response.forEach(function(reg){
					   var value = reg.value!=undefined ? reg.value : "0000000000000000"; 
						html += '<tr>';
						html += '<td>' +reg.name+'</td>';
						html += '<td>';
							html += '<input type="text" name="'+reg.name+'" value="'+toHex(value,16)+'">';
						html += '</td>';
					html += '</tr>';
				   });
				   $('#registerTable').html(html);
			   }
		});		
	}
	
});