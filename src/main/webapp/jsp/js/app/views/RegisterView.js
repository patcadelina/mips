var RegisterView = Backbone.View.extend({
	
	render: function(){
		var html = '';
		_.each(this.collection.models, function(model, index, list){
			html += '<tr>';
				html += '<td>' +model.get('registerName')+'</td>';
				html += '<td>';
					html += '<input type="text" class="registerTextBox" name="'+model.get('registerName')+'64" value="'+model.get('registerValue')+'">';
					html += '<input type="text" class="registerTextBox" name="'+model.get('registerName')+'48" value="'+model.get('registerValue')+'">';
					html += '<input type="text" class="registerTextBox" name="'+model.get('registerName')+'32" value="'+model.get('registerValue')+'">';
					html += '<input type="text" class="registerTextBox" name="'+model.get('registerName')+'16" value="'+model.get('registerValue')+'">';
				html += '</td>';
				html += '<td><button type="button" class="btn btn-default btn-sm" id="'+model.get('registerName')+'"><span class="fa-stack fa-1x"><i class="fa fa-check fa-stack-1x"></i></span></button></td>';
			html += '</tr>';
		});
		$('#registerTable').html(html);
	}
	
});