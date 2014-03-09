var RegisterView = Backbone.View.extend({
	
	registerCopy: '',
	
	render: function(){
		var html = '';
		this.registerCopy = new Array();
		var self = this;
		$.ajax({
			   url: App.registerUrl,
			   type: 'GET',
			   contentType: "application/json",
			   dataType: "json",
			   success: function(response) {
				   response.forEach(function(reg){
					   var value = reg.value!=undefined ? reg.value : "0000000000000000"; 
					   var regObj = {name: reg.name, val: reg.value};
					   self.registerCopy.push(regObject);
						html += '<tr>';
							html += '<td>' +reg.name+'</td>';
							html += '<td>';
								html += '<input type="text" name="register" id="'+reg.name+'" value="'+toHex(value,16)+'">';
							html += '</td>';
						html += '</tr>';
				   });
				   $('#registerTable').html(html);
			   }
		});		
	},
	events: {
		'blur [name~="register"]' : 'saveRegister'
	},
	
	saveRegister: function(e){
		var reg = $(e.target).attr("id");
		var isValid = validateHex($(e.target).val(), false);
		if(isValid){
			var val = toBinary($(e.target).val());
			reg = toHex(val, 16);
			val = toBinary(reg);
			var data = {name:reg, value:val};
			$.ajax({
			   url: App.registerUrl+"/"+reg,
			   data: JSON.stringify(data),
			   dataType:"json",
			   type: 'PUT',
			   contentType: "application/json",
			   success: function(response) {
				
			   }
		});
		}else{
			window.alert("Invalid Hex Constant in Register");
			for(var i=0; i<this.registerCopy.length; i++){
				if(this.registerCopy[i].name==reg){
					$(e.target).val(this.registerCopy[i].val);
				}
			}
		}
	}
	
	
});
