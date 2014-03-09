var MemoryView = Backbone.View.extend({
	
	memorySet: '',
	
	render: function(){
		var html = '';
		this.memorySet.forEach(function(mem){
			var value = mem.value!=undefined ? mem.value : "00";
			html += '<tr>';
				html += '<td>' +mem.address+'</td>';
				html += '<td>';
					html += '<input name="memory" type="text" class="memoryText" id="'+mem.address+'" value="'+toHex(value,2)+'">';
				html += '</td>';
			html += '</tr>';
	    });
		$('#memoryTable').html(html);
	},
	
	events: {
		'click [name~="fetchMem"]' : 'fetchMemory',
		'blur [name~="memory"]' : 'saveMemory'
	},
	
	fetchMemory: function(){
		var startAdd = $("#memStart").val();
		var endAdd = $("#memEnd").val();
		//GET ../memory?from=&to=
		var self = this;
		$.ajax({
			   url: App.memoryUrl,
			   data: {from:startAdd, to:endAdd},
			   dataType:"json",
			   type: 'GET',
			   success: function(response) {
				 self.memorySet = response;
			     self.render();
			   }
		});
	},
	
	saveMemory: function(e){
		var add = $(e.target).attr("id");
		var isValid = validateHex($(e.target).val(), false;
		if(isValid){
			var val = toBinary($(e.target).val());//for hex conversion
			val = toHex(val, 2); //to pad to 2 hex
			val = toBinary(val); //server needs binary
			var data = {address:add, value:val};
			$.ajax({
			   url: App.memoryUrl+"/"+add,
			   data: JSON.stringify(data),
			   dataType:"json",
			   type: 'PUT',
			   contentType: "application/json",
			   success: function(response) {
				
			   }
		});
		}else{
			window.alert("Invalid Hex Constant in Memory for address " + add);
			for(var i=0; i<this.memorySet.length; i++){
				if(this.memorySet[i].address==add){
					var value = memorySet[i].value!=undefined ? memorySet[i].value : "0000000000000000"; 
					$(e.target).val(toHex(value,2));
				}
			}	
		}
	}
	
	
	
});
