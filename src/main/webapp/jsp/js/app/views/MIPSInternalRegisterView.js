var MIPSInternalRegisterView = Backbone.View.extend({
	
	render: function(){
		var html = '';
		_.each(this.collection.models, function(model, index, list){
			html += '<tr>';
				html += '<td>' +model.get('registerName')+'</td>';
				html += '<td>';
					html += '<input type="text" class="mipsRegisterText" name="'+model.get('registerName')+'" value="'+model.get('registerValue')+'">';
				html += '</td>';
			html += '</tr>';
		});
		$('#internalRegistersTable').html(html);
	}
	
});